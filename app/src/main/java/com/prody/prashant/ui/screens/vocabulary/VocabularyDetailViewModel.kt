package com.prody.prashant.ui.screens.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.util.TextToSpeechManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VocabularyDetailUiState(
    val word: VocabularyEntity? = null,
    val isLoading: Boolean = true,
    val isSpeaking: Boolean = false
)

@HiltViewModel
class VocabularyDetailViewModel @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularyDetailUiState())
    val uiState: StateFlow<VocabularyDetailUiState> = _uiState.asStateFlow()

    fun loadWord(wordId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            vocabularyDao.observeWordById(wordId).collect { word ->
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

    // Text-to-Speech functions
    val isTtsInitialized: StateFlow<Boolean> = ttsManager.isInitialized
    val isSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking

    fun speakWord() {
        _uiState.value.word?.let { word ->
            ttsManager.speak(word.word)
        }
    }

    fun speakPronunciation() {
        _uiState.value.word?.let { word ->
            // Speak the word followed by pronunciation guide if available
            val textToSpeak = if (word.pronunciation.isNotBlank()) {
                "${word.word}. Pronunciation: ${word.pronunciation}"
            } else {
                word.word
            }
            ttsManager.speak(textToSpeak)
        }
    }

    fun speakDefinition() {
        _uiState.value.word?.let { word ->
            ttsManager.speak("${word.word}. ${word.definition}")
        }
    }

    fun speakExample() {
        _uiState.value.word?.let { word ->
            if (word.exampleSentence.isNotBlank()) {
                ttsManager.speak("Example: ${word.exampleSentence}")
            }
        }
    }

    fun speakAll() {
        _uiState.value.word?.let { word ->
            val fullText = buildString {
                append(word.word)
                append(". ")
                if (word.partOfSpeech.isNotBlank()) {
                    append("${word.partOfSpeech}. ")
                }
                append(word.definition)
                if (word.exampleSentence.isNotBlank()) {
                    append(". Example: ${word.exampleSentence}")
                }
            }
            ttsManager.speak(fullText)
        }
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun setSpeechRate(rate: Float) {
        ttsManager.setSpeechRate(rate)
    }

    fun setPitch(pitch: Float) {
        ttsManager.setPitch(pitch)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
