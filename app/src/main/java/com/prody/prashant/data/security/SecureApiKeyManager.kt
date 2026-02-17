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
 * Stores only short-lived runtime credentials.
 *
 * Provisioning strategy:
 * 1) Fetch provider routes (remote config)
 * 2) Verify device integrity (attestation)
 * 3) Exchange attestation for short-lived provider tokens
 *
 * Long-lived vendor secrets must never be bundled in the app.
 */
@Singleton
class SecureApiKeyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val SECURE_PREFS_NAME = "secure_runtime_credentials"
        private const val GEMINI_TOKEN = "gemini_runtime_token"
        private const val OPENROUTER_TOKEN = "openrouter_runtime_token"
        private const val HAVEN_TOKEN = "haven_runtime_token"
        private const val TTS_TOKEN = "tts_runtime_token"

        private const val GEMINI_EXPIRY = "gemini_runtime_token_expiry"
        private const val OPENROUTER_EXPIRY = "openrouter_runtime_token_expiry"
        private const val HAVEN_EXPIRY = "haven_runtime_token_expiry"
        private const val TTS_EXPIRY = "tts_runtime_token_expiry"
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

    suspend fun storeRuntimeCredentials(credentials: RuntimeAiCredentials) = withContext(Dispatchers.IO) {
        securePrefs.edit().apply {
            saveToken(GEMINI_TOKEN, GEMINI_EXPIRY, credentials.gemini)
            saveToken(OPENROUTER_TOKEN, OPENROUTER_EXPIRY, credentials.openRouter)
            saveToken(HAVEN_TOKEN, HAVEN_EXPIRY, credentials.haven)
            saveToken(TTS_TOKEN, TTS_EXPIRY, credentials.tts)
        }.apply()
    }

    suspend fun getGeminiApiKey(): String = withContext(Dispatchers.IO) {
        getActiveToken(GEMINI_TOKEN, GEMINI_EXPIRY)
    }

    suspend fun getOpenRouterApiKey(): String = withContext(Dispatchers.IO) {
        getActiveToken(OPENROUTER_TOKEN, OPENROUTER_EXPIRY)
    }

    suspend fun getTherapistApiKey(): String = withContext(Dispatchers.IO) {
        getActiveToken(HAVEN_TOKEN, HAVEN_EXPIRY)
    }

    suspend fun getTtsApiKey(): String = withContext(Dispatchers.IO) {
        getActiveToken(TTS_TOKEN, TTS_EXPIRY)
    }

    suspend fun areApiKeysConfigured(): Boolean = withContext(Dispatchers.IO) {
        getGeminiApiKey().isNotBlank() || getOpenRouterApiKey().isNotBlank() || getTherapistApiKey().isNotBlank()
    }

    suspend fun clearAllApiKeys() = withContext(Dispatchers.IO) {
        securePrefs.edit().clear().apply()
    }

    private fun android.content.SharedPreferences.Editor.saveToken(
        tokenKey: String,
        expiryKey: String,
        token: EphemeralCredential?
    ) {
        if (token == null || token.value.isBlank()) {
            remove(tokenKey)
            remove(expiryKey)
        } else {
            putString(tokenKey, token.value)
            putLong(expiryKey, token.expiresAtEpochMs)
        }
    }

    private fun getActiveToken(tokenKey: String, expiryKey: String): String {
        val value = securePrefs.getString(tokenKey, "") ?: ""
        if (value.isBlank()) return ""

        val expiry = securePrefs.getLong(expiryKey, 0L)
        val now = System.currentTimeMillis()
        if (expiry in 1 until now) {
            securePrefs.edit().remove(tokenKey).remove(expiryKey).apply()
            return ""
        }
        return value
    }
}

data class RuntimeAiCredentials(
    val gemini: EphemeralCredential? = null,
    val openRouter: EphemeralCredential? = null,
    val haven: EphemeralCredential? = null,
    val tts: EphemeralCredential? = null
)

data class EphemeralCredential(
    val value: String,
    val expiresAtEpochMs: Long
)
