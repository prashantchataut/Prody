package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for tracking spaced repetition learning progress for vocabulary words.
 * Implements SM-2 algorithm data storage.
 */
@Entity(
    tableName = "vocabulary_learning",
    primaryKeys = ["wordId", "userId"], // Composite key for multi-user
    foreignKeys = [
        ForeignKey(
            entity = VocabularyEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["wordId"]),
        Index(value = ["userId"]),
        Index(value = ["nextReviewDate"]),
        Index(value = ["boxLevel"]),
        Index(value = ["userId", "nextReviewDate"])
    ]
)
data class VocabularyLearningEntity(
    val wordId: Long,
    val userId: String = "local", // Multi-user support

    /**
     * SM-2 ease factor (difficulty). Default 2.5, min 1.3
     * Higher values = easier word
     */
    val easeFactor: Float = 2.5f,

    /**
     * Current interval in days until next review
     */
    val interval: Int = 1,

    /**
     * Number of consecutive correct answers
     */
    val repetitions: Int = 0,

    /**
     * Timestamp for next scheduled review
     */
    val nextReviewDate: Long = System.currentTimeMillis(),

    /**
     * Timestamp of last review
     */
    val lastReviewDate: Long? = null,

    /**
     * Total number of reviews for this word
     */
    val totalReviews: Int = 0,

    /**
     * Total correct reviews
     */
    val correctReviews: Int = 0,

    /**
     * Leitner box level (1-5) for simple spaced repetition visualization
     * Box 1: Review daily
     * Box 2: Review every 2 days
     * Box 3: Review every 4 days
     * Box 4: Review every week
     * Box 5: Review every 2 weeks (mastered)
     */
    val boxLevel: Int = 1,

    /**
     * Current learning stage
     */
    val stage: String = LearningStage.NEW.name,

    /**
     * Whether the word has been introduced to the user
     */
    val isIntroduced: Boolean = false,

    /**
     * Number of times the word was shown in review sessions
     */
    val reviewSessionCount: Int = 0,

    /**
     * Average response time in milliseconds (for analytics)
     */
    val averageResponseTimeMs: Long = 0,

    /**
     * Streak of correct answers
     */
    val correctStreak: Int = 0,

    /**
     * Longest streak of correct answers
     */
    val longestCorrectStreak: Int = 0,

    /**
     * Date when the word was first learned
     */
    val firstLearnedDate: Long? = null,

    /**
     * Date when the word was mastered (box level 5)
     */
    val masteredDate: Long? = null,

    /**
     * Whether the word has been used in context (journal entries)
     */
    val usedInContext: Boolean = false,

    /**
     * Timestamp when the word was last used in context
     */
    val lastUsedAt: Long? = null,

    /**
     * Number of times the word has been used in journal entries
     */
    val timesUsed: Int = 0
) {
    /**
     * Returns the accuracy percentage for this word
     */
    val accuracy: Float
        get() = if (totalReviews > 0) (correctReviews.toFloat() / totalReviews) * 100 else 0f

    /**
     * Returns true if the word is due for review
     */
    val isDueForReview: Boolean
        get() = System.currentTimeMillis() >= nextReviewDate

    /**
     * Returns the learning stage as enum
     */
    val learningStage: LearningStage
        get() = LearningStage.fromString(stage)

    /**
     * Returns true if the word is mastered (box level 5)
     */
    val isMastered: Boolean
        get() = boxLevel >= 5 || stage == LearningStage.MASTERED.name
}

/**
 * Enum representing the learning stages of a vocabulary word.
 */
enum class LearningStage {
    NEW,        // Word has not been introduced yet
    LEARNING,   // Word is being actively learned (boxes 1-2)
    REVIEWING,  // Word is in review phase (boxes 3-4)
    MASTERED;   // Word has been mastered (box 5)

    companion object {
        fun fromString(value: String): LearningStage {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: NEW
        }

        fun fromBoxLevel(level: Int): LearningStage {
            return when (level) {
                1, 2 -> LEARNING
                3, 4 -> REVIEWING
                5 -> MASTERED
                else -> NEW
            }
        }
    }
}
