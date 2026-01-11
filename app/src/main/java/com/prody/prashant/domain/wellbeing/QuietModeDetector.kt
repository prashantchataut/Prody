package com.prody.prashant.domain.wellbeing

import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.model.Mood
import javax.inject.Inject
import javax.inject.Singleton

/**
 * QuietModeDetector - Analyzes journal entries for stress patterns
 *
 * This detector examines recent journal entries to identify signs of overwhelm,
 * stress, or emotional distress. It's designed to be gentle and non-intrusive,
 * using multiple signals to determine if the user might benefit from a simplified
 * interface.
 *
 * Detection Criteria:
 * 1. Stress Keywords: overwhelmed, anxious, exhausted, stressed, can't cope, too much, etc.
 * 2. Mood Patterns: Multiple recent negative moods (anxious, sad, confused)
 * 3. Mood Trends: Declining mood intensity over time
 * 4. Content Intensity: High word count with negative sentiment
 *
 * Philosophy:
 * - Never judgmental, always caring
 * - Uses multiple signals to avoid false positives
 * - Respects user autonomy (suggestion, never forced)
 */
@Singleton
class QuietModeDetector @Inject constructor() {

    companion object {
        // Stress keywords that indicate overwhelm
        private val STRESS_KEYWORDS = setOf(
            "overwhelmed", "overwhelming", "too much", "can't cope", "can't handle",
            "exhausted", "drained", "burnt out", "burnout", "breaking down",
            "anxious", "anxiety", "stressed", "stress", "pressure",
            "drowning", "suffocating", "crushed", "breaking", "crumbling",
            "giving up", "can't do this", "too hard", "impossible",
            "collapsing", "falling apart", "losing it", "can't breathe",
            "overloaded", "swamped", "buried", "trapped", "stuck"
        )

        // Negative moods that indicate distress
        private val NEGATIVE_MOODS = setOf(
            Mood.ANXIOUS,
            Mood.SAD,
            Mood.CONFUSED
        )

        // Minimum number of entries to analyze
        private const val MIN_ENTRIES_FOR_ANALYSIS = 3

        // Threshold for stress keyword matches
        private const val STRESS_KEYWORD_THRESHOLD = 2

        // Threshold for negative mood count
        private const val NEGATIVE_MOOD_THRESHOLD = 3

        // Days to look back for pattern analysis
        private const val ANALYSIS_WINDOW_DAYS = 7
    }

    /**
     * Analyzes recent journal entries to detect stress patterns.
     *
     * @param recentEntries Recent journal entries (should be from last 7 days)
     * @return StressAnalysisResult containing detection results
     */
    fun analyzeStressPatterns(recentEntries: List<JournalEntryEntity>): StressAnalysisResult {
        if (recentEntries.size < MIN_ENTRIES_FOR_ANALYSIS) {
            return StressAnalysisResult(
                shouldSuggestQuietMode = false,
                reason = "Insufficient entries for analysis",
                stressScore = 0.0,
                detectedSignals = emptyList()
            )
        }

        val signals = mutableListOf<String>()
        var stressScore = 0.0

        // 1. Analyze stress keywords in content
        val stressKeywordMatches = analyzeStressKeywords(recentEntries)
        if (stressKeywordMatches >= STRESS_KEYWORD_THRESHOLD) {
            stressScore += 30.0
            signals.add("Detected stress-related keywords in recent entries")
        }

        // 2. Analyze mood patterns
        val negativeMoodCount = analyzeNegativeMoods(recentEntries)
        if (negativeMoodCount >= NEGATIVE_MOOD_THRESHOLD) {
            stressScore += 35.0
            signals.add("Multiple negative moods detected")
        }

        // 3. Analyze mood intensity trends
        val intensityTrend = analyzeMoodIntensityTrend(recentEntries)
        if (intensityTrend == IntensityTrend.DECLINING) {
            stressScore += 20.0
            signals.add("Declining mood intensity trend")
        }

        // 4. Check for high-intensity negative entries
        val hasHighIntensityNegative = hasHighIntensityNegativeEntries(recentEntries)
        if (hasHighIntensityNegative) {
            stressScore += 15.0
            signals.add("High-intensity negative emotions detected")
        }

        // Determine if we should suggest quiet mode
        // We want to be thoughtful, not trigger-happy
        val shouldSuggest = stressScore >= 50.0 && signals.size >= 2

        return StressAnalysisResult(
            shouldSuggestQuietMode = shouldSuggest,
            reason = if (shouldSuggest) {
                "Recent entries show signs of stress: ${signals.joinToString(", ")}"
            } else {
                "No significant stress patterns detected"
            },
            stressScore = stressScore,
            detectedSignals = signals
        )
    }

    /**
     * Counts stress keyword occurrences in recent entries.
     */
    private fun analyzeStressKeywords(entries: List<JournalEntryEntity>): Int {
        var count = 0
        entries.forEach { entry ->
            val contentLower = entry.content.lowercase()
            val hasStressKeyword = STRESS_KEYWORDS.any { keyword ->
                contentLower.contains(keyword)
            }
            if (hasStressKeyword) {
                count++
            }
        }
        return count
    }

    /**
     * Counts entries with negative moods.
     */
    private fun analyzeNegativeMoods(entries: List<JournalEntryEntity>): Int {
        return entries.count { entry ->
            val mood = try {
                Mood.fromString(entry.mood)
            } catch (e: Exception) {
                null
            }
            mood in NEGATIVE_MOODS
        }
    }

    /**
     * Analyzes the trend in mood intensity over recent entries.
     */
    private fun analyzeMoodIntensityTrend(entries: List<JournalEntryEntity>): IntensityTrend {
        if (entries.size < 3) return IntensityTrend.STABLE

        // Sort by creation date (oldest first)
        val sortedEntries = entries.sortedBy { it.createdAt }

        // Calculate average intensity for first half vs second half
        val midPoint = sortedEntries.size / 2
        val firstHalf = sortedEntries.take(midPoint)
        val secondHalf = sortedEntries.drop(midPoint)

        val firstAverage = firstHalf.map { it.moodIntensity }.average()
        val secondAverage = secondHalf.map { it.moodIntensity }.average()

        return when {
            secondAverage < firstAverage - 1.5 -> IntensityTrend.DECLINING
            secondAverage > firstAverage + 1.5 -> IntensityTrend.IMPROVING
            else -> IntensityTrend.STABLE
        }
    }

    /**
     * Checks if there are high-intensity negative entries.
     */
    private fun hasHighIntensityNegativeEntries(entries: List<JournalEntryEntity>): Boolean {
        return entries.any { entry ->
            val mood = try {
                Mood.fromString(entry.mood)
            } catch (e: Exception) {
                null
            }
            mood in NEGATIVE_MOODS && entry.moodIntensity >= 7
        }
    }

    /**
     * Checks if enough time has passed since last suggestion to avoid nagging.
     *
     * @param lastSuggestedAt Timestamp of last suggestion (milliseconds)
     * @return true if enough time has passed (at least 3 days)
     */
    fun shouldShowSuggestion(lastSuggestedAt: Long): Boolean {
        if (lastSuggestedAt == 0L) return true

        val threeDaysInMillis = 3L * 24 * 60 * 60 * 1000
        val timeSinceLastSuggestion = System.currentTimeMillis() - lastSuggestedAt

        return timeSinceLastSuggestion >= threeDaysInMillis
    }

    /**
     * Checks if it's time for a gentle check-in to see if user wants to exit quiet mode.
     *
     * @param enabledAt Timestamp when quiet mode was enabled
     * @param lastCheckInAt Timestamp of last check-in (0 if never)
     * @return true if 7 days have passed since enabled or last check-in
     */
    fun shouldShowExitCheckIn(enabledAt: Long, lastCheckInAt: Long): Boolean {
        if (enabledAt == 0L) return false

        val sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000
        val referenceTime = if (lastCheckInAt > enabledAt) lastCheckInAt else enabledAt
        val timeSinceReference = System.currentTimeMillis() - referenceTime

        return timeSinceReference >= sevenDaysInMillis
    }

    enum class IntensityTrend {
        DECLINING,
        STABLE,
        IMPROVING
    }
}

/**
 * Result of stress pattern analysis.
 */
data class StressAnalysisResult(
    val shouldSuggestQuietMode: Boolean,
    val reason: String,
    val stressScore: Double,
    val detectedSignals: List<String>
)
