package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ================================================================================================
 * SOUL LAYER DATABASE ENTITIES
 * ================================================================================================
 *
 * These entities support the Soul Layer intelligence system:
 * - SurfacedMemoryEntity: Tracks memories that have been surfaced to the user
 * - UserContextCacheEntity: Caches computed user context for performance
 * - NotificationHistoryEntity: Tracks notification decisions and user responses
 * - PatternDetectionEntity: Stores detected patterns in user behavior
 */

/**
 * Tracks memories that have been surfaced to the user.
 * Used to avoid showing the same memories too frequently and to track engagement.
 */
@Entity(
    tableName = "surfaced_memories",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["journalEntryId"]),
        Index(value = ["surfacedAt"]),
        Index(value = ["userId", "surfacedAt"])
    ]
)
data class SurfacedMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val journalEntryId: Long,
    val surfaceReason: String, // anniversary, mood_match, theme_match, growth_contrast, random
    val surfaceContext: String, // home, journal_write, notification, widget
    val surfacedAt: Long = System.currentTimeMillis(),
    val wasInteractedWith: Boolean = false,
    val interactionType: String? = null, // viewed, expanded, reflected, dismissed
    val interactedAt: Long? = null,
    val memoryPreview: String, // First ~100 chars of the entry
    val originalMood: String?,
    val originalDate: Long,
    val yearsAgo: Int = 0 // For anniversary memories
)

/**
 * Caches the computed user context for performance.
 * Recomputed periodically or when significant changes occur.
 */
@Entity(
    tableName = "user_context_cache",
    indices = [
        Index(value = ["userId"], unique = true)
    ]
)
data class UserContextCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    // Archetype and engagement
    val userArchetype: String, // EXPLORER, CONSISTENT, STRUGGLING, THRIVING, RETURNING, SPORADIC
    val trustLevel: String, // NEW, BUILDING, ESTABLISHED, DEEP
    val engagementLevel: String, // NEW, DAILY, REGULAR, SPORADIC, CHURNING, RETURNING
    // Emotional state
    val dominantMood: String?,
    val moodTrend: String, // IMPROVING, DECLINING, STABLE, VOLATILE
    val emotionalEnergy: String, // LOW, MEDIUM, HIGH
    val isStruggling: Boolean = false,
    val isThriving: Boolean = false,
    // Detected stress signals (JSON array)
    val stressSignalsJson: String = "[]",
    // Recent themes (JSON array)
    val recentThemesJson: String = "[]",
    // Recurring patterns (JSON array)
    val recurringPatternsJson: String = "[]",
    // Recent wins (JSON array)
    val recentWinsJson: String = "[]",
    // Recurring challenges (JSON array)
    val recurringChallengesJson: String = "[]",
    // Statistics
    val totalEntries: Int = 0,
    val daysWithPrody: Int = 0,
    val daysSinceLastEntry: Int = 0,
    val averageWordsPerEntry: Int = 0,
    // Preferences
    val preferredTone: String = "WARM",
    val preferredJournalTime: String?, // MORNING, AFTERNOON, EVENING, NIGHT
    // Timestamps
    val computedAt: Long = System.currentTimeMillis(),
    val validUntil: Long = System.currentTimeMillis() + (6 * 60 * 60 * 1000) // 6 hours default validity
)

/**
 * Tracks notification decisions and user responses.
 * Used to learn what notifications work best for each user.
 */
@Entity(
    tableName = "notification_history",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["notificationType"]),
        Index(value = ["sentAt"]),
        Index(value = ["userId", "sentAt"]),
        Index(value = ["userId", "notificationType"])
    ]
)
data class NotificationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val notificationType: String, // MORNING_REMINDER, EVENING_REFLECTION, STREAK_REMINDER, etc.
    val decision: String, // SEND, SKIP, RESCHEDULE, DELAY, MODIFY
    val decisionReason: String?,
    val title: String?,
    val body: String?,
    val scheduledAt: Long,
    val sentAt: Long?,
    val wasOpened: Boolean = false,
    val openedAt: Long? = null,
    val resultedInAction: Boolean = false, // Did user journal/engage after?
    val actionType: String? = null, // journal_entry, app_open, feature_use
    val actionAt: Long? = null,
    // Context at time of decision
    val userArchetypeAtTime: String?,
    val wasUserStruggling: Boolean = false,
    val hourOfDay: Int,
    val dayOfWeek: Int
)

/**
 * Stores detected patterns in user behavior.
 * These patterns inform personalization and insights.
 */
@Entity(
    tableName = "detected_patterns",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["patternType"]),
        Index(value = ["userId", "patternType"]),
        Index(value = ["detectedAt"])
    ]
)
data class DetectedPatternEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val patternType: String, // MOOD_CYCLE, WRITING_TIME, THEME_RECURRENCE, STRESS_TRIGGER, etc.
    val patternDescription: String,
    val confidence: Float, // 0.0 to 1.0
    val supportingEvidence: String, // JSON array of entry IDs or data points
    val firstDetectedAt: Long,
    val lastConfirmedAt: Long,
    val occurrenceCount: Int = 1,
    val isActive: Boolean = true, // Pattern still observed
    val wasShownToUser: Boolean = false,
    val shownAt: Long? = null,
    val userFeedback: String? = null, // HELPFUL, NOT_HELPFUL, DISMISSED
    val feedbackAt: Long? = null,
    val detectedAt: Long = System.currentTimeMillis()
)

/**
 * Tracks Buddha AI interactions for personalization.
 * Used to learn preferred wisdom styles and response patterns.
 */
@Entity(
    tableName = "buddha_interactions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["interactionType"]),
        Index(value = ["interactedAt"]),
        Index(value = ["userId", "interactedAt"])
    ]
)
data class BuddhaInteractionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val interactionType: String, // JOURNAL_RESPONSE, DAILY_WISDOM, QUOTE_EXPLANATION, PROMPT, REFLECTION
    val contextMood: String?,
    val contextMoodIntensity: Int?,
    val responseWisdomStyle: String?, // STOIC, EASTERN, PRACTICAL, POETIC, DIRECT
    val responseLength: Int = 0,
    val wasHelpful: Boolean? = null, // User feedback if given
    val helpfulnessRating: Int? = null, // 1-5 if explicit rating
    val wasExpanded: Boolean = false, // For cards that can expand
    val wasSaved: Boolean = false,
    val wasShared: Boolean = false,
    val timeSpentViewingMs: Long? = null,
    val interactedAt: Long = System.currentTimeMillis(),
    // For journal responses
    val journalEntryId: Long? = null,
    val journalWordCount: Int? = null
)

/**
 * Tracks Haven therapeutic session insights.
 * Used to inform future sessions and personalize therapeutic approach.
 */
@Entity(
    tableName = "haven_insights",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["sessionId"]),
        Index(value = ["createdAt"]),
        Index(value = ["userId", "createdAt"])
    ]
)
data class HavenInsightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val sessionId: Long,
    val sessionType: String, // CHECK_IN, ANXIETY, STRESS, SADNESS, etc.
    val insightType: String, // TRIGGER_IDENTIFIED, COPING_EFFECTIVE, THEME_RECURRING, etc.
    val insightContent: String,
    val confidence: Float = 0.5f,
    val therapeuticApproachUsed: String?, // CBT, DBT, MINDFULNESS, ACT, GENERAL
    val wasEffective: Boolean? = null,
    val moodBefore: Int?,
    val moodAfter: Int?,
    val moodImprovement: Int?, // moodAfter - moodBefore
    val createdAt: Long = System.currentTimeMillis(),
    val wasUsedInFutureSession: Boolean = false
)

/**
 * Tracks temporal content that has been shown.
 * Prevents repetition and tracks engagement.
 */
@Entity(
    tableName = "temporal_content_history",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["contentType"]),
        Index(value = ["shownAt"]),
        Index(value = ["userId", "contentType", "shownAt"])
    ]
)
data class TemporalContentHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val contentType: String, // GREETING, PROMPT, DAILY_THEME, MORNING_CONTENT, EVENING_CONTENT
    val contentId: String?, // For prompts with specific IDs
    val contentPreview: String, // First ~100 chars
    val timeOfDay: String, // MORNING, AFTERNOON, EVENING, NIGHT
    val dayOfWeek: Int,
    val seasonalContext: String?, // spring, summer, fall, winter, special_event
    val shownAt: Long = System.currentTimeMillis(),
    val wasEngaged: Boolean = false, // Did user act on it?
    val engagementType: String? = null, // clicked, journaled, dismissed
    val engagedAt: Long? = null
)

/**
 * First week journey progress tracking.
 * More detailed than preferences for analytics and personalization.
 */
@Entity(
    tableName = "first_week_progress",
    indices = [
        Index(value = ["userId"], unique = true)
    ]
)
data class FirstWeekProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val startedAt: Long,
    val graduatedAt: Long? = null,
    val isGraduated: Boolean = false,
    // Daily progress (JSON objects for each day)
    val day1ProgressJson: String = "{}",
    val day2ProgressJson: String = "{}",
    val day3ProgressJson: String = "{}",
    val day4ProgressJson: String = "{}",
    val day5ProgressJson: String = "{}",
    val day6ProgressJson: String = "{}",
    val day7ProgressJson: String = "{}",
    // Milestone tracking
    val completedMilestonesJson: String = "[]",
    val celebrationsShownJson: String = "[]",
    // Aggregated stats
    val totalEntriesInFirstWeek: Int = 0,
    val totalWordsInFirstWeek: Int = 0,
    val featuresExploredJson: String = "[]",
    val longestStreakInFirstWeek: Int = 0,
    // Rewards earned
    val totalXpEarned: Int = 0,
    val totalTokensEarned: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
