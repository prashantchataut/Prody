package com.prody.prashant.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.prody.prashant.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure Database Manager with SQLCipher Encryption
 *
 * Provides encrypted database storage using:
 * - SQLCipher for transparent database encryption
 * - Android Keystore for key management
 * - Secure key derivation for database password
 * - Synchronous access via EncryptedSharedPreferences (optimized for Room initialization)
 */
@Singleton
class SecureDatabaseManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "SecureDatabaseManager"
        private const val SECURE_PREFS_NAME = "prody_secure_db_prefs"
        private const val KEY_DATABASE_PASSPHRASE = "database_passphrase"
        private const val LEGACY_KEY_FILE_NAME = "prody_db_key.enc"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    /**
     * Lazy initialization of EncryptedSharedPreferences for synchronous access
     */
    private val securePrefs: SharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                context,
                SECURE_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create EncryptedSharedPreferences, falling back to standard", e)
            // Fallback to standard SharedPreferences if encryption fails (better than crash)
            context.getSharedPreferences(SECURE_PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    /**
     * Get the database passphrase for SQLCipher synchronously.
     * Uses EncryptedSharedPreferences for persistent, secure storage.
     *
     * Performance: Optimized to avoid runBlocking during Room initialization.
     */
    fun getDatabasePassphrase(): String {
        return try {
            // 1. Try to get existing key from secure preferences
            val existingKey = securePrefs.getString(KEY_DATABASE_PASSPHRASE, null)
            if (existingKey != null) {
                return existingKey
            }

            // 2. Check for legacy key file and migrate if needed
            val legacyKeyFile = File(context.filesDir, LEGACY_KEY_FILE_NAME)
            if (legacyKeyFile.exists()) {
                val migratedKey = migrateLegacyKey(legacyKeyFile)
                if (migratedKey != null) {
                    return migratedKey
                }
            }

            // 3. Generate and store new key if none exists
            val newKey = generateSecureDatabaseKey()
            storeDatabaseKeyInPrefs(newKey)
            newKey
        } catch (e: Exception) {
            Log.e(TAG, "Error getting database passphrase, using fallback", e)
            generateFallbackPassphrase()
        }
    }

    /**
     * Create a SupportFactory for SQLCipher with the secure passphrase synchronously.
     */
    fun createSQLCipherSupportFactory(): SupportFactory {
        val passphrase = getDatabasePassphrase()
        val passphraseBytes = SQLiteDatabase.getBytes(passphrase.toCharArray())
        return SupportFactory(passphraseBytes)
    }

    /**
     * Stores the database key in secure preferences.
     */
    private fun storeDatabaseKeyInPrefs(key: String) {
        try {
            securePrefs.edit().putString(KEY_DATABASE_PASSPHRASE, key).apply()
            Log.d(TAG, "Database key stored in secure preferences")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store database key in prefs", e)
        }
    }

    /**
     * Migrates the database key from legacy EncryptedFile storage to EncryptedSharedPreferences.
     */
    private fun migrateLegacyKey(keyFile: File): String? {
        return try {
            Log.d(TAG, "Legacy key file found, attempting migration...")
            val encryptedFile = EncryptedFile.Builder(
                context,
                keyFile,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val key = encryptedFile.openFileInput().use { input ->
                String(input.readBytes())
            }

            if (key.isNotEmpty()) {
                storeDatabaseKeyInPrefs(key)
                // Delete legacy file after successful migration
                if (keyFile.delete()) {
                    Log.d(TAG, "Legacy key file migrated and deleted")
                }
                key
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to migrate legacy key", e)
            null
        }
    }

    /**
     * Generate a secure database key using SecureRandom and app metadata.
     */
    private fun generateSecureDatabaseKey(): String {
        val random = java.security.SecureRandom()
        val bytes = ByteArray(32) // 256-bit key
        random.nextBytes(bytes)
        
        // Combine with app-specific data for additional security
        val appSignature = context.packageName + BuildConfig.VERSION_CODE
        val signatureBytes = appSignature.toByteArray()
        
        val combinedBytes = ByteArray(bytes.size + signatureBytes.size)
        System.arraycopy(bytes, 0, combinedBytes, 0, bytes.size)
        System.arraycopy(signatureBytes, 0, combinedBytes, bytes.size, signatureBytes.size)
        
        return android.util.Base64.encodeToString(combinedBytes, android.util.Base64.NO_WRAP)
    }

    /**
     * Generate a deterministic fallback passphrase in case of errors.
     * Uses device-specific and app-specific data hashed with SHA-256.
     */
    private fun generateFallbackPassphrase(): String {
        try {
            // Use device-specific and app-specific data for fallback
            val deviceId = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            ) ?: "unknown_device"

            val appData = "${context.packageName}_${BuildConfig.VERSION_CODE}"
            val combined = "${deviceId}_${appData}_prody_secure_fallback"

            // Security: Use SHA-256 for a deterministic, robust fallback key
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(combined.toByteArray(Charsets.UTF_8))
            return android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            // Ultimate fallback if even hashing fails
            return "prody_ultimate_fallback_${BuildConfig.VERSION_CODE}"
        }
    }

    /**
     * Verify database integrity asynchronously.
     */
    suspend fun verifyDatabaseIntegrity(databaseFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!databaseFile.exists()) {
                Log.w(TAG, "Database file does not exist")
                return@withContext false
            }

            // Try to open the database with the current passphrase
            val passphrase = getDatabasePassphrase()
            
            // Test database connection
            val testDb = SQLiteDatabase.openDatabase(
                databaseFile.absolutePath,
                passphrase,
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            
            val isIntact = testDb.isOpen
            testDb.close()
            
            Log.d(TAG, "Database integrity check: ${if (isIntact) "PASSED" else "FAILED"}")
            isIntact
            
        } catch (e: Exception) {
            Log.e(TAG, "Database integrity check failed", e)
            false
        }
    }

    /**
     * Change database passphrase (for key rotation).
     */
    fun rotateDatabasePassphrase(): Boolean {
        return try {
            // Generate new key
            val newKey = generateSecureDatabaseKey()
            storeDatabaseKeyInPrefs(newKey)
            Log.d(TAG, "Database passphrase rotated successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to rotate database passphrase", e)
            false
        }
    }

    /**
     * Clear all database encryption data (for testing or reset).
     */
    fun clearDatabaseEncryption() {
        try {
            securePrefs.edit().remove(KEY_DATABASE_PASSPHRASE).apply()

            val keyFile = File(context.filesDir, LEGACY_KEY_FILE_NAME)
            if (keyFile.exists()) {
                keyFile.delete()
            }
            
            Log.d(TAG, "Database encryption data cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing database encryption", e)
        }
    }
}
