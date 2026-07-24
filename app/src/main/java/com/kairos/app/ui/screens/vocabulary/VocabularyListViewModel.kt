package com.kairos.app.ui.screens.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.app.data.local.entity.VocabularyEntity
import com.kairos.app.domain.repository.VocabularyRepository
import com.kairos.app.ui.browse.filterVocabulary
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

enum class VocabularyFilter(val key: String, val label: String) {
    ALL("all", "All"),
    NEW("new", "New"),
    LEARNED("learned", "Learned")
}

data class VocabularyListUiState(
    val words: List<VocabularyEntity> = emptyList(),
    val learnedCount: Int = 0,
    val totalCount: Int = 0,
    val showFavoritesOnly: Boolean = false,
    val currentFilter: String = VocabularyFilter.ALL.key,
    val query: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

private data class VocabularyBrowseCriteria(
    val favoritesOnly: Boolean = false,
    val filter: VocabularyFilter = VocabularyFilter.ALL,
    val query: String = ""
)

@HiltViewModel
class VocabularyListViewModel @Inject constructor(
    private val vocabularyRepository: VocabularyRepository
) : ViewModel() {

    private val criteria = MutableStateFlow(VocabularyBrowseCriteria())
    private val reloadSignal = MutableStateFlow(0)
    private val transientError = MutableStateFlow<String?>(null)

    private val allWords = reloadSignal
        .flatMapLatest { vocabularyRepository.getAllWords() }

    val uiState: StateFlow<VocabularyListUiState> = combine(
        allWords,
        criteria,
        transientError
    ) { words, browse, error ->
        val filtered = filterVocabulary(
            words = words,
            favoritesOnly = browse.favoritesOnly,
            filterKey = browse.filter.key,
            query = browse.query
        )

        VocabularyListUiState(
            words = filtered,
            learnedCount = words.count { it.isLearned },
            totalCount = words.size,
            showFavoritesOnly = browse.favoritesOnly,
            currentFilter = browse.filter.key,
            query = browse.query,
            isLoading = false,
            error = error
        )
    }
        .catch { error ->
            emit(
                VocabularyListUiState(
                    isLoading = false,
                    error = error.message ?: "Vocabulary could not be loaded."
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VocabularyListUiState()
        )

    fun toggleFavoritesOnly() {
        criteria.update { it.copy(favoritesOnly = !it.favoritesOnly) }
    }

    fun setFilter(filter: String) {
        val selected = VocabularyFilter.entries.firstOrNull { it.key == filter } ?: VocabularyFilter.ALL
        criteria.update { it.copy(filter = selected) }
    }

    fun setQuery(query: String) {
        criteria.update { it.copy(query = query) }
    }

    fun toggleFavorite(wordId: Long) {
        viewModelScope.launch {
            val word = vocabularyRepository.getWordById(wordId).getOrNull()
            if (word == null) {
                transientError.value = "This word is no longer available."
                return@launch
            }
            vocabularyRepository.updateFavoriteStatus(wordId, !word.isFavorite)
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
