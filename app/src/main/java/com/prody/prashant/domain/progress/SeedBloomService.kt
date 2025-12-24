package com.prody.prashant.domain.progress

import android.util.Log
import com.prody.prashant.data.local.dao.SeedDao
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.dao.QuoteDao
import com.prody.prashant.data.local.dao.ProverbDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.data.local.entity.SeedBloomSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seed -> Bloom Service
 *
 * Manages the daily "Seed" mechanic:
 * - Every day, surfaces ONE seed (word/proverb/idea) from content library
 * - Seed starts as "Unapplied"
 * - It "Blooms" when used in journal entry OR future message
 * - Bloom triggers token reward and visual marker
 *
 * This makes wisdom actionable and creates a unique progression loop.
 */
@Singleton
class SeedBloomService @Inject constructor(
    private val seedDao: SeedDao,
    private val vocabularyDao: VocabularyDao,
    private val quoteDao: QuoteDao,
    private val proverbDao: ProverbDao,
    private val userDao: UserDao
) {
    companion object {
        private const val TAG = "SeedBloomService"
        private const val BLOOM_REWARD_POINTS = 25
    }

    /**
     * Get today's seed. Creates one if it doesn't exist.
     */
    suspend fun getTodaySeed(): SeedEntity {
        val todayStart = getTodayStartTimestamp()

        // Check if we already have a seed for today
        seedDao.getSeedForDate(todayStart)?.let { return it }

        // Create a new seed for today
        return createDailySeed(todayStart)
    }

    /**
     * Observe today's seed reactively.
     */
    fun observeTodaySeed(): Flow<SeedEntity?> {
        val todayStart = getTodayStartTimestamp()
        return seedDao.observeSeedForDate(todayStart)
    }

    /**
     * Check if content contains today's seed and mark it as bloomed if so.
     * Returns true if the seed bloomed (first time today).
     */
    suspend fun checkAndBloom(
        content: String,
        bloomSource: String, // "journal" or "future_message"
        entryId: Long? = null
    ): BloomResult {
        return try {
            val todaySeed = getTodaySeed()

            // Already bloomed today? Skip
            if (todaySeed.hasBloomedToday) {
                return BloomResult.AlreadyBloomed
            }

            // Check if content contains the seed
            if (!todaySeed.matchesContent(content)) {
                return BloomResult.NoMatch
            }

            // Mark as bloomed
            seedDao.markSeedAsBloomed(
                seedId = todaySeed.id,
                bloomedIn = bloomSource,
                entryId = entryId
            )

            // Award points
            userDao.addPoints(BLOOM_REWARD_POINTS)
            seedDao.markRewardClaimed(todaySeed.id)

            Log.d(TAG, "Seed '${todaySeed.seedContent}' bloomed in $bloomSource!")

            BloomResult.Bloomed(
                seedContent = todaySeed.seedContent,
                pointsAwarded = BLOOM_REWARD_POINTS
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error checking bloom", e)
            BloomResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Get bloom summary for profile/stats display.
     */
    suspend fun getBloomSummary(): SeedBloomSummary {
        return try {
            val seeds = seedDao.getAllSeeds().first()
            SeedBloomSummary.calculate(seeds)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bloom summary", e)
            SeedBloomSummary()
        }
    }

    /**
     * Get recent seeds for display.
     */
    fun getRecentSeeds(limit: Int = 7): Flow<List<SeedEntity>> {
        return seedDao.getRecentSeeds(limit)
    }

    /**
     * Create a new daily seed from content library.
     */
    private suspend fun createDailySeed(todayStart: Long): SeedEntity {
        // Rotate through seed types for variety
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val seedType = when (dayOfYear % 3) {
            0 -> createWordSeed(todayStart)
            1 -> createProverbSeed(todayStart)
            else -> createQuoteSeed(todayStart)
        }

        // If we got a valid seed, insert and return it
        if (seedType != null) {
            seedDao.insertSeed(seedType)
            return seedType
        }

        // Fallback to a generic wisdom seed
        val fallbackSeed = SeedEntity(
            date = todayStart,
            seedType = "idea",
            seedContent = getFallbackSeedContent(dayOfYear),
            seedSource = "curated"
        )
        seedDao.insertSeed(fallbackSeed)
        return fallbackSeed
    }

    private suspend fun createWordSeed(todayStart: Long): SeedEntity? {
        return try {
            // Get a word that hasn't been learned yet (for extra motivation)
            val word = vocabularyDao.getWordOfTheDay()
                ?: vocabularyDao.getRandomUnlearnedWord()

            word?.let {
                SeedEntity(
                    date = todayStart,
                    seedType = "word",
                    seedContent = it.word,
                    seedSource = "vocabulary",
                    sourceId = it.id
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to create word seed", e)
            null
        }
    }

    private suspend fun createProverbSeed(todayStart: Long): SeedEntity? {
        return try {
            val proverb = proverbDao.getProverbOfTheDay()

            proverb?.let {
                // Extract a key phrase from the proverb
                val keyPhrase = extractKeyPhrase(it.content)
                SeedEntity(
                    date = todayStart,
                    seedType = "proverb",
                    seedContent = keyPhrase,
                    seedSource = "proverbs",
                    sourceId = it.id
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to create proverb seed", e)
            null
        }
    }

    private suspend fun createQuoteSeed(todayStart: Long): SeedEntity? {
        return try {
            val quote = quoteDao.getQuoteOfTheDay()

            quote?.let {
                // Extract a key concept from the quote
                val keyConcept = extractKeyPhrase(it.content)
                SeedEntity(
                    date = todayStart,
                    seedType = "quote",
                    seedContent = keyConcept,
                    seedSource = "quotes",
                    sourceId = it.id
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to create quote seed", e)
            null
        }
    }

    /**
     * Extract a key phrase (usually 1-2 meaningful words) from longer content.
     */
    private fun extractKeyPhrase(content: String): String {
        // List of meaningful words to look for
        val meaningfulWords = listOf(
            "courage", "wisdom", "strength", "patience", "resilience",
            "gratitude", "peace", "growth", "change", "hope",
            "love", "kindness", "truth", "faith", "power",
            "journey", "purpose", "mind", "heart", "soul",
            "action", "thought", "present", "future", "moment"
        )

        val lowerContent = content.lowercase()
        for (word in meaningfulWords) {
            if (lowerContent.contains(word)) {
                return word
            }
        }

        // Fallback: get the longest meaningful word from content
        val words = content.split(" ", ",", ".", ";", ":")
            .map { it.trim().lowercase() }
            .filter { it.length >= 5 && !commonWords.contains(it) }
            .sortedByDescending { it.length }

        return words.firstOrNull() ?: content.split(" ").first()
    }

    private val commonWords = setOf(
        "about", "after", "again", "being", "could", "every",
        "first", "found", "great", "their", "there", "these",
        "thing", "think", "those", "under", "water", "where",
        "which", "while", "world", "would", "write", "years"
    )

    private fun getFallbackSeedContent(dayOfYear: Int): String {
        val fallbackSeeds = listOf(
            "resilience", "gratitude", "presence", "growth", "wisdom",
            "patience", "courage", "kindness", "purpose", "reflection"
        )
        return fallbackSeeds[dayOfYear % fallbackSeeds.size]
    }

    private fun getTodayStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

/**
 * Result of attempting to bloom a seed.
 */
sealed class BloomResult {
    data class Bloomed(val seedContent: String, val pointsAwarded: Int) : BloomResult()
    object AlreadyBloomed : BloomResult()
    object NoMatch : BloomResult()
    data class Error(val message: String) : BloomResult()
}
