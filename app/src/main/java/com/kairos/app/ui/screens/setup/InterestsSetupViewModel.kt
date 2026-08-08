package com.kairos.app.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.app.data.local.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterestsSetupUiState(
    val isLoading: Boolean = true,
    val saved: Boolean = false,
    val difficulty: Int = 3,
    val sessionSize: Int = 5,
    val wordCategories: Set<String> = emptySet(),
    val quoteCategories: Set<String> = emptySet()
)

/**
 * Loads and saves the user's content interests — the same choices offered during
 * onboarding, available again from Settings so taste can change over time.
 */
@HiltViewModel
class InterestsSetupViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(InterestsSetupUiState())
    val uiState: StateFlow<InterestsSetupUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val difficulty = runCatching { preferencesManager.vocabularyDifficulty.first() }.getOrDefault(3)
            val sessionSize = runCatching { preferencesManager.practiceSessionSize.first() }.getOrDefault(5)
            val wordCategories = runCatching { preferencesManager.preferredWordCategories.first() }
                .getOrDefault(emptySet())
            val quoteCategories = runCatching { preferencesManager.selectedWisdomCategories.first() }
                .getOrDefault(emptySet())
            _uiState.update {
                it.copy(
                    isLoading = false,
                    difficulty = difficulty.coerceIn(1, 5),
                    sessionSize = sessionSize.coerceIn(3, 20),
                    wordCategories = wordCategories,
                    quoteCategories = quoteCategories
                )
            }
        }
    }

    fun setDifficulty(difficulty: Int) {
        _uiState.update { it.copy(difficulty = difficulty.coerceIn(1, 5)) }
    }

    fun setSessionSize(size: Int) {
        _uiState.update { it.copy(sessionSize = size.coerceIn(3, 20)) }
    }

    fun toggleWordCategory(key: String) {
        _uiState.update { state ->
            val current = state.wordCategories
            val updated = if (key in current) {
                if (current.size > 1) current - key else current
            } else {
                current + key
            }
            state.copy(wordCategories = updated)
        }
    }

    fun toggleQuoteCategory(key: String) {
        _uiState.update { state ->
            val current = state.quoteCategories
            val updated = if (key in current) {
                if (current.size > 1) current - key else current
            } else {
                current + key
            }
            state.copy(quoteCategories = updated)
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.isLoading) return
        viewModelScope.launch {
            runCatching {
                preferencesManager.setVocabularyDifficulty(state.difficulty)
                preferencesManager.setPracticeSessionSize(state.sessionSize)
                preferencesManager.setPreferredWordCategories(state.wordCategories)
                preferencesManager.setSelectedWisdomCategories(state.quoteCategories)
            }
            _uiState.update { it.copy(saved = true) }
        }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(saved = false) }
    }
}
