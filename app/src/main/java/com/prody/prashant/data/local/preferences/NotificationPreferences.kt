package com.prody.prashant.data.local.preferences

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

internal class NotificationPreferences(
    private val support: PreferenceFacadeSupport,
    private val notificationsEnabledKey: Preferences.Key<Boolean>,
    private val dailyReminderHourKey: Preferences.Key<Int>,
    private val dailyReminderMinuteKey: Preferences.Key<Int>,
    private val wisdomNotificationEnabledKey: Preferences.Key<Boolean>,
    private val journalReminderEnabledKey: Preferences.Key<Boolean>,
    private val eveningReminderHourKey: Preferences.Key<Int>,
    private val eveningReminderMinuteKey: Preferences.Key<Int>
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
        dailyReminderHourKey to hour,
        dailyReminderMinuteKey to minute
    )

    suspend fun setWisdomNotificationEnabled(enabled: Boolean) = support.setBoolean(wisdomNotificationEnabledKey, enabled)
    suspend fun setJournalReminderEnabled(enabled: Boolean) = support.setBoolean(journalReminderEnabledKey, enabled)

    suspend fun setEveningReminderTime(hour: Int, minute: Int) = support.setInts(
        eveningReminderHourKey to hour,
        eveningReminderMinuteKey to minute
    )
}
