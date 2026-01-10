package com.prody.prashant.domain.repository

import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.model.MonthlyLetter
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

/**
 * Repository interface for Monthly Growth Letter operations.
 *
 * Manages the creation, retrieval, and lifecycle of personalized monthly letters
 * that summarize the user's journaling journey.
 */
interface MonthlyLetterRepository {

    // ==================== RETRIEVAL ====================

    /**
     * Get all letters ordered by date (newest first)
     */
    fun getAllLetters(userId: String = "local"): Flow<List<MonthlyLetter>>

    /**
     * Get letter by ID
     */
    suspend fun getLetterById(letterId: Long): Result<MonthlyLetter>

    /**
     * Observe letter by ID
     */
    fun observeLetterById(letterId: Long): Flow<MonthlyLetter?>

    /**
     * Get letter for a specific month
     */
    suspend fun getLetterForMonth(userId: String = "local", monthYear: YearMonth): Result<MonthlyLetter?>

    /**
     * Observe letter for a specific month
     */
    fun observeLetterForMonth(userId: String = "local", monthYear: YearMonth): Flow<MonthlyLetter?>

    /**
     * Get the most recent letter
     */
    suspend fun getMostRecentLetter(userId: String = "local"): Result<MonthlyLetter?>

    /**
     * Observe the most recent letter
     */
    fun observeMostRecentLetter(userId: String = "local"): Flow<MonthlyLetter?>

    /**
     * Get all unread letters
     */
    fun getUnreadLetters(userId: String = "local"): Flow<List<MonthlyLetter>>

    /**
     * Get count of unread letters
     */
    fun getUnreadLetterCount(userId: String = "local"): Flow<Int>

    /**
     * Get all favorite letters
     */
    fun getFavoriteLetters(userId: String = "local"): Flow<List<MonthlyLetter>>

    /**
     * Get recent N letters
     */
    fun getRecentLetters(userId: String = "local", limit: Int = 6): Flow<List<MonthlyLetter>>

    /**
     * Get letters for a specific year
     */
    fun getLettersForYear(userId: String = "local", year: Int): Flow<List<MonthlyLetter>>

    // ==================== CREATE/UPDATE ====================

    /**
     * Save a letter (insert or update)
     */
    suspend fun saveLetter(letter: MonthlyLetter): Result<Long>

    /**
     * Mark letter as read
     */
    suspend fun markAsRead(letterId: Long): Result<Unit>

    /**
     * Toggle favorite status
     */
    suspend fun toggleFavorite(letterId: Long, isFavorite: Boolean): Result<Unit>

    /**
     * Update shared timestamp
     */
    suspend fun markAsShared(letterId: Long): Result<Unit>

    // ==================== GENERATION ====================

    /**
     * Generate a new monthly letter for the specified month
     */
    suspend fun generateLetter(userId: String = "local", monthYear: YearMonth): Result<MonthlyLetter>

    /**
     * Generate letter for previous month if not already exists
     */
    suspend fun generateLetterForPreviousMonth(userId: String = "local"): Result<MonthlyLetter?>

    /**
     * Check if letter exists for month
     */
    suspend fun letterExistsForMonth(userId: String = "local", monthYear: YearMonth): Boolean

    /**
     * Check if there's enough data to generate a letter
     */
    suspend fun canGenerateLetter(userId: String = "local", monthYear: YearMonth): Boolean

    // ==================== STATISTICS ====================

    /**
     * Get total letter count
     */
    suspend fun getTotalLetterCount(userId: String = "local"): Int

    /**
     * Get count of letters with activity
     */
    suspend fun getActiveLetterCount(userId: String = "local"): Int

    /**
     * Get unread count
     */
    suspend fun getUnreadCount(userId: String = "local"): Int

    // ==================== CLEANUP ====================

    /**
     * Delete letter by ID
     */
    suspend fun deleteLetter(letterId: Long): Result<Unit>

    /**
     * Delete old letters (keep only recent N, excluding favorites)
     */
    suspend fun deleteOldLetters(userId: String = "local", keepCount: Int = 12): Result<Int>
}
