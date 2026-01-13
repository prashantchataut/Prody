package com.prody.prashant.domain.learning

/**
 * Path Type Enum
 * Defines all available learning path types with metadata
 */
enum class PathType(
    val id: String,
    val displayName: String,
    val icon: String,
    val description: String,
    val color: String,
    val estimatedMinutes: Int,
    val difficultyLevel: String
) {
    EMOTIONAL_INTELLIGENCE(
        id = "emotional_intelligence",
        displayName = "Emotional Intelligence",
        icon = "🎭",
        description = "Understand and manage your emotions with wisdom and grace",
        color = "#EC4899",
        estimatedMinutes = 180,
        difficultyLevel = "beginner"
    ),
    MINDFULNESS(
        id = "mindfulness",
        displayName = "Mindfulness Mastery",
        icon = "🧘",
        description = "Cultivate present-moment awareness and inner peace",
        color = "#8B5CF6",
        estimatedMinutes = 200,
        difficultyLevel = "beginner"
    ),
    CONFIDENCE(
        id = "confidence",
        displayName = "Building Confidence",
        icon = "💫",
        description = "Develop unshakeable self-belief and authentic courage",
        color = "#F59E0B",
        estimatedMinutes = 150,
        difficultyLevel = "intermediate"
    ),
    RELATIONSHIPS(
        id = "relationships",
        displayName = "Healthy Relationships",
        icon = "💝",
        description = "Deepen connections and build meaningful bonds",
        color = "#EF4444",
        estimatedMinutes = 170,
        difficultyLevel = "intermediate"
    ),
    STRESS_MANAGEMENT(
        id = "stress_management",
        displayName = "Stress Resilience",
        icon = "🌊",
        description = "Build calm in the chaos and navigate life's storms",
        color = "#06B6D4",
        estimatedMinutes = 160,
        difficultyLevel = "beginner"
    ),
    GRATITUDE(
        id = "gratitude",
        displayName = "Gratitude Practice",
        icon = "🙏",
        description = "Transform your perspective through thankfulness",
        color = "#10B981",
        estimatedMinutes = 120,
        difficultyLevel = "beginner"
    ),
    SELF_COMPASSION(
        id = "self_compassion",
        displayName = "Self-Compassion",
        icon = "💚",
        description = "Be kinder to yourself and embrace imperfection",
        color = "#84CC16",
        estimatedMinutes = 140,
        difficultyLevel = "intermediate"
    ),
    PRODUCTIVITY(
        id = "productivity",
        displayName = "Mindful Productivity",
        icon = "🎯",
        description = "Focus without burnout and achieve with ease",
        color = "#6366F1",
        estimatedMinutes = 190,
        difficultyLevel = "intermediate"
    ),
    ANXIETY_TOOLKIT(
        id = "anxiety_toolkit",
        displayName = "Anxiety Toolkit",
        icon = "🛠️",
        description = "Practical tools for managing anxious moments",
        color = "#F97316",
        estimatedMinutes = 160,
        difficultyLevel = "beginner"
    ),
    SLEEP_WELLNESS(
        id = "sleep_wellness",
        displayName = "Better Sleep",
        icon = "🌙",
        description = "Rest deeply and recover fully each night",
        color = "#6366F1",
        estimatedMinutes = 130,
        difficultyLevel = "beginner"
    );

    companion object {
        fun fromId(id: String): PathType? = values().find { it.id == id }
    }
}

/**
 * Domain model for a Learning Path
 */
data class LearningPath(
    val id: String,
    val type: PathType,
    val lessons: List<Lesson>,
    val progress: PathProgress,
    val isActive: Boolean,
    val recommendation: PathRecommendation? = null
)

/**
 * Path Progress tracking
 */
data class PathProgress(
    val completedLessons: Int,
    val totalLessons: Int,
    val percentage: Float,
    val currentLessonId: String? = null,
    val startedAt: Long,
    val lastAccessedAt: Long,
    val completedAt: Long? = null,
    val estimatedMinutesRemaining: Int
)

/**
 * Individual Lesson in a path
 */
data class Lesson(
    val id: String,
    val pathId: String,
    val orderIndex: Int,
    val title: String,
    val type: LessonType,
    val content: LessonContent,
    val estimatedMinutes: Int,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = true,
    val completedAt: Long? = null,
    val quizScore: Int? = null
)

/**
 * Lesson Type Enum
 */
enum class LessonType(val displayName: String) {
    READING("Reading"),
    REFLECTION("Reflection"),
    EXERCISE("Exercise"),
    JOURNAL_PROMPT("Journal Prompt"),
    MEDITATION("Meditation"),
    QUIZ("Quiz")
}

/**
 * Sealed class for lesson content
 * Each type has its own structure
 */
sealed class LessonContent {
    data class Reading(
        val title: String,
        val sections: List<ContentSection>,
        val keyTakeaways: List<String>,
        val reflectionQuestion: String? = null
    ) : LessonContent()

    data class Reflection(
        val prompt: String,
        val guidingQuestions: List<String>,
        val minWords: Int? = 100,
        val context: String? = null
    ) : LessonContent()

    data class Exercise(
        val title: String,
        val description: String,
        val steps: List<LearningExerciseStep>,
        val duration: Int,
        val materials: List<String> = emptyList()
    ) : LessonContent()

    data class JournalPrompt(
        val prompt: String,
        val context: String,
        val suggestedLength: String,
        val guidingQuestions: List<String> = emptyList()
    ) : LessonContent()

    data class Meditation(
        val title: String,
        val description: String,
        val durationOptions: List<Int>,
        val guidanceText: String,
        val steps: List<MeditationStep>,
        val backgroundSound: String? = null
    ) : LessonContent()

    data class Quiz(
        val title: String,
        val description: String,
        val questions: List<LearningQuizQuestion>,
        val passingScore: Int
    ) : LessonContent()
}

/**
 * Content Section for reading lessons
 */
data class ContentSection(
    val heading: String,
    val body: String,
    val bulletPoints: List<String> = emptyList(),
    val quote: String? = null,
    val quoteAuthor: String? = null
)

/**
 * Exercise Step for learning paths.
 *
 * Renamed from ExerciseStep to avoid collision with
 * com.prody.prashant.domain.haven.ExerciseStep.
 */
data class LearningExerciseStep(
    val stepNumber: Int,
    val instruction: String,
    val duration: Int? = null,
    val tip: String? = null
)

/**
 * Meditation Step
 */
data class MeditationStep(
    val phase: String,
    val instruction: String,
    val durationSeconds: Int
)

/**
 * Quiz Question for learning paths.
 *
 * Renamed from QuizQuestion to avoid collision with
 * com.prody.prashant.domain.games.QuizQuestion.
 */
data class LearningQuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int, // Index of correct option
    val explanation: String
)

/**
 * Path Recommendation from AI analysis
 */
data class PathRecommendation(
    val id: Long,
    val pathType: PathType,
    val reason: String,
    val basedOn: List<String>, // What journal entries/patterns led to this
    val confidence: Float,
    val createdAt: Long
)

/**
 * Learning Statistics
 */
data class LearningStats(
    val totalPathsStarted: Int,
    val totalPathsCompleted: Int,
    val totalLessonsCompleted: Int,
    val totalMinutesLearned: Int,
    val totalReflections: Int,
    val totalBadges: Int,
    val currentStreak: Int,
    val longestStreak: Int
)

/**
 * Path Badge
 */
data class PathBadge(
    val id: Long,
    val pathId: String,
    val badgeType: BadgeType,
    val badgeName: String,
    val badgeDescription: String,
    val badgeIcon: String,
    val earnedAt: Long,
    val rarity: BadgeRarity
)

/**
 * Badge Type Enum
 */
enum class BadgeType {
    PATH_COMPLETED,
    PERFECT_SCORE,
    SPEED_LEARNER,
    DEDICATED_STUDENT,
    WISDOM_SEEKER,
    REFLECTION_MASTER
}

/**
 * Badge Rarity
 */
enum class BadgeRarity(val displayName: String, val color: String) {
    COMMON("Common", "#9CA3AF"),
    RARE("Rare", "#3B82F6"),
    EPIC("Epic", "#8B5CF6"),
    LEGENDARY("Legendary", "#F59E0B")
}

/**
 * Learning Reflection
 */
data class LearningReflection(
    val id: Long,
    val lessonId: String,
    val pathId: String,
    val promptText: String,
    val userResponse: String,
    val aiInsight: String? = null,
    val createdAt: Long,
    val wordCount: Int,
    val mood: String? = null,
    val isBookmarked: Boolean = false
)

/**
 * Path Milestone
 */
data class PathMilestone(
    val title: String,
    val description: String,
    val icon: String,
    val achieved: Boolean,
    val achievedAt: Long? = null
)

/**
 * Learning Activity for tracking
 */
data class LearningActivity(
    val date: Long,
    val lessonsCompleted: Int,
    val minutesLearned: Int,
    val pathsActive: Int,
    val reflectionsWritten: Int
)
