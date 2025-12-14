package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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