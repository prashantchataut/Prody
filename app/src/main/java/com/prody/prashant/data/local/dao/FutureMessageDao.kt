package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.FutureMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FutureMessageDao {

    @Query("SELECT * FROM future_messages ORDER BY deliveryDate ASC")
    fun getAllMessages(): Flow<List<FutureMessageEntity>>

    @Query("SELECT * FROM future_messages WHERE id = :id")
    suspend fun getMessageById(id: Long): FutureMessageEntity?

    @Query("SELECT * FROM future_messages WHERE isDelivered = 0 ORDER BY deliveryDate ASC")
    fun getPendingMessages(): Flow<List<FutureMessageEntity>>

    @Query("SELECT * FROM future_messages WHERE isDelivered = 1 ORDER BY deliveredAt DESC")
    fun getDeliveredMessages(): Flow<List<FutureMessageEntity>>

    @Query("SELECT * FROM future_messages WHERE isDelivered = 1 AND isRead = 0 ORDER BY deliveredAt DESC")
    fun getUnreadDeliveredMessages(): Flow<List<FutureMessageEntity>>

    @Query("SELECT * FROM future_messages WHERE deliveryDate <= :currentTime AND isDelivered = 0")
    suspend fun getMessagesReadyForDelivery(currentTime: Long): List<FutureMessageEntity>

    @Query("SELECT * FROM future_messages WHERE category = :category ORDER BY deliveryDate ASC")
    fun getMessagesByCategory(category: String): Flow<List<FutureMessageEntity>>

    @Query("SELECT COUNT(*) FROM future_messages WHERE isDelivered = 0")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM future_messages WHERE isDelivered = 1 AND isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM future_messages")
    fun getTotalCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: FutureMessageEntity): Long

    @Update
    suspend fun updateMessage(message: FutureMessageEntity)

    @Delete
    suspend fun deleteMessage(message: FutureMessageEntity)

    @Query("DELETE FROM future_messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    @Query("UPDATE future_messages SET isDelivered = 1, deliveredAt = :deliveredAt WHERE id = :id")
    suspend fun markAsDelivered(id: Long, deliveredAt: Long = System.currentTimeMillis())

    @Query("UPDATE future_messages SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("SELECT * FROM future_messages WHERE deliveryDate BETWEEN :startDate AND :endDate ORDER BY deliveryDate ASC")
    fun getMessagesByDateRange(startDate: Long, endDate: Long): Flow<List<FutureMessageEntity>>

    @Query("SELECT MIN(deliveryDate) FROM future_messages WHERE isDelivered = 0")
    suspend fun getNextDeliveryTime(): Long?

    // Backup methods
    @Query("SELECT * FROM future_messages ORDER BY deliveryDate ASC")
    suspend fun getAllMessagesSync(): List<FutureMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<FutureMessageEntity>)

    @Query("DELETE FROM future_messages")
    suspend fun deleteAllMessages()

    // Data Hygiene - Soft delete management
    @Query("UPDATE future_messages SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteMessage(id: Long)

    @Query("SELECT * FROM future_messages WHERE isDeleted = 1")
    suspend fun getSoftDeletedMessages(): List<FutureMessageEntity>

    @Query("DELETE FROM future_messages WHERE isDeleted = 1")
    suspend fun purgeSoftDeleted(): Int

    // Sync-related queries
    @Query("SELECT * FROM future_messages WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncMessages(): List<FutureMessageEntity>

    @Query("UPDATE future_messages SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, syncTime: Long = System.currentTimeMillis())

    @Query("SELECT * FROM future_messages WHERE userId = :userId ORDER BY deliveryDate ASC")
    fun getMessagesByUser(userId: String): Flow<List<FutureMessageEntity>>

    // Search across future messages
    @Query("""
        SELECT * FROM future_messages
        WHERE (title LIKE '%' || :query || '%'
            OR content LIKE '%' || :query || '%'
            OR category LIKE '%' || :query || '%')
        AND isDeleted = 0
        ORDER BY deliveryDate DESC
    """)
    fun searchMessages(query: String): Flow<List<FutureMessageEntity>>

    // ==================== ACTIVE PROGRESS QUERIES ====================

    /**
     * Get message count since a timestamp (for daily/weekly progress)
     */
    @Query("SELECT COUNT(*) FROM future_messages WHERE createdAt >= :since AND isDeleted = 0")
    suspend fun getMessageCountSince(since: Long): Int

    /**
     * Get total message count (for "Next Action" suggestions)
     */
    @Query("SELECT COUNT(*) FROM future_messages WHERE isDeleted = 0")
    suspend fun getTotalMessageCount(): Int
}
