package com.prody.prashant.notification

import com.prody.prashant.data.auth.UserIdProvider
import com.prody.prashant.data.local.dao.DailyContentDao
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.notification.NotificationDeliveryDecision
import com.prody.prashant.domain.notification.NotificationDeliveryPolicy
import com.prody.prashant.domain.notification.NotificationIntentType
import com.prody.prashant.domain.notification.NotificationPolicyContext
import com.prody.prashant.domain.recommendation.DailyContentType
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationDeliveryGate @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val userIdProvider: UserIdProvider,
    private val dailyContentDao: DailyContentDao,
    private val journalDao: JournalDao,
    private val policy: NotificationDeliveryPolicy
) {
    suspend fun shouldDeliver(action: String?, now: Long = System.currentTimeMillis()): Boolean {
        val type = action.toIntentType() ?: return false
        val zone = ZoneId.systemDefault()
        val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
        resetDailyCounterIfNeeded(today, zone, now)

        val typeEnabled = when (type) {
            NotificationIntentType.DAILY_MOMENT ->
                preferencesManager.wisdomNotificationEnabled.first() &&
                    preferencesManager.morningReminderEnabled.first()
            NotificationIntentType.EVENING_REFLECTION ->
                preferencesManager.journalReminderEnabled.first() &&
                    preferencesManager.eveningReflectionEnabled.first()
            NotificationIntentType.WEEKLY_RECAP -> preferencesManager.weeklySummaryNotifications.first()
            NotificationIntentType.FUTURE_MESSAGE -> true
            else -> false
        }

        val userId = userIdProvider.getUserId()
        val completed = when (type) {
            NotificationIntentType.DAILY_MOMENT ->
                dailyContentDao.hasCompletedContent(
                    userId, today.toString(), DailyContentType.VOCABULARY.name
                )
            NotificationIntentType.EVENING_REFLECTION -> {
                val start = today.atStartOfDay(zone).toInstant().toEpochMilli()
                journalDao.getTodayEntryCount(start) > 0
            }
            else -> false
        }

        val decision = policy.evaluate(
            NotificationPolicyContext(
                type = type,
                notificationsEnabled = preferencesManager.notificationsEnabled.first(),
                typeEnabled = typeEnabled,
                currentHour = Instant.ofEpochMilli(now).atZone(zone).hour,
                quietModeEnabled = preferencesManager.quietModeEnabled.first(),
                quietStartHour = preferencesManager.quietModeStart.first(),
                quietEndHour = preferencesManager.quietModeEnd.first(),
                notificationsSentToday = preferencesManager.notificationsSentToday.first(),
                lastNotificationSentAt = preferencesManager.lastNotificationSentAt.first(),
                lastAppOpenAt = preferencesManager.lastAppOpenAt.first(),
                completedRelevantActionToday = completed,
                now = now
            )
        )
        return decision is NotificationDeliveryDecision.Deliver
    }

    suspend fun recordDelivered(now: Long = System.currentTimeMillis()) {
        preferencesManager.incrementNotificationsSentToday(now)
    }

    private suspend fun resetDailyCounterIfNeeded(
        today: LocalDate,
        zone: ZoneId,
        now: Long
    ) {
        val resetAt = preferencesManager.notificationResetDate.first()
        val resetDate = resetAt.takeIf { it > 0 }
            ?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }
        if (resetDate != today) preferencesManager.resetNotificationsSentToday(now)
    }

    private fun String?.toIntentType(): NotificationIntentType? = when (this) {
        NotificationReceiver.ACTION_MORNING_WISDOM -> NotificationIntentType.DAILY_MOMENT
        NotificationReceiver.ACTION_EVENING_REFLECTION -> NotificationIntentType.EVENING_REFLECTION
        NotificationReceiver.ACTION_WEEKLY_SUMMARY -> NotificationIntentType.WEEKLY_RECAP
        NotificationReceiver.ACTION_FUTURE_MESSAGE -> NotificationIntentType.FUTURE_MESSAGE
        NotificationReceiver.ACTION_WORD_OF_DAY -> NotificationIntentType.LEGACY_WORD
        NotificationReceiver.ACTION_STREAK_REMINDER -> NotificationIntentType.LEGACY_STREAK
        NotificationReceiver.ACTION_JOURNAL_REMINDER -> NotificationIntentType.LEGACY_JOURNAL
        else -> null
    }
}
