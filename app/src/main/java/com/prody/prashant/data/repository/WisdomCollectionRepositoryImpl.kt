package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.SavedWisdomDao
import com.prody.prashant.data.local.entity.SavedWisdomEntity
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.repository.WisdomCollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WisdomCollectionRepository using Room database.
 */
@Singleton
class WisdomCollectionRepositoryImpl @Inject constructor(
    private val savedWisdomDao: SavedWisdomDao
) : WisdomCollectionRepository {

    // ==================== RETRIEVAL ====================

    override fun getAllSavedWisdom(userId: String): Flow<List<SavedWisdomEntity>> {
        return savedWisdomDao.getAllSavedWisdom(userId)
    }

    override fun getSavedWisdomByType(userId: String, type: String): Flow<List<SavedWisdomEntity>> {
        return savedWisdomDao.getSavedWisdomByType(userId, type)
    }

    override suspend fun getSavedWisdomById(id: Long): Result<SavedWisdomEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to load saved wisdom") {
            savedWisdomDao.getSavedWisdomById(id)
                ?: throw NoSuchElementException("Saved wisdom not found")
        }
    }

    override fun searchSavedWisdom(userId: String, query: String): Flow<List<SavedWisdomEntity>> {
        return savedWisdomDao.searchSavedWisdom(userId, query)
    }

    override fun getSavedWisdomByTheme(userId: String, theme: String): Flow<List<SavedWisdomEntity>> {
        return savedWisdomDao.getSavedWisdomByTheme(userId, theme)
    }

    // ==================== SAVE/UPDATE ====================

    override suspend fun saveWisdom(wisdom: SavedWisdomEntity): Result<Long> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save wisdom") {
            savedWisdomDao.insertSavedWisdom(wisdom)
        }
    }

    override suspend fun updateSavedWisdom(wisdom: SavedWisdomEntity): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update saved wisdom") {
            savedWisdomDao.updateSavedWisdom(wisdom)
        }
    }

    override suspend fun addUserNote(id: Long, note: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to add note") {
            savedWisdomDao.updateUserNote(id, note)
        }
    }

    override suspend fun updateTags(id: Long, tags: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update tags") {
            savedWisdomDao.updateTags(id, tags)
        }
    }

    // ==================== DELETION ====================

    override suspend fun removeWisdom(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to remove wisdom") {
            savedWisdomDao.softDeleteSavedWisdom(id)
        }
    }

    override suspend fun deleteWisdom(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete wisdom") {
            savedWisdomDao.deleteSavedWisdomById(id)
        }
    }

    // ==================== SMART RESURFACING ====================

    override suspend fun getWisdomToResurface(userId: String, limit: Int): Result<List<SavedWisdomEntity>> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get wisdom to resurface") {
            savedWisdomDao.getWisdomToResurface(userId, limit = limit)
        }
    }

    override suspend fun recordWisdomShown(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to record wisdom shown") {
            savedWisdomDao.recordWisdomShown(id)
        }
    }

    override suspend fun recordWisdomViewed(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to record wisdom viewed") {
            savedWisdomDao.recordWisdomViewed(id)
        }
    }

    // ==================== CHECKING ====================

    override suspend fun isWisdomSaved(sourceId: Long, type: String): Boolean {
        return savedWisdomDao.isWisdomSaved(sourceId, type)
    }

    override suspend fun getSavedWisdomBySource(sourceId: Long, type: String): SavedWisdomEntity? {
        return savedWisdomDao.getWisdomBySource(sourceId, type)
    }

    // ==================== STATISTICS ====================

    override fun getSavedWisdomCount(userId: String): Flow<Int> {
        return savedWisdomDao.getSavedWisdomCount(userId)
    }

    override suspend fun getCountByType(userId: String): Map<String, Int> {
        val typeCounts = savedWisdomDao.getTypeCountForUser(userId)
        return typeCounts.associate { it.type to it.count }
    }
}
