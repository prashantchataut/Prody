package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Haven Session Entity - Stores therapeutic conversation sessions
 *
 * Each session represents a complete therapeutic conversation with Haven,
 * including the type of support requested, messages exchanged, techniques applied,
 * and outcomes measured (mood before/after, user rating).
 *
 * All sensitive conversation data is stored encrypted via EncryptionManager.
 */
@Entity(
    tableName = "haven_sessions",
    indices = [
        androidx.room.Index(value = ["userId"]),
        androidx.room.Index(value = ["startedAt"]),
        androidx.room.Index(value = ["sessionType"]),
        androidx.room.Index(value = ["userId", "startedAt"])
    ]
)
data class HavenSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User identification
    val userId: String = "local",

    // Session type and metadata
    val sessionType: String, // "check_in", "anxiety", "stress", "sadness", "anger", "general", "crisis_support"
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,

    // Conversation data (encrypted JSON)
    val messagesJson: String = "[]", // Encrypted JSON array of HavenMessage objects
    val techniquesUsedJson: String = "[]", // JSON array of therapeutic techniques applied

    // Mood tracking
    val moodBefore: Int? = null, // 1-10 scale
    val moodAfter: Int? = null, // 1-10 scale

    // Session completion and feedback
    val isCompleted: Boolean = false,
    val userRating: Int? = null, // 1-5 helpfulness rating

    // AI-generated insights (encrypted)
    val keyInsightsJson: String? = null, // Encrypted JSON array of insights
    val suggestedExercisesJson: String? = null, // JSON array of suggested exercises

    // Follow-up scheduling
    val followUpScheduled: Long? = null, // Timestamp for suggested check-in

    // Crisis support flag
    val containedCrisisDetection: Boolean = false,

    // Sync metadata
    val syncStatus: String = "pending", // pending, synced, conflict
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false // Soft delete for sync
)

/**
 * Haven Exercise Entity - Tracks completion of guided therapeutic exercises
 *
 * Exercises can be standalone or initiated from a Haven session.
 * Examples: breathing exercises, grounding techniques, thought records, etc.
 */
@Entity(
    tableName = "haven_exercises",
    indices = [
        androidx.room.Index(value = ["userId"]),
        androidx.room.Index(value = ["completedAt"]),
        androidx.room.Index(value = ["exerciseType"]),
        androidx.room.Index(value = ["fromSessionId"]),
        androidx.room.Index(value = ["userId", "completedAt"])
    ]
)
data class HavenExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User identification
    val userId: String = "local",

    // Exercise metadata
    val exerciseType: String, // "breathing", "grounding", "thought_record", "emotion_wheel", "gratitude", "body_scan"
    val completedAt: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,

    // Exercise details (encrypted if contains sensitive data)
    val notes: String? = null, // User's notes or reflections from exercise
    val exerciseDataJson: String? = null, // Additional structured data (e.g., thought record fields)

    // Session linkage
    val fromSessionId: Long? = null, // If exercise was suggested during a Haven session

    // Completion tracking
    val wasCompleted: Boolean = true, // false if abandoned mid-exercise
    val completionRate: Float = 1.0f, // 0.0 to 1.0 for partial completion

    // Effectiveness tracking
    val helpfulness: Int? = null, // 1-5 rating if user provides feedback

    // Sync metadata
    val syncStatus: String = "pending",
    val isDeleted: Boolean = false
)
