package com.prody.prashant.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
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
}

/**
 * Production-grade Gemini AI service for Buddha (Stoic AI Mentor) functionality.
 * Handles API communication, error handling, and response generation.
 */
@Singleton
class GeminiService @Inject constructor() {

    private var generativeModel: GenerativeModel? = null
    private var currentApiKey: String? = null
    private var currentModel: GeminiModel = GeminiModel.GEMINI_1_5_FLASH

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
    fun initialize(apiKey: String, model: GeminiModel = GeminiModel.GEMINI_1_5_FLASH) {
        if (apiKey.isBlank()) {
            generativeModel = null
            currentApiKey = null
            return
        }

        if (apiKey != currentApiKey || model != currentModel) {
            currentApiKey = apiKey
            currentModel = model

            generativeModel = GenerativeModel(
                modelName = model.modelId,
                apiKey = apiKey,
                generationConfig = generationConfiguration,
                safetySettings = safetySettings
            )
        }
    }

    /**
     * Checks if the service is properly configured with an API key.
     */
    fun isConfigured(): Boolean = generativeModel != null && !currentApiKey.isNullOrBlank()

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
