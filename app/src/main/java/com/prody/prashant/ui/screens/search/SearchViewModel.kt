package com.prody.prashant.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.repository.SearchCategory
import com.prody.prashant.data.repository.SearchRepository
import com.prody.prashant.data.repository.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val selectedCategory: SearchCategory = SearchCategory.ALL,
    val results: List<SearchResult> = emptyList(),
    val recentContent: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")
    private val _categoryFlow = MutableStateFlow(SearchCategory.ALL)

    init {
        loadRecentContent()
        observeSearch()
    }

    private fun loadRecentContent() {
        viewModelScope.launch {
            try {
                searchRepository.getRecentContent().collect { recent ->
                    _uiState.update { it.copy(recentContent = recent) }
                }
            } catch (e: Exception) {
                android.util.Log.e("SearchViewModel", "Error loading recent content", e)
            }
        }
    }

    private fun observeSearch() {
        viewModelScope.launch {
            combine(
                _queryFlow.debounce(300), // Debounce user input
                _categoryFlow
            ) { query, category ->
                Pair(query, category)
            }.flatMapLatest { (query, category) ->
                if (query.isBlank()) {
                    flowOf(emptyList())
                } else {
                    _uiState.update { it.copy(isSearching = true) }
                    searchRepository.search(query, category)
                }
            }.catch { e ->
                android.util.Log.e("SearchViewModel", "Search error", e)
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        errorMessage = "Search failed. Please try again."
                    )
                }
            }.collect { results ->
                _uiState.update {
                    it.copy(
                        results = results,
                        isSearching = false,
                        hasSearched = _queryFlow.value.isNotBlank()
                    )
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        _queryFlow.value = query
    }

    fun onCategorySelected(category: SearchCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
        _categoryFlow.value = category
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                query = "",
                results = emptyList(),
                hasSearched = false
            )
        }
        _queryFlow.value = ""
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
