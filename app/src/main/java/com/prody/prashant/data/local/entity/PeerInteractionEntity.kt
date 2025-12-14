package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "peer_interactions")
data class PeerInteractionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val peerId: String,
    val interactionType: String, // boost, congrats
    val timestamp: Long = System.currentTimeMillis(),
    val message: String? = null
)