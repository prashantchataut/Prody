package com.prody.prashant

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.prody.prashant.debug.CrashHandler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ProdyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = try {
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
                .build()
        } catch (e: Exception) {
            // Fallback configuration if workerFactory is not initialized
            Log.e(TAG, "WorkerFactory not ready, using default configuration", e)
            Configuration.Builder()
                .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
                .build()
        }

    /**
     * CRITICAL: Initialize crash handler at the earliest possible point.
     * attachBaseContext() is called BEFORE onCreate() and BEFORE Hilt injection.
     * This ensures we catch ALL crashes, including those during Hilt setup.
     */
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        // ALWAYS initialize crash handler, regardless of process.
        // The CrashHandler itself now has logic to prevent infinite loops (checks if in :crash process).
        try {
            CrashHandler.initialize(this)
            Log.d(TAG, "CrashHandler initialized in attachBaseContext")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize crash handler in attachBaseContext", e)
        }
    }

    override fun onCreate() {
        // Check for crash process to skip unnecessary heavy initialization
        if (CrashHandler.isCrashProcess(this)) {
            Log.d(TAG, "Running in crash process, skipping Hilt injection and initialization")
            return
        }

        // CRITICAL: Wrap super.onCreate() in try-catch.
        // Triggering Hilt injection (which happens in super.onCreate()) is a common source of startup crashes.
        try {
            super.onCreate()
        } catch (e: Throwable) {
            Log.e(TAG, "CRITICAL ERROR IN APPLICATION ONCREATE / HILT INIT", e)
            
            // Invoke uncaught exception handler directly to ensure we show the crash screen
            val handler = Thread.getDefaultUncaughtExceptionHandler()
            if (handler != null) {
                handler.uncaughtException(Thread.currentThread(), e)
            } else {
                // Fallback if somehow handler was lost
                CrashHandler.initialize(this)
                Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(Thread.currentThread(), e)
            }
            return
        }

        // Initialize other components safely
        initializeApp()
    }

    private fun initializeApp() {
        try {
            createNotificationChannels()
        } catch (e: Exception) {
            // Log error but don't crash the app - notifications are not critical for launch
            Log.e(TAG, "Failed to create notification channels", e)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Safely obtain NotificationManager - can be null on some custom ROMs
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (notificationManager == null) {
                Log.w(TAG, "NotificationManager not available, skipping channel creation")
                return
            }

            // Main notification channel
            val mainChannel = NotificationChannel(
                CHANNEL_MAIN,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.notification_channel_desc)
                enableVibration(true)
                setShowBadge(true)
            }

            // Daily wisdom channel
            val wisdomChannel = NotificationChannel(
                CHANNEL_WISDOM,
                "Daily Wisdom",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily vocabulary, quotes, and inspiration"
                enableVibration(true)
            }

            // Journal reminders channel
            val journalChannel = NotificationChannel(
                CHANNEL_JOURNAL,
                "Journal Reminders",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Gentle reminders to reflect and journal"
            }

            // Future messages channel
            val futureChannel = NotificationChannel(
                CHANNEL_FUTURE,
                "Messages from Past You",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Special messages from your past self"
                enableVibration(true)
                setShowBadge(true)
            }

            // Achievements channel
            val achievementsChannel = NotificationChannel(
                CHANNEL_ACHIEVEMENTS,
                "Achievements",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Celebrate your accomplishments"
                enableVibration(true)
            }

            try {
                notificationManager.createNotificationChannels(
                    listOf(mainChannel, wisdomChannel, journalChannel, futureChannel, achievementsChannel)
                )
                Log.d(TAG, "Notification channels created successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create notification channels", e)
            }
        }
    }

    companion object {
        private const val TAG = "ProdyApplication"
        const val CHANNEL_MAIN = "prody_main"
        const val CHANNEL_WISDOM = "prody_wisdom"
        const val CHANNEL_JOURNAL = "prody_journal"
        const val CHANNEL_FUTURE = "prody_future"
        const val CHANNEL_ACHIEVEMENTS = "prody_achievements"
    }
}
