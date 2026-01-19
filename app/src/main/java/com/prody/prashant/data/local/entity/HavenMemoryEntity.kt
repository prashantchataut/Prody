package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Haven Memory Entity - THE VAULT
 *
 * Stores "facts" or "truths" extracted from Haven conversations.
 * These are specific commitments, events, or statements the user mentions
 * that Haven should remember and follow up on later.
 *
 * Examples of facts:
 * - "I have an exam on Friday"
 * - "I promised to call mom this weekend"
 * - "My presentation is due next Tuesday"
 * - "I'm trying to quit smoking"
 * - "My anniversary is on March 15"
 *
 * Haven will use these facts to proactively check in with the user
 * and ask "Did you survive?" or "How did it go?"
 */
@Entity(
    tableName = "haven_memories",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["status"]),
        Index(value = ["factDate"]),
        Index(value = ["createdAt"]),
        Index(value = ["userId", "status"]),
        Index(value = ["category"])
    ]
)
data class HavenMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User identification
    val userId: String = "local",

    // The fact/truth extracted from conversation
    val fact: String,

    // The date associated with the fact (when the event is/was happening)
    // Can be null if the fact doesn't have a specific date
    val factDate: Long? = null,

    // Category of the fact for filtering and grouping
    val category: String = "general", // exam, deadline, commitment, event, goal, relationship, health

    // Status of the memory
    val status: String = "pending", // pending, followed_up, resolved, expired, dismissed

    // When we should follow up (calculated based on factDate)
    val followUpDate: Long? = null,

    // Source of this memory
    val sourceSessionId: Long? = null, // Haven session ID where this was extracted
    val sourceMessage: String? = null, // The original message containing the fact

    // Follow-up tracking
    val followedUpAt: Long? = null,
    val followUpResponse: String? = null, // User's response to follow-up ("Yes I survived", etc.)
    val outcome: String? = null, // success, failed, cancelled, postponed

    // Importance/urgency level
    val importance: Int = 1, // 1 = normal, 2 = important, 3 = critical

    // Whether this fact triggered a notification
    val notificationSent: Boolean = false,
    val notificationSentAt: Long? = null,

    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    // Sync metadata
    val syncStatus: String = "pending",
    val isDeleted: Boolean = false
)

/**
 * Status values for HavenMemory
 */
object HavenMemoryStatus {
    const val PENDING = "pending"           // Waiting for follow-up date
    const val FOLLOWED_UP = "followed_up"   // Follow-up sent, waiting for response
    const val RESOLVED = "resolved"         // User confirmed outcome
    const val EXPIRED = "expired"           // Past due, no response
    const val DISMISSED = "dismissed"       // User dismissed the memory
}

/**
 * Category values for HavenMemory
 */
object HavenMemoryCategory {
    const val GENERAL = "general"
    const val EXAM = "exam"
    const val DEADLINE = "deadline"
    const val COMMITMENT = "commitment"
    const val EVENT = "event"
    const val GOAL = "goal"
    const val RELATIONSHIP = "relationship"
    const val HEALTH = "health"
    const val WORK = "work"
    const val PERSONAL = "personal"
}

/**
 * Outcome values for resolved memories
 */
object HavenMemoryOutcome {
    const val SUCCESS = "success"
    const val FAILED = "failed"
    const val CANCELLED = "cancelled"
    const val POSTPONED = "postponed"
    const val PARTIAL = "partial"
}
