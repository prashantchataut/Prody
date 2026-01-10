package com.prody.prashant.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.prody.prashant.MainActivity
import com.prody.prashant.R
import com.prody.prashant.domain.collaborative.CollaborativeMessage
import com.prody.prashant.domain.collaborative.ReceivedCollaborativeMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles notification display for collaborative messages
 */
@Singleton
class CollaborativeMessageNotifications @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID_RECEIVED = "collaborative_messages_received"
        private const val CHANNEL_ID_DELIVERED = "collaborative_messages_delivered"
        private const val CHANNEL_ID_OCCASIONS = "collaborative_occasions"

        private const val NOTIFICATION_ID_RECEIVED_BASE = 5000
        private const val NOTIFICATION_ID_DELIVERED_BASE = 6000
        private const val NOTIFICATION_ID_OCCASION_BASE = 7000
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Received messages channel
            val receivedChannel = NotificationChannel(
                CHANNEL_ID_RECEIVED,
                "Received Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when you receive a collaborative message"
                enableVibration(true)
                enableLights(true)
            }

            // Delivered messages channel
            val deliveredChannel = NotificationChannel(
                CHANNEL_ID_DELIVERED,
                "Message Delivery",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications when your messages are delivered"
            }

            // Occasions channel
            val occasionsChannel = NotificationChannel(
                CHANNEL_ID_OCCASIONS,
                "Occasion Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming occasions"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(receivedChannel)
            notificationManager.createNotificationChannel(deliveredChannel)
            notificationManager.createNotificationChannel(occasionsChannel)
        }
    }

    /**
     * Notify user they received a new collaborative message
     */
    fun notifyNewMessageReceived(message: ReceivedCollaborativeMessage) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "received_message")
            putExtra("message_id", message.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            message.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_RECEIVED)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("💝 New Message from ${message.sender.name}")
            .setContentText(message.title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${message.title}\n\n${message.content.take(100)}${if (message.content.length > 100) "..." else ""}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Add occasion emoji if present
        if (message.occasion != null) {
            notificationBuilder.setContentTitle("${message.occasion.emoji} New ${message.occasion.displayName} Message from ${message.sender.name}")
        }

        notificationManager.notify(
            NOTIFICATION_ID_RECEIVED_BASE + message.id.hashCode(),
            notificationBuilder.build()
        )
    }

    /**
     * Notify user their message was delivered
     */
    fun notifyMessageDelivered(message: CollaborativeMessage) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "collaborative_messages")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            message.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_DELIVERED)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Message Delivered")
            .setContentText("Your message to ${message.recipient.name} has been delivered")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(
            NOTIFICATION_ID_DELIVERED_BASE + message.id.hashCode(),
            notificationBuilder.build()
        )
    }

    /**
     * Notify user about upcoming occasion
     */
    fun notifyOccasionReminder(
        contactName: String,
        occasionName: String,
        daysUntil: Int,
        occasionId: String,
        contactId: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "compose_collaborative_message")
            putExtra("contact_id", contactId)
            putExtra("occasion_id", occasionId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            occasionId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val daysText = when (daysUntil) {
            0 -> "today"
            1 -> "tomorrow"
            else -> "in $daysUntil days"
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_OCCASIONS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Upcoming: $contactName's $occasionName")
            .setContentText("$contactName's $occasionName is $daysText. Send them a message?")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$contactName's $occasionName is coming up $daysText!\n\nTap to compose a heartfelt message to make their day special."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_notification,
                "Write Message",
                pendingIntent
            )

        notificationManager.notify(
            NOTIFICATION_ID_OCCASION_BASE + occasionId.hashCode(),
            notificationBuilder.build()
        )
    }

    /**
     * Notify about message delivery failure
     */
    fun notifyDeliveryFailed(message: CollaborativeMessage, reason: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "collaborative_message_detail")
            putExtra("message_id", message.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            message.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_DELIVERED)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Message Delivery Failed")
            .setContentText("Could not deliver message to ${message.recipient.name}")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Failed to deliver your message to ${message.recipient.name}.\n\nReason: $reason\n\nTap to retry."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(
            NOTIFICATION_ID_DELIVERED_BASE + message.id.hashCode(),
            notificationBuilder.build()
        )
    }

    /**
     * Cancel all collaborative message notifications
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    /**
     * Cancel notification for specific message
     */
    fun cancelMessageNotification(messageId: String) {
        notificationManager.cancel(NOTIFICATION_ID_RECEIVED_BASE + messageId.hashCode())
    }
}
