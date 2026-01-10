package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.YearlyWrappedEntity
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.wrapped.YearlyWrapped
import com.prody.prashant.domain.wrapped.WrappedGenerationConfig
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Yearly Wrapped operations.
 *
 * Provides access to yearly wrapped summaries, enabling users to:
 * - Generate new wrapped for a year
 * - View past wrapped summaries
 * - Mark wrapped as viewed/favorite
 * - Share wrapped cards
 */
interface YearlyWrappedRepository {

    // ==================== RETRIEVAL ====================

    /**
     * Get all wrapped summaries for a user, ordered by year (newest first)
     */
    fun getAllWrapped(userId: String = "local"): Flow<List<YearlyWrappedEntity>>

    /**
     * Get wrapped for a specific year
     */
    suspend fun getWrappedByYear(userId: String = "local", year: Int): Result<YearlyWrappedEntity?>

    /**
     * Observe wrapped for a specific year
     */
    fun observeWrappedByYear(userId: String = "local", year: Int): Flow<YearlyWrappedEntity?>

    /**
     * Get the most recent wrapped
     */
    suspend fun getLatestWrapped(userId: String = "local"): Result<YearlyWrappedEntity?>

    /**
     * Observe the most recent wrapped
     */
    fun observeLatestWrapped(userId: String = "local"): Flow<YearlyWrappedEntity?>

    /**
     * Get wrapped by ID
     */
    suspend fun getWrappedById(id: Long): Result<YearlyWrappedEntity>

    /**
     * Observe wrapped by ID
     */
    fun observeWrappedById(id: Long): Flow<YearlyWrappedEntity?>

    /**
     * Get unviewed wrapped summaries
     */
    fun getUnviewedWrapped(userId: String = "local"): Flow<List<YearlyWrappedEntity>>

    /**
     * Get favorite wrapped summaries
     */
    fun getFavoriteWrapped(userId: String = "local"): Flow<List<YearlyWrappedEntity>>

    /**
     * Get list of years with available wrapped
     */
    suspend fun getAvailableYears(userId: String = "local"): List<Int>

    /**
     * Observe list of years with available wrapped
     */
    fun observeAvailableYears(userId: String = "local"): Flow<List<Int>>

    // ==================== GENERATION ====================

    /**
     * Generate wrapped for a specific year.
     * This analyzes all user data from that year to create a comprehensive summary.
     *
     * @param userId User ID
     * @param year Year to generate wrapped for
     * @param config Configuration for generation (themes count, narratives, etc.)
     * @return Result containing the generated wrapped entity
     */
    suspend fun generateWrapped(
        userId: String = "local",
        year: Int,
        config: WrappedGenerationConfig = WrappedGenerationConfig(year)
    ): Result<YearlyWrappedEntity>

    /**
     * Check if wrapped already exists for a year
     */
    suspend fun hasWrappedForYear(userId: String = "local", year: Int): Boolean

    /**
     * Check if sufficient data exists to generate wrapped for a year
     */
    suspend fun canGenerateWrappedForYear(userId: String = "local", year: Int): Result<Boolean>

    // ==================== UPDATE ====================

    /**
     * Mark wrapped as viewed
     */
    suspend fun markAsViewed(
        id: Long,
        completionPercent: Int = 100,
        slidesViewed: String = "[]"
    ): Result<Unit>

    /**
     * Update favorite status
     */
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Result<Unit>

    /**
     * Mark wrapped as shared
     */
    suspend fun markAsShared(id: Long): Result<Unit>

    /**
     * Update view progress (for partial views)
     */
    suspend fun updateViewProgress(
        id: Long,
        percent: Int,
        slidesViewed: String
    ): Result<Unit>

    /**
     * Save or update a wrapped entity
     */
    suspend fun saveWrapped(wrapped: YearlyWrappedEntity): Result<Long>

    // ==================== DELETE ====================

    /**
     * Delete wrapped by ID
     */
    suspend fun deleteWrapped(id: Long): Result<Unit>

    /**
     * Soft delete wrapped (marks as deleted without removing from database)
     */
    suspend fun softDeleteWrapped(id: Long): Result<Unit>

    // ==================== STATISTICS ====================

    /**
     * Get total count of wrapped summaries
     */
    fun getWrappedCount(userId: String = "local"): Flow<Int>

    /**
     * Get count of unviewed wrapped
     */
    suspend fun getUnviewedCount(userId: String = "local"): Int

    // ==================== UTILITY ====================

    /**
     * Convert entity to domain model
     */
    fun entityToDomain(entity: YearlyWrappedEntity): YearlyWrapped
}
