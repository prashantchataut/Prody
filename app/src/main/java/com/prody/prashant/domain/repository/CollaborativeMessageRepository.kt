package com.prody.prashant.domain.repository

import com.prody.prashant.domain.collaborative.*
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for collaborative message operations
 */
interface CollaborativeMessageRepository {

    // ==================== SENT MESSAGES ====================

    fun getAllSentMessages(userId: String = "local"): Flow<List<CollaborativeMessage>>

    suspend fun getMessageById(messageId: String): Result<CollaborativeMessage>

    fun observeMessageById(messageId: String): Flow<CollaborativeMessage?>

    fun getScheduledMessages(userId: String = "local"): Flow<List<CollaborativeMessage>>

    fun getDeliveredMessages(userId: String = "local"): Flow<List<CollaborativeMessage>>

    fun getMessagesForContact(contactId: String): Flow<List<CollaborativeMessage>>

    fun getMessagesByOccasion(userId: String = "local", occasion: Occasion): Flow<List<CollaborativeMessage>>

    suspend fun createMessage(message: CollaborativeMessage): Result<String>

    suspend fun updateMessage(message: CollaborativeMessage): Result<Unit>

    suspend fun deleteMessage(messageId: String): Result<Unit>

    suspend fun scheduleMessage(message: CollaborativeMessage): Result<Unit>

    suspend fun cancelScheduledMessage(messageId: String): Result<Unit>

    suspend fun getSentMessageCount(userId: String = "local"): Int

    // ==================== RECEIVED MESSAGES ====================

    fun getAllReceivedMessages(): Flow<List<ReceivedCollaborativeMessage>>

    suspend fun getReceivedMessageById(messageId: String): Result<ReceivedCollaborativeMessage>

    fun observeReceivedMessageById(messageId: String): Flow<ReceivedCollaborativeMessage?>

    fun getUnreadReceivedMessages(): Flow<List<ReceivedCollaborativeMessage>>

    fun getUnreadReceivedMessageCount(): Flow<Int>

    fun getFavoriteReceivedMessages(): Flow<List<ReceivedCollaborativeMessage>>

    suspend fun markReceivedAsRead(messageId: String): Result<Unit>

    suspend fun toggleReceivedFavorite(messageId: String, isFavorite: Boolean): Result<Unit>

    suspend fun setReplyMessage(messageId: String, replyMessageId: String): Result<Unit>

    suspend fun deleteReceivedMessage(messageId: String): Result<Unit>

    // ==================== CONTACTS ====================

    fun getAllContacts(userId: String = "local"): Flow<List<MessageContact>>

    suspend fun getContactById(contactId: String): Result<MessageContact>

    fun observeContactById(contactId: String): Flow<MessageContact?>

    fun getFavoriteContacts(userId: String = "local"): Flow<List<MessageContact>>

    fun searchContacts(userId: String = "local", query: String): Flow<List<MessageContact>>

    suspend fun createContact(contact: MessageContact): Result<String>

    suspend fun updateContact(contact: MessageContact): Result<Unit>

    suspend fun deleteContact(contactId: String): Result<Unit>

    suspend fun toggleContactFavorite(contactId: String, isFavorite: Boolean): Result<Unit>

    suspend fun getContactCount(userId: String = "local"): Int

    // ==================== OCCASIONS ====================

    fun getAllOccasions(userId: String = "local"): Flow<List<MessageOccasion>>

    suspend fun getOccasionById(occasionId: String): Result<MessageOccasion>

    fun getOccasionsForContact(contactId: String): Flow<List<MessageOccasion>>

    fun getUpcomingOccasions(
        userId: String = "local",
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<MessageOccasion>>

    suspend fun createOccasion(occasion: MessageOccasion): Result<String>

    suspend fun updateOccasion(occasion: MessageOccasion): Result<Unit>

    suspend fun deleteOccasion(occasionId: String): Result<Unit>

    // ==================== STATISTICS ====================

    suspend fun getMessagingStats(userId: String = "local"): MessagingStats

    suspend fun getContactStats(contactId: String): ContactStats?

    // ==================== DELIVERY ====================

    suspend fun deliverMessage(messageId: String): Result<Unit>

    suspend fun retryFailedMessage(messageId: String): Result<Unit>
}

/**
 * Statistics for user's collaborative messaging
 */
data class MessagingStats(
    val totalSent: Int,
    val totalReceived: Int,
    val totalScheduled: Int,
    val uniqueRecipients: Int,
    val favoriteMessagesReceived: Int,
    val upcomingDeliveries: Int
)

/**
 * Statistics for a specific contact
 */
data class ContactStats(
    val messagesSent: Int,
    val lastMessageAt: LocalDateTime?,
    val upcomingOccasions: Int
)
