package com.prody.prashant.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Available avatars for profile customization.
 */
data class AvatarOption(
    val id: String,
    val name: String,
    val isLocked: Boolean = false,
    val unlockRequirement: String? = null
)

/**
 * Available titles for profile customization.
 */
data class TitleOption(
    val id: String,
    val displayName: String,
    val isLocked: Boolean = false,
    val unlockRequirement: String? = null
)

data class EditProfileUiState(
    val displayName: String = "",
    val bio: String = "",
    val currentAvatarId: String = "default",
    val currentTitleId: String = "seeker",
    val availableAvatars: List<AvatarOption> = emptyList(),
    val availableTitles: List<TitleOption> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val hasUnsavedChanges: Boolean = false,
    val showDiscardDialog: Boolean = false,
    // Original values for comparison
    val originalDisplayName: String = "",
    val originalBio: String = "",
    val originalAvatarId: String = "default",
    val originalTitleId: String = "seeker"
) {
    val hasContent: Boolean
        get() = displayName != originalDisplayName ||
                bio != originalBio ||
                currentAvatarId != originalAvatarId ||
                currentTitleId != originalTitleId
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "EditProfileViewModel"
        private const val MAX_DISPLAY_NAME_LENGTH = 30
        private const val MAX_BIO_LENGTH = 150
    }

    init {
        loadProfile()
        loadAvatarOptions()
        loadTitleOptions()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                userDao.getUserProfile().collect { profile ->
                    profile?.let {
                        _uiState.update { state ->
                            state.copy(
                                displayName = it.displayName,
                                bio = it.bio,
                                currentAvatarId = it.avatarId,
                                currentTitleId = it.titleId,
                                originalDisplayName = it.displayName,
                                originalBio = it.bio,
                                originalAvatarId = it.avatarId,
                                originalTitleId = it.titleId,
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading profile", e)
                _uiState.update { it.copy(isLoading = false, error = "Failed to load profile") }
            }
        }
    }

    private fun loadAvatarOptions() {
        // Default available avatars - can be expanded with unlockable ones
        val avatars = listOf(
            AvatarOption("default", "Default", isLocked = false),
            AvatarOption("lotus", "Lotus", isLocked = false),
            AvatarOption("mountain", "Mountain", isLocked = false),
            AvatarOption("river", "River", isLocked = false),
            AvatarOption("tree", "Tree", isLocked = false),
            AvatarOption("sun", "Sun", isLocked = false),
            AvatarOption("moon", "Moon", isLocked = false),
            AvatarOption("star", "Star", isLocked = false),
            AvatarOption("flame", "Flame", isLocked = true, unlockRequirement = "7-day streak"),
            AvatarOption("diamond", "Diamond", isLocked = true, unlockRequirement = "Level 5"),
            AvatarOption("crown", "Crown", isLocked = true, unlockRequirement = "Level 10"),
            AvatarOption("phoenix", "Phoenix", isLocked = true, unlockRequirement = "30-day streak")
        )
        _uiState.update { it.copy(availableAvatars = avatars) }
    }

    private fun loadTitleOptions() {
        viewModelScope.launch {
            try {
                val profile = userDao.getUserProfileSync()
                val totalPoints = profile?.totalPoints ?: 0
                val currentStreak = profile?.currentStreak ?: 0
                val longestStreak = profile?.longestStreak ?: 0
                val journalEntries = profile?.journalEntriesCount ?: 0

                val titles = listOf(
                    TitleOption("seeker", "Growth Seeker", isLocked = false),
                    TitleOption("newcomer", "Newcomer", isLocked = false),
                    TitleOption("apprentice", "Apprentice", isLocked = totalPoints < 200, unlockRequirement = "200 XP"),
                    TitleOption("scholar", "Scholar", isLocked = totalPoints < 500, unlockRequirement = "500 XP"),
                    TitleOption("sage", "Sage", isLocked = totalPoints < 1500, unlockRequirement = "1500 XP"),
                    TitleOption("master", "Master", isLocked = totalPoints < 3500, unlockRequirement = "3500 XP"),
                    TitleOption("grandmaster", "Grandmaster", isLocked = totalPoints < 7500, unlockRequirement = "7500 XP"),
                    TitleOption("legend", "Legend", isLocked = totalPoints < 10000, unlockRequirement = "10000 XP"),
                    TitleOption("streak_warrior", "Streak Warrior", isLocked = longestStreak < 7, unlockRequirement = "7-day streak"),
                    TitleOption("streak_champion", "Streak Champion", isLocked = longestStreak < 30, unlockRequirement = "30-day streak"),
                    TitleOption("journal_keeper", "Journal Keeper", isLocked = journalEntries < 10, unlockRequirement = "10 journal entries"),
                    TitleOption("storyteller", "Storyteller", isLocked = journalEntries < 50, unlockRequirement = "50 journal entries")
                )
                _uiState.update { it.copy(availableTitles = titles) }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading title options", e)
            }
        }
    }

    fun updateDisplayName(name: String) {
        if (name.length <= MAX_DISPLAY_NAME_LENGTH) {
            _uiState.update {
                it.copy(
                    displayName = name,
                    hasUnsavedChanges = true
                )
            }
        }
    }

    fun updateBio(bio: String) {
        if (bio.length <= MAX_BIO_LENGTH) {
            _uiState.update {
                it.copy(
                    bio = bio,
                    hasUnsavedChanges = true
                )
            }
        }
    }

    fun selectAvatar(avatarId: String) {
        val avatar = _uiState.value.availableAvatars.find { it.id == avatarId }
        if (avatar != null && !avatar.isLocked) {
            _uiState.update {
                it.copy(
                    currentAvatarId = avatarId,
                    hasUnsavedChanges = true
                )
            }
        }
    }

    fun selectTitle(titleId: String) {
        val title = _uiState.value.availableTitles.find { it.id == titleId }
        if (title != null && !title.isLocked) {
            _uiState.update {
                it.copy(
                    currentTitleId = titleId,
                    hasUnsavedChanges = true
                )
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val state = _uiState.value

            // Validate display name
            if (state.displayName.isBlank()) {
                _uiState.update { it.copy(error = "Display name cannot be empty") }
                return@launch
            }

            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                // Update display name
                if (state.displayName != state.originalDisplayName) {
                    userDao.updateDisplayName(state.displayName)
                }

                // Update bio
                if (state.bio != state.originalBio) {
                    userDao.updateBio(state.bio)
                }

                // Update avatar
                if (state.currentAvatarId != state.originalAvatarId) {
                    userDao.updateAvatar(state.currentAvatarId)
                }

                // Update title
                if (state.currentTitleId != state.originalTitleId) {
                    userDao.updateTitle(state.currentTitleId)
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true,
                        hasUnsavedChanges = false,
                        originalDisplayName = state.displayName,
                        originalBio = state.bio,
                        originalAvatarId = state.currentAvatarId,
                        originalTitleId = state.currentTitleId
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error saving profile", e)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Failed to save: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun showDiscardDialog() {
        _uiState.update { it.copy(showDiscardDialog = true) }
    }

    fun hideDiscardDialog() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    /**
     * Handle back navigation with unsaved changes check.
     * Returns true if navigation should proceed, false if dialog should be shown.
     */
    fun handleBackNavigation(): Boolean {
        val state = _uiState.value
        return if (state.hasContent && !state.isSaved) {
            showDiscardDialog()
            false
        } else {
            true
        }
    }
}
