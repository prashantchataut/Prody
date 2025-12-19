package com.prody.prashant.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * Journal History UI State
 *
 * Manages the state for the Journal History screen with chronological grouping
 */
data class JournalHistoryUiState(
    val thisWeekEntries: List<JournalEntryEntity> = emptyList(),
    val lastWeekEntries: List<JournalEntryEntity> = emptyList(),
    val olderEntries: List<JournalEntryEntity> = emptyList(),
    val displayedOlderCount: Int = INITIAL_OLDER_DISPLAY_COUNT,
    val totalOlderCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedFilterMood: String? = null,
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST
)

enum class SortOrder {
    NEWEST_FIRST,
    OLDEST_FIRST,
    HIGHEST_INTENSITY,
    LOWEST_INTENSITY
}

private const val INITIAL_OLDER_DISPLAY_COUNT = 5
private const val LOAD_MORE_INCREMENT = 5

@HiltViewModel
class JournalHistoryViewModel @Inject constructor(
    private val journalDao: JournalDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalHistoryUiState())
    val uiState: StateFlow<JournalHistoryUiState> = _uiState.asStateFlow()

    private val _selectedFilterMood = MutableStateFlow<String?>(null)
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)

    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            try {
                combine(
                    journalDao.getAllEntries(),
                    _selectedFilterMood,
                    _sortOrder
                ) { allEntries, filterMood, sortOrder ->
                    // Apply mood filter if selected
                    val filteredEntries = if (filterMood != null) {
                        allEntries.filter { it.mood.equals(filterMood, ignoreCase = true) }
                    } else {
                        allEntries
                    }

                    // Apply sort order
                    val sortedEntries = when (sortOrder) {
                        SortOrder.NEWEST_FIRST -> filteredEntries.sortedByDescending { it.createdAt }
                        SortOrder.OLDEST_FIRST -> filteredEntries.sortedBy { it.createdAt }
                        SortOrder.HIGHEST_INTENSITY -> filteredEntries.sortedByDescending { it.moodIntensity }
                        SortOrder.LOWEST_INTENSITY -> filteredEntries.sortedBy { it.moodIntensity }
                    }

                    // Get time boundaries
                    val now = System.currentTimeMillis()
                    val startOfThisWeek = getStartOfWeek(now)
                    val startOfLastWeek = getStartOfWeek(now - 7 * 24 * 60 * 60 * 1000L)

                    // Group entries
                    val thisWeek = sortedEntries.filter { it.createdAt >= startOfThisWeek }
                    val lastWeek = sortedEntries.filter {
                        it.createdAt >= startOfLastWeek && it.createdAt < startOfThisWeek
                    }
                    val older = sortedEntries.filter { it.createdAt < startOfLastWeek }

                    JournalHistoryUiState(
                        thisWeekEntries = thisWeek,
                        lastWeekEntries = lastWeek,
                        olderEntries = older,
                        totalOlderCount = older.size,
                        displayedOlderCount = _uiState.value.displayedOlderCount,
                        isLoading = false,
                        error = null,
                        selectedFilterMood = filterMood,
                        sortOrder = sortOrder
                    )
                }.collect { state ->
                    _uiState.update { currentState ->
                        state.copy(displayedOlderCount = currentState.displayedOlderCount)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load journal entries")
                }
            }
        }
    }

    fun setFilterMood(mood: String?) {
        _selectedFilterMood.value = mood
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun loadMoreOlderEntries() {
        _uiState.update {
            it.copy(displayedOlderCount = it.displayedOlderCount + LOAD_MORE_INCREMENT)
        }
    }

    fun resetOlderEntries() {
        _uiState.update {
            it.copy(displayedOlderCount = INITIAL_OLDER_DISPLAY_COUNT)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun getStartOfWeek(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
