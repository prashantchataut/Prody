package com.prody.prashant.data.repository

import androidx.room.withTransaction
import com.prody.prashant.data.InitialContentData
import com.prody.prashant.data.local.dao.IdiomDao
import com.prody.prashant.data.local.dao.PhraseDao
import com.prody.prashant.data.local.dao.ProverbDao
import com.prody.prashant.data.local.dao.QuoteDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.database.ProdyDatabase
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.data.local.entity.UserProfileEntity
import com.prody.prashant.data.local.entity.UserStatsEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.gamification.Achievements
import com.prody.prashant.domain.repository.OnboardingPreferences
import com.prody.prashant.domain.repository.OnboardingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val vocabularyDao: VocabularyDao,
    private val quoteDao: QuoteDao,
    private val proverbDao: ProverbDao,
    private val idiomDao: IdiomDao,
    private val phraseDao: PhraseDao,
    private val userDao: UserDao,
    private val database: ProdyDatabase
) : OnboardingRepository {

    override suspend fun completeSetup(preferences: OnboardingPreferences): Result<Unit> =
        runSuspendCatching(
            errorType = ErrorType.DATABASE,
            errorMessage = "Kairos could not finish local setup."
        ) {
            val now = System.currentTimeMillis()
            val categories = preferences.wisdomCategories
                .map(String::lowercase)
                .filter(String::isNotBlank)
                .toSet()
                .ifEmpty { DEFAULT_WISDOM_CATEGORIES }

            database.withTransaction {
                userDao.insertUserProfile(
                    UserProfileEntity(
                        id = 1,
                        displayName = "Reader",
                        joinedAt = now
                    )
                )
                userDao.insertUserStats(
                    UserStatsEntity(
                        id = 1,
                        lastResetDate = now
                    )
                )
                userDao.insertAchievements(
                    Achievements.allAchievements.map { achievement ->
                        AchievementEntity(
                            id = achievement.id,
                            name = achievement.name,
                            description = achievement.description,
                            iconId = achievement.id,
                            category = achievement.category.name.lowercase(),
                            requirement = achievement.getRequirementTarget(),
                            currentProgress = 0,
                            isUnlocked = false,
                            rewardType = "points",
                            rewardValue = achievement.xpReward.toString(),
                            rarity = achievement.rarity.name.lowercase()
                        )
                    }
                )
                vocabularyDao.insertWords(InitialContentData.vocabularyWords)
                quoteDao.insertQuotes(InitialContentData.quotes)
                proverbDao.insertProverbs(InitialContentData.proverbs)
                idiomDao.insertIdioms(InitialContentData.idioms)
                phraseDao.insertPhrases(InitialContentData.phrases)
            }

            // Preference writes follow the successful Room transaction. On retry,
            // all inserts are idempotent and setup remains safe after process death.
            preferencesManager.setFirstLaunchTime(now)
            preferencesManager.setUserId(LOCAL_USER_ID)
            preferencesManager.setVocabularyDifficulty(preferences.vocabularyDifficulty.coerceIn(1, 5))
            preferencesManager.setSelectedWisdomCategories(categories)
            preferencesManager.setOnboardingCompleted(true)
        }

    private companion object {
        const val LOCAL_USER_ID = "local"
        val DEFAULT_WISDOM_CATEGORIES = setOf("wisdom", "life", "motivation")
    }
}
