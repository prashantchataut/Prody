package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.DailyRitualEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for DailyRitual operations.
 *
 * Handles all database operations for the Daily Ritual feature,
 * including morning/evening ritual tracking and statistics.
 */
@Dao
interface DailyRitualDao {

    // ==================== BASIC CRUD ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRitual(ritual: DailyRitualEntity): Long

    @Update
    suspend fun updateRitual(ritual: DailyRitualEntity)

    @Delete
    suspend fun deleteRitual(ritual: DailyRitualEntity)

    @Query("DELETE FROM daily_rituals WHERE id = :id")
    suspend fun deleteRitualById(id: Long)

    // ==================== TODAY'S RITUAL ====================

    /**
     * Get or create today's ritual
     */
    @Query("SELECT * FROM daily_rituals WHERE userId = :userId AND date = :date AND isDeleted = 0 LIMIT 1")
    suspend fun getRitualForDate(userId: String, date: Long): DailyRitualEntity?

    @Query("SELECT * FROM daily_rituals WHERE userId = :userId AND date = :date")
    fun observeRitualForDate(userId: String, date: Long): Flow<DailyRitualEntity?>

    /**
     * Check if ritual exists for today
     */
    @Query("SELECT EXISTS(SELECT 1 FROM daily_rituals WHERE userId = :userId AND date = :date AND isDeleted = 0)")
    suspend fun hasRitualForDate(userId: String, date: Long): Boolean

    // ==================== MORNING RITUAL ====================

    /**
     * Complete morning ritual with intention
     */
    @Query("""
        UPDATE daily_rituals
        SET morningCompleted = 1,
            morningCompletedAt = :completedAt,
            morningIntention = :intention,
            morningMood = :mood,
            morningWisdomId = :wisdomId,
            updatedAt = :completedAt
        WHERE id = :id
    """)
    suspend fun completeMorningRitual(
        id: Long,
        intention: String?,
        mood: String?,
        wisdomId: Long?,
        completedAt: Long = System.currentTimeMillis()
    )

    /**
     * Check if morning ritual is complete for today
     */
    @Query("SELECT morningCompleted FROM daily_rituals WHERE userId = :userId AND date = :date AND isDeleted = 0")
    suspend fun isMorningRitualComplete(userId: String, date: Long): Boolean?

    // ==================== EVENING RITUAL ====================

    /**
     * Complete evening ritual with reflection
     */
    @Query("""
        UPDATE daily_rituals
        SET eveningCompleted = 1,
            eveningCompletedAt = :completedAt,
            eveningDayRating = :dayRating,
            eveningReflection = :reflection,
            eveningMood = :mood,
            updatedAt = :completedAt
        WHERE id = :id
    """)
    suspend fun completeEveningRitual(
        id: Long,
        dayRating: String?,
        reflection: String?,
        mood: String?,
        completedAt: Long = System.currentTimeMillis()
    )

    /**
     * Check if evening ritual is complete for today
     */
    @Query("SELECT eveningCompleted FROM daily_rituals WHERE userId = :userId AND date = :date AND isDeleted = 0")
    suspend fun isEveningRitualComplete(userId: String, date: Long): Boolean?

    // ==================== EXPANSION TRACKING ====================

    /**
     * Mark that user expanded ritual to full journal
     */
    @Query("UPDATE daily_rituals SET expandedToJournalId = :journalId, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setExpandedToJournal(id: Long, journalId: Long, updatedAt: Long = System.currentTimeMillis())

    /**
     * Mark that user expanded ritual to micro entry
     */
    @Query("UPDATE daily_rituals SET expandedToMicroEntryId = :microEntryId, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setExpandedToMicroEntry(id: Long, microEntryId: Long, updatedAt: Long = System.currentTimeMillis())

    // ==================== STATISTICS ====================

    /**
     * Get ritual history for a date range
     */
    @Query("""
        SELECT * FROM daily_rituals
        WHERE userId = :userId
        AND date BETWEEN :startDate AND :endDate
        AND isDeleted = 0
        ORDER BY date DESC
    """)
    fun getRitualsInRange(userId: String, startDate: Long, endDate: Long): Flow<List<DailyRitualEntity>>

    /**
     * Get count of completed morning rituals
     */
    @Query("SELECT COUNT(*) FROM daily_rituals WHERE userId = :userId AND morningCompleted = 1 AND isDeleted = 0")
    suspend fun getMorningRitualsCompletedCount(userId: String): Int

    /**
     * Get count of completed evening rituals
     */
    @Query("SELECT COUNT(*) FROM daily_rituals WHERE userId = :userId AND eveningCompleted = 1 AND isDeleted = 0")
    suspend fun getEveningRitualsCompletedCount(userId: String): Int

    /**
     * Get count of fully completed days (both rituals)
     */
    @Query("SELECT COUNT(*) FROM daily_rituals WHERE userId = :userId AND morningCompleted = 1 AND eveningCompleted = 1 AND isDeleted = 0")
    suspend fun getFullyCompletedDaysCount(userId: String): Int

    /**
     * Get ritual completion streak
     */
    @Query("""
        SELECT COUNT(*) FROM (
            SELECT date FROM daily_rituals
            WHERE userId = :userId
            AND (morningCompleted = 1 OR eveningCompleted = 1)
            AND isDeleted = 0
            AND date >= :streakStartDate
            ORDER BY date DESC
        )
    """)
    suspend fun getRitualStreakDays(userId: String, streakStartDate: Long): Int

    /**
     * Get day ratings distribution for a period
     */
    @Query("""
        SELECT eveningDayRating as rating, COUNT(*) as count
        FROM daily_rituals
        WHERE userId = :userId
        AND eveningDayRating IS NOT NULL
        AND date >= :since
        AND isDeleted = 0
        GROUP BY eveningDayRating
    """)
    suspend fun getDayRatingsDistribution(userId: String, since: Long): List<DayRatingCount>

    /**
     * Get recent intentions for pattern analysis
     */
    @Query("""
        SELECT morningIntention FROM daily_rituals
        WHERE userId = :userId
        AND morningIntention IS NOT NULL
        AND morningIntention != ''
        AND isDeleted = 0
        ORDER BY date DESC
        LIMIT :limit
    """)
    suspend fun getRecentIntentions(userId: String, limit: Int = 10): List<String>

    // ==================== SYNC ====================

    @Query("SELECT * FROM daily_rituals WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncRituals(): List<DailyRitualEntity>

    @Query("UPDATE daily_rituals SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, syncTime: Long = System.currentTimeMillis())

    // ==================== CLEANUP ====================

    @Query("UPDATE daily_rituals SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteRitual(id: Long)

    @Query("DELETE FROM daily_rituals WHERE isDeleted = 1")
    suspend fun purgeSoftDeleted(): Int

    // ==================== BACKUP ====================

    @Query("SELECT * FROM daily_rituals ORDER BY date DESC")
    suspend fun getAllRitualsSync(): List<DailyRitualEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRituals(rituals: List<DailyRitualEntity>)
}

/**
 * Data class for day rating distribution query
 */
data class DayRatingCount(
    val rating: String?,
    val count: Int
)
