package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "future_messages")
data class FutureMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val deliveryDate: Long, // Timestamp when message should be delivered
    val isDelivered: Boolean = false,
    val isRead: Boolean = false,
    val category: String = "general", // general, goal, promise, reminder, motivation
    val attachedGoal: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveredAt: Long? = null,
    // Media attachments - stored as JSON array strings
    val attachedPhotos: String = "", // JSON array of photo URIs
    val attachedVideos: String = "", // JSON array of video URIs
    val voiceRecordingUri: String? = null, // URI to voice recording file
    val voiceRecordingDuration: Long = 0 // Duration in milliseconds
)
