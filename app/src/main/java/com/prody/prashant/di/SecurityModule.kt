package com.prody.prashant.di

import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.data.security.KeystoreManager
import com.prody.prashant.data.security.SecureStorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideKeystoreManager(): KeystoreManager {
        return KeystoreManager()
    }

    @Provides
    @Singleton
    fun provideSecureStorageManager(
        keystoreManager: KeystoreManager,
        preferencesManager: PreferencesManager
    ): SecureStorageManager {
        return SecureStorageManager(keystoreManager, preferencesManager)
    }
}
