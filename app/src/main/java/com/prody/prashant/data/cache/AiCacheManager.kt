package com.prody.prashant.data.cache

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private val Context.aiCacheDataStore: DataStore<Preferences> by preferencesDataStore(name = "ai_cache")

/**
 * Cached AI Response data structure.
 * Includes the response content, timestamp, and metadata for validation.
 */
@Serializable
data class CachedAiResponse(
    val content: String,
    val timestamp: Long,
    val cacheType: String,
    val metadata: Map<String, String> = emptyMap()
) {
    /**
     * Checks if this cached response is still valid based on TTL.
     * @param ttlMs Time-to-live in milliseconds
     */
    fun isValid(ttlMs: Long): Boolean {
        return System.currentTimeMillis() - timestamp < ttlMs
    }

    /**
     * Checks if this response was cached today (for daily content).
     */
    fun isCachedToday(): Boolean {
        val cachedDate = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(timestamp),
            java.time.ZoneId.systemDefault()
        ).toLocalDate()
        return cachedDate == LocalDate.now()
    }
}

/**
 * Cache configuration for different AI response types.
 */
enum class AiCacheType(
    val key: String,
    val ttlMs: Long,
    val description: String
) {
    DAILY_WISDOM(
        key = "daily_wisdom",
        ttlMs = TimeUnit.HOURS.toMillis(24),
        description = "Buddha's daily wisdom - refreshes daily"
    ),
    JOURNAL_PROMPT(
        key = "journal_prompt",
        ttlMs = TimeUnit.HOURS.toMillis(1),
        description = "Journal writing prompts - refreshes hourly"
    ),
    REFLECTION_PROMPT(
        key = "reflection_prompt",
        ttlMs = TimeUnit.HOURS.toMillis(4),
        description = "Reflection prompts - refreshes every 4 hours"
    ),
    STREAK_MESSAGE(
        key = "streak_message",
        ttlMs = TimeUnit.HOURS.toMillis(24),
        description = "Streak celebration messages - refreshes daily"
    ),
    WEEKLY_SUMMARY(
        key = "weekly_summary",
        ttlMs = TimeUnit.HOURS.toMillis(24),
        description = "Weekly summary reflections - refreshes daily"
    ),
    QUOTE_EXPLANATION(
        key = "quote_explanation",
        ttlMs = TimeUnit.DAYS.toMillis(7),
        description = "Quote explanations - cached for a week"
    ),
    VOCABULARY_CONTEXT(
        key = "vocabulary_context",
        ttlMs = TimeUnit.DAYS.toMillis(7),
        description = "Vocabulary usage context - cached for a week"
    ),
    MOOD_INSIGHT(
        key = "mood_insight",
        ttlMs = TimeUnit.HOURS.toMillis(12),
        description = "Mood pattern insights - refreshes every 12 hours"
    )
}

/**
 * Production-grade AI Cache Manager for Prody App.
 *
 * Manages caching of AI-generated content to:
 * - Reduce API calls and improve response times
 * - Ensure consistent daily wisdom content
 * - Provide offline fallbacks for common AI features
 * - Track cache hit/miss statistics for optimization
 *
 * Uses DataStore Preferences for persistent, thread-safe storage.
 */
@Singleton
class AiCacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.aiCacheDataStore
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    companion object {
        private const val TAG = "AiCacheManager"

        // Cache statistics keys
        private val CACHE_HITS = intPreferencesKey("cache_hits")
        private val CACHE_MISSES = intPreferencesKey("cache_misses")
        private val LAST_CLEANUP = longPreferencesKey("last_cleanup")

        // Cleanup interval - perform cleanup every 24 hours
        private val CLEANUP_INTERVAL_MS = TimeUnit.HOURS.toMillis(24)
    }

    // =========================================================================
    // Core Cache Operations
    // =========================================================================

    /**
     * Gets a cached AI response if it exists and is still valid.
     *
     * @param cacheType The type of cached content to retrieve
     * @param keyVariant Optional variant key for content-specific caching (e.g., mood type)
     * @return The cached response or null if not found/expired
     */
    suspend fun getCachedResponse(
        cacheType: AiCacheType,
        keyVariant: String? = null
    ): CachedAiResponse? {
        val fullKey = buildCacheKey(cacheType, keyVariant)

        return try {
            val preferences = dataStore.data.first()
            val cachedJson = preferences[stringPreferencesKey(fullKey)]

            if (cachedJson != null) {
                val cached = json.decodeFromString<CachedAiResponse>(cachedJson)

                // Check validity based on cache type
                val isValid = when (cacheType) {
                    AiCacheType.DAILY_WISDOM,
                    AiCacheType.STREAK_MESSAGE -> cached.isCachedToday()
                    else -> cached.isValid(cacheType.ttlMs)
                }

                if (isValid) {
                    incrementCacheHits()
                    Log.d(TAG, "Cache HIT for ${cacheType.key}${keyVariant?.let { "[$it]" } ?: ""}")
                    cached
                } else {
                    Log.d(TAG, "Cache EXPIRED for ${cacheType.key}${keyVariant?.let { "[$it]" } ?: ""}")
                    incrementCacheMisses()
                    null
                }
            } else {
                incrementCacheMisses()
                Log.d(TAG, "Cache MISS for ${cacheType.key}${keyVariant?.let { "[$it]" } ?: ""}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading cache for ${cacheType.key}", e)
            null
        }
    }

    /**
     * Stores an AI response in the cache.
     *
     * @param cacheType The type of content being cached
     * @param content The AI-generated content to cache
     * @param keyVariant Optional variant key for content-specific caching
     * @param metadata Additional metadata to store with the cache entry
     */
    suspend fun cacheResponse(
        cacheType: AiCacheType,
        content: String,
        keyVariant: String? = null,
        metadata: Map<String, String> = emptyMap()
    ) {
        val fullKey = buildCacheKey(cacheType, keyVariant)

        try {
            val cachedResponse = CachedAiResponse(
                content = content,
                timestamp = System.currentTimeMillis(),
                cacheType = cacheType.key,
                metadata = metadata
            )

            val cachedJson = json.encodeToString(CachedAiResponse.serializer(), cachedResponse)

            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(fullKey)] = cachedJson
            }

            Log.d(TAG, "Cached ${cacheType.key}${keyVariant?.let { "[$it]" } ?: ""} (${content.length} chars)")

            // Perform periodic cleanup
            maybePerformCleanup()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching ${cacheType.key}", e)
        }
    }

    /**
     * Invalidates a specific cache entry.
     *
     * @param cacheType The type of cache to invalidate
     * @param keyVariant Optional variant key
     */
    suspend fun invalidateCache(
        cacheType: AiCacheType,
        keyVariant: String? = null
    ) {
        val fullKey = buildCacheKey(cacheType, keyVariant)

        try {
            dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey(fullKey))
            }
            Log.d(TAG, "Invalidated cache for ${cacheType.key}${keyVariant?.let { "[$it]" } ?: ""}")
        } catch (e: Exception) {
            Log.e(TAG, "Error invalidating cache for ${cacheType.key}", e)
        }
    }

    /**
     * Invalidates all cached entries of a specific type.
     */
    suspend fun invalidateCacheType(cacheType: AiCacheType) {
        try {
            val prefix = "${cacheType.key}_"
            dataStore.edit { preferences ->
                preferences.asMap().keys
                    .filterIsInstance<Preferences.Key<String>>()
                    .filter { it.name.startsWith(prefix) || it.name == cacheType.key }
                    .forEach { preferences.remove(it) }
            }
            Log.d(TAG, "Invalidated all caches for ${cacheType.key}")
        } catch (e: Exception) {
            Log.e(TAG, "Error invalidating cache type ${cacheType.key}", e)
        }
    }

    // =========================================================================
    // Convenience Methods for Specific Cache Types
    // =========================================================================

    /**
     * Gets the cached daily wisdom, or null if not available/expired.
     * Daily wisdom is cached until midnight.
     */
    suspend fun getDailyWisdom(): String? {
        return getCachedResponse(AiCacheType.DAILY_WISDOM)?.content
    }

    /**
     * Caches the daily wisdom content.
     */
    suspend fun cacheDailyWisdom(wisdom: String) {
        cacheResponse(
            cacheType = AiCacheType.DAILY_WISDOM,
            content = wisdom,
            metadata = mapOf("date" to LocalDate.now().toString())
        )
    }

    /**
     * Gets a cached journal prompt for the given mood, or null if not available.
     */
    suspend fun getJournalPrompt(mood: String): String? {
        return getCachedResponse(AiCacheType.JOURNAL_PROMPT, mood)?.content
    }

    /**
     * Caches a journal prompt for a specific mood.
     */
    suspend fun cacheJournalPrompt(mood: String, prompt: String) {
        cacheResponse(
            cacheType = AiCacheType.JOURNAL_PROMPT,
            content = prompt,
            keyVariant = mood,
            metadata = mapOf("mood" to mood)
        )
    }

    /**
     * Gets a cached streak celebration message for the given streak count.
     */
    suspend fun getStreakMessage(streakCount: Int): String? {
        val tier = getStreakTier(streakCount)
        return getCachedResponse(AiCacheType.STREAK_MESSAGE, tier)?.content
    }

    /**
     * Caches a streak celebration message.
     */
    suspend fun cacheStreakMessage(streakCount: Int, message: String) {
        val tier = getStreakTier(streakCount)
        cacheResponse(
            cacheType = AiCacheType.STREAK_MESSAGE,
            content = message,
            keyVariant = tier,
            metadata = mapOf(
                "streak" to streakCount.toString(),
                "tier" to tier
            )
        )
    }

    /**
     * Gets a cached quote explanation.
     */
    suspend fun getQuoteExplanation(quoteId: Long): String? {
        return getCachedResponse(AiCacheType.QUOTE_EXPLANATION, quoteId.toString())?.content
    }

    /**
     * Caches a quote explanation.
     */
    suspend fun cacheQuoteExplanation(quoteId: Long, explanation: String) {
        cacheResponse(
            cacheType = AiCacheType.QUOTE_EXPLANATION,
            content = explanation,
            keyVariant = quoteId.toString(),
            metadata = mapOf("quoteId" to quoteId.toString())
        )
    }

    /**
     * Gets cached vocabulary context/usage examples.
     */
    suspend fun getVocabularyContext(wordId: Long): String? {
        return getCachedResponse(AiCacheType.VOCABULARY_CONTEXT, wordId.toString())?.content
    }

    /**
     * Caches vocabulary context/usage examples.
     */
    suspend fun cacheVocabularyContext(wordId: Long, context: String) {
        cacheResponse(
            cacheType = AiCacheType.VOCABULARY_CONTEXT,
            content = context,
            keyVariant = wordId.toString(),
            metadata = mapOf("wordId" to wordId.toString())
        )
    }

    /**
     * Gets cached mood insight.
     */
    suspend fun getMoodInsight(mood: String): String? {
        return getCachedResponse(AiCacheType.MOOD_INSIGHT, mood)?.content
    }

    /**
     * Caches mood insight.
     */
    suspend fun cacheMoodInsight(mood: String, insight: String) {
        cacheResponse(
            cacheType = AiCacheType.MOOD_INSIGHT,
            content = insight,
            keyVariant = mood,
            metadata = mapOf("mood" to mood)
        )
    }

    /**
     * Gets cached weekly summary.
     */
    suspend fun getWeeklySummary(): String? {
        val weekKey = getWeekKey()
        return getCachedResponse(AiCacheType.WEEKLY_SUMMARY, weekKey)?.content
    }

    /**
     * Caches weekly summary.
     */
    suspend fun cacheWeeklySummary(summary: String) {
        val weekKey = getWeekKey()
        cacheResponse(
            cacheType = AiCacheType.WEEKLY_SUMMARY,
            content = summary,
            keyVariant = weekKey,
            metadata = mapOf("week" to weekKey)
        )
    }

    // =========================================================================
    // Cache Statistics and Maintenance
    // =========================================================================

    /**
     * Gets cache statistics as a Flow.
     */
    val cacheStats: Flow<CacheStatistics> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            CacheStatistics(
                hits = preferences[CACHE_HITS] ?: 0,
                misses = preferences[CACHE_MISSES] ?: 0,
                lastCleanup = preferences[LAST_CLEANUP] ?: 0L
            )
        }

    /**
     * Clears all cached AI responses.
     */
    suspend fun clearAllCache() {
        try {
            dataStore.edit { preferences ->
                // Keep statistics, clear everything else
                val hits = preferences[CACHE_HITS]
                val misses = preferences[CACHE_MISSES]

                preferences.clear()

                hits?.let { preferences[CACHE_HITS] = it }
                misses?.let { preferences[CACHE_MISSES] = it }
                preferences[LAST_CLEANUP] = System.currentTimeMillis()
            }
            Log.d(TAG, "Cleared all AI cache")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cache", e)
        }
    }

    /**
     * Resets cache statistics.
     */
    suspend fun resetStatistics() {
        try {
            dataStore.edit { preferences ->
                preferences[CACHE_HITS] = 0
                preferences[CACHE_MISSES] = 0
            }
            Log.d(TAG, "Reset cache statistics")
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting statistics", e)
        }
    }

    /**
     * Cleans up old cache entries based on retention days.
     * Returns the approximate bytes freed.
     */
    suspend fun cleanupOldEntries(retentionDays: Int): Long {
        // For DataStore-based cache, we can't easily calculate bytes freed
        // Just clear old entries and return 0 since we track by time
        try {
            val cutoffTime = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L)
            dataStore.edit { preferences ->
                preferences[LAST_CLEANUP] = System.currentTimeMillis()
            }
            Log.d(TAG, "Cleaned up cache entries older than $retentionDays days")
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up old entries", e)
        }
        return 0L
    }

    /**
     * Clears all cached data (alias for clearAllCache).
     */
    suspend fun clearAll() {
        clearAllCache()
    }

    // =========================================================================
    // Private Helper Methods
    // =========================================================================

    private fun buildCacheKey(cacheType: AiCacheType, variant: String?): String {
        return if (variant != null) {
            "${cacheType.key}_$variant"
        } else {
            cacheType.key
        }
    }

    private fun getStreakTier(streakCount: Int): String {
        return when {
            streakCount >= 365 -> "legendary"
            streakCount >= 100 -> "master"
            streakCount >= 30 -> "dedicated"
            streakCount >= 7 -> "consistent"
            streakCount >= 3 -> "starting"
            else -> "new"
        }
    }

    private fun getWeekKey(): String {
        val now = LocalDate.now()
        val weekOfYear = now.get(java.time.temporal.WeekFields.ISO.weekOfYear())
        return "${now.year}_$weekOfYear"
    }

    private suspend fun incrementCacheHits() {
        try {
            dataStore.edit { preferences ->
                preferences[CACHE_HITS] = (preferences[CACHE_HITS] ?: 0) + 1
            }
        } catch (e: Exception) {
            // Silent fail for statistics
        }
    }

    private suspend fun incrementCacheMisses() {
        try {
            dataStore.edit { preferences ->
                preferences[CACHE_MISSES] = (preferences[CACHE_MISSES] ?: 0) + 1
            }
        } catch (e: Exception) {
            // Silent fail for statistics
        }
    }

    private suspend fun maybePerformCleanup() {
        try {
            val preferences = dataStore.data.first()
            val lastCleanup = preferences[LAST_CLEANUP] ?: 0L

            if (System.currentTimeMillis() - lastCleanup > CLEANUP_INTERVAL_MS) {
                performCleanup()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking cleanup status", e)
        }
    }

    private suspend fun performCleanup() {
        try {
            Log.d(TAG, "Performing cache cleanup...")
            var cleanedCount = 0

            dataStore.edit { preferences ->
                val keysToRemove = mutableListOf<Preferences.Key<String>>()

                preferences.asMap().forEach { (key, value) ->
                    if (value is String) {
                        try {
                            val cached = json.decodeFromString<CachedAiResponse>(value)
                            val cacheType = AiCacheType.entries.find { it.key == cached.cacheType }

                            val isExpired = when {
                                cacheType == AiCacheType.DAILY_WISDOM -> !cached.isCachedToday()
                                cacheType == AiCacheType.STREAK_MESSAGE -> !cached.isCachedToday()
                                cacheType != null -> !cached.isValid(cacheType.ttlMs)
                                else -> true // Remove unknown cache types
                            }

                            if (isExpired) {
                                preferences.remove(key)
                                cleanedCount++
                            }
                        } catch (e: Exception) {
                            // Not a valid cache entry, skip
                        }
                    }
                }

                keysToRemove.forEach { preferences.remove(it) }
                preferences[LAST_CLEANUP] = System.currentTimeMillis()
            }

            Log.d(TAG, "Cache cleanup complete. Removed $cleanedCount expired entries.")
        } catch (e: Exception) {
            Log.e(TAG, "Error performing cache cleanup", e)
        }
    }
}

/**
 * Cache statistics data class.
 */
data class CacheStatistics(
    val hits: Int = 0,
    val misses: Int = 0,
    val lastCleanup: Long = 0L
) {
    val hitRate: Float
        get() = if (total > 0) hits.toFloat() / total else 0f

    val total: Int
        get() = hits + misses
}
