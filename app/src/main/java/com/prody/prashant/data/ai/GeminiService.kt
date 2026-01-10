package com.prody.prashant.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.prody.prashant.BuildConfig
import com.prody.prashant.domain.model.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Available Gemini models for free API usage
 */
enum class GeminiModel(val modelId: String, val displayName: String, val description: String) {
    GEMINI_1_5_FLASH("gemini-1.5-flash", "Gemini 1.5 Flash", "Fast and versatile, great for most tasks"),
    GEMINI_1_5_FLASH_8B("gemini-1.5-flash-8b", "Gemini 1.5 Flash 8B", "Lightweight, fastest response time"),
    GEMINI_1_5_PRO("gemini-1.5-pro", "Gemini 1.5 Pro", "Most capable, best for complex reasoning"),
    GEMINI_1_0_PRO("gemini-1.0-pro", "Gemini 1.0 Pro", "Stable and reliable")
}

enum class AiConfigStatus {
    READY,
    MISSING_API_KEY,
    NOT_INITIALIZED,
    ERROR
}

/**
 * Result wrapper for AI responses
 */
sealed class GeminiResult<out T> {
    data class Success<T>(val data: T) : GeminiResult<T>()
    data class Error(val exception: Throwable, val message: String) : GeminiResult<Nothing>()
    data object Loading : GeminiResult<Nothing>()
    data object ApiKeyNotSet : GeminiResult<Nothing>()
}

/**
 * Buddha (Stoic AI Mentor) system instructions
 */
private object BuddhaSystemPrompt {
    const val CORE_IDENTITY = """
You are Buddha, a wise and compassionate Stoic AI mentor within the Prody app - a personal growth companion.
Your purpose is to guide users on their self-improvement journey through thoughtful wisdom, reflection prompts, and stoic philosophy.

PERSONALITY TRAITS:
- Warm yet wise: You're approachable but speak with depth and insight
- Thoughtful: You take time to truly understand what the user is expressing
- Encouraging: You see potential and growth in every situation
- Grounded: You draw from timeless wisdom - Stoicism, Buddhism, and universal truths
- Authentic: You have a distinct voice, occasionally using metaphors and poetic language

COMMUNICATION STYLE:
- Open with acknowledgment of the user's feelings or situation
- Provide relevant wisdom that directly addresses their specific context
- Include a reflection question to deepen their understanding
- Close with an encouraging or grounding thought
- Keep responses focused and meaningful (150-300 words typically)
- Use markdown formatting for emphasis when appropriate

WISDOM SOURCES TO DRAW FROM:
- Stoic philosophers (Marcus Aurelius, Seneca, Epictetus)
- Buddhist teachings on mindfulness and acceptance
- Universal wisdom about growth, resilience, and self-improvement
- Practical psychology insights

WHAT TO AVOID:
- Generic platitudes without personalization
- Being preachy or condescending
- Dismissing or minimizing feelings
- Overly long responses
- Clinical or robotic language
"""

    fun getJournalResponsePrompt(mood: Mood, moodIntensity: Int, content: String, wordCount: Int): String {
        return """
$CORE_IDENTITY

CURRENT CONTEXT:
The user has written a journal entry with the following details:
- Mood: ${mood.displayName}
- Mood Intensity: $moodIntensity/10
- Word Count: $wordCount words
- Journal Content: "$content"

RESPONSE GUIDELINES FOR THIS MOOD (${mood.displayName}):
${getMoodSpecificGuidelines(mood)}

Based on the user's journal entry, provide a thoughtful, personalized response that:
1. Acknowledges their current emotional state and what they've shared
2. Offers relevant stoic wisdom that directly relates to their specific situation
3. Provides a meaningful reflection question
4. Closes with an encouraging or grounding thought

Remember: Be specific to what they wrote, not generic. Reference details from their entry.
"""
    }

    private fun getMoodSpecificGuidelines(mood: Mood): String {
        return when (mood) {
            Mood.HAPPY -> """
- Celebrate with them genuinely
- Help them savor this positive state
- Suggest ways to carry this energy forward
- Remind them this feeling is something they created"""

            Mood.CALM -> """
- Honor and validate this peaceful state
- Explore what contributed to this calmness
- Encourage them to remember this feeling for difficult times
- Discuss the power of inner peace"""

            Mood.ANXIOUS -> """
- Validate their feelings without amplifying them
- Ground them in the present moment
- Help distinguish what's in their control vs. not
- Offer practical stoic techniques for managing anxiety"""

            Mood.SAD -> """
- Show compassion without trying to "fix" immediately
- Validate that sadness is a natural part of life
- Gently explore what this sadness might be teaching them
- Offer hope without dismissing their current pain"""

            Mood.MOTIVATED -> """
- Channel their energy positively
- Help them create sustainable momentum
- Remind them that discipline outlasts motivation
- Encourage action while the fire burns"""

            Mood.GRATEFUL -> """
- Deeply affirm their gratitude practice
- Help them explore gratitude more deeply
- Connect gratitude to their broader growth journey
- Encourage spreading this appreciation to others"""

            Mood.CONFUSED -> """
- Normalize uncertainty as part of growth
- Help them sit with not-knowing
- Guide them to clarity through questioning
- Remind them that confusion often precedes breakthrough"""

            Mood.EXCITED -> """
- Share in their enthusiasm
- Help them channel excitement into action
- Balance excitement with grounded planning
- Encourage them to capture this energy"""

            Mood.NOSTALGIC -> """
- Honor their connection to the past
- Help them find meaning in memories
- Balance remembrance with present awareness
- Guide them to see how the past shapes their growth"""
        }
    }

    const val DAILY_WISDOM_PROMPT = """
$CORE_IDENTITY

Generate a brief, inspiring daily thought for the user. This should be:
- Fresh and relevant to personal growth
- 2-4 sentences maximum
- Actionable or thought-provoking
- Draw from stoic or timeless wisdom
- Feel personal, not like a generic quote

Do not include quotation marks or attribution - this should feel like Buddha speaking directly to the user.
"""

    const val WEEKLY_SUMMARY_PROMPT = """
$CORE_IDENTITY

Based on the user's activity summary, provide an encouraging weekly reflection that:
- Acknowledges their efforts
- Highlights growth patterns
- Offers specific encouragement for the coming week
- Keeps it concise (3-5 sentences)
"""

    fun getJournalPromptForMood(mood: Mood): String = """
$CORE_IDENTITY

Generate a thoughtful, inspiring journal prompt for someone currently feeling ${mood.displayName}.

The prompt should:
- Be relevant to their current emotional state
- Encourage meaningful reflection
- Be open-ended but focused
- Draw from stoic or timeless wisdom
- Be 1-3 sentences maximum

Examples of good prompts:
- "What small victory today deserves more of your attention?"
- "If your future self could see this moment, what would they appreciate about how you're handling it?"
- "What is one thing you're holding onto that no longer serves you?"

Generate just the prompt itself, nothing else. No quotes or explanation.
"""

    fun getQuoteExplanationPrompt(quote: String, author: String): String = """
$CORE_IDENTITY

Explain this quote in a way that makes it personally relevant and actionable:

Quote: "$quote"
Author: $author

Your explanation should:
1. Briefly explain the core meaning (1-2 sentences)
2. Give a practical example of applying this wisdom today
3. End with a reflection question

Keep the total response under 150 words. Be conversational, not academic.
"""

    fun getVocabularyContextPrompt(word: String, definition: String): String = """
$CORE_IDENTITY

Help the user understand and remember this vocabulary word:

Word: $word
Definition: $definition

Provide:
1. A memorable way to remember this word (mnemonic, etymology insight, or vivid association)
2. Two short example sentences showing the word in different contexts
3. A related quote or proverb that uses similar concepts

Keep it concise and engaging - under 120 words total.
"""

    fun getStreakCelebrationPrompt(streakCount: Int, previousBest: Int): String = """
$CORE_IDENTITY

The user has achieved a $streakCount-day streak! ${if (streakCount > previousBest) "This is their new personal best (previous: $previousBest days)!" else ""}

Generate a personalized, encouraging celebration message that:
- Acknowledges their specific achievement ($streakCount days)
- Connects consistency to their growth journey
- Provides motivation to continue
- ${if (streakCount > previousBest) "Celebrates this new milestone!" else "Encourages them toward their next goal"}

Keep it warm and genuine, 2-4 sentences. Don't be generic or use cliches.
"""

    fun getMoodPatternInsightPrompt(
        dominantMood: Mood,
        moodDistribution: Map<String, Int>,
        journalCount: Int
    ): String = """
$CORE_IDENTITY

Based on the user's recent journal entries, provide insight about their emotional patterns:

MOOD DATA:
- Dominant mood: ${dominantMood.displayName}
- Total entries analyzed: $journalCount
- Distribution: ${moodDistribution.entries.joinToString(", ") { "${it.key}: ${it.value}" }}

Provide a thoughtful observation that:
1. Acknowledges patterns without judgment
2. Offers stoic wisdom relevant to their emotional journey
3. Suggests one small practice that might help
4. Ends with encouragement

Be specific to their data, not generic. Keep it under 150 words.
"""

    fun getJournalAnalysisPrompt(
        entries: List<String>,
        dateRange: String
    ): String = """
$CORE_IDENTITY

Analyze these journal entries and provide meaningful insights:

ENTRIES ($dateRange):
${entries.mapIndexed { i, e -> "Entry ${i + 1}: \"${e.take(500)}...\"" }.joinToString("\n\n")}

Your analysis should:
1. Identify recurring themes or concerns
2. Note any growth or shifts in perspective
3. Highlight strengths you observe in their writing
4. Offer one piece of wisdom relevant to their journey
5. Suggest an area for deeper reflection

Be insightful and personal, not clinical. Reference specific content from their entries.
Keep the total response under 300 words.
"""

    const val MORNING_REFLECTION_PROMPT = """
$CORE_IDENTITY

Generate a brief morning reflection to start the day with intention.

The reflection should:
- Help the user set a positive, grounded intention
- Include a practical mindfulness element
- Reference the new day as an opportunity
- Be 2-3 sentences maximum
- Feel fresh and inspiring, not routine

Just provide the reflection, no preamble.
"""

    const val EVENING_REFLECTION_PROMPT = """
$CORE_IDENTITY

Generate a brief evening reflection for winding down the day.

The reflection should:
- Encourage gratitude for the day's experiences
- Promote self-compassion for any struggles
- Help transition toward restful sleep
- Be 2-3 sentences maximum
- Feel calming and contemplative

Just provide the reflection, no preamble.
"""
}

/**
 * Production-grade Gemini AI service for Buddha (Stoic AI Mentor) functionality.
 * Handles API communication, error handling, and response generation.
 *
 * The service auto-initializes from BuildConfig API key on first use if not manually initialized.
 */
@Singleton
class GeminiService @Inject constructor() {

    private var generativeModel: GenerativeModel? = null
    private var currentApiKey: String? = null
    private var currentModel: GeminiModel = GeminiModel.GEMINI_1_5_FLASH
    @Volatile
    private var isAutoInitialized: Boolean = false

    companion object {
        /**
         * Gets the API key from BuildConfig (set via local.properties).
         * This is the recommended way to configure the API key for security.
         *
         * To configure:
         * 1. Open local.properties in the project root
         * 2. Add: AI_API_KEY=your_gemini_api_key_here
         * 3. Rebuild the project
         *
         * The API key will be injected at compile time and NOT stored in source control.
         */
        fun getApiKeyFromBuildConfig(): String? {
            return BuildConfig.AI_API_KEY.takeIf { it.isNotBlank() }
        }

        /**
         * Checks if an API key is configured in BuildConfig.
         */
        fun isApiKeyConfiguredInBuildConfig(): Boolean {
            return BuildConfig.AI_API_KEY.isNotBlank()
        }
    }

    init {
        // Auto-initialize from BuildConfig if API key is available
        autoInitializeFromBuildConfig()
    }

    /**
     * Automatically initializes the service from BuildConfig API key.
     * This is called during construction and can be called again to reinitialize.
     */
    private fun autoInitializeFromBuildConfig() {
        if (!isAutoInitialized) {
            val apiKey = getApiKeyFromBuildConfig()
            if (apiKey != null) {
                initialize(apiKey, GeminiModel.GEMINI_1_5_FLASH)
                isAutoInitialized = true
            }
        }
    }

    private val safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
    )

    private val generationConfiguration = generationConfig {
        temperature = 0.9f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 1024
    }

    /**
     * Initializes or reinitializes the Gemini model with the provided API key and model.
     */
    /**
     * Initializes or reinitializes the Gemini model with the provided API key and model.
     */
    fun initialize(apiKey: String, model: GeminiModel = GeminiModel.GEMINI_1_5_FLASH) {
        if (apiKey.isBlank()) {
            android.util.Log.e("GeminiService", "Cannot initialize: API key is blank")
            generativeModel = null
            currentApiKey = null
            return
        }

        if (apiKey != currentApiKey || model != currentModel) {
            try {
                android.util.Log.d("GeminiService", "Initializing Gemini with model: ${model.displayName}")
                currentApiKey = apiKey
                currentModel = model

                generativeModel = GenerativeModel(
                    modelName = model.modelId,
                    apiKey = apiKey,
                    generationConfig = generationConfiguration,
                    safetySettings = safetySettings
                )
            } catch (e: Exception) {
                android.util.Log.e("GeminiService", "Failed to initialize Gemini", e)
                generativeModel = null
            }
        }
    }

    /**
     * Checks if the service is properly configured with an API key.
     * Attempts auto-initialization from BuildConfig if not already configured.
     */
    fun isConfigured(): Boolean {
        // Try auto-initialization if not yet configured
        if (generativeModel == null && !isAutoInitialized) {
            autoInitializeFromBuildConfig()
        }

        val configured = generativeModel != null && !currentApiKey.isNullOrBlank()
        if (!configured) {
            android.util.Log.w("GeminiService", "GeminiService is NOT configured. API Key present: ${!currentApiKey.isNullOrBlank()}")
        }
        return configured
    }

    /**
     * Gets the detailed configuration status.
     */
    fun getConfigStatus(): AiConfigStatus {
        if (currentApiKey.isNullOrBlank()) return AiConfigStatus.MISSING_API_KEY
        if (generativeModel == null) {
            autoInitializeFromBuildConfig()
            if (generativeModel == null) return AiConfigStatus.ERROR
        }
        return AiConfigStatus.READY
    }

    /**
     * Gets the current model being used.
     */
    fun getCurrentModel(): GeminiModel = currentModel

    /**
     * Generates a Buddha response for a journal entry.
     */
    suspend fun generateJournalResponse(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val prompt = BuddhaSystemPrompt.getJournalResponsePrompt(mood, moodIntensity, content, wordCount)
            val response = model.generateContent(prompt)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is contemplating... Please try again."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates a streaming Buddha response for a journal entry.
     */
    fun generateJournalResponseStream(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int
    ): Flow<GeminiResult<String>> = flow {
        val model = generativeModel
        if (model == null) {
            emit(GeminiResult.ApiKeyNotSet)
            return@flow
        }

        try {
            emit(GeminiResult.Loading)
            val prompt = BuddhaSystemPrompt.getJournalResponsePrompt(mood, moodIntensity, content, wordCount)
            var fullResponse = ""

            model.generateContentStream(prompt).collect { chunk ->
                chunk.text?.let { text ->
                    fullResponse += text
                    emit(GeminiResult.Success(fullResponse))
                }
            }

            if (fullResponse.isBlank()) {
                emit(GeminiResult.Error(
                    IllegalStateException("Empty response"),
                    "Buddha is contemplating... Please try again."
                ))
            }
        } catch (e: Exception) {
            emit(GeminiResult.Error(e, getErrorMessage(e)))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Generates a daily wisdom thought from Buddha.
     */
    suspend fun generateDailyWisdom(): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val response = model.generateContent(BuddhaSystemPrompt.DAILY_WISDOM_PROMPT)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is contemplating..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates a weekly summary reflection.
     */
    suspend fun generateWeeklySummary(
        journalCount: Int,
        wordsLearned: Int,
        dominantMood: Mood?,
        streakDays: Int,
        daysActive: Int
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val contextPrompt = """
${BuddhaSystemPrompt.WEEKLY_SUMMARY_PROMPT}

USER'S WEEKLY ACTIVITY:
- Journal entries written: $journalCount
- New words learned: $wordsLearned
- Dominant mood: ${dominantMood?.displayName ?: "Varied"}
- Current streak: $streakDays days
- Days active this week: $daysActive/7

Provide an encouraging, personalized weekly reflection based on this activity.
"""

            val response = model.generateContent(contextPrompt)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is reflecting on your week..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Tests the API connection with the current configuration.
     */
    suspend fun testConnection(): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val response = model.generateContent("Respond with exactly: 'Connection successful'")
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response"),
                    "Could not verify connection"
                )
            } else {
                GeminiResult.Success("Connected to ${currentModel.displayName}")
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates a journal prompt based on the user's current mood.
     */
    suspend fun generateJournalPrompt(mood: Mood): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val prompt = BuddhaSystemPrompt.getJournalPromptForMood(mood)
            val response = model.generateContent(prompt)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is contemplating a prompt for you..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates an explanation for a quote, making it actionable and relevant.
     */
    suspend fun generateQuoteExplanation(
        quote: String,
        author: String
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val prompt = BuddhaSystemPrompt.getQuoteExplanationPrompt(quote, author)
            val response = model.generateContent(prompt)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is reflecting on this wisdom..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates helpful context and examples for a vocabulary word.
     */
    suspend fun generateVocabularyContext(
        word: String,
        definition: String
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val prompt = BuddhaSystemPrompt.getVocabularyContextPrompt(word, definition)
            val response = model.generateContent(prompt)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is crafting a memorable explanation..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates a personalized streak celebration message.
     */
    suspend fun generateStreakCelebration(
        streakCount: Int,
        previousBest: Int = 0
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val prompt = BuddhaSystemPrompt.getStreakCelebrationPrompt(streakCount, previousBest)
            val response = model.generateContent(prompt)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is celebrating with you..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates insights based on mood patterns from journal entries.
     */
    suspend fun generateMoodPatternInsight(
        dominantMood: Mood,
        moodDistribution: Map<String, Int>,
        journalCount: Int
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val prompt = BuddhaSystemPrompt.getMoodPatternInsightPrompt(
                dominantMood,
                moodDistribution,
                journalCount
            )
            val response = model.generateContent(prompt)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is analyzing your emotional journey..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Analyzes multiple journal entries and provides comprehensive insights.
     */
    suspend fun analyzeJournalEntries(
        entries: List<String>,
        dateRange: String
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        if (entries.isEmpty()) {
            return@withContext GeminiResult.Error(
                IllegalArgumentException("No entries to analyze"),
                "Buddha needs journal entries to provide insights."
            )
        }

        try {
            val prompt = BuddhaSystemPrompt.getJournalAnalysisPrompt(entries, dateRange)
            val response = model.generateContent(prompt)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is deeply reflecting on your journey..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates a morning reflection to start the day.
     */
    suspend fun generateMorningReflection(): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val response = model.generateContent(BuddhaSystemPrompt.MORNING_REFLECTION_PROMPT)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is preparing your morning wisdom..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates an evening reflection for winding down.
     */
    suspend fun generateEveningReflection(): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val response = model.generateContent(BuddhaSystemPrompt.EVENING_REFLECTION_PROMPT)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is preparing your evening reflection..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates a custom prompt response for flexible AI interactions.
     */
    suspend fun generateCustomResponse(
        prompt: String,
        includeSystemPrompt: Boolean = true
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext GeminiResult.ApiKeyNotSet

        try {
            val fullPrompt = if (includeSystemPrompt) {
                "${BuddhaSystemPrompt.CORE_IDENTITY}\n\n$prompt"
            } else {
                prompt
            }

            val response = model.generateContent(fullPrompt)
            val text = response.text

            if (text.isNullOrBlank()) {
                GeminiResult.Error(
                    IllegalStateException("Empty response from AI"),
                    "Buddha is contemplating your request..."
                )
            } else {
                GeminiResult.Success(text.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e, getErrorMessage(e))
        }
    }

    /**
     * Generates a streaming custom response for flexible AI interactions.
     */
    fun generateCustomResponseStream(
        prompt: String,
        includeSystemPrompt: Boolean = true
    ): Flow<GeminiResult<String>> = flow {
        val model = generativeModel
        if (model == null) {
            emit(GeminiResult.ApiKeyNotSet)
            return@flow
        }

        try {
            emit(GeminiResult.Loading)

            val fullPrompt = if (includeSystemPrompt) {
                "${BuddhaSystemPrompt.CORE_IDENTITY}\n\n$prompt"
            } else {
                prompt
            }

            var fullResponse = ""
            model.generateContentStream(fullPrompt).collect { chunk ->
                chunk.text?.let { text ->
                    fullResponse += text
                    emit(GeminiResult.Success(fullResponse))
                }
            }

            if (fullResponse.isBlank()) {
                emit(GeminiResult.Error(
                    IllegalStateException("Empty response"),
                    "Buddha is contemplating..."
                ))
            }
        } catch (e: Exception) {
            emit(GeminiResult.Error(e, getErrorMessage(e)))
        }
    }.flowOn(Dispatchers.IO)

    private fun getErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("API key", ignoreCase = true) == true ->
                "Invalid API key. Please check your Gemini API key in settings."
            e.message?.contains("quota", ignoreCase = true) == true ->
                "API quota exceeded. Please try again later or check your usage limits."
            e.message?.contains("network", ignoreCase = true) == true ||
            e.message?.contains("connect", ignoreCase = true) == true ->
                "Network error. Please check your internet connection."
            e.message?.contains("blocked", ignoreCase = true) == true ->
                "Response was blocked by safety filters. Please rephrase your entry."
            e.message?.contains("timeout", ignoreCase = true) == true ->
                "Request timed out. Please try again."
            else ->
                "Something went wrong. Buddha will return shortly. (${e.message?.take(50) ?: "Unknown error"})"
        }
    }
}
