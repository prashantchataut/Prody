package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.dao.EvidenceRarityCount
import com.prody.prashant.data.local.dao.EvidenceTypeCount
import com.prody.prashant.data.local.entity.EvidenceEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Evidence (THE LOCKER) operations.
 *
 * Evidence replaces abstract XP/plants with concrete proof of growth:
 * - Receipt: Mirror found a contradiction with past entry
 * - Witness: Haven followed up on something user mentioned
 * - Prophecy: Future Message prediction was verified
 * - Breakthrough: Journal insight was captured
 * - Streak: Milestone achievement was reached
 */
interface EvidenceRepository {

    // ==================== INSERT/UPDATE ====================

    /**
     * Insert a new piece of evidence
     */
    suspend fun insertEvidence(evidence: EvidenceEntity): Result<Long>

    /**
     * Insert multiple pieces of evidence
     */
    suspend fun insertAllEvidence(evidence: List<EvidenceEntity>): Result<List<Long>>

    /**
     * Mark evidence as viewed
     */
    suspend fun markAsViewed(evidenceId: Long): Result<Unit>

    /**
     * Toggle pin status
     */
    suspend fun setPinned(evidenceId: Long, isPinned: Boolean): Result<Unit>

    // ==================== QUERY - BASIC ====================

    /**
     * Get all evidence for a user (most recent first)
     */
    fun getAllEvidence(userId: String = "local"): Flow<List<EvidenceEntity>>

    /**
     * Get evidence by ID
     */
    suspend fun getEvidenceById(evidenceId: Long): Result<EvidenceEntity?>

    /**
     * Get evidence by type
     */
    fun getEvidenceByType(userId: String = "local", type: String): Flow<List<EvidenceEntity>>

    /**
     * Get pinned evidence
     */
    fun getPinnedEvidence(userId: String = "local"): Flow<List<EvidenceEntity>>

    /**
     * Get unviewed evidence
     */
    fun getUnviewedEvidence(userId: String = "local"): Flow<List<EvidenceEntity>>

    /**
     * Get recent evidence (last N)
     */
    fun getRecentEvidence(userId: String = "local", limit: Int = 10): Flow<List<EvidenceEntity>>

    /**
     * Get evidence by rarity
     */
    fun getEvidenceByRarity(userId: String = "local", rarity: String): Flow<List<EvidenceEntity>>

    // ==================== QUERY - THE LOCKER DISPLAY ====================

    /**
     * Get all evidence for the Locker view (grouped by type)
     */
    fun getEvidenceForLocker(userId: String = "local"): Flow<List<EvidenceEntity>>

    /**
     * Get evidence count by type for locker summary
     */
    suspend fun getEvidenceCountByType(userId: String = "local"): List<EvidenceTypeCount>

    /**
     * Get total evidence count
     */
    fun getTotalEvidenceCount(userId: String = "local"): Flow<Int>

    /**
     * Get unviewed evidence count (for badge)
     */
    fun getUnviewedEvidenceCount(userId: String = "local"): Flow<Int>

    // ==================== QUERY - SOURCE LOOKUP ====================

    /**
     * Check if evidence exists for a source (to prevent duplicates)
     */
    suspend fun getEvidenceBySource(sourceType: String, sourceId: Long): EvidenceEntity?

    // ==================== STATISTICS ====================

    /**
     * Get receipt count (Mirror contradictions found)
     */
    suspend fun getReceiptCount(userId: String = "local"): Int

    /**
     * Get witness count (Haven follow-ups completed)
     */
    suspend fun getWitnessCount(userId: String = "local"): Int

    /**
     * Get prophecy count (Future Message predictions)
     */
    suspend fun getProphecyCount(userId: String = "local"): Int

    /**
     * Get prophecy accuracy rate
     */
    suspend fun getProphecyAccuracyRate(userId: String = "local"): Float?

    /**
     * Get rarity distribution
     */
    suspend fun getRarityDistribution(userId: String = "local"): List<EvidenceRarityCount>

    // ==================== EVIDENCE DROP LOGIC ====================

    /**
     * Create a Receipt evidence when Mirror finds a contradiction.
     * This is the core "THE RECEIPT" feature.
     */
    suspend fun dropReceiptEvidence(
        userId: String = "local",
        thenContent: String,
        nowContent: String,
        thenDate: Long,
        nowDate: Long,
        sourceJournalId: Long
    ): Result<Long>

    /**
     * Create a Witness evidence when Haven follows up.
     * This is the "Witness Mode" completion reward.
     */
    suspend fun dropWitnessEvidence(
        userId: String = "local",
        fact: String,
        outcome: String,
        sourceMemoryId: Long
    ): Result<Long>

    /**
     * Create a Prophecy evidence when a Future Message prediction is verified.
     */
    suspend fun dropProphecyEvidence(
        userId: String = "local",
        prediction: String,
        actual: String?,
        wasAccurate: Boolean,
        sourceFutureMessageId: Long
    ): Result<Long>

    /**
     * Create a Breakthrough evidence from a journal insight.
     */
    suspend fun dropBreakthroughEvidence(
        userId: String = "local",
        insight: String,
        sourceJournalId: Long
    ): Result<Long>

    /**
     * Create a Streak evidence for a milestone.
     */
    suspend fun dropStreakEvidence(
        userId: String = "local",
        milestone: String,
        streakCount: Int
    ): Result<Long>

    // ==================== DELETE ====================

    /**
     * Soft delete evidence
     */
    suspend fun softDeleteEvidence(evidenceId: Long): Result<Unit>

    /**
     * Delete all evidence for a user
     */
    suspend fun deleteAllEvidence(userId: String = "local"): Result<Unit>
}
