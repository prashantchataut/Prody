package com.prody.prashant.notification

import com.prody.prashant.data.local.entity.FutureMessageEntity
import com.prody.prashant.data.local.entity.MessageAnniversaryEntity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Warm, human notification copy for Time Capsule features.
 *
 * These notifications should feel like a friend reminding you
 * about something meaningful, not a robotic system alert.
 *
 * Key principles:
 * - Always warm and personal
 * - Emphasize the emotional connection to past/future self
 * - Create anticipation and curiosity
 * - Never use robotic phrases like "Your future message is ready"
 */
object TimeCapsuleNotifications {

    // ==================== MESSAGE DELIVERY ====================

    /**
     * Get notification title when a time capsule is ready to open
     */
    fun getDeliveryTitle(message: FutureMessageEntity): String {
        return when (message.category) {
            "goal" -> "A goal from your past self"
            "promise" -> "A promise you made"
            "motivation" -> "Words you wrote to keep going"
            "reminder" -> "Something you wanted to remember"
            else -> "Your past self has something for you"
        }
    }

    /**
     * Get notification body text for delivery
     */
    fun getDeliveryMessage(message: FutureMessageEntity): String {
        val timeAgo = getTimeAgoText(message.createdAt)

        return when (message.category) {
            "goal" -> "$timeAgo, you set an intention. It's time to see how far you've come."
            "promise" -> "You wrote this on ${getDateText(message.createdAt)}. Ready to read what you promised yourself?"
            "motivation" -> "$timeAgo, you knew you'd need these words. They're waiting for you."
            "reminder" -> "On ${getDateText(message.createdAt)}, you wanted to remember something important."
            else -> "A message from $timeAgo just arrived. Your past self is waiting to talk."
        }
    }

    /**
     * Get short, intriguing notification text (for collapsed notification)
     */
    fun getDeliveryShortText(message: FutureMessageEntity): String {
        val timeAgo = getTimeAgoText(message.createdAt)
        return "From $timeAgo ago: \"${message.content.take(60)}${if (message.content.length > 60) "..." else ""}\""
    }

    // ==================== ANNIVERSARY REMINDERS ====================

    /**
     * Get anniversary notification title
     */
    fun getAnniversaryTitle(anniversary: MessageAnniversaryEntity): String {
        return anniversary.getNotificationTitle()
    }

    /**
     * Get anniversary notification message
     */
    fun getAnniversaryMessage(anniversary: MessageAnniversaryEntity): String {
        return anniversary.getNotificationMessage()
    }

    /**
     * Get anniversary preview text
     */
    fun getAnniversaryPreview(anniversary: MessageAnniversaryEntity): String {
        val preview = anniversary.originalContent.take(80)
        return "\"$preview${if (anniversary.originalContent.length > 80) "..." else ""}\""
    }

    // ==================== REMINDER TO READ ====================

    /**
     * Get gentle reminder for unread messages (sent 1 day after delivery)
     */
    fun getUnreadReminderTitle(): String {
        val variants = listOf(
            "Still waiting for you",
            "Your message is here",
            "Don't forget to open this"
        )
        return variants.random()
    }

    /**
     * Get reminder message text
     */
    fun getUnreadReminderMessage(message: FutureMessageEntity): String {
        val timeAgo = getTimeAgoText(message.createdAt)

        val variants = listOf(
            "The message you wrote $timeAgo is still waiting to be opened.",
            "You took the time to write this $timeAgo. Want to see what you said?",
            "Your past self reached out $timeAgo. Ready to listen?",
            "There's a message from $timeAgo that hasn't been read yet."
        )
        return variants.random()
    }

    // ==================== REPLY ENCOURAGEMENT ====================

    /**
     * Get encouragement to reply to past self (sent 1 hour after reading)
     */
    fun getReplyEncouragementTitle(): String {
        val variants = listOf(
            "Want to write back?",
            "How does it feel to read that?",
            "Reply to your past self"
        )
        return variants.random()
    }

    /**
     * Get reply encouragement message
     */
    fun getReplyEncouragementMessage(): String {
        val variants = listOf(
            "You just read a message from your past self. How do those words land now?",
            "Sometimes it helps to write back. What would you say to who you were then?",
            "Take a moment to reflect. Has anything changed since you wrote this?",
            "Your past self spoke. Want to tell them how things turned out?"
        )
        return variants.random()
    }

    // ==================== HELPER FUNCTIONS ====================

    /**
     * Get human-readable time ago text
     */
    private fun getTimeAgoText(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {
            years > 0 -> {
                val y = years.toInt()
                when {
                    y == 1 -> "A year"
                    y == 2 -> "Two years"
                    y == 3 -> "Three years"
                    else -> "$y years"
                }
            }
            months > 0 -> {
                val m = months.toInt()
                when {
                    m == 1 -> "A month"
                    m < 6 -> "${monthToWords(m)} months"
                    m < 12 -> "$m months"
                    else -> "Almost a year"
                }
            }
            weeks > 0 -> {
                val w = weeks.toInt()
                when {
                    w == 1 -> "A week"
                    w == 2 -> "Two weeks"
                    w == 3 -> "Three weeks"
                    else -> "$w weeks"
                }
            }
            days > 0 -> {
                val d = days.toInt()
                when {
                    d == 1 -> "Yesterday"
                    d < 7 -> "$d days"
                    else -> "A week"
                }
            }
            hours > 0 -> {
                val h = hours.toInt()
                when {
                    h == 1 -> "An hour"
                    h < 24 -> "$h hours"
                    else -> "A day"
                }
            }
            else -> "Just now"
        }
    }

    /**
     * Convert number to words for months (2 = "two", 3 = "three", etc.)
     */
    private fun monthToWords(months: Int): String {
        return when (months) {
            2 -> "two"
            3 -> "three"
            4 -> "four"
            5 -> "five"
            else -> months.toString()
        }
    }

    /**
     * Get human-readable date text
     */
    private fun getDateText(timestamp: Long): String {
        val date = Date(timestamp)
        val now = Date()

        val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

        val messageYear = yearFormat.format(date)
        val currentYear = yearFormat.format(now)

        return if (messageYear == currentYear) {
            dateFormat.format(date)
        } else {
            "${dateFormat.format(date)}, $messageYear"
        }
    }

    /**
     * Get duration text between two dates (e.g., "3 months", "1 year")
     */
    fun getDurationText(startTimestamp: Long, endTimestamp: Long): String {
        val diff = abs(endTimestamp - startTimestamp)
        val days = diff / (1000 * 60 * 60 * 24)
        val months = days / 30
        val years = days / 365

        return when {
            years > 0 -> "${years.toInt()} ${if (years.toInt() == 1) "year" else "years"}"
            months > 0 -> "${months.toInt()} ${if (months.toInt() == 1) "month" else "months"}"
            days > 0 -> "${days.toInt()} ${if (days.toInt() == 1) "day" else "days"}"
            else -> "today"
        }
    }

    /**
     * Get encouraging actions for notification buttons
     */
    fun getActionButtonText(category: String): String {
        return when (category) {
            "goal" -> "See Your Progress"
            "promise" -> "Open Promise"
            "motivation" -> "Read Message"
            "reminder" -> "See Reminder"
            else -> "Open Time Capsule"
        }
    }
}
