package com.prody.prashant.domain.model

import java.time.LocalDate

/**
 * Comprehensive mood analytics data model.
 */
data class MoodAnalytics(
    val weeklyTrend: List<DailyMoodData>,
    val monthlyDistribution: Map<Mood, Int>,
    val averageMoodIntensity: Float,
    val mostCommonMood: Mood?,
    val moodByTimeOfDay: Map<TimeOfDay, Mood?>,
    val wordFrequency: Map<String, Int>,
    val streakWithPositiveMood: Int,
    val totalEntriesAnalyzed: Int,
    val dateRange: DateRange
)

/**
 * Daily mood data point for trends.
 */
data class DailyMoodData(
    val date: LocalDate,
    val mood: Mood?,
    val intensity: Float,
    val entryCount: Int
) {
    val hasData: Boolean get() = mood != null && entryCount > 0
}

/**
 * Time of day categories.
 */
enum class TimeOfDay(val displayName: String, val hourRange: IntRange) {
    MORNING("Morning", 5..11),
    AFTERNOON("Afternoon", 12..16),
    EVENING("Evening", 17..20),
    NIGHT("Night", 21..23),
    LATE_NIGHT("Late Night", 0..4);

    companion object {
        fun fromHour(hour: Int): TimeOfDay {
            return entries.find { hour in it.hourRange } ?: NIGHT
        }
    }
}

/**
 * Date range for analytics.
 */
data class DateRange(
    val start: LocalDate,
    val end: LocalDate
) {
    val dayCount: Int get() = (end.toEpochDay() - start.toEpochDay()).toInt() + 1
}

/**
 * Period options for analytics.
 */
enum class AnalyticsPeriod(val displayName: String, val days: Int) {
    WEEK("This Week", 7),
    TWO_WEEKS("2 Weeks", 14),
    MONTH("This Month", 30),
    THREE_MONTHS("3 Months", 90),
    SIX_MONTHS("6 Months", 180),
    YEAR("This Year", 365),
    ALL_TIME("All Time", -1) // -1 indicates no limit
}

/**
 * Mood insight generated from analytics.
 */
data class MoodInsight(
    val title: String,
    val description: String,
    val type: InsightType,
    val actionSuggestion: String? = null
)

enum class InsightType {
    POSITIVE,      // Celebrating progress
    NEUTRAL,       // Informational
    ATTENTION,     // Needs attention but not critical
    ENCOURAGEMENT  // Motivational
}

/**
 * Mood streak information.
 */
data class MoodStreak(
    val mood: Mood,
    val streakDays: Int,
    val startDate: LocalDate,
    val endDate: LocalDate
)

/**
 * Mood comparison between two periods.
 */
data class MoodComparison(
    val currentPeriod: MoodAnalytics,
    val previousPeriod: MoodAnalytics,
    val moodShift: MoodShift,
    val intensityChange: Float,
    val consistencyChange: Float
)

enum class MoodShift {
    IMPROVING,
    STABLE,
    DECLINING,
    VARIED
}

/**
 * Extension to check if mood is positive.
 */
val Mood.isPositive: Boolean
    get() = this in listOf(Mood.HAPPY, Mood.CALM, Mood.MOTIVATED, Mood.GRATEFUL, Mood.EXCITED)

/**
 * Extension to check if mood is negative.
 */
val Mood.isNegative: Boolean
    get() = this in listOf(Mood.SAD, Mood.ANXIOUS)

/**
 * Extension to check if mood is neutral.
 */
val Mood.isNeutral: Boolean
    get() = this == Mood.CONFUSED
