package com.prody.prashant.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

internal class PreferenceFacadeSupport(
    private val dataStore: DataStore<Preferences>
) {
    fun booleanFlow(key: Preferences.Key<Boolean>, defaultValue: Boolean): Flow<Boolean> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { preferences -> preferences[key] ?: defaultValue }

    fun intFlow(key: Preferences.Key<Int>, defaultValue: Int): Flow<Int> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { preferences -> preferences[key] ?: defaultValue }

    fun longFlow(key: Preferences.Key<Long>, defaultValue: Long): Flow<Long> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { preferences -> preferences[key] ?: defaultValue }

    suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { preferences -> preferences[key] = value }
    }

    suspend fun setInt(key: Preferences.Key<Int>, value: Int) {
        dataStore.edit { preferences -> preferences[key] = value }
    }

    suspend fun setLong(key: Preferences.Key<Long>, value: Long) {
        dataStore.edit { preferences -> preferences[key] = value }
    }

    suspend fun setInts(vararg updates: Pair<Preferences.Key<Int>, Int>) {
        dataStore.edit { preferences -> updates.forEach { (key, value) -> preferences[key] = value } }
    }
}
