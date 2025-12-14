package com.prody.prashant.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.AchievementEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "Growth Seeker",
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
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    init {
        loadProfile()
        loadAchievements()
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
}
