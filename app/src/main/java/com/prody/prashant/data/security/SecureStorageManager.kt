package com.prody.prashant.data.security

import android.util.Base64
import com.prody.prashant.data.local.preferences.PreferencesManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorageManager @Inject constructor(
    private val keystoreManager: KeystoreManager,
    private val preferencesManager: PreferencesManager
) {

    suspend fun saveApiKey(apiKey: String) {
        val encryptedData = keystoreManager.encrypt(apiKey)
        val encryptedApiKey = Base64.encodeToString(encryptedData.ciphertext, Base64.DEFAULT)
        val iv = Base64.encodeToString(encryptedData.iv, Base64.DEFAULT)
        preferencesManager.setEncryptedApiKey(encryptedApiKey)
        preferencesManager.setEncryptionIv(iv)
    }

    suspend fun getApiKey(): String? {
        val encryptedApiKey = preferencesManager.encryptedApiKey.first()
        val iv = preferencesManager.encryptionIv.first()
        if (encryptedApiKey.isEmpty() || iv.isEmpty()) {
            return null
        }
        val encryptedData = EncryptedData(
            Base64.decode(encryptedApiKey, Base64.DEFAULT),
            Base64.decode(iv, Base64.DEFAULT)
        )
        return keystoreManager.decrypt(encryptedData)
    }
}
