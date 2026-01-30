package com.prody.prashant.ui.screens.haven

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.entity.HavenSessionEntity
import com.prody.prashant.data.repository.HavenRepository
import com.prody.prashant.domain.haven.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for Haven home screen (session list)
 */
data class HavenHomeUiState(
    val isLoading: Boolean = true,
    val sessions: List<HavenSessionEntity> = emptyList(),
    val stats: HavenStats? = null,
    val isConfigured: Boolean = true,
    val isOffline: Boolean = false,
    val configStatus: String? = null,
    val error: String? = null
)

/**
 * UI state for Haven chat session
 */
data class HavenChatUiState(
    val isLoading: Boolean = false,
    val sessionId: Long? = null,
    val sessionType: SessionType = SessionType.GENERAL,
    val messages: List<HavenMessage> = emptyList(),
    val moodBefore: Int? = null,
    val moodAfter: Int? = null,
    val isTyping: Boolean = false,
    val showCrisisResources: Boolean = false,
    val suggestedExercise: ExerciseType? = null,
    val summary: SessionSummary? = null,
    val isCompleted: Boolean = false,
    val error: String? = null
)

/**
 * UI state for exercise screen
 */
data class HavenExerciseUiState(
    val isLoading: Boolean = false,
    val exercise: GuidedExercise? = null,
    val currentStepIndex: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val elapsedSeconds: Int = 0,
    val isCompleted: Boolean = false,
    val error: String? = null
)

/**
 * Haven ViewModel - Manages all Haven UI states
 */
@HiltViewModel
class HavenViewModel @Inject constructor(
    private val havenRepository: HavenRepository
) : ViewModel() {

    // Home state
    private val _homeState = MutableStateFlow(HavenHomeUiState())
    val homeState: StateFlow<HavenHomeUiState> = _homeState.asStateFlow()

    // Chat state
    private val _chatState = MutableStateFlow(HavenChatUiState())
    val chatState: StateFlow<HavenChatUiState> = _chatState.asStateFlow()

    // Exercise state
    private val _exerciseState = MutableStateFlow(HavenExerciseUiState())
    val exerciseState: StateFlow<HavenExerciseUiState> = _exerciseState.asStateFlow()

    init {
        loadHomeData()
    }

    // ==================== HOME SCREEN ====================

    private fun loadHomeData() {
        viewModelScope.launch {
            _homeState.update { it.copy(isLoading = true) }

            // Check if Haven is configured
            val isConfigured = havenRepository.isConfigured()
            val isOffline = havenRepository.isOffline()
            val configStatus = if (!isConfigured && !isOffline) havenRepository.getConfigurationStatus() else null
            
            _homeState.update { 
                it.copy(
                    isConfigured = isConfigured,
                    isOffline = isOffline,
                    configStatus = configStatus
                ) 
            }

            // Load sessions
            havenRepository.getSessions().collect { sessions ->
                _homeState.update { it.copy(sessions = sessions, isLoading = false) }
            }
        }

        viewModelScope.launch {
            // Load stats - non-critical, use default stats on failure
            havenRepository.getStats().fold(
                onSuccess = { stats ->
                    _homeState.update { it.copy(stats = stats) }
                },
                onFailure = { e ->
                    android.util.Log.w("HavenViewModel", "Failed to load stats: ${e.message}")
                    // Stats are non-critical, UI will show default/empty stats
                }
            )
        }
    }

    fun refreshHome() {
        loadHomeData()
    }

    // ==================== CHAT SESSION ====================

    /**
     * Start a new Haven session
     */
    fun startSession(
        sessionType: SessionType,
        moodBefore: Int? = null,
        userName: String? = null
    ) {
        viewModelScope.launch {
            _chatState.update {
                it.copy(
                    isLoading = true,
                    sessionType = sessionType,
                    moodBefore = moodBefore,
                    messages = emptyList(),
                    isCompleted = false,
                    summary = null,
                    error = null
                )
            }

            havenRepository.startSession(
                sessionType = sessionType,
                moodBefore = moodBefore,
                userName = userName
            ).fold(
                onSuccess = { (sessionId, aiResponse) ->
                    val initialMessage = HavenMessage(
                        content = aiResponse.message,
                        isUser = false,
                        techniqueUsed = aiResponse.techniqueApplied,
                        exerciseSuggested = aiResponse.suggestedExercise,
                        isCrisisResponse = aiResponse.isCrisisDetected,
                        recalledMessage = aiResponse.recalledContent
                    )
                    _chatState.update {
                        it.copy(
                            isLoading = false,
                            sessionId = sessionId,
                            messages = listOf(initialMessage),
                            showCrisisResources = aiResponse.isCrisisDetected,
                            suggestedExercise = aiResponse.suggestedExercise
                        )
                    }
                },
                onFailure = { error ->
                    _chatState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to start session: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    /**
     * Resume an existing session
     */
    fun resumeSession(sessionId: Long) {
        viewModelScope.launch {
            _chatState.update { it.copy(isLoading = true, error = null) }

            havenRepository.getSessionWithMessages(sessionId).fold(
                onSuccess = { (session, messages) ->
                    _chatState.update {
                        it.copy(
                            isLoading = false,
                            sessionId = sessionId,
                            sessionType = SessionType.fromString(session.sessionType),
                            messages = messages,
                            moodBefore = session.moodBefore,
                            isCompleted = session.isCompleted,
                            showCrisisResources = session.containedCrisisDetection
                        )
                    }
                },
                onFailure = { error ->
                    _chatState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load session: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    /**
     * Send a message in the current session
     */
    fun sendMessage(message: String) {
        val sessionId = _chatState.value.sessionId ?: return

        viewModelScope.launch {
            // Add user message immediately
            val userMessage = HavenMessage(content = message, isUser = true)
            _chatState.update {
                it.copy(
                    messages = it.messages + userMessage,
                    isTyping = true,
                    error = null
                )
            }

            // Get AI response
            havenRepository.continueConversation(sessionId, message).fold(
                onSuccess = { aiResponse ->
                    val aiMessage = HavenMessage(
                        content = aiResponse.message,
                        isUser = false,
                        techniqueUsed = aiResponse.techniqueApplied,
                        exerciseSuggested = aiResponse.suggestedExercise,
                        isCrisisResponse = aiResponse.isCrisisDetected,
                        recalledMessage = aiResponse.recalledContent
                    )
                    _chatState.update {
                        it.copy(
                            messages = it.messages + aiMessage,
                            isTyping = false,
                            showCrisisResources = it.showCrisisResources || aiResponse.isCrisisDetected,
                            suggestedExercise = aiResponse.suggestedExercise
                        )
                    }
                },
                onFailure = { error ->
                    _chatState.update {
                        it.copy(
                            isTyping = false,
                            error = "Failed to send message: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    /**
     * Complete the current session
     */
    fun completeSession(moodAfter: Int? = null) {
        val sessionId = _chatState.value.sessionId ?: return

        viewModelScope.launch {
            _chatState.update { it.copy(isLoading = true) }

            havenRepository.completeSession(sessionId, moodAfter).fold(
                onSuccess = { summary ->
                    _chatState.update {
                        it.copy(
                            isLoading = false,
                            moodAfter = moodAfter,
                            summary = summary,
                            isCompleted = true
                        )
                    }
                },
                onFailure = { error ->
                    _chatState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to complete session: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    /**
     * Reset chat state for new session
     */
    fun resetChatState() {
        _chatState.value = HavenChatUiState()
    }

    fun clearChatError() {
        _chatState.update { it.copy(error = null) }
    }

    // ==================== EXERCISES ====================

    /**
     * Start a guided exercise
     */
    fun startExercise(exerciseType: ExerciseType, fromSessionId: Long? = null) {
        viewModelScope.launch {
            _exerciseState.update { it.copy(isLoading = true, error = null) }

            havenRepository.startExercise(exerciseType, fromSessionId = fromSessionId).fold(
                onSuccess = { exercise ->
                    _exerciseState.update {
                        it.copy(
                            isLoading = false,
                            exercise = exercise,
                            currentStepIndex = 0,
                            isRunning = false,
                            isPaused = false,
                            elapsedSeconds = 0,
                            isCompleted = false
                        )
                    }
                },
                onFailure = { error ->
                    _exerciseState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load exercise: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    /**
     * Control exercise playback
     */
    fun playExercise() {
        _exerciseState.update { it.copy(isRunning = true, isPaused = false) }
    }

    fun pauseExercise() {
        _exerciseState.update { it.copy(isPaused = true) }
    }

    fun resumeExercise() {
        _exerciseState.update { it.copy(isPaused = false) }
    }

    fun nextStep() {
        val currentState = _exerciseState.value
        val exercise = currentState.exercise ?: return
        val nextIndex = currentState.currentStepIndex + 1

        if (nextIndex >= exercise.steps.size) {
            _exerciseState.update { it.copy(isCompleted = true, isRunning = false) }
        } else {
            _exerciseState.update { it.copy(currentStepIndex = nextIndex) }
        }
    }

    fun updateElapsedTime(seconds: Int) {
        _exerciseState.update { it.copy(elapsedSeconds = seconds) }
    }

    /**
     * Complete exercise and record
     */
    fun completeExercise(notes: String? = null) {
        val state = _exerciseState.value
        val exercise = state.exercise ?: return
        val sessionId = _chatState.value.sessionId

        viewModelScope.launch {
            havenRepository.completeExercise(
                exerciseType = exercise.type,
                durationSeconds = state.elapsedSeconds,
                notes = notes,
                fromSessionId = sessionId,
                wasCompleted = state.isCompleted,
                completionRate = if (exercise.steps.isNotEmpty()) {
                    state.currentStepIndex.toFloat() / exercise.steps.size
                } else 1f
            )

            // Reset exercise state
            _exerciseState.value = HavenExerciseUiState()
        }
    }

    fun resetExerciseState() {
        _exerciseState.value = HavenExerciseUiState()
    }

    fun clearExerciseError() {
        _exerciseState.update { it.copy(error = null) }
    }

    // ==================== SESSION MANAGEMENT ====================

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            havenRepository.deleteSession(sessionId)
        }
    }

    fun clearHomeError() {
        _homeState.update { it.copy(error = null) }
    }

    /**
     * Manually trigger the crisis resources banner
     */
    fun showCrisisResources() {
        _chatState.update { it.copy(showCrisisResources = true) }
    }
}
