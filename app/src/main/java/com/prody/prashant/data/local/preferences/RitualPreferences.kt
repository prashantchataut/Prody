package com.prody.prashant.data.local.preferences

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

internal class RitualPreferences(
    private val support: PreferenceFacadeSupport,
    private val morningRitualEnabledKey: Preferences.Key<Boolean>,
    private val eveningRitualEnabledKey: Preferences.Key<Boolean>,
    private val morningRitualStartHourKey: Preferences.Key<Int>,
    private val morningRitualEndHourKey: Preferences.Key<Int>,
    private val eveningRitualStartHourKey: Preferences.Key<Int>,
    private val ritualReminderEnabledKey: Preferences.Key<Boolean>
) {
    val morningRitualEnabled: Flow<Boolean> = support.booleanFlow(morningRitualEnabledKey, true)
    val eveningRitualEnabled: Flow<Boolean> = support.booleanFlow(eveningRitualEnabledKey, true)
    val morningRitualStartHour: Flow<Int> = support.intFlow(morningRitualStartHourKey, 5)
    val morningRitualEndHour: Flow<Int> = support.intFlow(morningRitualEndHourKey, 12)
    val eveningRitualStartHour: Flow<Int> = support.intFlow(eveningRitualStartHourKey, 18)
    val ritualReminderEnabled: Flow<Boolean> = support.booleanFlow(ritualReminderEnabledKey, true)

    suspend fun setMorningRitualEnabled(enabled: Boolean) = support.setBoolean(morningRitualEnabledKey, enabled)
    suspend fun setEveningRitualEnabled(enabled: Boolean) = support.setBoolean(eveningRitualEnabledKey, enabled)
    suspend fun setMorningRitualStartHour(hour: Int) = support.setInt(morningRitualStartHourKey, hour)
    suspend fun setMorningRitualEndHour(hour: Int) = support.setInt(morningRitualEndHourKey, hour)
    suspend fun setEveningRitualStartHour(hour: Int) = support.setInt(eveningRitualStartHourKey, hour)
    suspend fun setRitualReminderEnabled(enabled: Boolean) = support.setBoolean(ritualReminderEnabledKey, enabled)
}
