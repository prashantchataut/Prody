package com.prody.prashant.data.ai

import android.util.Log
import com.prody.prashant.data.cache.AiCacheManager
import com.prody.prashant.domain.model.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * High-level Buddha AI Service that integrates GeminiService with AiCacheManager.
 *
 * This service provides:
 * - Cached AI responses for frequently accessed content
 * - Automatic cache management with TTL-based expiration
 * - Fallback to fresh generation when cache misses
 * - Optimized API usage through intelligent caching
 *
 * Use this service instead of GeminiService directly for most Buddha AI features.
 */
@Singleton
class BuddhaAiService @Inject constructor(
    private val geminiService: GeminiService,
    private val openRouterService: OpenRouterService,
    private val cacheManager: AiCacheManager
) {
    companion object {
        private const val TAG = "BuddhaAiService"
    }

    /**
     * Checks if ANY AI provider is available (Gemini or OpenRouter).
     */
    fun isAnyAiAvailable(): Boolean {
        return geminiService.isConfigured() || openRouterService.isConfigured()
    }

    /**
     * Gets the name of the currently active AI provider.
     */
    fun getActiveProviderName(): String {
        return when {
            geminiService.isConfigured() -> "Gemini ${geminiService.getCurrentModel().displayName}"
            openRouterService.isConfigured() -> "OpenRouter"
            else -> "No AI Provider"
        }
    }

    // =========================================================================
    // Daily Wisdom (Cached for the day)
    // =========================================================================

    /**
     * Gets the daily wisdom, using cache if available.
     * Generates fresh wisdom only once per day.
     */
    suspend fun getDailyWisdom(): GeminiResult<String> = withContext(Dispatchers.IO) {
        // Check cache first
        val cached = cacheManager.getDailyWisdom()
        if (cached != null) {
            com.prody.prashant.util.AppLogger.d(TAG, "Returning cached daily wisdom")
            return@withContext GeminiResult.Success(cached)
        }

        // Generate fresh wisdom
        com.prody.prashant.util.AppLogger.d(TAG, "Generating fresh daily wisdom")
        val result = geminiService.generateDailyWisdom()

        // Cache successful results
        if (result is GeminiResult.Success) {
            cacheManager.cacheDailyWisdom(result.data)
        }

        result
    }

    // =========================================================================
    // Journal Prompts (Cached per mood, 1 hour TTL)
    // =========================================================================

    /**
     * Gets a journal prompt for the specified mood, using cache if available.
     */
    suspend fun getJournalPrompt(mood: Mood): GeminiResult<String> = withContext(Dispatchers.IO) {
        val moodKey = mood.name.lowercase()

        // Check cache first
        val cached = cacheManager.getJournalPrompt(moodKey)
        if (cached != null) {
            com.prody.prashant.util.AppLogger.d(TAG, "Returning cached journal prompt for $moodKey")
            return@withContext GeminiResult.Success(cached)
        }

        // Generate fresh prompt
        com.prody.prashant.util.AppLogger.d(TAG, "Generating fresh journal prompt for $moodKey")
        val result = geminiService.generateJournalPrompt(mood)

        // Cache successful results
        if (result is GeminiResult.Success) {
            cacheManager.cacheJournalPrompt(moodKey, result.data)
        }

        result
    }

    // =========================================================================
    // Journal Response (Not cached - personalized content)
    // =========================================================================

    /**
     * Generates a Buddha response for a journal entry.
     * Not cached as each response is highly personalized.
     * 
     * Uses Gemini as primary, falls back to OpenRouter if Gemini fails.
     */
    suspend fun getJournalResponse(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int
    ): GeminiResult<String> {
        // Try Gemini first if configured
        if (geminiService.isConfigured()) {
            val result = geminiService.generateJournalResponse(content, mood, moodIntensity, wordCount)
            if (result is GeminiResult.Success) {
                return result
            }
            // If Gemini fails but OpenRouter is available, try fallback
            if (openRouterService.isConfigured()) {
                com.prody.prashant.util.AppLogger.d(TAG, "Gemini failed, falling back to OpenRouter")
                return tryOpenRouterFallback(content, mood, moodIntensity, wordCount)
            }
            return result
        }
        
        // If Gemini not configured, try OpenRouter directly
        if (openRouterService.isConfigured()) {
            com.prody.prashant.util.AppLogger.d(TAG, "Gemini not configured, using OpenRouter")
            return tryOpenRouterFallback(content, mood, moodIntensity, wordCount)
        }
        
        return GeminiResult.ApiKeyNotSet
    }

    /**
     * Falls back to OpenRouter for journal responses.
     */
    private suspend fun tryOpenRouterFallback(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val result = openRouterService.generateJournalResponse(content, mood, moodIntensity, wordCount)
        result.fold(
            onSuccess = { GeminiResult.Success(it) },
            onFailure = { GeminiResult.Error(it as Exception, openRouterService.getErrorMessage(it as Exception)) }
        )
    }

    /**
     * Generates a streaming Buddha response for a journal entry.
     */
    fun getJournalResponseStream(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int
    ): Flow<GeminiResult<String>> {
        return geminiService.generateJournalResponseStream(content, mood, moodIntensity, wordCount)
    }

    // =========================================================================
    // Quote Explanation (Cached per quote, 7 days TTL)
    // =========================================================================

    /**
     * Gets an explanation for a quote, using cache if available.
     */
    suspend fun getQuoteExplanation(
        quoteId: Long,
        quote: String,
        author: String
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        // Check cache first
        val cached = cacheManager.getQuoteExplanation(quoteId)
        if (cached != null) {
            com.prody.prashant.util.AppLogger.d(TAG, "Returning cached quote explanation for ID $quoteId")
            return@withContext GeminiResult.Success(cached)
        }

        // Generate fresh explanation
        com.prody.prashant.util.AppLogger.d(TAG, "Generating fresh quote explanation for ID $quoteId")
        val result = geminiService.generateQuoteExplanation(quote, author)

        // Cache successful results
        if (result is GeminiResult.Success) {
            cacheManager.cacheQuoteExplanation(quoteId, result.data)
        }

        result
    }

    // =========================================================================
    // Vocabulary Context (Cached per word, 7 days TTL)
    // =========================================================================

    /**
     * Gets context and examples for a vocabulary word, using cache if available.
     */
    suspend fun getVocabularyContext(
        wordId: Long,
        word: String,
        definition: String
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        // Check cache first
        val cached = cacheManager.getVocabularyContext(wordId)
        if (cached != null) {
            com.prody.prashant.util.AppLogger.d(TAG, "Returning cached vocabulary context for ID $wordId")
            return@withContext GeminiResult.Success(cached)
        }

        // Generate fresh context
        com.prody.prashant.util.AppLogger.d(TAG, "Generating fresh vocabulary context for ID $wordId")
        val result = geminiService.generateVocabularyContext(word, definition)

        // Cache successful results
        if (result is GeminiResult.Success) {
            cacheManager.cacheVocabularyContext(wordId, result.data)
        }

        result
    }

    // =========================================================================
    // Streak Celebration (Cached per streak tier, daily TTL)
    // =========================================================================

    /**
     * Gets a streak celebration message, using cache if available.
     */
    suspend fun getStreakCelebration(
        streakCount: Int,
        previousBest: Int = 0
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        // Check cache first
        val cached = cacheManager.getStreakMessage(streakCount)
        if (cached != null) {
            com.prody.prashant.util.AppLogger.d(TAG, "Returning cached streak message for $streakCount days")
            return@withContext GeminiResult.Success(cached)
        }

        // Generate fresh celebration
        com.prody.prashant.util.AppLogger.d(TAG, "Generating fresh streak celebration for $streakCount days")
        val result = geminiService.generateStreakCelebration(streakCount, previousBest)

        // Cache successful results
        if (result is GeminiResult.Success) {
            cacheManager.cacheStreakMessage(streakCount, result.data)
        }

        result
    }

    // =========================================================================
    // Mood Insights (Cached per mood, 12 hours TTL)
    // =========================================================================

    /**
     * Gets mood pattern insights, using cache if available.
     */
    suspend fun getMoodInsight(
        dominantMood: Mood,
        moodDistribution: Map<String, Int>,
        journalCount: Int
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val moodKey = dominantMood.name.lowercase()

        // Check cache first
        val cached = cacheManager.getMoodInsight(moodKey)
        if (cached != null) {
            com.prody.prashant.util.AppLogger.d(TAG, "Returning cached mood insight for $moodKey")
            return@withContext GeminiResult.Success(cached)
        }

        // Generate fresh insight
        com.prody.prashant.util.AppLogger.d(TAG, "Generating fresh mood insight for $moodKey")
        val result = geminiService.generateMoodPatternInsight(dominantMood, moodDistribution, journalCount)

        // Cache successful results
        if (result is GeminiResult.Success) {
            cacheManager.cacheMoodInsight(moodKey, result.data)
        }

        result
    }

    // =========================================================================
    // Weekly Summary (Cached per week, daily TTL)
    // =========================================================================

    /**
     * Gets the weekly summary, using cache if available.
     */
    suspend fun getWeeklySummary(
        journalCount: Int,
        wordsLearned: Int,
        dominantMood: Mood?,
        streakDays: Int,
        daysActive: Int
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        // Check cache first
        val cached = cacheManager.getWeeklySummary()
        if (cached != null) {
            com.prody.prashant.util.AppLogger.d(TAG, "Returning cached weekly summary")
            return@withContext GeminiResult.Success(cached)
        }

        // Generate fresh summary
        com.prody.prashant.util.AppLogger.d(TAG, "Generating fresh weekly summary")
        val result = geminiService.generateWeeklySummary(
            journalCount,
            wordsLearned,
            dominantMood,
            streakDays,
            daysActive
        )

        // Cache successful results
        if (result is GeminiResult.Success) {
            cacheManager.cacheWeeklySummary(result.data)
        }

        result
    }

    // =========================================================================
    // Journal Analysis (Not cached - dynamic content)
    // =========================================================================

    /**
     * Analyzes journal entries and provides insights.
     * Not cached as analysis depends on current entries.
     */
    suspend fun analyzeJournalEntries(
        entries: List<String>,
        dateRange: String
    ): GeminiResult<String> {
        return geminiService.analyzeJournalEntries(entries, dateRange)
    }

    // =========================================================================
    // Morning/Evening Reflections (Not cached - fresh daily)
    // =========================================================================

    /**
     * Generates a morning reflection.
     * Could be cached but keeping fresh for variety.
     */
    suspend fun getMorningReflection(): GeminiResult<String> {
        return geminiService.generateMorningReflection()
    }

    /**
     * Generates an evening reflection.
     * Could be cached but keeping fresh for variety.
     */
    suspend fun getEveningReflection(): GeminiResult<String> {
        return geminiService.generateEveningReflection()
    }

    // =========================================================================
    // THE MIRROR - Receipt Responses (Not cached - personalized)
    // =========================================================================

    /**
     * Generates a Mirror response that compares current entry to past entries.
     * This is the core of "The Receipt" feature - brutal honesty about patterns.
     *
     * @param currentContent Current journal entry content
     * @param currentMood Current mood
     * @param currentDate Formatted current date
     * @param pastEntry Past entry for comparison (or null if no similar entry found)
     */
    suspend fun getMirrorResponse(
        currentContent: String,
        currentMood: String,
        currentDate: String,
        pastEntry: PastEntryContext?
    ): GeminiResult<String> {
        return geminiService.generateMirrorResponse(
            currentContent = currentContent,
            currentMood = currentMood,
            currentDate = currentDate,
            pastEntry = pastEntry
        )
    }

    /**
     * Streaming version of Mirror response.
     */
    fun getMirrorResponseStream(
        currentContent: String,
        currentMood: String,
        currentDate: String,
        pastEntry: PastEntryContext?
    ): Flow<GeminiResult<String>> {
        return geminiService.generateMirrorResponseStream(
            currentContent = currentContent,
            currentMood = currentMood,
            currentDate = currentDate,
            pastEntry = pastEntry
        )
    }

    // =========================================================================
    // Custom Responses (Not cached)
    // =========================================================================

    /**
     * Generates a custom Buddha response.
     */
    suspend fun getCustomResponse(
        prompt: String,
        includeSystemPrompt: Boolean = true
    ): GeminiResult<String> {
        return geminiService.generateCustomResponse(prompt, includeSystemPrompt)
    }

    /**
     * Generates a streaming custom Buddha response.
     */
    fun getCustomResponseStream(
        prompt: String,
        includeSystemPrompt: Boolean = true
    ): Flow<GeminiResult<String>> {
        return geminiService.generateCustomResponseStream(prompt, includeSystemPrompt)
    }

    // =========================================================================
    // Configuration & Status
    // =========================================================================

    /**
     * Checks if the AI service is properly configured.
     */
    fun isConfigured(): Boolean = geminiService.isConfigured()

    /**
     * Tests the API connection.
     */
    suspend fun testConnection(): GeminiResult<String> = geminiService.testConnection()

    /**
     * Gets the current model being used.
     */
    fun getCurrentModel(): GeminiModel = geminiService.getCurrentModel()

    /**
     * Initializes the service with specific settings.
     */
    fun initialize(apiKey: String, model: GeminiModel = GeminiModel.GEMINI_1_5_FLASH) {
        geminiService.initialize(apiKey, model)
    }

    // =========================================================================
    // Cache Management
    // =========================================================================

    /**
     * Clears all cached AI responses.
     * Useful for testing or when user wants fresh content.
     */
    suspend fun clearAllCache() {
        cacheManager.clearAllCache()
        com.prody.prashant.util.AppLogger.d(TAG, "Cleared all AI cache")
    }

    /**
     * Gets cache statistics for monitoring.
     */
    val cacheStats = cacheManager.cacheStats

    /**
     * Invalidates the daily wisdom cache, forcing fresh generation.
     */
    suspend fun invalidateDailyWisdomCache() {
        cacheManager.invalidateCacheType(com.prody.prashant.data.cache.AiCacheType.DAILY_WISDOM)
    }

    /**
     * Invalidates the weekly summary cache.
     */
    suspend fun invalidateWeeklySummaryCache() {
        cacheManager.invalidateCacheType(com.prody.prashant.data.cache.AiCacheType.WEEKLY_SUMMARY)
    }

    /**
     * Preloads common AI content for offline availability.
     * Call this during app initialization or when on WiFi.
     */
    suspend fun preloadCommonContent() = withContext(Dispatchers.IO) {
        com.prody.prashant.util.AppLogger.d(TAG, "Preloading common AI content...")

        // Preload daily wisdom
        if (cacheManager.getDailyWisdom() == null) {
            getDailyWisdom()
        }

        // Preload journal prompts for common moods
        listOf(Mood.HAPPY, Mood.SAD, Mood.ANXIOUS, Mood.CALM).forEach { mood ->
            if (cacheManager.getJournalPrompt(mood.name.lowercase()) == null) {
                getJournalPrompt(mood)
            }
        }

        com.prody.prashant.util.AppLogger.d(TAG, "Preloading complete")
    }
}
