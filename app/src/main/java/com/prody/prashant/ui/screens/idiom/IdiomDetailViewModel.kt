package com.prody.prashant.ui.screens.idiom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.IdiomDao
import com.prody.prashant.data.local.entity.IdiomEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Idiom Detail ViewModel
 *
 * Manages the state for displaying detailed information about an idiom.
 * Supports:
 * - Loading idiom by ID
 * - Toggling favorite status
 * - Error handling with retry capability
 */
@HiltViewModel
class IdiomDetailViewModel @Inject constructor(
    private val idiomDao: IdiomDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdiomDetailUiState())
    val uiState: StateFlow<IdiomDetailUiState> = _uiState.asStateFlow()

    /**
     * Load idiom details by ID
     */
    fun loadIdiom(idiomId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val idiom = idiomDao.getIdiomById(idiomId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        idiom = idiom,
                        error = if (idiom == null) "Idiom not found" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load idiom: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Toggle the favorite status of the current idiom
     */
    fun toggleFavorite() {
        val currentIdiom = _uiState.value.idiom ?: return

        viewModelScope.launch {
            try {
                val newFavoriteStatus = !currentIdiom.isFavorite
                idiomDao.updateFavoriteStatus(currentIdiom.id, newFavoriteStatus)

                _uiState.update {
                    it.copy(
                        idiom = currentIdiom.copy(isFavorite = newFavoriteStatus)
                    )
                }
            } catch (e: Exception) {
                // Silent failure - favorite toggle is non-critical
            }
        }
    }

    /**
     * Retry loading the idiom after an error
     */
    fun retry(idiomId: Long) {
        loadIdiom(idiomId)
    }
}

/**
 * UI State for Idiom Detail Screen
 */
data class IdiomDetailUiState(
    val isLoading: Boolean = false,
    val idiom: IdiomEntity? = null,
    val error: String? = null
)
