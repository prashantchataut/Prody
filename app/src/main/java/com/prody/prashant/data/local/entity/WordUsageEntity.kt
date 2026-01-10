package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for tracking vocabulary word usage in journal entries.
 * Records when and where learned words are used in context.
 *
 * This enables:
 * - Celebrating when users apply learned vocabulary
 * - Tracking real-world usage of learned words
 * - Identifying words learned but never used
 * - Awarding bonus points for vocabulary application
 */
@Entity(
    tableName = "word_usages",
    foreignKeys = [
        ForeignKey(
            entity = VocabularyEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = JournalEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["journalEntryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["wordId"]),
        Index(value = ["journalEntryId"]),
        Index(value = ["userId", "wordId"]),
        Index(value = ["userId", "detectedAt"]),
        Index(value = ["celebrated"]),
        Index(value = ["userId", "celebrated"])
    ]
)
data class WordUsageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * User who used the word (for multi-user support)
     */
    val userId: String = "local",

    /**
     * ID of the vocabulary word that was used
     */
    val wordId: Long,

    /**
     * ID of the journal entry where the word was used
     */
    val journalEntryId: Long,

    /**
     * The sentence or context where the word was used
     */
    val usedInSentence: String,

    /**
     * The actual form of the word that was matched (e.g., "running" for "run")
     */
    val matchedForm: String,

    /**
     * Position in the journal entry content (start index)
     */
    val positionStart: Int,

    /**
     * Position in the journal entry content (end index)
     */
    val positionEnd: Int,

    /**
     * Timestamp when the word usage was detected
     */
    val detectedAt: Long = System.currentTimeMillis(),

    /**
     * Whether the user has seen the celebration for this usage
     */
    val celebrated: Boolean = false,

    /**
     * Timestamp when the celebration was shown
     */
    val celebratedAt: Long? = null,

    /**
     * Bonus discipline points awarded for this usage
     */
    val bonusPointsAwarded: Int = 0,

    /**
     * Whether bonus points have been claimed/awarded
     */
    val pointsClaimed: Boolean = false
)
