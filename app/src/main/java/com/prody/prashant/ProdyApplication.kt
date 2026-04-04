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
import com.prody.prashant.domain.haven.WitnessModeManager
import com.prody.prashant.BuildConfig
import com.prody.prashant.R
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

    @Inject
    lateinit var witnessModeManager: WitnessModeManager

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val workManagerConfiguration: Configuration
        get() = try {
            val builder = Configuration.Builder()
                .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
            
            if (::workerFactory.isInitialized) {
                builder.setWorkerFactory(workerFactory)
            } else {
                Log.e(TAG, "WorkerFactory not initialized")
            }
            
            builder.build()
        } catch (e: Exception) {
            Log.e(TAG, "Error building WorkManager configuration", e)
            Configuration.Builder()
                .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
                .build()
        }

    override fun attachBaseContext(base: Context) {
        try {
            super.attachBaseContext(base)
        } catch (e: Throwable) {
            Log.e(TAG, "CRITICAL: super.attachBaseContext failed", e)
            throw e
        }

        try {
            CrashHandler.initialize(this)
            Log.d(TAG, "CrashHandler initialized in attachBaseContext")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize crash handler in attachBaseContext", e)
        }
    }

    private var inCrashProcess = false

    override fun onCreate() {
        inCrashProcess = try {
            CrashHandler.isCrashProcess(this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to determine crash process status, assuming main process", e)
            false
        }

        if (inCrashProcess) {
            Log.d(TAG, "Running in crash process, skipping Hilt injection and initialization")
            return
        }

        try {
            super.onCreate()
        } catch (e: Throwable) {
            Log.e(TAG, "CRITICAL ERROR IN APPLICATION ONCREATE / HILT INIT", e)

            val handler = Thread.getDefaultUncaughtExceptionHandler()
            if (handler != null) {
                handler.uncaughtException(Thread.currentThread(), e)
            } else {
                CrashHandler.initialize(this)
                Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(Thread.currentThread(), e)
            }
            return
        }

        initializeApp()
    }

    private fun initializeApp() {
        applicationScope.launch {
            try {
                createNotificationChannels()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create notification channels", e)
            }
        }

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

        applicationScope.launch {
            try {
                if (::witnessModeManager.isInitialized) {
                    witnessModeManager.checkForPendingFollowUps()
                    Log.d(TAG, "Witness Mode check completed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check Witness Mode follow-ups", e)
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (notificationManager == null) {
                Log.w(TAG, "NotificationManager not available, skipping channel creation")
                return
            }

            val mainChannel = NotificationChannel(
                CHANNEL_MAIN,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.notification_channel_desc)
                enableVibration(true)
                setShowBadge(true)
            }

            val wisdomChannel = NotificationChannel(
                CHANNEL_WISDOM,
                "Daily Wisdom",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily vocabulary, quotes, and inspiration"
                enableVibration(true)
            }

            val journalChannel = NotificationChannel(
                CHANNEL_JOURNAL,
                "Journal Reminders",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Gentle reminders to reflect and journal"
            }

            val futureChannel = NotificationChannel(
                CHANNEL_FUTURE,
                "Messages from Past You",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Special messages from your past self"
                enableVibration(true)
                setShowBadge(true)
            }

            val achievementsChannel = NotificationChannel(
                CHANNEL_ACHIEVEMENTS,
                "Achievements",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Celebrate your accomplishments"
                enableVibration(true)
            }

            val havenChannel = NotificationChannel(
                CHANNEL_HAVEN,
                "Haven Check-ins",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Haven remembers things you mentioned and checks in"
                enableVibration(true)
                setShowBadge(true)
            }

            try {
                notificationManager.createNotificationChannels(
                    listOf(mainChannel, wisdomChannel, journalChannel, futureChannel, achievementsChannel, havenChannel)
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
        const val CHANNEL_HAVEN = "prody_haven"
    }
}
