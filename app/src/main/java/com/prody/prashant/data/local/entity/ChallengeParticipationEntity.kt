package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for tracking individual user's participation in challenges.
 */
@Entity(
    tableName = "challenge_participation",
    foreignKeys = [
        ForeignKey(
            entity = ChallengeEntity::class,
            parentColumns = ["id"],
            childColumns = ["challengeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["challengeId"]),
        Index(value = ["userId", "challengeId"]),
        Index(value = ["date"])
    ]
)
data class ChallengeParticipationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local", // Multi-user support
    val challengeId: String,
    val date: Long,
    val progressMade: Int, // Progress made on this date
    val activityType: String, // journal, word, meditation, etc.
    val activityId: String? = null, // Reference to specific activity (journal entry ID, etc.)
    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null
)