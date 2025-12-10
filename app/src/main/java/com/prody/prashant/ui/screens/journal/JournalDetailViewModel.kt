package com.prody.prashant.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalDetailUiState(
    val entry: JournalEntryEntity? = null,
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false
)

@HiltViewModel
class JournalDetailViewModel @Inject constructor(
    private val journalDao: JournalDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalDetailUiState())
    val uiState: StateFlow<JournalDetailUiState> = _uiState.asStateFlow()

    fun loadEntry(entryId: Long) {
        viewModelScope.launch {
            journalDao.observeEntryById(entryId).collect { entry ->
                _uiState.update {
                    it.copy(entry = entry, isLoading = false)
                }
            }
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            _uiState.value.entry?.let { entry ->
                journalDao.updateBookmarkStatus(entry.id, !entry.isBookmarked)
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
            _uiState.value.entry?.let { entry ->
                journalDao.deleteEntry(entry)
            }
        }
    }
}
