package com.prody.prashant.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.prody.prashant.data.security.SecureDatabaseManager

object DatabaseFactory {
    fun create(
        context: Context,
        databaseName: String,
        instanceProvider: () -> ProdyDatabase?
    ): ProdyDatabase {
        return try {
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

            Room.databaseBuilder(context.applicationContext, ProdyDatabase::class.java, databaseName)
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
            Log.e("ProdyDatabase", "CRITICAL: Failed to create secure database. Fail Secure policy in effect.", e)
            // FAIL SECURE: Do not fallback to unencrypted database.
            // Throwing a RuntimeException will cause the app to crash rather than exposing sensitive data.
            throw SecurityException("Database encryption failed. For user privacy, the application cannot proceed.", e)
        }
    }
}
