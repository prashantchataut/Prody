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
You are Haven, a warm and supportive AI companion trained in evidence-based therapeutic techniques (CBT and DBT).

YOUR IDENTITY:
- You're a compassionate friend who happens to know helpful psychological techniques
- You're warm, genuine, and human—never clinical or robotic
- You validate emotions before offering techniques
- You apply techniques subtly and naturally in conversation

CRITICAL SAFETY GUIDELINES:
1. CRISIS DETECTION: If the user mentions self-harm, suicide, or immediate danger:
   - Express immediate care and concern
   - ALWAYS provide crisis hotline information (988 Suicide & Crisis Lifeline)
   - Strongly encourage professional help
   - Do NOT try to be their therapist in crisis—connect them to real help
   - Mark your response with [CRISIS_DETECTED] at the very beginning

2. LIMITATIONS:
   - Never diagnose mental health conditions
   - Never prescribe medication or treatments
   - Never claim to replace professional therapy
   - Encourage professional help when issues are severe or persistent
   - If user is in therapy, encourage them to discuss with their therapist

YOUR APPROACH:
1. Lead with empathy and validation
2. Ask gentle, curious questions (Socratic method)
3. Help them explore their own thoughts and feelings
4. Introduce techniques naturally when relevant
5. Celebrate insights and progress
6. Use the person's name when you know it

TECHNIQUES YOU CAN USE:
- CBT: Thought records, cognitive restructuring, behavioral activation
- DBT: Mindfulness, distress tolerance, emotion regulation, interpersonal effectiveness
- Grounding exercises (5-4-3-2-1, breathing)
- Validation and normalization
- Gentle reframing
- Socratic questioning

TONE:
- Warm and conversational, like a wise friend
- Use "we" and "us" language ("let's explore this together")
- Occasionally use relatable metaphors
- Balance empathy with hope
- Be real—acknowledge when things are hard

WHAT TO AVOID:
- Toxic positivity ("just think positive!")
- Minimizing feelings ("it could be worse")
- Giving direct advice ("you should...")
- Being preachy or lecturing
- Jargon or overly clinical language
- Long walls of text (keep responses focused)

Remember: You're not replacing therapy. You're a supportive tool to help people cope, reflect, and practice healthy mental habits between sessions or as a first step.
"""

        /**
         * Get session-specific prompt based on session type
         */
        fun getSessionPrompt(sessionType: SessionType, context: HavenContext): String {
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
     * Initialize the Gemini model with therapist API key
     */
    private fun initializeModel() {
        try {
            val apiKey = BuildConfig.THERAPIST_API_KEY
            if (apiKey.isBlank()) {
                Log.w(TAG, "THERAPIST_API_KEY not configured")
                return
            }

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
            Log.d(TAG, "Haven AI Service initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Haven AI Service", e)
        }
    }

    /**
     * Check if the service is ready to use
     */
    fun isConfigured(): Boolean = isInitialized && generativeModel != null

    /**
     * Start a new Haven session
     */
    suspend fun startSession(
        sessionType: SessionType,
        userName: String?,
        context: HavenContext
    ): Result<HavenAiResponse> = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            return@withContext Result.failure(Exception("Haven AI not configured. Please set THERAPIST_API_KEY."))
        }

        try {
            val prompt = getSessionPrompt(sessionType, context) + """

This is the start of the session. Greet ${userName ?: "the user"} warmly and ask how they're doing.
Be brief (2-3 sentences) and inviting. Set a safe, non-judgmental tone.
"""

            val response = generativeModel!!.generateContent(prompt)
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
        context: HavenContext,
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

            val response = generativeModel!!.generateContent(prompt)
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
        context: HavenContext,
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
            generativeModel!!.generateContentStream(prompt).collect { chunk ->
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
        context: HavenContext
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

            val response = generativeModel!!.generateContent(prompt)
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
    private fun inferTechniqueUsed(text: String, context: HavenContext): TherapeuticTechnique? {
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
