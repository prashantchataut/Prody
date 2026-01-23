package com.prody.prashant.data.security

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.Properties
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure API key storage that reads keys from local.properties at runtime
 * instead of embedding them in BuildConfig (which can be decompiled from APK).
 *
 * Keys are stored using EncryptionManager which uses EncryptedSharedPreferences.
 */
@Singleton
class SecurityPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptionManager: EncryptionManager
) {
    companion object {
        private const val TAG = "SecurityPreferences"
        private const val LOCAL_PROPERTIES_FILE = "local.properties"
        private const val KEY_AI_API_KEY = "AI_API_KEY"
        private const val KEY_OPENROUTER_API_KEY = "OPENROUTER_API_KEY"
    }

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
                Log.d(TAG, "Loaded API keys from local.properties")
            } else {
                Log.w(TAG, "local.properties file not found. API keys will need to be configured.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load local.properties", e)
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
            val storedKey = encryptionManager.retrieveSecurely(KEY_AI_API_KEY)
            if (!storedKey.isNullOrEmpty()) {
                return storedKey
            }

            // Fall back to local.properties
            val keyFromProps = properties.getProperty(KEY_AI_API_KEY, "").trim()
            if (keyFromProps.isNotEmpty()) {
                // Store in encrypted storage for next time
                storeGeminiApiKey(keyFromProps)
            }
            keyFromProps
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
            val storedKey = encryptionManager.retrieveSecurely(KEY_OPENROUTER_API_KEY)
            if (!storedKey.isNullOrEmpty()) {
                return storedKey
            }

            val keyFromProps = properties.getProperty(KEY_OPENROUTER_API_KEY, "").trim()
            if (keyFromProps.isNotEmpty()) {
                storeOpenRouterApiKey(keyFromProps)
            }
            keyFromProps
        } catch (e: Exception) {
            Log.e(TAG, "Error getting OpenRouter API key", e)
            ""
        }
    }

    /**
     * Store Gemini API key in encrypted storage.
     */
    fun storeGeminiApiKey(key: String) {
        try {
            encryptionManager.storeSecurely(KEY_AI_API_KEY, key)
            Log.d(TAG, "Gemini API key stored securely")
        } catch (e: Exception) {
            Log.e(TAG, "Error storing Gemini API key", e)
        }
    }

    /**
     * Store OpenRouter API key in encrypted storage.
     */
    fun storeOpenRouterApiKey(key: String) {
        try {
            encryptionManager.storeSecurely(KEY_OPENROUTER_API_KEY, key)
            Log.d(TAG, "OpenRouter API key stored securely")
        } catch (e: Exception) {
            Log.e(TAG, "Error storing OpenRouter API key", e)
        }
    }

    /**
     * Clear all stored API keys.
     */
    fun clearAllKeys() {
        encryptionManager.removeSecurely(KEY_AI_API_KEY)
        encryptionManager.removeSecurely(KEY_OPENROUTER_API_KEY)
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

}
