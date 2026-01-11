package com.prody.prashant.ui.screens.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.entity.DailyMissionEntity
import com.prody.prashant.data.local.entity.WeeklyTrialEntity
import com.prody.prashant.domain.gamification.MissionSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for Missions Screen - Daily Missions & Weekly Trials.
 *
 * Manages:
 * - Today's daily missions (3 per day)
 * - Current weekly trial
 * - Mission completion tracking
 * - Historical stats
 */
@HiltViewModel
class MissionsViewModel @Inject constructor(
    private val missionSystem: MissionSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(MissionsUiState())
    val uiState: StateFlow<MissionsUiState> = _uiState.asStateFlow()

    init {
        loadMissions()
        observeMissions()
    }

    private fun loadMissions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Ensure today's missions exist
                missionSystem.getTodayMissions()
                // Ensure weekly trial exists
                missionSystem.getWeeklyTrial()

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = e.message
                ) }
            }
        }
    }

    private fun observeMissions() {
        // Observe today's missions
        viewModelScope.launch {
            missionSystem.observeTodayMissions()
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { missions ->
                    _uiState.update { it.copy(
                        todayMissions = missions,
                        completedToday = missions.count { m -> m.isCompleted }
                    ) }
                }
        }

        // Observe weekly trial
        viewModelScope.launch {
            missionSystem.observeWeeklyTrial()
                .catch { /* Ignore trial errors */ }
                .collect { trial ->
                    _uiState.update { it.copy(weeklyTrial = trial) }
                }
        }
    }

    /**
     * Navigate to the appropriate screen to complete a mission.
     */
    fun getMissionNavigationRoute(mission: DailyMissionEntity): String {
        return when (mission.missionType) {
            "reflect" -> "journal/new"
            "sharpen" -> "vocabulary"
            "commit" -> "future_message/write"
            else -> "home"
        }
    }

    /**
     * Get days remaining in current week for weekly trial.
     */
    fun getDaysRemainingInWeek(): Int {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> 0
            else -> Calendar.SATURDAY - dayOfWeek + 1
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun refresh() {
        loadMissions()
    }
}

/**
 * UI State for Missions Screen.
 */
data class MissionsUiState(
    val isLoading: Boolean = false,
    val todayMissions: List<DailyMissionEntity> = emptyList(),
    val weeklyTrial: WeeklyTrialEntity? = null,
    val completedToday: Int = 0,
    val errorMessage: String? = null
) {
    val allMissionsComplete: Boolean
        get() = todayMissions.isNotEmpty() && todayMissions.all { it.isCompleted }

    val totalMissions: Int
        get() = todayMissions.size

    val completionPercentage: Float
        get() = if (totalMissions > 0) {
            (completedToday.toFloat() / totalMissions) * 100f
        } else 0f

    val weeklyTrialProgress: Float
        get() = weeklyTrial?.progressPercent() ?: 0f

    val hasWeeklyTrial: Boolean
        get() = weeklyTrial != null

    val weeklyTrialComplete: Boolean
        get() = weeklyTrial?.isCompleted == true
}
