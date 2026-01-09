package com.prody.prashant.data.security

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileInputStream
import java.util.Properties
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
            val localPropertiesFile = context.getLocalPropertiesFile()
            if (localPropertiesFile.exists()) {
                FileInputStream(localPropertiesFile).use { input ->
                    properties.load(input)
                }
                Log.d(TAG, "Loaded API keys from local.properties")
            } else {
                Log.w(TAG, "local.properties file not found. API keys will need to be configured.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load local.properties", e)
        }
    }

    private fun context.getLocalPropertiesFile() = context.absoluteFile.resolve(LOCAL_PROPERTIES_FILE)

    /**
     * Get the Gemini API key.
     * First tries to get from encrypted storage, then falls back to local.properties.
     */
    fun getGeminiApiKey(): String {
        return try {
            // First check encrypted storage
            val encryptedKey = runCatching {
                dataStore.data.first()[ENCRYPTED_AI_API_KEY]
            }.getOrNull()

            if (!encryptedKey.isNullOrEmpty()) {
                decryptKey(encryptedKey)
            } else {
                // Fall back to local.properties
                val key = properties.getProperty(KEY_AI_API_KEY, "").trim()
                if (key.isNotEmpty()) {
                    // Store in encrypted storage for next time
                    storeGeminiApiKey(key)
                }
                key
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Gemini API key", e)
            ""
        }
    }

    /**
     * Get the OpenRouter API key.
     */
    fun getOpenRouterApiKey(): String {
        return try {
            val encryptedKey = runCatching {
                dataStore.data.first()[ENCRYPTED_OPENROUTER_API_KEY]
            }.getOrNull()

            if (!encryptedKey.isNullOrEmpty()) {
                decryptKey(encryptedKey)
            } else {
                val key = properties.getProperty(KEY_OPENROUTER_API_KEY, "").trim()
                if (key.isNotEmpty()) {
                    storeOpenRouterApiKey(key)
                }
                key
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting OpenRouter API key", e)
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
            Log.d(TAG, "Gemini API key stored securely")
        } catch (e: Exception) {
            Log.e(TAG, "Error storing Gemini API key", e)
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
            Log.d(TAG, "OpenRouter API key stored securely")
        } catch (e: Exception) {
            Log.e(TAG, "Error storing OpenRouter API key", e)
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
        Log.d(TAG, "All API keys cleared")
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
     * Simple XOR-based encryption for API keys.
     * Note: For production use, consider Android Keystore-backed encryption.
     */
    private fun encryptKey(key: String): String {
        val secret = "ProdySecureKey2024" // This should be derived from Android Keystore in production
        return key.mapIndexed { index, char ->
            (char.code xor secret[index % secret.length].code).toChar()
        }.joinToString("") + ":" + key.length
    }

    /**
     * Decrypt an API key.
     */
    private fun decryptKey(encryptedKey: String): String {
        val parts = encryptedKey.split(":")
        if (parts.size != 2) return ""

        val encrypted = parts[0]
        val length = parts[1].toIntOrNull() ?: return ""

        val secret = "ProdySecureKey2024"
        return encrypted.mapIndexed { index, char ->
            (char.code xor secret[index % secret.length].code).toChar()
        }.joinToString("").take(length)
    }
}
