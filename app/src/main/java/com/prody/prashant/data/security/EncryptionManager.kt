package com.prody.prashant.data.security

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * EncryptionManager provides secure encryption for sensitive user data.
 *
 * Security Features:
 * - AES-256-GCM encryption for journal content
 * - Secure key storage using Android Keystore
 * - Proper IV/nonce generation for each encryption
 * - No plaintext secrets stored on disk
 *
 * Usage:
 * - Call encryptText() before storing sensitive data
 * - Call decryptText() when reading sensitive data
 * - Keys are automatically managed and stored securely
 */
@Singleton
class EncryptionManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "EncryptionManager"
        private const val PREFS_NAME = "prody_secure_prefs"
        private const val KEY_JOURNAL_KEY = "journal_encryption_key"
        private const val ALGORITHM = "AES/GCM/NoPadding"
        private const val KEY_SIZE = 256
        private const val IV_SIZE = 12 // 96 bits for GCM
        private const val TAG_SIZE = 128 // Authentication tag size in bits
    }

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Gets or creates the encryption key for journal data.
     * Key is stored securely in EncryptedSharedPreferences.
     */
    private fun getOrCreateJournalKey(): SecretKey {
        val existingKey = encryptedPrefs.getString(KEY_JOURNAL_KEY, null)

        return if (existingKey != null) {
            val keyBytes = Base64.decode(existingKey, Base64.NO_WRAP)
            SecretKeySpec(keyBytes, "AES")
        } else {
            // Generate a new key
            val keyBytes = ByteArray(KEY_SIZE / 8)
            SecureRandom().nextBytes(keyBytes)
            val key = SecretKeySpec(keyBytes, "AES")

            // Store the key securely
            encryptedPrefs.edit()
                .putString(KEY_JOURNAL_KEY, Base64.encodeToString(keyBytes, Base64.NO_WRAP))
                .apply()

            Log.d(TAG, "Created new journal encryption key")
            key
        }
    }

    /**
     * Encrypts sensitive text using AES-256-GCM.
     * Returns base64-encoded string containing IV + ciphertext + auth tag.
     *
     * @param plaintext The text to encrypt
     * @return Encrypted string (base64 encoded), or original text if encryption fails
     */
    fun encryptText(plaintext: String): String {
        if (plaintext.isBlank()) return plaintext

        return try {
            val key = getOrCreateJournalKey()
            val cipher = Cipher.getInstance(ALGORITHM)

            // Generate random IV
            val iv = ByteArray(IV_SIZE)
            SecureRandom().nextBytes(iv)

            val gcmSpec = GCMParameterSpec(TAG_SIZE, iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec)

            val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

            // Combine IV and ciphertext
            val combined = ByteArray(iv.size + ciphertext.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(ciphertext, 0, combined, iv.size, ciphertext.size)

            // Prefix with marker to identify encrypted content
            "ENC:" + Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e(TAG, "Encryption failed", e)
            // CRITICAL SECURITY FIX: Fail-secure. Do not return plaintext on failure.
            throw EncryptionException("Encryption failed for a given plaintext.", e)
        }
    }

    /**
     * Decrypts text that was encrypted using encryptText().
     *
     * @param encryptedText The encrypted string (base64 encoded with ENC: prefix)
     * @return Decrypted plaintext, or original text if decryption fails or not encrypted
     */
    fun decryptText(encryptedText: String): String {
        if (encryptedText.isBlank()) return encryptedText

        // Check if this is encrypted content
        if (!encryptedText.startsWith("ENC:")) {
            // Not encrypted, return as-is (backwards compatibility)
            return encryptedText
        }

        return try {
            val key = getOrCreateJournalKey()
            val cipher = Cipher.getInstance(ALGORITHM)

            // Remove prefix and decode
            val combined = Base64.decode(encryptedText.substring(4), Base64.NO_WRAP)

            // Extract IV and ciphertext
            val iv = ByteArray(IV_SIZE)
            val ciphertext = ByteArray(combined.size - IV_SIZE)
            System.arraycopy(combined, 0, iv, 0, IV_SIZE)
            System.arraycopy(combined, IV_SIZE, ciphertext, 0, ciphertext.size)

            val gcmSpec = GCMParameterSpec(TAG_SIZE, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)

            String(cipher.doFinal(ciphertext), Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Decryption failed", e)
            // CRITICAL SECURITY FIX: Fail-secure. Do not return encrypted text on failure.
            throw EncryptionException("Decryption failed for a given encrypted text.", e)
        }
    }

    /**
     * Checks if encryption is available on this device.
     * Useful for debug/diagnostics.
     */
    fun isEncryptionAvailable(): Boolean {
        return try {
            val testText = "test_encryption"
            val encrypted = encryptText(testText)
            val decrypted = decryptText(encrypted)
            decrypted == testText
        } catch (e: Exception) {
            Log.e(TAG, "Encryption availability check failed", e)
            false
        }
    }

    /**
     * Clears all encryption keys. Used when user clears all data.
     * WARNING: This will make previously encrypted data unrecoverable.
     */
    fun clearEncryptionKeys() {
        try {
            encryptedPrefs.edit().clear().apply()
            Log.d(TAG, "Encryption keys cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear encryption keys", e)
        }
    }

    /**
     * Stores a sensitive string securely.
     * Use this for API keys, tokens, etc.
     */
    fun storeSecurely(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }

    /**
     * Retrieves a securely stored string.
     */
    fun retrieveSecurely(key: String): String? {
        return encryptedPrefs.getString(key, null)
    }

    /**
     * Removes a securely stored string.
     */
    fun removeSecurely(key: String) {
        encryptedPrefs.edit().remove(key).apply()
    }
}
