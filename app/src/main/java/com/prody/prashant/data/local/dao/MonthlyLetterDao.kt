package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.MonthlyLetterEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for monthly growth letters.
 *
 * Provides access to monthly letters with support for:
 * - Getting letters by month/year
 * - Unread letter notifications
 * - Historical letter browsing
 * - Favorites management
 */
@Dao
interface MonthlyLetterDao {

    // ==================== BASIC CRUD ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: MonthlyLetterEntity): Long

    @Update
    suspend fun updateLetter(letter: MonthlyLetterEntity)

    @Delete
    suspend fun deleteLetter(letter: MonthlyLetterEntity)

    @Query("DELETE FROM monthly_letters WHERE id = :letterId")
    suspend fun deleteLetterById(letterId: Long)

    // ==================== QUERIES ====================

    /**
     * Get all letters ordered by date (newest first)
     */
    @Query("SELECT * FROM monthly_letters WHERE userId = :userId AND isDeleted = 0 ORDER BY year DESC, month DESC")
    fun getAllLetters(userId: String = "local"): Flow<List<MonthlyLetterEntity>>

    /**
     * Get a specific letter by ID
     */
    @Query("SELECT * FROM monthly_letters WHERE id = :letterId")
    suspend fun getLetterById(letterId: Long): MonthlyLetterEntity?

    /**
     * Observe a specific letter by ID
     */
    @Query("SELECT * FROM monthly_letters WHERE id = :letterId")
    fun observeLetterById(letterId: Long): Flow<MonthlyLetterEntity?>

    /**
     * Get letter for a specific month and year
     */
    @Query("SELECT * FROM monthly_letters WHERE userId = :userId AND month = :month AND year = :year AND isDeleted = 0")
    suspend fun getLetterForMonth(userId: String = "local", month: Int, year: Int): MonthlyLetterEntity?

    /**
     * Observe letter for a specific month and year
     */
    @Query("SELECT * FROM monthly_letters WHERE userId = :userId AND month = :month AND year = :year AND isDeleted = 0")
    fun observeLetterForMonth(userId: String = "local", month: Int, year: Int): Flow<MonthlyLetterEntity?>

    /**
     * Get all unread letters
     */
    @Query("SELECT * FROM monthly_letters WHERE userId = :userId AND isRead = 0 AND isDeleted = 0 ORDER BY year DESC, month DESC")
    fun getUnreadLetters(userId: String = "local"): Flow<List<MonthlyLetterEntity>>

    /**
     * Get count of unread letters
     */
    @Query("SELECT COUNT(*) FROM monthly_letters WHERE userId = :userId AND isRead = 0 AND isDeleted = 0")
    fun getUnreadLetterCount(userId: String = "local"): Flow<Int>

    /**
     * Get all favorite letters
     */
    @Query("SELECT * FROM monthly_letters WHERE userId = :userId AND isFavorite = 1 AND isDeleted = 0 ORDER BY year DESC, month DESC")
    fun getFavoriteLetters(userId: String = "local"): Flow<List<MonthlyLetterEntity>>

    /**
     * Get the most recent letter
     */
    @Query("SELECT * FROM monthly_letters WHERE userId = :userId AND isDeleted = 0 ORDER BY year DESC, month DESC LIMIT 1")
    suspend fun getMostRecentLetter(userId: String = "local"): MonthlyLetterEntity?

    /**
     * Get the most recent letter as Flow
     */
    @Query("SELECT * FROM monthly_letters WHERE userId = :userId AND isDeleted = 0 ORDER BY year DESC, month DESC LIMIT 1")
    fun observeMostRecentLetter(userId: String = "local"): Flow<MonthlyLetterEntity?>

    /**
     * Get letters for a specific year
     */
    @Query("SELECT * FROM monthly_letters WHERE userId = :userId AND year = :year AND isDeleted = 0 ORDER BY month DESC")
    fun getLettersForYear(userId: String = "local", year: Int): Flow<List<MonthlyLetterEntity>>

    /**
     * Get recent N letters
     */
    @Query("SELECT * FROM monthly_letters WHERE userId = :userId AND isDeleted = 0 ORDER BY year DESC, month DESC LIMIT :limit")
    fun getRecentLetters(userId: String = "local", limit: Int): Flow<List<MonthlyLetterEntity>>

    // ==================== UPDATE OPERATIONS ====================

    /**
     * Mark letter as read
     */
    @Query("UPDATE monthly_letters SET isRead = 1, readAt = :readAt WHERE id = :letterId")
    suspend fun markAsRead(letterId: Long, readAt: Long = System.currentTimeMillis())

    /**
     * Toggle favorite status
     */
    @Query("UPDATE monthly_letters SET isFavorite = :isFavorite WHERE id = :letterId")
    suspend fun updateFavoriteStatus(letterId: Long, isFavorite: Boolean)

    /**
     * Update shared timestamp
     */
    @Query("UPDATE monthly_letters SET sharedAt = :sharedAt WHERE id = :letterId")
    suspend fun updateSharedTimestamp(letterId: Long, sharedAt: Long = System.currentTimeMillis())

    // ==================== ANALYTICS ====================

    /**
     * Get total letter count
     */
    @Query("SELECT COUNT(*) FROM monthly_letters WHERE userId = :userId AND isDeleted = 0")
    suspend fun getTotalLetterCount(userId: String = "local"): Int

    /**
     * Get count of letters with activity
     */
    @Query("SELECT COUNT(*) FROM monthly_letters WHERE userId = :userId AND entriesCount > 0 AND isDeleted = 0")
    suspend fun getActiveLetterCount(userId: String = "local"): Int

    /**
     * Check if letter exists for month
     */
    @Query("SELECT COUNT(*) FROM monthly_letters WHERE userId = :userId AND month = :month AND year = :year AND isDeleted = 0")
    suspend fun letterExistsForMonth(userId: String = "local", month: Int, year: Int): Int

    // ==================== SYNC & CLEANUP ====================

    /**
     * Soft delete letter
     */
    @Query("UPDATE monthly_letters SET isDeleted = 1 WHERE id = :letterId")
    suspend fun softDeleteLetter(letterId: Long)

    /**
     * Get letters pending sync
     */
    @Query("SELECT * FROM monthly_letters WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncLetters(): List<MonthlyLetterEntity>

    /**
     * Update sync status
     */
    @Query("UPDATE monthly_letters SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :letterId")
    suspend fun updateSyncStatus(letterId: Long, status: String, syncTime: Long = System.currentTimeMillis())

    /**
     * Delete old letters (keep only recent N)
     * Useful for managing storage
     */
    @Query("""
        DELETE FROM monthly_letters
        WHERE id NOT IN (
            SELECT id FROM monthly_letters
            WHERE userId = :userId AND isDeleted = 0
            ORDER BY year DESC, month DESC
            LIMIT :keepCount
        ) AND isFavorite = 0 AND userId = :userId
    """)
    suspend fun deleteOldLetters(userId: String = "local", keepCount: Int = 12)

    /**
     * Get all letters for backup
     */
    @Query("SELECT * FROM monthly_letters WHERE isDeleted = 0 ORDER BY year DESC, month DESC")
    suspend fun getAllLettersForBackup(): List<MonthlyLetterEntity>

    /**
     * Insert multiple letters (for restore)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetters(letters: List<MonthlyLetterEntity>)
}
