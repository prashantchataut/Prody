package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Social Accountability Circles.
 *
 * Provides all database operations for circles, members, updates, nudges,
 * challenges, and privacy settings.
 */
@Dao
interface SocialDao {

    // =========================================================================
    // CIRCLES - Core Operations
    // =========================================================================

    @Query("SELECT * FROM accountability_circles WHERE id = :circleId")
    suspend fun getCircleById(circleId: String): CircleEntity?

    @Query("SELECT * FROM accountability_circles WHERE id = :circleId")
    fun observeCircle(circleId: String): Flow<CircleEntity?>

    @Query("SELECT * FROM accountability_circles WHERE inviteCode = :inviteCode")
    suspend fun getCircleByInviteCode(inviteCode: String): CircleEntity?

    @Query("""
        SELECT c.* FROM accountability_circles c
        INNER JOIN circle_members m ON c.id = m.circleId
        WHERE m.userId = :userId AND m.isActive = 1 AND c.isActive = 1
        ORDER BY c.lastActivityAt DESC
    """)
    fun observeUserCircles(userId: String): Flow<List<CircleEntity>>

    @Query("""
        SELECT c.* FROM accountability_circles c
        INNER JOIN circle_members m ON c.id = m.circleId
        WHERE m.userId = :userId AND m.isActive = 1 AND c.isActive = 1
        ORDER BY c.lastActivityAt DESC
    """)
    suspend fun getUserCircles(userId: String): List<CircleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircle(circle: CircleEntity): Long

    @Update
    suspend fun updateCircle(circle: CircleEntity)

    @Query("UPDATE accountability_circles SET memberCount = :count WHERE id = :circleId")
    suspend fun updateMemberCount(circleId: String, count: Int)

    @Query("UPDATE accountability_circles SET lastActivityAt = :timestamp WHERE id = :circleId")
    suspend fun updateLastActivity(circleId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE accountability_circles SET isActive = 0 WHERE id = :circleId")
    suspend fun deactivateCircle(circleId: String)

    @Delete
    suspend fun deleteCircle(circle: CircleEntity)

    // =========================================================================
    // CIRCLE MEMBERS
    // =========================================================================

    @Query("SELECT * FROM circle_members WHERE circleId = :circleId AND isActive = 1 ORDER BY role DESC, joinedAt ASC")
    fun observeCircleMembers(circleId: String): Flow<List<CircleMemberEntity>>

    @Query("SELECT * FROM circle_members WHERE circleId = :circleId AND isActive = 1 ORDER BY role DESC, joinedAt ASC")
    suspend fun getCircleMembers(circleId: String): List<CircleMemberEntity>

    @Query("SELECT * FROM circle_members WHERE circleId = :circleId AND userId = :userId")
    suspend fun getCircleMember(circleId: String, userId: String): CircleMemberEntity?

    @Query("SELECT * FROM circle_members WHERE userId = :userId AND isActive = 1")
    suspend fun getUserMemberships(userId: String): List<CircleMemberEntity>

    @Query("SELECT COUNT(*) FROM circle_members WHERE circleId = :circleId AND isActive = 1")
    suspend fun getActiveMemberCount(circleId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: CircleMemberEntity): Long

    @Update
    suspend fun updateMember(member: CircleMemberEntity)

    @Query("UPDATE circle_members SET lastActiveAt = :timestamp WHERE userId = :userId AND circleId = :circleId")
    suspend fun updateMemberLastActive(userId: String, circleId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE circle_members SET currentStreak = :streak WHERE userId = :userId AND circleId = :circleId")
    suspend fun updateMemberStreak(userId: String, circleId: String, streak: Int)

    @Query("UPDATE circle_members SET totalEntries = :count WHERE userId = :userId AND circleId = :circleId")
    suspend fun updateMemberEntryCount(userId: String, circleId: String, count: Int)

    @Query("UPDATE circle_members SET isActive = 0 WHERE userId = :userId AND circleId = :circleId")
    suspend fun removeMember(userId: String, circleId: String)

    @Delete
    suspend fun deleteMember(member: CircleMemberEntity)

    // =========================================================================
    // CIRCLE UPDATES (Activity Feed)
    // =========================================================================

    @Query("""
        SELECT * FROM circle_updates
        WHERE circleId = :circleId
        ORDER BY createdAt DESC
        LIMIT :limit OFFSET :offset
    """)
    fun observeCircleUpdates(circleId: String, limit: Int = 50, offset: Int = 0): Flow<List<CircleUpdateEntity>>

    @Query("""
        SELECT * FROM circle_updates
        WHERE circleId = :circleId
        ORDER BY createdAt DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getCircleUpdates(circleId: String, limit: Int = 50, offset: Int = 0): List<CircleUpdateEntity>

    @Query("""
        SELECT * FROM circle_updates
        WHERE circleId = :circleId AND userId = :userId
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    suspend fun getUserUpdatesInCircle(circleId: String, userId: String, limit: Int = 20): List<CircleUpdateEntity>

    @Query("SELECT * FROM circle_updates WHERE id = :updateId")
    suspend fun getUpdateById(updateId: Long): CircleUpdateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(update: CircleUpdateEntity): Long

    @Update
    suspend fun updateUpdate(update: CircleUpdateEntity)

    @Query("UPDATE circle_updates SET reactionsJson = :reactionsJson, reactionCount = :count WHERE id = :updateId")
    suspend fun updateReactions(updateId: Long, reactionsJson: String, count: Int)

    @Delete
    suspend fun deleteUpdate(update: CircleUpdateEntity)

    @Query("DELETE FROM circle_updates WHERE circleId = :circleId AND createdAt < :cutoffTime")
    suspend fun deleteOldUpdates(circleId: String, cutoffTime: Long)

    // =========================================================================
    // NUDGES
    // =========================================================================

    @Query("""
        SELECT * FROM circle_nudges
        WHERE toUserId = :userId AND isRead = 0
        ORDER BY createdAt DESC
    """)
    fun observeUnreadNudges(userId: String): Flow<List<CircleNudgeEntity>>

    @Query("""
        SELECT * FROM circle_nudges
        WHERE toUserId = :userId
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    suspend fun getUserNudges(userId: String, limit: Int = 50): List<CircleNudgeEntity>

    @Query("""
        SELECT * FROM circle_nudges
        WHERE circleId = :circleId
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    suspend fun getCircleNudges(circleId: String, limit: Int = 50): List<CircleNudgeEntity>

    @Query("SELECT COUNT(*) FROM circle_nudges WHERE toUserId = :userId AND isRead = 0")
    fun observeUnreadNudgeCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNudge(nudge: CircleNudgeEntity): Long

    @Update
    suspend fun updateNudge(nudge: CircleNudgeEntity)

    @Query("UPDATE circle_nudges SET isRead = 1, respondedAt = :timestamp WHERE id = :nudgeId")
    suspend fun markNudgeAsRead(nudgeId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE circle_nudges SET isRead = 1 WHERE toUserId = :userId")
    suspend fun markAllNudgesAsRead(userId: String)

    @Delete
    suspend fun deleteNudge(nudge: CircleNudgeEntity)

    // =========================================================================
    // CHALLENGES
    // =========================================================================

    @Query("""
        SELECT * FROM circle_challenges
        WHERE circleId = :circleId AND isActive = 1
        ORDER BY endDate ASC
    """)
    fun observeActiveChallenges(circleId: String): Flow<List<CircleChallengeEntity>>

    @Query("""
        SELECT * FROM circle_challenges
        WHERE circleId = :circleId
        ORDER BY startDate DESC
        LIMIT :limit
    """)
    suspend fun getCircleChallenges(circleId: String, limit: Int = 20): List<CircleChallengeEntity>

    @Query("SELECT * FROM circle_challenges WHERE id = :challengeId")
    suspend fun getChallengeById(challengeId: String): CircleChallengeEntity?

    @Query("SELECT * FROM circle_challenges WHERE id = :challengeId")
    fun observeChallenge(challengeId: String): Flow<CircleChallengeEntity?>

    @Query("""
        SELECT c.* FROM circle_challenges c
        WHERE c.isActive = 1 AND c.endDate >= :currentTime
        AND c.participantsJson LIKE '%' || :userId || '%'
        ORDER BY c.endDate ASC
    """)
    fun observeUserActiveChallenges(userId: String, currentTime: Long = System.currentTimeMillis()): Flow<List<CircleChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: CircleChallengeEntity): Long

    @Update
    suspend fun updateChallenge(challenge: CircleChallengeEntity)

    @Query("UPDATE circle_challenges SET participantsJson = :participantsJson WHERE id = :challengeId")
    suspend fun updateChallengeParticipants(challengeId: String, participantsJson: String)

    @Query("UPDATE circle_challenges SET progressJson = :progressJson WHERE id = :challengeId")
    suspend fun updateChallengeProgress(challengeId: String, progressJson: String)

    @Query("UPDATE circle_challenges SET completedByJson = :completedByJson WHERE id = :challengeId")
    suspend fun updateChallengeCompletions(challengeId: String, completedByJson: String)

    @Query("UPDATE circle_challenges SET isActive = 0 WHERE id = :challengeId")
    suspend fun deactivateChallenge(challengeId: String)

    @Delete
    suspend fun deleteChallenge(challenge: CircleChallengeEntity)

    // =========================================================================
    // PRIVACY SETTINGS
    // =========================================================================

    @Query("SELECT * FROM circle_privacy_settings WHERE userId = :userId AND circleId = :circleId")
    suspend fun getPrivacySettings(userId: String, circleId: String): CirclePrivacySettingsEntity?

    @Query("SELECT * FROM circle_privacy_settings WHERE userId = :userId AND circleId = 'global'")
    suspend fun getGlobalPrivacySettings(userId: String): CirclePrivacySettingsEntity?

    @Query("SELECT * FROM circle_privacy_settings WHERE userId = :userId AND circleId = 'global'")
    fun observeGlobalPrivacySettings(userId: String): Flow<CirclePrivacySettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivacySettings(settings: CirclePrivacySettingsEntity): Long

    @Update
    suspend fun updatePrivacySettings(settings: CirclePrivacySettingsEntity)

    // =========================================================================
    // NOTIFICATIONS
    // =========================================================================

    @Query("""
        SELECT * FROM circle_notifications
        WHERE userId = :userId AND isRead = 0
        ORDER BY createdAt DESC
    """)
    fun observeUnreadNotifications(userId: String): Flow<List<CircleNotificationEntity>>

    @Query("""
        SELECT * FROM circle_notifications
        WHERE userId = :userId
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    suspend fun getUserNotifications(userId: String, limit: Int = 50): List<CircleNotificationEntity>

    @Query("SELECT COUNT(*) FROM circle_notifications WHERE userId = :userId AND isRead = 0")
    fun observeUnreadNotificationCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: CircleNotificationEntity): Long

    @Update
    suspend fun updateNotification(notification: CircleNotificationEntity)

    @Query("UPDATE circle_notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: Long)

    @Query("UPDATE circle_notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: String)

    @Delete
    suspend fun deleteNotification(notification: CircleNotificationEntity)

    @Query("DELETE FROM circle_notifications WHERE userId = :userId AND createdAt < :cutoffTime")
    suspend fun deleteOldNotifications(userId: String, cutoffTime: Long)

    // =========================================================================
    // MEMBER STATS CACHE
    // =========================================================================

    @Query("SELECT * FROM circle_member_stats_cache WHERE userId = :userId AND circleId = :circleId")
    suspend fun getMemberStatsCache(userId: String, circleId: String): CircleMemberStatsCacheEntity?

    @Query("SELECT * FROM circle_member_stats_cache WHERE circleId = :circleId")
    suspend fun getCircleMemberStatsCache(circleId: String): List<CircleMemberStatsCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemberStatsCache(cache: CircleMemberStatsCacheEntity): Long

    @Update
    suspend fun updateMemberStatsCache(cache: CircleMemberStatsCacheEntity)

    @Query("DELETE FROM circle_member_stats_cache WHERE lastUpdated < :cutoffTime")
    suspend fun deleteOldStatsCache(cutoffTime: Long)

    // =========================================================================
    // CLEANUP & MAINTENANCE
    // =========================================================================

    @Transaction
    suspend fun leaveCircle(userId: String, circleId: String) {
        removeMember(userId, circleId)
        val count = getActiveMemberCount(circleId)
        updateMemberCount(circleId, count)

        // If no members left, deactivate circle
        if (count == 0) {
            deactivateCircle(circleId)
        }
    }

    @Transaction
    suspend fun deleteCircleCompletely(circleId: String) {
        // Delete all related data
        val circle = getCircleById(circleId)
        circle?.let { deleteCircle(it) }

        val members = getCircleMembers(circleId)
        members.forEach { deleteMember(it) }

        val updates = getCircleUpdates(circleId, limit = Int.MAX_VALUE)
        updates.forEach { deleteUpdate(it) }

        val challenges = getCircleChallenges(circleId, limit = Int.MAX_VALUE)
        challenges.forEach { deleteChallenge(it) }
    }

    @Query("DELETE FROM circle_updates WHERE createdAt < :cutoffTime")
    suspend fun cleanupOldUpdates(cutoffTime: Long)

    @Query("DELETE FROM circle_nudges WHERE createdAt < :cutoffTime")
    suspend fun cleanupOldNudges(cutoffTime: Long)

    @Query("DELETE FROM circle_notifications WHERE createdAt < :cutoffTime")
    suspend fun cleanupOldNotifications(cutoffTime: Long)

    @Query("DELETE FROM circle_member_stats_cache WHERE lastUpdated < :cutoffTime")
    suspend fun cleanupOldStatsCache(cutoffTime: Long)
}
