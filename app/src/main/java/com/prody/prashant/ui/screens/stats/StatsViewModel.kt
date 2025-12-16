package com.prody.prashant.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class StatsUiState(
    val totalPoints: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val currentRank: Int = 0,
    val wordsLearned: Int = 0,
    val journalEntries: Int = 0,
    val futureMessages: Int = 0,
    val daysActive: Int = 0,
    val weeklyLeaderboard: List<LeaderboardEntryEntity> = emptyList(),
    val allTimeLeaderboard: List<LeaderboardEntryEntity> = emptyList(),
    val weeklyProgress: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0),
    val moodDistribution: Map<String, Int> = emptyMap(),
    val weeklyGrowthPercent: Int = 0,
    val consistencyScore: Int = 0,
    val learningPace: String = "Steady",
    val isLoading: Boolean = true,
    val totalWordsWritten: Int = 0
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val userDao: UserDao,
    private val journalDao: JournalDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "StatsViewModel"
    }

    init {
        loadStats()
        loadLeaderboard()
    }

    private fun loadStats() {
        viewModelScope.launch {
            try {
                userDao.getUserProfile().collect { profile ->
                    profile?.let {
                        _uiState.update { state ->
                            state.copy(
                                totalPoints = it.totalPoints,
                                currentStreak = it.currentStreak,
                                longestStreak = it.longestStreak,
                                wordsLearned = it.wordsLearned,
                                journalEntries = it.journalEntriesCount,
                                futureMessages = it.futureMessagesCount,
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading user profile", e)
                _uiState.update { state -> state.copy(isLoading = false) }
            }
        }

        viewModelScope.launch {
            try {
                userDao.getStreakHistory().collect { history ->
                    val daysActive = history.size
                    val weeklyProgress = calculateWeeklyProgress(history.map { it.pointsEarned })
                    val consistencyScore = calculateConsistencyScore(history.size)
                    val weeklyGrowth = calculateWeeklyGrowth(weeklyProgress)
                    val learningPace = determineLearningPace(weeklyProgress)

                    _uiState.update { state ->
                        state.copy(
                            daysActive = daysActive,
                            weeklyProgress = weeklyProgress,
                            consistencyScore = consistencyScore,
                            weeklyGrowthPercent = weeklyGrowth,
                            learningPace = learningPace
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading streak history", e)
            }
        }

        // Load real mood distribution from journal entries
        viewModelScope.launch {
            try {
                journalDao.getMoodDistribution().collect { moodCounts ->
                    val moodMap = moodCounts
                        .filter { it.mood.isNotBlank() }
                        .associate { it.mood.lowercase() to it.count }

                    _uiState.update { state ->
                        state.copy(moodDistribution = moodMap)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading mood distribution", e)
            }
        }

        // Load total words written from journals
        viewModelScope.launch {
            try {
                journalDao.getTotalWordCount().collect { wordCount ->
                    _uiState.update { state ->
                        state.copy(totalWordsWritten = wordCount ?: 0)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading total word count", e)
            }
        }
    }

    private fun calculateConsistencyScore(daysActive: Int): Int {
        // Calculate consistency based on streak relative to days active
        val currentStreak = _uiState.value.currentStreak
        return if (daysActive > 0) {
            ((currentStreak.toFloat() / daysActive.coerceAtLeast(1)) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }

    private fun calculateWeeklyGrowth(weeklyProgress: List<Int>): Int {
        val currentWeek = weeklyProgress.takeLast(7).sum()
        val previousSum = weeklyProgress.take(7).sum().coerceAtLeast(1)
        return ((currentWeek - previousSum).toFloat() / previousSum * 100).toInt()
    }

    private fun determineLearningPace(weeklyProgress: List<Int>): String {
        val avg = weeklyProgress.average()
        return when {
            avg >= 5 -> "Fast"
            avg >= 2 -> "Steady"
            avg >= 1 -> "Building"
            else -> "Starting"
        }
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            try {
                // Initialize with sample data for demo
                // In a real app, this would come from a backend
                val sampleLeaderboard = createSampleLeaderboard()
                userDao.insertLeaderboardEntries(sampleLeaderboard)

                userDao.getLeaderboard().collect { leaderboard ->
                    _uiState.update { state ->
                        val currentUserRank = leaderboard.indexOfFirst { it.isCurrentUser } + 1
                        state.copy(
                            allTimeLeaderboard = leaderboard,
                            currentRank = if (currentUserRank > 0) currentUserRank else 0
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading all-time leaderboard", e)
            }
        }

        viewModelScope.launch {
            try {
                userDao.getWeeklyLeaderboard().collect { leaderboard ->
                    _uiState.update { state ->
                        state.copy(weeklyLeaderboard = leaderboard)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading weekly leaderboard", e)
            }
        }
    }

    /**
     * Creates realistic demo leaderboard data with human-like names.
     * These represent typical users of a self-improvement app.
     *
     * In production, this would be replaced with actual backend data.
     * The demo data serves to:
     * 1. Show users how the leaderboard works
     * 2. Provide social motivation even without a backend
     * 3. Create a sense of community
     */
    private fun createSampleLeaderboard(): List<LeaderboardEntryEntity> {
        // Realistic, diverse names that feel authentic
        // Prashant Chataut is #1 - the app creator and top performer
        val leaderboardUsers = listOf(
            LeaderboardUser("Prashant Chataut", avatarId = "avatar_crown", titleId = "master", points = 99999, weekly = 500, streak = 365, isCurrentUser = true),
            LeaderboardUser("Sarah M.", avatarId = "avatar_1", titleId = "sage", points = 8500, weekly = 189, streak = 34),
            LeaderboardUser("Alex K.", avatarId = "avatar_2", titleId = "philosopher", points = 7200, weekly = 167, streak = 28),
            LeaderboardUser("Jordan T.", avatarId = "avatar_3", titleId = "practitioner", points = 6100, weekly = 142, streak = 21),
            LeaderboardUser("Emma R.", avatarId = "avatar_4", titleId = "contemplative", points = 5400, weekly = 128, streak = 19),
            LeaderboardUser("Chris P.", avatarId = "avatar_5", titleId = "initiate", points = 4800, weekly = 115, streak = 14),
            LeaderboardUser("Maya S.", avatarId = "avatar_6", titleId = "seeker", points = 4200, weekly = 98, streak = 12),
            LeaderboardUser("Daniel L.", avatarId = "avatar_7", titleId = "seeker", points = 3600, weekly = 87, streak = 9),
            LeaderboardUser("Priya N.", avatarId = "avatar_8", titleId = "seeker", points = 3100, weekly = 76, streak = 7),
            LeaderboardUser("Ryan H.", avatarId = "avatar_9", titleId = "seeker", points = 2500, weekly = 64, streak = 5)
        )

        return leaderboardUsers.mapIndexed { index, user ->
            LeaderboardEntryEntity(
                odId = if (user.isCurrentUser) "current_user" else UUID.randomUUID().toString(),
                displayName = user.name,
                avatarId = user.avatarId,
                titleId = user.titleId,
                totalPoints = user.points,
                weeklyPoints = user.weekly,
                currentStreak = user.streak,
                rank = index + 1,
                previousRank = if (user.isCurrentUser) index + 2 else index + 1 + (-1..1).random(),
                isCurrentUser = user.isCurrentUser,
                lastActiveAt = System.currentTimeMillis() - (0L..86400000L).random(), // Random time in last 24h
                boostsReceived = if (user.isCurrentUser) 0 else (0..15).random(),
                congratsReceived = if (user.isCurrentUser) 0 else (0..10).random()
            )
        }
    }

    /**
     * Helper data class for creating leaderboard entries
     */
    private data class LeaderboardUser(
        val name: String,
        val avatarId: String = "avatar_default",
        val titleId: String = "seeker",
        val points: Int,
        val weekly: Int,
        val streak: Int,
        val isCurrentUser: Boolean = false
    )

    private fun calculateWeeklyProgress(data: List<Int>): List<Int> {
        return if (data.size >= 7) {
            data.takeLast(7)
        } else {
            List(7 - data.size) { 0 } + data
        }
    }

    /**
     * Refresh all stats and leaderboard data
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loadStats()
            loadLeaderboard()
        }
    }
}
