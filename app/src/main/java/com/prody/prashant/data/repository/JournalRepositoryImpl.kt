package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.MoodCount
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of JournalRepository using Room database.
 */
@Singleton
class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao
) : JournalRepository {

    override fun getAllEntries(): Flow<List<JournalEntryEntity>> {
        return journalDao.getAllEntries()
    }

    override suspend fun getEntryById(id: Long): Result<JournalEntryEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to load journal entry") {
            journalDao.getEntryById(id) ?: throw NoSuchElementException("Entry not found")
        }
    }

    override fun observeEntryById(id: Long): Flow<JournalEntryEntity?> {
        return journalDao.observeEntryById(id)
    }

    override fun getBookmarkedEntries(): Flow<List<JournalEntryEntity>> {
        return journalDao.getBookmarkedEntries()
    }

    override fun getEntriesByMood(mood: Mood): Flow<List<JournalEntryEntity>> {
        return journalDao.getEntriesByMood(mood.name)
    }

    override fun getEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<JournalEntryEntity>> {
        return journalDao.getEntriesByDateRange(startDate, endDate)
    }

    override fun searchEntries(query: String): Flow<List<JournalEntryEntity>> {
        return journalDao.searchEntries(query)
    }

    override fun getEntryCount(): Flow<Int> {
        return journalDao.getEntryCount()
    }

    override suspend fun getTodayEntryCount(): Int {
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return journalDao.getTodayEntryCount(startOfDay)
    }

    override fun getTotalWordCount(): Flow<Int?> {
        return journalDao.getTotalWordCount()
    }

    override fun getMoodDistribution(): Flow<List<MoodCount>> {
        return journalDao.getMoodDistribution()
    }

    override fun getRecentEntries(limit: Int): Flow<List<JournalEntryEntity>> {
        return journalDao.getRecentEntries(limit)
    }

    override fun getAllMoods(): Flow<List<String>> {
        return journalDao.getAllMoods()
    }

    override suspend fun saveEntry(entry: JournalEntryEntity): Result<Long> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save journal entry") {
            journalDao.insertEntry(entry)
        }
    }

    override suspend fun updateEntry(entry: JournalEntryEntity): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update journal entry") {
            journalDao.updateEntry(entry)
        }
    }

    override suspend fun deleteEntry(entry: JournalEntryEntity): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete journal entry") {
            journalDao.deleteEntry(entry)
        }
    }

    override suspend fun deleteEntryById(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete journal entry") {
            journalDao.deleteEntryById(id)
        }
    }

    override suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update bookmark status") {
            journalDao.updateBookmarkStatus(id, isBookmarked)
        }
    }

    override suspend fun updateBuddhaResponse(id: Long, response: String): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to save Buddha's response") {
            journalDao.updateBuddhaResponse(id, response)
        }
    }

    override suspend fun getEntriesForWeek(weekStartTimestamp: Long): List<JournalEntryEntity> {
        val weekEndTimestamp = weekStartTimestamp + (7 * 24 * 60 * 60 * 1000L) // 7 days in milliseconds
        return journalDao.getAllEntriesSync().filter { entry ->
            entry.createdAt in weekStartTimestamp..weekEndTimestamp
        }
    }

    override fun getDominantMood(entries: List<JournalEntryEntity>): Mood? {
        if (entries.isEmpty()) return null

        val moodCounts = entries.groupingBy { it.mood }.eachCount()
        val dominantMoodName = moodCounts.maxByOrNull { it.value }?.key

        return dominantMoodName?.let { Mood.fromString(it) }
    }
}
