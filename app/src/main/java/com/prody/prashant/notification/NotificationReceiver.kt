package com.prody.prashant.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.prody.prashant.MainActivity
import com.prody.prashant.R
import com.prody.prashant.util.NotificationMessages
import kotlin.random.Random

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID_WISDOM = "prody_wisdom_channel"
        const val CHANNEL_ID_FUTURE = "prody_future_channel"
        const val CHANNEL_ID_REMINDER = "prody_reminder_channel"

        const val ACTION_MORNING_WISDOM = "com.prody.prashant.MORNING_WISDOM"
        const val ACTION_EVENING_REFLECTION = "com.prody.prashant.EVENING_REFLECTION"
        const val ACTION_WORD_OF_DAY = "com.prody.prashant.WORD_OF_DAY"
        const val ACTION_FUTURE_MESSAGE = "com.prody.prashant.FUTURE_MESSAGE"
        const val ACTION_STREAK_REMINDER = "com.prody.prashant.STREAK_REMINDER"
        const val ACTION_JOURNAL_REMINDER = "com.prody.prashant.JOURNAL_REMINDER"

        const val EXTRA_MESSAGE_TITLE = "message_title"
        const val EXTRA_MESSAGE_BODY = "message_body"

        private const val NOTIFICATION_ID_MORNING = 1001
        private const val NOTIFICATION_ID_EVENING = 1002
        private const val NOTIFICATION_ID_WORD = 1003
        private const val NOTIFICATION_ID_FUTURE = 1004
        private const val NOTIFICATION_ID_STREAK = 1005
        private const val NOTIFICATION_ID_JOURNAL = 1006

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(NotificationManager::class.java)

                // Wisdom Channel
                val wisdomChannel = NotificationChannel(
                    CHANNEL_ID_WISDOM,
                    "Daily Wisdom",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Daily wisdom, vocabulary, and inspiration"
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(wisdomChannel)

                // Future Messages Channel
                val futureChannel = NotificationChannel(
                    CHANNEL_ID_FUTURE,
                    "Messages from Past You",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Messages you wrote to your future self"
                    enableVibration(true)
                    enableLights(true)
                }
                notificationManager.createNotificationChannel(futureChannel)

                // Reminders Channel
                val reminderChannel = NotificationChannel(
                    CHANNEL_ID_REMINDER,
                    "Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Streak and journal reminders"
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(reminderChannel)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_MORNING_WISDOM -> showMorningWisdomNotification(context)
            ACTION_EVENING_REFLECTION -> showEveningReflectionNotification(context)
            ACTION_WORD_OF_DAY -> showWordOfDayNotification(context)
            ACTION_FUTURE_MESSAGE -> showFutureMessageNotification(
                context,
                intent.getStringExtra(EXTRA_MESSAGE_TITLE) ?: "A message from past you",
                intent.getStringExtra(EXTRA_MESSAGE_BODY) ?: "You have a message waiting"
            )
            ACTION_STREAK_REMINDER -> showStreakReminderNotification(context)
            ACTION_JOURNAL_REMINDER -> showJournalReminderNotification(context)
        }
    }

    private fun showMorningWisdomNotification(context: Context) {
        val (title, body, _) = NotificationMessages.morningWisdom.random()
        showNotification(
            context = context,
            channelId = CHANNEL_ID_WISDOM,
            notificationId = NOTIFICATION_ID_MORNING,
            title = title,
            body = body,
            smallIcon = R.drawable.ic_launcher_foreground
        )
    }

    private fun showEveningReflectionNotification(context: Context) {
        val notification = NotificationMessages.eveningReflection.random()
        val title = notification.first
        val body = notification.second
        showNotification(
            context = context,
            channelId = CHANNEL_ID_WISDOM,
            notificationId = NOTIFICATION_ID_EVENING,
            title = title,
            body = body,
            smallIcon = R.drawable.ic_launcher_foreground
        )
    }

    private fun showWordOfDayNotification(context: Context) {
        val notification = NotificationMessages.wordOfDay.random()
        val title = notification.first
        val body = notification.second
        showNotification(
            context = context,
            channelId = CHANNEL_ID_WISDOM,
            notificationId = NOTIFICATION_ID_WORD,
            title = title,
            body = body,
            smallIcon = R.drawable.ic_launcher_foreground
        )
    }

    private fun showFutureMessageNotification(context: Context, title: String, body: String) {
        val messageNotification = NotificationMessages.futureMessageReceived.random()
        val messageTitle = messageNotification.first
        showNotification(
            context = context,
            channelId = CHANNEL_ID_FUTURE,
            notificationId = NOTIFICATION_ID_FUTURE + Random.nextInt(1000),
            title = messageTitle,
            body = body,
            smallIcon = R.drawable.ic_launcher_foreground,
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    private fun showStreakReminderNotification(context: Context) {
        val notification = NotificationMessages.streakReminder.random()
        val title = notification.first
        val body = notification.second
        showNotification(
            context = context,
            channelId = CHANNEL_ID_REMINDER,
            notificationId = NOTIFICATION_ID_STREAK,
            title = title,
            body = body,
            smallIcon = R.drawable.ic_launcher_foreground
        )
    }

    private fun showJournalReminderNotification(context: Context) {
        val notification = NotificationMessages.journalPrompt.random()
        val title = notification.first
        val body = notification.second
        showNotification(
            context = context,
            channelId = CHANNEL_ID_REMINDER,
            notificationId = NOTIFICATION_ID_JOURNAL,
            title = title,
            body = body,
            smallIcon = R.drawable.ic_launcher_foreground
        )
    }

    private fun showNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        body: String,
        smallIcon: Int,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        // Check permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
