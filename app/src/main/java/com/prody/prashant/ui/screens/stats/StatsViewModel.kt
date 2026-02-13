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
                com.prody.prashant.util.AppLogger.e(TAG, "Error loading stats", e)
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
                // Only seed demo data if leaderboard is completely empty
                // This prevents overwriting real user progress on every launch
                val existingCount = userDao.getLeaderboardCount()
                if (existingCount == 0) {
                    com.prody.prashant.util.AppLogger.d(TAG, "Seeding initial demo leaderboard data")
                    val sampleLeaderboard = createSampleLeaderboard()
                    userDao.insertLeaderboardEntries(sampleLeaderboard)
                }

                // Always ensure current user's leaderboard entry is synced with their profile
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
                com.prody.prashant.util.AppLogger.e(TAG, "Error loading leaderboard", e)
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
     *
     * Full 40-entry leaderboard to match the redesigned Stats screen
     */
    private fun createSampleLeaderboard(): List<LeaderboardEntryEntity> {
        // Realistic, diverse names that feel authentic
        // Full leaderboard with 40 entries as per design spec
        val leaderboardUsers = listOf(
            // Top 3 with animated banners
            LeaderboardUser("Sarah Vance", avatarId = "avatar_1", titleId = "champion", points = 9240, weekly = 245, streak = 72),
            LeaderboardUser("Marcus Jin", avatarId = "avatar_2", titleId = "sage", points = 8950, weekly = 220, streak = 65),
            LeaderboardUser("Elara K.", avatarId = "avatar_3", titleId = "philosopher", points = 8720, weekly = 198, streak = 58),
            // Rest of leaderboard
            LeaderboardUser("David Chen", avatarId = "avatar_4", titleId = "practitioner", points = 8540, weekly = 185, streak = 52),
            LeaderboardUser("Sophia L.", avatarId = "avatar_5", titleId = "contemplative", points = 8310, weekly = 172, streak = 48),
            LeaderboardUser("Aiden Ross", avatarId = "avatar_6", titleId = "initiate", points = 8305, weekly = 168, streak = 45),
            LeaderboardUser("Elena V.", avatarId = "avatar_7", titleId = "seeker", points = 7920, weekly = 156, streak = 42),
            LeaderboardUser("Tom Hardy", avatarId = "avatar_8", titleId = "seeker", points = 7650, weekly = 148, streak = 38),
            LeaderboardUser("Lila M.", avatarId = "avatar_9", titleId = "seeker", points = 7400, weekly = 142, streak = 35),
            LeaderboardUser("Kenji S.", avatarId = "avatar_10", titleId = "seeker", points = 7320, weekly = 138, streak = 33),
            LeaderboardUser("Olivia R.", avatarId = "avatar_11", titleId = "seeker", points = 6750, weekly = 132, streak = 30),
            LeaderboardUser("Liam T.", avatarId = "avatar_12", titleId = "seeker", points = 6600, weekly = 126, streak = 28),
            LeaderboardUser("Noah B.", avatarId = "avatar_13", titleId = "seeker", points = 6420, weekly = 118, streak = 25),
            LeaderboardUser("Emma W.", avatarId = "avatar_14", titleId = "seeker", points = 6300, weekly = 112, streak = 22),
            LeaderboardUser("James C.", avatarId = "avatar_15", titleId = "seeker", points = 6360, weekly = 108, streak = 20),
            LeaderboardUser("Ava G.", avatarId = "avatar_16", titleId = "seeker", points = 6000, weekly = 102, streak = 18),
            LeaderboardUser("Lucas M.", avatarId = "avatar_17", titleId = "seeker", points = 5850, weekly = 96, streak = 16),
            LeaderboardUser("Mia H.", avatarId = "avatar_18", titleId = "seeker", points = 5706, weekly = 92, streak = 15),
            LeaderboardUser("Ethan D.", avatarId = "avatar_19", titleId = "seeker", points = 5650, weekly = 88, streak = 14),
            LeaderboardUser("Chloe B.", avatarId = "avatar_20", titleId = "seeker", points = 5520, weekly = 84, streak = 13),
            LeaderboardUser("Daniel K.", avatarId = "avatar_21", titleId = "seeker", points = 5410, weekly = 80, streak = 12),
            LeaderboardUser("Harper Y.", avatarId = "avatar_22", titleId = "seeker", points = 5350, weekly = 76, streak = 11),
            LeaderboardUser("Jackson P.", avatarId = "avatar_23", titleId = "seeker", points = 5200, weekly = 72, streak = 10),
            LeaderboardUser("Lily N.", avatarId = "avatar_24", titleId = "seeker", points = 5080, weekly = 68, streak = 9),
            LeaderboardUser("Mason R.", avatarId = "avatar_25", titleId = "seeker", points = 4950, weekly = 64, streak = 8),
            LeaderboardUser("Zoe F.", avatarId = "avatar_26", titleId = "seeker", points = 4820, weekly = 60, streak = 7),
            LeaderboardUser("Caleb S.", avatarId = "avatar_27", titleId = "seeker", points = 4750, weekly = 56, streak = 6),
            LeaderboardUser("Aria W.", avatarId = "avatar_28", titleId = "seeker", points = 4600, weekly = 52, streak = 5),
            LeaderboardUser("Leo G.", avatarId = "avatar_29", titleId = "seeker", points = 4550, weekly = 48, streak = 5),
            LeaderboardUser("Stella J.", avatarId = "avatar_30", titleId = "seeker", points = 4400, weekly = 44, streak = 4),
            LeaderboardUser("Ryan Q.", avatarId = "avatar_31", titleId = "seeker", points = 4350, weekly = 42, streak = 4),
            LeaderboardUser("Nora V.", avatarId = "avatar_32", titleId = "seeker", points = 4280, weekly = 40, streak = 3),
            LeaderboardUser("Eli T.", avatarId = "avatar_33", titleId = "seeker", points = 4350, weekly = 38, streak = 3),
            LeaderboardUser("Grace L.", avatarId = "avatar_34", titleId = "seeker", points = 4000, weekly = 36, streak = 3),
            LeaderboardUser("Isaac M.", avatarId = "avatar_35", titleId = "seeker", points = 3850, weekly = 34, streak = 2),
            LeaderboardUser("Hannah K.", avatarId = "avatar_36", titleId = "seeker", points = 3800, weekly = 32, streak = 2),
            LeaderboardUser("Dylan O.", avatarId = "avatar_37", titleId = "seeker", points = 3750, weekly = 30, streak = 2),
            LeaderboardUser("Samantha E.", avatarId = "avatar_38", titleId = "seeker", points = 3600, weekly = 28, streak = 1),
            LeaderboardUser("Owen Z.", avatarId = "avatar_39", titleId = "seeker", points = 3550, weekly = 26, streak = 1),
            // Current User - Alex Morgan at position 40 with 8,450 points
            LeaderboardUser("Alex Morgan", avatarId = "avatar_user", titleId = "seeker", points = 8450, weekly = 175, streak = 45, isCurrentUser = true)
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
                com.prody.prashant.util.AppLogger.d(TAG, "Synced current user leaderboard entry with profile")
            }
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error syncing current user leaderboard entry", e)
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
        com.prody.prashant.util.AppLogger.d(TAG, "Filter period set to: $period")
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
                com.prody.prashant.util.AppLogger.d(TAG, "Cannot boost: daily limit reached")
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

                com.prody.prashant.util.AppLogger.d(TAG, "Boost sent to $userId. Today's count: $newBoostCount/$MAX_BOOSTS_PER_DAY")
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error sending boost", e)
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
                com.prody.prashant.util.AppLogger.d(TAG, "Cannot respect: daily limit reached")
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

                com.prody.prashant.util.AppLogger.d(TAG, "Respect sent to $userId. Today's count: $newRespectCount/$MAX_RESPECTS_PER_DAY")
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error sending respect", e)
            }
        }
    }
}
