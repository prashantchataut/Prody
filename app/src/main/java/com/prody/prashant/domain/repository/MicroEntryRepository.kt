package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.MicroEntryEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Micro-Journaling operations.
 *
 * Micro-journaling provides ultra-low-friction quick thought capture.
 * Thoughts can later be expanded into full journal entries.
 */
interface MicroEntryRepository {

    // ==================== RETRIEVAL ====================

    /**
     * Get all micro entries as a Flow.
     */
    fun getAllMicroEntries(userId: String = "local"): Flow<List<MicroEntryEntity>>

    /**
     * Get micro entries for a specific date range.
     */
    fun getMicroEntriesByDateRange(
        userId: String = "local",
        startDate: Long,
        endDate: Long
    ): Flow<List<MicroEntryEntity>>

    /**
     * Get micro entries for today.
     */
    fun getTodayMicroEntries(userId: String = "local"): Flow<List<MicroEntryEntity>>

    /**
     * Get a specific micro entry by ID.
     */
    suspend fun getMicroEntryById(id: Long): Result<MicroEntryEntity>

    /**
     * Observe a micro entry by ID.
     */
    fun observeMicroEntryById(id: Long): Flow<MicroEntryEntity?>

    /**
     * Get unexpanded micro entries (not yet converted to full journal entries).
     */
    fun getUnexpandedMicroEntries(userId: String = "local"): Flow<List<MicroEntryEntity>>

    /**
     * Get micro entries by context (morning_ritual, evening_ritual, quick_capture).
     */
    fun getMicroEntriesByContext(userId: String = "local", context: String): Flow<List<MicroEntryEntity>>

    /**
     * Get micro entries by mood.
     */
    fun getMicroEntriesByMood(userId: String = "local", mood: String): Flow<List<MicroEntryEntity>>

    /**
     * Search micro entries.
     */
    fun searchMicroEntries(userId: String = "local", query: String): Flow<List<MicroEntryEntity>>

    // ==================== CREATE/UPDATE ====================

    /**
     * Create a new micro entry.
     */
    suspend fun createMicroEntry(entry: MicroEntryEntity): Result<Long>

    /**
     * Update an existing micro entry.
     */
    suspend fun updateMicroEntry(entry: MicroEntryEntity): Result<Unit>

    /**
     * Mark a micro entry as expanded to a full journal entry.
     */
    suspend fun markAsExpanded(microEntryId: Long, journalEntryId: Long): Result<Unit>

    // ==================== DELETION ====================

    /**
     * Delete a micro entry (soft delete).
     */
    suspend fun softDeleteMicroEntry(id: Long): Result<Unit>

    /**
     * Permanently delete a micro entry.
     */
    suspend fun deleteMicroEntry(id: Long): Result<Unit>

    // ==================== STATISTICS ====================

    /**
     * Get total count of micro entries.
     */
    fun getMicroEntryCount(userId: String = "local"): Flow<Int>

    /**
     * Get count for today.
     */
    suspend fun getTodayMicroEntryCount(userId: String = "local"): Int

    /**
     * Get count for this week.
     */
    suspend fun getThisWeekMicroEntryCount(userId: String = "local"): Int

    /**
     * Get unexpanded count.
     */
    suspend fun getUnexpandedCount(userId: String = "local"): Int

    /**
     * Get mood distribution for micro entries.
     */
    suspend fun getMoodDistribution(userId: String = "local"): Map<String, Int>

    // ==================== RECENT ====================

    /**
     * Get recent micro entries.
     */
    fun getRecentMicroEntries(userId: String = "local", limit: Int = 10): Flow<List<MicroEntryEntity>>
}
