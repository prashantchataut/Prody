package com.prody.prashant.ui.screens.social

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.repository.SocialRepository
import com.prody.prashant.domain.social.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Circle Detail screen
 */
data class CircleDetailUiState(
    val isLoading: Boolean = true,
    val circle: Circle? = null,
    val members: List<CircleMember> = emptyList(),
    val updates: List<CircleUpdate> = emptyList(),
    val activeChallenges: List<CircleChallenge> = emptyList(),
    val errorMessage: String? = null,
    val showMemberOptions: CircleMember? = null,
    val showNudgeDialog: CircleMember? = null,
    val showEncouragementDialog: Boolean = false,
    val showSettings: Boolean = false,
    val showInviteSheet: Boolean = false,
    val nudgeMessage: String = "",
    val encouragementMessage: String = "",
    val selectedNudgeType: NudgeType = NudgeType.ENCOURAGE
)

@HiltViewModel
class CircleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val circleId: String = checkNotNull(savedStateHandle["circleId"])
    private val userId = "local"

    private val _uiState = MutableStateFlow(CircleDetailUiState())
    val uiState: StateFlow<CircleDetailUiState> = _uiState.asStateFlow()

    init {
        loadCircleData()
    }

    private fun loadCircleData() {
        viewModelScope.launch {
            socialRepository.observeCircle(circleId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load circle: ${e.message}"
                        )
                    }
                }
                .collect { circle ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            circle = circle
                        )
                    }
                }
        }

        viewModelScope.launch {
            socialRepository.observeCircleMembers(circleId)
                .catch { e ->
                    com.prody.prashant.util.AppLogger.w("CircleDetailViewModel", "Failed to load members: ${e.message}")
                    _uiState.update { it.copy(members = emptyList()) }
                }
                .collect { members ->
                    _uiState.update { it.copy(members = members) }
                }
        }

        viewModelScope.launch {
            socialRepository.observeCircleUpdates(circleId, limit = 50)
                .catch { e ->
                    com.prody.prashant.util.AppLogger.w("CircleDetailViewModel", "Failed to load updates: ${e.message}")
                    _uiState.update { it.copy(updates = emptyList()) }
                }
                .collect { updates ->
                    _uiState.update { it.copy(updates = updates) }
                }
        }

        viewModelScope.launch {
            socialRepository.observeActiveChallenges(circleId)
                .catch { e ->
                    com.prody.prashant.util.AppLogger.w("CircleDetailViewModel", "Failed to load challenges: ${e.message}")
                    _uiState.update { it.copy(activeChallenges = emptyList()) }
                }
                .collect { challenges ->
                    _uiState.update { it.copy(activeChallenges = challenges) }
                }
        }
    }

    fun onMemberClick(member: CircleMember) {
        _uiState.update { it.copy(showMemberOptions = member) }
    }

    fun onSendNudgeClick(member: CircleMember) {
        _uiState.update {
            it.copy(
                showNudgeDialog = member,
                showMemberOptions = null
            )
        }
    }

    fun onNudgeTypeSelected(type: NudgeType) {
        _uiState.update { it.copy(selectedNudgeType = type) }
    }

    fun onNudgeMessageChanged(message: String) {
        _uiState.update { it.copy(nudgeMessage = message) }
    }

    fun sendNudge() {
        val member = _uiState.value.showNudgeDialog ?: return
        val type = _uiState.value.selectedNudgeType
        val message = _uiState.value.nudgeMessage.takeIf { it.isNotBlank() }

        viewModelScope.launch {
            when (socialRepository.sendNudge(
                fromUserId = userId,
                fromDisplayName = "You", // Replace with actual display name
                toUserId = member.userId,
                circleId = circleId,
                nudgeType = type,
                message = message
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            showNudgeDialog = null,
                            nudgeMessage = "",
                            selectedNudgeType = NudgeType.ENCOURAGE
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to send nudge")
                    }
                }
                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun onPostEncouragementClick() {
        _uiState.update { it.copy(showEncouragementDialog = true) }
    }

    fun onEncouragementMessageChanged(message: String) {
        _uiState.update { it.copy(encouragementMessage = message) }
    }

    fun postEncouragement() {
        val message = _uiState.value.encouragementMessage
        if (message.isBlank()) return

        viewModelScope.launch {
            when (socialRepository.postEncouragementUpdate(
                userId = userId,
                displayName = "You", // Replace with actual display name
                circleId = circleId,
                message = message
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            showEncouragementDialog = false,
                            encouragementMessage = ""
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to post encouragement")
                    }
                }
                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun onReactToUpdate(updateId: Long, emoji: String) {
        viewModelScope.launch {
            when (socialRepository.reactToUpdate(updateId, userId, emoji)) {
                is Result.Success -> {
                    // Reaction applied successfully - UI will update via Flow observation
                }
                is Result.Error -> {
                    _uiState.update { it.copy(errorMessage = "Failed to add reaction. Please try again.") }
                }
                is Result.Loading -> {
                    // Loading state handled by Flow
                }
            }
        }
    }

    fun onShowInviteSheet() {
        _uiState.update { it.copy(showInviteSheet = true) }
    }

    fun onShowSettings() {
        _uiState.update { it.copy(showSettings = true) }
    }

    fun dismissDialog() {
        _uiState.update {
            it.copy(
                showMemberOptions = null,
                showNudgeDialog = null,
                showEncouragementDialog = false,
                showSettings = false,
                showInviteSheet = false
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun refresh() {
        loadCircleData()
    }
}
