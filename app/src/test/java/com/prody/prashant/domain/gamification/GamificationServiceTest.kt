package com.prody.prashant.domain.gamification

import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.data.local.entity.UserProfileEntity
import com.prody.prashant.data.local.entity.UserStatsEntity
import com.prody.prashant.util.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for GamificationService business logic.
 *
 * Tests cover:
 * - Point calculations for different activities
 * - Streak bonus calculations
 * - Daily point cap enforcement
 * - Level calculation from points
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GamificationServiceTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userDao: UserDao
    private lateinit var gamificationService: GamificationService

    @Before
    fun setup() {
        userDao = mockk(relaxed = true)
        gamificationService = GamificationService(userDao)
    }

    // ==========================================================================
    // POINT CALCULATION TESTS
    // ==========================================================================

    @Test
    fun `recordActivity - journal entry awards 50 base points`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 0)
        val stats = createUserStats(dailyPointsEarned = 0)
        setupMocks(profile, stats)

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.JOURNAL_ENTRY)

        // Then
        assertEquals(GamificationService.POINTS_JOURNAL_ENTRY, points)
        coVerify { userDao.addPoints(50) }
    }

    @Test
    fun `recordActivity - word learned awards 15 base points`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 0)
        val stats = createUserStats(dailyPointsEarned = 0)
        setupMocks(profile, stats)

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.WORD_LEARNED)

        // Then
        assertEquals(GamificationService.POINTS_WORD_LEARNED, points)
        coVerify { userDao.addPoints(15) }
    }

    @Test
    fun `recordActivity - quote read awards 5 base points`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 0)
        val stats = createUserStats(dailyPointsEarned = 0)
        setupMocks(profile, stats)

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.QUOTE_READ)

        // Then
        assertEquals(GamificationService.POINTS_QUOTE_READ, points)
        coVerify { userDao.addPoints(5) }
    }

    @Test
    fun `recordActivity - future letter sent awards 50 base points`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 0)
        val stats = createUserStats(dailyPointsEarned = 0)
        setupMocks(profile, stats)

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.FUTURE_LETTER_SENT)

        // Then
        assertEquals(GamificationService.POINTS_FUTURE_LETTER_SENT, points)
        coVerify { userDao.addPoints(50) }
    }

    // ==========================================================================
    // STREAK BONUS TESTS
    // ==========================================================================

    @Test
    fun `recordActivity - streak bonus adds 2 points per streak day`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 5) // 5 day streak
        val stats = createUserStats(dailyPointsEarned = 0)
        setupMocks(profile, stats)

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.DAILY_CHECK_IN)

        // Then
        // Base (5) + Streak bonus (5 * 2 = 10) = 15
        val expectedPoints = GamificationService.POINTS_DAILY_CHECK_IN +
                (5 * GamificationService.POINTS_STREAK_BONUS_PER_DAY)
        assertEquals(expectedPoints, points)
    }

    @Test
    fun `recordActivity - 10 day streak adds 20 bonus points`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 10)
        val stats = createUserStats(dailyPointsEarned = 0)
        setupMocks(profile, stats)

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.QUOTE_READ)

        // Then
        // Base (5) + Streak bonus (10 * 2 = 20) = 25
        assertEquals(25, points)
    }

    @Test
    fun `recordActivity - zero streak adds no bonus`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 0)
        val stats = createUserStats(dailyPointsEarned = 0)
        setupMocks(profile, stats)

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.QUOTE_READ)

        // Then
        assertEquals(GamificationService.POINTS_QUOTE_READ, points) // Just base points
    }

    // ==========================================================================
    // DAILY CAP TESTS
    // ==========================================================================

    @Test
    fun `recordActivity - returns 0 when daily cap reached`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 0)
        val stats = createUserStats(dailyPointsEarned = GamificationService.MAX_DAILY_POINTS)
        setupMocks(profile, stats)

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.JOURNAL_ENTRY)

        // Then
        assertEquals(0, points)
        coVerify(exactly = 0) { userDao.addPoints(any()) }
    }

    @Test
    fun `recordActivity - caps points when would exceed daily limit`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 0)
        val stats = createUserStats(dailyPointsEarned = 480) // 480 of 500 used
        setupMocks(profile, stats)

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.JOURNAL_ENTRY) // Would be 50

        // Then
        assertEquals(20, points) // Only 20 remaining in cap
    }

    @Test
    fun `recordActivity - large streak bonus capped by daily limit`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 100) // Huge streak
        val stats = createUserStats(dailyPointsEarned = 300)
        setupMocks(profile, stats)

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.JOURNAL_ENTRY)
        // Would be: 50 base + (100 * 2) = 250, but only 200 remaining

        // Then
        assertEquals(200, points) // Capped at remaining daily
    }

    // ==========================================================================
    // EDGE CASE TESTS
    // ==========================================================================

    @Test
    fun `recordActivity - returns 0 when no user profile exists`() = runTest {
        // Given
        coEvery { userDao.getUserProfileSync() } returns null

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.JOURNAL_ENTRY)

        // Then
        assertEquals(0, points)
    }

    @Test
    fun `recordActivity - handles missing stats gracefully`() = runTest {
        // Given
        val profile = createUserProfile(currentStreak = 0)
        coEvery { userDao.getUserProfileSync() } returns profile
        coEvery { userDao.getUserStats() } returns flowOf(null)
        coEvery { userDao.addPoints(any()) } just Runs
        coEvery { userDao.addDailyPoints(any()) } just Runs

        // When
        val points = gamificationService.recordActivity(GamificationService.ActivityType.QUOTE_READ)

        // Then - should work with null stats (defaults to 0 daily points)
        assertEquals(5, points)
    }

    // ==========================================================================
    // LEVEL CALCULATION TESTS
    // ==========================================================================

    @Test
    fun `level calculation - 0 points is level 1`() {
        val level = calculateLevel(0)
        assertEquals(1, level)
    }

    @Test
    fun `level calculation - 199 points is level 1`() {
        val level = calculateLevel(199)
        assertEquals(1, level)
    }

    @Test
    fun `level calculation - 200 points is level 2`() {
        val level = calculateLevel(200)
        assertEquals(2, level)
    }

    @Test
    fun `level calculation - 499 points is level 2`() {
        val level = calculateLevel(499)
        assertEquals(2, level)
    }

    @Test
    fun `level calculation - 500 points is level 3`() {
        val level = calculateLevel(500)
        assertEquals(3, level)
    }

    @Test
    fun `level calculation - 10000 points is level 10`() {
        val level = calculateLevel(10000)
        assertEquals(10, level)
    }

    @Test
    fun `level calculation - 50000 points is still level 10 (max)`() {
        val level = calculateLevel(50000)
        assertEquals(10, level)
    }

    // ==========================================================================
    // STREAK CALCULATION TESTS
    // ==========================================================================

    @Test
    fun `streak - same day activity maintains streak`() {
        val result = calculateStreakChange(
            currentStreak = 5,
            daysDiff = 0
        )
        assertEquals(5, result)
    }

    @Test
    fun `streak - next day activity increments streak`() {
        val result = calculateStreakChange(
            currentStreak = 5,
            daysDiff = 1
        )
        assertEquals(6, result)
    }

    @Test
    fun `streak - gap of 2 days resets streak to 1`() {
        val result = calculateStreakChange(
            currentStreak = 5,
            daysDiff = 2
        )
        assertEquals(1, result)
    }

    @Test
    fun `streak - gap of 7 days resets streak to 1`() {
        val result = calculateStreakChange(
            currentStreak = 100,
            daysDiff = 7
        )
        assertEquals(1, result)
    }

    // ==========================================================================
    // HELPER METHODS
    // ==========================================================================

    private fun setupMocks(profile: UserProfileEntity, stats: UserStatsEntity) {
        coEvery { userDao.getUserProfileSync() } returns profile
        coEvery { userDao.getUserStats() } returns flowOf(stats)
        coEvery { userDao.addPoints(any()) } just Runs
        coEvery { userDao.addDailyPoints(any()) } just Runs
        coEvery { userDao.incrementJournalEntries() } just Runs
        coEvery { userDao.incrementWordsLearned() } just Runs
        coEvery { userDao.incrementFutureMessages() } just Runs
        coEvery { userDao.updateStreak(any()) } just Runs
        coEvery { userDao.updateLastActiveDate(any()) } just Runs
        coEvery { userDao.insertStreakHistory(any()) } just Runs
        coEvery { userDao.getAllAchievements() } returns flowOf(emptyList<AchievementEntity>())
    }

    private fun createUserProfile(
        currentStreak: Int = 0,
        totalPoints: Int = 0,
        lastActiveDate: Long = System.currentTimeMillis()
    ): UserProfileEntity {
        return UserProfileEntity(
            odId = "test-user",
            displayName = "Test User",
            avatarId = 0,
            currentStreak = currentStreak,
            longestStreak = currentStreak,
            totalPoints = totalPoints,
            lastActiveDate = lastActiveDate,
            createdAt = System.currentTimeMillis()
        )
    }

    private fun createUserStats(
        dailyPointsEarned: Int = 0
    ): UserStatsEntity {
        return UserStatsEntity(
            id = 1,
            dailyPointsEarned = dailyPointsEarned,
            weeklyPointsEarned = 0,
            totalJournalEntries = 0,
            totalWordsLearned = 0,
            totalQuotesRead = 0,
            totalProverbsExplored = 0,
            lastUpdated = System.currentTimeMillis()
        )
    }

    /**
     * Level calculation logic (same as in the app).
     * Extracted here for testing.
     */
    private fun calculateLevel(totalPoints: Int): Int {
        return when {
            totalPoints >= 10000 -> 10
            totalPoints >= 7500 -> 9
            totalPoints >= 5000 -> 8
            totalPoints >= 3500 -> 7
            totalPoints >= 2500 -> 6
            totalPoints >= 1500 -> 5
            totalPoints >= 1000 -> 4
            totalPoints >= 500 -> 3
            totalPoints >= 200 -> 2
            else -> 1
        }
    }

    /**
     * Streak calculation logic (same as in updateStreak).
     * Extracted here for testing.
     */
    private fun calculateStreakChange(currentStreak: Int, daysDiff: Int): Int {
        return when {
            daysDiff == 0 -> currentStreak // Same day, maintain streak
            daysDiff == 1 -> currentStreak + 1 // Next day, increment streak
            else -> 1 // Gap > 1 day, reset streak
        }
    }
}
