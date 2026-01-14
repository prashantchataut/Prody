package com.prody.prashant.ui.screens.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.model.MonthlyLetter
import com.prody.prashant.domain.repository.MonthlyLetterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

/**
 * UI State for Monthly Letter screen
 */
data class MonthlyLetterUiState(
    val isLoading: Boolean = true,
    val currentLetter: MonthlyLetter? = null,
    val allLetters: List<MonthlyLetter> = emptyList(),
    val unreadCount: Int = 0,
    val recentLetters: List<MonthlyLetter> = emptyList(),
    val isGenerating: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val envelopeOpened: Boolean = false,
    val showShareDialog: Boolean = false
)

@HiltViewModel
class MonthlyLetterViewModel @Inject constructor(
    private val monthlyLetterRepository: MonthlyLetterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyLetterUiState())
    val uiState: StateFlow<MonthlyLetterUiState> = _uiState.asStateFlow()

    private val userId = "local"

    init {
        loadMostRecentLetter()
        loadRecentLetters()
        observeUnreadCount()
    }

    /**
     * Load the most recent letter
     */
    private fun loadMostRecentLetter() {
        viewModelScope.launch {
            monthlyLetterRepository.observeMostRecentLetter(userId)
                .catch { e ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load letter: ${e.message}"
                    )}
                }
                .collect { letter ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        currentLetter = letter,
                        envelopeOpened = letter?.isRead ?: false
                    )}
                }
        }
    }

    /**
     * Load recent letters for history
     */
    private fun loadRecentLetters() {
        viewModelScope.launch {
            monthlyLetterRepository.getRecentLetters(userId, limit = 12)
                .collect { letters ->
                    _uiState.update { it.copy(recentLetters = letters) }
                }
        }
    }

    /**
     * Observe unread letter count
     */
    private fun observeUnreadCount() {
        viewModelScope.launch {
            monthlyLetterRepository.getUnreadLetterCount(userId)
                .collect { count ->
                    _uiState.update { it.copy(unreadCount = count) }
                }
        }
    }

    /**
     * Load a specific letter by ID
     */
    fun loadLetter(letterId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = monthlyLetterRepository.getLetterById(letterId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        currentLetter = result.data,
                        envelopeOpened = result.data.isRead
                    )}
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.userMessage
                    )}
                }
            }
        }
    }

    /**
     * Load letter for a specific month
     */
    fun loadLetterForMonth(monthYear: YearMonth) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = monthlyLetterRepository.getLetterForMonth(userId, monthYear)) {
                is Result.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        currentLetter = result.data,
                        envelopeOpened = result.data?.isRead ?: false
                    )}
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.userMessage
                    )}
                }
            }
        }
    }

    /**
     * Open the envelope (animate and mark as read)
     */
    fun openEnvelope() {
        val letter = _uiState.value.currentLetter ?: return

        // Update UI immediately for smooth animation
        _uiState.update { it.copy(envelopeOpened = true) }

        // Mark as read in database
        if (!letter.isRead) {
            viewModelScope.launch {
                monthlyLetterRepository.markAsRead(letter.id)
            }
        }
    }

    /**
     * Toggle favorite status
     */
    fun toggleFavorite() {
        val letter = _uiState.value.currentLetter ?: return

        viewModelScope.launch {
            when (monthlyLetterRepository.toggleFavorite(letter.id, !letter.isFavorite)) {
                is Result.Success -> {
                    _uiState.update { it.copy(
                        successMessage = if (!letter.isFavorite) "Added to favorites" else "Removed from favorites"
                    )}
                }
                is Result.Error -> {
                    _uiState.update { it.copy(errorMessage = "Failed to update favorite") }
                }
            }
        }
    }

    /**
     * Generate letter for previous month
     */
    fun generateLetterForPreviousMonth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }

            when (val result = monthlyLetterRepository.generateLetterForPreviousMonth(userId)) {
                is Result.Success -> {
                    if (result.data != null) {
                        _uiState.update { it.copy(
                            isGenerating = false,
                            currentLetter = result.data,
                            successMessage = "Your ${result.data.monthYear.month.name} letter is ready!"
                        )}
                    } else {
                        _uiState.update { it.copy(
                            isGenerating = false,
                            errorMessage = "Not enough activity to generate a letter yet"
                        )}
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isGenerating = false,
                        errorMessage = result.userMessage
                    )}
                }
            }
        }
    }

    /**
     * Show share dialog
     */
    fun showShareDialog() {
        _uiState.update { it.copy(showShareDialog = true) }
    }

    /**
     * Hide share dialog
     */
    fun hideShareDialog() {
        _uiState.update { it.copy(showShareDialog = false) }
    }

    /**
     * Mark letter as shared
     */
    fun markAsShared() {
        val letter = _uiState.value.currentLetter ?: return

        viewModelScope.launch {
            monthlyLetterRepository.markAsShared(letter.id)
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Clear success message
     */
    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    /**
     * Reset envelope state for new letter
     */
    fun resetEnvelope() {
        _uiState.update { it.copy(envelopeOpened = false) }
    }
}
