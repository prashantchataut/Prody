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

    private const val TAG = "StorageModule"
    private const val PRODY_SHARED_PREFS = "prody_shared_prefs"
    private const val PRODY_ENCRYPTED_SHARED_PREFS = "prody_encrypted_shared_prefs"
    private const val PRODY_ENCRYPTED_SHARED_PREFS_LEGACY = "prody_encrypted_shared_prefs_legacy"

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
        return createEncryptedSharedPreferences(context)
    }

    private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
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
            Log.w(TAG, "EncryptedSharedPreferences key error — attempting recovery by deleting corrupted prefs", e)
            return recoverEncryptedPreferences(context)
        } catch (e: IOException) {
            Log.w(TAG, "EncryptedSharedPreferences IO error — attempting recovery", e)
            return recoverEncryptedPreferences(context)
        }
    }

    private fun recoverEncryptedPreferences(context: Context): SharedPreferences {
        try {
            context.deleteSharedPreferences(PRODY_ENCRYPTED_SHARED_PREFS)
            Log.i(TAG, "Deleted corrupted encrypted preferences, recreating with fresh keys")

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
        } catch (e: Exception) {
            Log.e(TAG, "CRITICAL: EncryptedSharedPreferences recovery failed, falling back to unencrypted", e)
            return context.getSharedPreferences(PRODY_ENCRYPTED_SHARED_PREFS_LEGACY, Context.MODE_PRIVATE)
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
