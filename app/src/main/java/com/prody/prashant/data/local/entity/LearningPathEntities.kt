package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
 * Learning Path Entity
 * Represents a complete learning journey for a specific growth area
 */
@Entity(
    tableName = "learning_paths",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["pathType"]),
        Index(value = ["userId", "pathType"]),
        Index(value = ["isActive"])
    ]
)
data class LearningPathEntity(
    @PrimaryKey
    val id: String,
    val userId: String = "local",
    val pathType: String, // "emotional_intelligence", "mindfulness", "confidence", "relationships", etc.
    val title: String,
    val description: String,
    val totalLessons: Int,
    val completedLessons: Int = 0,
    val currentLessonId: String? = null,
    val startedAt: Long = System.currentTimeMillis(),
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isActive: Boolean = true,
    val progressPercentage: Float = 0f,
    val estimatedMinutesTotal: Int,
    val difficultyLevel: String = "beginner", // beginner, intermediate, advanced
    val iconEmoji: String = "📚",
    val colorTheme: String = "#6366F1" // Hex color for path theme
)

/**
 * Learning Lesson Entity
 * Individual lesson within a learning path
 */
@Entity(
    tableName = "learning_lessons",
    indices = [
        Index(value = ["pathId"]),
        Index(value = ["pathId", "orderIndex"]),
        Index(value = ["isCompleted"])
    ]
)
data class LearningLessonEntity(
    @PrimaryKey
    val id: String,
    val pathId: String,
    val orderIndex: Int,
    val title: String,
    val lessonType: String, // "reading", "reflection", "exercise", "journal_prompt", "meditation", "quiz"
    val contentJson: String, // Structured content based on type
    val estimatedMinutes: Int,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val userNotesJson: String? = null,
    val quizScore: Int? = null,
    val unlockRequirement: String? = null, // Previous lesson ID that must be completed
    val isLocked: Boolean = true
)

/**
 * Learning Reflection Entity
 * User responses to reflection prompts within lessons
 */
@Entity(
    tableName = "learning_reflections",
    indices = [
        Index(value = ["lessonId"]),
        Index(value = ["pathId"]),
        Index(value = ["userId"]),
        Index(value = ["userId", "createdAt"])
    ]
)
data class LearningReflectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lessonId: String,
    val pathId: String,
    val userId: String = "local",
    val promptText: String,
    val userResponse: String,
    val aiInsight: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val wordCount: Int = 0,
    val mood: String? = null,
    val isBookmarked: Boolean = false
)

/**
 * Path Recommendation Entity
 * AI-generated recommendations for learning paths based on journal analysis
 */
@Entity(
    tableName = "path_recommendations",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["pathType"]),
        Index(value = ["userId", "createdAt"]),
        Index(value = ["isDismissed"]),
        Index(value = ["isAccepted"])
    ]
)
data class PathRecommendationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val pathType: String,
    val reason: String, // Why this was recommended
    val confidenceScore: Float, // How sure AI is this fits user (0.0 - 1.0)
    val basedOnEntriesJson: String = "[]", // JSON array of journal entry IDs
    val basedOnPatternsJson: String = "[]", // JSON array of detected patterns
    val createdAt: Long = System.currentTimeMillis(),
    val isDismissed: Boolean = false,
    val isAccepted: Boolean = false,
    val dismissedAt: Long? = null,
    val acceptedAt: Long? = null
)

/**
 * Path Progress Checkpoint Entity
 * Tracks user progress and milestones within a path
 */
@Entity(
    tableName = "path_progress_checkpoints",
    indices = [
        Index(value = ["pathId"]),
        Index(value = ["userId"]),
        Index(value = ["userId", "pathId"])
    ]
)
data class PathProgressCheckpointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pathId: String,
    val userId: String = "local",
    val checkpointType: String, // "lesson_complete", "milestone_reached", "path_completed", "quiz_passed"
    val lessonId: String? = null,
    val description: String,
    val xpEarned: Int = 0,
    val tokensEarned: Int = 0,
    val achievedAt: Long = System.currentTimeMillis(),
    val celebrationShown: Boolean = false
)

/**
 * Learning Notes Entity
 * User's personal notes taken during lessons
 */
@Entity(
    tableName = "learning_notes",
    indices = [
        Index(value = ["lessonId"]),
        Index(value = ["pathId"]),
        Index(value = ["userId"]),
        Index(value = ["userId", "createdAt"])
    ]
)
data class LearningNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lessonId: String,
    val pathId: String,
    val userId: String = "local",
    val noteContent: String,
    val highlightedText: String? = null,
    val noteColor: String = "#FFF59D", // Highlight color
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Path Badge Entity
 * Achievements and badges earned for completing paths and milestones
 */
@Entity(
    tableName = "path_badges",
    indices = [
        Index(value = ["pathId"]),
        Index(value = ["userId"]),
        Index(value = ["userId", "earnedAt"])
    ]
)
data class PathBadgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pathId: String,
    val userId: String = "local",
    val badgeType: String, // "path_completed", "perfect_score", "speed_learner", "dedicated_student"
    val badgeName: String,
    val badgeDescription: String,
    val badgeIcon: String,
    val earnedAt: Long = System.currentTimeMillis(),
    val isDisplayed: Boolean = true,
    val rarity: String = "common" // common, rare, epic, legendary
)
