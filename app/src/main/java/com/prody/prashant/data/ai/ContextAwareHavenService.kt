package com.prody.prashant.data.ai

import android.util.Log
import com.prody.prashant.domain.haven.ExerciseType
import com.prody.prashant.domain.haven.HavenAiResponse
import com.prody.prashant.domain.haven.HavenMessage
import com.prody.prashant.domain.haven.SessionType
import com.prody.prashant.domain.intelligence.StressSignal
import com.prody.prashant.domain.intelligence.StressSignalType
import com.prody.prashant.domain.intelligence.TrustLevel
import com.prody.prashant.domain.intelligence.UserContext
import com.prody.prashant.domain.intelligence.UserContextEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Type alias to disambiguate between the HavenConversationContext and Soul Layer HavenContext
private typealias HavenSessionContext = com.prody.prashant.domain.haven.HavenConversationContext
private typealias SoulLayerHavenContext = com.prody.prashant.domain.intelligence.HavenContext

/**
 * ================================================================================================
 * CONTEXT-AWARE HAVEN SERVICE - Soul Layer Enhanced Therapeutic AI
 * ================================================================================================
 *
 * This service wraps HavenAiService with Soul Layer intelligence, providing:
 * - Deep user context awareness (emotional history, trust level, patterns)
 * - Personalized therapeutic approach selection
 * - Crisis history sensitivity
 * - Adaptive communication based on user state
 * - Memory of previous Haven sessions
 *
 * Philosophy:
 * - Haven should feel like it REMEMBERS this specific user
 * - Therapeutic approach should adapt to what has worked before
 * - Crisis history informs how careful to be
 * - Trust level determines how deep Haven can go
 * - Recent journal content provides context for support
 *
 * Use this service instead of HavenAiService when you want Soul Layer intelligence.
 */
@Singleton
class ContextAwareHavenService @Inject constructor(
    private val havenAiService: HavenAiService,
    private val userContextEngine: UserContextEngine
) {
    companion object {
        private const val TAG = "ContextAwareHaven"
    }

    // =========================================================================
    // CONTEXT-AWARE SESSION MANAGEMENT
    // =========================================================================

    /**
     * Starts a new Haven session with full Soul Layer context.
     * This enriches the session with user history and personalized approach.
     */
    suspend fun startContextAwareSession(
        sessionType: SessionType,
        userName: String?,
        baseContext: HavenSessionContext
    ): Result<HavenAiResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting context-aware Haven session")

        // Get rich Haven context from Soul Layer
        val soulLayerContext = userContextEngine.getContextForHaven()

        // Enrich the base context with Soul Layer intelligence
        val enrichedContext = enrichHavenContext(baseContext, soulLayerContext)

        // Start session with enriched context
        havenAiService.startSession(sessionType, userName, enrichedContext)
    }

    /**
     * Continues conversation with Soul Layer context awareness.
     */
    suspend fun continueContextAwareConversation(
        sessionType: SessionType,
        baseContext: HavenSessionContext,
        userMessage: String
    ): Result<HavenAiResponse> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Continuing context-aware conversation")

        val soulLayerContext = userContextEngine.getContextForHaven()
        val enrichedContext = enrichHavenContext(baseContext, soulLayerContext)

        havenAiService.continueConversation(sessionType, enrichedContext, userMessage)
    }

    /**
     * Streaming version of context-aware conversation.
     */
    fun continueContextAwareConversationStream(
        sessionType: SessionType,
        baseContext: HavenSessionContext,
        userMessage: String
    ): Flow<Result<HavenAiResponse>> = flow {
        Log.d(TAG, "Starting context-aware streaming conversation")

        val soulLayerContext = userContextEngine.getContextForHaven()
        val enrichedContext = enrichHavenContext(baseContext, soulLayerContext)

        havenAiService.continueConversationStream(sessionType, enrichedContext, userMessage)
            .collect { result ->
                emit(result)
            }
    }.flowOn(Dispatchers.IO)

    // =========================================================================
    // INTELLIGENT SESSION TYPE SELECTION
    // =========================================================================

    /**
     * Suggests the most appropriate session type based on user's current state.
     * This uses Soul Layer intelligence to make informed recommendations.
     */
    suspend fun suggestSessionType(): SessionTypeSuggestion = withContext(Dispatchers.IO) {
        Log.d(TAG, "Generating session type suggestion")

        val havenContext = userContextEngine.getContextForHaven()
        val userContext = havenContext.userContext

        // Analyze user state and suggest appropriate session
        val suggestion = when {
            // Crisis takes absolute priority
            havenContext.crisisHistory -> SessionTypeSuggestion(
                primarySuggestion = SessionType.CHECK_IN,
                reason = "Let's start with a gentle check-in to see how you're doing",
                confidence = 1.0f,
                alternatives = listOf(SessionType.GENERAL)
            )

            // Struggling users need support
            userContext.isStruggling -> {
                val sessionType = inferSessionFromStressSignals(userContext.stressSignals)
                SessionTypeSuggestion(
                    primarySuggestion = sessionType,
                    reason = getSessionTypeReason(sessionType, userContext),
                    confidence = 0.85f,
                    alternatives = listOf(SessionType.CHECK_IN, SessionType.GENERAL)
                )
            }

            // Check mood-based suggestions
            userContext.dominantMood != null -> {
                val sessionType = inferSessionFromMood(userContext.dominantMood)
                SessionTypeSuggestion(
                    primarySuggestion = sessionType,
                    reason = getSessionTypeReason(sessionType, userContext),
                    confidence = 0.7f,
                    alternatives = listOf(SessionType.CHECK_IN, SessionType.GENERAL)
                )
            }

            // New Haven users get a gentle check-in
            havenContext.sessionCount == 0 -> SessionTypeSuggestion(
                primarySuggestion = SessionType.CHECK_IN,
                reason = "Let's get to know each other with a simple check-in",
                confidence = 0.9f,
                alternatives = listOf(SessionType.GENERAL)
            )

            // Default to general session
            else -> SessionTypeSuggestion(
                primarySuggestion = SessionType.GENERAL,
                reason = "Open conversation - share whatever's on your mind",
                confidence = 0.6f,
                alternatives = listOf(SessionType.CHECK_IN)
            )
        }

        suggestion
    }

    /**
     * Gets a personalized opening message for Haven based on context.
     */
    suspend fun getContextAwareOpening(): HavenOpeningContent = withContext(Dispatchers.IO) {
        Log.d(TAG, "Generating context-aware opening")

        val havenContext = userContextEngine.getContextForHaven()
        val userContext = havenContext.userContext
        val firstName = userContext.firstName

        // Generate personalized opening based on user state
        val (greeting, subtitle, tone) = when {
            userContext.isStruggling -> Triple(
                "Hey $firstName",
                "I noticed things have been difficult lately. I'm here.",
                HavenTone.GENTLE
            )

            havenContext.sessionCount == 0 -> Triple(
                "Hi $firstName",
                "Welcome to Haven. This is your space to be heard.",
                HavenTone.WARM
            )

            havenContext.sessionCount == 1 -> Triple(
                "Welcome back, $firstName",
                "Good to see you again. How have things been since we talked?",
                HavenTone.WARM
            )

            userContext.daysSinceLastEntry > 7 -> Triple(
                "Hey $firstName",
                "It's been a little while. I'm glad you're here.",
                HavenTone.SUPPORTIVE
            )

            userContext.isThriving -> Triple(
                "Hey $firstName!",
                "Good to see you. What's on your mind today?",
                HavenTone.ENGAGED
            )

            else -> Triple(
                "Hi $firstName",
                "I'm here whenever you need to talk.",
                HavenTone.WARM
            )
        }

        // Generate contextual prompts
        val prompts = generateContextualPrompts(havenContext, userContext)

        HavenOpeningContent(
            greeting = greeting,
            subtitle = subtitle,
            tone = tone,
            suggestedPrompts = prompts,
            showRecentJournalReference = havenContext.recentJournalContent.isNotEmpty() &&
                userContext.trustLevel >= TrustLevel.BUILDING
        )
    }

    // =========================================================================
    // THERAPEUTIC APPROACH SELECTION
    // =========================================================================

    /**
     * Gets the recommended therapeutic approach based on user history.
     */
    suspend fun getRecommendedApproach(): TherapeuticApproachRecommendation =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Getting recommended therapeutic approach")

            val havenContext = userContextEngine.getContextForHaven()

            TherapeuticApproachRecommendation(
                primaryApproach = havenContext.preferredTherapeuticApproach,
                reason = getApproachReason(havenContext.preferredTherapeuticApproach, havenContext),
                alternativeApproaches = getAlternativeApproaches(
                    havenContext.preferredTherapeuticApproach
                ),
                basedOnHistory = havenContext.sessionCount > 0
            )
        }

    /**
     * Suggests an exercise based on current context and history.
     */
    suspend fun suggestContextAwareExercise(
        sessionType: SessionType
    ): Result<ExerciseSuggestion> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Suggesting context-aware exercise")

        val havenContext = userContextEngine.getContextForHaven()
        val userContext = havenContext.userContext

        // Get base exercise suggestion
        val exerciseResult = havenAiService.suggestExercise(
            sessionType,
            HavenSessionContext(
                sessionType = sessionType,
                previousMessages = emptyList(),
                moodBefore = userContext.dominantMood?.ordinal,
                userName = userContext.displayName,
                hasUsedTechniquesBefore = emptyList(),
                preferredExercises = emptyList(),
                sessionNumber = havenContext.sessionCount + 1
            )
        )

        exerciseResult.map { exerciseType ->
            ExerciseSuggestion(
                exercise = exerciseType,
                reason = getExerciseReason(exerciseType, userContext),
                duration = getExerciseDuration(exerciseType),
                isFirstTime = !hasUsedExerciseBefore(exerciseType, havenContext),
                alternativeExercise = getAlternativeExercise(exerciseType, sessionType)
            )
        }
    }

    // =========================================================================
    // CONTEXT ENRICHMENT
    // =========================================================================

    /**
     * Enriches base HavenContext with Soul Layer intelligence.
     */
    private fun enrichHavenContext(
        baseContext: HavenSessionContext,
        soulLayerContext: SoulLayerHavenContext
    ): HavenSessionContext {
        val userContext = soulLayerContext.userContext

        // Create enriched context with additional information
        // Note: We're working with the existing HavenContext structure
        // and enriching it with Soul Layer data

        return HavenSessionContext(
            sessionType = baseContext.sessionType,
            previousMessages = baseContext.previousMessages,
            moodBefore = baseContext.moodBefore ?: userContext.dominantMood?.ordinal,
            userName = userContext.displayName,
            hasUsedTechniquesBefore = baseContext.hasUsedTechniquesBefore,
            preferredExercises = baseContext.preferredExercises,
            sessionNumber = soulLayerContext.sessionCount + 1
        )
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private fun inferSessionFromStressSignals(signals: List<StressSignal>): SessionType {
        if (signals.isEmpty()) return SessionType.CHECK_IN

        val primarySignal = signals.maxByOrNull { it.confidence }?.type
            ?: return SessionType.CHECK_IN

        return when (primarySignal) {
            StressSignalType.ANXIETY_MARKERS -> SessionType.ANXIETY
            StressSignalType.OVERWHELM -> SessionType.STRESS
            StressSignalType.NEGATIVE_LANGUAGE, StressSignalType.LOW_SELF_WORTH -> SessionType.SADNESS
            StressSignalType.WORK_STRESS -> SessionType.STRESS
            StressSignalType.SLEEP_ISSUES -> SessionType.ANXIETY
            StressSignalType.ISOLATION -> SessionType.SADNESS
            StressSignalType.RELATIONSHIP_STRAIN -> SessionType.GENERAL
            StressSignalType.HEALTH_CONCERNS -> SessionType.CHECK_IN
        }
    }

    private fun inferSessionFromMood(mood: com.prody.prashant.domain.model.Mood): SessionType {
        return when (mood) {
            com.prody.prashant.domain.model.Mood.ANXIOUS -> SessionType.ANXIETY
            com.prody.prashant.domain.model.Mood.SAD -> SessionType.SADNESS
            com.prody.prashant.domain.model.Mood.CONFUSED -> SessionType.GENERAL
            else -> SessionType.CHECK_IN
        }
    }

    private fun getSessionTypeReason(
        sessionType: SessionType,
        userContext: UserContext
    ): String {
        return when (sessionType) {
            SessionType.ANXIETY -> "You seem to be dealing with some anxiety. Let's work through it together."
            SessionType.STRESS -> "It sounds like things have been overwhelming. Let's find some relief."
            SessionType.SADNESS -> "I've noticed you've been feeling down. I'm here to listen."
            SessionType.ANGER -> "Let's explore what's been frustrating you."
            SessionType.CHECK_IN -> "Let's see how you're doing today."
            SessionType.GENERAL -> "Open space to talk about whatever's on your mind."
            SessionType.CRISIS_SUPPORT -> "I'm here for you right now. Your safety matters."
        }
    }

    private fun generateContextualPrompts(
        havenContext: SoulLayerHavenContext,
        userContext: UserContext
    ): List<String> {
        val prompts = mutableListOf<String>()

        // Add prompts based on recent journal content
        if (havenContext.recentJournalContent.isNotEmpty()) {
            prompts.add("I want to talk about what I wrote in my journal")
        }

        // Add prompts based on user state
        when {
            userContext.isStruggling -> {
                prompts.add("Things have been really hard lately")
                prompts.add("I don't know where to start")
            }
            userContext.stressSignals.any { it.type == StressSignalType.ANXIETY_MARKERS } -> {
                prompts.add("I've been feeling anxious")
                prompts.add("I can't stop worrying")
            }
            userContext.stressSignals.any { it.type == StressSignalType.OVERWHELM } -> {
                prompts.add("I'm overwhelmed")
                prompts.add("There's too much going on")
            }
            else -> {
                prompts.add("I just need to talk")
                prompts.add("Something's been on my mind")
            }
        }

        // Always include a general option
        prompts.add("I'm not sure what I need")

        return prompts.take(4)
    }

    private fun getApproachReason(
        approach: SoulLayerHavenContext.TherapeuticApproach,
        havenContext: SoulLayerHavenContext
    ): String {
        val basedOnHistory = if (havenContext.sessionCount > 0) {
            "Based on our previous conversations, "
        } else {
            ""
        }

        return when (approach) {
            SoulLayerHavenContext.TherapeuticApproach.CBT ->
                "${basedOnHistory}cognitive-behavioral techniques seem to resonate with you."
            SoulLayerHavenContext.TherapeuticApproach.DBT ->
                "${basedOnHistory}dialectical techniques help you process emotions effectively."
            SoulLayerHavenContext.TherapeuticApproach.MINDFULNESS ->
                "${basedOnHistory}mindfulness practices work well for you."
            SoulLayerHavenContext.TherapeuticApproach.ACT ->
                "${basedOnHistory}acceptance-based approaches help you find peace."
            SoulLayerHavenContext.TherapeuticApproach.GENERAL ->
                "${basedOnHistory}we'll find what works best for you."
        }
    }

    private fun getAlternativeApproaches(
        primary: SoulLayerHavenContext.TherapeuticApproach
    ): List<SoulLayerHavenContext.TherapeuticApproach> {
        return SoulLayerHavenContext.TherapeuticApproach.entries
            .filter { it != primary }
            .take(2)
    }

    private fun getExerciseReason(
        exercise: ExerciseType,
        userContext: UserContext
    ): String {
        return when (exercise) {
            ExerciseType.BOX_BREATHING -> "Box breathing can help calm your nervous system."
            ExerciseType.FOUR_SEVEN_EIGHT_BREATHING -> "This breathing pattern activates your relaxation response."
            ExerciseType.GROUNDING_54321 -> "Grounding helps bring you back to the present moment."
            ExerciseType.BODY_SCAN -> "A body scan helps you notice and release tension."
            ExerciseType.THOUGHT_RECORD -> "Writing down thoughts helps you examine them more objectively."
            ExerciseType.EMOTION_WHEEL -> "The emotion wheel helps identify what you're really feeling."
            ExerciseType.GRATITUDE_MOMENT -> "Gratitude shifts your focus to what's going well."
            ExerciseType.PROGRESSIVE_RELAXATION -> "Progressive relaxation helps release physical tension."
            ExerciseType.LOVING_KINDNESS -> "Loving-kindness meditation cultivates self-compassion."
        }
    }

    private fun getExerciseDuration(exercise: ExerciseType): Int {
        return when (exercise) {
            ExerciseType.BOX_BREATHING -> 3
            ExerciseType.FOUR_SEVEN_EIGHT_BREATHING -> 3
            ExerciseType.GROUNDING_54321 -> 5
            ExerciseType.BODY_SCAN -> 10
            ExerciseType.THOUGHT_RECORD -> 10
            ExerciseType.EMOTION_WHEEL -> 5
            ExerciseType.GRATITUDE_MOMENT -> 3
            ExerciseType.PROGRESSIVE_RELAXATION -> 15
            ExerciseType.LOVING_KINDNESS -> 10
        }
    }

    private fun hasUsedExerciseBefore(
        exercise: ExerciseType,
        havenContext: SoulLayerHavenContext
    ): Boolean {
        // This would need to check session history - simplified for now
        return havenContext.sessionCount > 3
    }

    private fun getAlternativeExercise(
        primary: ExerciseType,
        sessionType: SessionType
    ): ExerciseType? {
        return when (sessionType) {
            SessionType.ANXIETY -> if (primary == ExerciseType.BOX_BREATHING) {
                ExerciseType.GROUNDING_54321
            } else {
                ExerciseType.BOX_BREATHING
            }
            SessionType.STRESS -> if (primary == ExerciseType.GROUNDING_54321) {
                ExerciseType.BODY_SCAN
            } else {
                ExerciseType.GROUNDING_54321
            }
            SessionType.SADNESS -> if (primary == ExerciseType.GRATITUDE_MOMENT) {
                ExerciseType.LOVING_KINDNESS
            } else {
                ExerciseType.GRATITUDE_MOMENT
            }
            else -> null
        }
    }

    // =========================================================================
    // DELEGATION TO BASE SERVICE
    // =========================================================================

    fun isConfigured() = havenAiService.isConfigured()

    suspend fun extractInsights(messages: List<HavenMessage>, sessionType: SessionType) =
        havenAiService.extractInsights(messages, sessionType)
}

// ================================================================================================
// CONTEXT-AWARE HAVEN MODELS
// ================================================================================================

/**
 * Suggestion for session type based on user context.
 */
data class SessionTypeSuggestion(
    val primarySuggestion: SessionType,
    val reason: String,
    val confidence: Float,
    val alternatives: List<SessionType>
)

/**
 * Opening content for Haven with context awareness.
 */
data class HavenOpeningContent(
    val greeting: String,
    val subtitle: String,
    val tone: HavenTone,
    val suggestedPrompts: List<String>,
    val showRecentJournalReference: Boolean
)

enum class HavenTone {
    WARM,
    GENTLE,
    SUPPORTIVE,
    ENGAGED
}

/**
 * Recommendation for therapeutic approach.
 */
data class TherapeuticApproachRecommendation(
    val primaryApproach: SoulLayerHavenContext.TherapeuticApproach,
    val reason: String,
    val alternativeApproaches: List<SoulLayerHavenContext.TherapeuticApproach>,
    val basedOnHistory: Boolean
)

/**
 * Context-aware exercise suggestion.
 */
data class ExerciseSuggestion(
    val exercise: ExerciseType,
    val reason: String,
    val duration: Int, // in minutes
    val isFirstTime: Boolean,
    val alternativeExercise: ExerciseType?
)
