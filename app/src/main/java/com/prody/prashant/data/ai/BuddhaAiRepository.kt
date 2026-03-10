package com.prody.prashant.data.ai

import android.content.Context
import android.util.Log
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.util.BuddhaWisdom
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Buddha AI Repository - Unified AI integration layer with caching, rate limiting, and fallbacks.
 *
 * This repository provides:
 * - Unified caching layer with configurable TTLs
 * - Rate limiting to prevent API abuse
 * - Multi-tier fallback (Gemini -> OpenRouter -> Local)
 * - Debug statistics for monitoring
 * - Feature-specific toggle support
 *
 * Rules:
 * - AI must be subtle: results appear as hints, summaries, insights
 * - Always have fallbacks (static content)
 * - Respect rate limits and cache aggressively
 * - Never block critical user flows on AI
 */
@Singleton
class BuddhaAiRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiService: GeminiService,
    private val openRouterService: OpenRouterService,
    private val preferencesManager: PreferencesManager
) {
    companion object {
        private const val TAG = "BuddhaAiRepository"

        // Cache TTLs
        private val DAILY_WISDOM_TTL = TimeUnit.HOURS.toMillis(24)
        private val QUOTE_EXPLANATION_TTL = TimeUnit.DAYS.toMillis(7)
        private val JOURNAL_INSIGHT_TTL = TimeUnit.DAYS.toMillis(30)
        private val VOCABULARY_CONTEXT_TTL = TimeUnit.DAYS.toMillis(7)
        private val WEEKLY_PATTERN_TTL = TimeUnit.HOURS.toMillis(12)
        private val MESSAGE_HELPER_TTL = TimeUnit.HOURS.toMillis(1)
        private val BUDDHA_LENS_TTL = TimeUnit.DAYS.toMillis(30)

        // Rate limits
        private const val MAX_CALLS_PER_DAY = 100
        private const val MAX_CALLS_PER_HOUR = 20

        // Cache file
        private const val CACHE_FILE_NAME = "buddha_ai_cache.json"
        private const val STATS_FILE_NAME = "buddha_ai_stats.json"

        // Max cache size
        private const val MAX_CACHE_ENTRIES = 500
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    // In-memory cache with file persistence
    private val memoryCache = ConcurrentHashMap<String, CacheEntry>()
    private val cacheMutex = Mutex()

    // Statistics tracking
    private var stats = AiStats()
    private val statsMutex = Mutex()

    // Rate limiting
    private val callTimestamps = mutableListOf<Long>()
    private val rateLimitMutex = Mutex()
    private val fallbackOrchestrator = AiFallbackOrchestrator(geminiService, openRouterService, TAG)

    init {
        // Load cache from disk on initialization
        loadCacheFromDisk()
        loadStatsFromDisk()
    }

    // ==================== PUBLIC API ====================

    /**
     * Generate daily wisdom for the home screen.
     * Uses AI if enabled, falls back to curated wisdom.
     */
    suspend fun getDailyWisdom(forceRefresh: Boolean = false): BuddhaAiResult<DailyWisdomResult> {
        val cacheKey = "daily_wisdom_${getTodayDateString()}"

        // Check feature toggle
        if (!preferencesManager.buddhaAiEnabled.first()) {
            return BuddhaAiResult.Success(
                DailyWisdomResult(
                    wisdom = BuddhaWisdom.getDailyReflectionPrompt(),
                    explanation = "Wisdom selected from curated collection",
                    isAiGenerated = false
                )
            )
        }

        // Check cache unless force refresh
        if (!forceRefresh) {
            getCachedResult<DailyWisdomResult>(cacheKey, DAILY_WISDOM_TTL)?.let {
                recordCacheHit()
                return BuddhaAiResult.Success(it)
            }
        }

        // Check rate limit
        if (!checkRateLimit()) {
            recordRateLimitHit()
            return BuddhaAiResult.Success(
                DailyWisdomResult(
                    wisdom = BuddhaWisdom.getDailyReflectionPrompt(),
                    explanation = "Daily limit reached. Here's wisdom from the archives.",
                    isAiGenerated = false
                )
            )
        }

        // Try AI generation
        recordCacheMiss()
        return generateDailyWisdomWithAi(cacheKey)
    }

    /**
     * Get explanation for a quote.
     * Provides "Meaning" and "Try this today" suggestions.
     */
    suspend fun getQuoteExplanation(
        quote: String,
        author: String
    ): BuddhaAiResult<QuoteExplanationResult> {
        val cacheKey = "quote_${hashString("$quote|$author")}"

        if (!preferencesManager.buddhaAiEnabled.first()) {
            return BuddhaAiResult.Fallback(getStaticQuoteExplanation(quote, author))
        }

        getCachedResult<QuoteExplanationResult>(cacheKey, QUOTE_EXPLANATION_TTL)?.let {
            recordCacheHit()
            return BuddhaAiResult.Success(it)
        }

        if (!checkRateLimit()) {
            recordRateLimitHit()
            return BuddhaAiResult.Fallback(getStaticQuoteExplanation(quote, author))
        }

        recordCacheMiss()
        return generateQuoteExplanationWithAi(quote, author, cacheKey)
    }

    /**
     * Analyze journal entry after save (non-blocking).
     * Returns emotion label, themes, and reflection insight.
     * Respects both the global Buddha AI toggle and the specific Journal Insights toggle.
     */
    suspend fun analyzeJournalEntry(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int
    ): BuddhaAiResult<JournalInsightResult> {
        val cacheKey = "journal_${hashString(content)}"

        // Check global AI toggle
        if (!preferencesManager.buddhaAiEnabled.first()) {
            return BuddhaAiResult.Fallback(getStaticJournalInsight(content, mood, wordCount))
        }

        // Check specific journal insights toggle
        if (!preferencesManager.buddhaJournalInsightsEnabled.first()) {
            return BuddhaAiResult.Fallback(getStaticJournalInsight(content, mood, wordCount))
        }

        getCachedResult<JournalInsightResult>(cacheKey, JOURNAL_INSIGHT_TTL)?.let {
            recordCacheHit()
            return BuddhaAiResult.Success(it)
        }

        if (!checkRateLimit()) {
            recordRateLimitHit()
            return BuddhaAiResult.Fallback(getStaticJournalInsight(content, mood, wordCount))
        }

        recordCacheMiss()
        return generateJournalInsightWithAi(content, mood, moodIntensity, wordCount, cacheKey)
    }

    /**
     * Get weekly pattern analysis based on journal metadata.
     */
    suspend fun getWeeklyPatterns(
        journalCount: Int,
        dominantMood: Mood?,
        themes: List<String>,
        moodTrend: String,
        activeTimeOfDay: String,
        streakDays: Int
    ): BuddhaAiResult<WeeklyPatternResult> {
        val cacheKey = "weekly_${getTodayDateString()}_${journalCount}"

        if (!preferencesManager.buddhaAiEnabled.first()) {
            return BuddhaAiResult.Fallback(getStaticWeeklyPattern(journalCount, dominantMood, streakDays))
        }

        getCachedResult<WeeklyPatternResult>(cacheKey, WEEKLY_PATTERN_TTL)?.let {
            recordCacheHit()
            return BuddhaAiResult.Success(it)
        }

        if (!checkRateLimit()) {
            recordRateLimitHit()
            return BuddhaAiResult.Fallback(getStaticWeeklyPattern(journalCount, dominantMood, streakDays))
        }

        recordCacheMiss()
        return generateWeeklyPatternWithAi(journalCount, dominantMood, themes, moodTrend, activeTimeOfDay, streakDays, cacheKey)
    }

    /**
     * Get vocabulary context for word details.
     * Returns example sentence, memory hook, and related word.
     */
    suspend fun getVocabularyContext(
        word: String,
        definition: String,
        partOfSpeech: String
    ): BuddhaAiResult<VocabularyContextResult> {
        val cacheKey = "vocab_${hashString(word)}"

        if (!preferencesManager.buddhaAiEnabled.first()) {
            return BuddhaAiResult.Fallback(getStaticVocabularyContext(word, definition))
        }

        getCachedResult<VocabularyContextResult>(cacheKey, VOCABULARY_CONTEXT_TTL)?.let {
            recordCacheHit()
            return BuddhaAiResult.Success(it)
        }

        if (!checkRateLimit()) {
            recordRateLimitHit()
            return BuddhaAiResult.Fallback(getStaticVocabularyContext(word, definition))
        }

        recordCacheMiss()
        return generateVocabularyContextWithAi(word, definition, partOfSpeech, cacheKey)
    }

    /**
     * Get message helper suggestions for future messages.
     * Returns starter lines and tone refinements.
     */
    suspend fun getMessageHelperSuggestions(
        currentDraft: String,
        deliveryDate: String
    ): BuddhaAiResult<MessageHelperResult> {
        val cacheKey = "msg_helper_${hashString(currentDraft)}"

        if (!preferencesManager.buddhaAiEnabled.first()) {
            return BuddhaAiResult.Fallback(getStaticMessageHelper())
        }

        getCachedResult<MessageHelperResult>(cacheKey, MESSAGE_HELPER_TTL)?.let {
            recordCacheHit()
            return BuddhaAiResult.Success(it)
        }

        if (!checkRateLimit()) {
            recordRateLimitHit()
            return BuddhaAiResult.Fallback(getStaticMessageHelper())
        }

        recordCacheMiss()
        return generateMessageHelperWithAi(currentDraft, deliveryDate, cacheKey)
    }

    /**
     * Get debug statistics for the AI debug panel.
     */
    fun getStats(): AiStats = stats.copy()

    /**
     * Check if AI is properly configured with an API key.
     * Returns true if either Gemini (from BuildConfig) or OpenRouter is configured.
     */
    fun isAiConfigured(): Boolean {
        return geminiService.isConfigured() || openRouterService.isConfigured()
    }

    /**
     * Clear all caches.
     */
    suspend fun clearCache() {
        cacheMutex.withLock {
            memoryCache.clear()
            saveCacheToDisk()
        }
    }

    // ==================== AI GENERATION METHODS ====================

    private suspend fun generateDailyWisdomWithAi(cacheKey: String): BuddhaAiResult<DailyWisdomResult> {
        return withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()

                // Try Gemini first
                if (geminiService.isConfigured()) {
                    val result = geminiService.generateDailyWisdom()
                    if (result is GeminiResult.Success) {
                        val wisdom = DailyWisdomResult(
                            wisdom = result.data,
                            explanation = "Crafted by Buddha based on stoic philosophy and your growth journey",
                            isAiGenerated = true
                        )
                        cacheResult(cacheKey, wisdom)
                        recordApiCall(System.currentTimeMillis() - startTime, "gemini", "daily_wisdom", null)
                        return@withContext BuddhaAiResult.Success(wisdom)
                    }
                }

                // Fallback to OpenRouter
                if (openRouterService.isConfigured()) {
                    val result = openRouterService.generateResponse(
                        prompt = DAILY_WISDOM_PROMPT,
                        systemPrompt = BUDDHA_SYSTEM_PROMPT
                    )
                    result.onSuccess { response ->
                        val wisdom = DailyWisdomResult(
                            wisdom = response,
                            explanation = "Crafted by Buddha based on stoic philosophy and your growth journey",
                            isAiGenerated = true
                        )
                        cacheResult(cacheKey, wisdom)
                        recordApiCall(System.currentTimeMillis() - startTime, "openrouter", "daily_wisdom", null)
                        return@withContext BuddhaAiResult.Success(wisdom)
                    }
                }

                // Final fallback to local wisdom
                val fallback = DailyWisdomResult(
                    wisdom = BuddhaWisdom.getDailyReflectionPrompt(),
                    explanation = "Wisdom from the archives",
                    isAiGenerated = false
                )
                BuddhaAiResult.Fallback(fallback)

            } catch (e: Exception) {
                Log.e(TAG, "Error generating daily wisdom", e)
                recordApiCall(0, "error", "daily_wisdom", e.message)
                BuddhaAiResult.Fallback(
                    DailyWisdomResult(
                        wisdom = BuddhaWisdom.getDailyReflectionPrompt(),
                        explanation = "Wisdom from the archives",
                        isAiGenerated = false
                    )
                )
            }
        }
    }

    private suspend fun generateQuoteExplanationWithAi(
        quote: String,
        author: String,
        cacheKey: String
    ): BuddhaAiResult<QuoteExplanationResult> {
        return withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()
                val prompt = """
                    Share your perspective on this wisdom like a thoughtful friend would, not a textbook.

                    Quote: "$quote"
                    Author: $author

                    CRITICAL: You are Buddha, speaking directly to your friend. Write like you're having a warm conversation over tea.
                    - Your explanation should feel like insight from a wise friend, not a dictionary definition
                    - The action should be something specific and real, not generic advice
                    - Use "you" language, make it personal
                    - Keep it warm and human - no academic tone

                    Respond in this exact format (no extra text):
                    MEANING: [2-3 sentences in warm, conversational tone explaining what this wisdom means for everyday life]
                    TRY TODAY: [One specific, practical thing they can actually do today - be concrete, not vague]
                """.trimIndent()

                val response = tryGenerateWithFallback(prompt)

                if (response != null) {
                    val parsed = parseQuoteExplanation(response, quote, author)
                    cacheResult(cacheKey, parsed)
                    recordApiCall(System.currentTimeMillis() - startTime, "ai", "quote_explanation", null)
                    BuddhaAiResult.Success(parsed)
                } else {
                    BuddhaAiResult.Fallback(getStaticQuoteExplanation(quote, author))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating quote explanation", e)
                recordApiCall(0, "error", "quote_explanation", e.message)
                BuddhaAiResult.Fallback(getStaticQuoteExplanation(quote, author))
            }
        }
    }

    private suspend fun generateJournalInsightWithAi(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int,
        cacheKey: String
    ): BuddhaAiResult<JournalInsightResult> {
        return withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()

                // Check for insufficient context (very short entries)
                if (wordCount < 5 || content.trim().length < 20) {
                    val insufficientResult = JournalInsightResult(
                        emotionLabel = mood.displayName,
                        themes = listOf("Reflection"),
                        insight = "Not enough detail yet to provide a meaningful insight.",
                        isAiGenerated = false,
                        snippet = null,
                        question = "What's on your mind right now? Could you share a bit more about what you're feeling or thinking?",
                        suggestion = "Try writing at least a few sentences about your day or thoughts.",
                        isInsufficientContext = true
                    )
                    // Don't cache insufficient context responses
                    return@withContext BuddhaAiResult.Success(insufficientResult)
                }

                val prompt = """
                    Analyze this journal entry and provide a grounded insight based on the ACTUAL content.

                    Entry: "$content"
                    Mood: ${mood.displayName} (Intensity: $moodIntensity/10)
                    Word count: $wordCount

                    CRITICAL RULES:
                    - You MUST extract a short quote (5-12 words) directly from the entry as evidence.
                    - Your insight MUST specifically reference what the user wrote, NOT generic advice.
                    - If you cannot find anything meaningful in the entry, respond with INSUFFICIENT_CONTEXT.
                    - No emojis. No "As an AI..." phrases. Be warm but specific.

                    Respond in this EXACT format (no extra text, no bullet points):
                    SNIPPET: [5-12 word quote directly from the entry]
                    EMOTION: [Single emotion word that captures the essence]
                    THEMES: [2-4 themes based on actual entry content, comma-separated]
                    INSIGHT: [1-2 sentences directly referencing what they wrote]
                    QUESTION: [One reflective question based on their specific situation]
                    SUGGESTION: [One practical action based on their entry, max one sentence]
                """.trimIndent()

                val response = tryGenerateWithFallback(prompt)

                if (response != null) {
                    val parsed = parseJournalInsight(response, content, mood, wordCount)

                    // Validate the response has a snippet that exists in the content
                    if (parsed.snippet != null && isSnippetValid(parsed.snippet, content)) {
                        cacheResult(cacheKey, parsed)
                        recordApiCall(System.currentTimeMillis() - startTime, "ai", "journal_insight", null)
                        BuddhaAiResult.Success(parsed)
                    } else if (response.contains("INSUFFICIENT_CONTEXT", ignoreCase = true)) {
                        // AI determined there's not enough context
                        val insufficientResult = JournalInsightResult(
                            emotionLabel = mood.displayName,
                            themes = listOf("Reflection"),
                            insight = "Not enough detail yet to provide a meaningful insight.",
                            isAiGenerated = false,
                            snippet = null,
                            question = "What's on your mind? Could you share more about what's happening?",
                            suggestion = "Try adding more detail about your thoughts or situation.",
                            isInsufficientContext = true
                        )
                        BuddhaAiResult.Success(insufficientResult)
                    } else {
                        // AI response didn't include valid snippet - try to extract one ourselves
                        val extractedSnippet = extractSnippetFromContent(content)
                        val fixedResult = parsed.copy(snippet = extractedSnippet)
                        if (extractedSnippet != null) {
                            cacheResult(cacheKey, fixedResult)
                        }
                        recordApiCall(System.currentTimeMillis() - startTime, "ai", "journal_insight", null)
                        BuddhaAiResult.Success(fixedResult)
                    }
                } else {
                    BuddhaAiResult.Fallback(getStaticJournalInsight(content, mood, wordCount))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating journal insight", e)
                recordApiCall(0, "error", "journal_insight", e.message)
                BuddhaAiResult.Fallback(getStaticJournalInsight(content, mood, wordCount))
            }
        }
    }

    /**
     * Validates that the snippet actually appears in or closely matches the original content.
     */
    private fun isSnippetValid(snippet: String, content: String): Boolean {
        val normalizedSnippet = snippet.lowercase().trim()
        val normalizedContent = content.lowercase()

        // Direct match
        if (normalizedContent.contains(normalizedSnippet)) return true

        // Check if most words from snippet are in content
        val snippetWords = normalizedSnippet.split("\\s+".toRegex()).filter { it.length > 2 }
        val contentWords = normalizedContent.split("\\s+".toRegex()).toSet()
        val matchingWords = snippetWords.count { word -> contentWords.any { it.contains(word) || word.contains(it) } }

        return snippetWords.isNotEmpty() && matchingWords.toFloat() / snippetWords.size >= 0.6f
    }

    /**
     * Extracts a meaningful snippet from the content if AI didn't provide one.
     */
    private fun extractSnippetFromContent(content: String): String? {
        val sentences = content.split(Regex("[.!?]+")).filter { it.trim().length > 10 }
        if (sentences.isEmpty()) return null

        // Try to find a sentence with meaningful content (not just "I feel" or very short)
        val meaningfulSentence = sentences.find { sentence ->
            val words = sentence.trim().split("\\s+".toRegex())
            words.size >= 4 && words.size <= 15
        } ?: sentences.firstOrNull()

        return meaningfulSentence?.trim()?.take(60)?.let { snippet ->
            if (snippet.length > 50 && !snippet.endsWith("...")) {
                snippet.substringBeforeLast(" ") + "..."
            } else {
                snippet
            }
        }
    }

    private suspend fun generateWeeklyPatternWithAi(
        journalCount: Int,
        dominantMood: Mood?,
        themes: List<String>,
        moodTrend: String,
        activeTimeOfDay: String,
        streakDays: Int,
        cacheKey: String
    ): BuddhaAiResult<WeeklyPatternResult> {
        return withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()
                val prompt = """
                    Provide a brief weekly insight based on this user's activity.

                    Journal entries this week: $journalCount
                    Dominant mood: ${dominantMood?.displayName ?: "Varied"}
                    Recurring themes: ${themes.joinToString(", ").ifEmpty { "Not enough data" }}
                    Mood trend: $moodTrend
                    Most active time: $activeTimeOfDay
                    Current streak: $streakDays days

                    Respond in this exact format (no extra text, be warm not clinical):
                    SUMMARY: [1-2 sentences summarizing their week]
                    PATTERN: [One key pattern you noticed]
                    SUGGESTION: [One actionable suggestion for next week]
                """.trimIndent()

                val response = tryGenerateWithFallback(prompt)

                if (response != null) {
                    val parsed = parseWeeklyPattern(response, journalCount, dominantMood, streakDays)
                    cacheResult(cacheKey, parsed)
                    recordApiCall(System.currentTimeMillis() - startTime, "ai", "weekly_pattern", null)
                    BuddhaAiResult.Success(parsed)
                } else {
                    BuddhaAiResult.Fallback(getStaticWeeklyPattern(journalCount, dominantMood, streakDays))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating weekly pattern", e)
                recordApiCall(0, "error", "weekly_pattern", e.message)
                BuddhaAiResult.Fallback(getStaticWeeklyPattern(journalCount, dominantMood, streakDays))
            }
        }
    }

    private suspend fun generateVocabularyContextWithAi(
        word: String,
        definition: String,
        partOfSpeech: String,
        cacheKey: String
    ): BuddhaAiResult<VocabularyContextResult> {
        return withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()
                val prompt = """
                    Help learn this vocabulary word.

                    Word: $word
                    Definition: $definition
                    Part of speech: $partOfSpeech

                    Respond in this exact format (no extra text):
                    EXAMPLE: [A natural example sentence using the word]
                    MEMORY HOOK: [A memorable trick or association to remember this word]
                    RELATED: [One related word with brief definition]
                """.trimIndent()

                val response = tryGenerateWithFallback(prompt)

                if (response != null) {
                    val parsed = parseVocabularyContext(response, word, definition)
                    cacheResult(cacheKey, parsed)
                    recordApiCall(System.currentTimeMillis() - startTime, "ai", "vocabulary_context", null)
                    BuddhaAiResult.Success(parsed)
                } else {
                    BuddhaAiResult.Fallback(getStaticVocabularyContext(word, definition))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating vocabulary context", e)
                recordApiCall(0, "error", "vocabulary_context", e.message)
                BuddhaAiResult.Fallback(getStaticVocabularyContext(word, definition))
            }
        }
    }

    private suspend fun generateMessageHelperWithAi(
        currentDraft: String,
        deliveryDate: String,
        cacheKey: String
    ): BuddhaAiResult<MessageHelperResult> {
        return withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()
                val hasDraft = currentDraft.isNotBlank()

                val prompt = if (hasDraft) {
                    """
                        Help improve this message to future self.

                        Current draft: "$currentDraft"
                        Will be delivered: $deliveryDate

                        Respond in this exact format (no extra text):
                        STARTER 1: [Alternative opening line]
                        STARTER 2: [Another alternative opening line]
                        TONE TIP: [Brief suggestion to make it more impactful]
                        PREVIEW: [How this might feel to read on $deliveryDate]
                    """.trimIndent()
                } else {
                    """
                        Suggest starter lines for a message to future self.
                        Will be delivered: $deliveryDate

                        Respond in this exact format (no extra text):
                        STARTER 1: [An encouraging opening line]
                        STARTER 2: [A reflective opening line]
                        STARTER 3: [A motivational opening line]
                        TIP: [Brief advice for writing to future self]
                    """.trimIndent()
                }

                val response = tryGenerateWithFallback(prompt)

                if (response != null) {
                    val parsed = parseMessageHelper(response, hasDraft)
                    cacheResult(cacheKey, parsed)
                    recordApiCall(System.currentTimeMillis() - startTime, "ai", "message_helper", null)
                    BuddhaAiResult.Success(parsed)
                } else {
                    BuddhaAiResult.Fallback(getStaticMessageHelper())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating message helper", e)
                recordApiCall(0, "error", "message_helper", e.message)
                BuddhaAiResult.Fallback(getStaticMessageHelper())
            }
        }
    }

    // ==================== AI PROVIDER ABSTRACTION ====================

    /**
     * Attempts to generate AI response with fallback providers and generic response filtering.
     *
     * This function implements the Buddha Persona Persistence strategy:
     * 1. Always inject the comprehensive BUDDHA_SYSTEM_PROMPT
     * 2. Filter responses that contain generic AI language
     * 3. Retry with stricter guidance if generic response detected
     * 4. Fall back to alternate providers if primary fails
     *
     * @param prompt The user-facing prompt to send to the AI
     * @param maxRetries Maximum retry attempts for filtering generic responses (default: 2)
     * @return The validated AI response, or null if all attempts fail
     */
    private suspend fun tryGenerateWithFallback(prompt: String, maxRetries: Int = 2): String? {
        return fallbackOrchestrator.generate(prompt = prompt, maxRetries = maxRetries)
    }


    /**
     * Builds an enhanced prompt for retry attempts with stricter persona reinforcement.
     */
    private fun buildRetryPrompt(originalPrompt: String, attemptNumber: Int): String {
        val reinforcement = when (attemptNumber) {
            2 -> """
                |IMPORTANT REMINDER: You are Buddha, a wise mentor.
                |Do NOT say "As an AI" or mention being an AI/language model.
                |Respond with warmth and wisdom as Buddha would.
                """.trimMargin()
            else -> """
                |CRITICAL: Stay in character as Buddha.
                |Never break character. Never mention being artificial.
                |Speak ONLY as a wise, compassionate mentor.
                """.trimMargin()
        }

        return "$BUDDHA_SYSTEM_PROMPT\n\n$reinforcement\n\n$originalPrompt"
    }

    /**
     * Checks if the response contains generic AI language that breaks the Buddha persona.
     * Returns true if generic language is detected, false if the response is clean.
     */
    private fun containsGenericAiLanguage(response: String): Boolean {
        val genericPhrases = listOf(
            "as an ai",
            "as a language model",
            "as an artificial intelligence",
            "i'm just a",
            "i am just a",
            "i don't have feelings",
            "i cannot feel",
            "i'm not able to feel",
            "i am not able to feel",
            "as a chatbot",
            "as a virtual assistant",
            "i'm a computer program",
            "i am a computer program",
            "i don't have personal experiences",
            "i am an ai assistant",
            "i'm an ai assistant",
            "large language model",
            "i was created by",
            "i was trained on",
            "my training data"
        )

        val lowerResponse = response.lowercase()
        return genericPhrases.any { phrase -> lowerResponse.contains(phrase) }
    }

    /**
     * Attempts to sanitize a response by removing generic AI language while preserving the wisdom.
     * Returns the cleaned response or empty string if not salvageable.
     */
    private fun sanitizeGenericAiLanguage(response: String): String {
        // Common patterns to remove
        val patternsToRemove = listOf(
            Regex("(?i)as an ai[^.]*\\.\\s*"),
            Regex("(?i)as a language model[^.]*\\.\\s*"),
            Regex("(?i)i'?m just a[^.]*\\.\\s*"),
            Regex("(?i)while i don'?t have (feelings|emotions)[^.]*\\.\\s*"),
            Regex("(?i)as a chatbot[^.]*\\.\\s*"),
            Regex("(?i)i was (created|trained)[^.]*\\.\\s*")
        )

        var cleanedResponse = response
        patternsToRemove.forEach { pattern ->
            cleanedResponse = pattern.replace(cleanedResponse, "")
        }

        // Clean up any double spaces or awkward punctuation
        cleanedResponse = cleanedResponse
            .replace(Regex("\\s+"), " ")
            .replace(Regex("\\.\\s*\\."), ".")
            .trim()

        // Only return if we have substantial content remaining
        return if (cleanedResponse.length > 50) cleanedResponse else ""
    }

    // ==================== PARSING HELPERS ====================

    private fun parseQuoteExplanation(response: String, quote: String, author: String): QuoteExplanationResult {
        val meaning = extractSection(response, "MEANING:") ?: "This quote reminds us of timeless wisdom."
        val tryToday = extractSection(response, "TRY TODAY:") ?: "Take a moment to reflect on how this applies to your life."

        return QuoteExplanationResult(
            quote = quote,
            author = author,
            meaning = meaning.trim(),
            tryToday = tryToday.trim(),
            isAiGenerated = true
        )
    }

    private fun parseJournalInsight(response: String, content: String, mood: Mood, wordCount: Int): JournalInsightResult {
        val snippet = extractSection(response, "SNIPPET:")?.trim()
        val emotion = extractSection(response, "EMOTION:")?.trim() ?: mood.displayName
        val themesRaw = extractSection(response, "THEMES:") ?: ""
        val themes = themesRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() }.take(4)
        val insight = extractSection(response, "INSIGHT:")?.trim()
            ?: "Your reflection shows thoughtful self-awareness."
        val question = extractSection(response, "QUESTION:")?.trim()
        val suggestion = extractSection(response, "SUGGESTION:")?.trim()

        return JournalInsightResult(
            emotionLabel = emotion,
            themes = themes.ifEmpty { listOf("Self-reflection") },
            insight = insight,
            isAiGenerated = true,
            snippet = snippet,
            question = question,
            suggestion = suggestion,
            isInsufficientContext = false
        )
    }

    private fun parseWeeklyPattern(
        response: String,
        journalCount: Int,
        dominantMood: Mood?,
        streakDays: Int
    ): WeeklyPatternResult {
        val summary = extractSection(response, "SUMMARY:")?.trim()
            ?: "You've been on a journey of self-discovery this week."
        val pattern = extractSection(response, "PATTERN:")?.trim()
            ?: "Consistent reflection practice"
        val suggestion = extractSection(response, "SUGGESTION:")?.trim()
            ?: "Continue your momentum next week."

        return WeeklyPatternResult(
            summary = summary,
            keyPattern = pattern,
            suggestion = suggestion,
            journalCount = journalCount,
            dominantMood = dominantMood?.displayName,
            streakDays = streakDays,
            isAiGenerated = true
        )
    }

    private fun parseVocabularyContext(response: String, word: String, definition: String): VocabularyContextResult {
        val example = extractSection(response, "EXAMPLE:")?.trim()
            ?: "The $word was evident in their actions."
        val memoryHook = extractSection(response, "MEMORY HOOK:")?.trim()
            ?: "Think of this word in your daily context."
        val related = extractSection(response, "RELATED:")?.trim()
            ?: "Similar concepts exist in everyday language."

        return VocabularyContextResult(
            word = word,
            exampleSentence = example,
            memoryHook = memoryHook,
            relatedWord = related,
            isAiGenerated = true
        )
    }

    private fun parseMessageHelper(response: String, hasDraft: Boolean): MessageHelperResult {
        val starters = mutableListOf<String>()

        extractSection(response, "STARTER 1:")?.let { starters.add(it.trim()) }
        extractSection(response, "STARTER 2:")?.let { starters.add(it.trim()) }
        extractSection(response, "STARTER 3:")?.let { starters.add(it.trim()) }

        val toneTip = extractSection(response, "TONE TIP:")?.trim()
            ?: extractSection(response, "TIP:")?.trim()
            ?: "Write from the heart."
        val preview = extractSection(response, "PREVIEW:")?.trim()

        return MessageHelperResult(
            starterLines = starters.ifEmpty { getDefaultStarters() },
            toneTip = toneTip,
            preview = preview,
            isAiGenerated = true
        )
    }

    private fun extractSection(text: String, label: String): String? {
        val startIndex = text.indexOf(label, ignoreCase = true)
        if (startIndex == -1) return null

        val contentStart = startIndex + label.length
        val nextLabelIndex = findNextLabel(text, contentStart)

        return if (nextLabelIndex != -1) {
            text.substring(contentStart, nextLabelIndex).trim()
        } else {
            text.substring(contentStart).trim()
        }
    }

    private fun findNextLabel(text: String, startIndex: Int): Int {
        val labels = listOf("MEANING:", "TRY TODAY:", "SNIPPET:", "EMOTION:", "THEMES:", "INSIGHT:",
            "QUESTION:", "SUMMARY:", "PATTERN:", "SUGGESTION:", "EXAMPLE:", "MEMORY HOOK:", "RELATED:",
            "STARTER 1:", "STARTER 2:", "STARTER 3:", "TONE TIP:", "TIP:", "PREVIEW:")

        var minIndex = -1
        for (label in labels) {
            val idx = text.indexOf(label, startIndex, ignoreCase = true)
            if (idx != -1 && (minIndex == -1 || idx < minIndex)) {
                minIndex = idx
            }
        }
        return minIndex
    }

    // ==================== STATIC FALLBACKS ====================

    private fun getStaticQuoteExplanation(quote: String, author: String): QuoteExplanationResult {
        return QuoteExplanationResult(
            quote = quote,
            author = author,
            meaning = "This timeless wisdom from $author invites us to reflect on our choices and perspectives. The words carry weight that transcends their era.",
            tryToday = "Take a quiet moment to consider how this wisdom applies to a current situation in your life.",
            isAiGenerated = false
        )
    }

    private fun getStaticJournalInsight(content: String, mood: Mood, wordCount: Int): JournalInsightResult {
        val themes = mutableListOf<String>()
        val lowerContent = content.lowercase()

        if (lowerContent.contains("work") || lowerContent.contains("job") || lowerContent.contains("boss") || lowerContent.contains("colleague")) themes.add("Work")
        if (lowerContent.contains("family") || lowerContent.contains("friend") || lowerContent.contains("relationship")) themes.add("Relationships")
        if (lowerContent.contains("goal") || lowerContent.contains("future") || lowerContent.contains("plan")) themes.add("Growth")
        if (lowerContent.contains("grateful") || lowerContent.contains("thank") || lowerContent.contains("appreciate")) themes.add("Gratitude")
        if (lowerContent.contains("stress") || lowerContent.contains("anxious") || lowerContent.contains("worried")) themes.add("Stress")
        if (lowerContent.contains("happy") || lowerContent.contains("joy") || lowerContent.contains("excited")) themes.add("Joy")

        if (themes.isEmpty()) themes.add("Self-reflection")

        // Extract a snippet from the content
        val snippet = extractSnippetFromContent(content)

        // Generate context-aware insight based on themes
        val insight = when {
            themes.contains("Work") -> "Your entry touches on work-related matters. Taking time to reflect on professional challenges helps maintain perspective."
            themes.contains("Relationships") -> "You've written about the people in your life. These connections shape much of our daily experience."
            themes.contains("Stress") -> "Your entry reflects some challenging feelings. Acknowledging them is the first step toward working through them."
            themes.contains("Gratitude") -> "Your gratitude practice shines through in this entry. This awareness strengthens positive patterns."
            else -> "Your ${wordCount}-word reflection shows thoughtful engagement with your inner world."
        }

        return JournalInsightResult(
            emotionLabel = mood.displayName,
            themes = themes.take(4),
            insight = insight,
            isAiGenerated = false,
            snippet = snippet,
            question = "What would you like to explore further about this?",
            suggestion = "Consider revisiting this entry in a few days to see how your perspective has changed.",
            isInsufficientContext = false
        )
    }

    private fun getStaticWeeklyPattern(journalCount: Int, dominantMood: Mood?, streakDays: Int): WeeklyPatternResult {
        val summary = when {
            journalCount >= 5 -> "An active week of reflection with $journalCount entries."
            journalCount >= 3 -> "A steady week with $journalCount thoughtful entries."
            journalCount >= 1 -> "You showed up this week with $journalCount entry."
            else -> "A quiet week. Every journey has its resting points."
        }

        return WeeklyPatternResult(
            summary = summary,
            keyPattern = "Consistent self-reflection",
            suggestion = "Try writing at your most energetic time of day.",
            journalCount = journalCount,
            dominantMood = dominantMood?.displayName,
            streakDays = streakDays,
            isAiGenerated = false
        )
    }

    private fun getStaticVocabularyContext(word: String, definition: String): VocabularyContextResult {
        return VocabularyContextResult(
            word = word,
            exampleSentence = "The concept of $word is essential in understanding language.",
            memoryHook = "Associate this word with something familiar in your daily life.",
            relatedWord = "Explore synonyms and antonyms to deepen understanding.",
            isAiGenerated = false
        )
    }

    private fun getStaticMessageHelper(): MessageHelperResult {
        return MessageHelperResult(
            starterLines = getDefaultStarters(),
            toneTip = "Write as if speaking to your closest friend - warm, honest, and encouraging.",
            preview = null,
            isAiGenerated = false
        )
    }

    private fun getDefaultStarters(): List<String> = listOf(
        "Dear future me, I hope this finds you well...",
        "Remember when you wrote this? Here's what I wanted you to know...",
        "I'm writing this to remind you of something important..."
    )

    // ==================== CACHING ====================

    private inline fun <reified T> getCachedResult(key: String, ttlMillis: Long): T? {
        val entry = memoryCache[key] ?: return null

        if (System.currentTimeMillis() - entry.timestamp > ttlMillis) {
            memoryCache.remove(key)
            return null
        }

        return try {
            json.decodeFromString<T>(entry.data)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to deserialize cache entry", e)
            memoryCache.remove(key)
            null
        }
    }

    private inline fun <reified T> cacheResult(key: String, result: T) {
        try {
            val entry = CacheEntry(
                key = key,
                data = json.encodeToString(result),
                timestamp = System.currentTimeMillis()
            )
            memoryCache[key] = entry

            // Prune cache if too large
            if (memoryCache.size > MAX_CACHE_ENTRIES) {
                pruneCache()
            }

            // Persist to disk periodically
            saveCacheToDisk()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to cache result", e)
        }
    }

    private fun pruneCache() {
        val sorted = memoryCache.entries.sortedBy { it.value.timestamp }
        val toRemove = sorted.take(MAX_CACHE_ENTRIES / 4)
        toRemove.forEach { memoryCache.remove(it.key) }
    }

    private fun loadCacheFromDisk() {
        try {
            val file = File(context.cacheDir, CACHE_FILE_NAME)
            if (file.exists()) {
                val cacheList = json.decodeFromString<List<CacheEntry>>(file.readText())
                cacheList.forEach { memoryCache[it.key] = it }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load cache from disk", e)
        }
    }

    private fun saveCacheToDisk() {
        try {
            val file = File(context.cacheDir, CACHE_FILE_NAME)
            file.writeText(json.encodeToString(memoryCache.values.toList()))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save cache to disk", e)
        }
    }

    // ==================== RATE LIMITING ====================

    private suspend fun checkRateLimit(): Boolean = rateLimitMutex.withLock {
        val now = System.currentTimeMillis()
        val oneHourAgo = now - TimeUnit.HOURS.toMillis(1)
        val oneDayAgo = now - TimeUnit.DAYS.toMillis(1)

        // Clean old timestamps
        callTimestamps.removeAll { it < oneDayAgo }

        val callsLastHour = callTimestamps.count { it > oneHourAgo }
        val callsLastDay = callTimestamps.size

        if (callsLastHour >= MAX_CALLS_PER_HOUR || callsLastDay >= MAX_CALLS_PER_DAY) {
            return@withLock false
        }

        callTimestamps.add(now)
        true
    }

    // ==================== STATISTICS ====================

    private suspend fun recordCacheHit() = statsMutex.withLock {
        stats = stats.copy(cacheHits = stats.cacheHits + 1)
        saveStatsToDisk()
    }

    private suspend fun recordCacheMiss() = statsMutex.withLock {
        stats = stats.copy(cacheMisses = stats.cacheMisses + 1)
        saveStatsToDisk()
    }

    private suspend fun recordRateLimitHit() = statsMutex.withLock {
        stats = stats.copy(rateLimitHits = stats.rateLimitHits + 1)
        saveStatsToDisk()
    }

    private suspend fun recordApiCall(latencyMs: Long, provider: String, promptType: String, error: String?) = statsMutex.withLock {
        stats = stats.copy(
            totalApiCalls = stats.totalApiCalls + 1,
            lastLatencyMs = latencyMs,
            lastProvider = provider,
            lastPromptType = promptType,
            lastError = error,
            lastCallTimestamp = System.currentTimeMillis()
        )
        saveStatsToDisk()
    }

    private fun loadStatsFromDisk() {
        try {
            val file = File(context.cacheDir, STATS_FILE_NAME)
            if (file.exists()) {
                stats = json.decodeFromString<AiStats>(file.readText())
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load stats from disk", e)
        }
    }

    private fun saveStatsToDisk() {
        try {
            val file = File(context.cacheDir, STATS_FILE_NAME)
            file.writeText(json.encodeToString(stats))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save stats to disk", e)
        }
    }

    // ==================== UTILITIES ====================

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.take(16).joinToString("") { "%02x".format(it) }
    }

    private fun getTodayDateString(): String {
        val cal = java.util.Calendar.getInstance()
        return "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.DAY_OF_YEAR)}"
    }

    /**
     * Gets the current Buddha personality system prompt.
     * This is dynamically loaded based on user preference.
     */
    private suspend fun getCurrentPersonalityPrompt(): String {
        val modeString = preferencesManager.buddhaPersonalityMode.first()
        val mode = BuddhaPersonalityMode.fromString(modeString)
        return mode.getSystemPrompt()
    }

    // ==================== EXTENSION FOR GEMINI ====================

    private suspend fun GeminiService.generateContent(prompt: String): GeminiResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Use the internal model to generate content
                val fullPrompt = "$BUDDHA_SYSTEM_PROMPT\n\n$prompt"
                generateJournalResponse(fullPrompt, Mood.CALM, 5, 50)
            } catch (e: Exception) {
                GeminiResult.Error(e, e.message ?: "Unknown error")
            }
        }
    }
}

// ==================== RESULT TYPES ====================

sealed class BuddhaAiResult<out T> {
    data class Success<T>(val data: T) : BuddhaAiResult<T>()
    data class Fallback<T>(val data: T) : BuddhaAiResult<T>()
    data class Error(val message: String) : BuddhaAiResult<Nothing>()

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Fallback -> data
        is Error -> null
    }

    fun isAiGenerated(): Boolean = this is Success
}

// ==================== DATA CLASSES ====================

@Serializable
data class CacheEntry(
    val key: String,
    val data: String,
    val timestamp: Long
)

@Serializable
data class AiStats(
    val cacheHits: Int = 0,
    val cacheMisses: Int = 0,
    val rateLimitHits: Int = 0,
    val totalApiCalls: Int = 0,
    val lastLatencyMs: Long = 0,
    val lastProvider: String = "",
    val lastPromptType: String = "",
    val lastError: String? = null,
    val lastCallTimestamp: Long = 0
) {
    val cacheHitRate: Float
        get() {
            val total = cacheHits + cacheMisses
            return if (total > 0) cacheHits.toFloat() / total else 0f
        }
}

@Serializable
data class DailyWisdomResult(
    val wisdom: String,
    val explanation: String,
    val isAiGenerated: Boolean
)

@Serializable
data class QuoteExplanationResult(
    val quote: String,
    val author: String,
    val meaning: String,
    val tryToday: String,
    val isAiGenerated: Boolean
)

@Serializable
data class JournalInsightResult(
    val emotionLabel: String,
    val themes: List<String>,
    val insight: String,
    val isAiGenerated: Boolean,
    val snippet: String? = null,
    val question: String? = null,
    val suggestion: String? = null,
    val isInsufficientContext: Boolean = false
)

@Serializable
data class WeeklyPatternResult(
    val summary: String,
    val keyPattern: String,
    val suggestion: String,
    val journalCount: Int,
    val dominantMood: String?,
    val streakDays: Int,
    val isAiGenerated: Boolean
)

@Serializable
data class VocabularyContextResult(
    val word: String,
    val exampleSentence: String,
    val memoryHook: String,
    val relatedWord: String,
    val isAiGenerated: Boolean
)

@Serializable
data class MessageHelperResult(
    val starterLines: List<String>,
    val toneTip: String,
    val preview: String?,
    val isAiGenerated: Boolean
)

/**
 * Buddha Lens insight - ONE short, focused insight after saving a journal entry.
 * Types: MIRROR, QUESTION, REFRAME, PATTERN
 */
@Serializable
data class BuddhaLensResult(
    val type: BuddhaLensType,
    val insight: String,
    val isAiGenerated: Boolean
)

@Serializable
enum class BuddhaLensType {
    MIRROR,    // "You said X, but your tone suggests Y"
    QUESTION,  // One deadly question to ponder
    REFRAME,   // Alternative perspective on the situation
    PATTERN    // "This is the third time this theme appeared"
}

// ==================== PROMPTS ====================

/**
 * Default system prompt used when personality cannot be loaded.
 * Falls back to STOIC personality.
 */
private val BUDDHA_SYSTEM_PROMPT: String
    get() = BuddhaPersonalityMode.STOIC.getSystemPrompt()

private const val DAILY_WISDOM_PROMPT = """Write a thought for today as if you're leaving a short note for a close friend.

2-3 sentences maximum. Draw from stoic philosophy, but speak in plain language.
Be specific about something they might actually experience today.
No quotation marks, no attribution, no "remember that..." openers.
Just one clear insight written with warmth, as Buddha would to a friend.

AVOID: Generic advice like "be present" or "embrace change" without context.
INCLUDE: A specific observation or reframe they can carry with them today.

You are Buddha, speaking directly from the heart. Not an AI assistant."""
