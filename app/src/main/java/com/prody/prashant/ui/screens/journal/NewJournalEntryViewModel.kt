package com.prody.prashant.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.ai.BuddhaAiRepository
import com.prody.prashant.data.ai.BuddhaAiResult
import com.prody.prashant.data.ai.GeminiResult
import com.prody.prashant.data.ai.GeminiService
import com.prody.prashant.data.ai.JournalInsightResult
import com.prody.prashant.data.ai.OpenRouterService
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.gamification.GamificationService
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.theme.JournalTemplate
import com.prody.prashant.util.AudioRecorderManager
import com.prody.prashant.util.BuddhaWisdom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewJournalEntryUiState(
    val title: String = "",
    val content: String = "",
    val selectedMood: Mood = Mood.CALM,
    val moodIntensity: Int = 5,
    val wordCount: Int = 0,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isGeneratingAiResponse: Boolean = false,
    val error: String? = null,
    val selectedTemplate: JournalTemplate? = null,
    val showTemplateSelector: Boolean = false,
    val availableTemplates: List<JournalTemplate> = JournalTemplate.all,
    val buddhaAiEnabled: Boolean = true,
    val geminiConfigured: Boolean = false,
    val openRouterConfigured: Boolean = false,
    // Post-save AI insights (non-blocking)
    val savedEntryId: Long? = null,
    val journalInsight: JournalInsightResult? = null,
    val isGeneratingInsight: Boolean = false,
    val showInsightCard: Boolean = false,
    // Media attachments
    val attachedPhotos: List<String> = emptyList(), // URIs as strings
    val attachedVideos: List<String> = emptyList(), // URIs as strings
    // Voice recording state
    val isRecording: Boolean = false,
    val voiceRecordingUri: String? = null,
    val voiceRecordingDuration: Long = 0, // milliseconds
    val recordingTimeElapsed: Long = 0, // milliseconds for UI display
    val isPlayingVoice: Boolean = false,
    // Unsaved changes tracking
    val hasUnsavedChanges: Boolean = false,
    val showDiscardDialog: Boolean = false,
    // Progress feedback - points awarded on save
    val pointsAwarded: Int = 0,
    val showSaveSuccess: Boolean = false
) {
    // Check if there are any unsaved changes
    val hasContent: Boolean
        get() = title.isNotBlank() || content.isNotBlank() ||
                attachedPhotos.isNotEmpty() || attachedVideos.isNotEmpty() ||
                voiceRecordingUri != null
}

@HiltViewModel
class NewJournalEntryViewModel @Inject constructor(
    private val journalDao: JournalDao,
    private val userDao: UserDao,
    private val geminiService: GeminiService,
    private val openRouterService: OpenRouterService,
    private val preferencesManager: PreferencesManager,
    private val gamificationService: GamificationService,
    private val buddhaAiRepository: BuddhaAiRepository,
    private val audioRecorderManager: AudioRecorderManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewJournalEntryUiState())
    val uiState: StateFlow<NewJournalEntryUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "NewJournalEntryViewModel"
    }

    init {
        loadAiSettings()
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

    private fun loadAiSettings() {
        viewModelScope.launch {
            try {
                combine(
                    preferencesManager.buddhaAiEnabled,
                    preferencesManager.geminiApiKey
                ) { enabled, apiKey ->
                    Triple(
                        enabled,
                        apiKey.isNotBlank() || GeminiService.isApiKeyConfiguredInBuildConfig(),
                        openRouterService.isConfigured()
                    )
                }.collect { (enabled, geminiConfigured, openRouterConfigured) ->
                    _uiState.update {
                        it.copy(
                            buddhaAiEnabled = enabled,
                            geminiConfigured = geminiConfigured,
                            openRouterConfigured = openRouterConfigured
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading AI settings", e)
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update {
            it.copy(title = title, hasUnsavedChanges = true)
        }
    }

    fun updateContent(content: String) {
        val wordCount = if (content.isBlank()) 0 else content.trim().split("\\s+".toRegex()).size
        _uiState.update {
            it.copy(content = content, wordCount = wordCount, hasUnsavedChanges = true)
        }
    }

    fun updateMood(mood: Mood) {
        _uiState.update { it.copy(selectedMood = mood, hasUnsavedChanges = true) }
    }

    fun updateMoodIntensity(intensity: Int) {
        _uiState.update { it.copy(moodIntensity = intensity.coerceIn(1, 10)) }
    }

    fun toggleTemplateSelector() {
        _uiState.update { it.copy(showTemplateSelector = !it.showTemplateSelector) }
    }

    fun selectTemplate(template: JournalTemplate) {
        val templateContent = template.prompts.joinToString("\n")
        _uiState.update {
            it.copy(
                selectedTemplate = template,
                content = templateContent,
                wordCount = templateContent.trim().split("\\s+".toRegex()).size,
                showTemplateSelector = false
            )
        }
    }

    fun clearTemplate() {
        _uiState.update {
            it.copy(
                selectedTemplate = null,
                content = "",
                wordCount = 0
            )
        }
    }

    fun saveEntry() {
        viewModelScope.launch {
            val state = _uiState.value

            // Don't save empty entries
            if (state.content.isBlank()) {
                _uiState.update { it.copy(error = "Please write something before saving") }
                return@launch
            }

            _uiState.update { it.copy(isSaving = true, isGeneratingAiResponse = true, error = null) }

            try {
                // Generate Buddha's response - use Gemini if available, fallback to local
                val buddhaResponse = generateBuddhaResponse(state)

                _uiState.update { it.copy(isGeneratingAiResponse = false) }

                val entry = JournalEntryEntity(
                    title = state.title,
                    content = state.content,
                    mood = state.selectedMood.name,
                    moodIntensity = state.moodIntensity,
                    buddhaResponse = buddhaResponse,
                    wordCount = state.wordCount,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    attachedPhotos = state.attachedPhotos.joinToString(","),
                    attachedVideos = state.attachedVideos.joinToString(","),
                    voiceRecordingUri = state.voiceRecordingUri,
                    voiceRecordingDuration = state.voiceRecordingDuration,
                    templateId = state.selectedTemplate?.id
                )

                val entryId = journalDao.insertEntry(entry)

                // Use GamificationService for all points and achievements
                val points = gamificationService.recordActivity(
                    GamificationService.ActivityType.JOURNAL_ENTRY
                )
                android.util.Log.d(TAG, "Journal entry saved, $points points awarded")

                // Check for time-based achievements (early bird, night owl)
                gamificationService.checkTimeBasedAchievements()

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true,
                        savedEntryId = entryId,
                        pointsAwarded = points,
                        showSaveSuccess = true
                    )
                }

                // Trigger non-blocking insight generation after save
                generateJournalInsight(entryId, state)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isGeneratingAiResponse = false,
                        error = "Failed to save: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Generates Buddha's response using a multi-tier fallback strategy:
     * 1. Primary: Google Gemini AI (if configured and enabled)
     * 2. Secondary: OpenRouter (if configured, when Gemini fails)
     * 3. Tertiary: Local BuddhaWisdom (always available, never fails)
     *
     * This ensures users always receive a meaningful response regardless of API availability.
     */
    private suspend fun generateBuddhaResponse(state: NewJournalEntryUiState): String {
        // Tier 1: Try Gemini AI if enabled and configured
        if (state.buddhaAiEnabled && geminiService.isConfigured()) {
            val geminiResult = geminiService.generateJournalResponse(
                content = state.content,
                mood = state.selectedMood,
                moodIntensity = state.moodIntensity,
                wordCount = state.wordCount
            )

            when (geminiResult) {
                is GeminiResult.Success -> {
                    android.util.Log.d(TAG, "Buddha response generated via Gemini")
                    return geminiResult.data
                }
                is GeminiResult.Error -> {
                    android.util.Log.w(TAG, "Gemini failed: ${geminiResult.message}, trying OpenRouter fallback")
                }
                is GeminiResult.ApiKeyNotSet -> {
                    android.util.Log.d(TAG, "Gemini API key not set, trying OpenRouter fallback")
                }
                is GeminiResult.Loading -> {
                    // Shouldn't happen for non-streaming calls, but handle gracefully
                }
            }
        }

        // Tier 2: Try OpenRouter as fallback if Gemini failed or is not available
        if (state.buddhaAiEnabled && openRouterService.isConfigured()) {
            val openRouterResult = openRouterService.generateJournalResponse(
                content = state.content,
                mood = state.selectedMood,
                moodIntensity = state.moodIntensity,
                wordCount = state.wordCount
            )

            openRouterResult.onSuccess { response ->
                android.util.Log.d(TAG, "Buddha response generated via OpenRouter fallback")
                return response
            }.onFailure { e ->
                android.util.Log.w(TAG, "OpenRouter fallback failed: ${e.message}, using local wisdom")
            }
        }

        // Tier 3: Local BuddhaWisdom - always available, guaranteed response
        android.util.Log.d(TAG, "Using local BuddhaWisdom for response")
        return BuddhaWisdom.generateResponse(
            content = state.content,
            mood = state.selectedMood,
            wordCount = state.wordCount
        )
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Generate AI insights for a journal entry (non-blocking, after save).
     * Outputs: emotion label, themes (2-4), short reflection insight.
     * No clinical tone - warm and personal.
     */
    private fun generateJournalInsight(entryId: Long, state: NewJournalEntryUiState) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isGeneratingInsight = true) }

                val result = buddhaAiRepository.analyzeJournalEntry(
                    content = state.content,
                    mood = state.selectedMood,
                    moodIntensity = state.moodIntensity,
                    wordCount = state.wordCount
                )

                val insight = result.getOrNull()
                if (insight != null) {
                    // Update the journal entry with AI insights
                    try {
                        val existingEntry = journalDao.getEntryById(entryId)
                        existingEntry?.let { entry ->
                            // Compute content hash for cache invalidation
                            val contentHash = state.content.hashCode().toString(16)
                            journalDao.updateEntry(
                                entry.copy(
                                    aiEmotionLabel = insight.emotionLabel,
                                    aiThemes = insight.themes.joinToString(","),
                                    aiInsight = insight.insight,
                                    aiInsightGenerated = !insight.isInsufficientContext,
                                    aiSnippet = insight.snippet,
                                    aiQuestion = insight.question,
                                    aiSuggestion = insight.suggestion,
                                    aiContentHash = contentHash
                                )
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.w(TAG, "Failed to persist insight to DB", e)
                    }

                    _uiState.update {
                        it.copy(
                            journalInsight = insight,
                            isGeneratingInsight = false,
                            showInsightCard = !insight.isInsufficientContext || insight.question != null
                        )
                    }
                    android.util.Log.d(TAG, "Journal insight generated: ${insight.emotionLabel}, snippet: ${insight.snippet}")
                } else {
                    _uiState.update { it.copy(isGeneratingInsight = false) }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error generating journal insight", e)
                _uiState.update { it.copy(isGeneratingInsight = false) }
            }
        }
    }

    /**
     * Dismiss the insight card.
     */
    fun dismissInsightCard() {
        _uiState.update { it.copy(showInsightCard = false) }
    }

    /**
     * Dismiss the save success feedback.
     */
    fun dismissSaveSuccess() {
        _uiState.update { it.copy(showSaveSuccess = false) }
    }

    // =========================================================================
    // MEDIA ATTACHMENT METHODS
    // =========================================================================

    /**
     * Add photos to the journal entry.
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
     * Remove a photo from the journal entry.
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
     * Add videos to the journal entry.
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
     * Remove a video from the journal entry.
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
                android.util.Log.w(TAG, "Failed to delete recording file", e)
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
