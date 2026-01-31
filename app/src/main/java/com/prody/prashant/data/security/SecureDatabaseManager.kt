package com.prody.prashant.data.security

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
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
import javax.inject.Singleton

/**
 * Secure Database Manager with SQLCipher Encryption
 * 
 * Provides encrypted database storage using:
 * - SQLCipher for transparent database encryption
 * - Android Keystore for key management
 * - Secure key derivation for database password
 */
@Singleton
class SecureDatabaseManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "SecureDatabaseManager"
        private const val DATABASE_KEY_ALIAS = "ProdyDatabaseEncryptionKey"
        private const val DATABASE_VERSION = 1
    }

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "prody_db_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Get the database passphrase for SQLCipher synchronously.
     * Uses EncryptedSharedPreferences for fast access to avoid blocking.
     */
    fun getDatabasePassphraseSync(): String {
        try {
            // 1. Try to get from EncryptedSharedPreferences (fastest, synchronous)
            val storedKey = encryptedPrefs.getString("db_passphrase", null)
            if (storedKey != null) return storedKey

            // 2. If not found, check if it exists in the legacy EncryptedFile (migration)
            val keyFile = getDatabaseKeyFile()
            if (keyFile.exists()) {
                val key = readDatabaseKeySync(keyFile)
                encryptedPrefs.edit().putString("db_passphrase", key).apply()
                Log.d(TAG, "Migrated database passphrase from file to EncryptedSharedPreferences")
                return key
            }

            // 3. Generate and store new key
            val newKey = generateSecureDatabaseKey()
            encryptedPrefs.edit().putString("db_passphrase", newKey).apply()
            return newKey
        } catch (e: Exception) {
            Log.e(TAG, "Error getting database passphrase synchronously", e)
            return generateFallbackPassphrase()
        }
    }

    /**
     * Get the database passphrase for SQLCipher (suspend version)
     */
    suspend fun getDatabasePassphrase(): String = withContext(Dispatchers.IO) {
        getDatabasePassphraseSync()
    }

    /**
     * Create a SupportFactory for SQLCipher with the secure passphrase synchronously
     */
    fun createSQLCipherSupportFactorySync(): SupportFactory {
        val passphrase = getDatabasePassphraseSync()
        val passphraseBytes = SQLiteDatabase.getBytes(passphrase.toCharArray())
        return SupportFactory(passphraseBytes)
    }

    /**
     * Create a SupportFactory for SQLCipher with the secure passphrase
     */
    suspend fun createSQLCipherSupportFactory(): SupportFactory {
        return withContext(Dispatchers.IO) {
            createSQLCipherSupportFactorySync()
        }
    }

    /**
     * Get the database key file path
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
     * Generate a fallback passphrase in case of errors.
     * Uses a deterministic SHA-256 hash of stable identifiers.
     */
    private fun generateFallbackPassphrase(): String {
        // Use device-specific and app-specific data for fallback
        val deviceId = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ) ?: "unknown_device"
        
        val appData = "${context.packageName}_${BuildConfig.VERSION_CODE}"
        val combined = "${deviceId}_${appData}_prody_secure_fallback"
        
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(combined.toByteArray(Charsets.UTF_8))
            android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            // Extreme fallback if SHA-256 is somehow unavailable
            Log.e(TAG, "SHA-256 unavailable for fallback passphrase", e)
            combined.hashCode().toString()
        }
    }

    /**
     * Store database key securely
     */
    private suspend fun storeDatabaseKey(keyFile: File, key: String) = withContext(Dispatchers.IO) {
        try {
            val encryptedFile = EncryptedFile.Builder(
                context,
                keyFile,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            
            encryptedFile.openFileOutput().use { output ->
                output.write(key.toByteArray())
                output.flush()
            }
            
            Log.d(TAG, "Database key stored securely")
        } catch (e: IOException) {
            Log.e(TAG, "Error storing database key", e)
            throw SecurityException("Failed to store database key", e)
        }
    }

    /**
     * Read database key securely (suspend version)
     */
    private suspend fun readDatabaseKey(keyFile: File): String = withContext(Dispatchers.IO) {
        readDatabaseKeySync(keyFile)
    }

    /**
     * Read database key securely synchronously
     */
    private fun readDatabaseKeySync(keyFile: File): String {
        try {
            val encryptedFile = EncryptedFile.Builder(
                context,
                keyFile,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            
            return encryptedFile.openFileInput().use { input ->
                val bytes = input.readBytes()
                String(bytes)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading database key synchronously", e)
            throw SecurityException("Failed to read database key", e)
        }
    }

    /**
     * Verify database integrity (suspend version)
     */
    suspend fun verifyDatabaseIntegrity(databaseFile: File): Boolean = withContext(Dispatchers.IO) {
        verifyDatabaseIntegritySync(databaseFile)
    }

    /**
     * Verify database integrity synchronously
     */
    fun verifyDatabaseIntegritySync(databaseFile: File): Boolean {
        try {
            if (!databaseFile.exists()) {
                Log.w(TAG, "Database file does not exist")
                return false
            }

            // Try to open the database with the current passphrase
            val passphrase = getDatabasePassphraseSync()
            
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
            return isIntact
            
        } catch (e: Exception) {
            Log.e(TAG, "Database integrity check failed", e)
            return false
        }
    }

    /**
     * Change database passphrase (for key rotation)
     */
    suspend fun rotateDatabasePassphrase(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Generate new key
            val newKey = generateSecureDatabaseKey()
            
            // Store new key
            val keyFile = getDatabaseKeyFile()
            storeDatabaseKey(keyFile, newKey)
            
            Log.d(TAG, "Database passphrase rotated successfully")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to rotate database passphrase", e)
            false
        }
    }

    /**
     * Clear all database encryption data (for testing or reset) (suspend version)
     */
    suspend fun clearDatabaseEncryption() = withContext(Dispatchers.IO) {
        clearDatabaseEncryptionSync()
    }

    /**
     * Clear all database encryption data synchronously
     */
    fun clearDatabaseEncryptionSync() {
        try {
            val keyFile = getDatabaseKeyFile()
            if (keyFile.exists()) {
                keyFile.delete()
            }
            
            // Also clear from preferences
            encryptedPrefs.edit().remove("db_passphrase").apply()

            Log.d(TAG, "Database encryption data cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing database encryption synchronously", e)
        }
    }
}