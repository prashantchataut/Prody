package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for tracking replies to delivered future messages,
 * enabling a "conversation across time" feature.
 *
 * When a future message is delivered, the user can:
 * 1. Read their past self's words
 * 2. Reply to their past self with current reflections
 * 3. Optionally send another message to their future self
 *
 * This creates a chain of self-conversation over time.
 */
@Entity(
    tableName = "future_message_replies",
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
        Index(value = ["userId", "originalMessageId"])
    ]
)
data class FutureMessageReplyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User authentication
    val userId: String = "local",

    // The original future message this is a reply to
    val originalMessageId: Long,

    // The reply content
    val replyContent: String,

    // Reflection prompt that was shown (for context)
    val promptShown: String? = null, // e.g., "How does this land now?"

    // How the user felt reading their past message
    val reactionMood: String? = null,

    // If user chose to send another message to future self
    val chainedMessageId: Long? = null, // ID of the new FutureMessageEntity created

    // Timestamp
    val repliedAt: Long = System.currentTimeMillis(),

    // Whether this was also saved as a journal reflection entry
    val savedAsJournalId: Long? = null,

    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false
) {
    /**
     * Check if this reply started a new chain (sent another message to future)
     */
    val hasChainedMessage: Boolean
        get() = chainedMessageId != null

    /**
     * Check if this reply was saved as a journal entry
     */
    val isSavedAsJournal: Boolean
        get() = savedAsJournalId != null
}
