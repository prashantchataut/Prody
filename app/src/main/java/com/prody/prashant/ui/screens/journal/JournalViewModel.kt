package com.prody.prashant.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalUiState(
    val entries: List<JournalEntryEntity> = emptyList(),
    val totalEntries: Int = 0,
    val showBookmarkedOnly: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalDao: JournalDao
) : ViewModel() {

    private val _showBookmarkedOnly = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            try {
                combine(
                    _showBookmarkedOnly,
                    journalDao.getAllEntries(),
                    journalDao.getBookmarkedEntries()
                ) { showBookmarked, allEntries, bookmarkedEntries ->
                    JournalUiState(
                        entries = if (showBookmarked) bookmarkedEntries else allEntries,
                        totalEntries = allEntries.size,
                        showBookmarkedOnly = showBookmarked,
                        isLoading = false,
                        error = null
                    )
                }.collect { state ->
                    _uiState.update { state }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load journal entries")
                }
            }
        }
    }

    fun toggleBookmarkFilter() {
        _showBookmarkedOnly.value = !_showBookmarkedOnly.value
    }

    fun toggleBookmark(entryId: Long) {
        viewModelScope.launch {
            try {
                val entry = journalDao.getEntryById(entryId)
                entry?.let {
                    journalDao.updateBookmarkStatus(entryId, !it.isBookmarked)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update bookmark") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        loadEntries()
    }
}
