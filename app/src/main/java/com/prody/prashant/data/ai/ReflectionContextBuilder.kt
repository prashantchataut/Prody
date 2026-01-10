package com.prody.prashant.data.ai

import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.MicroEntryDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.model.EntrySummary
import com.prody.prashant.domain.model.JournalEntrySummary
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.model.MoodTrend
import com.prody.prashant.domain.model.ReflectionContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reflection Context Builder
 *
 * Builds rich context from user's journaling history for Buddha's enhanced responses.
 * This enables "Buddha's Memory" - allowing Buddha to:
 * - Reference past entries and themes
 * - Notice patterns over time
 * - Acknowledge growth and changes
 * - Ask connecting questions
 *
 * This is NOT an AI service - it's a context aggregator that prepares data
 * for more meaningful AI responses.
 */
@Singleton
class ReflectionContextBuilder @Inject constructor(
    private val journalDao: JournalDao,
    private val microEntryDao: MicroEntryDao,
    private val userDao: UserDao
) {
    companion object {
        private const val RECENT_ENTRIES_LIMIT = 10
        private const val MIN_THEME_OCCURRENCES = 3
        private const val MOOD_TREND_WINDOW_DAYS = 14
    }

    /**
     * Build full reflection context for a current journal entry.
     *
     * @param currentEntry The entry being reflected upon
     * @param userId User ID (default "local")
     * @return ReflectionContext with historical data for enhanced responses
     */
    suspend fun buildContext(
        currentEntry: JournalEntryEntity,
        userId: String = "local"
    ): ReflectionContext = withContext(Dispatchers.IO) {
        val allEntries = journalDao.getAllEntriesSync()
            .filter { !it.isDeleted && it.id != currentEntry.id }
            .sortedByDescending { it.createdAt }

        val recentEntries = allEntries.take(RECENT_ENTRIES_LIMIT)
        val recurringThemes = extractRecurringThemes(allEntries)
        val moodTrend = calculateMoodTrend(allEntries)
        val journeyStart = allEntries.minByOrNull { it.createdAt }?.createdAt
        val daysOnJourney = if (journeyStart != null) {
            ChronoUnit.DAYS.between(
                Instant.ofEpochMilli(journeyStart).atZone(ZoneId.systemDefault()).toLocalDate(),
                LocalDate.now()
            ).toInt()
        } else {
            0
        }

        val currentStreak = calculateCurrentStreak(allEntries)
        val lastEntryDate = allEntries.firstOrNull()?.let { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }

        ReflectionContext(
            currentEntry = currentEntry.toSummary(),
            recentEntries = recentEntries.map { it.toEntrySummary() },
            recurringThemes = recurringThemes,
            moodTrend = moodTrend,
            daysOnJourney = daysOnJourney,
            totalEntriesCount = allEntries.size,
            currentStreak = currentStreak,
            lastEntryDate = lastEntryDate
        )
    }

    /**
     * Build context for a new entry (no current entry yet).
     * Useful for generating starting prompts.
     */
    suspend fun buildContextForNewEntry(userId: String = "local"): ReflectionContext? = withContext(Dispatchers.IO) {
        val allEntries = journalDao.getAllEntriesSync()
            .filter { !it.isDeleted }
            .sortedByDescending { it.createdAt }

        if (allEntries.isEmpty()) return@withContext null

        val recentEntries = allEntries.take(RECENT_ENTRIES_LIMIT)
        val recurringThemes = extractRecurringThemes(allEntries)
        val moodTrend = calculateMoodTrend(allEntries)
        val journeyStart = allEntries.minByOrNull { it.createdAt }?.createdAt
        val daysOnJourney = if (journeyStart != null) {
            ChronoUnit.DAYS.between(
                Instant.ofEpochMilli(journeyStart).atZone(ZoneId.systemDefault()).toLocalDate(),
                LocalDate.now()
            ).toInt()
        } else {
            0
        }

        val currentStreak = calculateCurrentStreak(allEntries)
        val lastEntry = allEntries.first()
        val lastEntryDate = Instant.ofEpochMilli(lastEntry.createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        // Use placeholder for current entry since we're starting fresh
        val placeholderEntry = JournalEntrySummary(
            id = 0,
            content = "",
            mood = null,
            moodIntensity = null,
            wordCount = 0,
            createdAt = System.currentTimeMillis(),
            themes = emptyList(),
            title = null
        )

        ReflectionContext(
            currentEntry = placeholderEntry,
            recentEntries = recentEntries.map { it.toEntrySummary() },
            recurringThemes = recurringThemes,
            moodTrend = moodTrend,
            daysOnJourney = daysOnJourney,
            totalEntriesCount = allEntries.size,
            currentStreak = currentStreak,
            lastEntryDate = lastEntryDate
        )
    }

    /**
     * Get recent themes for Writing Companion prompts.
     */
    suspend fun getRecentThemes(userId: String = "local", limit: Int = 5): List<String> = withContext(Dispatchers.IO) {
        val recentEntries = journalDao.getAllEntriesSync()
            .filter { !it.isDeleted }
            .sortedByDescending { it.createdAt }
            .take(5)

        val themes = mutableListOf<String>()
        for (entry in recentEntries) {
            themes.addAll(extractThemesFromContent(entry.content))
        }

        themes.distinct().take(limit)
    }

    /**
     * Get the user's dominant mood from recent entries.
     */
    suspend fun getRecentDominantMood(userId: String = "local", daysBack: Int = 7): Mood? = withContext(Dispatchers.IO) {
        val cutoffTime = Instant.now().minus(daysBack.toLong(), ChronoUnit.DAYS).toEpochMilli()

        val recentEntries = journalDao.getAllEntriesSync()
            .filter { !it.isDeleted && it.createdAt >= cutoffTime }

        val moods = recentEntries.mapNotNull { it.mood }
        if (moods.isEmpty()) return@withContext null

        val dominantMoodName = moods.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        dominantMoodName?.let { Mood.fromString(it) }
    }

    /**
     * Generate a contextual prompt for Buddha based on history.
     * This creates an enhanced system prompt with context.
     */
    fun generateEnhancedSystemPrompt(context: ReflectionContext): String {
        val prompt = StringBuilder()

        prompt.appendLine("You are Buddha, a stoic AI mentor guiding the user on their self-improvement journey.")
        prompt.appendLine()

        // Add historical context
        if (context.hasSignificantHistory) {
            prompt.appendLine("=== USER CONTEXT ===")
            prompt.appendLine("This user has been journaling for ${context.daysOnJourney} days with ${context.totalEntriesCount} total entries.")

            if (context.currentStreak > 1) {
                prompt.appendLine("They're on a ${context.currentStreak}-day journaling streak.")
            }

            prompt.appendLine()
            prompt.appendLine("Recent emotional trend: ${context.getMoodTrendDescription()}")
            prompt.appendLine()

            if (context.recurringThemes.isNotEmpty()) {
                prompt.appendLine("Recurring themes in their journal: ${context.getThemesSummary()}")
                prompt.appendLine()
            }

            // Add recent entries context
            if (context.recentEntries.isNotEmpty()) {
                prompt.appendLine("Recent journal activity:")
                context.recentEntries.take(3).forEach { entry ->
                    prompt.appendLine("- ${entry.date}: ${entry.getDescription()}")
                }
                prompt.appendLine()
            }
        }

        // Add guidance for response
        prompt.appendLine("=== RESPONSE GUIDANCE ===")
        prompt.appendLine("- Reference their history when relevant (without being repetitive)")
        prompt.appendLine("- Notice patterns and growth over time")
        prompt.appendLine("- Ask connecting questions that tie to past entries")
        prompt.appendLine("- Acknowledge their consistency and progress")
        prompt.appendLine("- Be warm but wise, personal but not intrusive")
        prompt.appendLine("- Keep responses focused and meaningful, not lengthy")

        return prompt.toString()
    }

    /**
     * Find connections between current entry and past entries.
     */
    suspend fun findConnections(
        currentEntry: JournalEntryEntity,
        userId: String = "local"
    ): List<EntryConnection> = withContext(Dispatchers.IO) {
        val allEntries = journalDao.getAllEntriesSync()
            .filter { !it.isDeleted && it.id != currentEntry.id }
            .sortedByDescending { it.createdAt }

        val connections = mutableListOf<EntryConnection>()
        val currentThemes = extractThemesFromContent(currentEntry.content)

        // Find theme connections
        for (entry in allEntries.take(20)) {
            val entryThemes = extractThemesFromContent(entry.content)
            val sharedThemes = currentThemes.intersect(entryThemes.toSet())

            if (sharedThemes.isNotEmpty()) {
                connections.add(
                    EntryConnection(
                        entryId = entry.id,
                        connectionType = ConnectionType.SHARED_THEME,
                        connectionDetail = sharedThemes.first(),
                        entryDate = Instant.ofEpochMilli(entry.createdAt)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate(),
                        entryPreview = entry.content.take(100)
                    )
                )
            }
        }

        // Find mood connections
        currentEntry.mood?.let { currentMood ->
            val similarMoodEntries = allEntries
                .filter { it.mood == currentMood }
                .take(3)

            similarMoodEntries.forEach { entry ->
                if (connections.none { it.entryId == entry.id }) {
                    connections.add(
                        EntryConnection(
                            entryId = entry.id,
                            connectionType = ConnectionType.SIMILAR_MOOD,
                            connectionDetail = currentMood,
                            entryDate = Instant.ofEpochMilli(entry.createdAt)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                            entryPreview = entry.content.take(100)
                        )
                    )
                }
            }
        }

        connections.take(5)
    }

    // ==================== PRIVATE HELPERS ====================

    private fun extractRecurringThemes(entries: List<JournalEntryEntity>): List<String> {
        val themeCounts = mutableMapOf<String, Int>()

        entries.forEach { entry ->
            val themes = extractThemesFromContent(entry.content)
            themes.forEach { theme ->
                themeCounts[theme] = (themeCounts[theme] ?: 0) + 1
            }
        }

        return themeCounts
            .filter { it.value >= MIN_THEME_OCCURRENCES }
            .entries
            .sortedByDescending { it.value }
            .map { it.key }
            .take(10)
    }

    private fun extractThemesFromContent(content: String): List<String> {
        val lowerContent = content.lowercase()
        val themes = mutableListOf<String>()

        val themeKeywords = mapOf(
            "work" to listOf("work", "job", "career", "office", "meeting", "project", "boss", "colleague", "deadline"),
            "relationships" to listOf("friend", "family", "partner", "relationship", "love", "connection", "married", "dating"),
            "health" to listOf("health", "exercise", "sleep", "body", "fitness", "energy", "tired", "sick", "doctor"),
            "growth" to listOf("learn", "grow", "improve", "progress", "goal", "achievement", "better", "development"),
            "emotions" to listOf("feel", "feeling", "emotion", "mood", "anxiety", "stress", "worry", "fear"),
            "creativity" to listOf("create", "idea", "inspiration", "art", "music", "write", "design", "build"),
            "gratitude" to listOf("grateful", "thankful", "appreciate", "blessed", "lucky", "fortune"),
            "challenges" to listOf("challenge", "problem", "struggle", "difficult", "hard", "obstacle", "issue"),
            "finances" to listOf("money", "finance", "budget", "savings", "invest", "debt", "income", "expense"),
            "spirituality" to listOf("spiritual", "meditate", "mindful", "soul", "purpose", "meaning", "faith")
        )

        for ((theme, keywords) in themeKeywords) {
            if (keywords.any { lowerContent.contains(it) }) {
                themes.add(theme)
            }
        }

        return themes
    }

    private fun calculateMoodTrend(entries: List<JournalEntryEntity>): MoodTrend {
        val cutoffTime = Instant.now().minus(MOOD_TREND_WINDOW_DAYS.toLong(), ChronoUnit.DAYS).toEpochMilli()
        val recentEntries = entries.filter { it.createdAt >= cutoffTime }

        if (recentEntries.size < 3) return MoodTrend.INSUFFICIENT_DATA

        val sortedEntries = recentEntries.sortedBy { it.createdAt }
        val moods = sortedEntries.mapNotNull { it.mood }

        if (moods.size < 3) return MoodTrend.INSUFFICIENT_DATA

        val positiveMoods = setOf("HAPPY", "CALM", "MOTIVATED", "GRATEFUL", "EXCITED")
        val negativeMoods = setOf("ANXIOUS", "SAD", "CONFUSED")

        val firstHalf = moods.take(moods.size / 2)
        val secondHalf = moods.drop(moods.size / 2)

        val firstHalfScore = firstHalf.count { it in positiveMoods } - firstHalf.count { it in negativeMoods }
        val secondHalfScore = secondHalf.count { it in positiveMoods } - secondHalf.count { it in negativeMoods }

        val scoreDiff = secondHalfScore - firstHalfScore

        // Check for variability
        val moodChanges = moods.zipWithNext().count { (a, b) -> a != b }
        val variabilityRatio = moodChanges.toFloat() / moods.size

        return when {
            variabilityRatio > 0.7 -> MoodTrend.VARIABLE
            scoreDiff >= 2 -> MoodTrend.IMPROVING
            scoreDiff <= -2 -> MoodTrend.DECLINING
            else -> MoodTrend.STABLE
        }
    }

    private fun calculateCurrentStreak(entries: List<JournalEntryEntity>): Int {
        if (entries.isEmpty()) return 0

        val sortedEntries = entries.sortedByDescending { it.createdAt }
        val entryDates = sortedEntries.map { entry ->
            Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.distinct()

        if (entryDates.isEmpty()) return 0

        val today = LocalDate.now()
        val mostRecentDate = entryDates.first()

        // If last entry was more than 1 day ago, streak is broken
        if (ChronoUnit.DAYS.between(mostRecentDate, today) > 1) {
            return 0
        }

        var streak = 1
        var expectedDate = if (mostRecentDate == today) today.minusDays(1) else mostRecentDate.minusDays(1)

        for (i in 1 until entryDates.size) {
            if (entryDates[i] == expectedDate) {
                streak++
                expectedDate = expectedDate.minusDays(1)
            } else if (entryDates[i] < expectedDate) {
                break
            }
        }

        return streak
    }

    private fun JournalEntryEntity.toSummary(): JournalEntrySummary {
        return JournalEntrySummary(
            id = id,
            content = content,
            mood = mood?.let { Mood.fromString(it) },
            moodIntensity = moodIntensity,
            wordCount = wordCount,
            createdAt = createdAt,
            themes = extractThemesFromContent(content),
            title = title
        )
    }

    private fun JournalEntryEntity.toEntrySummary(): EntrySummary {
        return EntrySummary(
            id = id,
            date = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).toLocalDate(),
            mood = mood?.let { Mood.fromString(it) },
            keyThemes = extractThemesFromContent(content).take(3),
            wordCount = wordCount,
            firstLine = content.split("\n").firstOrNull()?.take(100) ?: ""
        )
    }
}

/**
 * Represents a connection between entries.
 */
data class EntryConnection(
    val entryId: Long,
    val connectionType: ConnectionType,
    val connectionDetail: String,
    val entryDate: LocalDate,
    val entryPreview: String
)

/**
 * Types of connections between entries.
 */
enum class ConnectionType {
    SHARED_THEME,
    SIMILAR_MOOD,
    ANNIVERSARY,
    FOLLOW_UP
}
