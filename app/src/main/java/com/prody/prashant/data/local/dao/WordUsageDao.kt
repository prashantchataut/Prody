package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.WordUsageEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing word usage data.
 * Provides queries for tracking vocabulary usage in journal entries.
 */
@Dao
interface WordUsageDao {

    /**
     * Insert a new word usage record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordUsage(wordUsage: WordUsageEntity): Long

    /**
     * Insert multiple word usage records
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordUsages(wordUsages: List<WordUsageEntity>)

    /**
     * Update a word usage record
     */
    @Update
    suspend fun updateWordUsage(wordUsage: WordUsageEntity)

    /**
     * Delete a word usage record
     */
    @Delete
    suspend fun deleteWordUsage(wordUsage: WordUsageEntity)

    /**
     * Get all usages for a specific word
     */
    @Query("SELECT * FROM word_usages WHERE wordId = :wordId AND userId = :userId ORDER BY detectedAt DESC")
    fun getUsagesByWord(wordId: Long, userId: String = "local"): Flow<List<WordUsageEntity>>

    /**
     * Get usages for a specific word (suspend version for one-time queries)
     */
    @Query("SELECT * FROM word_usages WHERE wordId = :wordId AND userId = :userId ORDER BY detectedAt DESC")
    suspend fun getUsagesByWordSync(wordId: Long, userId: String = "local"): List<WordUsageEntity>

    /**
     * Get all word usages in a specific journal entry
     */
    @Query("SELECT * FROM word_usages WHERE journalEntryId = :journalEntryId ORDER BY positionStart ASC")
    fun getUsagesByJournalEntry(journalEntryId: Long): Flow<List<WordUsageEntity>>

    /**
     * Get usages by journal entry (suspend version)
     */
    @Query("SELECT * FROM word_usages WHERE journalEntryId = :journalEntryId ORDER BY positionStart ASC")
    suspend fun getUsagesByJournalEntrySync(journalEntryId: Long): List<WordUsageEntity>

    /**
     * Count how many times a word has been used
     */
    @Query("SELECT COUNT(*) FROM word_usages WHERE wordId = :wordId AND userId = :userId")
    fun countUsagesForWord(wordId: Long, userId: String = "local"): Flow<Int>

    /**
     * Count how many times a word has been used (suspend version)
     */
    @Query("SELECT COUNT(*) FROM word_usages WHERE wordId = :wordId AND userId = :userId")
    suspend fun countUsagesForWordSync(wordId: Long, userId: String = "local"): Int

    /**
     * Count unique words used by the user
     */
    @Query("SELECT COUNT(DISTINCT wordId) FROM word_usages WHERE userId = :userId")
    fun countUniqueWordsUsed(userId: String = "local"): Flow<Int>

    /**
     * Count unique words used (suspend version)
     */
    @Query("SELECT COUNT(DISTINCT wordId) FROM word_usages WHERE userId = :userId")
    suspend fun countUniqueWordsUsedSync(userId: String = "local"): Int

    /**
     * Get IDs of all words that have been used at least once
     */
    @Query("SELECT DISTINCT wordId FROM word_usages WHERE userId = :userId")
    suspend fun getUsedWordIds(userId: String = "local"): List<Long>

    /**
     * Get recent word usages for celebration
     */
    @Query("""
        SELECT * FROM word_usages
        WHERE userId = :userId AND celebrated = 0
        ORDER BY detectedAt DESC
        LIMIT :limit
    """)
    suspend fun getUncelebratedUsages(userId: String = "local", limit: Int = 10): List<WordUsageEntity>

    /**
     * Mark a word usage as celebrated
     */
    @Query("""
        UPDATE word_usages
        SET celebrated = 1, celebratedAt = :celebratedAt
        WHERE id = :usageId
    """)
    suspend fun markAsCelebrated(usageId: Long, celebratedAt: Long = System.currentTimeMillis())

    /**
     * Mark word usages as celebrated in bulk
     */
    @Query("""
        UPDATE word_usages
        SET celebrated = 1, celebratedAt = :celebratedAt
        WHERE id IN (:usageIds)
    """)
    suspend fun markMultipleAsCelebrated(usageIds: List<Long>, celebratedAt: Long = System.currentTimeMillis())

    /**
     * Mark bonus points as claimed for a usage
     */
    @Query("""
        UPDATE word_usages
        SET pointsClaimed = 1, bonusPointsAwarded = :points
        WHERE id = :usageId
    """)
    suspend fun markPointsClaimed(usageId: Long, points: Int)

    /**
     * Get total bonus points earned from vocabulary usage
     */
    @Query("SELECT SUM(bonusPointsAwarded) FROM word_usages WHERE userId = :userId AND pointsClaimed = 1")
    fun getTotalBonusPoints(userId: String = "local"): Flow<Int?>

    /**
     * Get word usages within a date range (for analytics)
     */
    @Query("""
        SELECT * FROM word_usages
        WHERE userId = :userId AND detectedAt >= :startTime AND detectedAt <= :endTime
        ORDER BY detectedAt DESC
    """)
    suspend fun getUsagesInDateRange(
        userId: String = "local",
        startTime: Long,
        endTime: Long
    ): List<WordUsageEntity>

    /**
     * Get count of words used in a specific time period (e.g., this week)
     */
    @Query("""
        SELECT COUNT(DISTINCT wordId) FROM word_usages
        WHERE userId = :userId AND detectedAt >= :startTime
    """)
    suspend fun countUniqueWordsUsedSince(userId: String = "local", startTime: Long): Int

    /**
     * Check if a specific word was used in a specific journal entry
     */
    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM word_usages
            WHERE wordId = :wordId AND journalEntryId = :journalEntryId
        )
    """)
    suspend fun isWordUsedInEntry(wordId: Long, journalEntryId: Long): Boolean

    /**
     * Delete all usages for a specific journal entry (when entry is deleted)
     */
    @Query("DELETE FROM word_usages WHERE journalEntryId = :journalEntryId")
    suspend fun deleteUsagesByJournalEntry(journalEntryId: Long)

    /**
     * Get the most recently used words
     */
    @Query("""
        SELECT DISTINCT wordId FROM word_usages
        WHERE userId = :userId
        ORDER BY detectedAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentlyUsedWordIds(userId: String = "local", limit: Int = 10): List<Long>

    /**
     * Get words that have been learned but never used
     * This requires joining with vocabulary_learning table
     */
    @Query("""
        SELECT vl.wordId FROM vocabulary_learning vl
        WHERE vl.userId = :userId
        AND vl.boxLevel >= 2
        AND NOT EXISTS (
            SELECT 1 FROM word_usages wu
            WHERE wu.wordId = vl.wordId AND wu.userId = :userId
        )
        LIMIT :limit
    """)
    suspend fun getLearnedButUnusedWordIds(userId: String = "local", limit: Int = 20): List<Long>

    /**
     * Get usage statistics for a word
     */
    @Query("""
        SELECT
            COUNT(*) as totalUsages,
            MIN(detectedAt) as firstUsedAt,
            MAX(detectedAt) as lastUsedAt
        FROM word_usages
        WHERE wordId = :wordId AND userId = :userId
    """)
    suspend fun getWordUsageStats(wordId: Long, userId: String = "local"): WordUsageStats?

    /**
     * Delete old word usages (cleanup)
     */
    @Query("DELETE FROM word_usages WHERE detectedAt < :beforeTime")
    suspend fun deleteOldUsages(beforeTime: Long)
}

/**
 * Statistics data class for word usage
 */
data class WordUsageStats(
    val totalUsages: Int,
    val firstUsedAt: Long?,
    val lastUsedAt: Long?
)
