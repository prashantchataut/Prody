package com.prody.prashant.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.prody.prashant.data.security.SecureDatabaseManager

object DatabaseFactory {
    /**
     * Creates a Room database with mandatory SQLCipher encryption.
     *
     * Security Policy: FAIL SECURE.
     * If encryption cannot be established, this method throws a SecurityException
     * rather than falling back to an unencrypted database.
     */
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
                .fallbackToDestructiveMigration()
                .addCallback(
                    SecureDatabaseLifecycleCallback(
                        context = context,
                        secureDbManager = secureDbManager,
                        databaseName = databaseName,
                        tag = "ProdyDatabase",
                        databaseProvider = instanceProvider
                    )
                )
                .build()
        } catch (e: Exception) {
            Log.e("ProdyDatabase", "CRITICAL SECURITY FAILURE: Could not create secure database", e)
            // Propagate security failure to prevent cleartext data exposure
            throw SecurityException("Database security initialization failed. Prody cannot run without encryption.", e)
        }
    }
}
