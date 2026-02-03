package com.prody.prashant.data.security

import android.content.Context
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.prody.prashant.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure Database Manager with SQLCipher Encryption
 * 
 * Provides encrypted database storage using:
 * - SQLCipher for transparent database encryption
 * - Android Keystore for key management
 * - Deterministic secure key derivation for database password
 * - Synchronous access to avoid Room initialization blocks
 */
@Singleton
class SecureDatabaseManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "SecureDatabaseManager"
        private const val SECURE_PREFS_NAME = "secure_db_prefs"
        private const val KEY_DATABASE_PASSPHRASE = "db_passphrase"
    }

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val securePrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            SECURE_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Get the database passphrase for SQLCipher.
     * Access is synchronous to avoid runBlocking during Room initialization.
     */
    fun getDatabasePassphrase(): String {
        return try {
            // First try to get from secure preferences
            val existingKey = securePrefs.getString(KEY_DATABASE_PASSPHRASE, null)
            
            if (existingKey != null) {
                existingKey
            } else {
                // Generate deterministic key, store it, and return
                val newKey = generateDeterministicPassphrase()
                securePrefs.edit().putString(KEY_DATABASE_PASSPHRASE, newKey).apply()
                newKey
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting database passphrase, using fallback", e)
            generateDeterministicPassphrase()
        }
    }

    /**
     * Create a SupportFactory for SQLCipher with the secure passphrase.
     * Synchronous to avoid thread blocking during dependency injection.
     */
    fun createSQLCipherSupportFactory(): SupportFactory {
        val passphrase = getDatabasePassphrase()
        val passphraseBytes = SQLiteDatabase.getBytes(passphrase.toCharArray())
        return SupportFactory(passphraseBytes)
    }

    /**
     * Generate a deterministic passphrase based on device and app identifiers.
     * This ensures the key is robust and consistent across re-installations.
     */
    private fun generateDeterministicPassphrase(): String {
        return try {
            val deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "unknown_device"

            val appData = "${context.packageName}_${BuildConfig.VERSION_CODE}"
            val salt = "prody_secure_v2_salt"
            val combined = "${deviceId}_${appData}_${salt}"

            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(combined.toByteArray(StandardCharsets.UTF_8))
            Base64.encodeToString(hash, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e(TAG, "Critical: Failed to generate deterministic passphrase", e)
            // Final fallback (last resort)
            "prody_ultimate_fallback_${BuildConfig.VERSION_CODE}"
        }
    }

    /**
     * Verify database integrity.
     */
    fun verifyDatabaseIntegrity(databaseFile: File): Boolean {
        if (!databaseFile.exists()) {
            Log.w(TAG, "Database file does not exist")
            return false
        }

        return try {
            val passphrase = getDatabasePassphrase()
            val testDb = SQLiteDatabase.openDatabase(
                databaseFile.absolutePath,
                passphrase,
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            val isIntact = testDb.isOpen
            testDb.close()
            Log.d(TAG, "Database integrity check: PASSED")
            isIntact
        } catch (e: Exception) {
            Log.e(TAG, "Database integrity check failed", e)
            false
        }
    }

    /**
     * Clear all database encryption data.
     */
    fun clearDatabaseEncryption() {
        try {
            securePrefs.edit().remove(KEY_DATABASE_PASSPHRASE).apply()
            Log.d(TAG, "Database encryption data cleared from secure preferences")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing database encryption", e)
        }
    }
}
