package com.prody.prashant.domain.learning

import com.prody.prashant.data.local.entity.LearningStage
import com.prody.prashant.data.local.entity.VocabularyLearningEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * SM-2 Spaced Repetition Algorithm Engine.
 *
 * This implementation is based on the SuperMemo SM-2 algorithm, which is widely used
 * for optimizing learning retention. The algorithm calculates the optimal time
 * interval before the next review based on how well the user remembered the item.
 *
 * Quality ratings:
 * - 0: Complete blackout, no recall at all
 * - 1: Incorrect response, but upon seeing correct answer, it was remembered
 * - 2: Incorrect response, but correct answer seemed easy to recall
 * - 3: Correct response with serious difficulty
 * - 4: Correct response after some hesitation
 * - 5: Perfect response with no hesitation
 */
@Singleton
class SpacedRepetitionEngine @Inject constructor() {

    companion object {
        private const val MIN_EASE_FACTOR = 1.3f
        private const val DEFAULT_EASE_FACTOR = 2.5f
        private const val MAX_INTERVAL_DAYS = 365
        private const val MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L

        // Leitner box intervals in days
        private val BOX_INTERVALS = mapOf(
            1 to 1,    // Box 1: Review daily
            2 to 2,    // Box 2: Review every 2 days
            3 to 4,    // Box 3: Review every 4 days
            4 to 7,    // Box 4: Review every week
            5 to 14    // Box 5: Review every 2 weeks (mastered)
        )
    }

    /**
     * Result of a review calculation containing all updated learning parameters.
     */
    data class ReviewResult(
        val newInterval: Int,
        val newEaseFactor: Float,
        val newRepetitions: Int,
        val nextReviewDate: Long,
        val newBoxLevel: Int,
        val newStage: LearningStage,
        val newCorrectStreak: Int,
        val isMastered: Boolean
    )

    /**
     * Calculate the next review parameters based on the quality of the response.
     *
     * @param quality The quality of the response (0-5)
     * @param currentLearning The current learning state of the word
     * @return ReviewResult containing all updated parameters
     */
    fun calculateNextReview(
        quality: Int,
        currentLearning: VocabularyLearningEntity
    ): ReviewResult {
        require(quality in 0..5) { "Quality must be between 0 and 5" }

        val wasCorrect = quality >= 3
        val currentTime = System.currentTimeMillis()

        // Calculate new ease factor using SM-2 formula
        val newEaseFactor = calculateNewEaseFactor(quality, currentLearning.easeFactor)

        // Calculate new repetitions count
        val newRepetitions = if (wasCorrect) {
            currentLearning.repetitions + 1
        } else {
            0 // Reset on incorrect answer
        }

        // Calculate new interval
        val newInterval = calculateNewInterval(
            quality = quality,
            currentInterval = currentLearning.interval,
            easeFactor = newEaseFactor,
            repetitions = newRepetitions
        )

        // Calculate next review date
        val nextReviewDate = currentTime + (newInterval * MILLISECONDS_PER_DAY)

        // Update Leitner box level
        val newBoxLevel = calculateNewBoxLevel(quality, currentLearning.boxLevel)

        // Determine learning stage
        val newStage = LearningStage.fromBoxLevel(newBoxLevel)

        // Update correct streak
        val newCorrectStreak = if (wasCorrect) {
            currentLearning.correctStreak + 1
        } else {
            0
        }

        // Check if mastered
        val isMastered = newBoxLevel >= 5 && newRepetitions >= 3

        return ReviewResult(
            newInterval = newInterval,
            newEaseFactor = newEaseFactor,
            newRepetitions = newRepetitions,
            nextReviewDate = nextReviewDate,
            newBoxLevel = newBoxLevel,
            newStage = newStage,
            newCorrectStreak = newCorrectStreak,
            isMastered = isMastered
        )
    }

    /**
     * Calculate a new ease factor using the SM-2 formula.
     *
     * EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
     *
     * Where:
     * - EF' is the new ease factor
     * - EF is the current ease factor
     * - q is the quality of the response (0-5)
     */
    private fun calculateNewEaseFactor(quality: Int, currentEaseFactor: Float): Float {
        val adjustment = 0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f)
        return max(MIN_EASE_FACTOR, currentEaseFactor + adjustment)
    }

    /**
     * Calculate the new interval based on SM-2 algorithm.
     *
     * For first review: interval = 1 day
     * For second review: interval = 6 days
     * For subsequent reviews: interval = previous_interval * ease_factor
     */
    private fun calculateNewInterval(
        quality: Int,
        currentInterval: Int,
        easeFactor: Float,
        repetitions: Int
    ): Int {
        // If answer was incorrect, reset to 1 day
        if (quality < 3) {
            return 1
        }

        return when (repetitions) {
            1 -> 1    // First successful recall
            2 -> 6    // Second successful recall
            else -> {
                // Subsequent recalls: multiply by ease factor
                val calculated = (currentInterval * easeFactor).roundToInt()
                calculated.coerceIn(1, MAX_INTERVAL_DAYS)
            }
        }
    }

    /**
     * Calculate new Leitner box level based on answer quality.
     *
     * - Correct answer (quality >= 3): Move up one box (max 5)
     * - Incorrect answer: Move down to box 1
     */
    private fun calculateNewBoxLevel(quality: Int, currentBoxLevel: Int): Int {
        return if (quality >= 3) {
            (currentBoxLevel + 1).coerceAtMost(5)
        } else {
            1 // Reset to box 1 on incorrect answer
        }
    }

    /**
     * Get the interval in days for a specific Leitner box level.
     */
    fun getBoxInterval(boxLevel: Int): Int {
        return BOX_INTERVALS[boxLevel.coerceIn(1, 5)] ?: 1
    }

    /**
     * Convert quality to a simple response type.
     */
    fun qualityFromResponse(response: ReviewResponse): Int {
        return when (response) {
            ReviewResponse.PERFECT -> 5      // No hesitation, perfect recall
            ReviewResponse.CORRECT -> 4      // Correct but with some thought
            ReviewResponse.HARD -> 3         // Correct but difficult
            ReviewResponse.WRONG_EASY -> 2   // Wrong but it was easy once shown
            ReviewResponse.WRONG_HARD -> 1   // Wrong and struggled even after seeing answer
            ReviewResponse.BLACKOUT -> 0     // Complete blackout
        }
    }

    /**
     * Create initial learning entity for a new word.
     */
    fun createInitialLearningEntity(wordId: Long): VocabularyLearningEntity {
        return VocabularyLearningEntity(
            wordId = wordId,
            easeFactor = DEFAULT_EASE_FACTOR,
            interval = 1,
            repetitions = 0,
            nextReviewDate = System.currentTimeMillis(),
            boxLevel = 1,
            stage = LearningStage.NEW.name,
            isIntroduced = false
        )
    }

    /**
     * Update a learning entity with review results.
     */
    fun applyReviewResult(
        currentLearning: VocabularyLearningEntity,
        result: ReviewResult,
        responseTimeMs: Long = 0
    ): VocabularyLearningEntity {
        val wasCorrect = result.newRepetitions > currentLearning.repetitions
        val currentTime = System.currentTimeMillis()

        return currentLearning.copy(
            easeFactor = result.newEaseFactor,
            interval = result.newInterval,
            repetitions = result.newRepetitions,
            nextReviewDate = result.nextReviewDate,
            lastReviewDate = currentTime,
            totalReviews = currentLearning.totalReviews + 1,
            correctReviews = if (wasCorrect) currentLearning.correctReviews + 1 else currentLearning.correctReviews,
            boxLevel = result.newBoxLevel,
            stage = result.newStage.name,
            correctStreak = result.newCorrectStreak,
            longestCorrectStreak = maxOf(currentLearning.longestCorrectStreak, result.newCorrectStreak),
            reviewSessionCount = currentLearning.reviewSessionCount + 1,
            averageResponseTimeMs = if (responseTimeMs > 0) {
                calculateNewAverageResponseTime(
                    currentAverage = currentLearning.averageResponseTimeMs,
                    totalReviews = currentLearning.totalReviews,
                    newResponseTime = responseTimeMs
                )
            } else {
                currentLearning.averageResponseTimeMs
            },
            firstLearnedDate = currentLearning.firstLearnedDate ?: currentTime,
            masteredDate = if (result.isMastered && currentLearning.masteredDate == null) {
                currentTime
            } else {
                currentLearning.masteredDate
            }
        )
    }

    private fun calculateNewAverageResponseTime(
        currentAverage: Long,
        totalReviews: Int,
        newResponseTime: Long
    ): Long {
        if (totalReviews == 0) return newResponseTime
        return ((currentAverage * totalReviews) + newResponseTime) / (totalReviews + 1)
    }

    /**
     * Get words that need review, prioritized by overdue amount.
     */
    fun prioritizeReviewWords(words: List<VocabularyLearningEntity>): List<VocabularyLearningEntity> {
        val currentTime = System.currentTimeMillis()
        return words
            .filter { it.isDueForReview }
            .sortedByDescending { currentTime - it.nextReviewDate } // Most overdue first
    }

    /**
     * Calculate the retention percentage based on correct reviews.
     */
    fun calculateRetentionPercentage(learning: VocabularyLearningEntity): Float {
        if (learning.totalReviews == 0) return 0f
        return (learning.correctReviews.toFloat() / learning.totalReviews) * 100f
    }

    /**
     * Get the difficulty label for a word based on its ease factor.
     */
    fun getDifficultyLabel(easeFactor: Float): String {
        return when {
            easeFactor >= 2.8f -> "Very Easy"
            easeFactor >= 2.4f -> "Easy"
            easeFactor >= 2.0f -> "Medium"
            easeFactor >= 1.6f -> "Hard"
            else -> "Very Hard"
        }
    }
}

/**
 * Simplified response types for review sessions.
 */
enum class ReviewResponse {
    PERFECT,     // Quality 5: No hesitation
    CORRECT,     // Quality 4: Correct with thought
    HARD,        // Quality 3: Correct but difficult
    WRONG_EASY,  // Quality 2: Wrong but easy once seen
    WRONG_HARD,  // Quality 1: Wrong and hard even after
    BLACKOUT     // Quality 0: Complete blackout
}
