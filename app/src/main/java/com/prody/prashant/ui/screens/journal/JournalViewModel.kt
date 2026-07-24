package com.prody.prashant.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.repository.JournalRepository
import com.prody.prashant.ui.browse.filterJournalEntries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalUiState(
    val entries: List<JournalEntryEntity> = emptyList(),
    val totalEntries: Int = 0,
    val showBookmarkedOnly: Boolean = false,
    val query: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

private data class JournalBrowseCriteria(
    val bookmarkedOnly: Boolean = false,
    val query: String = ""
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val criteria = MutableStateFlow(JournalBrowseCriteria())
    private val reloadSignal = MutableStateFlow(0)
    private val transientError = MutableStateFlow<String?>(null)

    private val allEntries = reloadSignal
        .flatMapLatest { journalRepository.getAllEntries() }

    val uiState: StateFlow<JournalUiState> = combine(
        allEntries,
        criteria,
        transientError
    ) { entries, browse, error ->
        val visibleEntries = filterJournalEntries(
            entries = entries,
            bookmarkedOnly = browse.bookmarkedOnly,
            query = browse.query
        )

        JournalUiState(
            entries = visibleEntries,
            totalEntries = entries.count { !it.isDeleted },
            showBookmarkedOnly = browse.bookmarkedOnly,
            query = browse.query,
            isLoading = false,
            error = error
        )
    }
        .catch { error ->
            emit(
                JournalUiState(
                    isLoading = false,
                    error = error.message ?: "Journal entries could not be loaded."
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = JournalUiState()
        )

    fun toggleBookmarkFilter() {
        criteria.update { it.copy(bookmarkedOnly = !it.bookmarkedOnly) }
    }

    fun setQuery(query: String) {
        criteria.update { it.copy(query = query) }
    }

    fun toggleBookmark(entryId: Long) {
        viewModelScope.launch {
            val entry = journalRepository.getEntryById(entryId).getOrNull()
            if (entry == null) {
                transientError.value = "This entry is no longer available."
                return@launch
            }
            journalRepository.updateBookmarkStatus(entryId, !entry.isBookmarked)
                .onError { transientError.value = it.userMessage }
        }
    }

    fun clearError() {
        transientError.value = null
    }

    fun retry() {
        transientError.value = null
        reloadSignal.update { it + 1 }
    }
}
