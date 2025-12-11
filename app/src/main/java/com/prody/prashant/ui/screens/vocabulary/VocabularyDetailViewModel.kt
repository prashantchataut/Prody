package com.prody.prashant.ui.screens.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VocabularyDetailUiState(
    val word: VocabularyEntity? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class VocabularyDetailViewModel @Inject constructor(
    private val vocabularyDao: VocabularyDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularyDetailUiState())
    val uiState: StateFlow<VocabularyDetailUiState> = _uiState.asStateFlow()

    fun loadWord(wordId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            vocabularyDao.getWordById(wordId).collect { word ->
                _uiState.update {
                    it.copy(
                        word = word,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.word?.let { word ->
                vocabularyDao.updateWord(
                    word.copy(isFavorite = !word.isFavorite)
                )
            }
        }
    }

    fun markAsLearned() {
        viewModelScope.launch {
            _uiState.value.word?.let { word ->
                vocabularyDao.updateWord(
                    word.copy(
                        isLearned = true,
                        learnedAt = System.currentTimeMillis(),
                        masteryLevel = maxOf(word.masteryLevel, 3)
                    )
                )
            }
        }
    }
}
