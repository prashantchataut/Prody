package com.prody.prashant.domain.gamification

import android.util.Log
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.fold
import com.prody.prashant.domain.repository.ActivityType
import com.prody.prashant.domain.repository.GamificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * New Game Session Manager - Orchestrates the core game loop using the new gamification system.
 *
 * This replaces the old GameSessionManager with the new skill-based progression system.
 *
 * Handles session completion for all 3 game modes:
 * - Reflect (Journaling) → Clarity XP
 * - Sharpen (Vocabulary/Flashcards) → Discipline XP
 * - Commit (Future messages) → Courage XP
 *
 * Each mode completion produces a SessionCompletionResult with:
 * - What you did (summary)
 * - What you gained (XP, tokens)
 * - What unlocked (achievements, level ups, rank ups)
 * - Current state (skills, streak, bloom)
 *
 * This is the single entry point for all gamification rewards.
 * No spam toasts - just clean session summaries.
 */
@Singleton
class NewGameSessionManager @Inject constructor(
    private val gamificationRepository: GamificationRepository
) {
    companion object {
        private const val TAG = "NewGameSessionManager"
    }

    // =========================================================================
    // REFLECT MODE (Journaling) → Clarity XP
    // =========================================================================

    /**
     * Complete a journal entry session.
     * Awards Clarity XP based on word count and content quality.
     */
    suspend fun completeReflectSession(
        entryId: Long,
        wordCount: Int,
        content: String,
        mood: String,
        triggeredInsight: Boolean = false
    ): SessionCompletionResult {
        val rewardKey = "journal_entry_$entryId"
        val builder = SessionCompletionResultBuilder(SessionMode.REFLECT)

        // Calculate XP using PointCalculator
        val includesReflection = PointCalculator.Clarity.detectReflection(content)
        val xpAmount = PointCalculator.Clarity.calculateJournalPoints(
            wordCount = wordCount,
            triggeredInsight = triggeredInsight,
            includesReflection = includesReflection,
            isMicroEntry = wordCount < 50
        )

        // Award Clarity XP
        val xpResult = gamificationRepository.awardSkillXp(
            skill = Skill.CLARITY,
            amount = xpAmount,
            rewardKey = rewardKey,
            respectDailyCap = true
        )

        builder.headline("Journal entry saved")
        builder.addDetail("$wordCount words")
        builder.addDetail("Mood: $mood")

        xpResult.fold(
            onSuccess = { result ->
                builder.addSkillXp(Skill.CLARITY, result.actualAmountAwarded)
                if (result.didLevelUp) {
                    builder.addLevelUp(
                        SkillLevelUpEvent(
                            skill = Skill.CLARITY,
                            previousLevel = result.previousLevel,
                            newLevel = result.newLevel,
                            tokenReward = PointCalculator.LevelUpRewards.calculateTokenReward(result.newLevel)
                        )
                    )
                }
                if (result.wasCapped) {
                    builder.addDetail("Daily Clarity cap reached")
                }
            },
            onError = { error ->
                Log.e(TAG, "Error awarding journal XP: ${error.message}")
            }
        )

        // Check for seed bloom
        val bloomResult = gamificationRepository.attemptBloom(
            content = content,
            bloomedIn = "journal",
            entryId = entryId
        )
        bloomResult.fold(
            onSuccess = { result ->
                if (result is BloomAttemptResult.Success) {
                    builder.addSeedBloom(
                        SeedBloomEvent(
                            seedContent = result.seedContent,
                            xpAwarded = result.pointsAwarded,
                            tokensAwarded = result.tokensAwarded,
                            bloomStreak = result.newBloomStreak
                        )
                    )
                }
            },
            onError = { /* Bloom failure is not critical */ }
        )

        // Record activity for streak
        recordActivityAndUpdateStreak(builder, ActivityType.JOURNAL)

        // Check for rank up
        checkForRankUp(builder)

        // Get current state
        val skillsState = gamificationRepository.getPlayerSkillsSync()
        builder.currentSkillsState(skillsState)

        Log.d(TAG, "Reflect session completed: $wordCount words, ${builder.build().totalXpAwarded} XP")

        return builder.build()
    }

    /**
     * Complete a micro-entry (quick thought capture).
     */
    suspend fun completeMicroEntry(
        entryId: Long,
        content: String
    ): SessionCompletionResult {
        val rewardKey = "micro_entry_$entryId"
        val builder = SessionCompletionResultBuilder(SessionMode.REFLECT)

        val xpAmount = PointCalculator.Clarity.MICRO_ENTRY

        val xpResult = gamificationRepository.awardSkillXp(
            skill = Skill.CLARITY,
            amount = xpAmount,
            rewardKey = rewardKey,
            respectDailyCap = true
        )

        builder.headline("Thought captured")
        builder.addDetail("Quick reflection saved")

        xpResult.fold(
            onSuccess = { result ->
                builder.addSkillXp(Skill.CLARITY, result.actualAmountAwarded)
                if (result.didLevelUp) {
                    builder.addLevelUp(
                        SkillLevelUpEvent(
                            skill = Skill.CLARITY,
                            previousLevel = result.previousLevel,
                            newLevel = result.newLevel,
                            tokenReward = PointCalculator.LevelUpRewards.calculateTokenReward(result.newLevel)
                        )
                    )
                }
            },
            onError = { /* Log error */ }
        )

        // Check for bloom (even micro entries can bloom seeds)
        val bloomResult = gamificationRepository.attemptBloom(
            content = content,
            bloomedIn = "micro_entry",
            entryId = entryId
        )
        bloomResult.fold(
            onSuccess = { result ->
                if (result is BloomAttemptResult.Success) {
                    builder.addSeedBloom(
                        SeedBloomEvent(
                            seedContent = result.seedContent,
                            xpAwarded = result.pointsAwarded,
                            tokensAwarded = result.tokensAwarded,
                            bloomStreak = result.newBloomStreak
                        )
                    )
                }
            },
            onError = { /* Bloom failure is not critical */ }
        )

        recordActivityAndUpdateStreak(builder, ActivityType.MICRO_ENTRY)

        val skillsState = gamificationRepository.getPlayerSkillsSync()
        builder.currentSkillsState(skillsState)

        return builder.build()
    }

    // =========================================================================
    // SHARPEN MODE (Vocabulary/Flashcards) → Discipline XP
    // =========================================================================

    /**
     * Complete a flashcard review session.
     * Awards Discipline XP based on cards reviewed and accuracy.
     */
    suspend fun completeSharpenSession(
        sessionId: String,
        cardsReviewed: Int,
        correctCount: Int,
        accuracy: Float
    ): SessionCompletionResult {
        val rewardKey = "flashcard_session_$sessionId"
        val builder = SessionCompletionResultBuilder(SessionMode.SHARPEN)

        val xpAmount = PointCalculator.Discipline.calculateFlashcardPoints(
            cardsReviewed = cardsReviewed,
            accuracy = accuracy
        )

        val xpResult = gamificationRepository.awardSkillXp(
            skill = Skill.DISCIPLINE,
            amount = xpAmount,
            rewardKey = rewardKey,
            respectDailyCap = true
        )

        builder.headline("Review session complete")
        builder.addDetail("$cardsReviewed cards reviewed")
        builder.addDetail("${(accuracy * 100).toInt()}% accuracy")
        builder.addDetail("$correctCount correct")

        xpResult.fold(
            onSuccess = { result ->
                builder.addSkillXp(Skill.DISCIPLINE, result.actualAmountAwarded)
                if (result.didLevelUp) {
                    builder.addLevelUp(
                        SkillLevelUpEvent(
                            skill = Skill.DISCIPLINE,
                            previousLevel = result.previousLevel,
                            newLevel = result.newLevel,
                            tokenReward = PointCalculator.LevelUpRewards.calculateTokenReward(result.newLevel)
                        )
                    )
                }
                if (result.wasCapped) {
                    builder.addDetail("Daily Discipline cap reached")
                }
            },
            onError = { error ->
                Log.e(TAG, "Error awarding flashcard XP: ${error.message}")
            }
        )

        recordActivityAndUpdateStreak(builder, ActivityType.FLASHCARD_SESSION)
        checkForRankUp(builder)

        val skillsState = gamificationRepository.getPlayerSkillsSync()
        builder.currentSkillsState(skillsState)

        Log.d(TAG, "Sharpen session completed: $cardsReviewed cards, ${builder.build().totalXpAwarded} XP")

        return builder.build()
    }

    /**
     * Record viewing Word of the Day (smaller XP action).
     */
    suspend fun recordWordOfDayViewed(wordId: Long): SessionCompletionResult {
        val rewardKey = "word_of_day_$wordId"
        val builder = SessionCompletionResultBuilder(SessionMode.SHARPEN)

        val xpResult = gamificationRepository.awardSkillXp(
            skill = Skill.DISCIPLINE,
            amount = PointCalculator.Discipline.VIEW_WORD_OF_DAY,
            rewardKey = rewardKey,
            respectDailyCap = true
        )

        builder.headline("Word explored")

        xpResult.fold(
            onSuccess = { result ->
                builder.addSkillXp(Skill.DISCIPLINE, result.actualAmountAwarded)
            },
            onError = { /* Silent on error for minor actions */ }
        )

        val skillsState = gamificationRepository.getPlayerSkillsSync()
        builder.currentSkillsState(skillsState)

        return builder.build()
    }

    /**
     * Record saving wisdom to collection.
     */
    suspend fun recordWisdomSaved(wisdomId: Long, wisdomType: String): SessionCompletionResult {
        val rewardKey = "wisdom_saved_${wisdomType}_$wisdomId"
        val builder = SessionCompletionResultBuilder(SessionMode.SHARPEN)

        val xpResult = gamificationRepository.awardSkillXp(
            skill = Skill.DISCIPLINE,
            amount = PointCalculator.Discipline.SAVE_TO_COLLECTION,
            rewardKey = rewardKey,
            respectDailyCap = true
        )

        builder.headline("Wisdom saved")
        builder.addDetail("Added to your collection")

        xpResult.fold(
            onSuccess = { result ->
                builder.addSkillXp(Skill.DISCIPLINE, result.actualAmountAwarded)
            },
            onError = { /* Silent */ }
        )

        val skillsState = gamificationRepository.getPlayerSkillsSync()
        builder.currentSkillsState(skillsState)

        return builder.build()
    }

    // =========================================================================
    // COMMIT MODE (Future Messages) → Courage XP
    // =========================================================================

    /**
     * Complete a future message session.
     * Awards Courage XP with bonuses for longer delivery times.
     */
    suspend fun completeCommitSession(
        messageId: Long,
        content: String,
        deliveryDate: Long
    ): SessionCompletionResult {
        val rewardKey = "future_message_$messageId"
        val builder = SessionCompletionResultBuilder(SessionMode.COMMIT)

        val now = System.currentTimeMillis()
        val daysUntilDelivery = ((deliveryDate - now) / (24 * 60 * 60 * 1000)).toInt()

        val xpAmount = PointCalculator.Courage.calculateFutureMessagePoints(daysUntilDelivery)

        val xpResult = gamificationRepository.awardSkillXp(
            skill = Skill.COURAGE,
            amount = xpAmount,
            rewardKey = rewardKey,
            respectDailyCap = true
        )

        builder.headline("Message scheduled")
        builder.addDetail("Delivery in $daysUntilDelivery days")

        xpResult.fold(
            onSuccess = { result ->
                builder.addSkillXp(Skill.COURAGE, result.actualAmountAwarded)
                if (result.didLevelUp) {
                    builder.addLevelUp(
                        SkillLevelUpEvent(
                            skill = Skill.COURAGE,
                            previousLevel = result.previousLevel,
                            newLevel = result.newLevel,
                            tokenReward = PointCalculator.LevelUpRewards.calculateTokenReward(result.newLevel)
                        )
                    )
                }
                if (result.wasCapped) {
                    builder.addDetail("Daily Courage cap reached")
                }
            },
            onError = { error ->
                Log.e(TAG, "Error awarding future message XP: ${error.message}")
            }
        )

        // Check for seed bloom
        val bloomResult = gamificationRepository.attemptBloom(
            content = content,
            bloomedIn = "future_message",
            entryId = messageId
        )
        bloomResult.fold(
            onSuccess = { result ->
                if (result is BloomAttemptResult.Success) {
                    builder.addSeedBloom(
                        SeedBloomEvent(
                            seedContent = result.seedContent,
                            xpAwarded = result.pointsAwarded,
                            tokensAwarded = result.tokensAwarded,
                            bloomStreak = result.newBloomStreak
                        )
                    )
                }
            },
            onError = { /* Bloom failure is not critical */ }
        )

        recordActivityAndUpdateStreak(builder, ActivityType.FUTURE_MESSAGE)
        checkForRankUp(builder)

        val skillsState = gamificationRepository.getPlayerSkillsSync()
        builder.currentSkillsState(skillsState)

        Log.d(TAG, "Commit session completed: $daysUntilDelivery days, ${builder.build().totalXpAwarded} XP")

        return builder.build()
    }

    /**
     * Record opening a delivered future message.
     */
    suspend fun recordMessageOpened(messageId: Long): SessionCompletionResult {
        val rewardKey = "message_opened_$messageId"
        val builder = SessionCompletionResultBuilder(SessionMode.COMMIT)

        val xpResult = gamificationRepository.awardSkillXp(
            skill = Skill.COURAGE,
            amount = PointCalculator.Courage.OPEN_DELIVERED_MESSAGE,
            rewardKey = rewardKey,
            respectDailyCap = true
        )

        builder.headline("Message from the past")
        builder.addDetail("Your past self reached out")

        xpResult.fold(
            onSuccess = { result ->
                builder.addSkillXp(Skill.COURAGE, result.actualAmountAwarded)
                if (result.didLevelUp) {
                    builder.addLevelUp(
                        SkillLevelUpEvent(
                            skill = Skill.COURAGE,
                            previousLevel = result.previousLevel,
                            newLevel = result.newLevel,
                            tokenReward = PointCalculator.LevelUpRewards.calculateTokenReward(result.newLevel)
                        )
                    )
                }
            },
            onError = { /* Log error */ }
        )

        recordActivityAndUpdateStreak(builder, ActivityType.OPENED_MESSAGE)

        val skillsState = gamificationRepository.getPlayerSkillsSync()
        builder.currentSkillsState(skillsState)

        return builder.build()
    }

    /**
     * Record replying to past self.
     */
    suspend fun recordReplyToPastSelf(
        originalMessageId: Long,
        replyContent: String
    ): SessionCompletionResult {
        val rewardKey = "reply_to_past_$originalMessageId"
        val builder = SessionCompletionResultBuilder(SessionMode.COMMIT)

        val xpResult = gamificationRepository.awardSkillXp(
            skill = Skill.COURAGE,
            amount = PointCalculator.Courage.REPLY_TO_PAST_SELF,
            rewardKey = rewardKey,
            respectDailyCap = true
        )

        builder.headline("Conversation with past you")
        builder.addDetail("A beautiful moment of reflection")

        xpResult.fold(
            onSuccess = { result ->
                builder.addSkillXp(Skill.COURAGE, result.actualAmountAwarded)
                if (result.didLevelUp) {
                    builder.addLevelUp(
                        SkillLevelUpEvent(
                            skill = Skill.COURAGE,
                            previousLevel = result.previousLevel,
                            newLevel = result.newLevel,
                            tokenReward = PointCalculator.LevelUpRewards.calculateTokenReward(result.newLevel)
                        )
                    )
                }
            },
            onError = { /* Log error */ }
        )

        val skillsState = gamificationRepository.getPlayerSkillsSync()
        builder.currentSkillsState(skillsState)

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
     * Get current player skills state.
     */
    suspend fun getPlayerSkills(): PlayerSkillsState = gamificationRepository.getPlayerSkillsSync()

    /**
     * Observe player skills reactively.
     */
    fun observePlayerSkills(): Flow<PlayerSkillsState> = gamificationRepository.observePlayerSkills()

    /**
     * Get today's seed.
     */
    suspend fun getTodaysSeed(): DailySeed? = gamificationRepository.getTodaysSeedSync()

    /**
     * Observe today's seed.
     */
    fun observeTodaysSeed(): Flow<DailySeed?> = gamificationRepository.observeTodaysSeed()

    /**
     * Get current streak state.
     */
    suspend fun getStreakState(): StreakData = gamificationRepository.getStreakStateSync()

    /**
     * Observe streak state.
     */
    fun observeStreakState(): Flow<StreakData> = gamificationRepository.observeStreakState()

    /**
     * Get current rank state.
     */
    suspend fun getCurrentRank(): RankState = gamificationRepository.getCurrentRank()

    /**
     * Observe rank state.
     */
    fun observeRankState(): Flow<RankState> = gamificationRepository.observeRankState()

    /**
     * Get token balance.
     */
    suspend fun getTokenBalance(): Int = gamificationRepository.getTokenBalance()

    /**
     * Observe token balance.
     */
    fun observeTokenBalance(): Flow<Int> = gamificationRepository.observeTokenBalance()

    /**
     * Use a Mindful Break (streak freeze).
     */
    suspend fun useMindfulBreak(): Result<MindfulBreakResult> = gamificationRepository.useMindfulBreak()

    /**
     * Perform daily reset checks (should be called on app launch).
     */
    suspend fun performDailyChecks() {
        gamificationRepository.performDailyResetChecks()
    }

    private suspend fun recordActivityAndUpdateStreak(
        builder: SessionCompletionResultBuilder,
        activityType: ActivityType
    ) {
        val streakResult = gamificationRepository.recordDailyActivity(activityType)
        streakResult.fold(
            onSuccess = { result ->
                when (result) {
                    is StreakUpdateResult.Incremented -> {
                        builder.streakUpdate(
                            StreakUpdateEvent(
                                newStreak = result.newStreak,
                                previousStreak = result.previousStreak,
                                isNewLongest = result.isNewLongest,
                                milestoneReached = result.milestoneReached
                            )
                        )
                        // Award tokens for milestone
                        result.milestoneReached?.let { milestone ->
                            PointCalculator.Tokens.getStreakMilestoneTokens(milestone)?.let { tokens ->
                                gamificationRepository.awardTokens(
                                    amount = tokens,
                                    rewardKey = "streak_milestone_${result.newStreak}"
                                )
                                builder.addTokens(tokens)
                            }
                        }
                    }
                    is StreakUpdateResult.Maintained -> {
                        builder.streakUpdate(
                            StreakUpdateEvent(
                                newStreak = result.streak,
                                previousStreak = result.streak,
                                isNewLongest = false,
                                milestoneReached = null
                            )
                        )
                    }
                    is StreakUpdateResult.Broken -> {
                        builder.addDetail(
                            if (result.canUseFreeze) "Streak broken - Use a Mindful Break to preserve it!"
                            else "Streak reset - A fresh start awaits"
                        )
                    }
                    else -> { /* PreservedWithFreeze or Error */ }
                }
            },
            onError = { /* Log error, streak update is not critical */ }
        )
    }

    private var previousRankState: RankState? = null

    private suspend fun checkForRankUp(builder: SessionCompletionResultBuilder) {
        val previousRank = previousRankState ?: gamificationRepository.getCurrentRank()
        val currentRank = gamificationRepository.getCurrentRank()

        if (currentRank.currentRank != previousRank.currentRank) {
            builder.rankUp(
                RankUpEvent(
                    previousRank = previousRank.currentRank,
                    newRank = currentRank.currentRank,
                    message = RankMessages.advancementMessage(currentRank.currentRank)
                )
            )
        }

        previousRankState = currentRank
    }
}

// =========================================================================
// SESSION COMPLETION DATA CLASSES
// =========================================================================

/**
 * Session modes corresponding to the three skills.
 */
enum class SessionMode(val displayName: String) {
    REFLECT("Reflect"),
    SHARPEN("Sharpen"),
    COMMIT("Commit")
}

/**
 * Result of completing a game session.
 */
data class SessionCompletionResult(
    val mode: SessionMode,
    val headline: String,
    val details: List<String>,
    val skillXpAwarded: Map<Skill, Int>,
    val tokensAwarded: Int,
    val levelUps: List<SkillLevelUpEvent>,
    val seedBloom: SeedBloomEvent?,
    val streakUpdate: StreakUpdateEvent?,
    val rankUp: RankUpEvent?,
    val currentSkillsState: PlayerSkillsState?
) {
    val totalXpAwarded: Int
        get() = skillXpAwarded.values.sum()

    val hasLevelUp: Boolean
        get() = levelUps.isNotEmpty()

    val hasSeedBloom: Boolean
        get() = seedBloom != null

    val hasRankUp: Boolean
        get() = rankUp != null
}

/**
 * Skill level up event.
 */
data class SkillLevelUpEvent(
    val skill: Skill,
    val previousLevel: Int,
    val newLevel: Int,
    val tokenReward: Int
)

/**
 * Seed bloom event.
 */
data class SeedBloomEvent(
    val seedContent: String,
    val xpAwarded: Int,
    val tokensAwarded: Int,
    val bloomStreak: Int
)

/**
 * Streak update event.
 */
data class StreakUpdateEvent(
    val newStreak: Int,
    val previousStreak: Int,
    val isNewLongest: Boolean,
    val milestoneReached: Int?
)

/**
 * Rank up event.
 */
data class RankUpEvent(
    val previousRank: Rank,
    val newRank: Rank,
    val message: String
)

/**
 * Builder for SessionCompletionResult.
 */
private class SessionCompletionResultBuilder(private val mode: SessionMode) {
    private var headline: String = ""
    private val details = mutableListOf<String>()
    private val skillXpAwarded = mutableMapOf<Skill, Int>()
    private var tokensAwarded: Int = 0
    private val levelUps = mutableListOf<SkillLevelUpEvent>()
    private var seedBloom: SeedBloomEvent? = null
    private var streakUpdate: StreakUpdateEvent? = null
    private var rankUp: RankUpEvent? = null
    private var currentSkillsState: PlayerSkillsState? = null

    fun headline(text: String) {
        headline = text
    }

    fun addDetail(text: String) {
        details.add(text)
    }

    fun addSkillXp(skill: Skill, amount: Int) {
        skillXpAwarded[skill] = (skillXpAwarded[skill] ?: 0) + amount
    }

    fun addTokens(amount: Int) {
        tokensAwarded += amount
    }

    fun addLevelUp(event: SkillLevelUpEvent) {
        levelUps.add(event)
        tokensAwarded += event.tokenReward
    }

    fun addSeedBloom(event: SeedBloomEvent) {
        seedBloom = event
        tokensAwarded += event.tokensAwarded
    }

    fun streakUpdate(event: StreakUpdateEvent) {
        streakUpdate = event
    }

    fun rankUp(event: RankUpEvent) {
        rankUp = event
    }

    fun currentSkillsState(state: PlayerSkillsState) {
        currentSkillsState = state
    }

    fun build(): SessionCompletionResult = SessionCompletionResult(
        mode = mode,
        headline = headline,
        details = details.toList(),
        skillXpAwarded = skillXpAwarded.toMap(),
        tokensAwarded = tokensAwarded,
        levelUps = levelUps.toList(),
        seedBloom = seedBloom,
        streakUpdate = streakUpdate,
        rankUp = rankUp,
        currentSkillsState = currentSkillsState
    )
}
