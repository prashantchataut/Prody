package com.prody.prashant.domain.summary

import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.MicroEntryDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.data.local.entity.MicroEntryEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.model.Mood
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WeeklySummaryEngine.
 *
 * Performs comprehensive analysis of journal entries to create
 * meaningful, personalized weekly summaries.
 */
@Singleton
class WeeklySummaryEngineImpl @Inject constructor(
    private val journalDao: JournalDao,
    private val microEntryDao: MicroEntryDao,
    private val preferencesManager: PreferencesManager,
    private val insightGenerator: BuddhaWeeklyInsightGenerator
) : WeeklySummaryEngine {

    override suspend fun generate(userId: String, weekOf: LocalDate): WeeklySummary {
        val (weekStart, weekEnd) = getWeekRange(weekOf)
        val weekStartMillis = weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val weekEndMillis = weekEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        // Fetch data
        val entries = fetchEntriesForWeek(userId, weekStartMillis, weekEndMillis)
        val microEntries = fetchMicroEntriesForWeek(userId, weekStartMillis, weekEndMillis)

        // Calculate metrics
        val entriesCount = entries.size
        val totalWords = calculateTotalWords(entries, microEntries)
        val activeDays = calculateActiveDays(entries, microEntries, weekStart)

        // Mood analysis
        val moodTrend = analyzeMoodTrend(entries)
        val dominantMood = calculateDominantMood(entries, microEntries)
        val moodDistribution = calculateMoodDistribution(entries, microEntries)

        // Content analysis
        val topThemes = extractTopThemes(entries, microEntries)
        val patterns = detectWritingPatterns(entries, weekStartMillis, weekEndMillis)

        // Gamification
        val streakStatus = calculateStreakStatus(userId, activeDays)

        // Highlight entry
        val highlightEntry = selectHighlightEntry(entries)

        // Week comparison
        val previousWeekComparison = calculatePreviousWeekComparison(
            userId,
            weekStart,
            entriesCount,
            totalWords,
            dominantMood,
            topThemes
        )

        // Generate personalized Buddha insight
        val buddhaInsight = insightGenerator.generateInsight(
            entries = entries,
            moodTrend = moodTrend,
            topThemes = topThemes,
            dominantMood = dominantMood,
            highlightEntry = highlightEntry
        )

        // Generate celebration or encouragement
        val celebrationMessage = insightGenerator.generateCelebration(
            entriesCount = entriesCount,
            isNewStreakRecord = streakStatus.isNewRecord,
            hasAllDays = activeDays == 7,
            moodTrend = moodTrend
        ) ?: insightGenerator.generateEncouragement(entriesCount, moodTrend)

        // Intention completion (if ritual tracking exists)
        val intentionCompletionRate = calculateIntentionCompletion(userId, weekStartMillis, weekEndMillis)

        return WeeklySummary(
            weekStart = weekStart,
            weekEnd = weekEnd,
            entriesCount = entriesCount,
            totalWords = totalWords,
            activeDays = activeDays,
            microEntriesCount = microEntries.size,
            moodTrend = moodTrend,
            dominantMood = dominantMood,
            moodDistribution = moodDistribution,
            topThemes = topThemes,
            patterns = patterns,
            streakStatus = streakStatus,
            buddhaInsight = buddhaInsight,
            intentionCompletionRate = intentionCompletionRate,
            highlightEntry = highlightEntry,
            highlightReason = generateHighlightReason(highlightEntry),
            previousWeekComparison = previousWeekComparison,
            celebrationMessage = celebrationMessage
        )
    }

    override suspend fun canGenerate(userId: String, weekOf: LocalDate): Boolean {
        val (weekStart, weekEnd) = getWeekRange(weekOf)
        val weekStartMillis = weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val weekEndMillis = weekEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val entries = fetchEntriesForWeek(userId, weekStartMillis, weekEndMillis)
        val microEntries = fetchMicroEntriesForWeek(userId, weekStartMillis, weekEndMillis)

        return entries.isNotEmpty() || microEntries.isNotEmpty()
    }

    // ==================== DATA FETCHING ====================

    private suspend fun fetchEntriesForWeek(
        userId: String,
        startMillis: Long,
        endMillis: Long
    ): List<JournalEntryEntity> {
        return journalDao.getAllEntriesSync().filter { entry ->
            entry.userId == userId &&
            entry.createdAt in startMillis..endMillis &&
            !entry.isDeleted
        }
    }

    private suspend fun fetchMicroEntriesForWeek(
        userId: String,
        startMillis: Long,
        endMillis: Long
    ): List<MicroEntryEntity> {
        return microEntryDao.getAllMicroEntriesSync().filter { entry ->
            entry.userId == userId &&
            entry.createdAt in startMillis..endMillis &&
            !entry.isDeleted
        }
    }

    // ==================== METRICS CALCULATION ====================

    private fun calculateTotalWords(
        entries: List<JournalEntryEntity>,
        microEntries: List<MicroEntryEntity>
    ): Int {
        val journalWords = entries.sumOf { it.wordCount }
        val microWords = microEntries.sumOf {
            it.content.split("\\s+".toRegex()).filter { w -> w.isNotBlank() }.size
        }
        return journalWords + microWords
    }

    private fun calculateActiveDays(
        entries: List<JournalEntryEntity>,
        microEntries: List<MicroEntryEntity>,
        weekStart: LocalDate
    ): Int {
        val entryDates = entries.map { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSet()

        val microDates = microEntries.map { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSet()

        return (entryDates + microDates).size
    }

    // ==================== MOOD ANALYSIS ====================

    private fun analyzeMoodTrend(entries: List<JournalEntryEntity>): MoodTrend {
        if (entries.size < 3) return MoodTrend.STABLE

        val sortedEntries = entries.sortedBy { it.createdAt }
        val firstHalf = sortedEntries.take(sortedEntries.size / 2)
        val secondHalf = sortedEntries.drop(sortedEntries.size / 2)

        val positiveMoods = setOf(
            Mood.HAPPY,
            Mood.CALM,
            Mood.MOTIVATED,
            Mood.GRATEFUL,
            Mood.EXCITED
        )

        val firstHalfPositive = firstHalf.count {
            Mood.fromString(it.mood) in positiveMoods
        }.toFloat() / firstHalf.size

        val secondHalfPositive = secondHalf.count {
            Mood.fromString(it.mood) in positiveMoods
        }.toFloat() / secondHalf.size

        val diff = secondHalfPositive - firstHalfPositive

        return when {
            diff > 0.2 -> MoodTrend.IMPROVING
            diff < -0.2 -> MoodTrend.DECLINING
            else -> MoodTrend.STABLE
        }
    }

    private fun calculateDominantMood(
        entries: List<JournalEntryEntity>,
        microEntries: List<MicroEntryEntity>
    ): Mood? {
        val allMoods = (entries.map { it.mood } + microEntries.mapNotNull { it.mood })
            .filter { it.isNotBlank() }

        if (allMoods.isEmpty()) return null

        val moodCounts = allMoods.groupingBy { it }.eachCount()
        val dominantMoodString = moodCounts.maxByOrNull { it.value }?.key ?: return null

        return Mood.fromString(dominantMoodString)
    }

    private fun calculateMoodDistribution(
        entries: List<JournalEntryEntity>,
        microEntries: List<MicroEntryEntity>
    ): Map<Mood, Int> {
        val allMoods = (entries.map { it.mood } + microEntries.mapNotNull { it.mood })
            .filter { it.isNotBlank() }

        if (allMoods.isEmpty()) return emptyMap()

        return allMoods
            .groupingBy { Mood.fromString(it) }
            .eachCount()
    }

    // ==================== CONTENT ANALYSIS ====================

    private fun extractTopThemes(
        entries: List<JournalEntryEntity>,
        microEntries: List<MicroEntryEntity>
    ): List<String> {
        val allContent = entries.map { it.content.lowercase() } +
            microEntries.map { it.content.lowercase() } +
            entries.mapNotNull { it.aiThemes?.lowercase() }

        if (allContent.isEmpty()) return emptyList()

        val themeKeywords = mapOf(
            "work" to listOf("work", "job", "career", "office", "meeting", "project", "boss", "colleague", "deadline", "client"),
            "relationships" to listOf("friend", "family", "partner", "relationship", "love", "connection", "dating", "trust", "conflict"),
            "health" to listOf("health", "exercise", "sleep", "body", "fitness", "energy", "workout", "wellness", "tired"),
            "growth" to listOf("learn", "grow", "improve", "progress", "goal", "achievement", "development", "skill", "better"),
            "emotions" to listOf("feel", "feeling", "emotion", "mood", "anxiety", "stress", "worry", "overwhelm", "emotional"),
            "creativity" to listOf("create", "idea", "inspiration", "art", "music", "write", "design", "creative", "project"),
            "gratitude" to listOf("grateful", "thankful", "appreciate", "blessed", "lucky", "gratitude", "fortunate"),
            "challenges" to listOf("challenge", "problem", "struggle", "difficult", "hard", "obstacle", "tough", "stuck")
        )

        val themeCounts = mutableMapOf<String, Int>()

        for (content in allContent) {
            for ((theme, keywords) in themeKeywords) {
                val matchCount = keywords.count { keyword ->
                    content.contains(keyword)
                }
                if (matchCount > 0) {
                    themeCounts[theme] = (themeCounts[theme] ?: 0) + matchCount
                }
            }
        }

        return themeCounts.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }
    }

    private fun detectWritingPatterns(
        entries: List<JournalEntryEntity>,
        weekStartMillis: Long,
        weekEndMillis: Long
    ): List<WritingPattern> {
        if (entries.isEmpty()) return emptyList()

        val patterns = mutableListOf<WritingPattern>()

        // Time of day pattern
        val morningEntries = entries.filter { entry ->
            val hour = Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .hour
            hour in 5..11
        }

        val eveningEntries = entries.filter { entry ->
            val hour = Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .hour
            hour in 18..23
        }

        if (morningEntries.size.toFloat() / entries.size > 0.6) {
            patterns.add(
                WritingPattern(
                    type = PatternType.MORNING_WRITER,
                    confidence = morningEntries.size.toFloat() / entries.size,
                    description = "You write most often in the morning"
                )
            )
        } else if (eveningEntries.size.toFloat() / entries.size > 0.6) {
            patterns.add(
                WritingPattern(
                    type = PatternType.EVENING_REFLECTOR,
                    confidence = eveningEntries.size.toFloat() / entries.size,
                    description = "You prefer evening reflection"
                )
            )
        }

        // Length pattern
        val avgWordCount = entries.map { it.wordCount }.average()
        if (avgWordCount > 300) {
            patterns.add(
                WritingPattern(
                    type = PatternType.DEEP_THINKER,
                    confidence = 0.8f,
                    description = "Your entries are thoughtful and detailed"
                )
            )
        } else if (avgWordCount < 100) {
            patterns.add(
                WritingPattern(
                    type = PatternType.CONCISE_REFLECTOR,
                    confidence = 0.8f,
                    description = "You keep your reflections concise"
                )
            )
        }

        // Consistency pattern
        if (entries.size >= 5) {
            patterns.add(
                WritingPattern(
                    type = PatternType.CONSISTENT_JOURNALER,
                    confidence = entries.size / 7.0f,
                    description = "You're building a consistent practice"
                )
            )
        }

        // Emotional processing
        val emotionalWords = listOf("feel", "feeling", "emotion", "process", "understand")
        val emotionalEntries = entries.count { entry ->
            emotionalWords.any { word -> entry.content.lowercase().contains(word) }
        }
        if (emotionalEntries.toFloat() / entries.size > 0.5) {
            patterns.add(
                WritingPattern(
                    type = PatternType.EMOTIONAL_PROCESSOR,
                    confidence = emotionalEntries.toFloat() / entries.size,
                    description = "You use writing to process emotions"
                )
            )
        }

        // Gratitude practice
        val gratitudeWords = listOf("grateful", "thankful", "appreciate", "blessed")
        val gratitudeEntries = entries.count { entry ->
            gratitudeWords.any { word -> entry.content.lowercase().contains(word) }
        }
        if (gratitudeEntries.toFloat() / entries.size > 0.3) {
            patterns.add(
                WritingPattern(
                    type = PatternType.GRATITUDE_PRACTICER,
                    confidence = gratitudeEntries.toFloat() / entries.size,
                    description = "You regularly practice gratitude"
                )
            )
        }

        // Goal-oriented
        val goalWords = listOf("goal", "target", "achieve", "accomplish", "plan")
        val goalEntries = entries.count { entry ->
            goalWords.any { word -> entry.content.lowercase().contains(word) }
        }
        if (goalEntries.toFloat() / entries.size > 0.3) {
            patterns.add(
                WritingPattern(
                    type = PatternType.GOAL_ORIENTED,
                    confidence = goalEntries.toFloat() / entries.size,
                    description = "You focus on goals and progress"
                )
            )
        }

        return patterns.sortedByDescending { it.confidence }
    }

    // ==================== GAMIFICATION ====================

    private suspend fun calculateStreakStatus(userId: String, weekContribution: Int): StreakStatus {
        val currentStreak = preferencesManager.currentStreak.first()
        // Note: We'd need to implement proper streak tracking to get previousBest
        // For now, we'll use a simple approach
        val previousBest = currentStreak // Simplified - would need historical data

        return StreakStatus(
            currentStreak = currentStreak,
            isNewRecord = currentStreak > previousBest,
            previousBest = previousBest,
            weekContribution = weekContribution
        )
    }

    // ==================== HIGHLIGHT SELECTION ====================

    private fun selectHighlightEntry(entries: List<JournalEntryEntity>): JournalEntryEntity? {
        if (entries.isEmpty()) return null

        // Scoring system for highlight selection
        return entries.maxByOrNull { entry ->
            var score = 0

            // Word count (more detailed entries)
            score += (entry.wordCount / 10).coerceAtMost(50)

            // Bookmarked entries
            if (entry.isBookmarked) score += 100

            // Has AI insights (means it was meaningful)
            if (!entry.aiInsight.isNullOrBlank()) score += 30

            // Positive moods
            val mood = Mood.fromString(entry.mood)
            if (mood in listOf(Mood.HAPPY, Mood.GRATEFUL, Mood.EXCITED, Mood.MOTIVATED)) {
                score += 20
            }

            // Has reflection questions
            if (entry.content.contains("?")) score += 10

            score
        }
    }

    private fun generateHighlightReason(entry: JournalEntryEntity?): String? {
        if (entry == null) return null

        return when {
            entry.isBookmarked -> "You bookmarked this entry"
            entry.wordCount > 400 -> "Your most detailed reflection this week"
            !entry.aiInsight.isNullOrBlank() -> "A meaningful moment worth revisiting"
            else -> "This entry stood out"
        }
    }

    // ==================== WEEK COMPARISON ====================

    private suspend fun calculatePreviousWeekComparison(
        userId: String,
        currentWeekStart: LocalDate,
        currentEntriesCount: Int,
        currentTotalWords: Int,
        currentDominantMood: Mood?,
        currentThemes: List<String>
    ): WeekComparison? {
        val previousWeekStart = currentWeekStart.minusWeeks(1)
        val (prevStart, prevEnd) = getWeekRange(previousWeekStart)
        val prevStartMillis = prevStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val prevEndMillis = prevEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val prevEntries = fetchEntriesForWeek(userId, prevStartMillis, prevEndMillis)
        val prevMicroEntries = fetchMicroEntriesForWeek(userId, prevStartMillis, prevEndMillis)

        if (prevEntries.isEmpty() && prevMicroEntries.isEmpty()) return null

        val prevEntriesCount = prevEntries.size
        val prevTotalWords = calculateTotalWords(prevEntries, prevMicroEntries)
        val prevDominantMood = calculateDominantMood(prevEntries, prevMicroEntries)
        val prevThemes = extractTopThemes(prevEntries, prevMicroEntries)

        val moodImprovement = if (currentDominantMood != null && prevDominantMood != null) {
            val positiveMoods = setOf(Mood.HAPPY, Mood.CALM, Mood.MOTIVATED, Mood.GRATEFUL, Mood.EXCITED)
            val currentIsPositive = currentDominantMood in positiveMoods
            val prevIsPositive = prevDominantMood in positiveMoods
            currentIsPositive && !prevIsPositive
        } else null

        val newThemes = currentThemes.filter { it !in prevThemes }

        return WeekComparison(
            entriesChange = currentEntriesCount - prevEntriesCount,
            wordsChange = currentTotalWords - prevTotalWords,
            moodImprovement = moodImprovement,
            newThemesDiscovered = newThemes
        )
    }

    // ==================== INTENTION TRACKING ====================

    private suspend fun calculateIntentionCompletion(
        userId: String,
        weekStartMillis: Long,
        weekEndMillis: Long
    ): Float? {
        // Note: This would integrate with daily ritual tracking if implemented
        // For now, return null to indicate no intention tracking
        return null
    }

    // ==================== HELPERS ====================

    private fun getWeekRange(date: LocalDate): Pair<LocalDate, LocalDate> {
        val weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekEnd = weekStart.plusDays(6)
        return Pair(weekStart, weekEnd)
    }
}
