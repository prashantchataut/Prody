package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for tracking AI Writing Companion suggestions shown to users.
 *
 * This tracks:
 * - Which suggestions were shown and when
 * - User interactions (accepted, dismissed, tapped)
 * - Context in which suggestions appeared
 *
 * Used for improving suggestion relevance and ensuring
 * we don't repeat the same suggestions too often.
 */
@Entity(
    tableName = "writing_suggestions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["journalEntryId"]),
        Index(value = ["suggestionType"]),
        Index(value = ["shownAt"])
    ]
)
data class WritingCompanionSuggestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User authentication
    val userId: String = "local",

    // Journal entry this suggestion was for
    val journalEntryId: Long? = null,

    // Type of suggestion
    val suggestionType: String, // "start_prompt", "continue_prompt", "stuck_help"

    // The suggestion text shown
    val suggestionText: String,

    // Context that triggered this suggestion
    val triggerContext: String, // "empty_entry", "pause_detected", "short_content", "help_button"

    // Context factors used to generate the suggestion
    val timeOfDay: String? = null,  // "morning", "afternoon", "evening"
    val currentMood: String? = null,
    val recentThemes: String? = null, // Comma-separated

    // User interaction
    val wasAccepted: Boolean = false,
    val wasDismissed: Boolean = false,
    val acceptedAt: Long? = null,
    val dismissedAt: Long? = null,

    // Timestamps
    val shownAt: Long = System.currentTimeMillis(),

    // Sync metadata
    val syncStatus: String = "pending",
    val isDeleted: Boolean = false
) {
    companion object {
        const val TYPE_START_PROMPT = "start_prompt"
        const val TYPE_CONTINUE_PROMPT = "continue_prompt"
        const val TYPE_STUCK_HELP = "stuck_help"

        const val TRIGGER_EMPTY_ENTRY = "empty_entry"
        const val TRIGGER_PAUSE_DETECTED = "pause_detected"
        const val TRIGGER_SHORT_CONTENT = "short_content"
        const val TRIGGER_HELP_BUTTON = "help_button"
    }

    /**
     * Whether the user engaged with this suggestion in any way
     */
    val wasEngaged: Boolean
        get() = wasAccepted || wasDismissed
}
