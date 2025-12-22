package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "peer_interactions",
    indices = [
        androidx.room.Index(value = ["userId"]),
        androidx.room.Index(value = ["peerId"]),
        androidx.room.Index(value = ["timestamp"])
    ]
)
data class PeerInteractionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local", // Multi-user support: who gave the interaction
    val peerId: String, // Who received the interaction
    val interactionType: String, // boost, congrats
    val timestamp: Long = System.currentTimeMillis(),
    val message: String? = null,
    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null
)