package com.prody.prashant.domain.wellbeing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.prody.prashant.data.local.preferences.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * QuietModeExtensions - Helper extensions for using Quiet Mode throughout the app
 *
 * These extensions make it easy to check Quiet Mode state and conditionally
 * show/hide features in UI components.
 */

/**
 * Extension to check if Quiet Mode is active as a Composable state.
 * Use this in Composable functions to reactively hide/show features.
 *
 * Example:
 * ```
 * @Composable
 * fun HomeScreen(preferencesManager: PreferencesManager) {
 *     val isQuietMode = preferencesManager.isQuietModeActive()
 *
 *     if (!isQuietMode) {
 *         StreakDisplay()  // Only shown when NOT in quiet mode
 *     }
 * }
 * ```
 */
@Composable
fun PreferencesManager.isQuietModeActive(): Boolean {
    val isActive by this.quietModeEnabled.collectAsState(initial = false)
    return isActive
}

/**
 * Extension to conditionally show a feature based on Quiet Mode state.
 *
 * Example:
 * ```
 * @Composable
 * fun ProfileScreen(preferencesManager: PreferencesManager) {
 *     val isQuietMode = preferencesManager.isQuietModeActive()
 *
 *     if (isQuietMode.shouldShow(Feature.ACHIEVEMENTS)) {
 *         AchievementsList()
 *     }
 * }
 * ```
 */
fun Boolean.shouldShow(feature: Feature): Boolean {
    return QuietModeFeatures.shouldShowFeature(feature, this)
}

/**
 * Extension to conditionally execute code based on Quiet Mode state.
 *
 * Example:
 * ```
 * suspend fun saveJournal(preferencesManager: PreferencesManager) {
 *     // ... save logic ...
 *
 *     preferencesManager.quietModeEnabled.first().whenQuietModeInactive {
 *         showAchievementNotification()
 *     }
 * }
 * ```
 */
inline fun Boolean.whenQuietModeActive(block: () -> Unit) {
    if (this) block()
}

inline fun Boolean.whenQuietModeInactive(block: () -> Unit) {
    if (!this) block()
}

/**
 * Helper function to get a Flow that emits whether a specific feature should be shown.
 */
fun PreferencesManager.shouldShowFeature(feature: Feature): Flow<Boolean> {
    return this.quietModeEnabled.map { isActive ->
        QuietModeFeatures.shouldShowFeature(feature, isActive)
    }
}

/**
 * Helper object with common UI adjustments for Quiet Mode.
 */
object QuietModeUI {
    /**
     * Get animation duration multiplier for Quiet Mode.
     * In Quiet Mode, animations are faster/simpler.
     */
    fun getAnimationDuration(isQuietMode: Boolean, normalDuration: Int): Int {
        return if (isQuietMode) {
            (normalDuration * 0.5f).toInt() // 50% faster
        } else {
            normalDuration
        }
    }

    /**
     * Get corner radius adjustment for Quiet Mode.
     * In Quiet Mode, corners are slightly softer.
     */
    fun getCornerRadius(isQuietMode: Boolean, normalRadius: Float): Float {
        return if (isQuietMode) {
            normalRadius + 4f // +4dp softer
        } else {
            normalRadius
        }
    }

    /**
     * Get padding adjustment for Quiet Mode.
     * In Quiet Mode, spacing is slightly increased for breathing room.
     */
    fun getPadding(isQuietMode: Boolean, normalPadding: Float): Float {
        return if (isQuietMode) {
            normalPadding + 4f // +4dp more space
        } else {
            normalPadding
        }
    }

    /**
     * Determines if a notification should be shown based on type and Quiet Mode.
     */
    fun shouldShowNotification(notificationType: QuietModeNotificationType, isQuietMode: Boolean): Boolean {
        return if (isQuietMode) {
            // Only essential notifications in Quiet Mode
            when (notificationType) {
                QuietModeNotificationType.JOURNAL_REMINDER -> true
                QuietModeNotificationType.WISDOM_DAILY -> true
                QuietModeNotificationType.FUTURE_MESSAGE_ARRIVED -> true
                QuietModeNotificationType.ACHIEVEMENT -> false
                QuietModeNotificationType.LEVEL_UP -> false
                QuietModeNotificationType.STREAK_MILESTONE -> false
                QuietModeNotificationType.LEADERBOARD_UPDATE -> false
            }
        } else {
            true // Show all notifications when not in Quiet Mode
        }
    }
}

/**
 * Notification types for filtering in Quiet Mode.
 *
 * Renamed from NotificationType to avoid collision with
 * com.prody.prashant.domain.intelligence.NotificationType.
 */
enum class QuietModeNotificationType {
    JOURNAL_REMINDER,
    WISDOM_DAILY,
    FUTURE_MESSAGE_ARRIVED,
    ACHIEVEMENT,
    LEVEL_UP,
    STREAK_MILESTONE,
    LEADERBOARD_UPDATE
}
