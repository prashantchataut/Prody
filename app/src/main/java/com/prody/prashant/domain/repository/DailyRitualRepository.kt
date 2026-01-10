package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.DailyRitualEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Daily Ritual operations.
 *
 * The Daily Ritual is a 60-second morning/evening habit that creates
 * the daily engagement hook for the app.
 */
interface DailyRitualRepository {

    // ==================== RETRIEVAL ====================

    /**
     * Get today's ritual status.
     */
    suspend fun getTodayRitual(userId: String = "local"): Result<DailyRitualEntity?>

    /**
     * Observe today's ritual status.
     */
    fun observeTodayRitual(userId: String = "local"): Flow<DailyRitualEntity?>

    /**
     * Get ritual for a specific date.
     */
    suspend fun getRitualForDate(userId: String = "local", date: Long): Result<DailyRitualEntity?>

    /**
     * Get all rituals.
     */
    fun getAllRituals(userId: String = "local"): Flow<List<DailyRitualEntity>>

    /**
     * Get rituals for a date range.
     */
    fun getRitualsForDateRange(
        userId: String = "local",
        startDate: Long,
        endDate: Long
    ): Flow<List<DailyRitualEntity>>

    /**
     * Get recent rituals.
     */
    fun getRecentRituals(userId: String = "local", limit: Int = 7): Flow<List<DailyRitualEntity>>

    // ==================== MORNING RITUAL ====================

    /**
     * Start or get today's ritual (creates if doesn't exist).
     */
    suspend fun getOrCreateTodayRitual(userId: String = "local"): Result<DailyRitualEntity>

    /**
     * Complete morning ritual.
     */
    suspend fun completeMorningRitual(
        userId: String = "local",
        intention: String?,
        mood: String?,
        wisdomId: Long? = null
    ): Result<Unit>

    /**
     * Update morning intention.
     */
    suspend fun updateMorningIntention(userId: String = "local", intention: String): Result<Unit>

    /**
     * Check if morning ritual is completed today.
     */
    suspend fun isMorningRitualCompletedToday(userId: String = "local"): Boolean

    // ==================== EVENING RITUAL ====================

    /**
     * Complete evening ritual.
     */
    suspend fun completeEveningRitual(
        userId: String = "local",
        dayRating: String,
        reflection: String?,
        mood: String?
    ): Result<Unit>

    /**
     * Update evening reflection.
     */
    suspend fun updateEveningReflection(userId: String = "local", reflection: String): Result<Unit>

    /**
     * Check if evening ritual is completed today.
     */
    suspend fun isEveningRitualCompletedToday(userId: String = "local"): Boolean

    // ==================== EXPANSION ====================

    /**
     * Mark ritual as expanded to journal entry.
     */
    suspend fun markExpandedToJournal(userId: String = "local", journalId: Long): Result<Unit>

    /**
     * Mark ritual as expanded to micro entry.
     */
    suspend fun markExpandedToMicroEntry(userId: String = "local", microEntryId: Long): Result<Unit>

    // ==================== STATISTICS ====================

    /**
     * Get total completed rituals count.
     */
    suspend fun getCompletedRitualsCount(userId: String = "local"): Int

    /**
     * Get morning ritual completion count.
     */
    suspend fun getMorningRitualCount(userId: String = "local"): Int

    /**
     * Get evening ritual completion count.
     */
    suspend fun getEveningRitualCount(userId: String = "local"): Int

    /**
     * Get both morning and evening completed count.
     */
    suspend fun getFullRitualDaysCount(userId: String = "local"): Int

    /**
     * Get current ritual streak (consecutive days with at least one ritual).
     */
    suspend fun getCurrentRitualStreak(userId: String = "local"): Int

    /**
     * Get day rating distribution.
     */
    suspend fun getDayRatingDistribution(userId: String = "local"): Map<String, Int>

    /**
     * Get days this week with rituals completed.
     */
    suspend fun getThisWeekRitualDays(userId: String = "local"): Int

    // ==================== CLEANUP ====================

    /**
     * Purge soft-deleted rituals.
     */
    suspend fun purgeSoftDeleted(): Int
}
