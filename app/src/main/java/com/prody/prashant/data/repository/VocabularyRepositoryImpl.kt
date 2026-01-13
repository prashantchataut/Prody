package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.dao.VocabularyLearningDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.data.local.entity.VocabularyLearningEntity
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.learning.SpacedRepetitionEngine
import com.prody.prashant.domain.repository.VocabularyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of VocabularyRepository using Room database.
 */
@Singleton
class VocabularyRepositoryImpl @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    private val vocabularyLearningDao: VocabularyLearningDao,
    private val spacedRepetitionEngine: SpacedRepetitionEngine
) : VocabularyRepository {

    override fun getAllWords(): Flow<List<VocabularyEntity>> {
        return vocabularyDao.getAllVocabulary()
    }

    override suspend fun getWordById(id: Long): Result<VocabularyEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to load word") {
            vocabularyDao.getWordById(id) ?: throw NoSuchElementException("Word not found")
        }
    }

    override fun observeWordById(id: Long): Flow<VocabularyEntity?> {
        return vocabularyDao.observeWordById(id)
    }

    override suspend fun getWordOfTheDay(): Result<VocabularyEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get word of the day") {
            vocabularyDao.getWordOfTheDay()
                ?: vocabularyDao.getRandomUnlearnedWord()
                ?: throw NoSuchElementException("No words available")
        }
    }

    override fun getLearnedWords(): Flow<List<VocabularyEntity>> {
        return vocabularyDao.getLearnedWords()
    }

    override fun getFavoriteWords(): Flow<List<VocabularyEntity>> {
        return vocabularyDao.getFavoriteWords()
    }

    override suspend fun getWordsForReview(limit: Int): List<VocabularyEntity> {
        return vocabularyDao.getWordsForReview(System.currentTimeMillis(), limit)
    }

    override suspend fun getWordsNeedingPractice(limit: Int): List<VocabularyEntity> {
        return vocabularyDao.getWordsNeedingPractice(limit)
    }

    override fun getWordsByCategory(category: String): Flow<List<VocabularyEntity>> {
        return vocabularyDao.getWordsByCategory(category)
    }

    override fun getWordsByDifficulty(difficulty: Int): Flow<List<VocabularyEntity>> {
        return vocabularyDao.getWordsByDifficulty(difficulty)
    }

    override fun searchVocabulary(query: String): Flow<List<VocabularyEntity>> {
        return vocabularyDao.searchVocabulary(query)
    }

    override fun getLearnedCount(): Flow<Int> {
        return vocabularyDao.getLearnedCount()
    }

    override fun getTotalCount(): Flow<Int> {
        return vocabularyDao.getTotalCount()
    }

    override fun getAverageMastery(): Flow<Float?> {
        return vocabularyDao.getAverageMastery()
    }

    override fun getAllCategories(): Flow<List<String>> {
        return vocabularyDao.getAllCategories()
    }

    override suspend fun insertWord(word: VocabularyEntity): Result<Long> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save word") {
            vocabularyDao.insertWord(word)
        }
    }

    override suspend fun insertWords(words: List<VocabularyEntity>): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save words") {
            vocabularyDao.insertWords(words)
        }
    }

    override suspend fun updateWord(word: VocabularyEntity): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update word") {
            vocabularyDao.updateWord(word)
        }
    }

    override suspend fun deleteWord(word: VocabularyEntity): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete word") {
            vocabularyDao.deleteWord(word)
        }
    }

    override suspend fun markAsLearned(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to mark word as learned") {
            vocabularyDao.markAsLearned(id)
        }
    }

    override suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update favorite status") {
            vocabularyDao.updateFavoriteStatus(id, isFavorite)
        }
    }

    override suspend fun markAsShownDaily(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to mark word as shown") {
            vocabularyDao.markAsShownDaily(id)
        }
    }

    override suspend fun updateReviewProgress(
        id: Long,
        nextReviewAt: Long,
        masteryLevel: Int
    ): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update review progress") {
            vocabularyDao.updateReviewProgress(
                id = id,
                reviewedAt = System.currentTimeMillis(),
                nextReview = nextReviewAt,
                mastery = masteryLevel
            )
        }
    }

    override suspend fun processWordReview(wordId: Long, quality: Int): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to process review") {
            // 1. Get current learning data or create new if not exists
            var currentData = vocabularyLearningDao.getLearningForWord(wordId)
            
            if (currentData == null) {
                currentData = spacedRepetitionEngine.createInitialLearningEntity(wordId)
            }

            // 2. Calculate new state using SRS Engine
            val reviewResult = spacedRepetitionEngine.calculateNextReview(quality, currentData)

            // 3. Update learning entity with new values
            val updatedData = currentData.copy(
                easeFactor = reviewResult.newEaseFactor,
                interval = reviewResult.newInterval,
                repetitions = reviewResult.newRepetitions,
                nextReviewDate = reviewResult.nextReviewDate,
                boxLevel = reviewResult.newBoxLevel,
                stage = reviewResult.newStage.name,
                correctStreak = reviewResult.newCorrectStreak,
                lastReviewDate = System.currentTimeMillis(),
                totalReviews = currentData.totalReviews + 1,
                correctReviews = if (quality >= 3) currentData.correctReviews + 1 else currentData.correctReviews,
                masteredDate = if (reviewResult.isMastered && currentData.masteredDate == null) System.currentTimeMillis() else currentData.masteredDate
            )

            // 4. Save to database
            vocabularyLearningDao.insertLearning(updatedData)

            // 5. Also update the simpler VocabularyEntity stats for backward compatibility/UI
            vocabularyDao.updateReviewProgress(
                id = wordId,
                reviewedAt = System.currentTimeMillis(),
                nextReview = reviewResult.nextReviewDate,
                mastery = reviewResult.newBoxLevel
            )
            
            // 6. If mastered, ensure isLearned is set
            if (reviewResult.isMastered) {
                vocabularyDao.markAsLearned(wordId)
            }
        }
    }
}
