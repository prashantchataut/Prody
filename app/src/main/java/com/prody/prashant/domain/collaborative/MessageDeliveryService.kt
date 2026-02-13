package com.prody.prashant.domain.collaborative

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import com.prody.prashant.data.local.dao.CollaborativeMessageDao
import com.prody.prashant.notification.CollaborativeMessageNotifications
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for delivering collaborative messages via different channels.
 * Handles in-app, email, and SMS delivery methods.
 */
@Singleton
class MessageDeliveryService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: CollaborativeMessageDao,
    private val notifications: CollaborativeMessageNotifications
) {
    companion object {
        private const val TAG = "MessageDeliveryService"
        private const val MAX_RETRY_COUNT = 3
    }

    /**
     * Deliver a message using its configured delivery method
     */
    suspend fun deliverMessage(message: CollaborativeMessage): DeliveryResult {
        return withContext(Dispatchers.IO) {
            try {
                com.prody.prashant.util.AppLogger.d(TAG, "Attempting to deliver message ${message.id} via ${message.recipient.method}")

                val result = when (message.recipient.method) {
                    ContactMethod.APP_USER -> deliverInApp(message)
                    ContactMethod.IN_APP -> deliverInApp(message)
                    ContactMethod.EMAIL -> deliverViaEmail(message)
                    ContactMethod.PHONE -> deliverViaSMS(message)
                    ContactMethod.SMS -> deliverViaSMS(message)
                    ContactMethod.WHATSAPP -> deliverViaWhatsApp(message)
                }

                when (result) {
                    is DeliveryResult.Success -> {
                        dao.markAsDelivered(message.id)
                        dao.updateMessageStatus(message.id, MessageStatus.DELIVERED.name.lowercase())

                        // Update contact's message count
                        val contactEntity = dao.getContactByValue(message.recipient.contactValue)
                        if (contactEntity != null) {
                            dao.incrementContactMessageCount(contactEntity.id)
                        }

                        // Notify sender that message was delivered
                        notifications.notifyMessageDelivered(message)

                        com.prody.prashant.util.AppLogger.d(TAG, "Successfully delivered message ${message.id}")
                    }
                    is DeliveryResult.Failure -> {
                        dao.incrementRetryCount(message.id)
                        val entity = dao.getMessageById(message.id)

                        if (entity != null && entity.retryCount >= MAX_RETRY_COUNT) {
                            dao.updateMessageStatus(message.id, MessageStatus.FAILED.name.lowercase())
                            com.prody.prashant.util.AppLogger.e(TAG, "Message ${message.id} failed after ${entity.retryCount} attempts")
                        } else {
                            com.prody.prashant.util.AppLogger.w(TAG, "Message ${message.id} delivery failed, will retry")
                        }
                    }
                }

                result
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error delivering message ${message.id}", e)
                DeliveryResult.Failure(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Deliver message as in-app notification
     * (For MVP, this creates a local notification and stores in received messages)
     */
    private suspend fun deliverInApp(message: CollaborativeMessage): DeliveryResult {
        return try {
            // In a real implementation, this would send to recipient's device via push notification
            // For MVP/demo purposes, we'll create a received message entry and show notification

            val receivedMessage = ReceivedCollaborativeMessage(
                id = message.id,
                sender = MessageSender(
                    id = message.userId,
                    name = "You", // In real app, would be sender's actual name
                    avatarUrl = null
                ),
                title = message.title,
                content = message.content,
                deliveredAt = LocalDateTime.now(),
                isRead = false,
                attachments = message.attachments,
                cardDesign = message.cardDesign,
                occasion = message.occasion
            )

            dao.insertReceivedMessage(receivedMessage.toEntity())

            // Show notification to recipient
            notifications.notifyNewMessageReceived(receivedMessage)

            DeliveryResult.Success("Message delivered in-app")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to deliver in-app message", e)
            DeliveryResult.Failure(e.message ?: "In-app delivery failed")
        }
    }

    /**
     * Deliver message via email
     * Opens email client with pre-filled message
     */
    private fun deliverViaEmail(message: CollaborativeMessage): DeliveryResult {
        return try {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(message.recipient.contactValue))
                putExtra(Intent.EXTRA_SUBJECT, message.title)

                // Format the email body with the message content
                val body = buildEmailBody(message)
                putExtra(Intent.EXTRA_TEXT, body)

                // Add voice recording as attachment if present
                if (message.attachments.hasVoiceRecording) {
                    putExtra(Intent.EXTRA_STREAM, Uri.parse(message.attachments.voiceRecordingUri))
                }

                // Add photos as attachments
                if (message.attachments.hasPhotos) {
                    val photoUris = message.attachments.photoUris.map { Uri.parse(it) }
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(photoUris))
                }

                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Check if email client is available
            if (emailIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(emailIntent)
                DeliveryResult.Success("Email client opened")
            } else {
                DeliveryResult.Failure("No email client available")
            }
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to deliver via email", e)
            DeliveryResult.Failure(e.message ?: "Email delivery failed")
        }
    }

    /**
     * Deliver message via SMS
     * Opens SMS app with pre-filled message
     */
    private fun deliverViaSMS(message: CollaborativeMessage): DeliveryResult {
        return try {
            val smsBody = buildSMSBody(message)

            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${message.recipient.contactValue}")
                putExtra("sms_body", smsBody)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // Check if SMS app is available
            if (smsIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(smsIntent)
                DeliveryResult.Success("SMS app opened")
            } else {
                DeliveryResult.Failure("No SMS app available")
            }
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to deliver via SMS", e)
            DeliveryResult.Failure(e.message ?: "SMS delivery failed")
        }
    }

    /**
     * Deliver message via WhatsApp
     * Opens WhatsApp with pre-filled message
     */
    private fun deliverViaWhatsApp(message: CollaborativeMessage): DeliveryResult {
        return try {
            val whatsappBody = buildSMSBody(message) // Reuse SMS body format
            val phoneNumber = message.recipient.contactValue.replace(Regex("[^0-9]"), "")

            val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(whatsappBody)}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // Check if WhatsApp is available
            if (whatsappIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(whatsappIntent)
                DeliveryResult.Success("WhatsApp opened")
            } else {
                DeliveryResult.Failure("WhatsApp not available")
            }
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to deliver via WhatsApp", e)
            DeliveryResult.Failure(e.message ?: "WhatsApp delivery failed")
        }
    }

    /**
     * Build formatted email body
     */
    private fun buildEmailBody(message: CollaborativeMessage): String {
        return buildString {
            append(message.content)
            append("\n\n")

            if (message.occasion != null) {
                append("Occasion: ${message.occasion.displayName} ${message.occasion.emoji}\n")
            }

            append("\n---\n")
            append("Sent with love via Prody\n")

            if (message.attachments.hasVoiceRecording) {
                append("🎤 Voice recording attached\n")
            }

            if (message.attachments.hasPhotos) {
                append("📷 ${message.attachments.photoUris.size} photo(s) attached\n")
            }
        }
    }

    /**
     * Build formatted SMS body (shorter due to SMS length limits)
     */
    private fun buildSMSBody(message: CollaborativeMessage): String {
        return buildString {
            append(message.title)
            append("\n\n")
            append(message.content)

            if (message.occasion != null) {
                append("\n\n${message.occasion.emoji} ${message.occasion.displayName}")
            }

            // Truncate if too long (SMS limit is typically 160 characters per message)
            if (length > 480) { // Allow for 3 messages
                val truncated = substring(0, 477)
                clear()
                append(truncated)
                append("...")
            }
        }
    }

    /**
     * Retry failed message delivery
     */
    suspend fun retryMessageDelivery(messageId: String): DeliveryResult {
        return withContext(Dispatchers.IO) {
            val entity = dao.getMessageById(messageId)
            if (entity == null) {
                return@withContext DeliveryResult.Failure("Message not found")
            }

            if (entity.retryCount >= MAX_RETRY_COUNT) {
                return@withContext DeliveryResult.Failure("Maximum retry attempts exceeded")
            }

            val message = CollaborativeMessage.fromEntity(entity)
            deliverMessage(message)
        }
    }

    /**
     * Test delivery method (for user to verify contact info)
     */
    suspend fun testDelivery(recipient: MessageRecipient): DeliveryResult {
        return withContext(Dispatchers.IO) {
            val testMessage = CollaborativeMessage(
                id = "test_${System.currentTimeMillis()}",
                userId = "local",
                recipient = recipient,
                title = "Test Message",
                content = "This is a test message from Prody to verify your contact information.",
                deliveryDate = LocalDateTime.now(),
                occasion = null,
                cardDesign = CardDesign(CardTheme.DEFAULT),
                attachments = MessageAttachments(),
                status = MessageStatus.PENDING
            )

            when (recipient.method) {
                ContactMethod.APP_USER, ContactMethod.IN_APP -> DeliveryResult.Success("In-app delivery available")
                ContactMethod.EMAIL -> {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(recipient.contactValue).matches()) {
                        DeliveryResult.Success("Email address is valid")
                    } else {
                        DeliveryResult.Failure("Invalid email address")
                    }
                }
                ContactMethod.PHONE, ContactMethod.SMS -> {
                    if (android.util.Patterns.PHONE.matcher(recipient.contactValue).matches()) {
                        DeliveryResult.Success("Phone number is valid")
                    } else {
                        DeliveryResult.Failure("Invalid phone number")
                    }
                }
                ContactMethod.WHATSAPP -> {
                    if (android.util.Patterns.PHONE.matcher(recipient.contactValue).matches()) {
                        DeliveryResult.Success("WhatsApp number is valid")
                    } else {
                        DeliveryResult.Failure("Invalid WhatsApp number")
                    }
                }
            }
        }
    }

    /**
     * Cancel pending delivery
     */
    suspend fun cancelDelivery(messageId: String) {
        withContext(Dispatchers.IO) {
            dao.updateMessageStatus(messageId, MessageStatus.PENDING.name.lowercase())
        }
    }
}

/**
 * Result of message delivery attempt
 */
sealed class DeliveryResult {
    data class Success(val message: String) : DeliveryResult()
    data class Failure(val error: String) : DeliveryResult()

    val isSuccess: Boolean
        get() = this is Success

    val isFailure: Boolean
        get() = this is Failure
}
