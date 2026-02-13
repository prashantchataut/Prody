package com.prody.prashant.ui.screens.futuremessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.FutureMessageDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.FutureMessageEntity
import com.prody.prashant.domain.gamification.GameSessionManager
import com.prody.prashant.domain.gamification.SessionResult
import com.prody.prashant.util.AudioRecorderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class WriteMessageUiState(
    val title: String = "",
    val content: String = "",
    val deliveryDate: Long = getDefaultDeliveryDate(),
    val selectedPreset: DatePreset = DatePreset.ONE_MONTH,
    val selectedCategory: MessageCategory = MessageCategory.GENERAL,
    val selectedCategories: Set<MessageCategory> = setOf(MessageCategory.GENERAL),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val showDatePicker: Boolean = false,
    // Media attachments
    val attachedPhotos: List<String> = emptyList(),
    val attachedVideos: List<String> = emptyList(),
    // Voice recording state
    val isRecording: Boolean = false,
    val voiceRecordingUri: String? = null,
    val voiceRecordingDuration: Long = 0,
    val recordingTimeElapsed: Long = 0,
    val isPlayingVoice: Boolean = false,
    // Animation state
    val showSealingAnimation: Boolean = false,
    // Unsaved changes
    val hasUnsavedChanges: Boolean = false,
    val showDiscardDialog: Boolean = false,
    // Session result for completion feedback
    val sessionResult: SessionResult? = null,
    val showSessionResult: Boolean = false
) {
    val canSave: Boolean
        get() = title.isNotBlank() && content.isNotBlank()

    val hasContent: Boolean
        get() = title.isNotBlank() || content.isNotBlank() ||
                attachedPhotos.isNotEmpty() || attachedVideos.isNotEmpty() ||
                voiceRecordingUri != null
}

private fun getDefaultDeliveryDate(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, 1)
    return calendar.timeInMillis
}

@HiltViewModel
class WriteMessageViewModel @Inject constructor(
    private val futureMessageDao: FutureMessageDao,
    private val userDao: UserDao,
    private val audioRecorderManager: AudioRecorderManager,
    private val gameSessionManager: GameSessionManager
) : ViewModel() {

    companion object {
        private const val TAG = "WriteMessageViewModel"
    }

    private val _uiState = MutableStateFlow(WriteMessageUiState())
    val uiState: StateFlow<WriteMessageUiState> = _uiState.asStateFlow()

    init {
        observeAudioRecorderStates()
    }

    /**
     * Observe audio recorder state changes and update UI state accordingly.
     */
    private fun observeAudioRecorderStates() {
        viewModelScope.launch {
            audioRecorderManager.isRecording.collect { isRecording ->
                _uiState.update { it.copy(isRecording = isRecording) }
            }
        }
        viewModelScope.launch {
            audioRecorderManager.recordingDuration.collect { duration ->
                _uiState.update { it.copy(recordingTimeElapsed = duration) }
            }
        }
        viewModelScope.launch {
            audioRecorderManager.isPlaying.collect { isPlaying ->
                _uiState.update { it.copy(isPlayingVoice = isPlaying) }
            }
        }
        viewModelScope.launch {
            audioRecorderManager.error.collect { error ->
                error?.let {
                    _uiState.update { state -> state.copy(error = it) }
                    audioRecorderManager.clearError()
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, hasUnsavedChanges = true) }
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content, hasUnsavedChanges = true) }
    }

    fun updateCategory(category: MessageCategory) {
        _uiState.update { it.copy(selectedCategory = category, hasUnsavedChanges = true) }
    }

    /**
     * Toggle a category in the multi-select set.
     */
    fun toggleCategory(category: MessageCategory) {
        _uiState.update { state ->
            val currentCategories = state.selectedCategories
            val newCategories = if (currentCategories.contains(category)) {
                // Don't allow deselecting the last category
                if (currentCategories.size > 1) {
                    currentCategories - category
                } else {
                    currentCategories
                }
            } else {
                currentCategories + category
            }
            state.copy(
                selectedCategories = newCategories,
                selectedCategory = newCategories.first(),
                hasUnsavedChanges = true
            )
        }
    }

    fun selectDatePreset(preset: DatePreset) {
        val calendar = Calendar.getInstance()
        when (preset) {
            DatePreset.ONE_WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            DatePreset.ONE_MONTH -> calendar.add(Calendar.MONTH, 1)
            DatePreset.SIX_MONTHS -> calendar.add(Calendar.MONTH, 6)
            DatePreset.ONE_YEAR -> calendar.add(Calendar.YEAR, 1)
            DatePreset.CUSTOM -> {
                // Show date picker for custom selection
                _uiState.update { it.copy(showDatePicker = true) }
                return
            }
        }

        _uiState.update {
            it.copy(
                selectedPreset = preset,
                deliveryDate = calendar.timeInMillis,
                hasUnsavedChanges = true
            )
        }
    }

    fun selectCustomDate(date: Long) {
        _uiState.update {
            it.copy(
                selectedPreset = DatePreset.CUSTOM,
                deliveryDate = date,
                showDatePicker = false,
                hasUnsavedChanges = true
            )
        }
    }

    fun showDatePicker() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    fun hideDatePicker() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    fun saveMessage() {
        viewModelScope.launch {
            val state = _uiState.value

            // Validate content
            if (state.content.isBlank()) {
                _uiState.update { it.copy(error = "Please write a message before sealing") }
                return@launch
            }

            // Show sealing animation
            _uiState.update { it.copy(showSealingAnimation = true, isSaving = true) }

            try {
                // Simulate animation delay (animation runs in UI)
                kotlinx.coroutines.delay(1500)

                val message = FutureMessageEntity(
                    title = state.title,
                    content = state.content,
                    deliveryDate = state.deliveryDate,
                    category = state.selectedCategories.joinToString(",") { it.name.lowercase() },
                    createdAt = System.currentTimeMillis(),
                    attachedPhotos = state.attachedPhotos.joinToString(","),
                    attachedVideos = state.attachedVideos.joinToString(","),
                    voiceRecordingUri = state.voiceRecordingUri,
                    voiceRecordingDuration = state.voiceRecordingDuration
                )

                val messageId = futureMessageDao.insertMessage(message)

                // Update user stats (legacy tracking)
                userDao.incrementFutureMessages()

                // Complete the Commit session via the game system
                val sessionResult = gameSessionManager.completeCommitSession(
                    messageId = messageId,
                    content = state.content,
                    deliveryDate = state.deliveryDate
                )

                // Update achievements
                updateFutureMessageAchievements()

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true,
                        showSealingAnimation = false,
                        hasUnsavedChanges = false,
                        sessionResult = sessionResult,
                        showSessionResult = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        showSealingAnimation = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Dismiss the session result card.
     */
    fun dismissSessionResult() {
        _uiState.update { it.copy(showSessionResult = false) }
    }

    private suspend fun updateFutureMessageAchievements() {
        val profile = userDao.getUserProfileSync() ?: return
        val messagesCount = profile.futureMessagesCount

        // Update progress
        userDao.updateAchievementProgress("future_1", messagesCount)
        userDao.updateAchievementProgress("future_10", messagesCount)

        // Check unlocks
        if (messagesCount >= 1) checkAndUnlockAchievement("future_1")
        if (messagesCount >= 10) checkAndUnlockAchievement("future_10")
    }

    private suspend fun checkAndUnlockAchievement(achievementId: String) {
        val achievement = userDao.getAchievementById(achievementId)
        if (achievement != null && !achievement.isUnlocked) {
            userDao.unlockAchievement(achievementId)
            val points = achievement.rewardValue.toIntOrNull() ?: 100
            userDao.addPoints(points)
        }
    }

    // =========================================================================
    // MEDIA ATTACHMENT METHODS
    // =========================================================================

    /**
     * Add photos to the message.
     */
    fun addPhotos(photoUris: List<String>) {
        _uiState.update {
            it.copy(
                attachedPhotos = it.attachedPhotos + photoUris,
                hasUnsavedChanges = true
            )
        }
    }

    /**
     * Remove a photo from the message.
     */
    fun removePhoto(uri: String) {
        _uiState.update {
            it.copy(
                attachedPhotos = it.attachedPhotos - uri,
                hasUnsavedChanges = true
            )
        }
    }

    /**
     * Add videos to the message.
     */
    fun addVideos(videoUris: List<String>) {
        _uiState.update {
            it.copy(
                attachedVideos = it.attachedVideos + videoUris,
                hasUnsavedChanges = true
            )
        }
    }

    /**
     * Remove a video from the message.
     */
    fun removeVideo(uri: String) {
        _uiState.update {
            it.copy(
                attachedVideos = it.attachedVideos - uri,
                hasUnsavedChanges = true
            )
        }
    }

    // =========================================================================
    // VOICE RECORDING METHODS
    // =========================================================================

    /**
     * Start voice recording using AudioRecorderManager.
     * Returns true if recording started successfully.
     */
    fun startRecording(): Boolean {
        val uri = audioRecorderManager.startRecording()
        return uri != null
    }

    /**
     * Stop voice recording and save the URI.
     * The result is automatically handled via state observation.
     */
    fun stopRecording() {
        val result = audioRecorderManager.stopRecording()
        if (result != null) {
            val (recordingUri, recordingDuration) = result
            _uiState.update {
                it.copy(
                    voiceRecordingUri = recordingUri.toString(),
                    voiceRecordingDuration = recordingDuration,
                    hasUnsavedChanges = true
                )
            }
        }
    }

    /**
     * Cancel voice recording without saving.
     */
    fun cancelRecording() {
        audioRecorderManager.cancelRecording()
    }

    /**
     * Remove the voice recording and delete the file.
     */
    fun removeVoiceRecording() {
        val uri = _uiState.value.voiceRecordingUri
        if (uri != null) {
            try {
                audioRecorderManager.deleteRecording(android.net.Uri.parse(uri))
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.w(TAG, "Failed to delete recording file", e)
            }
        }
        audioRecorderManager.stopPlayback()
        _uiState.update {
            it.copy(
                voiceRecordingUri = null,
                voiceRecordingDuration = 0,
                hasUnsavedChanges = true
            )
        }
    }

    /**
     * Toggle voice playback state.
     */
    fun toggleVoicePlayback() {
        val uri = _uiState.value.voiceRecordingUri ?: return
        if (_uiState.value.isPlayingVoice) {
            audioRecorderManager.togglePlaybackPause()
        } else {
            audioRecorderManager.startPlayback(android.net.Uri.parse(uri))
        }
    }

    /**
     * Stop voice playback.
     */
    fun stopVoicePlayback() {
        audioRecorderManager.stopPlayback()
    }

    /**
     * Clean up audio resources when ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        audioRecorderManager.release()
    }

    // =========================================================================
    // UNSAVED CHANGES DIALOG METHODS
    // =========================================================================

    /**
     * Show the discard changes confirmation dialog.
     */
    fun showDiscardDialog() {
        _uiState.update { it.copy(showDiscardDialog = true) }
    }

    /**
     * Hide the discard changes confirmation dialog.
     */
    fun hideDiscardDialog() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    /**
     * Check if there are unsaved changes and handle back navigation.
     * Returns true if navigation should proceed, false if dialog should be shown.
     */
    fun handleBackNavigation(): Boolean {
        val state = _uiState.value
        return if (state.hasContent && !state.isSaved) {
            showDiscardDialog()
            false
        } else {
            true
        }
    }
}
