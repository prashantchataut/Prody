package com.prody.prashant.domain.gamification

import android.util.Log
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.domain.progress.BloomResult
import com.prody.prashant.domain.progress.SeedBloomService
import com.prody.prashant.domain.vocabulary.VocabularyDetector
import com.prody.prashant.domain.vocabulary.WordUsage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Context Bloom Service - The heart of "real gamification"
 *
 * Instead of boring fill-in-the-blank exercises, Context Bloom proves actual vocabulary
 * usage by detecting when users naturally use learned words in their journal entries.
 *
 * This creates a powerful feedback loop:
 * - User learns a word (Word of the Day, flashcards, etc.)
 * - User writes in their journal
 * - System detects when learned words are used naturally
 * - User gets rewarded with "Bloom" event (XP boost, visual celebration)
 * - User is motivated to use more vocabulary naturally
 *
 * Philosophy: Knowledge isn't proven by tests - it's proven by application.
 */
@Singleton
class ContextBloomService @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    private val vocabularyDetector: VocabularyDetector,
    private val seedBloomService: SeedBloomService,
    private val gameSkillSystem: GameSkillSystem
) {
    companion object {
        private const val TAG = "ContextBloomService"

        // XP rewards for Context Bloom
        const val BASE_BLOOM_XP = 25
        const val WORD_OF_DAY_BONUS_XP = 15
        const val LEARNED_VOCABULARY_XP_PER_WORD = 10
        const val MAX_VOCABULARY_XP_PER_ENTRY = 50 // Cap to prevent gaming
        const val FIRST_BLOOM_BONUS_XP = 20 // First time using a word in context

        // Token rewards
        const val BLOOM_TOKENS = 5
        const val MULTI_WORD_BONUS_TOKENS = 10 // Using 3+ learned words
    }

    // State for UI to observe bloom events
    private val _lastBloomEvent = MutableStateFlow<ContextBloomEvent?>(null)
    val lastBloomEvent: StateFlow<ContextBloomEvent?> = _lastBloomEvent.asStateFlow()

    /**
     * Analyze journal content for Context Bloom opportunities.
     *
     * This is the main entry point - called when a journal entry is saved.
     * Detects:
     * 1. Daily Seed usage (Word of the Day, Quote, Proverb)
     * 2. Previously learned vocabulary words
     *
     * @param content The journal entry text
     * @param entryId The journal entry ID for tracking
     * @return ContextBloomResult with all detected blooms and rewards
     */
    suspend fun analyzeForBloom(
        content: String,
        entryId: Long
    ): ContextBloomResult {
        if (content.isBlank()) {
            return ContextBloomResult.NoContent
        }

        val idempotencyKey = "context_bloom_${entryId}"
        var totalXp = 0
        var totalTokens = 0
        val bloomedWords = mutableListOf<BloomedWord>()
        var seedBloomed = false
        var seedContent: String? = null

        // 1. Check for Daily Seed bloom (Word of the Day, Quote, Proverb)
        val seedResult = seedBloomService.checkAndBloom(content, "journal", entryId)
        if (seedResult is BloomResult.Bloomed) {
            seedBloomed = true
            seedContent = seedResult.seedContent
            totalXp += BASE_BLOOM_XP + WORD_OF_DAY_BONUS_XP
            totalTokens += BLOOM_TOKENS
            Log.d(TAG, "Daily seed bloomed: ${seedResult.seedContent}")
        }

        // 2. Detect learned vocabulary usage
        val learnedWords = vocabularyDao.getLearnedWordsSync()
        if (learnedWords.isNotEmpty()) {
            val detectedUsages = vocabularyDetector.detectLearnedWords(content, learnedWords)

            if (detectedUsages.isNotEmpty()) {
                Log.d(TAG, "Detected ${detectedUsages.size} learned words in context")

                // Process each detected word
                for (usage in detectedUsages) {
                    // Check if this is the first time user used this word in context
                    val isFirstBloom = !vocabularyDao.hasBloomedInContext(usage.word.id)

                    val wordXp = if (isFirstBloom) {
                        LEARNED_VOCABULARY_XP_PER_WORD + FIRST_BLOOM_BONUS_XP
                    } else {
                        LEARNED_VOCABULARY_XP_PER_WORD
                    }

                    bloomedWords.add(
                        BloomedWord(
                            word = usage.word.word,
                            matchedForm = usage.matchedForm,
                            sentence = usage.usedIn,
                            position = usage.position,
                            xpEarned = wordXp,
                            isFirstBloom = isFirstBloom
                        )
                    )

                    // Mark word as bloomed in context
                    vocabularyDao.markBloomedInContext(usage.word.id, entryId)
                }

                // Calculate vocabulary XP (capped)
                val vocabularyXp = bloomedWords.sumOf { it.xpEarned }.coerceAtMost(MAX_VOCABULARY_XP_PER_ENTRY)
                totalXp += vocabularyXp

                // Bonus for using multiple words
                if (bloomedWords.size >= 3) {
                    totalTokens += MULTI_WORD_BONUS_TOKENS
                    Log.d(TAG, "Multi-word bonus awarded for ${bloomedWords.size} words")
                }
            }
        }

        // If we have any blooms, award XP
        if (totalXp > 0) {
            val xpResult = gameSkillSystem.awardSkillXp(
                skillType = GameSkillSystem.SkillType.DISCIPLINE,
                baseXp = totalXp,
                idempotencyKey = idempotencyKey
            )

            val actualXpAwarded = when (xpResult) {
                is SkillXpResult.Success -> xpResult.xpAwarded
                is SkillXpResult.DailyCapReached -> 0
                is SkillXpResult.AlreadyAwarded -> 0
                is SkillXpResult.Error -> 0
            }

            // Create and emit the bloom event for UI
            val event = ContextBloomEvent(
                entryId = entryId,
                seedBloomed = seedBloomed,
                seedContent = seedContent,
                bloomedWords = bloomedWords,
                totalXpAwarded = actualXpAwarded,
                totalTokensAwarded = totalTokens,
                timestamp = System.currentTimeMillis()
            )
            _lastBloomEvent.value = event

            Log.d(TAG, "Context Bloom complete: ${bloomedWords.size} words, seed=$seedBloomed, XP=$actualXpAwarded")

            return ContextBloomResult.Success(
                bloomedWords = bloomedWords,
                seedBloomed = seedBloomed,
                seedContent = seedContent,
                xpAwarded = actualXpAwarded,
                tokensAwarded = totalTokens
            )
        }

        return ContextBloomResult.NoBloom
    }

    /**
     * Clear the last bloom event (called after UI has shown the celebration).
     */
    fun clearBloomEvent() {
        _lastBloomEvent.value = null
    }

    /**
     * Get bloom statistics for the user.
     */
    suspend fun getBloomStats(): BloomStats {
        val totalBlooms = vocabularyDao.getTotalBloomCount()
        val uniqueWordsBloomed = vocabularyDao.getUniqueWordsBloomed()
        val bloomStreak = seedBloomService.getBloomSummary().currentStreak

        return BloomStats(
            totalBlooms = totalBlooms,
            uniqueWordsBloomed = uniqueWordsBloomed,
            currentBloomStreak = bloomStreak
        )
    }
}

/**
 * Result of analyzing content for Context Bloom.
 */
sealed class ContextBloomResult {
    /** Content was empty or blank */
    object NoContent : ContextBloomResult()

    /** No blooms detected */
    object NoBloom : ContextBloomResult()

    /** One or more blooms detected and rewarded */
    data class Success(
        val bloomedWords: List<BloomedWord>,
        val seedBloomed: Boolean,
        val seedContent: String?,
        val xpAwarded: Int,
        val tokensAwarded: Int
    ) : ContextBloomResult() {
        val totalBlooms: Int get() = bloomedWords.size + (if (seedBloomed) 1 else 0)
        val hasAnyBlooms: Boolean get() = totalBlooms > 0
    }
}

/**
 * Represents a word that "bloomed" (was used in context).
 */
data class BloomedWord(
    val word: String,
    val matchedForm: String,
    val sentence: String,
    val position: IntRange,
    val xpEarned: Int,
    val isFirstBloom: Boolean
)

/**
 * Event emitted when a Context Bloom occurs, for UI to display.
 */
data class ContextBloomEvent(
    val entryId: Long,
    val seedBloomed: Boolean,
    val seedContent: String?,
    val bloomedWords: List<BloomedWord>,
    val totalXpAwarded: Int,
    val totalTokensAwarded: Int,
    val timestamp: Long
) {
    val hasAnyBlooms: Boolean get() = seedBloomed || bloomedWords.isNotEmpty()

    /**
     * Get the primary message for the bloom notification.
     */
    fun getNotificationMessage(): String {
        return when {
            seedBloomed && bloomedWords.isNotEmpty() -> {
                "You used '$seedContent' and ${bloomedWords.size} other words perfectly. Knowledge applied!"
            }
            seedBloomed -> {
                "You used '$seedContent' perfectly. Knowledge applied!"
            }
            bloomedWords.size == 1 -> {
                "You used '${bloomedWords.first().word}' perfectly. Knowledge applied!"
            }
            bloomedWords.isNotEmpty() -> {
                "You used ${bloomedWords.size} learned words naturally. Knowledge applied!"
            }
            else -> ""
        }
    }
}

/**
 * Statistics about user's bloom history.
 */
data class BloomStats(
    val totalBlooms: Int,
    val uniqueWordsBloomed: Int,
    val currentBloomStreak: Int
)
