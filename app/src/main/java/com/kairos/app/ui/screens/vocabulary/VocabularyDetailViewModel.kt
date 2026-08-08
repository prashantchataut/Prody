package com.kairos.app.ui.screens.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.app.data.auth.UserIdProvider
import com.kairos.app.data.local.dao.VocabularyDao
import com.kairos.app.data.local.entity.VocabularyEntity
import com.kairos.app.domain.recommendation.ContentInteractionType
import com.kairos.app.domain.recommendation.DailyContentType
import com.kairos.app.domain.repository.DailyPlanRepository
import com.kairos.app.util.TextToSpeechManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class VocabularyDetailUiState(
    val word: VocabularyEntity? = null,
    val isLoading: Boolean = true,
    val isSpeaking: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VocabularyDetailViewModel @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    private val dailyPlanRepository: DailyPlanRepository,
    private val userIdProvider: UserIdProvider,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularyDetailUiState())
    val uiState: StateFlow<VocabularyDetailUiState> = _uiState.asStateFlow()

    fun loadWord(wordId: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                vocabularyDao.observeWordById(wordId).collect { word ->
                    _uiState.update {
                        it.copy(
                            word = word,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load word details") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retry(wordId: Long) {
        loadWord(wordId)
    }

    /**
     * Save or un-save this word. Saving is the user's explicit "I like this" signal
     * and feeds the recommendation profile, so similar words surface first.
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            val word = _uiState.value.word ?: return@launch
            val saving = !word.isFavorite
            vocabularyDao.updateWord(word.copy(isFavorite = saving))
            runCatching {
                dailyPlanRepository.recordInteraction(
                    userId = userIdProvider.getUserId(),
                    localDate = LocalDate.now(),
                    type = DailyContentType.VOCABULARY,
                    contentId = word.id,
                    interaction = if (saving) ContentInteractionType.SAVED else ContentInteractionType.UNSAVED,
                    category = word.category,
                    sourceKey = word.partOfSpeech.ifBlank { "vocabulary" },
                    difficulty = word.difficulty
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
