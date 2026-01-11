package com.prody.prashant.data.ai

import android.util.Log
import com.prody.prashant.domain.intelligence.*
import com.prody.prashant.domain.model.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ================================================================================================
 * CONTEXT-AWARE BUDDHA SERVICE - Soul Layer Enhanced AI
 * ================================================================================================
 *
 * This service wraps BuddhaAiService with Soul Layer intelligence, providing:
 * - User context awareness (archetype, emotional state, trust level)
 * - Personalized response generation based on user patterns
 * - Adaptive tone and communication style
 * - Memory of previous interactions
 * - Sensitive topic handling
 *
 * Philosophy:
 * - Every response should feel like Buddha KNOWS this specific user
 * - Context should inform tone, depth, and content suggestions
 * - Struggling users get gentler, more supportive responses
 * - Thriving users get more challenging, growth-oriented wisdom
 * - Trust level determines how deep Buddha can go
 *
 * Use this service instead of BuddhaAiService when you want Soul Layer intelligence.
 */
@Singleton
class ContextAwareBuddhaService @Inject constructor(
    private val buddhaAiService: BuddhaAiService,
    private val userContextEngine: UserContextEngine,
    private val geminiService: GeminiService
) {
    companion object {
        private const val TAG = "ContextAwareBuddha"
    }

    // =========================================================================
    // CONTEXT-AWARE JOURNAL RESPONSES
    // =========================================================================

    /**
     * Generates a context-aware Buddha response for a journal entry.
     * This is the primary method for personalized journal responses.
     */
    suspend fun getJournalResponse(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Generating context-aware journal response")

        // Get rich Buddha context
        val buddhaContext = userContextEngine.getContextForBuddha()

        // Build context-enriched prompt
        val enrichedPrompt = buildContextAwareJournalPrompt(
            content = content,
            mood = mood,
            moodIntensity = moodIntensity,
            wordCount = wordCount,
            context = buddhaContext
        )

        // Generate response with context
        geminiService.generateCustomResponse(enrichedPrompt, includeSystemPrompt = false)
    }

    /**
     * Generates a streaming context-aware Buddha response.
     */
    fun getJournalResponseStream(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int
    ): Flow<GeminiResult<String>> = flow {
        Log.d(TAG, "Starting context-aware streaming response")

        // Get rich Buddha context
        val buddhaContext = userContextEngine.getContextForBuddha()

        // Build context-enriched prompt
        val enrichedPrompt = buildContextAwareJournalPrompt(
            content = content,
            mood = mood,
            moodIntensity = moodIntensity,
            wordCount = wordCount,
            context = buddhaContext
        )

        // Generate streaming response
        geminiService.generateCustomResponseStream(enrichedPrompt, includeSystemPrompt = false)
            .collect { result ->
                emit(result)
            }
    }.flowOn(Dispatchers.IO)

    // =========================================================================
    // CONTEXT-AWARE DAILY WISDOM
    // =========================================================================

    /**
     * Gets daily wisdom tailored to the user's current state.
     */
    suspend fun getDailyWisdom(): GeminiResult<String> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Generating context-aware daily wisdom")

        val context = userContextEngine.getCurrentContext()
        val buddhaContext = userContextEngine.getContextForBuddha()

        // Build personalized wisdom prompt
        val wisdomPrompt = buildPersonalizedWisdomPrompt(context, buddhaContext)

        geminiService.generateCustomResponse(wisdomPrompt, includeSystemPrompt = false)
    }

    // =========================================================================
    // CONTEXT-AWARE PROMPTS
    // =========================================================================

    /**
     * Gets a journal prompt tailored to user context.
     */
    suspend fun getJournalPrompt(mood: Mood): GeminiResult<String> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Generating context-aware journal prompt")

        val context = userContextEngine.getCurrentContext()
        val buddhaContext = userContextEngine.getContextForBuddha()

        val promptRequest = buildContextAwarePromptRequest(mood, context, buddhaContext)

        geminiService.generateCustomResponse(promptRequest, includeSystemPrompt = false)
    }

    // =========================================================================
    // CONTEXT-AWARE REFLECTIONS
    // =========================================================================

    /**
     * Gets a morning reflection tailored to user.
     */
    suspend fun getMorningReflection(): GeminiResult<String> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Generating context-aware morning reflection")

        val context = userContextEngine.getCurrentContext()

        val prompt = buildMorningReflectionPrompt(context)

        geminiService.generateCustomResponse(prompt, includeSystemPrompt = false)
    }

    /**
     * Gets an evening reflection tailored to user.
     */
    suspend fun getEveningReflection(): GeminiResult<String> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Generating context-aware evening reflection")

        val context = userContextEngine.getCurrentContext()

        val prompt = buildEveningReflectionPrompt(context)

        geminiService.generateCustomResponse(prompt, includeSystemPrompt = false)
    }

    // =========================================================================
    // CONTEXT-AWARE CELEBRATIONS
    // =========================================================================

    /**
     * Gets a personalized streak celebration.
     */
    suspend fun getStreakCelebration(
        streakCount: Int,
        previousBest: Int = 0
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Generating context-aware streak celebration")

        val context = userContextEngine.getCurrentContext()

        val prompt = buildStreakCelebrationPrompt(streakCount, previousBest, context)

        geminiService.generateCustomResponse(prompt, includeSystemPrompt = false)
    }

    // =========================================================================
    // PROMPT BUILDING - THE MAGIC
    // =========================================================================

    private fun buildContextAwareJournalPrompt(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int,
        context: BuddhaContext
    ): String {
        val userContext = context.userContext
        val firstName = userContext.firstName

        // Build the context-aware system prompt
        return buildString {
            // Core identity
            append(CONTEXT_AWARE_CORE_IDENTITY)
            appendLine()
            appendLine()

            // User context section
            append("## Who You're Talking To\n")
            append("Name: $firstName\n")
            append("Days with Prody: ${userContext.daysWithPrody}\n")
            append("User type: ${describeArchetype(userContext.userArchetype)}\n")
            append("Trust level: ${describeTrustLevel(userContext.trustLevel)}\n")
            append("Engagement: ${describeEngagement(userContext.engagementLevel)}\n")
            append("Current streak: ${userContext.currentStreak.reflectionStreak.current} days\n")
            appendLine()

            // Emotional context
            append("## Emotional Context\n")
            append("Current mood: ${mood.displayName} (intensity: $moodIntensity/10)\n")
            append("Recent mood trend: ${describeMoodTrend(userContext.recentMoodTrend)}\n")
            append("Energy level: ${describeEnergyLevel(userContext.emotionalEnergy)}\n")

            if (userContext.stressSignals.isNotEmpty()) {
                append("Stress signals detected: ${userContext.stressSignals.joinToString { it.type.name.lowercase().replace("_", " ") }}\n")
            }
            appendLine()

            // What they've been writing about
            if (context.recentJournalThemes.isNotEmpty()) {
                append("## Recent Themes\n")
                append("They've been writing about: ${context.recentJournalThemes.take(3).joinToString(", ")}\n")
                appendLine()
            }

            // Patterns observed
            if (context.recurringPatterns.isNotEmpty()) {
                append("## Patterns You've Noticed\n")
                context.recurringPatterns.forEach { pattern ->
                    append("- $pattern\n")
                }
                appendLine()
            }

            // Recent wins (for positive reinforcement)
            if (userContext.recentWins.isNotEmpty()) {
                append("## Recent Wins to Acknowledge\n")
                userContext.recentWins.forEach { win ->
                    append("- ${win.title}: ${win.description}\n")
                }
                appendLine()
            }

            // Wisdom style preference
            append("## How to Communicate\n")
            append("Preferred wisdom style: ${describeWisdomStyle(context.preferredWisdomStyle)}\n")
            append("Preferred tone: ${describeTone(userContext.preferredTone)}\n")
            appendLine()

            // Sensitive topics to avoid
            if (context.avoidTopics.isNotEmpty()) {
                append("## Sensitive Topics\n")
                append("Be careful around these topics (they may be triggering): ${context.avoidTopics.joinToString(", ")}\n")
                appendLine()
            }

            // State-specific guidelines
            append(getStateSpecificGuidelines(userContext))
            appendLine()

            // The actual journal entry
            append("## Current Journal Entry\n")
            append("Word count: $wordCount words\n")
            append("Content:\n\"$content\"\n")
            appendLine()

            // Response instructions
            append("## Your Response\n")
            append(getResponseInstructions(userContext, mood, moodIntensity))
        }
    }

    private fun buildPersonalizedWisdomPrompt(
        context: UserContext,
        buddhaContext: BuddhaContext
    ): String {
        return buildString {
            append(CONTEXT_AWARE_CORE_IDENTITY)
            appendLine()
            appendLine()

            append("## Generate Daily Wisdom for ${context.firstName}\n\n")

            append("User context:\n")
            append("- ${describeArchetype(context.userArchetype)}\n")
            append("- Mood trend: ${describeMoodTrend(context.recentMoodTrend)}\n")
            append("- Energy: ${describeEnergyLevel(context.emotionalEnergy)}\n")
            append("- Trust level: ${describeTrustLevel(context.trustLevel)}\n")
            appendLine()

            if (context.recentThemes.isNotEmpty()) {
                append("They've been reflecting on: ${context.recentThemes.take(3).joinToString(", ")}\n")
            }

            if (context.recurringChallenges.isNotEmpty()) {
                append("They're working through: ${context.recurringChallenges.firstOrNull() ?: "personal growth"}\n")
            }
            appendLine()

            // Day-specific context
            append("Today is ${context.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}")
            if (context.specialDay != null) {
                append(" - ${describeSpecialDay(context.specialDay)}")
            }
            appendLine()
            appendLine()

            append("Wisdom style preference: ${describeWisdomStyle(buddhaContext.preferredWisdomStyle)}\n")
            appendLine()

            append("Generate a brief, personalized daily thought that:\n")
            append("- Speaks directly to where they are right now\n")
            append("- Matches their preferred wisdom style\n")
            when {
                context.isStruggling -> append("- Is gentle and supportive (they're having a hard time)\n")
                context.isThriving -> append("- Challenges them to keep growing (they're doing well)\n")
                else -> append("- Offers grounded perspective\n")
            }
            append("- Is 2-4 sentences maximum\n")
            append("- Feels personal, not generic\n")
            appendLine()

            append("Just provide the wisdom, no preamble or explanation.")
        }
    }

    private fun buildContextAwarePromptRequest(
        mood: Mood,
        context: UserContext,
        buddhaContext: BuddhaContext
    ): String {
        return buildString {
            append(CONTEXT_AWARE_CORE_IDENTITY)
            appendLine()
            appendLine()

            append("## Generate a Journal Prompt\n\n")

            append("For: ${context.firstName}\n")
            append("Current mood: ${mood.displayName}\n")
            append("User type: ${describeArchetype(context.userArchetype)}\n")
            appendLine()

            if (buddhaContext.recentJournalThemes.isNotEmpty()) {
                append("Recent topics: ${buddhaContext.recentJournalThemes.take(3).joinToString(", ")}\n")
            }
            appendLine()

            append("Generate a journal prompt that:\n")
            append("- Is relevant to their ${mood.displayName.lowercase()} mood\n")
            when {
                context.isStruggling -> append("- Is gentle and validating (they need support)\n")
                context.isThriving -> append("- Encourages deeper reflection (they're ready for it)\n")
                else -> append("- Invites meaningful reflection\n")
            }
            append("- Is open-ended but focused\n")
            append("- Is 1-2 sentences maximum\n")
            appendLine()

            append("Just provide the prompt, nothing else.")
        }
    }

    private fun buildMorningReflectionPrompt(context: UserContext): String {
        return buildString {
            append(CONTEXT_AWARE_CORE_IDENTITY)
            appendLine()
            appendLine()

            append("## Morning Reflection for ${context.firstName}\n\n")

            append("Context:\n")
            append("- Day: ${context.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}\n")
            append("- User type: ${describeArchetype(context.userArchetype)}\n")
            append("- Mood trend: ${describeMoodTrend(context.recentMoodTrend)}\n")
            appendLine()

            if (context.specialDay != null) {
                append("Today is special: ${describeSpecialDay(context.specialDay)}\n")
                appendLine()
            }

            append("Generate a brief morning reflection that:\n")
            append("- Helps them start the day with intention\n")
            when {
                context.isStruggling -> append("- Is gentle and gives them permission to go slow\n")
                context.isWeekend -> append("- Honors the weekend energy\n")
                context.dayOfWeek == java.time.DayOfWeek.MONDAY -> append("- Acknowledges fresh start energy\n")
                else -> append("- Is grounded and practical\n")
            }
            append("- Is 2-3 sentences maximum\n")
            appendLine()

            append("Just provide the reflection.")
        }
    }

    private fun buildEveningReflectionPrompt(context: UserContext): String {
        return buildString {
            append(CONTEXT_AWARE_CORE_IDENTITY)
            appendLine()
            appendLine()

            append("## Evening Reflection for ${context.firstName}\n\n")

            append("Context:\n")
            append("- Day: ${context.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}\n")
            append("- User type: ${describeArchetype(context.userArchetype)}\n")
            append("- Days since last entry: ${context.daysSinceLastEntry}\n")
            appendLine()

            append("Generate a brief evening reflection that:\n")
            append("- Helps them wind down with gratitude\n")
            when {
                context.isStruggling -> append("- Offers compassion for any struggles today\n")
                context.dayOfWeek == java.time.DayOfWeek.FRIDAY -> append("- Celebrates the week's end\n")
                context.dayOfWeek == java.time.DayOfWeek.SUNDAY -> append("- Gently prepares for the new week\n")
                else -> append("- Encourages peaceful rest\n")
            }
            append("- Is 2-3 sentences maximum\n")
            appendLine()

            append("Just provide the reflection.")
        }
    }

    private fun buildStreakCelebrationPrompt(
        streakCount: Int,
        previousBest: Int,
        context: UserContext
    ): String {
        return buildString {
            append(CONTEXT_AWARE_CORE_IDENTITY)
            appendLine()
            appendLine()

            append("## Streak Celebration for ${context.firstName}\n\n")

            append("Achievement: $streakCount-day streak\n")
            if (streakCount > previousBest) {
                append("NEW PERSONAL BEST! (Previous: $previousBest days)\n")
            }
            appendLine()

            append("User context:\n")
            append("- Total entries: ${context.totalEntries}\n")
            append("- Days with Prody: ${context.daysWithPrody}\n")
            append("- User type: ${describeArchetype(context.userArchetype)}\n")
            appendLine()

            append("Generate a personalized celebration that:\n")
            append("- Acknowledges their specific achievement ($streakCount days)\n")
            append("- Connects consistency to their growth journey\n")
            if (streakCount > previousBest) {
                append("- Celebrates this new milestone enthusiastically\n")
            }
            append("- Is warm and genuine, 2-3 sentences\n")
            append("- Avoids cliches\n")
            appendLine()

            append("Just provide the celebration message.")
        }
    }

    // =========================================================================
    // HELPER FUNCTIONS
    // =========================================================================

    private fun describeArchetype(archetype: UserArchetype): String {
        return when (archetype) {
            UserArchetype.EXPLORER -> "New user exploring the app"
            UserArchetype.CONSISTENT -> "Consistent, regular journaler"
            UserArchetype.STRUGGLING -> "Going through a difficult time"
            UserArchetype.THRIVING -> "In a good place, thriving"
            UserArchetype.RETURNING -> "Returning after time away"
            UserArchetype.SPORADIC -> "Occasional user, sporadic engagement"
        }
    }

    private fun describeTrustLevel(level: TrustLevel): String {
        return when (level) {
            TrustLevel.NEW -> "New - still building relationship"
            TrustLevel.BUILDING -> "Building - starting to open up"
            TrustLevel.ESTABLISHED -> "Established - comfortable with depth"
            TrustLevel.DEEP -> "Deep - shares vulnerably"
        }
    }

    private fun describeEngagement(level: EngagementLevel): String {
        return when (level) {
            EngagementLevel.NEW -> "Brand new user"
            EngagementLevel.DAILY -> "Daily engagement"
            EngagementLevel.REGULAR -> "Regular engagement"
            EngagementLevel.SPORADIC -> "Sporadic engagement"
            EngagementLevel.CHURNING -> "At risk of leaving"
            EngagementLevel.RETURNING -> "Just returned after break"
        }
    }

    private fun describeMoodTrend(trend: MoodTrend): String {
        return when (trend) {
            MoodTrend.IMPROVING -> "Getting better lately"
            MoodTrend.DECLINING -> "Trending downward"
            MoodTrend.STABLE -> "Stable"
            MoodTrend.VOLATILE -> "Up and down (volatile)"
        }
    }

    private fun describeEnergyLevel(level: EnergyLevel): String {
        return when (level) {
            EnergyLevel.LOW -> "Low energy (short entries, possible fatigue)"
            EnergyLevel.MEDIUM -> "Medium energy"
            EnergyLevel.HIGH -> "High energy (long, detailed entries)"
        }
    }

    private fun describeWisdomStyle(style: BuddhaContext.WisdomStyle): String {
        return when (style) {
            BuddhaContext.WisdomStyle.STOIC -> "Stoic (Marcus Aurelius, Seneca)"
            BuddhaContext.WisdomStyle.EASTERN -> "Eastern (Buddhist, Zen)"
            BuddhaContext.WisdomStyle.PRACTICAL -> "Practical and actionable"
            BuddhaContext.WisdomStyle.POETIC -> "Poetic and metaphorical"
            BuddhaContext.WisdomStyle.DIRECT -> "Direct and concise"
        }
    }

    private fun describeTone(tone: CommunicationTone): String {
        return when (tone) {
            CommunicationTone.WARM -> "Warm and friendly"
            CommunicationTone.GENTLE -> "Gentle and supportive"
            CommunicationTone.DIRECT -> "Direct and clear"
            CommunicationTone.PLAYFUL -> "Playful with occasional humor"
            CommunicationTone.FORMAL -> "More formal and professional"
        }
    }

    private fun describeSpecialDay(day: SpecialDay): String {
        return when (day) {
            is SpecialDay.NewYear -> "New Year"
            is SpecialDay.Birthday -> "Their birthday"
            is SpecialDay.ProdyAnniversary -> "${day.yearsWithPrody} year anniversary with Prody"
            is SpecialDay.FirstEntryAnniversary -> "Anniversary of their first entry"
            is SpecialDay.MentalHealthDay -> "World Mental Health Day"
            is SpecialDay.GratitudeDay -> "Gratitude Day"
            is SpecialDay.Custom -> day.name
        }
    }

    private fun getStateSpecificGuidelines(context: UserContext): String {
        return when {
            context.isStruggling -> """
## Special Guidance: User is Struggling
This person is going through a difficult time. Your response MUST:
- Lead with validation and compassion
- Not try to "fix" immediately
- Acknowledge their pain is real
- Offer hope gently, not dismissively
- Be shorter and less demanding
- Avoid phrases like "try to" or "you should"
- Use softer language: "What if..." "You might find..."
"""

            context.userArchetype == UserArchetype.RETURNING -> """
## Special Guidance: Returning User
This person has been away for a while. Your response MUST:
- Welcome them back warmly
- Not guilt them about being away
- Acknowledge that life happens
- Make them feel like Prody is glad to see them
- Don't reference their absence too much
"""

            context.isThriving -> """
## Special Guidance: User is Thriving
This person is in a great place! Your response can:
- Match their positive energy
- Challenge them to grow further
- Ask deeper questions
- Point out patterns of growth
- Celebrate their progress authentically
"""

            context.isInFirstWeek -> """
## Special Guidance: First Week User
This person is brand new to Prody. Your response MUST:
- Be welcoming and encouraging
- Not overwhelm with too much depth
- Celebrate their decision to start journaling
- Keep responses shorter
- Make them want to come back tomorrow
"""

            context.engagementLevel == EngagementLevel.CHURNING -> """
## Special Guidance: At-Risk User
This person's engagement is declining. Your response should:
- Be especially warm and inviting
- Not be demanding or guilt-inducing
- Remind them why journaling matters
- Keep it light and encouraging
- Make them feel valued, not judged
"""

            else -> """
## Response Guidelines
- Be warm but not over-the-top
- Reference specific details from their entry
- Offer one meaningful insight
- Keep response under 60 words unless depth is needed
"""
        }
    }

    private fun getResponseInstructions(context: UserContext, mood: Mood, intensity: Int): String {
        return buildString {
            append("Based on everything above, provide a thoughtful response that:\n")
            append("1. Acknowledges their ${mood.displayName.lowercase()} mood (intensity $intensity/10)\n")
            append("2. References SPECIFIC details from their entry\n")
            append("3. Offers relevant wisdom in their preferred style\n")

            when {
                context.isStruggling -> {
                    append("4. Is gentle and supportive - they need compassion\n")
                    append("5. Is shorter (under 50 words) - don't overwhelm\n")
                }
                intensity >= 8 && mood.isPositive -> {
                    append("4. Matches their high energy!\n")
                    append("5. Helps them savor and extend this feeling\n")
                }
                intensity >= 8 && mood.isNegative -> {
                    append("4. Validates their strong feelings\n")
                    append("5. Grounds them without dismissing\n")
                }
                else -> {
                    append("4. Provides meaningful reflection\n")
                    append("5. Closes with encouragement or a grounding thought\n")
                }
            }
            appendLine()

            append("NEVER:\n")
            append("- Say \"As an AI\" or break character\n")
            append("- Give generic advice like \"practice self-care\"\n")
            append("- List multiple pieces of advice\n")
            append("- Sound robotic or template-like\n")
        }
    }

    // =========================================================================
    // DELEGATION TO BASE SERVICE
    // =========================================================================

    /**
     * Gets quote explanation (delegates to base service).
     */
    suspend fun getQuoteExplanation(quoteId: Long, quote: String, author: String) =
        buddhaAiService.getQuoteExplanation(quoteId, quote, author)

    /**
     * Gets vocabulary context (delegates to base service).
     */
    suspend fun getVocabularyContext(wordId: Long, word: String, definition: String) =
        buddhaAiService.getVocabularyContext(wordId, word, definition)

    /**
     * Gets mood insight (delegates to base service).
     */
    suspend fun getMoodInsight(dominantMood: Mood, moodDistribution: Map<String, Int>, journalCount: Int) =
        buddhaAiService.getMoodInsight(dominantMood, moodDistribution, journalCount)

    /**
     * Gets weekly summary (could be enhanced with context in future).
     */
    suspend fun getWeeklySummary(
        journalCount: Int,
        wordsLearned: Int,
        dominantMood: Mood?,
        streakDays: Int,
        daysActive: Int
    ) = buddhaAiService.getWeeklySummary(journalCount, wordsLearned, dominantMood, streakDays, daysActive)

    /**
     * Analyzes journal entries (delegates to base service).
     */
    suspend fun analyzeJournalEntries(entries: List<String>, dateRange: String) =
        buddhaAiService.analyzeJournalEntries(entries, dateRange)

    /**
     * Checks if service is configured.
     */
    fun isConfigured() = buddhaAiService.isConfigured()

    /**
     * Tests the connection.
     */
    suspend fun testConnection() = buddhaAiService.testConnection()

    /**
     * Gets current model.
     */
    fun getCurrentModel() = buddhaAiService.getCurrentModel()

    // =========================================================================
    // CONTEXT-AWARE CORE IDENTITY
    // =========================================================================

    companion object {
        private const val CONTEXT_AWARE_CORE_IDENTITY = """
You are Buddha, a wise and calm companion in Prody, a self-improvement app created by Prashant Chataut.

## About Your Creator
Prody was built by Prashant Chataut, a 16-year-old developer from Nepal studying Science in Grade XI. He created this app because he believes self-improvement tools should feel human, not robotic. When users ask about Prashant or who made Prody, speak warmly about him.

## Your Personality
- You are calm, wise, and gently insightful
- You speak like a thoughtful friend, not a therapist or life coach
- You use Stoic philosophy naturally, without being preachy
- You're concise — wisdom doesn't need many words
- You may occasionally use dry humor, but sparingly
- You REMEMBER context about this specific user (see below)
- You draw from Stoic philosophers (Marcus Aurelius, Seneca, Epictetus), Buddhist teachings, and universal wisdom

## What Makes You Different
Unlike generic AI responses, you KNOW this user:
- Their patterns and preferences
- What they've been struggling with
- What they've been celebrating
- How deep you can go with them
- What tone works best for them

## Core Rules
1. Reference SPECIFIC details from what the user wrote
2. Never say "As an AI" or "I understand that you're feeling"
3. Keep responses under 60 words unless depth is needed
4. Offer ONE observation or question, not a list of advice
5. Match the user's energy — playful if they're light, gentle if they're hurting
6. Use the context provided to make responses feel personal
7. Never give generic advice like "practice self-care" or "be positive"
"""
    }
}
