package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import com.prody.prashant.data.local.entity.MotivationalMessageEntity
import com.prody.prashant.data.local.entity.PeerInteractionEntity
import com.prody.prashant.data.local.entity.PlayerSkillsEntity
import com.prody.prashant.data.local.entity.ProcessedRewardEntity
import com.prody.prashant.data.local.entity.StreakHistoryEntity
import com.prody.prashant.data.local.entity.UserProfileEntity
import com.prody.prashant.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET displayName = :name WHERE id = 1")
    suspend fun updateDisplayName(name: String)

    @Query("UPDATE user_profile SET avatarId = :avatarId WHERE id = 1")
    suspend fun updateAvatar(avatarId: String)

    @Query("UPDATE user_profile SET bannerId = :bannerId WHERE id = 1")
    suspend fun updateBanner(bannerId: String)

    @Query("UPDATE user_profile SET titleId = :titleId WHERE id = 1")
    suspend fun updateTitle(titleId: String)

    @Query("UPDATE user_profile SET bio = :bio WHERE id = 1")
    suspend fun updateBio(bio: String)

    @Query("UPDATE user_profile SET preferences = :preferences WHERE id = 1")
    suspend fun updatePreferences(preferences: String)

    @Query("UPDATE user_profile SET totalPoints = totalPoints + :points WHERE id = 1")
    suspend fun addPoints(points: Int)

    /**
     * Atomically add points to both user profile and daily stats.
     * Ensures consistency between total points and daily tracking.
     */
    @Transaction
    suspend fun addPointsWithDailyTracking(points: Int) {
        addPoints(points)
        addDailyPoints(points)
    }

    @Query("UPDATE user_profile SET currentStreak = :streak, longestStreak = CASE WHEN :streak > longestStreak THEN :streak ELSE longestStreak END WHERE id = 1")
    suspend fun updateStreak(streak: Int)

    @Query("UPDATE user_profile SET lastActiveDate = :date WHERE id = 1")
    suspend fun updateLastActiveDate(date: Long)

    @Query("UPDATE user_profile SET wordsLearned = wordsLearned + 1 WHERE id = 1")
    suspend fun incrementWordsLearned()

    @Query("UPDATE user_profile SET journalEntriesCount = journalEntriesCount + 1 WHERE id = 1")
    suspend fun incrementJournalEntries()

    @Query("UPDATE user_profile SET futureMessagesCount = futureMessagesCount + 1 WHERE id = 1")
    suspend fun incrementFutureMessages()

    @Query("UPDATE user_profile SET totalPoints = :points WHERE id = 1")
    suspend fun setTotalPoints(points: Int)

    @Query("UPDATE user_profile SET currentStreak = :streak WHERE id = 1")
    suspend fun updateCurrentStreak(streak: Int)

    @Query("UPDATE user_profile SET longestStreak = :streak WHERE id = 1")
    suspend fun updateLongestStreak(streak: Int)

    @Query("SELECT totalPoints FROM user_profile WHERE id = 1")
    fun getTotalPoints(): Flow<Int?>

    @Query("SELECT currentStreak FROM user_profile WHERE id = 1")
    fun getCurrentStreak(): Flow<Int?>

    // User Stats
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStatsEntity)

    @Update
    suspend fun updateUserStats(stats: UserStatsEntity)

    @Query("UPDATE user_stats SET dailyPointsEarned = dailyPointsEarned + :points WHERE id = 1")
    suspend fun addDailyPoints(points: Int)

    @Query("UPDATE user_stats SET dailyPointsEarned = 0, lastResetDate = :resetDate WHERE id = 1")
    suspend fun resetDailyStats(resetDate: Long)

    // Achievements
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, rarity DESC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getAchievementById(id: String): AchievementEntity?

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 0 ORDER BY currentProgress DESC")
    fun getLockedAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE category = :category ORDER BY isUnlocked DESC")
    fun getAchievementsByCategory(category: String): Flow<List<AchievementEntity>>

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("UPDATE achievements SET currentProgress = :progress WHERE id = :id")
    suspend fun updateAchievementProgress(id: String, progress: Int)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :id")
    suspend fun unlockAchievement(id: String, unlockedAt: Long = System.currentTimeMillis())

    // Streak History
    @Query("SELECT * FROM streak_history ORDER BY date DESC")
    fun getStreakHistory(): Flow<List<StreakHistoryEntity>>

    @Query("SELECT * FROM streak_history WHERE date = :date")
    suspend fun getStreakForDate(date: Long): StreakHistoryEntity?

    @Query("SELECT * FROM streak_history ORDER BY date DESC LIMIT :days")
    fun getRecentStreakHistory(days: Int): Flow<List<StreakHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreakHistory(streak: StreakHistoryEntity)

    @Query("SELECT COUNT(*) FROM streak_history WHERE date >= :startDate")
    suspend fun getStreakDaysInRange(startDate: Long): Int

    // Leaderboard
    @Query("SELECT COUNT(*) FROM leaderboard")
    suspend fun getLeaderboardCount(): Int

    @Query("SELECT * FROM leaderboard ORDER BY totalPoints DESC")
    fun getLeaderboard(): Flow<List<LeaderboardEntryEntity>>

    @Query("SELECT * FROM leaderboard ORDER BY weeklyPoints DESC")
    fun getWeeklyLeaderboard(): Flow<List<LeaderboardEntryEntity>>

    @Query("SELECT * FROM leaderboard WHERE isCurrentUser = 1")
    fun getCurrentUserRank(): Flow<LeaderboardEntryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntry(entry: LeaderboardEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntryEntity>)

    @Update
    suspend fun updateLeaderboardEntry(entry: LeaderboardEntryEntity)

    @Query("UPDATE leaderboard SET boostsReceived = boostsReceived + 1 WHERE odId = :odId")
    suspend fun incrementBoosts(odId: String)

    @Query("UPDATE leaderboard SET congratsReceived = congratsReceived + 1 WHERE odId = :odId")
    suspend fun incrementCongrats(odId: String)

    @Query("UPDATE leaderboard SET respectsReceived = respectsReceived + 1 WHERE odId = :odId")
    suspend fun incrementRespects(odId: String)

    // Peer Interactions
    @Query("SELECT * FROM peer_interactions ORDER BY timestamp DESC")
    fun getPeerInteractions(): Flow<List<PeerInteractionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeerInteraction(interaction: PeerInteractionEntity)

    @Query("SELECT COUNT(*) FROM peer_interactions WHERE peerId = :peerId AND interactionType = :type AND timestamp >= :since")
    suspend fun getInteractionCount(peerId: String, type: String, since: Long): Int

    // Motivational Messages
    @Query("SELECT * FROM motivational_messages ORDER BY receivedAt DESC")
    fun getMotivationalMessages(): Flow<List<MotivationalMessageEntity>>

    @Query("SELECT * FROM motivational_messages WHERE isRead = 0 ORDER BY receivedAt DESC")
    fun getUnreadMotivationalMessages(): Flow<List<MotivationalMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotivationalMessage(message: MotivationalMessageEntity)

    @Query("UPDATE motivational_messages SET isRead = 1 WHERE id = :id")
    suspend fun markMessageAsRead(id: Long)

    @Query("SELECT COUNT(*) FROM motivational_messages WHERE isRead = 0")
    fun getUnreadMessageCount(): Flow<Int>

    // =========================================================================
    // Data Hygiene Methods
    // =========================================================================

    @Query("DELETE FROM user_profile WHERE id = 1")
    suspend fun deleteUserProfile()

    @Query("DELETE FROM user_stats")
    suspend fun clearUserStats()

    @Query("DELETE FROM streak_history")
    suspend fun clearStreakHistory()

    @Query("DELETE FROM streak_history WHERE date < :cutoffTime")
    suspend fun deleteOldStreakHistory(cutoffTime: Long)

    @Query("DELETE FROM achievements")
    suspend fun clearAchievements()

    @Query("DELETE FROM leaderboard")
    suspend fun clearLeaderboard()

    @Query("DELETE FROM peer_interactions")
    suspend fun clearPeerInteractions()

    @Query("DELETE FROM motivational_messages")
    suspend fun clearMotivationalMessages()

    /**
     * Atomically clear all user data in a transaction.
     * Use for account reset or logout functionality.
     */
    @Transaction
    suspend fun clearAllUserData() {
        deleteUserProfile()
        clearUserStats()
        clearStreakHistory()
        clearAchievements()
        clearLeaderboard()
        clearPeerInteractions()
        clearMotivationalMessages()
    }

    // =========================================================================
    // Multi-User / Sync Methods
    // =========================================================================

    @Query("SELECT * FROM user_profile WHERE odUserId = :odUserId")
    suspend fun getUserByOdUserId(odUserId: String): UserProfileEntity?

    @Query("UPDATE user_profile SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = 1")
    suspend fun updateProfileSyncStatus(status: String, syncTime: Long = System.currentTimeMillis())

    @Query("SELECT * FROM streak_history WHERE userId = :userId ORDER BY date DESC")
    fun getStreakHistoryByUser(userId: String): Flow<List<StreakHistoryEntity>>

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    fun getUserStatsByUser(userId: String): Flow<UserStatsEntity?>

    // =========================================================================
    // Player Skills (Gamification 3.0 - Skill System)
    // =========================================================================

    @Query("SELECT * FROM player_skills WHERE id = 1")
    fun observePlayerSkills(): Flow<PlayerSkillsEntity?>

    @Query("SELECT * FROM player_skills WHERE id = 1")
    suspend fun getPlayerSkillsSync(): PlayerSkillsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerSkills(skills: PlayerSkillsEntity)

    @Update
    suspend fun updatePlayerSkills(skills: PlayerSkillsEntity)

    @Query("UPDATE player_skills SET clarityXp = clarityXp + :amount WHERE id = 1")
    suspend fun addClarityXp(amount: Int)

    @Query("UPDATE player_skills SET disciplineXp = disciplineXp + :amount WHERE id = 1")
    suspend fun addDisciplineXp(amount: Int)

    @Query("UPDATE player_skills SET courageXp = courageXp + :amount WHERE id = 1")
    suspend fun addCourageXp(amount: Int)

    @Query("UPDATE player_skills SET dailyClarityXp = dailyClarityXp + :amount WHERE id = 1")
    suspend fun addDailyClarityXp(amount: Int)

    @Query("UPDATE player_skills SET dailyDisciplineXp = dailyDisciplineXp + :amount WHERE id = 1")
    suspend fun addDailyDisciplineXp(amount: Int)

    @Query("UPDATE player_skills SET dailyCourageXp = dailyCourageXp + :amount WHERE id = 1")
    suspend fun addDailyCourageXp(amount: Int)

    @Query("""
        UPDATE player_skills
        SET dailyClarityXp = 0,
            dailyDisciplineXp = 0,
            dailyCourageXp = 0,
            dailyResetDate = :resetDate
        WHERE id = 1
    """)
    suspend fun resetDailySkillXp(resetDate: Long = System.currentTimeMillis())

    // =========================================================================
    // Processed Rewards (Idempotency for anti-exploit)
    // =========================================================================

    @Query("SELECT COUNT(*) > 0 FROM processed_rewards WHERE rewardKey = :key")
    suspend fun hasProcessedRewardKey(key: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProcessedReward(reward: ProcessedRewardEntity)

    suspend fun markRewardKeyProcessed(key: String) {
        insertProcessedReward(ProcessedRewardEntity(rewardKey = key))
    }

    @Query("DELETE FROM processed_rewards WHERE processedAt < :cutoffTime")
    suspend fun deleteOldProcessedRewards(cutoffTime: Long)
}
