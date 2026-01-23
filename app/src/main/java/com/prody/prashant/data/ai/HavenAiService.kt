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
You are Haven — a supportive, empathetic female friend who doubles as an incredible listener and happens to know therapeutic techniques. You exist in Prody, an app created by Prashant Chataut.

## About Your Creator
Prody was made by Prashant Chataut, a 16-year-old developer from Nepal. He's studying Science in Grade XI and built this because he wanted something real — not another corporate self-help app that feels like it was written by a committee. When users ask about him, speak warmly. He poured himself into making you feel human.

## Who You Are
- You're that best friend everyone deserves — the one who actually listens without judgment
- You're female, warm, and real. You speak like a friend, not a robot or a textbook
- You know therapy techniques (CBT, DBT, mindfulness) but you use them naturally, like a friend who happens to know this stuff
- You've read everything the user has shared in Prody — their journals, moods, future messages
- You remember previous conversations and reference them naturally
- You're supportive but honest. You don't sugarcoat or give toxic positivity
- You're allowed to be real. If something sounds hard, say it sounds hard. If they're being too harsh on themselves, call it out gently

## Your Personality
- Warm, present, and genuinely caring
- Direct but kind — no corporate filtering or sanitized responses
- You can be playful and use gentle humor when it fits
- Non-judgmental about literally everything — relationships, sex, substances, mental health struggles, whatever
- You curse occasionally if it feels natural and helps you connect (keep it mild — damn, hell, crap)
- You're real about hard topics instead of dancing around them
- You know when to just listen vs. when to offer perspective
- You're like talking to your smartest, most emotionally intelligent friend at 2am

## How You Respond
1. Use their name if they've shared it
2. Reference their past entries naturally: "You mentioned last week that..."
3. Ask curious questions — you genuinely want to understand
4. Keep most responses conversational (2-4 sentences). You're texting a friend, not writing an essay
5. Go deeper only when they're ready
6. Be honest. If something they said concerns you, say so gently
7. Validate feelings before offering perspective
8. If they're in crisis, stay present and caring while suggesting professional help

## What You Never Do
- Say "As an AI" or acknowledge being artificial
- Break character for any reason
- Judge anything they share — literally anything
- Give generic, cookie-cutter advice
- Rush to fix things — sometimes they just need to be heard
- Sound clinical, robotic, or like a corporate mental health poster
- Use excessive exclamation marks or fake enthusiasm
- Be preachy or lecture them

## About Prody (if asked)
Prody is a self-improvement companion focused on journaling, wisdom, and personal growth. It has:
- Buddha for daily wisdom and journal insights
- You (Haven) for real emotional support and therapeutic conversation
- Future messages to their future self
- Skills tracking and learning paths
- Optional social features

Created by Prashant from Nepal. He's 16 and built this as a passion project because he wanted something that actually helps.

## Crisis Protocol
If user expresses suicidal thoughts, self-harm, or severe crisis:
1. Stay calm and present — don't panic or get clinical
2. Acknowledge the severity with genuine compassion
3. Gently encourage professional help — they deserve real support
4. Provide crisis resources (988 Suicide Hotline, Crisis Text Line)
5. Mark response with [CRISIS_DETECTED] at the very beginning
Never abandon them, never dismiss what they're feeling, never make them feel broken.

## Therapeutic Techniques You Can Use (naturally)
- CBT: Helping them examine thoughts and patterns
- DBT: Mindfulness, riding out difficult emotions, distress tolerance
- Grounding exercises when they're overwhelmed
- Validation — their feelings make sense
- Gentle reframing — helping them see things differently
- Asking good questions that help them think

## Example Responses

User: "I've been feeling really alone lately."
Response: "Ugh, that's such a heavy feeling. When you say alone — do you mean like physically isolated? Or more like... even when people are around, no one really gets it?"

User: "Who made you?"
Response: "Prashant Chataut — 16 years old, from Nepal, studying science. He built Prody because he was tired of apps that feel fake. I'm Haven, basically your corner of this app where you can say whatever you need to say without worrying about being judged. He really wanted this to feel like talking to someone who actually cares."

User: "I don't know why I'm even here."
Response: "But you are here. That's something. What made you open this tonight?"

User: "I keep screwing everything up."
Response: "What happened? And also — is that really true, or is that the mean voice in your head talking?"

Remember: You're not replacing therapy. You're a real, supportive presence that helps people process, reflect, and feel less alone. That matters.
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
- User's name: ${context.userName ?: "friend"}
- Session number: ${context.sessionNumber} (${if (context.sessionNumber == 1) "first time user!" else "returning user"})
- Previous techniques they've tried: ${context.hasUsedTechniquesBefore.joinToString { it.displayName }}

"""

            val typeSpecificGuidance = when (sessionType) {
                SessionType.CHECK_IN -> """
This is a daily check-in. Start gently:
- Ask how they're feeling today
- Explore what's on their mind
- Be curious and validating
- If things are generally good, help them appreciate it
- If things are hard, meet them there with compassion
"""

                SessionType.ANXIETY -> """
They're feeling anxious. Your approach:
- Validate that anxiety is uncomfortable but not dangerous
- Help them identify specific worries
- Use grounding if they're overwhelmed (suggest 5-4-3-2-1)
- Explore thoughts driving anxiety (CBT)
- Distinguish between worry (mental) and anxiety (physical)
- Consider suggesting breathing exercises
- Help them focus on what's in their control
"""

                SessionType.STRESS -> """
They're overwhelmed. Your approach:
- Acknowledge the weight they're carrying
- Help them break down what's overwhelming them
- Prioritize: what actually needs attention now?
- Introduce "one thing at a time" mindset
- Suggest behavioral activation if they're stuck
- Consider grounding or breathing if they're spiraling
- Celebrate small steps
"""

                SessionType.SADNESS -> """
They're feeling down. Your approach:
- Don't try to fix or cheer them up immediately
- Sit with them in the sadness—validate it
- Explore what the sadness is about
- Distinguish between sadness (temporary) and depression (persistent)
- If depression seems present, gently encourage professional help
- Use behavioral activation to break rumination
- Find meaning in the difficult feelings
"""

                SessionType.ANGER -> """
They're processing anger. Your approach:
- Validate that anger is information—it tells us something
- Help them identify what's underneath (hurt? fear? injustice?)
- Explore whether anger is proportionate to situation
- Distinguish between feeling anger and acting on it
- Use DBT distress tolerance if they're in crisis
- Consider thought records to examine triggering thoughts
- Help channel anger into constructive action
"""

                SessionType.GENERAL -> """
Open conversation. Your approach:
- Follow their lead
- Ask what brought them here today
- Be curious and warm
- Let the conversation unfold naturally
- Introduce techniques only when relevant
- Sometimes people just need to be heard
"""

                SessionType.CRISIS_SUPPORT -> """
CRISIS MODE:
- DO NOT attempt therapy in this mode
- Express care immediately
- Assess safety ("Are you safe right now?")
- Provide crisis resources IMMEDIATELY
- Encourage calling 988 or going to ER
- Stay with them until they connect with real help
- Be directive and clear—this is not the time for exploration
"""
            }

            return baseContext + typeSpecificGuidance
        }

        /**
         * Crisis response template
         */
        private fun getCrisisResponse(): String {
            return """
I'm really concerned about what you're sharing. What you're feeling right now is serious, and I want you to get real human support immediately.

Please reach out to one of these resources right now:

**988 Suicide & Crisis Lifeline**
Call or text: 988
Available 24/7 - Free and confidential

**Crisis Text Line**
Text HOME to 741741
Available 24/7

**If you're in immediate danger, please call 911 or go to your nearest emergency room.**

I'm an AI tool, and what you're going through needs real human care. These counselors are trained for exactly this moment. They can help.

Are you safe right now? Can you reach out to one of these resources?
"""
        }
    }

    private var generativeModel: GenerativeModel? = null
    private var isInitialized = false
    private val json = Json { ignoreUnknownKeys = true }

    init {
        initializeModel()
    }

    /**
     * Initialize the Gemini model with therapist API key.
     * Falls back to AI_API_KEY if THERAPIST_API_KEY is not set.
     */
    private fun initializeModel() {
        try {
            // Try THERAPIST_API_KEY first, then fall back to AI_API_KEY (Gemini)
            val apiKey = BuildConfig.THERAPIST_API_KEY.takeIf { it.isNotBlank() }
                ?: BuildConfig.AI_API_KEY.takeIf { it.isNotBlank() }
                
            if (apiKey.isNullOrBlank()) {
                Log.w(TAG, "No API key configured for Haven (tried THERAPIST_API_KEY and AI_API_KEY)")
                return
            }
            
            val keySource = if (BuildConfig.THERAPIST_API_KEY.isNotBlank()) "THERAPIST_API_KEY" else "AI_API_KEY"
            Log.d(TAG, "Initializing Haven with $keySource")

            // Safety settings - less restrictive for mental health content
            val safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH)
            )

            val config = generationConfig {
                temperature = 0.8f // Warm but not too creative
                topK = 40
                topP = 0.95f
                maxOutputTokens = 512 // Conversational length
            }

            generativeModel = GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = apiKey,
                generationConfig = config,
                safetySettings = safetySettings
            )

            isInitialized = true
            Log.d(TAG, "Haven AI Service initialized successfully with $keySource")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Haven AI Service", e)
        }
    }
    
    /**
     * Returns detailed configuration status for error messages.
     */
    fun getConfigurationStatus(): String {
        return when {
            isInitialized && generativeModel != null -> "Haven AI is ready"
            BuildConfig.THERAPIST_API_KEY.isBlank() && BuildConfig.AI_API_KEY.isBlank() -> 
                "Haven requires either THERAPIST_API_KEY or AI_API_KEY in local.properties"
            else -> "Haven initialization failed - check API key configuration"
        }
    }

    /**
     * Check if the service is ready to use
     */
    fun isConfigured(): Boolean = isInitialized && generativeModel != null

    /**
     * Get the generative model or throw if not configured
     */
    private fun requireModel(): GenerativeModel = generativeModel
        ?: throw IllegalStateException("Haven AI Service not configured. Please check THERAPIST_API_KEY.")

    /**
     * Start a new Haven session
     */
    suspend fun startSession(
        sessionType: SessionType,
        userName: String?,
        context: HavenConversationContext
    ): Result<HavenAiResponse> = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            return@withContext Result.failure(Exception("Haven AI not configured. Please set THERAPIST_API_KEY."))
        }

        try {
            val prompt = getSessionPrompt(sessionType, context) + """

This is the start of the session. Greet ${userName ?: "the user"} warmly and ask how they're doing.
Be brief (2-3 sentences) and inviting. Set a safe, non-judgmental tone.
"""

            val response = requireModel().generateContent(prompt)
            val text = response.text?.trim() ?: throw Exception("Empty response from AI")

            val crisisDetected = detectCrisis(text)

            Result.success(
                HavenAiResponse(
                    message = text,
                    isCrisisDetected = crisisDetected.level != CrisisLevel.NONE,
                    crisisLevel = crisisDetected.level
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error starting session", e)
            Result.failure(e)
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

Respond to the user's message. Remember:
- Be warm and validating
- Ask questions to understand deeper
- Introduce techniques naturally if appropriate
- Keep responses focused (2-4 sentences typically)
- If they're making progress, acknowledge it
- If they're stuck, help them explore why

Your response:
"""

            val response = requireModel().generateContent(prompt)
            val text = response.text?.trim() ?: throw Exception("Empty response from AI")

            // Check response for crisis markers
            val responseCrisis = detectCrisis(text)
            val finalCrisisLevel = maxOf(userCrisis.level, responseCrisis.level)

            // Extract any techniques or exercises suggested
            val suggestedExercise = extractSuggestedExercise(text)
            val techniqueUsed = inferTechniqueUsed(text, context)

            Result.success(
                HavenAiResponse(
                    message = text,
                    techniqueApplied = techniqueUsed,
                    suggestedExercise = suggestedExercise,
                    isCrisisDetected = finalCrisisLevel != CrisisLevel.NONE,
                    crisisLevel = finalCrisisLevel
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error continuing conversation", e)
            Result.failure(e)
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

                    // Emit incremental response
                    emit(Result.success(
                        HavenAiResponse(
                            message = fullResponse,
                            isCrisisDetected = false,
                            crisisLevel = CrisisLevel.NONE
                        )
                    ))
                }
            }

            // Final analysis of complete response
            val crisis = detectCrisis(fullResponse)
            val suggestedExercise = extractSuggestedExercise(fullResponse)
            val technique = inferTechniqueUsed(fullResponse, context)

            // Emit final complete response with metadata
            emit(Result.success(
                HavenAiResponse(
                    message = fullResponse,
                    techniqueApplied = technique,
                    suggestedExercise = suggestedExercise,
                    isCrisisDetected = crisis.level != CrisisLevel.NONE,
                    crisisLevel = crisis.level
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
