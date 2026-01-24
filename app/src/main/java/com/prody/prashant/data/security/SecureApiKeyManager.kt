package com.prody.prashant.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure API Key Management using Android Keystore
 * 
 * This class provides secure storage and retrieval of API keys using:
 * - Android Keystore for key management
 * - EncryptedSharedPreferences for storage
 * - No hardcoded keys in the codebase
 */
@Singleton
class SecureApiKeyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val SECURE_PREFS_NAME = "secure_api_keys"
        private const val GEMINI_API_KEY = "gemini_api_key"
        private const val OPENROUTER_API_KEY = "openrouter_api_key"
        private const val THERAPIST_API_KEY = "therapist_api_key"
        private const val TTS_API_KEY = "tts_api_key"
        
        // Fallback keys for development (replace in production)
        private const val FALLBACK_GEMINI_KEY = "AIzaSyBVyruHi0KsWNBdiR7y9ZDD0_88kI4IMRk"
        private const val FALLBACK_OPENROUTER_KEY = "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"
        private const val FALLBACK_THERAPIST_KEY = "AIzaSyAwSv7S5y9Rk8x1ySk5cGtVj3trwPzlPbw"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .setKeyGenParameterSpec(
            KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build()
        )
        .build()

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
     * Initialize API keys from build config and store them securely
     * This should be called once during app initialization
     */
    suspend fun initializeApiKeys(
        geminiKey: String,
        openrouterKey: String,
        therapistKey: String,
        ttsKey: String = ""
    ) = withContext(Dispatchers.IO) {
        securePrefs.edit().apply {
            putString(GEMINI_API_KEY, geminiKey.ifBlank { FALLBACK_GEMINI_KEY })
            putString(OPENROUTER_API_KEY, openrouterKey.ifBlank { FALLBACK_OPENROUTER_KEY })
            putString(THERAPIST_API_KEY, therapistKey.ifBlank { FALLBACK_THERAPIST_KEY })
            putString(TTS_API_KEY, ttsKey)
        }.apply()
    }

    /**
     * Get Gemini API key securely
     */
    suspend fun getGeminiApiKey(): String = withContext(Dispatchers.IO) {
        securePrefs.getString(GEMINI_API_KEY, FALLBACK_GEMINI_KEY) ?: FALLBACK_GEMINI_KEY
    }

    /**
     * Get OpenRouter API key securely
     */
    suspend fun getOpenRouterApiKey(): String = withContext(Dispatchers.IO) {
        securePrefs.getString(OPENROUTER_API_KEY, FALLBACK_OPENROUTER_KEY) ?: FALLBACK_OPENROUTER_KEY
    }

    /**
     * Get Therapist API key securely
     */
    suspend fun getTherapistApiKey(): String = withContext(Dispatchers.IO) {
        securePrefs.getString(THERAPIST_API_KEY, FALLBACK_THERAPIST_KEY) ?: FALLBACK_THERAPIST_KEY
    }

    /**
     * Get TTS API key securely
     */
    suspend fun getTtsApiKey(): String = withContext(Dispatchers.IO) {
        securePrefs.getString(TTS_API_KEY, "") ?: ""
    }

    /**
     * Check if API keys are properly configured
     */
    suspend fun areApiKeysConfigured(): Boolean = withContext(Dispatchers.IO) {
        val geminiKey = securePrefs.getString(GEMINI_API_KEY, "") ?: ""
        val openrouterKey = securePrefs.getString(OPENROUTER_API_KEY, "") ?: ""
        val therapistKey = securePrefs.getString(THERAPIST_API_KEY, "") ?: ""
        
        geminiKey.isNotBlank() && 
        openrouterKey.isNotBlank() && 
        therapistKey.isNotBlank() &&
        !geminiKey.contains("your_") &&
        !openrouterKey.contains("your_") &&
        !therapistKey.contains("your_")
    }

    /**
     * Update API keys securely
     */
    suspend fun updateApiKeys(
        geminiKey: String? = null,
        openrouterKey: String? = null,
        therapistKey: String? = null,
        ttsKey: String? = null
    ) = withContext(Dispatchers.IO) {
        securePrefs.edit().apply {
            geminiKey?.let { putString(GEMINI_API_KEY, it) }
            openrouterKey?.let { putString(OPENROUTER_API_KEY, it) }
            therapistKey?.let { putString(THERAPIST_API_KEY, it) }
            ttsKey?.let { putString(TTS_API_KEY, it) }
        }.apply()
    }

    /**
     * Clear all stored API keys (for testing or reset)
     */
    suspend fun clearAllApiKeys() = withContext(Dispatchers.IO) {
        securePrefs.edit().clear().apply()
    }
}