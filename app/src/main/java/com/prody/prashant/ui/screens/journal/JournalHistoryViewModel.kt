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
    val totalEntryCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedFilterMood: String? = null,
    val showBookmarkedOnly: Boolean = false,
    val dateRangeFilter: DateRangeFilter = DateRangeFilter.ALL_TIME,
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST
) {
    val hasActiveFilters: Boolean
        get() = selectedFilterMood != null || showBookmarkedOnly || dateRangeFilter != DateRangeFilter.ALL_TIME

    val activeFilterCount: Int
        get() = listOfNotNull(
            selectedFilterMood,
            if (showBookmarkedOnly) "bookmarked" else null,
            if (dateRangeFilter != DateRangeFilter.ALL_TIME) "date" else null
        ).size
}

enum class SortOrder {
    NEWEST_FIRST,
    OLDEST_FIRST,
    HIGHEST_INTENSITY,
    LOWEST_INTENSITY
}

enum class DateRangeFilter {
    ALL_TIME,
    THIS_WEEK,
    THIS_MONTH,
    LAST_3_MONTHS,
    THIS_YEAR
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
    private val _showBookmarkedOnly = MutableStateFlow(false)
    private val _dateRangeFilter = MutableStateFlow(DateRangeFilter.ALL_TIME)
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
                    _showBookmarkedOnly,
                    _dateRangeFilter,
                    _sortOrder
                ) { allEntries, filterMood, bookmarkedOnly, dateRange, sortOrder ->
                    // Apply mood filter if selected
                    var filteredEntries = if (filterMood != null) {
                        allEntries.filter { it.mood.equals(filterMood, ignoreCase = true) }
                    } else {
                        allEntries
                    }

                    // Apply bookmarked filter
                    if (bookmarkedOnly) {
                        filteredEntries = filteredEntries.filter { it.isBookmarked }
                    }

                    // Apply date range filter
                    val now = System.currentTimeMillis()
                    filteredEntries = when (dateRange) {
                        DateRangeFilter.ALL_TIME -> filteredEntries
                        DateRangeFilter.THIS_WEEK -> {
                            val startOfWeek = getStartOfWeek(now)
                            filteredEntries.filter { it.createdAt >= startOfWeek }
                        }
                        DateRangeFilter.THIS_MONTH -> {
                            val startOfMonth = getStartOfMonth(now)
                            filteredEntries.filter { it.createdAt >= startOfMonth }
                        }
                        DateRangeFilter.LAST_3_MONTHS -> {
                            val threeMonthsAgo = getMonthsAgo(now, 3)
                            filteredEntries.filter { it.createdAt >= threeMonthsAgo }
                        }
                        DateRangeFilter.THIS_YEAR -> {
                            val startOfYear = getStartOfYear(now)
                            filteredEntries.filter { it.createdAt >= startOfYear }
                        }
                    }

                    // Apply sort order
                    val sortedEntries = when (sortOrder) {
                        SortOrder.NEWEST_FIRST -> filteredEntries.sortedByDescending { it.createdAt }
                        SortOrder.OLDEST_FIRST -> filteredEntries.sortedBy { it.createdAt }
                        SortOrder.HIGHEST_INTENSITY -> filteredEntries.sortedByDescending { it.moodIntensity }
                        SortOrder.LOWEST_INTENSITY -> filteredEntries.sortedBy { it.moodIntensity }
                    }

                    // Get time boundaries for grouping
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
                        totalEntryCount = allEntries.size,
                        displayedOlderCount = _uiState.value.displayedOlderCount,
                        isLoading = false,
                        error = null,
                        selectedFilterMood = filterMood,
                        showBookmarkedOnly = bookmarkedOnly,
                        dateRangeFilter = dateRange,
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

    fun setBookmarkedOnly(enabled: Boolean) {
        _showBookmarkedOnly.value = enabled
    }

    fun setDateRangeFilter(range: DateRangeFilter) {
        _dateRangeFilter.value = range
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun clearAllFilters() {
        _selectedFilterMood.value = null
        _showBookmarkedOnly.value = false
        _dateRangeFilter.value = DateRangeFilter.ALL_TIME
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

    fun retry() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        loadEntries()
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

    private fun getStartOfMonth(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getMonthsAgo(timestamp: Long, months: Int): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            add(Calendar.MONTH, -months)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getStartOfYear(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
