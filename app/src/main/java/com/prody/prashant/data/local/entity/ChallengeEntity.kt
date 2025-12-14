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
