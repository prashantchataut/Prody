package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for storing Deep Dive sessions - scheduled days for deeper self-reflection.
 *
 * Deep Dives are structured reflection experiences that guide users through
 * meaningful exploration of specific themes like gratitude, growth, relationships,
 * purpose, fear, joy, forgiveness, and ambition.
 *
 * Each Deep Dive includes:
 * - Opening reflection to settle in
 * - Core questions for deep exploration
 * - Key insight identification
 * - Commitment statement for action
 * - Mood tracking before/after
 * - AI-enhanced personalization
 */
@Entity(
    tableName = "deep_dives",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["scheduledDate"]),
        Index(value = ["userId", "scheduledDate"]),
        Index(value = ["theme"]),
        Index(value = ["isCompleted"]),
        Index(value = ["userId", "isCompleted"])
    ]
)
data class DeepDiveEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User authentication
    val userId: String = "local",

    // Theme and scheduling
    val theme: String, // "gratitude", "growth", "relationships", "purpose", "fear", "joy", "forgiveness", "ambition"
    val scheduledDate: Long,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,

    // Structured responses - the heart of the Deep Dive
    val openingReflection: String? = null, // Initial settling thoughts
    val coreResponse: String? = null, // Main deep dive content (combined answers)
    val keyInsight: String? = null, // What they learned/discovered
    val commitmentStatement: String? = null, // What they'll do with this insight

    // Emotional tracking
    val moodBefore: Int? = null, // 1-10 scale before starting
    val moodAfter: Int? = null, // 1-10 scale after completing

    // AI enhancements for personalization
    val aiThemeContext: String? = null, // AI-generated context for theme
    val aiPrompts: String? = null, // JSON array of personalized prompts
    val aiReflectionSummary: String? = null, // AI summary of their responses
    val aiFollowUpSuggestions: String? = null, // AI suggestions for continued growth

    // Session metadata
    val durationMinutes: Int = 0, // How long the session took
    val sessionStartedAt: Long? = null, // When they started the session
    val currentStep: String = STEP_NOT_STARTED, // Current progress: "not_started", "opening", "core", "insight", "commitment", "completed"
    val promptVariation: Int = 0, // Which prompt variation was used (0-4)

    // Progress tracking
    val isScheduledNotificationSent: Boolean = false,
    val reminderSentAt: Long? = null,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    // Sync metadata
    val syncStatus: String = "pending", // pending, synced, conflict
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false // Soft delete for sync
) {
    companion object {
        // Theme constants
        const val THEME_GRATITUDE = "gratitude"
        const val THEME_GROWTH = "growth"
        const val THEME_RELATIONSHIPS = "relationships"
        const val THEME_PURPOSE = "purpose"
        const val THEME_FEAR = "fear"
        const val THEME_JOY = "joy"
        const val THEME_FORGIVENESS = "forgiveness"
        const val THEME_AMBITION = "ambition"

        // Step constants
        const val STEP_NOT_STARTED = "not_started"
        const val STEP_OPENING = "opening"
        const val STEP_CORE = "core"
        const val STEP_INSIGHT = "insight"
        const val STEP_COMMITMENT = "commitment"
        const val STEP_COMPLETED = "completed"

        /**
         * Get all available themes
         */
        fun getAllThemes(): List<String> = listOf(
            THEME_GRATITUDE,
            THEME_GROWTH,
            THEME_RELATIONSHIPS,
            THEME_PURPOSE,
            THEME_FEAR,
            THEME_JOY,
            THEME_FORGIVENESS,
            THEME_AMBITION
        )

        /**
         * Get the scheduled date for next week
         */
        fun getNextWeekScheduledDate(): Long {
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 7)
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 19) // Default to 7 PM
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }
    }

    /**
     * Check if the deep dive has been started
     */
    val isStarted: Boolean
        get() = currentStep != STEP_NOT_STARTED

    /**
     * Calculate completion percentage
     */
    val completionPercentage: Int
        get() = when (currentStep) {
            STEP_NOT_STARTED -> 0
            STEP_OPENING -> 20
            STEP_CORE -> 50
            STEP_INSIGHT -> 75
            STEP_COMMITMENT -> 90
            STEP_COMPLETED -> 100
            else -> 0
        }

    /**
     * Check if there's any content saved
     */
    val hasContent: Boolean
        get() = !openingReflection.isNullOrBlank() ||
                !coreResponse.isNullOrBlank() ||
                !keyInsight.isNullOrBlank() ||
                !commitmentStatement.isNullOrBlank()

    /**
     * Get estimated reading time in minutes based on content
     */
    val estimatedReadingTimeMinutes: Int
        get() {
            val totalWords = listOfNotNull(
                openingReflection,
                coreResponse,
                keyInsight,
                commitmentStatement
            ).sumOf { it.split("\\s+".toRegex()).size }

            // Average reading speed is ~200 words per minute
            return maxOf(1, totalWords / 200)
        }

    /**
     * Check if the scheduled date is in the past
     */
    fun isOverdue(): Boolean {
        return scheduledDate < System.currentTimeMillis() && !isCompleted
    }

    /**
     * Check if scheduled for today
     */
    fun isScheduledForToday(): Boolean {
        val today = java.time.LocalDate.now()
        val scheduledLocalDate = java.time.Instant.ofEpochMilli(scheduledDate)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        return today == scheduledLocalDate
    }

    /**
     * Get mood change (positive = improved, negative = declined, 0 = no change)
     */
    fun getMoodChange(): Int? {
        return if (moodBefore != null && moodAfter != null) {
            moodAfter - moodBefore
        } else {
            null
        }
    }
}
