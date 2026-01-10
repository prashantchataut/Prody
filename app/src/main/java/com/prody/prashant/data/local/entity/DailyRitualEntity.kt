package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for tracking daily ritual completions and user's daily intentions/reflections.
 *
 * The Daily Ritual is a 60-second daily hook that:
 * - Delivers immediate value (today's seed/wisdom)
 * - Captures quick intentions (morning) or reflections (evening)
 * - Builds daily habit through simplicity
 */
@Entity(
    tableName = "daily_rituals",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["date"]),
        Index(value = ["userId", "date"], unique = true)
    ]
)
data class DailyRitualEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User authentication
    val userId: String = "local",

    // Date of the ritual (day timestamp at 00:00)
    val date: Long,

    // Morning ritual data
    val morningCompleted: Boolean = false,
    val morningCompletedAt: Long? = null,
    val morningIntention: String? = null,  // "What's one thing you want to focus on today?"
    val morningMood: String? = null,
    val morningWisdomId: Long? = null,     // ID of the seed/quote shown

    // Evening ritual data
    val eveningCompleted: Boolean = false,
    val eveningCompletedAt: Long? = null,
    val eveningDayRating: String? = null,  // "good", "neutral", "tough"
    val eveningReflection: String? = null, // Quick evening capture
    val eveningMood: String? = null,

    // Tracking
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    // Whether user created a deeper entry from the ritual
    val expandedToJournalId: Long? = null,
    val expandedToMicroEntryId: Long? = null,

    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false
) {
    companion object {
        const val DAY_RATING_GOOD = "good"
        const val DAY_RATING_NEUTRAL = "neutral"
        const val DAY_RATING_TOUGH = "tough"

        /**
         * Get the date timestamp for today (midnight)
         */
        fun getTodayDate(): Long {
            val now = java.time.LocalDate.now()
            return now.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }

    /**
     * Check if any part of the ritual was completed today
     */
    val hasAnyActivity: Boolean
        get() = morningCompleted || eveningCompleted

    /**
     * Check if both rituals are complete
     */
    val isFullyComplete: Boolean
        get() = morningCompleted && eveningCompleted
}
