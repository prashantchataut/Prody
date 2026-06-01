package com.prody.prashant.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.prody.prashant.data.security.SecureDatabaseManager

object DatabaseFactory {
    private const val TAG = "ProdyDatabase"

    /**
     * Schema versions that lack explicit migrations.
     * These are early versions (1-3) from before the migration framework was established.
     * Users on these versions will have their data destructively migrated as a last resort.
     * Version 4+ all have explicit migrations and will never be destructively migrated.
     */
    private val DESTRUCTIVE_MIGRATION_FLOOR_VERSIONS = intArrayOf(1, 2, 3)

    fun create(
        context: Context,
        databaseName: String,
        instanceProvider: () -> ProdyDatabase?
    ): ProdyDatabase {
        try {
            val masterKey = androidx.security.crypto.MasterKey.Builder(context)
                .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
                .build()

            val encryptedPrefs = androidx.security.crypto.EncryptedSharedPreferences.create(
                context,
                "prody_encrypted_shared_prefs",
                masterKey,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val secureDbManager = SecureDatabaseManager(context, encryptedPrefs)
            val supportFactory = secureDbManager.createSQLCipherSupportFactorySync()

            return Room.databaseBuilder(context.applicationContext, ProdyDatabase::class.java, databaseName)
                .openHelperFactory(supportFactory)
                .addMigrations(*DatabaseMigrations.all)
                .fallbackToDestructiveMigrationFrom(*DESTRUCTIVE_MIGRATION_FLOOR_VERSIONS)
                .addCallback(
                    SecureDatabaseLifecycleCallback(
                        context = context,
                        secureDbManager = secureDbManager,
                        databaseName = databaseName,
                        tag = TAG,
                        databaseProvider = instanceProvider
                    )
                )
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "CRITICAL: Failed to create encrypted database. " +
                "Refusing to fall back to unencrypted storage for user data safety.", e)
            throw IllegalStateException(
                "Database encryption initialization failed. " +
                "User data cannot be stored without encryption. " +
                "Original error: ${e.message}", e
            )
        }
    }
}
