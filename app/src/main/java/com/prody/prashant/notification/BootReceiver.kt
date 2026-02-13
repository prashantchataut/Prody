package com.prody.prashant.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver that reschedules notifications after device boot or app update.
 *
 * This receiver handles:
 * - ACTION_BOOT_COMPLETED: Device finished booting
 * - ACTION_MY_PACKAGE_REPLACED: App was updated
 *
 * Notifications scheduled via AlarmManager are cleared on device reboot, so we need
 * to reschedule them when the device boots up or when the app is updated.
 */
class BootReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BootReceiverEntryPoint {
        fun notificationScheduler(): NotificationScheduler
    }

    companion object {
        private const val TAG = "BootReceiver"
    }

    // Coroutine exception handler to prevent silent failures
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        com.prody.prashant.util.AppLogger.e(TAG, "Coroutine exception during notification rescheduling", throwable)
    }

    // SupervisorJob ensures that if one notification fails, others can still be scheduled
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob() + exceptionHandler)

    override fun onReceive(context: Context, intent: Intent) {
        // Validate intent action
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) {
            com.prody.prashant.util.AppLogger.d(TAG, "Ignoring unhandled action: $action")
            return
        }

        com.prody.prashant.util.AppLogger.d(TAG, "Received action: $action")

        // Use goAsync() to get more time for background work in BroadcastReceiver
        val pendingResult = goAsync()

        try {
            // Get NotificationScheduler from Hilt using EntryPointAccessors
            // This can throw if Hilt is not initialized or the application context is invalid
            val applicationContext = context.applicationContext
            if (applicationContext == null) {
                com.prody.prashant.util.AppLogger.e(TAG, "Application context is null, cannot access Hilt")
                pendingResult.finish()
                return
            }

            val entryPoint = try {
                EntryPointAccessors.fromApplication(
                    applicationContext,
                    BootReceiverEntryPoint::class.java
                )
            } catch (e: IllegalStateException) {
                com.prody.prashant.util.AppLogger.e(TAG, "Hilt not initialized yet, skipping notification rescheduling", e)
                pendingResult.finish()
                return
            }

            val notificationScheduler = entryPoint.notificationScheduler()

            // Reschedule all notifications after boot/update
            scope.launch {
                try {
                    notificationScheduler.rescheduleAllNotifications()
                    com.prody.prashant.util.AppLogger.d(TAG, "Notifications rescheduled successfully after $action")
                } catch (e: Exception) {
                    com.prody.prashant.util.AppLogger.e(TAG, "Failed to reschedule notifications", e)
                } finally {
                    // Always finish the pending result to avoid ANR
                    pendingResult.finish()
                }
            }
        } catch (e: Exception) {
            // Catch-all for any unexpected errors during initialization
            com.prody.prashant.util.AppLogger.e(TAG, "Unexpected error in BootReceiver", e)
            pendingResult.finish()
        }
    }
}
