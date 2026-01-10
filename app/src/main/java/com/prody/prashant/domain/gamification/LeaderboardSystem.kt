package com.prody.prashant.domain.gamification

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Leaderboard System - Weekly Competition
 *
 * Weekly leaderboard resets every Monday at midnight.
 * Score formula: Weekly Score = (Clarity XP × 1.0) + (Discipline XP × 1.0) + (Courage XP × 1.2) + Streak Bonus
 *
 * Courage has a slight multiplier (1.2x) because writing to your future self
 * requires more vulnerability and is encouraged.
 *
 * Streak bonus adds incentive for daily consistency:
 * - 3-day streak: +10 bonus
 * - 7-day streak: +25 bonus
 * - 14-day streak: +50 bonus
 * - 30-day streak: +100 bonus
 */
object LeaderboardScoring {
    /** Multipliers for each skill type */
    const val CLARITY_MULTIPLIER = 1.0f
    const val DISCIPLINE_MULTIPLIER = 1.0f
    const val COURAGE_MULTIPLIER = 1.2f

    /** Streak bonuses */
    const val STREAK_3_DAY_BONUS = 10
    const val STREAK_7_DAY_BONUS = 25
    const val STREAK_14_DAY_BONUS = 50
    const val STREAK_30_DAY_BONUS = 100

    /**
     * Calculate weekly score from XP earned this week.
     *
     * @param clarityXpThisWeek Clarity XP earned this week
     * @param disciplineXpThisWeek Discipline XP earned this week
     * @param courageXpThisWeek Courage XP earned this week
     * @param currentStreak Current day streak
     * @return Total weekly score
     */
    fun calculateWeeklyScore(
        clarityXpThisWeek: Int,
        disciplineXpThisWeek: Int,
        courageXpThisWeek: Int,
        currentStreak: Int
    ): Int {
        val baseScore = (clarityXpThisWeek * CLARITY_MULTIPLIER +
                disciplineXpThisWeek * DISCIPLINE_MULTIPLIER +
                courageXpThisWeek * COURAGE_MULTIPLIER).toInt()

        val streakBonus = calculateStreakBonus(currentStreak)

        return baseScore + streakBonus
    }

    /**
     * Calculate streak bonus.
     */
    fun calculateStreakBonus(streak: Int): Int = when {
        streak >= 30 -> STREAK_30_DAY_BONUS
        streak >= 14 -> STREAK_14_DAY_BONUS
        streak >= 7 -> STREAK_7_DAY_BONUS
        streak >= 3 -> STREAK_3_DAY_BONUS
        else -> 0
    }

    /**
     * Get the start of the current week (Monday at midnight).
     */
    fun getWeekStartDate(today: LocalDate = LocalDate.now()): LocalDate {
        return today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    /**
     * Get the end of the current week (Sunday).
     */
    fun getWeekEndDate(today: LocalDate = LocalDate.now()): LocalDate {
        return today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    }

    /**
     * Check if a date is in the current week.
     */
    fun isInCurrentWeek(date: LocalDate, today: LocalDate = LocalDate.now()): Boolean {
        val weekStart = getWeekStartDate(today)
        val weekEnd = getWeekEndDate(today)
        return !date.isBefore(weekStart) && !date.isAfter(weekEnd)
    }

    /**
     * Days remaining in the current week.
     */
    fun daysRemainingInWeek(today: LocalDate = LocalDate.now()): Int {
        val weekEnd = getWeekEndDate(today)
        return (weekEnd.toEpochDay() - today.toEpochDay()).toInt()
    }
}

/**
 * A single entry in the leaderboard.
 */
data class LeaderboardEntry(
    val odId: String,
    val displayName: String,
    val avatarId: String,
    val titleId: String,
    val bannerId: String,
    val frameId: String,
    val weeklyScore: Int,
    val totalScore: Int,
    val rank: Int,
    val previousRank: Int,
    val currentStreak: Int,
    val clarityLevel: Int,
    val disciplineLevel: Int,
    val courageLevel: Int,
    val isCurrentUser: Boolean,
    val isDevBadgeHolder: Boolean = false,
    val isBetaTester: Boolean = false,
    val isFounder: Boolean = false,
    val lastActiveAt: Long
) {
    /**
     * Combined level for rank display.
     */
    val combinedLevel: Int get() = clarityLevel + disciplineLevel + courageLevel

    /**
     * Get current rank based on combined level.
     */
    val userRank: Rank get() = Rank.fromCombinedLevel(combinedLevel)

    /**
     * Rank change from previous week.
     */
    val rankChange: Int get() = previousRank - rank

    /**
     * Check if rank improved.
     */
    val hasImproved: Boolean get() = rankChange > 0

    /**
     * Check if rank declined.
     */
    val hasDeclined: Boolean get() = rankChange < 0

    /**
     * Check if rank stayed the same.
     */
    val noChange: Boolean get() = rankChange == 0
}

/**
 * Leaderboard state for UI.
 */
data class LeaderboardState(
    val entries: List<LeaderboardEntry>,
    val currentUserEntry: LeaderboardEntry?,
    val currentUserRank: Int?,
    val weekStartDate: LocalDate,
    val weekEndDate: LocalDate,
    val daysRemaining: Int,
    val totalParticipants: Int,
    val lastUpdatedAt: Long
) {
    /**
     * Get top N entries.
     */
    fun topEntries(count: Int = 10): List<LeaderboardEntry> = entries.take(count)

    /**
     * Check if current user is in top N.
     */
    fun isUserInTop(count: Int = 10): Boolean {
        return currentUserRank != null && currentUserRank <= count
    }

    /**
     * Get entries around the current user.
     */
    fun entriesAroundUser(context: Int = 2): List<LeaderboardEntry> {
        val userEntry = currentUserEntry ?: return emptyList()
        val userIndex = entries.indexOf(userEntry)
        if (userIndex == -1) return listOf(userEntry)

        val start = (userIndex - context).coerceAtLeast(0)
        val end = (userIndex + context + 1).coerceAtMost(entries.size)
        return entries.subList(start, end)
    }

    companion object {
        fun empty(): LeaderboardState {
            val today = LocalDate.now()
            return LeaderboardState(
                entries = emptyList(),
                currentUserEntry = null,
                currentUserRank = null,
                weekStartDate = LeaderboardScoring.getWeekStartDate(today),
                weekEndDate = LeaderboardScoring.getWeekEndDate(today),
                daysRemaining = LeaderboardScoring.daysRemainingInWeek(today),
                totalParticipants = 0,
                lastUpdatedAt = System.currentTimeMillis()
            )
        }
    }
}

/**
 * Weekly XP tracking per skill.
 */
data class WeeklyXpProgress(
    val clarityXp: Int,
    val disciplineXp: Int,
    val courageXp: Int,
    val weekStartDate: LocalDate
) {
    val totalXp: Int get() = clarityXp + disciplineXp + courageXp

    /**
     * Calculate weekly leaderboard score.
     */
    fun calculateScore(currentStreak: Int): Int {
        return LeaderboardScoring.calculateWeeklyScore(
            clarityXpThisWeek = clarityXp,
            disciplineXpThisWeek = disciplineXp,
            courageXpThisWeek = courageXp,
            currentStreak = currentStreak
        )
    }

    /**
     * Get breakdown of score calculation.
     */
    fun getScoreBreakdown(currentStreak: Int): ScoreBreakdown {
        val clarityContribution = (clarityXp * LeaderboardScoring.CLARITY_MULTIPLIER).toInt()
        val disciplineContribution = (disciplineXp * LeaderboardScoring.DISCIPLINE_MULTIPLIER).toInt()
        val courageContribution = (courageXp * LeaderboardScoring.COURAGE_MULTIPLIER).toInt()
        val streakBonus = LeaderboardScoring.calculateStreakBonus(currentStreak)

        return ScoreBreakdown(
            clarityBase = clarityXp,
            clarityContribution = clarityContribution,
            disciplineBase = disciplineXp,
            disciplineContribution = disciplineContribution,
            courageBase = courageXp,
            courageContribution = courageContribution,
            streakBonus = streakBonus,
            totalScore = clarityContribution + disciplineContribution + courageContribution + streakBonus
        )
    }

    companion object {
        fun empty(): WeeklyXpProgress {
            return WeeklyXpProgress(
                clarityXp = 0,
                disciplineXp = 0,
                courageXp = 0,
                weekStartDate = LeaderboardScoring.getWeekStartDate()
            )
        }
    }
}

/**
 * Breakdown of how weekly score is calculated.
 */
data class ScoreBreakdown(
    val clarityBase: Int,
    val clarityContribution: Int,
    val disciplineBase: Int,
    val disciplineContribution: Int,
    val courageBase: Int,
    val courageContribution: Int,
    val streakBonus: Int,
    val totalScore: Int
)

/**
 * Leaderboard tier for display purposes.
 */
enum class LeaderboardTier(
    val displayName: String,
    val minRank: Int,
    val maxRank: Int,
    val description: String
) {
    TOP_3("Summit", 1, 3, "The very top"),
    TOP_10("Ascendant", 4, 10, "Among the leaders"),
    TOP_25("Rising", 11, 25, "Climbing steadily"),
    TOP_50("Dedicated", 26, 50, "Committed to growth"),
    PARTICIPANT("Journeyer", 51, Int.MAX_VALUE, "Walking the path");

    companion object {
        fun fromRank(rank: Int): LeaderboardTier {
            return entries.first { rank in it.minRank..it.maxRank }
        }
    }
}

/**
 * Weekly summary shown at week's end.
 */
data class WeeklySummary(
    val weekStartDate: LocalDate,
    val weekEndDate: LocalDate,
    val finalRank: Int,
    val finalScore: Int,
    val scoreBreakdown: ScoreBreakdown,
    val rankChange: Int,
    val tier: LeaderboardTier,
    val daysActive: Int,
    val totalActivities: Int
) {
    val improvedFromLastWeek: Boolean get() = rankChange > 0
    val declinedFromLastWeek: Boolean get() = rankChange < 0

    /**
     * Get message based on performance.
     */
    fun getSummaryMessage(): String = when {
        tier == LeaderboardTier.TOP_3 -> "You reached the summit this week!"
        tier == LeaderboardTier.TOP_10 -> "Excellent week! You're among the leaders."
        improvedFromLastWeek && rankChange >= 10 -> "Great progress! You climbed $rankChange positions."
        improvedFromLastWeek -> "Nice improvement from last week."
        tier == LeaderboardTier.TOP_25 -> "Solid week of growth."
        declinedFromLastWeek -> "Keep at it. Every day is a new opportunity."
        else -> "Another week on the path. Keep growing."
    }
}
