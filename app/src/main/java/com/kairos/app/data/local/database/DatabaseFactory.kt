package com.kairos.app.data.local.database

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.kairos.app.data.security.SecureDatabaseManager

object DatabaseFactory {
    private const val TAG = "KairosDatabase"
    private const val Kairos_ENCRYPTED_SHARED_PREFS = "Kairos_encrypted_shared_prefs"
    private const val Kairos_ENCRYPTED_FALLBACK_PREFS = "Kairos_encrypted_shared_prefs_fallback"

    private val DESTRUCTIVE_MIGRATION_FLOOR_VERSIONS = intArrayOf(1, 2, 3)

    fun create(
        context: Context,
        databaseName: String,
        instanceProvider: () -> KairosDatabase?
    ): KairosDatabase {
        // SQLCipher native libs must be loaded before SupportFactory opens the DB.
        try {
            net.sqlcipher.database.SQLiteDatabase.loadLibs(context.applicationContext)
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "SQLCipher native libraries failed to load", e)
            throw e
        }

        val encryptedPrefs = createOrRecoverEncryptedPrefs(context)
        val secureDbManager = SecureDatabaseManager(context, encryptedPrefs)
        val supportFactory = secureDbManager.createSQLCipherSupportFactorySync()

        return Room.databaseBuilder(context.applicationContext, KairosDatabase::class.java, databaseName)
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
            return buildEncryptedPrefs(context)
        } catch (e: Exception) {
            Log.w(TAG, "EncryptedSharedPreferences init failed, attempting recovery", e)
            return recoverEncryptedPrefs(context)
        }
    }

    private fun recoverEncryptedPrefs(context: Context): SharedPreferences {
        // Old passphrase is gone; remove the encrypted DB so a new key can open a fresh file.
        deleteDatabaseFiles(context)
        try {
            context.deleteSharedPreferences(Kairos_ENCRYPTED_SHARED_PREFS)
            Log.i(TAG, "Deleted corrupted encrypted prefs, recreating with fresh keys")
            return buildEncryptedPrefs(context)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "EncryptedSharedPreferences recovery failed — using private fallback prefs for passphrase storage",
                e
            )
            return context.getSharedPreferences(Kairos_ENCRYPTED_FALLBACK_PREFS, Context.MODE_PRIVATE)
        }
    }

    private fun buildEncryptedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            Kairos_ENCRYPTED_SHARED_PREFS,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun deleteDatabaseFiles(context: Context) {
        try {
            val deleted = context.deleteDatabase(KairosDatabase.DATABASE_NAME)
            Log.i(TAG, "Deleted database files during encryption recovery: deleted=$deleted")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to delete database files during encryption recovery", e)
        }
    }
}
