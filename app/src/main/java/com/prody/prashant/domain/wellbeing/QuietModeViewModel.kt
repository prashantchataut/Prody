package com.prody.prashant.domain.wellbeing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * QuietModeViewModel - Example ViewModel showing Quiet Mode integration
 *
 * This is a reference implementation showing how to integrate Quiet Mode
 * into your app's ViewModels. You can adapt this pattern to your existing
 * HomeViewModel, SettingsViewModel, etc.
 *
 * Usage in existing ViewModels:
 * 1. Inject QuietModeManager
 * 2. Expose Quiet Mode state as StateFlow
 * 3. Add toggle/enable/disable functions
 * 4. Handle auto-suggestion logic
 */
@HiltViewModel
class QuietModeViewModel @Inject constructor(
    private val quietModeManager: QuietModeManager
) : ViewModel() {

    // =============================================================================
    // STATE FLOWS
    // =============================================================================

    /**
     * Observes whether Quiet Mode is currently enabled.
     * Collect this in your Composables to reactively show/hide features.
     */
    val isQuietModeEnabled: StateFlow<Boolean> = quietModeManager
        .isQuietModeEnabled()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    /**
     * State for showing the auto-suggestion dialog.
     */
    private val _showSuggestionDialog = MutableStateFlow(false)
    val showSuggestionDialog: StateFlow<Boolean> = _showSuggestionDialog.asStateFlow()

    /**
     * State for showing the exit check-in dialog (after 7 days).
     */
    private val _showExitCheckIn = MutableStateFlow<Int?>(null) // Days in quiet mode
    val showExitCheckIn: StateFlow<Int?> = _showExitCheckIn.asStateFlow()

    /**
     * Analysis result for debugging/logging.
     */
    private val _lastAnalysis = MutableStateFlow<StressAnalysisResult?>(null)
    val lastAnalysis: StateFlow<StressAnalysisResult?> = _lastAnalysis.asStateFlow()

    // =============================================================================
    // INITIALIZATION
    // =============================================================================

    init {
        // Check if we should suggest Quiet Mode on app startup or when returning to home
        checkForSuggestion()

        // Check if we should show exit check-in
        checkForExitCheckIn()
    }

    // =============================================================================
    // PUBLIC FUNCTIONS
    // =============================================================================

    /**
     * Toggles Quiet Mode on/off.
     */
    fun toggleQuietMode() {
        viewModelScope.launch {
            quietModeManager.toggleQuietMode()
        }
    }

    /**
     * Enables Quiet Mode.
     */
    fun enableQuietMode() {
        viewModelScope.launch {
            quietModeManager.enableQuietMode()
        }
    }

    /**
     * Disables Quiet Mode.
     */
    fun disableQuietMode() {
        viewModelScope.launch {
            quietModeManager.disableQuietMode()
        }
    }

    /**
     * Dismisses the suggestion dialog and records that it was shown.
     */
    fun dismissSuggestion() {
        viewModelScope.launch {
            quietModeManager.recordSuggestionShown()
            _showSuggestionDialog.value = false
        }
    }

    /**
     * Accepts the Quiet Mode suggestion.
     */
    fun acceptSuggestion() {
        viewModelScope.launch {
            quietModeManager.enableQuietMode()
            quietModeManager.recordSuggestionShown()
            _showSuggestionDialog.value = false
        }
    }

    /**
     * User wants to keep Quiet Mode during check-in.
     */
    fun keepQuietMode() {
        viewModelScope.launch {
            quietModeManager.recordExitCheckInShown()
            _showExitCheckIn.value = null
        }
    }

    /**
     * User wants to exit Quiet Mode during check-in.
     */
    fun exitQuietMode() {
        viewModelScope.launch {
            quietModeManager.disableQuietMode()
            quietModeManager.recordExitCheckInShown()
            _showExitCheckIn.value = null
        }
    }

    /**
     * Manually triggers a check for whether to suggest Quiet Mode.
     * Call this when returning to home screen or after saving a journal entry.
     */
    fun checkForSuggestion() {
        viewModelScope.launch {
            val suggestion = quietModeManager.shouldSuggestQuietMode()

            if (suggestion.shouldSuggest) {
                _showSuggestionDialog.value = true
                _lastAnalysis.value = suggestion.analysisResult
            }
        }
    }

    /**
     * Checks if we should show the exit check-in dialog.
     */
    fun checkForExitCheckIn() {
        viewModelScope.launch {
            if (quietModeManager.shouldShowExitCheckIn()) {
                val duration = quietModeManager.getQuietModeDuration()
                _showExitCheckIn.value = duration
            }
        }
    }

    /**
     * Gets the current duration of Quiet Mode (in days).
     * Useful for showing "Active for X days" in the UI.
     */
    fun getQuietModeDuration(): StateFlow<Int> {
        return flow {
            while (true) {
                val duration = quietModeManager.getQuietModeDuration()
                emit(duration)
                kotlinx.coroutines.delay(60_000) // Update every minute
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    }

    // =============================================================================
    // HELPER FUNCTIONS
    // =============================================================================

    /**
     * Checks if a specific feature should be shown based on Quiet Mode state.
     * Use this in your ViewModel logic before emitting UI events.
     *
     * Example:
     * ```
     * fun saveJournal() {
     *     // ... save logic ...
     *
     *     if (shouldShowFeature(Feature.ACHIEVEMENT_NOTIFICATIONS)) {
     *         showAchievementNotification()
     *     }
     * }
     * ```
     */
    suspend fun shouldShowFeature(feature: Feature): Boolean {
        val isQuietMode = isQuietModeEnabled.value
        return QuietModeFeatures.shouldShowFeature(feature, isQuietMode)
    }

    /**
     * Executes a block only if NOT in Quiet Mode.
     * Useful for conditional gamification logic.
     *
     * Example:
     * ```
     * whenNotInQuietMode {
     *     awardXP(50)
     *     checkForLevelUp()
     * }
     * ```
     */
    inline fun whenNotInQuietMode(block: () -> Unit) {
        if (!isQuietModeEnabled.value) {
            block()
        }
    }

    /**
     * Executes a block only if in Quiet Mode.
     * Useful for conditional quiet-specific logic.
     */
    inline fun whenInQuietMode(block: () -> Unit) {
        if (isQuietModeEnabled.value) {
            block()
        }
    }
}

// =============================================================================
// INTEGRATION EXAMPLE
// =============================================================================

/**
 * Example of integrating Quiet Mode into an existing HomeViewModel:
 *
 * ```kotlin
 * @HiltViewModel
 * class HomeViewModel @Inject constructor(
 *     private val journalRepository: JournalRepository,
 *     private val gamificationService: GamificationService,
 *     private val quietModeManager: QuietModeManager
 * ) : ViewModel() {
 *
 *     // Observe Quiet Mode state
 *     val isQuietMode = quietModeManager
 *         .isQuietModeEnabled()
 *         .stateIn(viewModelScope, SharingStarted.Lazily, false)
 *
 *     // Check for suggestion on init
 *     init {
 *         checkQuietModeSuggestion()
 *     }
 *
 *     private fun checkQuietModeSuggestion() {
 *         viewModelScope.launch {
 *             val suggestion = quietModeManager.shouldSuggestQuietMode()
 *             if (suggestion.shouldSuggest) {
 *                 _showQuietModeSuggestion.value = true
 *             }
 *         }
 *     }
 *
 *     fun saveJournal(entry: JournalEntry) {
 *         viewModelScope.launch {
 *             journalRepository.insert(entry)
 *
 *             // Only award XP if not in Quiet Mode
 *             if (!isQuietMode.value) {
 *                 gamificationService.awardXP(10)
 *                 checkForAchievements()
 *             }
 *         }
 *     }
 *
 *     fun toggleQuietMode() {
 *         viewModelScope.launch {
 *             quietModeManager.toggleQuietMode()
 *         }
 *     }
 * }
 * ```
 */

// =============================================================================
// COMPOSABLE INTEGRATION EXAMPLE
// =============================================================================

/**
 * Example of using Quiet Mode in a Composable:
 *
 * ```kotlin
 * @Composable
 * fun HomeScreen(
 *     viewModel: HomeViewModel,
 *     quietModeViewModel: QuietModeViewModel
 * ) {
 *     val isQuietMode by quietModeViewModel.isQuietModeEnabled.collectAsState()
 *     val showSuggestion by quietModeViewModel.showSuggestionDialog.collectAsState()
 *     val showCheckIn by quietModeViewModel.showExitCheckIn.collectAsState()
 *
 *     // Suggestion dialog
 *     if (showSuggestion) {
 *         QuietModeSuggestionDialog(
 *             onAccept = { quietModeViewModel.acceptSuggestion() },
 *             onDismiss = { quietModeViewModel.dismissSuggestion() }
 *         )
 *     }
 *
 *     // Check-in dialog
 *     showCheckIn?.let { days ->
 *         QuietModeExitCheckInDialog(
 *             daysInQuietMode = days,
 *             onKeepQuietMode = { quietModeViewModel.keepQuietMode() },
 *             onExitQuietMode = { quietModeViewModel.exitQuietMode() },
 *             onDismiss = { quietModeViewModel.keepQuietMode() }
 *         )
 *     }
 *
 *     Column {
 *         // Indicator when active
 *         if (isQuietMode) {
 *             QuietModeIndicator(
 *                 onExit = { quietModeViewModel.disableQuietMode() }
 *             )
 *         }
 *
 *         // Toggle card
 *         QuietModeToggle(
 *             isQuietModeActive = isQuietMode,
 *             onToggle = { quietModeViewModel.toggleQuietMode() }
 *         )
 *
 *         // Conditional content
 *         if (!isQuietMode) {
 *             StreakCard()
 *             XPProgressBar()
 *         }
 *
 *         // Always shown
 *         JournalQuickAction()
 *         DailyWisdomCard()
 *     }
 * }
 * ```
 */
