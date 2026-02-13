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
import com.prody.prashant.domain.gamification.GameSessionManager
import com.prody.prashant.domain.gamification.SessionResult
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.theme.JournalTemplate
import com.prody.prashant.util.AudioRecorderManager
import com.prody.prashant.util.BuddhaWisdom
import com.prody.prashant.util.TranscriptionError
import com.prody.prashant.util.TranscriptionResult
import com.prody.prashant.util.VoiceTranscriptionService
import com.prody.prashant.domain.validation.ContentValidation
import com.prody.prashant.domain.validation.ContentValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TranscriptionChoice {
    NOW, LATER, NEVER
}

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
    // Session result (replaces spam feedback)
    val sessionResult: SessionResult? = null,
    val showSessionResult: Boolean = false,
    // Content validation state - provides real-time feedback
    val contentValidation: ContentValidation = ContentValidation.Empty,
    val contentCompletionProgress: Float = 0f,
    val validationHint: String? = null,
    // Voice transcription state
    val isTranscribing: Boolean = false,
    val transcriptionText: String = "",
    val transcriptionPartial: String = "",
    val transcriptionSoundLevel: Float = 0f,
    val transcriptionAvailable: Boolean = true,
    // Transcription choice UI
    val showTranscriptionChoice: Boolean = false,
    val pendingTranscriptionUri: String? = null
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
    private val gameSessionManager: GameSessionManager,
    private val buddhaAiRepository: BuddhaAiRepository,
    private val audioRecorderManager: AudioRecorderManager,
    private val voiceTranscriptionService: VoiceTranscriptionService
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewJournalEntryUiState())
    val uiState: StateFlow<NewJournalEntryUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "NewJournalEntryViewModel"
    }

    init {
        loadAiSettings()
        observeAudioRecorderStates()
        checkTranscriptionAvailability()
    }

    /**
     * Check if voice transcription is available on this device.
     */
    private fun checkTranscriptionAvailability() {
        _uiState.update {
            it.copy(transcriptionAvailable = voiceTranscriptionService.isAvailable())
        }
    }

    /**
     * Observe audio recorder state changes and update UI state accordingly.
     * Uses combine to merge all audio states into a single flow subscription,
     * preventing multiple coroutine leaks on configuration changes.
     */
    private fun observeAudioRecorderStates() {
        viewModelScope.launch {
            combine(
                audioRecorderManager.isRecording,
                audioRecorderManager.recordingDuration,
                audioRecorderManager.isPlaying,
                audioRecorderManager.error
            ) { isRecording, duration, isPlaying, error ->
                AudioRecorderState(isRecording, duration, isPlaying, error)
            }.collect { audioState ->
                _uiState.update {
                    it.copy(
                        isRecording = audioState.isRecording,
                        recordingTimeElapsed = audioState.duration,
                        isPlayingVoice = audioState.isPlaying
                    )
                }
                audioState.error?.let { errorMsg ->
                    _uiState.update { it.copy(error = errorMsg) }
                    audioRecorderManager.clearError()
                }
            }
        }
    }

    /**
     * Helper data class for combining audio recorder states.
     */
    private data class AudioRecorderState(
        val isRecording: Boolean,
        val duration: Long,
        val isPlaying: Boolean,
        val error: String?
    )

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
                com.prody.prashant.util.AppLogger.e(TAG, "Error loading AI settings", e)
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
        val validation = ContentValidator.validate(content)
        val completionProgress = ContentValidator.getCompletionProgress(content)
        _uiState.update {
            it.copy(
                content = content,
                wordCount = wordCount,
                hasUnsavedChanges = true,
                contentValidation = validation,
                contentCompletionProgress = completionProgress,
                validationHint = validation.getMessage()
            )
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

            // Guard against double-taps - prevent duplicate saves
            if (state.isSaving || state.isSaved) {
                com.prody.prashant.util.AppLogger.d(TAG, "Save already in progress or completed, ignoring duplicate save request")
                return@launch
            }

            // Validate content before saving
            val validation = state.contentValidation
            when (validation) {
                is ContentValidation.Empty -> {
                    _uiState.update { it.copy(error = "Please write something before saving") }
                    return@launch
                }
                is ContentValidation.TooShort -> {
                    _uiState.update {
                        it.copy(error = "Your entry needs a bit more detail. ${validation.suggestion}")
                    }
                    return@launch
                }
                is ContentValidation.TooVague -> {
                    // TooVague can still be saved, but show the hint
                    _uiState.update { it.copy(validationHint = validation.suggestion) }
                    // Continue with save - vague content is still valid
                }
                is ContentValidation.MinimalContent -> {
                    // MinimalContent is fine to save, just a hint
                }
                is ContentValidation.Valid -> {
                    // Perfect - proceed
                }
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

                // Use GameSessionManager for clean session result (no spam feedback)
                val sessionResult = gameSessionManager.completeReflectSession(
                    entryId = entryId,
                    wordCount = state.wordCount,
                    content = state.content,
                    mood = state.selectedMood.name
                )
                com.prody.prashant.util.AppLogger.d(TAG, "Journal entry saved, session result: ${sessionResult.rewards.totalXp} XP")

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true,
                        savedEntryId = entryId,
                        sessionResult = sessionResult,
                        showSessionResult = true
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
                    com.prody.prashant.util.AppLogger.d(TAG, "Buddha response generated via Gemini")
                    return geminiResult.data
                }
                is GeminiResult.Error -> {
                    com.prody.prashant.util.AppLogger.w(TAG, "Gemini failed: ${geminiResult.message}, trying OpenRouter fallback")
                }
                is GeminiResult.ApiKeyNotSet -> {
                    com.prody.prashant.util.AppLogger.d(TAG, "Gemini API key not set, trying OpenRouter fallback")
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
                com.prody.prashant.util.AppLogger.d(TAG, "Buddha response generated via OpenRouter fallback")
                return response
            }.onFailure { e ->
                com.prody.prashant.util.AppLogger.w(TAG, "OpenRouter fallback failed: ${e.message}, using local wisdom")
            }
        }

        // Tier 3: Local BuddhaWisdom - always available, guaranteed response
        com.prody.prashant.util.AppLogger.d(TAG, "Using local BuddhaWisdom for response")
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
                        com.prody.prashant.util.AppLogger.w(TAG, "Failed to persist insight to DB", e)
                    }

                    _uiState.update {
                        it.copy(
                            journalInsight = insight,
                            isGeneratingInsight = false,
                            showInsightCard = !insight.isInsufficientContext || insight.question != null
                        )
                    }
                    com.prody.prashant.util.AppLogger.d(TAG, "Journal insight generated: ${insight.emotionLabel}, snippet: ${com.prody.prashant.util.AppLogger.redactJournalText(insight.snippet)}")
                } else {
                    _uiState.update { it.copy(isGeneratingInsight = false) }
                }
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error generating journal insight", e)
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
     * Dismiss the session result card.
     */
    fun dismissSessionResult() {
        _uiState.update { it.copy(showSessionResult = false) }
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
                    hasUnsavedChanges = true,
                    // If transcription is available, ask the user
                    showTranscriptionChoice = it.transcriptionAvailable,
                    pendingTranscriptionUri = recordingUri.toString()
                )
            }
        }
    }

    /**
     * Handle the user's choice for transcription.
     */
    fun onTranscriptionChoiceSelected(choice: TranscriptionChoice) {
        val uri = _uiState.value.pendingTranscriptionUri
        
        _uiState.update { it.copy(showTranscriptionChoice = false, pendingTranscriptionUri = null) }
        
        when (choice) {
            TranscriptionChoice.NOW -> {
                // For now, we use the existing startTranscription method 
                // which uses real-time transcription. 
                // TODO: Implement file-based transcription if available in VoiceTranscriptionService
                startTranscription()
            }
            TranscriptionChoice.LATER -> {
                // Queue for later sync (handled by SyncManager)
                uri?.let {
                    // Logic to be implemented in SyncManager or a dedicated TranscriptionWorker
                    com.prody.prashant.util.AppLogger.d(TAG, "Queued recording for later transcription: $it")
                }
            }
            TranscriptionChoice.NEVER -> {
                // Do nothing
                com.prody.prashant.util.AppLogger.d(TAG, "User chose not to transcribe recording")
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
     * Clean up audio and transcription resources when ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        audioRecorderManager.release()
        voiceTranscriptionService.release()
    }

    // =========================================================================
    // VOICE TRANSCRIPTION METHODS
    // =========================================================================

    /**
     * Start voice-to-text transcription.
     * Appends transcribed text to the current content.
     */
    fun startTranscription() {
        if (!voiceTranscriptionService.isAvailable()) {
            _uiState.update { it.copy(error = "Voice transcription is not available on this device") }
            return
        }

        if (_uiState.value.isTranscribing) {
            com.prody.prashant.util.AppLogger.w(TAG, "Transcription already in progress")
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isTranscribing = true,
                    transcriptionText = "",
                    transcriptionPartial = ""
                )
            }

            voiceTranscriptionService.transcribe()
                .collect { result ->
                    when (result) {
                        is TranscriptionResult.Ready -> {
                            com.prody.prashant.util.AppLogger.d(TAG, "Transcription ready")
                        }
                        is TranscriptionResult.Started -> {
                            com.prody.prashant.util.AppLogger.d(TAG, "Transcription started")
                        }
                        is TranscriptionResult.Partial -> {
                            _uiState.update { it.copy(transcriptionPartial = result.text) }
                        }
                        is TranscriptionResult.Final -> {
                            // Append transcribed text to content
                            val currentContent = _uiState.value.content
                            val separator = if (currentContent.isNotEmpty() && !currentContent.endsWith(" ")) " " else ""
                            val newContent = currentContent + separator + result.text

                            updateContent(newContent)

                            _uiState.update {
                                it.copy(
                                    isTranscribing = false,
                                    transcriptionText = result.text,
                                    transcriptionPartial = ""
                                )
                            }
                            com.prody.prashant.util.AppLogger.d(TAG, "Transcription complete: ${com.prody.prashant.util.AppLogger.redactJournalText(result.text)}")
                        }
                        is TranscriptionResult.SoundLevel -> {
                            _uiState.update { it.copy(transcriptionSoundLevel = result.level) }
                        }
                        is TranscriptionResult.Ended -> {
                            com.prody.prashant.util.AppLogger.d(TAG, "Speech ended, processing...")
                        }
                        is TranscriptionResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isTranscribing = false,
                                    error = result.error.message
                                )
                            }
                            com.prody.prashant.util.AppLogger.e(TAG, "Transcription error: ${result.error.message}")
                        }
                    }
                }
        }
    }

    /**
     * Stop the current voice transcription.
     */
    fun stopTranscription() {
        voiceTranscriptionService.stopListening()
        _uiState.update {
            it.copy(
                isTranscribing = false,
                transcriptionPartial = ""
            )
        }
    }

    /**
     * Cancel the current voice transcription without saving.
     */
    fun cancelTranscription() {
        voiceTranscriptionService.cancel()
        _uiState.update {
            it.copy(
                isTranscribing = false,
                transcriptionPartial = "",
                transcriptionSoundLevel = 0f
            )
        }
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
