package com.prody.prashant.domain.gamification

import androidx.compose.ui.graphics.Color

/**
 * Achievement categories for meaningful milestones.
 */
enum class AchievementCategory(val id: String, val displayName: String) {
    REFLECTION("reflection", "Reflection"),
    WISDOM("wisdom", "Wisdom"),
    TIME("time", "Time"),
    MASTERY("mastery", "Mastery"),
    JOURNEY("journey", "Journey"),
    SPECIAL("special", "Special")
}

/**
 * Achievement rarity levels with associated colors.
 */
enum class AchievementRarity(
    val id: String,
    val displayName: String,
    val color: Color,
    val sortOrder: Int
) {
    COMMON("common", "Common", Color(0xFF9E9E9E), 1),
    UNCOMMON("uncommon", "Uncommon", Color(0xFF4CAF50), 2),
    RARE("rare", "Rare", Color(0xFF2196F3), 3),
    EPIC("epic", "Epic", Color(0xFF9C27B0), 4),
    LEGENDARY("legendary", "Legendary", Color(0xFFFFD700), 5)
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

    /** Time-based requirement (e.g., use app for 7 days) */
    data class DaysOnApp(val days: Int) : AchievementRequirement()

    /** Single action requirement (e.g., write first entry) */
    data class SingleAction(val actionType: String) : AchievementRequirement()

    /** Time of day requirement (e.g., before 7am) */
    data class TimeOfDay(val beforeHour: Int?, val afterHour: Int?, val count: Int) : AchievementRequirement()

    /** Word count requirement */
    data class WordCount(val minWords: Int) : AchievementRequirement()

    /** Bloom streak requirement */
    data class BloomStreak(val days: Int) : AchievementRequirement()

    /** Bloom count requirement */
    data class BloomCount(val count: Int) : AchievementRequirement()
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
    val xpReward: Int = PointCalculator.AchievementRewards.getReward(rarity),
    val celebrationMessage: String = ""
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
            is AchievementRequirement.TimeOfDay -> currentValue >= requirement.count
            else -> false
        }
    }

    /**
     * Calculate progress percentage (0.0 to 1.0).
     */
    fun calculateProgress(currentValue: Int): Float {
        val target = when (requirement) {
            is AchievementRequirement.Count -> requirement.target
            is AchievementRequirement.Streak -> requirement.days
            is AchievementRequirement.DaysOnApp -> requirement.days
            is AchievementRequirement.BloomStreak -> requirement.days
            is AchievementRequirement.BloomCount -> requirement.count
            is AchievementRequirement.WordCount -> requirement.minWords
            is AchievementRequirement.TimeOfDay -> requirement.count
            else -> 1
        }
        return (currentValue.toFloat() / target).coerceIn(0f, 1f)
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
    // JOURNEY CATEGORY (Overall progress)
    // =========================================================================

    private val journeyAchievements = listOf(
        Achievement(
            id = "triple_digit",
            name = "Triple Digit",
            description = "Reach combined skill level of 10",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.UNCOMMON,
            iconName = "ic_achievement_triple_digit",
            requirement = AchievementRequirement.CombinedLevel(10),
            celebrationMessage = "Balance across all paths."
        ),
        Achievement(
            id = "well_rounded",
            name = "Well Rounded",
            description = "Reach Level 5 in all three skills",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.RARE,
            iconName = "ic_achievement_well_rounded",
            requirement = AchievementRequirement.CombinedLevel(15),
            rewardBannerId = "contemplative_garden",
            celebrationMessage = "Harmony in all aspects of growth."
        ),
        Achievement(
            id = "journey_milestone",
            name = "Journey Milestone",
            description = "Reach combined skill level of 25",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.EPIC,
            iconName = "ic_achievement_journey_milestone",
            requirement = AchievementRequirement.CombinedLevel(25),
            rewardBannerId = "sages_vista",
            celebrationMessage = "The vista from the heights of understanding."
        ),
        Achievement(
            id = "true_seeker",
            name = "True Seeker",
            description = "Reach Level 10 in all three skills",
            category = AchievementCategory.JOURNEY,
            rarity = AchievementRarity.LEGENDARY,
            iconName = "ic_achievement_true_seeker",
            requirement = AchievementRequirement.CombinedLevel(30),
            rewardBannerId = "awakened_sky",
            rewardTitleId = "awakened",
            celebrationMessage = "You have reached the summit. True seeing is yours."
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
