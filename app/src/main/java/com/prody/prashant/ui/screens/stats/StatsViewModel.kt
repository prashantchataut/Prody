package com.prody.prashant.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val userDao: UserDao
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

        // Load mood distribution
        viewModelScope.launch {
            try {
                // Generate sample mood distribution data
                // In a real app, this would come from journal entries
                val sampleMoodData = mapOf(
                    "happy" to (10..30).random(),
                    "calm" to (15..35).random(),
                    "motivated" to (8..25).random(),
                    "grateful" to (5..20).random(),
                    "anxious" to (3..12).random(),
                    "sad" to (2..8).random()
                )
                _uiState.update { state ->
                    state.copy(moodDistribution = sampleMoodData)
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading mood distribution", e)
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

    private fun createSampleLeaderboard(): List<LeaderboardEntryEntity> {
        val names = listOf(
            "GrowthSeeker", "MindfulMaster", "WisdomWanderer", "StoicSoul",
            "ZenZealot", "ThoughtTracker", "ReflectiveRider", "InsightInquirer"
        )

        return names.mapIndexed { index, name ->
            LeaderboardEntryEntity(
                odId = UUID.randomUUID().toString(),
                displayName = name,
                totalPoints = (1000 - index * 100) + (0..50).random(),
                weeklyPoints = (200 - index * 20) + (0..20).random(),
                currentStreak = (30 - index * 3).coerceAtLeast(0),
                rank = index + 1,
                isCurrentUser = index == 3 // User is 4th place initially
            )
        }
    }

    private fun calculateWeeklyProgress(data: List<Int>): List<Int> {
        return if (data.size >= 7) {
            data.takeLast(7)
        } else {
            List(7 - data.size) { 0 } + data
        }
    }

    fun boostPeer(peerId: String) {
        viewModelScope.launch {
            try {
                userDao.incrementBoosts(peerId)
                // In a real app, this would also send a notification to the peer
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error boosting peer: $peerId", e)
            }
        }
    }

    fun congratulatePeer(peerId: String) {
        viewModelScope.launch {
            try {
                userDao.incrementCongrats(peerId)
                // In a real app, this would also send a notification to the peer
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error congratulating peer: $peerId", e)
            }
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
