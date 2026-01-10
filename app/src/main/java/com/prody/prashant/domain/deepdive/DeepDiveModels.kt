package com.prody.prashant.domain.deepdive

import androidx.compose.ui.graphics.Color
import com.prody.prashant.data.local.entity.DeepDiveEntity

/**
 * Domain models for Deep Dive feature.
 */

/**
 * Themes available for Deep Dive sessions.
 * Each theme has specific prompts and color schemes.
 */
enum class DeepDiveTheme(
    val id: String,
    val displayName: String,
    val icon: String,
    val description: String,
    val colorLight: Long, // Light theme color (as ARGB)
    val colorDark: Long   // Dark theme color (as ARGB)
) {
    GRATITUDE(
        id = DeepDiveEntity.THEME_GRATITUDE,
        displayName = "Gratitude",
        icon = "🙏",
        description = "Explore what you're thankful for and the gifts in your life",
        colorLight = 0xFFFFF3E0, // Amber 50
        colorDark = 0xFFFF8F00    // Amber 700
    ),
    GROWTH(
        id = DeepDiveEntity.THEME_GROWTH,
        displayName = "Personal Growth",
        icon = "🌱",
        description = "Reflect on your evolution and the person you're becoming",
        colorLight = 0xFFE8F5E9, // Green 50
        colorDark = 0xFF43A047    // Green 600
    ),
    RELATIONSHIPS(
        id = DeepDiveEntity.THEME_RELATIONSHIPS,
        displayName = "Relationships",
        icon = "💝",
        description = "Understand your connections and the bonds that shape you",
        colorLight = 0xFFFCE4EC, // Pink 50
        colorDark = 0xFFE91E63    // Pink 500
    ),
    PURPOSE(
        id = DeepDiveEntity.THEME_PURPOSE,
        displayName = "Purpose",
        icon = "🎯",
        description = "Discover your why and the meaning behind your journey",
        colorLight = 0xFFE3F2FD, // Blue 50
        colorDark = 0xFF1976D2    // Blue 700
    ),
    FEAR(
        id = DeepDiveEntity.THEME_FEAR,
        displayName = "Facing Fears",
        icon = "🦁",
        description = "Confront what holds you back and find your courage",
        colorLight = 0xFFFFF9C4, // Yellow 100
        colorDark = 0xFFF9A825    // Yellow 800
    ),
    JOY(
        id = DeepDiveEntity.THEME_JOY,
        displayName = "Finding Joy",
        icon = "✨",
        description = "Identify what brings light and delight into your life",
        colorLight = 0xFFFFF3E0, // Orange 50
        colorDark = 0xFFFF6F00    // Orange 900
    ),
    FORGIVENESS(
        id = DeepDiveEntity.THEME_FORGIVENESS,
        displayName = "Forgiveness",
        icon = "🕊️",
        description = "Release what weighs you down and heal old wounds",
        colorLight = 0xFFF3E5F5, // Purple 50
        colorDark = 0xFF8E24AA    // Purple 600
    ),
    AMBITION(
        id = DeepDiveEntity.THEME_AMBITION,
        displayName = "Dreams & Ambition",
        icon = "🚀",
        description = "Envision your future and the heights you want to reach",
        colorLight = 0xFFE0F2F1, // Teal 50
        colorDark = 0xFF00897B    // Teal 600
    );

    companion object {
        fun fromId(id: String): DeepDiveTheme? {
            return values().find { it.id == id }
        }

        fun getAllThemes(): List<DeepDiveTheme> {
            return values().toList()
        }
    }
}

/**
 * Progress steps in a Deep Dive session
 */
enum class DeepDiveProgress(
    val stepName: String,
    val displayName: String,
    val description: String
) {
    NOT_STARTED(
        stepName = DeepDiveEntity.STEP_NOT_STARTED,
        displayName = "Not Started",
        description = "Begin your deep dive journey"
    ),
    OPENING(
        stepName = DeepDiveEntity.STEP_OPENING,
        displayName = "Opening",
        description = "Settle in and prepare your mind"
    ),
    CORE(
        stepName = DeepDiveEntity.STEP_CORE,
        displayName = "Core Reflection",
        description = "Explore the heart of this theme"
    ),
    INSIGHT(
        stepName = DeepDiveEntity.STEP_INSIGHT,
        displayName = "Key Insight",
        description = "Identify what you've discovered"
    ),
    COMMITMENT(
        stepName = DeepDiveEntity.STEP_COMMITMENT,
        displayName = "Commitment",
        description = "Choose how you'll apply this wisdom"
    ),
    COMPLETED(
        stepName = DeepDiveEntity.STEP_COMPLETED,
        displayName = "Completed",
        description = "Deep dive complete"
    );

    companion object {
        fun fromStepName(stepName: String): DeepDiveProgress {
            return values().find { it.stepName == stepName } ?: NOT_STARTED
        }

        fun getNextStep(current: DeepDiveProgress): DeepDiveProgress? {
            val currentIndex = values().indexOf(current)
            return if (currentIndex < values().size - 1) {
                values()[currentIndex + 1]
            } else {
                null
            }
        }

        fun getPreviousStep(current: DeepDiveProgress): DeepDiveProgress? {
            val currentIndex = values().indexOf(current)
            return if (currentIndex > 0) {
                values()[currentIndex - 1]
            } else {
                null
            }
        }
    }
}

/**
 * Complete set of prompts for a Deep Dive session
 */
data class DeepDivePrompt(
    val theme: DeepDiveTheme,
    val variation: Int, // Which variation of prompts (0-4)
    val openingQuestion: String,
    val coreQuestions: List<String>,
    val insightPrompt: String,
    val commitmentPrompt: String
) {
    /**
     * Get all questions in order
     */
    fun getAllQuestions(): List<String> {
        return listOf(openingQuestion) + coreQuestions + listOf(insightPrompt, commitmentPrompt)
    }

    /**
     * Get total estimated time in minutes
     */
    fun getEstimatedTimeMinutes(): Int {
        // Opening: 2 min, Core: 3 min per question, Insight: 3 min, Commitment: 2 min
        return 2 + (coreQuestions.size * 3) + 3 + 2
    }
}

/**
 * Complete Deep Dive session data including prompts and entity
 */
data class DeepDiveSession(
    val entity: DeepDiveEntity,
    val theme: DeepDiveTheme,
    val prompts: DeepDivePrompt,
    val progress: DeepDiveProgress
) {
    /**
     * Get current prompt based on progress
     */
    fun getCurrentPrompt(): String? {
        return when (progress) {
            DeepDiveProgress.NOT_STARTED -> null
            DeepDiveProgress.OPENING -> prompts.openingQuestion
            DeepDiveProgress.CORE -> prompts.coreQuestions.firstOrNull() // In reality, show all
            DeepDiveProgress.INSIGHT -> prompts.insightPrompt
            DeepDiveProgress.COMMITMENT -> prompts.commitmentPrompt
            DeepDiveProgress.COMPLETED -> null
        }
    }

    /**
     * Check if session can advance to next step
     */
    fun canAdvanceToNextStep(): Boolean {
        return when (progress) {
            DeepDiveProgress.NOT_STARTED -> true
            DeepDiveProgress.OPENING -> !entity.openingReflection.isNullOrBlank()
            DeepDiveProgress.CORE -> !entity.coreResponse.isNullOrBlank()
            DeepDiveProgress.INSIGHT -> !entity.keyInsight.isNullOrBlank()
            DeepDiveProgress.COMMITMENT -> !entity.commitmentStatement.isNullOrBlank()
            DeepDiveProgress.COMPLETED -> false
        }
    }

    /**
     * Get session duration if started
     */
    fun getSessionDurationMinutes(): Int {
        return if (entity.sessionStartedAt != null) {
            val endTime = entity.completedAt ?: System.currentTimeMillis()
            ((endTime - entity.sessionStartedAt) / 60000).toInt()
        } else {
            0
        }
    }
}

/**
 * Summary of a completed Deep Dive for display
 */
data class DeepDiveSummary(
    val id: Long,
    val theme: DeepDiveTheme,
    val completedAt: Long,
    val keyInsight: String?,
    val commitmentStatement: String?,
    val moodChange: Int?, // Positive = improved, negative = declined
    val durationMinutes: Int,
    val hasFullContent: Boolean
) {
    companion object {
        fun fromEntity(entity: DeepDiveEntity): DeepDiveSummary? {
            val theme = DeepDiveTheme.fromId(entity.theme) ?: return null
            return DeepDiveSummary(
                id = entity.id,
                theme = theme,
                completedAt = entity.completedAt ?: return null,
                keyInsight = entity.keyInsight,
                commitmentStatement = entity.commitmentStatement,
                moodChange = entity.getMoodChange(),
                durationMinutes = entity.durationMinutes,
                hasFullContent = entity.hasContent
            )
        }
    }
}

/**
 * Analytics data for Deep Dive feature
 */
data class DeepDiveAnalytics(
    val totalCompleted: Int,
    val totalScheduled: Int,
    val averageDurationMinutes: Double,
    val averageMoodImprovement: Double,
    val themeFrequency: Map<DeepDiveTheme, Int>,
    val mostRecentCompletionDate: Long?,
    val unexploredThemes: List<DeepDiveTheme>
) {
    /**
     * Get the most explored theme
     */
    fun getMostExploredTheme(): DeepDiveTheme? {
        return themeFrequency.maxByOrNull { it.value }?.key
    }

    /**
     * Get completion rate (completed / total)
     */
    fun getCompletionRate(): Float {
        val total = totalCompleted + totalScheduled
        return if (total > 0) {
            totalCompleted.toFloat() / total.toFloat()
        } else {
            0f
        }
    }

    /**
     * Check if user is consistent (completed at least one in the last 14 days)
     */
    fun isConsistent(): Boolean {
        val twoWeeksAgo = System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000)
        return mostRecentCompletionDate?.let { it >= twoWeeksAgo } ?: false
    }
}

/**
 * Mood rating for before/after comparison
 */
enum class MoodRating(val value: Int, val displayName: String, val emoji: String) {
    VERY_LOW(1, "Very Low", "😢"),
    LOW(2, "Low", "😔"),
    SOMEWHAT_LOW(3, "Somewhat Low", "😕"),
    NEUTRAL(4, "Neutral", "😐"),
    SOMEWHAT_GOOD(5, "Somewhat Good", "🙂"),
    GOOD(6, "Good", "😊"),
    VERY_GOOD(7, "Very Good", "😄"),
    GREAT(8, "Great", "😁"),
    EXCELLENT(9, "Excellent", "🤩"),
    AMAZING(10, "Amazing", "✨");

    companion object {
        fun fromValue(value: Int): MoodRating {
            return values().find { it.value == value } ?: NEUTRAL
        }

        fun getByValue(value: Int): MoodRating? {
            return values().find { it.value == value }
        }
    }
}

/**
 * Scheduled notification data for a Deep Dive
 */
data class DeepDiveNotification(
    val deepDiveId: Long,
    val theme: DeepDiveTheme,
    val scheduledTime: Long,
    val message: String
) {
    companion object {
        /**
         * Generate notification message based on theme
         */
        fun generateMessage(theme: DeepDiveTheme): String {
            return when (theme) {
                DeepDiveTheme.GRATITUDE -> "Time to reflect on what you're grateful for today"
                DeepDiveTheme.GROWTH -> "Ready to explore how you've grown?"
                DeepDiveTheme.RELATIONSHIPS -> "Let's think about the connections that matter"
                DeepDiveTheme.PURPOSE -> "Discover what gives your life meaning"
                DeepDiveTheme.FEAR -> "Face your fears with courage and compassion"
                DeepDiveTheme.JOY -> "What brings joy to your life?"
                DeepDiveTheme.FORGIVENESS -> "Time to release and heal"
                DeepDiveTheme.AMBITION -> "Dream big and envision your future"
            }
        }
    }
}
