package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for vocabulary operations.
 * Provides abstraction layer between ViewModels and data sources.
 */
interface VocabularyRepository {
    /**
     * Get all vocabulary words.
     */
    fun getAllWords(): Flow<List<VocabularyEntity>>

    /**
     * Get a word by its ID.
     */
    suspend fun getWordById(id: Long): Result<VocabularyEntity>

    /**
     * Observe a word by its ID.
     */
    fun observeWordById(id: Long): Flow<VocabularyEntity?>

    /**
     * Get the word of the day (randomly selected from unshown words).
     */
    suspend fun getWordOfTheDay(): Result<VocabularyEntity>

    /**
     * Get all learned words.
     */
    fun getLearnedWords(): Flow<List<VocabularyEntity>>

    /**
     * Get favorite words.
     */
    fun getFavoriteWords(): Flow<List<VocabularyEntity>>

    /**
     * Get words that are due for review (spaced repetition).
     */
    suspend fun getWordsForReview(limit: Int = 10): List<VocabularyEntity>

    /**
     * Get words that need practice based on low mastery level.
     */
    suspend fun getWordsNeedingPractice(limit: Int = 5): List<VocabularyEntity>

    /**
     * Get words by category.
     */
    fun getWordsByCategory(category: String): Flow<List<VocabularyEntity>>

    /**
     * Get words by difficulty level.
     */
    fun getWordsByDifficulty(difficulty: Int): Flow<List<VocabularyEntity>>

    /**
     * Search vocabulary by word or definition.
     */
    fun searchVocabulary(query: String): Flow<List<VocabularyEntity>>

    /**
     * Get count of learned words.
     */
    fun getLearnedCount(): Flow<Int>

    /**
     * Get total count of words.
     */
    fun getTotalCount(): Flow<Int>

    /**
     * Get average mastery level of learned words.
     */
    fun getAverageMastery(): Flow<Float?>

    /**
     * Get all distinct categories.
     */
    fun getAllCategories(): Flow<List<String>>

    /**
     * Insert a new word.
     */
    suspend fun insertWord(word: VocabularyEntity): Result<Long>

    /**
     * Insert multiple words.
     */
    suspend fun insertWords(words: List<VocabularyEntity>): Result<Unit>

    /**
     * Update an existing word.
     */
    suspend fun updateWord(word: VocabularyEntity): Result<Unit>

    /**
     * Delete a word.
     */
    suspend fun deleteWord(word: VocabularyEntity): Result<Unit>

    /**
     * Mark a word as learned.
     */
    suspend fun markAsLearned(id: Long): Result<Unit>

    /**
     * Update favorite status of a word.
     */
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Result<Unit>

    /**
     * Mark a word as shown as daily word.
     */
    suspend fun markAsShownDaily(id: Long): Result<Unit>

    /**
     * Update review progress for spaced repetition.
     */
    suspend fun updateReviewProgress(
        id: Long,
        nextReviewAt: Long,
        masteryLevel: Int
    ): Result<Unit>

    /**
     * Process a review session for a word using SRS algorithm.
     * @param wordId The ID of the word reviewed
     * @param quality The quality rating of the recall (0-5)
     * @return Result containing updated review stats
     */
    suspend fun processWordReview(wordId: Long, quality: Int): Result<Unit>
}
