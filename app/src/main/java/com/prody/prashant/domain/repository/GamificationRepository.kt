package com.prody.prashant.domain.repository

import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.gamification.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for gamification operations.
 *
 * Provides abstraction layer for all gamification features:
 * - Skill progression (Clarity, Discipline, Courage)
 * - Streak tracking with Mindful Breaks
 * - Achievement progress and unlocks
 * - Seed -> Bloom mechanic
 * - Leaderboard operations
 * - Token currency
 *
 * All write operations are idempotent via ProcessedRewardEntity.
 */
interface GamificationRepository {

    // =========================================================================
    // Skill Progression
    // =========================================================================

    /**
     * Observe current player skills state.
     */
    fun observePlayerSkills(): Flow<PlayerSkillsState>

    /**
     * Get current player skills synchronously.
     */
    suspend fun getPlayerSkillsSync(): PlayerSkillsState

    /**
     * Award XP for a skill with idempotency.
     *
     * @param skill The skill to award XP for
     * @param amount Amount of XP to award
     * @param rewardKey Unique key for idempotency (prevents double-awarding)
     * @param respectDailyCap If true, respect daily XP caps
     * @return Result containing the actual XP awarded (may be less if capped)
     */
    suspend fun awardSkillXp(
        skill: Skill,
        amount: Int,
        rewardKey: String,
        respectDailyCap: Boolean = true
    ): Result<SkillXpAwardResult>

    /**
     * Get XP earned today for a specific skill.
     */
    suspend fun getDailyXpForSkill(skill: Skill): Int

    /**
     * Get remaining daily XP capacity for a skill.
     */
    suspend fun getRemainingDailyCapacity(skill: Skill): Int

    /**
     * Get weekly XP for leaderboard calculations.
     */
    suspend fun getWeeklyXp(): WeeklyXpProgress

    /**
     * Reset daily XP tracking (called at midnight).
     */
    suspend fun resetDailyXp()

    /**
     * Reset weekly XP tracking (called on Monday).
     */
    suspend fun resetWeeklyXp()

    // =========================================================================
    // Streak System
    // =========================================================================

    /**
     * Observe current streak state.
     */
    fun observeStreakState(): Flow<StreakData>

    /**
     * Get current streak state synchronously.
     */
    suspend fun getStreakStateSync(): StreakData

    /**
     * Record activity for today (updates streak).
     *
     * @param activityType Type of activity performed
     * @return Result of the streak update (maintained, incremented, broken, etc.)
     */
    suspend fun recordDailyActivity(activityType: ActivityType): Result<StreakUpdateResult>

    /**
     * Use a Mindful Break (streak freeze) to preserve streak.
     *
     * @return Result of the freeze attempt
     */
    suspend fun useMindfulBreak(): Result<MindfulBreakResult>

    /**
     * Get detailed activity history for a date range.
     */
    suspend fun getActivityHistory(startDate: LocalDate, endDate: LocalDate): List<DailyActivity>

    /**
     * Reset monthly freezes (called on 1st of month).
     */
    suspend fun resetMonthlyFreezes()

    // =========================================================================
    // Achievements
    // =========================================================================

    /**
     * Observe all achievements with progress.
     */
    fun observeAllAchievements(): Flow<List<AchievementProgress>>

    /**
     * Observe unlocked achievements.
     */
    fun observeUnlockedAchievements(): Flow<List<AchievementProgress>>

    /**
     * Get unlocked achievement IDs for cosmetic unlock checks.
     */
    suspend fun getUnlockedAchievementIds(): Set<String>

    /**
     * Update progress for an achievement.
     *
     * @param achievementId Achievement ID
     * @param progress New progress value
     * @return True if achievement was just unlocked
     */
    suspend fun updateAchievementProgress(achievementId: String, progress: Int): Result<Boolean>

    /**
     * Check and update achievements based on current stats.
     *
     * @return List of newly unlocked achievements
     */
    suspend fun checkAndUpdateAchievements(): List<Achievement>

    // =========================================================================
    // Seed -> Bloom
    // =========================================================================

    /**
     * Observe today's seed.
     */
    fun observeTodaysSeed(): Flow<DailySeed?>

    /**
     * Get today's seed synchronously.
     */
    suspend fun getTodaysSeedSync(): DailySeed?

    /**
     * Create or get today's seed.
     *
     * @return Today's seed (created if none exists)
     */
    suspend fun getOrCreateTodaysSeed(): Result<DailySeed>

    /**
     * Update seed state (planted -> growing -> bloomed).
     *
     * @param seedId Seed ID
     * @param newState New state
     */
    suspend fun updateSeedState(seedId: Long, newState: SeedState): Result<Unit>

    /**
     * Attempt to bloom today's seed by checking content match.
     *
     * @param content Content to check for seed match
     * @param bloomedIn Where the seed was used ("journal", "future_message")
     * @param entryId Reference to the entry where it bloomed
     * @return Result of the bloom attempt
     */
    suspend fun attemptBloom(
        content: String,
        bloomedIn: String,
        entryId: Long
    ): Result<BloomAttemptResult>

    /**
     * Get bloom summary statistics.
     */
    suspend fun getBloomSummary(): BloomSummary

    // =========================================================================
    // Rank System
    // =========================================================================

    /**
     * Observe current rank state.
     */
    fun observeRankState(): Flow<RankState>

    /**
     * Get current rank synchronously.
     */
    suspend fun getCurrentRank(): RankState

    /**
     * Check for rank up after skill gains.
     *
     * @return RankUpdateResult indicating if rank changed
     */
    suspend fun checkForRankUp(previousState: RankState): RankUpdateResult

    // =========================================================================
    // Leaderboard
    // =========================================================================

    /**
     * Observe weekly leaderboard.
     */
    fun observeWeeklyLeaderboard(): Flow<LeaderboardState>

    /**
     * Get current user's leaderboard entry.
     */
    suspend fun getCurrentUserLeaderboardEntry(): LeaderboardEntry?

    /**
     * Update current user's leaderboard score.
     */
    suspend fun updateLeaderboardScore(): Result<Unit>

    /**
     * Get weekly summary for the just-ended week.
     */
    suspend fun getWeeklySummary(weekStartDate: LocalDate): WeeklySummary?

    // =========================================================================
    // Tokens
    // =========================================================================

    /**
     * Observe token balance.
     */
    fun observeTokenBalance(): Flow<Int>

    /**
     * Get current token balance.
     */
    suspend fun getTokenBalance(): Int

    /**
     * Award tokens with idempotency.
     *
     * @param amount Tokens to award
     * @param rewardKey Unique key for idempotency
     * @return Result containing new balance
     */
    suspend fun awardTokens(amount: Int, rewardKey: String): Result<Int>

    /**
     * Spend tokens (for cosmetic purchases).
     *
     * @param amount Tokens to spend
     * @param reason Reason for spending (for audit)
     * @return Result indicating success/failure
     */
    suspend fun spendTokens(amount: Int, reason: String): Result<Int>

    // =========================================================================
    // Utility
    // =========================================================================

    /**
     * Check if a reward key has been processed (for idempotency).
     */
    suspend fun hasProcessedReward(rewardKey: String): Boolean

    /**
     * Mark a reward key as processed.
     */
    suspend fun markRewardProcessed(rewardKey: String)

    /**
     * Initialize gamification data for new user.
     */
    suspend fun initializeForNewUser()

    /**
     * Perform daily reset checks (call this on app startup).
     * Handles daily XP reset, weekly reset, monthly freeze reset.
     */
    suspend fun performDailyResetChecks()
}

/**
 * Result of awarding skill XP.
 */
data class SkillXpAwardResult(
    val skill: Skill,
    val requestedAmount: Int,
    val actualAmountAwarded: Int,
    val newTotalXp: Int,
    val newLevel: Int,
    val previousLevel: Int,
    val didLevelUp: Boolean,
    val remainingDailyCap: Int,
    val wasCapped: Boolean
)

/**
 * Types of daily activities that contribute to streaks.
 */
enum class ActivityType {
    /** Full journal entry */
    JOURNAL,
    /** Full journal entry (alias) */
    JOURNAL_ENTRY,
    /** Quick micro-entry */
    MICRO_ENTRY,
    /** Scheduled future message */
    FUTURE_MESSAGE,
    /** Flashcard review session */
    FLASHCARD_SESSION,
    /** Wisdom engagement (word, quote, proverb view) */
    WISDOM_ENGAGEMENT,
    /** Seed bloom */
    BLOOM,
    /** Opened a delivered future message */
    OPENED_MESSAGE
}

/**
 * Achievement with current progress.
 */
data class AchievementProgress(
    val achievement: Achievement,
    val currentProgress: Int,
    val isUnlocked: Boolean,
    val unlockedAt: Long?,
    val progressPercent: Float
)
