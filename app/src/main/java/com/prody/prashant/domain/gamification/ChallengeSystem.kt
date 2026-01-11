package com.prody.prashant.domain.gamification

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

/**
 * Enhanced Challenge System
 *
 * Philosophy: Challenges should provide exciting, time-limited goals that:
 * - Encourage specific behaviors without being punishing
 * - Offer meaningful rewards (XP + skill-specific bonuses)
 * - Create community engagement through shared goals
 * - Refresh regularly to keep engagement high
 *
 * Challenge Types:
 * - DAILY: Fresh challenge every day, lower stakes, quick wins
 * - WEEKLY: Larger goals, better rewards, community milestones
 * - SPECIAL: Seasonal/event-based challenges with unique rewards
 *
 * Integration with Skills:
 * - Each challenge can target specific skills
 * - XP earned is applied to the relevant skill
 * - Some challenges offer perk-locked bonuses
 */

/**
 * Challenge duration types.
 */
enum class ChallengeDuration(
    val displayName: String,
    val baseDays: Int
) {
    DAILY("Daily", 1),
    WEEKLY("Weekly", 7),
    BIWEEKLY("Bi-Weekly", 14),
    MONTHLY("Monthly", 30),
    SPECIAL("Special Event", -1) // Variable duration
}

/**
 * Categories of challenges that map to app activities.
 */
enum class ChallengeCategory(
    val id: String,
    val displayName: String,
    val description: String,
    val primarySkill: Skill?,
    val iconName: String
) {
    JOURNALING(
        "journaling",
        "Journaling",
        "Write and reflect in your journal",
        Skill.CLARITY,
        "ic_challenge_journal"
    ),
    VOCABULARY(
        "vocabulary",
        "Vocabulary",
        "Learn and use new words",
        Skill.DISCIPLINE,
        "ic_challenge_vocab"
    ),
    FUTURE_SELF(
        "future_self",
        "Future Self",
        "Send messages to your future self",
        Skill.COURAGE,
        "ic_challenge_future"
    ),
    WISDOM(
        "wisdom",
        "Wisdom",
        "Engage with daily wisdom and quotes",
        Skill.DISCIPLINE,
        "ic_challenge_wisdom"
    ),
    BLOOM(
        "bloom",
        "Bloom",
        "Use learned vocabulary in your writing",
        Skill.DISCIPLINE,
        "ic_challenge_bloom"
    ),
    STREAK(
        "streak",
        "Consistency",
        "Maintain your daily streak",
        null,
        "ic_challenge_streak"
    ),
    MIXED(
        "mixed",
        "Mixed",
        "Complete various activities",
        null,
        "ic_challenge_mixed"
    ),
    COMMUNITY(
        "community",
        "Community",
        "Collaborative challenges with other users",
        null,
        "ic_challenge_community"
    )
}

/**
 * Action types that can count toward challenge progress.
 */
enum class ChallengeAction(
    val id: String,
    val displayName: String,
    val category: ChallengeCategory
) {
    // Journaling actions
    WRITE_ENTRY("write_entry", "Write Journal Entry", ChallengeCategory.JOURNALING),
    WRITE_LONG_ENTRY("write_long_entry", "Write Entry (500+ words)", ChallengeCategory.JOURNALING),
    WRITE_MICRO_ENTRY("write_micro_entry", "Quick Thought", ChallengeCategory.JOURNALING),

    // Vocabulary actions
    VIEW_WORD("view_word", "View Word Explanation", ChallengeCategory.VOCABULARY),
    SAVE_WORD("save_word", "Save Word to Collection", ChallengeCategory.VOCABULARY),
    REVIEW_FLASHCARD("review_flashcard", "Review Flashcard", ChallengeCategory.VOCABULARY),
    MASTER_WORD("master_word", "Master a Word", ChallengeCategory.VOCABULARY),

    // Future self actions
    SEND_FUTURE_MESSAGE("send_future_message", "Send Future Message", ChallengeCategory.FUTURE_SELF),
    SEND_LONG_FUTURE("send_long_future", "Send 30+ Day Message", ChallengeCategory.FUTURE_SELF),
    OPEN_RECEIVED_MESSAGE("open_received_message", "Open Received Message", ChallengeCategory.FUTURE_SELF),
    REPLY_TO_PAST_SELF("reply_to_past_self", "Reply to Past Self", ChallengeCategory.FUTURE_SELF),

    // Wisdom actions
    VIEW_DAILY_WISDOM("view_daily_wisdom", "View Daily Wisdom", ChallengeCategory.WISDOM),
    SAVE_WISDOM("save_wisdom", "Save Wisdom to Collection", ChallengeCategory.WISDOM),
    SHARE_WISDOM("share_wisdom", "Share Wisdom", ChallengeCategory.WISDOM),

    // Bloom actions
    BLOOM_SEED("bloom_seed", "Bloom a Seed", ChallengeCategory.BLOOM),
    USE_VOCAB_IN_ENTRY("use_vocab_in_entry", "Use Vocab Word in Entry", ChallengeCategory.BLOOM),

    // Streak actions
    MAINTAIN_STREAK("maintain_streak", "Maintain Streak", ChallengeCategory.STREAK),

    // Mixed actions
    ANY_ACTIVITY("any_activity", "Any Activity", ChallengeCategory.MIXED)
}

/**
 * A daily challenge definition.
 */
data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val category: ChallengeCategory,
    val action: ChallengeAction,
    val targetCount: Int,
    val xpReward: Int,
    val bonusXp: Int = 0, // Extra XP for completing quickly or perfectly
    val skillBonus: Skill? = null, // Optional skill that gets bonus XP
    val streakBonus: Boolean = false, // Extra bonus if user has active streak
    val iconName: String = category.iconName,
    val encouragementMessage: String = "You've got this!"
) {
    /**
     * Calculate total XP reward including bonuses.
     */
    fun calculateTotalXp(hasStreak: Boolean, streakDays: Int): Int {
        var total = xpReward
        if (bonusXp > 0) total += bonusXp
        if (streakBonus && hasStreak) {
            // 10% bonus for streak, up to 50% for 30+ day streak
            val streakMultiplier = (streakDays / 30f).coerceAtMost(0.5f) + 0.1f
            total += (xpReward * streakMultiplier).toInt()
        }
        return total
    }
}

/**
 * User's progress on a daily challenge.
 */
data class DailyChallengeProgress(
    val challengeId: String,
    val date: LocalDate,
    val currentProgress: Int,
    val targetCount: Int,
    val isCompleted: Boolean,
    val completedAt: LocalDateTime?,
    val xpEarned: Int
) {
    val progressPercent: Float
        get() = if (targetCount > 0) (currentProgress.toFloat() / targetCount).coerceIn(0f, 1f) else 0f

    val remainingCount: Int
        get() = (targetCount - currentProgress).coerceAtLeast(0)
}

/**
 * A weekly challenge with milestones.
 */
data class WeeklyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val category: ChallengeCategory,
    val actions: List<ChallengeAction>, // Multiple actions can count
    val targetCount: Int,
    val xpReward: Int,
    val milestones: List<WeeklyMilestone>,
    val skillBonus: Skill? = null,
    val rewardBadgeId: String? = null,
    val weekNumber: Int, // Week of year
    val year: Int,
    val communityTarget: Int = 0, // Optional community goal
    val isFeatured: Boolean = false
)

/**
 * Milestone within a weekly challenge.
 */
data class WeeklyMilestone(
    val id: String,
    val title: String,
    val description: String,
    val targetPercent: Int, // 25, 50, 75, 100
    val xpBonus: Int,
    val celebrationMessage: String
)

/**
 * User's progress on a weekly challenge.
 */
data class WeeklyChallengeProgress(
    val challengeId: String,
    val weekNumber: Int,
    val year: Int,
    val currentProgress: Int,
    val targetCount: Int,
    val isCompleted: Boolean,
    val completedAt: LocalDateTime?,
    val xpEarned: Int,
    val milestonesReached: List<String>, // IDs of reached milestones
    val communityProgress: Int = 0
) {
    val progressPercent: Float
        get() = if (targetCount > 0) (currentProgress.toFloat() / targetCount).coerceIn(0f, 1f) else 0f

    val currentMilestone: Int
        get() = when {
            progressPercent >= 1f -> 4
            progressPercent >= 0.75f -> 3
            progressPercent >= 0.50f -> 2
            progressPercent >= 0.25f -> 1
            else -> 0
        }
}

/**
 * Result of completing a challenge action.
 */
sealed class ChallengeActionResult {
    /**
     * Progress was made toward a challenge.
     */
    data class Progress(
        val challengeId: String,
        val previousProgress: Int,
        val newProgress: Int,
        val targetCount: Int,
        val isNowComplete: Boolean
    ) : ChallengeActionResult()

    /**
     * Challenge was completed with this action.
     */
    data class Completed(
        val challengeId: String,
        val xpEarned: Int,
        val skillXp: Map<Skill, Int>,
        val milestoneReached: String?,
        val celebrationMessage: String
    ) : ChallengeActionResult()

    /**
     * Milestone was reached.
     */
    data class MilestoneReached(
        val challengeId: String,
        val milestoneId: String,
        val milestoneTitle: String,
        val xpBonus: Int,
        val celebrationMessage: String
    ) : ChallengeActionResult()

    /**
     * Challenge was already completed.
     */
    data class AlreadyCompleted(
        val challengeId: String
    ) : ChallengeActionResult()

    /**
     * No active challenge for this action.
     */
    object NoActiveChallenge : ChallengeActionResult()
}

/**
 * Predefined daily challenges that rotate.
 */
object DailyChallenges {

    // =========================================================================
    // JOURNALING CHALLENGES
    // =========================================================================

    private val journalingChallenges = listOf(
        DailyChallenge(
            id = "daily_journal_1",
            title = "Daily Reflection",
            description = "Write one journal entry today",
            category = ChallengeCategory.JOURNALING,
            action = ChallengeAction.WRITE_ENTRY,
            targetCount = 1,
            xpReward = 20,
            skillBonus = Skill.CLARITY,
            streakBonus = true,
            encouragementMessage = "Take a moment to reflect on your day."
        ),
        DailyChallenge(
            id = "daily_journal_deep",
            title = "Deep Dive",
            description = "Write a detailed entry (500+ words)",
            category = ChallengeCategory.JOURNALING,
            action = ChallengeAction.WRITE_LONG_ENTRY,
            targetCount = 1,
            xpReward = 40,
            bonusXp = 10,
            skillBonus = Skill.CLARITY,
            encouragementMessage = "Go deeper. Explore your thoughts fully."
        ),
        DailyChallenge(
            id = "daily_micro_3",
            title = "Thought Catcher",
            description = "Capture 3 quick thoughts today",
            category = ChallengeCategory.JOURNALING,
            action = ChallengeAction.WRITE_MICRO_ENTRY,
            targetCount = 3,
            xpReward = 15,
            skillBonus = Skill.CLARITY,
            encouragementMessage = "Quick thoughts can lead to big insights."
        )
    )

    // =========================================================================
    // VOCABULARY CHALLENGES
    // =========================================================================

    private val vocabularyChallenges = listOf(
        DailyChallenge(
            id = "daily_vocab_5",
            title = "Word Explorer",
            description = "View 5 word explanations",
            category = ChallengeCategory.VOCABULARY,
            action = ChallengeAction.VIEW_WORD,
            targetCount = 5,
            xpReward = 15,
            skillBonus = Skill.DISCIPLINE,
            encouragementMessage = "Expand your vocabulary one word at a time."
        ),
        DailyChallenge(
            id = "daily_flashcard_10",
            title = "Memory Master",
            description = "Review 10 flashcards",
            category = ChallengeCategory.VOCABULARY,
            action = ChallengeAction.REVIEW_FLASHCARD,
            targetCount = 10,
            xpReward = 25,
            skillBonus = Skill.DISCIPLINE,
            streakBonus = true,
            encouragementMessage = "Repetition builds mastery."
        ),
        DailyChallenge(
            id = "daily_save_3",
            title = "Curator's Eye",
            description = "Save 3 words to your collection",
            category = ChallengeCategory.VOCABULARY,
            action = ChallengeAction.SAVE_WORD,
            targetCount = 3,
            xpReward = 20,
            skillBonus = Skill.DISCIPLINE,
            encouragementMessage = "Build your personal word treasury."
        )
    )

    // =========================================================================
    // FUTURE SELF CHALLENGES
    // =========================================================================

    private val futureSelfChallenges = listOf(
        DailyChallenge(
            id = "daily_future_1",
            title = "Time Capsule",
            description = "Send a message to your future self",
            category = ChallengeCategory.FUTURE_SELF,
            action = ChallengeAction.SEND_FUTURE_MESSAGE,
            targetCount = 1,
            xpReward = 25,
            skillBonus = Skill.COURAGE,
            encouragementMessage = "What will future you need to hear?"
        ),
        DailyChallenge(
            id = "daily_future_long",
            title = "Bold Promise",
            description = "Send a message 30+ days into the future",
            category = ChallengeCategory.FUTURE_SELF,
            action = ChallengeAction.SEND_LONG_FUTURE,
            targetCount = 1,
            xpReward = 40,
            bonusXp = 15,
            skillBonus = Skill.COURAGE,
            encouragementMessage = "The bravest messages travel the farthest."
        ),
        DailyChallenge(
            id = "daily_reply_past",
            title = "Dialogue Across Time",
            description = "Reply to a message from your past self",
            category = ChallengeCategory.FUTURE_SELF,
            action = ChallengeAction.REPLY_TO_PAST_SELF,
            targetCount = 1,
            xpReward = 30,
            skillBonus = Skill.COURAGE,
            encouragementMessage = "Connect with who you were."
        )
    )

    // =========================================================================
    // WISDOM CHALLENGES
    // =========================================================================

    private val wisdomChallenges = listOf(
        DailyChallenge(
            id = "daily_wisdom_view",
            title = "Wisdom Seeker",
            description = "View today's daily wisdom",
            category = ChallengeCategory.WISDOM,
            action = ChallengeAction.VIEW_DAILY_WISDOM,
            targetCount = 1,
            xpReward = 10,
            skillBonus = Skill.DISCIPLINE,
            encouragementMessage = "A moment of wisdom each day."
        ),
        DailyChallenge(
            id = "daily_wisdom_save",
            title = "Wisdom Keeper",
            description = "Save a piece of wisdom to your collection",
            category = ChallengeCategory.WISDOM,
            action = ChallengeAction.SAVE_WISDOM,
            targetCount = 1,
            xpReward = 15,
            skillBonus = Skill.DISCIPLINE,
            encouragementMessage = "Collect wisdom like precious gems."
        )
    )

    // =========================================================================
    // BLOOM CHALLENGES
    // =========================================================================

    private val bloomChallenges = listOf(
        DailyChallenge(
            id = "daily_bloom_1",
            title = "Garden Tender",
            description = "Bloom a seed today",
            category = ChallengeCategory.BLOOM,
            action = ChallengeAction.BLOOM_SEED,
            targetCount = 1,
            xpReward = 30,
            skillBonus = Skill.DISCIPLINE,
            streakBonus = true,
            encouragementMessage = "Turn knowledge into practice."
        ),
        DailyChallenge(
            id = "daily_use_vocab",
            title = "Word Weaver",
            description = "Use a vocabulary word in your entry",
            category = ChallengeCategory.BLOOM,
            action = ChallengeAction.USE_VOCAB_IN_ENTRY,
            targetCount = 1,
            xpReward = 25,
            skillBonus = Skill.DISCIPLINE,
            encouragementMessage = "Weave your new words into expression."
        )
    )

    // =========================================================================
    // MIXED CHALLENGES
    // =========================================================================

    private val mixedChallenges = listOf(
        DailyChallenge(
            id = "daily_active_3",
            title = "Triple Threat",
            description = "Complete 3 different activities",
            category = ChallengeCategory.MIXED,
            action = ChallengeAction.ANY_ACTIVITY,
            targetCount = 3,
            xpReward = 25,
            streakBonus = true,
            encouragementMessage = "Variety strengthens growth."
        )
    )

    /**
     * All daily challenge templates.
     */
    val allChallenges: List<DailyChallenge> = listOf(
        journalingChallenges,
        vocabularyChallenges,
        futureSelfChallenges,
        wisdomChallenges,
        bloomChallenges,
        mixedChallenges
    ).flatten()

    /**
     * Get challenges by category.
     */
    fun getByCategory(category: ChallengeCategory): List<DailyChallenge> =
        allChallenges.filter { it.category == category }

    /**
     * Get a deterministic daily challenge based on date.
     * Uses the date as a seed to ensure same challenge for all users on same day.
     */
    fun getDailyChallengeForDate(date: LocalDate): DailyChallenge {
        val dayOfYear = date.dayOfYear
        val index = dayOfYear % allChallenges.size
        return allChallenges[index]
    }

    /**
     * Get secondary daily challenge (different from primary).
     */
    fun getSecondaryChallengeForDate(date: LocalDate): DailyChallenge {
        val dayOfYear = date.dayOfYear
        val primaryIndex = dayOfYear % allChallenges.size
        val secondaryIndex = (dayOfYear + allChallenges.size / 2) % allChallenges.size

        // Ensure different challenge
        val actualIndex = if (secondaryIndex == primaryIndex) {
            (secondaryIndex + 1) % allChallenges.size
        } else {
            secondaryIndex
        }
        return allChallenges[actualIndex]
    }

    /**
     * Get bonus challenge for users with active streaks.
     */
    fun getBonusChallengeForDate(date: LocalDate, hasStreak: Boolean): DailyChallenge? {
        if (!hasStreak) return null
        val dayOfYear = date.dayOfYear
        val index = (dayOfYear * 7) % allChallenges.size // Different algorithm for variety
        return allChallenges[index].copy(
            id = allChallenges[index].id + "_bonus",
            title = "Bonus: ${allChallenges[index].title}",
            xpReward = (allChallenges[index].xpReward * 1.5).toInt()
        )
    }
}

/**
 * Predefined weekly challenges.
 */
object WeeklyChallenges {

    /**
     * Generate the standard milestones for a weekly challenge.
     */
    private fun standardMilestones(challengeId: String): List<WeeklyMilestone> = listOf(
        WeeklyMilestone(
            id = "${challengeId}_m25",
            title = "Getting Started",
            description = "25% complete",
            targetPercent = 25,
            xpBonus = 10,
            celebrationMessage = "Great start! Keep going!"
        ),
        WeeklyMilestone(
            id = "${challengeId}_m50",
            title = "Halfway There",
            description = "50% complete",
            targetPercent = 50,
            xpBonus = 25,
            celebrationMessage = "You're halfway! The momentum is building!"
        ),
        WeeklyMilestone(
            id = "${challengeId}_m75",
            title = "Almost Done",
            description = "75% complete",
            targetPercent = 75,
            xpBonus = 35,
            celebrationMessage = "So close! Push through!"
        ),
        WeeklyMilestone(
            id = "${challengeId}_m100",
            title = "Challenge Complete!",
            description = "100% complete",
            targetPercent = 100,
            xpBonus = 50,
            celebrationMessage = "You did it! Weekly challenge conquered!"
        )
    )

    /**
     * Generate weekly challenge for journaling.
     */
    fun journalingWeeklyChallenge(weekNumber: Int, year: Int): WeeklyChallenge {
        val id = "weekly_journal_${year}_w$weekNumber"
        return WeeklyChallenge(
            id = id,
            title = "Week of Reflection",
            description = "Write 7 journal entries this week - one each day.",
            category = ChallengeCategory.JOURNALING,
            actions = listOf(ChallengeAction.WRITE_ENTRY),
            targetCount = 7,
            xpReward = 150,
            milestones = standardMilestones(id),
            skillBonus = Skill.CLARITY,
            weekNumber = weekNumber,
            year = year,
            communityTarget = 5000,
            isFeatured = true
        )
    }

    /**
     * Generate weekly challenge for vocabulary.
     */
    fun vocabularyWeeklyChallenge(weekNumber: Int, year: Int): WeeklyChallenge {
        val id = "weekly_vocab_${year}_w$weekNumber"
        return WeeklyChallenge(
            id = id,
            title = "Word Collector",
            description = "Learn and review 50 vocabulary items this week.",
            category = ChallengeCategory.VOCABULARY,
            actions = listOf(
                ChallengeAction.VIEW_WORD,
                ChallengeAction.SAVE_WORD,
                ChallengeAction.REVIEW_FLASHCARD
            ),
            targetCount = 50,
            xpReward = 200,
            milestones = standardMilestones(id),
            skillBonus = Skill.DISCIPLINE,
            weekNumber = weekNumber,
            year = year,
            communityTarget = 10000
        )
    }

    /**
     * Generate weekly challenge for future self.
     */
    fun futureSelfWeeklyChallenge(weekNumber: Int, year: Int): WeeklyChallenge {
        val id = "weekly_future_${year}_w$weekNumber"
        return WeeklyChallenge(
            id = id,
            title = "Time Traveler",
            description = "Send 3 messages to your future self this week.",
            category = ChallengeCategory.FUTURE_SELF,
            actions = listOf(
                ChallengeAction.SEND_FUTURE_MESSAGE,
                ChallengeAction.SEND_LONG_FUTURE
            ),
            targetCount = 3,
            xpReward = 100,
            milestones = standardMilestones(id),
            skillBonus = Skill.COURAGE,
            weekNumber = weekNumber,
            year = year,
            communityTarget = 2000
        )
    }

    /**
     * Generate weekly challenge for blooming.
     */
    fun bloomWeeklyChallenge(weekNumber: Int, year: Int): WeeklyChallenge {
        val id = "weekly_bloom_${year}_w$weekNumber"
        return WeeklyChallenge(
            id = id,
            title = "Garden of Knowledge",
            description = "Bloom 5 seeds this week to cement your learning.",
            category = ChallengeCategory.BLOOM,
            actions = listOf(
                ChallengeAction.BLOOM_SEED,
                ChallengeAction.USE_VOCAB_IN_ENTRY
            ),
            targetCount = 5,
            xpReward = 175,
            milestones = standardMilestones(id),
            skillBonus = Skill.DISCIPLINE,
            weekNumber = weekNumber,
            year = year,
            communityTarget = 3000
        )
    }

    /**
     * Get all weekly challenges for a given week.
     */
    fun getChallengesForWeek(weekNumber: Int, year: Int): List<WeeklyChallenge> = listOf(
        journalingWeeklyChallenge(weekNumber, year),
        vocabularyWeeklyChallenge(weekNumber, year),
        futureSelfWeeklyChallenge(weekNumber, year),
        bloomWeeklyChallenge(weekNumber, year)
    )

    /**
     * Get the featured weekly challenge.
     */
    fun getFeaturedChallenge(weekNumber: Int, year: Int): WeeklyChallenge {
        // Rotate featured challenge each week
        val challenges = getChallengesForWeek(weekNumber, year)
        return challenges[weekNumber % challenges.size].copy(isFeatured = true)
    }
}

/**
 * Helper functions for challenge system.
 */
object ChallengeCalculator {

    /**
     * Get current week number.
     */
    fun getCurrentWeekNumber(): Int {
        val today = LocalDate.now()
        return today.get(java.time.temporal.WeekFields.ISO.weekOfYear())
    }

    /**
     * Get start of current week.
     */
    fun getWeekStart(date: LocalDate = LocalDate.now()): LocalDate {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    /**
     * Get end of current week.
     */
    fun getWeekEnd(date: LocalDate = LocalDate.now()): LocalDate {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    }

    /**
     * Calculate time remaining in current day.
     */
    fun hoursRemainingToday(): Int {
        val now = LocalDateTime.now()
        val endOfDay = now.toLocalDate().atTime(23, 59, 59)
        return ChronoUnit.HOURS.between(now, endOfDay).toInt()
    }

    /**
     * Calculate days remaining in current week.
     */
    fun daysRemainingInWeek(): Int {
        val today = LocalDate.now()
        val endOfWeek = getWeekEnd(today)
        return ChronoUnit.DAYS.between(today, endOfWeek).toInt()
    }

    /**
     * Check if an action counts toward a challenge.
     */
    fun actionCountsForChallenge(
        action: ChallengeAction,
        challenge: DailyChallenge
    ): Boolean {
        return challenge.action == action ||
               (challenge.action == ChallengeAction.ANY_ACTIVITY)
    }

    /**
     * Check if an action counts toward a weekly challenge.
     */
    fun actionCountsForWeeklyChallenge(
        action: ChallengeAction,
        challenge: WeeklyChallenge
    ): Boolean {
        return action in challenge.actions ||
               challenge.actions.contains(ChallengeAction.ANY_ACTIVITY)
    }

    /**
     * Calculate XP earned from challenge completion.
     */
    fun calculateChallengeXp(
        challenge: DailyChallenge,
        hasStreak: Boolean,
        streakDays: Int,
        clarityLevel: Int,
        disciplineLevel: Int,
        courageLevel: Int
    ): Map<Skill, Int> {
        val baseXp = challenge.calculateTotalXp(hasStreak, streakDays)
        val result = mutableMapOf<Skill, Int>()

        // Primary skill gets the XP
        val primarySkill = challenge.skillBonus ?: challenge.category.primarySkill
        if (primarySkill != null) {
            result[primarySkill] = baseXp
        }

        return result
    }

    /**
     * Get motivational message based on progress.
     */
    fun getProgressMessage(progressPercent: Float): String = when {
        progressPercent == 0f -> "Ready to start? Let's go!"
        progressPercent < 0.25f -> "Off to a good start!"
        progressPercent < 0.50f -> "Making progress! Keep it up!"
        progressPercent < 0.75f -> "Halfway there! You've got this!"
        progressPercent < 1f -> "So close! Push through!"
        else -> "Complete! Well done!"
    }

    /**
     * Get celebration message for challenge completion.
     */
    fun getCompletionMessage(challenge: DailyChallenge): String {
        return when (challenge.category) {
            ChallengeCategory.JOURNALING -> "Your thoughts are captured! Reflection complete."
            ChallengeCategory.VOCABULARY -> "Words mastered! Your vocabulary grows."
            ChallengeCategory.FUTURE_SELF -> "Message sent across time! Brave choice."
            ChallengeCategory.WISDOM -> "Wisdom collected! May it guide you well."
            ChallengeCategory.BLOOM -> "Knowledge bloomed! Learning becomes action."
            ChallengeCategory.STREAK -> "Consistency maintained! The fire burns bright."
            ChallengeCategory.MIXED -> "Variety achieved! A well-rounded day."
            ChallengeCategory.COMMUNITY -> "Community contribution made! Together we grow."
        }
    }
}

/**
 * Summary of user's challenge activity.
 */
data class ChallengeSummary(
    val dailyChallengesCompleted: Int,
    val weeklyChallengesCompleted: Int,
    val totalChallengesCompleted: Int,
    val currentDailyStreak: Int, // Days in a row completing daily challenge
    val longestDailyStreak: Int,
    val weeklyCompletionRate: Float, // % of weeks with completed challenge
    val favoriteCategory: ChallengeCategory?,
    val totalXpFromChallenges: Int
)
