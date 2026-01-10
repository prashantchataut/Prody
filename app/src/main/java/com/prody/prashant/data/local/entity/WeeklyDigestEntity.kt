package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for weekly digest summaries of user's journaling activity.
 *
 * Generated every Sunday (or Monday morning), the digest provides:
 * - Weekly statistics (entries, words, active days)
 * - Mood trends and patterns
 * - Top themes from the week
 * - Buddha's personalized reflection
 * - Comparison to previous week
 *
 * Last 4 digests are kept for historical review.
 */
@Entity(
    tableName = "weekly_digests",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["weekStartDate"]),
        Index(value = ["userId", "weekStartDate"], unique = true)
    ]
)
data class WeeklyDigestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User authentication
    val userId: String = "local",

    // Week period (Monday to Sunday)
    val weekStartDate: Long, // Start of week (Monday 00:00)
    val weekEndDate: Long,   // End of week (Sunday 23:59)

    // Activity statistics
    val entriesCount: Int = 0,
    val microEntriesCount: Int = 0,
    val totalWordsWritten: Int = 0,
    val activeDays: Int = 0,
    val averageWordsPerEntry: Int = 0,

    // Mood analysis
    val dominantMood: String? = null,
    val moodTrend: String = "stable", // improving, stable, declining
    val moodDistribution: String = "", // JSON: {"HAPPY": 3, "CALM": 2, ...}

    // Theme analysis (comma-separated top themes)
    val topThemes: String = "",
    val recurringPatterns: String = "", // JSON array of detected patterns

    // Buddha's personalized weekly reflection
    val buddhaReflection: String? = null,

    // Week-over-week comparison
    val entriesChangePercent: Int = 0,
    val wordsChangePercent: Int = 0,
    val previousWeekEntriesCount: Int = 0,
    val previousWeekWordsWritten: Int = 0,

    // Highlights
    val highlightEntryId: Long? = null, // Most significant entry of the week
    val highlightQuote: String? = null, // A quote from user's writing

    // Status
    val isRead: Boolean = false,
    val readAt: Long? = null,
    val generatedAt: Long = System.currentTimeMillis(),

    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false
) {
    companion object {
        const val MOOD_TREND_IMPROVING = "improving"
        const val MOOD_TREND_STABLE = "stable"
        const val MOOD_TREND_DECLINING = "declining"

        // Maximum digests to keep
        const val MAX_STORED_DIGESTS = 4
    }

    /**
     * Returns true if the week had any activity
     */
    val hasActivity: Boolean
        get() = entriesCount > 0 || microEntriesCount > 0

    /**
     * Returns a human-readable date range string
     */
    fun getDateRangeDisplay(): String {
        val startDate = java.time.Instant.ofEpochMilli(weekStartDate)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        val endDate = java.time.Instant.ofEpochMilli(weekEndDate)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()

        val startMonth = startDate.month.name.take(3).lowercase()
            .replaceFirstChar { it.uppercase() }
        val endMonth = endDate.month.name.take(3).lowercase()
            .replaceFirstChar { it.uppercase() }

        return if (startMonth == endMonth) {
            "$startMonth ${startDate.dayOfMonth}-${endDate.dayOfMonth}"
        } else {
            "$startMonth ${startDate.dayOfMonth} - $endMonth ${endDate.dayOfMonth}"
        }
    }
}
