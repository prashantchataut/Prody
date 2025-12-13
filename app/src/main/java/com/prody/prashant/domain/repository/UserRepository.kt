package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import com.prody.prashant.data.local.entity.MotivationalMessageEntity
import com.prody.prashant.data.local.entity.PeerInteractionEntity
import com.prody.prashant.data.local.entity.StreakHistoryEntity
import com.prody.prashant.data.local.entity.UserProfileEntity
import com.prody.prashant.data.local.entity.UserStatsEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user-related operations.
 * Provides abstraction layer between ViewModels and data sources.
 */
interface UserRepository {
    // User Profile Operations
    /**
     * Get the current user's profile.
     */
    fun getUserProfile(): Flow<UserProfileEntity?>

    /**
     * Get the current user's profile synchronously.
     */
    suspend fun getUserProfileSync(): Result<UserProfileEntity>

    /**
     * Create or update user profile.
     */
    suspend fun saveUserProfile(profile: UserProfileEntity): Result<Unit>

    /**
     * Update display name.
     */
    suspend fun updateDisplayName(name: String): Result<Unit>

    /**
     * Update avatar.
     */
    suspend fun updateAvatar(avatarId: String): Result<Unit>

    /**
     * Update banner.
     */
    suspend fun updateBanner(bannerId: String): Result<Unit>

    /**
     * Update title.
     */
    suspend fun updateTitle(titleId: String): Result<Unit>

    /**
     * Add points to user.
     */
    suspend fun addPoints(points: Int): Result<Unit>

    /**
     * Update streak.
     */
    suspend fun updateStreak(streak: Int): Result<Unit>

    /**
     * Update last active date.
     */
    suspend fun updateLastActiveDate(date: Long = System.currentTimeMillis()): Result<Unit>

    /**
     * Increment words learned count.
     */
    suspend fun incrementWordsLearned(): Result<Unit>

    /**
     * Increment journal entries count.
     */
    suspend fun incrementJournalEntries(): Result<Unit>

    /**
     * Increment future messages count.
     */
    suspend fun incrementFutureMessages(): Result<Unit>

    // User Stats Operations
    /**
     * Get user stats.
     */
    fun getUserStats(): Flow<UserStatsEntity?>

    /**
     * Update user stats.
     */
    suspend fun updateUserStats(stats: UserStatsEntity): Result<Unit>

    /**
     * Add daily points.
     */
    suspend fun addDailyPoints(points: Int): Result<Unit>

    /**
     * Reset daily stats.
     */
    suspend fun resetDailyStats(): Result<Unit>

    // Achievement Operations
    /**
     * Get all achievements.
     */
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    /**
     * Get achievement by ID.
     */
    suspend fun getAchievementById(id: String): Result<AchievementEntity>

    /**
     * Get unlocked achievements.
     */
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>

    /**
     * Get locked achievements.
     */
    fun getLockedAchievements(): Flow<List<AchievementEntity>>

    /**
     * Get achievements by category.
     */
    fun getAchievementsByCategory(category: String): Flow<List<AchievementEntity>>

    /**
     * Get unlocked count.
     */
    fun getUnlockedCount(): Flow<Int>

    /**
     * Update achievement progress.
     */
    suspend fun updateAchievementProgress(id: String, progress: Int): Result<Unit>

    /**
     * Unlock an achievement.
     */
    suspend fun unlockAchievement(id: String): Result<Unit>

    /**
     * Check and unlock achievements based on current progress.
     */
    suspend fun checkAndUnlockAchievements(): Result<List<AchievementEntity>>

    // Streak History Operations
    /**
     * Get streak history.
     */
    fun getStreakHistory(): Flow<List<StreakHistoryEntity>>

    /**
     * Get recent streak history.
     */
    fun getRecentStreakHistory(days: Int): Flow<List<StreakHistoryEntity>>

    /**
     * Record streak for date.
     */
    suspend fun recordStreakForDate(streak: StreakHistoryEntity): Result<Unit>

    /**
     * Get streak for a specific date.
     */
    suspend fun getStreakForDate(date: Long): Result<StreakHistoryEntity?>

    /**
     * Get streak days count in a range.
     */
    suspend fun getStreakDaysInRange(startDate: Long): Int

    // Leaderboard Operations
    /**
     * Get leaderboard.
     */
    fun getLeaderboard(): Flow<List<LeaderboardEntryEntity>>

    /**
     * Get weekly leaderboard.
     */
    fun getWeeklyLeaderboard(): Flow<List<LeaderboardEntryEntity>>

    /**
     * Get current user's rank.
     */
    fun getCurrentUserRank(): Flow<LeaderboardEntryEntity?>

    /**
     * Update leaderboard entry.
     */
    suspend fun updateLeaderboardEntry(entry: LeaderboardEntryEntity): Result<Unit>

    /**
     * Send boost to peer.
     */
    suspend fun sendBoostToPeer(odId: String): Result<Unit>

    /**
     * Send congrats to peer.
     */
    suspend fun sendCongratsToPeer(odId: String): Result<Unit>

    // Peer Interaction Operations
    /**
     * Get peer interactions.
     */
    fun getPeerInteractions(): Flow<List<PeerInteractionEntity>>

    /**
     * Record peer interaction.
     */
    suspend fun recordPeerInteraction(interaction: PeerInteractionEntity): Result<Unit>

    /**
     * Get interaction count for a peer.
     */
    suspend fun getInteractionCount(peerId: String, type: String, since: Long): Int

    // Motivational Messages Operations
    /**
     * Get motivational messages.
     */
    fun getMotivationalMessages(): Flow<List<MotivationalMessageEntity>>

    /**
     * Get unread motivational messages.
     */
    fun getUnreadMotivationalMessages(): Flow<List<MotivationalMessageEntity>>

    /**
     * Mark message as read.
     */
    suspend fun markMessageAsRead(id: Long): Result<Unit>

    /**
     * Get unread message count.
     */
    fun getUnreadMessageCount(): Flow<Int>

    /**
     * Initialize default user profile if not exists.
     */
    suspend fun initializeUserIfNeeded(): Result<Unit>
}
