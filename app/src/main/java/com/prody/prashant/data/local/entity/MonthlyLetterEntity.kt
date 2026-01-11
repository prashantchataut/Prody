package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for monthly growth letters - personalized summaries that feel like opening mail.
 *
 * Generated on the first day of each month for the previous month, providing:
 * - Activity summary (entries, words, active days)
 * - Theme analysis from AI tags
 * - Mood journey and progression
 * - Buddha's pattern observation
 * - Milestones achieved and upcoming
 * - Personal, warm closing message
 *
 * The tone is personal and conversational, like a caring friend who's been watching your journey.
 */
@Entity(
    tableName = "monthly_letters",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["month", "year"]),
        Index(value = ["userId", "month", "year"], unique = true),
        Index(value = ["generatedAt"])
    ]
)
data class MonthlyLetterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User authentication
    val userId: String = "local",

    // Time period
    val month: Int, // 1-12
    val year: Int,

    // Opening
    val greeting: String, // Personalized greeting based on month and user's journey

    // Activity summary
    val entriesCount: Int = 0,
    val microEntriesCount: Int = 0,
    val totalWords: Int = 0,
    val activeDays: Int = 0,
    val averageWordsPerEntry: Int = 0,
    val mostActiveWeek: String? = null, // Week date range when most active

    // Theme analysis
    val topThemes: String = "", // JSON array of top themes from AI tags
    val themesAnalysis: String = "", // Narrative about themes: "You wrote a lot about work. Something shifting there?"
    val recurringWords: String = "", // JSON array of words that appeared frequently

    // Mood journey
    val moodJourney: String = "", // JSON array with mood progression [{date, mood, intensity}, ...]
    val dominantMood: String? = null,
    val moodAnalysis: String = "", // Narrative: "Your mood lifted in the last week. I noticed."
    val moodTrend: String = "stable", // improving, stable, declining

    // Buddha's insight
    val patternObservation: String = "", // Personal observation about patterns seen
    val buddhaWisdom: String? = null, // Optional wisdom quote related to their month

    // Milestones
    val achievedMilestones: String = "", // JSON array of milestones achieved this month
    val upcomingMilestones: String = "", // JSON array of milestones close to achieving
    val streakInfo: String? = null, // Special note about streak if significant

    // Comparison to previous month
    val entriesChangePercent: Int = 0,
    val wordsChangePercent: Int = 0,
    val comparisonNote: String? = null, // Brief note about month-over-month change

    // Highlights
    val highlightEntryId: Long? = null, // Most significant entry of the month
    val highlightQuote: String? = null, // A meaningful quote from user's writing
    val highlightReason: String? = null, // Why this entry stood out

    // Closing
    val closingMessage: String = "", // Warm, personal closing: "Keep going. You're doing better than you think."
    val encouragementNote: String? = null, // Additional encouragement if needed

    // Metadata
    val generatedAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val readAt: Long? = null,
    val isFavorite: Boolean = false,
    val sharedAt: Long? = null, // When user shared this letter

    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false
) {
    companion object {
        const val MOOD_TREND_IMPROVING = "improving"
        const val MOOD_TREND_STABLE = "stable"
        const val MOOD_TREND_DECLINING = "declining"

        // Month names for display
        val MONTH_NAMES = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
    }

    /**
     * Returns true if the month had any activity
     */
    val hasActivity: Boolean
        get() = entriesCount > 0 || microEntriesCount > 0

    /**
     * Returns a human-readable month name
     */
    fun getMonthName(): String {
        return if (month in 1..12) MONTH_NAMES[month - 1] else "Unknown"
    }

    /**
     * Returns a display string like "January 2024"
     */
    fun getDisplayTitle(): String {
        return "${getMonthName()} $year"
    }

    /**
     * Returns a shorter display like "Jan '24"
     */
    fun getShortDisplayTitle(): String {
        val monthShort = if (month in 1..12) MONTH_NAMES[month - 1].take(3) else "???"
        val yearShort = year.toString().takeLast(2)
        return "$monthShort '$yearShort"
    }
}
