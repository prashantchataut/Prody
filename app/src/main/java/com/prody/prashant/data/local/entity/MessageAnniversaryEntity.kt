package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for tracking message anniversaries.
 *
 * Enables warm, thoughtful reminders like:
 * - "A year ago today, you wrote a message to your future self"
 * - "Two years ago, you set this intention..."
 *
 * This creates beautiful moments of reflection on how far you've come.
 */
@Entity(
    tableName = "message_anniversaries",
    foreignKeys = [
        ForeignKey(
            entity = FutureMessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["originalMessageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["originalMessageId"]),
        Index(value = ["anniversaryDate"]),
        Index(value = ["userId", "anniversaryDate"])
    ]
)
data class MessageAnniversaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User authentication
    val userId: String = "local",

    // The original message this anniversary is for
    val originalMessageId: Long,

    // How many years ago (1, 2, 3, etc.)
    val yearsAgo: Int,

    // Message summary for notification context
    val originalContent: String, // First 100-150 chars or AI summary

    // Message metadata
    val category: String, // goal, promise, motivation, etc.
    val originalCreatedAt: Long, // When the message was originally written

    // Anniversary date (the day/month, for recurring reminders)
    val anniversaryDate: Long, // Timestamp for this year's anniversary

    // Notification tracking
    val notifiedAt: Long? = null, // When user was notified
    val isRead: Boolean = false, // Whether user viewed the anniversary
    val readAt: Long? = null,

    // Reflection tracking
    val hasReflection: Boolean = false, // Did user write a reflection?
    val reflectionJournalId: Long? = null, // Link to journal entry if created

    // Creation metadata
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    /**
     * Get a warm, human notification message
     */
    fun getNotificationMessage(): String {
        val yearText = when (yearsAgo) {
            1 -> "A year ago"
            2 -> "Two years ago"
            3 -> "Three years ago"
            else -> "$yearsAgo years ago"
        }

        return when (category) {
            "goal" -> "$yearText, you set an intention. Want to see how far you've come?"
            "promise" -> "$yearText, you made a promise to yourself..."
            "motivation" -> "$yearText, you wrote these words to keep yourself going."
            "reminder" -> "$yearText today, you wanted to remember something."
            else -> "$yearText, your past self wrote something for you."
        }
    }

    /**
     * Get notification title
     */
    fun getNotificationTitle(): String {
        return when (yearsAgo) {
            1 -> "One Year Anniversary"
            else -> "$yearsAgo Years Ago Today"
        }
    }
}
