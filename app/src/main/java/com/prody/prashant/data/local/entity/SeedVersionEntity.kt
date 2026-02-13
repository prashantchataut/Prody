package com.prody.prashant.data.local.entity

import androidx.room.Entity

@Entity(tableName = "seed_versions", primaryKeys = ["domain", "version"])
data class SeedVersionEntity(
    val domain: String,
    val version: Int,
    val checksum: String,
    val appliedAt: Long = System.currentTimeMillis()
)
