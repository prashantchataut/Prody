package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.WeeklyDigestEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for WeeklyDigest operations.
 *
 * Handles all database operations for the Weekly Digest feature,
 * including digest creation, retrieval, and cleanup of old digests.
 */
@Dao
interface WeeklyDigestDao {

    // ==================== BASIC CRUD ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDigest(digest: WeeklyDigestEntity): Long

    @Update
    suspend fun updateDigest(digest: WeeklyDigestEntity)

    @Delete
    suspend fun deleteDigest(digest: WeeklyDigestEntity)

    @Query("DELETE FROM weekly_digests WHERE id = :id")
    suspend fun deleteDigestById(id: Long)

    // ==================== RETRIEVAL QUERIES ====================

    @Query("SELECT * FROM weekly_digests WHERE isDeleted = 0 ORDER BY weekStartDate DESC")
    fun getAllDigests(): Flow<List<WeeklyDigestEntity>>

    @Query("SELECT * FROM weekly_digests WHERE userId = :userId AND isDeleted = 0 ORDER BY weekStartDate DESC")
    fun getAllDigests(userId: String): Flow<List<WeeklyDigestEntity>>

    @Query("SELECT * FROM weekly_digests WHERE userId = :userId AND isDeleted = 0 ORDER BY weekStartDate DESC")
    fun getDigestsByUser(userId: String): Flow<List<WeeklyDigestEntity>>

    @Query("SELECT * FROM weekly_digests WHERE id = :id AND isDeleted = 0")
    suspend fun getDigestById(id: Long): WeeklyDigestEntity?

    @Query("SELECT * FROM weekly_digests WHERE id = :id")
    fun observeDigestById(id: Long): Flow<WeeklyDigestEntity?>

    /**
     * Get the most recent digest
     */
    @Query("SELECT * FROM weekly_digests WHERE isDeleted = 0 ORDER BY weekStartDate DESC LIMIT 1")
    suspend fun getLatestDigest(): WeeklyDigestEntity?

    @Query("SELECT * FROM weekly_digests WHERE userId = :userId AND isDeleted = 0 ORDER BY weekStartDate DESC LIMIT 1")
    suspend fun getLatestDigest(userId: String): WeeklyDigestEntity?

    @Query("SELECT * FROM weekly_digests WHERE isDeleted = 0 ORDER BY weekStartDate DESC LIMIT 1")
    fun observeLatestDigest(): Flow<WeeklyDigestEntity?>

    @Query("SELECT * FROM weekly_digests WHERE userId = :userId AND isDeleted = 0 ORDER BY weekStartDate DESC LIMIT 1")
    fun observeLatestDigest(userId: String): Flow<WeeklyDigestEntity?>

    /**
     * Get digest for a specific week
     */
    @Query("""
        SELECT * FROM weekly_digests
        WHERE userId = :userId
        AND weekStartDate = :weekStartDate
        AND isDeleted = 0
        LIMIT 1
    """)
    suspend fun getDigestForWeek(userId: String, weekStartDate: Long): WeeklyDigestEntity?

    /**
     * Get the last N digests for historical viewing
     */
    @Query("SELECT * FROM weekly_digests WHERE isDeleted = 0 ORDER BY weekStartDate DESC LIMIT :limit")
    fun getRecentDigests(limit: Int): Flow<List<WeeklyDigestEntity>>

    @Query("SELECT * FROM weekly_digests WHERE userId = :userId AND isDeleted = 0 ORDER BY weekStartDate DESC LIMIT :limit")
    fun getRecentDigestsByUser(userId: String, limit: Int): Flow<List<WeeklyDigestEntity>>

    // ==================== UNREAD DIGESTS ====================

    /**
     * Get unread digests (for notification badge)
     */
    @Query("SELECT * FROM weekly_digests WHERE isRead = 0 AND isDeleted = 0 ORDER BY weekStartDate DESC")
    fun getUnreadDigests(): Flow<List<WeeklyDigestEntity>>

    @Query("SELECT * FROM weekly_digests WHERE userId = :userId AND isRead = 0 AND isDeleted = 0 ORDER BY weekStartDate DESC")
    fun getUnreadDigests(userId: String): Flow<List<WeeklyDigestEntity>>

    @Query("SELECT COUNT(*) FROM weekly_digests WHERE isRead = 0 AND isDeleted = 0")
    fun getUnreadCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM weekly_digests WHERE userId = :userId AND isRead = 0 AND isDeleted = 0")
    suspend fun getUnreadCount(userId: String): Int

    /**
     * Mark digest as read
     */
    @Query("UPDATE weekly_digests SET isRead = 1, readAt = :readAt WHERE id = :id")
    suspend fun markAsRead(id: Long, readAt: Long = System.currentTimeMillis())

    /**
     * Mark all digests as read
     */
    @Query("UPDATE weekly_digests SET isRead = 1, readAt = :readAt WHERE isRead = 0")
    suspend fun markAllAsRead(readAt: Long = System.currentTimeMillis())

    @Query("UPDATE weekly_digests SET isRead = 1, readAt = :readAt WHERE userId = :userId AND isRead = 0")
    suspend fun markAllAsRead(userId: String, readAt: Long = System.currentTimeMillis())

    // ==================== EXISTENCE CHECKS ====================

    /**
     * Check if a digest exists for a specific week
     */
    @Query("SELECT COUNT(*) FROM weekly_digests WHERE userId = :userId AND weekStartDate = :weekStartDate AND isDeleted = 0")
    suspend fun getDigestCountForWeek(userId: String, weekStartDate: Long): Int

    suspend fun hasDigestForWeek(userId: String, weekStartDate: Long): Boolean {
        return getDigestCountForWeek(userId, weekStartDate) > 0
    }

    // ==================== CLEANUP ====================

    /**
     * Delete old digests beyond the maximum to keep
     * Keeps only the most recent MAX_STORED_DIGESTS entries
     */
    @Query("""
        DELETE FROM weekly_digests
        WHERE id NOT IN (
            SELECT id FROM weekly_digests
            WHERE userId = :userId AND isDeleted = 0
            ORDER BY weekStartDate DESC
            LIMIT :keepCount
        )
        AND userId = :userId
    """)
    suspend fun cleanupOldDigests(userId: String, keepCount: Int = WeeklyDigestEntity.MAX_STORED_DIGESTS): Int

    /**
     * Get count of stored digests
     */
    @Query("SELECT COUNT(*) FROM weekly_digests WHERE userId = :userId AND isDeleted = 0")
    suspend fun getDigestCountForUser(userId: String): Int

    @Query("SELECT COUNT(*) FROM weekly_digests WHERE userId = :userId AND isDeleted = 0")
    fun getDigestCount(userId: String): Flow<Int>

    // ==================== STATISTICS ====================

    /**
     * Get average entries per week over all digests
     */
    @Query("SELECT AVG(entriesCount) FROM weekly_digests WHERE isDeleted = 0 AND entriesCount > 0")
    suspend fun getAverageEntriesPerWeek(): Float?

    /**
     * Get average words per week over all digests
     */
    @Query("SELECT AVG(totalWordsWritten) FROM weekly_digests WHERE isDeleted = 0 AND totalWordsWritten > 0")
    suspend fun getAverageWordsPerWeek(): Float?

    /**
     * Get the most productive week
     */
    @Query("SELECT * FROM weekly_digests WHERE isDeleted = 0 ORDER BY entriesCount DESC LIMIT 1")
    suspend fun getMostProductiveWeek(): WeeklyDigestEntity?

    // ==================== SYNC ====================

    @Query("SELECT * FROM weekly_digests WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncDigests(): List<WeeklyDigestEntity>

    @Query("UPDATE weekly_digests SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, syncTime: Long = System.currentTimeMillis())

    // ==================== SOFT DELETE ====================

    @Query("UPDATE weekly_digests SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteDigest(id: Long)

    @Query("DELETE FROM weekly_digests WHERE isDeleted = 1")
    suspend fun purgeSoftDeleted(): Int
}
