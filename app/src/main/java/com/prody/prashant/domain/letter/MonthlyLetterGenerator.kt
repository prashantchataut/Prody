package com.prody.prashant.domain.letter

import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.MicroEntryDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.model.*
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Generator for personalized monthly growth letters.
 *
 * Creates warm, personal monthly summaries that feel like they're written by
 * a caring friend who's been watching the user's journey - not a robot.
 *
 * Tone principles:
 * - Personal, not clinical
 * - Observant, not judgmental
 * - Encouraging, not patronizing
 * - Specific, not generic
 */
@Singleton
class MonthlyLetterGenerator @Inject constructor(
    private val journalDao: JournalDao,
    private val microEntryDao: MicroEntryDao
) {

    /**
     * Generate a monthly letter for the specified month
     */
    suspend fun generateLetter(userId: String, monthYear: YearMonth): MonthlyLetter {
        val startOfMonth = monthYear.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfMonth = monthYear.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        // Get all journal data for the month
        val entries = journalDao.getEntriesByDateRange(startOfMonth, endOfMonth)
        val microEntries = microEntryDao.getEntriesByDateRange(startOfMonth, endOfMonth)

        // Get previous month data for comparison
        val previousMonth = monthYear.minusMonths(1)
        val prevStartOfMonth = previousMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val prevEndOfMonth = previousMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val previousEntries = journalDao.getEntriesByDateRange(prevStartOfMonth, prevEndOfMonth)

        // Generate all sections
        val greeting = generateGreeting(monthYear, entries.size + microEntries.size)
        val activitySummary = generateActivitySummary(entries, microEntries, startOfMonth, endOfMonth)
        val themeAnalysis = generateThemeAnalysis(entries)
        val moodJourney = generateMoodJourney(entries)
        val buddhaInsight = generateBuddhaInsight(entries, themeAnalysis, moodJourney)
        val milestones = generateMilestones(entries, microEntries)
        val comparison = generateComparison(entries, previousEntries)
        val highlight = generateHighlight(entries)
        val closing = generateClosing(activitySummary, moodJourney, comparison)

        return MonthlyLetter(
            userId = userId,
            monthYear = monthYear,
            greeting = greeting,
            activitySummary = activitySummary,
            themeAnalysis = themeAnalysis,
            moodJourney = moodJourney,
            buddhaInsight = buddhaInsight,
            milestones = milestones,
            comparison = comparison,
            highlight = highlight,
            closing = closing,
            generatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Generate personalized greeting based on month and activity
     */
    private fun generateGreeting(monthYear: YearMonth, totalEntries: Int): String {
        val monthName = monthYear.month.name.lowercase().replaceFirstChar { it.uppercase() }

        return when {
            totalEntries == 0 -> "Hey there,\n\n$monthName was quiet. That's okay."
            totalEntries < 5 -> "Hi,\n\n$monthName came and went. You checked in a few times."
            totalEntries < 15 -> "Hello,\n\n$monthName was steady. You kept showing up."
            totalEntries < 25 -> "Hey,\n\n$monthName was active. You were here a lot."
            else -> "Hi there,\n\nYou really showed up in $monthName. Impressive."
        }
    }

    /**
     * Generate activity summary with personal tone
     */
    private suspend fun generateActivitySummary(
        entries: List<JournalEntryEntity>,
        microEntries: List<Any>,
        startOfMonth: Long,
        endOfMonth: Long
    ): ActivitySummary {
        val entriesCount = entries.size
        val microEntriesCount = microEntries.size
        val totalWords = entries.sumOf { it.wordCount }
        val activeDays = journalDao.getActiveDaysCountSince(startOfMonth)
        val averageWordsPerEntry = if (entriesCount > 0) (totalWords / entriesCount) else 0

        // Find most active week
        val mostActiveWeek = findMostActiveWeek(entries, startOfMonth, endOfMonth)

        return ActivitySummary(
            entriesCount = entriesCount,
            microEntriesCount = microEntriesCount,
            totalWords = totalWords,
            activeDays = activeDays,
            averageWordsPerEntry = averageWordsPerEntry,
            mostActiveWeek = mostActiveWeek
        )
    }

    /**
     * Find the most active week in the month
     */
    private fun findMostActiveWeek(entries: List<JournalEntryEntity>, startOfMonth: Long, endOfMonth: Long): String? {
        if (entries.isEmpty()) return null

        val weekCounts = mutableMapOf<String, Int>()
        entries.forEach { entry ->
            val date = Instant.ofEpochMilli(entry.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
            val weekStart = date.minusDays((date.dayOfWeek.value - 1).toLong())
            val weekKey = "${weekStart.monthValue}/${weekStart.dayOfMonth}"
            weekCounts[weekKey] = weekCounts.getOrDefault(weekKey, 0) + 1
        }

        val maxWeek = weekCounts.maxByOrNull { it.value }
        return maxWeek?.key
    }

    /**
     * Generate theme analysis from AI tags
     */
    private fun generateThemeAnalysis(entries: List<JournalEntryEntity>): ThemeAnalysis {
        // Extract themes from AI tags
        val themeMap = mutableMapOf<String, Int>()
        entries.forEach { entry ->
            entry.aiThemes?.split(",")?.forEach { theme ->
                val trimmed = theme.trim().lowercase()
                if (trimmed.isNotEmpty()) {
                    themeMap[trimmed] = themeMap.getOrDefault(trimmed, 0) + 1
                }
            }
        }

        val topThemes = themeMap.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key }

        // Extract recurring words from content
        val wordFrequency = mutableMapOf<String, Int>()
        entries.forEach { entry ->
            entry.content.lowercase()
                .split("\\s+".toRegex())
                .filter { it.length > 4 } // Only significant words
                .forEach { word ->
                    val cleaned = word.replace(Regex("[^a-z]"), "")
                    if (cleaned.isNotEmpty()) {
                        wordFrequency[cleaned] = wordFrequency.getOrDefault(cleaned, 0) + 1
                    }
                }
        }

        val recurringWords = wordFrequency.entries
            .filter { it.value >= 3 } // Appeared at least 3 times
            .sortedByDescending { it.value }
            .take(10)
            .map { it.key }

        // Generate narrative
        val narrative = generateThemeNarrative(topThemes, recurringWords, entries.size)

        return ThemeAnalysis(
            topThemes = topThemes,
            narrative = narrative,
            recurringWords = recurringWords
        )
    }

    /**
     * Generate personal narrative about themes
     */
    private fun generateThemeNarrative(themes: List<String>, words: List<String>, entryCount: Int): String {
        if (themes.isEmpty() && words.isEmpty()) {
            return "You kept your thoughts close this month. That's okay."
        }

        val parts = mutableListOf<String>()

        // Comment on themes
        if (themes.isNotEmpty()) {
            val primaryTheme = themes.first()
            parts.add("You wrote a lot about $primaryTheme. Something shifting there?")

            if (themes.size > 1) {
                val otherThemes = themes.drop(1).take(2).joinToString(" and ")
                parts.add("$otherThemes came up too.")
            }
        }

        // Comment on recurring words if significant
        if (words.isNotEmpty()) {
            val notableWords = words.filter {
                it !in listOf("just", "really", "think", "feel", "like", "want", "need", "know")
            }.take(2)

            if (notableWords.isNotEmpty()) {
                val wordStr = notableWords.joinToString(" and ")
                parts.add("The word${if (notableWords.size > 1) "s" else ""} '$wordStr' showed up a lot.")
            }
        }

        return parts.joinToString(" ")
    }

    /**
     * Generate mood journey through the month
     */
    private fun generateMoodJourney(entries: List<JournalEntryEntity>): MoodJourney {
        val dataPoints = entries.map { entry ->
            MoodDataPoint(
                date = Instant.ofEpochMilli(entry.createdAt).atZone(ZoneId.systemDefault()).toLocalDate(),
                mood = entry.mood,
                intensity = entry.moodIntensity
            )
        }.sortedBy { it.date }

        // Find dominant mood
        val moodCounts = entries.groupingBy { it.mood }.eachCount()
        val dominantMood = moodCounts.maxByOrNull { it.value }?.key

        // Determine trend
        val trend = if (dataPoints.size >= 3) {
            val firstHalf = dataPoints.take(dataPoints.size / 2).map { it.intensity }.average()
            val secondHalf = dataPoints.takeLast(dataPoints.size / 2).map { it.intensity }.average()
            when {
                secondHalf > firstHalf + 1.5 -> MoodTrend.IMPROVING
                secondHalf < firstHalf - 1.5 -> MoodTrend.DECLINING
                else -> MoodTrend.STABLE
            }
        } else {
            MoodTrend.STABLE
        }

        // Generate narrative
        val narrative = generateMoodNarrative(dataPoints, dominantMood, trend)

        return MoodJourney(
            dataPoints = dataPoints,
            dominantMood = dominantMood,
            narrative = narrative,
            trend = trend
        )
    }

    /**
     * Generate personal narrative about mood journey
     */
    private fun generateMoodNarrative(
        dataPoints: List<MoodDataPoint>,
        dominantMood: String?,
        trend: MoodTrend
    ): String {
        if (dataPoints.isEmpty()) {
            return "Not much mood data this month."
        }

        val parts = mutableListOf<String>()

        // Comment on dominant mood
        dominantMood?.let {
            parts.add("You felt $it most often this month.")
        }

        // Comment on trend
        when (trend) {
            MoodTrend.IMPROVING -> parts.add("Your mood lifted as the month went on. I noticed.")
            MoodTrend.DECLINING -> parts.add("Things got heavier toward the end. That's worth noting.")
            MoodTrend.STABLE -> parts.add("Your emotional state stayed pretty consistent.")
            MoodTrend.VOLATILE -> parts.add("Your emotions went through many shifts this month.")
            MoodTrend.VARIABLE -> parts.add("You experienced quite a range of emotions this month.")
            MoodTrend.FLUCTUATING -> parts.add("Your mood fluctuated throughout the month.")
            MoodTrend.INSUFFICIENT_DATA -> parts.add("I'm still learning your patterns.")
        }

        return parts.joinToString(" ")
    }

    /**
     * Generate Buddha's pattern observation
     */
    private fun generateBuddhaInsight(
        entries: List<JournalEntryEntity>,
        themes: ThemeAnalysis,
        mood: MoodJourney
    ): BuddhaInsight {
        val observations = mutableListOf<String>()

        // Observe patterns
        if (entries.isEmpty()) {
            observations.add("Silence is also a message. What kept you away this month?")
        } else {
            // Pattern about frequency
            if (entries.size > 20) {
                observations.add("You turned to writing a lot. It's becoming a refuge.")
            }

            // Pattern about themes and mood
            if (themes.topThemes.contains("work") && mood.trend == MoodTrend.DECLINING) {
                observations.add("Work weighs on you. The connection is clear in your words.")
            }

            // Pattern about consistency
            val dates = entries.map {
                Instant.ofEpochMilli(it.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
            }
            val gaps = dates.zipWithNext { a, b ->
                java.time.temporal.ChronoUnit.DAYS.between(a, b)
            }
            val avgGap = gaps.average()
            if (avgGap <= 2.0) {
                observations.add("You've built a rhythm. Small steps, but they add up.")
            }
        }

        val observation = observations.firstOrNull() ?: "Keep watching yourself. Patterns reveal themselves slowly."

        return BuddhaInsight(
            observation = observation,
            wisdom = null // Could add relevant Buddha quote
        )
    }

    /**
     * Generate milestones achieved and upcoming
     */
    private fun generateMilestones(entries: List<JournalEntryEntity>, microEntries: List<Any>): Milestones {
        val achieved = mutableListOf<Milestone>()
        val upcoming = mutableListOf<Milestone>()

        val totalEntries = entries.size
        val totalWords = entries.sumOf { it.wordCount }

        // Check for achieved milestones
        when {
            totalEntries >= 30 -> achieved.add(Milestone(
                "Daily Journaler",
                "Wrote every day this month",
                100
            ))
            totalEntries >= 20 -> achieved.add(Milestone(
                "Committed Writer",
                "20+ entries this month",
                100
            ))
            totalEntries >= 10 -> achieved.add(Milestone(
                "Building Momentum",
                "10+ entries this month",
                100
            ))
        }

        if (totalWords >= 10000) {
            achieved.add(Milestone(
                "Wordsmith",
                "10,000+ words written",
                100
            ))
        }

        // Check for upcoming milestones
        if (totalEntries in 15..19) {
            upcoming.add(Milestone(
                "Committed Writer",
                "Write ${20 - totalEntries} more entries",
                (totalEntries.toFloat() / 20 * 100).roundToInt()
            ))
        }

        if (totalWords in 7500..9999) {
            upcoming.add(Milestone(
                "Wordsmith",
                "${10000 - totalWords} words to go",
                (totalWords.toFloat() / 10000 * 100).roundToInt()
            ))
        }

        return Milestones(
            achieved = achieved,
            upcoming = upcoming,
            streakNote = null
        )
    }

    /**
     * Generate month-over-month comparison
     */
    private fun generateComparison(currentEntries: List<JournalEntryEntity>, previousEntries: List<JournalEntryEntity>): MonthComparison? {
        if (previousEntries.isEmpty()) return null

        val currentCount = currentEntries.size
        val prevCount = previousEntries.size
        val currentWords = currentEntries.sumOf { it.wordCount }
        val prevWords = previousEntries.sumOf { it.wordCount }

        val entriesChange = if (prevCount > 0) {
            ((currentCount - prevCount).toFloat() / prevCount * 100).roundToInt()
        } else {
            100
        }

        val wordsChange = if (prevWords > 0) {
            ((currentWords - prevWords).toFloat() / prevWords * 100).roundToInt()
        } else {
            100
        }

        val note = when {
            entriesChange > 20 -> "You wrote a lot more than last month. What changed?"
            entriesChange < -20 -> "You pulled back this month. Life got busy?"
            else -> "Pretty similar to last month. Consistency."
        }

        return MonthComparison(
            entriesChangePercent = entriesChange,
            wordsChangePercent = wordsChange,
            note = note
        )
    }

    /**
     * Find the most significant entry as a highlight
     */
    private fun generateHighlight(entries: List<JournalEntryEntity>): LetterHighlight? {
        if (entries.isEmpty()) return null

        // Find longest entry (often most significant)
        val highlight = entries.maxByOrNull { it.wordCount } ?: return null

        // Extract a meaningful quote (first 100 chars)
        val quote = highlight.content.take(100).trim() + if (highlight.content.length > 100) "..." else ""

        return LetterHighlight(
            entryId = highlight.id,
            quote = quote,
            reason = "Your longest entry this month"
        )
    }

    /**
     * Generate warm, personal closing message
     */
    private fun generateClosing(
        activity: ActivitySummary,
        mood: MoodJourney,
        comparison: MonthComparison?
    ): LetterClosing {
        val messages = mutableListOf<String>()

        // Base message on activity level
        when {
            activity.totalEntries == 0 -> {
                messages.add("Next month, just try once. One entry. See what happens.")
            }
            activity.totalEntries < 10 -> {
                messages.add("You showed up ${activity.totalEntries} times. That's not nothing.")
            }
            activity.totalEntries >= 20 -> {
                messages.add("This is becoming a practice. Keep going.")
            }
            else -> {
                messages.add("You're building something here. I can see it.")
            }
        }

        // Add encouragement based on trend
        val encouragement = when {
            mood.trend == MoodTrend.IMPROVING -> "You're doing better than you think."
            comparison != null && comparison.entriesChangePercent > 0 -> "The progress is real, even when it doesn't feel like it."
            activity.totalEntries > 0 -> "Keep showing up. That's the whole thing."
            else -> null
        }

        return LetterClosing(
            message = messages.first(),
            encouragement = encouragement
        )
    }

    /**
     * Check if enough data exists to generate a letter
     */
    suspend fun canGenerate(userId: String, monthYear: YearMonth): Boolean {
        val startOfMonth = monthYear.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfMonth = monthYear.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val count = journalDao.getEntryCountSince(startOfMonth)
        return count > 0 // Generate even with 1 entry
    }
}
