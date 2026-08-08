package com.kairos.app.data.repository

import com.kairos.app.data.local.dao.JournalDao
import com.kairos.app.data.local.dao.QuoteDao
import com.kairos.app.data.local.dao.UserDao
import com.kairos.app.data.local.dao.VocabularyDao
import com.kairos.app.data.local.dao.VocabularyLearningDao
import com.kairos.app.data.local.entity.LearningStage
import com.kairos.app.data.local.entity.VocabularyLearningEntity
import com.kairos.app.domain.common.ErrorType
import com.kairos.app.domain.common.Result
import com.kairos.app.domain.common.runSuspendCatching
import com.kairos.app.domain.repository.TodayProgressRepository
import com.kairos.app.domain.repository.TodayProgressSnapshot
import com.kairos.app.domain.repository.TodayWordCompletion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodayProgressRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val vocabularyDao: VocabularyDao,
    private val vocabularyLearningDao: VocabularyLearningDao,
    private val journalDao: JournalDao,
    private val quoteDao: QuoteDao
) : TodayProgressRepository {

    override fun observeProgress(
        userId: String,
        weekStartMillis: Long,
        nowMillis: Long
    ): Flow<TodayProgressSnapshot> = combine(
        userDao.getUserProfile(),
        journalDao.getEntriesByDateRange(weekStartMillis, nowMillis),
        vocabularyLearningDao.getIntroducedCountSince(userId, weekStartMillis)
    ) { profile, journalEntries, learnedWords ->
        TodayProgressSnapshot(
            displayName = profile?.displayName,
            currentStreak = profile?.currentStreak ?: 0,
            journalEntriesThisWeek = journalEntries.size,
            wordsLearnedThisWeek = learnedWords
        )
    }

    override suspend fun completeWord(
        userId: String,
        wordId: Long,
        completedAtMillis: Long
    ): Result<TodayWordCompletion> = runSuspendCatching(
        errorType = ErrorType.DATABASE,
        errorMessage = "Could not save vocabulary progress."
    ) {
        val learning = vocabularyLearningDao.getLearningForWordForUser(wordId, userId)
        val wasIntroduced = learning?.isIntroduced == true
        val updatedLearning = (learning ?: VocabularyLearningEntity(
            wordId = wordId,
            userId = userId
        )).copy(
            isIntroduced = true,
            firstLearnedDate = learning?.firstLearnedDate ?: completedAtMillis,
            stage = if (learning == null || learning.stage == LearningStage.NEW.name) {
                LearningStage.LEARNING.name
            } else {
                learning.stage
            }
        )
        vocabularyLearningDao.insertLearning(updatedLearning)

        if (!wasIntroduced) {
            userDao.incrementWordsLearned()
            userDao.addPoints(25)
        }

        TodayWordCompletion(newlyIntroduced = !wasIntroduced)
    }

    override suspend fun setWordSaved(
        userId: String,
        wordId: Long,
        saved: Boolean
    ): Result<Unit> = runSuspendCatching(
        errorType = ErrorType.DATABASE,
        errorMessage = "Could not update saved words."
    ) {
        vocabularyDao.updateFavoriteStatus(wordId, saved)
    }

    override suspend fun setQuoteSaved(
        userId: String,
        quoteId: Long,
        saved: Boolean
    ): Result<Unit> = runSuspendCatching(
        errorType = ErrorType.DATABASE,
        errorMessage = "Could not update saved quotes."
    ) {
        quoteDao.updateFavoriteStatus(quoteId, saved)
    }

    override suspend fun isWordSaved(wordId: Long): Boolean {
        return vocabularyDao.getWordById(wordId)?.isFavorite == true
    }

    override suspend fun isQuoteSaved(quoteId: Long): Boolean {
        return quoteDao.getQuoteById(quoteId)?.isFavorite == true
    }
}
