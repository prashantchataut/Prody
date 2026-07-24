package com.kairos.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.app.data.ai.BuddhaAiRepository
import com.kairos.app.data.ai.WeeklyPatternResult
import com.kairos.app.data.auth.AuthRepository
import com.kairos.app.data.auth.AuthState
import com.kairos.app.domain.repository.JournalRepository
import com.kairos.app.domain.repository.ProfileRepository
import com.kairos.app.data.local.entity.AchievementEntity
import com.kairos.app.data.local.preferences.PreferencesManager
import com.kairos.app.domain.gamification.KairosProgressionPolicy
import com.kairos.app.domain.model.Mood
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
    val daysOnKairos: Int = 0,
    val authDisplayName: String? = null,
    val authEmail: String? = null,
    val authPhotoUrl: String? = null,
    val unlockedAchievements: List<AchievementEntity> = emptyList(),
    val lockedAchievements: List<AchievementEntity> = emptyList(),
    val isLoading: Boolean = true,
    // Weekly AI Pattern Tracking
    val weeklyPattern: WeeklyPatternResult? = null,
    val isLoadingWeeklyPattern: Boolean = false,
    val hasEnoughDataForPattern: Boolean = false,
    // Badge flags (from debug preferences)
    val isDev: Boolean = false,
    val isBetaPioneer: Boolean = false,
    // Player Skills (Gamification 3.0)
    val clarityXp: Int = 0,
    val disciplineXp: Int = 0,
    val courageXp: Int = 0,
    val dailyClarityXp: Int = 0,
    val dailyDisciplineXp: Int = 0,
    val dailyCourageXp: Int = 0,
    val tokens: Int = 0,
    // Soul Layer Context for Identity Card
    val userContext: com.kairos.app.domain.intelligence.UserContext = com.kairos.app.domain.intelligence.UserContext.empty(),
    val level: Int = 1,
    // Error state
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val journalRepository: JournalRepository,
    private val buddhaAiRepository: BuddhaAiRepository,
    private val preferencesManager: PreferencesManager,
    private val soulLayerRepository: com.kairos.app.domain.repository.SoulLayerRepository,
    private val authRepository: AuthRepository
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
        loadBadgePreferences()
        loadPlayerSkills()
        loadSoulContext()
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    _uiState.update { it.copy(
                        authDisplayName = state.displayName,
                        authEmail = state.email,
                        authPhotoUrl = state.photoUrl
                    )}
                }
            }
        }
    }

    private fun loadSoulContext() {
        viewModelScope.launch {
            try {
                val context = soulLayerRepository.getCurrentContext()
                _uiState.update { it.copy(userContext = context) }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading soul context", e)
            }
        }
    }

    private fun loadBadgePreferences() {
        viewModelScope.launch {
            combine(
                preferencesManager.debugPreviewDevBadge,
                preferencesManager.debugPreviewBetaBadge
            ) { isDev, isBetaPioneer ->
                Pair(isDev, isBetaPioneer)
            }.collect { (isDev, isBetaPioneer) ->
                _uiState.update { state ->
                    state.copy(
                        isDev = isDev,
                        isBetaPioneer = isBetaPioneer
                    )
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                profileRepository.observeProfile().collect { profile ->
                    profile?.let {
                        val daysOnKairos = TimeUnit.MILLISECONDS.toDays(
                            System.currentTimeMillis() - it.joinedAt
                        ).toInt().coerceAtLeast(1)

                        val resolvedDisplayName = it.displayName.takeIf { name ->
                            name != "Growth Seeker" && name.isNotBlank()
                        } ?: authRepository.currentUser?.displayName

                        _uiState.update { state ->
                            state.copy(
                                displayName = resolvedDisplayName ?: it.displayName,
                                bio = it.bio,
                                title = getTitleFromId(it.titleId),
                                avatarId = it.avatarId,
                                bannerId = it.bannerId,
                                totalPoints = it.totalPoints,
                                currentStreak = it.currentStreak,
                                longestStreak = it.longestStreak,
                                wordsLearned = it.wordsLearned,
                                journalEntries = it.journalEntriesCount,
                                daysOnKairos = daysOnKairos,
                                level = getLevelFromPoints(it.totalPoints),
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading profile", e)
                _uiState.update { state ->
                    state.copy(isLoading = false, error = "Failed to load profile. Please try again.")
                }
            }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            try {
                combine(
                    profileRepository.observeUnlockedAchievements(),
                    profileRepository.observeLockedAchievements()
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

    private fun loadPlayerSkills() {
        viewModelScope.launch {
            try {
                profileRepository.observePlayerSkills().collect { skills ->
                    skills?.let {
                        _uiState.update { state ->
                            state.copy(
                                clarityXp = it.clarityXp,
                                disciplineXp = it.disciplineXp,
                                courageXp = it.courageXp,
                                dailyClarityXp = it.dailyClarityXp,
                                dailyDisciplineXp = it.dailyDisciplineXp,
                                dailyCourageXp = it.dailyCourageXp,
                                tokens = it.tokens
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading player skills", e)
            }
        }
    }

    private fun getLevelFromPoints(points: Int): Int = KairosProgressionPolicy.levelFor(points)

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

                val weekStart = getWeekStartTimestamp()
                val currentStreak = profileRepository.getProfile().getOrNull()?.currentStreak
                    ?: _uiState.value.currentStreak
                val entries = journalRepository.getEntriesForWeek(weekStart)
                val journalCount = entries.size

                // Pattern language without enough evidence feels fabricated.
                if (journalCount < 3) {
                    _uiState.update {
                        it.copy(
                            isLoadingWeeklyPattern = false,
                            hasEnoughDataForPattern = false,
                            weeklyPattern = null
                        )
                    }
                    return@launch
                }

                val dominantMood = journalRepository.getDominantMood(entries)
                val themes = entries.asSequence()
                    .mapNotNull { it.aiThemes }
                    .flatMap { it.split(',').asSequence() }
                    .map(String::trim)
                    .filter(String::isNotEmpty)
                    .groupingBy { it.lowercase() }
                    .eachCount()
                    .entries
                    .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
                    .take(4)
                    .map { it.key.replaceFirstChar(Char::titlecase) }

                val timeOfDay = mostActiveTimeOfDay(entries.map { it.createdAt })
                val moodTrend = when (dominantMood) {
                    Mood.HAPPY, Mood.EXCITED -> "positive"
                    Mood.SAD, Mood.ANXIOUS -> "challenging"
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


    private fun mostActiveTimeOfDay(timestamps: List<Long>): String {
        if (timestamps.isEmpty()) return "varied"
        return timestamps
            .map { timestamp ->
                Calendar.getInstance().apply { timeInMillis = timestamp }.get(Calendar.HOUR_OF_DAY)
            }
            .map { hour ->
                when (hour) {
                    in 5..11 -> "morning"
                    in 12..16 -> "afternoon"
                    in 17..21 -> "evening"
                    else -> "night"
                }
            }
            .groupingBy { it }
            .eachCount()
            .maxWithOrNull(compareBy<Map.Entry<String, Int>> { it.value }.thenByDescending { it.key })
            ?.key
            ?: "varied"
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

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        loadProfile()
        loadAchievements()
        loadWeeklyPattern()
    }
}
