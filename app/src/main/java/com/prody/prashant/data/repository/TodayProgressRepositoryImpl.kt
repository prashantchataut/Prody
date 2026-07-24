package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.dao.VocabularyLearningDao
import com.prody.prashant.data.local.entity.LearningStage
import com.prody.prashant.data.local.entity.VocabularyLearningEntity
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.repository.TodayProgressRepository
import com.prody.prashant.domain.repository.TodayProgressSnapshot
import com.prody.prashant.domain.repository.TodayWordCompletion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodayProgressRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val vocabularyDao: VocabularyDao,
    private val vocabularyLearningDao: VocabularyLearningDao,
    private val journalDao: JournalDao
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

        // Temporary compatibility write for older vocabulary screens. Remove once
        // every consumer reads user-scoped VocabularyLearningEntity state.
        val storedWord = vocabularyDao.getWordById(wordId)
        if (storedWord?.isLearned != true) {
            vocabularyDao.markAsLearned(wordId, completedAtMillis)
        }
        if (!wasIntroduced) {
            userDao.incrementWordsLearned()
            userDao.addPoints(25)
        }

        TodayWordCompletion(newlyIntroduced = !wasIntroduced)
    }
}
