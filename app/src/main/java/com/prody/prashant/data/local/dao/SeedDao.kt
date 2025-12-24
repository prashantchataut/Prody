package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.SeedEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Seed -> Bloom mechanic.
 */
@Dao
interface SeedDao {

    // ==================== QUERIES ====================

    @Query("SELECT * FROM daily_seeds WHERE date = :date AND userId = :userId LIMIT 1")
    suspend fun getSeedForDate(date: Long, userId: String = "local"): SeedEntity?

    @Query("SELECT * FROM daily_seeds WHERE date = :date AND userId = :userId LIMIT 1")
    fun observeSeedForDate(date: Long, userId: String = "local"): Flow<SeedEntity?>

    @Query("SELECT * FROM daily_seeds WHERE userId = :userId ORDER BY date DESC")
    fun getAllSeeds(userId: String = "local"): Flow<List<SeedEntity>>

    @Query("SELECT * FROM daily_seeds WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    fun getRecentSeeds(limit: Int, userId: String = "local"): Flow<List<SeedEntity>>

    @Query("SELECT * FROM daily_seeds WHERE userId = :userId AND hasBloomedToday = 1 ORDER BY date DESC")
    fun getBloomedSeeds(userId: String = "local"): Flow<List<SeedEntity>>

    @Query("SELECT COUNT(*) FROM daily_seeds WHERE userId = :userId AND hasBloomedToday = 1")
    suspend fun getTotalBloomedCount(userId: String = "local"): Int

    @Query("SELECT COUNT(*) FROM daily_seeds WHERE userId = :userId")
    suspend fun getTotalSeedCount(userId: String = "local"): Int

    // ==================== INSERTS ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeed(seed: SeedEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeedIfNotExists(seed: SeedEntity): Long

    // ==================== UPDATES ====================

    @Update
    suspend fun updateSeed(seed: SeedEntity)

    @Query("""
        UPDATE daily_seeds
        SET hasBloomedToday = 1,
            bloomedAt = :bloomedAt,
            bloomedIn = :bloomedIn,
            bloomedEntryId = :entryId
        WHERE id = :seedId
    """)
    suspend fun markSeedAsBloomed(
        seedId: Long,
        bloomedAt: Long = System.currentTimeMillis(),
        bloomedIn: String,
        entryId: Long? = null
    )

    @Query("UPDATE daily_seeds SET rewardClaimed = 1 WHERE id = :seedId")
    suspend fun markRewardClaimed(seedId: Long)

    // ==================== DELETE ====================

    @Query("DELETE FROM daily_seeds WHERE date < :cutoffDate")
    suspend fun deleteOldSeeds(cutoffDate: Long)

    @Query("DELETE FROM daily_seeds WHERE userId = :userId")
    suspend fun clearAllSeeds(userId: String = "local")
}
