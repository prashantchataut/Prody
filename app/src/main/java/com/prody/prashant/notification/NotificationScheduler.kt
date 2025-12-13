package com.prody.prashant.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.prody.prashant.data.local.dao.FutureMessageDao
import com.prody.prashant.data.local.preferences.PreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager,
    private val futureMessageDao: FutureMessageDao
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val REQUEST_MORNING = 100
        private const val REQUEST_EVENING = 101
        private const val REQUEST_WORD = 102
        private const val REQUEST_STREAK = 103
        private const val REQUEST_JOURNAL = 104
        private const val REQUEST_FUTURE_BASE = 1000
    }

    suspend fun rescheduleAllNotifications() {
        try {
            val notificationsEnabled = preferencesManager.notificationsEnabled.first()
            if (!notificationsEnabled) {
                cancelAllNotifications()
                return
            }

            scheduleMorningWisdom()
            scheduleEveningReflection()
            scheduleWordOfDay()
            scheduleStreakReminder()
            scheduleJournalReminder()
            scheduleFutureMessages()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scheduleMorningWisdom() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        scheduleRepeatingAlarm(
            action = NotificationReceiver.ACTION_MORNING_WISDOM,
            requestCode = REQUEST_MORNING,
            triggerTime = calendar.timeInMillis,
            intervalMillis = AlarmManager.INTERVAL_DAY
        )
    }

    fun scheduleEveningReflection() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        scheduleRepeatingAlarm(
            action = NotificationReceiver.ACTION_EVENING_REFLECTION,
            requestCode = REQUEST_EVENING,
            triggerTime = calendar.timeInMillis,
            intervalMillis = AlarmManager.INTERVAL_DAY
        )
    }

    fun scheduleWordOfDay() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        scheduleRepeatingAlarm(
            action = NotificationReceiver.ACTION_WORD_OF_DAY,
            requestCode = REQUEST_WORD,
            triggerTime = calendar.timeInMillis,
            intervalMillis = AlarmManager.INTERVAL_DAY
        )
    }

    fun scheduleStreakReminder() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        scheduleRepeatingAlarm(
            action = NotificationReceiver.ACTION_STREAK_REMINDER,
            requestCode = REQUEST_STREAK,
            triggerTime = calendar.timeInMillis,
            intervalMillis = AlarmManager.INTERVAL_DAY
        )
    }

    fun scheduleJournalReminder() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        scheduleRepeatingAlarm(
            action = NotificationReceiver.ACTION_JOURNAL_REMINDER,
            requestCode = REQUEST_JOURNAL,
            triggerTime = calendar.timeInMillis,
            intervalMillis = AlarmManager.INTERVAL_DAY
        )
    }

    suspend fun scheduleFutureMessages() {
        val pendingMessages = futureMessageDao.getAllMessagesSync()
        val currentTime = System.currentTimeMillis()

        pendingMessages.forEachIndexed { index, message ->
            if (message.deliveryDate > currentTime) {
                scheduleExactAlarm(
                    action = NotificationReceiver.ACTION_FUTURE_MESSAGE,
                    requestCode = REQUEST_FUTURE_BASE + index,
                    triggerTime = message.deliveryDate,
                    extras = mapOf(
                        NotificationReceiver.EXTRA_MESSAGE_TITLE to message.title,
                        NotificationReceiver.EXTRA_MESSAGE_BODY to message.content.take(200)
                    )
                )
            }
        }
    }

    fun scheduleFutureMessage(messageId: Long, deliveryDate: Long, title: String, content: String) {
        if (deliveryDate <= System.currentTimeMillis()) return

        scheduleExactAlarm(
            action = NotificationReceiver.ACTION_FUTURE_MESSAGE,
            requestCode = (REQUEST_FUTURE_BASE + messageId).toInt(),
            triggerTime = deliveryDate,
            extras = mapOf(
                NotificationReceiver.EXTRA_MESSAGE_TITLE to title,
                NotificationReceiver.EXTRA_MESSAGE_BODY to content.take(200)
            )
        )
    }

    fun cancelFutureMessageNotification(messageId: Long) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_FUTURE_MESSAGE
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (REQUEST_FUTURE_BASE + messageId).toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    private fun scheduleRepeatingAlarm(
        action: String,
        requestCode: Int,
        triggerTime: Long,
        intervalMillis: Long
    ) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            intervalMillis,
            pendingIntent
        )
    }

    private fun scheduleExactAlarm(
        action: String,
        requestCode: Int,
        triggerTime: Long,
        extras: Map<String, String> = emptyMap()
    ) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            this.action = action
            extras.forEach { (key, value) ->
                putExtra(key, value)
            }
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun cancelAllNotifications() {
        listOf(
            REQUEST_MORNING to NotificationReceiver.ACTION_MORNING_WISDOM,
            REQUEST_EVENING to NotificationReceiver.ACTION_EVENING_REFLECTION,
            REQUEST_WORD to NotificationReceiver.ACTION_WORD_OF_DAY,
            REQUEST_STREAK to NotificationReceiver.ACTION_STREAK_REMINDER,
            REQUEST_JOURNAL to NotificationReceiver.ACTION_JOURNAL_REMINDER
        ).forEach { (requestCode, action) ->
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                this.action = action
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }
    }
}
