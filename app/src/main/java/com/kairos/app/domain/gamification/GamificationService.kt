package com.kairos.app.domain.gamification

import android.util.Log
import com.kairos.app.data.local.dao.ChallengeDao
import com.kairos.app.data.local.dao.UserDao
import com.kairos.app.data.local.entity.AchievementEntity
import com.kairos.app.data.local.entity.StreakHistoryEntity
import com.kairos.app.data.local.entity.UserProfileEntity
import com.kairos.app.data.local.entity.UserStatsEntity
import com.kairos.app.domain.identity.KairosAchievements
import com.kairos.app.data.local.preferences.PreferencesManager
import com.kairos.app.domain.identity.KairosRanks
import com.kairos.app.domain.model.ChallengeType
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

        // Legacy total-points bridge. Values are deliberately smaller than the
        // skill XP system and only meaningful, completed actions earn points.
        const val POINTS_JOURNAL_ENTRY = 28
        const val POINTS_WORD_LEARNED = 12
        const val POINTS_QUOTE_READ = 0
        const val POINTS_PROVERB_EXPLORED = 0
        const val POINTS_FUTURE_LETTER_SENT = 26
        const val POINTS_FUTURE_LETTER_RECEIVED = 0
        const val POINTS_DAILY_CHECK_IN = 0
        const val POINTS_STREAK_BONUS_PER_DAY = 0
        const val POINTS_REVIEW_COMPLETED = 12
        const val POINTS_BUDDHA_CONVERSATION = 0

        // A bounded record of practice, not an infinite engagement counter.
        const val MAX_DAILY_POINTS = 180
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
            // Old installs may already be marked initialized while their progress
            // counters and achievement rows drifted apart. Reconcile at startup;
            // unlocking remains idempotent because unlocked rows are skipped.
            reconcileAchievementProgress()
            return@withContext
        }

        try {

            // Initialize user profile if needed
            val existingProfile = userDao.getUserProfileSync()
            if (existingProfile == null) {
                userDao.insertUserProfile(UserProfileEntity())
            }

            // Initialize user stats if needed
            val existingStats = userDao.getUserStats().firstOrNull()
            if (existingStats == null) {
                userDao.insertUserStats(UserStatsEntity())
            }

            // Upsert only missing canonical achievements. Older installs may
            // contain a legacy catalogue; replacing every row would erase earned
            // progress, while checking only for an empty table would never add the
            // new Kairos milestone IDs.
            val existingAchievementIds = userDao.getAllAchievements().first().mapTo(mutableSetOf()) { it.id }
            val missingAchievements = KairosAchievements.allAchievements
                .filterNot { it.id in existingAchievementIds }
                .map { achievement ->
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
            if (missingAchievements.isNotEmpty()) {
                userDao.insertAchievements(missingAchievements)
            }

            reconcileAchievementProgress()

            // Set the flag to true after successful initialization
            preferencesManager.setGamificationInitialized(true)

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
    suspend fun recordActivity(
        activityType: ActivityType,
        updateLegacyStreak: Boolean = true
    ): Int = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync() ?: return@withContext 0

            // Check daily cap
            val stats = userDao.getUserStats().firstOrNull()
            val todayPoints = stats?.dailyPointsEarned ?: 0

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

            // Passive actions intentionally do not alter points, streaks,
            // challenges, or achievement progress.
            if (basePoints <= 0) return@withContext 0

            val totalPoints = basePoints
                .coerceAtMost((MAX_DAILY_POINTS - todayPoints).coerceAtLeast(0))

            // The cap limits currency, not evidence. A legitimate journal entry or
            // review still advances milestones after the daily point budget is used.
            if (totalPoints > 0) {
                userDao.addPoints(totalPoints)
                userDao.addDailyPoints(totalPoints)
            }

            // Update activity-specific stats
            when (activityType) {
                ActivityType.JOURNAL_ENTRY -> userDao.incrementJournalEntries()
                ActivityType.WORD_LEARNED -> userDao.incrementWordsLearned()
                ActivityType.FUTURE_LETTER_SENT -> userDao.incrementFutureMessages()
                else -> { /* Other activities don't have dedicated counters */ }
            }

            // The session coordinator owns the visible streak update. Legacy call
            // sites can still request the profile bridge to update it directly.
            if (updateLegacyStreak) {
                updateStreak()
            }

            // Update achievement progress
            updateAchievementProgress(activityType, profile)

            // Record progress for any joined challenges that match this activity type
            recordChallengeProgress(activityType)

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
                val count = profile.futureMessagesCount + 1
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

    private suspend fun updateJournalAchievements(count: Int) =
        checkMilestoneAchievements(
            AchievementMilestonePolicy.milestonesFor(AchievementMilestonePolicy.Evidence.JOURNAL_ENTRY),
            count
        )

    private suspend fun updateWordAchievements(count: Int) =
        checkMilestoneAchievements(
            AchievementMilestonePolicy.milestonesFor(AchievementMilestonePolicy.Evidence.WORD_LEARNED),
            count
        )

    private suspend fun updateQuoteAchievements(count: Int) =
        checkMilestoneAchievements(
            AchievementMilestonePolicy.milestonesFor(AchievementMilestonePolicy.Evidence.QUOTE_REFLECTION),
            count
        )

    private suspend fun updateFutureLetterAchievements(count: Int, isSent: Boolean) =
        checkMilestoneAchievements(
            AchievementMilestonePolicy.milestonesFor(
                if (isSent) AchievementMilestonePolicy.Evidence.FUTURE_LETTER_SENT
                else AchievementMilestonePolicy.Evidence.FUTURE_LETTER_RECEIVED
            ),
            count
        )

    private suspend fun updateBuddhaAchievements(count: Int) =
        checkMilestoneAchievements(
            AchievementMilestonePolicy.milestonesFor(AchievementMilestonePolicy.Evidence.GUIDE_CONVERSATION),
            count
        )

    /**
     * Rebuilds milestone progress from durable profile counters. This repairs
     * upgrades and interrupted reward flows without inventing activity.
     */
    suspend fun reconcileAchievementProgress() = withContext(Dispatchers.IO) {
        val profile = userDao.getUserProfileSync() ?: return@withContext
        updateJournalAchievements(profile.journalEntriesCount.coerceAtLeast(0))
        updateWordAchievements(profile.wordsLearned.coerceAtLeast(0))
        checkStreakAchievements(profile.currentStreak.coerceAtLeast(0))
        updateFutureLetterAchievements(profile.futureMessagesCount.coerceAtLeast(0), isSent = true)
        updateFutureLetterAchievements(profile.futureLettersReceived.coerceAtLeast(0), isSent = false)
        if (profile.quotesReflected > 0) updateQuoteAchievements(profile.quotesReflected)
        if (profile.buddhaConversations > 0) updateBuddhaAchievements(profile.buddhaConversations)
    }

    private suspend fun checkStreakAchievements(streakDays: Int) =
        checkMilestoneAchievements(
            AchievementMilestonePolicy.milestonesFor(AchievementMilestonePolicy.Evidence.ACTIVE_DAY),
            streakDays
        )

    /**
     * Checks and unlocks milestone achievements.
     */
    private suspend fun checkMilestoneAchievements(
        milestones: List<AchievementMilestonePolicy.Milestone>,
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
            val newlyUnlocked = userDao.unlockAchievementIfLocked(achievementId) > 0
            if (newlyUnlocked && bonusPoints > 0) {
                userDao.addPoints(bonusPoints)
            }
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
    suspend fun getCurrentRank(): KairosRanks.Rank = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync()
            val totalPoints = profile?.totalPoints ?: 0
            KairosRanks.Rank.fromPoints(totalPoints)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current rank", e)
            KairosRanks.Rank.SEEKER
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
                    }
                } else {
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error recording challenge progress", e)
        }
    }
}
