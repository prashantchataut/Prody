package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for micro journal entries - quick, low-friction thought capture.
 *
 * Micro-entries are designed for:
 * - Ultra-fast capture (under 10 seconds)
 * - Brief thoughts (max 280 chars)
 * - Optional mood tagging
 * - Later expansion into full journal entries
 *
 * These appear in the journal timeline alongside full entries
 * but are visually distinct as quick captures.
 */
@Entity(
    tableName = "micro_entries",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["createdAt"]),
        Index(value = ["userId", "createdAt"]),
        Index(value = ["expandedToEntryId"])
    ]
)
data class MicroEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User authentication - prepared for multi-user support
    val userId: String = "local",

    // The quick thought (max 280 characters enforced at UI level)
    val content: String,

    // Optional mood (same enum as full entries)
    val mood: String? = null,

    // Optional mood intensity (1-10 scale)
    val moodIntensity: Int? = null,

    // Timestamp of creation
    val createdAt: Long = System.currentTimeMillis(),

    // If this micro-entry was expanded into a full entry, track the link
    val expandedToEntryId: Long? = null,
    val expandedAt: Long? = null,

    // Context about when this was captured
    val captureContext: String? = null, // e.g., "morning_ritual", "evening_ritual", "quick_capture"

    // Location context (if user opts in)
    val locationContext: String? = null,

    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false
) {
    companion object {
        const val MAX_CONTENT_LENGTH = 280

        const val CONTEXT_MORNING_RITUAL = "morning_ritual"
        const val CONTEXT_EVENING_RITUAL = "evening_ritual"
        const val CONTEXT_QUICK_CAPTURE = "quick_capture"
        const val CONTEXT_DAILY_INTENTION = "daily_intention"
    }

    /**
     * Check if this micro-entry has been expanded to a full entry
     */
    val isExpanded: Boolean
        get() = expandedToEntryId != null
}
