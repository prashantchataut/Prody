package com.prody.prashant.ui.screens.wrapped

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.entity.YearlyWrappedEntity
import com.prody.prashant.domain.repository.YearlyWrappedRepository
import com.prody.prashant.domain.wrapped.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for Yearly Wrapped experience.
 *
 * Manages the immersive slideshow presentation, user interactions,
 * and sharing functionality.
 */
@HiltViewModel
class YearlyWrappedViewModel @Inject constructor(
    private val wrappedRepository: YearlyWrappedRepository
) : ViewModel() {

    private val userId = "local"

    private val _uiState = MutableStateFlow(YearlyWrappedUiState())
    val uiState: StateFlow<YearlyWrappedUiState> = _uiState.asStateFlow()

    init {
        loadAvailableYears()
        checkForUnviewedWrapped()
    }

    // ==================== INITIALIZATION ====================

    /**
     * Load wrapped for a specific year
     */
    fun loadWrapped(year: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            wrappedRepository.getWrappedByYear(userId, year)
                .onSuccess { entity ->
                    if (entity != null) {
                        val domainModel = wrappedRepository.entityToDomain(entity)
                        val slides = generateSlides(domainModel)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                currentWrapped = domainModel,
                                slides = slides,
                                currentSlideIndex = 0,
                                totalSlides = slides.size,
                                isPlaying = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Wrapped not found for year $year"
                            )
                        }
                    }
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.userMessage)
                    }
                }
        }
    }

    /**
     * Load the most recent wrapped
     */
    fun loadLatestWrapped() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            wrappedRepository.getLatestWrapped(userId)
                .onSuccess { entity ->
                    if (entity != null) {
                        val domainModel = wrappedRepository.entityToDomain(entity)
                        val slides = generateSlides(domainModel)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                currentWrapped = domainModel,
                                slides = slides,
                                currentSlideIndex = 0,
                                totalSlides = slides.size,
                                isPlaying = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "No wrapped available yet"
                            )
                        }
                    }
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.userMessage)
                    }
                }
        }
    }

    private fun loadAvailableYears() {
        viewModelScope.launch {
            wrappedRepository.observeAvailableYears(userId)
                .collect { years ->
                    _uiState.update { it.copy(availableYears = years) }
                }
        }
    }

    private fun checkForUnviewedWrapped() {
        viewModelScope.launch {
            val count = wrappedRepository.getUnviewedCount(userId)
            _uiState.update { it.copy(hasUnviewedWrapped = count > 0) }
        }
    }

    // ==================== NAVIGATION ====================

    fun nextSlide() {
        val currentIndex = _uiState.value.currentSlideIndex
        val totalSlides = _uiState.value.totalSlides

        if (currentIndex < totalSlides - 1) {
            val newIndex = currentIndex + 1
            _uiState.update {
                it.copy(
                    currentSlideIndex = newIndex,
                    viewedSlides = it.viewedSlides + newIndex
                )
            }

            // Auto-save progress
            updateViewProgress()
        }
    }

    fun previousSlide() {
        val currentIndex = _uiState.value.currentSlideIndex
        if (currentIndex > 0) {
            _uiState.update {
                it.copy(currentSlideIndex = currentIndex - 1)
            }
        }
    }

    fun goToSlide(index: Int) {
        val totalSlides = _uiState.value.totalSlides
        if (index in 0 until totalSlides) {
            _uiState.update {
                it.copy(
                    currentSlideIndex = index,
                    viewedSlides = it.viewedSlides + index
                )
            }
            updateViewProgress()
        }
    }

    fun toggleAutoPlay() {
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    // ==================== INTERACTIONS ====================

    fun markAsViewed() {
        val wrappedId = _uiState.value.currentWrapped?.id ?: return

        viewModelScope.launch {
            val completionPercent = calculateCompletionPercent()
            val slidesViewed = _uiState.value.viewedSlides.sorted().toString()

            wrappedRepository.markAsViewed(
                id = wrappedId,
                completionPercent = completionPercent,
                slidesViewed = slidesViewed
            )

            _uiState.update {
                it.copy(
                    currentWrapped = it.currentWrapped?.copy(
                        isViewed = true,
                        viewedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun toggleFavorite() {
        val wrappedId = _uiState.value.currentWrapped?.id ?: return
        val currentFavorite = _uiState.value.currentWrapped?.isFavorite ?: false

        viewModelScope.launch {
            wrappedRepository.updateFavoriteStatus(wrappedId, !currentFavorite)

            _uiState.update {
                it.copy(
                    currentWrapped = it.currentWrapped?.copy(isFavorite = !currentFavorite)
                )
            }
        }
    }

    fun shareCard(card: ShareableCard) {
        _uiState.update {
            it.copy(
                shareMode = true,
                selectedShareCard = card
            )
        }
    }

    fun dismissShareCard() {
        _uiState.update {
            it.copy(
                shareMode = false,
                selectedShareCard = null
            )
        }
    }

    fun markAsShared() {
        val wrappedId = _uiState.value.currentWrapped?.id ?: return

        viewModelScope.launch {
            wrappedRepository.markAsShared(wrappedId)
        }
    }

    // ==================== GENERATION ====================

    fun generateWrapped(year: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isGenerating = true,
                    generationProgress = 0,
                    generationError = null
                )
            }

            val config = WrappedGenerationConfig(
                year = year,
                includeNarratives = true,
                generateShareableCards = true
            )

            wrappedRepository.generateWrapped(userId, year, config)
                .onSuccess { entity ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            generationProgress = 100
                        )
                    }

                    // Load the newly generated wrapped
                    loadWrapped(year)
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            generationError = error.userMessage
                        )
                    }
                }
        }
    }

    fun checkCanGenerate(year: Int) {
        viewModelScope.launch {
            wrappedRepository.canGenerateWrappedForYear(userId, year)
                .onSuccess { canGenerate ->
                    _uiState.update { it.copy(canGenerateForYear = canGenerate) }
                }
        }
    }

    // ==================== HELPERS ====================

    private fun generateSlides(wrapped: YearlyWrapped): List<WrappedSlide> {
        val slides = mutableListOf<WrappedSlide>()
        var order = 0

        // Opening
        slides.add(WrappedSlide(
            id = "opening",
            type = SlideType.OPENING,
            order = order++,
            autoAdvanceDelay = 0L
        ))

        // Stats overview
        slides.add(WrappedSlide(
            id = "stats_overview",
            type = SlideType.STATS_OVERVIEW,
            order = order++
        ))

        // Writing stats
        if (wrapped.stats.totalEntries > 0) {
            slides.add(WrappedSlide(
                id = "writing_stats",
                type = SlideType.WRITING_STATS,
                order = order++
            ))
        }

        // Engagement stats
        if (wrapped.stats.longestStreak > 0 || wrapped.stats.activeDays > 0) {
            slides.add(WrappedSlide(
                id = "engagement_stats",
                type = SlideType.ENGAGEMENT_STATS,
                order = order++
            ))
        }

        // Learning stats
        if (wrapped.stats.wordsLearned > 0 || wrapped.stats.wordsUsed > 0) {
            slides.add(WrappedSlide(
                id = "learning_stats",
                type = SlideType.LEARNING_STATS,
                order = order++
            ))
        }

        // Mood journey
        slides.add(WrappedSlide(
            id = "mood_journey",
            type = SlideType.MOOD_JOURNEY,
            order = order++
        ))

        // Mood highlights
        if (wrapped.moodJourney.brightestMonth != null || wrapped.moodJourney.mostReflectiveMonth != null) {
            slides.add(WrappedSlide(
                id = "mood_highlights",
                type = SlideType.MOOD_HIGHLIGHTS,
                order = order++
            ))
        }

        // Top themes
        if (wrapped.themes.isNotEmpty()) {
            slides.add(WrappedSlide(
                id = "top_themes",
                type = SlideType.TOP_THEMES,
                order = order++
            ))
        }

        // Growth areas
        if (wrapped.growthAreas.isNotEmpty()) {
            slides.add(WrappedSlide(
                id = "growth_areas",
                type = SlideType.GROWTH_AREAS,
                order = order++
            ))
        }

        // Challenges
        if (wrapped.challenges.isNotEmpty()) {
            slides.add(WrappedSlide(
                id = "challenges",
                type = SlideType.CHALLENGES,
                order = order++
            ))
        }

        // Key moments
        if (wrapped.keyMoments.isNotEmpty()) {
            slides.add(WrappedSlide(
                id = "key_moments",
                type = SlideType.KEY_MOMENTS,
                order = order++
            ))
        }

        // Patterns
        if (wrapped.patterns.isNotEmpty()) {
            slides.add(WrappedSlide(
                id = "patterns",
                type = SlideType.PATTERNS,
                order = order++
            ))
        }

        // Narratives
        if (wrapped.narratives.yearSummary != null) {
            slides.add(WrappedSlide(
                id = "narratives",
                type = SlideType.NARRATIVES,
                order = order++
            ))
        }

        // Looking ahead
        if (wrapped.narratives.lookingAhead != null) {
            slides.add(WrappedSlide(
                id = "looking_ahead",
                type = SlideType.LOOKING_AHEAD,
                order = order++
            ))
        }

        // Shareable cards
        if (wrapped.shareableCards.isNotEmpty()) {
            slides.add(WrappedSlide(
                id = "shareable_cards",
                type = SlideType.SHAREABLE_CARDS,
                order = order++
            ))
        }

        // Celebration
        slides.add(WrappedSlide(
            id = "celebration",
            type = SlideType.CELEBRATION,
            order = order++
        ))

        return slides
    }

    private fun updateViewProgress() {
        val wrappedId = _uiState.value.currentWrapped?.id ?: return
        val completionPercent = calculateCompletionPercent()
        val slidesViewed = _uiState.value.viewedSlides.sorted().toString()

        viewModelScope.launch {
            wrappedRepository.updateViewProgress(wrappedId, completionPercent, slidesViewed)
        }
    }

    private fun calculateCompletionPercent(): Int {
        val totalSlides = _uiState.value.totalSlides
        val viewedSlides = _uiState.value.viewedSlides.size

        return if (totalSlides > 0) {
            ((viewedSlides.toFloat() / totalSlides) * 100).toInt()
        } else 0
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun dismissGenerationError() {
        _uiState.update { it.copy(generationError = null) }
    }
}

/**
 * UI State for Yearly Wrapped experience
 */
data class YearlyWrappedUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Current wrapped being viewed
    val currentWrapped: YearlyWrapped? = null,

    // Slides
    val slides: List<WrappedSlide> = emptyList(),
    val currentSlideIndex: Int = 0,
    val totalSlides: Int = 0,
    val viewedSlides: Set<Int> = emptySet(), // Track which slides have been viewed

    // Playback
    val isPlaying: Boolean = false, // Auto-advance enabled

    // Available years
    val availableYears: List<Int> = emptyList(),
    val hasUnviewedWrapped: Boolean = false,

    // Sharing
    val shareMode: Boolean = false,
    val selectedShareCard: ShareableCard? = null,

    // Generation
    val isGenerating: Boolean = false,
    val generationProgress: Int = 0,
    val generationError: String? = null,
    val canGenerateForYear: Boolean = false
)
