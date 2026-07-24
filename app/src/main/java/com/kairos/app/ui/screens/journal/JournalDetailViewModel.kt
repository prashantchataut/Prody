package com.kairos.app.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.app.data.local.entity.JournalEntryEntity
import com.kairos.app.data.onboarding.AiHint
import com.kairos.app.data.onboarding.AiHintType
import com.kairos.app.data.onboarding.AiOnboardingManager
import com.kairos.app.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalDetailUiState(
    val entry: JournalEntryEntity? = null,
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false,
    val error: String? = null,
    val showJournalInsightHint: Boolean = false
)

@HiltViewModel
class JournalDetailViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val aiOnboardingManager: AiOnboardingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalDetailUiState())
    val uiState: StateFlow<JournalDetailUiState> = _uiState.asStateFlow()

    init {
        checkOnboarding()
    }

    private fun checkOnboarding() {
        viewModelScope.launch {
            aiOnboardingManager.shouldShowHint(AiHintType.FIRST_JOURNAL_INSIGHT).collect { shouldShow ->
                _uiState.update { state ->
                    state.copy(showJournalInsightHint = shouldShow)
                }
            }
        }
    }

    fun onJournalInsightHintDismiss() {
        viewModelScope.launch {
            aiOnboardingManager.markHintShown(AiHintType.FIRST_JOURNAL_INSIGHT)
            _uiState.update { it.copy(showJournalInsightHint = false) }
        }
    }

    fun getJournalInsightHint(): AiHint {
        return aiOnboardingManager.getHintContent(AiHintType.FIRST_JOURNAL_INSIGHT)
    }

    fun loadEntry(entryId: Long) {
        viewModelScope.launch {
            try {
                journalRepository.observeEntryById(entryId).collect { entry ->
                    _uiState.update {
                        it.copy(entry = entry, isLoading = false, error = null)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load journal entry")
                }
            }
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            try {
                _uiState.value.entry?.let { entry ->
                    val result = journalRepository.updateBookmarkStatus(entry.id, !entry.isBookmarked)
                    if (result.isError) {
                        _uiState.update { it.copy(error = "Failed to update bookmark") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update bookmark") }
            }
        }
    }

    fun showDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun deleteEntry() {
        viewModelScope.launch {
            try {
                _uiState.value.entry?.let { entry ->
                    val result = journalRepository.deleteEntry(entry)
                    if (result.isError) {
                        _uiState.update { it.copy(error = "Failed to delete entry") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete entry") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retry(entryId: Long) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        loadEntry(entryId)
    }
}
