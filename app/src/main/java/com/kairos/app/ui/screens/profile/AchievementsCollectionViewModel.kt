package com.kairos.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.app.domain.model.AchievementProgress
import com.kairos.app.domain.repository.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AchievementFilter(
    val displayName: String,
    val categories: Set<String> = emptySet()
) {
    ALL("All"),
    UNLOCKED("Earned"),
    IN_PROGRESS("In progress"),
    WISDOM("Learning", setOf("wisdom", "learning")),
    REFLECTION("Reflection", setOf("reflection", "journal")),
    CONSISTENCY("Consistency", setOf("consistency", "time")),
    PRESENCE("Presence", setOf("presence", "wellbeing")),
    TEMPORAL("Across time", setOf("temporal", "future")),
    MASTERY("Mastery", setOf("mastery", "journey"))
}

data class AchievementsCollectionUiState(
    val allAchievements: List<AchievementProgress> = emptyList(),
    val selectedFilter: AchievementFilter = AchievementFilter.ALL,
    val selectedAchievement: AchievementProgress? = null,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val unlockedCount: Int get() = allAchievements.count(AchievementProgress::isUnlocked)
    val totalCount: Int get() = allAchievements.size
    val completionFraction: Float get() = if (totalCount == 0) 0f else unlockedCount.toFloat() / totalCount
    val totalPointsFromAchievements: Int get() = allAchievements.filter(AchievementProgress::isUnlocked).sumOf { it.rewardPoints }

    val filteredAchievements: List<AchievementProgress>
        get() = allAchievements
            .asSequence()
            .filter { achievement ->
                when (selectedFilter) {
                    AchievementFilter.ALL -> true
                    AchievementFilter.UNLOCKED -> achievement.isUnlocked
                    AchievementFilter.IN_PROGRESS -> !achievement.isUnlocked
                    else -> selectedFilter.categories.any { category ->
                        achievement.category.equals(category, ignoreCase = true)
                    }
                }
            }
            .sortedWith(
                compareByDescending<AchievementProgress> { it.isUnlocked }
                    .thenByDescending { it.progressFraction }
                    .thenByDescending { rarityWeight(it.rarity) }
            )
            .toList()

    val closestMilestones: List<AchievementProgress>
        get() = allAchievements
            .filterNot(AchievementProgress::isUnlocked)
            .filter { it.currentProgress > 0 }
            .sortedByDescending(AchievementProgress::progressFraction)
            .take(3)
}

@HiltViewModel
class AchievementsCollectionViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsCollectionUiState())
    val uiState: StateFlow<AchievementsCollectionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            achievementRepository.observeAchievements()
                .catch {
                    _uiState.update { state ->
                        state.copy(isLoading = false, error = "Kairos could not load your milestones.")
                    }
                }
                .collect { achievements ->
                    _uiState.update { state ->
                        state.copy(
                            allAchievements = achievements,
                            isLoading = false,
                            error = null,
                            selectedAchievement = state.selectedAchievement?.let { selected ->
                                achievements.firstOrNull { it.id == selected.id }
                            }
                        )
                    }
                }
        }
    }

    fun selectFilter(filter: AchievementFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun selectAchievement(achievement: AchievementProgress) {
        _uiState.update { it.copy(selectedAchievement = achievement) }
    }

    fun clearSelectedAchievement() {
        _uiState.update { it.copy(selectedAchievement = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

private fun rarityWeight(rarity: String): Int = when (rarity.lowercase()) {
    "mythic" -> 6
    "legendary" -> 5
    "epic" -> 4
    "rare" -> 3
    "uncommon" -> 2
    else -> 1
}
