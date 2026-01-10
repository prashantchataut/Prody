package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.MicroEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for MicroEntry operations.
 *
 * Handles all database operations for the Micro-Journaling feature,
 * including quick capture, retrieval, and expansion tracking.
 */
@Dao
interface MicroEntryDao {

    // ==================== BASIC CRUD ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMicroEntry(entry: MicroEntryEntity): Long

    @Update
    suspend fun updateMicroEntry(entry: MicroEntryEntity)

    @Delete
    suspend fun deleteMicroEntry(entry: MicroEntryEntity)

    @Query("DELETE FROM micro_entries WHERE id = :id")
    suspend fun deleteMicroEntryById(id: Long)

    @Query("UPDATE micro_entries SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteMicroEntry(id: Long)

    // ==================== RETRIEVAL QUERIES ====================

    @Query("SELECT * FROM micro_entries WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllMicroEntries(): Flow<List<MicroEntryEntity>>

    @Query("SELECT * FROM micro_entries WHERE userId = :userId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getMicroEntriesByUser(userId: String): Flow<List<MicroEntryEntity>>

    @Query("SELECT * FROM micro_entries WHERE id = :id AND isDeleted = 0")
    suspend fun getMicroEntryById(id: Long): MicroEntryEntity?

    @Query("SELECT * FROM micro_entries WHERE id = :id")
    fun observeMicroEntryById(id: Long): Flow<MicroEntryEntity?>

    @Query("SELECT * FROM micro_entries WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentMicroEntries(limit: Int): Flow<List<MicroEntryEntity>>

    // ==================== DATE RANGE QUERIES ====================

    @Query("""
        SELECT * FROM micro_entries
        WHERE isDeleted = 0
        AND createdAt BETWEEN :startDate AND :endDate
        ORDER BY createdAt DESC
    """)
    fun getMicroEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<MicroEntryEntity>>

    @Query("""
        SELECT * FROM micro_entries
        WHERE isDeleted = 0
        AND createdAt >= :startOfDay
        AND createdAt < :endOfDay
        ORDER BY createdAt DESC
    """)
    suspend fun getMicroEntriesForDay(startOfDay: Long, endOfDay: Long): List<MicroEntryEntity>

    // ==================== MOOD QUERIES ====================

    @Query("SELECT * FROM micro_entries WHERE mood = :mood AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getMicroEntriesByMood(mood: String): Flow<List<MicroEntryEntity>>

    // ==================== CONTEXT QUERIES ====================

    @Query("SELECT * FROM micro_entries WHERE captureContext = :context AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getMicroEntriesByContext(context: String): Flow<List<MicroEntryEntity>>

    @Query("""
        SELECT * FROM micro_entries
        WHERE captureContext = :context
        AND createdAt >= :startOfDay
        AND createdAt < :endOfDay
        AND isDeleted = 0
        LIMIT 1
    """)
    suspend fun getMicroEntryForContextToday(context: String, startOfDay: Long, endOfDay: Long): MicroEntryEntity?

    // ==================== EXPANSION TRACKING ====================

    /**
     * Get micro entries that haven't been expanded yet (candidates for expansion)
     */
    @Query("SELECT * FROM micro_entries WHERE expandedToEntryId IS NULL AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getUnexpandedMicroEntries(): Flow<List<MicroEntryEntity>>

    /**
     * Mark a micro entry as expanded to a full journal entry
     */
    @Query("UPDATE micro_entries SET expandedToEntryId = :journalEntryId, expandedAt = :expandedAt WHERE id = :id")
    suspend fun markAsExpanded(id: Long, journalEntryId: Long, expandedAt: Long = System.currentTimeMillis())

    /**
     * Get the micro entry that was expanded to a specific journal entry
     */
    @Query("SELECT * FROM micro_entries WHERE expandedToEntryId = :journalEntryId")
    suspend fun getMicroEntryByExpandedJournalId(journalEntryId: Long): MicroEntryEntity?

    // ==================== STATISTICS ====================

    @Query("SELECT COUNT(*) FROM micro_entries WHERE isDeleted = 0")
    fun getTotalMicroEntryCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM micro_entries WHERE createdAt >= :startOfDay AND isDeleted = 0")
    suspend fun getTodayMicroEntryCount(startOfDay: Long): Int

    @Query("SELECT COUNT(*) FROM micro_entries WHERE createdAt >= :since AND isDeleted = 0")
    suspend fun getMicroEntryCountSince(since: Long): Int

    /**
     * Get mood distribution for micro entries
     */
    @Query("""
        SELECT mood, COUNT(*) as count
        FROM micro_entries
        WHERE mood IS NOT NULL AND isDeleted = 0
        GROUP BY mood
        ORDER BY count DESC
    """)
    fun getMoodDistribution(): Flow<List<MicroEntryMoodCount>>

    // ==================== SEARCH ====================

    @Query("""
        SELECT * FROM micro_entries
        WHERE isDeleted = 0
        AND content LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchMicroEntries(query: String): Flow<List<MicroEntryEntity>>

    // ==================== SYNC ====================

    @Query("SELECT * FROM micro_entries WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncEntries(): List<MicroEntryEntity>

    @Query("UPDATE micro_entries SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, syncTime: Long = System.currentTimeMillis())

    // ==================== BACKUP ====================

    @Query("SELECT * FROM micro_entries ORDER BY createdAt DESC")
    suspend fun getAllMicroEntriesSync(): List<MicroEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMicroEntries(entries: List<MicroEntryEntity>)

    // ==================== CLEANUP ====================

    @Query("DELETE FROM micro_entries WHERE isDeleted = 1")
    suspend fun purgeSoftDeleted(): Int

    @Query("SELECT * FROM micro_entries WHERE isDeleted = 1")
    suspend fun getSoftDeletedEntries(): List<MicroEntryEntity>
}

/**
 * Data class for mood distribution query
 */
data class MicroEntryMoodCount(
    val mood: String?,
    val count: Int
)
