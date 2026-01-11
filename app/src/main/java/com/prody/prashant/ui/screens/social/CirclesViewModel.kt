package com.prody.prashant.ui.screens.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.repository.SocialRepository
import com.prody.prashant.domain.social.Circle
import com.prody.prashant.domain.social.CircleNotification
import com.prody.prashant.domain.social.CircleSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Circles List screen
 */
data class CirclesUiState(
    val isLoading: Boolean = true,
    val circles: List<Circle> = emptyList(),
    val circleSummaries: Map<String, CircleSummary> = emptyMap(),
    val unreadNotificationCount: Int = 0,
    val unreadNudgeCount: Int = 0,
    val errorMessage: String? = null,
    val showCreateDialog: Boolean = false,
    val showJoinDialog: Boolean = false,
    val showLeaveConfirmation: Boolean = false,
    val circleToLeave: Circle? = null,
    val selectedCircle: Circle? = null
)

@HiltViewModel
class CirclesViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CirclesUiState())
    val uiState: StateFlow<CirclesUiState> = _uiState.asStateFlow()

    private val userId = "local" // Will be replaced with actual user ID from auth

    init {
        loadCircles()
        loadNotificationCounts()
    }

    private fun loadCircles() {
        viewModelScope.launch {
            socialRepository.observeUserCircles(userId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load circles: ${e.message}"
                        )
                    }
                }
                .collect { circles ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            circles = circles
                        )
                    }

                    // Load summaries for each circle
                    circles.forEach { circle ->
                        loadCircleSummary(circle.id)
                    }
                }
        }
    }

    private fun loadCircleSummary(circleId: String) {
        viewModelScope.launch {
            socialRepository.getCircleSummary(circleId).onSuccess { summary ->
                _uiState.update { state ->
                    val summaries = state.circleSummaries.toMutableMap()
                    summaries[circleId] = summary
                    state.copy(circleSummaries = summaries)
                }
            }
        }
    }

    private fun loadNotificationCounts() {
        viewModelScope.launch {
            socialRepository.observeUnreadNotificationCount(userId)
                .catch { }
                .collect { count ->
                    _uiState.update { it.copy(unreadNotificationCount = count) }
                }
        }

        viewModelScope.launch {
            socialRepository.observeUnreadNudgeCount(userId)
                .catch { }
                .collect { count ->
                    _uiState.update { it.copy(unreadNudgeCount = count) }
                }
        }
    }

    fun onCircleClick(circle: Circle) {
        _uiState.update { it.copy(selectedCircle = circle) }
    }

    fun onCreateCircleClick() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun onJoinCircleClick() {
        _uiState.update { it.copy(showJoinDialog = true) }
    }

    fun onLeaveCircleClick(circle: Circle) {
        _uiState.update {
            it.copy(
                showLeaveConfirmation = true,
                circleToLeave = circle
            )
        }
    }

    fun confirmLeaveCircle() {
        val circle = _uiState.value.circleToLeave ?: return

        viewModelScope.launch {
            when (socialRepository.leaveCircle(userId, circle.id)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            showLeaveConfirmation = false,
                            circleToLeave = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Failed to leave circle",
                            showLeaveConfirmation = false,
                            circleToLeave = null
                        )
                    }
                }
            }
        }
    }

    fun dismissLeaveConfirmation() {
        _uiState.update {
            it.copy(
                showLeaveConfirmation = false,
                circleToLeave = null
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun dismissCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }

    fun dismissJoinDialog() {
        _uiState.update { it.copy(showJoinDialog = false) }
    }

    fun refresh() {
        loadCircles()
        loadNotificationCounts()
    }
}
