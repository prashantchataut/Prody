package com.prody.prashant.ui.screens.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.repository.SocialRepository
import com.prody.prashant.domain.social.Circle
import com.prody.prashant.domain.social.CircleTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Create Circle screen
 */
data class CreateCircleUiState(
    val circleName: String = "",
    val circleDescription: String = "",
    val selectedTheme: CircleTheme = CircleTheme.DEFAULT,
    val selectedEmoji: String = "🌟",
    val maxMembers: Int = 10,
    val isCreating: Boolean = false,
    val createdCircle: Circle? = null,
    val errorMessage: String? = null,
    val showThemePicker: Boolean = false,
    val showEmojiPicker: Boolean = false,
    val showMaxMembersPicker: Boolean = false
)

@HiltViewModel
class CreateCircleViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCircleUiState())
    val uiState: StateFlow<CreateCircleUiState> = _uiState.asStateFlow()

    private val userId = "local"
    private val displayName = "You" // Replace with actual user name

    private val emojiOptions = listOf(
        "🌟", "🔥", "💪", "🎯", "🌱", "✨", "🚀", "💫",
        "🎨", "📚", "🌊", "🌸", "🎭", "🎪", "🎬", "🎮"
    )

    private val maxMemberOptions = listOf(5, 10, 15, 20, 30, 50)

    fun onCircleNameChanged(name: String) {
        _uiState.update { it.copy(circleName = name) }
    }

    fun onCircleDescriptionChanged(description: String) {
        _uiState.update { it.copy(circleDescription = description) }
    }

    fun onThemeSelected(theme: CircleTheme) {
        _uiState.update {
            it.copy(
                selectedTheme = theme,
                showThemePicker = false
            )
        }
    }

    fun onEmojiSelected(emoji: String) {
        _uiState.update {
            it.copy(
                selectedEmoji = emoji,
                showEmojiPicker = false
            )
        }
    }

    fun onMaxMembersSelected(max: Int) {
        _uiState.update {
            it.copy(
                maxMembers = max,
                showMaxMembersPicker = false
            )
        }
    }

    fun onShowThemePicker() {
        _uiState.update { it.copy(showThemePicker = true) }
    }

    fun onShowEmojiPicker() {
        _uiState.update { it.copy(showEmojiPicker = true) }
    }

    fun onShowMaxMembersPicker() {
        _uiState.update { it.copy(showMaxMembersPicker = true) }
    }

    fun createCircle() {
        val state = _uiState.value
        if (state.circleName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Circle name is required") }
            return
        }

        _uiState.update { it.copy(isCreating = true) }

        viewModelScope.launch {
            when (val result = socialRepository.createCircle(
                userId = userId,
                displayName = displayName,
                name = state.circleName,
                description = state.circleDescription.takeIf { it.isNotBlank() },
                colorTheme = state.selectedTheme,
                iconEmoji = state.selectedEmoji,
                maxMembers = state.maxMembers
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            createdCircle = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isCreating = false,
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

    fun dismissPicker() {
        _uiState.update {
            it.copy(
                showThemePicker = false,
                showEmojiPicker = false,
                showMaxMembersPicker = false
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun getAvailableEmojis() = emojiOptions

    fun getAvailableMaxMembers() = maxMemberOptions

    fun getAvailableThemes() = CircleTheme.entries.toList()
}
