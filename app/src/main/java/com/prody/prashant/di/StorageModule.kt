package com.prody.prashant.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import android.util.Log
import java.security.GeneralSecurityException
import java.io.IOException
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    private const val PRODY_SHARED_PREFS = "prody_shared_prefs"
    private const val PRODY_ENCRYPTED_SHARED_PREFS = "prody_encrypted_shared_prefs"

    @Provides
    @Singleton
    @Named("UnencryptedSharedPreferences")
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PRODY_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named("EncryptedSharedPreferences")
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                context,
                PRODY_ENCRYPTED_SHARED_PREFS,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
} catch (e: GeneralSecurityException) {
            Log.e("StorageModule", "Could not create EncryptedSharedPreferences", e)
            throw RuntimeException("Could not create EncryptedSharedPreferences", e)
        } catch (e: IOException) {
            Log.e("StorageModule", "Could not create EncryptedSharedPreferences", e)
            throw RuntimeException("Could not create EncryptedSharedPreferences", e)
        }
    }

    private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(name = "sync_preferences")

    @Provides
    @Singleton
    @Named("SyncDataStore")
    fun provideSyncDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.syncDataStore
    }
}
