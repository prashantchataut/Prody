package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.EvidenceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Evidence (THE LOCKER) feature.
 *
 * Provides database access for evidence collected throughout the user's journey.
 * Evidence replaces abstract XP/plants with concrete moments of truth.
 */
@Dao
interface EvidenceDao {

    // ==================== INSERT/UPDATE ====================

    /**
     * Insert a new piece of evidence
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidence: EvidenceEntity): Long

    /**
     * Insert multiple pieces of evidence
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEvidence(evidence: List<EvidenceEntity>): List<Long>

    /**
     * Update existing evidence
     */
    @Update
    suspend fun updateEvidence(evidence: EvidenceEntity)

    /**
     * Mark evidence as viewed
     */
    @Query("UPDATE evidence SET isViewed = 1, viewedAt = :viewedAt, updatedAt = :updatedAt WHERE id = :evidenceId")
    suspend fun markAsViewed(
        evidenceId: Long,
        viewedAt: Long = System.currentTimeMillis(),
        updatedAt: Long = System.currentTimeMillis()
    )

    /**
     * Toggle pin status
     */
    @Query("UPDATE evidence SET isPinned = :isPinned, updatedAt = :updatedAt WHERE id = :evidenceId")
    suspend fun setPinned(
        evidenceId: Long,
        isPinned: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    )

    // ==================== QUERY - BASIC ====================

    /**
     * Get all evidence for a user (most recent first)
     */
    @Query("SELECT * FROM evidence WHERE userId = :userId AND isDeleted = 0 ORDER BY collectedAt DESC")
    fun getAllEvidence(userId: String): Flow<List<EvidenceEntity>>

    /**
     * Get evidence by ID
     */
    @Query("SELECT * FROM evidence WHERE id = :evidenceId")
    suspend fun getEvidenceById(evidenceId: Long): EvidenceEntity?

    /**
     * Get evidence by type
     */
    @Query("SELECT * FROM evidence WHERE userId = :userId AND evidenceType = :type AND isDeleted = 0 ORDER BY collectedAt DESC")
    fun getEvidenceByType(userId: String, type: String): Flow<List<EvidenceEntity>>

    /**
     * Get pinned evidence
     */
    @Query("SELECT * FROM evidence WHERE userId = :userId AND isPinned = 1 AND isDeleted = 0 ORDER BY collectedAt DESC")
    fun getPinnedEvidence(userId: String): Flow<List<EvidenceEntity>>

    /**
     * Get unviewed evidence
     */
    @Query("SELECT * FROM evidence WHERE userId = :userId AND isViewed = 0 AND isDeleted = 0 ORDER BY collectedAt DESC")
    fun getUnviewedEvidence(userId: String): Flow<List<EvidenceEntity>>

    /**
     * Get recent evidence (last N)
     */
    @Query("SELECT * FROM evidence WHERE userId = :userId AND isDeleted = 0 ORDER BY collectedAt DESC LIMIT :limit")
    fun getRecentEvidence(userId: String, limit: Int = 10): Flow<List<EvidenceEntity>>

    /**
     * Get evidence by rarity
     */
    @Query("SELECT * FROM evidence WHERE userId = :userId AND rarity = :rarity AND isDeleted = 0 ORDER BY collectedAt DESC")
    fun getEvidenceByRarity(userId: String, rarity: String): Flow<List<EvidenceEntity>>

    // ==================== QUERY - THE LOCKER DISPLAY ====================

    /**
     * Get all evidence for the Locker view (grouped by type)
     */
    @Query("""
        SELECT * FROM evidence
        WHERE userId = :userId AND isDeleted = 0
        ORDER BY
            CASE evidenceType
                WHEN 'receipt' THEN 1
                WHEN 'witness' THEN 2
                WHEN 'prophecy' THEN 3
                WHEN 'breakthrough' THEN 4
                WHEN 'streak' THEN 5
                ELSE 6
            END,
            collectedAt DESC
    """)
    fun getEvidenceForLocker(userId: String): Flow<List<EvidenceEntity>>

    /**
     * Get evidence count by type for locker summary
     */
    @Query("""
        SELECT evidenceType, COUNT(*) as count
        FROM evidence
        WHERE userId = :userId AND isDeleted = 0
        GROUP BY evidenceType
        ORDER BY count DESC
    """)
    suspend fun getEvidenceCountByType(userId: String): List<EvidenceTypeCount>

    /**
     * Get total evidence count
     */
    @Query("SELECT COUNT(*) FROM evidence WHERE userId = :userId AND isDeleted = 0")
    fun getTotalEvidenceCount(userId: String): Flow<Int>

    /**
     * Get unviewed evidence count (for badge)
     */
    @Query("SELECT COUNT(*) FROM evidence WHERE userId = :userId AND isViewed = 0 AND isDeleted = 0")
    fun getUnviewedEvidenceCount(userId: String): Flow<Int>

    // ==================== QUERY - SOURCE LOOKUP ====================

    /**
     * Check if evidence exists for a source (to prevent duplicates)
     */
    @Query("SELECT * FROM evidence WHERE sourceType = :sourceType AND sourceId = :sourceId AND isDeleted = 0 LIMIT 1")
    suspend fun getEvidenceBySource(sourceType: String, sourceId: Long): EvidenceEntity?

    /**
     * Get all evidence from a specific source
     */
    @Query("SELECT * FROM evidence WHERE sourceType = :sourceType AND isDeleted = 0 ORDER BY collectedAt DESC")
    fun getEvidenceFromSourceType(sourceType: String): Flow<List<EvidenceEntity>>

    // ==================== STATISTICS ====================

    /**
     * Get receipt count (Mirror contradictions found)
     */
    @Query("SELECT COUNT(*) FROM evidence WHERE userId = :userId AND evidenceType = 'receipt' AND isDeleted = 0")
    suspend fun getReceiptCount(userId: String): Int

    /**
     * Get witness count (Haven follow-ups completed)
     */
    @Query("SELECT COUNT(*) FROM evidence WHERE userId = :userId AND evidenceType = 'witness' AND isDeleted = 0")
    suspend fun getWitnessCount(userId: String): Int

    /**
     * Get prophecy count (Future Message predictions)
     */
    @Query("SELECT COUNT(*) FROM evidence WHERE userId = :userId AND evidenceType = 'prophecy' AND isDeleted = 0")
    suspend fun getProphecyCount(userId: String): Int

    /**
     * Get prophecy accuracy rate
     */
    @Query("""
        SELECT CAST(SUM(CASE WHEN predictionAccurate = 1 THEN 1 ELSE 0 END) AS FLOAT) / COUNT(*)
        FROM evidence
        WHERE userId = :userId AND evidenceType = 'prophecy' AND predictionAccurate IS NOT NULL AND isDeleted = 0
    """)
    suspend fun getProphecyAccuracyRate(userId: String): Float?

    /**
     * Get rarity distribution
     */
    @Query("""
        SELECT rarity, COUNT(*) as count
        FROM evidence
        WHERE userId = :userId AND isDeleted = 0
        GROUP BY rarity
        ORDER BY
            CASE rarity
                WHEN 'legendary' THEN 1
                WHEN 'epic' THEN 2
                WHEN 'rare' THEN 3
                WHEN 'common' THEN 4
                ELSE 5
            END
    """)
    suspend fun getRarityDistribution(userId: String): List<EvidenceRarityCount>

    // ==================== DELETE ====================

    /**
     * Soft delete evidence
     */
    @Query("UPDATE evidence SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :evidenceId")
    suspend fun softDeleteEvidence(evidenceId: Long, updatedAt: Long = System.currentTimeMillis())

    /**
     * Hard delete evidence
     */
    @Delete
    suspend fun deleteEvidence(evidence: EvidenceEntity)

    /**
     * Delete all evidence for a user
     */
    @Query("DELETE FROM evidence WHERE userId = :userId")
    suspend fun deleteAllEvidence(userId: String)
}

/**
 * Data class for evidence type count results
 */
data class EvidenceTypeCount(
    val evidenceType: String,
    val count: Int
)

/**
 * Data class for rarity distribution results
 */
data class EvidenceRarityCount(
    val rarity: String,
    val count: Int
)
