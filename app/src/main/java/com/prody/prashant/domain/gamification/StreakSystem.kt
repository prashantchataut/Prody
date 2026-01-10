package com.prody.prashant.domain.gamification

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Streak data with Mindful Break (freeze) support.
 *
 * Philosophy: Streaks should motivate, not punish. Missing a day happens.
 * The system acknowledges that with "Mindful Breaks" instead of harsh resets.
 */
data class StreakData(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActiveDate: LocalDate,
    val freezesAvailable: Int,
    val freezesUsedThisMonth: Int,
    val lastFreezeResetMonth: Int
) {
    /**
     * Check if user can use a Mindful Break.
     */
    val canUseMindfulBreak: Boolean
        get() = freezesAvailable > 0

    /**
     * Check if currently on a streak.
     */
    val isOnStreak: Boolean
        get() = currentStreak > 0

    /**
     * Check if at a milestone (7, 14, 30, 60, 100, 365).
     */
    val isAtMilestone: Boolean
        get() = currentStreak in MILESTONES

    /**
     * Get next milestone.
     */
    val nextMilestone: Int
        get() = MILESTONES.firstOrNull { it > currentStreak } ?: Int.MAX_VALUE

    /**
     * Days until next milestone.
     */
    val daysToNextMilestone: Int
        get() = (nextMilestone - currentStreak).coerceAtLeast(0)

    companion object {
        /** Streak milestones that trigger celebrations. */
        val MILESTONES = listOf(7, 14, 30, 60, 100, 365)

        /** Maximum freezes available per month. */
        const val MAX_FREEZES_PER_MONTH = 2

        /** Hours after missed day to use freeze. */
        const val FREEZE_WINDOW_HOURS = 24

        fun initial(): StreakData = StreakData(
            currentStreak = 0,
            longestStreak = 0,
            lastActiveDate = LocalDate.now(),
            freezesAvailable = MAX_FREEZES_PER_MONTH,
            freezesUsedThisMonth = 0,
            lastFreezeResetMonth = LocalDate.now().monthValue
        )
    }
}

/**
 * Result of recording daily activity for streak.
 */
sealed class StreakUpdateResult {
    /**
     * Streak maintained (same day activity).
     */
    data class Maintained(val streak: Int) : StreakUpdateResult()

    /**
     * Streak incremented (consecutive day).
     */
    data class Incremented(
        val newStreak: Int,
        val previousStreak: Int,
        val isNewLongest: Boolean,
        val milestoneReached: Int?
    ) : StreakUpdateResult()

    /**
     * Streak was broken (gap > 1 day, no freeze used).
     */
    data class Broken(
        val previousStreak: Int,
        val newStreak: Int,
        val daysMissed: Int,
        val canUseFreeze: Boolean
    ) : StreakUpdateResult()

    /**
     * Streak preserved by using Mindful Break.
     */
    data class PreservedWithFreeze(
        val streak: Int,
        val freezesRemaining: Int
    ) : StreakUpdateResult()

    /**
     * Error during update.
     */
    data class Error(val message: String) : StreakUpdateResult()
}

/**
 * Result of attempting to use a Mindful Break.
 */
sealed class MindfulBreakResult {
    /**
     * Mindful Break used successfully.
     */
    data class Success(
        val preservedStreak: Int,
        val freezesRemaining: Int
    ) : MindfulBreakResult()

    /**
     * No freezes available.
     */
    object NoFreezesAvailable : MindfulBreakResult()

    /**
     * Not within freeze window (too late).
     */
    object OutsideFreezeWindow : MindfulBreakResult()

    /**
     * Streak wasn't broken (no need for freeze).
     */
    object StreakNotBroken : MindfulBreakResult()

    /**
     * Error.
     */
    data class Error(val message: String) : MindfulBreakResult()
}

/**
 * Helper class for streak calculations.
 */
object StreakCalculator {

    /**
     * Calculate streak status based on dates.
     */
    fun calculateStreakStatus(
        lastActiveDate: LocalDate,
        today: LocalDate = LocalDate.now()
    ): StreakStatus {
        val daysSince = ChronoUnit.DAYS.between(lastActiveDate, today).toInt()

        return when {
            daysSince == 0 -> StreakStatus.SAME_DAY
            daysSince == 1 -> StreakStatus.CONSECUTIVE
            daysSince == 2 -> StreakStatus.CAN_FREEZE
            else -> StreakStatus.BROKEN
        }
    }

    /**
     * Check if a milestone was reached.
     */
    fun getMilestoneReached(previousStreak: Int, newStreak: Int): Int? {
        return StreakData.MILESTONES.firstOrNull { milestone ->
            previousStreak < milestone && newStreak >= milestone
        }
    }

    /**
     * Calculate if freezes should be reset (new month).
     */
    fun shouldResetFreezes(lastResetMonth: Int, currentMonth: Int): Boolean {
        return currentMonth != lastResetMonth
    }

    /**
     * Get motivational message for streak.
     */
    fun getStreakMessage(streak: Int): String = when {
        streak == 0 -> "Start your streak today!"
        streak == 1 -> "Day 1 - A journey of a thousand days begins with one."
        streak in 2..6 -> "$streak days - You're building momentum."
        streak == 7 -> "One week! Consistency is becoming habit."
        streak in 8..13 -> "$streak days - Keep the fire burning."
        streak == 14 -> "Two weeks! Your commitment shows."
        streak in 15..29 -> "$streak days - Nearly a month of dedication."
        streak == 30 -> "One month! You are a Flame Keeper."
        streak in 31..59 -> "$streak days - The fire burns steady."
        streak == 60 -> "Two months! Your consistency is remarkable."
        streak in 61..99 -> "$streak days - The century approaches."
        streak == 100 -> "100 days! A true testament to your dedication."
        streak in 101..364 -> "$streak days - Ever closer to a full year."
        streak == 365 -> "One full year! You are the Eternal Flame."
        else -> "$streak days - A legend in consistency."
    }

    /**
     * Get message for broken streak.
     */
    fun getBrokenStreakMessage(previousStreak: Int, freezesAvailable: Int): String {
        val streakPart = when {
            previousStreak >= 30 -> "Your $previousStreak-day streak took a break."
            previousStreak >= 7 -> "Your ${previousStreak}-day streak paused."
            else -> "Yesterday was a rest day."
        }

        val freezePart = if (freezesAvailable > 0) {
            " You can use a Mindful Break to preserve it."
        } else {
            " Fresh starts are beautiful too."
        }

        return streakPart + freezePart
    }
}

/**
 * Status of streak based on activity timing.
 */
enum class StreakStatus {
    /** Already active today, streak maintained. */
    SAME_DAY,

    /** Last activity was yesterday, streak continues. */
    CONSECUTIVE,

    /** Missed yesterday, but within freeze window. */
    CAN_FREEZE,

    /** Streak is broken (missed 2+ days or freeze not used). */
    BROKEN
}

/**
 * Daily activity record for streak tracking.
 */
data class DailyActivity(
    val date: LocalDate,
    val hasJournalEntry: Boolean,
    val hasMicroEntry: Boolean,
    val hasBloom: Boolean,
    val hasFutureMessage: Boolean,
    val hasFlashcardSession: Boolean
) {
    /**
     * Check if any qualifying activity was done.
     */
    val hasAnyActivity: Boolean
        get() = hasJournalEntry || hasMicroEntry || hasBloom ||
                hasFutureMessage || hasFlashcardSession

    /**
     * Get description of activities.
     */
    fun getActivitySummary(): String {
        val activities = mutableListOf<String>()
        if (hasJournalEntry) activities.add("journaled")
        if (hasMicroEntry) activities.add("captured thoughts")
        if (hasBloom) activities.add("bloomed a seed")
        if (hasFutureMessage) activities.add("wrote to future self")
        if (hasFlashcardSession) activities.add("reviewed words")

        return when (activities.size) {
            0 -> "No activity"
            1 -> "You ${activities[0]}"
            else -> "You ${activities.dropLast(1).joinToString(", ")} and ${activities.last()}"
        }
    }
}
