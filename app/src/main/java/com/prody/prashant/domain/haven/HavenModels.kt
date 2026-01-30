package com.prody.prashant.domain.haven

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

/**
 * Session types available in Haven
 * Each type tailors the AI's approach and suggested techniques
 */
enum class SessionType(
    val displayName: String,
    val description: String,
    val color: Long // ARGB color for UI theming
) {
    CHECK_IN(
        "Daily Check-in",
        "How are you feeling today?",
        0xFF7B61FF
    ),
    ANXIETY(
        "Feeling Anxious",
        "Let's work through anxious feelings together",
        0xFF4A90E2
    ),
    STRESS(
        "Feeling Overwhelmed",
        "Take a breath. We'll tackle this step by step",
        0xFFE2A14A
    ),
    SADNESS(
        "Feeling Down",
        "It's okay to not be okay. I'm here",
        0xFF5E7CE2
    ),
    ANGER(
        "Processing Anger",
        "Let's understand and channel this energy",
        0xFFE24A4A
    ),
    GENERAL(
        "Just Want to Talk",
        "Sometimes we just need to talk. I'm listening",
        0xFF9B7BF7
    ),
    CRISIS_SUPPORT(
        "Need Support Now",
        "You're not alone. Let's get you help",
        0xFFE24A8D
    );

    /**
     * Get the Material Icon for this session type.
     * Use this instead of the old emoji icon property.
     */
    val icon: ImageVector
        get() = when (this) {
            CHECK_IN -> Icons.Outlined.WavingHand
            ANXIETY -> Icons.Outlined.Waves
            STRESS -> Icons.Outlined.Terrain
            SADNESS -> Icons.Outlined.Favorite
            ANGER -> Icons.Outlined.LocalFireDepartment
            GENERAL -> Icons.Outlined.Chat
            CRISIS_SUPPORT -> Icons.Outlined.Handshake
        }

    companion object {
        fun fromString(value: String): SessionType {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: GENERAL
        }
    }
}

/**
 * Therapeutic techniques that Haven can apply
 * Based on evidence-based CBT and DBT practices
 */
enum class TherapeuticTechnique(
    val displayName: String,
    val description: String,
    val category: String // "CBT", "DBT", "Grounding", "Validation"
) {
    CBT_THOUGHT_RECORD(
        "Thought Record",
        "Examine and reframe automatic thoughts",
        "CBT"
    ),
    CBT_COGNITIVE_RESTRUCTURING(
        "Cognitive Reframing",
        "Find alternative, balanced perspectives",
        "CBT"
    ),
    CBT_BEHAVIORAL_ACTIVATION(
        "Behavioral Activation",
        "Identify activities that improve mood",
        "CBT"
    ),
    DBT_MINDFULNESS(
        "Mindfulness",
        "Present moment awareness without judgment",
        "DBT"
    ),
    DBT_DISTRESS_TOLERANCE(
        "Distress Tolerance",
        "Cope with difficult emotions skillfully",
        "DBT"
    ),
    DBT_EMOTION_REGULATION(
        "Emotion Regulation",
        "Understand and manage intense feelings",
        "DBT"
    ),
    DBT_INTERPERSONAL(
        "Interpersonal Effectiveness",
        "Navigate relationships with confidence",
        "DBT"
    ),
    GROUNDING(
        "Grounding Exercise",
        "5-4-3-2-1 technique to anchor in the present",
        "Grounding"
    ),
    BREATHING(
        "Breathing Exercise",
        "Calm your nervous system with breath",
        "Grounding"
    ),
    VALIDATION(
        "Emotional Validation",
        "Your feelings make sense and are valid",
        "Validation"
    ),
    SOCRATIC(
        "Gentle Questioning",
        "Explore thoughts through curiosity",
        "CBT"
    ),
    PROGRESSIVE_MUSCLE_RELAXATION(
        "Progressive Muscle Relaxation",
        "Release physical tension systematically",
        "Grounding"
    );

    companion object {
        fun fromString(value: String): TherapeuticTechnique? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}

/**
 * Types of guided exercises available
 */
enum class ExerciseType(
    val displayName: String,
    val estimatedDuration: Int, // seconds
    val description: String
) {
    BOX_BREATHING(
        "Box Breathing",
        240, // 4 minutes
        "4-4-4-4 breathing pattern to calm your nervous system"
    ),
    FOUR_SEVEN_EIGHT_BREATHING(
        "4-7-8 Breathing",
        300, // 5 minutes
        "Dr. Weil's relaxation breathing technique"
    ),
    GROUNDING_54321(
        "5-4-3-2-1 Grounding",
        300, // 5 minutes
        "Engage your senses to anchor in the present moment"
    ),
    BODY_SCAN(
        "Body Scan Meditation",
        600, // 10 minutes
        "Progressive awareness of physical sensations"
    ),
    THOUGHT_RECORD(
        "Thought Record",
        600, // 10 minutes
        "CBT worksheet to examine and reframe thoughts"
    ),
    EMOTION_WHEEL(
        "Emotion Wheel",
        300, // 5 minutes
        "Identify and name your emotions with precision"
    ),
    GRATITUDE_MOMENT(
        "Gratitude Moment",
        180, // 3 minutes
        "Shift focus to what you're thankful for"
    ),
    PROGRESSIVE_RELAXATION(
        "Progressive Muscle Relaxation",
        900, // 15 minutes
        "Tense and release muscle groups to reduce tension"
    ),
    LOVING_KINDNESS(
        "Loving-Kindness Meditation",
        600, // 10 minutes
        "Cultivate compassion for yourself and others"
    );

    /**
     * Get the Material Icon for this exercise type.
     * Use this instead of the old emoji icon property.
     */
    val icon: ImageVector
        get() = when (this) {
            BOX_BREATHING -> Icons.Outlined.Square
            FOUR_SEVEN_EIGHT_BREATHING -> Icons.Outlined.Air
            GROUNDING_54321 -> Icons.Outlined.Public
            BODY_SCAN -> Icons.Outlined.SelfImprovement
            THOUGHT_RECORD -> Icons.Outlined.EditNote
            EMOTION_WHEEL -> Icons.Outlined.EmojiEmotions
            GRATITUDE_MOMENT -> Icons.Outlined.VolunteerActivism
            PROGRESSIVE_RELAXATION -> Icons.Outlined.FitnessCenter
            LOVING_KINDNESS -> Icons.Outlined.Favorite
        }

    companion object {
        fun fromString(value: String): ExerciseType? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}

/**
 * A message in a Haven conversation
 */
data class HavenMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val techniqueUsed: TherapeuticTechnique? = null,
    val exerciseSuggested: ExerciseType? = null,
    val isCrisisResponse: Boolean = false,
    val recalledMessage: String? = null,
    val recalledMessageId: String? = null
)

/**
 * A complete guided exercise with steps
 */
data class GuidedExercise(
    val type: ExerciseType,
    val title: String,
    val steps: List<ExerciseStep>,
    val durationSeconds: Int,
    val introMessage: String? = null,
    val completionMessage: String? = null
)

/**
 * A single step in a guided exercise
 */
data class ExerciseStep(
    val instruction: String,
    val durationSeconds: Int? = null, // null for steps without timer
    val breathingPattern: BreathingPattern? = null,
    val audioGuide: String? = null, // Text for text-to-speech
    val visualCue: VisualCue? = null
)

/**
 * Breathing pattern for breathing exercises
 */
data class BreathingPattern(
    val inhaleSeconds: Int,
    val holdInSeconds: Int = 0,
    val exhaleSeconds: Int,
    val holdOutSeconds: Int = 0,
    val cycles: Int = 1
) {
    val totalCycleTime: Int
        get() = inhaleSeconds + holdInSeconds + exhaleSeconds + holdOutSeconds
}

/**
 * Visual cue for exercise steps
 */
sealed class VisualCue {
    object ExpandingCircle : VisualCue() // For inhale
    object ContractingCircle : VisualCue() // For exhale
    object StaticCircle : VisualCue() // For hold
    data class ProgressBar(val progress: Float) : VisualCue()
    data class Image(val resourceName: String) : VisualCue()
}

/**
 * Thought record for CBT - structured worksheet
 */
data class ThoughtRecord(
    val situation: String,
    val automaticThought: String,
    val emotions: List<String>,
    val emotionIntensity: Map<String, Int>, // emotion -> intensity (1-10)
    val evidence: List<String>,
    val alternativeThoughts: List<String>,
    val balancedThought: String,
    val outcomeEmotions: Map<String, Int> // After reframing
)

/**
 * Crisis resources to display when crisis is detected
 */
data class CrisisResource(
    val name: String,
    val phone: String,
    val description: String,
    val availability: String,
    val url: String? = null
) {
    companion object {
        val CRISIS_RESOURCES = listOf(
            CrisisResource(
                name = "988 Suicide & Crisis Lifeline",
                phone = "988",
                description = "Free, confidential support 24/7",
                availability = "24/7"
            ),
            CrisisResource(
                name = "Crisis Text Line",
                phone = "Text HOME to 741741",
                description = "Text-based crisis support",
                availability = "24/7"
            ),
            CrisisResource(
                name = "SAMHSA National Helpline",
                phone = "1-800-662-4357",
                description = "Mental health and substance abuse support",
                availability = "24/7"
            ),
            CrisisResource(
                name = "Veterans Crisis Line",
                phone = "988 (Press 1)",
                description = "Support for veterans and their families",
                availability = "24/7"
            ),
            CrisisResource(
                name = "Trevor Project (LGBTQ Youth)",
                phone = "1-866-488-7386",
                description = "Crisis support for LGBTQ young people",
                availability = "24/7"
            ),
            CrisisResource(
                name = "National Domestic Violence Hotline",
                phone = "1-800-799-7233",
                description = "Support for domestic violence situations",
                availability = "24/7"
            )
        )
    }
}

/**
 * Session summary generated at the end of a Haven session
 */
data class SessionSummary(
    val sessionType: SessionType,
    val duration: Long, // milliseconds
    val messageCount: Int,
    val techniquesUsed: List<TherapeuticTechnique>,
    val exercisesCompleted: List<ExerciseType>,
    val moodBefore: Int?,
    val moodAfter: Int?,
    val moodChange: Int?, // Calculated: moodAfter - moodBefore
    val keyInsights: List<String>,
    val suggestedFollowUp: String?,
    val containedCrisis: Boolean
)

/**
 * Haven statistics for user progress tracking
 */
data class HavenStats(
    val totalSessions: Int,
    val completedSessions: Int,
    val totalExercises: Int,
    val completedExercises: Int,
    val totalTimeMinutes: Int,
    val averageMoodImprovement: Float?,
    val mostUsedSessionType: SessionType?,
    val mostUsedExercise: ExerciseType?,
    val streakDays: Int,
    val sessionTypeBreakdown: Map<SessionType, Int>,
    val exerciseTypeBreakdown: Map<ExerciseType, Int>,
    val hasCrisisHistory: Boolean
)

/**
 * AI response from Haven including metadata
 */
data class HavenAiResponse(
    val message: String,
    val techniqueApplied: TherapeuticTechnique? = null,
    val suggestedExercise: ExerciseType? = null,
    val isCrisisDetected: Boolean = false,
    val crisisLevel: CrisisLevel = CrisisLevel.NONE,
    val shouldEndSession: Boolean = false,
    val recalledContent: String? = null
)

/**
 * Crisis severity levels for appropriate response
 */
enum class CrisisLevel {
    NONE,
    MILD_DISTRESS,
    MODERATE_DISTRESS,
    SEVERE_CRISIS,
    IMMEDIATE_DANGER
}

/**
 * Haven conversation context for AI.
 *
 * Renamed from HavenContext to avoid collision with
 * com.prody.prashant.domain.intelligence.HavenContext.
 */
data class HavenConversationContext(
    val sessionType: SessionType,
    val previousMessages: List<HavenMessage>,
    val moodBefore: Int?,
    val userName: String?,
    val hasUsedTechniquesBefore: List<TherapeuticTechnique>,
    val preferredExercises: List<ExerciseType>,
    val sessionNumber: Int // How many sessions user has had
)
