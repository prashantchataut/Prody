package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard")
data class LeaderboardEntryEntity(
    @PrimaryKey
    val odId: String, // Unique peer ID
    val displayName: String,
    val avatarId: String = "default",
    val titleId: String = "newcomer",
    val totalPoints: Int = 0,
    val weeklyPoints: Int = 0,
    val currentStreak: Int = 0,
    val rank: Int = 0,
    val previousRank: Int = 0, // To show rank changes
    val isCurrentUser: Boolean = false,
    val lastActiveAt: Long = System.currentTimeMillis(),
    val boostsReceived: Int = 0,
    val congratsReceived: Int = 0
)

@Entity(tableName = "peer_interactions")
data class PeerInteractionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val peerId: String,
    val interactionType: String, // boost, congrats
    val timestamp: Long = System.currentTimeMillis(),
    val message: String? = null
)

@Entity(tableName = "motivational_messages")
data class MotivationalMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderId: String,
    val senderName: String,
    val message: String,
    val messageType: String, // boost, congrats, encouragement
    val isRead: Boolean = false,
    val receivedAt: Long = System.currentTimeMillis()
)
