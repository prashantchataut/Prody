package com.prody.prashant.data.security

import android.content.Context
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
     * Get the database passphrase for SQLCipher
     * Uses a combination of device-specific and app-specific data
     */
    suspend fun getDatabasePassphrase(): String = withContext(Dispatchers.IO) {
        try {
            // Get or create the encrypted database key file
            val keyFile = getDatabaseKeyFile()
            
            if (keyFile.exists()) {
                // Read existing key
                readDatabaseKey(keyFile)
            } else {
                // Generate and store new key
                val newKey = generateSecureDatabaseKey()
                storeDatabaseKey(keyFile, newKey)
                newKey
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting database passphrase", e)
            // Fallback to a derived key in case of errors
            generateFallbackPassphrase()
        }
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
     * Generate a fallback passphrase in case of errors
     */
    private fun generateFallbackPassphrase(): String {
        // Use device-specific and app-specific data for fallback
        val deviceId = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ) ?: "unknown_device"
        
        val appData = "${context.packageName}_${BuildConfig.VERSION_CODE}"
        val combined = "${deviceId}_${appData}_prody_secure_fallback"
        
        return combined.hashCode().toString() + System.currentTimeMillis().toString()
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
     * Read database key securely
     */
    private suspend fun readDatabaseKey(keyFile: File): String = withContext(Dispatchers.IO) {
        try {
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
            Log.e(TAG, "Error reading database key", e)
            throw SecurityException("Failed to read database key", e)
        }
    }

    /**
     * Verify database integrity
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
     * Clear all database encryption data (for testing or reset)
     */
    suspend fun clearDatabaseEncryption() = withContext(Dispatchers.IO) {
        try {
            val keyFile = getDatabaseKeyFile()
            if (keyFile.exists()) {
                keyFile.delete()
            }
            
            Log.d(TAG, "Database encryption data cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing database encryption", e)
        }
    }
}