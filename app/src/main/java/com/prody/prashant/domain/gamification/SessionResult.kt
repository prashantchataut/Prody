package com.prody.prashant.domain.gamification

/**
 * Session Result - Clean feedback after completing any game mode.
 *
 * This replaces spam toasts with a single, comprehensive summary showing:
 * - What you did
 * - What you gained
 * - What unlocked (if any)
 * - Next suggested move
 *
 * This is how games feel satisfying.
 */
data class SessionResult(
    val sessionType: SessionType,
    val summary: SessionSummary,
    val rewards: SessionRewards,
    val unlocks: List<SessionUnlock> = emptyList(),
    val nextSuggestion: NextSuggestion? = null,
    val missionProgress: MissionProgress? = null,
    val seedBloom: SeedBloomInfo? = null
) {
    /**
     * Check if this session resulted in any meaningful gains.
     */
    fun hasRewards(): Boolean = rewards.totalXp > 0 || rewards.tokens > 0 || unlocks.isNotEmpty()

    /**
     * Check if user leveled up any skill.
     */
    fun hasLevelUp(): Boolean = rewards.skillLevelUps.isNotEmpty()
}

/**
 * The type of game session completed.
 */
enum class SessionType(val displayName: String, val verb: String) {
    REFLECT("Reflect", "reflected"),
    SHARPEN("Sharpen", "sharpened"),
    COMMIT("Commit", "committed")
}

/**
 * Summary of what the user did in the session.
 */
data class SessionSummary(
    val headline: String, // e.g., "Journal entry saved"
    val details: List<String> = emptyList() // e.g., ["127 words", "Mood: Calm"]
)

/**
 * Rewards earned from the session.
 */
data class SessionRewards(
    val skillXpGains: Map<GameSkillSystem.SkillType, Int> = emptyMap(),
    val tokens: Int = 0,
    val streakBonus: Int = 0,
    val skillLevelUps: List<SkillLevelUp> = emptyList()
) {
    val totalXp: Int
        get() = skillXpGains.values.sum() + streakBonus
}

/**
 * Skill level up information.
 */
data class SkillLevelUp(
    val skill: GameSkillSystem.SkillType,
    val previousLevel: Int,
    val newLevel: Int
)

/**
 * Something unlocked during the session.
 */
data class SessionUnlock(
    val type: UnlockType,
    val id: String,
    val name: String,
    val description: String? = null
)

enum class UnlockType {
    ACHIEVEMENT,
    BANNER,
    TITLE,
    FRAME,
    BADGE_SLOT
}

/**
 * Suggested next action.
 */
data class NextSuggestion(
    val type: SuggestionType,
    val title: String,
    val reason: String? = null
)

enum class SuggestionType {
    COMPLETE_DAILY_MISSION,
    TRY_DIFFERENT_MODE,
    BLOOM_SEED,
    CHECK_WEEKLY_TRIAL,
    VIEW_PROGRESS
}

/**
 * Mission progress update from this session.
 */
data class MissionProgress(
    val missionTitle: String,
    val previousProgress: Int,
    val newProgress: Int,
    val targetValue: Int,
    val justCompleted: Boolean
)

/**
 * Seed bloom information if a seed was bloomed.
 */
data class SeedBloomInfo(
    val seedContent: String,
    val bonusTokens: Int
)

/**
 * Builder for creating session results.
 */
class SessionResultBuilder(private val sessionType: SessionType) {
    private var headline: String = ""
    private var details: MutableList<String> = mutableListOf()
    private var skillXpGains: MutableMap<GameSkillSystem.SkillType, Int> = mutableMapOf()
    private var tokens: Int = 0
    private var streakBonus: Int = 0
    private var levelUps: MutableList<SkillLevelUp> = mutableListOf()
    private var unlocks: MutableList<SessionUnlock> = mutableListOf()
    private var nextSuggestion: NextSuggestion? = null
    private var missionProgress: MissionProgress? = null
    private var seedBloom: SeedBloomInfo? = null

    fun headline(headline: String) = apply { this.headline = headline }
    fun addDetail(detail: String) = apply { this.details.add(detail) }
    fun addSkillXp(skill: GameSkillSystem.SkillType, amount: Int) = apply {
        skillXpGains[skill] = (skillXpGains[skill] ?: 0) + amount
    }
    fun tokens(amount: Int) = apply { this.tokens = amount }
    fun streakBonus(amount: Int) = apply { this.streakBonus = amount }
    fun addLevelUp(levelUp: SkillLevelUp) = apply { this.levelUps.add(levelUp) }
    fun addUnlock(unlock: SessionUnlock) = apply { this.unlocks.add(unlock) }
    fun nextSuggestion(suggestion: NextSuggestion?) = apply { this.nextSuggestion = suggestion }
    fun missionProgress(progress: MissionProgress?) = apply { this.missionProgress = progress }
    fun seedBloom(bloom: SeedBloomInfo?) = apply { this.seedBloom = bloom }

    fun build(): SessionResult = SessionResult(
        sessionType = sessionType,
        summary = SessionSummary(headline, details),
        rewards = SessionRewards(skillXpGains, tokens, streakBonus, levelUps),
        unlocks = unlocks,
        nextSuggestion = nextSuggestion,
        missionProgress = missionProgress,
        seedBloom = seedBloom
    )
}
