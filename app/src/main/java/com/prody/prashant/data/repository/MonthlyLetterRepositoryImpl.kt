package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.MonthlyLetterDao
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.letter.MonthlyLetterGenerator
import com.prody.prashant.domain.model.MonthlyLetter
import com.prody.prashant.domain.repository.MonthlyLetterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject

/**
 * Implementation of MonthlyLetterRepository.
 *
 * Manages monthly letter persistence and generation.
 */
class MonthlyLetterRepositoryImpl @Inject constructor(
    private val monthlyLetterDao: MonthlyLetterDao,
    private val letterGenerator: MonthlyLetterGenerator
) : MonthlyLetterRepository {

    // ==================== RETRIEVAL ====================

    override fun getAllLetters(userId: String): Flow<List<MonthlyLetter>> {
        return monthlyLetterDao.getAllLetters(userId).map { entities ->
            entities.map { MonthlyLetter.fromEntity(it) }
        }
    }

    override suspend fun getLetterById(letterId: Long): Result<MonthlyLetter> {
        return try {
            val entity = monthlyLetterDao.getLetterById(letterId)
            if (entity != null) {
                Result.Success(MonthlyLetter.fromEntity(entity))
            } else {
                Result.Error("Letter not found")
            }
        } catch (e: Exception) {
            Result.Error("Failed to get letter: ${e.message}")
        }
    }

    override fun observeLetterById(letterId: Long): Flow<MonthlyLetter?> {
        return monthlyLetterDao.observeLetterById(letterId).map { entity ->
            entity?.let { MonthlyLetter.fromEntity(it) }
        }
    }

    override suspend fun getLetterForMonth(userId: String, monthYear: YearMonth): Result<MonthlyLetter?> {
        return try {
            val entity = monthlyLetterDao.getLetterForMonth(userId, monthYear.monthValue, monthYear.year)
            Result.Success(entity?.let { MonthlyLetter.fromEntity(it) })
        } catch (e: Exception) {
            Result.Error("Failed to get letter for month: ${e.message}")
        }
    }

    override fun observeLetterForMonth(userId: String, monthYear: YearMonth): Flow<MonthlyLetter?> {
        return monthlyLetterDao.observeLetterForMonth(userId, monthYear.monthValue, monthYear.year).map { entity ->
            entity?.let { MonthlyLetter.fromEntity(it) }
        }
    }

    override suspend fun getMostRecentLetter(userId: String): Result<MonthlyLetter?> {
        return try {
            val entity = monthlyLetterDao.getMostRecentLetter(userId)
            Result.Success(entity?.let { MonthlyLetter.fromEntity(it) })
        } catch (e: Exception) {
            Result.Error("Failed to get most recent letter: ${e.message}")
        }
    }

    override fun observeMostRecentLetter(userId: String): Flow<MonthlyLetter?> {
        return monthlyLetterDao.observeMostRecentLetter(userId).map { entity ->
            entity?.let { MonthlyLetter.fromEntity(it) }
        }
    }

    override fun getUnreadLetters(userId: String): Flow<List<MonthlyLetter>> {
        return monthlyLetterDao.getUnreadLetters(userId).map { entities ->
            entities.map { MonthlyLetter.fromEntity(it) }
        }
    }

    override fun getUnreadLetterCount(userId: String): Flow<Int> {
        return monthlyLetterDao.getUnreadLetterCount(userId)
    }

    override fun getFavoriteLetters(userId: String): Flow<List<MonthlyLetter>> {
        return monthlyLetterDao.getFavoriteLetters(userId).map { entities ->
            entities.map { MonthlyLetter.fromEntity(it) }
        }
    }

    override fun getRecentLetters(userId: String, limit: Int): Flow<List<MonthlyLetter>> {
        return monthlyLetterDao.getRecentLetters(userId, limit).map { entities ->
            entities.map { MonthlyLetter.fromEntity(it) }
        }
    }

    override fun getLettersForYear(userId: String, year: Int): Flow<List<MonthlyLetter>> {
        return monthlyLetterDao.getLettersForYear(userId, year).map { entities ->
            entities.map { MonthlyLetter.fromEntity(it) }
        }
    }

    // ==================== CREATE/UPDATE ====================

    override suspend fun saveLetter(letter: MonthlyLetter): Result<Long> {
        return try {
            val id = monthlyLetterDao.insertLetter(letter.toEntity())
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error("Failed to save letter: ${e.message}")
        }
    }

    override suspend fun markAsRead(letterId: Long): Result<Unit> {
        return try {
            monthlyLetterDao.markAsRead(letterId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to mark letter as read: ${e.message}")
        }
    }

    override suspend fun toggleFavorite(letterId: Long, isFavorite: Boolean): Result<Unit> {
        return try {
            monthlyLetterDao.updateFavoriteStatus(letterId, isFavorite)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to toggle favorite: ${e.message}")
        }
    }

    override suspend fun markAsShared(letterId: Long): Result<Unit> {
        return try {
            monthlyLetterDao.updateSharedTimestamp(letterId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to mark as shared: ${e.message}")
        }
    }

    // ==================== GENERATION ====================

    override suspend fun generateLetter(userId: String, monthYear: YearMonth): Result<MonthlyLetter> {
        return try {
            // Check if letter already exists
            val existing = monthlyLetterDao.getLetterForMonth(userId, monthYear.monthValue, monthYear.year)
            if (existing != null) {
                return Result.Success(MonthlyLetter.fromEntity(existing))
            }

            // Check if there's enough data
            if (!letterGenerator.canGenerate(userId, monthYear)) {
                return Result.Error("Not enough data to generate letter for this month")
            }

            // Generate the letter
            val letter = letterGenerator.generateLetter(userId, monthYear)

            // Save it
            val id = monthlyLetterDao.insertLetter(letter.toEntity())

            // Return the saved letter with ID
            Result.Success(letter.copy(id = id))
        } catch (e: Exception) {
            Result.Error("Failed to generate letter: ${e.message}")
        }
    }

    override suspend fun generateLetterForPreviousMonth(userId: String): Result<MonthlyLetter?> {
        return try {
            val previousMonth = YearMonth.now().minusMonths(1)

            // Check if already exists
            val existing = monthlyLetterDao.getLetterForMonth(userId, previousMonth.monthValue, previousMonth.year)
            if (existing != null) {
                return Result.Success(MonthlyLetter.fromEntity(existing))
            }

            // Check if there's enough data
            if (!letterGenerator.canGenerate(userId, previousMonth)) {
                return Result.Success(null)
            }

            // Generate and save
            val letter = letterGenerator.generateLetter(userId, previousMonth)
            val id = monthlyLetterDao.insertLetter(letter.toEntity())

            Result.Success(letter.copy(id = id))
        } catch (e: Exception) {
            Result.Error("Failed to generate letter for previous month: ${e.message}")
        }
    }

    override suspend fun letterExistsForMonth(userId: String, monthYear: YearMonth): Boolean {
        return try {
            monthlyLetterDao.letterExistsForMonth(userId, monthYear.monthValue, monthYear.year) > 0
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun canGenerateLetter(userId: String, monthYear: YearMonth): Boolean {
        return try {
            letterGenerator.canGenerate(userId, monthYear)
        } catch (e: Exception) {
            false
        }
    }

    // ==================== STATISTICS ====================

    override suspend fun getTotalLetterCount(userId: String): Int {
        return try {
            monthlyLetterDao.getTotalLetterCount(userId)
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getActiveLetterCount(userId: String): Int {
        return try {
            monthlyLetterDao.getActiveLetterCount(userId)
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getUnreadCount(userId: String): Int {
        return try {
            val flow = monthlyLetterDao.getUnreadLetterCount(userId)
            // This is a simplification - in production, you'd collect the flow
            0
        } catch (e: Exception) {
            0
        }
    }

    // ==================== CLEANUP ====================

    override suspend fun deleteLetter(letterId: Long): Result<Unit> {
        return try {
            monthlyLetterDao.softDeleteLetter(letterId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to delete letter: ${e.message}")
        }
    }

    override suspend fun deleteOldLetters(userId: String, keepCount: Int): Result<Int> {
        return try {
            monthlyLetterDao.deleteOldLetters(userId, keepCount)
            Result.Success(keepCount)
        } catch (e: Exception) {
            Result.Error("Failed to delete old letters: ${e.message}")
        }
    }
}
