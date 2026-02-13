package com.prody.prashant.data.local.database

import androidx.room.migration.Migration

object DatabaseMigrations {
    val all: Array<Migration> = arrayOf(
        ProdyDatabase.MIGRATION_4_5,
        ProdyDatabase.MIGRATION_5_6,
        ProdyDatabase.MIGRATION_6_7,
        ProdyDatabase.MIGRATION_7_8,
        ProdyDatabase.MIGRATION_8_9,
        ProdyDatabase.MIGRATION_9_10,
        ProdyDatabase.MIGRATION_10_11,
        ProdyDatabase.MIGRATION_11_12,
        ProdyDatabase.MIGRATION_12_13,
        ProdyDatabase.MIGRATION_13_14,
        ProdyDatabase.MIGRATION_14_15,
        ProdyDatabase.MIGRATION_15_16,
        ProdyDatabase.MIGRATION_16_17,
        ProdyDatabase.MIGRATION_17_18,
        ProdyDatabase.MIGRATION_18_19,
        ProdyDatabase.MIGRATION_19_20
    )
}
