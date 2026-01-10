package com.prody.prashant.ui.screens.digest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.entity.WeeklyDigestEntity
import com.prody.prashant.domain.repository.WeeklyDigestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Parsed mood distribution for display
 */
data class MoodDistributionItem(
    val mood: String,
    val count: Int,
    val percentage: Float
)

/**
 * UI State for Weekly Digest
 */
data class WeeklyDigestUiState(
    val isLoading: Boolean = true,
    val latestDigest: WeeklyDigestEntity? = null,
    val allDigests: List<WeeklyDigestEntity> = emptyList(),
    val selectedDigest: WeeklyDigestEntity? = null,
    val hasUnreadDigests: Boolean = false,
    val unreadCount: Int = 0,
    // Parsed data for display
    val weekRangeFormatted: String = "",
    val moodDistribution: List<MoodDistributionItem> = emptyList(),
    val topThemes: List<String> = emptyList(),
    val patterns: List<String> = emptyList(),
    // Generation
    val isGenerating: Boolean = false,
    val canGenerateDigest: Boolean = false,
    val lastGeneratedDate: LocalDate? = null,
    // Error handling
    val errorMessage: String? = null,
    val successMessage: String? = null,
    // Navigation
    val highlightEntryId: Long? = null,
    val shouldNavigateToEntry: Boolean = false
)

@HiltViewModel
class WeeklyDigestViewModel @Inject constructor(
    private val weeklyDigestRepository: WeeklyDigestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyDigestUiState())
    val uiState: StateFlow<WeeklyDigestUiState> = _uiState.asStateFlow()

    private val userId = "local"

    init {
        loadLatestDigest()
        loadAllDigests()
        checkCanGenerateDigest()
    }

    private fun loadLatestDigest() {
        viewModelScope.launch {
            weeklyDigestRepository.observeLatestDigest(userId)
                .catch { e ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load weekly digest"
                    )}
                }
                .collect { digest ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            latestDigest = digest,
                            selectedDigest = state.selectedDigest ?: digest,
                            weekRangeFormatted = digest?.let { formatWeekRange(it) } ?: "",
                            moodDistribution = digest?.let { parseMoodDistribution(it.moodDistribution) } ?: emptyList(),
                            topThemes = digest?.topThemes?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
                            patterns = digest?.recurringPatterns?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
                            highlightEntryId = digest?.highlightEntryId
                        )
                    }
                }
        }
    }

    private fun loadAllDigests() {
        viewModelScope.launch {
            weeklyDigestRepository.getAllDigests(userId)
                .collect { digests ->
                    val unreadCount = digests.count { !it.isRead }
                    _uiState.update { it.copy(
                        allDigests = digests,
                        hasUnreadDigests = unreadCount > 0,
                        unreadCount = unreadCount
                    )}
                }
        }
    }

    private fun checkCanGenerateDigest() {
        viewModelScope.launch {
            val hasDigest = weeklyDigestRepository.hasDigestForCurrentWeek(userId)
            _uiState.update { it.copy(canGenerateDigest = !hasDigest) }
        }
    }

    fun selectDigest(digest: WeeklyDigestEntity) {
        _uiState.update { state ->
            state.copy(
                selectedDigest = digest,
                weekRangeFormatted = formatWeekRange(digest),
                moodDistribution = parseMoodDistribution(digest.moodDistribution),
                topThemes = digest.topThemes.split(",").filter { it.isNotBlank() },
                patterns = digest.recurringPatterns.split(",").filter { it.isNotBlank() },
                highlightEntryId = digest.highlightEntryId
            )
        }

        // Mark as read
        if (!digest.isRead) {
            markAsRead(digest.id)
        }
    }

    fun markAsRead(digestId: Long) {
        viewModelScope.launch {
            weeklyDigestRepository.markDigestAsRead(digestId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            weeklyDigestRepository.markAllAsRead(userId)
        }
    }

    fun generateWeeklyDigest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }

            weeklyDigestRepository.generateWeeklyDigest(userId)
                .onSuccess { digest ->
                    _uiState.update { it.copy(
                        isGenerating = false,
                        latestDigest = digest,
                        selectedDigest = digest,
                        canGenerateDigest = false,
                        lastGeneratedDate = LocalDate.now(),
                        successMessage = "Weekly digest generated!"
                    )}
                }
                .onError { error ->
                    _uiState.update { it.copy(
                        isGenerating = false,
                        errorMessage = error.userMessage
                    )}
                }
        }
    }

    fun navigateToHighlightEntry() {
        val entryId = _uiState.value.highlightEntryId ?: return
        _uiState.update { it.copy(shouldNavigateToEntry = true) }
    }

    fun clearNavigation() {
        _uiState.update { it.copy(shouldNavigateToEntry = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun dismissSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadLatestDigest()
        loadAllDigests()
        checkCanGenerateDigest()
    }

    // ==================== HELPERS ====================

    private fun formatWeekRange(digest: WeeklyDigestEntity): String {
        val startDate = Instant.ofEpochMilli(digest.weekStartDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val endDate = Instant.ofEpochMilli(digest.weekEndDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val formatter = DateTimeFormatter.ofPattern("MMM d")
        val yearFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

        return if (startDate.year == endDate.year) {
            "${startDate.format(formatter)} - ${endDate.format(yearFormatter)}"
        } else {
            "${startDate.format(yearFormatter)} - ${endDate.format(yearFormatter)}"
        }
    }

    private fun parseMoodDistribution(distributionString: String): List<MoodDistributionItem> {
        if (distributionString.isBlank()) return emptyList()

        val items = distributionString.split(",")
            .mapNotNull { item ->
                val parts = item.split(":")
                if (parts.size == 2) {
                    val mood = parts[0].trim()
                    val count = parts[1].trim().toIntOrNull() ?: 0
                    Pair(mood, count)
                } else null
            }

        val total = items.sumOf { it.second }.toFloat()
        if (total == 0f) return emptyList()

        return items.map { (mood, count) ->
            MoodDistributionItem(
                mood = mood,
                count = count,
                percentage = count / total
            )
        }.sortedByDescending { it.count }
    }

    fun getPatternDisplayName(pattern: String): String {
        return when (pattern) {
            "morning_writer" -> "Early Bird Writer"
            "evening_reflector" -> "Evening Reflector"
            "deep_thinker" -> "Deep Thinker"
            "concise_reflector" -> "Concise Reflector"
            "consistent_journaler" -> "Consistent Journaler"
            else -> pattern.replace("_", " ").replaceFirstChar { it.uppercase() }
        }
    }

    fun getThemeDisplayName(theme: String): String {
        return theme.replaceFirstChar { it.uppercase() }
    }

    fun getMoodTrendDescription(trend: String): String {
        return when (trend) {
            "improving" -> "Your mood has been getting better"
            "declining" -> "You've been going through challenges"
            "stable" -> "Your emotional state has been consistent"
            else -> "Your emotional journey continues"
        }
    }

    fun getChangeDescription(percent: Int): String {
        return when {
            percent > 50 -> "Significant increase"
            percent > 20 -> "Moderate increase"
            percent > 0 -> "Slight increase"
            percent == 0 -> "Same as last week"
            percent > -20 -> "Slight decrease"
            percent > -50 -> "Moderate decrease"
            else -> "Significant decrease"
        }
    }
}
