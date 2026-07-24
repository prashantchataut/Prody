package com.kairos.app.domain.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationDeliveryPolicyTest {
    private val policy = NotificationDeliveryPolicy()
    private val now = 1_800_000_000_000L

    @Test
    fun `legacy notifications always fail closed`() {
        val decision = policy.evaluate(context(NotificationIntentType.LEGACY_STREAK))
        assertTrue(decision is NotificationDeliveryDecision.Skip)
    }

    @Test
    fun `quiet hours support overnight ranges`() {
        val decision = policy.evaluate(
            context(NotificationIntentType.DAILY_MOMENT).copy(
                currentHour = 23,
                quietModeEnabled = true,
                quietStartHour = 22,
                quietEndHour = 7
            )
        )
        assertEquals(NotificationDeliveryDecision.Skip("quiet hours"), decision)
    }

    @Test
    fun `equal quiet hour boundaries do not mute the whole day`() {
        val decision = policy.evaluate(
            context(NotificationIntentType.DAILY_MOMENT).copy(
                currentHour = 10,
                quietModeEnabled = true,
                quietStartHour = 8,
                quietEndHour = 8
            )
        )
        assertEquals(NotificationDeliveryDecision.Deliver, decision)
    }

    @Test
    fun `completed daily action suppresses redundant reminder`() {
        val decision = policy.evaluate(
            context(NotificationIntentType.DAILY_MOMENT).copy(completedRelevantActionToday = true)
        )
        assertEquals(NotificationDeliveryDecision.Skip("already completed"), decision)
    }

    @Test
    fun `daily cap and cooldown protect attention`() {
        val capped = policy.evaluate(
            context(NotificationIntentType.WEEKLY_RECAP).copy(notificationsSentToday = 2)
        )
        val coolingDown = policy.evaluate(
            context(NotificationIntentType.WEEKLY_RECAP).copy(lastNotificationSentAt = now - HOUR_MS)
        )

        assertEquals(NotificationDeliveryDecision.Skip("daily cap reached"), capped)
        assertEquals(NotificationDeliveryDecision.Skip("cooldown"), coolingDown)
    }

    @Test
    fun `future message delivery bypasses fatigue limits`() {
        val decision = policy.evaluate(
            context(NotificationIntentType.FUTURE_MESSAGE).copy(
                currentHour = 2,
                quietModeEnabled = true,
                notificationsSentToday = 10,
                lastNotificationSentAt = now - 1_000L
            )
        )
        assertEquals(NotificationDeliveryDecision.Deliver, decision)
    }

    private fun context(type: NotificationIntentType) = NotificationPolicyContext(
        type = type,
        notificationsEnabled = true,
        typeEnabled = true,
        currentHour = 10,
        quietModeEnabled = false,
        quietStartHour = 22,
        quietEndHour = 7,
        notificationsSentToday = 0,
        lastNotificationSentAt = 0,
        lastAppOpenAt = 0,
        completedRelevantActionToday = false,
        now = now
    )

    private companion object {
        const val HOUR_MS = 60L * 60L * 1_000L
    }
}
