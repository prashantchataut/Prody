package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Streak Data Entity - Enhanced streak tracking with Mindful Break support.
 *
 * Philosophy: Streaks should motivate, not punish. Missing a day happens.
 * The system acknowledges this with "Mindful Breaks" instead of harsh resets.
 *
 * Mindful Breaks:
 * - 2 available per month (reset on 1st of each month)
 * - Can be used within 24 hours of missing a day
 * - Preserves the streak without adding a day
 *
 * Milestones: 7, 14, 30, 60, 100, 365 days
 */
@Entity(
    tableName = "streak_data",
    indices = [Index(value = ["userId"], unique = true)]
)
data class StreakDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",

    // Current streak state
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: Long = System.currentTimeMillis(), // Date of last qualifying activity

    // Mindful Break (freeze) tracking
    val freezesAvailable: Int = 2,
    val freezesUsedThisMonth: Int = 0,
    val lastFreezeResetMonth: Int = 0, // Month number (1-12) when freezes were last reset

    // Streak history
    val totalDaysActive: Int = 0, // All-time days with activity
    val streakBrokenCount: Int = 0, // How many times streak was reset to 0

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val MAX_FREEZES_PER_MONTH = 2
        const val FREEZE_WINDOW_HOURS = 24
    }
}

/**
 * Daily Activity Record - Tracks what activities were done each day.
 *
 * This provides a detailed history of daily engagement for:
 * - Streak calculation verification
 * - Activity pattern analysis
 * - Bloom tracking correlation
 */
@Entity(
    tableName = "daily_activity",
    indices = [
        Index(value = ["userId", "date"], unique = true),
        Index(value = ["date"])
    ]
)
data class DailyActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val date: Long, // Start of day timestamp

    // Activity flags
    val hasJournalEntry: Boolean = false,
    val hasMicroEntry: Boolean = false,
    val hasBloom: Boolean = false,
    val hasFutureMessage: Boolean = false,
    val hasFlashcardSession: Boolean = false,
    val hasWisdomEngagement: Boolean = false, // Viewed word/quote/proverb

    // XP earned this day
    val clarityXpEarned: Int = 0,
    val disciplineXpEarned: Int = 0,
    val courageXpEarned: Int = 0,

    // Streak info at end of day
    val streakDayNumber: Int = 0, // Which day of the streak this was
    val usedMindfulBreak: Boolean = false, // Was a freeze used this day?

    // Timestamps
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Check if any qualifying activity was done.
     */
    fun hasAnyActivity(): Boolean =
        hasJournalEntry || hasMicroEntry || hasBloom ||
                hasFutureMessage || hasFlashcardSession || hasWisdomEngagement

    /**
     * Get total XP earned this day.
     */
    fun totalXpEarned(): Int = clarityXpEarned + disciplineXpEarned + courageXpEarned
}

/**
 * Mindful Break Usage Record - Tracks when freezes were used.
 *
 * Kept separate for audit trail and analytics.
 */
@Entity(
    tableName = "mindful_break_usage",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["usedAt"])
    ]
)
data class MindfulBreakUsageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val usedAt: Long = System.currentTimeMillis(),
    val preservedStreak: Int, // Streak that was preserved
    val missedDate: Long // The date that was missed
)
