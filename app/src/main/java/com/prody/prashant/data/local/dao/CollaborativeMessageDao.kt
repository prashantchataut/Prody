package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for collaborative message operations
 */
@Dao
interface CollaborativeMessageDao {

    // ==================== SENT MESSAGES ====================

    @Query("SELECT * FROM collaborative_messages WHERE userId = :userId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllSentMessages(userId: String = "local"): Flow<List<CollaborativeMessageEntity>>

    @Query("SELECT * FROM collaborative_messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): CollaborativeMessageEntity?

    @Query("SELECT * FROM collaborative_messages WHERE id = :messageId")
    fun observeMessageById(messageId: String): Flow<CollaborativeMessageEntity?>

    @Query("SELECT * FROM collaborative_messages WHERE userId = :userId AND status = 'scheduled' AND isDeleted = 0 ORDER BY deliveryDate ASC")
    fun getScheduledMessages(userId: String = "local"): Flow<List<CollaborativeMessageEntity>>

    @Query("SELECT * FROM collaborative_messages WHERE userId = :userId AND isDelivered = 1 AND isDeleted = 0 ORDER BY deliveredAt DESC")
    fun getDeliveredMessages(userId: String = "local"): Flow<List<CollaborativeMessageEntity>>

    @Query("SELECT * FROM collaborative_messages WHERE userId = :userId AND status = 'pending' AND isDeleted = 0")
    fun getPendingMessages(userId: String = "local"): Flow<List<CollaborativeMessageEntity>>

    @Query("SELECT * FROM collaborative_messages WHERE recipientId = :recipientId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getMessagesForContact(recipientId: String): Flow<List<CollaborativeMessageEntity>>

    @Query("SELECT * FROM collaborative_messages WHERE recipientContact = :contact AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getMessagesByContact(contact: String): Flow<List<CollaborativeMessageEntity>>

    @Query("SELECT * FROM collaborative_messages WHERE userId = :userId AND occasion = :occasion AND isDeleted = 0 ORDER BY deliveryDate ASC")
    fun getMessagesByOccasion(userId: String = "local", occasion: String): Flow<List<CollaborativeMessageEntity>>

    @Query("SELECT * FROM collaborative_messages WHERE deliveryDate <= :timestamp AND status = 'scheduled' AND isDeleted = 0")
    suspend fun getMessagesReadyForDelivery(timestamp: Long): List<CollaborativeMessageEntity>

    @Query("SELECT * FROM collaborative_messages WHERE userId = :userId AND deliveryDate BETWEEN :startDate AND :endDate AND isDeleted = 0 ORDER BY deliveryDate ASC")
    fun getMessagesInDateRange(
        userId: String = "local",
        startDate: Long,
        endDate: Long
    ): Flow<List<CollaborativeMessageEntity>>

    @Query("SELECT COUNT(*) FROM collaborative_messages WHERE userId = :userId AND isDeleted = 0")
    suspend fun getTotalMessageCount(userId: String = "local"): Int

    @Query("SELECT COUNT(*) FROM collaborative_messages WHERE userId = :userId AND status = 'scheduled' AND isDeleted = 0")
    fun getScheduledMessageCount(userId: String = "local"): Flow<Int>

    @Query("SELECT COUNT(*) FROM collaborative_messages WHERE userId = :userId AND isDelivered = 1 AND isDeleted = 0")
    fun getDeliveredMessageCount(userId: String = "local"): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: CollaborativeMessageEntity): Long

    @Update
    suspend fun updateMessage(message: CollaborativeMessageEntity)

    @Delete
    suspend fun deleteMessage(message: CollaborativeMessageEntity)

    @Query("DELETE FROM collaborative_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("UPDATE collaborative_messages SET isDeleted = 1 WHERE id = :messageId")
    suspend fun softDeleteMessage(messageId: String)

    @Query("UPDATE collaborative_messages SET status = :status WHERE id = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: String)

    @Query("UPDATE collaborative_messages SET isDelivered = 1, deliveredAt = :deliveredAt, status = 'delivered' WHERE id = :messageId")
    suspend fun markAsDelivered(messageId: String, deliveredAt: Long = System.currentTimeMillis())

    @Query("UPDATE collaborative_messages SET retryCount = retryCount + 1 WHERE id = :messageId")
    suspend fun incrementRetryCount(messageId: String)

    // ==================== RECEIVED MESSAGES ====================

    @Query("SELECT * FROM received_collaborative_messages WHERE isDeleted = 0 ORDER BY deliveredAt DESC")
    fun getAllReceivedMessages(): Flow<List<ReceivedCollaborativeMessageEntity>>

    @Query("SELECT * FROM received_collaborative_messages WHERE id = :messageId")
    suspend fun getReceivedMessageById(messageId: String): ReceivedCollaborativeMessageEntity?

    @Query("SELECT * FROM received_collaborative_messages WHERE id = :messageId")
    fun observeReceivedMessageById(messageId: String): Flow<ReceivedCollaborativeMessageEntity?>

    @Query("SELECT * FROM received_collaborative_messages WHERE senderId = :senderId AND isDeleted = 0 ORDER BY deliveredAt DESC")
    fun getReceivedMessagesFromSender(senderId: String): Flow<List<ReceivedCollaborativeMessageEntity>>

    @Query("SELECT * FROM received_collaborative_messages WHERE isRead = 0 AND isDeleted = 0 ORDER BY deliveredAt DESC")
    fun getUnreadReceivedMessages(): Flow<List<ReceivedCollaborativeMessageEntity>>

    @Query("SELECT COUNT(*) FROM received_collaborative_messages WHERE isRead = 0 AND isDeleted = 0")
    fun getUnreadReceivedMessageCount(): Flow<Int>

    @Query("SELECT * FROM received_collaborative_messages WHERE isFavorite = 1 AND isDeleted = 0 ORDER BY deliveredAt DESC")
    fun getFavoriteReceivedMessages(): Flow<List<ReceivedCollaborativeMessageEntity>>

    @Query("SELECT * FROM received_collaborative_messages WHERE occasion = :occasion AND isDeleted = 0 ORDER BY deliveredAt DESC")
    fun getReceivedMessagesByOccasion(occasion: String): Flow<List<ReceivedCollaborativeMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceivedMessage(message: ReceivedCollaborativeMessageEntity): Long

    @Update
    suspend fun updateReceivedMessage(message: ReceivedCollaborativeMessageEntity)

    @Delete
    suspend fun deleteReceivedMessage(message: ReceivedCollaborativeMessageEntity)

    @Query("DELETE FROM received_collaborative_messages WHERE id = :messageId")
    suspend fun deleteReceivedMessageById(messageId: String)

    @Query("UPDATE received_collaborative_messages SET isDeleted = 1 WHERE id = :messageId")
    suspend fun softDeleteReceivedMessage(messageId: String)

    @Query("UPDATE received_collaborative_messages SET isRead = 1, readAt = :readAt WHERE id = :messageId")
    suspend fun markReceivedAsRead(messageId: String, readAt: Long = System.currentTimeMillis())

    @Query("UPDATE received_collaborative_messages SET isFavorite = :isFavorite WHERE id = :messageId")
    suspend fun updateReceivedFavoriteStatus(messageId: String, isFavorite: Boolean)

    @Query("UPDATE received_collaborative_messages SET replyMessageId = :replyMessageId WHERE id = :messageId")
    suspend fun setReplyMessage(messageId: String, replyMessageId: String)

    // ==================== CONTACTS ====================

    @Query("SELECT * FROM message_contacts WHERE userId = :userId AND isDeleted = 0 ORDER BY lastMessageAt DESC")
    fun getAllContacts(userId: String = "local"): Flow<List<MessageContactEntity>>

    @Query("SELECT * FROM message_contacts WHERE id = :contactId")
    suspend fun getContactById(contactId: String): MessageContactEntity?

    @Query("SELECT * FROM message_contacts WHERE id = :contactId")
    fun observeContactById(contactId: String): Flow<MessageContactEntity?>

    @Query("SELECT * FROM message_contacts WHERE userId = :userId AND isFavorite = 1 AND isDeleted = 0 ORDER BY displayName ASC")
    fun getFavoriteContacts(userId: String = "local"): Flow<List<MessageContactEntity>>

    @Query("SELECT * FROM message_contacts WHERE userId = :userId AND contactMethod = :method AND isDeleted = 0 ORDER BY displayName ASC")
    fun getContactsByMethod(userId: String = "local", method: String): Flow<List<MessageContactEntity>>

    @Query("SELECT * FROM message_contacts WHERE contactValue = :contactValue")
    suspend fun getContactByValue(contactValue: String): MessageContactEntity?

    @Query("SELECT * FROM message_contacts WHERE userId = :userId AND displayName LIKE '%' || :query || '%' AND isDeleted = 0")
    fun searchContacts(userId: String = "local", query: String): Flow<List<MessageContactEntity>>

    @Query("SELECT COUNT(*) FROM message_contacts WHERE userId = :userId AND isDeleted = 0")
    suspend fun getContactCount(userId: String = "local"): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: MessageContactEntity): Long

    @Update
    suspend fun updateContact(contact: MessageContactEntity)

    @Delete
    suspend fun deleteContact(contact: MessageContactEntity)

    @Query("DELETE FROM message_contacts WHERE id = :contactId")
    suspend fun deleteContactById(contactId: String)

    @Query("UPDATE message_contacts SET isDeleted = 1 WHERE id = :contactId")
    suspend fun softDeleteContact(contactId: String)

    @Query("UPDATE message_contacts SET messagesSent = messagesSent + 1, lastMessageAt = :timestamp WHERE id = :contactId")
    suspend fun incrementContactMessageCount(contactId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE message_contacts SET isFavorite = :isFavorite WHERE id = :contactId")
    suspend fun updateContactFavoriteStatus(contactId: String, isFavorite: Boolean)

    // ==================== OCCASIONS ====================

    @Query("SELECT * FROM message_occasions WHERE userId = :userId AND isDeleted = 0 ORDER BY date ASC")
    fun getAllOccasions(userId: String = "local"): Flow<List<MessageOccasionEntity>>

    @Query("SELECT * FROM message_occasions WHERE id = :occasionId")
    suspend fun getOccasionById(occasionId: String): MessageOccasionEntity?

    @Query("SELECT * FROM message_occasions WHERE id = :occasionId")
    fun observeOccasionById(occasionId: String): Flow<MessageOccasionEntity?>

    @Query("SELECT * FROM message_occasions WHERE contactId = :contactId AND isDeleted = 0 ORDER BY date ASC")
    fun getOccasionsForContact(contactId: String): Flow<List<MessageOccasionEntity>>

    @Query("SELECT * FROM message_occasions WHERE userId = :userId AND occasionType = :type AND isDeleted = 0 ORDER BY date ASC")
    fun getOccasionsByType(userId: String = "local", type: String): Flow<List<MessageOccasionEntity>>

    @Query("SELECT * FROM message_occasions WHERE userId = :userId AND date BETWEEN :startDate AND :endDate AND isDeleted = 0 ORDER BY date ASC")
    fun getUpcomingOccasions(
        userId: String = "local",
        startDate: Long,
        endDate: Long
    ): Flow<List<MessageOccasionEntity>>

    @Query("SELECT * FROM message_occasions WHERE userId = :userId AND isRecurring = 1 AND isDeleted = 0")
    suspend fun getRecurringOccasions(userId: String = "local"): List<MessageOccasionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOccasion(occasion: MessageOccasionEntity): Long

    @Update
    suspend fun updateOccasion(occasion: MessageOccasionEntity)

    @Delete
    suspend fun deleteOccasion(occasion: MessageOccasionEntity)

    @Query("DELETE FROM message_occasions WHERE id = :occasionId")
    suspend fun deleteOccasionById(occasionId: String)

    @Query("UPDATE message_occasions SET isDeleted = 1 WHERE id = :occasionId")
    suspend fun softDeleteOccasion(occasionId: String)

    @Query("UPDATE message_occasions SET lastNotifiedYear = :year WHERE id = :occasionId")
    suspend fun updateLastNotifiedYear(occasionId: String, year: Int)

    // ==================== STATISTICS ====================

    @Query("""
        SELECT COUNT(DISTINCT recipientId)
        FROM collaborative_messages
        WHERE userId = :userId AND recipientId IS NOT NULL AND isDeleted = 0
    """)
    suspend fun getUniqueRecipientCount(userId: String = "local"): Int

    @Query("""
        SELECT COUNT(*)
        FROM collaborative_messages
        WHERE userId = :userId AND deliveryDate > :now AND isDeleted = 0
    """)
    suspend fun getFutureMessageCount(userId: String = "local", now: Long = System.currentTimeMillis()): Int

    // ==================== SYNC ====================

    @Query("SELECT * FROM collaborative_messages WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncMessages(): List<CollaborativeMessageEntity>

    @Query("UPDATE collaborative_messages SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :messageId")
    suspend fun updateMessageSyncStatus(messageId: String, status: String, syncTime: Long = System.currentTimeMillis())

    @Query("SELECT * FROM received_collaborative_messages WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncReceivedMessages(): List<ReceivedCollaborativeMessageEntity>

    @Query("UPDATE received_collaborative_messages SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :messageId")
    suspend fun updateReceivedMessageSyncStatus(messageId: String, status: String, syncTime: Long = System.currentTimeMillis())

    @Query("SELECT * FROM message_contacts WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncContacts(): List<MessageContactEntity>

    @Query("UPDATE message_contacts SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :contactId")
    suspend fun updateContactSyncStatus(contactId: String, status: String, syncTime: Long = System.currentTimeMillis())
}
