package com.prody.prashant.ui.screens.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.domain.gamification.WisdomQuestEngine
import com.prody.prashant.util.TextToSpeechManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Wisdom Quest challenge states
 */
sealed class WisdomQuestState {
    /** No challenge active - shows "Start Challenge" button */
    data object Idle : WisdomQuestState()
    /** Challenge is being generated */
    data object Loading : WisdomQuestState()
    /** Active challenge awaiting user response */
    data class Active(
        val challenge: WisdomQuestEngine.WisdomChallenge,
        val currentStreak: Int,
        val dailyFocus: WisdomQuestEngine.DailyFocus?
    ) : WisdomQuestState()
    /** Challenge completed - showing result */
    data class Result(val result: WisdomQuestEngine.QuestResult) : WisdomQuestState()
    /** User needs to set daily focus first */
    data object NeedsDailyFocus : WisdomQuestState()
}

data class VocabularyDetailUiState(
    val word: VocabularyEntity? = null,
    val isLoading: Boolean = true,
    val isSpeaking: Boolean = false,
    val wisdomQuestState: WisdomQuestState = WisdomQuestState.Idle
)

@HiltViewModel
class VocabularyDetailViewModel @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    private val ttsManager: TextToSpeechManager,
    private val wisdomQuestEngine: WisdomQuestEngine
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

    // ==========================================================================
    // WISDOM QUEST - Active Recall Challenge System
    // ==========================================================================

    /**
     * Starts a new Wisdom Quest challenge for the current word.
     * Replaces the boring "Mark as Learned" with an engaging micro-challenge.
     */
    fun startWisdomQuest() {
        viewModelScope.launch {
            val word = _uiState.value.word ?: return@launch

            // Check if daily focus is set
            if (wisdomQuestEngine.needsDailyFocus()) {
                _uiState.update { it.copy(wisdomQuestState = WisdomQuestState.NeedsDailyFocus) }
                return@launch
            }

            _uiState.update { it.copy(wisdomQuestState = WisdomQuestState.Loading) }

            try {
                val challenge = wisdomQuestEngine.generateChallenge(word)
                val currentStreak = wisdomQuestEngine.getCurrentStreak()
                val dailyFocus = wisdomQuestEngine.getCurrentDailyFocus()

                _uiState.update {
                    it.copy(
                        wisdomQuestState = WisdomQuestState.Active(
                            challenge = challenge,
                            currentStreak = currentStreak,
                            dailyFocus = dailyFocus
                        )
                    )
                }
            } catch (e: Exception) {
                // Fallback to idle state on error
                _uiState.update { it.copy(wisdomQuestState = WisdomQuestState.Idle) }
            }
        }
    }

    /**
     * Submits the user's answer to the current challenge.
     */
    fun submitWisdomQuestAnswer(answer: String) {
        viewModelScope.launch {
            val currentState = _uiState.value.wisdomQuestState
            if (currentState !is WisdomQuestState.Active) return@launch

            val result = wisdomQuestEngine.validateAnswer(
                challenge = currentState.challenge,
                userAnswer = answer
            )

            _uiState.update {
                it.copy(wisdomQuestState = WisdomQuestState.Result(result))
            }
        }
    }

    /**
     * Skips the current challenge (no XP awarded, streak reset).
     */
    fun skipWisdomQuest() {
        _uiState.update { it.copy(wisdomQuestState = WisdomQuestState.Idle) }
    }

    /**
     * Continues after seeing the result (return to idle or start new challenge).
     */
    fun continueAfterResult() {
        _uiState.update { it.copy(wisdomQuestState = WisdomQuestState.Idle) }
    }

    /**
     * Sets the daily focus category for 2x XP bonus.
     */
    fun setDailyFocus(focus: WisdomQuestEngine.DailyFocus) {
        viewModelScope.launch {
            wisdomQuestEngine.setDailyFocus(focus)
            // After setting focus, start the challenge
            startWisdomQuest()
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
