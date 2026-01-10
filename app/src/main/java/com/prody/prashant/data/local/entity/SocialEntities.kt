package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Social Accountability Circles - Entity Layer
 *
 * Privacy-first social feature for sharing progress and supporting friends.
 * NO actual journal content is ever shared - only privacy-safe statistics.
 */

@Entity(
    tableName = "accountability_circles",
    indices = [
        Index(value = ["createdBy"]),
        Index(value = ["inviteCode"], unique = true),
        Index(value = ["isActive"])
    ]
)
data class CircleEntity(
    @PrimaryKey
    val id: String, // UUID
    val name: String,
    val description: String? = null,
    val createdBy: String, // userId
    val createdAt: Long = System.currentTimeMillis(),
    val inviteCode: String, // 6-char code for joining
    val isActive: Boolean = true,
    val memberCount: Int = 1,
    val colorTheme: String = "default", // Circle color theme
    val iconEmoji: String = "🌟",
    // Privacy settings
    val allowNudges: Boolean = true,
    val allowChallenges: Boolean = true,
    val maxMembers: Int = 10,
    // Metadata
    val lastActivityAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "circle_members",
    indices = [
        Index(value = ["circleId"]),
        Index(value = ["userId"]),
        Index(value = ["circleId", "userId"], unique = true),
        Index(value = ["isActive"])
    ]
)
data class CircleMemberEntity(
    @PrimaryKey
    val id: String, // Composite: circleId_userId
    val circleId: String,
    val userId: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val joinedAt: Long = System.currentTimeMillis(),
    val role: String = "member", // "owner", "admin", "member"
    val isActive: Boolean = true,
    val lastActiveAt: Long = System.currentTimeMillis(),
    // Cached stats for quick display
    val currentStreak: Int = 0,
    val totalEntries: Int = 0,
    val lastEntryAt: Long? = null
)

@Entity(
    tableName = "circle_updates",
    indices = [
        Index(value = ["circleId"]),
        Index(value = ["userId"]),
        Index(value = ["updateType"]),
        Index(value = ["createdAt"])
    ]
)
data class CircleUpdateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val circleId: String,
    val userId: String,
    val updateType: String, // "streak", "milestone", "check_in", "encouragement"
    val content: String, // Privacy-safe content (stats, not journal text)
    val metadata: String? = null, // JSON for type-specific data
    val createdAt: Long = System.currentTimeMillis(),
    val reactionsJson: String = "{}", // Map of emoji to list of userIds
    val reactionCount: Int = 0
)

@Entity(
    tableName = "circle_nudges",
    indices = [
        Index(value = ["circleId"]),
        Index(value = ["fromUserId"]),
        Index(value = ["toUserId"]),
        Index(value = ["isRead"]),
        Index(value = ["createdAt"])
    ]
)
data class CircleNudgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val circleId: String,
    val fromUserId: String,
    val fromDisplayName: String,
    val toUserId: String,
    val nudgeType: String, // "encourage", "celebrate", "miss_you", "proud"
    val message: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val respondedAt: Long? = null
)

@Entity(
    tableName = "circle_challenges",
    indices = [
        Index(value = ["circleId"]),
        Index(value = ["createdBy"]),
        Index(value = ["isActive"]),
        Index(value = ["startDate"]),
        Index(value = ["endDate"])
    ]
)
data class CircleChallengeEntity(
    @PrimaryKey
    val id: String, // UUID
    val circleId: String,
    val title: String,
    val description: String,
    val startDate: Long,
    val endDate: Long,
    val targetType: String, // "streak", "entries", "meditation_minutes", "words"
    val targetValue: Int,
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val participantsJson: String = "[]", // List of userIds who joined
    val progressJson: String = "{}", // Map of userId to progress value
    val isActive: Boolean = true,
    val completedByJson: String = "[]" // List of userIds who completed
)

@Entity(
    tableName = "circle_privacy_settings",
    indices = [
        Index(value = ["userId", "circleId"], unique = true)
    ]
)
data class CirclePrivacySettingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val circleId: String, // "global" for default settings
    val shareStreakCount: Boolean = true,
    val shareEntryCount: Boolean = true,
    val shareMeditationStats: Boolean = true,
    val shareChallengeParticipation: Boolean = true,
    val allowNudgesFromMembers: Boolean = true,
    val showOnlineStatus: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "circle_notifications",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["circleId"]),
        Index(value = ["isRead"]),
        Index(value = ["createdAt"])
    ]
)
data class CircleNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val circleId: String,
    val notificationType: String, // "nudge", "milestone", "challenge_update", "new_member", "encouragement"
    val title: String,
    val message: String,
    val actionType: String? = null, // "open_circle", "view_challenge", "respond_nudge"
    val actionData: String? = null, // JSON with action details
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "circle_member_stats_cache",
    indices = [
        Index(value = ["userId", "circleId"], unique = true),
        Index(value = ["lastUpdated"])
    ]
)
data class CircleMemberStatsCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val circleId: String,
    // Cached stats based on privacy settings
    val currentStreak: Int? = null,
    val longestStreak: Int? = null,
    val totalEntries: Int? = null,
    val totalWords: Int? = null,
    val meditationMinutes: Int? = null,
    val lastActiveAt: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
