package com.prody.prashant.domain.gamification

import android.util.Log
import com.prody.prashant.data.local.dao.MissionDao
import com.prody.prashant.data.local.entity.DailyMissionEntity
import com.prody.prashant.data.local.entity.WeeklyTrialEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Mission System - Daily and Weekly game missions (NOT habits).
 *
 * Daily Mission Board (3 slots):
 * - 1 Reflect mission (journaling)
 * - 1 Sharpen mission (vocabulary/flashcards)
 * - 1 Commit mission (future messages)
 *
 * Weekly Boss Trial:
 * - Bigger multi-day challenge
 * - Rare rewards (banners, titles, token packs)
 *
 * Missions auto-complete from user actions - no manual checkboxes.
 */
@Singleton
class MissionSystem @Inject constructor(
    private val missionDao: MissionDao,
    private val gameSkillSystem: GameSkillSystem
) {
    companion object {
        private const val TAG = "MissionSystem"

        // Mission reward values
        const val DAILY_MISSION_XP = 25
        const val DAILY_MISSION_TOKENS = 5
        const val WEEKLY_TRIAL_XP = 100
        const val WEEKLY_TRIAL_TOKENS = 25
    }

    // =========================================================================
    // DAILY MISSIONS
    // =========================================================================

    /**
     * Get today's missions. Creates them if they don't exist.
     */
    suspend fun getTodayMissions(): List<DailyMissionEntity> {
        val todayStart = getTodayStartTimestamp()

        // Check if we already have missions for today
        val existing = missionDao.getMissionsForDate(todayStart).firstOrNull()
        if (!existing.isNullOrEmpty()) {
            return existing
        }

        // Generate new missions for today
        return generateDailyMissions(todayStart)
    }

    /**
     * Observe today's missions reactively.
     */
    fun observeTodayMissions(): Flow<List<DailyMissionEntity>> {
        val todayStart = getTodayStartTimestamp()
        return missionDao.getMissionsForDate(todayStart)
    }

    /**
     * Record progress on a mission type. Auto-completes if target reached.
     */
    suspend fun recordProgress(
        missionType: MissionType,
        amount: Int
    ): MissionProgressResult {
        return try {
            val todayStart = getTodayStartTimestamp()
            val missions = getTodayMissions()
            val mission = missions.find { it.missionType == missionType.key }
                ?: return MissionProgressResult.NoMissionFound

            if (mission.isCompleted) {
                return MissionProgressResult.AlreadyCompleted
            }

            val newProgress = (mission.currentProgress + amount).coerceAtMost(mission.targetValue)
            val nowComplete = newProgress >= mission.targetValue

            missionDao.updateMissionProgress(
                missionId = mission.id,
                progress = newProgress,
                isCompleted = nowComplete,
                completedAt = if (nowComplete) System.currentTimeMillis() else null
            )

            // If completed, award rewards
            if (nowComplete && !mission.rewardClaimed) {
                awardMissionRewards(mission)
            }

            Log.d(TAG, "Mission progress: ${mission.title} - $newProgress/${mission.targetValue}")

            MissionProgressResult.Success(
                missionTitle = mission.title,
                previousProgress = mission.currentProgress,
                newProgress = newProgress,
                targetValue = mission.targetValue,
                justCompleted = nowComplete && !mission.isCompleted
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error recording mission progress", e)
            MissionProgressResult.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun awardMissionRewards(mission: DailyMissionEntity) {
        val skillType = when (mission.missionType) {
            "reflect" -> GameSkillSystem.SkillType.CLARITY
            "sharpen" -> GameSkillSystem.SkillType.DISCIPLINE
            "commit" -> GameSkillSystem.SkillType.COURAGE
            else -> return
        }

        gameSkillSystem.awardSkillXp(
            skillType = skillType,
            baseXp = mission.rewardXp,
            idempotencyKey = "mission_${mission.id}"
        )

        if (mission.rewardTokens > 0) {
            missionDao.addTokens(mission.rewardTokens)
        }

        missionDao.markMissionRewardClaimed(mission.id)
    }

    /**
     * Generate daily missions based on player level and history.
     */
    private suspend fun generateDailyMissions(todayStart: Long): List<DailyMissionEntity> {
        val skills = gameSkillSystem.getPlayerSkills()

        val missions = listOf(
            generateReflectMission(todayStart, skills.clarityLevel),
            generateSharpenMission(todayStart, skills.disciplineLevel),
            generateCommitMission(todayStart, skills.courageLevel)
        )

        missions.forEach { missionDao.insertMission(it) }
        return missions
    }

    private fun generateReflectMission(todayStart: Long, level: Int): DailyMissionEntity {
        val templates = listOf(
            MissionTemplate("Write at least %d words", "reflect_words", 80 + level * 10, "words"),
            MissionTemplate("Complete a journal entry", "reflect_entry", 1, "entries"),
            MissionTemplate("Reflect on your mood", "reflect_mood", 1, "entries")
        )

        val template = templates.random()
        val target = template.baseTarget

        return DailyMissionEntity(
            missionId = "daily_reflect_${todayStart}",
            date = todayStart,
            missionType = "reflect",
            title = template.title.format(target),
            description = "Reflect mode mission",
            targetValue = target,
            rewardXp = DAILY_MISSION_XP,
            rewardTokens = DAILY_MISSION_TOKENS,
            difficulty = if (level > 5) "hard" else "normal"
        )
    }

    private fun generateSharpenMission(todayStart: Long, level: Int): DailyMissionEntity {
        val templates = listOf(
            MissionTemplate("Review %d flashcards", "sharpen_cards", 5 + level * 2, "cards"),
            MissionTemplate("Learn %d new words", "sharpen_words", 2 + level, "words"),
            MissionTemplate("Complete a review session", "sharpen_session", 1, "sessions")
        )

        val template = templates.random()
        val target = template.baseTarget

        return DailyMissionEntity(
            missionId = "daily_sharpen_${todayStart}",
            date = todayStart,
            missionType = "sharpen",
            title = template.title.format(target),
            description = "Sharpen mode mission",
            targetValue = target,
            rewardXp = DAILY_MISSION_XP,
            rewardTokens = DAILY_MISSION_TOKENS,
            difficulty = if (level > 5) "hard" else "normal"
        )
    }

    private fun generateCommitMission(todayStart: Long, level: Int): DailyMissionEntity {
        val templates = listOf(
            MissionTemplate("Write a message to future you", "commit_message", 1, "messages"),
            MissionTemplate("Schedule a future reflection", "commit_schedule", 1, "messages")
        )

        val template = templates.random()

        return DailyMissionEntity(
            missionId = "daily_commit_${todayStart}",
            date = todayStart,
            missionType = "commit",
            title = template.title,
            description = "Commit mode mission",
            targetValue = template.baseTarget,
            rewardXp = DAILY_MISSION_XP,
            rewardTokens = DAILY_MISSION_TOKENS,
            difficulty = if (level > 3) "hard" else "normal"
        )
    }

    // =========================================================================
    // WEEKLY TRIALS
    // =========================================================================

    /**
     * Get current week's trial. Creates one if it doesn't exist.
     */
    suspend fun getWeeklyTrial(): WeeklyTrialEntity {
        val (weekStart, weekEnd) = getWeekBounds()

        // Check if we already have a trial for this week
        missionDao.getTrialForWeek(weekStart)?.let { return it }

        // Generate new trial for this week
        return generateWeeklyTrial(weekStart, weekEnd)
    }

    /**
     * Observe current weekly trial.
     */
    fun observeWeeklyTrial(): Flow<WeeklyTrialEntity?> {
        val (weekStart, _) = getWeekBounds()
        return missionDao.observeTrialForWeek(weekStart)
    }

    /**
     * Record progress on weekly trial.
     */
    suspend fun recordWeeklyTrialProgress(amount: Int): WeeklyTrialProgressResult {
        return try {
            val trial = getWeeklyTrial()

            if (trial.isCompleted) {
                return WeeklyTrialProgressResult.AlreadyCompleted
            }

            val newProgress = (trial.currentProgress + amount).coerceAtMost(trial.targetValue)
            val nowComplete = newProgress >= trial.targetValue

            missionDao.updateTrialProgress(
                trialId = trial.id,
                progress = newProgress,
                isCompleted = nowComplete,
                completedAt = if (nowComplete) System.currentTimeMillis() else null
            )

            if (nowComplete && !trial.rewardClaimed) {
                awardTrialRewards(trial)
            }

            WeeklyTrialProgressResult.Success(
                trialTitle = trial.title,
                newProgress = newProgress,
                targetValue = trial.targetValue,
                justCompleted = nowComplete && !trial.isCompleted
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error recording trial progress", e)
            WeeklyTrialProgressResult.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun awardTrialRewards(trial: WeeklyTrialEntity) {
        // Award XP split across skills
        gameSkillSystem.awardSkillXp(
            GameSkillSystem.SkillType.CLARITY,
            trial.rewardXp / 3,
            "trial_clarity_${trial.id}"
        )
        gameSkillSystem.awardSkillXp(
            GameSkillSystem.SkillType.DISCIPLINE,
            trial.rewardXp / 3,
            "trial_discipline_${trial.id}"
        )
        gameSkillSystem.awardSkillXp(
            GameSkillSystem.SkillType.COURAGE,
            trial.rewardXp / 3,
            "trial_courage_${trial.id}"
        )

        if (trial.rewardTokens > 0) {
            missionDao.addTokens(trial.rewardTokens)
        }

        missionDao.markTrialRewardClaimed(trial.id)
    }

    private suspend fun generateWeeklyTrial(weekStart: Long, weekEnd: Long): WeeklyTrialEntity {
        val templates = listOf(
            WeeklyTrialTemplate(
                "Versatile Mind",
                "Complete 4 sessions across at least 2 modes",
                "multi_mode", 4
            ),
            WeeklyTrialTemplate(
                "Bloom Garden",
                "Bloom 3 Seeds this week",
                "bloom", 3
            ),
            WeeklyTrialTemplate(
                "Steady Practice",
                "Complete missions on 5 different days",
                "streak", 5
            ),
            WeeklyTrialTemplate(
                "Deep Reflection",
                "Write 500 total words in journal entries",
                "words", 500
            )
        )

        val template = templates.random()

        val trial = WeeklyTrialEntity(
            trialId = "weekly_${weekStart}",
            weekStart = weekStart,
            weekEnd = weekEnd,
            title = template.title,
            description = template.description,
            trialType = template.type,
            targetValue = template.target,
            rewardXp = WEEKLY_TRIAL_XP,
            rewardTokens = WEEKLY_TRIAL_TOKENS
        )

        missionDao.insertTrial(trial)
        return trial
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private fun getTodayStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getWeekBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val weekEnd = calendar.timeInMillis

        return weekStart to weekEnd
    }

    /**
     * Mission type enum for progress tracking.
     */
    enum class MissionType(val key: String) {
        REFLECT("reflect"),
        SHARPEN("sharpen"),
        COMMIT("commit")
    }

    private data class MissionTemplate(
        val title: String,
        val key: String,
        val baseTarget: Int,
        val unit: String
    )

    private data class WeeklyTrialTemplate(
        val title: String,
        val description: String,
        val type: String,
        val target: Int
    )
}

/**
 * Result of recording mission progress.
 */
sealed class MissionProgressResult {
    data class Success(
        val missionTitle: String,
        val previousProgress: Int,
        val newProgress: Int,
        val targetValue: Int,
        val justCompleted: Boolean
    ) : MissionProgressResult()

    object NoMissionFound : MissionProgressResult()
    object AlreadyCompleted : MissionProgressResult()
    data class Error(val message: String) : MissionProgressResult()
}

/**
 * Result of recording weekly trial progress.
 */
sealed class WeeklyTrialProgressResult {
    data class Success(
        val trialTitle: String,
        val newProgress: Int,
        val targetValue: Int,
        val justCompleted: Boolean
    ) : WeeklyTrialProgressResult()

    object AlreadyCompleted : WeeklyTrialProgressResult()
    data class Error(val message: String) : WeeklyTrialProgressResult()
}
