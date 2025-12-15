package com.prody.prashant.data.ai

import com.prody.prashant.BuildConfig
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

// --- Service ---

@Singleton
class OpenRouterService @Inject constructor() {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.OPENROUTER_API_KEY}")
                    .addHeader("HTTP-Referer", "https://prody.app") // Replace with actual site if available
                    .addHeader("X-Title", "Prody")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
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

    suspend fun generateResponse(
        prompt: String,
        model: String = "openai/gpt-3.5-turbo" // Default model, can be changed
    ): Result<String> {
        return try {
            if (BuildConfig.OPENROUTER_API_KEY.isBlank()) {
                return Result.failure(IllegalStateException("OpenRouter API key not set"))
            }

            val messages = listOf(
                OpenRouterMessage(role = "user", content = prompt)
            )
            val request = OpenRouterRequest(model = model, messages = messages)
            val response = api.chatCompletions(request)
            
            val content = response.choices.firstOrNull()?.message?.content
            if (content != null) {
                Result.success(content)
            } else {
                Result.failure(IOException("Empty response from OpenRouter"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testConnection(): Result<String> {
        return generateResponse("Say 'Connection successful'", model = "google/gemini-pro") // Using a cheap/free model for test if possible
    }
}
