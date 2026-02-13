package com.prody.prashant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prody.prashant.data.local.entity.SeedVersionEntity

@Dao
interface SeedVersionDao {

    @Query("SELECT * FROM seed_versions WHERE domain = :domain AND version = :version LIMIT 1")
    suspend fun getSeedVersion(domain: String, version: Int): SeedVersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSeedVersion(seedVersion: SeedVersionEntity)
}
