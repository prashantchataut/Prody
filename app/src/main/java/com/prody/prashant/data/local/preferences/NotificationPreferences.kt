package com.prody.prashant.data.local.preferences

import androidx.datastore.preferences.core.Preferences.Key
import kotlinx.coroutines.flow.Flow

internal class NotificationPreferences(
    private val support: PreferenceFacadeSupport,
    private val notificationsEnabledKey: Key<Boolean>,
    private val dailyReminderHourKey: Key<Int>,
    private val dailyReminderMinuteKey: Key<Int>,
    private val wisdomNotificationEnabledKey: Key<Boolean>,
    private val journalReminderEnabledKey: Key<Boolean>,
    private val eveningReminderHourKey: Key<Int>,
    private val eveningReminderMinuteKey: Key<Int>
) {
    val notificationsEnabled: Flow<Boolean> = support.booleanFlow(notificationsEnabledKey, true)
    val dailyReminderHour: Flow<Int> = support.intFlow(dailyReminderHourKey, 9)
    val dailyReminderMinute: Flow<Int> = support.intFlow(dailyReminderMinuteKey, 0)
    val wisdomNotificationEnabled: Flow<Boolean> = support.booleanFlow(wisdomNotificationEnabledKey, true)
    val journalReminderEnabled: Flow<Boolean> = support.booleanFlow(journalReminderEnabledKey, true)
    val eveningReminderHour: Flow<Int> = support.intFlow(eveningReminderHourKey, 20)
    val eveningReminderMinute: Flow<Int> = support.intFlow(eveningReminderMinuteKey, 0)

    suspend fun setNotificationsEnabled(enabled: Boolean) = support.setBoolean(notificationsEnabledKey, enabled)

    suspend fun setDailyReminderTime(hour: Int, minute: Int) = support.setInts(
        kotlin.Pair(dailyReminderHourKey, hour),
        kotlin.Pair(dailyReminderMinuteKey, minute)
    )

    suspend fun setWisdomNotificationEnabled(enabled: Boolean) = support.setBoolean(wisdomNotificationEnabledKey, enabled)
    suspend fun setJournalReminderEnabled(enabled: Boolean) = support.setBoolean(journalReminderEnabledKey, enabled)

    suspend fun setEveningReminderTime(hour: Int, minute: Int) = support.setInts(
        kotlin.Pair(eveningReminderHourKey, hour),
        kotlin.Pair(eveningReminderMinuteKey, minute)
    )
}
