package com.prody.prashant.domain.gamification

import android.util.Log
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.PlayerSkillsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Game Skill System - The "Character Build" for Prody
 *
 * Players have 3 growth stats that level separately:
 * - CLARITY (Reflect): Journaling depth/consistency
 * - DISCIPLINE (Sharpen): Flashcards/vocabulary consistency
 * - COURAGE (Commit): Future messages + confronting themes
 *
 * Each stat has its own XP pool and level curve.
 * Overall Level = sum of skill levels.
 *
 * This replaces the single "XP blob" with meaningful progression.
 */
@Singleton
class GameSkillSystem @Inject constructor(
    private val userDao: UserDao
) {
    companion object {
        private const val TAG = "GameSkillSystem"

        // XP required for each level (cumulative)
        // Level 1: 0 XP, Level 2: 100 XP, Level 3: 250 XP, etc.
        private val LEVEL_THRESHOLDS = listOf(
            0, 100, 250, 450, 700, 1000, 1400, 1900, 2500, 3200,
            4000, 5000, 6200, 7600, 9200, 11000, 13000, 15500, 18500, 22000,
            26000, 30500, 35500, 41000, 47000, 54000, 62000, 71000, 81000, 92000
        )

        const val MAX_SKILL_LEVEL = 30

        // Daily XP caps per skill type (anti-exploit)
        const val DAILY_CLARITY_CAP = 300
        const val DAILY_DISCIPLINE_CAP = 300
        const val DAILY_COURAGE_CAP = 200
    }

    /**
     * Skill types that map to the 3 core game modes.
     */
    enum class SkillType(val displayName: String, val verb: String) {
        CLARITY("Clarity", "Reflect"),
        DISCIPLINE("Discipline", "Sharpen"),
        COURAGE("Courage", "Commit")
    }

    /**
     * Award skill XP for an action. Returns the XP actually awarded (may be capped).
     */
    suspend fun awardSkillXp(
        skillType: SkillType,
        baseXp: Int,
        idempotencyKey: String? = null
    ): SkillXpResult {
        return try {
            // Idempotency check - prevent double awarding
            if (idempotencyKey != null) {
                val alreadyAwarded = userDao.hasProcessedRewardKey(idempotencyKey)
                if (alreadyAwarded) {
                    Log.d(TAG, "Reward already processed for key: $idempotencyKey")
                    return SkillXpResult.AlreadyAwarded
                }
            }

            val skills = getOrCreateSkills()
            val dailyXp = getDailySkillXp(skillType)
            val dailyCap = getDailyCap(skillType)

            // Check daily cap
            val remainingCap = (dailyCap - dailyXp).coerceAtLeast(0)
            val actualXp = baseXp.coerceAtMost(remainingCap)

            if (actualXp <= 0) {
                Log.d(TAG, "Daily cap reached for $skillType")
                return SkillXpResult.DailyCapReached
            }

            // Award XP
            val previousXp = getSkillXp(skills, skillType)
            val previousLevel = calculateLevel(previousXp)
            val newXp = previousXp + actualXp
            val newLevel = calculateLevel(newXp)

            // Update database
            updateSkillXp(skillType, actualXp)
            updateDailySkillXp(skillType, actualXp)

            // Mark idempotency key as processed
            if (idempotencyKey != null) {
                userDao.markRewardKeyProcessed(idempotencyKey)
            }

            val leveledUp = newLevel > previousLevel

            Log.d(TAG, "Awarded $actualXp $skillType XP (base: $baseXp, capped: ${actualXp != baseXp})")

            SkillXpResult.Success(
                skillType = skillType,
                xpAwarded = actualXp,
                newTotalXp = newXp,
                newLevel = newLevel,
                leveledUp = leveledUp,
                previousLevel = previousLevel
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error awarding skill XP", e)
            SkillXpResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Get current player skills.
     */
    suspend fun getPlayerSkills(): PlayerSkills {
        val skills = getOrCreateSkills()
        return PlayerSkills(
            clarityXp = skills.clarityXp,
            clarityLevel = calculateLevel(skills.clarityXp),
            disciplineXp = skills.disciplineXp,
            disciplineLevel = calculateLevel(skills.disciplineXp),
            courageXp = skills.courageXp,
            courageLevel = calculateLevel(skills.courageXp),
            overallLevel = calculateLevel(skills.clarityXp) +
                    calculateLevel(skills.disciplineXp) +
                    calculateLevel(skills.courageXp)
        )
    }

    /**
     * Observe player skills reactively.
     */
    fun observePlayerSkills(): Flow<PlayerSkills> {
        return userDao.observePlayerSkills().map { entity ->
            val skills = entity ?: PlayerSkillsEntity()
            PlayerSkills(
                clarityXp = skills.clarityXp,
                clarityLevel = calculateLevel(skills.clarityXp),
                disciplineXp = skills.disciplineXp,
                disciplineLevel = calculateLevel(skills.disciplineXp),
                courageXp = skills.courageXp,
                courageLevel = calculateLevel(skills.courageXp),
                overallLevel = calculateLevel(skills.clarityXp) +
                        calculateLevel(skills.disciplineXp) +
                        calculateLevel(skills.courageXp)
            )
        }
    }

    /**
     * Calculate level from XP amount.
     */
    fun calculateLevel(xp: Int): Int {
        for (i in LEVEL_THRESHOLDS.indices.reversed()) {
            if (xp >= LEVEL_THRESHOLDS[i]) {
                return (i + 1).coerceAtMost(MAX_SKILL_LEVEL)
            }
        }
        return 1
    }

    /**
     * Get XP required for next level.
     */
    fun getXpForNextLevel(currentXp: Int): Int {
        val currentLevel = calculateLevel(currentXp)
        if (currentLevel >= MAX_SKILL_LEVEL) return 0
        return LEVEL_THRESHOLDS.getOrElse(currentLevel) { LEVEL_THRESHOLDS.last() }
    }

    /**
     * Get progress percentage toward next level.
     */
    fun getLevelProgress(currentXp: Int): Float {
        val currentLevel = calculateLevel(currentXp)
        if (currentLevel >= MAX_SKILL_LEVEL) return 1f

        val currentThreshold = LEVEL_THRESHOLDS.getOrElse(currentLevel - 1) { 0 }
        val nextThreshold = LEVEL_THRESHOLDS.getOrElse(currentLevel) { currentThreshold + 100 }
        val xpInLevel = currentXp - currentThreshold
        val xpNeeded = nextThreshold - currentThreshold

        return if (xpNeeded > 0) (xpInLevel.toFloat() / xpNeeded).coerceIn(0f, 1f) else 1f
    }

    /**
     * Reset daily caps (call at start of day).
     */
    suspend fun resetDailyCaps() {
        userDao.resetDailySkillXp()
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    private suspend fun getOrCreateSkills(): PlayerSkillsEntity {
        return userDao.getPlayerSkillsSync() ?: run {
            val newSkills = PlayerSkillsEntity()
            userDao.insertPlayerSkills(newSkills)
            newSkills
        }
    }

    private fun getSkillXp(skills: PlayerSkillsEntity, type: SkillType): Int {
        return when (type) {
            SkillType.CLARITY -> skills.clarityXp
            SkillType.DISCIPLINE -> skills.disciplineXp
            SkillType.COURAGE -> skills.courageXp
        }
    }

    private suspend fun getDailySkillXp(type: SkillType): Int {
        val skills = getOrCreateSkills()
        return when (type) {
            SkillType.CLARITY -> skills.dailyClarityXp
            SkillType.DISCIPLINE -> skills.dailyDisciplineXp
            SkillType.COURAGE -> skills.dailyCourageXp
        }
    }

    private fun getDailyCap(type: SkillType): Int {
        return when (type) {
            SkillType.CLARITY -> DAILY_CLARITY_CAP
            SkillType.DISCIPLINE -> DAILY_DISCIPLINE_CAP
            SkillType.COURAGE -> DAILY_COURAGE_CAP
        }
    }

    private suspend fun updateSkillXp(type: SkillType, amount: Int) {
        when (type) {
            SkillType.CLARITY -> userDao.addClarityXp(amount)
            SkillType.DISCIPLINE -> userDao.addDisciplineXp(amount)
            SkillType.COURAGE -> userDao.addCourageXp(amount)
        }
    }

    private suspend fun updateDailySkillXp(type: SkillType, amount: Int) {
        when (type) {
            SkillType.CLARITY -> userDao.addDailyClarityXp(amount)
            SkillType.DISCIPLINE -> userDao.addDailyDisciplineXp(amount)
            SkillType.COURAGE -> userDao.addDailyCourageXp(amount)
        }
    }
}

/**
 * Player skills data class for UI consumption.
 */
data class PlayerSkills(
    val clarityXp: Int = 0,
    val clarityLevel: Int = 1,
    val disciplineXp: Int = 0,
    val disciplineLevel: Int = 1,
    val courageXp: Int = 0,
    val courageLevel: Int = 1,
    val overallLevel: Int = 3
) {
    fun getSkillXp(type: GameSkillSystem.SkillType): Int = when (type) {
        GameSkillSystem.SkillType.CLARITY -> clarityXp
        GameSkillSystem.SkillType.DISCIPLINE -> disciplineXp
        GameSkillSystem.SkillType.COURAGE -> courageXp
    }

    fun getSkillLevel(type: GameSkillSystem.SkillType): Int = when (type) {
        GameSkillSystem.SkillType.CLARITY -> clarityLevel
        GameSkillSystem.SkillType.DISCIPLINE -> disciplineLevel
        GameSkillSystem.SkillType.COURAGE -> courageLevel
    }
}

/**
 * Result of awarding skill XP.
 */
sealed class SkillXpResult {
    data class Success(
        val skillType: GameSkillSystem.SkillType,
        val xpAwarded: Int,
        val newTotalXp: Int,
        val newLevel: Int,
        val leveledUp: Boolean,
        val previousLevel: Int
    ) : SkillXpResult()

    object AlreadyAwarded : SkillXpResult()
    object DailyCapReached : SkillXpResult()
    data class Error(val message: String) : SkillXpResult()
}
