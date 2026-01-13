package com.prody.prashant.domain.deepdive

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.prody.prashant.R
import com.prody.prashant.data.local.dao.DeepDiveDao
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.entity.DeepDiveEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedules Deep Dive sessions based on user preferences and history.
 *
 * Features:
 * - Weekly scheduling (user-configurable day)
 * - Smart theme rotation (prioritizes unexplored themes)
 * - Theme suggestion based on recent journal moods
 * - Notification scheduling
 */
@Singleton
class DeepDiveScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deepDiveDao: DeepDiveDao,
    private val journalDao: JournalDao
) {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "deep_dive_channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Deep Dive Reminders"
        private const val NOTIFICATION_REQUEST_CODE_BASE = 9000

        // Default scheduling preferences
        private const val DEFAULT_DAY_OF_WEEK = 6 // Saturday
        private const val DEFAULT_HOUR = 19 // 7 PM
        private const val DEFAULT_MINUTE = 0

        // Notification timing (24 hours before scheduled time)
        private const val NOTIFICATION_ADVANCE_HOURS = 24L
    }

    /**
     * Schedule the next Deep Dive session for a user
     */
    suspend fun scheduleNextDeepDive(
        userId: String,
        preferredDayOfWeek: Int = DEFAULT_DAY_OF_WEEK,
        preferredHour: Int = DEFAULT_HOUR,
        preferredMinute: Int = DEFAULT_MINUTE
    ): DeepDiveEntity = withContext(Dispatchers.IO) {
        // Calculate next scheduled date
        val scheduledDate = calculateNextScheduledDate(preferredDayOfWeek, preferredHour, preferredMinute)

        // Select theme for the next session
        val theme = selectNextTheme(userId)

        // Create the deep dive entity
        val deepDive = DeepDiveEntity(
            userId = userId,
            theme = theme.id,
            scheduledDate = scheduledDate,
            promptVariation = 0, // Will be determined when session starts
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        // Insert into database
        val id = deepDiveDao.insertDeepDive(deepDive)
        val createdDeepDive = deepDive.copy(id = id)

        // Schedule notification
        scheduleNotification(createdDeepDive, theme)

        createdDeepDive
    }

    /**
     * Schedule multiple Deep Dives in advance (e.g., for the next month)
     */
    suspend fun scheduleMultipleDeepDives(
        userId: String,
        count: Int = 4,
        preferredDayOfWeek: Int = DEFAULT_DAY_OF_WEEK,
        preferredHour: Int = DEFAULT_HOUR,
        preferredMinute: Int = DEFAULT_MINUTE
    ): List<DeepDiveEntity> = withContext(Dispatchers.IO) {
        val scheduledDeepDives = mutableListOf<DeepDiveEntity>()

        // Get currently scheduled deep dives to avoid duplicates
        val existingScheduled = deepDiveDao.getScheduledDeepDivesSync(userId)

        // Calculate how many more we need
        val needToSchedule = count - existingScheduled.size

        if (needToSchedule <= 0) {
            return@withContext existingScheduled.take(count)
        }

        // Get theme rotation
        val themeRotation = createThemeRotation(userId)

        // Start from next week
        var currentDate = calculateNextScheduledDate(preferredDayOfWeek, preferredHour, preferredMinute)

        repeat(needToSchedule) { index ->
            val theme = themeRotation[index % themeRotation.size]

            val deepDive = DeepDiveEntity(
                userId = userId,
                theme = theme.id,
                scheduledDate = currentDate,
                promptVariation = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val id = deepDiveDao.insertDeepDive(deepDive)
            val created = deepDive.copy(id = id)
            scheduledDeepDives.add(created)

            // Schedule notification
            scheduleNotification(created, theme)

            // Move to next week
            currentDate += (7 * 24 * 60 * 60 * 1000) // Add one week
        }

        scheduledDeepDives
    }

    /**
     * Suggest a theme based on recent journal moods and patterns
     */
    suspend fun suggestThemeBasedOnMood(userId: String): DeepDiveTheme = withContext(Dispatchers.IO) {
        try {
            // Get recent journal entries
            val recentEntries = journalDao.getRecentEntriesSync(userId, 10)

            if (recentEntries.isEmpty()) {
                // Default to gratitude for new users
                return@withContext DeepDiveTheme.GRATITUDE
            }

            // Analyze mood patterns
            val moods = recentEntries.map { it.mood.lowercase() }
            val hasNegativeMoods = moods.any {
                it.contains("sad") || it.contains("anxious") ||
                it.contains("stressed") || it.contains("angry")
            }
            val hasJoyfulMoods = moods.any {
                it.contains("happy") || it.contains("excited") ||
                it.contains("joyful") || it.contains("content")
            }

            // Suggest theme based on mood patterns
            when {
                hasNegativeMoods && moods.count { it.contains("anxious") || it.contains("worried") } > 2 ->
                    DeepDiveTheme.FEAR // Help face fears
                hasNegativeMoods && moods.count { it.contains("sad") || it.contains("lonely") } > 2 ->
                    DeepDiveTheme.RELATIONSHIPS // Explore connections
                hasNegativeMoods ->
                    DeepDiveTheme.FORGIVENESS // Help with emotional release
                hasJoyfulMoods ->
                    DeepDiveTheme.JOY // Amplify positive emotions
                else ->
                    selectNextTheme(userId) // Use standard rotation
            }
        } catch (e: Exception) {
            // Fallback to standard selection
            selectNextTheme(userId)
        }
    }

    /**
     * Select the next theme for a Deep Dive
     * Prioritizes themes that haven't been explored yet
     */
    private suspend fun selectNextTheme(userId: String): DeepDiveTheme {
        // Get unexplored themes
        val unexploredThemeIds = deepDiveDao.getUnexploredThemes(userId).toSet()
        val allThemes = DeepDiveTheme.getAllThemes()

        if (unexploredThemeIds.isNotEmpty()) {
            // Prioritize unexplored themes
            val unexploredThemes = allThemes.filter { it.id in unexploredThemeIds }
            return unexploredThemes.random()
        }

        // Get theme frequency to rotate fairly
        val themeFrequency = deepDiveDao.getThemeFrequency(userId).associate { it.theme to it.count }

        // Find least explored theme
        val leastExploredTheme = allThemes.minByOrNull { theme ->
            themeFrequency[theme.id] ?: 0
        }

        return leastExploredTheme ?: DeepDiveTheme.GRATITUDE
    }

    /**
     * Create a balanced theme rotation
     */
    private suspend fun createThemeRotation(userId: String): List<DeepDiveTheme> {
        val allThemes = DeepDiveTheme.getAllThemes().toMutableList()
        val rotation = mutableListOf<DeepDiveTheme>()

        // Get unexplored themes first
        val unexploredThemeIds = deepDiveDao.getUnexploredThemes(userId).toSet()
        val unexploredThemes = allThemes.filter { it.id in unexploredThemeIds }

        // Add unexplored themes first
        rotation.addAll(unexploredThemes.shuffled())

        // Add remaining themes
        val exploredThemes = allThemes.filter { it.id !in unexploredThemeIds }
        rotation.addAll(exploredThemes.shuffled())

        return rotation
    }

    /**
     * Calculate the next scheduled date based on day of week preference
     */
    private fun calculateNextScheduledDate(
        dayOfWeek: Int,
        hour: Int,
        minute: Int
    ): Long {
        val now = LocalDate.now()
        val currentDayOfWeek = now.dayOfWeek.value
        val targetDayOfWeek = if (dayOfWeek == 7) 0 else dayOfWeek // Convert Sunday from 7 to 0

        // Calculate days until target day
        val daysUntilTarget = when {
            targetDayOfWeek > currentDayOfWeek -> targetDayOfWeek - currentDayOfWeek
            targetDayOfWeek < currentDayOfWeek -> 7 - (currentDayOfWeek - targetDayOfWeek)
            else -> { // Same day
                val nowTime = LocalTime.now()
                val targetTime = LocalTime.of(hour, minute)
                if (nowTime.isBefore(targetTime)) 0 else 7 // Today if before time, else next week
            }
        }

        val targetDate = now.plusDays(daysUntilTarget.toLong())
        val targetDateTime = targetDate.atTime(hour, minute)

        return targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    /**
     * Schedule notification for a Deep Dive
     */
    private fun scheduleNotification(deepDive: DeepDiveEntity, theme: DeepDiveTheme) {
        try {
            createNotificationChannel()

            val notificationTime = deepDive.scheduledDate - (NOTIFICATION_ADVANCE_HOURS * 60 * 60 * 1000)

            // Only schedule if in the future
            if (notificationTime <= System.currentTimeMillis()) {
                return
            }

            val intent = Intent(context, DeepDiveNotificationReceiver::class.java).apply {
                putExtra("deepDiveId", deepDive.id)
                putExtra("theme", theme.id)
                putExtra("themeDisplayName", theme.displayName)
                putExtra("themeIcon", theme.icon)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_REQUEST_CODE_BASE + deepDive.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Schedule exact alarm if possible
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            // Log error but don't crash
            e.printStackTrace()
        }
    }

    /**
     * Cancel notification for a Deep Dive
     */
    fun cancelNotification(deepDiveId: Long) {
        try {
            val intent = Intent(context, DeepDiveNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_REQUEST_CODE_BASE + deepDiveId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Reschedule a Deep Dive to a new date
     */
    suspend fun rescheduleDeepDive(
        deepDiveId: Long,
        newScheduledDate: Long
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val deepDive = deepDiveDao.getDeepDiveById(deepDiveId) ?: return@withContext false
            val theme = DeepDiveTheme.fromId(deepDive.theme) ?: return@withContext false

            // Cancel old notification
            cancelNotification(deepDiveId)

            // Update deep dive
            val updated = deepDive.copy(
                scheduledDate = newScheduledDate,
                updatedAt = System.currentTimeMillis()
            )
            deepDiveDao.updateDeepDive(updated)

            // Schedule new notification
            scheduleNotification(updated, theme)

            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Create notification channel for Deep Dive reminders
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for scheduled Deep Dive reflection sessions"
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Check and schedule notifications for upcoming Deep Dives
     */
    suspend fun syncNotifications(userId: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val oneWeekFromNow = now + (7 * 24 * 60 * 60 * 1000)

        // Get deep dives that need notifications
        val needingNotification = deepDiveDao.getDeepDivesNeedingNotification(
            userId = userId,
            startWindow = now,
            endWindow = oneWeekFromNow
        )

        needingNotification.forEach { deepDive ->
            val theme = DeepDiveTheme.fromId(deepDive.theme)
            if (theme != null) {
                scheduleNotification(deepDive, theme)
                deepDiveDao.markNotificationSent(deepDive.id)
            }
        }
    }

    /**
     * Get next scheduled Deep Dive time
     */
    suspend fun getNextScheduledTime(userId: String): Long? = withContext(Dispatchers.IO) {
        deepDiveDao.getNextScheduledDeepDive(userId)?.scheduledDate
    }

    /**
     * Check if auto-scheduling is needed and schedule if necessary
     */
    suspend fun ensureScheduledDeepDives(
        userId: String,
        minimumScheduled: Int = 2
    ) = withContext(Dispatchers.IO) {
        val scheduled = deepDiveDao.getScheduledDeepDivesSync(userId)
        if (scheduled.size < minimumScheduled) {
            scheduleMultipleDeepDives(userId, minimumScheduled - scheduled.size)
        }
    }
}

/**
 * Broadcast receiver for Deep Dive notifications
 * This would typically be in a separate file, but included here for completeness
 */
class DeepDiveNotificationReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val deepDiveId = intent.getLongExtra("deepDiveId", -1)
        if (deepDiveId == -1L) return

        val themeDisplayName = intent.getStringExtra("themeDisplayName") ?: "Deep Dive"
        val themeIcon = intent.getStringExtra("themeIcon") ?: "🌟"

        val message = DeepDiveNotification.generateMessage(
            DeepDiveTheme.fromId(intent.getStringExtra("theme") ?: "") ?: DeepDiveTheme.GRATITUDE
        )

        // Create notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, DeepDiveScheduler.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Make sure this exists
            .setContentTitle("$themeIcon $themeDisplayName Deep Dive Tomorrow")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(deepDiveId.toInt(), notification)
    }
}
