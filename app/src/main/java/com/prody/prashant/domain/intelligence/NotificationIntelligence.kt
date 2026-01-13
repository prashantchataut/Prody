package com.prody.prashant.domain.intelligence

import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.model.TimeOfDay
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * ================================================================================================
 * NOTIFICATION INTELLIGENCE - Smart, Contextual Notifications
 * ================================================================================================
 *
 * The Problem: Generic notification systems annoy users with irrelevant, poorly-timed messages.
 *
 * The Solution: This engine decides IF, WHEN, and WHAT to notify based on:
 * - User's current context and emotional state
 * - Historical engagement patterns
 * - Time of day and day of week
 * - Notification fatigue prevention
 * - Content relevance
 *
 * Key principles:
 * - Less is more - better to not notify than to annoy
 * - Context is king - right message at right time
 * - Respect user patterns - don't notify during ignored times
 * - Build trust - every notification should feel valuable
 * - When struggling - be extra gentle
 */
@Singleton
class NotificationIntelligence @Inject constructor(
    private val userContextEngine: UserContextEngine,
    private val memoryEngine: MemoryEngine,
    private val temporalContentEngine: TemporalContentEngine,
    private val preferencesManager: PreferencesManager
) {

    // =============================================================================================
    // PUBLIC API
    // =============================================================================================

    /**
     * Decide whether to send a notification and what it should contain.
     * This is the main entry point for notification decisions.
     */
    suspend fun makeNotificationDecision(
        pendingNotification: PendingNotification
    ): NotificationDecision {
        val context = userContextEngine.getContextForNotification()
        val userContext = context.userContext

        // Check if notifications are enabled
        if (!isNotificationsEnabled()) {
            return NotificationDecision.Skip("Notifications disabled")
        }

        // Check if in quiet hours
        if (isInQuietHours()) {
            return NotificationDecision.Reschedule(
                to = getNextActiveTime(),
                reason = "Quiet hours active"
            )
        }

        // Apply intelligence rules
        return evaluateNotification(pendingNotification, context, userContext)
    }

    /**
     * Get the optimal notification time for a given notification type.
     */
    suspend fun getOptimalNotificationTime(type: NotificationType): LocalDateTime {
        val context = userContextEngine.getContextForNotification()
        val preferredTimes = context.preferredNotificationTimes
        val ignoredTimes = context.ignoresNotificationsAt

        // Find a time that's preferred and not ignored
        val optimalHour = preferredTimes.firstOrNull { it !in ignoredTimes }
            ?: preferredTimes.firstOrNull()
            ?: when (type) {
                NotificationType.MORNING_REMINDER -> 9
                NotificationType.EVENING_REFLECTION -> 20
                NotificationType.STREAK_REMINDER -> 18
                NotificationType.COMEBACK -> 10
                NotificationType.MEMORY_ANNIVERSARY -> 11
                NotificationType.FUTURE_MESSAGE_DELIVERY -> 9
                NotificationType.WEEKLY_SUMMARY -> 18
                NotificationType.ACHIEVEMENT -> 12
                NotificationType.HAVEN_CHECK_IN -> 15
                NotificationType.GENTLE_NUDGE -> 14
            }

        return LocalDateTime.now()
            .withHour(optimalHour)
            .withMinute(0)
            .withSecond(0)
    }

    /**
     * Generate personalized notification content based on context.
     */
    suspend fun generateNotificationContent(
        type: NotificationType,
        context: NotificationContext? = null
    ): IntelligentNotificationContent {
        val notificationContext = context ?: userContextEngine.getContextForNotification()
        val userContext = notificationContext.userContext

        return when (type) {
            NotificationType.MORNING_REMINDER -> generateMorningReminderContent(userContext)
            NotificationType.EVENING_REFLECTION -> generateEveningReflectionContent(userContext)
            NotificationType.STREAK_REMINDER -> generateStreakReminderContent(userContext)
            NotificationType.COMEBACK -> generateComebackContent(userContext)
            NotificationType.MEMORY_ANNIVERSARY -> generateMemoryContent(userContext)
            NotificationType.FUTURE_MESSAGE_DELIVERY -> generateFutureMessageContent()
            NotificationType.WEEKLY_SUMMARY -> generateWeeklySummaryContent(userContext)
            NotificationType.ACHIEVEMENT -> generateAchievementContent(userContext)
            NotificationType.HAVEN_CHECK_IN -> generateHavenCheckInContent(userContext)
            NotificationType.GENTLE_NUDGE -> generateGentleNudgeContent(userContext)
        }
    }

    /**
     * Check if now is a good time to send any notification.
     */
    suspend fun isGoodTimeToNotify(): Boolean {
        val context = userContextEngine.getContextForNotification()

        // Check quiet hours
        if (isInQuietHours()) return false

        // Check notification frequency
        if (isTooFrequent(context)) return false

        // Check if user is currently active
        if (isUserCurrentlyActive(context)) return false

        // Check ignored times
        val currentHour = LocalDateTime.now().hour
        if (currentHour in context.ignoresNotificationsAt) return false

        return true
    }

    /**
     * Get notification schedule for the day.
     */
    suspend fun getDailyNotificationSchedule(): DailyNotificationSchedule {
        val context = userContextEngine.getContextForNotification()
        val userContext = context.userContext
        val scheduledNotifications = mutableListOf<ScheduledNotification>()

        // Morning reminder (if enabled)
        if (shouldScheduleMorningReminder(userContext)) {
            val morningTime = getOptimalNotificationTime(NotificationType.MORNING_REMINDER)
            scheduledNotifications.add(
                ScheduledNotification(
                    type = NotificationType.MORNING_REMINDER,
                    scheduledTime = morningTime,
                    content = generateMorningReminderContent(userContext),
                    priority = NotificationPriority.MEDIUM
                )
            )
        }

        // Evening reflection (if enabled)
        if (shouldScheduleEveningReflection(userContext)) {
            val eveningTime = getOptimalNotificationTime(NotificationType.EVENING_REFLECTION)
            scheduledNotifications.add(
                ScheduledNotification(
                    type = NotificationType.EVENING_REFLECTION,
                    scheduledTime = eveningTime,
                    content = generateEveningReflectionContent(userContext),
                    priority = NotificationPriority.MEDIUM
                )
            )
        }

        // Streak reminder (if streak is at risk)
        if (shouldScheduleStreakReminder(userContext)) {
            val streakTime = getOptimalNotificationTime(NotificationType.STREAK_REMINDER)
            scheduledNotifications.add(
                ScheduledNotification(
                    type = NotificationType.STREAK_REMINDER,
                    scheduledTime = streakTime,
                    content = generateStreakReminderContent(userContext),
                    priority = NotificationPriority.HIGH
                )
            )
        }

        return DailyNotificationSchedule(
            date = LocalDateTime.now().toLocalDate(),
            notifications = scheduledNotifications,
            maxNotificationsToday = getMaxNotificationsForDay(userContext)
        )
    }

    // =============================================================================================
    // PRIVATE HELPERS - DECISION MAKING
    // =============================================================================================

    private suspend fun evaluateNotification(
        notification: PendingNotification,
        context: NotificationContext,
        userContext: UserContext
    ): NotificationDecision {
        // Rule 1: Don't notify during struggling times (except supportive notifications)
        if (userContext.isStruggling && notification.type !in SUPPORTIVE_NOTIFICATION_TYPES) {
            return NotificationDecision.Skip("User is struggling - reducing pressure")
        }

        // Rule 2: Limit notifications per day
        if (context.notificationsSentToday >= getMaxNotificationsForDay(userContext)) {
            return NotificationDecision.Skip("Daily limit reached")
        }

        // Rule 3: Minimum time between notifications
        val lastNotification = context.lastNotificationSentAt
        if (lastNotification != null) {
            val minutesSinceLastNotification = ChronoUnit.MINUTES.between(lastNotification, LocalDateTime.now())
            if (minutesSinceLastNotification < MIN_NOTIFICATION_INTERVAL_MINUTES) {
                return NotificationDecision.Delay(
                    until = lastNotification.plusMinutes(MIN_NOTIFICATION_INTERVAL_MINUTES.toLong()),
                    reason = "Too soon after last notification"
                )
            }
        }

        // Rule 4: Check if this notification type is relevant
        val relevanceScore = calculateRelevanceScore(notification.type, userContext)
        if (relevanceScore < MINIMUM_RELEVANCE_THRESHOLD) {
            return NotificationDecision.Skip("Low relevance score: $relevanceScore")
        }

        // Rule 5: Check user's historical response to this type
        if (shouldSkipBasedOnHistory(notification.type, context)) {
            return NotificationDecision.Skip("User rarely engages with this notification type")
        }

        // Rule 6: Modify content based on context
        val modifiedContent = adaptContentToContext(notification.content, userContext)
        if (modifiedContent != notification.content) {
            return NotificationDecision.Modify(
                newContent = modifiedContent,
                reason = "Content adapted to current context"
            )
        }

        // All checks passed - send the notification
        return NotificationDecision.Send(notification)
    }

    private fun calculateRelevanceScore(type: NotificationType, userContext: UserContext): Float {
        var score = 0.5f // Base score

        // Boost for appropriate timing
        when (type) {
            NotificationType.MORNING_REMINDER -> {
                if (userContext.timeOfDay == TimeOfDay.MORNING) score += 0.3f
            }
            NotificationType.EVENING_REFLECTION -> {
                if (userContext.timeOfDay == TimeOfDay.EVENING) score += 0.3f
            }
            NotificationType.STREAK_REMINDER -> {
                // High relevance if streak is at risk
                if (userContext.currentStreak.reflectionStreak.isAtRisk) score += 0.4f
                if (userContext.daysSinceLastEntry > 0) score += 0.2f
            }
            NotificationType.COMEBACK -> {
                // Only relevant if user has been away
                if (userContext.daysSinceLastEntry > 3) score += 0.4f
                else score -= 0.3f
            }
            NotificationType.HAVEN_CHECK_IN -> {
                // More relevant for struggling users
                if (userContext.isStruggling) score += 0.4f
            }
            else -> {}
        }

        // Boost for engaged users
        if (userContext.engagementLevel == EngagementLevel.DAILY) score += 0.1f
        if (userContext.trustLevel == TrustLevel.DEEP) score += 0.1f

        // Reduce for first week users (they have their own journey)
        if (userContext.isInFirstWeek) score -= 0.2f

        return score.coerceIn(0f, 1f)
    }

    private fun shouldSkipBasedOnHistory(
        type: NotificationType,
        context: NotificationContext
    ): Boolean {
        // If open rate for this type is below threshold, consider skipping
        return context.notificationOpenRate < MINIMUM_OPEN_RATE_THRESHOLD
    }

    private fun adaptContentToContext(
        content: IntelligentNotificationContent,
        userContext: UserContext
    ): IntelligentNotificationContent {
        // Adjust tone based on user state
        return when {
            userContext.isStruggling -> content.copy(
                title = makeGentler(content.title),
                body = makeGentler(content.body)
            )
            userContext.isThriving -> content.copy(
                body = addEnthusiasm(content.body)
            )
            userContext.userArchetype == UserArchetype.RETURNING -> content.copy(
                title = "Welcome back!",
                body = content.body.replace("Don't forget", "When you're ready")
            )
            else -> content
        }
    }

    private fun makeGentler(text: String): String {
        return text
            .replace("Don't forget", "Whenever you're ready")
            .replace("Time to", "If you feel like it")
            .replace("You need", "You might want to")
            .replace("!", ".")
    }

    private fun addEnthusiasm(text: String): String {
        return if (!text.endsWith("!")) "$text!" else text
    }

    // =============================================================================================
    // PRIVATE HELPERS - CONTENT GENERATION
    // =============================================================================================

    private fun generateMorningReminderContent(userContext: UserContext): IntelligentNotificationContent {
        val greeting = when (userContext.dayOfWeek) {
            DayOfWeek.MONDAY -> "New week, new possibilities"
            DayOfWeek.FRIDAY -> "Almost the weekend!"
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> "Weekend vibes"
            else -> "Good morning"
        }

        val bodies = when {
            userContext.isStruggling -> listOf(
                "No pressure, but your journal is here when you need it.",
                "A quiet moment awaits whenever you're ready.",
                "Just checking in. Take it easy today."
            )
            userContext.currentStreak.reflectionStreak.current >= 7 -> listOf(
                "Keep that amazing streak going!",
                "You've been on fire! Ready to continue?",
                "${userContext.currentStreak.reflectionStreak.current} days strong. Let's make it ${userContext.currentStreak.reflectionStreak.current + 1}."
            )
            else -> listOf(
                "What's on your mind this morning?",
                "A moment for reflection awaits.",
                "Start your day with intention."
            )
        }

        return IntelligentNotificationContent(
            title = greeting,
            body = bodies.random(),
            deepLink = "prody://journal/new"
        )
    }

    private fun generateEveningReflectionContent(userContext: UserContext): IntelligentNotificationContent {
        val titles = when (userContext.dayOfWeek) {
            DayOfWeek.FRIDAY -> listOf("End of week reflection", "Week wrap-up")
            DayOfWeek.SUNDAY -> listOf("Sunday evening check-in", "Ready for the week ahead?")
            else -> listOf("Evening reflection", "Time to reflect", "End of day thoughts")
        }

        val bodies = when {
            userContext.daysSinceLastEntry == 0 -> listOf(
                "Already journaled today! Want to add an evening thought?",
                "You wrote this morning. How did the day unfold?"
            )
            userContext.isStruggling -> listOf(
                "How are you feeling tonight?",
                "It's okay to not be okay. Your journal is here."
            )
            else -> listOf(
                "Before the day ends, capture a thought.",
                "What are you grateful for today?",
                "A few words before bed?"
            )
        }

        return IntelligentNotificationContent(
            title = titles.random(),
            body = bodies.random(),
            deepLink = "prody://journal/new"
        )
    }

    private fun generateStreakReminderContent(userContext: UserContext): IntelligentNotificationContent {
        val streak = userContext.currentStreak.reflectionStreak.current

        val titles = when {
            streak >= 30 -> listOf("Protect that ${streak}-day streak!", "Don't lose ${streak} days of progress!")
            streak >= 7 -> listOf("Your ${streak}-day streak is at risk", "Keep the streak alive!")
            else -> listOf("Streak reminder", "Don't forget to journal")
        }

        val bodies = when {
            userContext.currentStreak.reflectionStreak.gracePeriodAvailable -> listOf(
                "You have a grace day available if you need it.",
                "Even a quick entry counts!"
            )
            streak >= 30 -> listOf(
                "Just a quick entry keeps it going!",
                "You've worked hard for this - don't let it slip!"
            )
            else -> listOf(
                "A few words is all it takes.",
                "Quick check-in to maintain your streak."
            )
        }

        return IntelligentNotificationContent(
            title = titles.random(),
            body = bodies.random(),
            deepLink = "prody://journal/new"
        )
    }

    private fun generateComebackContent(userContext: UserContext): IntelligentNotificationContent {
        val daysAway = userContext.daysSinceLastEntry

        val titles = listOf(
            "We miss you!",
            "Your journal awaits",
            "Ready to come back?"
        )

        val bodies = when {
            daysAway > 14 -> listOf(
                "It's been a while. No guilt, just possibilities.",
                "Fresh start whenever you're ready.",
                "Your journal is here when you need it."
            )
            daysAway > 7 -> listOf(
                "A week is a long time. Checking in.",
                "How have things been?",
                "Ready to pick up where you left off?"
            )
            else -> listOf(
                "It's been a few days. Everything okay?",
                "Your journal misses you!",
                "Ready to reflect?"
            )
        }

        return IntelligentNotificationContent(
            title = titles.random(),
            body = bodies.random(),
            deepLink = "prody://home"
        )
    }

    private suspend fun generateMemoryContent(userContext: UserContext): IntelligentNotificationContent {
        val anniversaryMemories = memoryEngine.getAnniversaryMemories()
        val memory = anniversaryMemories.firstOrNull()

        return if (memory != null) {
            IntelligentNotificationContent(
                title = "A memory from ${memory.memory.surfaceReason}",
                body = memory.memory.preview.take(100) + "...",
                deepLink = "prody://memory/${memory.memory.id}"
            )
        } else {
            IntelligentNotificationContent(
                title = "Remember this?",
                body = "You have memories worth revisiting.",
                deepLink = "prody://memories"
            )
        }
    }

    private fun generateFutureMessageContent(): IntelligentNotificationContent {
        return IntelligentNotificationContent(
            title = "A message from past you!",
            body = "You sent yourself something special. Ready to open it?",
            deepLink = "prody://future-messages"
        )
    }

    private fun generateWeeklySummaryContent(userContext: UserContext): IntelligentNotificationContent {
        val entriesThisWeek = userContext.totalEntries // Simplified - would need actual weekly count
        val streakInfo = userContext.currentStreak

        return IntelligentNotificationContent(
            title = "Your week in review",
            body = when {
                entriesThisWeek >= 7 -> "Amazing week! You journaled every day."
                entriesThisWeek >= 4 -> "Great consistency this week!"
                entriesThisWeek > 0 -> "$entriesThisWeek entries this week. Every one counts!"
                else -> "A quiet week. Ready for a fresh start?"
            },
            deepLink = "prody://stats/weekly"
        )
    }

    private fun generateAchievementContent(userContext: UserContext): IntelligentNotificationContent {
        val milestone = userContext.recentWins.firstOrNull()

        return if (milestone != null) {
            IntelligentNotificationContent(
                title = milestone.title,
                body = milestone.description,
                deepLink = "prody://achievements"
            )
        } else {
            IntelligentNotificationContent(
                title = "Achievement unlocked!",
                body = "You've hit a new milestone!",
                deepLink = "prody://achievements"
            )
        }
    }

    private fun generateHavenCheckInContent(userContext: UserContext): IntelligentNotificationContent {
        return IntelligentNotificationContent(
            title = "Haven is here for you",
            body = when {
                userContext.isStruggling -> "Sometimes it helps to talk. Haven is ready when you are."
                else -> "A moment of support is always available."
            },
            deepLink = "prody://haven"
        )
    }

    private fun generateGentleNudgeContent(userContext: UserContext): IntelligentNotificationContent {
        val nudges = when {
            userContext.isStruggling -> listOf(
                IntelligentNotificationContent(
                    title = "Just checking in",
                    body = "How are you doing today?",
                    deepLink = "prody://journal/new"
                ),
                IntelligentNotificationContent(
                    title = "No pressure",
                    body = "Your space is here when you need it.",
                    deepLink = "prody://home"
                )
            )
            userContext.preferredJournalTime == TimeOfDay.MORNING && userContext.timeOfDay == TimeOfDay.MORNING -> listOf(
                IntelligentNotificationContent(
                    title = "Your favorite time to write",
                    body = "Morning is when you usually reflect best.",
                    deepLink = "prody://journal/new"
                )
            )
            else -> listOf(
                IntelligentNotificationContent(
                    title = "A thought for you",
                    body = "Have a moment? Your journal awaits.",
                    deepLink = "prody://journal/new"
                ),
                IntelligentNotificationContent(
                    title = "Quick check-in",
                    body = "How's your day going?",
                    deepLink = "prody://journal/new"
                )
            )
        }

        return nudges.random()
    }

    // =============================================================================================
    // PRIVATE HELPERS - SCHEDULING
    // =============================================================================================

    private suspend fun shouldScheduleMorningReminder(userContext: UserContext): Boolean {
        // Don't schedule if disabled
        val morningEnabled = preferencesManager.morningReminderEnabled.first()
        if (!morningEnabled) return false

        // Don't schedule in first week (they have their own journey)
        if (userContext.isInFirstWeek) return false

        // Don't schedule if user never journals in morning
        if (userContext.preferredJournalTime != null &&
            userContext.preferredJournalTime != TimeOfDay.MORNING
        ) {
            return Random.nextFloat() < 0.3f // Still 30% chance to try
        }

        return true
    }

    private suspend fun shouldScheduleEveningReflection(userContext: UserContext): Boolean {
        // Don't schedule if disabled
        val eveningEnabled = preferencesManager.eveningReflectionEnabled.first()
        if (!eveningEnabled) return false

        // Don't schedule in first week
        if (userContext.isInFirstWeek) return false

        // Higher chance if evening is their preferred time
        if (userContext.preferredJournalTime == TimeOfDay.EVENING) {
            return true
        }

        return Random.nextFloat() < 0.5f
    }

    private fun shouldScheduleStreakReminder(userContext: UserContext): Boolean {
        // Only if streak exists and is at risk
        return userContext.currentStreak.reflectionStreak.current > 0 &&
            userContext.currentStreak.reflectionStreak.isAtRisk &&
            userContext.daysSinceLastEntry > 0
    }

    private fun getMaxNotificationsForDay(userContext: UserContext): Int {
        return when {
            userContext.isStruggling -> 1 // Minimal notifications
            userContext.isInFirstWeek -> 2 // Guided but not overwhelming
            userContext.engagementLevel == EngagementLevel.DAILY -> 3 // Engaged users can handle more
            else -> 2 // Default
        }
    }

    private suspend fun isNotificationsEnabled(): Boolean {
        return preferencesManager.notificationsEnabled.first()
    }

    private suspend fun isInQuietHours(): Boolean {
        val quietModeEnabled = preferencesManager.quietModeEnabled.first()
        if (!quietModeEnabled) return false

        val quietModeStart = preferencesManager.quietModeStart.first()
        val quietModeEnd = preferencesManager.quietModeEnd.first()

        val now = LocalTime.now()
        val startTime = LocalTime.of(quietModeStart, 0)
        val endTime = LocalTime.of(quietModeEnd, 0)

        return if (startTime.isBefore(endTime)) {
            // Normal range (e.g., 22:00 to 07:00 doesn't cross midnight)
            now.isAfter(startTime) && now.isBefore(endTime)
        } else {
            // Crosses midnight (e.g., 22:00 to 07:00)
            now.isAfter(startTime) || now.isBefore(endTime)
        }
    }

    private suspend fun getNextActiveTime(): LocalDateTime {
        val quietModeEnd = preferencesManager.quietModeEnd.first()
        val now = LocalDateTime.now()

        var nextActive = now.withHour(quietModeEnd).withMinute(0).withSecond(0)
        if (nextActive.isBefore(now)) {
            nextActive = nextActive.plusDays(1)
        }

        return nextActive
    }

    private fun isTooFrequent(context: NotificationContext): Boolean {
        val lastNotification = context.lastNotificationSentAt ?: return false
        val minutesSinceLastNotification = ChronoUnit.MINUTES.between(lastNotification, LocalDateTime.now())
        return minutesSinceLastNotification < MIN_NOTIFICATION_INTERVAL_MINUTES
    }

    private fun isUserCurrentlyActive(context: NotificationContext): Boolean {
        val lastAppOpen = context.lastAppOpenAt ?: return false
        val minutesSinceLastOpen = ChronoUnit.MINUTES.between(lastAppOpen, LocalDateTime.now())
        return minutesSinceLastOpen < 5 // User was active in last 5 minutes
    }

    // =============================================================================================
    // CONSTANTS
    // =============================================================================================

    companion object {
        private const val MIN_NOTIFICATION_INTERVAL_MINUTES = 60 // At least 1 hour between notifications
        private const val MINIMUM_RELEVANCE_THRESHOLD = 0.4f
        private const val MINIMUM_OPEN_RATE_THRESHOLD = 0.1f // Skip if user opens less than 10% of this type

        private val SUPPORTIVE_NOTIFICATION_TYPES = setOf(
            NotificationType.HAVEN_CHECK_IN,
            NotificationType.GENTLE_NUDGE
        )
    }
}

// ================================================================================================
// NOTIFICATION INTELLIGENCE MODELS
// ================================================================================================

/**
 * A scheduled notification for the day.
 */
data class ScheduledNotification(
    val type: NotificationType,
    val scheduledTime: LocalDateTime,
    val content: IntelligentNotificationContent,
    val priority: NotificationPriority
)

enum class NotificationPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

/**
 * Daily notification schedule.
 */
data class DailyNotificationSchedule(
    val date: java.time.LocalDate,
    val notifications: List<ScheduledNotification>,
    val maxNotificationsToday: Int
)
