package com.prody.prashant.domain.analytics

import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.model.AnalyticsPeriod
import com.prody.prashant.domain.model.DailyMoodData
import com.prody.prashant.domain.model.DateRange
import com.prody.prashant.domain.model.InsightType
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.model.MoodAnalytics
import com.prody.prashant.domain.model.MoodComparison
import com.prody.prashant.domain.model.MoodInsight
import com.prody.prashant.domain.model.MoodShift
import com.prody.prashant.domain.model.MoodStreak
import com.prody.prashant.domain.model.TimeOfDay
import com.prody.prashant.domain.model.isNegative
import com.prody.prashant.domain.model.isPositive
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Engine for analyzing journal mood data and generating insights.
 */
@Singleton
class MoodAnalyticsEngine @Inject constructor() {

    /**
     * Generate comprehensive mood analytics from journal entries.
     */
    fun generateAnalytics(
        entries: List<JournalEntryEntity>,
        period: AnalyticsPeriod = AnalyticsPeriod.MONTH
    ): MoodAnalytics {
        val now = LocalDate.now()
        val startDate = if (period.days > 0) {
            now.minusDays(period.days.toLong())
        } else {
            entries.minOfOrNull { it.createdAt.toLocalDate() } ?: now
        }

        val filteredEntries = filterEntriesByPeriod(entries, startDate, now)

        return MoodAnalytics(
            weeklyTrend = calculateWeeklyTrend(filteredEntries, startDate, now),
            monthlyDistribution = calculateMoodDistribution(filteredEntries),
            averageMoodIntensity = calculateAverageIntensity(filteredEntries),
            mostCommonMood = findMostCommonMood(filteredEntries),
            moodByTimeOfDay = analyzeMoodByTimeOfDay(filteredEntries),
            wordFrequency = analyzeWordFrequency(filteredEntries),
            streakWithPositiveMood = calculatePositiveMoodStreak(filteredEntries),
            totalEntriesAnalyzed = filteredEntries.size,
            dateRange = DateRange(startDate, now)
        )
    }

    /**
     * Generate mood insights based on analytics.
     */
    fun generateInsights(analytics: MoodAnalytics): List<MoodInsight> {
        val insights = mutableListOf<MoodInsight>()

        // Positive mood streak insight
        if (analytics.streakWithPositiveMood >= 3) {
            insights.add(
                MoodInsight(
                    title = "Positive Momentum",
                    description = "You've had ${analytics.streakWithPositiveMood} consecutive days of positive mood entries. Keep up the great work!",
                    type = InsightType.POSITIVE,
                    actionSuggestion = "Consider journaling what's been contributing to your positive state."
                )
            )
        }

        // Most common mood insight
        analytics.mostCommonMood?.let { mood ->
            val percentage = analytics.monthlyDistribution[mood]?.let {
                (it * 100) / analytics.totalEntriesAnalyzed.coerceAtLeast(1)
            } ?: 0

            insights.add(
                MoodInsight(
                    title = "Your Dominant Mood",
                    description = "You've felt ${mood.displayName.lowercase()} in $percentage% of your entries.",
                    type = if (mood.isPositive) InsightType.POSITIVE else InsightType.NEUTRAL
                )
            )
        }

        // Time of day insight
        val bestTimeOfDay = analytics.moodByTimeOfDay.entries
            .filter { it.value?.isPositive == true }
            .maxByOrNull { analytics.moodByTimeOfDay.values.count { v -> v == it.value } }

        bestTimeOfDay?.let {
            insights.add(
                MoodInsight(
                    title = "Best Time to Journal",
                    description = "You tend to feel most positive when journaling in the ${it.key.displayName.lowercase()}.",
                    type = InsightType.NEUTRAL,
                    actionSuggestion = "Try to make journaling during ${it.key.displayName.lowercase()} a habit."
                )
            )
        }

        // Low entry count encouragement
        if (analytics.totalEntriesAnalyzed < 5 && analytics.dateRange.dayCount >= 7) {
            insights.add(
                MoodInsight(
                    title = "Journal More Often",
                    description = "You've only journaled ${analytics.totalEntriesAnalyzed} times this period. More entries help us give better insights.",
                    type = InsightType.ENCOURAGEMENT,
                    actionSuggestion = "Try journaling at least once a day for deeper self-understanding."
                )
            )
        }

        // Intensity insight
        if (analytics.averageMoodIntensity > 7) {
            insights.add(
                MoodInsight(
                    title = "Strong Emotional Experiences",
                    description = "Your average mood intensity is ${analytics.averageMoodIntensity.toInt()}/10. You're experiencing emotions deeply.",
                    type = InsightType.NEUTRAL
                )
            )
        }

        return insights
    }

    /**
     * Compare mood analytics between two periods.
     */
    fun comparePeriods(
        currentEntries: List<JournalEntryEntity>,
        previousEntries: List<JournalEntryEntity>,
        period: AnalyticsPeriod
    ): MoodComparison {
        val currentAnalytics = generateAnalytics(currentEntries, period)
        val previousAnalytics = generateAnalytics(previousEntries, period)

        val currentPositiveCount = currentEntries.count { Mood.fromString(it.mood).isPositive }
        val previousPositiveCount = previousEntries.count { Mood.fromString(it.mood).isPositive }

        val moodShift = when {
            currentAnalytics.totalEntriesAnalyzed == 0 || previousAnalytics.totalEntriesAnalyzed == 0 ->
                MoodShift.VARIED
            currentPositiveCount.toFloat() / currentAnalytics.totalEntriesAnalyzed >
                    previousPositiveCount.toFloat() / previousAnalytics.totalEntriesAnalyzed + 0.1f ->
                MoodShift.IMPROVING
            currentPositiveCount.toFloat() / currentAnalytics.totalEntriesAnalyzed <
                    previousPositiveCount.toFloat() / previousAnalytics.totalEntriesAnalyzed - 0.1f ->
                MoodShift.DECLINING
            else -> MoodShift.STABLE
        }

        return MoodComparison(
            currentPeriod = currentAnalytics,
            previousPeriod = previousAnalytics,
            moodShift = moodShift,
            intensityChange = currentAnalytics.averageMoodIntensity - previousAnalytics.averageMoodIntensity,
            consistencyChange = calculateConsistencyChange(currentEntries, previousEntries)
        )
    }

    /**
     * Find the longest positive mood streak.
     */
    fun findLongestPositiveStreak(entries: List<JournalEntryEntity>): MoodStreak? {
        if (entries.isEmpty()) return null

        val sortedEntries = entries.sortedBy { it.createdAt }
        var maxStreak = 0
        var currentStreak = 0
        var streakMood: Mood? = null
        var streakStart: LocalDate? = null
        var streakEnd: LocalDate? = null
        var currentStreakStart: LocalDate? = null

        for (entry in sortedEntries) {
            val mood = Mood.fromString(entry.mood)
            val date = entry.createdAt.toLocalDate()

            if (mood.isPositive) {
                if (currentStreak == 0) {
                    currentStreakStart = date
                }
                currentStreak++
                if (currentStreak > maxStreak) {
                    maxStreak = currentStreak
                    streakMood = mood
                    streakStart = currentStreakStart
                    streakEnd = date
                }
            } else {
                currentStreak = 0
            }
        }

        return if (maxStreak > 0 && streakMood != null && streakStart != null && streakEnd != null) {
            MoodStreak(streakMood, maxStreak, streakStart, streakEnd)
        } else null
    }

    // Private helper methods

    private fun filterEntriesByPeriod(
        entries: List<JournalEntryEntity>,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<JournalEntryEntity> {
        val startMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return entries.filter { it.createdAt in startMillis until endMillis }
    }

    private fun calculateWeeklyTrend(
        entries: List<JournalEntryEntity>,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailyMoodData> {
        val entriesByDate = entries.groupBy { it.createdAt.toLocalDate() }

        return generateSequence(startDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(endDate) }
            .map { date ->
                val dayEntries = entriesByDate[date] ?: emptyList()
                DailyMoodData(
                    date = date,
                    mood = dayEntries.mostCommonMood(),
                    intensity = dayEntries.map { it.moodIntensity }.average().toFloat().takeIf { !it.isNaN() } ?: 0f,
                    entryCount = dayEntries.size
                )
            }
            .toList()
    }

    private fun calculateMoodDistribution(entries: List<JournalEntryEntity>): Map<Mood, Int> {
        return entries
            .groupingBy { Mood.fromString(it.mood) }
            .eachCount()
    }

    private fun calculateAverageIntensity(entries: List<JournalEntryEntity>): Float {
        if (entries.isEmpty()) return 0f
        return entries.map { it.moodIntensity }.average().toFloat()
    }

    private fun findMostCommonMood(entries: List<JournalEntryEntity>): Mood? {
        return entries.mostCommonMood()
    }

    private fun List<JournalEntryEntity>.mostCommonMood(): Mood? {
        if (isEmpty()) return null
        return groupingBy { Mood.fromString(it.mood) }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }

    private fun analyzeMoodByTimeOfDay(entries: List<JournalEntryEntity>): Map<TimeOfDay, Mood?> {
        val entriesByTime = entries.groupBy { entry ->
            val hour = Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .hour
            TimeOfDay.fromHour(hour)
        }

        return TimeOfDay.entries.associateWith { timeOfDay ->
            entriesByTime[timeOfDay]?.mostCommonMood()
        }
    }

    private fun analyzeWordFrequency(
        entries: List<JournalEntryEntity>,
        topN: Int = 20
    ): Map<String, Int> {
        val stopWords = setOf(
            "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for",
            "of", "with", "by", "from", "as", "is", "was", "are", "were", "been",
            "be", "have", "has", "had", "do", "does", "did", "will", "would", "could",
            "should", "may", "might", "can", "this", "that", "these", "those", "i",
            "me", "my", "myself", "we", "our", "you", "your", "he", "him", "she",
            "her", "it", "its", "they", "them", "what", "which", "who", "when",
            "where", "why", "how", "all", "each", "every", "both", "few", "more",
            "most", "other", "some", "such", "no", "not", "only", "same", "so",
            "than", "too", "very", "just", "also", "now", "here", "there", "then"
        )

        return entries
            .flatMap { entry ->
                entry.content
                    .lowercase()
                    .replace(Regex("[^a-z\\s]"), "")
                    .split(Regex("\\s+"))
                    .filter { it.length > 2 && it !in stopWords }
            }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(topN)
            .associate { it.key to it.value }
    }

    private fun calculatePositiveMoodStreak(entries: List<JournalEntryEntity>): Int {
        if (entries.isEmpty()) return 0

        val sortedByDate = entries.sortedByDescending { it.createdAt }
        var streak = 0

        for (entry in sortedByDate) {
            if (Mood.fromString(entry.mood).isPositive) {
                streak++
            } else {
                break
            }
        }

        return streak
    }

    private fun calculateConsistencyChange(
        currentEntries: List<JournalEntryEntity>,
        previousEntries: List<JournalEntryEntity>
    ): Float {
        val currentConsistency = if (currentEntries.isNotEmpty()) {
            currentEntries.map { Mood.fromString(it.mood) }.toSet().size.toFloat() / Mood.entries.size
        } else 0f

        val previousConsistency = if (previousEntries.isNotEmpty()) {
            previousEntries.map { Mood.fromString(it.mood) }.toSet().size.toFloat() / Mood.entries.size
        } else 0f

        return currentConsistency - previousConsistency
    }

    private fun Long.toLocalDate(): LocalDate {
        return Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}
