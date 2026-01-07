package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.DailyMissionEntity
import com.prody.prashant.data.local.entity.PlayerSkillsEntity
import com.prody.prashant.data.local.entity.WeeklyTrialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {

    // =========================================================================
    // DAILY MISSIONS
    // =========================================================================

    @Query("SELECT * FROM daily_missions WHERE date = :date ORDER BY missionType")
    fun getMissionsForDate(date: Long): Flow<List<DailyMissionEntity>>

    @Query("SELECT * FROM daily_missions WHERE date = :date ORDER BY missionType")
    suspend fun getMissionsForDateSync(date: Long): List<DailyMissionEntity>

    @Query("SELECT * FROM daily_missions WHERE id = :id")
    suspend fun getMissionById(id: Long): DailyMissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: DailyMissionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<DailyMissionEntity>)

    @Query("""
        UPDATE daily_missions
        SET currentProgress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt
        WHERE id = :missionId
    """)
    suspend fun updateMissionProgress(
        missionId: Long,
        progress: Int,
        isCompleted: Boolean,
        completedAt: Long?
    )

    @Query("UPDATE daily_missions SET rewardClaimed = 1 WHERE id = :missionId")
    suspend fun markMissionRewardClaimed(missionId: Long)

    @Query("SELECT COUNT(*) FROM daily_missions WHERE date = :date AND isCompleted = 1")
    fun getCompletedMissionCountForDate(date: Long): Flow<Int>

    @Query("SELECT * FROM daily_missions WHERE isCompleted = 1 ORDER BY completedAt DESC LIMIT :limit")
    fun getRecentCompletedMissions(limit: Int): Flow<List<DailyMissionEntity>>

    @Query("DELETE FROM daily_missions WHERE date < :cutoffDate")
    suspend fun deleteOldMissions(cutoffDate: Long)

    // =========================================================================
    // WEEKLY TRIALS
    // =========================================================================

    @Query("SELECT * FROM weekly_trials WHERE weekStart = :weekStart LIMIT 1")
    suspend fun getTrialForWeek(weekStart: Long): WeeklyTrialEntity?

    @Query("SELECT * FROM weekly_trials WHERE weekStart = :weekStart LIMIT 1")
    fun observeTrialForWeek(weekStart: Long): Flow<WeeklyTrialEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrial(trial: WeeklyTrialEntity): Long

    @Query("""
        UPDATE weekly_trials
        SET currentProgress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt
        WHERE id = :trialId
    """)
    suspend fun updateTrialProgress(
        trialId: Long,
        progress: Int,
        isCompleted: Boolean,
        completedAt: Long?
    )

    @Query("UPDATE weekly_trials SET rewardClaimed = 1 WHERE id = :trialId")
    suspend fun markTrialRewardClaimed(trialId: Long)

    @Query("SELECT * FROM weekly_trials WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTrials(): Flow<List<WeeklyTrialEntity>>

    @Query("DELETE FROM weekly_trials WHERE weekEnd < :cutoffDate")
    suspend fun deleteOldTrials(cutoffDate: Long)

    // =========================================================================
    // TOKENS (from PlayerSkillsEntity)
    // =========================================================================

    @Query("UPDATE player_skills SET tokens = tokens + :amount WHERE id = 1")
    suspend fun addTokens(amount: Int)

    @Query("SELECT tokens FROM player_skills WHERE id = 1")
    fun getTokens(): Flow<Int?>

    @Query("SELECT tokens FROM player_skills WHERE id = 1")
    suspend fun getTokensSync(): Int?

    @Query("UPDATE player_skills SET tokens = tokens - :amount WHERE id = 1 AND tokens >= :amount")
    suspend fun spendTokens(amount: Int): Int // Returns rows affected (0 if insufficient)
}
