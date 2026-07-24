package com.kairos.app.data.repository

import com.kairos.app.data.local.dao.MicroEntryDao
import com.kairos.app.data.local.entity.MicroEntryEntity
import com.kairos.app.domain.common.ErrorType
import com.kairos.app.domain.common.Result
import com.kairos.app.domain.common.runSuspendCatching
import com.kairos.app.domain.repository.MicroEntryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MicroEntryRepository using Room database.
 */
@Singleton
class MicroEntryRepositoryImpl @Inject constructor(
    private val microEntryDao: MicroEntryDao
) : MicroEntryRepository {

    // ==================== RETRIEVAL ====================

    override fun getAllMicroEntries(userId: String): Flow<List<MicroEntryEntity>> {
        return microEntryDao.getAllMicroEntries(userId)
    }

    override fun getMicroEntriesByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<MicroEntryEntity>> {
        return microEntryDao.getMicroEntriesByDateRange(userId, startDate, endDate)
    }

    override fun getTodayMicroEntries(userId: String): Flow<List<MicroEntryEntity>> {
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val endOfDay = LocalDate.now()
            .atTime(LocalTime.MAX)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return microEntryDao.getMicroEntriesByDateRange(userId, startOfDay, endOfDay)
    }

    override suspend fun getMicroEntryById(id: Long): Result<MicroEntryEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to load micro entry") {
            microEntryDao.getMicroEntryById(id)
                ?: throw NoSuchElementException("Micro entry not found")
        }
    }

    override fun observeMicroEntryById(id: Long): Flow<MicroEntryEntity?> {
        return microEntryDao.observeMicroEntryById(id)
    }

    override fun getUnexpandedMicroEntries(userId: String): Flow<List<MicroEntryEntity>> {
        return microEntryDao.getUnexpandedMicroEntries(userId)
    }

    override fun getMicroEntriesByContext(userId: String, context: String): Flow<List<MicroEntryEntity>> {
        return microEntryDao.getMicroEntriesByContext(userId, context)
    }

    override fun getMicroEntriesByMood(userId: String, mood: String): Flow<List<MicroEntryEntity>> {
        return microEntryDao.getMicroEntriesByMood(userId, mood)
    }

    override fun searchMicroEntries(userId: String, query: String): Flow<List<MicroEntryEntity>> {
        return microEntryDao.searchMicroEntries(userId, query)
    }

    // ==================== CREATE/UPDATE ====================

    override suspend fun createMicroEntry(entry: MicroEntryEntity): Result<Long> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to create micro entry") {
            microEntryDao.insertMicroEntry(entry)
        }
    }

    override suspend fun updateMicroEntry(entry: MicroEntryEntity): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update micro entry") {
            microEntryDao.updateMicroEntry(entry)
        }
    }

    override suspend fun markAsExpanded(microEntryId: Long, journalEntryId: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to mark as expanded") {
            microEntryDao.markAsExpanded(microEntryId, journalEntryId, System.currentTimeMillis())
        }
    }

    // ==================== DELETION ====================

    override suspend fun softDeleteMicroEntry(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete micro entry") {
            microEntryDao.softDeleteMicroEntry(id)
        }
    }

    override suspend fun deleteMicroEntry(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete micro entry") {
            microEntryDao.deleteMicroEntryById(id)
        }
    }

    // ==================== STATISTICS ====================

    override fun getMicroEntryCount(userId: String): Flow<Int> {
        return microEntryDao.getMicroEntryCount(userId)
    }

    override suspend fun getTodayMicroEntryCount(userId: String): Int {
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return microEntryDao.getTodayMicroEntryCount(userId, startOfDay)
    }

    override suspend fun getThisWeekMicroEntryCount(userId: String): Int {
        val startOfWeek = LocalDate.now()
            .minusDays(LocalDate.now().dayOfWeek.value.toLong() - 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return microEntryDao.getWeekMicroEntryCount(userId, startOfWeek)
    }

    override suspend fun getUnexpandedCount(userId: String): Int {
        return microEntryDao.getUnexpandedCount(userId)
    }

    override suspend fun getMoodDistribution(userId: String): Map<String, Int> {
        val moodCounts = microEntryDao.getMoodDistribution(userId)
        return moodCounts.associate { (it.mood ?: "UNKNOWN") to it.count }
    }

    // ==================== RECENT ====================

    override fun getRecentMicroEntries(userId: String, limit: Int): Flow<List<MicroEntryEntity>> {
        return microEntryDao.getRecentMicroEntries(userId, limit)
    }
}
