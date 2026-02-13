package com.prody.prashant.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.prody.prashant.data.local.database.ProdyDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import com.prody.prashant.BuildConfig
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Secure Database Manager with SQLCipher Encryption
 * 
 * Provides encrypted database storage using:
 * - SQLCipher for transparent database encryption
 * - Android Keystore for key management
 * - Secure key derivation for database password
 * - Synchronous access via EncryptedSharedPreferences for performance
 */
@Singleton
class SecureDatabaseManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("EncryptedSharedPreferences") private val encryptedPrefs: SharedPreferences
) {
    companion object {
        private const val TAG = "SecureDatabaseManager"
        private const val DATABASE_KEY_ALIAS = "ProdyDatabaseEncryptionKey"
        private const val DB_PASSPHRASE_KEY = "prody_db_passphrase_secure"
        private const val DATABASE_VERSION = 1
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .setKeyGenParameterSpec(
            android.security.keystore.KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or android.security.keystore.KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build()
        )
        .build()

    /**
     * Synchronous access to the database passphrase.
     * Required for Room initialization to avoid blocking the main thread with runBlocking.
     */
    fun getDatabasePassphraseSync(): String {
        return try {
            // 1. Try to get from EncryptedSharedPreferences (Fast path)
            val existingKey = encryptedPrefs.getString(DB_PASSPHRASE_KEY, null)
            if (existingKey != null) {
                return existingKey
            }

            // 2. Not in SharedPreferences, check if migration from legacy file is needed
            val legacyFile = getDatabaseKeyFile()
            if (legacyFile.exists()) {
                com.prody.prashant.util.AppLogger.i(TAG, "Legacy database key file found, migrating to EncryptedSharedPreferences")
                val legacyKey = readDatabaseKeySync(legacyFile)
                if (legacyKey != null) {
                    // Use commit() instead of apply() to ensure the key is written before deleting the legacy file
                    val success = encryptedPrefs.edit().putString(DB_PASSPHRASE_KEY, legacyKey).commit()
                    if (success) {
                        com.prody.prashant.util.AppLogger.d(TAG, "Successfully migrated database key, deleting legacy file")
                        legacyFile.delete()
                    }
                    return legacyKey
                }
            }

            // 3. No key exists, generate a new one
            val newKey = generateSecureDatabaseKey()
            encryptedPrefs.edit().putString(DB_PASSPHRASE_KEY, newKey).apply()
            newKey
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error getting database passphrase synchronously", e)
            generateFallbackPassphrase()
        }
    }

    /**
     * Get the database passphrase for SQLCipher (Suspend version)
     */
    suspend fun getDatabasePassphrase(): String = withContext(Dispatchers.IO) {
        getDatabasePassphraseSync()
    }

    /**
     * Create a SupportFactory for SQLCipher with the secure passphrase
     */
    suspend fun createSQLCipherSupportFactory(): SupportFactory {
        val passphrase = getDatabasePassphrase()
        val passphraseBytes = SQLiteDatabase.getBytes(passphrase.toCharArray())
        return SupportFactory(passphraseBytes)
    }

    /**
     * Create a SupportFactory for SQLCipher synchronously
     */
    fun createSQLCipherSupportFactorySync(): SupportFactory {
        val passphrase = getDatabasePassphraseSync()
        val passphraseBytes = SQLiteDatabase.getBytes(passphrase.toCharArray())
        return SupportFactory(passphraseBytes)
    }

    /**
     * Get the database key file path (Legacy)
     */
    private fun getDatabaseKeyFile(): File {
        return File(context.filesDir, "prody_db_key.enc")
    }

    /**
     * Generate a secure database key
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
     */
    private fun generateFallbackPassphrase(): String {
        try {
            val deviceId = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            ) ?: "unknown_device"

            val appData = "${context.packageName}_${BuildConfig.VERSION_CODE}"
            val combined = "${deviceId}_${appData}_prody_secure_fallback"

            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(combined.toByteArray(Charsets.UTF_8))
            return android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            return "prody_ultimate_fallback_${BuildConfig.VERSION_CODE}"
        }
    }

    /**
     * Read legacy database key securely (Synchronous version for migration)
     */
    private fun readDatabaseKeySync(keyFile: File): String? {
        return try {
            val encryptedFile = EncryptedFile.Builder(
                context,
                keyFile,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            
            encryptedFile.openFileInput().use { input ->
                val bytes = input.readBytes()
                String(bytes)
            }
        } catch (e: IOException) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error reading legacy database key", e)
            null
        }
    }

    /**
     * Verify database integrity
     */
    suspend fun verifyDatabaseIntegrity(databaseFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!databaseFile.exists()) {
                com.prody.prashant.util.AppLogger.w(TAG, "Database file does not exist")
                return@withContext false
            }

            val passphrase = getDatabasePassphraseSync()
            
            val testDb = SQLiteDatabase.openDatabase(
                databaseFile.absolutePath,
                passphrase,
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            
            val isIntact = testDb.isOpen
            testDb.close()
            
            com.prody.prashant.util.AppLogger.d(TAG, "Database integrity check: ${if (isIntact) "PASSED" else "FAILED"}")
            isIntact
            
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Database integrity check failed", e)
            false
        }
    }

    /**
     * Clear all database encryption data
     */
    suspend fun clearDatabaseEncryption() = withContext(Dispatchers.IO) {
        try {
            encryptedPrefs.edit().remove(DB_PASSPHRASE_KEY).apply()

            val keyFile = getDatabaseKeyFile()
            if (keyFile.exists()) {
                keyFile.delete()
            }
            
            com.prody.prashant.util.AppLogger.d(TAG, "Database encryption data cleared")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error clearing database encryption", e)
        }
    }
}
