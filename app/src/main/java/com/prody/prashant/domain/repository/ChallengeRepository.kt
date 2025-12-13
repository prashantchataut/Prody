package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.ChallengeEntity
import com.prody.prashant.data.local.entity.ChallengeLeaderboardEntity
import com.prody.prashant.data.local.entity.ChallengeMilestoneEntity
import com.prody.prashant.data.local.entity.ChallengeParticipationEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for challenge operations.
 * Provides abstraction layer between ViewModels and data sources.
 */
interface ChallengeRepository {
    /**
     * Get all challenges.
     */
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    /**
     * Get active challenges (not expired).
     */
    fun getActiveChallenges(): Flow<List<ChallengeEntity>>

    /**
     * Get challenges the user has joined.
     */
    fun getJoinedChallenges(): Flow<List<ChallengeEntity>>

    /**
     * Get a challenge by ID.
     */
    suspend fun getChallengeById(id: Long): Result<ChallengeEntity>

    /**
     * Get challenges by type.
     */
    fun getChallengesByType(type: String): Flow<List<ChallengeEntity>>

    /**
     * Get challenges by difficulty.
     */
    fun getChallengesByDifficulty(difficulty: String): Flow<List<ChallengeEntity>>

    /**
     * Create a new challenge.
     */
    suspend fun createChallenge(challenge: ChallengeEntity): Result<Long>

    /**
     * Update a challenge.
     */
    suspend fun updateChallenge(challenge: ChallengeEntity): Result<Unit>

    /**
     * Delete a challenge.
     */
    suspend fun deleteChallenge(challenge: ChallengeEntity): Result<Unit>

    /**
     * Join a challenge.
     */
    suspend fun joinChallenge(challengeId: Long): Result<Unit>

    /**
     * Leave a challenge.
     */
    suspend fun leaveChallenge(challengeId: Long): Result<Unit>

    /**
     * Get user participation in a challenge.
     */
    suspend fun getParticipation(challengeId: Long): Result<ChallengeParticipationEntity?>

    /**
     * Update user progress in a challenge.
     */
    suspend fun updateProgress(challengeId: Long, progress: Int): Result<Unit>

    /**
     * Increment progress in a challenge.
     */
    suspend fun incrementProgress(challengeId: Long, increment: Int = 1): Result<Unit>

    /**
     * Get milestones for a challenge.
     */
    fun getMilestones(challengeId: Long): Flow<List<ChallengeMilestoneEntity>>

    /**
     * Unlock a milestone.
     */
    suspend fun unlockMilestone(challengeId: Long, milestoneId: Long): Result<Unit>

    /**
     * Get challenge leaderboard.
     */
    fun getChallengeLeaderboard(challengeId: Long): Flow<List<ChallengeLeaderboardEntity>>

    /**
     * Get user rank in challenge.
     */
    suspend fun getUserRank(challengeId: Long): Result<Int>

    /**
     * Get daily challenges.
     */
    fun getDailyChallenges(): Flow<List<ChallengeEntity>>

    /**
     * Get weekly challenges.
     */
    fun getWeeklyChallenges(): Flow<List<ChallengeEntity>>

    /**
     * Check if challenge is completed.
     */
    suspend fun isChallengeCompleted(challengeId: Long): Boolean

    /**
     * Get completed challenges count.
     */
    fun getCompletedChallengesCount(): Flow<Int>
}
