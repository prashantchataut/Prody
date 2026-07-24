package com.prody.prashant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.prody.prashant.data.local.entity.ContentInteractionEntity
import com.prody.prashant.data.local.entity.DailyContentSelectionEntity

@Dao
interface DailyContentDao {

    @Query(
        """SELECT * FROM daily_content_selections
           WHERE userId = :userId AND localDate = :localDate"""
    )
    suspend fun getSelectionsForDate(
        userId: String,
        localDate: String
    ): List<DailyContentSelectionEntity>

    @Query(
        """SELECT * FROM daily_content_selections
           WHERE userId = :userId AND localDate = :localDate AND contentType = :contentType
           LIMIT 1"""
    )
    suspend fun getSelection(
        userId: String,
        localDate: String,
        contentType: String
    ): DailyContentSelectionEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSelection(selection: DailyContentSelectionEntity): Long

    @Query(
        """DELETE FROM daily_content_selections
           WHERE userId = :userId AND localDate = :localDate AND contentType = :contentType"""
    )
    suspend fun deleteSelection(userId: String, localDate: String, contentType: String): Int

    @Transaction
    suspend fun insertSelectionsIfAbsent(selections: List<DailyContentSelectionEntity>) {
        selections.forEach { insertSelection(it) }
    }

    @Query(
        """UPDATE daily_content_selections
           SET impressedAt = COALESCE(impressedAt, :timestamp)
           WHERE userId = :userId AND localDate = :localDate
             AND contentType = :contentType AND contentId = :contentId
             AND impressedAt IS NULL"""
    )
    suspend fun markImpressed(
        userId: String,
        localDate: String,
        contentType: String,
        contentId: Long,
        timestamp: Long
    ): Int

    @Query(
        """UPDATE daily_content_selections
           SET completedAt = COALESCE(completedAt, :timestamp)
           WHERE userId = :userId AND localDate = :localDate
             AND contentType = :contentType AND contentId = :contentId"""
    )
    suspend fun markCompleted(
        userId: String,
        localDate: String,
        contentType: String,
        contentId: Long,
        timestamp: Long
    ): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInteraction(interaction: ContentInteractionEntity): Long

    @Query(
        """SELECT * FROM daily_content_selections
           WHERE userId = :userId AND selectedAt >= :since
           ORDER BY selectedAt DESC"""
    )
    suspend fun getRecentSelections(
        userId: String,
        since: Long
    ): List<DailyContentSelectionEntity>

    @Query(
        """SELECT * FROM content_interactions
           WHERE userId = :userId AND createdAt >= :since
           ORDER BY createdAt DESC"""
    )
    suspend fun getRecentInteractions(
        userId: String,
        since: Long
    ): List<ContentInteractionEntity>

    @Query(
        """SELECT COUNT(*) > 0 FROM daily_content_selections
           WHERE userId = :userId AND localDate = :localDate
             AND contentType = :contentType AND completedAt IS NOT NULL"""
    )
    suspend fun hasCompletedContent(
        userId: String,
        localDate: String,
        contentType: String
    ): Boolean

    @Query("DELETE FROM daily_content_selections WHERE selectedAt < :before")
    suspend fun deleteSelectionsOlderThan(before: Long): Int
}
