package com.prody.prashant.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.prody.prashant.domain.collaborative.CollaborativeMessageScheduler
import com.prody.prashant.domain.collaborative.MessageDeliveryService
import com.prody.prashant.domain.repository.CollaborativeMessageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Broadcast receiver for collaborative message delivery and occasion reminders
 */
@AndroidEntryPoint
class CollaborativeMessageReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: CollaborativeMessageRepository
        internal set

    @Inject
    lateinit var deliveryService: MessageDeliveryService
        internal set

    @Inject
    lateinit var notifications: CollaborativeMessageNotifications
        internal set

    @Inject
    lateinit var scheduler: CollaborativeMessageScheduler
        internal set

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val TAG = "CollabMessageReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received broadcast: ${intent.action}")

        when (intent.action) {
            CollaborativeMessageScheduler.ACTION_DELIVER_MESSAGE -> {
                handleMessageDelivery(intent)
            }
            CollaborativeMessageScheduler.ACTION_OCCASION_REMINDER -> {
                handleOccasionReminder(intent)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                handleBootCompleted()
            }
        }
    }

    private fun handleMessageDelivery(intent: Intent) {
        val messageId = intent.getStringExtra(CollaborativeMessageScheduler.EXTRA_MESSAGE_ID)
        if (messageId == null) {
            Log.e(TAG, "Message ID not found in intent")
            return
        }

        scope.launch {
            try {
                Log.d(TAG, "Delivering message: $messageId")
                repository.deliverMessage(messageId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to deliver message $messageId", e)
            }
        }
    }

    private fun handleOccasionReminder(intent: Intent) {
        val occasionId = intent.getStringExtra(CollaborativeMessageScheduler.EXTRA_OCCASION_ID)
        val contactId = intent.getStringExtra(CollaborativeMessageScheduler.EXTRA_CONTACT_ID)
        val occasionName = intent.getStringExtra(CollaborativeMessageScheduler.EXTRA_OCCASION_NAME)
        val daysUntil = intent.getIntExtra(CollaborativeMessageScheduler.EXTRA_DAYS_UNTIL, 7)

        if (occasionId == null || contactId == null || occasionName == null) {
            Log.e(TAG, "Missing occasion reminder data in intent")
            return
        }

        scope.launch {
            try {
                Log.d(TAG, "Showing occasion reminder: $occasionName in $daysUntil days")

                // Get contact details
                val contactResult = repository.getContactById(contactId)
                if (contactResult is com.prody.prashant.domain.common.Result.Success) {
                    val contact = contactResult.data
                    notifications.notifyOccasionReminder(
                        contactName = contact.displayName,
                        occasionName = occasionName,
                        daysUntil = daysUntil,
                        occasionId = occasionId,
                        contactId = contactId
                    )

                    // Update last notified year
                    val occasionResult = repository.getOccasionById(occasionId)
                    if (occasionResult is com.prody.prashant.domain.common.Result.Success) {
                        val occasion = occasionResult.data
                        val currentYear = java.time.LocalDate.now().year
                        val updatedOccasion = occasion.copy(lastNotifiedYear = currentYear)
                        repository.updateOccasion(updatedOccasion)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to show occasion reminder", e)
            }
        }
    }

    private fun handleBootCompleted() {
        scope.launch {
            try {
                Log.d(TAG, "Rescheduling all pending messages and reminders after boot")
                scheduler.rescheduleAllPendingMessages()
                scheduler.rescheduleAllOccasionReminders()
                scheduler.deliverOverdueMessages()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reschedule after boot", e)
            }
        }
    }
}
