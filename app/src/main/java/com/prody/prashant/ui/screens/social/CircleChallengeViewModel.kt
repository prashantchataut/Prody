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
 * UI State for Circle Challenge screen
 */
data class CircleChallengeUiState(
    val isLoading: Boolean = true,
    val challenge: CircleChallenge? = null,
    val leaderboard: ChallengeLeaderboard? = null,
    val yourProgress: Int = 0,
    val yourProgressPercent: Float = 0f,
    val yourRank: Int = 0,
    val isParticipating: Boolean = false,
    val hasCompleted: Boolean = false,
    val errorMessage: String? = null,
    val showJoinDialog: Boolean = false,
    val showLeaveDialog: Boolean = false,
    val showProgressUpdateDialog: Boolean = false,
    val progressInput: String = ""
)

@HiltViewModel
class CircleChallengeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val challengeId: String = checkNotNull(savedStateHandle["challengeId"])
    private val userId = "local"

    private val _uiState = MutableStateFlow(CircleChallengeUiState())
    val uiState: StateFlow<CircleChallengeUiState> = _uiState.asStateFlow()

    init {
        loadChallengeData()
    }

    private fun loadChallengeData() {
        viewModelScope.launch {
            socialRepository.observeChallenge(challengeId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load challenge: ${e.message}"
                        )
                    }
                }
                .collect { challenge ->
                    if (challenge != null) {
                        val isParticipating = challenge.isUserParticipant(userId)
                        val yourProgress = challenge.getUserProgress(userId)
                        val yourProgressPercent = challenge.getUserProgressPercent(userId)
                        val hasCompleted = challenge.hasUserCompleted(userId)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                challenge = challenge,
                                isParticipating = isParticipating,
                                yourProgress = yourProgress,
                                yourProgressPercent = yourProgressPercent,
                                hasCompleted = hasCompleted
                            )
                        }

                        // Load leaderboard
                        loadLeaderboard()
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Challenge not found"
                            )
                        }
                    }
                }
        }
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            when (val result = socialRepository.getChallengeLeaderboard(challengeId)) {
                is Result.Success -> {
                    val leaderboard = result.data
                    val yourRanking = leaderboard.rankings.find { it.member.userId == userId }
                    val yourRank = yourRanking?.rank ?: 0

                    _uiState.update {
                        it.copy(
                            leaderboard = leaderboard,
                            yourRank = yourRank
                        )
                    }
                }
                is Result.Error -> {
                    // Leaderboard load failed, but don't block UI
                }
                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun onJoinChallengeClick() {
        _uiState.update { it.copy(showJoinDialog = true) }
    }

    fun confirmJoinChallenge() {
        viewModelScope.launch {
            _uiState.update { it.copy(showJoinDialog = false) }

            when (socialRepository.joinChallenge(userId, challengeId)) {
                is Result.Success -> {
                    // State will update via flow
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to join challenge")
                    }
                }
                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun onLeaveChallengeClick() {
        _uiState.update { it.copy(showLeaveDialog = true) }
    }

    fun confirmLeaveChallenge() {
        viewModelScope.launch {
            _uiState.update { it.copy(showLeaveDialog = false) }

            when (socialRepository.leaveChallenge(userId, challengeId)) {
                is Result.Success -> {
                    // State will update via flow
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to leave challenge")
                    }
                }
                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun onUpdateProgressClick() {
        _uiState.update {
            it.copy(
                showProgressUpdateDialog = true,
                progressInput = it.yourProgress.toString()
            )
        }
    }

    fun onProgressInputChanged(input: String) {
        // Only allow numbers
        val filtered = input.filter { it.isDigit() }
        _uiState.update { it.copy(progressInput = filtered) }
    }

    fun confirmProgressUpdate() {
        val input = _uiState.value.progressInput
        val progress = input.toIntOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(showProgressUpdateDialog = false) }

            when (socialRepository.updateChallengeProgress(userId, challengeId, progress)) {
                is Result.Success -> {
                    // State will update via flow
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to update progress")
                    }
                }
                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun dismissDialog() {
        _uiState.update {
            it.copy(
                showJoinDialog = false,
                showLeaveDialog = false,
                showProgressUpdateDialog = false
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun refresh() {
        loadChallengeData()
    }
}
