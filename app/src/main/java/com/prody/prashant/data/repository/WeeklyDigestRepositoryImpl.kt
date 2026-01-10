package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.MicroEntryDao
import com.prody.prashant.data.local.dao.WeeklyDigestDao
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.data.local.entity.MicroEntryEntity
import com.prody.prashant.data.local.entity.WeeklyDigestEntity
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.repository.WeeklyDigestRepository
import com.prody.prashant.domain.summary.WeeklySummaryEngine
import com.prody.prashant.util.BuddhaWisdom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WeeklyDigestRepository using Room database.
 * Now enhanced with WeeklySummaryEngine for richer insights.
 */
@Singleton
class WeeklyDigestRepositoryImpl @Inject constructor(
    private val weeklyDigestDao: WeeklyDigestDao,
    private val journalDao: JournalDao,
    private val microEntryDao: MicroEntryDao,
    private val weeklySummaryEngine: WeeklySummaryEngine
) : WeeklyDigestRepository {

    // ==================== RETRIEVAL ====================

    override fun getAllDigests(userId: String): Flow<List<WeeklyDigestEntity>> {
        return weeklyDigestDao.getAllDigests(userId)
    }

    override suspend fun getLatestDigest(userId: String): Result<WeeklyDigestEntity?> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get latest digest") {
            weeklyDigestDao.getLatestDigest(userId)
        }
    }

    override fun observeLatestDigest(userId: String): Flow<WeeklyDigestEntity?> {
        return weeklyDigestDao.observeLatestDigest(userId)
    }

    override suspend fun getDigestById(id: Long): Result<WeeklyDigestEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to load weekly digest") {
            weeklyDigestDao.getDigestById(id)
                ?: throw NoSuchElementException("Weekly digest not found")
        }
    }

    override fun observeDigestById(id: Long): Flow<WeeklyDigestEntity?> {
        return weeklyDigestDao.observeDigestById(id)
    }

    override suspend fun getDigestForWeek(userId: String, weekStartDate: Long): Result<WeeklyDigestEntity?> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get digest for week") {
            weeklyDigestDao.getDigestForWeek(userId, weekStartDate)
        }
    }

    override fun getUnreadDigests(userId: String): Flow<List<WeeklyDigestEntity>> {
        return weeklyDigestDao.getUnreadDigests(userId)
    }

    // ==================== CREATE/UPDATE ====================

    override suspend fun saveDigest(digest: WeeklyDigestEntity): Result<Long> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save weekly digest") {
            weeklyDigestDao.insertDigest(digest)
        }
    }

    override suspend fun markDigestAsRead(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to mark digest as read") {
            weeklyDigestDao.markAsRead(id, System.currentTimeMillis())
        }
    }

    override suspend fun markAllAsRead(userId: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to mark all digests as read") {
            weeklyDigestDao.markAllAsRead(userId, System.currentTimeMillis())
        }
    }

    // ==================== GENERATION ====================

    override suspend fun generateWeeklyDigest(userId: String): Result<WeeklyDigestEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to generate weekly digest") {
            val previousWeekDate = LocalDate.now().minusWeeks(1)
            val (weekStart, weekEnd) = getWeekRange(previousWeekDate)
            val weekStartDate = weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val weekEndDate = weekEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            // Check if already exists
            val existing = weeklyDigestDao.getDigestForWeek(userId, weekStartDate)
            if (existing != null) {
                return@runSuspendCatching existing
            }

            // Use the new WeeklySummaryEngine for comprehensive analysis
            val summary = weeklySummaryEngine.generate(userId, previousWeekDate)

            // Convert WeeklySummary to WeeklyDigestEntity
            val digest = WeeklyDigestEntity(
                userId = userId,
                weekStartDate = weekStartDate,
                weekEndDate = weekEndDate,
                entriesCount = summary.entriesCount,
                microEntriesCount = summary.microEntriesCount,
                totalWordsWritten = summary.totalWords,
                activeDays = summary.activeDays,
                averageWordsPerEntry = summary.averageWordsPerEntry,
                dominantMood = summary.dominantMood?.name,
                moodTrend = summary.moodTrend.name.lowercase(),
                moodDistribution = formatMoodDistribution(summary.moodDistribution),
                topThemes = summary.topThemes.joinToString(","),
                recurringPatterns = summary.patterns.take(5).map { it.type.name.lowercase() }.joinToString(","),
                buddhaReflection = summary.buddhaInsight,
                entriesChangePercent = summary.previousWeekComparison?.entriesChangePercent ?: 0,
                wordsChangePercent = summary.previousWeekComparison?.wordsChangePercent ?: 0,
                previousWeekEntriesCount = if (summary.previousWeekComparison != null) {
                    summary.entriesCount - summary.previousWeekComparison.entriesChange
                } else 0,
                previousWeekWordsWritten = if (summary.previousWeekComparison != null) {
                    summary.totalWords - summary.previousWeekComparison.wordsChange
                } else 0,
                highlightEntryId = summary.highlightEntry?.id,
                highlightQuote = summary.highlightEntry?.let { extractHighlightQuote(it.content) },
                generatedAt = System.currentTimeMillis()
            )

            val id = weeklyDigestDao.insertDigest(digest)
            digest.copy(id = id)
        }
    }

    override suspend fun hasDigestForCurrentWeek(userId: String): Boolean {
        val (weekStartDate, _) = getCurrentWeekRange()
        return weeklyDigestDao.hasDigestForWeek(userId, weekStartDate)
    }

    override suspend fun hasDigestForWeek(userId: String, weekStartDate: Long): Boolean {
        return weeklyDigestDao.hasDigestForWeek(userId, weekStartDate)
    }

    // ==================== STATISTICS ====================

    override fun getDigestCount(userId: String): Flow<Int> {
        return weeklyDigestDao.getDigestCount(userId)
    }

    override suspend fun getUnreadCount(userId: String): Int {
        return weeklyDigestDao.getUnreadCount(userId)
    }

    // ==================== CLEANUP ====================

    override suspend fun cleanupOldDigests(userId: String, keepCount: Int): Int {
        return weeklyDigestDao.cleanupOldDigests(userId, keepCount)
    }

    override suspend fun purgeSoftDeleted(): Int {
        return weeklyDigestDao.purgeSoftDeleted()
    }

    override suspend fun deleteDigest(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete digest") {
            weeklyDigestDao.deleteDigestById(id)
        }
    }

    // ==================== PRIVATE HELPERS ====================

    private fun formatMoodDistribution(distribution: Map<Mood, Int>): String {
        if (distribution.isEmpty()) return ""
        return distribution.entries.joinToString(",") { "${it.key.name}:${it.value}" }
    }

    private fun getWeekRange(date: LocalDate): Pair<LocalDate, LocalDate> {
        val weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekEnd = weekStart.plusDays(6)
        return Pair(weekStart, weekEnd)
    }

    private fun getCurrentWeekRange(): Pair<Long, Long> {
        val now = LocalDate.now()
        val weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekEnd = weekStart.plusDays(6)
        return Pair(
            weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            weekEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    private fun getPreviousWeekRange(fromDate: Long? = null): Pair<Long, Long> {
        val referenceDate = if (fromDate != null) {
            java.time.Instant.ofEpochMilli(fromDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .minusWeeks(1)
        } else {
            LocalDate.now().minusWeeks(1)
        }
        val weekStart = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekEnd = weekStart.plusDays(6)
        return Pair(
            weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            weekEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    private fun calculateActiveDays(
        entries: List<JournalEntryEntity>,
        microEntries: List<MicroEntryEntity>,
        weekStart: Long
    ): Int {
        val entryDays = entries.map { entry ->
            java.time.Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSet()

        val microDays = microEntries.map { entry ->
            java.time.Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSet()

        return (entryDays + microDays).size
    }

    private fun calculateDominantMood(
        entries: List<JournalEntryEntity>,
        microEntries: List<MicroEntryEntity>
    ): String? {
        val allMoods = entries.mapNotNull { it.mood } + microEntries.mapNotNull { it.mood }
        if (allMoods.isEmpty()) return null

        return allMoods.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
    }

    private fun calculateMoodDistribution(
        entries: List<JournalEntryEntity>,
        microEntries: List<MicroEntryEntity>
    ): String {
        val allMoods = entries.mapNotNull { it.mood } + microEntries.mapNotNull { it.mood }
        if (allMoods.isEmpty()) return ""

        val distribution = allMoods.groupingBy { it }.eachCount()
        return distribution.entries.joinToString(",") { "${it.key}:${it.value}" }
    }

    private fun calculateMoodTrend(entries: List<JournalEntryEntity>): String {
        if (entries.size < 3) return "stable"

        val sortedEntries = entries.sortedBy { it.createdAt }
        val firstHalf = sortedEntries.take(sortedEntries.size / 2)
        val secondHalf = sortedEntries.drop(sortedEntries.size / 2)

        val positiveMoods = setOf("HAPPY", "CALM", "MOTIVATED", "GRATEFUL", "EXCITED")

        val firstHalfPositive = firstHalf.count { it.mood in positiveMoods }.toFloat() / firstHalf.size
        val secondHalfPositive = secondHalf.count { it.mood in positiveMoods }.toFloat() / secondHalf.size

        val diff = secondHalfPositive - firstHalfPositive

        return when {
            diff > 0.2 -> "improving"
            diff < -0.2 -> "declining"
            else -> "stable"
        }
    }

    private fun extractTopThemes(
        entries: List<JournalEntryEntity>,
        microEntries: List<MicroEntryEntity>
    ): String {
        val allContent = entries.map { it.content.lowercase() } +
            microEntries.map { it.content.lowercase() }

        if (allContent.isEmpty()) return ""

        val themeKeywords = mapOf(
            "work" to listOf("work", "job", "career", "office", "meeting", "project", "boss", "colleague"),
            "relationships" to listOf("friend", "family", "partner", "relationship", "love", "connection"),
            "health" to listOf("health", "exercise", "sleep", "body", "fitness", "energy"),
            "growth" to listOf("learn", "grow", "improve", "progress", "goal", "achievement"),
            "emotions" to listOf("feel", "feeling", "emotion", "mood", "anxiety", "stress"),
            "creativity" to listOf("create", "idea", "inspiration", "art", "music", "write"),
            "gratitude" to listOf("grateful", "thankful", "appreciate", "blessed", "lucky"),
            "challenges" to listOf("challenge", "problem", "struggle", "difficult", "hard", "obstacle")
        )

        val themeCounts = mutableMapOf<String, Int>()

        for (content in allContent) {
            for ((theme, keywords) in themeKeywords) {
                if (keywords.any { content.contains(it) }) {
                    themeCounts[theme] = (themeCounts[theme] ?: 0) + 1
                }
            }
        }

        return themeCounts.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }
            .joinToString(",")
    }

    private fun detectPatterns(entries: List<JournalEntryEntity>): String {
        val patterns = mutableListOf<String>()

        // Time pattern
        val morningEntries = entries.filter { entry ->
            val hour = java.time.Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .hour
            hour in 5..11
        }

        val eveningEntries = entries.filter { entry ->
            val hour = java.time.Instant.ofEpochMilli(entry.createdAt)
                .atZone(ZoneId.systemDefault())
                .hour
            hour in 18..23
        }

        if (morningEntries.size > entries.size * 0.6) {
            patterns.add("morning_writer")
        } else if (eveningEntries.size > entries.size * 0.6) {
            patterns.add("evening_reflector")
        }

        // Length pattern
        val avgWordCount = entries.map { it.wordCount }.average()
        if (avgWordCount > 300) {
            patterns.add("deep_thinker")
        } else if (avgWordCount < 100) {
            patterns.add("concise_reflector")
        }

        // Consistency pattern
        if (entries.size >= 5) {
            patterns.add("consistent_journaler")
        }

        return patterns.joinToString(",")
    }

    private fun calculateChangePercent(current: Int, previous: Int): Int {
        if (previous == 0) return if (current > 0) 100 else 0
        return ((current - previous).toFloat() / previous * 100).toInt()
    }

    private fun extractHighlightQuote(content: String): String? {
        if (content.length < 50) return null

        val sentences = content.split(Regex("[.!?]"))
            .map { it.trim() }
            .filter { it.length in 30..150 }

        return sentences.maxByOrNull { sentence ->
            val meaningfulWords = listOf(
                "realize", "understand", "learn", "grateful", "happy", "proud",
                "discover", "overcome", "achieve", "grow", "important", "meaningful"
            )
            meaningfulWords.count { sentence.lowercase().contains(it) }
        }
    }

    private fun generateBuddhaReflection(
        entriesCount: Int,
        dominantMood: String?,
        topThemes: String,
        moodTrend: String
    ): String {
        val mood = dominantMood?.let { Mood.fromString(it) }
        val themes = topThemes.split(",").filter { it.isNotBlank() }

        val reflection = StringBuilder()

        // Opening based on activity
        when {
            entriesCount >= 7 -> reflection.append("Your dedication to daily reflection this week is commendable. ")
            entriesCount >= 4 -> reflection.append("You've maintained a steady practice of self-examination. ")
            entriesCount >= 1 -> reflection.append("Every moment of reflection adds to your growth. ")
            else -> reflection.append("Even in quiet weeks, wisdom continues to unfold. ")
        }

        // Mood insight
        mood?.let {
            reflection.append(BuddhaWisdom.generateWeeklySummary(entriesCount, it, 0)
                .substringAfter("**${it.displayName}**.")
                .substringBefore("\n")
                .trim())
            reflection.append(" ")
        }

        // Theme observation
        if (themes.isNotEmpty()) {
            val themeStr = themes.joinToString(" and ")
            reflection.append("Your thoughts have dwelt upon $themeStr - these threads weave the tapestry of your current journey. ")
        }

        // Trend wisdom
        when (moodTrend) {
            "improving" -> reflection.append("The upward arc of your emotional state speaks to inner resilience.")
            "declining" -> reflection.append("Remember: difficult seasons precede growth. Be gentle with yourself.")
            else -> reflection.append("In steadiness, there is strength. Continue walking your path.")
        }

        return reflection.toString().trim()
    }
}
