package com.prody.prashant.domain.collaborative

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.prody.prashant.data.local.dao.CollaborativeMessageDao
import com.prody.prashant.notification.CollaborativeMessageReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Scheduler for collaborative message delivery and occasion reminders.
 * Manages AlarmManager scheduling for timely message delivery.
 */
@Singleton
class CollaborativeMessageScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: CollaborativeMessageDao,
    private val deliveryService: MessageDeliveryService
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val TAG = "CollabMessageScheduler"
        const val ACTION_DELIVER_MESSAGE = "com.prody.prashant.DELIVER_COLLABORATIVE_MESSAGE"
        const val ACTION_OCCASION_REMINDER = "com.prody.prashant.OCCASION_REMINDER"
        const val EXTRA_MESSAGE_ID = "message_id"
        const val EXTRA_OCCASION_ID = "occasion_id"
        const val EXTRA_CONTACT_ID = "contact_id"
        const val EXTRA_OCCASION_NAME = "occasion_name"
        const val EXTRA_DAYS_UNTIL = "days_until"
    }

    /**
     * Schedule delivery for a collaborative message
     */
    fun scheduleMessageDelivery(message: CollaborativeMessage) {
        val deliveryTime = message.deliveryDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val now = System.currentTimeMillis()

        if (deliveryTime <= now) {
            // Deliver immediately if time has passed
            scope.launch {
                deliveryService.deliverMessage(message)
            }
            return
        }

        val intent = Intent(context, CollaborativeMessageReceiver::class.java).apply {
            action = ACTION_DELIVER_MESSAGE
            putExtra(EXTRA_MESSAGE_ID, message.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            message.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                deliveryTime,
                pendingIntent
            )

            scope.launch {
                dao.updateMessageStatus(message.id, MessageStatus.SCHEDULED.name.lowercase())
            }

            com.prody.prashant.util.AppLogger.d(TAG, "Scheduled message ${message.id} for delivery at ${message.deliveryDate}")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to schedule message delivery", e)
        }
    }

    /**
     * Cancel scheduled delivery for a message
     */
    fun cancelMessageDelivery(messageId: String) {
        val intent = Intent(context, CollaborativeMessageReceiver::class.java).apply {
            action = ACTION_DELIVER_MESSAGE
            putExtra(EXTRA_MESSAGE_ID, messageId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            messageId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            com.prody.prashant.util.AppLogger.d(TAG, "Canceled scheduled delivery for message $messageId")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to cancel message delivery", e)
        }
    }

    /**
     * Reschedule message delivery (e.g., after editing)
     */
    fun rescheduleMessageDelivery(message: CollaborativeMessage) {
        cancelMessageDelivery(message.id)
        scheduleMessageDelivery(message)
    }

    /**
     * Schedule occasion reminder
     */
    fun scheduleOccasionReminder(
        occasion: MessageOccasion,
        contact: MessageContact
    ) {
        val occasionDate = occasion.date
        val reminderDate = occasionDate.minusDays(occasion.reminderDaysBefore.toLong())
        val reminderTime = reminderDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val now = System.currentTimeMillis()

        if (reminderTime <= now) {
            // Already passed, schedule for next year if recurring
            if (occasion.isRecurring) {
                val nextYearReminder = reminderDate.plusYears(1)
                scheduleOccasionReminderAtTime(occasion, contact, nextYearReminder)
            }
            return
        }

        scheduleOccasionReminderAtTime(occasion, contact, reminderDate)
    }

    private fun scheduleOccasionReminderAtTime(
        occasion: MessageOccasion,
        contact: MessageContact,
        reminderDateTime: LocalDateTime
    ) {
        val reminderTime = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(context, CollaborativeMessageReceiver::class.java).apply {
            action = ACTION_OCCASION_REMINDER
            putExtra(EXTRA_OCCASION_ID, occasion.id)
            putExtra(EXTRA_CONTACT_ID, contact.id)
            putExtra(EXTRA_OCCASION_NAME, occasion.occasionType.displayName)
            putExtra(EXTRA_DAYS_UNTIL, occasion.reminderDaysBefore)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            occasion.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )

            com.prody.prashant.util.AppLogger.d(TAG, "Scheduled occasion reminder for ${occasion.occasionType.displayName} at $reminderDateTime")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to schedule occasion reminder", e)
        }
    }

    /**
     * Cancel occasion reminder
     */
    fun cancelOccasionReminder(occasionId: String) {
        val intent = Intent(context, CollaborativeMessageReceiver::class.java).apply {
            action = ACTION_OCCASION_REMINDER
            putExtra(EXTRA_OCCASION_ID, occasionId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            occasionId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            com.prody.prashant.util.AppLogger.d(TAG, "Canceled occasion reminder $occasionId")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to cancel occasion reminder", e)
        }
    }

    /**
     * Reschedule all pending messages (called on boot or app update)
     */
    suspend fun rescheduleAllPendingMessages() {
        try {
            val now = System.currentTimeMillis()
            val scheduledMessages = dao.getMessagesReadyForDelivery(Long.MAX_VALUE)

            scheduledMessages.forEach { entity ->
                val message = CollaborativeMessage.fromEntity(entity)
                if (entity.deliveryDate > now && entity.status == "scheduled") {
                    scheduleMessageDelivery(message)
                }
            }

            com.prody.prashant.util.AppLogger.d(TAG, "Rescheduled ${scheduledMessages.size} pending messages")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to reschedule pending messages", e)
        }
    }

    /**
     * Reschedule all occasion reminders
     */
    suspend fun rescheduleAllOccasionReminders() {
        try {
            val occasions = dao.getRecurringOccasions()
            val currentYear = LocalDate.now().year

            occasions.forEach { occasionEntity ->
                val occasion = MessageOccasion.fromEntity(occasionEntity)

                // Only schedule if not already notified this year
                if (occasion.lastNotifiedYear != currentYear) {
                    val contactEntity = dao.getContactById(occasion.contactId)
                    if (contactEntity != null) {
                        val contact = MessageContact.fromEntity(contactEntity)
                        scheduleOccasionReminder(occasion, contact)
                    }
                }
            }

            com.prody.prashant.util.AppLogger.d(TAG, "Rescheduled ${occasions.size} occasion reminders")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to reschedule occasion reminders", e)
        }
    }

    /**
     * Check for and deliver any overdue messages
     */
    suspend fun deliverOverdueMessages() {
        try {
            val now = System.currentTimeMillis()
            val overdueMessages = dao.getMessagesReadyForDelivery(now)

            overdueMessages.forEach { entity ->
                val message = CollaborativeMessage.fromEntity(entity)
                deliveryService.deliverMessage(message)
            }

            if (overdueMessages.isNotEmpty()) {
                com.prody.prashant.util.AppLogger.d(TAG, "Delivered ${overdueMessages.size} overdue messages")
            }
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to deliver overdue messages", e)
        }
    }

    /**
     * Get days until occasion
     */
    fun getDaysUntilOccasion(occasionDate: LocalDateTime): Long {
        val now = LocalDateTime.now()
        return ChronoUnit.DAYS.between(now, occasionDate)
    }

    /**
     * Check if occasion is coming up (within reminder window)
     */
    fun isOccasionUpcoming(occasion: MessageOccasion): Boolean {
        val daysUntil = getDaysUntilOccasion(occasion.date)
        return daysUntil in 0..occasion.reminderDaysBefore.toLong()
    }
}
