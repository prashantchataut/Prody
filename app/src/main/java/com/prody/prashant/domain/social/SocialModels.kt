package com.prody.prashant.domain.social

import androidx.compose.ui.graphics.Color
import com.prody.prashant.ui.theme.*

/**
 * Domain models for Social Accountability Circles.
 *
 * These models represent the business logic layer, transforming entity data
 * into user-friendly, privacy-aware representations.
 */

data class Circle(
    val id: String,
    val name: String,
    val description: String?,
    val members: List<CircleMember>,
    val inviteCode: String,
    val colorTheme: CircleTheme,
    val iconEmoji: String,
    val yourRole: MemberRole,
    val memberCount: Int,
    val lastActivityAt: Long,
    val createdAt: Long,
    val isActive: Boolean = true,
    val allowNudges: Boolean = true,
    val allowChallenges: Boolean = true,
    val maxMembers: Int = 10
) {
    val isFull: Boolean
        get() = memberCount >= maxMembers

    val isOwner: Boolean
        get() = yourRole == MemberRole.OWNER

    val canManageMembers: Boolean
        get() = yourRole == MemberRole.OWNER || yourRole == MemberRole.ADMIN
}

data class CircleMember(
    val id: String,
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
    val currentStreak: Int,
    val totalEntries: Int,
    val lastActiveAt: Long,
    val joinedAt: Long,
    val role: MemberRole,
    val isActive: Boolean = true
) {
    val streakDisplay: String
        get() = when {
            currentStreak == 0 -> "No streak"
            currentStreak == 1 -> "1 day"
            else -> "$currentStreak days"
        }

    val isOnFire: Boolean
        get() = currentStreak >= 7

    val streakMilestone: StreakMilestone?
        get() = StreakMilestone.fromStreak(currentStreak)
}

enum class MemberRole {
    OWNER,
    ADMIN,
    MEMBER;

    val displayName: String
        get() = when (this) {
            OWNER -> "Owner"
            ADMIN -> "Admin"
            MEMBER -> "Member"
        }
}

data class CircleUpdate(
    val id: Long,
    val circleId: String,
    val member: CircleMember,
    val type: UpdateType,
    val content: String,
    val timestamp: Long,
    val reactions: Map<String, List<String>>, // emoji -> userIds
    val metadata: Map<String, Any>? = null
) {
    val totalReactions: Int
        get() = reactions.values.sumOf { it.size }

    val hasReactions: Boolean
        get() = reactions.isNotEmpty()

    fun hasUserReacted(userId: String): Boolean {
        return reactions.values.any { it.contains(userId) }
    }

    fun getUserReaction(userId: String): String? {
        return reactions.entries.firstOrNull { it.value.contains(userId) }?.key
    }
}

enum class UpdateType(val emoji: String, val color: Color) {
    STREAK_MILESTONE("🔥", StreakColor),
    ENTRY_COUNT("📝", ProdyAccentGreen),
    CHECK_IN("✅", ProdySuccess),
    ENCOURAGEMENT("💪", MoodMotivated),
    CHALLENGE_PROGRESS("🎯", ChallengeActive),
    MILESTONE_100("💯", LeaderboardGold),
    MILESTONE_365("🎉", LeaderboardGold),
    BACK_FROM_BREAK("👋", ProdyInfo);

    companion object {
        fun fromString(value: String): UpdateType {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: CHECK_IN
        }
    }
}

data class Nudge(
    val id: Long,
    val circleId: String,
    val from: CircleMember,
    val toUserId: String,
    val type: NudgeType,
    val message: String?,
    val timestamp: Long,
    val isRead: Boolean = false,
    val respondedAt: Long? = null
) {
    val displayMessage: String
        get() = message ?: type.defaultMessage
}

enum class NudgeType(val emoji: String, val defaultMessage: String, val color: Color) {
    ENCOURAGE("💪", "You've got this! Keep going!", MoodMotivated),
    CELEBRATE("🎉", "Celebrating your amazing progress!", LeaderboardGold),
    MISS_YOU("👋", "We miss you in the circle!", ProdyInfo),
    PROUD("⭐", "So proud of your commitment!", ProdySuccess),
    KEEP_IT_UP("🔥", "Keep that streak alive!", StreakColor),
    AWESOME("✨", "You're doing awesome!", ProdyAccentGreen);

    companion object {
        fun fromString(value: String): NudgeType {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: ENCOURAGE
        }
    }
}

data class CircleChallenge(
    val id: String,
    val circleId: String,
    val title: String,
    val description: String,
    val startDate: Long,
    val endDate: Long,
    val targetType: ChallengeTargetType,
    val targetValue: Int,
    val createdBy: String,
    val createdAt: Long,
    val participants: List<String>, // userIds
    val progress: Map<String, Int>, // userId -> progress
    val completedBy: List<String>, // userIds who completed
    val isActive: Boolean = true
) {
    val daysRemaining: Int
        get() {
            val now = System.currentTimeMillis()
            val diff = endDate - now
            return (diff / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
        }

    val isEnded: Boolean
        get() = System.currentTimeMillis() > endDate

    val isStarted: Boolean
        get() = System.currentTimeMillis() >= startDate

    val participantCount: Int
        get() = participants.size

    val completionRate: Float
        get() = if (participants.isEmpty()) 0f
        else completedBy.size.toFloat() / participants.size.toFloat()

    fun getUserProgress(userId: String): Int {
        return progress[userId] ?: 0
    }

    fun getUserProgressPercent(userId: String): Float {
        val userProgress = getUserProgress(userId)
        return if (targetValue > 0) {
            (userProgress.toFloat() / targetValue.toFloat()).coerceIn(0f, 1f)
        } else 0f
    }

    fun hasUserCompleted(userId: String): Boolean {
        return completedBy.contains(userId)
    }

    fun isUserParticipant(userId: String): Boolean {
        return participants.contains(userId)
    }
}

enum class ChallengeTargetType(val displayName: String, val unit: String, val emoji: String) {
    STREAK("Streak Days", "days", "🔥"),
    ENTRIES("Journal Entries", "entries", "📝"),
    MEDITATION_MINUTES("Meditation Minutes", "minutes", "🧘"),
    WORDS("Words Written", "words", "✍️"),
    RITUALS("Daily Rituals", "rituals", "🌅");

    companion object {
        fun fromString(value: String): ChallengeTargetType {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: ENTRIES
        }
    }
}

data class ChallengeLeaderboard(
    val challenge: CircleChallenge,
    val rankings: List<ChallengeRanking>
)

data class ChallengeRanking(
    val rank: Int,
    val member: CircleMember,
    val progress: Int,
    val progressPercent: Float,
    val isCompleted: Boolean
)

enum class CircleTheme(
    val displayName: String,
    val primaryColor: Color,
    val backgroundColor: Color,
    val textColor: Color
) {
    DEFAULT("Forest", Color(0xFF36F97F), Color(0xFF1A3331), Color(0xFFFFFFFF)),
    OCEAN("Ocean", Color(0xFF42A5F5), Color(0xFF1A2D40), Color(0xFFFFFFFF)),
    SUNSET("Sunset", Color(0xFFFF7043), Color(0xFF4A2520), Color(0xFFFFFFFF)),
    LAVENDER("Lavender", Color(0xFFAB47BC), Color(0xFF3A2440), Color(0xFFFFFFFF)),
    GOLD("Gold", Color(0xFFD4AF37), Color(0xFF3A3320), Color(0xFF000000)),
    RUBY("Ruby", Color(0xFFE53935), Color(0xFF4A2020), Color(0xFFFFFFFF)),
    MINT("Mint", Color(0xFF26A69A), Color(0xFF1A3330), Color(0xFFFFFFFF)),
    AMBER("Amber", Color(0xFFFFB300), Color(0xFF3A3020), Color(0xFF000000));

    companion object {
        fun fromString(value: String): CircleTheme {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: DEFAULT
        }
    }
}

data class PrivacySettings(
    val shareStreakCount: Boolean = true,
    val shareEntryCount: Boolean = true,
    val shareMeditationStats: Boolean = true,
    val shareChallengeParticipation: Boolean = true,
    val allowNudgesFromMembers: Boolean = true,
    val showOnlineStatus: Boolean = false
) {
    val isFullyPrivate: Boolean
        get() = !shareStreakCount && !shareEntryCount &&
                !shareMeditationStats && !shareChallengeParticipation

    val isFullyOpen: Boolean
        get() = shareStreakCount && shareEntryCount &&
                shareMeditationStats && shareChallengeParticipation
}

data class CircleNotification(
    val id: Long,
    val circleId: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val actionType: NotificationAction?,
    val actionData: Map<String, String>?,
    val isRead: Boolean,
    val createdAt: Long
)

enum class NotificationType(val emoji: String) {
    NUDGE("💪"),
    MILESTONE("🎉"),
    CHALLENGE_UPDATE("🎯"),
    NEW_MEMBER("👋"),
    ENCOURAGEMENT("⭐"),
    CHALLENGE_COMPLETED("✅"),
    STREAK_BROKEN("💔"),
    BACK_FROM_BREAK("🎊");

    companion object {
        fun fromString(value: String): NotificationType {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: ENCOURAGEMENT
        }
    }
}

enum class NotificationAction {
    OPEN_CIRCLE,
    VIEW_CHALLENGE,
    RESPOND_NUDGE,
    VIEW_UPDATE,
    NONE;

    companion object {
        fun fromString(value: String?): NotificationAction? {
            if (value == null) return null
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}

sealed class StreakMilestone(
    val days: Int,
    val emoji: String,
    val title: String,
    val message: String,
    val color: Color
) {
    object Week : StreakMilestone(
        7,
        "🔥",
        "7 Day Streak!",
        "One week of consistency!",
        StreakWeekMilestone
    )

    object TwoWeeks : StreakMilestone(
        14,
        "🔥🔥",
        "14 Day Streak!",
        "Two weeks strong!",
        StreakWeekMilestone
    )

    object Month : StreakMilestone(
        30,
        "🌟",
        "30 Day Streak!",
        "A full month of dedication!",
        StreakMonthMilestone
    )

    object TwoMonths : StreakMilestone(
        60,
        "⭐",
        "60 Day Streak!",
        "Two months of commitment!",
        StreakMonthMilestone
    )

    object HundredDays : StreakMilestone(
        100,
        "💯",
        "100 Day Streak!",
        "Triple digits of excellence!",
        StreakMilestone100
    )

    object Year : StreakMilestone(
        365,
        "🏆",
        "365 Day Streak!",
        "A full year of growth!",
        StreakMilestone365
    )

    companion object {
        val milestones = listOf(Week, TwoWeeks, Month, TwoMonths, HundredDays, Year)

        fun fromStreak(streak: Int): StreakMilestone? {
            return milestones.lastOrNull { streak >= it.days }
        }

        fun isStreakMilestone(streak: Int): Boolean {
            return milestones.any { it.days == streak }
        }

        fun getNextMilestone(currentStreak: Int): StreakMilestone? {
            return milestones.firstOrNull { it.days > currentStreak }
        }
    }
}

data class MemberStats(
    val userId: String,
    val currentStreak: Int?,
    val longestStreak: Int?,
    val totalEntries: Int?,
    val totalWords: Int?,
    val meditationMinutes: Int?,
    val lastActiveAt: Long?
) {
    fun getVisibleStats(privacySettings: PrivacySettings): MemberStats {
        return MemberStats(
            userId = userId,
            currentStreak = if (privacySettings.shareStreakCount) currentStreak else null,
            longestStreak = if (privacySettings.shareStreakCount) longestStreak else null,
            totalEntries = if (privacySettings.shareEntryCount) totalEntries else null,
            totalWords = if (privacySettings.shareEntryCount) totalWords else null,
            meditationMinutes = if (privacySettings.shareMeditationStats) meditationMinutes else null,
            lastActiveAt = lastActiveAt
        )
    }
}

data class CircleSummary(
    val circle: Circle,
    val unreadNotifications: Int,
    val recentActivity: List<CircleUpdate>,
    val activeChallenges: List<CircleChallenge>
)
