package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.SavedWisdomEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SavedWisdom operations.
 *
 * Handles all database operations for the Wisdom Collection feature,
 * including saving, retrieving, filtering, and smart resurfacing queries.
 */
@Dao
interface SavedWisdomDao {

    // ==================== BASIC CRUD ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWisdom(wisdom: SavedWisdomEntity): Long

    @Update
    suspend fun updateWisdom(wisdom: SavedWisdomEntity)

    @Delete
    suspend fun deleteWisdom(wisdom: SavedWisdomEntity)

    @Query("DELETE FROM saved_wisdom WHERE id = :id")
    suspend fun deleteWisdomById(id: Long)

    @Query("UPDATE saved_wisdom SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteWisdom(id: Long)

    // ==================== RETRIEVAL QUERIES ====================

    @Query("SELECT * FROM saved_wisdom WHERE isDeleted = 0 ORDER BY savedAt DESC")
    fun getAllSavedWisdom(): Flow<List<SavedWisdomEntity>>

    @Query("SELECT * FROM saved_wisdom WHERE userId = :userId AND isDeleted = 0 ORDER BY savedAt DESC")
    fun getSavedWisdomByUser(userId: String): Flow<List<SavedWisdomEntity>>

    @Query("SELECT * FROM saved_wisdom WHERE id = :id AND isDeleted = 0")
    suspend fun getWisdomById(id: Long): SavedWisdomEntity?

    @Query("SELECT * FROM saved_wisdom WHERE id = :id")
    fun observeWisdomById(id: Long): Flow<SavedWisdomEntity?>

    @Query("SELECT * FROM saved_wisdom WHERE sourceId = :sourceId AND type = :type AND isDeleted = 0 LIMIT 1")
    suspend fun getWisdomBySource(sourceId: Long, type: String): SavedWisdomEntity?

    // ==================== FILTER BY TYPE ====================

    @Query("SELECT * FROM saved_wisdom WHERE type = :type AND isDeleted = 0 ORDER BY savedAt DESC")
    fun getWisdomByType(type: String): Flow<List<SavedWisdomEntity>>

    @Query("SELECT * FROM saved_wisdom WHERE userId = :userId AND type = :type AND isDeleted = 0 ORDER BY savedAt DESC")
    fun getWisdomByTypeForUser(userId: String, type: String): Flow<List<SavedWisdomEntity>>

    // ==================== SEARCH ====================

    @Query("""
        SELECT * FROM saved_wisdom
        WHERE isDeleted = 0
        AND (content LIKE '%' || :query || '%'
             OR author LIKE '%' || :query || '%'
             OR tags LIKE '%' || :query || '%'
             OR secondaryContent LIKE '%' || :query || '%')
        ORDER BY savedAt DESC
    """)
    fun searchWisdom(query: String): Flow<List<SavedWisdomEntity>>

    // ==================== SMART RESURFACING ====================

    /**
     * Get wisdom for resurfacing - prioritizes items that:
     * 1. Haven't been shown recently
     * 2. Have been shown fewer times
     * 3. Match the given theme (if provided)
     */
    @Query("""
        SELECT * FROM saved_wisdom
        WHERE isDeleted = 0
        AND (lastShownAt IS NULL OR lastShownAt < :notShownSince)
        ORDER BY timesShown ASC, savedAt DESC
        LIMIT :limit
    """)
    suspend fun getWisdomForResurfacing(notShownSince: Long, limit: Int = 3): List<SavedWisdomEntity>

    /**
     * Get wisdom matching a specific theme for contextual resurfacing
     */
    @Query("""
        SELECT * FROM saved_wisdom
        WHERE isDeleted = 0
        AND (theme = :theme OR tags LIKE '%' || :theme || '%')
        AND (lastShownAt IS NULL OR lastShownAt < :notShownSince)
        ORDER BY timesShown ASC, RANDOM()
        LIMIT 1
    """)
    suspend fun getWisdomByTheme(theme: String, notShownSince: Long): SavedWisdomEntity?

    /**
     * Update when wisdom is shown (for resurfacing tracking)
     */
    @Query("UPDATE saved_wisdom SET lastShownAt = :timestamp, timesShown = timesShown + 1 WHERE id = :id")
    suspend fun markWisdomAsShown(id: Long, timestamp: Long = System.currentTimeMillis())

    /**
     * Update when wisdom is viewed by user
     */
    @Query("UPDATE saved_wisdom SET lastViewedAt = :timestamp, timesViewed = timesViewed + 1 WHERE id = :id")
    suspend fun markWisdomAsViewed(id: Long, timestamp: Long = System.currentTimeMillis())

    // ==================== STATISTICS ====================

    @Query("SELECT COUNT(*) FROM saved_wisdom WHERE isDeleted = 0")
    fun getTotalSavedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM saved_wisdom WHERE type = :type AND isDeleted = 0")
    fun getCountByType(type: String): Flow<Int>

    @Query("SELECT type, COUNT(*) as count FROM saved_wisdom WHERE isDeleted = 0 GROUP BY type ORDER BY count DESC")
    fun getTypeDistribution(): Flow<List<WisdomTypeCount>>

    // ==================== CHECK IF ALREADY SAVED ====================

    @Query("SELECT EXISTS(SELECT 1 FROM saved_wisdom WHERE sourceId = :sourceId AND type = :type AND isDeleted = 0)")
    suspend fun isWisdomSaved(sourceId: Long, type: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM saved_wisdom WHERE content = :content AND type = :type AND isDeleted = 0)")
    suspend fun isWisdomSavedByContent(content: String, type: String): Boolean

    // ==================== SYNC ====================

    @Query("SELECT * FROM saved_wisdom WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncWisdom(): List<SavedWisdomEntity>

    @Query("UPDATE saved_wisdom SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, syncTime: Long = System.currentTimeMillis())

    // ==================== CLEANUP ====================

    @Query("DELETE FROM saved_wisdom WHERE isDeleted = 1")
    suspend fun purgeSoftDeleted(): Int

    @Query("SELECT * FROM saved_wisdom WHERE isDeleted = 1")
    suspend fun getSoftDeletedWisdom(): List<SavedWisdomEntity>
}

/**
 * Data class for wisdom type distribution query
 */
data class WisdomTypeCount(
    val type: String,
    val count: Int
)
