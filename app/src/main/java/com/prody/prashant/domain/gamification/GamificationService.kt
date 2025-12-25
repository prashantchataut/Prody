package com.prody.prashant.domain.gamification

import android.util.Log
import com.prody.prashant.data.local.dao.ChallengeDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.data.local.entity.StreakHistoryEntity
import com.prody.prashant.data.local.entity.UserProfileEntity
import com.prody.prashant.data.local.entity.UserStatsEntity
import com.prody.prashant.domain.identity.ProdyAchievements
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.identity.ProdyRanks
import com.prody.prashant.domain.model.ChallengeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production-grade gamification service that manages all game mechanics:
 * - Points/XP system
 * - Achievement tracking and unlocking
 * - Streak calculation and maintenance
 * - Rank/Level progression
 *
 * This service is the single source of truth for all gamification logic,
 * ensuring consistency across the app.
 */
@Singleton
class GamificationService @Inject constructor(
    private val userDao: UserDao,
    private val challengeDao: ChallengeDao,
    private val preferencesManager: PreferencesManager
) {
    companion object {
        private const val TAG = "GamificationService"

        // Point values for activities
        const val POINTS_JOURNAL_ENTRY = 50
        const val POINTS_WORD_LEARNED = 15
        const val POINTS_QUOTE_READ = 5
        const val POINTS_PROVERB_EXPLORED = 8
        const val POINTS_FUTURE_LETTER_SENT = 50
        const val POINTS_FUTURE_LETTER_RECEIVED = 30
        const val POINTS_DAILY_CHECK_IN = 5
        const val POINTS_STREAK_BONUS_PER_DAY = 2
        const val POINTS_REVIEW_COMPLETED = 15
        const val POINTS_BUDDHA_CONVERSATION = 15

        // Maximum daily points to prevent abuse
        const val MAX_DAILY_POINTS = 500
    }

    /**
     * Activity types for tracking
     */
    enum class ActivityType {
        JOURNAL_ENTRY,
        WORD_LEARNED,
        QUOTE_READ,
        PROVERB_EXPLORED,
        FUTURE_LETTER_SENT,
        FUTURE_LETTER_RECEIVED,
        DAILY_CHECK_IN,
        REVIEW_COMPLETED,
        BUDDHA_CONVERSATION
    }

    /**
     * Initializes user profile and achievements if not exists.
     * Should be called on app startup.
     */
    suspend fun initializeUserData() = withContext(Dispatchers.IO) {
        // PERF: Check flag to ensure this heavy setup runs only once, ever.
        if (preferencesManager.gamificationInitialized.first()) {
            Log.d(TAG, "Gamification data already initialized. Skipping setup.")
            return@withContext
        }

        try {
            Log.d(TAG, "Performing one-time initialization of gamification data...")

            // Initialize user profile if needed
            val existingProfile = userDao.getUserProfileSync()
            if (existingProfile == null) {
                Log.d(TAG, "Creating new user profile")
                userDao.insertUserProfile(UserProfileEntity())
            }

            // Initialize user stats if needed
            val existingStats = userDao.getUserStats().firstOrNull()
            if (existingStats == null) {
                Log.d(TAG, "Creating new user stats")
                userDao.insertUserStats(UserStatsEntity())
            }

            // Initialize achievements if needed
            val existingAchievements = userDao.getAllAchievements().first()
            if (existingAchievements.isEmpty()) {
                Log.d(TAG, "Initializing ${ProdyAchievements.allAchievements.size} achievements")
                val achievementEntities = ProdyAchievements.allAchievements.map { achievement ->
                    AchievementEntity.fromDomain(
                        id = achievement.id,
                        name = achievement.name,
                        description = achievement.description,
                        category = achievement.category.id,
                        rarity = achievement.rarity.id,
                        iconName = achievement.iconName,
                        requirement = achievement.requirement,
                        celebrationMessage = achievement.celebrationMessage,
                        rewardPoints = achievement.rewardPoints
                    )
                }
                userDao.insertAchievements(achievementEntities)
                Log.d(TAG, "Achievements initialized successfully")
            }

            // Set the flag to true after successful initialization
            preferencesManager.setGamificationInitialized(true)
            Log.d(TAG, "Gamification data initialization complete. Flag set to true.")

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing user data. The process will retry on next launch.", e)
            // Do NOT set the flag to true if initialization fails
        }
    }

    /**
     * Records an activity and awards points accordingly.
     * Also updates achievement progress and checks for unlocks.
     *
     * @param activityType The type of activity completed
     * @return The points awarded (may be 0 if daily cap reached)
     */
    suspend fun recordActivity(activityType: ActivityType): Int = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync() ?: return@withContext 0

            // Check daily cap
            val stats = userDao.getUserStats().firstOrNull()
            val todayPoints = stats?.dailyPointsEarned ?: 0
            if (todayPoints >= MAX_DAILY_POINTS) {
                Log.d(TAG, "Daily points cap reached ($MAX_DAILY_POINTS)")
                return@withContext 0
            }

            val basePoints = when (activityType) {
                ActivityType.JOURNAL_ENTRY -> POINTS_JOURNAL_ENTRY
                ActivityType.WORD_LEARNED -> POINTS_WORD_LEARNED
                ActivityType.QUOTE_READ -> POINTS_QUOTE_READ
                ActivityType.PROVERB_EXPLORED -> POINTS_PROVERB_EXPLORED
                ActivityType.FUTURE_LETTER_SENT -> POINTS_FUTURE_LETTER_SENT
                ActivityType.FUTURE_LETTER_RECEIVED -> POINTS_FUTURE_LETTER_RECEIVED
                ActivityType.DAILY_CHECK_IN -> POINTS_DAILY_CHECK_IN
                ActivityType.REVIEW_COMPLETED -> POINTS_REVIEW_COMPLETED
                ActivityType.BUDDHA_CONVERSATION -> POINTS_BUDDHA_CONVERSATION
            }

            // Apply streak bonus
            val streakBonus = profile.currentStreak * POINTS_STREAK_BONUS_PER_DAY
            val totalPoints = (basePoints + streakBonus).coerceAtMost(MAX_DAILY_POINTS - todayPoints)

            // Award points
            userDao.addPoints(totalPoints)
            userDao.addDailyPoints(totalPoints)

            // Update activity-specific stats
            when (activityType) {
                ActivityType.JOURNAL_ENTRY -> userDao.incrementJournalEntries()
                ActivityType.WORD_LEARNED -> userDao.incrementWordsLearned()
                ActivityType.FUTURE_LETTER_SENT -> userDao.incrementFutureMessages()
                else -> { /* Other activities don't have dedicated counters */ }
            }

            // Update streak
            updateStreak()

            // Update achievement progress
            updateAchievementProgress(activityType, profile)

            // Record progress for any joined challenges that match this activity type
            recordChallengeProgress(activityType)

            Log.d(TAG, "Awarded $totalPoints points for $activityType (base: $basePoints, streak bonus: $streakBonus)")
            return@withContext totalPoints
        } catch (e: Exception) {
            Log.e(TAG, "Error recording activity", e)
            return@withContext 0
        }
    }

    /**
     * Updates the user's streak based on activity.
     * Called automatically when recording an activity.
     */
    suspend fun updateStreak() = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync() ?: return@withContext
            val today = getStartOfDayMillis()
            val lastActive = getStartOfDayMillis(profile.lastActiveDate)

            val daysDiff = ((today - lastActive) / (24 * 60 * 60 * 1000)).toInt()

            val newStreak = when {
                daysDiff == 0 -> profile.currentStreak // Same day, maintain streak
                daysDiff == 1 -> profile.currentStreak + 1 // Next day, increment streak
                else -> 1 // Gap > 1 day, reset streak
            }

            // Update streak
            userDao.updateStreak(newStreak)
            userDao.updateLastActiveDate(System.currentTimeMillis())

            // Record in streak history
            val historyEntry = StreakHistoryEntity(
                date = today,
                activitiesCompleted = "",
                pointsEarned = 0,
                streakDay = newStreak
            )
            userDao.insertStreakHistory(historyEntry)

            // Check streak achievements
            checkStreakAchievements(newStreak)

            Log.d(TAG, "Streak updated: $newStreak (previous: ${profile.currentStreak}, days gap: $daysDiff)")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating streak", e)
        }
    }

    /**
     * Updates achievement progress based on activity type.
     */
    private suspend fun updateAchievementProgress(
        activityType: ActivityType,
        profile: UserProfileEntity
    ) {
        when (activityType) {
            ActivityType.JOURNAL_ENTRY -> {
                val count = profile.journalEntriesCount + 1
                updateJournalAchievements(count)
            }
            ActivityType.WORD_LEARNED -> {
                val count = profile.wordsLearned + 1
                updateWordAchievements(count)
            }
            ActivityType.QUOTE_READ -> {
                val count = profile.quotesReflected + 1
                updateQuoteAchievements(count)
            }
            ActivityType.FUTURE_LETTER_SENT -> {
                val count = profile.futureLettersSent + 1
                updateFutureLetterAchievements(count, true)
            }
            ActivityType.FUTURE_LETTER_RECEIVED -> {
                val count = profile.futureLettersReceived + 1
                updateFutureLetterAchievements(count, false)
            }
            ActivityType.BUDDHA_CONVERSATION -> {
                val count = profile.buddhaConversations + 1
                updateBuddhaAchievements(count)
            }
            else -> { /* No achievements for these activities */ }
        }
    }

    private suspend fun updateJournalAchievements(count: Int) {
        val milestones = listOf(
            "first_journal" to 1,
            "journal_5" to 5,
            "journal_10" to 10,
            "journal_30" to 30,
            "journal_50" to 50,
            "journal_100" to 100,
            "journal_365" to 365
        )
        checkMilestoneAchievements(milestones, count)
    }

    private suspend fun updateWordAchievements(count: Int) {
        val milestones = listOf(
            "first_word" to 1,
            "word_collector_10" to 10,
            "word_collector_25" to 25,
            "word_collector_50" to 50,
            "word_collector_100" to 100,
            "word_collector_250" to 250,
            "word_collector_500" to 500,
            "word_collector_1000" to 1000
        )
        checkMilestoneAchievements(milestones, count)
    }

    private suspend fun updateQuoteAchievements(count: Int) {
        val milestones = listOf(
            "quote_reader_10" to 10,
            "quote_devotee" to 50,
            "quote_collector_100" to 100
        )
        checkMilestoneAchievements(milestones, count)
    }

    private suspend fun updateFutureLetterAchievements(count: Int, isSent: Boolean) {
        val milestones = if (isSent) {
            listOf(
                "first_future_letter" to 1,
                "time_traveler" to 5,
                "temporal_correspondent" to 10,
                "chronicle_keeper" to 25
            )
        } else {
            listOf(
                "letter_received" to 1,
                "message_from_past" to 5
            )
        }
        checkMilestoneAchievements(milestones, count)
    }

    private suspend fun updateBuddhaAchievements(count: Int) {
        val milestones = listOf(
            "first_conversation" to 1,
            "seeker_of_wisdom" to 10,
            "student_of_buddha" to 25,
            "mindful_dialoguer" to 50,
            "enlightened_conversant" to 100
        )
        checkMilestoneAchievements(milestones, count)
    }

    private suspend fun checkStreakAchievements(streakDays: Int) {
        val milestones = listOf(
            "streak_3" to 3,
            "streak_7" to 7,
            "streak_14" to 14,
            "streak_21" to 21,
            "streak_30" to 30,
            "streak_60" to 60,
            "streak_90" to 90,
            "streak_180" to 180,
            "streak_365" to 365
        )
        checkMilestoneAchievements(milestones, streakDays)
    }

    /**
     * Checks and unlocks milestone achievements.
     */
    private suspend fun checkMilestoneAchievements(
        milestones: List<Pair<String, Int>>,
        currentValue: Int
    ) {
        for ((achievementId, requirement) in milestones) {
            try {
                val achievement = userDao.getAchievementById(achievementId)
                if (achievement != null) {
                    // Update progress
                    userDao.updateAchievementProgress(achievementId, currentValue)

                    // Unlock if requirement met and not already unlocked
                    if (!achievement.isUnlocked && currentValue >= requirement) {
                        unlockAchievement(achievementId, achievement.rewardValue.toIntOrNull() ?: 100)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking achievement $achievementId", e)
            }
        }
    }

    /**
     * Unlocks an achievement and awards bonus points.
     */
    private suspend fun unlockAchievement(achievementId: String, bonusPoints: Int) {
        try {
            userDao.unlockAchievement(achievementId)
            userDao.addPoints(bonusPoints)
            Log.d(TAG, "Achievement unlocked: $achievementId (+$bonusPoints points)")
        } catch (e: Exception) {
            Log.e(TAG, "Error unlocking achievement $achievementId", e)
        }
    }

    /**
     * Checks and potentially unlocks special time-based achievements.
     */
    suspend fun checkTimeBasedAchievements() = withContext(Dispatchers.IO) {
        try {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

            // Early bird (before 7 AM)
            if (currentHour in 5..6) {
                val achievement = userDao.getAchievementById("early_bird")
                if (achievement != null && !achievement.isUnlocked) {
                    unlockAchievement("early_bird", achievement.rewardValue.toIntOrNull() ?: 100)
                }
            }

            // Night owl (after 10 PM)
            if (currentHour >= 22 || currentHour in 0..4) {
                val achievement = userDao.getAchievementById("night_owl")
                if (achievement != null && !achievement.isUnlocked) {
                    unlockAchievement("night_owl", achievement.rewardValue.toIntOrNull() ?: 100)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking time-based achievements", e)
        }
    }

    /**
     * Gets the user's current rank based on total points.
     */
    suspend fun getCurrentRank(): ProdyRanks.Rank = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync()
            val totalPoints = profile?.totalPoints ?: 0
            ProdyRanks.Rank.fromPoints(totalPoints)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current rank", e)
            ProdyRanks.Rank.SEEKER
        }
    }

    /**
     * Resets daily stats if it's a new day.
     * Should be called on app launch.
     */
    suspend fun checkAndResetDailyStats() = withContext(Dispatchers.IO) {
        try {
            val stats = userDao.getUserStats().firstOrNull() ?: return@withContext
            val today = getStartOfDayMillis()
            val lastReset = getStartOfDayMillis(stats.lastResetDate)

            if (today > lastReset) {
                userDao.resetDailyStats(today)
                Log.d(TAG, "Daily stats reset")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting daily stats", e)
        }
    }

    /**
     * Gets the start of day in milliseconds for consistent date comparisons.
     */
    private fun getStartOfDayMillis(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Records progress for any active challenges that match the activity type.
     * Called automatically when recording an activity.
     */
    private suspend fun recordChallengeProgress(activityType: ActivityType) {
        try {
            // Map activity type to challenge type
            val challengeType = when (activityType) {
                ActivityType.JOURNAL_ENTRY -> ChallengeType.JOURNALING.name.lowercase()
                ActivityType.WORD_LEARNED -> ChallengeType.VOCABULARY.name.lowercase()
                ActivityType.DAILY_CHECK_IN -> ChallengeType.STREAK.name.lowercase()
                ActivityType.BUDDHA_CONVERSATION -> ChallengeType.MEDITATION.name.lowercase()
                ActivityType.QUOTE_READ, ActivityType.PROVERB_EXPLORED -> ChallengeType.REFLECTION.name.lowercase()
                else -> null
            }

            if (challengeType == null) {
                return
            }

            // Get all joined challenges that match this type (or are "mixed" type)
            val matchingChallenges = challengeDao.getJoinedChallengesByTypeSync(challengeType)

            for (challenge in matchingChallenges) {
                // Increment progress
                challengeDao.incrementUserProgress(challenge.id, 1)

                // Check if challenge is now completed
                val newProgress = challenge.currentUserProgress + 1
                if (newProgress >= challenge.targetCount && !challenge.isCompleted) {
                    // Mark as completed and award points
                    challengeDao.markChallengeCompleted(challenge.id)
                    if (challenge.rewardPoints > 0) {
                        userDao.addPoints(challenge.rewardPoints)
                        Log.d(TAG, "Challenge completed: ${challenge.title} (+${challenge.rewardPoints} points)")
                    }
                } else {
                    Log.d(TAG, "Challenge progress: ${challenge.title} ($newProgress/${challenge.targetCount})")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error recording challenge progress", e)
        }
    }
}
