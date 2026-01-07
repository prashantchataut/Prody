package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.ChallengeEntity
import com.prody.prashant.data.local.entity.ChallengeLeaderboardEntity
import com.prody.prashant.data.local.entity.ChallengeMilestoneEntity
import com.prody.prashant.data.local.entity.ChallengeParticipationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

    // Challenges
    @Query("SELECT * FROM challenges WHERE endDate >= :currentTime ORDER BY isFeatured DESC, startDate ASC")
    fun getActiveChallenges(currentTime: Long = System.currentTimeMillis()): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE isJoined = 1 AND endDate >= :currentTime ORDER BY startDate ASC")
    fun getJoinedChallenges(currentTime: Long = System.currentTimeMillis()): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE isJoined = 1 AND isCompleted = 0 AND endDate >= :currentTime AND (type = :challengeType OR type = 'mixed')")
    suspend fun getJoinedChallengesByTypeSync(challengeType: String, currentTime: Long = System.currentTimeMillis()): List<ChallengeEntity>

    @Query("SELECT * FROM challenges WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE isFeatured = 1 AND endDate >= :currentTime LIMIT 1")
    fun getFeaturedChallenge(currentTime: Long = System.currentTimeMillis()): Flow<ChallengeEntity?>

    @Query("SELECT * FROM challenges WHERE id = :challengeId")
    fun getChallengeById(challengeId: String): Flow<ChallengeEntity?>

    @Query("SELECT * FROM challenges WHERE id = :challengeId")
    suspend fun getChallengeByIdSync(challengeId: String): ChallengeEntity?

    @Query("SELECT * FROM challenges WHERE endDate < :currentTime AND isJoined = 1 ORDER BY endDate DESC")
    fun getPastChallenges(currentTime: Long = System.currentTimeMillis()): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Query("UPDATE challenges SET isJoined = 1, joinedAt = :joinedAt WHERE id = :challengeId")
    suspend fun joinChallenge(challengeId: String, joinedAt: Long = System.currentTimeMillis())

    @Query("UPDATE challenges SET currentUserProgress = currentUserProgress + :progress WHERE id = :challengeId")
    suspend fun incrementUserProgress(challengeId: String, progress: Int = 1)

    @Query("UPDATE challenges SET currentUserProgress = :progress WHERE id = :challengeId")
    suspend fun setUserProgress(challengeId: String, progress: Int)

    @Query("UPDATE challenges SET communityProgress = :progress, totalParticipants = :participants WHERE id = :challengeId")
    suspend fun updateCommunityProgress(challengeId: String, progress: Int, participants: Int)

    @Query("UPDATE challenges SET isCompleted = 1, completedAt = :completedAt WHERE id = :challengeId")
    suspend fun markChallengeCompleted(challengeId: String, completedAt: Long = System.currentTimeMillis())

    /**
     * Atomically increment user progress and check for completion.
     * Returns the new progress value.
     */
    @Transaction
    suspend fun incrementProgressAndCheckCompletion(
        challengeId: String,
        progressIncrement: Int = 1
    ): Int {
        incrementUserProgress(challengeId, progressIncrement)
        val challenge = getChallengeByIdSync(challengeId)
        return challenge?.currentUserProgress ?: 0
    }

    @Delete
    suspend fun deleteChallenge(challenge: ChallengeEntity)

    // Challenge Milestones
    @Query("SELECT * FROM challenge_milestones WHERE challengeId = :challengeId ORDER BY orderIndex ASC")
    fun getMilestonesForChallenge(challengeId: String): Flow<List<ChallengeMilestoneEntity>>

    @Query("SELECT * FROM challenge_milestones WHERE challengeId = :challengeId AND isReached = 0 ORDER BY orderIndex ASC LIMIT 1")
    suspend fun getNextMilestone(challengeId: String): ChallengeMilestoneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: ChallengeMilestoneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<ChallengeMilestoneEntity>)

    @Query("UPDATE challenge_milestones SET isReached = 1, reachedAt = :reachedAt WHERE id = :milestoneId")
    suspend fun markMilestoneReached(milestoneId: String, reachedAt: Long = System.currentTimeMillis())

    // Challenge Participation
    @Query("SELECT * FROM challenge_participation WHERE challengeId = :challengeId ORDER BY date DESC")
    fun getParticipationHistory(challengeId: String): Flow<List<ChallengeParticipationEntity>>

    @Query("SELECT * FROM challenge_participation WHERE challengeId = :challengeId AND date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getParticipationInRange(challengeId: String, startDate: Long, endDate: Long): Flow<List<ChallengeParticipationEntity>>

    @Query("SELECT SUM(progressMade) FROM challenge_participation WHERE challengeId = :challengeId")
    suspend fun getTotalProgressForChallenge(challengeId: String): Int?

    @Query("SELECT COUNT(DISTINCT date) FROM challenge_participation WHERE challengeId = :challengeId")
    suspend fun getActiveDaysInChallenge(challengeId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipation(participation: ChallengeParticipationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipations(participations: List<ChallengeParticipationEntity>)

    // Challenge Leaderboard
    @Query("SELECT * FROM challenge_leaderboard WHERE challengeId = :challengeId ORDER BY progress DESC LIMIT :limit")
    fun getChallengeLeaderboard(challengeId: String, limit: Int = 20): Flow<List<ChallengeLeaderboardEntity>>

    @Query("SELECT * FROM challenge_leaderboard WHERE challengeId = :challengeId AND isCurrentUser = 1")
    fun getCurrentUserRankInChallenge(challengeId: String): Flow<ChallengeLeaderboardEntity?>

    @Query("SELECT * FROM challenge_leaderboard WHERE challengeId = :challengeId ORDER BY progress DESC")
    fun getFullChallengeLeaderboard(challengeId: String): Flow<List<ChallengeLeaderboardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntry(entry: ChallengeLeaderboardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<ChallengeLeaderboardEntity>)

    @Query("UPDATE challenge_leaderboard SET progress = :progress, rank = :rank WHERE challengeId = :challengeId AND odId = :odId")
    suspend fun updateLeaderboardEntry(challengeId: String, odId: String, progress: Int, rank: Int)

    @Query("DELETE FROM challenge_leaderboard WHERE challengeId = :challengeId")
    suspend fun clearChallengeLeaderboard(challengeId: String)

    // Statistics
    @Query("SELECT COUNT(*) FROM challenges WHERE isJoined = 1")
    fun getTotalChallengesJoined(): Flow<Int>

    @Query("SELECT COUNT(*) FROM challenges WHERE isCompleted = 1")
    fun getTotalChallengesCompleted(): Flow<Int>

    @Query("SELECT SUM(rewardPoints) FROM challenges WHERE isCompleted = 1")
    fun getTotalPointsFromChallenges(): Flow<Int?>

    // =========================================================================
    // Data Hygiene Methods
    // =========================================================================

    @Query("DELETE FROM challenge_participation")
    suspend fun clearUserParticipation()

    @Query("DELETE FROM challenge_participation WHERE userId = :userId")
    suspend fun clearParticipationByUser(userId: String)

    @Query("DELETE FROM challenge_milestones")
    suspend fun clearAllMilestones()

    @Query("DELETE FROM challenge_leaderboard")
    suspend fun clearAllLeaderboards()

    // Reset user's challenge progress (without leaving the challenge)
    @Query("UPDATE challenges SET currentUserProgress = 0, isCompleted = 0, completedAt = NULL WHERE isJoined = 1")
    suspend fun resetUserChallengeProgress()

    // =========================================================================
    // Multi-User / Sync Methods
    // =========================================================================

    @Query("SELECT * FROM challenge_participation WHERE userId = :userId ORDER BY date DESC")
    fun getParticipationByUser(userId: String): Flow<List<ChallengeParticipationEntity>>

    @Query("UPDATE challenge_participation SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :id")
    suspend fun updateParticipationSyncStatus(id: Long, status: String, syncTime: Long = System.currentTimeMillis())

    @Query("SELECT * FROM challenge_participation WHERE syncStatus = 'pending'")
    suspend fun getPendingSyncParticipation(): List<ChallengeParticipationEntity>
}
