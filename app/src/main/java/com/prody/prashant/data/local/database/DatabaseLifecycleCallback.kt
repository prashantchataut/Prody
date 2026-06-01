package com.prody.prashant.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.prody.prashant.data.security.SecureDatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

open class DatabaseLifecycleCallback(
    private val tag: String,
    private val databaseProvider: () -> ProdyDatabase?
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Log.d(tag, "Database created successfully - initiating data seeding")
        databaseProvider()?.let { DatabaseSeeder.seedDatabase(it) }
            ?: Log.e(tag, "Failed to seed database: INSTANCE is null")
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        Log.d(tag, "Database opened")
    }

    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
        super.onDestructiveMigration(db)
        Log.w(tag, "Destructive migration performed - re-seeding database")
        databaseProvider()?.let { DatabaseSeeder.seedDatabase(it) }
            ?: Log.e(tag, "Failed to seed database after destructive migration: INSTANCE is null")
    }
}

class SecureDatabaseLifecycleCallback(
    private val context: Context,
    private val secureDbManager: SecureDatabaseManager,
    private val databaseName: String,
    tag: String,
    databaseProvider: () -> ProdyDatabase?
) : DatabaseLifecycleCallback(tag = tag, databaseProvider = databaseProvider) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        scope.launch {
            try {
                val databaseFile = context.getDatabasePath(databaseName)
                val isIntegrityValid = secureDbManager.verifyDatabaseIntegrity(databaseFile)
                if (!isIntegrityValid) {
                    Log.e("ProdyDatabase", "Database integrity check failed!")
                }
            } catch (e: Exception) {
                Log.e("ProdyDatabase", "Database integrity check error", e)
            }
        }
    }

    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
        super.onDestructiveMigration(db)
        scope.launch {
            try {
                secureDbManager.clearDatabaseEncryption()
            } catch (e: Exception) {
                Log.e("ProdyDatabase", "Failed to clear database encryption after migration", e)
            }
        }
    }
}