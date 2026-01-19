package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "future_messages",
    indices = [
        androidx.room.Index(value = ["userId"]),
        androidx.room.Index(value = ["deliveryDate"]),
        androidx.room.Index(value = ["userId", "deliveryDate"])
    ]
)
data class FutureMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // User authentication - prepared for multi-user support
    val userId: String = "local",
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
    val voiceRecordingDuration: Long = 0, // Duration in milliseconds
    // Sync metadata - for cloud synchronization
    val syncStatus: String = "pending", // pending, synced, conflict
    val lastSyncedAt: Long? = null,
    val serverVersion: Long = 0,
    val isDeleted: Boolean = false,
    // Time Capsule Reveal enhancements
    val isFavorite: Boolean = false, // Mark special messages as favorites
    val replyJournalEntryId: Long? = null, // Link to journal entry created as reply to past self
    val readAt: Long? = null, // Timestamp when message was actually read/opened
    // Mirror Evolution: Prophecy feature - predictions about the future
    val prediction: String? = null, // User's prediction (e.g., "I will have finished my degree")
    val predictionVerified: Boolean? = null, // Was the prediction accurate? (set when delivered)
    val predictionVerifiedAt: Long? = null // When the prediction was verified
)
