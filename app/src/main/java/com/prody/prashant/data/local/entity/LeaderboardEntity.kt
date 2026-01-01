package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "leaderboard",
    indices = [
        Index(value = ["totalPoints"]),
        Index(value = ["weeklyPoints"]),
        Index(value = ["isCurrentUser"])
    ]
)
data class LeaderboardEntryEntity(
    @PrimaryKey
    val odId: String, // Unique peer ID
    val displayName: String,
    val avatarId: String = "default",
    val titleId: String = "newcomer",
    val bannerId: String = "default_dawn", // Gamification 2.0: Banner next to name
    val frameId: String = "default_clean", // Identity 3.0: Avatar frame
    val accentColorId: String = "default_green", // Identity 3.0: Accent color
    val totalPoints: Int = 0,
    val weeklyPoints: Int = 0,
    val currentStreak: Int = 0,
    val rank: Int = 0,
    val previousRank: Int = 0, // To show rank changes
    val isCurrentUser: Boolean = false,
    val lastActiveAt: Long = System.currentTimeMillis(),
    val boostsReceived: Int = 0,
    val congratsReceived: Int = 0,
    val respectsReceived: Int = 0,
    // Gamification 2.0 fields
    val isDevBadgeHolder: Boolean = false, // DEV badge
    val isBetaTester: Boolean = false, // Beta tester badge
    val isFounder: Boolean = false, // Founder badge
    val profileFrameRarity: String = "common", // Frame rarity for avatar (legacy, use frameId instead)
    val lastBoostedByCurrentUser: Long? = null // Track when current user last boosted this person
)
