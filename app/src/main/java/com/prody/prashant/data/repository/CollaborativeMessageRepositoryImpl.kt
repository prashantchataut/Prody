package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.CollaborativeMessageDao
import com.prody.prashant.domain.collaborative.*
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.repository.CollaborativeMessageRepository
import com.prody.prashant.domain.repository.ContactStats
import com.prody.prashant.domain.repository.MessagingStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CollaborativeMessageRepository
 */
@Singleton
class CollaborativeMessageRepositoryImpl @Inject constructor(
    private val dao: CollaborativeMessageDao,
    private val scheduler: CollaborativeMessageScheduler,
    private val deliveryService: MessageDeliveryService
) : CollaborativeMessageRepository {

    // ==================== SENT MESSAGES ====================

    override fun getAllSentMessages(userId: String): Flow<List<CollaborativeMessage>> {
        return dao.getAllSentMessages(userId).map { entities ->
            entities.map { CollaborativeMessage.fromEntity(it) }
        }
    }

    override suspend fun getMessageById(messageId: String): Result<CollaborativeMessage> {
        return try {
            val entity = dao.getMessageById(messageId)
            if (entity != null) {
                Result.Success(CollaborativeMessage.fromEntity(entity))
            } else {
                Result.Error("Message not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get message")
        }
    }

    override fun observeMessageById(messageId: String): Flow<CollaborativeMessage?> {
        return dao.observeMessageById(messageId).map { entity ->
            entity?.let { CollaborativeMessage.fromEntity(it) }
        }
    }

    override fun getScheduledMessages(userId: String): Flow<List<CollaborativeMessage>> {
        return dao.getScheduledMessages(userId).map { entities ->
            entities.map { CollaborativeMessage.fromEntity(it) }
        }
    }

    override fun getDeliveredMessages(userId: String): Flow<List<CollaborativeMessage>> {
        return dao.getDeliveredMessages(userId).map { entities ->
            entities.map { CollaborativeMessage.fromEntity(it) }
        }
    }

    override fun getMessagesForContact(contactId: String): Flow<List<CollaborativeMessage>> {
        return dao.getMessagesForContact(contactId).map { entities ->
            entities.map { CollaborativeMessage.fromEntity(it) }
        }
    }

    override fun getMessagesByOccasion(userId: String, occasion: Occasion): Flow<List<CollaborativeMessage>> {
        return dao.getMessagesByOccasion(userId, occasion.name.lowercase()).map { entities ->
            entities.map { CollaborativeMessage.fromEntity(it) }
        }
    }

    override suspend fun createMessage(message: CollaborativeMessage): Result<String> {
        return try {
            dao.insertMessage(message.toEntity())
            Result.Success(message.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create message")
        }
    }

    override suspend fun updateMessage(message: CollaborativeMessage): Result<Unit> {
        return try {
            dao.updateMessage(message.toEntity())

            // If message is scheduled and date changed, reschedule
            if (message.status == MessageStatus.SCHEDULED) {
                scheduler.rescheduleMessageDelivery(message)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update message")
        }
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        return try {
            // Cancel scheduled delivery if exists
            scheduler.cancelMessageDelivery(messageId)
            dao.softDeleteMessage(messageId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete message")
        }
    }

    override suspend fun scheduleMessage(message: CollaborativeMessage): Result<Unit> {
        return try {
            val updatedMessage = message.copy(status = MessageStatus.SCHEDULED)
            dao.updateMessage(updatedMessage.toEntity())
            scheduler.scheduleMessageDelivery(updatedMessage)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to schedule message")
        }
    }

    override suspend fun cancelScheduledMessage(messageId: String): Result<Unit> {
        return try {
            scheduler.cancelMessageDelivery(messageId)
            dao.updateMessageStatus(messageId, MessageStatus.PENDING.name.lowercase())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to cancel scheduled message")
        }
    }

    override suspend fun getSentMessageCount(userId: String): Int {
        return dao.getTotalMessageCount(userId)
    }

    // ==================== RECEIVED MESSAGES ====================

    override fun getAllReceivedMessages(): Flow<List<ReceivedCollaborativeMessage>> {
        return dao.getAllReceivedMessages().map { entities ->
            entities.map { ReceivedCollaborativeMessage.fromEntity(it) }
        }
    }

    override suspend fun getReceivedMessageById(messageId: String): Result<ReceivedCollaborativeMessage> {
        return try {
            val entity = dao.getReceivedMessageById(messageId)
            if (entity != null) {
                Result.Success(ReceivedCollaborativeMessage.fromEntity(entity))
            } else {
                Result.Error("Message not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get received message")
        }
    }

    override fun observeReceivedMessageById(messageId: String): Flow<ReceivedCollaborativeMessage?> {
        return dao.observeReceivedMessageById(messageId).map { entity ->
            entity?.let { ReceivedCollaborativeMessage.fromEntity(it) }
        }
    }

    override fun getUnreadReceivedMessages(): Flow<List<ReceivedCollaborativeMessage>> {
        return dao.getUnreadReceivedMessages().map { entities ->
            entities.map { ReceivedCollaborativeMessage.fromEntity(it) }
        }
    }

    override fun getUnreadReceivedMessageCount(): Flow<Int> {
        return dao.getUnreadReceivedMessageCount()
    }

    override fun getFavoriteReceivedMessages(): Flow<List<ReceivedCollaborativeMessage>> {
        return dao.getFavoriteReceivedMessages().map { entities ->
            entities.map { ReceivedCollaborativeMessage.fromEntity(it) }
        }
    }

    override suspend fun markReceivedAsRead(messageId: String): Result<Unit> {
        return try {
            dao.markReceivedAsRead(messageId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to mark message as read")
        }
    }

    override suspend fun toggleReceivedFavorite(messageId: String, isFavorite: Boolean): Result<Unit> {
        return try {
            dao.updateReceivedFavoriteStatus(messageId, isFavorite)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update favorite status")
        }
    }

    override suspend fun setReplyMessage(messageId: String, replyMessageId: String): Result<Unit> {
        return try {
            dao.setReplyMessage(messageId, replyMessageId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to set reply message")
        }
    }

    override suspend fun deleteReceivedMessage(messageId: String): Result<Unit> {
        return try {
            dao.softDeleteReceivedMessage(messageId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete received message")
        }
    }

    // ==================== CONTACTS ====================

    override fun getAllContacts(userId: String): Flow<List<MessageContact>> {
        return dao.getAllContacts(userId).map { entities ->
            entities.map { MessageContact.fromEntity(it) }
        }
    }

    override suspend fun getContactById(contactId: String): Result<MessageContact> {
        return try {
            val entity = dao.getContactById(contactId)
            if (entity != null) {
                Result.Success(MessageContact.fromEntity(entity))
            } else {
                Result.Error("Contact not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get contact")
        }
    }

    override fun observeContactById(contactId: String): Flow<MessageContact?> {
        return dao.observeContactById(contactId).map { entity ->
            entity?.let { MessageContact.fromEntity(it) }
        }
    }

    override fun getFavoriteContacts(userId: String): Flow<List<MessageContact>> {
        return dao.getFavoriteContacts(userId).map { entities ->
            entities.map { MessageContact.fromEntity(it) }
        }
    }

    override fun searchContacts(userId: String, query: String): Flow<List<MessageContact>> {
        return dao.searchContacts(userId, query).map { entities ->
            entities.map { MessageContact.fromEntity(it) }
        }
    }

    override suspend fun createContact(contact: MessageContact): Result<String> {
        return try {
            dao.insertContact(contact.toEntity())
            Result.Success(contact.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create contact")
        }
    }

    override suspend fun updateContact(contact: MessageContact): Result<Unit> {
        return try {
            dao.updateContact(contact.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update contact")
        }
    }

    override suspend fun deleteContact(contactId: String): Result<Unit> {
        return try {
            dao.softDeleteContact(contactId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete contact")
        }
    }

    override suspend fun toggleContactFavorite(contactId: String, isFavorite: Boolean): Result<Unit> {
        return try {
            dao.updateContactFavoriteStatus(contactId, isFavorite)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update contact favorite status")
        }
    }

    override suspend fun getContactCount(userId: String): Int {
        return dao.getContactCount(userId)
    }

    // ==================== OCCASIONS ====================

    override fun getAllOccasions(userId: String): Flow<List<MessageOccasion>> {
        return dao.getAllOccasions(userId).map { entities ->
            entities.map { MessageOccasion.fromEntity(it) }
        }
    }

    override suspend fun getOccasionById(occasionId: String): Result<MessageOccasion> {
        return try {
            val entity = dao.getOccasionById(occasionId)
            if (entity != null) {
                Result.Success(MessageOccasion.fromEntity(entity))
            } else {
                Result.Error("Occasion not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get occasion")
        }
    }

    override fun getOccasionsForContact(contactId: String): Flow<List<MessageOccasion>> {
        return dao.getOccasionsForContact(contactId).map { entities ->
            entities.map { MessageOccasion.fromEntity(it) }
        }
    }

    override fun getUpcomingOccasions(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<MessageOccasion>> {
        val startMillis = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return dao.getUpcomingOccasions(userId, startMillis, endMillis).map { entities ->
            entities.map { MessageOccasion.fromEntity(it) }
        }
    }

    override suspend fun createOccasion(occasion: MessageOccasion): Result<String> {
        return try {
            dao.insertOccasion(occasion.toEntity())

            // Schedule reminder for this occasion
            val contactEntity = dao.getContactById(occasion.contactId)
            if (contactEntity != null) {
                val contact = MessageContact.fromEntity(contactEntity)
                scheduler.scheduleOccasionReminder(occasion, contact)
            }

            Result.Success(occasion.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create occasion")
        }
    }

    override suspend fun updateOccasion(occasion: MessageOccasion): Result<Unit> {
        return try {
            dao.updateOccasion(occasion.toEntity())

            // Reschedule reminder
            val contactEntity = dao.getContactById(occasion.contactId)
            if (contactEntity != null) {
                val contact = MessageContact.fromEntity(contactEntity)
                scheduler.cancelOccasionReminder(occasion.id)
                scheduler.scheduleOccasionReminder(occasion, contact)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update occasion")
        }
    }

    override suspend fun deleteOccasion(occasionId: String): Result<Unit> {
        return try {
            scheduler.cancelOccasionReminder(occasionId)
            dao.softDeleteOccasion(occasionId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete occasion")
        }
    }

    // ==================== STATISTICS ====================

    override suspend fun getMessagingStats(userId: String): MessagingStats {
        val totalSent = dao.getTotalMessageCount(userId)
        val uniqueRecipients = dao.getUniqueRecipientCount(userId)
        val futureMessages = dao.getFutureMessageCount(userId)

        return MessagingStats(
            totalSent = totalSent,
            totalReceived = 0, // Will be populated when multi-user sync is implemented
            totalScheduled = futureMessages,
            uniqueRecipients = uniqueRecipients,
            favoriteMessagesReceived = 0,
            upcomingDeliveries = futureMessages
        )
    }

    override suspend fun getContactStats(contactId: String): ContactStats? {
        return try {
            val entity = dao.getContactById(contactId)
            if (entity != null) {
                ContactStats(
                    messagesSent = entity.messagesSent,
                    lastMessageAt = entity.lastMessageAt?.let {
                        LocalDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(it),
                            ZoneId.systemDefault()
                        )
                    },
                    upcomingOccasions = 0 // Can be calculated if needed
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // ==================== DELIVERY ====================

    override suspend fun deliverMessage(messageId: String): Result<Unit> {
        return try {
            val entity = dao.getMessageById(messageId)
            if (entity != null) {
                val message = CollaborativeMessage.fromEntity(entity)
                val deliveryResult = deliveryService.deliverMessage(message)

                if (deliveryResult.isSuccess) {
                    Result.Success(Unit)
                } else {
                    Result.Error("Delivery failed: ${(deliveryResult as DeliveryResult.Failure).error}")
                }
            } else {
                Result.Error("Message not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to deliver message")
        }
    }

    override suspend fun retryFailedMessage(messageId: String): Result<Unit> {
        return try {
            val deliveryResult = deliveryService.retryMessageDelivery(messageId)

            if (deliveryResult.isSuccess) {
                Result.Success(Unit)
            } else {
                Result.Error("Retry failed: ${(deliveryResult as DeliveryResult.Failure).error}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to retry message")
        }
    }
}
