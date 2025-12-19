package com.prody.prashant.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.ai.BuddhaAiRepository
import com.prody.prashant.data.ai.WeeklyPatternResult
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.domain.model.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "Growth Seeker",
    val bio: String = "",
    val title: String = "Newcomer",
    val avatarId: String = "default",
    val bannerId: String = "default",
    val totalPoints: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val wordsLearned: Int = 0,
    val journalEntries: Int = 0,
    val achievementsUnlocked: Int = 0,
    val daysOnPrody: Int = 0,
    val unlockedAchievements: List<AchievementEntity> = emptyList(),
    val lockedAchievements: List<AchievementEntity> = emptyList(),
    val isLoading: Boolean = true,
    // Weekly AI Pattern Tracking
    val weeklyPattern: WeeklyPatternResult? = null,
    val isLoadingWeeklyPattern: Boolean = false,
    val hasEnoughDataForPattern: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDao: UserDao,
    private val journalDao: JournalDao,
    private val buddhaAiRepository: BuddhaAiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    init {
        loadProfile()
        loadAchievements()
        loadWeeklyPattern()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                userDao.getUserProfile().collect { profile ->
                    profile?.let {
                        val daysOnPrody = TimeUnit.MILLISECONDS.toDays(
                            System.currentTimeMillis() - it.joinedAt
                        ).toInt().coerceAtLeast(1)

                        _uiState.update { state ->
                            state.copy(
                                displayName = it.displayName,
                                bio = it.bio,
                                title = getTitleFromId(it.titleId),
                                avatarId = it.avatarId,
                                bannerId = it.bannerId,
                                totalPoints = it.totalPoints,
                                currentStreak = it.currentStreak,
                                longestStreak = it.longestStreak,
                                wordsLearned = it.wordsLearned,
                                journalEntries = it.journalEntriesCount,
                                daysOnPrody = daysOnPrody,
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading profile", e)
                _uiState.update { state ->
                    state.copy(isLoading = false)
                }
            }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            try {
                combine(
                    userDao.getUnlockedAchievements(),
                    userDao.getLockedAchievements()
                ) { unlocked, locked ->
                    Pair(unlocked, locked)
                }.collect { (unlocked, locked) ->
                    _uiState.update { state ->
                        state.copy(
                            unlockedAchievements = unlocked,
                            lockedAchievements = locked,
                            achievementsUnlocked = unlocked.size
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading achievements", e)
            }
        }
    }

    private fun getTitleFromId(titleId: String): String {
        return when (titleId) {
            "newcomer" -> "Newcomer"
            "apprentice" -> "Apprentice"
            "scholar" -> "Scholar"
            "sage" -> "Sage"
            "master" -> "Master"
            "grandmaster" -> "Grandmaster"
            "legend" -> "Legend"
            else -> "Growth Seeker"
        }
    }

    /**
     * Load weekly pattern analysis based on journal data.
     * Shows: recurring themes, mood trend, time-of-day patterns.
     */
    private fun loadWeeklyPattern() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoadingWeeklyPattern = true) }

                // Get start of week
                val weekStart = getWeekStartTimestamp()
                val currentStreak = _uiState.value.currentStreak

                // Gather journal data for the week
                val journalCount = journalDao.getEntriesCountThisWeek(weekStart)

                // Need at least 3 entries for meaningful patterns
                if (journalCount < 3) {
                    _uiState.update {
                        it.copy(
                            isLoadingWeeklyPattern = false,
                            hasEnoughDataForPattern = false
                        )
                    }
                    return@launch
                }

                // Get dominant mood
                val dominantMoodData = journalDao.getDominantMoodThisWeek(weekStart)
                val dominantMood = dominantMoodData?.mood?.let { moodName ->
                    try { Mood.valueOf(moodName) } catch (e: Exception) { null }
                }

                // Get themes
                val themesRaw = journalDao.getThemesThisWeek(weekStart)
                val themes = themesRaw
                    .flatMap { it.split(",") }
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .groupingBy { it }
                    .eachCount()
                    .entries
                    .sortedByDescending { it.value }
                    .take(4)
                    .map { it.key }

                // Get active time of day
                val timeOfDay = journalDao.getMostActiveTimeOfDay(weekStart)?.timeOfDay ?: "varied"

                // Calculate mood trend (simple)
                val moodTrend = when {
                    dominantMood == Mood.HAPPY || dominantMood == Mood.EXCITED -> "positive"
                    dominantMood == Mood.SAD || dominantMood == Mood.ANXIOUS -> "challenging"
                    else -> "balanced"
                }

                // Generate AI pattern analysis
                val result = buddhaAiRepository.getWeeklyPatterns(
                    journalCount = journalCount,
                    dominantMood = dominantMood,
                    themes = themes,
                    moodTrend = moodTrend,
                    activeTimeOfDay = timeOfDay,
                    streakDays = currentStreak
                )

                result.getOrNull()?.let { pattern ->
                    _uiState.update {
                        it.copy(
                            weeklyPattern = pattern,
                            isLoadingWeeklyPattern = false,
                            hasEnoughDataForPattern = true
                        )
                    }
                    android.util.Log.d(TAG, "Weekly pattern loaded: ${pattern.keyPattern}")
                } ?: run {
                    _uiState.update {
                        it.copy(isLoadingWeeklyPattern = false)
                    }
                }

            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading weekly pattern", e)
                _uiState.update { it.copy(isLoadingWeeklyPattern = false) }
            }
        }
    }

    /**
     * Refresh weekly pattern (for manual refresh).
     */
    fun refreshWeeklyPattern() {
        loadWeeklyPattern()
    }

    private fun getWeekStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
