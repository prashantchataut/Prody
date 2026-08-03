package com.kairos.app.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.kairos.app.data.local.database.KairosDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.IOException
import java.security.GeneralSecurityException
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    private const val TAG = "StorageModule"
    private const val Kairos_SHARED_PREFS = "Kairos_shared_prefs"
    private const val Kairos_ENCRYPTED_SHARED_PREFS = "Kairos_encrypted_shared_prefs"
    private const val Kairos_ENCRYPTED_FALLBACK_PREFS = "Kairos_encrypted_shared_prefs_fallback"

    @Provides
    @Singleton
    @Named("UnencryptedSharedPreferences")
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Kairos_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named("EncryptedSharedPreferences")
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return createEncryptedSharedPreferences(context)
    }

    private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
        try {
            return buildEncryptedPrefs(context)
        } catch (e: GeneralSecurityException) {
            Log.w(TAG, "EncryptedSharedPreferences key error — attempting recovery by deleting corrupted prefs", e)
            return recoverEncryptedPreferences(context)
        } catch (e: IOException) {
            Log.w(TAG, "EncryptedSharedPreferences IO error — attempting recovery", e)
            return recoverEncryptedPreferences(context)
        } catch (e: Exception) {
            Log.w(TAG, "EncryptedSharedPreferences unexpected error — attempting recovery", e)
            return recoverEncryptedPreferences(context)
        }
    }

    private fun recoverEncryptedPreferences(context: Context): SharedPreferences {
        // Passphrase lives in these prefs; wipe DB files so a new passphrase cannot
        // brick an encrypted database that still uses the old key.
        deleteEncryptedDatabaseFiles(context)
        try {
            context.deleteSharedPreferences(Kairos_ENCRYPTED_SHARED_PREFS)
            Log.i(TAG, "Deleted corrupted encrypted preferences, recreating with fresh keys")
            return buildEncryptedPrefs(context)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "EncryptedSharedPreferences recovery failed — using private fallback prefs so the app can launch",
                e
            )
            return context.getSharedPreferences(Kairos_ENCRYPTED_FALLBACK_PREFS, Context.MODE_PRIVATE)
        }
    }

    private fun buildEncryptedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            Kairos_ENCRYPTED_SHARED_PREFS,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun deleteEncryptedDatabaseFiles(context: Context) {
        try {
            val deleted = context.deleteDatabase(KairosDatabase.DATABASE_NAME)
            Log.i(TAG, "Deleted encrypted database after prefs recovery: deleted=$deleted")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to delete encrypted database during prefs recovery", e)
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
