package com.prody.prashant.domain.wellbeing

import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * QuietModeManager - Manages Quiet Mode state and auto-suggestion logic
 *
 * Quiet Mode is a wellbeing feature that simplifies the app interface when
 * users are experiencing stress or overwhelm. It can be:
 * 1. Manually enabled by the user via settings or quick toggle
 * 2. Auto-suggested based on journal entry patterns
 *
 * Philosophy:
 * - User's mental wellbeing comes first
 * - Never forced, always optional
 * - Gentle suggestions, not nagging
 * - Easy to enable, easy to disable
 *
 * When Quiet Mode is active:
 * - Gamification elements are hidden (streaks, XP, missions, leaderboard)
 * - UI uses muted, calmer colors
 * - Animations are minimized
 * - Focus shifts entirely to journaling and reflection
 * - Core features remain accessible (journal, wisdom, future messages)
 */
@Singleton
class QuietModeManager @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val journalRepository: JournalRepository,
    private val quietModeDetector: QuietModeDetector
) {

    /**
     * Observes whether Quiet Mode is currently enabled.
     */
    fun isQuietModeEnabled(): Flow<Boolean> {
        return preferencesManager.quietModeEnabled
    }

    /**
     * Enables Quiet Mode.
     * Records the timestamp when it was enabled for future check-ins.
     */
    suspend fun enableQuietMode() {
        preferencesManager.setQuietModeEnabled(true)
    }

    /**
     * Disables Quiet Mode.
     */
    suspend fun disableQuietMode() {
        preferencesManager.setQuietModeEnabled(false)
    }

    /**
     * Toggles Quiet Mode on/off.
     */
    suspend fun toggleQuietMode() {
        val currentState = preferencesManager.quietModeEnabled.first()
        preferencesManager.setQuietModeEnabled(!currentState)
    }

    /**
     * Determines if we should suggest Quiet Mode to the user based on their
     * recent journal entries and stress patterns.
     *
     * This method:
     * 1. Checks if enough time has passed since last suggestion (to avoid nagging)
     * 2. Retrieves recent journal entries (last 7 days)
     * 3. Analyzes entries for stress patterns
     * 4. Returns whether to show the suggestion
     *
     * @return QuietModeSuggestion with recommendation and reasoning
     */
    suspend fun shouldSuggestQuietMode(): QuietModeSuggestion {
        // Don't suggest if already in quiet mode
        val isEnabled = preferencesManager.quietModeEnabled.first()
        if (isEnabled) {
            return QuietModeSuggestion(
                shouldSuggest = false,
                reason = "Already in Quiet Mode",
                analysisResult = null
            )
        }

        // Check if enough time has passed since last suggestion
        val lastSuggestedAt = preferencesManager.quietModeLastSuggestedAt.first()
        if (!quietModeDetector.shouldShowSuggestion(lastSuggestedAt)) {
            return QuietModeSuggestion(
                shouldSuggest = false,
                reason = "Too soon since last suggestion",
                analysisResult = null
            )
        }

        // Get recent journal entries (last 7 days)
        val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        val recentEntries = try {
            journalRepository.getEntriesAfterTimestamp(sevenDaysAgo).first()
        } catch (e: Exception) {
            emptyList()
        }

        // Analyze stress patterns
        val analysisResult = quietModeDetector.analyzeStressPatterns(recentEntries)

        return QuietModeSuggestion(
            shouldSuggest = analysisResult.shouldSuggestQuietMode,
            reason = analysisResult.reason,
            analysisResult = analysisResult
        )
    }

    /**
     * Records that the Quiet Mode suggestion was shown to the user.
     * This prevents showing the suggestion too frequently.
     */
    suspend fun recordSuggestionShown() {
        preferencesManager.setQuietModeLastSuggestedAt(System.currentTimeMillis())
    }

    /**
     * Checks if it's time to show a gentle check-in asking if the user
     * wants to exit Quiet Mode (after 7 days).
     *
     * @return true if check-in should be shown
     */
    suspend fun shouldShowExitCheckIn(): Boolean {
        val isEnabled = preferencesManager.quietModeEnabled.first()
        if (!isEnabled) return false

        val enabledAt = preferencesManager.quietModeEnabledAt.first()
        val lastCheckInAt = preferencesManager.quietModeLastCheckInAt.first()

        return quietModeDetector.shouldShowExitCheckIn(enabledAt, lastCheckInAt)
    }

    /**
     * Records that the exit check-in was shown.
     */
    suspend fun recordExitCheckInShown() {
        preferencesManager.setQuietModeLastCheckInAt(System.currentTimeMillis())
    }

    /**
     * Gets the duration that Quiet Mode has been enabled (in days).
     * Returns 0 if not currently enabled.
     */
    suspend fun getQuietModeDuration(): Int {
        val isEnabled = preferencesManager.quietModeEnabled.first()
        if (!isEnabled) return 0

        val enabledAt = preferencesManager.quietModeEnabledAt.first()
        if (enabledAt == 0L) return 0

        val durationMillis = System.currentTimeMillis() - enabledAt
        return TimeUnit.MILLISECONDS.toDays(durationMillis).toInt()
    }

    /**
     * Observes the current Quiet Mode configuration.
     * This can be used by UI components to determine what to show/hide.
     */
    fun observeQuietModeConfig(): Flow<QuietModeConfig> {
        return preferencesManager.quietModeEnabled.map { isEnabled ->
            if (isEnabled) {
                QuietModeConfig.Active
            } else {
                QuietModeConfig.Inactive
            }
        }
    }
}

/**
 * Represents the result of a Quiet Mode suggestion analysis.
 */
data class QuietModeSuggestion(
    val shouldSuggest: Boolean,
    val reason: String,
    val analysisResult: StressAnalysisResult?
)

/**
 * Represents the current Quiet Mode configuration.
 */
sealed class QuietModeConfig {
    /**
     * Quiet Mode is active - UI should be simplified.
     */
    object Active : QuietModeConfig()

    /**
     * Quiet Mode is inactive - normal UI.
     */
    object Inactive : QuietModeConfig()
}
