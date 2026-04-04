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

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception during notification rescheduling", throwable)
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob() + exceptionHandler)

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) {
            return
        }

        val pendingResult = goAsync()

        try {
            val applicationContext = context.applicationContext
            if (applicationContext == null) {
                Log.e(TAG, "Application context is null, cannot access Hilt")
                pendingResult.finish()
                return
            }

            val entryPoint = try {
                EntryPointAccessors.fromApplication(
                    applicationContext,
                    BootReceiverEntryPoint::class.java
                )
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Hilt not initialized yet, skipping notification rescheduling", e)
                pendingResult.finish()
                return
            }

            val notificationScheduler = entryPoint.notificationScheduler()

            scope.launch {
                try {
                    notificationScheduler.rescheduleAllNotifications()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reschedule notifications", e)
                } finally {
                    pendingResult.finish()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in BootReceiver", e)
            pendingResult.finish()
        }
    }
}
