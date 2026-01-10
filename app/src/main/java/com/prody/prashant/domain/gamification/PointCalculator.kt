package com.prody.prashant.domain.gamification

/**
 * Calculates points for all gamification actions.
 *
 * Points are tied to meaningful accomplishments rather than trivial actions.
 * Each action awards points to the appropriate skill:
 * - Clarity: Journal-focused actions
 * - Discipline: Wisdom-focused actions
 * - Courage: Future-focused actions
 */
object PointCalculator {

    // =========================================================================
    // CLARITY POINTS (Journal-focused)
    // =========================================================================

    object Clarity {
        /** Base points for saving a journal entry */
        const val JOURNAL_ENTRY_BASE = 10

        /** Bonus for entry over 100 words (depth) */
        const val ENTRY_OVER_100_WORDS = 5

        /** Bonus for entry over 300 words (comprehensive) */
        const val ENTRY_OVER_300_WORDS = 10

        /** Bonus when entry triggers Buddha/AI insight */
        const val TRIGGERS_INSIGHT = 5

        /** Bonus when entry includes question or reflection */
        const val INCLUDES_REFLECTION = 3

        /** Points for saving a micro-entry */
        const val MICRO_ENTRY = 3

        /**
         * Calculate total Clarity points for a journal entry.
         */
        fun calculateJournalPoints(
            wordCount: Int,
            triggeredInsight: Boolean = false,
            includesReflection: Boolean = false,
            isMicroEntry: Boolean = false
        ): Int {
            if (isMicroEntry) return MICRO_ENTRY

            var points = JOURNAL_ENTRY_BASE

            // Word count bonuses (not cumulative - only highest applies)
            points += when {
                wordCount >= 300 -> ENTRY_OVER_300_WORDS
                wordCount >= 100 -> ENTRY_OVER_100_WORDS
                else -> 0
            }

            if (triggeredInsight) points += TRIGGERS_INSIGHT
            if (includesReflection) points += INCLUDES_REFLECTION

            return points
        }

        /**
         * Detect if content includes reflective elements (questions, introspection).
         */
        fun detectReflection(content: String): Boolean {
            val reflectiveIndicators = listOf(
                "?", // Questions
                "I wonder", "I think", "I feel", "I realize", "I notice",
                "perhaps", "maybe", "what if", "why do", "how do",
                "reflecting on", "looking back", "grateful for", "thankful"
            )
            val lowerContent = content.lowercase()
            return reflectiveIndicators.any { lowerContent.contains(it.lowercase()) }
        }
    }

    // =========================================================================
    // DISCIPLINE POINTS (Wisdom-focused)
    // =========================================================================

    object Discipline {
        /** Points for viewing Word of the Day */
        const val VIEW_WORD_OF_DAY = 2

        /** Points for reading quote explanation */
        const val READ_QUOTE_EXPLANATION = 3

        /** Points for saving wisdom to collection */
        const val SAVE_TO_COLLECTION = 5

        /** Points for using daily Seed in journal (Bloom) */
        const val BLOOM_SEED = 15

        /** Points for viewing proverb/idiom detail */
        const val VIEW_WISDOM_DETAIL = 2

        /** Points for completing weekly digest */
        const val COMPLETE_WEEKLY_DIGEST = 10

        /** Points per flashcard reviewed (base) */
        const val FLASHCARD_REVIEWED = 2

        /** Bonus for flashcard session with 80%+ accuracy */
        const val FLASHCARD_HIGH_ACCURACY_BONUS = 10

        /** Bonus for flashcard session with 60-79% accuracy */
        const val FLASHCARD_MEDIUM_ACCURACY_BONUS = 5

        /**
         * Calculate total Discipline points for flashcard session.
         */
        fun calculateFlashcardPoints(
            cardsReviewed: Int,
            accuracy: Float
        ): Int {
            var points = cardsReviewed * FLASHCARD_REVIEWED

            // Accuracy bonus
            points += when {
                accuracy >= 0.80f -> FLASHCARD_HIGH_ACCURACY_BONUS
                accuracy >= 0.60f -> FLASHCARD_MEDIUM_ACCURACY_BONUS
                else -> 0
            }

            return points
        }
    }

    // =========================================================================
    // COURAGE POINTS (Future-focused)
    // =========================================================================

    object Courage {
        /** Base points for creating a future message */
        const val CREATE_FUTURE_MESSAGE = 10

        /** Bonus for delivery 30+ days in future */
        const val DELIVERY_30_PLUS_DAYS = 5

        /** Bonus for delivery 180+ days in future */
        const val DELIVERY_180_PLUS_DAYS = 10

        /** Points for opening a delivered message */
        const val OPEN_DELIVERED_MESSAGE = 15

        /** Points for replying to past self */
        const val REPLY_TO_PAST_SELF = 20

        /** Points for anniversary reflection */
        const val ANNIVERSARY_REFLECTION = 25

        /**
         * Calculate total Courage points for creating a future message.
         */
        fun calculateFutureMessagePoints(daysUntilDelivery: Int): Int {
            var points = CREATE_FUTURE_MESSAGE

            // Patience bonuses (not cumulative - only highest applies)
            points += when {
                daysUntilDelivery >= 180 -> DELIVERY_180_PLUS_DAYS
                daysUntilDelivery >= 30 -> DELIVERY_30_PLUS_DAYS
                else -> 0
            }

            return points
        }
    }

    // =========================================================================
    // STREAK BONUSES
    // =========================================================================

    object Streak {
        /** Bonus multiplier for 7+ day streak */
        const val WEEK_MULTIPLIER = 1.1f

        /** Bonus multiplier for 30+ day streak */
        const val MONTH_MULTIPLIER = 1.25f

        /** Bonus multiplier for 100+ day streak */
        const val CENTURY_MULTIPLIER = 1.5f

        /**
         * Get streak multiplier based on current streak.
         */
        fun getMultiplier(currentStreak: Int): Float = when {
            currentStreak >= 100 -> CENTURY_MULTIPLIER
            currentStreak >= 30 -> MONTH_MULTIPLIER
            currentStreak >= 7 -> WEEK_MULTIPLIER
            else -> 1.0f
        }

        /**
         * Apply streak bonus to points.
         */
        fun applyStreakBonus(basePoints: Int, currentStreak: Int): Int {
            val multiplier = getMultiplier(currentStreak)
            return (basePoints * multiplier).toInt()
        }
    }

    // =========================================================================
    // DAILY CAPS (Anti-exploit)
    // =========================================================================

    object DailyCaps {
        /** Maximum Clarity XP per day */
        const val CLARITY = 150

        /** Maximum Discipline XP per day */
        const val DISCIPLINE = 150

        /** Maximum Courage XP per day */
        const val COURAGE = 100

        /**
         * Get daily cap for a skill.
         */
        fun getCap(skill: Skill): Int = when (skill) {
            Skill.CLARITY -> CLARITY
            Skill.DISCIPLINE -> DISCIPLINE
            Skill.COURAGE -> COURAGE
        }

        /**
         * Calculate actual points after applying daily cap.
         */
        fun applyDailyCap(
            skill: Skill,
            pointsToAdd: Int,
            currentDailyPoints: Int
        ): Int {
            val cap = getCap(skill)
            val remaining = (cap - currentDailyPoints).coerceAtLeast(0)
            return pointsToAdd.coerceAtMost(remaining)
        }
    }

    // =========================================================================
    // ACHIEVEMENT REWARDS
    // =========================================================================

    object AchievementRewards {
        /** XP reward for Common achievement */
        const val COMMON = 10

        /** XP reward for Uncommon achievement */
        const val UNCOMMON = 25

        /** XP reward for Rare achievement */
        const val RARE = 50

        /** XP reward for Epic achievement */
        const val EPIC = 100

        /** XP reward for Legendary achievement */
        const val LEGENDARY = 200

        /**
         * Get XP reward based on achievement rarity.
         */
        fun getReward(rarity: AchievementRarity): Int = when (rarity) {
            AchievementRarity.COMMON -> COMMON
            AchievementRarity.UNCOMMON -> UNCOMMON
            AchievementRarity.RARE -> RARE
            AchievementRarity.EPIC -> EPIC
            AchievementRarity.LEGENDARY -> LEGENDARY
        }
    }

    // =========================================================================
    // LEVEL UP REWARDS
    // =========================================================================

    object LevelUpRewards {
        /** Base token reward for leveling up */
        const val BASE_TOKENS = 10

        /** Additional tokens per level reached */
        const val TOKENS_PER_LEVEL = 5

        /** Bonus tokens for reaching mastery (level 10) */
        const val MASTERY_BONUS_TOKENS = 50

        /**
         * Calculate token reward for reaching a new level.
         */
        fun calculateTokenReward(newLevel: Int): Int {
            var tokens = BASE_TOKENS + (newLevel * TOKENS_PER_LEVEL)
            if (newLevel >= Skill.MAX_LEVEL) {
                tokens += MASTERY_BONUS_TOKENS
            }
            return tokens
        }
    }

    // =========================================================================
    // BLOOM REWARDS
    // =========================================================================

    object BloomRewards {
        /** Base points for blooming today's seed */
        const val BLOOM_BASE_POINTS = 15

        /** Bonus tokens for consecutive day bloom */
        const val CONSECUTIVE_BLOOM_TOKENS = 5

        /** Bonus for 7-day bloom streak */
        const val WEEK_BLOOM_STREAK_BONUS = 25

        /** Bonus for 30-day bloom streak */
        const val MONTH_BLOOM_STREAK_BONUS = 100
    }

    // =========================================================================
    // TOKEN REWARDS
    // =========================================================================

    object Tokens {
        /** Tokens for blooming a seed */
        const val BLOOM_SEED = 5

        /** Tokens for completing daily mission */
        const val DAILY_MISSION_COMPLETE = 5

        /** Tokens for weekly trial completion */
        const val WEEKLY_TRIAL_COMPLETE = 25

        /** Tokens for streak milestone (7 days) */
        const val STREAK_7_DAY = 10

        /** Tokens for streak milestone (30 days) */
        const val STREAK_30_DAY = 25

        /** Tokens for streak milestone (100 days) */
        const val STREAK_100_DAY = 50

        /**
         * Get token reward for streak milestone.
         */
        fun getStreakMilestoneTokens(streak: Int): Int? = when (streak) {
            7 -> STREAK_7_DAY
            14 -> 15
            30 -> STREAK_30_DAY
            60 -> 35
            100 -> STREAK_100_DAY
            365 -> 100
            else -> null
        }
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    /**
     * Get daily cap for a skill.
     */
    fun getDailyCap(skill: Skill): Int = DailyCaps.getCap(skill)
}
