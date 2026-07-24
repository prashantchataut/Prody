package com.kairos.app.domain.gamification

import com.kairos.app.data.local.dao.ChallengeDao
import com.kairos.app.data.local.dao.UserDao
import com.kairos.app.data.local.entity.AchievementEntity
import com.kairos.app.data.local.entity.UserProfileEntity
import com.kairos.app.data.local.entity.UserStatsEntity
import com.kairos.app.data.local.preferences.PreferencesManager
import com.kairos.app.util.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GamificationServiceTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userDao: UserDao
    private lateinit var challengeDao: ChallengeDao
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var service: GamificationService

    @Before
    fun setup() {
        userDao = mockk(relaxed = true)
        challengeDao = mockk(relaxed = true)
        preferencesManager = mockk(relaxed = true)
        every { preferencesManager.gamificationInitialized } returns flowOf(true)
        coEvery { challengeDao.getJoinedChallengesByTypeSync(any()) } returns emptyList()
        service = GamificationService(userDao, challengeDao, preferencesManager)
    }

    @Test
    fun `meaningful actions award bounded points`() = runTest {
        setupMocks(dailyPoints = 0, streak = 42)

        val points = service.recordActivity(GamificationService.ActivityType.JOURNAL_ENTRY)

        assertEquals(GamificationService.POINTS_JOURNAL_ENTRY, points)
        coVerify { userDao.addPoints(GamificationService.POINTS_JOURNAL_ENTRY) }
    }

    @Test
    fun `streak does not multiply every action`() = runTest {
        setupMocks(dailyPoints = 0, streak = 365)

        val points = service.recordActivity(GamificationService.ActivityType.WORD_LEARNED)

        assertEquals(GamificationService.POINTS_WORD_LEARNED, points)
    }

    @Test
    fun `passive content views do not award points or advance streak`() = runTest {
        setupMocks(dailyPoints = 0, streak = 12)

        val quotePoints = service.recordActivity(GamificationService.ActivityType.QUOTE_READ)
        val checkInPoints = service.recordActivity(GamificationService.ActivityType.DAILY_CHECK_IN)
        val aiPoints = service.recordActivity(GamificationService.ActivityType.BUDDHA_CONVERSATION)

        assertEquals(0, quotePoints)
        assertEquals(0, checkInPoints)
        assertEquals(0, aiPoints)
        coVerify(exactly = 0) { userDao.addPoints(any()) }
        coVerify(exactly = 0) { userDao.updateStreak(any()) }
    }

    @Test
    fun `daily cap truncates final meaningful reward`() = runTest {
        setupMocks(dailyPoints = GamificationService.MAX_DAILY_POINTS - 7)

        val points = service.recordActivity(GamificationService.ActivityType.JOURNAL_ENTRY)

        assertEquals(7, points)
        coVerify { userDao.addPoints(7) }
    }

    @Test
    fun `daily cap blocks currency but preserves legitimate progress`() = runTest {
        setupMocks(dailyPoints = GamificationService.MAX_DAILY_POINTS)

        val points = service.recordActivity(
            GamificationService.ActivityType.JOURNAL_ENTRY,
            updateLegacyStreak = false
        )

        assertEquals(0, points)
        coVerify(exactly = 0) { userDao.addPoints(any()) }
        coVerify(exactly = 1) { userDao.incrementJournalEntries() }
        coVerify(exactly = 0) { userDao.updateStreak(any()) }
    }

    @Test
    fun `missing profile safely returns zero`() = runTest {
        coEvery { userDao.getUserProfileSync() } returns null
        assertEquals(0, service.recordActivity(GamificationService.ActivityType.JOURNAL_ENTRY))
    }

    private fun setupMocks(dailyPoints: Int, streak: Int = 0) {
        val profile = UserProfileEntity(
            id = 1,
            displayName = "Test User",
            currentStreak = streak,
            longestStreak = streak
        )
        val stats = UserStatsEntity(id = 1, dailyPointsEarned = dailyPoints)
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
}
