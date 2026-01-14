package com.prody.prashant.ui.screens.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.repository.SocialRepository
import com.prody.prashant.domain.social.Circle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Join Circle screen
 */
data class JoinCircleUiState(
    val inviteCode: String = "",
    val isValidating: Boolean = false,
    val isJoining: Boolean = false,
    val previewCircle: Circle? = null,
    val joinedCircle: Circle? = null,
    val errorMessage: String? = null,
    val showPrivacyReminder: Boolean = false
)

@HiltViewModel
class JoinCircleViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinCircleUiState())
    val uiState: StateFlow<JoinCircleUiState> = _uiState.asStateFlow()

    private val userId = "local"
    private val displayName = "You" // Replace with actual user name

    fun onInviteCodeChanged(code: String) {
        // Format code as uppercase, max 6 chars
        val formatted = code.uppercase().take(6)
        _uiState.update { it.copy(inviteCode = formatted) }

        // Auto-validate when 6 characters entered
        if (formatted.length == 6) {
            validateInviteCode()
        } else {
            _uiState.update { it.copy(previewCircle = null) }
        }
    }

    private fun validateInviteCode() {
        val code = _uiState.value.inviteCode
        if (code.length != 6) return

        _uiState.update { it.copy(isValidating = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = socialRepository.validateInviteCode(code)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isValidating = false,
                            previewCircle = result.data,
                            showPrivacyReminder = true
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isValidating = false,
                            previewCircle = null,
                            errorMessage = result.userMessage
                        )
                    }
                }
                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun joinCircle() {
        val code = _uiState.value.inviteCode
        if (code.length != 6) return

        _uiState.update { it.copy(isJoining = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = socialRepository.joinCircle(
                userId = userId,
                displayName = displayName,
                inviteCode = code
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isJoining = false,
                            joinedCircle = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isJoining = false,
                            errorMessage = result.userMessage
                        )
                    }
                }
                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun dismissPrivacyReminder() {
        _uiState.update { it.copy(showPrivacyReminder = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun reset() {
        _uiState.update {
            JoinCircleUiState()
        }
    }
}
