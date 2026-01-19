package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.EvidenceDao
import com.prody.prashant.data.local.dao.EvidenceRarityCount
import com.prody.prashant.data.local.dao.EvidenceTypeCount
import com.prody.prashant.data.local.entity.EvidenceEntity
import com.prody.prashant.data.local.entity.EvidenceRarity
import com.prody.prashant.data.local.entity.EvidenceType
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.repository.EvidenceRepository
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of EvidenceRepository for THE LOCKER feature.
 *
 * Manages evidence persistence and drop logic. Evidence replaces
 * abstract gamification with concrete proof of growth moments.
 */
@Singleton
class EvidenceRepositoryImpl @Inject constructor(
    private val evidenceDao: EvidenceDao
) : EvidenceRepository {

    // ==================== INSERT/UPDATE ====================

    override suspend fun insertEvidence(evidence: EvidenceEntity): Result<Long> {
        return try {
            val id = evidenceDao.insertEvidence(evidence)
            Result.Success(id)
        } catch (e: Exception) {
            Result.error(e, "Failed to insert evidence: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun insertAllEvidence(evidence: List<EvidenceEntity>): Result<List<Long>> {
        return try {
            val ids = evidenceDao.insertAllEvidence(evidence)
            Result.Success(ids)
        } catch (e: Exception) {
            Result.error(e, "Failed to insert evidence: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun markAsViewed(evidenceId: Long): Result<Unit> {
        return try {
            evidenceDao.markAsViewed(evidenceId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to mark evidence as viewed: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun setPinned(evidenceId: Long, isPinned: Boolean): Result<Unit> {
        return try {
            evidenceDao.setPinned(evidenceId, isPinned)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to update pin status: ${e.message}", ErrorType.DATABASE)
        }
    }

    // ==================== QUERY - BASIC ====================

    override fun getAllEvidence(userId: String): Flow<List<EvidenceEntity>> {
        return evidenceDao.getAllEvidence(userId)
    }

    override suspend fun getEvidenceById(evidenceId: Long): Result<EvidenceEntity?> {
        return try {
            val evidence = evidenceDao.getEvidenceById(evidenceId)
            Result.Success(evidence)
        } catch (e: Exception) {
            Result.error(e, "Failed to get evidence: ${e.message}", ErrorType.DATABASE)
        }
    }

    override fun getEvidenceByType(userId: String, type: String): Flow<List<EvidenceEntity>> {
        return evidenceDao.getEvidenceByType(userId, type)
    }

    override fun getPinnedEvidence(userId: String): Flow<List<EvidenceEntity>> {
        return evidenceDao.getPinnedEvidence(userId)
    }

    override fun getUnviewedEvidence(userId: String): Flow<List<EvidenceEntity>> {
        return evidenceDao.getUnviewedEvidence(userId)
    }

    override fun getRecentEvidence(userId: String, limit: Int): Flow<List<EvidenceEntity>> {
        return evidenceDao.getRecentEvidence(userId, limit)
    }

    override fun getEvidenceByRarity(userId: String, rarity: String): Flow<List<EvidenceEntity>> {
        return evidenceDao.getEvidenceByRarity(userId, rarity)
    }

    // ==================== QUERY - THE LOCKER DISPLAY ====================

    override fun getEvidenceForLocker(userId: String): Flow<List<EvidenceEntity>> {
        return evidenceDao.getEvidenceForLocker(userId)
    }

    override suspend fun getEvidenceCountByType(userId: String): List<EvidenceTypeCount> {
        return evidenceDao.getEvidenceCountByType(userId)
    }

    override fun getTotalEvidenceCount(userId: String): Flow<Int> {
        return evidenceDao.getTotalEvidenceCount(userId)
    }

    override fun getUnviewedEvidenceCount(userId: String): Flow<Int> {
        return evidenceDao.getUnviewedEvidenceCount(userId)
    }

    // ==================== QUERY - SOURCE LOOKUP ====================

    override suspend fun getEvidenceBySource(sourceType: String, sourceId: Long): EvidenceEntity? {
        return evidenceDao.getEvidenceBySource(sourceType, sourceId)
    }

    // ==================== STATISTICS ====================

    override suspend fun getReceiptCount(userId: String): Int {
        return evidenceDao.getReceiptCount(userId)
    }

    override suspend fun getWitnessCount(userId: String): Int {
        return evidenceDao.getWitnessCount(userId)
    }

    override suspend fun getProphecyCount(userId: String): Int {
        return evidenceDao.getProphecyCount(userId)
    }

    override suspend fun getProphecyAccuracyRate(userId: String): Float? {
        return evidenceDao.getProphecyAccuracyRate(userId)
    }

    override suspend fun getRarityDistribution(userId: String): List<EvidenceRarityCount> {
        return evidenceDao.getRarityDistribution(userId)
    }

    // ==================== EVIDENCE DROP LOGIC ====================

    /**
     * Drop a Receipt evidence when Mirror finds a contradiction.
     * Rarity is determined by the time span between entries.
     */
    override suspend fun dropReceiptEvidence(
        userId: String,
        thenContent: String,
        nowContent: String,
        thenDate: Long,
        nowDate: Long,
        sourceJournalId: Long
    ): Result<Long> {
        return try {
            // Check for duplicate
            val existing = evidenceDao.getEvidenceBySource("journal", sourceJournalId)
            if (existing != null) {
                return Result.Success(existing.id)
            }

            val daysApart = TimeUnit.MILLISECONDS.toDays(nowDate - thenDate).toInt()
            val rarity = calculateReceiptRarity(daysApart)

            val evidence = EvidenceEntity(
                userId = userId,
                evidenceType = EvidenceType.RECEIPT,
                content = thenContent.take(500), // "Then" content
                secondaryContent = nowContent.take(500), // "Now" content
                sourceType = "journal",
                sourceId = sourceJournalId,
                thenDate = thenDate,
                nowDate = nowDate,
                daysApart = daysApart,
                rarity = rarity
            )

            val id = evidenceDao.insertEvidence(evidence)
            Result.Success(id)
        } catch (e: Exception) {
            Result.error(e, "Failed to drop receipt evidence: ${e.message}", ErrorType.DATABASE)
        }
    }

    /**
     * Drop a Witness evidence when Haven follows up on a memory.
     */
    override suspend fun dropWitnessEvidence(
        userId: String,
        fact: String,
        outcome: String,
        sourceMemoryId: Long
    ): Result<Long> {
        return try {
            // Check for duplicate
            val existing = evidenceDao.getEvidenceBySource("haven_memory", sourceMemoryId)
            if (existing != null) {
                return Result.Success(existing.id)
            }

            // Rarity based on outcome
            val rarity = when (outcome.lowercase()) {
                "success", "achieved", "completed" -> EvidenceRarity.RARE
                "partial", "in_progress" -> EvidenceRarity.COMMON
                else -> EvidenceRarity.COMMON
            }

            val evidence = EvidenceEntity(
                userId = userId,
                evidenceType = EvidenceType.WITNESS,
                content = fact.take(500),
                secondaryContent = "Outcome: $outcome",
                sourceType = "haven_memory",
                sourceId = sourceMemoryId,
                witnessOutcome = outcome,
                rarity = rarity
            )

            val id = evidenceDao.insertEvidence(evidence)
            Result.Success(id)
        } catch (e: Exception) {
            Result.error(e, "Failed to drop witness evidence: ${e.message}", ErrorType.DATABASE)
        }
    }

    /**
     * Drop a Prophecy evidence when a Future Message prediction is verified.
     */
    override suspend fun dropProphecyEvidence(
        userId: String,
        prediction: String,
        actual: String?,
        wasAccurate: Boolean,
        sourceFutureMessageId: Long
    ): Result<Long> {
        return try {
            // Check for duplicate
            val existing = evidenceDao.getEvidenceBySource("future_message", sourceFutureMessageId)
            if (existing != null) {
                return Result.Success(existing.id)
            }

            // Accurate prophecies are rarer and more valuable
            val rarity = if (wasAccurate) EvidenceRarity.EPIC else EvidenceRarity.RARE

            val evidence = EvidenceEntity(
                userId = userId,
                evidenceType = EvidenceType.PROPHECY,
                content = prediction.take(500),
                secondaryContent = actual?.take(500),
                sourceType = "future_message",
                sourceId = sourceFutureMessageId,
                predictionAccurate = wasAccurate,
                rarity = rarity
            )

            val id = evidenceDao.insertEvidence(evidence)
            Result.Success(id)
        } catch (e: Exception) {
            Result.error(e, "Failed to drop prophecy evidence: ${e.message}", ErrorType.DATABASE)
        }
    }

    /**
     * Drop a Breakthrough evidence from a journal insight.
     */
    override suspend fun dropBreakthroughEvidence(
        userId: String,
        insight: String,
        sourceJournalId: Long
    ): Result<Long> {
        return try {
            // Don't check for duplicates - multiple breakthroughs from same entry are fine

            val evidence = EvidenceEntity(
                userId = userId,
                evidenceType = EvidenceType.BREAKTHROUGH,
                content = insight.take(500),
                sourceType = "journal",
                sourceId = sourceJournalId,
                rarity = EvidenceRarity.RARE
            )

            val id = evidenceDao.insertEvidence(evidence)
            Result.Success(id)
        } catch (e: Exception) {
            Result.error(e, "Failed to drop breakthrough evidence: ${e.message}", ErrorType.DATABASE)
        }
    }

    /**
     * Drop a Streak evidence for a milestone achievement.
     */
    override suspend fun dropStreakEvidence(
        userId: String,
        milestone: String,
        streakCount: Int
    ): Result<Long> {
        return try {
            val rarity = calculateStreakRarity(streakCount)

            val evidence = EvidenceEntity(
                userId = userId,
                evidenceType = EvidenceType.STREAK,
                content = milestone,
                secondaryContent = "$streakCount day streak",
                rarity = rarity
            )

            val id = evidenceDao.insertEvidence(evidence)
            Result.Success(id)
        } catch (e: Exception) {
            Result.error(e, "Failed to drop streak evidence: ${e.message}", ErrorType.DATABASE)
        }
    }

    // ==================== DELETE ====================

    override suspend fun softDeleteEvidence(evidenceId: Long): Result<Unit> {
        return try {
            evidenceDao.softDeleteEvidence(evidenceId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to delete evidence: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun deleteAllEvidence(userId: String): Result<Unit> {
        return try {
            evidenceDao.deleteAllEvidence(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to delete all evidence: ${e.message}", ErrorType.DATABASE)
        }
    }

    // ==================== RARITY CALCULATION ====================

    /**
     * Calculate rarity for Receipt evidence based on time gap.
     * Longer time gaps = rarer and more impactful contradictions.
     */
    private fun calculateReceiptRarity(daysApart: Int): String {
        return when {
            daysApart >= 365 -> EvidenceRarity.LEGENDARY // 1+ year ago
            daysApart >= 180 -> EvidenceRarity.EPIC      // 6+ months ago
            daysApart >= 90 -> EvidenceRarity.RARE       // 3+ months ago
            daysApart >= 30 -> EvidenceRarity.COMMON     // 1+ month ago
            else -> EvidenceRarity.COMMON
        }
    }

    /**
     * Calculate rarity for Streak evidence based on streak count.
     */
    private fun calculateStreakRarity(streakCount: Int): String {
        return when {
            streakCount >= 365 -> EvidenceRarity.LEGENDARY // 1 year streak
            streakCount >= 100 -> EvidenceRarity.EPIC      // 100 day streak
            streakCount >= 30 -> EvidenceRarity.RARE       // 30 day streak
            streakCount >= 7 -> EvidenceRarity.COMMON      // 1 week streak
            else -> EvidenceRarity.COMMON
        }
    }
}
