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
    val bannerId: String = "default_dawn", // User's selected profile banner
    val totalPoints: Int = 0,
    val weeklyPoints: Int = 0,
    val currentStreak: Int = 0,
    val rank: Int = 0,
    val previousRank: Int = 0, // To show rank changes
    val isCurrentUser: Boolean = false,
    val lastActiveAt: Long = System.currentTimeMillis(),
    val boostsReceived: Int = 0,
    val congratsReceived: Int = 0,
    val respectsReceived: Int = 0 // New "Respect" interaction type
)
