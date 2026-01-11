package com.prody.prashant.domain.gamification

import androidx.compose.ui.graphics.Color

/**
 * Rank System - Based on Combined Skill Levels
 *
 * Combined level = Clarity level + Discipline level + Courage level
 * Maximum combined level = 60 (all three skills at level 20)
 *
 * With the updated 20-level skill system, ranks are distributed across
 * a wider range to create meaningful milestones throughout the journey.
 *
 * This creates a meaningful progression where rank reflects balanced growth
 * across all three domains, not just total points accumulated.
 *
 * Philosophy: True mastery comes from balance. A user with all three skills
 * developed shows more wisdom than one who only focused on a single path.
 */

/**
 * User rank based on combined skill levels.
 *
 * The rank names draw from diverse wisdom traditions:
 * - Seeker: Beginning the path of inquiry
 * - Learner: Active engagement with knowledge
 * - Initiate: Commitment to the practice
 * - Student: Learning from every experience
 * - Practitioner: Embodying wisdom in action
 * - Contemplative: Deep understanding
 * - Philosopher: Loving wisdom
 * - Sage: Wisdom tempered by time
 * - Luminary: Illuminating the path for others
 * - Awakened: Full mastery across all domains
 */
enum class Rank(
    val id: String,
    val displayName: String,
    val description: String,
    val minCombinedLevel: Int,
    val color: Color,
    val philosophicalOrigin: String
) {
    SEEKER(
        id = "seeker",
        displayName = "Seeker",
        description = "One who has begun the path of inquiry",
        minCombinedLevel = 0,
        color = Color(0xFF9E9E9E),
        philosophicalOrigin = "The first step in any journey of wisdom"
    ),
    LEARNER(
        id = "learner",
        displayName = "Learner",
        description = "One who actively engages with knowledge",
        minCombinedLevel = 6,
        color = Color(0xFF78909C),
        philosophicalOrigin = "The foundation of all wisdom traditions"
    ),
    INITIATE(
        id = "initiate",
        displayName = "Initiate",
        description = "One who has committed to the practice",
        minCombinedLevel = 12,
        color = Color(0xFF607D8B),
        philosophicalOrigin = "Ancient mystery school traditions"
    ),
    STUDENT(
        id = "student",
        displayName = "Student of Life",
        description = "One who learns from every experience",
        minCombinedLevel = 18,
        color = Color(0xFF5C6BC0),
        philosophicalOrigin = "Socratic tradition of perpetual learning"
    ),
    PRACTITIONER(
        id = "practitioner",
        displayName = "Practitioner",
        description = "One who embodies wisdom in daily action",
        minCombinedLevel = 24,
        color = Color(0xFF26A69A),
        philosophicalOrigin = "Stoic philosophy of praxis"
    ),
    CONTEMPLATIVE(
        id = "contemplative",
        displayName = "Contemplative",
        description = "One who sees deeply into the nature of things",
        minCombinedLevel = 30,
        color = Color(0xFF4ECDC4),
        philosophicalOrigin = "Meditative traditions East and West"
    ),
    PHILOSOPHER(
        id = "philosopher",
        displayName = "Philosopher",
        description = "A lover of wisdom who questions and reflects",
        minCombinedLevel = 36,
        color = Color(0xFF6B5CE7),
        philosophicalOrigin = "Greek: philos (lover) + sophia (wisdom)"
    ),
    SAGE(
        id = "sage",
        displayName = "Sage",
        description = "One whose wisdom has been tempered by time",
        minCombinedLevel = 45,
        color = Color(0xFFAC32E4),
        philosophicalOrigin = "Universal archetype of the wise elder"
    ),
    LUMINARY(
        id = "luminary",
        displayName = "Luminary",
        description = "One whose presence illuminates the path for others",
        minCombinedLevel = 54,
        color = Color(0xFFD4AF37),
        philosophicalOrigin = "Latin: lumen - light"
    ),
    AWAKENED(
        id = "awakened",
        displayName = "Awakened",
        description = "One who has achieved mastery across all domains",
        minCombinedLevel = 60,
        color = Color(0xFFFFD700),
        philosophicalOrigin = "Buddhist concept of Bodhi - full enlightenment"
    );

    companion object {
        /** Maximum combined level (all three skills at level 20) */
        const val MAX_COMBINED_LEVEL = 60

        /**
         * Determines the appropriate rank for a given combined level.
         *
         * @param combinedLevel Sum of all three skill levels (3-30)
         * @return The highest rank achieved
         */
        fun fromCombinedLevel(combinedLevel: Int): Rank {
            return entries.lastOrNull { combinedLevel >= it.minCombinedLevel } ?: SEEKER
        }

        /**
         * Determines rank from individual skill levels.
         *
         * @param clarityLevel Level 1-20
         * @param disciplineLevel Level 1-20
         * @param courageLevel Level 1-20
         */
        fun fromSkillLevels(clarityLevel: Int, disciplineLevel: Int, courageLevel: Int): Rank {
            val combined = clarityLevel + disciplineLevel + courageLevel
            return fromCombinedLevel(combined)
        }

        /**
         * Gets the next rank in progression.
         */
        fun getNextRank(current: Rank): Rank? {
            val currentIndex = entries.indexOf(current)
            return entries.getOrNull(currentIndex + 1)
        }

        /**
         * Finds a rank by its unique identifier.
         */
        fun fromId(id: String): Rank? {
            return entries.find { it.id == id }
        }

        /**
         * Calculate progress toward next rank as a percentage.
         *
         * @param combinedLevel Current combined level
         * @return Float between 0.0 and 1.0, or 1.0 if at max rank
         */
        fun calculateProgressToNextRank(combinedLevel: Int): Float {
            val currentRank = fromCombinedLevel(combinedLevel)
            val nextRank = getNextRank(currentRank) ?: return 1f

            val progressInRank = combinedLevel - currentRank.minCombinedLevel
            val levelsNeeded = nextRank.minCombinedLevel - currentRank.minCombinedLevel

            return if (levelsNeeded > 0) {
                (progressInRank.toFloat() / levelsNeeded).coerceIn(0f, 1f)
            } else {
                1f
            }
        }

        /**
         * Calculate combined levels needed to reach next rank.
         *
         * @param combinedLevel Current combined level
         * @return Levels needed, or 0 if at maximum rank
         */
        fun levelsToNextRank(combinedLevel: Int): Int {
            val currentRank = fromCombinedLevel(combinedLevel)
            val nextRank = getNextRank(currentRank) ?: return 0
            return (nextRank.minCombinedLevel - combinedLevel).coerceAtLeast(0)
        }
    }
}

/**
 * Current rank state for a player.
 */
data class RankState(
    val currentRank: Rank,
    val combinedLevel: Int,
    val progressToNextRank: Float,
    val levelsToNextRank: Int,
    val nextRank: Rank?,
    val clarityLevel: Int,
    val disciplineLevel: Int,
    val courageLevel: Int
) {
    /**
     * Check if user is at maximum rank.
     */
    val isMaxRank: Boolean get() = currentRank == Rank.AWAKENED

    /**
     * Check if user has mastered all skills.
     */
    val isFullyMastered: Boolean get() = combinedLevel >= Rank.MAX_COMBINED_LEVEL

    /**
     * Get which skill is lagging (lowest level).
     */
    val lowestSkill: Skill
        get() = when {
            clarityLevel <= disciplineLevel && clarityLevel <= courageLevel -> Skill.CLARITY
            disciplineLevel <= courageLevel -> Skill.DISCIPLINE
            else -> Skill.COURAGE
        }

    /**
     * Get suggested focus for balanced growth.
     */
    val suggestedFocus: String
        get() = when (lowestSkill) {
            Skill.CLARITY -> "Focus on journaling to balance your growth"
            Skill.DISCIPLINE -> "Engage with wisdom content to balance your growth"
            Skill.COURAGE -> "Write to your future self to balance your growth"
        }

    companion object {
        fun fromSkillLevels(
            clarityLevel: Int,
            disciplineLevel: Int,
            courageLevel: Int
        ): RankState {
            val combined = clarityLevel + disciplineLevel + courageLevel
            val currentRank = Rank.fromCombinedLevel(combined)
            val nextRank = Rank.getNextRank(currentRank)

            return RankState(
                currentRank = currentRank,
                combinedLevel = combined,
                progressToNextRank = Rank.calculateProgressToNextRank(combined),
                levelsToNextRank = Rank.levelsToNextRank(combined),
                nextRank = nextRank,
                clarityLevel = clarityLevel,
                disciplineLevel = disciplineLevel,
                courageLevel = courageLevel
            )
        }

        fun initial(): RankState = fromSkillLevels(1, 1, 1)
    }
}

/**
 * Result of a rank check/update.
 */
sealed class RankUpdateResult {
    /**
     * Rank stayed the same.
     */
    data class NoChange(val currentRank: Rank) : RankUpdateResult()

    /**
     * User advanced to a new rank!
     */
    data class RankUp(
        val previousRank: Rank,
        val newRank: Rank,
        val message: String
    ) : RankUpdateResult()
}

/**
 * Messages for rank advancement and display.
 */
object RankMessages {
    /**
     * Get advancement message when user reaches a new rank.
     */
    fun advancementMessage(newRank: Rank): String = when (newRank) {
        Rank.SEEKER -> "Your journey begins. Every great sage was once where you stand now."
        Rank.LEARNER -> "You've begun to learn. Knowledge is the foundation of wisdom."
        Rank.INITIATE -> "You have committed to the path. The practice deepens with each step."
        Rank.STUDENT -> "A student sees lessons everywhere. Your eyes are opening."
        Rank.PRACTITIONER -> "Wisdom without action is merely theory. You embody what you learn."
        Rank.CONTEMPLATIVE -> "In stillness, you find depth. Your understanding grows profound."
        Rank.PHILOSOPHER -> "You have become a lover of wisdom. Questions matter as much as answers."
        Rank.SAGE -> "Time has tempered your understanding. Others may learn from your journey."
        Rank.LUMINARY -> "Your presence illuminates the path. You carry light for those who follow."
        Rank.AWAKENED -> "You have achieved mastery across all domains. True seeing is yours."
    }

    /**
     * Get personalized greeting based on rank.
     */
    fun greetingForRank(rank: Rank, displayName: String): String = when (rank) {
        Rank.SEEKER -> "Welcome, $displayName. The path awaits."
        Rank.LEARNER -> "Welcome back, Learner $displayName. What will you discover today?"
        Rank.INITIATE -> "Greetings, Initiate $displayName. Your commitment is noted."
        Rank.STUDENT -> "Welcome back, Student $displayName. What will you learn today?"
        Rank.PRACTITIONER -> "Good to see you, Practitioner $displayName. The practice continues."
        Rank.CONTEMPLATIVE -> "Welcome, Contemplative $displayName. Depth awaits your attention."
        Rank.PHILOSOPHER -> "Greetings, Philosopher $displayName. What questions arise today?"
        Rank.SAGE -> "Welcome, Sage $displayName. Your wisdom grows with each return."
        Rank.LUMINARY -> "Greetings, Luminary $displayName. Your light guides others."
        Rank.AWAKENED -> "Greetings, Awakened One. You see clearly now."
    }

    /**
     * Get description of what combined level represents.
     */
    fun combinedLevelDescription(combinedLevel: Int): String = when {
        combinedLevel <= 5 -> "Beginning your journey across all three paths"
        combinedLevel <= 10 -> "Building foundation in reflection, wisdom, and courage"
        combinedLevel <= 15 -> "Growing steadily across all domains"
        combinedLevel <= 20 -> "Demonstrating balanced dedication"
        combinedLevel <= 25 -> "Approaching mastery in multiple areas"
        combinedLevel < 30 -> "Nearly complete mastery across all paths"
        else -> "Full mastery achieved - you have walked all three paths"
    }

    /**
     * Get motivational message for current state.
     */
    fun motivationalMessage(rankState: RankState): String {
        return when {
            rankState.isFullyMastered -> "You have achieved complete mastery. Your journey is complete, yet continues."
            rankState.levelsToNextRank == 1 -> "Just one more level to reach ${rankState.nextRank?.displayName}!"
            rankState.levelsToNextRank <= 3 -> "You're close to becoming a ${rankState.nextRank?.displayName}."
            else -> rankState.suggestedFocus
        }
    }
}
