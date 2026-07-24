package com.kairos.app.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.kairos.app.MainActivity
import com.kairos.app.R
import com.kairos.app.util.NotificationMessages
import kotlin.random.Random

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject lateinit var deliveryGate: NotificationDeliveryGate
    @Inject lateinit var contentProvider: DailyNotificationContentProvider

    companion object {
        private const val TAG = "NotificationReceiver"

        // IMPORTANT: These channel IDs MUST match the ones created in KairosApplication
        // Channels are: Kairos_wisdom, Kairos_journal, Kairos_future, Kairos_achievements, Kairos_main
        const val CHANNEL_ID_WISDOM = "Kairos_wisdom"
        const val CHANNEL_ID_FUTURE = "Kairos_future"
        const val CHANNEL_ID_REMINDER = "Kairos_journal"  // Using journal channel for reminders

        const val ACTION_MORNING_WISDOM = "com.kairos.app.MORNING_WISDOM"
        const val ACTION_EVENING_REFLECTION = "com.kairos.app.EVENING_REFLECTION"
        const val ACTION_WORD_OF_DAY = "com.kairos.app.WORD_OF_DAY"
        const val ACTION_FUTURE_MESSAGE = "com.kairos.app.FUTURE_MESSAGE"
        const val ACTION_STREAK_REMINDER = "com.kairos.app.STREAK_REMINDER"
        const val ACTION_JOURNAL_REMINDER = "com.kairos.app.JOURNAL_REMINDER"
        const val ACTION_WEEKLY_SUMMARY = "com.kairos.app.WEEKLY_SUMMARY"

        const val EXTRA_MESSAGE_TITLE = "message_title"
        const val EXTRA_MESSAGE_BODY = "message_body"

        private const val NOTIFICATION_ID_MORNING = 1001
        private const val NOTIFICATION_ID_EVENING = 1002
        private const val NOTIFICATION_ID_WORD = 1003
        private const val NOTIFICATION_ID_FUTURE = 1004
        private const val NOTIFICATION_ID_STREAK = 1005
        private const val NOTIFICATION_ID_JOURNAL = 1006
        private const val NOTIFICATION_ID_WEEKLY_SUMMARY = 1007

        // Default fallback messages for when lists are empty (defensive programming)
        private val DEFAULT_WISDOM = Triple("Daily Wisdom", "Take a moment to reflect on your journey today.", "Read more")
        private val DEFAULT_EVENING = Triple("Evening Reflection", "How did today shape you?", "Reflect")
        private val DEFAULT_WORD = Triple("Word of the Day", "Expand your vocabulary today.", "Learn")
        private val DEFAULT_STREAK = Triple("Keep Going!", "Your consistency is building something great.", "Continue")
        private val DEFAULT_JOURNAL = Triple("Journal Time", "Capture your thoughts for today.", "Write")
        private val DEFAULT_FUTURE_MESSAGE = Triple("Message from the Past", "Your past self has something to share.", "Read")
        private val DEFAULT_WEEKLY_SUMMARY = Triple("Your Weekly Insights", "See how your week went — reflections, growth, and highlights.", "View")

        // NOTE: Notification channels are created in KairosApplication.kt
        // This ensures channels exist before any notifications are sent
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                if (!deliveryGate.shouldDeliver(intent.action)) return@launch
                val shown = when (intent.action) {
                    ACTION_MORNING_WISDOM -> showMorningWisdomNotification(
                        context, contentProvider.dailyMoment()
                    )
                    ACTION_EVENING_REFLECTION -> showEveningReflectionNotification(
                        context, contentProvider.eveningReflection()
                    )
                    ACTION_WORD_OF_DAY -> showWordOfDayNotification(context)
                    ACTION_FUTURE_MESSAGE -> showFutureMessageNotification(
                        context,
                        intent.getStringExtra(EXTRA_MESSAGE_TITLE) ?: "A message from past you",
                        intent.getStringExtra(EXTRA_MESSAGE_BODY) ?: "You have a message waiting"
                    )
                    ACTION_STREAK_REMINDER -> showStreakReminderNotification(context)
                    ACTION_JOURNAL_REMINDER -> showJournalReminderNotification(context)
                    ACTION_WEEKLY_SUMMARY -> showWeeklySummaryNotification(context)
                    else -> false
                }
                if (shown) deliveryGate.recordDelivered()
            } catch (error: Exception) {
                android.util.Log.e(TAG, "Notification delivery failed", error)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showMorningWisdomNotification(
        context: Context,
        copy: NotificationCopy
    ): Boolean {
        return showNotification(
            context = context,
            channelId = CHANNEL_ID_WISDOM,
            notificationId = NOTIFICATION_ID_MORNING,
            title = copy.title,
            body = copy.body,
            smallIcon = R.drawable.ic_launcher_foreground
        )
    }

    private fun showEveningReflectionNotification(
        context: Context,
        copy: NotificationCopy
    ): Boolean {
        return showNotification(
            context = context,
            channelId = CHANNEL_ID_WISDOM,
            notificationId = NOTIFICATION_ID_EVENING,
            title = copy.title,
            body = copy.body,
            smallIcon = R.drawable.ic_launcher_foreground
        )
    }

    private fun showWordOfDayNotification(context: Context): Boolean {
        val (title, body, _) = NotificationMessages.wordOfDay.randomOrNull() ?: DEFAULT_WORD
        return showNotification(
            context = context,
            channelId = CHANNEL_ID_WISDOM,
            notificationId = NOTIFICATION_ID_WORD,
            title = title,
            body = body,
            smallIcon = R.drawable.ic_launcher_foreground
        )
    }

    private fun showFutureMessageNotification(context: Context, title: String, body: String): Boolean {
        val messageTitle = title.ifBlank { DEFAULT_FUTURE_MESSAGE.first }
        return showNotification(
            context = context,
            channelId = CHANNEL_ID_FUTURE,
            notificationId = NOTIFICATION_ID_FUTURE + Random.nextInt(1000),
            title = messageTitle,
            body = body,
            smallIcon = R.drawable.ic_launcher_foreground,
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    private fun showStreakReminderNotification(context: Context): Boolean {
        val (title, body, _) = NotificationMessages.streakReminder.randomOrNull() ?: DEFAULT_STREAK
        return showNotification(
            context = context,
            channelId = CHANNEL_ID_REMINDER,
            notificationId = NOTIFICATION_ID_STREAK,
            title = title,
            body = body,
            smallIcon = R.drawable.ic_launcher_foreground
        )
    }

    private fun showJournalReminderNotification(context: Context): Boolean {
        val (title, body, _) = NotificationMessages.journalPrompt.randomOrNull() ?: DEFAULT_JOURNAL
        return showNotification(
            context = context,
            channelId = CHANNEL_ID_REMINDER,
            notificationId = NOTIFICATION_ID_JOURNAL,
            title = title,
            body = body,
            smallIcon = R.drawable.ic_launcher_foreground
        )
    }

    private fun showWeeklySummaryNotification(context: Context): Boolean {
        val (title, body, _) = DEFAULT_WEEKLY_SUMMARY
        return showNotification(
            context = context,
            channelId = CHANNEL_ID_REMINDER,
            notificationId = NOTIFICATION_ID_WEEKLY_SUMMARY,
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
    ): Boolean {
        // Check permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
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
        return true
    }
}
