package com.prody.prashant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.prody.prashant.data.local.entity.VocabularyLearningEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for vocabulary learning operations (Spaced Repetition).
 */
@Dao
interface VocabularyLearningDao {

    /**
     * Get all learning entries.
     */
    @Query("SELECT * FROM vocabulary_learning ORDER BY nextReviewDate ASC")
    fun getAllLearningEntries(): Flow<List<VocabularyLearningEntity>>

    /**
     * Get learning entry for a specific word.
     */
    @Query("SELECT * FROM vocabulary_learning WHERE wordId = :wordId")
    suspend fun getLearningForWord(wordId: Long): VocabularyLearningEntity?

    /**
     * Observe learning entry for a specific word.
     */
    @Query("SELECT * FROM vocabulary_learning WHERE wordId = :wordId")
    fun observeLearningForWord(wordId: Long): Flow<VocabularyLearningEntity?>

    /**
     * Get all words due for review (nextReviewDate <= current time).
     */
    @Query("SELECT * FROM vocabulary_learning WHERE nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    suspend fun getWordsDueForReview(currentTime: Long = System.currentTimeMillis()): List<VocabularyLearningEntity>

    /**
     * Observe words due for review.
     */
    @Query("SELECT * FROM vocabulary_learning WHERE nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    fun observeWordsDueForReview(currentTime: Long = System.currentTimeMillis()): Flow<List<VocabularyLearningEntity>>

    /**
     * Get words due for review with limit.
     */
    @Query("SELECT * FROM vocabulary_learning WHERE nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC LIMIT :limit")
    suspend fun getWordsDueForReviewWithLimit(
        currentTime: Long = System.currentTimeMillis(),
        limit: Int = 20
    ): List<VocabularyLearningEntity>

    /**
     * Get words in a specific Leitner box.
     */
    @Query("SELECT * FROM vocabulary_learning WHERE boxLevel = :boxLevel ORDER BY nextReviewDate ASC")
    fun getWordsByBoxLevel(boxLevel: Int): Flow<List<VocabularyLearningEntity>>

    /**
     * Get words in a specific learning stage.
     */
    @Query("SELECT * FROM vocabulary_learning WHERE stage = :stage ORDER BY nextReviewDate ASC")
    fun getWordsByStage(stage: String): Flow<List<VocabularyLearningEntity>>

    /**
     * Get mastered words (box level 5).
     */
    @Query("SELECT * FROM vocabulary_learning WHERE boxLevel >= 5 ORDER BY masteredDate DESC")
    fun getMasteredWords(): Flow<List<VocabularyLearningEntity>>

    /**
     * Get count of mastered words.
     */
    @Query("SELECT COUNT(*) FROM vocabulary_learning WHERE boxLevel >= 5")
    fun getMasteredCount(): Flow<Int>

    /**
     * Get count of words due for review.
     */
    @Query("SELECT COUNT(*) FROM vocabulary_learning WHERE nextReviewDate <= :currentTime")
    fun getDueReviewCount(currentTime: Long = System.currentTimeMillis()): Flow<Int>

    /**
     * Get words that need practice (low accuracy or struggling words).
     */
    @Query("""
        SELECT * FROM vocabulary_learning
        WHERE totalReviews > 0
        AND (correctReviews * 100.0 / totalReviews) < :accuracyThreshold
        ORDER BY (correctReviews * 1.0 / totalReviews) ASC
        LIMIT :limit
    """)
    suspend fun getStrugglingWords(
        accuracyThreshold: Float = 70f,
        limit: Int = 10
    ): List<VocabularyLearningEntity>

    /**
     * Get words introduced but not yet reviewed.
     */
    @Query("SELECT * FROM vocabulary_learning WHERE isIntroduced = 1 AND totalReviews = 0")
    suspend fun getUnreviewedIntroducedWords(): List<VocabularyLearningEntity>

    /**
     * Get new words that haven't been introduced yet.
     */
    @Query("SELECT * FROM vocabulary_learning WHERE isIntroduced = 0 LIMIT :limit")
    suspend fun getNewWordsToIntroduce(limit: Int = 5): List<VocabularyLearningEntity>

    /**
     * Get learning statistics.
     */
    @Query("""
        SELECT
            COUNT(*) as totalWords,
            SUM(CASE WHEN boxLevel >= 5 THEN 1 ELSE 0 END) as masteredCount,
            AVG(CASE WHEN totalReviews > 0 THEN correctReviews * 100.0 / totalReviews ELSE 0 END) as averageAccuracy,
            SUM(totalReviews) as totalReviews,
            SUM(correctReviews) as totalCorrectReviews
        FROM vocabulary_learning
    """)
    suspend fun getLearningStats(): LearningStatsResult

    /**
     * Get words reviewed today.
     */
    @Query("SELECT * FROM vocabulary_learning WHERE lastReviewDate >= :startOfDay ORDER BY lastReviewDate DESC")
    suspend fun getWordsReviewedToday(startOfDay: Long): List<VocabularyLearningEntity>

    /**
     * Get count of words reviewed today.
     */
    @Query("SELECT COUNT(*) FROM vocabulary_learning WHERE lastReviewDate >= :startOfDay")
    suspend fun getReviewCountToday(startOfDay: Long): Int

    /**
     * Insert a new learning entry.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearning(learning: VocabularyLearningEntity)

    /**
     * Insert multiple learning entries.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningEntries(entries: List<VocabularyLearningEntity>)

    /**
     * Update a learning entry.
     */
    @Update
    suspend fun updateLearning(learning: VocabularyLearningEntity)

    /**
     * Mark a word as introduced.
     */
    @Query("UPDATE vocabulary_learning SET isIntroduced = 1, firstLearnedDate = :timestamp WHERE wordId = :wordId")
    suspend fun markAsIntroduced(wordId: Long, timestamp: Long = System.currentTimeMillis())

    /**
     * Reset learning progress for a word.
     */
    @Query("""
        UPDATE vocabulary_learning SET
            easeFactor = 2.5,
            interval = 1,
            repetitions = 0,
            nextReviewDate = :currentTime,
            boxLevel = 1,
            stage = 'LEARNING',
            correctStreak = 0,
            masteredDate = NULL
        WHERE wordId = :wordId
    """)
    suspend fun resetLearningProgress(wordId: Long, currentTime: Long = System.currentTimeMillis())

    /**
     * Delete learning entry for a word.
     */
    @Query("DELETE FROM vocabulary_learning WHERE wordId = :wordId")
    suspend fun deleteLearningForWord(wordId: Long)

    /**
     * Delete all learning entries.
     */
    @Query("DELETE FROM vocabulary_learning")
    suspend fun deleteAllLearning()

    /**
     * Get average ease factor across all words.
     */
    @Query("SELECT AVG(easeFactor) FROM vocabulary_learning WHERE totalReviews > 0")
    suspend fun getAverageEaseFactor(): Float?

    /**
     * Get words by ease factor range (difficulty).
     */
    @Query("SELECT * FROM vocabulary_learning WHERE easeFactor BETWEEN :minEf AND :maxEf ORDER BY easeFactor ASC")
    fun getWordsByDifficulty(minEf: Float, maxEf: Float): Flow<List<VocabularyLearningEntity>>

    /**
     * Get the longest correct streak.
     */
    @Query("SELECT MAX(longestCorrectStreak) FROM vocabulary_learning")
    suspend fun getLongestCorrectStreak(): Int?

    /**
     * Get words with the highest accuracy.
     */
    @Query("""
        SELECT * FROM vocabulary_learning
        WHERE totalReviews >= :minReviews
        ORDER BY (correctReviews * 1.0 / totalReviews) DESC
        LIMIT :limit
    """)
    suspend fun getHighestAccuracyWords(minReviews: Int = 3, limit: Int = 10): List<VocabularyLearningEntity>
}

/**
 * Result data class for learning statistics query.
 */
data class LearningStatsResult(
    val totalWords: Int,
    val masteredCount: Int,
    val averageAccuracy: Float,
    val totalReviews: Int,
    val totalCorrectReviews: Int
)
