package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.dao.JournalEntrySummary
import com.prody.prashant.data.local.dao.MoodCount
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.model.Mood
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for journal operations.
 * Provides abstraction layer between ViewModels and data sources.
 */
interface JournalRepository {
    /**
     * Get all journal entries.
     */
    fun getAllEntries(): Flow<List<JournalEntryEntity>>

    /**
     * Get a journal entry by ID.
     */
    suspend fun getEntryById(id: Long): Result<JournalEntryEntity>

    /**
     * Observe a journal entry by ID.
     */
    fun observeEntryById(id: Long): Flow<JournalEntryEntity?>

    /**
     * Get bookmarked entries.
     */
    fun getBookmarkedEntries(): Flow<List<JournalEntryEntity>>

    /**
     * Get entries by mood.
     */
    fun getEntriesByMood(mood: Mood): Flow<List<JournalEntryEntity>>

    /**
     * Get entries within a date range.
     */
    fun getEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<JournalEntryEntity>>

    /**
     * Search entries by content or tags.
     */
    fun searchEntries(query: String): Flow<List<JournalEntryEntity>>

    /**
     * Get total entry count.
     */
    fun getEntryCount(): Flow<Int>

    /**
     * Get entry count for today.
     */
    suspend fun getTodayEntryCount(): Int

    /**
     * Get total word count across all entries.
     */
    fun getTotalWordCount(): Flow<Int?>

    /**
     * Get mood distribution statistics.
     */
    fun getMoodDistribution(): Flow<List<MoodCount>>

    /**
     * Get recent entries.
     */
    fun getRecentEntries(limit: Int): Flow<List<JournalEntryEntity>>

    /**
     * Get all unique moods used in entries.
     */
    fun getAllMoods(): Flow<List<String>>

    /**
     * Save a new journal entry.
     */
    suspend fun saveEntry(entry: JournalEntryEntity): Result<Long>

    /**
     * Update an existing entry.
     */
    suspend fun updateEntry(entry: JournalEntryEntity): Result<Unit>

    /**
     * Delete an entry.
     */
    suspend fun deleteEntry(entry: JournalEntryEntity): Result<Unit>

    /**
     * Delete entry by ID.
     */
    suspend fun deleteEntryById(id: Long): Result<Unit>

    /**
     * Update bookmark status.
     */
    suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean): Result<Unit>

    /**
     * Update Buddha response for an entry.
     */
    suspend fun updateBuddhaResponse(id: Long, response: String): Result<Unit>

    /**
     * Get entries for a specific week.
     */
    suspend fun getEntriesForWeek(weekStartTimestamp: Long): List<JournalEntryEntity>

    /**
     * Get dominant mood for a list of entries.
     */
    fun getDominantMood(entries: List<JournalEntryEntity>): Mood?

    // ==================== MIRROR/RECEIPT COMPARISON ====================

    /**
     * Find similar entries based on content keywords.
     * Used for "The Receipt" feature to compare past vs present entries.
     * @param content The current entry content to find similar entries for
     * @param excludeId The ID of the current entry to exclude from results
     * @param limit Maximum number of similar entries to return
     * @return List of similar journal entries
     */
    suspend fun findSimilarEntries(
        content: String,
        excludeId: Long = 0,
        limit: Int = 5
    ): List<JournalEntryEntity>

    /**
     * Find entries with the same mood.
     * @param mood The mood to match
     * @param excludeId The ID of the current entry to exclude
     * @param limit Maximum number of entries to return
     */
    suspend fun findEntriesWithSameMood(
        mood: String,
        excludeId: Long = 0,
        limit: Int = 5
    ): List<JournalEntryEntity>

    /**
     * Get entries for similarity matching (for advanced similarity algorithms).
     * @param excludeId The ID to exclude
     * @param limit Maximum number of entries
     */
    suspend fun getEntriesForSimilarityMatching(
        excludeId: Long = 0,
        limit: Int = 100
    ): List<JournalEntrySummary>
}
