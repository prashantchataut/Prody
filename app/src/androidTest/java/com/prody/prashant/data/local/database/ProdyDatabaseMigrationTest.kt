package com.prody.prashant.data.local.database

import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProdyDatabaseMigrationTest {

    private val testDbName = "migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation(),
        ProdyDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate4To20_isSuccessful() {
        helper.createDatabase(testDbName, 4).close()

        helper.runMigrationsAndValidate(
            testDbName,
            20,
            true,
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

    @Test
    fun migrate17To20_isSuccessful() {
        helper.createDatabase(testDbName, 17).close()

        helper.runMigrationsAndValidate(
            testDbName,
            20,
            true,
            ProdyDatabase.MIGRATION_17_18,
            ProdyDatabase.MIGRATION_18_19,
            ProdyDatabase.MIGRATION_19_20
        )
    }

    @Test
    fun startupGuard_marksLegacyVersionsForRecovery() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        context.deleteDatabase(ProdyDatabase.DATABASE_NAME)

        val legacyDb = context.getDatabasePath(ProdyDatabase.DATABASE_NAME)
        legacyDb.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(legacyDb, null).use { db ->
            db.version = 3
            db.execSQL("CREATE TABLE IF NOT EXISTS legacy_table (id INTEGER PRIMARY KEY)")
        }

        val ready = ProdyDatabase.ensureDatabaseReady(context)
        assertFalse(ready)
        assertTrue(ProdyDatabase.requiresRecovery(context))

        context.deleteDatabase(ProdyDatabase.DATABASE_NAME)
        ProdyDatabase.clearRecoveryFlag(context)
    }
}
