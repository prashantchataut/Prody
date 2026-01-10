package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.MessageAnniversaryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for MessageAnniversary operations.
 *
 * Handles tracking and querying of message anniversaries for thoughtful
 * reminders about messages written in the past.
 */
@Dao
interface MessageAnniversaryDao {

    // ==================== BASIC CRUD ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnniversary(anniversary: MessageAnniversaryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnniversaries(anniversaries: List<MessageAnniversaryEntity>)

    @Update
    suspend fun updateAnniversary(anniversary: MessageAnniversaryEntity)

    @Delete
    suspend fun deleteAnniversary(anniversary: MessageAnniversaryEntity)

    @Query("DELETE FROM message_anniversaries WHERE id = :id")
    suspend fun deleteAnniversaryById(id: Long)

    @Query("UPDATE message_anniversaries SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteAnniversary(id: Long)

    // ==================== RETRIEVAL QUERIES ====================

    @Query("SELECT * FROM message_anniversaries WHERE userId = :userId AND isDeleted = 0 ORDER BY anniversaryDate DESC")
    fun getAllAnniversaries(userId: String): Flow<List<MessageAnniversaryEntity>>

    @Query("SELECT * FROM message_anniversaries WHERE id = :id AND isDeleted = 0")
    suspend fun getAnniversaryById(id: Long): MessageAnniversaryEntity?

    @Query("SELECT * FROM message_anniversaries WHERE originalMessageId = :messageId AND isDeleted = 0 ORDER BY yearsAgo DESC")
    fun getAnniversariesForMessage(messageId: Long): Flow<List<MessageAnniversaryEntity>>

    // ==================== UPCOMING ANNIVERSARIES ====================

    /**
     * Get anniversaries coming up within the next N days
     */
    @Query("""
        SELECT * FROM message_anniversaries
        WHERE userId = :userId
        AND isDeleted = 0
        AND notifiedAt IS NULL
        AND anniversaryDate >= :startDate
        AND anniversaryDate <= :endDate
        ORDER BY anniversaryDate ASC
    """)
    suspend fun getUpcomingAnniversaries(
        userId: String,
        startDate: Long,
        endDate: Long
    ): List<MessageAnniversaryEntity>

    /**
     * Get anniversaries that should be shown today
     */
    @Query("""
        SELECT * FROM message_anniversaries
        WHERE userId = :userId
        AND isDeleted = 0
        AND notifiedAt IS NULL
        AND anniversaryDate >= :todayStart
        AND anniversaryDate < :todayEnd
        ORDER BY yearsAgo DESC
    """)
    suspend fun getTodayAnniversaries(
        userId: String,
        todayStart: Long,
        todayEnd: Long
    ): List<MessageAnniversaryEntity>

    // ==================== NOTIFICATION TRACKING ====================

    /**
     * Mark anniversary as notified
     */
    @Query("UPDATE message_anniversaries SET notifiedAt = :notifiedAt WHERE id = :id")
    suspend fun markAsNotified(id: Long, notifiedAt: Long = System.currentTimeMillis())

    /**
     * Mark anniversary as read
     */
    @Query("UPDATE message_anniversaries SET isRead = 1, readAt = :readAt WHERE id = :id")
    suspend fun markAsRead(id: Long, readAt: Long = System.currentTimeMillis())

    /**
     * Link reflection journal entry
     */
    @Query("UPDATE message_anniversaries SET hasReflection = 1, reflectionJournalId = :journalId WHERE id = :id")
    suspend fun linkReflection(id: Long, journalId: Long)

    // ==================== ANNIVERSARY GENERATION ====================

    /**
     * Get messages that need anniversaries created for this year
     * (Messages written at least 1 year ago)
     */
    @Query("""
        SELECT fm.* FROM future_messages fm
        WHERE fm.userId = :userId
        AND fm.isDeleted = 0
        AND fm.createdAt < :oneYearAgo
        AND NOT EXISTS (
            SELECT 1 FROM message_anniversaries ma
            WHERE ma.originalMessageId = fm.id
            AND ma.yearsAgo = :targetYear
            AND ma.isDeleted = 0
        )
    """)
    suspend fun getMessagesNeedingAnniversaries(
        userId: String,
        oneYearAgo: Long,
        targetYear: Int
    ): List<com.prody.prashant.data.local.entity.FutureMessageEntity>

    // ==================== STATISTICS ====================

    /**
     * Get total anniversary count
     */
    @Query("SELECT COUNT(*) FROM message_anniversaries WHERE userId = :userId AND isDeleted = 0")
    fun getAnniversaryCount(userId: String): Flow<Int>

    /**
     * Get unread anniversary count
     */
    @Query("SELECT COUNT(*) FROM message_anniversaries WHERE userId = :userId AND isRead = 0 AND notifiedAt IS NOT NULL AND isDeleted = 0")
    fun getUnreadAnniversaryCount(userId: String): Flow<Int>

    /**
     * Get anniversaries with reflections
     */
    @Query("SELECT COUNT(*) FROM message_anniversaries WHERE userId = :userId AND hasReflection = 1 AND isDeleted = 0")
    suspend fun getReflectionCount(userId: String): Int

    // ==================== CLEANUP ====================

    @Query("DELETE FROM message_anniversaries WHERE isDeleted = 1")
    suspend fun purgeSoftDeleted(): Int

    @Query("SELECT * FROM message_anniversaries WHERE isDeleted = 1")
    suspend fun getSoftDeletedAnniversaries(): List<MessageAnniversaryEntity>

    /**
     * Delete old anniversaries that were never notified (cleanup stale data)
     */
    @Query("DELETE FROM message_anniversaries WHERE notifiedAt IS NULL AND anniversaryDate < :cutoffDate")
    suspend fun deleteStaleAnniversaries(cutoffDate: Long): Int
}
