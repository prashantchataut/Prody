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
 * UI State for Path Detail Screen
 */
data class PathDetailUiState(
    val isLoading: Boolean = true,
    val path: LearningPath? = null,
    val lessons: List<Lesson> = emptyList(),
    val currentLesson: Lesson? = null,
    val errorMessage: String? = null,
    val showCompletionCelebration: Boolean = false
)

/**
 * ViewModel for Path Detail Screen
 *
 * Shows detailed path information, lesson list, and progress tracking.
 */
@HiltViewModel
class PathDetailViewModel @Inject constructor(
    private val repository: LearningPathRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pathId: String = checkNotNull(savedStateHandle.get<String>("pathId"))

    private val _uiState = MutableStateFlow(PathDetailUiState())
    val uiState: StateFlow<PathDetailUiState> = _uiState.asStateFlow()

    init {
        loadPathDetails()
    }

    private fun loadPathDetails() {
        viewModelScope.launch {
            combine(
                repository.observePath(pathId),
                repository.observeLessons(pathId)
            ) { path, lessons ->
                val currentLesson = path?.progress?.currentLessonId?.let { currentId ->
                    lessons.find { it.id == currentId }
                } ?: lessons.firstOrNull { !it.isLocked && !it.isCompleted }

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        path = path,
                        lessons = lessons,
                        currentLesson = currentLesson
                    )
                }
            }.collect()
        }
    }

    fun resumePath() {
        viewModelScope.launch {
            repository.resumePath(pathId)
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(showCompletionCelebration = false) }
    }
}
