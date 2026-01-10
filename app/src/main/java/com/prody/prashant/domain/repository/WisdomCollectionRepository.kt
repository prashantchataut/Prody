package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.SavedWisdomEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Wisdom Collection operations.
 *
 * The Wisdom Collection allows users to save quotes, proverbs, idioms,
 * and other pieces of wisdom that resonate with them. These are then
 * resurfaced intelligently at meaningful moments.
 */
interface WisdomCollectionRepository {

    // ==================== RETRIEVAL ====================

    /**
     * Get all saved wisdom items as a Flow.
     */
    fun getAllSavedWisdom(userId: String = "local"): Flow<List<SavedWisdomEntity>>

    /**
     * Get saved wisdom by type (QUOTE, WORD, PROVERB, etc.)
     */
    fun getSavedWisdomByType(userId: String = "local", type: String): Flow<List<SavedWisdomEntity>>

    /**
     * Get a specific saved wisdom item by ID.
     */
    suspend fun getSavedWisdomById(id: Long): Result<SavedWisdomEntity>

    /**
     * Search saved wisdom by content, author, or tags.
     */
    fun searchSavedWisdom(userId: String = "local", query: String): Flow<List<SavedWisdomEntity>>

    /**
     * Get wisdom by theme/tag.
     */
    fun getSavedWisdomByTheme(userId: String = "local", theme: String): Flow<List<SavedWisdomEntity>>

    // ==================== SAVE/UPDATE ====================

    /**
     * Save a new wisdom item to the collection.
     */
    suspend fun saveWisdom(wisdom: SavedWisdomEntity): Result<Long>

    /**
     * Update an existing saved wisdom item.
     */
    suspend fun updateSavedWisdom(wisdom: SavedWisdomEntity): Result<Unit>

    /**
     * Add a user note to saved wisdom.
     */
    suspend fun addUserNote(id: Long, note: String): Result<Unit>

    /**
     * Update tags for saved wisdom.
     */
    suspend fun updateTags(id: Long, tags: String): Result<Unit>

    // ==================== DELETION ====================

    /**
     * Remove wisdom from collection (soft delete).
     */
    suspend fun removeWisdom(id: Long): Result<Unit>

    /**
     * Permanently delete wisdom.
     */
    suspend fun deleteWisdom(id: Long): Result<Unit>

    // ==================== SMART RESURFACING ====================

    /**
     * Get wisdom to resurface - prioritizes items not shown recently.
     * Returns a random selection weighted by time since last shown.
     */
    suspend fun getWisdomToResurface(userId: String = "local", limit: Int = 3): Result<List<SavedWisdomEntity>>

    /**
     * Record that a wisdom item was shown to the user.
     */
    suspend fun recordWisdomShown(id: Long): Result<Unit>

    /**
     * Record that a wisdom item was viewed (tapped/expanded).
     */
    suspend fun recordWisdomViewed(id: Long): Result<Unit>

    // ==================== CHECKING ====================

    /**
     * Check if a wisdom item is already saved.
     */
    suspend fun isWisdomSaved(sourceId: Long, type: String): Boolean

    /**
     * Get saved wisdom by source ID and type.
     */
    suspend fun getSavedWisdomBySource(sourceId: Long, type: String): SavedWisdomEntity?

    // ==================== STATISTICS ====================

    /**
     * Get total count of saved wisdom items.
     */
    fun getSavedWisdomCount(userId: String = "local"): Flow<Int>

    /**
     * Get count by type.
     */
    suspend fun getCountByType(userId: String = "local"): Map<String, Int>
}
