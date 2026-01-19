package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.JournalEntrySummary
import com.prody.prashant.data.local.dao.MoodCount
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of JournalRepository using Room database.
 */
@Singleton
class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao
) : JournalRepository {

    override fun getAllEntries(): Flow<List<JournalEntryEntity>> {
        return journalDao.getAllEntries()
    }

    override suspend fun getEntryById(id: Long): Result<JournalEntryEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to load journal entry") {
            journalDao.getEntryById(id) ?: throw NoSuchElementException("Entry not found")
        }
    }

    override fun observeEntryById(id: Long): Flow<JournalEntryEntity?> {
        return journalDao.observeEntryById(id)
    }

    override fun getBookmarkedEntries(): Flow<List<JournalEntryEntity>> {
        return journalDao.getBookmarkedEntries()
    }

    override fun getEntriesByMood(mood: Mood): Flow<List<JournalEntryEntity>> {
        return journalDao.getEntriesByMood(mood.name)
    }

    override fun getEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<JournalEntryEntity>> {
        return journalDao.getEntriesByDateRange(startDate, endDate)
    }

    override fun searchEntries(query: String): Flow<List<JournalEntryEntity>> {
        return journalDao.searchEntries(query)
    }

    override fun getEntryCount(): Flow<Int> {
        return journalDao.getEntryCount()
    }

    override suspend fun getTodayEntryCount(): Int {
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return journalDao.getTodayEntryCount(startOfDay)
    }

    override fun getTotalWordCount(): Flow<Int?> {
        return journalDao.getTotalWordCount()
    }

    override fun getMoodDistribution(): Flow<List<MoodCount>> {
        return journalDao.getMoodDistribution()
    }

    override fun getRecentEntries(limit: Int): Flow<List<JournalEntryEntity>> {
        return journalDao.getRecentEntries(limit)
    }

    override fun getAllMoods(): Flow<List<String>> {
        return journalDao.getAllMoods()
    }

    override suspend fun saveEntry(entry: JournalEntryEntity): Result<Long> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save journal entry") {
            journalDao.insertEntry(entry)
        }
    }

    override suspend fun updateEntry(entry: JournalEntryEntity): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update journal entry") {
            journalDao.updateEntry(entry)
        }
    }

    override suspend fun deleteEntry(entry: JournalEntryEntity): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete journal entry") {
            journalDao.deleteEntry(entry)
        }
    }

    override suspend fun deleteEntryById(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete journal entry") {
            journalDao.deleteEntryById(id)
        }
    }

    override suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update bookmark status") {
            journalDao.updateBookmarkStatus(id, isBookmarked)
        }
    }

    override suspend fun updateBuddhaResponse(id: Long, response: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save Buddha's response") {
            journalDao.updateBuddhaResponse(id, response)
        }
    }

    override suspend fun getEntriesForWeek(weekStartTimestamp: Long): List<JournalEntryEntity> {
        val weekEndTimestamp = weekStartTimestamp + (7 * 24 * 60 * 60 * 1000L) // 7 days in milliseconds
        return journalDao.getAllEntriesSync().filter { entry ->
            entry.createdAt in weekStartTimestamp..weekEndTimestamp
        }
    }

    override fun getDominantMood(entries: List<JournalEntryEntity>): Mood? {
        if (entries.isEmpty()) return null

        val moodCounts = entries.groupingBy { it.mood }.eachCount()
        val dominantMoodName = moodCounts.maxByOrNull { it.value }?.key

        return dominantMoodName?.let { Mood.fromString(it) }
    }

    // ==================== MIRROR/RECEIPT COMPARISON ====================

    /**
     * Extracts meaningful keywords from content for similarity matching.
     * Filters out common stop words and short words.
     */
    private fun extractKeywords(content: String): List<String> {
        val stopWords = setOf(
            "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did", "will", "would", "could", "should",
            "may", "might", "must", "shall", "can", "need", "dare", "ought", "used",
            "to", "of", "in", "for", "on", "with", "at", "by", "from", "as", "into",
            "through", "during", "before", "after", "above", "below", "between", "under",
            "again", "further", "then", "once", "here", "there", "when", "where", "why",
            "how", "all", "each", "few", "more", "most", "other", "some", "such", "no",
            "nor", "not", "only", "own", "same", "so", "than", "too", "very", "just",
            "and", "but", "if", "or", "because", "until", "while", "although", "though",
            "this", "that", "these", "those", "i", "me", "my", "myself", "we", "our",
            "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves",
            "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which",
            "who", "whom", "whose", "am", "about", "also", "even", "like", "really",
            "feel", "feeling", "felt", "today", "day", "time", "think", "know", "going",
            "get", "got", "make", "made", "want", "wanted", "much", "many", "lot", "lots"
        )

        return content.lowercase()
            .replace(Regex("[^a-z\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length > 3 && it !in stopWords }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(10)
            .map { it.key }
    }

    override suspend fun findSimilarEntries(
        content: String,
        excludeId: Long,
        limit: Int
    ): List<JournalEntryEntity> {
        val keywords = extractKeywords(content)

        // If we have fewer than 3 keywords, pad with empty strings
        val keyword1 = keywords.getOrElse(0) { "" }
        val keyword2 = keywords.getOrElse(1) { "" }
        val keyword3 = keywords.getOrElse(2) { "" }

        // First try keyword matching
        val keywordResults = if (keyword1.isNotEmpty()) {
            journalDao.findEntriesWithKeywords(excludeId, keyword1, keyword2, keyword3, limit)
        } else {
            emptyList()
        }

        return keywordResults
    }

    override suspend fun findEntriesWithSameMood(
        mood: String,
        excludeId: Long,
        limit: Int
    ): List<JournalEntryEntity> {
        return journalDao.findEntriesWithSameMood(excludeId, mood, limit)
    }

    override suspend fun getEntriesForSimilarityMatching(
        excludeId: Long,
        limit: Int
    ): List<JournalEntrySummary> {
        return journalDao.getEntriesForSimilarityMatching(excludeId, limit)
    }
}
