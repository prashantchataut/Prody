package com.prody.prashant.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.prody.prashant.data.local.preferences.PreferencesManager
import kotlinx.coroutines.flow.Flow

/**
 * Haptic feedback types for different user interactions.
 * Maps semantic actions to appropriate haptic patterns.
 */
enum class HapticType {
    /** Light tap feedback for button presses and selections */
    CLICK,
    /** Medium feedback for successful actions like saves, completions */
    SUCCESS,
    /** Heavier feedback for errors or warnings */
    ERROR,
    /** Feedback for long press gestures */
    LONG_PRESS,
    /** Light feedback for selection changes in lists/options */
    SELECTION,
    /** Feedback for achieving milestones or unlocking achievements */
    ACHIEVEMENT,
    /** Subtle feedback for drag/scroll boundaries */
    BOUNDARY
}

/**
 * CompositionLocal to provide haptic enabled state throughout the composition tree.
 * Defaults to true (haptic enabled).
 */
val LocalHapticEnabled = compositionLocalOf { true }

/**
 * Preference-aware haptic feedback wrapper.
 * Provides haptic feedback only when user has enabled it in settings.
 */
class ProdyHapticFeedback(
    private val hapticFeedback: HapticFeedback,
    private val isEnabled: Boolean
) {
    /**
     * Perform haptic feedback with the specified type.
     * Respects user preference - does nothing if haptic feedback is disabled.
     */
    fun perform(type: HapticType) {
        if (!isEnabled) return

        val hapticType = when (type) {
            HapticType.CLICK -> HapticFeedbackType.TextHandleMove
            HapticType.SUCCESS -> HapticFeedbackType.LongPress
            HapticType.ERROR -> HapticFeedbackType.LongPress
            HapticType.LONG_PRESS -> HapticFeedbackType.LongPress
            HapticType.SELECTION -> HapticFeedbackType.TextHandleMove
            HapticType.ACHIEVEMENT -> HapticFeedbackType.LongPress
            HapticType.BOUNDARY -> HapticFeedbackType.TextHandleMove
        }

        hapticFeedback.performHapticFeedback(hapticType)
    }

    /**
     * Perform click feedback - light tap for button presses.
     */
    fun click() = perform(HapticType.CLICK)

    /**
     * Perform success feedback - for successful completions.
     */
    fun success() = perform(HapticType.SUCCESS)

    /**
     * Perform error feedback - for errors or warnings.
     */
    fun error() = perform(HapticType.ERROR)

    /**
     * Perform long press feedback - for long press gestures.
     */
    fun longPress() = perform(HapticType.LONG_PRESS)

    /**
     * Perform selection feedback - for option/item selections.
     */
    fun selection() = perform(HapticType.SELECTION)

    /**
     * Perform achievement feedback - for milestone unlocks.
     */
    fun achievement() = perform(HapticType.ACHIEVEMENT)

    /**
     * Perform boundary feedback - for scroll/drag boundaries.
     */
    fun boundary() = perform(HapticType.BOUNDARY)
}

/**
 * Provider composable that sets up haptic feedback preference in the composition tree.
 * Wrap your app content with this to enable preference-aware haptic feedback.
 *
 * Usage in your main activity or app composable:
 * ```
 * @Composable
 * fun ProdyApp(preferencesManager: PreferencesManager) {
 *     ProvideHapticEnabled(preferencesManager) {
 *         // Your app content
 *     }
 * }
 * ```
 */
@Composable
fun ProvideHapticEnabled(
    preferencesManager: PreferencesManager,
    content: @Composable () -> Unit
) {
    val isEnabled by preferencesManager.hapticFeedbackEnabled.collectAsState(initial = true)

    CompositionLocalProvider(LocalHapticEnabled provides isEnabled) {
        content()
    }
}

/**
 * Provider composable that sets up haptic feedback preference from a Flow.
 */
@Composable
fun ProvideHapticEnabled(
    hapticEnabledFlow: Flow<Boolean>,
    content: @Composable () -> Unit
) {
    val isEnabled by hapticEnabledFlow.collectAsState(initial = true)

    CompositionLocalProvider(LocalHapticEnabled provides isEnabled) {
        content()
    }
}

/**
 * Get a preference-aware haptic feedback instance using CompositionLocal.
 * This is the simplest way to use haptic feedback in components.
 *
 * Usage:
 * ```
 * @Composable
 * fun MyButton(onClick: () -> Unit) {
 *     val haptic = rememberProdyHaptic()
 *
 *     Button(onClick = {
 *         haptic.click()
 *         onClick()
 *     }) { ... }
 * }
 * ```
 */
@Composable
fun rememberProdyHaptic(): ProdyHapticFeedback {
    val hapticFeedback = LocalHapticFeedback.current
    val isEnabled = LocalHapticEnabled.current

    return remember(hapticFeedback, isEnabled) {
        ProdyHapticFeedback(hapticFeedback, isEnabled)
    }
}

/**
 * Composable function to get a preference-aware haptic feedback instance.
 * Use this when you have direct access to PreferencesManager.
 *
 * Usage:
 * ```
 * @Composable
 * fun MyScreen(preferencesManager: PreferencesManager) {
 *     val haptic = rememberProdyHaptic(preferencesManager)
 *
 *     Button(onClick = {
 *         haptic.click()
 *         // ... action
 *     }) { ... }
 * }
 * ```
 */
@Composable
fun rememberProdyHaptic(preferencesManager: PreferencesManager): ProdyHapticFeedback {
    val hapticFeedback = LocalHapticFeedback.current
    val isEnabled by preferencesManager.hapticFeedbackEnabled.collectAsState(initial = true)

    return remember(hapticFeedback, isEnabled) {
        ProdyHapticFeedback(hapticFeedback, isEnabled)
    }
}

/**
 * Composable function to get a preference-aware haptic feedback instance
 * using a Flow for the enabled state.
 *
 * This variant is useful when you only have access to the Flow directly.
 */
@Composable
fun rememberProdyHaptic(hapticEnabledFlow: Flow<Boolean>): ProdyHapticFeedback {
    val hapticFeedback = LocalHapticFeedback.current
    val isEnabled by hapticEnabledFlow.collectAsState(initial = true)

    return remember(hapticFeedback, isEnabled) {
        ProdyHapticFeedback(hapticFeedback, isEnabled)
    }
}

/**
 * Extension function to create a ProdyHapticFeedback directly from HapticFeedback.
 * Use this when you already know the enabled state.
 */
fun HapticFeedback.toProdyHaptic(isEnabled: Boolean): ProdyHapticFeedback {
    return ProdyHapticFeedback(this, isEnabled)
}

/**
 * Perform preference-aware haptic feedback directly.
 * Convenience extension for direct use with HapticFeedback.
 *
 * Usage:
 * ```
 * val haptic = LocalHapticFeedback.current
 * val isEnabled = LocalHapticEnabled.current
 *
 * Button(onClick = {
 *     haptic.performIfEnabled(isEnabled, HapticFeedbackType.LongPress)
 * })
 * ```
 */
fun HapticFeedback.performIfEnabled(isEnabled: Boolean, hapticFeedbackType: HapticFeedbackType) {
    if (isEnabled) {
        performHapticFeedback(hapticFeedbackType)
    }
}
