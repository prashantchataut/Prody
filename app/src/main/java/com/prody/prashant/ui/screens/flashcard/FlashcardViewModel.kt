package com.prody.prashant.ui.screens.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.dao.VocabularyLearningDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.data.local.entity.VocabularyLearningEntity
import com.prody.prashant.domain.learning.ReviewResponse
import com.prody.prashant.domain.learning.SpacedRepetitionEngine
import com.prody.prashant.util.TextToSpeechManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Flashcard review screen.
 */
@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    private val vocabularyLearningDao: VocabularyLearningDao,
    private val spacedRepetitionEngine: SpacedRepetitionEngine,
    private val textToSpeechManager: TextToSpeechManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    private var sessionStartTime: Long = 0L
    private var cardStartTime: Long = 0L

    init {
        loadReviewCards()
    }

    /**
     * Load cards that are due for review.
     */
    fun loadReviewCards() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Get words due for review from the SRS system
                val learningEntries = vocabularyLearningDao.getWordsDueForReviewWithLimit(
                    currentTime = System.currentTimeMillis(),
                    limit = 20
                )

                val wordIds = learningEntries.map { it.wordId }
                val words = wordIds.mapNotNull { vocabularyDao.getWordById(it) }

                // If no SRS entries, get some unlearned words
                val finalCards = if (words.isEmpty()) {
                    getNewWordsForSession()
                } else {
                    words
                }

                sessionStartTime = System.currentTimeMillis()
                cardStartTime = System.currentTimeMillis()

                _uiState.update {
                    it.copy(
                        cards = finalCards,
                        currentIndex = 0,
                        isLoading = false,
                        sessionComplete = finalCards.isEmpty(),
                        learningEntries = learningEntries.associateBy { entry -> entry.wordId }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load cards"
                    )
                }
            }
        }
    }

    private suspend fun getNewWordsForSession(): List<VocabularyEntity> {
        // Get words that haven't been marked as learned yet
        val unlearnedWords = mutableListOf<VocabularyEntity>()
        repeat(10) {
            vocabularyDao.getRandomUnlearnedWord()?.let { word ->
                if (unlearnedWords.none { it.id == word.id }) {
                    unlearnedWords.add(word)
                    // Initialize learning entry for new words
                    initializeLearningEntry(word.id)
                }
            }
        }
        return unlearnedWords
    }

    private suspend fun initializeLearningEntry(wordId: Long) {
        val existing = vocabularyLearningDao.getLearningForWord(wordId)
        if (existing == null) {
            val newEntry = spacedRepetitionEngine.createInitialLearningEntity(wordId)
            vocabularyLearningDao.insertLearning(newEntry)
        }
    }

    /**
     * Handle when user indicates they know the word (swipe right).
     */
    fun onKnow() {
        processAnswer(ReviewResponse.CORRECT)
    }

    /**
     * Handle when user indicates they don't know the word (swipe left).
     */
    fun onDontKnow() {
        processAnswer(ReviewResponse.WRONG_EASY)
    }

    /**
     * Handle skip (swipe up).
     */
    fun onSkip() {
        _uiState.update {
            it.copy(
                currentIndex = it.currentIndex + 1,
                skippedCount = it.skippedCount + 1,
                sessionComplete = it.currentIndex + 1 >= it.cards.size
            )
        }
        cardStartTime = System.currentTimeMillis()
    }

    /**
     * Handle "I knew it perfectly" action.
     */
    fun onPerfect() {
        processAnswer(ReviewResponse.PERFECT)
    }

    /**
     * Handle "I knew it but it was hard" action.
     */
    fun onHard() {
        processAnswer(ReviewResponse.HARD)
    }

    private fun processAnswer(response: ReviewResponse) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState.currentIndex >= currentState.cards.size) return@launch

                val currentWord = currentState.cards[currentState.currentIndex]
                val responseTimeMs = System.currentTimeMillis() - cardStartTime

                // Get or create learning entry
                val learningEntry = currentState.learningEntries[currentWord.id]
                    ?: vocabularyLearningDao.getLearningForWord(currentWord.id)
                    ?: spacedRepetitionEngine.createInitialLearningEntity(currentWord.id)

                // Calculate review result using SM-2 algorithm
                val quality = spacedRepetitionEngine.qualityFromResponse(response)
                val reviewResult = spacedRepetitionEngine.calculateNextReview(quality, learningEntry)

                // Apply the result to the learning entry
                val updatedLearning = spacedRepetitionEngine.applyReviewResult(
                    currentLearning = learningEntry,
                    result = reviewResult,
                    responseTimeMs = responseTimeMs
                )

                // Save to database
                vocabularyLearningDao.updateLearning(updatedLearning)

                // Update mastery level in vocabulary entity
                vocabularyDao.updateReviewProgress(
                    id = currentWord.id,
                    reviewedAt = System.currentTimeMillis(),
                    nextReview = reviewResult.nextReviewDate,
                    mastery = (updatedLearning.accuracy).toInt().coerceIn(0, 100)
                )

                // Update UI state
                val isCorrect = quality >= 3
                _uiState.update {
                    it.copy(
                        currentIndex = it.currentIndex + 1,
                        knownCount = if (isCorrect) it.knownCount + 1 else it.knownCount,
                        unknownCount = if (!isCorrect) it.unknownCount + 1 else it.unknownCount,
                        sessionComplete = it.currentIndex + 1 >= it.cards.size,
                        learningEntries = it.learningEntries + (currentWord.id to updatedLearning)
                    )
                }

                cardStartTime = System.currentTimeMillis()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to save review. Please try again.")
                }
            }
        }
    }

    /**
     * Speak the word using TTS.
     */
    fun speakWord(word: String) {
        textToSpeechManager.speak(word)
    }

    /**
     * Undo the last action.
     */
    fun undoLastAction() {
        _uiState.update {
            if (it.currentIndex > 0) {
                it.copy(
                    currentIndex = it.currentIndex - 1,
                    sessionComplete = false
                )
            } else {
                it
            }
        }
    }

    /**
     * Restart the session with the same cards.
     */
    fun restartSession() {
        _uiState.update {
            it.copy(
                currentIndex = 0,
                knownCount = 0,
                unknownCount = 0,
                skippedCount = 0,
                sessionComplete = false
            )
        }
        sessionStartTime = System.currentTimeMillis()
        cardStartTime = System.currentTimeMillis()
    }

    /**
     * Get session duration in minutes.
     */
    fun getSessionDurationMinutes(): Int {
        val durationMs = System.currentTimeMillis() - sessionStartTime
        return (durationMs / 60000).toInt()
    }

    /**
     * Clear any error state.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        // Stop any ongoing speech but don't shutdown TTS since it's a singleton
        // managed at the application level
        textToSpeechManager.stop()
    }
}

/**
 * UI state for the flashcard screen.
 */
data class FlashcardUiState(
    val cards: List<VocabularyEntity> = emptyList(),
    val currentIndex: Int = 0,
    val knownCount: Int = 0,
    val unknownCount: Int = 0,
    val skippedCount: Int = 0,
    val isLoading: Boolean = false,
    val sessionComplete: Boolean = false,
    val error: String? = null,
    val learningEntries: Map<Long, VocabularyLearningEntity> = emptyMap()
) {
    val currentCard: VocabularyEntity?
        get() = cards.getOrNull(currentIndex)

    val progress: Float
        get() = if (cards.isNotEmpty()) currentIndex.toFloat() / cards.size else 0f

    val totalReviewed: Int
        get() = knownCount + unknownCount + skippedCount

    val accuracy: Float
        get() = if (totalReviewed > 0) (knownCount.toFloat() / (knownCount + unknownCount)) * 100 else 0f
}
