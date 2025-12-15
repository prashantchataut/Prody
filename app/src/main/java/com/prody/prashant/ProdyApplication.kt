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
import com.prody.prashant.domain.gamification.GamificationService
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ProdyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var gamificationService: GamificationService

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

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
     *
     * Order of Android Application lifecycle:
     * 1. Application constructor (class loading)
     * 2. attachBaseContext(base) - EARLIEST point we can safely run code
     * 3. onCreate() - Hilt injection happens here for @HiltAndroidApp
     * 4. Activity/Service/Receiver creation
     *
     * Crashes BEFORE attachBaseContext cannot be caught by any exception handler
     * because the handler cannot be installed before the Application exists.
     * This includes:
     * - Static initializers in any class
     * - Class loading failures (missing dependencies, dex issues)
     * - Application constructor failures
     *
     * Crashes AFTER attachBaseContext but BEFORE CrashHandler.initialize()
     * are covered by the try-catch around super.attachBaseContext(base).
     */
    override fun attachBaseContext(base: Context) {
        // Wrap super.attachBaseContext in try-catch as a safety measure
        // This catches any crashes in the base framework initialization
        try {
            super.attachBaseContext(base)
        } catch (e: Throwable) {
            // If super.attachBaseContext fails, we're in a very bad state
            // Log to system log and rethrow - there's nothing we can do
            Log.e(TAG, "CRITICAL: super.attachBaseContext failed", e)
            throw e
        }

        // ALWAYS initialize crash handler, regardless of process.
        // The CrashHandler itself has logic to prevent infinite loops (checks if in :crash process).
        // This MUST happen IMMEDIATELY after super.attachBaseContext() completes.
        try {
            CrashHandler.initialize(this)
            Log.d(TAG, "CrashHandler initialized in attachBaseContext")
        } catch (e: Exception) {
            // Failed to initialize crash handler - log but don't crash
            // The app will run without crash reporting, but at least it might start
            Log.e(TAG, "Failed to initialize crash handler in attachBaseContext", e)
        }
    }

    /**
     * Track if we're in the crash process for later checks.
     * Determined once during onCreate to avoid repeated process checks.
     */
    private var inCrashProcess = false

    override fun onCreate() {
        // Determine if we're in the crash process FIRST
        // We need to do this check carefully as isCrashProcess can potentially
        // have issues during early initialization
        inCrashProcess = try {
            CrashHandler.isCrashProcess(this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to determine crash process status, assuming main process", e)
            false
        }

        if (inCrashProcess) {
            Log.d(TAG, "Running in crash process, skipping Hilt injection and initialization")
            // CRITICAL: For the crash process, we skip Hilt initialization entirely.
            // CrashActivity is designed to work without any Hilt/DI dependencies.
            // We don't call super.onCreate() because:
            // 1. @HiltAndroidApp generates code in super.onCreate() that initializes Hilt
            // 2. The crash might have been caused by Hilt/DI - re-initializing would cause infinite loops
            // 3. CrashActivity only needs basic Android framework, not our DI graph
            //
            // Note: The base Application.onCreate() does minimal work (just sets mLoadedApk)
            // and Hilt's Hilt_ProdyApplication handles the Hilt setup we want to skip.
            // On modern Android, not calling super.onCreate() in a secondary process is safe.
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

        // Initialize gamification data (user profile, achievements) asynchronously
        applicationScope.launch {
            try {
                if (::gamificationService.isInitialized) {
                    gamificationService.initializeUserData()
                    gamificationService.checkAndResetDailyStats()
                    Log.d(TAG, "Gamification data initialized")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize gamification data", e)
            }
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
