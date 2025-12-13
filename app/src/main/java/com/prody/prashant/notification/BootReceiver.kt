package com.prody.prashant.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BootReceiverEntryPoint {
        fun notificationScheduler(): NotificationScheduler
    }

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            try {
                // Get NotificationScheduler from Hilt using EntryPointAccessors
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    BootReceiverEntryPoint::class.java
                )
                val notificationScheduler = entryPoint.notificationScheduler()

                // Reschedule all notifications after boot
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        notificationScheduler.rescheduleAllNotifications()
                        android.util.Log.d(TAG, "Notifications rescheduled after boot/update")
                    } catch (e: Exception) {
                        android.util.Log.e(TAG, "Failed to reschedule notifications", e)
                    }
                }
            } catch (e: Exception) {
                // Hilt might not be initialized yet or other initialization issues
                android.util.Log.e(TAG, "Failed to get NotificationScheduler from Hilt", e)
            }
        }
    }
}
