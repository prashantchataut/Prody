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
        private const val TAG = "NotificationReceiver"

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

        // Default fallback messages for when lists are empty (defensive programming)
        private val DEFAULT_WISDOM = Triple("Daily Wisdom", "Take a moment to reflect on your journey today.", "Read more")
        private val DEFAULT_EVENING = Triple("Evening Reflection", "How did today shape you?", "Reflect")
        private val DEFAULT_WORD = Triple("Word of the Day", "Expand your vocabulary today.", "Learn")
        private val DEFAULT_STREAK = Triple("Keep Going!", "Your consistency is building something great.", "Continue")
        private val DEFAULT_JOURNAL = Triple("Journal Time", "Capture your thoughts for today.", "Write")
        private val DEFAULT_FUTURE_MESSAGE = Triple("Message from the Past", "Your past self has something to share.", "Read")

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Safely obtain NotificationManager - can return null on some devices/custom ROMs
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                if (notificationManager == null) {
                    android.util.Log.w(TAG, "NotificationManager not available, skipping channel creation")
                    return
                }

                try {
                    // Wisdom Channel
                    val wisdomChannel = NotificationChannel(
                        CHANNEL_ID_WISDOM,
                        "Daily Wisdom",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        description = "Daily wisdom, vocabulary, and inspiration"
                        enableVibration(true)
                    }

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

                    // Reminders Channel
                    val reminderChannel = NotificationChannel(
                        CHANNEL_ID_REMINDER,
                        "Reminders",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        description = "Streak and journal reminders"
                        enableVibration(true)
                    }

                    notificationManager.createNotificationChannels(
                        listOf(wisdomChannel, futureChannel, reminderChannel)
                    )
                    android.util.Log.d(TAG, "Notification channels created successfully")
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Failed to create notification channels", e)
                }
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
        val (title, body, _) = NotificationMessages.morningWisdom.randomOrNull() ?: DEFAULT_WISDOM
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
        val (title, body, _) = NotificationMessages.eveningReflection.randomOrNull() ?: DEFAULT_EVENING
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
        val (title, body, _) = NotificationMessages.wordOfDay.randomOrNull() ?: DEFAULT_WORD
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
        val messageTitle = NotificationMessages.futureMessageReceived.randomOrNull()?.first
            ?: DEFAULT_FUTURE_MESSAGE.first
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
        val (title, body, _) = NotificationMessages.streakReminder.randomOrNull() ?: DEFAULT_STREAK
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
        val (title, body, _) = NotificationMessages.journalPrompt.randomOrNull() ?: DEFAULT_JOURNAL
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
