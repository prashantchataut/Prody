package com.prody.prashant.ui.screens.learning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.domain.learning.*
import com.prody.prashant.domain.repository.LearningPathRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Lesson Screen
 */
data class LessonUiState(
    val isLoading: Boolean = true,
    val lesson: Lesson? = null,
    val content: LessonContent? = null,
    val reflectionText: String = "",
    val userNotes: String = "",
    val quizAnswers: Map<String, Int> = emptyMap(),
    val showQuizResults: Boolean = false,
    val quizScore: Int = 0,
    val showCompletionDialog: Boolean = false,
    val errorMessage: String? = null,
    val isSavingReflection: Boolean = false,
    val meditationTimeRemaining: Int = 0,
    val meditationIsPlaying: Boolean = false
)

/**
 * ViewModel for Lesson Screen
 *
 * Handles lesson content display, user interactions, and completion tracking.
 * Adapts UI based on lesson type (reading, reflection, exercise, etc.)
 */
@HiltViewModel
class LessonViewModel @Inject constructor(
    private val repository: LearningPathRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val lessonId: String = checkNotNull(savedStateHandle.get<String>("lessonId"))
    private val pathId: String = checkNotNull(savedStateHandle.get<String>("pathId"))

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
    }

    private fun loadLesson() {
        viewModelScope.launch {
            repository.getLesson(lessonId)
                .onSuccess { lesson ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            lesson = lesson,
                            content = lesson.content
                        )
                    }
                }
                .onError { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = error.userMessage
                        )
                    }
                }
        }
    }

    fun onReflectionTextChanged(text: String) {
        _uiState.update { it.copy(reflectionText = text) }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(userNotes = notes) }
    }

    fun saveReflection(mood: String? = null) {
        val state = _uiState.value
        val reflectionContent = state.lesson?.content as? LessonContent.Reflection ?: return

        if (state.reflectionText.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please write your reflection") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingReflection = true) }

            repository.saveReflection(
                lessonId = lessonId,
                pathId = pathId,
                promptText = reflectionContent.prompt,
                userResponse = state.reflectionText,
                mood = mood
            ).onSuccess {
                completeLesson()
            }.onError { error ->
                _uiState.update { state ->
                    state.copy(
                        isSavingReflection = false,
                        errorMessage = error.userMessage
                    )
                }
            }
        }
    }

    fun saveNotes() {
        val notes = _uiState.value.userNotes
        if (notes.isBlank()) return

        viewModelScope.launch {
            repository.saveNotes(lessonId, pathId, notes)
        }
    }

    fun onQuizAnswerSelected(questionId: String, answerIndex: Int) {
        _uiState.update { state ->
            state.copy(
                quizAnswers = state.quizAnswers + (questionId to answerIndex)
            )
        }
    }

    fun submitQuiz() {
        val state = _uiState.value
        val quizContent = state.lesson?.content as? LessonContent.Quiz ?: return

        // Calculate score
        var correct = 0
        quizContent.questions.forEach { question ->
            val userAnswer = state.quizAnswers[question.id]
            if (userAnswer == question.correctAnswer) {
                correct++
            }
        }

        val score = (correct.toFloat() / quizContent.questions.size * 100).toInt()

        _uiState.update { it.copy(
            showQuizResults = true,
            quizScore = score
        )}

        // Complete lesson if passed
        if (score >= quizContent.passingScore) {
            completeLesson(score)
        }
    }

    fun completeLesson(quizScore: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.completeLesson(lessonId, pathId, quizScore)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            showCompletionDialog = true
                        )
                    }
                }
                .onError { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = error.userMessage
                        )
                    }
                }
        }
    }

    fun startMeditation(durationMinutes: Int) {
        _uiState.update { state ->
            state.copy(
                meditationTimeRemaining = durationMinutes * 60,
                meditationIsPlaying = true
            )
        }
    }

    fun pauseMeditation() {
        _uiState.update { it.copy(meditationIsPlaying = false) }
    }

    fun resumeMeditation() {
        _uiState.update { it.copy(meditationIsPlaying = true) }
    }

    fun updateMeditationTime(seconds: Int) {
        _uiState.update { it.copy(meditationTimeRemaining = seconds) }
    }

    fun dismissCompletionDialog() {
        _uiState.update { it.copy(showCompletionDialog = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
