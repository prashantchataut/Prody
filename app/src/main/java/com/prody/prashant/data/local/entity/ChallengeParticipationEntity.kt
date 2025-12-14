package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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