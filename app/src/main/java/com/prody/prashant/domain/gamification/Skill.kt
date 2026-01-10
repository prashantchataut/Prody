package com.prody.prashant.domain.gamification

import androidx.compose.ui.graphics.Color

/**
 * The three core skills that map to app activities.
 *
 * Clarity (Journal-focused): The depth of your reflection
 * Discipline (Wisdom-focused): Your commitment to learning
 * Courage (Future-focused): Your willingness to face the future
 *
 * Each skill has independent progression with a max level of 10.
 * Level 10 = Mastery, earning a special badge.
 */
enum class Skill(
    val displayName: String,
    val description: String,
    val color: Color,
    val maxLevel: Int = 10
) {
    CLARITY(
        displayName = "Clarity",
        description = "The depth of your reflection",
        color = Color(0xFF4A90D9)
    ),
    DISCIPLINE(
        displayName = "Discipline",
        description = "Your commitment to learning",
        color = Color(0xFF7B68EE)
    ),
    COURAGE(
        displayName = "Courage",
        description = "Your willingness to face the future",
        color = Color(0xFFE57373)
    );

    companion object {
        /**
         * XP thresholds for each level (cumulative).
         * Level 1 starts at 0 XP, Level 10 (Mastery) requires 2200 total XP.
         *
         * The curve is exponential to make early progress feel quick
         * while mastery requires sustained effort.
         */
        val LEVEL_THRESHOLDS = listOf(
            0,      // Level 1
            50,     // Level 2
            120,    // Level 3
            220,    // Level 4
            360,    // Level 5
            550,    // Level 6
            800,    // Level 7
            1150,   // Level 8
            1600,   // Level 9
            2200    // Level 10 (Mastery)
        )

        const val MAX_LEVEL = 10

        /**
         * Calculate level from total XP.
         */
        fun calculateLevel(totalXp: Int): Int {
            for (i in LEVEL_THRESHOLDS.indices.reversed()) {
                if (totalXp >= LEVEL_THRESHOLDS[i]) {
                    return (i + 1).coerceAtMost(MAX_LEVEL)
                }
            }
            return 1
        }

        /**
         * Get XP required to reach the next level.
         */
        fun getXpForNextLevel(currentLevel: Int): Int {
            if (currentLevel >= MAX_LEVEL) return 0
            return LEVEL_THRESHOLDS.getOrElse(currentLevel) { LEVEL_THRESHOLDS.last() }
        }

        /**
         * Get XP threshold for a specific level.
         */
        fun getXpThreshold(level: Int): Int {
            return LEVEL_THRESHOLDS.getOrElse(level - 1) { 0 }
        }

        /**
         * Calculate progress percentage toward next level (0.0 to 1.0).
         */
        fun calculateLevelProgress(totalXp: Int): Float {
            val currentLevel = calculateLevel(totalXp)
            if (currentLevel >= MAX_LEVEL) return 1f

            val currentThreshold = LEVEL_THRESHOLDS.getOrElse(currentLevel - 1) { 0 }
            val nextThreshold = LEVEL_THRESHOLDS.getOrElse(currentLevel) { currentThreshold + 50 }
            val xpInLevel = totalXp - currentThreshold
            val xpNeeded = nextThreshold - currentThreshold

            return if (xpNeeded > 0) {
                (xpInLevel.toFloat() / xpNeeded).coerceIn(0f, 1f)
            } else 1f
        }

        /**
         * Get XP remaining until next level.
         */
        fun getXpUntilNextLevel(totalXp: Int): Int {
            val currentLevel = calculateLevel(totalXp)
            if (currentLevel >= MAX_LEVEL) return 0
            val nextThreshold = LEVEL_THRESHOLDS.getOrElse(currentLevel) { LEVEL_THRESHOLDS.last() }
            return (nextThreshold - totalXp).coerceAtLeast(0)
        }
    }
}

/**
 * Represents a user's progress in a single skill.
 */
data class SkillProgress(
    val skill: Skill,
    val level: Int,
    val currentPoints: Int,
    val pointsForNextLevel: Int,
    val totalPointsEarned: Int,
    val progressPercent: Float,
    val isMastered: Boolean
) {
    companion object {
        fun fromTotalXp(skill: Skill, totalXp: Int): SkillProgress {
            val level = Skill.calculateLevel(totalXp)
            val currentThreshold = Skill.getXpThreshold(level)
            val pointsInCurrentLevel = totalXp - currentThreshold
            val pointsForNext = if (level >= Skill.MAX_LEVEL) 0 else {
                Skill.getXpForNextLevel(level) - currentThreshold
            }

            return SkillProgress(
                skill = skill,
                level = level,
                currentPoints = pointsInCurrentLevel,
                pointsForNextLevel = pointsForNext,
                totalPointsEarned = totalXp,
                progressPercent = Skill.calculateLevelProgress(totalXp),
                isMastered = level >= Skill.MAX_LEVEL
            )
        }
    }
}

/**
 * Combined player skills for UI display.
 */
data class PlayerSkillsState(
    val clarity: SkillProgress,
    val discipline: SkillProgress,
    val courage: SkillProgress,
    val combinedLevel: Int,
    val tokens: Int
) {
    /**
     * Get skill progress by type.
     */
    fun getProgress(skill: Skill): SkillProgress = when (skill) {
        Skill.CLARITY -> clarity
        Skill.DISCIPLINE -> discipline
        Skill.COURAGE -> courage
    }

    /**
     * Check if any skill has reached mastery.
     */
    val hasMastery: Boolean
        get() = clarity.isMastered || discipline.isMastered || courage.isMastered

    /**
     * Count of mastered skills.
     */
    val masteryCount: Int
        get() = listOf(clarity, discipline, courage).count { it.isMastered }

    /**
     * Check if all skills are mastered.
     */
    val isFullyMastered: Boolean
        get() = clarity.isMastered && discipline.isMastered && courage.isMastered

    companion object {
        fun empty(): PlayerSkillsState = PlayerSkillsState(
            clarity = SkillProgress.fromTotalXp(Skill.CLARITY, 0),
            discipline = SkillProgress.fromTotalXp(Skill.DISCIPLINE, 0),
            courage = SkillProgress.fromTotalXp(Skill.COURAGE, 0),
            combinedLevel = 3,
            tokens = 0
        )
    }
}
