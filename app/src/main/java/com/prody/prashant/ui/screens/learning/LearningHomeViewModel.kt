package com.prody.prashant.ui.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.domain.learning.*
import com.prody.prashant.domain.repository.LearningPathRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Learning Home Screen
 */
data class LearningHomeUiState(
    val isLoading: Boolean = true,
    val activePaths: List<LearningPath> = emptyList(),
    val completedPaths: List<LearningPath> = emptyList(),
    val recommendations: List<PathRecommendation> = emptyList(),
    val allPathTypes: List<PathType> = PathType.values().toList(),
    val learningStats: LearningStats? = null,
    val displayedBadges: List<PathBadge> = emptyList(),
    val errorMessage: String? = null,
    val showRecommendationDialog: PathRecommendation? = null,
    val showPathSelectionSheet: Boolean = false
)

/**
 * ViewModel for Learning Home Screen
 *
 * Manages the main learning dashboard showing active paths,
 * recommendations, and overall progress.
 */
@HiltViewModel
class LearningHomeViewModel @Inject constructor(
    private val repository: LearningPathRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningHomeUiState())
    val uiState: StateFlow<LearningHomeUiState> = _uiState.asStateFlow()

    init {
        loadLearningData()
        generateRecommendations()
    }

    private fun loadLearningData() {
        viewModelScope.launch {
            // Combine multiple flows
            combine(
                repository.observeActivePaths(),
                repository.observeCompletedPaths(),
                repository.observeActiveRecommendations(),
                repository.observeDisplayedBadges(3)
            ) { active, completed, recommendations, badges ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        activePaths = active,
                        completedPaths = completed,
                        recommendations = recommendations,
                        displayedBadges = badges
                    )
                }
            }.collect()
        }

        // Load stats separately
        viewModelScope.launch {
            repository.getLearningStats().onSuccess { stats ->
                _uiState.update { it.copy(learningStats = stats) }
            }
        }
    }

    fun generateRecommendations() {
        viewModelScope.launch {
            repository.generateRecommendations().onSuccess { recommendations ->
                _uiState.update { it.copy(recommendations = recommendations) }
            }
        }
    }

    fun onPathSelected(pathType: PathType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.startPath(pathType)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(isLoading = false, showPathSelectionSheet = false)
                    }
                }
                .onError { error ->
                    _uiState.update { state ->
                        state.copy(isLoading = false, errorMessage = error.userMessage)
                    }
                }
        }
    }

    fun onRecommendationClicked(recommendation: PathRecommendation) {
        _uiState.update { it.copy(showRecommendationDialog = recommendation) }
    }

    fun acceptRecommendation(recommendationId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showRecommendationDialog = null) }
            repository.acceptRecommendation(recommendationId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onError { error ->
                    _uiState.update { state ->
                        state.copy(isLoading = false, errorMessage = error.userMessage)
                    }
                }
        }
    }

    fun dismissRecommendation(recommendationId: Long) {
        viewModelScope.launch {
            repository.dismissRecommendation(recommendationId)
            _uiState.update { it.copy(showRecommendationDialog = null) }
        }
    }

    fun showPathSelection() {
        _uiState.update { it.copy(showPathSelectionSheet = true) }
    }

    fun hidePathSelection() {
        _uiState.update { it.copy(showPathSelectionSheet = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
