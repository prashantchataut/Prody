package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a community challenge that all users can participate in.
 */
@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val type: String, // journaling, vocabulary, streak, meditation, mixed
    val iconName: String = "challenge_default",
    val startDate: Long,
    val endDate: Long,
    val targetCount: Int, // e.g., 30 journal entries, 100 words learned
    val currentUserProgress: Int = 0,
    val isJoined: Boolean = false,
    val joinedAt: Long? = null,
    val totalParticipants: Int = 0,
    val communityProgress: Int = 0, // Aggregate progress across all participants
    val communityTarget: Int = 0, // Community-wide target (e.g., 10,000 total entries)
    val rewardPoints: Int = 0,
    val rewardBadgeId: String? = null,
    val rewardTitle: String? = null,
    val difficulty: String = "medium", // easy, medium, hard, extreme
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entity for tracking challenge milestones (community achievements within challenges).
 */
@Entity(tableName = "challenge_milestones")
data class ChallengeMilestoneEntity(
    @PrimaryKey
    val id: String,
    val challengeId: String,
    val title: String,
    val description: String,
    val targetProgress: Int, // Percentage or count to reach
    val isPercentage: Boolean = true, // If true, targetProgress is percentage of communityTarget
    val isReached: Boolean = false,
    val reachedAt: Long? = null,
    val rewardPoints: Int = 0,
    val celebrationMessage: String = "",
    val orderIndex: Int = 0
)

/**
 * Entity for tracking individual user's participation in challenges.
 */
@Entity(tableName = "challenge_participation")
data class ChallengeParticipationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val challengeId: String,
    val date: Long,
    val progressMade: Int, // Progress made on this date
    val activityType: String, // journal, word, meditation, etc.
    val activityId: String? = null // Reference to specific activity (journal entry ID, etc.)
)

/**
 * Entity for challenge leaderboard entries.
 */
@Entity(tableName = "challenge_leaderboard")
data class ChallengeLeaderboardEntity(
    @PrimaryKey
    val odId: String, // Unique participant ID
    val challengeId: String,
    val displayName: String,
    val avatarId: String = "default",
    val progress: Int = 0,
    val rank: Int = 0,
    val isCurrentUser: Boolean = false,
    val lastActiveAt: Long = System.currentTimeMillis()
)
