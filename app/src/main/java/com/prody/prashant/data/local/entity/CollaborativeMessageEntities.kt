package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
 * Entity representing a collaborative message sent to friends/family
 * for delivery at a future date/occasion.
 */
@Entity(
    tableName = "collaborative_messages",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["deliveryDate"]),
        Index(value = ["userId", "deliveryDate"]),
        Index(value = ["recipientId"]),
        Index(value = ["status"]),
        Index(value = ["occasion"])
    ]
)
data class CollaborativeMessageEntity(
    @PrimaryKey
    val id: String, // UUID for sync
    val userId: String = "local",

    // Recipient information
    val recipientId: String?, // null for contact-based (phone/email)
    val recipientContact: String?, // Phone or email if not app user
    val recipientName: String,

    // Message content
    val title: String,
    val content: String,
    val deliveryDate: Long,
    val occasion: String? = null, // "birthday", "anniversary", "graduation", "new_year", null for custom

    // Delivery status
    val isDelivered: Boolean = false,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveredAt: Long? = null,
    val readAt: Long? = null,

    // Media attachments
    val attachedPhotosJson: String = "[]", // JSON array of photo URIs
    val voiceRecordingUri: String? = null,
    val voiceRecordingDuration: Long = 0,

    // Styling
    val cardTheme: String = "default",
    val cardBackgroundColor: String? = null,

    // Status tracking
    val status: String = "pending", // pending, scheduled, delivered, read, failed
    val deliveryMethod: String = "in_app", // in_app, email, sms
    val retryCount: Int = 0,

    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false
)

/**
 * Entity representing a collaborative message received from others
 */
@Entity(
    tableName = "received_collaborative_messages",
    indices = [
        Index(value = ["senderId"]),
        Index(value = ["deliveredAt"]),
        Index(value = ["isRead"]),
        Index(value = ["isFavorite"]),
        Index(value = ["occasion"])
    ]
)
data class ReceivedCollaborativeMessageEntity(
    @PrimaryKey
    val id: String,

    // Sender information
    val senderId: String,
    val senderName: String,

    // Message content
    val title: String,
    val content: String,
    val deliveredAt: Long,

    // Status
    val isRead: Boolean = false,
    val readAt: Long? = null,

    // Media attachments
    val attachedPhotosJson: String = "[]",
    val voiceRecordingUri: String? = null,
    val voiceRecordingDuration: Long = 0,

    // Styling
    val cardTheme: String,
    val occasion: String? = null,

    // User actions
    val isFavorite: Boolean = false,
    val replyMessageId: String? = null, // ID of message sent as reply

    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false
)

/**
 * Entity representing a contact for collaborative messaging
 */
@Entity(
    tableName = "message_contacts",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["contactMethod"]),
        Index(value = ["contactValue"], unique = true),
        Index(value = ["isFavorite"]),
        Index(value = ["userId", "isFavorite"])
    ]
)
data class MessageContactEntity(
    @PrimaryKey
    val id: String,
    val userId: String = "local",

    // Contact information
    val displayName: String,
    val contactMethod: String, // "app_user", "email", "phone"
    val contactValue: String, // userId, email, or phone
    val avatarUrl: String? = null,

    // Statistics
    val messagesSent: Int = 0,
    val lastMessageAt: Long? = null,

    // User preferences
    val isFavorite: Boolean = false,

    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

/**
 * Entity representing an occasion reminder for a contact
 */
@Entity(
    tableName = "message_occasions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["contactId"]),
        Index(value = ["occasionType"]),
        Index(value = ["date"]),
        Index(value = ["userId", "contactId"])
    ]
)
data class MessageOccasionEntity(
    @PrimaryKey
    val id: String,
    val userId: String = "local",
    val contactId: String,

    // Occasion details
    val occasionType: String, // birthday, anniversary, etc.
    val date: Long, // Timestamp of the occasion date
    val isRecurring: Boolean = true,

    // Reminder settings
    val reminderDaysBefore: Int = 7,
    val lastNotifiedYear: Int? = null,

    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
