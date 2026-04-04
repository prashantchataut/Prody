package com.prody.prashant.ui.screens.wisdom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.entity.SavedWisdomEntity
import com.prody.prashant.domain.repository.WisdomCollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Wisdom Collection screen
 */
data class WisdomCollectionUiState(
    val isLoading: Boolean = true,
    val allWisdom: List<SavedWisdomEntity> = emptyList(),
    val filteredWisdom: List<SavedWisdomEntity> = emptyList(),
    val selectedType: WisdomType? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val wisdomCounts: Map<String, Int> = emptyMap(),
    val totalCount: Int = 0,
    val selectedWisdom: SavedWisdomEntity? = null,
    val showDetailSheet: Boolean = false,
    val errorMessage: String? = null,
    val showUnsaveConfirmation: Boolean = false,
    val wisdomToUnsave: SavedWisdomEntity? = null,
    val resurfacedWisdom: List<SavedWisdomEntity> = emptyList()
)

/**
 * Types of wisdom that can be saved
 */
enum class WisdomType(val displayName: String, val entityType: String) {
    ALL("All", ""),
    QUOTE("Quotes", "QUOTE"),
    WORD("Words", "WORD"),
    PROVERB("Proverbs", "PROVERB"),
    IDIOM("Idioms", "IDIOM"),
    PHRASE("Phrases", "PHRASE"),
    BUDDHA("Buddha", "BUDDHA_WISDOM")
}

@HiltViewModel
class WisdomCollectionViewModel @Inject constructor(
    private val wisdomRepository: WisdomCollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WisdomCollectionUiState())
    val uiState: StateFlow<WisdomCollectionUiState> = _uiState.asStateFlow()

    private val userId = "local"

    init {
        loadWisdomCollection()
        loadResurfacedWisdom()
    }

    private fun loadWisdomCollection() {
        viewModelScope.launch {
            wisdomRepository.getAllSavedWisdom(userId)
                .catch { e ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load wisdom collection"
                    )}
                }
                .collect { wisdom ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            allWisdom = wisdom,
                            filteredWisdom = applyFilters(wisdom, state.selectedType, state.searchQuery),
                            totalCount = wisdom.size,
                            wisdomCounts = wisdom.groupingBy { it.type }.eachCount()
                        )
                    }
                }
        }
    }

    private fun loadResurfacedWisdom() {
        viewModelScope.launch {
            wisdomRepository.getWisdomToResurface(userId, 3)
                .onSuccess { resurfaced ->
                    _uiState.update { it.copy(resurfacedWisdom = resurfaced) }
                }
        }
    }

    fun onTypeSelected(type: WisdomType) {
        val newType = if (type == WisdomType.ALL) null else type
        _uiState.update { state ->
            state.copy(
                selectedType = newType,
                filteredWisdom = applyFilters(state.allWisdom, newType, state.searchQuery)
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredWisdom = applyFilters(state.allWisdom, state.selectedType, query)
            )
        }
    }

    fun toggleSearch() {
        _uiState.update { state ->
            if (state.isSearchActive) {
                state.copy(
                    isSearchActive = false,
                    searchQuery = "",
                    filteredWisdom = applyFilters(state.allWisdom, state.selectedType, "")
                )
            } else {
                state.copy(isSearchActive = true)
            }
        }
    }

    fun onWisdomSelected(wisdom: SavedWisdomEntity) {
        viewModelScope.launch {
            wisdomRepository.recordWisdomViewed(wisdom.id)
        }
        _uiState.update { it.copy(
            selectedWisdom = wisdom,
            showDetailSheet = true
        )}
    }

    fun dismissDetailSheet() {
        _uiState.update { it.copy(
            selectedWisdom = null,
            showDetailSheet = false
        )}
    }

    fun addUserNote(wisdomId: Long, note: String) {
        viewModelScope.launch {
            wisdomRepository.addUserNote(wisdomId, note)
                .onSuccess {
                    _uiState.update { state ->
                        val updatedWisdom = state.selectedWisdom?.copy(userNote = note)
                        state.copy(selectedWisdom = updatedWisdom)
                    }
                }
                .onError { error ->
                    _uiState.update { it.copy(errorMessage = error.userMessage) }
                }
        }
    }

    fun showUnsaveConfirmation(wisdom: SavedWisdomEntity) {
        _uiState.update { it.copy(
            showUnsaveConfirmation = true,
            wisdomToUnsave = wisdom
        )}
    }

    fun dismissUnsaveConfirmation() {
        _uiState.update { it.copy(
            showUnsaveConfirmation = false,
            wisdomToUnsave = null
        )}
    }

    fun confirmUnsave() {
        val wisdom = _uiState.value.wisdomToUnsave ?: return

        viewModelScope.launch {
            wisdomRepository.removeWisdom(wisdom.id)
                .onSuccess {
                    _uiState.update { it.copy(
                        showUnsaveConfirmation = false,
                        wisdomToUnsave = null,
                        showDetailSheet = false,
                        selectedWisdom = null
                    )}
                }
                .onError { error ->
                    _uiState.update { it.copy(
                        showUnsaveConfirmation = false,
                        wisdomToUnsave = null,
                        errorMessage = error.userMessage
                    )}
                }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun markResurfacedAsShown(wisdomId: Long) {
        viewModelScope.launch {
            wisdomRepository.recordWisdomShown(wisdomId)
        }
    }

    private fun applyFilters(
        wisdom: List<SavedWisdomEntity>,
        type: WisdomType?,
        query: String
    ): List<SavedWisdomEntity> {
        var filtered = wisdom

        // Filter by type
        if (type != null) {
            filtered = filtered.filter { it.type == type.entityType }
        }

        // Filter by search query
        if (query.isNotBlank()) {
            val lowerQuery = query.lowercase()
            filtered = filtered.filter { item ->
                item.content.lowercase().contains(lowerQuery) ||
                item.author?.lowercase()?.contains(lowerQuery) == true ||
                item.secondaryContent?.lowercase()?.contains(lowerQuery) == true ||
                item.tags.lowercase().contains(lowerQuery) ||
                item.userNote?.lowercase()?.contains(lowerQuery) == true
            }
        }

        return filtered
    }
}
