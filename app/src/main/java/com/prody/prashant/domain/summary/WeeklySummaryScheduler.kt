package com.prody.prashant.domain.summary

import android.content.Context
import android.util.Log
import androidx.work.*
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.repository.WeeklyDigestRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Scheduler for automated weekly summary generation.
 *
 * Responsibilities:
 * - Schedule weekly digest generation on user's configured day
 * - Trigger modal display on first app open after generation
 * - Handle notification scheduling (optional)
 */
@Singleton
class WeeklySummaryScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager
) {

    companion object {
        private const val WORK_NAME = "weekly_summary_generation"
        private const val WORK_TAG = "weekly_summary"
    }

    /**
     * Schedule weekly summary generation based on user preferences.
     */
    suspend fun schedule() {
        val isEnabled = preferencesManager.weeklySummaryEnabled.first()
        if (!isEnabled) {
            cancel()
            return
        }

        val summaryDay = preferencesManager.weeklySummaryDay.first()
        val targetDayOfWeek = DayOfWeek.of((summaryDay % 7) + 1) // Convert 0-6 to 1-7

        // Schedule for next occurrence of target day at 9 AM
        val now = LocalDateTime.now()
        val nextTargetDay = getNextOccurrence(now.toLocalDate(), targetDayOfWeek)
        val targetDateTime = nextTargetDay.atTime(9, 0)

        val delay = Duration.between(now, targetDateTime)
        val delayMinutes = delay.toMinutes().coerceAtLeast(0)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // For AI insights if needed
            .build()

        val workRequest = PeriodicWorkRequestBuilder<WeeklySummaryWorker>(
            repeatInterval = 7,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    /**
     * Cancel scheduled weekly summary generation.
     */
    fun cancel() {
        WorkManager.getInstance(context)
            .cancelUniqueWork(WORK_NAME)
    }

    /**
     * Check if a summary should be shown now (on app open).
     *
     * @return true if summary should be displayed
     */
    suspend fun shouldShowSummaryNow(): Boolean {
        val isEnabled = preferencesManager.weeklySummaryEnabled.first()
        if (!isEnabled) return false

        val lastShown = preferencesManager.lastWeeklySummaryShown.first()
        val summaryDay = preferencesManager.weeklySummaryDay.first()
        val targetDayOfWeek = DayOfWeek.of((summaryDay % 7) + 1)

        val today = LocalDate.now()
        val todayDayOfWeek = today.dayOfWeek

        // Only show on the configured day
        if (todayDayOfWeek != targetDayOfWeek) return false

        // Check if already shown this week
        val lastShownDate = if (lastShown > 0) {
            java.time.Instant.ofEpochMilli(lastShown)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
        } else null

        // If last shown was this week, don't show again
        if (lastShownDate != null && lastShownDate >= today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) {
            return false
        }

        return true
    }

    /**
     * Mark that the summary was shown.
     */
    suspend fun markSummaryShown() {
        preferencesManager.setLastWeeklySummaryShown(System.currentTimeMillis())
    }

    private fun getNextOccurrence(from: LocalDate, targetDay: DayOfWeek): LocalDate {
        val currentDay = from.dayOfWeek
        val daysUntilTarget = (targetDay.value - currentDay.value + 7) % 7
        return if (daysUntilTarget == 0) {
            // If today is the target day but we've passed the time, schedule for next week
            from.plusWeeks(1)
        } else {
            from.plusDays(daysUntilTarget.toLong())
        }
    }
}

@HiltWorker
class WeeklySummaryWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val weeklyDigestRepository: WeeklyDigestRepository,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Check if enabled
            val isEnabled = preferencesManager.weeklySummaryEnabled.first()
            if (!isEnabled) {
                return Result.success()
            }

            // Generate weekly digest for previous week
            val userId = preferencesManager.userId.first().ifBlank { "local" }
            weeklyDigestRepository.generateWeeklyDigest(userId)

            // Schedule notification if enabled
            val notificationsEnabled = preferencesManager.weeklySummaryNotifications.first()
            if (notificationsEnabled) {
                scheduleNotification()
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("WeeklySummaryWorker", "Failed to generate weekly summary", e)
            Result.retry()
        }
    }

    private fun scheduleNotification() {
        // Implement notification scheduling for weekly summary
        // This creates a notification to remind user to check their weekly summary
        // Note: WeeklySummaryNotificationWorker needs to be implemented separately
        try {
            // For now, we'll skip the notification worker as it's not yet implemented
            Log.d("WeeklySummaryWorker", "Weekly summary notification would be scheduled here")
        } catch (e: Exception) {
            Log.e("WeeklySummaryWorker", "Failed to schedule weekly summary notification", e)
        }
    }
}
