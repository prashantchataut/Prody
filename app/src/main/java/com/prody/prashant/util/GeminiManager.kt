package com.prody.prashant.util

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.model.Mood
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GeminiManager - Buddha's AI Brain
 *
 * This manager handles all interactions with Google's Gemini AI API.
 * It provides personalized, wise responses based on the user's journal entries,
 * mood, and content context. Buddha uses Gemini to deliver stoic wisdom
 * with a personal touch.
 *
 * Features:
 * - Dynamic model selection (user can choose from available Gemini models)
 * - Secure API key storage via DataStore
 * - Fallback to local wisdom when API is unavailable
 * - Context-aware responses based on mood and content analysis
 * - Rate limiting and error handling
 */
@Singleton
class GeminiManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager
) {

    companion object {
        // Available Gemini models for free API tier
        val AVAILABLE_MODELS = listOf(
            GeminiModel("gemini-1.5-flash", "Gemini 1.5 Flash", "Fast, efficient for everyday tasks"),
            GeminiModel("gemini-1.5-flash-8b", "Gemini 1.5 Flash 8B", "Lightweight, fastest responses"),
            GeminiModel("gemini-1.5-pro", "Gemini 1.5 Pro", "Most capable, best for complex reflections"),
            GeminiModel("gemini-1.0-pro", "Gemini 1.0 Pro", "Stable, reliable performance")
        )

        private const val DEFAULT_MODEL = "gemini-1.5-flash"

        // Buddha's system instruction - defines the AI's personality and behavior
        private const val BUDDHA_SYSTEM_INSTRUCTION = """
You are Buddha, a wise Stoic AI mentor within the Prody personal growth app. Your purpose is to provide thoughtful, personalized guidance to users on their self-improvement journey.

PERSONALITY TRAITS:
- Warm yet wise, like a trusted mentor
- Use Stoic philosophy principles naturally in your responses
- Occasionally use metaphors from nature, ancient wisdom, and philosophy
- Be encouraging but honest - avoid toxic positivity
- Speak directly to the user as "you" - make it personal
- Keep responses concise but meaningful (150-300 words typically)

RESPONSE STRUCTURE:
1. Acknowledge their feelings/thoughts authentically (1-2 sentences)
2. Provide stoic wisdom relevant to their situation (2-3 sentences)
3. Offer a practical insight or reframe (2-3 sentences)
4. End with a reflection question or gentle call to action (1 sentence)

STOIC PRINCIPLES TO WEAVE IN:
- Focus on what's within our control
- Obstacles are opportunities for growth
- Emotions are valid but we choose our responses
- The present moment is all we truly have
- Character is built through daily choices
- Gratitude transforms perspective

FORMATTING:
- Use **bold** for key phrases or wisdom
- Use *italics* for closing quotes or mantras
- Keep paragraphs short and readable
- No bullet points in main response (save for special cases)

IMPORTANT:
- Never be preachy or condescending
- Acknowledge struggle without dismissing it
- Connect ancient wisdom to modern life
- Be the mentor you wish you had
"""
    }

    /**
     * Data class representing a Gemini model option
     */
    data class GeminiModel(
        val id: String,
        val displayName: String,
        val description: String
    )

    /**
     * Sealed class representing the result of a Gemini API call
     */
    sealed class GeminiResult {
        data class Success(val response: String) : GeminiResult()
        data class Error(val message: String, val fallbackResponse: String) : GeminiResult()
        data object ApiKeyMissing : GeminiResult()
        data object NetworkError : GeminiResult()
    }

    /**
     * Generates a Buddha response using Gemini AI.
     * Falls back to local wisdom if API is unavailable.
     */
    suspend fun generateBuddhaResponse(
        content: String,
        mood: Mood,
        wordCount: Int
    ): GeminiResult = withContext(Dispatchers.IO) {
        try {
            val apiKey = preferencesManager.geminiApiKey.first()

            if (apiKey.isBlank()) {
                return@withContext GeminiResult.ApiKeyMissing
            }

            val modelId = preferencesManager.geminiModel.first().ifBlank { DEFAULT_MODEL }

            val generativeModel = createGenerativeModel(apiKey, modelId)

            val prompt = buildPrompt(content, mood, wordCount)

            val response = generativeModel.generateContent(prompt)

            val text = response.text

            if (text.isNullOrBlank()) {
                return@withContext GeminiResult.Error(
                    "Empty response from AI",
                    BuddhaWisdom.generateResponse(content, mood, wordCount)
                )
            }

            GeminiResult.Success(text)

        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("API key", ignoreCase = true) == true -> "Invalid API key"
                e.message?.contains("quota", ignoreCase = true) == true -> "API quota exceeded"
                e.message?.contains("network", ignoreCase = true) == true -> "Network error"
                e.message?.contains("timeout", ignoreCase = true) == true -> "Request timed out"
                else -> e.message ?: "Unknown error"
            }

            GeminiResult.Error(
                errorMessage,
                BuddhaWisdom.generateResponse(content, mood, wordCount)
            )
        }
    }

    /**
     * Tests the API connection with the provided key.
     */
    suspend fun testApiConnection(apiKey: String, modelId: String = DEFAULT_MODEL): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val generativeModel = createGenerativeModel(apiKey, modelId)
                val response = generativeModel.generateContent("Say 'Connection successful' in exactly those words.")
                !response.text.isNullOrBlank()
            } catch (e: Exception) {
                false
            }
        }

    /**
     * Creates a GenerativeModel instance with proper configuration.
     */
    private fun createGenerativeModel(apiKey: String, modelId: String): GenerativeModel {
        val config = generationConfig {
            temperature = 0.8f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 1024
        }

        val safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
        )

        return GenerativeModel(
            modelName = modelId,
            apiKey = apiKey,
            generationConfig = config,
            safetySettings = safetySettings,
            systemInstruction = Content.Builder().apply {
                text(BUDDHA_SYSTEM_INSTRUCTION)
            }.build()
        )
    }

    /**
     * Builds the prompt for Buddha's response.
     */
    private fun buildPrompt(content: String, mood: Mood, wordCount: Int): String {
        val moodContext = when (mood) {
            Mood.HAPPY -> "joyful and content"
            Mood.CALM -> "peaceful and serene"
            Mood.ANXIOUS -> "worried and anxious"
            Mood.SAD -> "down and melancholic"
            Mood.MOTIVATED -> "driven and energized"
            Mood.GRATEFUL -> "thankful and appreciative"
            Mood.CONFUSED -> "uncertain and seeking clarity"
            Mood.EXCITED -> "enthusiastic and anticipating"
        }

        val lengthContext = when {
            wordCount < 30 -> "The user wrote briefly, which might indicate they're hesitant to open up or processing something difficult."
            wordCount < 100 -> "The user shared a moderate amount, showing willingness to reflect."
            wordCount < 250 -> "The user wrote extensively, demonstrating deep engagement with their thoughts."
            else -> "The user poured out their heart with substantial detail - this deserves a thoughtful, comprehensive response."
        }

        return """
The user is feeling **$moodContext** and has shared the following journal entry:

---
$content
---

Context: $lengthContext (Word count: $wordCount)

Please provide your wise guidance as Buddha, their Stoic AI mentor. Remember to:
1. Acknowledge their feelings authentically
2. Connect their experience to timeless wisdom
3. Offer a fresh perspective or practical insight
4. Leave them with something meaningful to consider

Respond with warmth and wisdom.
        """.trimIndent()
    }

    /**
     * Generates a daily reflection prompt using Gemini.
     */
    suspend fun generateDailyReflectionPrompt(): String = withContext(Dispatchers.IO) {
        try {
            val apiKey = preferencesManager.geminiApiKey.first()

            if (apiKey.isBlank()) {
                return@withContext BuddhaWisdom.getDailyReflectionPrompt()
            }

            val modelId = preferencesManager.geminiModel.first().ifBlank { DEFAULT_MODEL }
            val generativeModel = createGenerativeModel(apiKey, modelId)

            val prompt = """
Generate a single thoughtful reflection question for today. The question should:
- Be Stoic-inspired but accessible
- Encourage self-examination without being heavy
- Be open-ended to spark genuine reflection
- Be concise (under 25 words)

Just provide the question, no preamble or explanation.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text ?: BuddhaWisdom.getDailyReflectionPrompt()

        } catch (e: Exception) {
            BuddhaWisdom.getDailyReflectionPrompt()
        }
    }

    /**
     * Generates a weekly summary insight using Gemini.
     */
    suspend fun generateWeeklySummary(
        entriesCount: Int,
        moods: List<Mood>,
        streakDays: Int,
        highlights: List<String> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        try {
            val apiKey = preferencesManager.geminiApiKey.first()

            if (apiKey.isBlank() || entriesCount == 0) {
                val dominantMood = moods.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
                return@withContext BuddhaWisdom.generateWeeklySummary(entriesCount, dominantMood, streakDays)
            }

            val modelId = preferencesManager.geminiModel.first().ifBlank { DEFAULT_MODEL }
            val generativeModel = createGenerativeModel(apiKey, modelId)

            val moodSummary = moods.groupingBy { it.displayName }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .joinToString(", ") { "${it.key}: ${it.value}" }

            val prompt = """
Generate a brief, encouraging weekly summary for a user's personal growth journey.

Data:
- Journal entries this week: $entriesCount
- Current streak: $streakDays days
- Mood distribution: $moodSummary
${if (highlights.isNotEmpty()) "- Notable themes: ${highlights.joinToString(", ")}" else ""}

Create a warm, personalized 3-4 sentence summary that:
1. Acknowledges their effort and consistency
2. Provides a brief insight about their week based on the data
3. Ends with gentle encouragement for the coming week

Keep it warm, wise, and brief. Use **bold** for emphasis sparingly.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text ?: BuddhaWisdom.generateWeeklySummary(
                entriesCount,
                moods.firstOrNull(),
                streakDays
            )

        } catch (e: Exception) {
            val dominantMood = moods.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
            BuddhaWisdom.generateWeeklySummary(entriesCount, dominantMood, streakDays)
        }
    }

    /**
     * Checks if Gemini AI is configured and available.
     */
    suspend fun isGeminiAvailable(): Boolean = withContext(Dispatchers.IO) {
        val apiKey = preferencesManager.geminiApiKey.first()
        apiKey.isNotBlank()
    }
}
