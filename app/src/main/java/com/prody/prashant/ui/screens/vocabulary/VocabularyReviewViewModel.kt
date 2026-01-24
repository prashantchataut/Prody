package com.prody.prashant.ui.screens.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.dao.VocabularyLearningDao
import com.prody.prashant.data.local.dao.WordUsageDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.domain.vocabulary.VocabularyCelebrationService
import com.prody.prashant.domain.vocabulary.VocabularySuggestionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * ViewModel for the Vocabulary Review screen.
 *
 * Shows:
 * - Words learned this week
 * - Words used in context (with celebrations)
 * - Words not yet used (gentle nudge)
 * - Vocabulary growth chart
 */
@HiltViewModel
class VocabularyReviewViewModel @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    private val vocabularyLearningDao: VocabularyLearningDao,
    private val wordUsageDao: WordUsageDao,
    private val celebrationService: VocabularyCelebrationService,
    private val suggestionEngine: VocabularySuggestionEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularyReviewUiState())
    val uiState: StateFlow<VocabularyReviewUiState> = _uiState.asStateFlow()

    // Temporary userId - will be replaced with user authentication system
    // TODO: Implement user authentication service and get current userId
    private val userId = "local"

    init {
        loadVocabularyReview()
    }

    private fun loadVocabularyReview() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Get week boundaries
                val weekStart = getWeekStartTimestamp()
                val now = System.currentTimeMillis()

                // Load words learned this week
                val learnedThisWeek = vocabularyDao.getLearnedCountSinceSync(weekStart)

                // Load all learned words
                val allLearnedWords = vocabularyDao.getLearnedWords().first()

                // Load words used in context
                val usedWordIds = wordUsageDao.getUsedWordIds(userId)
                val wordsUsedInContext = allLearnedWords.filter { it.id in usedWordIds }

                // Load words not yet used
                val unusedWordIds = celebrationService.getLearnedButUnusedWords(userId, 20)
                val wordsNotYetUsed = unusedWordIds
                    .mapNotNull { id -> allLearnedWords.find { it.id == id } }

                // Calculate vocabulary growth over time
                val growthData = calculateGrowthData(allLearnedWords)

                // Get words needing practice
                val wordsNeedingPractice = suggestionEngine.getWordsNeedingPractice(userId, 5)

                // Get usage statistics
                val totalWordsUsed = usedWordIds.size
                val totalBonusPoints = wordUsageDao.getTotalBonusPoints(userId).first() ?: 0

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        wordsLearnedThisWeek = learnedThisWeek,
                        wordsUsedInContext = wordsUsedInContext,
                        wordsNotYetUsed = wordsNotYetUsed,
                        wordsNeedingPractice = wordsNeedingPractice,
                        growthData = growthData,
                        totalWordsLearned = allLearnedWords.size,
                        totalWordsUsed = totalWordsUsed,
                        totalBonusPoints = totalBonusPoints,
                        usagePercentage = if (allLearnedWords.isNotEmpty()) {
                            (totalWordsUsed.toFloat() / allLearnedWords.size * 100).toInt()
                        } else 0
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load vocabulary review: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Calculate vocabulary growth data for the chart.
     */
    private suspend fun calculateGrowthData(learnedWords: List<VocabularyEntity>): List<VocabularyGrowthPoint> {
        if (learnedWords.isEmpty()) return emptyList()

        // Group words by week
        val now = Instant.now()
        val weeksAgo = 12 // Show last 12 weeks

        val growthPoints = mutableListOf<VocabularyGrowthPoint>()

        for (weekOffset in (weeksAgo - 1) downTo 0) {
            val weekStart = now.minus((weekOffset + 1).toLong(), ChronoUnit.WEEKS)
            val weekEnd = now.minus(weekOffset.toLong(), ChronoUnit.WEEKS)

            val wordsLearnedInWeek = learnedWords.count { word ->
                word.learnedAt?.let { learnedAt ->
                    val learnedInstant = Instant.ofEpochMilli(learnedAt)
                    learnedInstant.isAfter(weekStart) && learnedInstant.isBefore(weekEnd)
                } ?: false
            }

            val wordsUsedInWeek = wordUsageDao.countUniqueWordsUsedSince(
                userId,
                weekStart.toEpochMilli()
            ) - (if (weekOffset < weeksAgo - 1) {
                wordUsageDao.countUniqueWordsUsedSince(
                    userId,
                    weekEnd.toEpochMilli()
                )
            } else 0)

            growthPoints.add(
                VocabularyGrowthPoint(
                    weekLabel = formatWeekLabel(weekStart),
                    wordsLearned = wordsLearnedInWeek,
                    wordsUsed = wordsUsedInWeek
                )
            )
        }

        return growthPoints
    }

    private fun formatWeekLabel(instant: Instant): String {
        val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        return "${date.month.name.take(3)} ${date.dayOfMonth}"
    }

    private fun getWeekStartTimestamp(): Long {
        val now = Instant.now()
        val weekStart = now.atZone(ZoneId.systemDefault())
            .with(java.time.DayOfWeek.MONDAY)
            .truncatedTo(ChronoUnit.DAYS)
        return weekStart.toInstant().toEpochMilli()
    }

    fun refresh() {
        loadVocabularyReview()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun selectWord(word: VocabularyEntity) {
        _uiState.update { it.copy(selectedWord = word) }
    }

    fun clearSelectedWord() {
        _uiState.update { it.copy(selectedWord = null) }
    }
}

/**
 * UI state for the Vocabulary Review screen.
 */
data class VocabularyReviewUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Statistics
    val wordsLearnedThisWeek: Int = 0,
    val totalWordsLearned: Int = 0,
    val totalWordsUsed: Int = 0,
    val totalBonusPoints: Int = 0,
    val usagePercentage: Int = 0,

    // Word lists
    val wordsUsedInContext: List<VocabularyEntity> = emptyList(),
    val wordsNotYetUsed: List<VocabularyEntity> = emptyList(),
    val wordsNeedingPractice: List<VocabularyEntity> = emptyList(),

    // Growth data
    val growthData: List<VocabularyGrowthPoint> = emptyList(),

    // Selected word for details
    val selectedWord: VocabularyEntity? = null
) {
    val hasData: Boolean
        get() = totalWordsLearned > 0

    val hasWordsUsed: Boolean
        get() = wordsUsedInContext.isNotEmpty()

    val hasUnusedWords: Boolean
        get() = wordsNotYetUsed.isNotEmpty()
}

/**
 * Data point for vocabulary growth chart.
 */
data class VocabularyGrowthPoint(
    val weekLabel: String,
    val wordsLearned: Int,
    val wordsUsed: Int
)
