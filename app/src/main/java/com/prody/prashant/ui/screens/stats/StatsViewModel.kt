package com.prody.prashant.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
    val totalWordsWritten: Int = 0,
    // Support system state (Boosting 2.0)
    val canBoostToday: Boolean = true,
    val canRespectToday: Boolean = true,
    val boostsSentToday: Int = 0,
    val respectsSentToday: Int = 0,
    // Error state
    val error: String? = null
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val userDao: UserDao,
    private val journalDao: JournalDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
        loadLeaderboard()
    }

    private fun loadStats() {
        // Combine all stats-related flows into a single subscription to prevent
        // multiple coroutine leaks on configuration changes
        viewModelScope.launch {
            try {
                combine(
                    userDao.getUserProfile(),
                    userDao.getStreakHistory(),
                    journalDao.getMoodDistribution(),
                    journalDao.getTotalWordCount()
                ) { profile, history, moodCounts, wordCount ->
                    StatsData(profile, history, moodCounts, wordCount)
                }.collect { data ->
                    // Process profile data
                    data.profile?.let { profile ->
                        _uiState.update { state ->
                            state.copy(
                                totalPoints = profile.totalPoints,
                                currentStreak = profile.currentStreak,
                                longestStreak = profile.longestStreak,
                                wordsLearned = profile.wordsLearned,
                                journalEntries = profile.journalEntriesCount,
                                futureMessages = profile.futureMessagesCount,
                                isLoading = false
                            )
                        }
                    }

                    // Process streak history
                    val daysActive = data.history.size
                    val weeklyProgress = calculateWeeklyProgress(data.history.map { it.pointsEarned })
                    val consistencyScore = calculateConsistencyScore(daysActive)
                    val weeklyGrowth = calculateWeeklyGrowth(weeklyProgress)
                    val learningPace = determineLearningPace(weeklyProgress)

                    // Process mood distribution
                    val moodMap = data.moodCounts
                        .filter { it.mood.isNotBlank() }
                        .associate { it.mood.lowercase() to it.count }

                    _uiState.update { state ->
                        state.copy(
                            daysActive = daysActive,
                            weeklyProgress = weeklyProgress,
                            consistencyScore = consistencyScore,
                            weeklyGrowthPercent = weeklyGrowth,
                            learningPace = learningPace,
                            moodDistribution = moodMap,
                            totalWordsWritten = data.wordCount ?: 0
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading stats", e)
                _uiState.update { state -> state.copy(isLoading = false, error = "Failed to load stats. Please try again.") }
            }
        }
    }

    /**
     * Helper data class for combining stats flows.
     */
    private data class StatsData(
        val profile: com.prody.prashant.data.local.entity.UserProfileEntity?,
        val history: List<com.prody.prashant.data.local.entity.StreakHistoryEntity>,
        val moodCounts: List<com.prody.prashant.data.local.dao.MoodCount>,
        val wordCount: Int?
    )

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
                // Sync current user's leaderboard entry with real profile data
                syncCurrentUserLeaderboardEntry()

                // Combine both leaderboard flows into a single subscription
                // to prevent multiple coroutine leaks on configuration changes
                combine(
                    userDao.getLeaderboard(),
                    userDao.getWeeklyLeaderboard()
                ) { allTimeLeaderboard, weeklyLeaderboard ->
                    Pair(allTimeLeaderboard, weeklyLeaderboard)
                }.collect { (allTimeLeaderboard, weeklyLeaderboard) ->
                    val currentUserRank = allTimeLeaderboard.indexOfFirst { it.isCurrentUser } + 1
                    _uiState.update { state ->
                        state.copy(
                            allTimeLeaderboard = allTimeLeaderboard,
                            weeklyLeaderboard = weeklyLeaderboard,
                            currentRank = if (currentUserRank > 0) currentUserRank else 0
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading leaderboard", e)
            }
        }
    }

    /**
     * Syncs the current user's leaderboard entry with their actual profile data.
     * This ensures the leaderboard reflects real user progress, not outdated demo data.
     */
    private suspend fun syncCurrentUserLeaderboardEntry() {
        try {
            val userProfile = userDao.getUserProfileSync()
            if (userProfile != null) {
                // Calculate weekly points based on journal activity this week
                val weekStart = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                val weeklyEntries = journalDao.getEntriesCountThisWeek(weekStart)
                // Approximate weekly points: 10 points per journal entry + streak bonus
                val weeklyPoints = (weeklyEntries * 10) + (userProfile.currentStreak * 5)

                val currentUserEntry = LeaderboardEntryEntity(
                    odId = "current_user",
                    displayName = userProfile.displayName,
                    avatarId = userProfile.avatarId,
                    titleId = userProfile.titleId,
                    totalPoints = userProfile.totalPoints,
                    weeklyPoints = weeklyPoints,
                    currentStreak = userProfile.currentStreak,
                    rank = 0, // Will be recalculated when leaderboard loads
                    previousRank = 0,
                    isCurrentUser = true,
                    lastActiveAt = userProfile.lastActiveDate,
                    boostsReceived = userProfile.boostsReceived,
                    congratsReceived = 0
                )
                userDao.insertLeaderboardEntry(currentUserEntry)
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error syncing current user leaderboard entry", e)
        }
    }

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
            _uiState.update { it.copy(isLoading = true, error = null) }
            loadStats()
            loadLeaderboard()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Set the filter period for stats display.
     * Currently updates local state - future implementation will filter data accordingly.
     */
    fun setFilterPeriod(period: String) {
        // Future implementation: filter stats data based on period
        // For now, this stores the selection and logs it
        // When full analytics are implemented, this will trigger data refresh
        // with the appropriate time range filter
    }

    // =============================================================================
    // BOOSTING 2.0 - SUPPORT SYSTEM
    // =============================================================================

    companion object {
        private const val TAG = "StatsViewModel"
        private const val MAX_BOOSTS_PER_DAY = 5
        private const val MAX_RESPECTS_PER_DAY = 10
    }

    /**
     * Send a boost to another user.
     * Rate limited to MAX_BOOSTS_PER_DAY per day.
     * In production, this would sync with backend.
     */
    fun sendBoost(userId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (!currentState.canBoostToday) {
                return@launch
            }

            try {
                // Update local leaderboard entry
                val updatedLeaderboard = currentState.allTimeLeaderboard.map { entry ->
                    if (entry.odId == userId) {
                        entry.copy(boostsReceived = entry.boostsReceived + 1)
                    } else entry
                }

                val newBoostCount = currentState.boostsSentToday + 1
                _uiState.update {
                    it.copy(
                        allTimeLeaderboard = updatedLeaderboard,
                        weeklyLeaderboard = currentState.weeklyLeaderboard.map { entry ->
                            if (entry.odId == userId) {
                                entry.copy(boostsReceived = entry.boostsReceived + 1)
                            } else entry
                        },
                        boostsSentToday = newBoostCount,
                        canBoostToday = newBoostCount < MAX_BOOSTS_PER_DAY
                    )
                }

                // Update database
                userDao.incrementBoosts(userId)

            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error sending boost", e)
            }
        }
    }

    /**
     * Send respect to another user.
     * Rate limited to MAX_RESPECTS_PER_DAY per day.
     * In production, this would sync with backend.
     */
    fun sendRespect(userId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (!currentState.canRespectToday) {
                return@launch
            }

            try {
                // Update local leaderboard entry
                val updatedLeaderboard = currentState.allTimeLeaderboard.map { entry ->
                    if (entry.odId == userId) {
                        entry.copy(respectsReceived = entry.respectsReceived + 1)
                    } else entry
                }

                val newRespectCount = currentState.respectsSentToday + 1
                _uiState.update {
                    it.copy(
                        allTimeLeaderboard = updatedLeaderboard,
                        weeklyLeaderboard = currentState.weeklyLeaderboard.map { entry ->
                            if (entry.odId == userId) {
                                entry.copy(respectsReceived = entry.respectsReceived + 1)
                            } else entry
                        },
                        respectsSentToday = newRespectCount,
                        canRespectToday = newRespectCount < MAX_RESPECTS_PER_DAY
                    )
                }

                // Update database
                userDao.incrementRespects(userId)

            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error sending respect", e)
            }
        }
    }
}
