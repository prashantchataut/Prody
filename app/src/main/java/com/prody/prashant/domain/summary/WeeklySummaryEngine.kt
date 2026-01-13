package com.prody.prashant.domain.summary

import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.model.MoodTrend
import java.time.LocalDate

/**
 * Engine for generating comprehensive weekly summaries that make journaling feel valuable.
 *
 * The summary analyzes:
 * - Activity metrics (entries, words, active days)
 * - Mood trends and emotional journey
 * - Themes and patterns in writing
 * - Intention completion from daily rituals
 * - Highlight moments worth celebrating
 * - Personalized Buddha insights (NOT generic)
 */
interface WeeklySummaryEngine {

    /**
     * Generate a comprehensive weekly summary for the user.
     *
     * @param userId The user to generate summary for
     * @param weekOf Any date within the target week (defaults to previous week)
     * @return WeeklySummary with all analysis complete
     */
    suspend fun generate(userId: String, weekOf: LocalDate = LocalDate.now().minusWeeks(1)): WeeklySummary

    /**
     * Check if a summary can be generated (has sufficient data).
     */
    suspend fun canGenerate(userId: String, weekOf: LocalDate): Boolean
}

/**
 * Comprehensive weekly summary data.
 *
 * This is the heart of making journaling feel valuable - showing users
 * their progress, patterns, and providing personalized wisdom.
 */
data class WeeklySummary(
    // Time period
    val weekStart: LocalDate,
    val weekEnd: LocalDate,

    // Activity metrics
    val entriesCount: Int,
    val totalWords: Int,
    val activeDays: Int,
    val microEntriesCount: Int = 0,

    // Mood analysis
    val moodTrend: MoodTrend,
    val dominantMood: Mood?,
    val moodDistribution: Map<Mood, Int> = emptyMap(),

    // Content analysis
    val topThemes: List<String>,
    val patterns: List<WritingPattern>,

    // Gamification
    val streakStatus: WeeklyStreakInfo,

    // Personalized insights (NOT generic)
    val buddhaInsight: String,

    // Daily rituals / intentions
    val intentionCompletionRate: Float? = null,

    // Highlight of the week
    val highlightEntry: JournalEntryEntity? = null,
    val highlightReason: String? = null,

    // Week-over-week comparison
    val previousWeekComparison: WeekComparison? = null,

    // Celebration or encouragement
    val celebrationMessage: String? = null
) {
    val hasActivity: Boolean
        get() = entriesCount > 0 || microEntriesCount > 0

    val averageWordsPerEntry: Int
        get() = if (entriesCount > 0) totalWords / entriesCount else 0
}

/**
 * Writing patterns detected in the week.
 */
data class WritingPattern(
    val type: WeeklyPatternType,
    val confidence: Float, // 0.0 to 1.0
    val description: String
)

/**
 * Pattern types for weekly summary analysis.
 *
 * Renamed from PatternType to avoid collision with other PatternType definitions.
 */
enum class WeeklyPatternType {
    MORNING_WRITER,
    EVENING_REFLECTOR,
    DEEP_THINKER,
    CONCISE_REFLECTOR,
    CONSISTENT_JOURNALER,
    EMOTIONAL_PROCESSOR,
    GRATITUDE_PRACTICER,
    GOAL_ORIENTED
}

/**
 * Streak status for the week.
 */
data class WeeklyStreakInfo(
    val currentStreak: Int,
    val isNewRecord: Boolean,
    val previousBest: Int,
    val weekContribution: Int // How many days this week contributed
)

/**
 * Week-over-week comparison.
 */
data class WeekComparison(
    val entriesChange: Int,
    val wordsChange: Int,
    val moodImprovement: Boolean?,
    val newThemesDiscovered: List<String>
) {
    val entriesChangePercent: Int
        get() = if (entriesChange != 0) {
            // Calculate from context
            0
        } else 0

    val wordsChangePercent: Int
        get() = if (wordsChange != 0) {
            // Calculate from context
            0
        } else 0
}
