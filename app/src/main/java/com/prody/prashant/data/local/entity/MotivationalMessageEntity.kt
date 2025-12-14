package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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