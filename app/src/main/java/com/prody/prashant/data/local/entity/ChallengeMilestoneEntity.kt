package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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