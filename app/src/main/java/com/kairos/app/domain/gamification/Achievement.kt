package com.kairos.app.domain.gamification

import com.kairos.app.domain.identity.KairosAchievements

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
    val colorArgb: Long,
    val glowColorArgb: Long,
    val sortOrder: Int,
    val xpMultiplier: Float
) {
    COMMON(
        "common",
        "Common",
        0xFF78909C,
        0x3378909C,
        1,
        1.0f
    ),
    UNCOMMON(
        "uncommon",
        "Uncommon",
        0xFF66BB6A,
        0x3366BB6A,
        2,
        1.5f
    ),
    RARE(
        "rare",
        "Rare",
        0xFF42A5F5,
        0x3342A5F5,
        3,
        2.0f
    ),
    EPIC(
        "epic",
        "Epic",
        0xFFAB47BC,
        0x33AB47BC,
        4,
        3.0f
    ),
    LEGENDARY(
        "legendary",
        "Legendary",
        0xFFD4AF37,
        0x33D4AF37,
        5,
        5.0f
    ),
    MYTHIC(
        "mythic",
        "Mythic",
        0xFFFFD700,
        0x66FFD700,
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
    /**
     * Compatibility view over the single canonical Kairos achievement catalogue.
     *
     * Older gamification APIs still expose [Achievement], while profile and
     * persistence use [KairosAchievements]. Mapping here prevents two lists of
     * IDs, thresholds, and copy from drifting apart again.
     */
    val allAchievements: List<Achievement> by lazy {
        KairosAchievements.allAchievements.map(::fromCanonical)
    }

    fun getByCategory(category: AchievementCategory): List<Achievement> =
        allAchievements.filter { it.category == category }

    fun getByRarity(rarity: AchievementRarity): List<Achievement> =
        allAchievements.filter { it.rarity == rarity }

    fun findById(id: String): Achievement? =
        allAchievements.find { it.id == id }

    val categoryCount: Map<AchievementCategory, Int>
        get() = allAchievements.groupBy { it.category }.mapValues { it.value.size }

    val totalCount: Int get() = allAchievements.size

    private fun fromCanonical(source: KairosAchievements.Achievement): Achievement {
        val category = when (source.category) {
            KairosAchievements.Category.WISDOM -> AchievementCategory.WISDOM
            KairosAchievements.Category.REFLECTION -> AchievementCategory.REFLECTION
            KairosAchievements.Category.CONSISTENCY -> AchievementCategory.TIME
            KairosAchievements.Category.PRESENCE -> AchievementCategory.JOURNEY
            KairosAchievements.Category.TEMPORAL -> AchievementCategory.FUTURE
            KairosAchievements.Category.MASTERY -> AchievementCategory.MASTERY
            KairosAchievements.Category.SOCIAL -> AchievementCategory.SOCIAL
            KairosAchievements.Category.EXPLORER -> AchievementCategory.JOURNEY
        }
        val rarity = when (source.rarity) {
            KairosAchievements.Rarity.COMMON -> AchievementRarity.COMMON
            KairosAchievements.Rarity.UNCOMMON -> AchievementRarity.UNCOMMON
            KairosAchievements.Rarity.RARE -> AchievementRarity.RARE
            KairosAchievements.Rarity.EPIC -> AchievementRarity.EPIC
            KairosAchievements.Rarity.LEGENDARY -> AchievementRarity.LEGENDARY
        }
        val requirement = when {
            source.id.startsWith("streak_") -> AchievementRequirement.Streak(source.requirement)
            source.category == KairosAchievements.Category.TEMPORAL ->
                AchievementRequirement.FutureMessages(source.requirement)
            source.id.contains("word") -> AchievementRequirement.VocabularyMastered(source.requirement)
            else -> AchievementRequirement.Count(source.requirement)
        }
        return Achievement(
            id = source.id,
            name = source.name,
            description = source.description,
            category = category,
            rarity = rarity,
            iconName = source.iconName,
            requirement = requirement,
            xpReward = source.rewardPoints,
            celebrationMessage = source.celebrationMessage
        )
    }
}
