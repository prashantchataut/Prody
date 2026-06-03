package com.prody.prashant.data.local.database

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.prody.prashant.data.security.SecureDatabaseManager
import javax.inject.Named

object DatabaseFactory {
    private const val TAG = "ProdyDatabase"
    private const val PRODY_ENCRYPTED_SHARED_PREFS = "prody_encrypted_shared_prefs"

    private val DESTRUCTIVE_MIGRATION_FLOOR_VERSIONS = intArrayOf(1, 2, 3)

    fun create(
        context: Context,
        databaseName: String,
        instanceProvider: () -> ProdyDatabase?
    ): ProdyDatabase {
        val encryptedPrefs = createOrRecoverEncryptedPrefs(context)
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
    }

    private fun createOrRecoverEncryptedPrefs(context: Context): SharedPreferences {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                context,
                PRODY_ENCRYPTED_SHARED_PREFS,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.w(TAG, "EncryptedSharedPreferences init failed, attempting recovery", e)
            return recoverEncryptedPrefs(context)
        }
    }

    private fun recoverEncryptedPrefs(context: Context): SharedPreferences {
        try {
            context.deleteSharedPreferences(PRODY_ENCRYPTED_SHARED_PREFS)
            Log.i(TAG, "Deleted corrupted encrypted prefs, recreating with fresh keys")

            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                context,
                PRODY_ENCRYPTED_SHARED_PREFS,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "CRITICAL: EncryptedSharedPreferences recovery failed. Database encryption will use fallback.", e)
            throw IllegalStateException(
                "Database encryption initialization failed. " +
                "Clear app data or reinstall. Original error: ${e.message}", e
            )
        }
    }
}
    }
}
