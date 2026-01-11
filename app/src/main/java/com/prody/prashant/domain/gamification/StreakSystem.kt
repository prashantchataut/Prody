package com.prody.prashant.domain.gamification

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Enhanced Streak System with Humane Design
 *
 * Philosophy: Streaks should motivate, not punish. Life happens.
 * The system acknowledges that with:
 *
 * 1. GRACE PERIODS (Automatic Forgiveness)
 *    - 1 automatic grace period every 2 weeks
 *    - Applied automatically when you miss a single day
 *    - No action required from user - just come back tomorrow
 *    - Resets every 14 days from first use
 *
 * 2. FREEZE TOKENS (Mindful Breaks)
 *    - Earned through Discipline skill perks (not given for free)
 *    - Level 5: +1 token, Level 12: +1 token
 *    - Maximum 3 tokens can be saved
 *    - Must be manually activated within 24 hours of missing a day
 *    - Preserves streak when grace period already used
 *
 * 3. STREAK MILESTONES
 *    - 7 days: Week Warrior
 *    - 14 days: Fortnight Champion
 *    - 30 days: Monthly Master
 *    - 60 days: Dedicated Seeker
 *    - 100 days: Century Sage
 *    - 365 days: Eternal Flame
 *
 * The goal is to create a system that:
 * - Rewards consistency without creating anxiety
 * - Acknowledges that missing a day is human
 * - Makes freeze tokens feel valuable (earned, not free)
 * - Celebrates milestones meaningfully
 */
data class StreakData(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActiveDate: LocalDate,
    // Grace period tracking
    val gracePeriodAvailable: Boolean,
    val lastGracePeriodUsed: LocalDate?,
    // Freeze token tracking (earned through perks)
    val freezeTokensEarned: Int,
    val freezeTokensUsed: Int,
    // Legacy fields for backward compatibility
    val freezesAvailable: Int,
    val freezesUsedThisMonth: Int,
    val lastFreezeResetMonth: Int,
    // Statistics
    val totalDaysActive: Int,
    val totalStreaksStarted: Int,
    val longestStreakDate: LocalDate?
) {
    /**
     * Check if automatic grace period is available.
     * Grace period resets every 14 days after use.
     */
    val canUseGracePeriod: Boolean
        get() {
            if (!gracePeriodAvailable) {
                // Check if 14 days have passed since last use
                val lastUsed = lastGracePeriodUsed ?: return true
                return ChronoUnit.DAYS.between(lastUsed, LocalDate.now()) >= GRACE_PERIOD_COOLDOWN_DAYS
            }
            return true
        }

    /**
     * Check if user has freeze tokens available (earned through perks).
     */
    val canUseFreezeToken: Boolean
        get() = availableFreezeTokens > 0

    /**
     * Available freeze tokens (earned minus used, max 3).
     */
    val availableFreezeTokens: Int
        get() = (freezeTokensEarned - freezeTokensUsed).coerceIn(0, MAX_FREEZE_TOKENS)

    /**
     * Check if user can use a Mindful Break (either grace period or freeze token).
     */
    val canUseMindfulBreak: Boolean
        get() = canUseGracePeriod || canUseFreezeToken

    /**
     * Check if currently on a streak.
     */
    val isOnStreak: Boolean
        get() = currentStreak > 0

    /**
     * Check if at a milestone.
     */
    val isAtMilestone: Boolean
        get() = currentStreak in MILESTONES

    /**
     * Get current milestone info if at one.
     */
    val currentMilestone: StreakMilestone?
        get() = StreakMilestone.forDays(currentStreak)

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

    /**
     * Progress toward next milestone (0.0 to 1.0).
     */
    val progressToNextMilestone: Float
        get() {
            val previousMilestone = MILESTONES.lastOrNull { it <= currentStreak } ?: 0
            val next = nextMilestone
            if (next == Int.MAX_VALUE) return 1f
            val range = next - previousMilestone
            val progress = currentStreak - previousMilestone
            return (progress.toFloat() / range).coerceIn(0f, 1f)
        }

    /**
     * Get streak tier based on current streak.
     */
    val tier: StreakTier
        get() = StreakTier.forStreak(currentStreak)

    /**
     * Get streak intensity for visual effects (0.0 to 1.0).
     * Higher streaks = more intense fire effects.
     */
    val intensity: Float
        get() = when {
            currentStreak == 0 -> 0f
            currentStreak < 7 -> 0.3f
            currentStreak < 14 -> 0.5f
            currentStreak < 30 -> 0.6f
            currentStreak < 60 -> 0.7f
            currentStreak < 100 -> 0.8f
            currentStreak < 365 -> 0.9f
            else -> 1f
        }

    companion object {
        /** Streak milestones that trigger celebrations. */
        val MILESTONES = listOf(7, 14, 30, 60, 100, 365)

        /** Grace period cooldown in days. */
        const val GRACE_PERIOD_COOLDOWN_DAYS = 14L

        /** Maximum freeze tokens that can be saved. */
        const val MAX_FREEZE_TOKENS = 3

        /** Hours after missed day to use freeze token. */
        const val FREEZE_WINDOW_HOURS = 24

        /** Legacy: Maximum freezes available per month (deprecated). */
        const val MAX_FREEZES_PER_MONTH = 2

        fun initial(): StreakData = StreakData(
            currentStreak = 0,
            longestStreak = 0,
            lastActiveDate = LocalDate.now(),
            gracePeriodAvailable = true,
            lastGracePeriodUsed = null,
            freezeTokensEarned = 0,
            freezeTokensUsed = 0,
            freezesAvailable = 0, // No free freezes in new system
            freezesUsedThisMonth = 0,
            lastFreezeResetMonth = LocalDate.now().monthValue,
            totalDaysActive = 0,
            totalStreaksStarted = 0,
            longestStreakDate = null
        )

        /**
         * Create from existing data (migration helper).
         */
        fun fromLegacy(
            currentStreak: Int,
            longestStreak: Int,
            lastActiveDate: LocalDate,
            freezesAvailable: Int,
            freezesUsedThisMonth: Int,
            lastFreezeResetMonth: Int
        ): StreakData = StreakData(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastActiveDate = lastActiveDate,
            gracePeriodAvailable = true,
            lastGracePeriodUsed = null,
            freezeTokensEarned = freezesAvailable, // Convert old freezes to tokens
            freezeTokensUsed = 0,
            freezesAvailable = freezesAvailable,
            freezesUsedThisMonth = freezesUsedThisMonth,
            lastFreezeResetMonth = lastFreezeResetMonth,
            totalDaysActive = currentStreak,
            totalStreaksStarted = if (currentStreak > 0) 1 else 0,
            longestStreakDate = if (longestStreak > 0) lastActiveDate else null
        )
    }
}

/**
 * Streak tiers for visual representation.
 */
enum class StreakTier(
    val displayName: String,
    val minDays: Int,
    val colorName: String
) {
    SPARK("Spark", 0, "warm"),
    EMBER("Ember", 3, "warm"),
    FLAME("Flame", 7, "fire"),
    BLAZE("Blaze", 14, "fire"),
    INFERNO("Inferno", 30, "hot"),
    PHOENIX("Phoenix", 60, "hot"),
    SUPERNOVA("Supernova", 100, "blazing"),
    ETERNAL("Eternal Flame", 365, "blazing");

    companion object {
        fun forStreak(days: Int): StreakTier = entries
            .sortedByDescending { it.minDays }
            .first { days >= it.minDays }
    }
}

/**
 * Streak milestone definitions with rewards and messages.
 */
enum class StreakMilestone(
    val days: Int,
    val title: String,
    val description: String,
    val xpBonus: Int,
    val celebrationDuration: Int // milliseconds
) {
    WEEK_WARRIOR(7, "Week Warrior", "7 days of consistent dedication!", 50, 2000),
    FORTNIGHT_CHAMPION(14, "Fortnight Champion", "Two weeks strong!", 100, 2500),
    MONTHLY_MASTER(30, "Monthly Master", "A full month of commitment!", 200, 3000),
    DEDICATED_SEEKER(60, "Dedicated Seeker", "Two months of growth!", 350, 3500),
    CENTURY_SAGE(100, "Century Sage", "100 days - A true testament!", 500, 4000),
    ETERNAL_FLAME(365, "Eternal Flame", "One year! Legendary dedication!", 1000, 5000);

    companion object {
        fun forDays(days: Int): StreakMilestone? = entries.find { it.days == days }
    }
}

/**
 * Result of recording daily activity for streak.
 */
sealed class StreakUpdateResult {
    /**
     * Streak maintained (same day activity).
     */
    data class Maintained(
        val streak: Int,
        val tier: StreakTier
    ) : StreakUpdateResult()

    /**
     * Streak incremented (consecutive day).
     */
    data class Incremented(
        val newStreak: Int,
        val previousStreak: Int,
        val isNewLongest: Boolean,
        val milestoneReached: StreakMilestone?,
        val newTier: StreakTier,
        val previousTier: StreakTier,
        val tierChanged: Boolean
    ) : StreakUpdateResult()

    /**
     * Streak preserved automatically by grace period.
     */
    data class PreservedWithGracePeriod(
        val streak: Int,
        val tier: StreakTier,
        val nextGracePeriodIn: Int // Days until grace period resets
    ) : StreakUpdateResult()

    /**
     * Streak preserved by using freeze token.
     */
    data class PreservedWithFreezeToken(
        val streak: Int,
        val tier: StreakTier,
        val tokensRemaining: Int
    ) : StreakUpdateResult()

    /**
     * Streak was broken (gap > 1 day, no protection available).
     */
    data class Broken(
        val previousStreak: Int,
        val newStreak: Int,
        val daysMissed: Int,
        val canUseGracePeriod: Boolean,
        val canUseFreezeToken: Boolean,
        val freezeTokensAvailable: Int
    ) : StreakUpdateResult()

    /**
     * Legacy: Streak preserved by using Mindful Break (for backward compatibility).
     */
    data class PreservedWithFreeze(
        val streak: Int,
        val freezesRemaining: Int
    ) : StreakUpdateResult()

    /**
     * Streak at risk - missed yesterday, need to act today.
     */
    data class AtRisk(
        val streak: Int,
        val tier: StreakTier,
        val hoursRemaining: Int,
        val canUseGracePeriod: Boolean,
        val canUseFreezeToken: Boolean
    ) : StreakUpdateResult()

    /**
     * Error during update.
     */
    data class Error(val message: String) : StreakUpdateResult()
}

/**
 * Result of attempting to use a Mindful Break (grace period or freeze token).
 */
sealed class MindfulBreakResult {
    /**
     * Grace period used automatically.
     */
    data class GracePeriodUsed(
        val preservedStreak: Int,
        val tier: StreakTier,
        val nextGracePeriodIn: Int // Days until grace period resets
    ) : MindfulBreakResult()

    /**
     * Freeze token used successfully.
     */
    data class FreezeTokenUsed(
        val preservedStreak: Int,
        val tier: StreakTier,
        val tokensRemaining: Int
    ) : MindfulBreakResult()

    /**
     * Legacy: Mindful Break used successfully.
     */
    data class Success(
        val preservedStreak: Int,
        val freezesRemaining: Int
    ) : MindfulBreakResult()

    /**
     * No grace period or freeze tokens available.
     */
    object NoProtectionAvailable : MindfulBreakResult()

    /**
     * No freezes available (legacy).
     */
    object NoFreezesAvailable : MindfulBreakResult()

    /**
     * Not within freeze window (too late).
     */
    object OutsideFreezeWindow : MindfulBreakResult()

    /**
     * Streak wasn't broken (no need for protection).
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
     * Check if a milestone was reached and return milestone info.
     */
    fun getMilestoneReached(previousStreak: Int, newStreak: Int): StreakMilestone? {
        val milestoneDay = StreakData.MILESTONES.firstOrNull { milestone ->
            previousStreak < milestone && newStreak >= milestone
        }
        return milestoneDay?.let { StreakMilestone.forDays(it) }
    }

    /**
     * Check if tier changed.
     */
    fun checkTierChange(previousStreak: Int, newStreak: Int): Pair<StreakTier, StreakTier>? {
        val previousTier = StreakTier.forStreak(previousStreak)
        val newTier = StreakTier.forStreak(newStreak)
        return if (previousTier != newTier) Pair(previousTier, newTier) else null
    }

    /**
     * Calculate days until grace period resets.
     */
    fun daysUntilGracePeriodReset(lastUsed: LocalDate?): Int {
        if (lastUsed == null) return 0
        val daysSinceUse = ChronoUnit.DAYS.between(lastUsed, LocalDate.now()).toInt()
        return (StreakData.GRACE_PERIOD_COOLDOWN_DAYS.toInt() - daysSinceUse).coerceAtLeast(0)
    }

    /**
     * Calculate if grace period is available.
     */
    fun isGracePeriodAvailable(lastUsed: LocalDate?): Boolean {
        if (lastUsed == null) return true
        return ChronoUnit.DAYS.between(lastUsed, LocalDate.now()) >= StreakData.GRACE_PERIOD_COOLDOWN_DAYS
    }

    /**
     * Calculate if freezes should be reset (new month) - legacy support.
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
        streak == 7 -> "🔥 One week! Consistency is becoming habit."
        streak in 8..13 -> "$streak days - Keep the fire burning."
        streak == 14 -> "🔥🔥 Two weeks! Your commitment shows."
        streak in 15..29 -> "$streak days - Nearly a month of dedication."
        streak == 30 -> "🔥🔥🔥 One month! You are a Flame Keeper."
        streak in 31..59 -> "$streak days - The fire burns steady."
        streak == 60 -> "🔥🔥🔥🔥 Two months! Your consistency is remarkable."
        streak in 61..99 -> "$streak days - The century approaches."
        streak == 100 -> "💯 100 days! A true testament to your dedication."
        streak in 101..364 -> "$streak days - Ever closer to a full year."
        streak == 365 -> "🏆 One full year! You are the Eternal Flame."
        else -> "🌟 $streak days - A legend in consistency."
    }

    /**
     * Get message for streak tier.
     */
    fun getTierMessage(tier: StreakTier): String = when (tier) {
        StreakTier.SPARK -> "Your spark is lit. Keep it alive!"
        StreakTier.EMBER -> "The ember glows. Nurture it with care."
        StreakTier.FLAME -> "Your flame burns bright!"
        StreakTier.BLAZE -> "A blaze of dedication! Two weeks strong."
        StreakTier.INFERNO -> "An inferno of commitment!"
        StreakTier.PHOENIX -> "Rising like a phoenix, unstoppable."
        StreakTier.SUPERNOVA -> "A supernova of persistence!"
        StreakTier.ETERNAL -> "The eternal flame - legendary!"
    }

    /**
     * Get message for broken streak with new protection system.
     */
    fun getBrokenStreakMessage(
        previousStreak: Int,
        canUseGracePeriod: Boolean,
        freezeTokensAvailable: Int
    ): String {
        val streakPart = when {
            previousStreak >= 30 -> "Your $previousStreak-day streak took a break."
            previousStreak >= 7 -> "Your ${previousStreak}-day streak paused."
            else -> "Yesterday was a rest day."
        }

        val protectionPart = when {
            canUseGracePeriod -> " Your grace period will automatically protect your streak!"
            freezeTokensAvailable > 0 -> " You have $freezeTokensAvailable freeze token${if (freezeTokensAvailable > 1) "s" else ""} available."
            else -> " Fresh starts are beautiful too."
        }

        return streakPart + protectionPart
    }

    /**
     * Get message for broken streak - legacy version.
     */
    fun getBrokenStreakMessage(previousStreak: Int, freezesAvailable: Int): String {
        return getBrokenStreakMessage(previousStreak, false, freezesAvailable)
    }

    /**
     * Get encouraging message when grace period saves the day.
     */
    fun getGracePeriodSavedMessage(streak: Int, daysUntilReset: Int): String {
        return "Your $streak-day streak is safe! Grace period activated. " +
               "Your next grace period will be available in $daysUntilReset days."
    }

    /**
     * Get message when freeze token is used.
     */
    fun getFreezeTokenUsedMessage(streak: Int, tokensRemaining: Int): String {
        return "Freeze token activated! Your $streak-day streak is preserved. " +
               "$tokensRemaining token${if (tokensRemaining != 1) "s" else ""} remaining."
    }

    /**
     * Calculate XP bonus for maintaining streak.
     */
    fun getStreakXpBonus(streak: Int): Int = when {
        streak < 3 -> 0
        streak < 7 -> 5
        streak < 14 -> 10
        streak < 30 -> 15
        streak < 60 -> 20
        streak < 100 -> 25
        streak < 365 -> 30
        else -> 50
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
