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
}
