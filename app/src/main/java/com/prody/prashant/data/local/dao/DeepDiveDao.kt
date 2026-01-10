package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.DeepDiveEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Deep Dive operations.
 *
 * Handles all database operations for the Deep Dive feature,
 * including CRUD operations, scheduling, and analytics.
 */
@Dao
interface DeepDiveDao {

    // ==================== BASIC CRUD ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeepDive(deepDive: DeepDiveEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeepDives(deepDives: List<DeepDiveEntity>)

    @Update
    suspend fun updateDeepDive(deepDive: DeepDiveEntity)

    @Delete
    suspend fun deleteDeepDive(deepDive: DeepDiveEntity)

    @Query("DELETE FROM deep_dives WHERE id = :id")
    suspend fun deleteDeepDiveById(id: Long)

    @Query("SELECT * FROM deep_dives WHERE id = :id AND isDeleted = 0 LIMIT 1")
    suspend fun getDeepDiveById(id: Long): DeepDiveEntity?

    @Query("SELECT * FROM deep_dives WHERE id = :id AND isDeleted = 0")
    fun observeDeepDiveById(id: Long): Flow<DeepDiveEntity?>

    // ==================== SCHEDULED DEEP DIVES ====================

    /**
     * Get all scheduled (not completed) deep dives for a user
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 0
        AND isDeleted = 0
        ORDER BY scheduledDate ASC
    """)
    fun getScheduledDeepDives(userId: String): Flow<List<DeepDiveEntity>>

    /**
     * Get scheduled deep dives (not completed) synchronously
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 0
        AND isDeleted = 0
        ORDER BY scheduledDate ASC
    """)
    suspend fun getScheduledDeepDivesSync(userId: String): List<DeepDiveEntity>

    /**
     * Get the next scheduled deep dive for a user
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 0
        AND isDeleted = 0
        ORDER BY scheduledDate ASC
        LIMIT 1
    """)
    suspend fun getNextScheduledDeepDive(userId: String): DeepDiveEntity?

    /**
     * Get deep dives scheduled for a specific date
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND scheduledDate BETWEEN :startOfDay AND :endOfDay
        AND isDeleted = 0
        ORDER BY scheduledDate ASC
    """)
    suspend fun getDeepDivesForDate(userId: String, startOfDay: Long, endOfDay: Long): List<DeepDiveEntity>

    /**
     * Get overdue deep dives (scheduled in the past but not completed)
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 0
        AND scheduledDate < :currentTime
        AND isDeleted = 0
        ORDER BY scheduledDate DESC
    """)
    suspend fun getOverdueDeepDives(userId: String, currentTime: Long = System.currentTimeMillis()): List<DeepDiveEntity>

    // ==================== COMPLETED DEEP DIVES ====================

    /**
     * Get all completed deep dives for a user
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 1
        AND isDeleted = 0
        ORDER BY completedAt DESC
    """)
    fun getCompletedDeepDives(userId: String): Flow<List<DeepDiveEntity>>

    /**
     * Get completed deep dives synchronously
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 1
        AND isDeleted = 0
        ORDER BY completedAt DESC
    """)
    suspend fun getCompletedDeepDivesSync(userId: String): List<DeepDiveEntity>

    /**
     * Get recent completed deep dives
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 1
        AND isDeleted = 0
        ORDER BY completedAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentCompletedDeepDives(userId: String, limit: Int): List<DeepDiveEntity>

    /**
     * Get completed deep dives by theme
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND theme = :theme
        AND isCompleted = 1
        AND isDeleted = 0
        ORDER BY completedAt DESC
    """)
    fun getCompletedDeepDivesByTheme(userId: String, theme: String): Flow<List<DeepDiveEntity>>

    // ==================== THEME-BASED QUERIES ====================

    /**
     * Get all deep dives for a specific theme
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND theme = :theme
        AND isDeleted = 0
        ORDER BY scheduledDate DESC
    """)
    fun getDeepDivesByTheme(userId: String, theme: String): Flow<List<DeepDiveEntity>>

    /**
     * Get count of completed deep dives by theme
     */
    @Query("""
        SELECT COUNT(*) FROM deep_dives
        WHERE userId = :userId
        AND theme = :theme
        AND isCompleted = 1
        AND isDeleted = 0
    """)
    suspend fun getCompletedCountByTheme(userId: String, theme: String): Int

    /**
     * Get the last completed deep dive for a theme
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND theme = :theme
        AND isCompleted = 1
        AND isDeleted = 0
        ORDER BY completedAt DESC
        LIMIT 1
    """)
    suspend fun getLastCompletedDeepDiveForTheme(userId: String, theme: String): DeepDiveEntity?

    /**
     * Check if a theme has been completed at least once
     */
    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM deep_dives
            WHERE userId = :userId
            AND theme = :theme
            AND isCompleted = 1
            AND isDeleted = 0
        )
    """)
    suspend fun hasCompletedTheme(userId: String, theme: String): Boolean

    /**
     * Get themes that have never been completed
     */
    @Query("""
        SELECT DISTINCT theme FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 0
        AND isDeleted = 0
        AND theme NOT IN (
            SELECT DISTINCT theme FROM deep_dives
            WHERE userId = :userId
            AND isCompleted = 1
            AND isDeleted = 0
        )
    """)
    suspend fun getUnexploredThemes(userId: String): List<String>

    // ==================== STATISTICS & ANALYTICS ====================

    /**
     * Get total count of completed deep dives
     */
    @Query("""
        SELECT COUNT(*) FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 1
        AND isDeleted = 0
    """)
    suspend fun getTotalCompletedCount(userId: String): Int

    /**
     * Get total count of scheduled deep dives
     */
    @Query("""
        SELECT COUNT(*) FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 0
        AND isDeleted = 0
    """)
    suspend fun getTotalScheduledCount(userId: String): Int

    /**
     * Get average duration of completed deep dives
     */
    @Query("""
        SELECT AVG(durationMinutes) FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 1
        AND durationMinutes > 0
        AND isDeleted = 0
    """)
    suspend fun getAverageDuration(userId: String): Double?

    /**
     * Get average mood improvement (moodAfter - moodBefore)
     */
    @Query("""
        SELECT AVG(moodAfter - moodBefore) FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 1
        AND moodBefore IS NOT NULL
        AND moodAfter IS NOT NULL
        AND isDeleted = 0
    """)
    suspend fun getAverageMoodImprovement(userId: String): Double?

    /**
     * Get completed deep dives in a date range
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 1
        AND completedAt BETWEEN :startDate AND :endDate
        AND isDeleted = 0
        ORDER BY completedAt DESC
    """)
    suspend fun getCompletedInDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): List<DeepDiveEntity>

    /**
     * Get theme frequency (how many times each theme was completed)
     */
    @Query("""
        SELECT theme, COUNT(*) as count FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 1
        AND isDeleted = 0
        GROUP BY theme
        ORDER BY count DESC
    """)
    suspend fun getThemeFrequency(userId: String): List<ThemeCount>

    /**
     * Get most recent activity date
     */
    @Query("""
        SELECT MAX(completedAt) FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 1
        AND isDeleted = 0
    """)
    suspend fun getMostRecentCompletionDate(userId: String): Long?

    // ==================== SEARCH & FILTER ====================

    /**
     * Search deep dives by content
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isDeleted = 0
        AND (
            openingReflection LIKE '%' || :query || '%'
            OR coreResponse LIKE '%' || :query || '%'
            OR keyInsight LIKE '%' || :query || '%'
            OR commitmentStatement LIKE '%' || :query || '%'
        )
        ORDER BY
            CASE WHEN isCompleted = 1 THEN completedAt ELSE scheduledDate END DESC
    """)
    fun searchDeepDives(userId: String, query: String): Flow<List<DeepDiveEntity>>

    /**
     * Get all deep dives (for backup/export)
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isDeleted = 0
        ORDER BY scheduledDate DESC
    """)
    suspend fun getAllDeepDivesSync(userId: String): List<DeepDiveEntity>

    // ==================== PROGRESS TRACKING ====================

    /**
     * Update current step of a deep dive
     */
    @Query("""
        UPDATE deep_dives
        SET currentStep = :step,
            updatedAt = :updatedAt
        WHERE id = :id
    """)
    suspend fun updateCurrentStep(id: Long, step: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Mark deep dive as completed
     */
    @Query("""
        UPDATE deep_dives
        SET isCompleted = 1,
            completedAt = :completedAt,
            currentStep = 'completed',
            updatedAt = :completedAt
        WHERE id = :id
    """)
    suspend fun markAsCompleted(id: Long, completedAt: Long = System.currentTimeMillis())

    /**
     * Update session metadata (started time, duration)
     */
    @Query("""
        UPDATE deep_dives
        SET sessionStartedAt = :startedAt,
            durationMinutes = :durationMinutes,
            updatedAt = :updatedAt
        WHERE id = :id
    """)
    suspend fun updateSessionMetadata(
        id: Long,
        startedAt: Long,
        durationMinutes: Int,
        updatedAt: Long = System.currentTimeMillis()
    )

    // ==================== NOTIFICATIONS ====================

    /**
     * Mark notification as sent
     */
    @Query("""
        UPDATE deep_dives
        SET isScheduledNotificationSent = 1,
            reminderSentAt = :sentAt,
            updatedAt = :sentAt
        WHERE id = :id
    """)
    suspend fun markNotificationSent(id: Long, sentAt: Long = System.currentTimeMillis())

    /**
     * Get deep dives that need notification (scheduled soon, notification not sent)
     */
    @Query("""
        SELECT * FROM deep_dives
        WHERE userId = :userId
        AND isCompleted = 0
        AND isScheduledNotificationSent = 0
        AND scheduledDate BETWEEN :startWindow AND :endWindow
        AND isDeleted = 0
        ORDER BY scheduledDate ASC
    """)
    suspend fun getDeepDivesNeedingNotification(
        userId: String,
        startWindow: Long,
        endWindow: Long
    ): List<DeepDiveEntity>

    // ==================== SYNC ====================

    @Query("SELECT * FROM deep_dives WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncDeepDives(): List<DeepDiveEntity>

    @Query("UPDATE deep_dives SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, syncTime: Long = System.currentTimeMillis())

    // ==================== CLEANUP ====================

    @Query("UPDATE deep_dives SET isDeleted = 1, updatedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteDeepDive(id: Long, deletedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM deep_dives WHERE isDeleted = 1")
    suspend fun purgeSoftDeleted(): Int

    @Query("DELETE FROM deep_dives WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}

/**
 * Data class for theme count query
 */
data class ThemeCount(
    val theme: String,
    val count: Int
)
