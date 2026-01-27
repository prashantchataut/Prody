package com.prody.prashant.domain.gamification

/**
 * Seed states for the Bloom mechanic.
 *
 * The daily Seed progresses through three states:
 * - PLANTED: Shown to user, waiting for engagement
 * - GROWING: User engaged with it (viewed detail, saved)
 * - BLOOMED: User applied it (used in journal/message)
 */
enum class SeedState {
    PLANTED,
    GROWING,
    BLOOMED
}

/**
 * Types of seeds that can be assigned daily.
 */
enum class SeedType(val displayName: String) {
    WORD("Word of the Day"),
    QUOTE("Quote"),
    PROVERB("Proverb"),
    PHRASE("Phrase")
}

/**
 * Represents a daily seed with its content and state.
 */
data class DailySeed(
    val id: Long,
    val date: Long,
    val type: SeedType,
    val content: String,
    val source: String,
    val sourceId: Long?,
    val state: SeedState,
    val bloomedAt: Long?,
    val bloomedIn: String?,
    val variations: List<String>,
    val keywords: List<String>,
    val keyPhrase: String?
) {
    /**
     * Check if content contains this seed.
     */
    fun matchesContent(text: String): Boolean {
        val normalizedContent = text.lowercase()

        return when (type) {
            SeedType.WORD -> {
                // Check for word or common variations
                val word = content.lowercase()
                normalizedContent.contains(word) ||
                        variations.any { normalizedContent.contains(it.lowercase()) }
            }
            SeedType.QUOTE -> {
                // Check for key phrase from quote
                keyPhrase?.let { normalizedContent.contains(it.lowercase()) } ?: false
            }
            SeedType.PROVERB -> {
                // Check for core concept keywords
                keywords.any { normalizedContent.contains(it.lowercase()) }
            }
            SeedType.PHRASE -> {
                normalizedContent.contains(content.lowercase())
            }
        }
    }

    /**
     * Check if this seed is currently active (today's seed, not yet bloomed).
     */
    val isActive: Boolean get() = state != SeedState.BLOOMED

    /**
     * Check if this seed has been engaged with (viewed, saved).
     */
    val isGrowing: Boolean get() = state == SeedState.GROWING

    /**
     * Check if this seed has been applied (used in writing).
     */
    val hasBloomed: Boolean get() = state == SeedState.BLOOMED

    companion object {
        /**
         * Extracts word variations for matching.
         */
        fun generateWordVariations(word: String): List<String> {
            val base = word.lowercase()
            return listOf(
                base,
                "${base}s",          // plural
                base.removeSuffix("e") + "ed",         // past tense
                "${base}ing",        // gerund
                "${base}ly",         // adverb
                "${base}ness",       // noun form
                base.removeSuffix("e") + "ing",  // e.g., hope -> hoping
                base.removeSuffix("y") + "ies",  // e.g., happy -> happies
                base.removeSuffix("y") + "ied",  // e.g., happy -> happied
                base.removeSuffix("y") + "ily"   // e.g., happy -> happily
            ).distinct()
        }

        /**
         * Extracts key phrases from a quote.
         */
        fun extractKeyPhrase(quote: String): String? {
            // Find meaningful phrase (typically 3-5 words)
            val words = quote.split(" ", ",", ".", ";", ":")
                .map { it.trim().lowercase() }
                .filter { it.length >= 3 && !COMMON_WORDS.contains(it) }

            // Return first meaningful sequence or null
            return if (words.size >= 2) {
                words.take(3).joinToString(" ")
            } else words.firstOrNull()
        }

        /**
         * Extracts keywords from proverb content.
         */
        fun extractKeywords(proverb: String): List<String> {
            val meaningfulWords = listOf(
                "courage", "wisdom", "strength", "patience", "resilience",
                "gratitude", "peace", "growth", "change", "hope",
                "love", "kindness", "truth", "faith", "power",
                "journey", "purpose", "mind", "heart", "soul",
                "action", "thought", "present", "future", "moment",
                "light", "path", "time", "learn", "teach",
                "begin", "end", "fall", "rise", "persevere"
            )

            val lowerContent = proverb.lowercase()
            return meaningfulWords.filter { lowerContent.contains(it) }
        }

        private val COMMON_WORDS = setOf(
            "about", "after", "again", "being", "could", "every",
            "first", "found", "great", "their", "there", "these",
            "thing", "think", "those", "under", "water", "where",
            "which", "while", "world", "would", "write", "years",
            "that", "this", "with", "have", "from", "will",
            "been", "were", "they", "what", "when", "your"
        )
    }
}

/**
 * Summary of a user's bloom history.
 */
data class BloomSummary(
    val totalSeeds: Int,
    val totalBloomed: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastBloomDate: Long?,
    val bloomRate: Float,
    val thisMonthBlooms: Int
) {
    val hasAnyBlooms: Boolean get() = totalBloomed > 0
    val bloomPercentage: Int get() = (bloomRate * 100).toInt()
    val isOnStreak: Boolean get() = currentStreak > 0

    companion object {
        fun empty(): BloomSummary = BloomSummary(
            totalSeeds = 0,
            totalBloomed = 0,
            currentStreak = 0,
            longestStreak = 0,
            lastBloomDate = null,
            bloomRate = 0f,
            thisMonthBlooms = 0
        )
    }
}

/**
 * Result of attempting to bloom a seed.
 */
sealed class BloomAttemptResult {
    /**
     * Seed successfully bloomed.
     */
    data class Success(
        val seedContent: String,
        val pointsAwarded: Int,
        val tokensAwarded: Int,
        val newBloomStreak: Int,
        val isStreakMilestone: Boolean
    ) : BloomAttemptResult()

    /**
     * Seed was already bloomed today.
     */
    object AlreadyBloomed : BloomAttemptResult()

    /**
     * Content didn't match the seed.
     */
    object NoMatch : BloomAttemptResult()

    /**
     * No seed available for today.
     */
    object NoSeedAvailable : BloomAttemptResult()

    /**
     * Error during bloom attempt.
     */
    data class Error(val message: String) : BloomAttemptResult()
}

/**
 * Seed engagement action types.
 */
enum class SeedEngagementAction {
    VIEW_DETAIL,
    SAVE_TO_COLLECTION,
    USE_IN_JOURNAL,
    USE_IN_FUTURE_MESSAGE
}
