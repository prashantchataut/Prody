package com.prody.prashant.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.AchievementEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AchievementsCollectionUiState(
    val allAchievements: List<AchievementEntity> = emptyList(),
    val selectedFilter: AchievementFilter = AchievementFilter.ALL,
    val selectedAchievement: AchievementEntity? = null,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val unlockedCount: Int
        get() = allAchievements.count { it.isUnlocked }

    val totalCount: Int
        get() = allAchievements.size

    val totalPointsFromAchievements: Int
        get() = allAchievements
            .filter { it.isUnlocked }
            .sumOf { it.rewardValue.toIntOrNull() ?: 0 }

    val filteredAchievements: List<AchievementEntity>
        get() = when (selectedFilter) {
            AchievementFilter.ALL -> allAchievements.sortedByDescending { it.isUnlocked }
            AchievementFilter.UNLOCKED -> allAchievements.filter { it.isUnlocked }
                .sortedByDescending { it.unlockedAt }
            AchievementFilter.LOCKED -> allAchievements.filter { !it.isUnlocked }
                .sortedByDescending { it.currentProgress.toFloat() / it.requirement.coerceAtLeast(1) }
            AchievementFilter.WISDOM -> allAchievements.filter { it.category == "wisdom" }
            AchievementFilter.REFLECTION -> allAchievements.filter { it.category == "reflection" }
            AchievementFilter.CONSISTENCY -> allAchievements.filter { it.category == "consistency" }
            AchievementFilter.PRESENCE -> allAchievements.filter { it.category == "presence" }
            AchievementFilter.TEMPORAL -> allAchievements.filter { it.category == "temporal" }
            AchievementFilter.MASTERY -> allAchievements.filter { it.category == "mastery" }
        }
}

@HiltViewModel
class AchievementsCollectionViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsCollectionUiState())
    val uiState: StateFlow<AchievementsCollectionUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "AchievementsCollectionVM"
    }

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            try {
                userDao.getAllAchievements().collect { achievements ->
                    _uiState.update {
                        it.copy(
                            allAchievements = achievements,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading achievements", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load achievements"
                    )
                }
            }
        }
    }

    fun selectFilter(filter: AchievementFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun selectAchievement(achievement: AchievementEntity) {
        _uiState.update { it.copy(selectedAchievement = achievement) }
    }

    fun clearSelectedAchievement() {
        _uiState.update { it.copy(selectedAchievement = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
