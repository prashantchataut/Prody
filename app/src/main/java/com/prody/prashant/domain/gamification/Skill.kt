package com.prody.prashant.domain.gamification

import androidx.compose.ui.graphics.Color

/**
 * The three core skills that map to app activities.
 *
 * Clarity (Journal-focused): The depth of your reflection
 * - XP from journal word count (diminishing returns after 500 words)
 * - XP from using vocabulary words
 * - XP from deep dive completions
 * - Bonus for entries with questions (self-reflection)
 * - Bonus for pattern identification by Buddha
 *
 * Discipline (Wisdom-focused): Your commitment to learning
 * - XP from daily wisdom engagement
 * - XP from vocabulary usage (Bloom mechanic)
 * - XP from learning path progress
 * - XP from maintaining streaks (not losing them)
 * - Bonus for completing weekly challenges
 *
 * Courage (Future-focused): Your willingness to face the future
 * - XP from creating future messages
 * - Bonus XP for longer delivery times (harder commitment)
 * - XP from opening delivered messages
 * - XP from achieving goals set in messages
 * - XP from collaborative messages
 *
 * Each skill has independent progression with a max level of 20.
 * Level 20 = True Mastery, earning a legendary badge.
 */
enum class Skill(
    val displayName: String,
    val description: String,
    val color: Color,
    val secondaryColor: Color,
    val iconName: String,
    val maxLevel: Int = MAX_LEVEL
) {
    CLARITY(
        displayName = "Clarity",
        description = "The depth of your reflection",
        color = Color(0xFF4A90D9),
        secondaryColor = Color(0xFF7EB8FF),
        iconName = "ic_clarity"
    ),
    DISCIPLINE(
        displayName = "Discipline",
        description = "Your commitment to learning",
        color = Color(0xFF7B68EE),
        secondaryColor = Color(0xFFA594FF),
        iconName = "ic_discipline"
    ),
    COURAGE(
        displayName = "Courage",
        description = "Your willingness to face the future",
        color = Color(0xFFE57373),
        secondaryColor = Color(0xFFFF9E9E),
        iconName = "ic_courage"
    );

    companion object {
        /**
         * XP thresholds for each level (cumulative).
         * Level 1 starts at 0 XP, Level 20 (True Mastery) requires 15,300 total XP.
         *
         * The curve is exponential to make early progress feel quick
         * while true mastery requires sustained effort over time.
         *
         * Approximate time to reach each milestone:
         * - Level 5: ~1 week of consistent use
         * - Level 10: ~1 month of consistent use
         * - Level 15: ~3 months of consistent use
         * - Level 20: ~6 months of dedicated use
         */
        val LEVEL_THRESHOLDS = listOf(
            0,       // Level 1  - Starting point
            50,      // Level 2  - Getting started
            120,     // Level 3  - Building momentum
            220,     // Level 4  - Finding rhythm
            360,     // Level 5  - First milestone (unlocks: Advanced templates)
            550,     // Level 6  - Developing habits
            800,     // Level 7  - Growing stronger
            1150,    // Level 8  - Gaining mastery
            1600,    // Level 9  - Nearly there
            2200,    // Level 10 - Mastery I (unlocks: Weekly insight summaries)
            2900,    // Level 11 - Beyond mastery
            3700,    // Level 12 - Expert
            4600,    // Level 13 - Advanced expert
            5600,    // Level 14 - Nearly legendary
            6700,    // Level 15 - Legendary I (unlocks: Premium features)
            8000,    // Level 16 - Transcendent
            9500,    // Level 17 - Enlightened
            11200,   // Level 18 - Awakened
            13100,   // Level 19 - Nearly perfect
            15300    // Level 20 - True Mastery (unlocks: Ultimate badge + banner)
        )

        const val MAX_LEVEL = 20

        /**
         * Title progression for each skill at each level.
         */
        fun getTitleForLevel(skill: Skill, level: Int): String {
            return when (skill) {
                CLARITY -> when {
                    level >= 20 -> "Master of Clarity"
                    level >= 15 -> "Enlightened Thinker"
                    level >= 10 -> "Deep Reflector"
                    level >= 5 -> "Thoughtful Writer"
                    else -> "Novice Thinker"
                }
                DISCIPLINE -> when {
                    level >= 20 -> "Master of Discipline"
                    level >= 15 -> "Wisdom Keeper"
                    level >= 10 -> "Dedicated Learner"
                    level >= 5 -> "Curious Mind"
                    else -> "Novice Learner"
                }
                COURAGE -> when {
                    level >= 20 -> "Master of Courage"
                    level >= 15 -> "Time Traveler"
                    level >= 10 -> "Future Builder"
                    level >= 5 -> "Bold Explorer"
                    else -> "Novice Explorer"
                }
            }
        }

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

        /**
         * Get all perks for a skill up to the current level.
         */
        fun getUnlockedPerks(skill: Skill, level: Int): List<SkillPerk> {
            return SkillPerks.getPerksForSkill(skill).filter { it.unlockLevel <= level }
        }

        /**
         * Get the next perk to unlock for a skill.
         */
        fun getNextPerk(skill: Skill, level: Int): SkillPerk? {
            return SkillPerks.getPerksForSkill(skill)
                .filter { it.unlockLevel > level }
                .minByOrNull { it.unlockLevel }
        }

        /**
         * Check if a perk is unlocked at a given level.
         */
        fun isPerkUnlocked(perk: SkillPerk, currentLevel: Int): Boolean {
            return currentLevel >= perk.unlockLevel
        }
    }
}

/**
 * A perk that is unlocked at a specific skill level.
 * Perks provide tangible benefits and unlock features.
 */
data class SkillPerk(
    val id: String,
    val name: String,
    val description: String,
    val skill: Skill,
    val unlockLevel: Int,
    val iconName: String,
    val benefit: PerkBenefit
)

/**
 * Types of benefits that perks can provide.
 */
sealed class PerkBenefit {
    /** Unlocks a new feature */
    data class FeatureUnlock(val featureId: String) : PerkBenefit()

    /** Provides an XP multiplier */
    data class XpMultiplier(val multiplier: Float) : PerkBenefit()

    /** Provides bonus XP per action */
    data class BonusXp(val amount: Int) : PerkBenefit()

    /** Unlocks a cosmetic item (banner, avatar, title) */
    data class CosmeticUnlock(val cosmeticId: String, val type: CosmeticType) : PerkBenefit()

    /** Provides enhanced AI responses */
    data class EnhancedAI(val enhancementType: String) : PerkBenefit()

    /** Unlocks advanced templates */
    data class TemplateUnlock(val templateIds: List<String>) : PerkBenefit()

    /** Grants freeze tokens for streaks */
    data class FreezeTokenGrant(val tokens: Int) : PerkBenefit()
}

enum class CosmeticType {
    BANNER, AVATAR, TITLE, FRAME
}

/**
 * All perks organized by skill.
 */
object SkillPerks {

    // =========================================================================
    // CLARITY PERKS (Journaling Depth)
    // =========================================================================

    private val clarityPerks = listOf(
        SkillPerk(
            id = "clarity_l2_insight",
            name = "First Insight",
            description = "Buddha responds with more personalized insights",
            skill = Skill.CLARITY,
            unlockLevel = 2,
            iconName = "ic_perk_insight",
            benefit = PerkBenefit.EnhancedAI("personalized_insights")
        ),
        SkillPerk(
            id = "clarity_l3_mood",
            name = "Mood Tracker",
            description = "Unlock advanced mood tracking and patterns",
            skill = Skill.CLARITY,
            unlockLevel = 3,
            iconName = "ic_perk_mood",
            benefit = PerkBenefit.FeatureUnlock("advanced_mood_tracking")
        ),
        SkillPerk(
            id = "clarity_l5_templates",
            name = "Reflective Templates",
            description = "Access 10 advanced journaling templates",
            skill = Skill.CLARITY,
            unlockLevel = 5,
            iconName = "ic_perk_templates",
            benefit = PerkBenefit.TemplateUnlock(listOf(
                "gratitude_deep", "stoic_reflection", "future_letter",
                "emotional_inventory", "weekly_review", "monthly_retrospective",
                "relationship_check", "career_reflection", "creative_prompt",
                "philosophical_inquiry"
            ))
        ),
        SkillPerk(
            id = "clarity_l7_xp_boost",
            name = "Thoughtful Writer",
            description = "+10% XP from all journal entries",
            skill = Skill.CLARITY,
            unlockLevel = 7,
            iconName = "ic_perk_xp_boost",
            benefit = PerkBenefit.XpMultiplier(1.10f)
        ),
        SkillPerk(
            id = "clarity_l10_weekly",
            name = "Weekly Wisdom",
            description = "Buddha provides weekly insight summaries",
            skill = Skill.CLARITY,
            unlockLevel = 10,
            iconName = "ic_perk_weekly",
            benefit = PerkBenefit.FeatureUnlock("weekly_insights")
        ),
        SkillPerk(
            id = "clarity_l12_patterns",
            name = "Pattern Recognition",
            description = "AI identifies emotional patterns in your writing",
            skill = Skill.CLARITY,
            unlockLevel = 12,
            iconName = "ic_perk_patterns",
            benefit = PerkBenefit.EnhancedAI("pattern_recognition")
        ),
        SkillPerk(
            id = "clarity_l15_premium",
            name = "Deep Diver",
            description = "Access premium deep dive topics",
            skill = Skill.CLARITY,
            unlockLevel = 15,
            iconName = "ic_perk_deep_dive",
            benefit = PerkBenefit.FeatureUnlock("premium_deep_dives")
        ),
        SkillPerk(
            id = "clarity_l17_xp_major",
            name = "Reflective Soul",
            description = "+20% XP from all journal entries",
            skill = Skill.CLARITY,
            unlockLevel = 17,
            iconName = "ic_perk_xp_major",
            benefit = PerkBenefit.XpMultiplier(1.20f)
        ),
        SkillPerk(
            id = "clarity_l20_master",
            name = "Master Writer",
            description = "Exclusive \"Master of Clarity\" badge + banner",
            skill = Skill.CLARITY,
            unlockLevel = 20,
            iconName = "ic_perk_master_clarity",
            benefit = PerkBenefit.CosmeticUnlock("clarity_master", CosmeticType.BANNER)
        )
    )

    // =========================================================================
    // DISCIPLINE PERKS (Consistency & Learning)
    // =========================================================================

    private val disciplinePerks = listOf(
        SkillPerk(
            id = "discipline_l2_reminder",
            name = "Gentle Reminder",
            description = "Personalized notification messages",
            skill = Skill.DISCIPLINE,
            unlockLevel = 2,
            iconName = "ic_perk_reminder",
            benefit = PerkBenefit.FeatureUnlock("custom_notifications")
        ),
        SkillPerk(
            id = "discipline_l3_vocab",
            name = "Word Collector",
            description = "Track vocabulary usage across entries",
            skill = Skill.DISCIPLINE,
            unlockLevel = 3,
            iconName = "ic_perk_vocab",
            benefit = PerkBenefit.FeatureUnlock("vocabulary_tracking")
        ),
        SkillPerk(
            id = "discipline_l5_streak_shield",
            name = "Streak Shield",
            description = "Earn 1 freeze token to protect your streak",
            skill = Skill.DISCIPLINE,
            unlockLevel = 5,
            iconName = "ic_perk_shield",
            benefit = PerkBenefit.FreezeTokenGrant(1)
        ),
        SkillPerk(
            id = "discipline_l7_srs",
            name = "Memory Master",
            description = "Advanced spaced repetition for vocabulary",
            skill = Skill.DISCIPLINE,
            unlockLevel = 7,
            iconName = "ic_perk_srs",
            benefit = PerkBenefit.FeatureUnlock("advanced_srs")
        ),
        SkillPerk(
            id = "discipline_l10_vocab_advanced",
            name = "Lexicon Builder",
            description = "Create custom vocabulary lists",
            skill = Skill.DISCIPLINE,
            unlockLevel = 10,
            iconName = "ic_perk_lexicon",
            benefit = PerkBenefit.FeatureUnlock("custom_vocabulary_lists")
        ),
        SkillPerk(
            id = "discipline_l12_streak_shield_2",
            name = "Double Shield",
            description = "Earn another freeze token",
            skill = Skill.DISCIPLINE,
            unlockLevel = 12,
            iconName = "ic_perk_shield_2",
            benefit = PerkBenefit.FreezeTokenGrant(1)
        ),
        SkillPerk(
            id = "discipline_l15_learning",
            name = "Path Creator",
            description = "Create custom learning paths",
            skill = Skill.DISCIPLINE,
            unlockLevel = 15,
            iconName = "ic_perk_path",
            benefit = PerkBenefit.FeatureUnlock("custom_learning_paths")
        ),
        SkillPerk(
            id = "discipline_l17_xp_major",
            name = "Dedicated Mind",
            description = "+20% XP from wisdom activities",
            skill = Skill.DISCIPLINE,
            unlockLevel = 17,
            iconName = "ic_perk_xp_discipline",
            benefit = PerkBenefit.XpMultiplier(1.20f)
        ),
        SkillPerk(
            id = "discipline_l20_master",
            name = "Disciplined Mind",
            description = "Exclusive \"Master of Discipline\" badge + banner",
            skill = Skill.DISCIPLINE,
            unlockLevel = 20,
            iconName = "ic_perk_master_discipline",
            benefit = PerkBenefit.CosmeticUnlock("discipline_master", CosmeticType.BANNER)
        )
    )

    // =========================================================================
    // COURAGE PERKS (Future Self Commitment)
    // =========================================================================

    private val couragePerks = listOf(
        SkillPerk(
            id = "courage_l2_themes",
            name = "Time Capsule Themes",
            description = "Unlock 5 premium message themes",
            skill = Skill.COURAGE,
            unlockLevel = 2,
            iconName = "ic_perk_themes",
            benefit = PerkBenefit.FeatureUnlock("premium_message_themes")
        ),
        SkillPerk(
            id = "courage_l3_reminder",
            name = "Future Echo",
            description = "Set reminder notifications for future messages",
            skill = Skill.COURAGE,
            unlockLevel = 3,
            iconName = "ic_perk_echo",
            benefit = PerkBenefit.FeatureUnlock("message_reminders")
        ),
        SkillPerk(
            id = "courage_l5_xp_boost",
            name = "Bold Explorer",
            description = "+15% XP for messages sent 30+ days ahead",
            skill = Skill.COURAGE,
            unlockLevel = 5,
            iconName = "ic_perk_bold",
            benefit = PerkBenefit.XpMultiplier(1.15f)
        ),
        SkillPerk(
            id = "courage_l7_anniversary",
            name = "Anniversary Keeper",
            description = "Set recurring annual messages",
            skill = Skill.COURAGE,
            unlockLevel = 7,
            iconName = "ic_perk_anniversary",
            benefit = PerkBenefit.FeatureUnlock("annual_messages")
        ),
        SkillPerk(
            id = "courage_l10_collaborative",
            name = "Time Bridge",
            description = "Send collaborative messages to friends",
            skill = Skill.COURAGE,
            unlockLevel = 10,
            iconName = "ic_perk_bridge",
            benefit = PerkBenefit.FeatureUnlock("collaborative_messages")
        ),
        SkillPerk(
            id = "courage_l12_templates",
            name = "Temporal Templates",
            description = "Access 10 collaborative message templates",
            skill = Skill.COURAGE,
            unlockLevel = 12,
            iconName = "ic_perk_collab_templates",
            benefit = PerkBenefit.TemplateUnlock(listOf(
                "birthday_surprise", "graduation_message", "new_year_reflection",
                "anniversary_love", "milestone_celebration", "encouragement_boost",
                "gratitude_letter", "future_goals", "time_capsule_group",
                "legacy_message"
            ))
        ),
        SkillPerk(
            id = "courage_l15_vault",
            name = "Time Vault",
            description = "Create multi-part message series",
            skill = Skill.COURAGE,
            unlockLevel = 15,
            iconName = "ic_perk_vault",
            benefit = PerkBenefit.FeatureUnlock("message_series")
        ),
        SkillPerk(
            id = "courage_l17_xp_major",
            name = "Temporal Master",
            description = "+25% XP for all future messages",
            skill = Skill.COURAGE,
            unlockLevel = 17,
            iconName = "ic_perk_xp_courage",
            benefit = PerkBenefit.XpMultiplier(1.25f)
        ),
        SkillPerk(
            id = "courage_l20_master",
            name = "Time Traveler",
            description = "Exclusive \"Master of Courage\" badge + banner",
            skill = Skill.COURAGE,
            unlockLevel = 20,
            iconName = "ic_perk_master_courage",
            benefit = PerkBenefit.CosmeticUnlock("courage_master", CosmeticType.BANNER)
        )
    )

    /**
     * Get all perks for a specific skill.
     */
    fun getPerksForSkill(skill: Skill): List<SkillPerk> {
        return when (skill) {
            Skill.CLARITY -> clarityPerks
            Skill.DISCIPLINE -> disciplinePerks
            Skill.COURAGE -> couragePerks
        }
    }

    /**
     * Get all perks across all skills.
     */
    fun getAllPerks(): List<SkillPerk> {
        return clarityPerks + disciplinePerks + couragePerks
    }

    /**
     * Get a specific perk by ID.
     */
    fun getPerkById(id: String): SkillPerk? {
        return getAllPerks().find { it.id == id }
    }

    /**
     * Get perks that provide freeze tokens.
     */
    fun getFreezeTokenPerks(): List<SkillPerk> {
        return getAllPerks().filter { it.benefit is PerkBenefit.FreezeTokenGrant }
    }

    /**
     * Calculate total freeze tokens from perks at given levels.
     */
    fun calculateFreezeTokensFromPerks(clarityLevel: Int, disciplineLevel: Int, courageLevel: Int): Int {
        return getFreezeTokenPerks().sumOf { perk ->
            val currentLevel = when (perk.skill) {
                Skill.CLARITY -> clarityLevel
                Skill.DISCIPLINE -> disciplineLevel
                Skill.COURAGE -> courageLevel
            }
            if (currentLevel >= perk.unlockLevel && perk.benefit is PerkBenefit.FreezeTokenGrant) {
                perk.benefit.tokens
            } else {
                0
            }
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
    val isMastered: Boolean,
    val title: String,
    val unlockedPerks: List<SkillPerk>,
    val nextPerk: SkillPerk?
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
                isMastered = level >= Skill.MAX_LEVEL,
                title = Skill.getTitleForLevel(skill, level),
                unlockedPerks = Skill.getUnlockedPerks(skill, level),
                nextPerk = Skill.getNextPerk(skill, level)
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
    val tokens: Int,
    val freezeTokensFromPerks: Int
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

    /**
     * Get all unlocked perks across all skills.
     */
    val allUnlockedPerks: List<SkillPerk>
        get() = clarity.unlockedPerks + discipline.unlockedPerks + courage.unlockedPerks

    /**
     * Get XP multiplier from perks for a specific skill.
     */
    fun getXpMultiplier(skill: Skill): Float {
        val progress = getProgress(skill)
        return progress.unlockedPerks
            .mapNotNull { perk ->
                (perk.benefit as? PerkBenefit.XpMultiplier)?.multiplier
            }
            .maxOrNull() ?: 1.0f
    }

    /**
     * Check if a feature is unlocked through perks.
     */
    fun isFeatureUnlocked(featureId: String): Boolean {
        return allUnlockedPerks.any { perk ->
            val benefit = perk.benefit
            benefit is PerkBenefit.FeatureUnlock && benefit.featureId == featureId
        }
    }

    companion object {
        fun empty(): PlayerSkillsState = PlayerSkillsState(
            clarity = SkillProgress.fromTotalXp(Skill.CLARITY, 0),
            discipline = SkillProgress.fromTotalXp(Skill.DISCIPLINE, 0),
            courage = SkillProgress.fromTotalXp(Skill.COURAGE, 0),
            combinedLevel = 3,
            tokens = 0,
            freezeTokensFromPerks = 0
        )
    }
}

/**
 * Event triggered when a skill levels up.
 */
data class SkillLevelUpEvent(
    val skill: Skill,
    val previousLevel: Int,
    val newLevel: Int,
    val newTitle: String,
    val newPerks: List<SkillPerk>,
    val isNewMilestone: Boolean
) {
    val isMasteryReached: Boolean = newLevel >= Skill.MAX_LEVEL

    val milestoneType: MilestoneType? = when {
        newLevel == 5 -> MilestoneType.FIRST_MILESTONE
        newLevel == 10 -> MilestoneType.MASTERY_I
        newLevel == 15 -> MilestoneType.LEGENDARY
        newLevel == 20 -> MilestoneType.TRUE_MASTERY
        else -> null
    }
}

enum class MilestoneType(val displayName: String, val celebrationDuration: Int) {
    FIRST_MILESTONE("First Milestone", 2000),
    MASTERY_I("Mastery Achieved", 3000),
    LEGENDARY("Legendary Status", 4000),
    TRUE_MASTERY("True Mastery", 5000)
}

/**
 * Result of applying XP to a skill.
 */
sealed class XpGainResult {
    data class Success(
        val skill: Skill,
        val xpGained: Int,
        val previousTotal: Int,
        val newTotal: Int,
        val levelUpEvent: SkillLevelUpEvent?
    ) : XpGainResult()

    data class DailyCapReached(
        val skill: Skill,
        val xpApplied: Int,
        val xpCapped: Int
    ) : XpGainResult()

    data class Error(val message: String) : XpGainResult()
}
