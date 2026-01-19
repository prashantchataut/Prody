package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.HavenMemoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Haven Memory (THE VAULT) feature.
 *
 * Provides database access for stored facts/truths that Haven
 * should remember and follow up on.
 */
@Dao
interface HavenMemoryDao {

    // ==================== INSERT/UPDATE ====================

    /**
     * Insert a new memory
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: HavenMemoryEntity): Long

    /**
     * Insert multiple memories
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemories(memories: List<HavenMemoryEntity>): List<Long>

    /**
     * Update an existing memory
     */
    @Update
    suspend fun updateMemory(memory: HavenMemoryEntity)

    /**
     * Update memory status
     */
    @Query("UPDATE haven_memories SET status = :status, updatedAt = :updatedAt WHERE id = :memoryId")
    suspend fun updateMemoryStatus(memoryId: Long, status: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Mark memory as followed up
     */
    @Query("""
        UPDATE haven_memories
        SET status = 'followed_up',
            followedUpAt = :followedUpAt,
            notificationSent = 1,
            notificationSentAt = :notificationSentAt,
            updatedAt = :updatedAt
        WHERE id = :memoryId
    """)
    suspend fun markAsFollowedUp(
        memoryId: Long,
        followedUpAt: Long = System.currentTimeMillis(),
        notificationSentAt: Long = System.currentTimeMillis(),
        updatedAt: Long = System.currentTimeMillis()
    )

    /**
     * Record the outcome of a memory (user's response to follow-up)
     */
    @Query("""
        UPDATE haven_memories
        SET status = 'resolved',
            outcome = :outcome,
            followUpResponse = :response,
            updatedAt = :updatedAt
        WHERE id = :memoryId
    """)
    suspend fun recordOutcome(
        memoryId: Long,
        outcome: String,
        response: String? = null,
        updatedAt: Long = System.currentTimeMillis()
    )

    /**
     * Dismiss a memory
     */
    @Query("UPDATE haven_memories SET status = 'dismissed', updatedAt = :updatedAt WHERE id = :memoryId")
    suspend fun dismissMemory(memoryId: Long, updatedAt: Long = System.currentTimeMillis())

    // ==================== QUERY - BASIC ====================

    /**
     * Get all memories for a user
     */
    @Query("SELECT * FROM haven_memories WHERE userId = :userId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllMemories(userId: String): Flow<List<HavenMemoryEntity>>

    /**
     * Get memory by ID
     */
    @Query("SELECT * FROM haven_memories WHERE id = :memoryId")
    suspend fun getMemoryById(memoryId: Long): HavenMemoryEntity?

    /**
     * Get memories by status
     */
    @Query("SELECT * FROM haven_memories WHERE userId = :userId AND status = :status AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getMemoriesByStatus(userId: String, status: String): Flow<List<HavenMemoryEntity>>

    /**
     * Get memories by category
     */
    @Query("SELECT * FROM haven_memories WHERE userId = :userId AND category = :category AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getMemoriesByCategory(userId: String, category: String): Flow<List<HavenMemoryEntity>>

    // ==================== QUERY - WITNESS MODE (CALLBACK LOGIC) ====================

    /**
     * Get pending memories that are due for follow-up.
     * Returns memories where:
     * - Status is PENDING
     * - Follow-up date is in the past or null (factDate was > 3 days ago)
     * - Notification hasn't been sent yet
     *
     * This is the core query for "Witness Mode" callback logic.
     */
    @Query("""
        SELECT * FROM haven_memories
        WHERE userId = :userId
        AND status = 'pending'
        AND isDeleted = 0
        AND notificationSent = 0
        AND (
            (followUpDate IS NOT NULL AND followUpDate <= :currentTime)
            OR (followUpDate IS NULL AND factDate IS NOT NULL AND factDate < :threeDaysAgo)
            OR (followUpDate IS NULL AND factDate IS NULL AND createdAt < :threeDaysAgo)
        )
        ORDER BY importance DESC, factDate ASC
    """)
    suspend fun getPendingMemoriesForFollowUp(
        userId: String,
        currentTime: Long = System.currentTimeMillis(),
        threeDaysAgo: Long = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L)
    ): List<HavenMemoryEntity>

    /**
     * Get upcoming memories (events/deadlines in the next N days)
     */
    @Query("""
        SELECT * FROM haven_memories
        WHERE userId = :userId
        AND status = 'pending'
        AND isDeleted = 0
        AND factDate IS NOT NULL
        AND factDate BETWEEN :now AND :futureDate
        ORDER BY factDate ASC
    """)
    fun getUpcomingMemories(
        userId: String,
        now: Long = System.currentTimeMillis(),
        futureDate: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L) // 7 days
    ): Flow<List<HavenMemoryEntity>>

    /**
     * Get memories from a specific Haven session
     */
    @Query("SELECT * FROM haven_memories WHERE sourceSessionId = :sessionId AND isDeleted = 0 ORDER BY createdAt ASC")
    fun getMemoriesFromSession(sessionId: Long): Flow<List<HavenMemoryEntity>>

    /**
     * Get recent memories (last N)
     */
    @Query("SELECT * FROM haven_memories WHERE userId = :userId AND isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentMemories(userId: String, limit: Int = 10): Flow<List<HavenMemoryEntity>>

    // ==================== QUERY - STATISTICS ====================

    /**
     * Get total memory count
     */
    @Query("SELECT COUNT(*) FROM haven_memories WHERE userId = :userId AND isDeleted = 0")
    fun getTotalMemoryCount(userId: String): Flow<Int>

    /**
     * Get pending memory count
     */
    @Query("SELECT COUNT(*) FROM haven_memories WHERE userId = :userId AND status = 'pending' AND isDeleted = 0")
    suspend fun getPendingMemoryCount(userId: String): Int

    /**
     * Get resolved memory count
     */
    @Query("SELECT COUNT(*) FROM haven_memories WHERE userId = :userId AND status = 'resolved' AND isDeleted = 0")
    suspend fun getResolvedMemoryCount(userId: String): Int

    /**
     * Get success rate (resolved with success outcome)
     */
    @Query("""
        SELECT CAST(
            SUM(CASE WHEN outcome = 'success' THEN 1 ELSE 0 END) AS FLOAT
        ) / COUNT(*)
        FROM haven_memories
        WHERE userId = :userId
        AND status = 'resolved'
        AND isDeleted = 0
    """)
    suspend fun getSuccessRate(userId: String): Float?

    /**
     * Get category distribution
     */
    @Query("""
        SELECT category, COUNT(*) as count
        FROM haven_memories
        WHERE userId = :userId AND isDeleted = 0
        GROUP BY category
        ORDER BY count DESC
    """)
    suspend fun getCategoryDistribution(userId: String): List<MemoryCategoryCount>

    // ==================== DELETE ====================

    /**
     * Soft delete a memory
     */
    @Query("UPDATE haven_memories SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :memoryId")
    suspend fun softDeleteMemory(memoryId: Long, updatedAt: Long = System.currentTimeMillis())

    /**
     * Hard delete a memory
     */
    @Delete
    suspend fun deleteMemory(memory: HavenMemoryEntity)

    /**
     * Delete all memories for a user
     */
    @Query("DELETE FROM haven_memories WHERE userId = :userId")
    suspend fun deleteAllMemories(userId: String)

    /**
     * Delete expired memories older than N days
     */
    @Query("""
        UPDATE haven_memories
        SET status = 'expired', updatedAt = :updatedAt
        WHERE status = 'pending'
        AND isDeleted = 0
        AND factDate IS NOT NULL
        AND factDate < :expiryDate
    """)
    suspend fun expireOldMemories(
        expiryDate: Long = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L), // 30 days
        updatedAt: Long = System.currentTimeMillis()
    )

    // ==================== SEARCH ====================

    /**
     * Search memories by fact content
     */
    @Query("""
        SELECT * FROM haven_memories
        WHERE userId = :userId
        AND isDeleted = 0
        AND fact LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchMemories(userId: String, query: String): Flow<List<HavenMemoryEntity>>
}

/**
 * Data class for category distribution results
 */
data class MemoryCategoryCount(
    val category: String,
    val count: Int
)
