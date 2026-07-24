package com.kairos.app.domain.notification

/** The small set of notification jobs allowed in the simplified product. */
enum class NotificationIntentType {
    DAILY_MOMENT,
    EVENING_REFLECTION,
    WEEKLY_RECAP,
    FUTURE_MESSAGE,
    LEGACY_WORD,
    LEGACY_STREAK,
    LEGACY_JOURNAL
}

data class NotificationPolicyContext(
    val type: NotificationIntentType,
    val notificationsEnabled: Boolean,
    val typeEnabled: Boolean,
    val currentHour: Int,
    val quietModeEnabled: Boolean,
    val quietStartHour: Int,
    val quietEndHour: Int,
    val notificationsSentToday: Int,
    val lastNotificationSentAt: Long,
    val lastAppOpenAt: Long,
    val completedRelevantActionToday: Boolean,
    val now: Long
)

sealed interface NotificationDeliveryDecision {
    data object Deliver : NotificationDeliveryDecision
    data class Skip(val reason: String) : NotificationDeliveryDecision
}

/**
 * Fatigue-control policy. This is pure Kotlin so every rule can be unit tested.
 */
class NotificationDeliveryPolicy {

    fun evaluate(context: NotificationPolicyContext): NotificationDeliveryDecision {
        if (!context.notificationsEnabled || !context.typeEnabled) {
            return NotificationDeliveryDecision.Skip("disabled")
        }
        if (context.type in LEGACY_TYPES) {
            return NotificationDeliveryDecision.Skip("legacy notification removed")
        }
        if (context.type == NotificationIntentType.FUTURE_MESSAGE) {
            return NotificationDeliveryDecision.Deliver
        }
        if (context.quietModeEnabled && isQuietHour(context)) {
            return NotificationDeliveryDecision.Skip("quiet hours")
        }
        if (context.completedRelevantActionToday &&
            context.type in setOf(NotificationIntentType.DAILY_MOMENT, NotificationIntentType.EVENING_REFLECTION)
        ) {
            return NotificationDeliveryDecision.Skip("already completed")
        }
        if (context.notificationsSentToday >= MAX_DAILY_NOTIFICATIONS) {
            return NotificationDeliveryDecision.Skip("daily cap reached")
        }
        if (context.lastNotificationSentAt > 0 &&
            context.now - context.lastNotificationSentAt < MIN_NOTIFICATION_GAP_MS
        ) {
            return NotificationDeliveryDecision.Skip("cooldown")
        }
        if (context.lastAppOpenAt > 0 && context.now - context.lastAppOpenAt < RECENT_APP_OPEN_MS) {
            return NotificationDeliveryDecision.Skip("app opened recently")
        }
        return NotificationDeliveryDecision.Deliver
    }

    private fun isQuietHour(context: NotificationPolicyContext): Boolean {
        val start = context.quietStartHour.coerceIn(0, 23)
        val end = context.quietEndHour.coerceIn(0, 23)
        val hour = context.currentHour.coerceIn(0, 23)
        return when {
            start == end -> false
            start < end -> hour in start until end
            else -> hour >= start || hour < end
        }
    }

    private companion object {
        const val MAX_DAILY_NOTIFICATIONS = 2
        const val MIN_NOTIFICATION_GAP_MS = 6L * 60L * 60L * 1_000L
        const val RECENT_APP_OPEN_MS = 20L * 60L * 1_000L
        val LEGACY_TYPES = setOf(
            NotificationIntentType.LEGACY_WORD,
            NotificationIntentType.LEGACY_STREAK,
            NotificationIntentType.LEGACY_JOURNAL
        )
    }
}
