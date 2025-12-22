package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "journal_entries",
    indices = [
        androidx.room.Index(value = ["userId"]),
        androidx.room.Index(value = ["createdAt"]),
        androidx.room.Index(value = ["userId", "createdAt"])
    ]
)
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // User authentication - prepared for multi-user support
    // Default "local" for offline-first, replaced with Firebase UID on auth
    val userId: String = "local",
    val title: String = "", // Optional title for the journal entry
    val content: String,
    val mood: String,
    val moodIntensity: Int = 5, // 1-10 scale
    val buddhaResponse: String? = null,
    val tags: String = "", // Comma-separated tags
    val isBookmarked: Boolean = false,
    val wordCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    // AI-generated insights (non-blocking, analyzed after save)
    val aiEmotionLabel: String? = null,
    val aiThemes: String? = null, // Comma-separated themes
    val aiInsight: String? = null,
    val aiInsightGenerated: Boolean = false,
    // Media attachments - stored as JSON array strings
    val attachedPhotos: String = "", // JSON array of photo URIs
    val attachedVideos: String = "", // JSON array of video URIs
    val voiceRecordingUri: String? = null, // URI to voice recording file
    val voiceRecordingDuration: Long = 0, // Duration in milliseconds
    // Template used for this entry
    val templateId: String? = null,
    // Sync metadata - for cloud synchronization
    val syncStatus: String = "pending", // pending, synced, conflict
    val lastSyncedAt: Long? = null,
    val serverVersion: Long = 0, // For conflict resolution
    val isDeleted: Boolean = false // Soft delete for sync
)
