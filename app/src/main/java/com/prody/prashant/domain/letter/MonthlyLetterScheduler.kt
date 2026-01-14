package com.prody.prashant.domain.letter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.prody.prashant.domain.common.Result as DomainResult
import com.prody.prashant.domain.repository.MonthlyLetterRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.YearMonth
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Scheduler for automatic monthly letter generation.
 *
 * Schedules generation to run on the first day of each month,
 * creating a letter for the previous month.
 */
@Singleton
class MonthlyLetterScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val WORK_NAME = "monthly_letter_generation"
        private const val CHANNEL_ID = "monthly_letters"
        private const val NOTIFICATION_ID = 1001
    }

    /**
     * Schedule monthly letter generation
     */
    fun scheduleMonthlyGeneration() {
        // Calculate delay until first day of next month
        val now = java.time.LocalDateTime.now()
        val firstOfNextMonth = now.withDayOfMonth(1).plusMonths(1).withHour(9).withMinute(0)
        val delayMillis = java.time.Duration.between(now, firstOfNextMonth).toMillis()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<MonthlyLetterGenerationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag("monthly_letter")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    /**
     * Cancel scheduled generation
     */
    fun cancelScheduledGeneration() {
        WorkManager.getInstance(context)
            .cancelUniqueWork(WORK_NAME)
    }

    /**
     * Check if generation is scheduled
     */
    fun isScheduled(): Boolean {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(WORK_NAME)
            .get()

        return workInfos.any { workInfo ->
            workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
        }
    }

    /**
     * Create notification channel for monthly letters
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Monthly Growth Letters"
            val descriptionText = "Notifications for your monthly reflection letters"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Show notification that a new letter is ready
     */
    fun showLetterReadyNotification(monthName: String) {
        createNotificationChannel()

        // Create intent to open the letter
        // Note: You'll need to replace this with your actual MainActivity intent
        val intent = Intent(context, Class.forName("com.prody.prashant.MainActivity")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_monthly_letter", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email) // Replace with your app icon
            .setContentTitle("Your $monthName letter is ready")
            .setContentText("Your monthly reflection is here. Tap to open.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

/**
 * Worker that generates the monthly letter
 */
class MonthlyLetterGenerationWorker(
    context: Context,
    params: WorkerParameters,
    private val monthlyLetterRepository: MonthlyLetterRepository,
    private val scheduler: MonthlyLetterScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Generate letter for previous month
            val previousMonth = YearMonth.now().minusMonths(1)

            // Check if letter already exists
            if (monthlyLetterRepository.letterExistsForMonth("local", previousMonth)) {
                // Letter already exists, schedule next generation
                scheduler.scheduleMonthlyGeneration()
                return@withContext Result.success()
            }

            // Check if there's enough data
            if (!monthlyLetterRepository.canGenerateLetter("local", previousMonth)) {
                // Not enough data, schedule next generation anyway
                scheduler.scheduleMonthlyGeneration()
                return@withContext Result.success()
            }

            // Generate the letter
            when (val result = monthlyLetterRepository.generateLetter("local", previousMonth)) {
                is DomainResult.Success -> {
                    // Show notification
                    scheduler.showLetterReadyNotification(previousMonth.month.name)

                    // Schedule next generation
                    scheduler.scheduleMonthlyGeneration()

                    Result.success()
                }
                is DomainResult.Error -> {
                    // Retry later
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    class Factory @Inject constructor(
        private val monthlyLetterRepository: MonthlyLetterRepository,
        private val scheduler: MonthlyLetterScheduler
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? {
            return when (workerClassName) {
                MonthlyLetterGenerationWorker::class.java.name -> {
                    MonthlyLetterGenerationWorker(
                        appContext,
                        workerParameters,
                        monthlyLetterRepository,
                        scheduler
                    )
                }
                else -> null
            }
        }
    }
}

/**
 * Preferences for monthly letter feature
 */
object MonthlyLetterPreferences {
    const val PREF_NAME = "monthly_letter_prefs"
    const val KEY_ENABLED = "monthly_letter_enabled"
    const val KEY_NOTIFICATIONS_ENABLED = "monthly_letter_notifications"

    fun isEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ENABLED, true) // Default: enabled
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()

        // Schedule or cancel based on preference
        val scheduler = MonthlyLetterScheduler(context)
        if (enabled) {
            scheduler.scheduleMonthlyGeneration()
        } else {
            scheduler.cancelScheduledGeneration()
        }
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true) // Default: enabled
    }

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }
}
