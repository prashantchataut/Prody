package com.prody.prashant.ui.screens.ritual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.ai.WritingCompanionService
import com.prody.prashant.data.local.entity.DailyRitualEntity
import com.prody.prashant.data.local.entity.SavedWisdomEntity
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.repository.DailyRitualRepository
import com.prody.prashant.domain.repository.WisdomCollectionRepository
import com.prody.prashant.domain.ritual.EveningReflectionEngine
import com.prody.prashant.domain.ritual.IntentionOutcome
import com.prody.prashant.domain.ritual.MorningIntentionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Ritual mode - morning or evening
 */
enum class RitualMode {
    MORNING,
    EVENING
}

/**
 * Day rating for evening ritual
 */
enum class DayRating(val value: String, val displayName: String, val emoji: String) {
    GOOD("good", "Good", ""),
    NEUTRAL("neutral", "Okay", ""),
    TOUGH("tough", "Tough", "")
}

/**
 * Steps in the ritual flow
 */
enum class RitualStep {
    WELCOME,           // Initial welcome screen
    WISDOM,            // Show wisdom from collection (morning)
    MOOD,              // Select mood
    INTENTION,         // Set intention (morning) or rate day (evening)
    REFLECTION,        // Quick reflection text
    COMPLETE           // Ritual complete
}

/**
 * UI State for Daily Ritual
 */
data class DailyRitualUiState(
    val isLoading: Boolean = true,
    val ritualMode: RitualMode = RitualMode.MORNING,
    val currentStep: RitualStep = RitualStep.WELCOME,
    val todayRitual: DailyRitualEntity? = null,
    // Status
    val isMorningCompleted: Boolean = false,
    val isEveningCompleted: Boolean = false,
    val currentStreak: Int = 0,
    val totalRituals: Int = 0,
    val thisWeekRituals: Int = 0,
    // Morning ritual inputs
    val selectedMood: Mood? = null,
    val intention: String = "",
    val wisdomForMorning: SavedWisdomEntity? = null,
    val intentionSource: String = DailyRitualEntity.INTENTION_SOURCE_WRITTEN,
    val intentionSuggestions: List<String> = emptyList(),
    // Evening ritual inputs
    val dayRating: DayRating? = null,
    val eveningReflection: String = "",
    val morningIntention: String? = null, // To reference in evening
    val intentionOutcome: IntentionOutcome? = null,
    // Prompts
    val currentPrompt: String = "",
    val reflectionPrompt: String = "",
    // Progress
    val isSaving: Boolean = false,
    val isComplete: Boolean = false,
    val completionMessage: String = "",
    // Error handling
    val errorMessage: String? = null,
    // Navigation
    val shouldNavigateToJournal: Boolean = false,
    val shouldNavigateToMicroEntry: Boolean = false,
    val prefilledContent: String = ""
)

@HiltViewModel
class DailyRitualViewModel @Inject constructor(
    private val dailyRitualRepository: DailyRitualRepository,
    private val wisdomRepository: WisdomCollectionRepository,
    private val writingCompanionService: WritingCompanionService,
    private val morningIntentionEngine: MorningIntentionEngine,
    private val eveningReflectionEngine: EveningReflectionEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyRitualUiState())
    val uiState: StateFlow<DailyRitualUiState> = _uiState.asStateFlow()

    private val userId = "local"

    init {
        loadRitualStatus()
        determineRitualMode()
    }

    private fun loadRitualStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Get today's ritual status
                val isMorningDone = dailyRitualRepository.isMorningRitualCompletedToday(userId)
                val isEveningDone = dailyRitualRepository.isEveningRitualCompletedToday(userId)
                val streak = dailyRitualRepository.getCurrentRitualStreak(userId)
                val total = dailyRitualRepository.getCompletedRitualsCount(userId)
                val thisWeek = dailyRitualRepository.getThisWeekRitualDays(userId)

                _uiState.update { it.copy(
                    isLoading = false,
                    isMorningCompleted = isMorningDone,
                    isEveningCompleted = isEveningDone,
                    currentStreak = streak,
                    totalRituals = total,
                    thisWeekRituals = thisWeek
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Failed to load ritual status"
                )}
            }
        }
    }

    private fun determineRitualMode() {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val mode = if (hour < 15) RitualMode.MORNING else RitualMode.EVENING
        _uiState.update { it.copy(ritualMode = mode) }
    }

    fun setRitualMode(mode: RitualMode) {
        _uiState.update { it.copy(ritualMode = mode) }
    }

    // ==================== RITUAL FLOW ====================

    fun startRitual() {
        viewModelScope.launch {
            val mode = _uiState.value.ritualMode
            val streak = _uiState.value.currentStreak

            // Load wisdom for morning ritual
            if (mode == RitualMode.MORNING) {
                wisdomRepository.getWisdomToResurface(userId, 1)
                    .onSuccess { wisdom ->
                        if (wisdom.isNotEmpty()) {
                            _uiState.update { it.copy(wisdomForMorning = wisdom.first()) }
                        }
                    }
                    .onError { error ->
                        android.util.Log.w("DailyRitualViewModel", "Failed to load morning wisdom: ${error.userMessage}")
                        // Wisdom is optional for the ritual, continue without it
                    }
            }

            // Generate smart contextual prompts using the engines
            val prompt = when (mode) {
                RitualMode.MORNING -> {
                    // Check if yesterday was difficult
                    val wasYesterdayDifficult = morningIntentionEngine.wasYesterdayDifficult(userId)

                    // Generate smart morning prompt
                    val morningPrompt = morningIntentionEngine.generateMorningPrompt(
                        userId = userId,
                        currentStreak = streak,
                        wasYesterdayDifficult = wasYesterdayDifficult
                    )

                    // Generate intention suggestions based on patterns
                    val suggestions = morningIntentionEngine.generateIntentionSuggestions(userId, limit = 3)
                    _uiState.update { it.copy(intentionSuggestions = suggestions) }

                    morningPrompt
                }
                RitualMode.EVENING -> {
                    // Get today's ritual to check for morning intention
                    val todayDate = DailyRitualEntity.getTodayDate()
                    val todayRitual = dailyRitualRepository.getRitualForDate(userId, todayDate)
                        .getOrNull()

                    val morningIntention = todayRitual?.morningIntention
                    _uiState.update { it.copy(morningIntention = morningIntention) }

                    // Generate evening prompt (may reference morning intention)
                    eveningReflectionEngine.generateEveningPrompt(
                        userId = userId,
                        morningIntention = morningIntention,
                        dayRating = null // Not set yet
                    )
                }
            }

            _uiState.update { it.copy(
                currentStep = if (mode == RitualMode.MORNING && it.wisdomForMorning != null) {
                    RitualStep.WISDOM
                } else {
                    RitualStep.MOOD
                },
                currentPrompt = prompt
            )}
        }
    }

    fun nextStep() {
        val currentStep = _uiState.value.currentStep
        val mode = _uiState.value.ritualMode

        val nextStep = when (currentStep) {
            RitualStep.WELCOME -> {
                if (mode == RitualMode.MORNING && _uiState.value.wisdomForMorning != null) {
                    RitualStep.WISDOM
                } else {
                    RitualStep.MOOD
                }
            }
            RitualStep.WISDOM -> RitualStep.MOOD
            RitualStep.MOOD -> RitualStep.INTENTION
            RitualStep.INTENTION -> RitualStep.REFLECTION
            RitualStep.REFLECTION -> {
                completeRitual()
                RitualStep.COMPLETE
            }
            RitualStep.COMPLETE -> RitualStep.COMPLETE
        }

        _uiState.update { it.copy(currentStep = nextStep) }
    }

    fun previousStep() {
        val currentStep = _uiState.value.currentStep
        val mode = _uiState.value.ritualMode

        val prevStep = when (currentStep) {
            RitualStep.WELCOME -> RitualStep.WELCOME
            RitualStep.WISDOM -> RitualStep.WELCOME
            RitualStep.MOOD -> {
                if (mode == RitualMode.MORNING && _uiState.value.wisdomForMorning != null) {
                    RitualStep.WISDOM
                } else {
                    RitualStep.WELCOME
                }
            }
            RitualStep.INTENTION -> RitualStep.MOOD
            RitualStep.REFLECTION -> RitualStep.INTENTION
            RitualStep.COMPLETE -> RitualStep.REFLECTION
        }

        _uiState.update { it.copy(currentStep = prevStep) }
    }

    // ==================== INPUT HANDLERS ====================

    fun onMoodSelected(mood: Mood) {
        _uiState.update { it.copy(selectedMood = mood) }
    }

    fun onIntentionChanged(intention: String) {
        _uiState.update { it.copy(intention = intention) }
    }

    fun onDayRatingSelected(rating: DayRating) {
        viewModelScope.launch {
            // Generate a follow-up reflection prompt based on the rating
            val reflectionPrompt = eveningReflectionEngine.generateReflectionPrompt(
                dayRating = rating.value,
                morningIntention = _uiState.value.morningIntention
            )

            _uiState.update { it.copy(
                dayRating = rating,
                reflectionPrompt = reflectionPrompt
            )}
        }
    }

    fun onIntentionOutcomeSelected(outcome: IntentionOutcome) {
        _uiState.update { it.copy(intentionOutcome = outcome) }
    }

    fun selectIntentionSuggestion(suggestion: String) {
        _uiState.update { it.copy(
            intention = suggestion,
            intentionSource = DailyRitualEntity.INTENTION_SOURCE_SUGGESTED
        )}
    }

    fun onEveningReflectionChanged(reflection: String) {
        _uiState.update { it.copy(eveningReflection = reflection) }
    }

    // ==================== COMPLETE RITUAL ====================

    private fun completeRitual() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val result = when (state.ritualMode) {
                RitualMode.MORNING -> {
                    dailyRitualRepository.completeMorningRitual(
                        userId = userId,
                        intention = state.intention.takeIf { it.isNotBlank() },
                        mood = state.selectedMood?.name,
                        wisdomId = state.wisdomForMorning?.id
                    )
                }
                RitualMode.EVENING -> {
                    // Infer intention outcome if not explicitly set
                    val outcome = state.intentionOutcome ?: eveningReflectionEngine.inferIntentionOutcome(
                        morningIntention = state.morningIntention,
                        eveningReflection = state.eveningReflection.takeIf { it.isNotBlank() },
                        dayRating = state.dayRating?.value
                    )

                    dailyRitualRepository.completeEveningRitual(
                        userId = userId,
                        dayRating = state.dayRating?.value ?: "neutral",
                        reflection = state.eveningReflection.takeIf { it.isNotBlank() },
                        mood = state.selectedMood?.name
                    )
                }
            }

            result
                .fold(
                    onSuccess = {
                        // Generate warm, personal completion message using engines
                        val message = when (state.ritualMode) {
                            RitualMode.MORNING -> {
                                morningIntentionEngine.getMorningCompletionMessage(
                                    hasIntention = state.intention.isNotBlank()
                                )
                            }
                            RitualMode.EVENING -> {
                                eveningReflectionEngine.getEveningCompletionMessage(
                                    dayRating = state.dayRating?.value,
                                    intentionOutcome = state.intentionOutcome,
                                    currentStreak = state.currentStreak
                                )
                            }
                        }

                        _uiState.update { it.copy(
                            isSaving = false,
                            isComplete = true,
                            completionMessage = message,
                            isMorningCompleted = state.ritualMode == RitualMode.MORNING || it.isMorningCompleted,
                            isEveningCompleted = state.ritualMode == RitualMode.EVENING || it.isEveningCompleted
                        )}
                        loadRitualStatus() // Refresh stats
                    },
                    onError = { error ->
                        _uiState.update { it.copy(
                            isSaving = false,
                            errorMessage = error.userMessage
                        )}
                    }
                )
        }
    }

    // ==================== EXPANSION ====================

    fun expandToJournal() {
        val state = _uiState.value
        val content = buildString {
            when (state.ritualMode) {
                RitualMode.MORNING -> {
                    if (state.intention.isNotBlank()) {
                        appendLine("**Today's Intention:** ${state.intention}")
                        appendLine()
                    }
                    state.selectedMood?.let {
                        appendLine("Starting the day feeling ${it.displayName.lowercase()}.")
                        appendLine()
                    }
                }
                RitualMode.EVENING -> {
                    state.dayRating?.let {
                        appendLine("Today was a ${it.displayName.lowercase()} day.")
                        appendLine()
                    }
                    if (state.eveningReflection.isNotBlank()) {
                        appendLine(state.eveningReflection)
                        appendLine()
                    }
                    state.selectedMood?.let {
                        appendLine("Ending the day feeling ${it.displayName.lowercase()}.")
                    }
                }
            }
        }

        _uiState.update { it.copy(
            shouldNavigateToJournal = true,
            prefilledContent = content
        )}
    }

    fun expandToMicroEntry() {
        val state = _uiState.value
        val content = when (state.ritualMode) {
            RitualMode.MORNING -> state.intention
            RitualMode.EVENING -> state.eveningReflection
        }

        _uiState.update { it.copy(
            shouldNavigateToMicroEntry = true,
            prefilledContent = content
        )}
    }

    fun clearNavigation() {
        _uiState.update { it.copy(
            shouldNavigateToJournal = false,
            shouldNavigateToMicroEntry = false,
            prefilledContent = ""
        )}
    }

    // ==================== MARK EXPANDED ====================

    fun markExpandedToJournal(journalId: Long) {
        viewModelScope.launch {
            dailyRitualRepository.markExpandedToJournal(userId, journalId)
        }
    }

    fun markExpandedToMicroEntry(microEntryId: Long) {
        viewModelScope.launch {
            dailyRitualRepository.markExpandedToMicroEntry(userId, microEntryId)
        }
    }

    // ==================== ERROR HANDLING ====================

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ==================== RESET ====================

    fun resetRitual() {
        _uiState.update { it.copy(
            currentStep = RitualStep.WELCOME,
            selectedMood = null,
            intention = "",
            intentionSource = DailyRitualEntity.INTENTION_SOURCE_WRITTEN,
            intentionSuggestions = emptyList(),
            dayRating = null,
            eveningReflection = "",
            morningIntention = null,
            intentionOutcome = null,
            isComplete = false,
            completionMessage = "",
            reflectionPrompt = ""
        )}
    }
}
