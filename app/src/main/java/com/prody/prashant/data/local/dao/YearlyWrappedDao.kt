package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.YearlyWrappedEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Yearly Wrapped operations.
 *
 * Provides database access for yearly wrapped summaries, enabling
 * users to revisit past years' celebrations.
 */
@Dao
interface YearlyWrappedDao {

    // ==================== RETRIEVAL ====================

    @Query("SELECT * FROM yearly_wrapped WHERE userId = :userId ORDER BY year DESC")
    fun getAllWrapped(userId: String = "local"): Flow<List<YearlyWrappedEntity>>

    @Query("SELECT * FROM yearly_wrapped WHERE id = :id")
    suspend fun getWrappedById(id: Long): YearlyWrappedEntity?

    @Query("SELECT * FROM yearly_wrapped WHERE id = :id")
    fun observeWrappedById(id: Long): Flow<YearlyWrappedEntity?>

    @Query("SELECT * FROM yearly_wrapped WHERE userId = :userId AND year = :year LIMIT 1")
    suspend fun getWrappedByYear(userId: String = "local", year: Int): YearlyWrappedEntity?

    @Query("SELECT * FROM yearly_wrapped WHERE userId = :userId AND year = :year LIMIT 1")
    fun observeWrappedByYear(userId: String = "local", year: Int): Flow<YearlyWrappedEntity?>

    @Query("SELECT * FROM yearly_wrapped WHERE userId = :userId ORDER BY year DESC LIMIT 1")
    suspend fun getLatestWrapped(userId: String = "local"): YearlyWrappedEntity?

    @Query("SELECT * FROM yearly_wrapped WHERE userId = :userId ORDER BY year DESC LIMIT 1")
    fun observeLatestWrapped(userId: String = "local"): Flow<YearlyWrappedEntity?>

    @Query("SELECT * FROM yearly_wrapped WHERE userId = :userId AND isViewed = 0 ORDER BY year DESC")
    fun getUnviewedWrapped(userId: String = "local"): Flow<List<YearlyWrappedEntity>>

    @Query("SELECT * FROM yearly_wrapped WHERE userId = :userId AND isFavorite = 1 ORDER BY year DESC")
    fun getFavoriteWrapped(userId: String = "local"): Flow<List<YearlyWrappedEntity>>

    // ==================== CREATE/UPDATE ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrapped(wrapped: YearlyWrappedEntity): Long

    @Update
    suspend fun updateWrapped(wrapped: YearlyWrappedEntity)

    @Query("""
        UPDATE yearly_wrapped
        SET isViewed = 1,
            viewedAt = :viewedAt,
            viewCompletionPercent = :completionPercent,
            slidesViewed = :slidesViewed
        WHERE id = :id
    """)
    suspend fun markAsViewed(
        id: Long,
        viewedAt: Long = System.currentTimeMillis(),
        completionPercent: Int = 100,
        slidesViewed: String = "[]"
    )

    @Query("UPDATE yearly_wrapped SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("""
        UPDATE yearly_wrapped
        SET isShared = 1,
            sharedAt = :sharedAt
        WHERE id = :id
    """)
    suspend fun markAsShared(id: Long, sharedAt: Long = System.currentTimeMillis())

    @Query("""
        UPDATE yearly_wrapped
        SET viewCompletionPercent = :percent,
            slidesViewed = :slidesViewed
        WHERE id = :id
    """)
    suspend fun updateViewProgress(id: Long, percent: Int, slidesViewed: String)

    // ==================== DELETE ====================

    @Delete
    suspend fun deleteWrapped(wrapped: YearlyWrappedEntity)

    @Query("DELETE FROM yearly_wrapped WHERE id = :id")
    suspend fun deleteWrappedById(id: Long)

    @Query("UPDATE yearly_wrapped SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteWrapped(id: Long)

    @Query("DELETE FROM yearly_wrapped WHERE isDeleted = 1")
    suspend fun purgeSoftDeleted(): Int

    // ==================== STATISTICS ====================

    @Query("SELECT COUNT(*) FROM yearly_wrapped WHERE userId = :userId")
    fun getWrappedCount(userId: String = "local"): Flow<Int>

    @Query("SELECT COUNT(*) FROM yearly_wrapped WHERE userId = :userId AND isViewed = 0")
    suspend fun getUnviewedCount(userId: String = "local"): Int

    @Query("SELECT COUNT(*) FROM yearly_wrapped WHERE userId = :userId AND year = :year")
    suspend fun getWrappedCountForYear(userId: String = "local", year: Int): Int

    suspend fun wrappedExistsForYear(userId: String = "local", year: Int): Boolean {
        return getWrappedCountForYear(userId, year) > 0
    }

    @Query("SELECT year FROM yearly_wrapped WHERE userId = :userId ORDER BY year DESC")
    suspend fun getAvailableYears(userId: String = "local"): List<Int>

    @Query("SELECT year FROM yearly_wrapped WHERE userId = :userId ORDER BY year DESC")
    fun observeAvailableYears(userId: String = "local"): Flow<List<Int>>

    // ==================== SYNC ====================

    @Query("SELECT * FROM yearly_wrapped WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncWrapped(): List<YearlyWrappedEntity>

    @Query("""
        UPDATE yearly_wrapped
        SET syncStatus = :status,
            lastSyncedAt = :syncTime
        WHERE id = :id
    """)
    suspend fun updateSyncStatus(
        id: Long,
        status: String,
        syncTime: Long = System.currentTimeMillis()
    )
}
