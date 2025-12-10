package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    fun observeEntryById(id: Long): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM journal_entries WHERE isBookmarked = 1 ORDER BY createdAt DESC")
    fun getBookmarkedEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE mood = :mood ORDER BY createdAt DESC")
    fun getEntriesByMood(mood: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchEntries(query: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT COUNT(*) FROM journal_entries")
    fun getEntryCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM journal_entries WHERE createdAt >= :startOfDay")
    suspend fun getTodayEntryCount(startOfDay: Long): Int

    @Query("SELECT SUM(wordCount) FROM journal_entries")
    fun getTotalWordCount(): Flow<Int?>

    @Query("SELECT mood, COUNT(*) as count FROM journal_entries GROUP BY mood ORDER BY count DESC")
    fun getMoodDistribution(): Flow<List<MoodCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)

    @Query("UPDATE journal_entries SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean)

    @Query("UPDATE journal_entries SET buddhaResponse = :response, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateBuddhaResponse(id: Long, response: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentEntries(limit: Int): Flow<List<JournalEntryEntity>>

    @Query("SELECT DISTINCT mood FROM journal_entries")
    fun getAllMoods(): Flow<List<String>>
}

data class MoodCount(
    val mood: String,
    val count: Int
)
