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
    val currentRank: Int = 0,
    val wordsLearned: Int = 0,
    val journalEntries: Int = 0,
    val futureMessages: Int = 0,
    val daysActive: Int = 0,
    val weeklyLeaderboard: List<LeaderboardEntryEntity> = emptyList(),
    val allTimeLeaderboard: List<LeaderboardEntryEntity> = emptyList(),
    val weeklyProgress: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
        loadLeaderboard()
    }

    private fun loadStats() {
        viewModelScope.launch {
            userDao.getUserProfile().collect { profile ->
                profile?.let {
                    _uiState.update { state ->
                        state.copy(
                            totalPoints = it.totalPoints,
                            currentStreak = it.currentStreak,
                            wordsLearned = it.wordsLearned,
                            journalEntries = it.journalEntriesCount,
                            futureMessages = it.futureMessagesCount,
                            isLoading = false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            userDao.getStreakHistory().collect { history ->
                val daysActive = history.size
                val weeklyProgress = calculateWeeklyProgress(history.map { it.pointsEarned })

                _uiState.update { state ->
                    state.copy(
                        daysActive = daysActive,
                        weeklyProgress = weeklyProgress
                    )
                }
            }
        }
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
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
        }

        viewModelScope.launch {
            userDao.getWeeklyLeaderboard().collect { leaderboard ->
                _uiState.update { state ->
                    state.copy(weeklyLeaderboard = leaderboard)
                }
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
            userDao.incrementBoosts(peerId)
            // In a real app, this would also send a notification to the peer
        }
    }

    fun congratulatePeer(peerId: String) {
        viewModelScope.launch {
            userDao.incrementCongrats(peerId)
            // In a real app, this would also send a notification to the peer
        }
    }
}
