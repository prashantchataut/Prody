package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.WeeklyDigestEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Weekly Digest operations.
 *
 * The Weekly Digest provides users with a summary of their journaling
 * activity, patterns, and insights for the past week.
 */
interface WeeklyDigestRepository {

    // ==================== RETRIEVAL ====================

    /**
     * Get all weekly digests.
     */
    fun getAllDigests(userId: String = "local"): Flow<List<WeeklyDigestEntity>>

    /**
     * Get the latest/most recent digest.
     */
    suspend fun getLatestDigest(userId: String = "local"): Result<WeeklyDigestEntity?>

    /**
     * Observe the latest digest.
     */
    fun observeLatestDigest(userId: String = "local"): Flow<WeeklyDigestEntity?>

    /**
     * Get digest by ID.
     */
    suspend fun getDigestById(id: Long): Result<WeeklyDigestEntity>

    /**
     * Observe digest by ID.
     */
    fun observeDigestById(id: Long): Flow<WeeklyDigestEntity?>

    /**
     * Get digest for a specific week (by week start date).
     */
    suspend fun getDigestForWeek(userId: String = "local", weekStartDate: Long): Result<WeeklyDigestEntity?>

    /**
     * Get unread digests.
     */
    fun getUnreadDigests(userId: String = "local"): Flow<List<WeeklyDigestEntity>>

    // ==================== CREATE/UPDATE ====================

    /**
     * Create or update a weekly digest.
     */
    suspend fun saveDigest(digest: WeeklyDigestEntity): Result<Long>

    /**
     * Mark a digest as read.
     */
    suspend fun markDigestAsRead(id: Long): Result<Unit>

    /**
     * Mark all digests as read.
     */
    suspend fun markAllAsRead(userId: String = "local"): Result<Unit>

    // ==================== GENERATION ====================

    /**
     * Generate a new weekly digest for the previous week.
     * This analyzes journal entries and micro entries to create
     * a comprehensive summary.
     */
    suspend fun generateWeeklyDigest(userId: String = "local"): Result<WeeklyDigestEntity>

    /**
     * Check if a digest already exists for the current week.
     */
    suspend fun hasDigestForCurrentWeek(userId: String = "local"): Boolean

    /**
     * Check if a digest already exists for a specific week.
     */
    suspend fun hasDigestForWeek(userId: String = "local", weekStartDate: Long): Boolean

    // ==================== STATISTICS ====================

    /**
     * Get total digest count.
     */
    fun getDigestCount(userId: String = "local"): Flow<Int>

    /**
     * Get unread digest count.
     */
    suspend fun getUnreadCount(userId: String = "local"): Int

    // ==================== CLEANUP ====================

    /**
     * Clean up old digests, keeping only the most recent ones.
     */
    suspend fun cleanupOldDigests(userId: String = "local", keepCount: Int = 4): Int

    /**
     * Purge soft-deleted digests.
     */
    suspend fun purgeSoftDeleted(): Int

    /**
     * Delete a digest by ID.
     */
    suspend fun deleteDigest(id: Long): Result<Unit>
}
