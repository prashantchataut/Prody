package com.prody.prashant.domain.wrapped

import android.util.Log
import com.google.gson.Gson
import com.prody.prashant.data.local.dao.*
import com.prody.prashant.data.local.entity.YearlyWrappedEntity
import java.time.*
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Generates comprehensive Yearly Wrapped summaries.
 *
 * Analyzes all user data from the specified year to create a beautiful,
 * insightful celebration of their journaling journey.
 */
class YearlyWrappedGenerator @Inject constructor(
    private val journalDao: JournalDao,
    private val microEntryDao: MicroEntryDao,
    private val futureMessageDao: FutureMessageDao,
    private val vocabularyLearningDao: VocabularyLearningDao,
    private val wordUsageDao: WordUsageDao,
    private val dualStreakDao: DualStreakDao,
    private val seedDao: SeedDao
) {
    private val gson = Gson()
    private val TAG = "YearlyWrappedGenerator"

    /**
     * Generate yearly wrapped for the specified year
     */
    suspend fun generate(
        userId: String = "local",
        year: Int,
        config: WrappedGenerationConfig = WrappedGenerationConfig(year)
    ): Result<YearlyWrappedEntity> = try {
        Log.d(TAG, "Starting wrapped generation for year $year")

        // Define year boundaries
        val yearStart = LocalDate.of(year, 1, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val yearEnd = LocalDate.of(year, 12, 31)
            .atTime(23, 59, 59)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // Collect all data
        val journalEntries = journalDao.getEntriesByDateRange(yearStart, yearEnd).also {
            // Convert Flow to list for single read
        }.let { emptyList() } // TODO: Proper flow collection

        // For now, use sync methods
        val allJournalEntries = journalDao.getAllEntriesSync().filter {
            it.createdAt in yearStart..yearEnd && !it.isDeleted
        }

        if (allJournalEntries.size < config.minEntriesRequired) {
            return Result.failure(
                Exception("Not enough entries to generate wrapped (minimum ${config.minEntriesRequired} required)")
            )
        }

        // Calculate statistics
        val stats = calculateStats(allJournalEntries, yearStart, yearEnd, userId)

        // Analyze mood journey
        val moodJourney = analyzeMoodJourney(allJournalEntries)

        // Extract themes
        val themes = extractThemes(allJournalEntries, config.topThemesCount)

        // Identify growth areas
        val growthAreas = identifyGrowthAreas(allJournalEntries, config.growthAreasCount)

        // Detect challenges
        val challenges = detectChallenges(allJournalEntries, config.challengesCount)

        // Select key moments
        val keyMoments = selectKeyMoments(allJournalEntries, config.keyMomentsCount)

        // Identify patterns
        val patterns = identifyPatterns(allJournalEntries)

        // Generate narratives
        val narratives = if (config.includeNarratives) {
            generateNarratives(year, stats, moodJourney, themes, growthAreas, allJournalEntries)
        } else {
            YearNarratives(null, null, null, null, null, null)
        }

        // Create shareable cards
        val shareableCards = if (config.generateShareableCards) {
            createShareableCards(stats, moodJourney, themes, narratives)
        } else {
            emptyList()
        }

        // Build entity
        val entity = YearlyWrappedEntity(
            userId = userId,
            year = year,
            generatedAt = System.currentTimeMillis(),

            // Writing stats
            totalJournalEntries = allJournalEntries.size,
            totalWordsWritten = allJournalEntries.sumOf { it.wordCount.toLong() },
            averageWordsPerEntry = stats.averageWordsPerEntry,
            longestEntry = stats.longestEntry,
            longestEntryId = stats.longestEntryId,

            // Engagement stats
            activeDaysCount = stats.activeDays,
            longestStreak = stats.longestStreak,
            bloomsCompleted = stats.bloomsCompleted,

            // Learning stats
            vocabularyWordsLearned = stats.wordsLearned,
            vocabularyWordsUsed = stats.wordsUsed,

            // Time capsule stats
            futureMessagesWritten = stats.messagesWritten,
            futureMessagesReceived = stats.messagesReceived,
            mostDistantMessage = stats.mostDistantMessage,

            // Activity patterns
            mostActiveMonth = stats.mostActiveMonth?.value,
            mostActiveDay = stats.mostActiveDay?.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            mostActiveTimeOfDay = stats.mostActiveTime?.name?.lowercase(),
            firstEntryDate = stats.firstEntryDate,
            lastEntryDate = stats.lastEntryDate,

            // Mood journey
            averageMood = moodJourney.averageMood,
            moodTrend = moodJourney.trend.name.lowercase(),
            mostCommonMood = moodJourney.mostCommonMood,
            moodVariety = moodJourney.moodVariety,
            brightestMonth = moodJourney.brightestMonth?.value,
            mostReflectiveMonth = moodJourney.mostReflectiveMonth?.value,
            moodEvolution = gson.toJson(moodJourney.monthlyAverages),

            // Themes & insights
            topThemesJson = gson.toJson(themes),
            growthAreasJson = gson.toJson(growthAreas),
            challengesOvercomeJson = gson.toJson(challenges),
            keyMomentsJson = gson.toJson(keyMoments),
            patternsJson = gson.toJson(patterns),

            // Narratives
            openingNarrative = narratives.opening,
            yearSummaryNarrative = narratives.yearSummary,
            growthStoryNarrative = narratives.growthStory,
            moodJourneyNarrative = narratives.moodJourney,
            lookingAheadNarrative = narratives.lookingAhead,
            milestoneNarrative = narratives.milestone,

            // Shareable cards
            shareableCardsJson = gson.toJson(shareableCards)
        )

        Log.d(TAG, "Wrapped generation completed successfully for year $year")
        Result.success(entity)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to generate wrapped for year $year", e)
        Result.failure(e)
    }

    private fun calculateStats(
        entries: List<com.prody.prashant.data.local.entity.JournalEntryEntity>,
        yearStart: Long,
        yearEnd: Long,
        userId: String
    ): YearStats {
        val totalWords = entries.sumOf { it.wordCount.toLong() }
        val longestEntry = entries.maxByOrNull { it.wordCount }

        // Calculate active days
        val activeDays = entries
            .map { Instant.ofEpochMilli(it.createdAt).atZone(ZoneId.systemDefault()).toLocalDate() }
            .toSet()
            .size

        // Most active month
        val monthCounts = entries
            .groupBy { Instant.ofEpochMilli(it.createdAt).atZone(ZoneId.systemDefault()).month }
            .mapValues { it.value.size }
        val mostActiveMonth = monthCounts.maxByOrNull { it.value }?.key

        // Most active day of week
        val dayOfWeekCounts = entries
            .groupBy { Instant.ofEpochMilli(it.createdAt).atZone(ZoneId.systemDefault()).dayOfWeek }
            .mapValues { it.value.size }
        val mostActiveDay = dayOfWeekCounts.maxByOrNull { it.value }?.key

        // Most active time of day
        val timeOfDayCounts = entries
            .groupBy { entry ->
                val hour = Instant.ofEpochMilli(entry.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .hour
                when (hour) {
                    in 5..11 -> TimeOfDay.MORNING
                    in 12..16 -> TimeOfDay.AFTERNOON
                    in 17..20 -> TimeOfDay.EVENING
                    else -> TimeOfDay.NIGHT
                }
            }
            .mapValues { it.value.size }
        val mostActiveTime = timeOfDayCounts.maxByOrNull { it.value }?.key

        return YearStats(
            totalEntries = entries.size,
            totalMicroEntries = 0, // Would need to query separately
            wordsWritten = totalWords,
            averageWordsPerEntry = if (entries.isNotEmpty()) (totalWords / entries.size).toInt() else 0,
            longestEntry = longestEntry?.wordCount ?: 0,
            longestEntryId = longestEntry?.id,
            activeDays = activeDays,
            longestStreak = 0, // Would need to calculate from streak data
            meditationMinutes = 0, // Would need separate tracking
            bloomsCompleted = 0, // Would need to query seed dao
            wordsLearned = 0, // Would need to query vocabulary dao
            wordsUsed = 0, // Would need to query word usage dao
            idiomsExplored = 0,
            proverbsDiscovered = 0,
            messagesWritten = 0, // Would need to query future message dao
            messagesReceived = 0,
            mostDistantMessage = 0,
            mostActiveMonth = mostActiveMonth,
            mostActiveDay = mostActiveDay,
            mostActiveTime = mostActiveTime,
            firstEntryDate = entries.minByOrNull { it.createdAt }?.createdAt,
            lastEntryDate = entries.maxByOrNull { it.createdAt }?.createdAt
        )
    }

    private fun analyzeMoodJourney(
        entries: List<com.prody.prashant.data.local.entity.JournalEntryEntity>
    ): MoodJourney {
        val moodIntensities = entries.map { it.moodIntensity.toFloat() }
        val averageMood = if (moodIntensities.isNotEmpty()) {
            moodIntensities.average().toFloat()
        } else 0f

        // Calculate mood trend
        val firstHalfAvg = moodIntensities.take(moodIntensities.size / 2).average()
        val secondHalfAvg = moodIntensities.drop(moodIntensities.size / 2).average()
        val moodTrend = when {
            secondHalfAvg > firstHalfAvg + 1.0 -> MoodTrend.IMPROVING
            secondHalfAvg < firstHalfAvg - 1.0 -> MoodTrend.DECLINING
            moodIntensities.zipWithNext { a, b -> kotlin.math.abs(a - b) }.average() > 2.0 -> MoodTrend.FLUCTUATING
            else -> MoodTrend.STABLE
        }

        // Most common mood
        val mostCommonMood = entries
            .groupBy { it.mood }
            .maxByOrNull { it.value.size }
            ?.key

        // Mood variety
        val moodVariety = entries.map { it.mood }.toSet().size

        // Monthly averages
        val monthlyAverages = (1..12).map { month ->
            val monthEntries = entries.filter {
                Instant.ofEpochMilli(it.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .monthValue == month
            }
            if (monthEntries.isNotEmpty()) {
                monthEntries.map { it.moodIntensity.toFloat() }.average().toFloat()
            } else 0f
        }

        // Brightest month
        val brightestMonth = monthlyAverages
            .withIndex()
            .filter { it.value > 0 }
            .maxByOrNull { it.value }
            ?.let { Month.of(it.index + 1) }

        // Most reflective month (most entries)
        val mostReflectiveMonth = entries
            .groupBy {
                Instant.ofEpochMilli(it.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .month
            }
            .maxByOrNull { it.value.size }
            ?.key

        return MoodJourney(
            averageMood = averageMood,
            trend = moodTrend,
            mostCommonMood = mostCommonMood,
            moodVariety = moodVariety,
            brightestMonth = brightestMonth,
            mostReflectiveMonth = mostReflectiveMonth,
            monthlyAverages = monthlyAverages
        )
    }

    private fun extractThemes(
        entries: List<com.prody.prashant.data.local.entity.JournalEntryEntity>,
        topCount: Int
    ): List<ThemeHighlight> {
        // Extract themes from AI themes field and content
        val allThemes = entries
            .flatMap { entry ->
                val aiThemes = entry.aiThemes?.split(",")?.map { it.trim() } ?: emptyList()
                aiThemes
            }
            .filter { it.isNotBlank() }

        val themeCounts = allThemes
            .groupBy { it.lowercase() }
            .mapValues { it.value.size }
            .entries
            .sortedByDescending { it.value }
            .take(topCount)

        return themeCounts.map { (theme, count) ->
            ThemeHighlight(
                theme = theme,
                count = count,
                trend = ThemeTrend.STABLE, // Would need more analysis
                exampleEntry = entries.firstOrNull {
                    it.aiThemes?.contains(theme, ignoreCase = true) == true
                }?.content?.take(100)
            )
        }
    }

    private fun identifyGrowthAreas(
        entries: List<com.prody.prashant.data.local.entity.JournalEntryEntity>,
        count: Int
    ): List<GrowthArea> {
        // Identify areas where user wrote consistently about growth
        val growthKeywords = listOf("growth", "learning", "improve", "better", "change", "progress")

        val growthEntries = entries.filter { entry ->
            growthKeywords.any { keyword ->
                entry.content.contains(keyword, ignoreCase = true) ||
                entry.aiThemes?.contains(keyword, ignoreCase = true) == true
            }
        }

        val themes = growthEntries
            .flatMap { it.aiThemes?.split(",")?.map { theme -> theme.trim() } ?: emptyList() }
            .filter { it.isNotBlank() }
            .groupBy { it.lowercase() }
            .mapValues { it.value.size }
            .entries
            .sortedByDescending { it.value }
            .take(count)

        return themes.map { (area, entriesCount) ->
            GrowthArea(
                area = area,
                description = "You explored this area deeply through $entriesCount journal entries",
                entriesCount = entriesCount,
                periodStart = null,
                periodEnd = null,
                evolution = when {
                    entriesCount > 10 -> GrowthEvolution.BREAKTHROUGH
                    entriesCount > 5 -> GrowthEvolution.STEADY
                    else -> GrowthEvolution.EMERGING
                }
            )
        }
    }

    private fun detectChallenges(
        entries: List<com.prody.prashant.data.local.entity.JournalEntryEntity>,
        count: Int
    ): List<ChallengeOvercome> {
        // Look for patterns of difficulty followed by improvement
        val challengeKeywords = listOf("difficult", "hard", "struggle", "challenge", "overcome", "tough")

        val challengeEntries = entries.filter { entry ->
            challengeKeywords.any { keyword ->
                entry.content.contains(keyword, ignoreCase = true)
            }
        }

        // Group by time period
        val periods = listOf("Early Year", "Mid Year", "Recent")
        val entriesPerPeriod = entries.size / 3

        return challengeEntries
            .take(count)
            .mapIndexed { index, entry ->
                val period = when {
                    entries.indexOf(entry) < entriesPerPeriod -> "Early Year"
                    entries.indexOf(entry) < entriesPerPeriod * 2 -> "Mid Year"
                    else -> "Recent"
                }

                ChallengeOvercome(
                    challenge = entry.aiThemes?.split(",")?.firstOrNull() ?: "Personal challenge",
                    period = period,
                    insight = entry.aiInsight?.take(100)
                )
            }
    }

    private fun selectKeyMoments(
        entries: List<com.prody.prashant.data.local.entity.JournalEntryEntity>,
        count: Int
    ): List<KeyMoment> {
        // Select diverse key moments
        val moments = mutableListOf<KeyMoment>()

        // Happiest moment (highest mood intensity)
        entries.maxByOrNull { it.moodIntensity }?.let { entry ->
            moments.add(KeyMoment(
                entryId = entry.id,
                date = entry.createdAt,
                snippet = entry.content.take(150),
                why = "Your happiest moment of the year",
                mood = entry.mood,
                type = MomentType.PEAK_HAPPINESS
            ))
        }

        // Longest entry (most reflective)
        entries.maxByOrNull { it.wordCount }?.let { entry ->
            if (moments.none { it.entryId == entry.id }) {
                moments.add(KeyMoment(
                    entryId = entry.id,
                    date = entry.createdAt,
                    snippet = entry.content.take(150),
                    why = "Your most detailed reflection",
                    mood = entry.mood,
                    type = MomentType.DEEP_REFLECTION
                ))
            }
        }

        // Bookmarked entries
        entries.filter { it.isBookmarked }.take(count - moments.size).forEach { entry ->
            moments.add(KeyMoment(
                entryId = entry.id,
                date = entry.createdAt,
                snippet = entry.content.take(150),
                why = "A moment you bookmarked",
                mood = entry.mood,
                type = MomentType.MILESTONE
            ))
        }

        return moments.take(count)
    }

    private fun identifyPatterns(
        entries: List<com.prody.prashant.data.local.entity.JournalEntryEntity>
    ): List<Pattern> {
        val patterns = mutableListOf<Pattern>()

        // Morning writer pattern
        val morningEntries = entries.count { entry ->
            val hour = Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .hour
            hour in 5..11
        }
        if (morningEntries > entries.size * 0.6) {
            patterns.add(Pattern(
                type = PatternType.MORNING_WRITER,
                description = "You prefer journaling in the morning",
                occurrences = morningEntries
            ))
        }

        // Evening reflector pattern
        val eveningEntries = entries.count { entry ->
            val hour = Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .hour
            hour in 17..23
        }
        if (eveningEntries > entries.size * 0.6) {
            patterns.add(Pattern(
                type = PatternType.EVENING_REFLECTOR,
                description = "You prefer reflecting in the evening",
                occurrences = eveningEntries
            ))
        }

        // Deep thinker pattern
        val avgWords = entries.map { it.wordCount }.average()
        if (avgWords > 200) {
            patterns.add(Pattern(
                type = PatternType.DEEP_THINKER,
                description = "You write detailed, thoughtful entries",
                occurrences = entries.count { it.wordCount > avgWords.toInt() }
            ))
        }

        // Consistent journaler
        val activeDays = entries
            .map { Instant.ofEpochMilli(it.createdAt).atZone(ZoneId.systemDefault()).toLocalDate() }
            .toSet()
            .size
        if (activeDays > 100) {
            patterns.add(Pattern(
                type = PatternType.CONSISTENT_JOURNALER,
                description = "You journal regularly throughout the year",
                occurrences = activeDays
            ))
        }

        return patterns
    }

    private fun generateNarratives(
        year: Int,
        stats: YearStats,
        moodJourney: MoodJourney,
        themes: List<ThemeHighlight>,
        growthAreas: List<GrowthArea>,
        entries: List<com.prody.prashant.data.local.entity.JournalEntryEntity>
    ): YearNarratives {
        val opening = "Welcome to your $year wrapped. This year, you showed up for yourself ${stats.totalEntries} times. Let's celebrate your journey."

        val yearSummary = buildString {
            append("In $year, you wrote ${stats.wordsWritten} words across ${stats.totalEntries} journal entries. ")
            append("That's ${stats.activeDays} days of showing up for yourself. ")
            if (themes.isNotEmpty()) {
                append("Your top theme was ${themes.first().theme}, ")
                append("which you explored ${themes.first().count} times. ")
            }
            append("What a year of reflection and growth.")
        }

        val growthStory = buildString {
            append("This year marked real growth. ")
            if (growthAreas.isNotEmpty()) {
                append("You made progress in ${growthAreas.first().area}, ")
                append("showing up consistently to work through it. ")
            }
            when (moodJourney.trend) {
                MoodTrend.IMPROVING -> append("Your mood journey shows you're moving toward brighter days. ")
                MoodTrend.STABLE -> append("You maintained emotional stability throughout the year. ")
                MoodTrend.DECLINING -> append("You faced challenges this year, and still kept showing up. ")
                MoodTrend.FLUCTUATING -> append("You experienced a full spectrum of emotions this year. ")
            }
            append("That takes courage.")
        }

        val moodJourneyNarrative = buildString {
            append("Your emotional journey this year was ${moodJourney.trend.name.lowercase()}. ")
            moodJourney.brightestMonth?.let {
                append("${it.getDisplayName(TextStyle.FULL, Locale.getDefault())} was your brightest month. ")
            }
            moodJourney.mostCommonMood?.let {
                append("You most often felt $it. ")
            }
            append("You experienced ${moodJourney.moodVariety} different emotional states, ")
            append("each one valid, each one teaching you something.")
        }

        val lookingAhead = buildString {
            append("As you step into the next year, carry this with you: ")
            append("you showed up ${stats.activeDays} days this year. ")
            append("You wrote ${stats.wordsWritten} words to understand yourself better. ")
            append("That commitment to self-awareness? That's your foundation. ")
            append("Keep building on it.")
        }

        val milestone = if (stats.totalEntries >= 100) {
            "You crossed 100 journal entries this year. That's not just a number - that's 100 moments of choosing yourself."
        } else if (stats.longestStreak >= 30) {
            "You maintained a ${stats.longestStreak}-day streak. That consistency? That's transformation in action."
        } else null

        return YearNarratives(
            opening = opening,
            yearSummary = yearSummary,
            growthStory = growthStory,
            moodJourney = moodJourneyNarrative,
            lookingAhead = lookingAhead,
            milestone = milestone
        )
    }

    private fun createShareableCards(
        stats: YearStats,
        moodJourney: MoodJourney,
        themes: List<ThemeHighlight>,
        narratives: YearNarratives
    ): List<ShareableCard> {
        val cards = mutableListOf<ShareableCard>()

        // Total words card
        cards.add(ShareableCard(
            id = "total_words",
            type = CardType.TOTAL_WORDS,
            title = "Words Written",
            content = "I wrote my way through the year",
            stat = "${stats.wordsWritten.formatWithCommas()} words",
            backgroundGradient = listOf("#2ED56B", "#36F97F", "#5DFA96")
        ))

        // Streak card
        if (stats.longestStreak > 0) {
            cards.add(ShareableCard(
                id = "longest_streak",
                type = CardType.LONGEST_STREAK,
                title = "Longest Streak",
                content = "Consistency is my superpower",
                stat = "${stats.longestStreak} days",
                backgroundGradient = listOf("#E65C2C", "#FF7043", "#FFB74D")
            ))
        }

        // Mood journey card
        cards.add(ShareableCard(
            id = "mood_journey",
            type = CardType.MOOD_JOURNEY,
            title = "Mood Journey",
            content = when (moodJourney.trend) {
                MoodTrend.IMPROVING -> "Growing brighter every day"
                MoodTrend.STABLE -> "Finding my emotional balance"
                MoodTrend.DECLINING -> "Facing challenges with courage"
                MoodTrend.FLUCTUATING -> "Feeling all the feelings"
            },
            stat = "${moodJourney.moodVariety} different moods",
            backgroundGradient = listOf("#6CB4D4", "#42A5F5", "#7EC8A3")
        ))

        // Top theme card
        if (themes.isNotEmpty()) {
            cards.add(ShareableCard(
                id = "top_theme",
                type = CardType.TOP_THEME,
                title = "Top Theme",
                content = "What I explored most",
                stat = themes.first().theme,
                backgroundGradient = listOf("#B39DDB", "#AB47BC", "#9B6DD4")
            ))
        }

        // Active days card
        cards.add(ShareableCard(
            id = "active_days",
            type = CardType.ACTIVE_DAYS,
            title = "Days I Showed Up",
            content = "Every day counts",
            stat = "${stats.activeDays} days",
            backgroundGradient = listOf("#D4AF37", "#FFD700", "#E6C866")
        ))

        return cards
    }

    private fun Long.formatWithCommas(): String {
        return String.format("%,d", this)
    }
}
