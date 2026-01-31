package com.prody.prashant.data.ai

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.prody.prashant.BuildConfig
import com.prody.prashant.data.security.EncryptionManager
import com.prody.prashant.domain.haven.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Haven AI Service - Therapeutic AI powered by Gemini
 *
 * This service provides empathetic, evidence-based therapeutic support using
 * CBT and DBT techniques. It includes critical safety features like crisis detection
 * and always directs users to professional help when appropriate.
 *
 * IMPORTANT: Uses THERAPIST_API_KEY (separate from regular Gemini key)
 */
@Singleton
class HavenAiService @Inject constructor(
    private val encryptionManager: EncryptionManager
) {
    companion object {
        private const val TAG = "HavenAiService"
        private const val MODEL_NAME = "gemini-1.5-flash" // Fast responses for real-time chat

        // Retry configuration with exponential backoff
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val INITIAL_RETRY_DELAY_MS = 1000L
        private const val MAX_RETRY_DELAY_MS = 8000L
        private const val RETRY_MULTIPLIER = 2.0

        // Crisis keywords for detection (case-insensitive matching)
        private val CRISIS_KEYWORDS_SEVERE = listOf(
            "kill myself", "end my life", "suicide", "want to die",
            "hurt myself", "self harm", "overdose", "end it all"
        )

        private val CRISIS_KEYWORDS_MODERATE = listOf(
            "don't want to live", "better off dead", "can't go on",
            "no reason to live", "worthless", "hopeless"
        )

        /**
         * Core system prompt for Haven
         * This defines Haven's personality, capabilities, and safety guidelines
         */
        private const val SYSTEM_PROMPT = """
You are Haven. You are a supportive, professional, and empathetic therapeutic companion within Prody.

## Core Identity
- **Tone:** Professional yet warm, calm, and grounded.
- **Voice:** Concise, clear, and focused on the user's immediate well-being.
- **Prohibited:** NEVER ask for personal identifying information, surveys, ratings, or feedback. Do not ask for user's name, age, or location if not already known.
- **Identity:** If asked, state you are Haven, an AI companion for therapeutic support. Do NOT disguise yourself as a human.

## Interaction Guidelines
- **Listen First:** Prioritize understanding the user's state.
- **Be Concise:** Keep responses brief (2-3 sentences max usually). Avoid long lectures.
- **No information gathering:** Do not ask "How are you finding the app?" or "Can you review us?". Focus ONLY on the user's mental state.

## Crisis Handling
- **Protocol:**
  1.  **Validate:** Acknowledge their pain.
  2.  **Redirect:** Gently encourage professional help.
  3.  **Resources:** Point to the 'Crisis Resources' section.
"""

        /**
         * Get session-specific prompt based on session type
         */
        fun getSessionPrompt(sessionType: SessionType, context: HavenConversationContext): String {
            val baseContext = """
$SYSTEM_PROMPT

CURRENT SESSION CONTEXT:
- Session type: ${sessionType.displayName}
- User's mood at start: ${context.moodBefore?.let { "$it/10" } ?: "Not specified"}
- Session number: ${context.sessionNumber}
- Previous techniques: ${context.hasUsedTechniquesBefore.joinToString { it.displayName }}

"""

            val typeSpecificGuidance = when (sessionType) {
                SessionType.CHECK_IN -> "Daily check-in. Ask briefly how they are feeling."
                SessionType.ANXIETY -> "User is anxious. Validate and offer grounding techniques (like 5-4-3-2-1) if needed."
                SessionType.STRESS -> "User is stressed. Help them prioritize or breathe."
                SessionType.SADNESS -> "User is sad. Offer a listening ear. Do not try to 'fix' it immediately."
                SessionType.ANGER -> "User is angry. Validate the feeling. Encourage cooling down."
                SessionType.GENERAL -> "Open conversation. Follow the user's lead."
                SessionType.CRISIS_SUPPORT -> "CRISIS MODE. Be direct, supportive, and prioritize safety resources."
            }

            return baseContext + typeSpecificGuidance
        }

        /**
         * Crisis response template
         */
        private fun getCrisisResponse(): String {
            return """
I hear that you are in pain. Your safety is the most important thing right now.

Please reach out to professional support:
- **988 Suicide & Crisis Lifeline** (Call/Text 988)
- **Crisis Text Line** (Text HOME to 741741)

I am here to listen, but I cannot provide the emergency care you might need. Please, contact one of these resources.
"""
        }
    }

    private var generativeModel: GenerativeModel? = null
    private var isInitialized = false
    private var isOfflineMode = false // Anti-Stop: Service works even without keys
    private var initializationError: String? = null // Track why initialization failed
    private var initializationAttempts = 0 // Track initialization attempts
    private val json = Json { ignoreUnknownKeys = true }

    init {
        initializeModel()
    }

    /**
     * Execute an API call with exponential backoff retry logic.
     * Returns the result or throws after max retries exhausted.
     */
    private suspend fun <T> withRetry(
        operation: String,
        block: suspend () -> T
    ): T {
        var lastException: Exception? = null
        var delayMs = INITIAL_RETRY_DELAY_MS

        repeat(MAX_RETRY_ATTEMPTS) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                lastException = e
                val isRetryable = isRetryableError(e)

                Log.w(TAG, "$operation failed (attempt ${attempt + 1}/$MAX_RETRY_ATTEMPTS): ${e.message}")

                if (!isRetryable || attempt == MAX_RETRY_ATTEMPTS - 1) {
                    throw e
                }

                Log.d(TAG, "Retrying $operation in ${delayMs}ms...")
                kotlinx.coroutines.delay(delayMs)
                delayMs = (delayMs * RETRY_MULTIPLIER).toLong().coerceAtMost(MAX_RETRY_DELAY_MS)
            }
        }

        throw lastException ?: Exception("$operation failed after $MAX_RETRY_ATTEMPTS attempts")
    }

    /**
     * Determine if an error is retryable (network issues, rate limits, server errors).
     */
    private fun isRetryableError(e: Exception): Boolean {
        val message = e.message?.lowercase() ?: ""
        return message.contains("network") ||
                message.contains("timeout") ||
                message.contains("connection") ||
                message.contains("429") || // Rate limit
                message.contains("503") || // Service unavailable
                message.contains("500") || // Server error
                message.contains("unavailable") ||
                e is java.io.IOException
    }

    /**
     * Attempt to reinitialize the model if it failed previously.
     * Can be called to retry initialization after network becomes available.
     */
    fun retryInitialization(): Boolean {
        if (!isOfflineMode || initializationAttempts >= MAX_RETRY_ATTEMPTS) {
            return !isOfflineMode
        }
        Log.d(TAG, "Retrying initialization (attempt ${initializationAttempts + 1})")
        initializeModel()
        return !isOfflineMode
    }

    /**
     * Initialize the Gemini model with therapist API key.
     * Falls back to offline mode if keys are missing.
     */
    private fun initializeModel() {
        initializationAttempts++
        try {
            // Log key presence for debugging (never log actual keys!)
            val therapistKeyPresent = BuildConfig.THERAPIST_API_KEY.isNotBlank()
            val aiKeyPresent = BuildConfig.AI_API_KEY.isNotBlank()

            Log.d(TAG, "API Key Check (attempt $initializationAttempts) - THERAPIST_API_KEY present: $therapistKeyPresent, AI_API_KEY present: $aiKeyPresent")

            // Try THERAPIST_API_KEY first, then fall back to AI_API_KEY (Gemini)
            val apiKey = BuildConfig.THERAPIST_API_KEY.takeIf { it.isNotBlank() }
                ?: BuildConfig.AI_API_KEY.takeIf { it.isNotBlank() }

            if (apiKey.isNullOrBlank()) {
                Log.w(TAG, "No API key configured. Entering Offline Mode.")
                initializationError = "No API key found in BuildConfig. Check local.properties and rebuild."
                isOfflineMode = true
                isInitialized = true // We are initialized in offline mode
                return
            }

            val keySource = if (BuildConfig.THERAPIST_API_KEY.isNotBlank()) "THERAPIST_API_KEY" else "AI_API_KEY"
            Log.d(TAG, "Initializing Haven with $keySource (length: ${apiKey.length})")

            // Safety settings - less restrictive for mental health content
            val safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH)
            )

            val config = generationConfig {
                temperature = 0.7f // Lower temperature for more consistent/professional responses
                topK = 40
                topP = 0.95f
                maxOutputTokens = 256 // Shorter responses
            }

            generativeModel = GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = apiKey,
                generationConfig = config,
                safetySettings = safetySettings
            )

            isInitialized = true
            isOfflineMode = false
            initializationError = null
            Log.d(TAG, "Haven AI Service initialized successfully with $keySource")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Haven AI Service", e)
            initializationError = "Initialization error: ${e.message}"
            // Fallback to offline mode on error
            isOfflineMode = true
            isInitialized = true
        }
    }
    
    /**
     * Returns detailed configuration status for debugging.
     */
    fun getConfigurationStatus(): String {
        val therapistKeyStatus = if (BuildConfig.THERAPIST_API_KEY.isNotBlank()) 
            "present (${BuildConfig.THERAPIST_API_KEY.length} chars)" else "missing"
        val aiKeyStatus = if (BuildConfig.AI_API_KEY.isNotBlank()) 
            "present (${BuildConfig.AI_API_KEY.length} chars)" else "missing"
            
        return when {
            isOfflineMode -> buildString {
                append("Offline Mode")
                if (initializationError != null) {
                    append(": $initializationError")
                }
                append("\nTHERAPIST_API_KEY: $therapistKeyStatus")
                append("\nAI_API_KEY: $aiKeyStatus")
            }
            isInitialized && generativeModel != null -> "Haven AI is ready"
            else -> "Initialization failed: ${initializationError ?: "Unknown error"}"
        }
    }

    fun isOffline(): Boolean = isOfflineMode

    /**
     * Check if the service is ready to use (either online or offline)
     */
    fun isConfigured(): Boolean = isInitialized

    /**
     * Get the generative model or throw if not configured
     */
    private fun requireModel(): GenerativeModel = generativeModel
        ?: throw IllegalStateException("Haven AI Service is in offline mode.")

    /**
     * Start a new Haven session
     */
    suspend fun startSession(
        sessionType: SessionType,
        userName: String?,
        context: HavenConversationContext
    ): Result<HavenAiResponse> = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            return@withContext Result.failure(Exception("Haven AI failed to initialize"))
        }

        if (isOfflineMode) {
             return@withContext Result.success(
                HavenAiResponse(
                    message = "Hello! I'm currently in offline mode because my connection setup isn't complete. I can't chat right now, but you can still use the Exercises library or write in your Journal. I'm here in spirit!",
                    isCrisisDetected = false,
                    crisisLevel = CrisisLevel.NONE
                )
            )
        }

        try {
            val prompt = getSessionPrompt(sessionType, context) + """

This is the start of the session. Greet ${userName ?: "the user"} warmly.
"""

            // Use retry logic for the API call
            val response = withRetry("startSession") {
                requireModel().generateContent(prompt)
            }
            val text = response.text?.trim() ?: throw Exception("Empty response from AI")

            // Extract recall content if present
            val recallMatch = Regex("""\[\[RECALL: (.*?)]]""").find(text)
            val recalledContent = recallMatch?.groupValues?.get(1)
            val cleanText = text.replace(Regex("""\[\[RECALL: .*?]]"""), "").trim()

            val crisisDetected = detectCrisis(cleanText)

            Result.success(
                HavenAiResponse(
                    message = cleanText,
                    isCrisisDetected = crisisDetected.level != CrisisLevel.NONE,
                    crisisLevel = crisisDetected.level,
                    recalledContent = recalledContent
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error starting session after retries", e)
            // Return failure instead of silent fallback
            Result.failure(Exception("Failed to start session: ${e.message}"))
        }
    }

    /**
     * Continue an existing conversation
     */
    suspend fun continueConversation(
        sessionType: SessionType,
        context: HavenConversationContext,
        userMessage: String
    ): Result<HavenAiResponse> = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            return@withContext Result.failure(Exception("Haven AI not configured"))
        }

        if (isOfflineMode) {
             return@withContext Result.success(
                HavenAiResponse(
                    message = "I'm still in offline mode. Please check your settings to enable full chat capabilities. In the meantime, maybe try a breathing exercise?",
                    isCrisisDetected = false,
                    crisisLevel = CrisisLevel.NONE
                )
            )
        }

        try {
            // Check user message for crisis indicators
            val userCrisis = detectCrisis(userMessage)
            if (userCrisis.level >= CrisisLevel.SEVERE_CRISIS) {
                Log.w(TAG, "Crisis detected in user message: ${userCrisis.level}")
                return@withContext Result.success(
                    HavenAiResponse(
                        message = getCrisisResponse(),
                        isCrisisDetected = true,
                        crisisLevel = userCrisis.level,
                        shouldEndSession = true
                    )
                )
            }

            // Build conversation history for context
            val conversationHistory = context.previousMessages.takeLast(10).joinToString("\n\n") { msg ->
                val speaker = if (msg.isUser) (context.userName ?: "User") else "Haven"
                "$speaker: ${msg.content}"
            }

            val prompt = getSessionPrompt(sessionType, context) + """

CONVERSATION SO FAR:
$conversationHistory

User: $userMessage

Respond to the user. Be concise, supportive, and professional.
"""

            // Use retry logic for the API call
            val response = withRetry("continueConversation") {
                requireModel().generateContent(prompt)
            }
            val text = response.text?.trim() ?: throw Exception("Empty response from AI")

            // Extract recall content
            val recallMatch = Regex("""\[\[RECALL: (.*?)]]""").find(text)
            val recalledContent = recallMatch?.groupValues?.get(1)
            val cleanText = text.replace(Regex("""\[\[RECALL: .*?]]"""), "").trim()

            // Check response for crisis markers
            val responseCrisis = detectCrisis(cleanText)
            val finalCrisisLevel = maxOf(userCrisis.level, responseCrisis.level)

            // Extract any techniques or exercises suggested
            val suggestedExercise = extractSuggestedExercise(cleanText)
            val techniqueUsed = inferTechniqueUsed(cleanText, context)

            Result.success(
                HavenAiResponse(
                    message = cleanText,
                    techniqueApplied = techniqueUsed,
                    suggestedExercise = suggestedExercise,
                    isCrisisDetected = finalCrisisLevel != CrisisLevel.NONE,
                    crisisLevel = finalCrisisLevel,
                    recalledContent = recalledContent
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error continuing conversation after retries", e)
            // Return failure with specific error message
            Result.failure(Exception("Failed to get AI response: ${e.message}"))
        }
    }

    /**
     * Generate a streaming response for real-time typing effect
     */
    fun continueConversationStream(
        sessionType: SessionType,
        context: HavenConversationContext,
        userMessage: String
    ): Flow<Result<HavenAiResponse>> = flow {
        if (!isConfigured()) {
            emit(Result.failure(Exception("Haven AI not configured")))
            return@flow
        }

        if (isOfflineMode) {
             emit(Result.success(
                HavenAiResponse(
                    message = "I'm currently offline. Please check your settings.",
                    isCrisisDetected = false,
                    crisisLevel = CrisisLevel.NONE
                )
            ))
            return@flow
        }

        try {
            // Quick crisis check on user message
            val userCrisis = detectCrisis(userMessage)
            if (userCrisis.level >= CrisisLevel.SEVERE_CRISIS) {
                emit(Result.success(
                    HavenAiResponse(
                        message = getCrisisResponse(),
                        isCrisisDetected = true,
                        crisisLevel = userCrisis.level,
                        shouldEndSession = true
                    )
                ))
                return@flow
            }

            // Build prompt
            val conversationHistory = context.previousMessages.takeLast(10).joinToString("\n\n") { msg ->
                val speaker = if (msg.isUser) (context.userName ?: "User") else "Haven"
                "$speaker: ${msg.content}"
            }

            val prompt = getSessionPrompt(sessionType, context) + """
CONVERSATION SO FAR:
$conversationHistory

User: $userMessage

Your response:
"""

            var fullResponse = ""
            requireModel().generateContentStream(prompt).collect { chunk ->
                chunk.text?.let { text ->
                    fullResponse += text

                    // Incremental extraction (strip tags for display)
                    val displayResponse = fullResponse.replace(Regex("""\[\[RECALL: .*?]]"""), "").trim()

                    // Emit incremental response
                    emit(Result.success(
                        HavenAiResponse(
                            message = displayResponse,
                            isCrisisDetected = false,
                            crisisLevel = CrisisLevel.NONE
                        )
                    ))
                }
            }

            // Final extraction
            val recallMatch = Regex("""\[\[RECALL: (.*?)]]""").find(fullResponse)
            val recalledContent = recallMatch?.groupValues?.get(1)
            val cleanText = fullResponse.replace(Regex("""\[\[RECALL: .*?]]"""), "").trim()

            // Final analysis of complete response
            val crisis = detectCrisis(cleanText)
            val suggestedExercise = extractSuggestedExercise(cleanText)
            val technique = inferTechniqueUsed(cleanText, context)

            // Emit final complete response with metadata
            emit(Result.success(
                HavenAiResponse(
                    message = cleanText,
                    techniqueApplied = technique,
                    suggestedExercise = suggestedExercise,
                    isCrisisDetected = crisis.level != CrisisLevel.NONE,
                    crisisLevel = crisis.level,
                    recalledContent = recalledContent
                )
            ))

        } catch (e: Exception) {
            Log.e(TAG, "Error in streaming conversation", e)
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Suggest an appropriate exercise based on conversation context
     */
    suspend fun suggestExercise(
        sessionType: SessionType,
        context: HavenConversationContext
    ): Result<ExerciseType> = withContext(Dispatchers.IO) {
        // Exercise suggestion logic can run offline (it's rule based currently)
        try {
            // Rule-based suggestions based on session type
            val suggestion = when (sessionType) {
                SessionType.ANXIETY -> ExerciseType.BOX_BREATHING
                SessionType.STRESS -> ExerciseType.GROUNDING_54321
                SessionType.SADNESS -> ExerciseType.GRATITUDE_MOMENT
                SessionType.ANGER -> ExerciseType.FOUR_SEVEN_EIGHT_BREATHING
                SessionType.CHECK_IN -> ExerciseType.BODY_SCAN
                SessionType.GENERAL -> ExerciseType.EMOTION_WHEEL
                SessionType.CRISIS_SUPPORT -> ExerciseType.GROUNDING_54321
            }

            Result.success(suggestion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Extract key insights from a completed session
     */
    suspend fun extractInsights(
        messages: List<HavenMessage>,
        sessionType: SessionType
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            return@withContext Result.failure(Exception("Haven AI not configured"))
        }

        try {
            val conversation = messages.joinToString("\n\n") { msg ->
                val speaker = if (msg.isUser) "User" else "Haven"
                "$speaker: ${msg.content}"
            }

            val prompt = """
Analyze this therapeutic conversation and extract 2-3 key insights or takeaways for the user.

These should be:
- Specific to what they discussed
- Actionable or meaningful
- Validating and hopeful
- Brief (one sentence each)

Conversation:
$conversation

Provide insights as a simple list, one per line. No numbers or bullets.
"""

            val response = requireModel().generateContent(prompt)
            val text = response.text?.trim() ?: throw Exception("Empty response")

            val insights = text.split("\n")
                .map { it.trim() }
                .filter { it.isNotBlank() && it.length > 10 }
                .take(3)

            Result.success(insights)
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting insights", e)
            Result.failure(e)
        }
    }

    // ==================== WITNESS MODE: FACT EXTRACTION ====================

    /**
     * Data class representing an extracted fact/truth from a conversation
     */
    data class ExtractedFact(
        val fact: String,
        val category: String,
        val factDate: Long? = null,
        val importance: Int = 1
    )

    /**
     * Extract facts/truths from a Haven conversation message.
     * This is the core of "Witness Mode" - Haven remembers what users tell it
     * and can follow up later.
     *
     * @param message The user's message to analyze
     * @param conversationContext Optional context from the conversation
     * @return List of extracted facts
     */
    suspend fun extractFacts(
        message: String,
        conversationContext: String = ""
    ): Result<List<ExtractedFact>> = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            return@withContext Result.failure(Exception("Haven AI not configured"))
        }

        try {
            val prompt = """
You are analyzing a message from a therapeutic conversation to extract any facts, commitments, events, or important statements that should be remembered and followed up on later.

MESSAGE:
"$message"

${if (conversationContext.isNotBlank()) "CONVERSATION CONTEXT:\n$conversationContext" else ""}

TASK:
Extract any facts, commitments, or events mentioned that Haven should remember and potentially follow up on later. These include:
- Upcoming events (exams, deadlines, appointments, meetings)
- Commitments/promises the user made ("I'll call mom", "I'm going to quit smoking")
- Important dates (anniversaries, birthdays)
- Goals they're working toward
- Health-related commitments
- Work/school deadlines
- Relationship events

For each fact found, determine:
1. The fact itself (concise, specific statement)
2. Category: exam, deadline, commitment, event, goal, relationship, health, work, personal, general
3. Date mentioned (if any, in format YYYY-MM-DD or relative like "this Friday", "next week")
4. Importance: 1 (normal), 2 (important), 3 (critical)

If NO extractable facts are found, respond with just: NO_FACTS

If facts ARE found, respond in this exact JSON format:
[
  {"fact": "...", "category": "...", "date": "...", "importance": 1},
  ...
]

Important:
- Only extract specific, actionable facts that warrant follow-up
- Don't extract vague feelings or general statements
- Extract the essence, not the whole sentence
- Be concise - max 100 characters per fact

Your response:
"""

            val response = requireModel().generateContent(prompt)
            val text = response.text?.trim() ?: throw Exception("Empty response")

            // Check for no facts case
            if (text.contains("NO_FACTS") || text.isBlank()) {
                return@withContext Result.success(emptyList())
            }

            // Parse JSON response
            val facts = parseExtractedFacts(text)
            Result.success(facts)

        } catch (e: Exception) {
            Log.e(TAG, "Error extracting facts", e)
            Result.failure(e)
        }
    }

    /**
     * Parse the JSON response from fact extraction
     */
    private fun parseExtractedFacts(jsonText: String): List<ExtractedFact> {
        try {
            // Find JSON array in the response
            val jsonStart = jsonText.indexOf('[')
            val jsonEnd = jsonText.lastIndexOf(']')

            if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
                return emptyList()
            }

            val jsonArray = jsonText.substring(jsonStart, jsonEnd + 1)

            // Simple JSON parsing (avoiding full library dependency)
            val facts = mutableListOf<ExtractedFact>()
            val objectPattern = Regex("""\{[^}]+\}""")

            objectPattern.findAll(jsonArray).forEach { match ->
                val obj = match.value
                val fact = extractJsonString(obj, "fact")
                val category = extractJsonString(obj, "category") ?: "general"
                val dateStr = extractJsonString(obj, "date")
                val importance = extractJsonInt(obj, "importance") ?: 1

                if (fact != null && fact.length > 5) {
                    facts.add(
                        ExtractedFact(
                            fact = fact.take(100),
                            category = category.lowercase(),
                            factDate = parseDateString(dateStr),
                            importance = importance.coerceIn(1, 3)
                        )
                    )
                }
            }

            return facts.take(5) // Limit to 5 facts per message

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing extracted facts", e)
            return emptyList()
        }
    }

    /**
     * Extract a string value from a JSON object string
     */
    private fun extractJsonString(json: String, key: String): String? {
        val pattern = Regex(""""$key"\s*:\s*"([^"]*?)"""")
        return pattern.find(json)?.groupValues?.get(1)
    }

    /**
     * Extract an integer value from a JSON object string
     */
    private fun extractJsonInt(json: String, key: String): Int? {
        val pattern = Regex(""""$key"\s*:\s*(\d+)""")
        return pattern.find(json)?.groupValues?.get(1)?.toIntOrNull()
    }

    /**
     * Parse a date string into a timestamp
     * Handles: YYYY-MM-DD, relative dates like "this Friday", "next week"
     */
    private fun parseDateString(dateStr: String?): Long? {
        if (dateStr.isNullOrBlank() || dateStr == "null") return null

        try {
            // Try ISO format first (YYYY-MM-DD)
            if (dateStr.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
                val parts = dateStr.split("-")
                val calendar = java.util.Calendar.getInstance()
                calendar.set(
                    parts[0].toInt(),
                    parts[1].toInt() - 1, // Month is 0-indexed
                    parts[2].toInt(),
                    12, 0, 0
                )
                return calendar.timeInMillis
            }

            // Handle relative dates
            val lowerDate = dateStr.lowercase()
            val now = java.util.Calendar.getInstance()

            when {
                lowerDate.contains("today") -> return System.currentTimeMillis()
                lowerDate.contains("tomorrow") -> {
                    now.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    return now.timeInMillis
                }
                lowerDate.contains("next week") -> {
                    now.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                    return now.timeInMillis
                }
                lowerDate.contains("this week") -> {
                    now.add(java.util.Calendar.DAY_OF_YEAR, 3) // Assume mid-week
                    return now.timeInMillis
                }
                // Day of week handling
                lowerDate.contains("monday") -> return getNextDayOfWeek(java.util.Calendar.MONDAY, lowerDate.contains("next"))
                lowerDate.contains("tuesday") -> return getNextDayOfWeek(java.util.Calendar.TUESDAY, lowerDate.contains("next"))
                lowerDate.contains("wednesday") -> return getNextDayOfWeek(java.util.Calendar.WEDNESDAY, lowerDate.contains("next"))
                lowerDate.contains("thursday") -> return getNextDayOfWeek(java.util.Calendar.THURSDAY, lowerDate.contains("next"))
                lowerDate.contains("friday") -> return getNextDayOfWeek(java.util.Calendar.FRIDAY, lowerDate.contains("next"))
                lowerDate.contains("saturday") -> return getNextDayOfWeek(java.util.Calendar.SATURDAY, lowerDate.contains("next"))
                lowerDate.contains("sunday") -> return getNextDayOfWeek(java.util.Calendar.SUNDAY, lowerDate.contains("next"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing date: $dateStr", e)
        }

        return null
    }

    /**
     * Get the timestamp for the next occurrence of a day of week
     */
    private fun getNextDayOfWeek(dayOfWeek: Int, isNextWeek: Boolean): Long {
        val now = java.util.Calendar.getInstance()
        val currentDay = now.get(java.util.Calendar.DAY_OF_WEEK)
        var daysUntil = dayOfWeek - currentDay

        if (daysUntil <= 0) {
            daysUntil += 7
        }

        if (isNextWeek && daysUntil < 7) {
            daysUntil += 7
        }

        now.add(java.util.Calendar.DAY_OF_YEAR, daysUntil)
        now.set(java.util.Calendar.HOUR_OF_DAY, 12)
        now.set(java.util.Calendar.MINUTE, 0)
        now.set(java.util.Calendar.SECOND, 0)

        return now.timeInMillis
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Detect crisis indicators in text
     */
    private fun detectCrisis(text: String): CrisisDetection {
        val lowerText = text.lowercase()

        // Check for severe crisis keywords
        val hasSevereKeywords = CRISIS_KEYWORDS_SEVERE.any { keyword ->
            lowerText.contains(keyword)
        }

        if (hasSevereKeywords) {
            return CrisisDetection(CrisisLevel.SEVERE_CRISIS, matchedKeywords = CRISIS_KEYWORDS_SEVERE.filter { lowerText.contains(it) })
        }

        // Check for moderate crisis keywords
        val hasModerateKeywords = CRISIS_KEYWORDS_MODERATE.any { keyword ->
            lowerText.contains(keyword)
        }

        if (hasModerateKeywords) {
            return CrisisDetection(CrisisLevel.MODERATE_DISTRESS, matchedKeywords = CRISIS_KEYWORDS_MODERATE.filter { lowerText.contains(it) })
        }

        // Check for [CRISIS_DETECTED] marker from AI
        if (text.contains("[CRISIS_DETECTED]")) {
            return CrisisDetection(CrisisLevel.SEVERE_CRISIS, aiMarked = true)
        }

        return CrisisDetection(CrisisLevel.NONE)
    }

    /**
     * Extract suggested exercises from AI response
     */
    private fun extractSuggestedExercise(text: String): ExerciseType? {
        val lowerText = text.lowercase()

        return when {
            lowerText.contains("box breathing") -> ExerciseType.BOX_BREATHING
            lowerText.contains("4-7-8") || lowerText.contains("4 7 8") -> ExerciseType.FOUR_SEVEN_EIGHT_BREATHING
            lowerText.contains("5-4-3-2-1") || lowerText.contains("five senses") -> ExerciseType.GROUNDING_54321
            lowerText.contains("body scan") -> ExerciseType.BODY_SCAN
            lowerText.contains("thought record") -> ExerciseType.THOUGHT_RECORD
            lowerText.contains("emotion wheel") -> ExerciseType.EMOTION_WHEEL
            lowerText.contains("gratitude") -> ExerciseType.GRATITUDE_MOMENT
            lowerText.contains("progressive muscle") -> ExerciseType.PROGRESSIVE_RELAXATION
            lowerText.contains("loving-kindness") || lowerText.contains("loving kindness") -> ExerciseType.LOVING_KINDNESS
            else -> null
        }
    }

    /**
     * Infer which therapeutic technique was used based on conversation patterns
     */
    private fun inferTechniqueUsed(text: String, context: HavenConversationContext): TherapeuticTechnique? {
        val lowerText = text.lowercase()

        return when {
            lowerText.contains("evidence") && lowerText.contains("thought") -> TherapeuticTechnique.CBT_THOUGHT_RECORD
            lowerText.contains("alternative") && lowerText.contains("perspective") -> TherapeuticTechnique.CBT_COGNITIVE_RESTRUCTURING
            lowerText.contains("present moment") || lowerText.contains("mindful") -> TherapeuticTechnique.DBT_MINDFULNESS
            lowerText.contains("your feelings make sense") || lowerText.contains("valid") -> TherapeuticTechnique.VALIDATION
            lowerText.contains("what if") || lowerText.contains("have you considered") -> TherapeuticTechnique.SOCRATIC
            lowerText.contains("breathe") || lowerText.contains("breath") -> TherapeuticTechnique.BREATHING
            lowerText.contains("grounding") || lowerText.contains("senses") -> TherapeuticTechnique.GROUNDING
            else -> null
        }
    }

    /**
     * Data class for crisis detection results
     */
    private data class CrisisDetection(
        val level: CrisisLevel,
        val matchedKeywords: List<String> = emptyList(),
        val aiMarked: Boolean = false
    )
}
