package com.kairos.app.data.local.database

import androidx.room.migration.Migration

object DatabaseMigrations {
    val all: Array<Migration> = arrayOf(
        KairosDatabase.MIGRATION_4_5,
        KairosDatabase.MIGRATION_5_6,
        KairosDatabase.MIGRATION_6_7,
        KairosDatabase.MIGRATION_7_8,
        KairosDatabase.MIGRATION_8_9,
        KairosDatabase.MIGRATION_9_10,
        KairosDatabase.MIGRATION_10_11,
        KairosDatabase.MIGRATION_11_12,
        KairosDatabase.MIGRATION_12_13,
        KairosDatabase.MIGRATION_13_14,
        KairosDatabase.MIGRATION_14_15,
        KairosDatabase.MIGRATION_15_16,
        KairosDatabase.MIGRATION_16_17,
        KairosDatabase.MIGRATION_17_18,
        KairosDatabase.MIGRATION_18_19,
        KairosDatabase.MIGRATION_19_20,
        KairosDatabase.MIGRATION_20_21,
        KairosDatabase.MIGRATION_21_22,
        KairosDatabase.MIGRATION_22_23,
        KairosDatabase.MIGRATION_23_24
    )
}
