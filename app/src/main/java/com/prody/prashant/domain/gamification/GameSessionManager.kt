package com.prody.prashant.domain.gamification

import android.util.Log
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.domain.progress.BloomResult
import com.prody.prashant.domain.progress.SeedBloomService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Game Session Manager - Orchestrates the core game loop.
 *
 * Handles session completion for all 3 game modes:
 * - Reflect (Journaling)
 * - Sharpen (Vocabulary/Flashcards)
 * - Commit (Future messages)
 *
 * Each mode completion produces a SessionResult with:
 * - What you did (summary)
 * - What you gained (XP, tokens)
 * - What unlocked (achievements, items)
 * - Next suggested action
 *
 * This is the single entry point for all gamification rewards.
 * No spam toasts - just clean session summaries.
 */
@Singleton
class GameSessionManager @Inject constructor(
    private val gameSkillSystem: GameSkillSystem,
    private val missionSystem: MissionSystem,
    private val seedBloomService: SeedBloomService,
    private val userDao: UserDao
) {
    companion object {
        private const val TAG = "GameSessionManager"

        // Base XP values for each activity
        const val JOURNAL_ENTRY_BASE_XP = 40
        const val JOURNAL_WORD_BONUS_PER_50 = 10 // Bonus per 50 words
        const val FLASHCARD_SESSION_BASE_XP = 30
        const val FLASHCARD_CARD_XP = 3 // Per card reviewed
        const val WORD_LEARNED_XP = 15
        const val FUTURE_MESSAGE_BASE_XP = 35
        const val SEED_BLOOM_BONUS_TOKENS = 10
    }

    // =========================================================================
    // REFLECT MODE (Journaling)
    // =========================================================================

    /**
     * Complete a journal entry session.
     * Awards Clarity XP based on word count.
     */
    suspend fun completeReflectSession(
        entryId: Long,
        wordCount: Int,
        content: String,
        mood: String
    ): SessionResult {
        val idempotencyKey = "journal_${entryId}"
        val builder = SessionResultBuilder(SessionType.REFLECT)

        // Calculate XP based on word count
        val wordBonus = (wordCount / 50) * JOURNAL_WORD_BONUS_PER_50
        val totalXp = JOURNAL_ENTRY_BASE_XP + wordBonus

        // Award Clarity XP
        val xpResult = gameSkillSystem.awardSkillXp(
            skillType = GameSkillSystem.SkillType.CLARITY,
            baseXp = totalXp,
            idempotencyKey = idempotencyKey
        )

        builder.headline("Journal entry saved")
        builder.addDetail("$wordCount words")
        builder.addDetail("Mood: $mood")

        when (xpResult) {
            is SkillXpResult.Success -> {
                builder.addSkillXp(GameSkillSystem.SkillType.CLARITY, xpResult.xpAwarded)
                if (xpResult.leveledUp) {
                    builder.addLevelUp(
                        SkillLevelUp(
                            GameSkillSystem.SkillType.CLARITY,
                            xpResult.previousLevel,
                            xpResult.newLevel
                        )
                    )
                }
            }
            is SkillXpResult.DailyCapReached -> {
                builder.addDetail("Daily Clarity cap reached")
            }
            is SkillXpResult.AlreadyAwarded -> {
                Log.d(TAG, "Journal entry already rewarded: $entryId")
            }
            is SkillXpResult.Error -> {
                Log.e(TAG, "Error awarding journal XP: ${xpResult.message}")
            }
        }

        // Record mission progress
        val missionResult = missionSystem.recordProgress(MissionSystem.MissionType.REFLECT, 1)
        if (missionResult is MissionProgressResult.Success) {
            builder.missionProgress(
                MissionProgress(
                    missionTitle = missionResult.missionTitle,
                    previousProgress = missionResult.previousProgress,
                    newProgress = missionResult.newProgress,
                    targetValue = missionResult.targetValue,
                    justCompleted = missionResult.justCompleted
                )
            )
            if (missionResult.justCompleted) {
                builder.tokens(MissionSystem.DAILY_MISSION_TOKENS)
            }
        }

        // Check for seed bloom
        val bloomResult = seedBloomService.checkAndBloom(content, "journal", entryId)
        if (bloomResult is BloomResult.Bloomed) {
            builder.seedBloom(
                SeedBloomInfo(
                    seedContent = bloomResult.seedContent,
                    bonusTokens = SEED_BLOOM_BONUS_TOKENS
                )
            )
            builder.tokens((builder.build().rewards.tokens) + SEED_BLOOM_BONUS_TOKENS)
        }

        // Suggest next action
        builder.nextSuggestion(getNextSuggestion(SessionType.REFLECT))

        // Update streak
        updateStreak()

        Log.d(TAG, "Reflect session completed: $wordCount words, ${builder.build().rewards.totalXp} XP")

        return builder.build()
    }

    // =========================================================================
    // SHARPEN MODE (Vocabulary/Flashcards)
    // =========================================================================

    /**
     * Complete a flashcard review session.
     * Awards Discipline XP based on cards reviewed.
     */
    suspend fun completeSharpenSession(
        sessionId: String,
        cardsReviewed: Int,
        correctCount: Int,
        accuracy: Float
    ): SessionResult {
        val idempotencyKey = "flashcard_session_${sessionId}"
        val builder = SessionResultBuilder(SessionType.SHARPEN)

        // Calculate XP based on performance
        val baseXp = FLASHCARD_SESSION_BASE_XP + (cardsReviewed * FLASHCARD_CARD_XP)
        val accuracyBonus = if (accuracy >= 0.8f) 20 else if (accuracy >= 0.6f) 10 else 0
        val totalXp = baseXp + accuracyBonus

        // Award Discipline XP
        val xpResult = gameSkillSystem.awardSkillXp(
            skillType = GameSkillSystem.SkillType.DISCIPLINE,
            baseXp = totalXp,
            idempotencyKey = idempotencyKey
        )

        builder.headline("Review session complete")
        builder.addDetail("$cardsReviewed cards reviewed")
        builder.addDetail("${(accuracy * 100).toInt()}% accuracy")
        builder.addDetail("$correctCount correct")

        when (xpResult) {
            is SkillXpResult.Success -> {
                builder.addSkillXp(GameSkillSystem.SkillType.DISCIPLINE, xpResult.xpAwarded)
                if (xpResult.leveledUp) {
                    builder.addLevelUp(
                        SkillLevelUp(
                            GameSkillSystem.SkillType.DISCIPLINE,
                            xpResult.previousLevel,
                            xpResult.newLevel
                        )
                    )
                }
            }
            is SkillXpResult.DailyCapReached -> {
                builder.addDetail("Daily Discipline cap reached")
            }
            is SkillXpResult.AlreadyAwarded -> {
                Log.d(TAG, "Flashcard session already rewarded: $sessionId")
            }
            is SkillXpResult.Error -> {
                Log.e(TAG, "Error awarding flashcard XP: ${xpResult.message}")
            }
        }

        // Record mission progress
        val missionResult = missionSystem.recordProgress(
            MissionSystem.MissionType.SHARPEN,
            cardsReviewed
        )
        if (missionResult is MissionProgressResult.Success) {
            builder.missionProgress(
                MissionProgress(
                    missionTitle = missionResult.missionTitle,
                    previousProgress = missionResult.previousProgress,
                    newProgress = missionResult.newProgress,
                    targetValue = missionResult.targetValue,
                    justCompleted = missionResult.justCompleted
                )
            )
            if (missionResult.justCompleted) {
                builder.tokens(MissionSystem.DAILY_MISSION_TOKENS)
            }
        }

        // Suggest next action
        builder.nextSuggestion(getNextSuggestion(SessionType.SHARPEN))

        // Update streak
        updateStreak()

        Log.d(TAG, "Sharpen session completed: $cardsReviewed cards, ${builder.build().rewards.totalXp} XP")

        return builder.build()
    }

    /**
     * Award XP for learning a new word (separate from flashcard sessions).
     */
    suspend fun recordWordLearned(wordId: Long): SkillXpResult {
        return gameSkillSystem.awardSkillXp(
            skillType = GameSkillSystem.SkillType.DISCIPLINE,
            baseXp = WORD_LEARNED_XP,
            idempotencyKey = "word_learned_${wordId}"
        )
    }

    // =========================================================================
    // COMMIT MODE (Future Messages)
    // =========================================================================

    /**
     * Complete a future message session.
     * Awards Courage XP.
     */
    suspend fun completeCommitSession(
        messageId: Long,
        content: String,
        deliveryDate: Long
    ): SessionResult {
        val idempotencyKey = "future_message_${messageId}"
        val builder = SessionResultBuilder(SessionType.COMMIT)

        // Award Courage XP
        val xpResult = gameSkillSystem.awardSkillXp(
            skillType = GameSkillSystem.SkillType.COURAGE,
            baseXp = FUTURE_MESSAGE_BASE_XP,
            idempotencyKey = idempotencyKey
        )

        val daysUntilDelivery = ((deliveryDate - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()

        builder.headline("Message scheduled")
        builder.addDetail("Delivery in $daysUntilDelivery days")

        when (xpResult) {
            is SkillXpResult.Success -> {
                builder.addSkillXp(GameSkillSystem.SkillType.COURAGE, xpResult.xpAwarded)
                if (xpResult.leveledUp) {
                    builder.addLevelUp(
                        SkillLevelUp(
                            GameSkillSystem.SkillType.COURAGE,
                            xpResult.previousLevel,
                            xpResult.newLevel
                        )
                    )
                }
            }
            is SkillXpResult.DailyCapReached -> {
                builder.addDetail("Daily Courage cap reached")
            }
            is SkillXpResult.AlreadyAwarded -> {
                Log.d(TAG, "Future message already rewarded: $messageId")
            }
            is SkillXpResult.Error -> {
                Log.e(TAG, "Error awarding future message XP: ${xpResult.message}")
            }
        }

        // Record mission progress
        val missionResult = missionSystem.recordProgress(MissionSystem.MissionType.COMMIT, 1)
        if (missionResult is MissionProgressResult.Success) {
            builder.missionProgress(
                MissionProgress(
                    missionTitle = missionResult.missionTitle,
                    previousProgress = missionResult.previousProgress,
                    newProgress = missionResult.newProgress,
                    targetValue = missionResult.targetValue,
                    justCompleted = missionResult.justCompleted
                )
            )
            if (missionResult.justCompleted) {
                builder.tokens(MissionSystem.DAILY_MISSION_TOKENS)
            }
        }

        // Check for seed bloom
        val bloomResult = seedBloomService.checkAndBloom(content, "future_message", messageId)
        if (bloomResult is BloomResult.Bloomed) {
            builder.seedBloom(
                SeedBloomInfo(
                    seedContent = bloomResult.seedContent,
                    bonusTokens = SEED_BLOOM_BONUS_TOKENS
                )
            )
            builder.tokens((builder.build().rewards.tokens) + SEED_BLOOM_BONUS_TOKENS)
        }

        // Suggest next action
        builder.nextSuggestion(getNextSuggestion(SessionType.COMMIT))

        // Update streak
        updateStreak()

        Log.d(TAG, "Commit session completed: ${builder.build().rewards.totalXp} XP")

        return builder.build()
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    /**
     * Generate a unique session ID.
     */
    fun generateSessionId(): String = UUID.randomUUID().toString()

    /**
     * Get current player skills.
     */
    suspend fun getPlayerSkills(): PlayerSkills = gameSkillSystem.getPlayerSkills()

    /**
     * Observe player skills reactively.
     */
    fun observePlayerSkills(): Flow<PlayerSkills> = gameSkillSystem.observePlayerSkills()

    /**
     * Get today's missions.
     */
    suspend fun getTodayMissions() = missionSystem.getTodayMissions()

    /**
     * Observe today's missions.
     */
    fun observeTodayMissions() = missionSystem.observeTodayMissions()

    /**
     * Get weekly trial.
     */
    suspend fun getWeeklyTrial() = missionSystem.getWeeklyTrial()

    /**
     * Observe weekly trial.
     */
    fun observeWeeklyTrial() = missionSystem.observeWeeklyTrial()

    private suspend fun updateStreak() {
        try {
            val profile = userDao.getUserProfileSync() ?: return
            val now = System.currentTimeMillis()
            val lastActive = profile.lastActiveDate

            // Check if we need to update streak
            val oneDayMs = 24 * 60 * 60 * 1000L
            val daysSinceLast = if (lastActive > 0) (now - lastActive) / oneDayMs else 0

            when {
                daysSinceLast == 0L -> {
                    // Same day, no change
                }
                daysSinceLast == 1L -> {
                    // Consecutive day, increment streak
                    userDao.updateStreak(profile.currentStreak + 1)
                }
                else -> {
                    // Streak broken, reset to 1
                    userDao.updateStreak(1)
                }
            }

            userDao.updateLastActiveDate(now)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating streak", e)
        }
    }

    private suspend fun getNextSuggestion(justCompleted: SessionType): NextSuggestion? {
        val missions = missionSystem.getTodayMissions()
        val incompleteMissions = missions.filter { !it.isCompleted }

        // Suggest completing a different mode's mission
        val otherModeMission = incompleteMissions.find { mission ->
            when (justCompleted) {
                SessionType.REFLECT -> mission.missionType != "reflect"
                SessionType.SHARPEN -> mission.missionType != "sharpen"
                SessionType.COMMIT -> mission.missionType != "commit"
            }
        }

        if (otherModeMission != null) {
            return NextSuggestion(
                type = SuggestionType.TRY_DIFFERENT_MODE,
                title = otherModeMission.title,
                reason = "Complete your daily missions"
            )
        }

        // Check for seed bloom opportunity
        val todaySeed = seedBloomService.getTodaySeed()
        if (!todaySeed.hasBloomedToday) {
            return NextSuggestion(
                type = SuggestionType.BLOOM_SEED,
                title = "Bloom today's seed: ${todaySeed.seedContent}",
                reason = "Use it in your writing to earn bonus tokens"
            )
        }

        // Check weekly trial
        val trial = missionSystem.getWeeklyTrial()
        if (!trial.isCompleted && trial.currentProgress < trial.targetValue) {
            return NextSuggestion(
                type = SuggestionType.CHECK_WEEKLY_TRIAL,
                title = trial.title,
                reason = "${trial.currentProgress}/${trial.targetValue} complete"
            )
        }

        return NextSuggestion(
            type = SuggestionType.VIEW_PROGRESS,
            title = "View your progress",
            reason = "See how you're growing"
        )
    }
}
