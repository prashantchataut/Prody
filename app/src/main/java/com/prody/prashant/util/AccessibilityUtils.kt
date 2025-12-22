package com.prody.prashant.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription

/**
 * Accessibility utilities for Prody app.
 *
 * Design Principles:
 * - All interactive elements have content descriptions
 * - State changes are announced to screen readers
 * - Dynamic type support for all text
 * - Minimum touch targets of 48dp
 * - High contrast support
 */
object AccessibilityUtils {

    // ============================================================================
    // CONTENT DESCRIPTION TEMPLATES
    // ============================================================================

    /**
     * Generate content description for mood selection
     */
    fun moodDescription(moodName: String, isSelected: Boolean): String {
        return if (isSelected) {
            "Selected mood: $moodName"
        } else {
            "Select $moodName mood"
        }
    }

    /**
     * Generate content description for streak display
     */
    fun streakDescription(days: Int): String {
        return when (days) {
            0 -> "No current streak. Start journaling to begin your streak!"
            1 -> "1 day streak"
            else -> "$days day streak"
        }
    }

    /**
     * Generate content description for points display
     */
    fun pointsDescription(points: Int): String {
        return "$points points earned"
    }

    /**
     * Generate content description for favorite toggle
     */
    fun favoriteDescription(isFavorite: Boolean, itemType: String): String {
        return if (isFavorite) {
            "Remove $itemType from favorites"
        } else {
            "Add $itemType to favorites"
        }
    }

    /**
     * Generate content description for bookmark toggle
     */
    fun bookmarkDescription(isBookmarked: Boolean): String {
        return if (isBookmarked) {
            "Remove bookmark"
        } else {
            "Bookmark this entry"
        }
    }

    /**
     * Generate content description for quote card
     */
    fun quoteDescription(quote: String, author: String): String {
        return "Quote by $author: $quote"
    }

    /**
     * Generate content description for word of the day
     */
    fun wordDescription(word: String, pronunciation: String, definition: String): String {
        return "Word of the day: $word. Pronounced: $pronunciation. Definition: $definition"
    }

    /**
     * Generate content description for journal entry
     */
    fun journalEntryDescription(mood: String, date: String, hasMedia: Boolean): String {
        val mediaText = if (hasMedia) " with media attachments" else ""
        return "Journal entry from $date. Mood: $mood$mediaText"
    }

    /**
     * Generate content description for Buddha AI response
     */
    fun buddhaResponseDescription(isAiGenerated: Boolean): String {
        return if (isAiGenerated) {
            "AI-generated wisdom from Buddha, your personal reflection guide"
        } else {
            "Curated wisdom from Buddha, your personal reflection guide"
        }
    }

    /**
     * Generate content description for loading state
     */
    fun loadingDescription(context: String): String {
        return "Loading $context. Please wait."
    }

    /**
     * Generate content description for error state
     */
    fun errorDescription(message: String): String {
        return "Error: $message"
    }

    /**
     * Generate content description for achievement
     */
    fun achievementDescription(name: String, isUnlocked: Boolean, progress: Int, requirement: Int): String {
        return if (isUnlocked) {
            "Achievement unlocked: $name"
        } else {
            "Achievement: $name. Progress: $progress of $requirement"
        }
    }

    /**
     * Generate content description for sync status
     */
    fun syncStatusDescription(isOnline: Boolean, pendingChanges: Int): String {
        return when {
            !isOnline -> "Offline. Changes will sync when connection is restored."
            pendingChanges > 0 -> "$pendingChanges changes waiting to sync"
            else -> "All data synced"
        }
    }

    // ============================================================================
    // STATE DESCRIPTIONS FOR SCREEN READERS
    // ============================================================================

    /**
     * Generate state description for toggle switches
     */
    fun toggleStateDescription(isEnabled: Boolean, featureName: String): String {
        return if (isEnabled) {
            "$featureName is enabled"
        } else {
            "$featureName is disabled"
        }
    }

    /**
     * Generate state description for expandable content
     */
    fun expandableStateDescription(isExpanded: Boolean, contentType: String): String {
        return if (isExpanded) {
            "$contentType expanded. Double tap to collapse."
        } else {
            "$contentType collapsed. Double tap to expand."
        }
    }

    // ============================================================================
    // NAVIGATION DESCRIPTIONS
    // ============================================================================

    /**
     * Generate content description for navigation items
     */
    fun navigationDescription(destination: String, isSelected: Boolean): String {
        return if (isSelected) {
            "$destination, currently selected"
        } else {
            "Navigate to $destination"
        }
    }

    /**
     * Generate content description for back navigation
     */
    fun backNavigationDescription(currentScreen: String = "current screen"): String {
        return "Go back from $currentScreen"
    }

    // ============================================================================
    // ACTION DESCRIPTIONS
    // ============================================================================

    /**
     * Generate content description for share action
     */
    fun shareDescription(itemType: String): String {
        return "Share this $itemType"
    }

    /**
     * Generate content description for delete action
     */
    fun deleteDescription(itemType: String): String {
        return "Delete this $itemType"
    }

    /**
     * Generate content description for edit action
     */
    fun editDescription(itemType: String): String {
        return "Edit this $itemType"
    }

    /**
     * Generate content description for refresh action
     */
    fun refreshDescription(contentType: String): String {
        return "Refresh $contentType"
    }
}

// ============================================================================
// MODIFIER EXTENSIONS FOR ACCESSIBILITY
// ============================================================================

/**
 * Add content description to a composable
 */
fun Modifier.accessibleDescription(description: String): Modifier {
    return this.semantics { contentDescription = description }
}

/**
 * Mark composable as a heading for screen readers
 */
fun Modifier.accessibleHeading(): Modifier {
    return this.semantics { heading() }
}

/**
 * Add state description to a composable
 */
fun Modifier.accessibleState(state: String): Modifier {
    return this.semantics { stateDescription = state }
}

/**
 * Combine content and state descriptions
 */
fun Modifier.accessible(
    contentDesc: String,
    stateDesc: String? = null
): Modifier {
    return this.semantics {
        contentDescription = contentDesc
        stateDesc?.let { stateDescription = it }
    }
}

// ============================================================================
// MINIMUM TOUCH TARGET CONSTANTS
// ============================================================================

/**
 * Minimum touch target size for accessibility compliance.
 * WCAG 2.1 recommends a minimum of 44x44 CSS pixels for touch targets.
 * Android Material guidelines recommend 48dp for touch targets.
 */
object TouchTargetSize {
    /** Minimum size for interactive elements (48dp) */
    const val MINIMUM_DP = 48

    /** Comfortable size for primary actions (56dp) */
    const val COMFORTABLE_DP = 56

    /** Large size for important CTAs (64dp) */
    const val LARGE_DP = 64
}

// ============================================================================
// CONTENT LABELS FOR COMMON ACTIONS
// ============================================================================

/**
 * Common accessibility labels to ensure consistency.
 */
object AccessibilityLabels {
    // Navigation
    const val NAVIGATE_BACK = "Go back"
    const val CLOSE = "Close"
    const val MENU = "Open menu"

    // Common actions
    const val REFRESH = "Refresh content"
    const val SEARCH = "Search"
    const val FILTER = "Filter options"
    const val SORT = "Sort options"
    const val SHARE = "Share"
    const val DELETE = "Delete"
    const val EDIT = "Edit"
    const val SAVE = "Save"
    const val CANCEL = "Cancel"

    // Toggle actions
    const val FAVORITE_ADD = "Add to favorites"
    const val FAVORITE_REMOVE = "Remove from favorites"
    const val BOOKMARK_ADD = "Add bookmark"
    const val BOOKMARK_REMOVE = "Remove bookmark"

    // Media actions
    const val PLAY = "Play"
    const val PAUSE = "Pause"
    const val STOP = "Stop"
    const val RECORD = "Start recording"
    const val STOP_RECORDING = "Stop recording"

    // Loading states
    const val LOADING = "Loading, please wait"
    const val LOADING_COMPLETE = "Loading complete"
}
