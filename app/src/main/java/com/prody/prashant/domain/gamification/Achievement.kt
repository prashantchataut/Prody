package com.prody.prashant.domain.gamification

import androidx.compose.ui.graphics.Color

/**
 * Enhanced Achievement System
 *
 * Achievements are meaningful milestones that celebrate progress and unlock
 * cosmetic rewards (banners, titles, avatars). Each achievement has:
 *
 * - Category: Groups achievements by theme
 * - Rarity: Determines XP reward and visual prominence
 * - Requirement: Specific condition to unlock
 * - Rewards: Optional cosmetics unlocked on completion
 *
 * Philosophy:
 * - Achievements should feel earned, not given
 * - Each achievement tells a story of progress
 * - Rare achievements should be memorable moments
 * - Legendary/Mythic achievements require true dedication
 */

/**
 * Achievement categories for meaningful milestones.
 */
enum class AchievementCategory(
    val id: String,
    val displayName: String,
    val description: String,
    val iconName: String
) {
    REFLECTION(
        "reflection",
        "Reflection",
        "Journaling milestones",
        "ic_category_reflection"
    ),
    WISDOM(
        "wisdom",
        "Wisdom",
        "Learning and vocabulary mastery",
        "ic_category_wisdom"
    ),
    TIME(
        "time",
        "Time",
        "Temporal milestones and streaks",
        "ic_category_time"
    ),
    MASTERY(
        "mastery",
        "Mastery",
        "Skill level achievements",
        "ic_category_mastery"
    ),
    JOURNEY(
        "journey",
        "Journey",
        "Overall progress milestones",
        "ic_category_journey"
    ),
    FUTURE(
        "future",
        "Future Self",
        "Time capsule achievements",
        "ic_category_future"
    ),
    SOCIAL(
        "social",
        "Community",
        "Social and collaborative achievements",
        "ic_category_social"
    ),
    SPECIAL(
        "special",
        "Special",
        "Rare and hidden achievements",
        "ic_category_special"
    )
}

/**
 * Achievement rarity levels with associated colors.
 */
enum class AchievementRarity(
    val id: String,
    val displayName: String,
    val color: Color,
    val glowColor: Color,
    val sortOrder: Int,
    val xpMultiplier: Float
) {
    COMMON(
        "common",
        "Common",
        Color(0xFF78909C),
        Color(0x3378909C),
        1,
        1.0f
    ),
    UNCOMMON(
        "uncommon",
        "Uncommon",
        Color(0xFF66BB6A),
        Color(0x3366BB6A),
        2,
        1.5f
    ),
    RARE(
        "rare",
        "Rare",
        Color(0xFF42A5F5),
        Color(0x3342A5F5),
        3,
        2.0f
    ),
    EPIC(
        "epic",
        "Epic",
        Color(0xFFAB47BC),
        Color(0x33AB47BC),
        4,
        3.0f
    ),
    LEGENDARY(
        "legendary",
        "Legendary",
        Color(0xFFD4AF37),
        Color(0x33D4AF37),
        5,
        5.0f
    ),
    MYTHIC(
        "mythic",
        "Mythic",
        Color(0xFFFFD700),
        Color(0x66FFD700),
        6,
        10.0f
    )
}

/**
 * Types of requirements for achievements.
 */
sealed class AchievementRequirement {
    /** Count-based requirement (e.g., write 10 entries) */
    data class Count(val target: Int) : AchievementRequirement()

    /** Streak-based requirement (e.g., 7 days in a row) */
    data class Streak(val days: Int) : AchievementRequirement()

    /** Level-based requirement (e.g., reach level 10 in Clarity) */
    data class SkillLevel(val skill: Skill, val level: Int) : AchievementRequirement()

    /** Combined skill level requirement */
    data class CombinedLevel(val totalLevel: Int) : AchievementRequirement()

    /** All skills at minimum level requirement */
    data class AllSkillsMinLevel(val minLevel: Int) : AchievementRequirement()

    /** Time-based requirement (e.g., use app for 7 days) */
    data class DaysOnApp(val days: Int) : AchievementRequirement()

    /** Single action requirement (e.g., write first entry) */
    data class SingleAction(val actionType: String) : AchievementRequirement()

    /** Time of day requirement (e.g., before 7am) */
    data class TimeOfDay(val beforeHour: Int?, val afterHour: Int?, val count: Int) : AchievementRequirement()

    /** Word count requirement */
    data class WordCount(val minWords: Int) : AchievementRequirement()

    /** Total words written requirement */
    data class TotalWords(val words: Int) : AchievementRequirement()

    /** Bloom streak requirement */
    data class BloomStreak(val days: Int) : AchievementRequirement()

    /** Bloom count requirement */
    data class BloomCount(val count: Int) : AchievementRequirement()

    /** Future message requirement (e.g., send 5 messages) */
    data class FutureMessages(val count: Int) : AchievementRequirement()

    /** Future message with minimum delay */
    data class FutureMessageDelay(val minDays: Int, val count: Int) : AchievementRequirement()

    /** Flashcard review requirement */
    data class FlashcardReviews(val count: Int) : AchievementRequirement()

    /** Vocabulary mastered requirement */
    data class VocabularyMastered(val count: Int) : AchievementRequirement()

    /** Challenge completion requirement */
    data class ChallengesCompleted(val count: Int) : AchievementRequirement()

    /** Weekly challenge streak */
    data class WeeklyChallengeStreak(val weeks: Int) : AchievementRequirement()

    /** Leaderboard position */
    data class LeaderboardPosition(val maxPosition: Int) : AchievementRequirement()

    /** Freeze token usage */
    data class FreezeTokensEarned(val count: Int) : AchievementRequirement()

    /** Streak recovery (times streak was saved) */
    data class StreakRecoveries(val count: Int) : AchievementRequirement()

    /** XP milestone */
    data class TotalXp(val xp: Int) : AchievementRequirement()

    /** Perfect week (all 7 days with activity) */
    data class PerfectWeeks(val count: Int) : AchievementRequirement()

    /** Perfect month (all days in a month with activity) */
    data class PerfectMonths(val count: Int) : AchievementRequirement()
}

/**
 * Achievement definition with all properties.
 */
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val category: AchievementCategory,
    val rarity: AchievementRarity,
    val iconName: String,
    val requirement: AchievementRequirement,
    val rewardBannerId: String? = null,
    val rewardAvatarId: String? = null,
    val rewardTitleId: String? = null,
    val rewardFrameId: String? = null,
    val xpReward: Int = calculateBaseXpReward(rarity),
    val celebrationMessage: String = "",
    val isHidden: Boolean = false, // Hidden achievements not shown until unlocked
    val hint: String? = null // Hint shown for hidden achievements
) {
    /**
     * Check if requirement is met.
     */
    fun isRequirementMet(currentValue: Int): Boolean {
        return when (requirement) {
            is AchievementRequirement.Count -> currentValue >= requirement.target
            is AchievementRequirement.Streak -> currentValue >= requirement.days
            is AchievementRequirement.DaysOnApp -> currentValue >= requirement.days
            is AchievementRequirement.BloomStreak -> currentValue >= requirement.days
            is AchievementRequirement.BloomCount -> currentValue >= requirement.count
            is AchievementRequirement.WordCount -> currentValue >= requirement.minWords
            is AchievementRequirement.TotalWords -> currentValue >= requirement.words
            is AchievementRequirement.TimeOfDay -> currentValue >= requirement.count
            is AchievementRequirement.FutureMessages -> currentValue >= requirement.count
            is AchievementRequirement.FutureMessageDelay -> currentValue >= requirement.count
            is AchievementRequirement.FlashcardReviews -> currentValue >= requirement.count
            is AchievementRequirement.VocabularyMastered -> currentValue >= requirement.count
            is AchievementRequirement.ChallengesCompleted -> currentValue >= requirement.count
            is AchievementRequirement.WeeklyChallengeStreak -> currentValue >= requirement.weeks
            is AchievementRequirement.LeaderboardPosition -> currentValue <= requirement.maxPosition && currentValue > 0
            is AchievementRequirement.FreezeTokensEarned -> currentValue >= requirement.count
            is AchievementRequirement.StreakRecoveries -> currentValue >= requirement.count
            is AchievementRequirement.TotalXp -> currentValue >= requirement.xp
            is AchievementRequirement.PerfectWeeks -> currentValue >= requirement.count
            is AchievementRequirement.PerfectMonths -> currentValue >= requirement.count
            is AchievementRequirement.SkillLevel,
            is AchievementRequirement.CombinedLevel,
            is AchievementRequirement.AllSkillsMinLevel,
            is AchievementRequirement.SingleAction -> false // Handled separately with context
        }
    }

    /**
     * Calculate progress percentage (0.0 to 1.0).
     */
    fun calculateProgress(currentValue: Int): Float {
        val target = getRequirementTarget()
        if (target <= 0) return 0f
        // For leaderboard positions, lower is better
        if (requirement is AchievementRequirement.LeaderboardPosition) {
            return if (currentValue <= requirement.maxPosition && currentValue > 0) 1f else 0f
        }
        return (currentValue.toFloat() / target).coerceIn(0f, 1f)
    }

    /**
     * Get the target value for this achievement's requirement.
     */
    fun getRequirementTarget(): Int {
        return when (requirement) {
            is AchievementRequirement.Count -> requirement.target
            is AchievementRequirement.Streak -> requirement.days
            is AchievementRequirement.SkillLevel -> requirement.level
            is AchievementRequirement.CombinedLevel -> requirement.totalLevel
            is AchievementRequirement.AllSkillsMinLevel -> requirement.minLevel * 3
            is AchievementRequirement.DaysOnApp -> requirement.days
            is AchievementRequirement.BloomStreak -> requirement.days
            is AchievementRequirement.BloomCount -> requirement.count
            is AchievementRequirement.WordCount -> requirement.minWords
            is AchievementRequirement.TotalWords -> requirement.words
            is AchievementRequirement.TimeOfDay -> requirement.count
            is AchievementRequirement.FutureMessages -> requirement.count
            is AchievementRequirement.FutureMessageDelay -> requirement.count
            is AchievementRequirement.FlashcardReviews -> requirement.count
            is AchievementRequirement.VocabularyMastered -> requirement.count
            is AchievementRequirement.ChallengesCompleted -> requirement.count
            is AchievementRequirement.WeeklyChallengeStreak -> requirement.weeks
            is AchievementRequirement.LeaderboardPosition -> requirement.maxPosition
            is AchievementRequirement.FreezeTokensEarned -> requirement.count
            is AchievementRequirement.StreakRecoveries -> requirement.count
            is AchievementRequirement.TotalXp -> requirement.xp
            is AchievementRequirement.PerfectWeeks -> requirement.count
            is AchievementRequirement.PerfectMonths -> requirement.count
            is AchievementRequirement.SingleAction -> 1
        }
    }

    companion object {
        /**
         * Calculate base XP reward based on rarity.
         */
        fun calculateBaseXpReward(rarity: AchievementRarity): Int = when (rarity) {
            AchievementRarity.COMMON -> 25
            AchievementRarity.UNCOMMON -> 50
            AchievementRarity.RARE -> 100
            AchievementRarity.EPIC -> 200
            AchievementRarity.LEGENDARY -> 500
            AchievementRarity.MYTHIC -> 1000
        }
    }
}

/**
 * User's progress on an achievement.
 */
data class UserAchievement(
    val achievementId: String,
    val isUnlocked: Boolean,
    val earnedAt: Long?,
    val progress: Float,
    val currentValue: Int
)

/**
 * Combined achievement with progress for UI display.
 */
data class AchievementWithProgress(
    val achievement: Achievement,
    val isUnlocked: Boolean,
    val earnedAt: Long?,
    val progress: Float,
    val currentValue: Int
) {
    val progressPercent: Int get() = (progress * 100).toInt()
    val isComplete: Boolean get() = isUnlocked || progress >= 1f
}

/**
 * All achievements defined for the app.
 *
 * Organized by category with progressive difficulty.
 * Each category has achievements from Common to Mythic rarity.
 */
object Achievements {

    // =========================================================================
    // REFLECTION CATEGORY (Journaling milestones)
    // =========================================================================

    private val reflectionAchievements = listOf(
        Achievement(
            id = "first_words",
            name = "First Words",
            description = "Write your first journal entry",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.COMMON,
            iconName = "ic_achievement_first_words",
            requirement = AchievementRequirement.Count(1),
            celebrationMessage = "Your journey of reflection begins."
        ),
        Achievement(
            id = "finding_voice",
            name = "Finding Voice",
            description = "Write 10 journal entries",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.COMMON,
            iconName = "ic_achievement_finding_voice",
            requirement = AchievementRequirement.Count(10),
            celebrationMessage = "Your voice grows clearer with each entry."
        ),
        Achievement(
            id = "regular_reflector",
            name = "Regular Reflector",
            description = "Write 50 journal entries",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.UNCOMMON,
            iconName = "ic_achievement_regular_reflector",
            requirement = AchievementRequirement.Count(50),
            celebrationMessage = "Reflection has become a natural part of your day."
        ),
        Achievement(
            id = "dedicated_writer",
            name = "Dedicated Writer",
            description = "Write 100 journal entries",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_dedicated_writer",
            requirement = AchievementRequirement.Count(100),
            rewardBannerId = "inner_sanctum",
            celebrationMessage = "A hundred chapters of your inner life."
        ),
        Achievement(
            id = "journaling_master",
            name = "Journaling Master",
            description = "Write 365 journal entries",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_journaling_master",
            requirement = AchievementRequirement.Count(365),
            rewardTitleId = "memoirist",
            rewardBannerId = "year_of_pages",
            celebrationMessage = "A year of pages, each one a step on your path."
        ),
        Achievement(
            id = "prolific_writer",
            name = "Prolific Writer",
            description = "Write 1000 journal entries",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.LEGENDARY,
            iconName = "ic_achievement_prolific",
            requirement = AchievementRequirement.Count(1000),
            rewardTitleId = "master_scribe",
            rewardBannerId = "infinite_pages",
            celebrationMessage = "A thousand reflections. A thousand steps forward."
        ),
        Achievement(
            id = "deep_diver",
            name = "Deep Diver",
            description = "Write an entry over 500 words",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.UNCOMMON,
            iconName = "ic_achievement_deep_diver",
            requirement = AchievementRequirement.WordCount(500),
            celebrationMessage = "You've gone deep into the waters of thought."
        ),
        Achievement(
            id = "stream_of_consciousness",
            name = "Stream of Consciousness",
            description = "Write 1000+ words in a single entry",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_stream",
            requirement = AchievementRequirement.WordCount(1000),
            celebrationMessage = "Words flowed like a river from your mind."
        ),
        Achievement(
            id = "novelist",
            name = "Novelist",
            description = "Write 50,000 total words",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_novelist",
            requirement = AchievementRequirement.TotalWords(50000),
            rewardBannerId = "writers_sanctuary",
            celebrationMessage = "You've written a novel's worth of reflection."
        ),
        Achievement(
            id = "epic_saga",
            name = "Epic Saga",
            description = "Write 100,000 total words",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.LEGENDARY,
            iconName = "ic_achievement_epic_saga",
            requirement = AchievementRequirement.TotalWords(100000),
            rewardTitleId = "epic_writer",
            rewardBannerId = "library_of_self",
            celebrationMessage = "Your words could fill a library."
        ),
        Achievement(
            id = "perfect_week",
            name = "Perfect Week",
            description = "Journal every day for a week",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.UNCOMMON,
            iconName = "ic_achievement_perfect_week",
            requirement = AchievementRequirement.PerfectWeeks(1),
            celebrationMessage = "Seven days of unbroken reflection."
        ),
        Achievement(
            id = "perfect_month",
            name = "Perfect Month",
            description = "Journal every day for a month",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_perfect_month",
            requirement = AchievementRequirement.PerfectMonths(1),
            rewardBannerId = "monthly_devotion",
            celebrationMessage = "A perfect moon cycle of dedication."
        )
    )

    // =========================================================================
    // WISDOM CATEGORY (Learning milestones)
    // =========================================================================

    private val wisdomAchievements = listOf(
        Achievement(
            id = "curious_mind",
            name = "Curious Mind",
            description = "View 10 word explanations",
            category = AchievementCategory.WISDOM,
            rarity = AchievementRarity.COMMON,
            iconName = "ic_achievement_curious",
            requirement = AchievementRequirement.Count(10),
            celebrationMessage = "Curiosity is the root of wisdom."
        ),
        Achievement(
            id = "bloom_beginner",
            name = "Bloom Beginner",
            description = "Bloom your first Seed",
            category = AchievementCategory.WISDOM,
            rarity = AchievementRarity.COMMON,
            iconName = "ic_achievement_bloom_beginner",
            requirement = AchievementRequirement.BloomCount(1),
            celebrationMessage = "You've turned wisdom into action."
        ),
        Achievement(
            id = "consistent_bloomer",
            name = "Consistent Bloomer",
            description = "Bloom 7 days in a row",
            category = AchievementCategory.WISDOM,
            rarity = AchievementRarity.UNCOMMON,
            iconName = "ic_achievement_consistent_bloomer",
            requirement = AchievementRequirement.BloomStreak(7),
            celebrationMessage = "A week of wisdom applied."
        ),
        Achievement(
            id = "wisdom_curator",
            name = "Wisdom Curator",
            description = "Save 25 pieces to Collection",
            category = AchievementCategory.WISDOM,
            rarity = AchievementRarity.UNCOMMON,
            iconName = "ic_achievement_curator",
            requirement = AchievementRequirement.Count(25),
            celebrationMessage = "You've built a library of wisdom."
        ),
        Achievement(
            id = "discipline_master",
            name = "Discipline Master",
            description = "Reach Level 10 in Discipline",
            category = AchievementCategory.WISDOM,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_discipline_master",
            requirement = AchievementRequirement.SkillLevel(Skill.DISCIPLINE, 10),
            rewardTitleId = "master_of_words",
            celebrationMessage = "Mastery of discipline opens all doors."
        )
    )

    // =========================================================================
    // TIME CATEGORY (Temporal milestones)
    // =========================================================================

    private val timeAchievements = listOf(
        Achievement(
            id = "getting_started",
            name = "Getting Started",
            description = "Use Prody for 7 days",
            category = AchievementCategory.TIME,
            rarity = AchievementRarity.COMMON,
            iconName = "ic_achievement_week_one",
            requirement = AchievementRequirement.DaysOnApp(7),
            rewardBannerId = "week_one",
            celebrationMessage = "Seven days of possibility."
        ),
        Achievement(
            id = "one_month_in",
            name = "One Month In",
            description = "Use Prody for 30 days",
            category = AchievementCategory.TIME,
            rarity = AchievementRarity.UNCOMMON,
            iconName = "ic_achievement_month_one",
            requirement = AchievementRequirement.DaysOnApp(30),
            rewardBannerId = "month_one",
            rewardTitleId = "dedicated_30",
            celebrationMessage = "One lunar cycle of growth."
        ),
        Achievement(
            id = "quarterly_reflection",
            name = "Quarterly Reflection",
            description = "Use Prody for 90 days",
            category = AchievementCategory.TIME,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_quarter",
            requirement = AchievementRequirement.DaysOnApp(90),
            rewardBannerId = "quarter_year",
            rewardTitleId = "steadfast_90",
            celebrationMessage = "A full season on the path."
        ),
        Achievement(
            id = "year_of_growth",
            name = "Year of Growth",
            description = "Use Prody for 365 days",
            category = AchievementCategory.TIME,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_year_one",
            requirement = AchievementRequirement.DaysOnApp(365),
            rewardBannerId = "year_one",
            rewardTitleId = "elder_365",
            celebrationMessage = "One complete orbit of growth."
        ),
        Achievement(
            id = "early_bird",
            name = "Early Bird",
            description = "Write 10 entries before 7am",
            category = AchievementCategory.TIME,
            rarity = AchievementRarity.UNCOMMON,
            iconName = "ic_achievement_early_bird",
            requirement = AchievementRequirement.TimeOfDay(beforeHour = 7, afterHour = null, count = 10),
            rewardBannerId = "dawn_seeker",
            rewardTitleId = "dawn_seeker",
            celebrationMessage = "You greet the morning light."
        ),
        Achievement(
            id = "night_owl",
            name = "Night Owl",
            description = "Write 10 entries after 10pm",
            category = AchievementCategory.TIME,
            rarity = AchievementRarity.UNCOMMON,
            iconName = "ic_achievement_night_owl",
            requirement = AchievementRequirement.TimeOfDay(beforeHour = null, afterHour = 22, count = 10),
            rewardBannerId = "night_contemplative",
            rewardTitleId = "night_owl",
            celebrationMessage = "Wisdom found in the quiet hours."
        )
    )

    // =========================================================================
    // JOURNEY CATEGORY (Overall progress with 20-level system)
    // =========================================================================

    private val journeyAchievements = listOf(
        Achievement(
            id = "first_steps",
            name = "First Steps",
            description = "Reach combined skill level of 6",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.COMMON,
            iconName = "ic_achievement_first_steps",
            requirement = AchievementRequirement.CombinedLevel(6),
            celebrationMessage = "Your journey has begun."
        ),
        Achievement(
            id = "triple_digit",
            name = "Triple Digit",
            description = "Reach combined skill level of 15",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.UNCOMMON,
            iconName = "ic_achievement_triple_digit",
            requirement = AchievementRequirement.CombinedLevel(15),
            celebrationMessage = "Balance across all paths."
        ),
        Achievement(
            id = "well_rounded",
            name = "Well Rounded",
            description = "Reach Level 5 in all three skills",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_well_rounded",
            requirement = AchievementRequirement.AllSkillsMinLevel(5),
            rewardBannerId = "contemplative_garden",
            celebrationMessage = "Harmony in all aspects of growth."
        ),
        Achievement(
            id = "balanced_seeker",
            name = "Balanced Seeker",
            description = "Reach Level 10 in all three skills",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_balanced_seeker",
            requirement = AchievementRequirement.AllSkillsMinLevel(10),
            rewardBannerId = "sages_vista",
            celebrationMessage = "The vista from the heights of understanding."
        ),
        Achievement(
            id = "journey_milestone",
            name = "Journey Milestone",
            description = "Reach combined skill level of 45",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_journey_milestone",
            requirement = AchievementRequirement.CombinedLevel(45),
            rewardBannerId = "mountain_peak",
            celebrationMessage = "Halfway to true mastery."
        ),
        Achievement(
            id = "enlightened_one",
            name = "Enlightened One",
            description = "Reach Level 15 in all three skills",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.LEGENDARY,
            iconName = "ic_achievement_enlightened",
            requirement = AchievementRequirement.AllSkillsMinLevel(15),
            rewardBannerId = "awakened_sky",
            rewardTitleId = "enlightened",
            celebrationMessage = "True wisdom flows through you."
        ),
        Achievement(
            id = "true_master",
            name = "True Master",
            description = "Reach Level 20 in all three skills",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.MYTHIC,
            iconName = "ic_achievement_true_master",
            requirement = AchievementRequirement.AllSkillsMinLevel(20),
            rewardBannerId = "celestial_summit",
            rewardTitleId = "awakened_master",
            rewardFrameId = "golden_aura",
            celebrationMessage = "You have achieved perfection. The summit is yours."
        ),
        Achievement(
            id = "xp_milestone_1k",
            name = "Thousand Steps",
            description = "Earn 1,000 total XP",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.COMMON,
            iconName = "ic_achievement_xp_1k",
            requirement = AchievementRequirement.TotalXp(1000),
            celebrationMessage = "The first thousand steps."
        ),
        Achievement(
            id = "xp_milestone_10k",
            name = "Ten Thousand Steps",
            description = "Earn 10,000 total XP",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_xp_10k",
            requirement = AchievementRequirement.TotalXp(10000),
            celebrationMessage = "Ten thousand steps on the path."
        ),
        Achievement(
            id = "xp_milestone_50k",
            name = "Fifty Thousand Steps",
            description = "Earn 50,000 total XP",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_xp_50k",
            requirement = AchievementRequirement.TotalXp(50000),
            rewardBannerId = "golden_path",
            celebrationMessage = "Fifty thousand moments of growth."
        )
    )

    // =========================================================================
    // SPECIAL CATEGORY (Rare/Hidden)
    // =========================================================================

    private val specialAchievements = listOf(
        Achievement(
            id = "time_traveler",
            name = "Time Traveler",
            description = "Receive a future message from 6+ months ago",
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_time_traveler",
            requirement = AchievementRequirement.SingleAction("receive_6_month_message"),
            rewardTitleId = "time_weaver",
            rewardBannerId = "timeless",
            celebrationMessage = "A conversation across the river of time."
        ),
        Achievement(
            id = "conversation_across_time",
            name = "Conversation Across Time",
            description = "Reply to 5 past self messages",
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_conversation",
            requirement = AchievementRequirement.Count(5),
            rewardTitleId = "temporal_architect",
            rewardBannerId = "time_architect",
            celebrationMessage = "You speak to who you were."
        ),
        Achievement(
            id = "seven_day_bloom",
            name = "Seven Day Bloom",
            description = "Bloom every day for a week",
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_seven_bloom",
            requirement = AchievementRequirement.BloomStreak(7),
            celebrationMessage = "A week of wisdom applied."
        ),
        Achievement(
            id = "month_of_blooms",
            name = "Month of Blooms",
            description = "Bloom 20 times in a month",
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_month_blooms",
            requirement = AchievementRequirement.BloomCount(20),
            celebrationMessage = "A garden of wisdom blooms within you."
        ),
        Achievement(
            id = "clarity_master",
            name = "Clarity Master",
            description = "Reach Level 10 in Clarity",
            category = AchievementCategory.MASTERY,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_clarity_master",
            requirement = AchievementRequirement.SkillLevel(Skill.CLARITY, 10),
            rewardTitleId = "reflective_soul",
            celebrationMessage = "Your inner vision is crystal clear."
        ),
        Achievement(
            id = "courage_master",
            name = "Courage Master",
            description = "Reach Level 10 in Courage",
            category = AchievementCategory.MASTERY,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_courage_master",
            requirement = AchievementRequirement.SkillLevel(Skill.COURAGE, 10),
            rewardTitleId = "temporal_architect",
            celebrationMessage = "You face the future without fear."
        ),
        Achievement(
            id = "flame_keeper",
            name = "Flame Keeper",
            description = "Maintain a 30-day streak",
            category = AchievementCategory.TIME,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_flame_keeper",
            requirement = AchievementRequirement.Streak(30),
            rewardBannerId = "flame_keeper",
            rewardTitleId = "flame_keeper",
            celebrationMessage = "You maintain the sacred fire of consistency."
        ),
        Achievement(
            id = "eternal_flame",
            name = "Eternal Flame",
            description = "Maintain a 365-day streak",
            category = AchievementCategory.TIME,
            rarity = AchievementRarity.LEGENDARY,
            iconName = "ic_achievement_eternal_flame",
            requirement = AchievementRequirement.Streak(365),
            rewardBannerId = "eternal_flame",
            rewardTitleId = "eternal_flame",
            celebrationMessage = "A year of unbroken fire."
        )
    )

    /**
     * All achievements combined.
     */
    val allAchievements: List<Achievement> = listOf(
        reflectionAchievements,
        wisdomAchievements,
        timeAchievements,
        journeyAchievements,
        specialAchievements
    ).flatten()

    /**
     * Get achievements by category.
     */
    fun getByCategory(category: AchievementCategory): List<Achievement> =
        allAchievements.filter { it.category == category }

    /**
     * Get achievements by rarity.
     */
    fun getByRarity(rarity: AchievementRarity): List<Achievement> =
        allAchievements.filter { it.rarity == rarity }

    /**
     * Find achievement by ID.
     */
    fun findById(id: String): Achievement? =
        allAchievements.find { it.id == id }

    /**
     * Get count of achievements by category.
     */
    val categoryCount: Map<AchievementCategory, Int>
        get() = allAchievements.groupBy { it.category }.mapValues { it.value.size }

    /**
     * Total number of achievements.
     */
    val totalCount: Int get() = allAchievements.size
}
