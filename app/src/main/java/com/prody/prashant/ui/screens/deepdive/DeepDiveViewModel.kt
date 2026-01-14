package com.prody.prashant.ui.screens.deepdive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.entity.DeepDiveEntity
import com.prody.prashant.domain.common.fold
import com.prody.prashant.domain.deepdive.*
import com.prody.prashant.domain.repository.DeepDiveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Deep Dive feature - Weekly themed reflection sessions.
 *
 * Manages both the home dashboard (list view) and active session state.
 */
@HiltViewModel
class DeepDiveViewModel @Inject constructor(
    private val deepDiveRepository: DeepDiveRepository
) : ViewModel() {

    // =========================================================================
    // HOME DASHBOARD STATE
    // =========================================================================

    private val _homeState = MutableStateFlow(DeepDiveHomeUiState())
    val homeState: StateFlow<DeepDiveHomeUiState> = _homeState.asStateFlow()

    // =========================================================================
    // SESSION STATE
    // =========================================================================

    private val _sessionState = MutableStateFlow(DeepDiveSessionUiState())
    val sessionState: StateFlow<DeepDiveSessionUiState> = _sessionState.asStateFlow()

    init {
        loadHomeData()
    }

    // =========================================================================
    // HOME DASHBOARD METHODS
    // =========================================================================

    fun loadHomeData() {
        viewModelScope.launch {
            _homeState.update { it.copy(isLoading = true) }

            // Load scheduled deep dives
            deepDiveRepository.getScheduledDeepDives()
                .catch { e ->
                    _homeState.update { it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    ) }
                }
                .collect { scheduled ->
                    _homeState.update { it.copy(
                        scheduledSessions = scheduled,
                        isLoading = false
                    ) }
                }
        }

        viewModelScope.launch {
            // Load completed deep dives
            deepDiveRepository.getCompletedDeepDives()
                .catch { /* Ignore errors for secondary data */ }
                .collect { completed ->
                    _homeState.update { it.copy(
                        completedSessions = completed
                    ) }
                }
        }

        viewModelScope.launch {
            // Load analytics
            deepDiveRepository.getAnalytics().fold(
                onSuccess = { analytics ->
                    _homeState.update { it.copy(analytics = analytics) }
                },
                onError = { /* Ignore analytics errors */ }
            )
        }
    }

    fun scheduleNewDeepDive(theme: DeepDiveTheme? = null) {
        viewModelScope.launch {
            _homeState.update { it.copy(isScheduling = true) }

            deepDiveRepository.scheduleNextDeepDive().fold(
                onSuccess = { entity ->
                    _homeState.update { it.copy(
                        isScheduling = false,
                        showScheduleSuccess = true
                    ) }
                    loadHomeData()
                },
                onError = { error ->
                    _homeState.update { it.copy(
                        isScheduling = false,
                        errorMessage = error.userMessage
                    ) }
                }
            )
        }
    }

    fun rescheduleDeepDive(deepDiveId: Long, newDate: Long) {
        viewModelScope.launch {
            deepDiveRepository.rescheduleDeepDive(deepDiveId, newDate).fold(
                onSuccess = { loadHomeData() },
                onError = { error ->
                    _homeState.update { it.copy(errorMessage = error.userMessage) }
                }
            )
        }
    }

    fun deleteDeepDive(deepDiveId: Long) {
        viewModelScope.launch {
            deepDiveRepository.softDeleteDeepDive(deepDiveId).fold(
                onSuccess = { loadHomeData() },
                onError = { error ->
                    _homeState.update { it.copy(errorMessage = error.userMessage) }
                }
            )
        }
    }

    fun dismissScheduleSuccess() {
        _homeState.update { it.copy(showScheduleSuccess = false) }
    }

    fun dismissHomeError() {
        _homeState.update { it.copy(errorMessage = null) }
    }

    // =========================================================================
    // SESSION METHODS
    // =========================================================================

    fun startSession(deepDiveId: Long) {
        viewModelScope.launch {
            _sessionState.update { it.copy(isLoading = true) }

            deepDiveRepository.startSession(deepDiveId).fold(
                onSuccess = { session ->
                    _sessionState.update { it.copy(
                        isLoading = false,
                        session = session,
                        currentProgress = session.progress
                    ) }
                },
                onError = { error ->
                    _sessionState.update { it.copy(
                        isLoading = false,
                        errorMessage = error.userMessage
                    ) }
                }
            )
        }
    }

    fun loadSession(deepDiveId: Long) {
        viewModelScope.launch {
            _sessionState.update { it.copy(isLoading = true) }

            deepDiveRepository.getSession(deepDiveId).fold(
                onSuccess = { session ->
                    if (session != null) {
                        _sessionState.update { it.copy(
                            isLoading = false,
                            session = session,
                            currentProgress = session.progress,
                            openingText = session.entity.openingReflection.orEmpty(),
                            coreText = session.entity.coreResponse.orEmpty(),
                            insightText = session.entity.keyInsight.orEmpty(),
                            commitmentText = session.entity.commitmentStatement.orEmpty(),
                            moodBefore = session.entity.moodBefore,
                            moodAfter = session.entity.moodAfter
                        ) }
                    } else {
                        _sessionState.update { it.copy(
                            isLoading = false,
                            errorMessage = "Session not found"
                        ) }
                    }
                },
                onError = { error ->
                    _sessionState.update { it.copy(
                        isLoading = false,
                        errorMessage = error.userMessage
                    ) }
                }
            )
        }
    }

    fun onMoodBeforeSelected(mood: Int) {
        viewModelScope.launch {
            _sessionState.update { it.copy(moodBefore = mood) }
            val session = _sessionState.value.session ?: return@launch
            deepDiveRepository.saveMoodRating(session.entity.id, mood, null)
        }
    }

    fun onOpeningTextChanged(text: String) {
        _sessionState.update { it.copy(openingText = text) }
    }

    fun saveOpeningAndAdvance() {
        viewModelScope.launch {
            val state = _sessionState.value
            val session = state.session ?: return@launch

            _sessionState.update { it.copy(isSaving = true) }

            deepDiveRepository.saveOpeningReflection(session.entity.id, state.openingText).fold(
                onSuccess = {
                    deepDiveRepository.updateCurrentStep(session.entity.id, DeepDiveProgress.CORE)
                    _sessionState.update { it.copy(
                        isSaving = false,
                        currentProgress = DeepDiveProgress.CORE
                    ) }
                },
                onError = { error ->
                    _sessionState.update { it.copy(
                        isSaving = false,
                        errorMessage = error.userMessage
                    ) }
                }
            )
        }
    }

    fun onCoreTextChanged(text: String) {
        _sessionState.update { it.copy(coreText = text) }
    }

    fun saveCoreAndAdvance() {
        viewModelScope.launch {
            val state = _sessionState.value
            val session = state.session ?: return@launch

            _sessionState.update { it.copy(isSaving = true) }

            deepDiveRepository.saveCoreResponse(session.entity.id, state.coreText).fold(
                onSuccess = {
                    deepDiveRepository.updateCurrentStep(session.entity.id, DeepDiveProgress.INSIGHT)
                    _sessionState.update { it.copy(
                        isSaving = false,
                        currentProgress = DeepDiveProgress.INSIGHT
                    ) }
                },
                onError = { error ->
                    _sessionState.update { it.copy(
                        isSaving = false,
                        errorMessage = error.userMessage
                    ) }
                }
            )
        }
    }

    fun onInsightTextChanged(text: String) {
        _sessionState.update { it.copy(insightText = text) }
    }

    fun saveInsightAndAdvance() {
        viewModelScope.launch {
            val state = _sessionState.value
            val session = state.session ?: return@launch

            _sessionState.update { it.copy(isSaving = true) }

            deepDiveRepository.saveKeyInsight(session.entity.id, state.insightText).fold(
                onSuccess = {
                    deepDiveRepository.updateCurrentStep(session.entity.id, DeepDiveProgress.COMMITMENT)
                    _sessionState.update { it.copy(
                        isSaving = false,
                        currentProgress = DeepDiveProgress.COMMITMENT
                    ) }
                },
                onError = { error ->
                    _sessionState.update { it.copy(
                        isSaving = false,
                        errorMessage = error.userMessage
                    ) }
                }
            )
        }
    }

    fun onCommitmentTextChanged(text: String) {
        _sessionState.update { it.copy(commitmentText = text) }
    }

    fun saveCommitmentAndComplete() {
        viewModelScope.launch {
            val state = _sessionState.value
            val session = state.session ?: return@launch

            _sessionState.update { it.copy(isSaving = true) }

            deepDiveRepository.saveCommitmentStatement(session.entity.id, state.commitmentText).fold(
                onSuccess = {
                    // Complete the session
                    val duration = session.getSessionDurationMinutes()
                    deepDiveRepository.completeSession(session.entity.id, duration).fold(
                        onSuccess = { completedEntity ->
                            _sessionState.update { it.copy(
                                isSaving = false,
                                currentProgress = DeepDiveProgress.COMPLETED,
                                showMoodAfterDialog = true
                            ) }
                        },
                        onError = { error ->
                            _sessionState.update { it.copy(
                                isSaving = false,
                                errorMessage = error.userMessage
                            ) }
                        }
                    )
                },
                onError = { error ->
                    _sessionState.update { it.copy(
                        isSaving = false,
                        errorMessage = error.userMessage
                    ) }
                }
            )
        }
    }

    fun onMoodAfterSelected(mood: Int) {
        viewModelScope.launch {
            _sessionState.update { it.copy(moodAfter = mood, showMoodAfterDialog = false) }
            val session = _sessionState.value.session ?: return@launch
            deepDiveRepository.saveMoodRating(session.entity.id, null, mood)
            _sessionState.update { it.copy(showCompletionCelebration = true) }
        }
    }

    fun skipMoodAfter() {
        _sessionState.update { it.copy(
            showMoodAfterDialog = false,
            showCompletionCelebration = true
        ) }
    }

    fun goToPreviousStep() {
        viewModelScope.launch {
            val state = _sessionState.value
            val session = state.session ?: return@launch
            val previousStep = DeepDiveProgress.getPreviousStep(state.currentProgress) ?: return@launch

            deepDiveRepository.updateCurrentStep(session.entity.id, previousStep)
            _sessionState.update { it.copy(currentProgress = previousStep) }
        }
    }

    fun dismissCompletionCelebration() {
        _sessionState.update { it.copy(showCompletionCelebration = false) }
    }

    fun dismissSessionError() {
        _sessionState.update { it.copy(errorMessage = null) }
    }

    fun resetSessionState() {
        _sessionState.value = DeepDiveSessionUiState()
    }
}

// =========================================================================
// UI STATE DATA CLASSES
// =========================================================================

/**
 * UI state for the Deep Dive home dashboard.
 */
data class DeepDiveHomeUiState(
    val isLoading: Boolean = false,
    val isScheduling: Boolean = false,
    val scheduledSessions: List<DeepDiveEntity> = emptyList(),
    val completedSessions: List<DeepDiveEntity> = emptyList(),
    val analytics: DeepDiveAnalytics? = null,
    val showScheduleSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val nextScheduled: DeepDiveEntity?
        get() = scheduledSessions.minByOrNull { it.scheduledDate }

    val hasUpcoming: Boolean
        get() = scheduledSessions.isNotEmpty()

    val totalCompleted: Int
        get() = completedSessions.size
}

/**
 * UI state for an active Deep Dive session.
 */
data class DeepDiveSessionUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val session: DeepDiveSession? = null,
    val currentProgress: DeepDiveProgress = DeepDiveProgress.NOT_STARTED,
    val moodBefore: Int? = null,
    val moodAfter: Int? = null,
    val openingText: String = "",
    val coreText: String = "",
    val insightText: String = "",
    val commitmentText: String = "",
    val showMoodAfterDialog: Boolean = false,
    val showCompletionCelebration: Boolean = false,
    val errorMessage: String? = null
) {
    val canAdvance: Boolean
        get() = when (currentProgress) {
            DeepDiveProgress.NOT_STARTED -> moodBefore != null
            DeepDiveProgress.OPENING -> openingText.isNotBlank()
            DeepDiveProgress.CORE -> coreText.isNotBlank()
            DeepDiveProgress.INSIGHT -> insightText.isNotBlank()
            DeepDiveProgress.COMMITMENT -> commitmentText.isNotBlank()
            DeepDiveProgress.COMPLETED -> false
        }

    val isCompleted: Boolean
        get() = currentProgress == DeepDiveProgress.COMPLETED

    val progressPercent: Float
        get() = when (currentProgress) {
            DeepDiveProgress.NOT_STARTED -> 0f
            DeepDiveProgress.OPENING -> 0.2f
            DeepDiveProgress.CORE -> 0.5f
            DeepDiveProgress.INSIGHT -> 0.75f
            DeepDiveProgress.COMMITMENT -> 0.9f
            DeepDiveProgress.COMPLETED -> 1f
        }
}
