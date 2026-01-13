package com.prody.prashant.domain.collaborative

import androidx.compose.ui.graphics.Color
import com.prody.prashant.data.local.entity.*
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

/**
 * Enum representing different occasions for collaborative messages
 */
enum class Occasion(
    val displayName: String,
    val emoji: String,
    val suggestedTheme: String,
    val description: String
) {
    BIRTHDAY("Birthday", "🎂", "celebration", "Celebrate another year of life"),
    ANNIVERSARY("Anniversary", "💕", "romantic", "Mark a special milestone together"),
    GRADUATION("Graduation", "🎓", "achievement", "Celebrate academic success"),
    NEW_YEAR("New Year", "🎊", "festive", "Welcome the new year"),
    CHRISTMAS("Christmas", "🎄", "holiday", "Share holiday cheer"),
    MOTHERS_DAY("Mother's Day", "💐", "warm", "Honor the special mom in your life"),
    FATHERS_DAY("Father's Day", "👔", "classic", "Celebrate dad"),
    WEDDING("Wedding Day", "💒", "elegant", "Congratulate the happy couple"),
    NEW_BABY("New Baby", "👶", "soft", "Welcome a new life"),
    ENCOURAGEMENT("Encouragement", "💪", "uplifting", "Send support and motivation"),
    THANK_YOU("Thank You", "🙏", "heartfelt", "Express gratitude"),
    THINKING_OF_YOU("Thinking of You", "💭", "warm", "Let them know you care"),
    GET_WELL("Get Well Soon", "🌸", "soft", "Send healing wishes"),
    CONGRATULATIONS("Congratulations", "🎉", "celebration", "Celebrate their success"),
    JUST_BECAUSE("Just Because", "💝", "heartfelt", "No reason needed"),
    CUSTOM("Custom Occasion", "✨", "default", "Your own special occasion");

    companion object {
        fun fromString(value: String?): Occasion? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}

/**
 * Enum representing delivery methods
 */
enum class DeliveryMethod(val displayName: String) {
    IN_APP("In-App Notification"),
    EMAIL("Email"),
    SMS("Text Message");

    companion object {
        fun fromString(value: String?): DeliveryMethod {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: IN_APP
        }
    }
}

/**
 * Enum representing message status
 */
enum class MessageStatus(val displayName: String) {
    DRAFT("Draft"),
    PENDING("Pending"),
    SENT("Sent"),
    SCHEDULED("Scheduled"),
    DELIVERED("Delivered"),
    READ("Read"),
    FAILED("Failed");

    companion object {
        fun fromString(value: String?): MessageStatus {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}

/**
 * Enum representing contact methods
 */
enum class ContactMethod(val displayName: String) {
    APP_USER("Prody User"),
    EMAIL("Email"),
    PHONE("Phone Number"),
    WHATSAPP("WhatsApp"),
    IN_APP("In-App");

    companion object {
        fun fromString(value: String?): ContactMethod {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: EMAIL
        }
    }
}

/**
 * Enum representing card themes for message presentation
 */
enum class CardTheme(
    val displayName: String,
    val primaryColor: Long,
    val secondaryColor: Long,
    val textColor: Long = 0xFF212121,
    val colorDark: Long = 0xFF212121
) {
    DEFAULT("Classic", 0xFFFFFFFF, 0xFFF5F5F5, 0xFF212121, 0xFF424242),
    CELEBRATION("Celebration", 0xFFFFE082, 0xFFFFCA28, 0xFF5D4037, 0xFF5D4037),
    ROMANTIC("Romantic", 0xFFFFCDD2, 0xFFE91E63, 0xFF880E4F, 0xFF880E4F),
    NATURE("Nature", 0xFFC8E6C9, 0xFF4CAF50, 0xFF1B5E20, 0xFF1B5E20),
    OCEAN("Ocean", 0xFFB3E5FC, 0xFF03A9F4, 0xFF01579B, 0xFF01579B),
    SUNSET("Sunset", 0xFFFFCCBC, 0xFFFF5722, 0xFFBF360C, 0xFFBF360C),
    MIDNIGHT("Midnight", 0xFF7986CB, 0xFF3F51B5, 0xFFFFFFFF, 0xFF1A237E),
    FESTIVE("Festive", 0xFFC62828, 0xFF2E7D32, 0xFFFFFFFF, 0xFF1B5E20),
    ELEGANT("Elegant", 0xFF424242, 0xFF212121, 0xFFFFFFFF, 0xFF000000),
    SOFT("Soft & Warm", 0xFFFFF3E0, 0xFFFFE0B2, 0xFF5D4037, 0xFF5D4037),
    LAVENDER("Lavender Dreams", 0xFFE1BEE7, 0xFF9C27B0, 0xFF4A148C, 0xFF4A148C),
    MINT("Mint Fresh", 0xFFB2DFDB, 0xFF009688, 0xFF004D40, 0xFF004D40);

    fun getPrimaryColor(): Color = Color(primaryColor)
    fun getSecondaryColor(): Color = Color(secondaryColor)
    fun getTextColor(): Color = Color(textColor)

    companion object {
        fun fromString(value: String?): CardTheme {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: DEFAULT
        }

        fun forOccasion(occasion: Occasion): CardTheme {
            return when (occasion.suggestedTheme) {
                "celebration" -> CELEBRATION
                "romantic" -> ROMANTIC
                "nature" -> NATURE
                "ocean" -> OCEAN
                "festive" -> FESTIVE
                "holiday" -> FESTIVE
                "achievement" -> CELEBRATION
                "elegant" -> ELEGANT
                "soft" -> SOFT
                "warm" -> SOFT
                "uplifting" -> NATURE
                "heartfelt" -> ROMANTIC
                "classic" -> DEFAULT
                else -> DEFAULT
            }
        }
    }
}

/**
 * Domain model for a collaborative message
 */
data class CollaborativeMessage(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "local",
    val recipient: MessageRecipient,
    val title: String,
    val content: String,
    val deliveryDate: LocalDateTime,
    val occasion: Occasion?,
    val cardDesign: CardDesign,
    val attachments: MessageAttachments,
    val status: MessageStatus,
    val isDelivered: Boolean = false,
    val isRead: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deliveredAt: LocalDateTime? = null,
    val readAt: LocalDateTime? = null,
    val retryCount: Int = 0
) {
    fun toEntity(): CollaborativeMessageEntity {
        return CollaborativeMessageEntity(
            id = id,
            userId = userId,
            recipientId = if (recipient.method == ContactMethod.APP_USER) recipient.contactValue else null,
            recipientContact = if (recipient.method != ContactMethod.APP_USER) recipient.contactValue else null,
            recipientName = recipient.name,
            title = title,
            content = content,
            deliveryDate = deliveryDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            occasion = occasion?.name?.lowercase(),
            isDelivered = isDelivered,
            isRead = isRead,
            createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            deliveredAt = deliveredAt?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
            readAt = readAt?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
            attachedPhotosJson = JSONArray(attachments.photoUris).toString(),
            voiceRecordingUri = attachments.voiceRecordingUri,
            voiceRecordingDuration = attachments.voiceRecordingDuration,
            cardTheme = cardDesign.theme.name.lowercase(),
            cardBackgroundColor = cardDesign.customBackgroundColor?.toString(),
            status = status.name.lowercase(),
            deliveryMethod = recipient.method.name.lowercase(),
            retryCount = retryCount
        )
    }

    companion object {
        fun fromEntity(entity: CollaborativeMessageEntity): CollaborativeMessage {
            val photoUris = try {
                val array = JSONArray(entity.attachedPhotosJson)
                List(array.length()) { array.getString(it) }
            } catch (e: Exception) {
                emptyList()
            }

            return CollaborativeMessage(
                id = entity.id,
                userId = entity.userId,
                recipient = MessageRecipient(
                    name = entity.recipientName,
                    method = ContactMethod.fromString(entity.deliveryMethod),
                    contactValue = entity.recipientId ?: entity.recipientContact ?: "",
                    avatarUrl = null
                ),
                title = entity.title,
                content = entity.content,
                deliveryDate = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(entity.deliveryDate),
                    ZoneId.systemDefault()
                ),
                occasion = Occasion.fromString(entity.occasion),
                cardDesign = CardDesign(
                    theme = CardTheme.fromString(entity.cardTheme),
                    customBackgroundColor = entity.cardBackgroundColor?.let { Color(it.toLong()) }
                ),
                attachments = MessageAttachments(
                    photoUris = photoUris,
                    voiceRecordingUri = entity.voiceRecordingUri,
                    voiceRecordingDuration = entity.voiceRecordingDuration
                ),
                status = MessageStatus.fromString(entity.status),
                isDelivered = entity.isDelivered,
                isRead = entity.isRead,
                createdAt = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(entity.createdAt),
                    ZoneId.systemDefault()
                ),
                deliveredAt = entity.deliveredAt?.let {
                    LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(it),
                        ZoneId.systemDefault()
                    )
                },
                readAt = entity.readAt?.let {
                    LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(it),
                        ZoneId.systemDefault()
                    )
                },
                retryCount = entity.retryCount
            )
        }
    }
}

/**
 * Message recipient information
 */
data class MessageRecipient(
    val name: String,
    val method: ContactMethod,
    val contactValue: String, // userId, email, or phone
    val avatarUrl: String? = null
)

/**
 * Card design configuration
 */
data class CardDesign(
    val theme: CardTheme,
    val customBackgroundColor: Color? = null
) {
    fun getBackgroundColor(): Color {
        return customBackgroundColor ?: theme.getPrimaryColor()
    }

    fun getAccentColor(): Color {
        return theme.getSecondaryColor()
    }

    fun getTextColor(): Color {
        return theme.getTextColor()
    }
}

/**
 * Message attachments
 */
data class MessageAttachments(
    val photoUris: List<String> = emptyList(),
    val voiceRecordingUri: String? = null,
    val voiceRecordingDuration: Long = 0
) {
    val hasPhotos: Boolean
        get() = photoUris.isNotEmpty()

    val hasVoiceRecording: Boolean
        get() = voiceRecordingUri != null

    val hasAnyAttachments: Boolean
        get() = hasPhotos || hasVoiceRecording
}

/**
 * Domain model for received collaborative message
 */
data class ReceivedCollaborativeMessage(
    val id: String,
    val sender: MessageSender,
    val title: String,
    val content: String,
    val deliveredAt: LocalDateTime,
    val isRead: Boolean = false,
    val readAt: LocalDateTime? = null,
    val attachments: MessageAttachments,
    val cardDesign: CardDesign,
    val occasion: Occasion?,
    val isFavorite: Boolean = false,
    val replyMessageId: String? = null
) {
    /** Convenience property for sender name */
    val senderName: String get() = sender.name

    /** Alias for deliveredAt for UI compatibility */
    val receivedAt: LocalDateTime get() = deliveredAt

    fun toEntity(): ReceivedCollaborativeMessageEntity {
        return ReceivedCollaborativeMessageEntity(
            id = id,
            senderId = sender.id,
            senderName = sender.name,
            title = title,
            content = content,
            deliveredAt = deliveredAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            isRead = isRead,
            readAt = readAt?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
            attachedPhotosJson = JSONArray(attachments.photoUris).toString(),
            voiceRecordingUri = attachments.voiceRecordingUri,
            voiceRecordingDuration = attachments.voiceRecordingDuration,
            cardTheme = cardDesign.theme.name.lowercase(),
            occasion = occasion?.name?.lowercase(),
            isFavorite = isFavorite,
            replyMessageId = replyMessageId
        )
    }

    companion object {
        fun fromEntity(entity: ReceivedCollaborativeMessageEntity): ReceivedCollaborativeMessage {
            val photoUris = try {
                val array = JSONArray(entity.attachedPhotosJson)
                List(array.length()) { array.getString(it) }
            } catch (e: Exception) {
                emptyList()
            }

            return ReceivedCollaborativeMessage(
                id = entity.id,
                sender = MessageSender(
                    id = entity.senderId,
                    name = entity.senderName,
                    avatarUrl = null
                ),
                title = entity.title,
                content = entity.content,
                deliveredAt = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(entity.deliveredAt),
                    ZoneId.systemDefault()
                ),
                isRead = entity.isRead,
                readAt = entity.readAt?.let {
                    LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(it),
                        ZoneId.systemDefault()
                    )
                },
                attachments = MessageAttachments(
                    photoUris = photoUris,
                    voiceRecordingUri = entity.voiceRecordingUri,
                    voiceRecordingDuration = entity.voiceRecordingDuration
                ),
                cardDesign = CardDesign(theme = CardTheme.fromString(entity.cardTheme)),
                occasion = Occasion.fromString(entity.occasion),
                isFavorite = entity.isFavorite,
                replyMessageId = entity.replyMessageId
            )
        }
    }
}

/**
 * Message sender information
 */
data class MessageSender(
    val id: String,
    val name: String,
    val avatarUrl: String? = null
)

/**
 * Domain model for a message contact
 */
data class MessageContact(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "local",
    val displayName: String,
    val method: ContactMethod,
    val contactValue: String,
    val avatarUrl: String? = null,
    val messagesSent: Int = 0,
    val lastMessageAt: LocalDateTime? = null,
    val isFavorite: Boolean = false
) {
    fun toEntity(): MessageContactEntity {
        return MessageContactEntity(
            id = id,
            userId = userId,
            displayName = displayName,
            contactMethod = method.name.lowercase(),
            contactValue = contactValue,
            avatarUrl = avatarUrl,
            messagesSent = messagesSent,
            lastMessageAt = lastMessageAt?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
            isFavorite = isFavorite
        )
    }

    companion object {
        fun fromEntity(entity: MessageContactEntity): MessageContact {
            return MessageContact(
                id = entity.id,
                userId = entity.userId,
                displayName = entity.displayName,
                method = ContactMethod.fromString(entity.contactMethod),
                contactValue = entity.contactValue,
                avatarUrl = entity.avatarUrl,
                messagesSent = entity.messagesSent,
                lastMessageAt = entity.lastMessageAt?.let {
                    LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(it),
                        ZoneId.systemDefault()
                    )
                },
                isFavorite = entity.isFavorite
            )
        }
    }
}

/**
 * Domain model for occasion tracking
 */
data class MessageOccasion(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "local",
    val contactId: String,
    val occasionType: Occasion,
    val date: LocalDateTime,
    val isRecurring: Boolean = true,
    val reminderDaysBefore: Int = 7,
    val lastNotifiedYear: Int? = null
) {
    fun toEntity(): MessageOccasionEntity {
        return MessageOccasionEntity(
            id = id,
            userId = userId,
            contactId = contactId,
            occasionType = occasionType.name.lowercase(),
            date = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            isRecurring = isRecurring,
            reminderDaysBefore = reminderDaysBefore,
            lastNotifiedYear = lastNotifiedYear
        )
    }

    companion object {
        fun fromEntity(entity: MessageOccasionEntity): MessageOccasion {
            return MessageOccasion(
                id = entity.id,
                userId = entity.userId,
                contactId = entity.contactId,
                occasionType = Occasion.fromString(entity.occasionType) ?: Occasion.CUSTOM,
                date = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(entity.date),
                    ZoneId.systemDefault()
                ),
                isRecurring = entity.isRecurring,
                reminderDaysBefore = entity.reminderDaysBefore,
                lastNotifiedYear = entity.lastNotifiedYear
            )
        }
    }

    /**
     * Extension function to convert MessageOccasion to its Occasion type.
     * Used in UI navigation when selecting an occasion to compose a message.
     */
    fun toOccasion(): Occasion = occasionType
}
