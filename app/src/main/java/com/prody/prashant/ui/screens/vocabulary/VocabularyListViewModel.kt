package com.prody.prashant.ui.screens.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VocabularyListUiState(
    val words: List<VocabularyEntity> = emptyList(),
    val learnedCount: Int = 0,
    val totalCount: Int = 0,
    val showFavoritesOnly: Boolean = false,
    val currentFilter: String = "all",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class VocabularyListViewModel @Inject constructor(
    private val vocabularyDao: VocabularyDao
) : ViewModel() {

    private val _showFavoritesOnly = MutableStateFlow(false)
    private val _currentFilter = MutableStateFlow("all")

    private val _uiState = MutableStateFlow(VocabularyListUiState())
    val uiState: StateFlow<VocabularyListUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "VocabularyListViewModel"
    }

    init {
        loadVocabulary()
        loadCounts()
    }

    private fun loadVocabulary() {
        viewModelScope.launch {
            try {
                combine(
                    vocabularyDao.getAllVocabulary(),
                    vocabularyDao.getLearnedWords(),
                    vocabularyDao.getFavoriteWords(),
                    _showFavoritesOnly,
                    _currentFilter
                ) { all, learned, favorites, favOnly, filter ->
                    val baseList = when {
                        favOnly -> favorites
                        filter == "learned" -> learned
                        filter == "new" -> all.filter { !it.isLearned }
                        else -> all
                    }
                    baseList
                }.collect { words ->
                    _uiState.update { it.copy(words = words, isLoading = false) }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading vocabulary", e)
                _uiState.update { it.copy(isLoading = false, error = "Failed to load vocabulary. Please try again.") }
            }
        }
    }

    private fun loadCounts() {
        viewModelScope.launch {
            try {
                combine(
                    vocabularyDao.getLearnedCount(),
                    vocabularyDao.getTotalCount()
                ) { learned, total ->
                    Pair(learned, total)
                }.collect { (learned, total) ->
                    _uiState.update { it.copy(learnedCount = learned, totalCount = total) }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error loading vocabulary counts", e)
            }
        }
    }

    fun toggleFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
        _uiState.update { it.copy(showFavoritesOnly = _showFavoritesOnly.value) }
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
        _uiState.update { it.copy(currentFilter = filter) }
    }

    fun toggleFavorite(wordId: Long) {
        viewModelScope.launch {
            try {
                val word = vocabularyDao.getWordById(wordId)
                word?.let {
                    vocabularyDao.updateFavoriteStatus(wordId, !it.isFavorite)
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error toggling favorite for word: $wordId", e)
                _uiState.update { it.copy(error = "Failed to update favorite status") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        loadVocabulary()
        loadCounts()
    }
}
