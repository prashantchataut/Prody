package com.prody.prashant.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.InitialContentData
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.data.local.entity.UserProfileEntity
import com.prody.prashant.data.local.entity.UserStatsEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.theme.Achievements
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
    private val userDao: UserDao
) : ViewModel() {

    companion object {
        private const val TAG = "OnboardingViewModel"
    }

    fun completeOnboarding() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Mark onboarding as completed
                preferencesManager.setOnboardingCompleted(true)

                // Set first launch time
                preferencesManager.setFirstLaunchTime(System.currentTimeMillis())

                // Generate unique user ID
                val userId = UUID.randomUUID().toString()
                preferencesManager.setUserId(userId)

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
                        requirement = achievement.requirement,
                        currentProgress = 0,
                        isUnlocked = false,
                        rewardType = "points",
                        rewardValue = achievement.rewardPoints.toString(),
                        rarity = achievement.rarity.name.lowercase()
                    )
                }
                userDao.insertAchievements(achievements)

                // Populate initial content
                populateInitialContent()
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

    private suspend fun populateInitialContent() {
        try {
            // Insert vocabulary
            vocabularyDao.insertWords(InitialContentData.vocabularyWords)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error inserting vocabulary", e)
        }

        try {
            // Insert quotes
            quoteDao.insertQuotes(InitialContentData.quotes)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error inserting quotes", e)
        }

        try {
            // Insert proverbs
            proverbDao.insertProverbs(InitialContentData.proverbs)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error inserting proverbs", e)
        }

        try {
            // Insert idioms
            idiomDao.insertIdioms(InitialContentData.idioms)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error inserting idioms", e)
        }

        try {
            // Insert phrases
            phraseDao.insertPhrases(InitialContentData.phrases)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error inserting phrases", e)
        }
    }
}
