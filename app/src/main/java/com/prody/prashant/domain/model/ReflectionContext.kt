package com.prody.prashant.domain.model

import java.time.LocalDate

/**
 * Domain model for building context for Buddha's enhanced responses.
 *
 * This context allows Buddha to:
 * - Reference past entries and themes
 * - Notice patterns over time
 * - Acknowledge growth and changes
 * - Ask connecting questions
 */
data class ReflectionContext(
    val currentEntry: JournalEntrySummary,
    val recentEntries: List<EntrySummary>,  // Last 5-10 entries
    val recurringThemes: List<String>,       // Themes appearing 3+ times
    val moodTrend: MoodTrend,
    val daysOnJourney: Int,
    val totalEntriesCount: Int,
    val currentStreak: Int,
    val lastEntryDate: LocalDate?
) {
    /**
     * Check if user has enough history for contextual responses
     */
    val hasSignificantHistory: Boolean
        get() = recentEntries.size >= 3 || daysOnJourney >= 7

    /**
     * Get a summary of recent themes for Buddha's prompt
     */
    fun getThemesSummary(): String {
        return if (recurringThemes.isNotEmpty()) {
            recurringThemes.take(5).joinToString(", ")
        } else {
            "exploring various topics"
        }
    }

    /**
     * Get mood trend description for Buddha's prompt
     */
    fun getMoodTrendDescription(): String {
        return when (moodTrend) {
            MoodTrend.IMPROVING -> "Your mood has been improving recently"
            MoodTrend.STABLE -> "Your emotional state has been fairly consistent"
            MoodTrend.DECLINING -> "You've been going through some challenging times"
            MoodTrend.VARIABLE, MoodTrend.VOLATILE, MoodTrend.FLUCTUATING -> "Your emotions have been fluctuating"
            MoodTrend.INSUFFICIENT_DATA -> "We're still getting to know your patterns"
        }
    }
}

/**
 * Summary of a journal entry for context building
 */
data class JournalEntrySummary(
    val id: Long,
    val content: String,
    val mood: Mood?,
    val moodIntensity: Int?,
    val wordCount: Int,
    val createdAt: Long,
    val themes: List<String>,
    val title: String?
) {
    /**
     * Get a brief preview of the content (first ~100 chars)
     */
    fun getContentPreview(): String {
        return if (content.length > 100) {
            content.take(100).trim() + "..."
        } else {
            content
        }
    }
}

/**
 * Lightweight summary for recent entries list
 */
data class EntrySummary(
    val id: Long,
    val date: LocalDate,
    val mood: Mood?,
    val keyThemes: List<String>,
    val wordCount: Int,
    val firstLine: String
) {
    /**
     * Get a description for Buddha's context
     */
    fun getDescription(): String {
        val moodPart = mood?.let { "Feeling ${it.displayName.lowercase()}" } ?: "Reflecting"
        val themePart = if (keyThemes.isNotEmpty()) {
            " about ${keyThemes.first()}"
        } else ""
        return "$moodPart$themePart"
    }
}

/**
 * Detected pattern in user's journal entries
 */
data class JournalPattern(
    val type: PatternType,
    val description: String,
    val occurrences: Int,
    val relatedEntryIds: List<Long>,
    val firstDetectedAt: Long,
    val lastSeenAt: Long
) {
    /**
     * Whether this pattern is significant enough to mention
     */
    val isSignificant: Boolean
        get() = occurrences >= 3
}

/**
 * Types of patterns that can be detected
 */
enum class PatternType {
    RECURRING_THEME,      // Same topic appearing multiple times
    MOOD_PATTERN,         // Consistent mood at certain times
    TIME_PATTERN,         // Writing at consistent times
    GROWTH_INDICATOR,     // Signs of progress on an issue
    CONCERN_INDICATOR     // Something that might need attention
}
