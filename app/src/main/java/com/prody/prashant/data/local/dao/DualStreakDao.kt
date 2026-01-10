package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.DualStreakEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Dual Streak System.
 *
 * Manages two independent streaks:
 * - Wisdom Streak: Quick daily engagement (viewing wisdom)
 * - Reflection Streak: Deep engagement (journaling, reflection)
 *
 * Each streak has its own grace period (one skip per 14 days).
 */
@Dao
interface DualStreakDao {

    // ============== CORE QUERIES ==============

    /**
     * Get dual streak data for a user (reactive).
     */
    @Query("SELECT * FROM dual_streaks WHERE userId = :userId LIMIT 1")
    fun getDualStreakFlow(userId: String = "local"): Flow<DualStreakEntity?>

    /**
     * Get dual streak data for a user (one-time).
     */
    @Query("SELECT * FROM dual_streaks WHERE userId = :userId LIMIT 1")
    suspend fun getDualStreak(userId: String = "local"): DualStreakEntity?

    /**
     * Insert or update dual streak data.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(dualStreak: DualStreakEntity)

    /**
     * Delete dual streak data for a user.
     */
    @Query("DELETE FROM dual_streaks WHERE userId = :userId")
    suspend fun delete(userId: String = "local")

    // ============== WISDOM STREAK OPERATIONS ==============

    /**
     * Maintain wisdom streak for today.
     * Increments streak if not already maintained today.
     */
    @Transaction
    suspend fun maintainWisdomStreak(userId: String = "local"): Boolean {
        val now = System.currentTimeMillis()
        val todayStart = getTodayStartTimestamp()

        // Get or create streak data
        var streak = getDualStreak(userId) ?: DualStreakEntity(userId = userId)

        // Check if already maintained today
        if (streak.isWisdomMaintainedToday()) {
            return false // Already maintained
        }

        // Check if this continues the streak or breaks it
        val isConsecutive = streak.wasWisdomMaintainedYesterday()
        val newCurrent = if (isConsecutive) streak.wisdomStreakCurrent + 1 else 1
        val newLongest = maxOf(newCurrent, streak.wisdomStreakLongest)

        // Reset grace period if it's time
        val graceAvailable = DualStreakEntity.isWisdomGraceAvailable(streak)
        val updatedStreak = streak.copy(
            wisdomStreakCurrent = newCurrent,
            wisdomStreakLongest = newLongest,
            wisdomLastMaintainedDate = now,
            wisdomGracePeriodUsed = if (graceAvailable) false else streak.wisdomGracePeriodUsed,
            wisdomGracePeriodResetDate = if (graceAvailable) 0 else streak.wisdomGracePeriodResetDate,
            updatedAt = now
        )

        insertOrUpdate(updatedStreak)
        return true
    }

    /**
     * Apply grace period to wisdom streak to preserve it after missing a day.
     * Returns true if grace was successfully applied, false if not available.
     */
    @Transaction
    suspend fun applyWisdomGrace(userId: String = "local"): Boolean {
        val now = System.currentTimeMillis()
        val streak = getDualStreak(userId) ?: return false

        // Check if grace is available
        if (!DualStreakEntity.isWisdomGraceAvailable(streak)) {
            return false // Grace not available
        }

        // Check if we actually need grace (streak would be broken without it)
        if (streak.isWisdomMaintainedToday() || streak.wasWisdomMaintainedYesterday()) {
            return false // Don't need grace
        }

        // Apply grace period - preserve streak but mark grace as used
        val gracePeriodEndDate = now + (DualStreakEntity.GRACE_PERIOD_DAYS * DualStreakEntity.MILLIS_PER_DAY)
        val updatedStreak = streak.copy(
            wisdomGracePeriodUsed = true,
            wisdomGracePeriodUsedDate = now,
            wisdomGracePeriodResetDate = gracePeriodEndDate,
            wisdomLastMaintainedDate = now, // Extend last maintained to today
            updatedAt = now
        )

        insertOrUpdate(updatedStreak)
        return true
    }

    // ============== REFLECTION STREAK OPERATIONS ==============

    /**
     * Maintain reflection streak for today.
     * Increments streak if not already maintained today.
     */
    @Transaction
    suspend fun maintainReflectionStreak(userId: String = "local"): Boolean {
        val now = System.currentTimeMillis()
        val todayStart = getTodayStartTimestamp()

        // Get or create streak data
        var streak = getDualStreak(userId) ?: DualStreakEntity(userId = userId)

        // Check if already maintained today
        if (streak.isReflectionMaintainedToday()) {
            return false // Already maintained
        }

        // Check if this continues the streak or breaks it
        val isConsecutive = streak.wasReflectionMaintainedYesterday()
        val newCurrent = if (isConsecutive) streak.reflectionStreakCurrent + 1 else 1
        val newLongest = maxOf(newCurrent, streak.reflectionStreakLongest)

        // Reset grace period if it's time
        val graceAvailable = DualStreakEntity.isReflectionGraceAvailable(streak)
        val updatedStreak = streak.copy(
            reflectionStreakCurrent = newCurrent,
            reflectionStreakLongest = newLongest,
            reflectionLastMaintainedDate = now,
            reflectionGracePeriodUsed = if (graceAvailable) false else streak.reflectionGracePeriodUsed,
            reflectionGracePeriodResetDate = if (graceAvailable) 0 else streak.reflectionGracePeriodResetDate,
            updatedAt = now
        )

        insertOrUpdate(updatedStreak)
        return true
    }

    /**
     * Apply grace period to reflection streak to preserve it after missing a day.
     * Returns true if grace was successfully applied, false if not available.
     */
    @Transaction
    suspend fun applyReflectionGrace(userId: String = "local"): Boolean {
        val now = System.currentTimeMillis()
        val streak = getDualStreak(userId) ?: return false

        // Check if grace is available
        if (!DualStreakEntity.isReflectionGraceAvailable(streak)) {
            return false // Grace not available
        }

        // Check if we actually need grace (streak would be broken without it)
        if (streak.isReflectionMaintainedToday() || streak.wasReflectionMaintainedYesterday()) {
            return false // Don't need grace
        }

        // Apply grace period - preserve streak but mark grace as used
        val gracePeriodEndDate = now + (DualStreakEntity.GRACE_PERIOD_DAYS * DualStreakEntity.MILLIS_PER_DAY)
        val updatedStreak = streak.copy(
            reflectionGracePeriodUsed = true,
            reflectionGracePeriodUsedDate = now,
            reflectionGracePeriodResetDate = gracePeriodEndDate,
            reflectionLastMaintainedDate = now, // Extend last maintained to today
            updatedAt = now
        )

        insertOrUpdate(updatedStreak)
        return true
    }

    // ============== HELPER FUNCTIONS ==============

    private fun getTodayStartTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
