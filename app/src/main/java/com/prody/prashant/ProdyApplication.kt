package com.prody.prashant

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ProdyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        try {
            createNotificationChannels()
        } catch (e: Exception) {
            // Log error but don't crash the app - notifications are not critical for launch
            android.util.Log.e("ProdyApplication", "Failed to create notification channels", e)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

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

            notificationManager.createNotificationChannels(
                listOf(mainChannel, wisdomChannel, journalChannel, futureChannel, achievementsChannel)
            )
        }
    }

    companion object {
        const val CHANNEL_MAIN = "prody_main"
        const val CHANNEL_WISDOM = "prody_wisdom"
        const val CHANNEL_JOURNAL = "prody_journal"
        const val CHANNEL_FUTURE = "prody_future"
        const val CHANNEL_ACHIEVEMENTS = "prody_achievements"
    }
}
