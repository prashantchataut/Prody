package com.kairos.app.data.ai

import com.kairos.app.BuildConfig
import com.kairos.app.domain.model.Mood
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// --- Models ---

@Serializable
data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val temperature: Double = 0.7,
    @SerialName("max_tokens") val maxTokens: Int = 1000
)

@Serializable
data class OpenRouterMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenRouterResponse(
    val id: String,
    val choices: List<OpenRouterChoice>,
    val created: Long,
    val model: String
)

@Serializable
data class OpenRouterChoice(
    val message: OpenRouterMessage,
    @SerialName("finish_reason") val finishReason: String?
)

// --- API Interface ---

interface OpenRouterApi {
    @Headers("Content-Type: application/json")
    @POST("api/v1/chat/completions")
    suspend fun chatCompletions(@Body request: OpenRouterRequest): OpenRouterResponse
}

// --- Kairos Guide prompt for the legacy OpenRouter fallback ---

private object OpenRouterGuidePrompt {
    const val SYSTEM_PROMPT = """You are Kairos Guide, an optional reflection assistant inside a learning and reflection app.

Respond with a calm editorial voice. Use only details the user supplied. Give one concrete observation, one relevant connection, and at most one question. Avoid motivational clichés, diagnosis, therapy claims, fabricated memories, and dependency-forming language. If the user appears to be in immediate danger or considering self-harm, prioritize urgent real-world support instead of ordinary reflection guidance. Keep most responses between 40 and 90 words."""

    fun getJournalPrompt(content: String, mood: Mood, moodIntensity: Int, wordCount: Int): String {
        val moodGuideline = when (mood) {
            Mood.HAPPY -> "Celebrate with them genuinely and help them savor this positive state."
            Mood.CALM -> "Honor their peaceful state and explore what contributed to this calmness."
            Mood.ANXIOUS -> "Validate feelings without amplifying, ground them in the present moment."
            Mood.SAD -> "Show compassion without trying to 'fix' immediately, validate that sadness is natural."
            Mood.MOTIVATED -> "Channel their energy positively and remind them that discipline outlasts motivation."
            Mood.GRATEFUL -> "Deeply affirm their gratitude practice and help them explore it more deeply."
            Mood.CONFUSED -> "Normalize uncertainty as part of growth and guide them to clarity through questioning."
            Mood.EXCITED -> "Share in their enthusiasm and help them channel excitement into action."
            Mood.NOSTALGIC -> "Honor their connection to the past and help them find meaning in memories."
        }

        return """$SYSTEM_PROMPT

The user has written a journal entry:
- Mood: ${mood.displayName} (Intensity: $moodIntensity/10)
- Word Count: $wordCount words
- Content: "$content"

GUIDANCE FOR ${mood.displayName.uppercase()} MOOD:
$moodGuideline

Respond with a thoughtful, personalized message that:
1. Acknowledges their emotional state and what they've shared
2. Offers relevant wisdom that directly relates to their specific situation
3. Provides a meaningful reflection question
4. Closes with an encouraging thought

Be specific to what they wrote - reference details from their entry."""
    }
}

// --- Service ---

/**
 * OpenRouter API service for backup AI provider.
 * Used when Gemini is unavailable or rate-limited.
 * Supports multiple AI models through OpenRouter's unified API.
 */
@Singleton
class OpenRouterService @Inject constructor() {

    companion object {
        // Cost-effective models that work well for Buddha responses
        const val MODEL_GPT_35_TURBO = "openai/gpt-3.5-turbo"
        const val MODEL_CLAUDE_INSTANT = "anthropic/claude-instant-1"
        const val MODEL_MISTRAL_7B = "mistralai/mistral-7b-instruct"
        const val MODEL_GEMINI_PRO = "google/gemini-pro"

        /**
         * Checks if an API key is configured in BuildConfig.
         */
        fun isApiKeyConfiguredInBuildConfig(): Boolean {
            return BuildConfig.OPENROUTER_API_KEY.isNotBlank()
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.OPENROUTER_API_KEY}")
                    .addHeader("HTTP-Referer", "https://Kairos.app")
                    .addHeader("X-Title", "Kairos - Learn and Reflect")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
                // Security: Redact Authorization header to prevent API key leak in logs
                redactHeader("Authorization")
            })
            .build()
    }

    private val api: OpenRouterApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://openrouter.ai/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OpenRouterApi::class.java)
    }

    /**
     * Checks if the service is properly configured with an API key.
     */
    fun isConfigured(): Boolean {
        return BuildConfig.OPENROUTER_API_KEY.isNotBlank()
    }

    /**
     * Generates a Buddha response for a journal entry.
     * This mirrors the GeminiService.generateJournalResponse signature for easy fallback use.
     */
    suspend fun generateJournalResponse(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int,
        model: String = MODEL_GPT_35_TURBO
    ): Result<String> {
        if (!isConfigured()) {
            return Result.failure(IllegalStateException("OpenRouter API key not configured"))
        }

        return try {
            val prompt = OpenRouterGuidePrompt.getJournalPrompt(content, mood, moodIntensity, wordCount)

            val messages = listOf(
                OpenRouterMessage(role = "system", content = OpenRouterGuidePrompt.SYSTEM_PROMPT),
                OpenRouterMessage(role = "user", content = prompt)
            )

            val request = OpenRouterRequest(
                model = model,
                messages = messages,
                temperature = 0.7,
                maxTokens = 800
            )

            val response = api.chatCompletions(request)
            val responseContent = response.choices.firstOrNull()?.message?.content

            if (responseContent != null && responseContent.isNotBlank()) {
                Result.success(responseContent.trim())
            } else {
                Result.failure(IOException("Empty response from OpenRouter"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates a generic response with custom system and user prompts.
     */
    suspend fun generateResponse(
        prompt: String,
        systemPrompt: String? = null,
        model: String = MODEL_GPT_35_TURBO
    ): Result<String> {
        if (!isConfigured()) {
            return Result.failure(IllegalStateException("OpenRouter API key not configured"))
        }

        return try {
            val messages = buildList {
                if (systemPrompt != null) {
                    add(OpenRouterMessage(role = "system", content = systemPrompt))
                }
                add(OpenRouterMessage(role = "user", content = prompt))
            }

            val request = OpenRouterRequest(
                model = model,
                messages = messages,
                temperature = 0.7,
                maxTokens = 1000
            )

            val response = api.chatCompletions(request)
            val content = response.choices.firstOrNull()?.message?.content

            if (content != null && content.isNotBlank()) {
                Result.success(content.trim())
            } else {
                Result.failure(IOException("Empty response from OpenRouter"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Tests the API connection with a minimal request.
     */
    suspend fun testConnection(): Result<String> {
        return generateResponse(
            prompt = "Say 'Connection successful' in exactly those two words.",
            model = MODEL_MISTRAL_7B // Using a cheap model for testing
        )
    }

    /**
     * Gets a descriptive error message for the given exception.
     */
    fun getErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("API key", ignoreCase = true) == true ->
                "Invalid OpenRouter API key. Please check your configuration."
            e.message?.contains("quota", ignoreCase = true) == true ||
            e.message?.contains("rate", ignoreCase = true) == true ->
                "API rate limit reached. Please try again in a moment."
            e.message?.contains("network", ignoreCase = true) == true ||
            e.message?.contains("connect", ignoreCase = true) == true ||
            e.message?.contains("timeout", ignoreCase = true) == true ->
                "Network error. Please check your internet connection."
            e.message?.contains("model", ignoreCase = true) == true ->
                "Selected AI model is unavailable. Trying alternative..."
            else ->
                "AI service temporarily unavailable. (${e.message?.take(40) ?: "Unknown error"})"
        }
    }
}
