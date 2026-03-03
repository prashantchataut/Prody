package com.prody.prashant.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.InitialContentData
import androidx.room.withTransaction
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.database.ProdyDatabase
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.data.local.entity.UserProfileEntity
import com.prody.prashant.data.local.entity.UserStatsEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.gamification.Achievements
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val vocabularyDao: VocabularyDao,
    private val quoteDao: QuoteDao,
    private val proverbDao: ProverbDao,
    private val idiomDao: IdiomDao,
    private val phraseDao: PhraseDao,
    private val userDao: UserDao,
    private val database: ProdyDatabase
) : ViewModel() {

    companion object {
        private const val TAG = "OnboardingViewModel"
    }

    fun completeOnboarding() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. DataStore updates - executed OUTSIDE Room transaction as per best practices.
                // Room transactions should only contain Room operations.
                preferencesManager.setOnboardingCompleted(true)
                preferencesManager.setFirstLaunchTime(System.currentTimeMillis())
                val userId = UUID.randomUUID().toString()
                preferencesManager.setUserId(userId)

                // 2. Room database operations - wrapped in a single transaction for performance.
                // This drastically reduces disk sync overhead from multiple individual inserts.
                database.withTransaction {
                    // Initialize user profile
                    val userProfile = UserProfileEntity(
                        id = 1,
                        displayName = "Growth Seeker",
                        joinedAt = System.currentTimeMillis()
                    )
                    userDao.insertUserProfile(userProfile)

                    // Initialize user stats
                    val userStats = UserStatsEntity(
                        id = 1,
                        lastResetDate = System.currentTimeMillis()
                    )
                    userDao.insertUserStats(userStats)

                    // Initialize achievements
                    val achievements = Achievements.allAchievements.map { achievement ->
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
                    userDao.insertAchievements(achievements)

                    // Populate initial content within the same transaction
                    performInitialContentPopulation()
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error during onboarding completion", e)
                // Still mark onboarding as completed to prevent being stuck in a loop
                try {
                    preferencesManager.setOnboardingCompleted(true)
                } catch (prefError: Exception) {
                    android.util.Log.e(TAG, "Failed to set onboarding completed flag", prefError)
                }
            }
        }
    }

    /**
     * Internal helper to populate all initial content entities.
     * Should be called within a transaction for optimal performance.
     */
    private suspend fun performInitialContentPopulation() {
        // We use individual inserts/batches but no internal try-catches here
        // to allow the parent transaction to handle atomicity and avoid disk sync overhead.
        vocabularyDao.insertWords(InitialContentData.vocabularyWords)
        quoteDao.insertQuotes(InitialContentData.quotes)
        proverbDao.insertProverbs(InitialContentData.proverbs)
        idiomDao.insertIdioms(InitialContentData.idioms)
        phraseDao.insertPhrases(InitialContentData.phrases)
    }
}
