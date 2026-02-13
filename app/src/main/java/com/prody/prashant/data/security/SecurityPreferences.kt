package com.prody.prashant.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.util.Properties
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

private val Context.securityDataStore: DataStore<Preferences> by preferencesDataStore(name = "security_preferences")

/**
 * Secure API key storage that reads keys from local.properties at runtime
 * instead of embedding them in BuildConfig (which can be decompiled from APK).
 *
 * Keys are stored encrypted in DataStore for session persistence.
 */
@Singleton
class SecurityPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "SecurityPreferences"
        private const val LOCAL_PROPERTIES_FILE = "local.properties"
        private const val KEY_AI_API_KEY = "AI_API_KEY"
        private const val KEY_OPENROUTER_API_KEY = "OPENROUTER_API_KEY"
        
        // Keystore and encryption constants
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "ProdyApiEncryptionKey"
        private const val AES_GCM_NO_PADDING = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12 // 96-bit IV for GCM
        private const val GCM_TAG_LENGTH = 128 // 128-bit authentication tag

        private val ENCRYPTED_AI_API_KEY = stringPreferencesKey("encrypted_ai_api_key")
        private val ENCRYPTED_OPENROUTER_API_KEY = stringPreferencesKey("encrypted_openrouter_api_key")
    }

    private val dataStore = context.securityDataStore
    private val properties = Properties()

    init {
        loadLocalProperties()
    }

    /**
     * Load API keys from local.properties file.
     * This file should be in the project root and NOT committed to version control.
     */
    private fun loadLocalProperties() {
        try {
            val localPropertiesFile = getLocalPropertiesFile()
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { input ->
                    properties.load(input)
                }
                com.prody.prashant.util.AppLogger.d(TAG, "Loaded API keys from local.properties")
            } else {
                com.prody.prashant.util.AppLogger.w(TAG, "local.properties file not found. API keys will need to be configured.")
            }
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to load local.properties", e)
        }
    }

    private fun getLocalPropertiesFile(): File = File(context.filesDir, LOCAL_PROPERTIES_FILE)

    /**
     * Get the Gemini API key.
     * First tries to get from encrypted storage, then falls back to local.properties.
     */
    fun getGeminiApiKey(): String {
        return try {
            // First check encrypted storage
            val encryptedKey = runBlocking {
                runCatching { dataStore.data.first()[ENCRYPTED_AI_API_KEY] }.getOrNull()
            }

            if (!encryptedKey.isNullOrEmpty()) {
                decryptKey(encryptedKey)
            } else {
                // Fall back to local.properties
                val key = properties.getProperty(KEY_AI_API_KEY, "").trim()
                if (key.isNotEmpty()) {
                    // Store in encrypted storage for next time
                    runBlocking { storeGeminiApiKey(key) }
                }
                key
            }
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error getting Gemini API key", e)
            ""
        }
    }

    /**
     * Get the OpenRouter API key.
     */
    fun getOpenRouterApiKey(): String {
        return try {
            val encryptedKey = runBlocking {
                runCatching { dataStore.data.first()[ENCRYPTED_OPENROUTER_API_KEY] }.getOrNull()
            }

            if (!encryptedKey.isNullOrEmpty()) {
                decryptKey(encryptedKey)
            } else {
                val key = properties.getProperty(KEY_OPENROUTER_API_KEY, "").trim()
                if (key.isNotEmpty()) {
                    runBlocking { storeOpenRouterApiKey(key) }
                }
                key
            }
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error getting OpenRouter API key", e)
            ""
        }
    }

    /**
     * Store Gemini API key in encrypted storage.
     */
    suspend fun storeGeminiApiKey(key: String) {
        try {
            val encryptedKey = encryptKey(key)
            dataStore.edit { preferences ->
                preferences[ENCRYPTED_AI_API_KEY] = encryptedKey
            }
            com.prody.prashant.util.AppLogger.d(TAG, "Gemini API key stored securely")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error storing Gemini API key", e)
        }
    }

    /**
     * Store OpenRouter API key in encrypted storage.
     */
    suspend fun storeOpenRouterApiKey(key: String) {
        try {
            val encryptedKey = encryptKey(key)
            dataStore.edit { preferences ->
                preferences[ENCRYPTED_OPENROUTER_API_KEY] = encryptedKey
            }
            com.prody.prashant.util.AppLogger.d(TAG, "OpenRouter API key stored securely")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error storing OpenRouter API key", e)
        }
    }

    /**
     * Clear all stored API keys.
     */
    suspend fun clearAllKeys() {
        dataStore.edit { preferences ->
            preferences.remove(ENCRYPTED_AI_API_KEY)
            preferences.remove(ENCRYPTED_OPENROUTER_API_KEY)
        }
        com.prody.prashant.util.AppLogger.d(TAG, "All API keys cleared")
    }

    /**
     * Check if Gemini API key is configured.
     */
    fun isGeminiConfigured(): Boolean {
        return getGeminiApiKey().isNotEmpty()
    }

    /**
     * Check if OpenRouter API key is configured.
     */
    fun isOpenRouterConfigured(): Boolean {
        return getOpenRouterApiKey().isNotEmpty()
    }

/**
     * Get or create the encryption key from Android Keystore.
     * Uses AES-256-GCM for secure encryption.
     */
    private fun getOrCreateEncryptionKey(): SecretKey {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            // Try to get existing key
            keyStore.getKey(KEY_ALIAS, null) as? SecretKey
                ?: createNewEncryptionKey(keyStore)
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error getting encryption key from keystore", e)
            throw SecurityException("Failed to get encryption key", e)
        }
    }

    /**
     * Create a new encryption key in Android Keystore.
     */
    private fun createNewEncryptionKey(keyStore: KeyStore): SecretKey {
        return try {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256) // AES-256
                .setUserAuthenticationRequired(false)
                .setRandomizedEncryptionRequired(true)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            val key = keyGenerator.generateKey()
            
            com.prody.prashant.util.AppLogger.d(TAG, "Created new encryption key in Android Keystore")
            key
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error creating encryption key", e)
            throw SecurityException("Failed to create encryption key", e)
        }
    }

    /**
     * Encrypt API key using AES-256-GCM with Android Keystore.
     * Returns Base64 encoded string with IV + ciphertext + tag.
     */
    private fun encryptKey(plaintext: String): String {
        return try {
            val secretKey = getOrCreateEncryptionKey()
            val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
            
            // Generate random IV
            val iv = ByteArray(GCM_IV_LENGTH)
            java.security.SecureRandom().nextBytes(iv)
            
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)
            
            // Encrypt the plaintext
            val ciphertext = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))
            
            // Combine IV + ciphertext and encode as Base64
            val encryptedData = iv + ciphertext
            Base64.encodeToString(encryptedData, Base64.NO_WRAP)
            
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error encrypting API key", e)
            throw SecurityException("Failed to encrypt API key", e)
        }
    }

    /**
     * Decrypt API key using AES-256-GCM with Android Keystore.
     * Expects Base64 encoded string with IV + ciphertext + tag.
     */
    private fun decryptKey(encryptedData: String): String {
        return try {
            val secretKey = getOrCreateEncryptionKey()
            val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
            
            // Decode Base64 and extract IV and ciphertext
            val encryptedBytes = Base64.decode(encryptedData, Base64.NO_WRAP)
            
            if (encryptedBytes.size < GCM_IV_LENGTH) {
                throw SecurityException("Invalid encrypted data format")
            }
            
            val iv = encryptedBytes.copyOfRange(0, GCM_IV_LENGTH)
            val ciphertext = encryptedBytes.copyOfRange(GCM_IV_LENGTH, encryptedBytes.size)
            
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)
            
            // Decrypt the ciphertext
            val plaintext = cipher.doFinal(ciphertext)
            String(plaintext, StandardCharsets.UTF_8)
            
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error decrypting API key", e)
            // Return empty string on decryption failure instead of crashing
            ""
        }
    }
}
