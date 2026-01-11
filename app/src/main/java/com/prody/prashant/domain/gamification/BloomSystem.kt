package com.prody.prashant.domain.gamification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Enhanced Bloom System - Wisdom Detection & Application
 *
 * Philosophy: "Plant a seed of wisdom, let it grow through engagement,
 * bloom it by applying it in your own words."
 *
 * The Bloom system creates a daily cycle of:
 * 1. SEED: Daily wisdom (word/quote/proverb) is presented
 * 2. GROW: User engages with the wisdom (reads, saves, reflects)
 * 3. BLOOM: User applies the wisdom in their own writing
 *
 * Detection Methods:
 * - Direct match: The exact word/phrase appears in user's writing
 * - Variation match: Grammatical variations (plurals, tenses)
 * - Semantic match: Related concepts and synonyms
 * - Theme match: Core themes of quotes/proverbs
 *
 * Rewards:
 * - XP to Discipline skill (primary)
 * - Bonus XP for streak maintenance
 * - Tokens for vocabulary mastery
 */

/**
 * Enhanced seed types with detection configurations.
 */
enum class WisdomType(
    val displayName: String,
    val description: String,
    val baseXp: Int,
    val streakBonusXp: Int,
    val detectionDifficulty: DetectionDifficulty
) {
    WORD_OF_DAY(
        "Word of the Day",
        "Expand your vocabulary with a new word",
        25,
        5,
        DetectionDifficulty.EASY
    ),
    IDIOM(
        "Idiom",
        "Master common English expressions",
        30,
        5,
        DetectionDifficulty.MEDIUM
    ),
    PROVERB(
        "Proverb",
        "Ancient wisdom for modern life",
        35,
        10,
        DetectionDifficulty.HARD
    ),
    QUOTE(
        "Quote",
        "Wisdom from notable figures",
        30,
        5,
        DetectionDifficulty.MEDIUM
    ),
    SEED(
        "Seed",
        "A thought to plant and grow",
        35,
        10,
        DetectionDifficulty.MEDIUM
    )
}

/**
 * How difficult it is to detect this wisdom type in text.
 */
enum class DetectionDifficulty {
    EASY,   // Direct word matching
    MEDIUM, // Requires variation/synonym matching
    HARD    // Requires semantic/theme matching
}

/**
 * A piece of wisdom that can be bloomed.
 */
data class WisdomSeed(
    val id: String,
    val type: WisdomType,
    val content: String,              // The word, phrase, or quote
    val meaning: String,              // Definition or explanation
    val example: String?,             // Example usage
    val source: String?,              // Author/origin
    val assignedDate: LocalDate,

    // Detection configuration
    val primaryTerms: List<String>,   // Main terms to detect (must match at least one)
    val synonyms: List<String>,       // Acceptable synonyms
    val relatedConcepts: List<String>, // Related themes/concepts
    val variations: List<String>,     // Grammatical variations

    // State
    val state: BloomState = BloomState.PLANTED,
    val engagedAt: LocalDateTime? = null,
    val bloomedAt: LocalDateTime? = null,
    val bloomContext: BloomContext? = null
) {
    /**
     * Check if content can bloom this seed.
     */
    fun canBloomWith(text: String): BloomMatchResult {
        val normalizedText = text.lowercase()
        val words = normalizedText.split(Regex("\\W+")).filter { it.isNotBlank() }

        // Check primary terms (required for EASY difficulty)
        val primaryMatch = primaryTerms.any { term ->
            val normalizedTerm = term.lowercase()
            normalizedText.contains(normalizedTerm) ||
            words.any { word -> word == normalizedTerm || word.startsWith(normalizedTerm) }
        }

        if (primaryMatch) {
            return BloomMatchResult.DirectMatch(
                matchedTerm = primaryTerms.first { normalizedText.contains(it.lowercase()) },
                confidence = 1.0f
            )
        }

        // Check variations
        val variationMatch = variations.firstOrNull { variation ->
            normalizedText.contains(variation.lowercase())
        }
        if (variationMatch != null) {
            return BloomMatchResult.VariationMatch(
                originalTerm = primaryTerms.firstOrNull() ?: content,
                matchedVariation = variationMatch,
                confidence = 0.9f
            )
        }

        // Check synonyms (for MEDIUM+ difficulty)
        if (type.detectionDifficulty != DetectionDifficulty.EASY) {
            val synonymMatch = synonyms.firstOrNull { synonym ->
                normalizedText.contains(synonym.lowercase())
            }
            if (synonymMatch != null) {
                return BloomMatchResult.SynonymMatch(
                    originalTerm = primaryTerms.firstOrNull() ?: content,
                    matchedSynonym = synonymMatch,
                    confidence = 0.8f
                )
            }
        }

        // Check related concepts (for HARD difficulty)
        if (type.detectionDifficulty == DetectionDifficulty.HARD) {
            val conceptMatches = relatedConcepts.filter { concept ->
                normalizedText.contains(concept.lowercase())
            }
            if (conceptMatches.size >= 2) {
                return BloomMatchResult.ConceptMatch(
                    matchedConcepts = conceptMatches,
                    confidence = 0.7f
                )
            }
        }

        return BloomMatchResult.NoMatch
    }

    /**
     * Calculate XP reward for blooming this seed.
     */
    fun calculateXpReward(
        bloomStreak: Int,
        disciplineLevel: Int
    ): Int {
        var xp = type.baseXp

        // Streak bonus (increases with streak length)
        if (bloomStreak > 0) {
            val streakMultiplier = minOf(bloomStreak, 7) * type.streakBonusXp / 7f
            xp += streakMultiplier.toInt()
        }

        // Difficulty bonus
        xp += when (type.detectionDifficulty) {
            DetectionDifficulty.EASY -> 0
            DetectionDifficulty.MEDIUM -> 5
            DetectionDifficulty.HARD -> 15
        }

        return xp
    }
}

/**
 * States a wisdom seed can be in.
 */
enum class BloomState {
    PLANTED,    // Assigned to user, not yet engaged
    GROWING,    // User has engaged (viewed, saved)
    BLOOMED,    // User successfully applied in their writing
    WILTED      // Day passed without blooming (for analytics)
}

/**
 * Context of how a seed was bloomed.
 */
data class BloomContext(
    val sourceType: BloomSourceType,
    val sourceId: String?,
    val matchedText: String,
    val matchType: String,
    val confidence: Float
)

/**
 * Where the bloom occurred.
 */
enum class BloomSourceType {
    JOURNAL_ENTRY,
    FUTURE_MESSAGE,
    MICRO_ENTRY,
    REPLY_TO_PAST
}

/**
 * Result of checking if content matches a seed.
 */
sealed class BloomMatchResult {
    abstract val confidence: Float

    /**
     * Direct match of primary term.
     */
    data class DirectMatch(
        val matchedTerm: String,
        override val confidence: Float
    ) : BloomMatchResult()

    /**
     * Matched a grammatical variation.
     */
    data class VariationMatch(
        val originalTerm: String,
        val matchedVariation: String,
        override val confidence: Float
    ) : BloomMatchResult()

    /**
     * Matched a synonym.
     */
    data class SynonymMatch(
        val originalTerm: String,
        val matchedSynonym: String,
        override val confidence: Float
    ) : BloomMatchResult()

    /**
     * Matched related concepts.
     */
    data class ConceptMatch(
        val matchedConcepts: List<String>,
        override val confidence: Float
    ) : BloomMatchResult()

    /**
     * No match found.
     */
    object NoMatch : BloomMatchResult() {
        override val confidence: Float = 0f
    }
}

/**
 * Result of attempting to bloom.
 */
sealed class BloomResult {
    /**
     * Successfully bloomed.
     */
    data class Success(
        val seed: WisdomSeed,
        val matchResult: BloomMatchResult,
        val xpEarned: Int,
        val newBloomStreak: Int,
        val isStreakMilestone: Boolean,
        val milestoneReached: BloomMilestone?,
        val celebrationMessage: String
    ) : BloomResult()

    /**
     * Already bloomed today.
     */
    data class AlreadyBloomed(
        val seed: WisdomSeed,
        val bloomedAt: LocalDateTime
    ) : BloomResult()

    /**
     * Content didn't match seed.
     */
    data class NoMatch(
        val seed: WisdomSeed,
        val suggestion: String
    ) : BloomResult()

    /**
     * No active seed for today.
     */
    object NoActiveSeed : BloomResult()

    /**
     * Error during bloom attempt.
     */
    data class Error(val message: String) : BloomResult()
}

/**
 * Bloom streak milestones.
 */
enum class BloomMilestone(
    val days: Int,
    val title: String,
    val description: String,
    val bonusXp: Int
) {
    FIRST_BLOOM(1, "First Bloom", "You bloomed your first seed!", 10),
    WEEK_GARDENER(7, "Week Gardener", "7 days of consistent growth!", 50),
    FORTNIGHT_FARMER(14, "Fortnight Farmer", "Two weeks of wisdom applied!", 100),
    MONTH_CULTIVATOR(30, "Month Cultivator", "A full month of blooming!", 200),
    SEASON_SAGE(90, "Season Sage", "Three months of dedicated growth!", 500),
    YEAR_MASTER(365, "Year Master", "A year of wisdom mastered!", 1000);

    companion object {
        fun forStreak(days: Int): BloomMilestone? = entries.find { it.days == days }
        fun getNextMilestone(currentStreak: Int): BloomMilestone? =
            entries.firstOrNull { it.days > currentStreak }
    }
}

/**
 * User's bloom statistics.
 */
data class BloomStats(
    val totalBlooms: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val bloomRate: Float,           // % of seeds bloomed vs assigned
    val lastBloomDate: LocalDate?,
    val thisWeekBlooms: Int,
    val thisMonthBlooms: Int,
    val favoriteWisdomType: WisdomType?,
    val totalXpFromBlooms: Int,

    // Type breakdown
    val wordBlooms: Int,
    val idiomBlooms: Int,
    val proverbBlooms: Int,
    val quoteBlooms: Int,
    val seedBlooms: Int
) {
    val isOnStreak: Boolean get() = currentStreak > 0
    val bloomPercentage: Int get() = (bloomRate * 100).toInt()

    val nextMilestone: BloomMilestone? get() = BloomMilestone.getNextMilestone(currentStreak)
    val daysToNextMilestone: Int get() = nextMilestone?.let { it.days - currentStreak } ?: 0

    companion object {
        fun empty(): BloomStats = BloomStats(
            totalBlooms = 0,
            currentStreak = 0,
            longestStreak = 0,
            bloomRate = 0f,
            lastBloomDate = null,
            thisWeekBlooms = 0,
            thisMonthBlooms = 0,
            favoriteWisdomType = null,
            totalXpFromBlooms = 0,
            wordBlooms = 0,
            idiomBlooms = 0,
            proverbBlooms = 0,
            quoteBlooms = 0,
            seedBlooms = 0
        )
    }
}

/**
 * Helper object for bloom calculations.
 */
object BloomCalculator {

    /**
     * Generate word variations for detection.
     */
    fun generateWordVariations(word: String): List<String> {
        val base = word.lowercase().trim()
        val variations = mutableSetOf(base)

        // Common suffixes
        variations.add("${base}s")
        variations.add("${base}es")
        variations.add("${base}ed")
        variations.add("${base}ing")
        variations.add("${base}ly")
        variations.add("${base}er")
        variations.add("${base}est")
        variations.add("${base}ness")
        variations.add("${base}ment")
        variations.add("${base}tion")
        variations.add("${base}ity")

        // Handle words ending in 'e'
        if (base.endsWith("e")) {
            val stem = base.dropLast(1)
            variations.add("${stem}ing")
            variations.add("${stem}ed")
            variations.add("${stem}er")
            variations.add("${stem}est")
        }

        // Handle words ending in 'y'
        if (base.endsWith("y") && base.length > 2) {
            val stem = base.dropLast(1)
            variations.add("${stem}ies")
            variations.add("${stem}ied")
            variations.add("${stem}ier")
            variations.add("${stem}iest")
            variations.add("${stem}ily")
        }

        // Handle doubling consonants
        if (base.length >= 3 && base.last() in "bcdfghjklmnpqrstvwxz") {
            val doubled = base + base.last()
            variations.add("${doubled}ed")
            variations.add("${doubled}ing")
            variations.add("${doubled}er")
        }

        return variations.toList()
    }

    /**
     * Extract key concepts from a quote or proverb.
     */
    fun extractKeyConcepts(text: String): List<String> {
        val concepts = mutableListOf<String>()
        val lowerText = text.lowercase()

        // Positive concepts
        val positiveTerms = listOf(
            "courage", "strength", "wisdom", "patience", "resilience",
            "gratitude", "peace", "growth", "change", "hope",
            "love", "kindness", "truth", "faith", "power",
            "joy", "compassion", "integrity", "honor", "perseverance",
            "determination", "confidence", "clarity", "purpose", "meaning"
        )

        // Journey/process concepts
        val journeyTerms = listOf(
            "journey", "path", "road", "way", "step",
            "begin", "start", "continue", "end", "finish",
            "climb", "rise", "fall", "learn", "grow"
        )

        // Time concepts
        val timeTerms = listOf(
            "time", "moment", "present", "future", "past",
            "today", "tomorrow", "yesterday", "now", "forever"
        )

        // Check all concept lists
        positiveTerms.filter { lowerText.contains(it) }.forEach { concepts.add(it) }
        journeyTerms.filter { lowerText.contains(it) }.forEach { concepts.add(it) }
        timeTerms.filter { lowerText.contains(it) }.forEach { concepts.add(it) }

        return concepts.distinct()
    }

    /**
     * Get common synonyms for a word.
     */
    fun getCommonSynonyms(word: String): List<String> {
        val synonymMap = mapOf(
            "courage" to listOf("bravery", "valor", "boldness", "fearlessness"),
            "wisdom" to listOf("insight", "knowledge", "understanding", "sagacity"),
            "strength" to listOf("power", "might", "force", "fortitude"),
            "patience" to listOf("endurance", "tolerance", "perseverance", "composure"),
            "gratitude" to listOf("thankfulness", "appreciation", "gratefulness"),
            "peace" to listOf("calm", "tranquility", "serenity", "harmony"),
            "growth" to listOf("development", "progress", "advancement", "evolution"),
            "hope" to listOf("optimism", "aspiration", "faith", "expectation"),
            "love" to listOf("affection", "devotion", "care", "fondness"),
            "joy" to listOf("happiness", "delight", "pleasure", "bliss"),
            "fear" to listOf("anxiety", "worry", "dread", "apprehension"),
            "change" to listOf("transformation", "shift", "transition", "evolution"),
            "success" to listOf("achievement", "accomplishment", "triumph", "victory"),
            "failure" to listOf("defeat", "setback", "loss", "disappointment"),
            "begin" to listOf("start", "commence", "initiate", "launch"),
            "end" to listOf("finish", "conclude", "complete", "terminate")
        )

        return synonymMap[word.lowercase()] ?: emptyList()
    }

    /**
     * Calculate streak status after a bloom.
     */
    fun calculateStreakAfterBloom(
        lastBloomDate: LocalDate?,
        currentStreak: Int,
        today: LocalDate = LocalDate.now()
    ): Pair<Int, Boolean> { // (newStreak, isNewLongest)
        return when {
            lastBloomDate == null -> 1 to true
            lastBloomDate == today -> currentStreak to false
            ChronoUnit.DAYS.between(lastBloomDate, today) == 1L -> {
                (currentStreak + 1) to true // Consecutive day
            }
            else -> 1 to false // Streak broken, restart
        }
    }

    /**
     * Check if streak was broken (for notifications).
     */
    fun isStreakBroken(lastBloomDate: LocalDate?, today: LocalDate = LocalDate.now()): Boolean {
        if (lastBloomDate == null) return false
        return ChronoUnit.DAYS.between(lastBloomDate, today) > 1
    }

    /**
     * Get encouragement message based on bloom state.
     */
    fun getEncouragementMessage(
        hasActiveSeed: Boolean,
        currentStreak: Int,
        lastBloomDate: LocalDate?
    ): String {
        val today = LocalDate.now()

        return when {
            lastBloomDate == today -> "You've already bloomed today! Come back tomorrow."
            !hasActiveSeed -> "Check back for today's seed of wisdom."
            isStreakBroken(lastBloomDate, today) -> "Fresh start! Let's bloom today's seed."
            currentStreak == 0 -> "Your first bloom awaits! Use today's wisdom in your writing."
            currentStreak < 7 -> "Keep the streak alive! $currentStreak days and counting."
            currentStreak < 30 -> "Amazing $currentStreak day streak! Keep nurturing your garden."
            else -> "Legendary $currentStreak day streak! You're a true wisdom gardener."
        }
    }

    /**
     * Get celebration message for successful bloom.
     */
    fun getCelebrationMessage(
        wisdomType: WisdomType,
        newStreak: Int,
        milestone: BloomMilestone?
    ): String {
        val baseMessage = when (wisdomType) {
            WisdomType.WORD_OF_DAY -> "Word bloomed! Your vocabulary grows."
            WisdomType.IDIOM -> "Idiom mastered! Expression enriched."
            WisdomType.PROVERB -> "Proverb applied! Ancient wisdom lives on."
            WisdomType.QUOTE -> "Quote embodied! Wisdom in action."
            WisdomType.SEED -> "Seed bloomed! Knowledge takes root."
        }

        val streakMessage = when {
            milestone != null -> " ${milestone.title}!"
            newStreak > 1 -> " $newStreak day streak!"
            else -> ""
        }

        return baseMessage + streakMessage
    }

    /**
     * Get suggestion for near-miss bloom attempts.
     */
    fun getSuggestion(seed: WisdomSeed): String {
        return when (seed.type) {
            WisdomType.WORD_OF_DAY -> "Try using \"${seed.content}\" or a variation like \"${seed.variations.firstOrNull() ?: seed.content + "ing"}\" in your writing."
            WisdomType.IDIOM -> "Express the meaning of \"${seed.content}\" in your own words."
            WisdomType.PROVERB -> "Reflect on themes like ${seed.relatedConcepts.take(3).joinToString(", ")} from this proverb."
            WisdomType.QUOTE -> "Consider what \"${seed.primaryTerms.firstOrNull() ?: "this wisdom"}\" means to you."
            WisdomType.SEED -> "Let \"${seed.content}\" inspire your reflection."
        }
    }
}

/**
 * Sample wisdom seeds for testing and default content.
 */
object SampleWisdomSeeds {

    fun createWordOfDay(word: String, meaning: String, date: LocalDate): WisdomSeed {
        val variations = BloomCalculator.generateWordVariations(word)
        val synonyms = BloomCalculator.getCommonSynonyms(word)

        return WisdomSeed(
            id = "word_${word}_${date}",
            type = WisdomType.WORD_OF_DAY,
            content = word,
            meaning = meaning,
            example = null,
            source = null,
            assignedDate = date,
            primaryTerms = listOf(word),
            synonyms = synonyms,
            relatedConcepts = emptyList(),
            variations = variations
        )
    }

    fun createProverb(content: String, meaning: String, source: String?, date: LocalDate): WisdomSeed {
        val concepts = BloomCalculator.extractKeyConcepts(content)

        return WisdomSeed(
            id = "proverb_${content.hashCode()}_${date}",
            type = WisdomType.PROVERB,
            content = content,
            meaning = meaning,
            example = null,
            source = source,
            assignedDate = date,
            primaryTerms = concepts.take(2),
            synonyms = emptyList(),
            relatedConcepts = concepts,
            variations = emptyList()
        )
    }

    fun createQuote(content: String, author: String, date: LocalDate): WisdomSeed {
        val concepts = BloomCalculator.extractKeyConcepts(content)

        return WisdomSeed(
            id = "quote_${content.hashCode()}_${date}",
            type = WisdomType.QUOTE,
            content = content,
            meaning = "A thought from $author",
            example = null,
            source = author,
            assignedDate = date,
            primaryTerms = concepts.take(3),
            synonyms = emptyList(),
            relatedConcepts = concepts,
            variations = emptyList()
        )
    }

    // Sample words
    val sampleWords = listOf(
        "resilience" to "The capacity to recover quickly from difficulties",
        "serendipity" to "The occurrence of events by chance in a happy way",
        "ephemeral" to "Lasting for a very short time",
        "eloquent" to "Fluent or persuasive in speaking or writing",
        "tenacious" to "Holding firmly to something; persistent",
        "sanguine" to "Optimistic or positive, especially in a bad situation",
        "perspicacious" to "Having a ready insight into and understanding of things",
        "mellifluous" to "Sweet or musical; pleasant to hear",
        "ineffable" to "Too great or extreme to be expressed in words",
        "luminous" to "Full of or shedding light; bright"
    )

    // Sample proverbs
    val sampleProverbs = listOf(
        Triple("The journey of a thousand miles begins with a single step", "Great achievements start with small actions", "Lao Tzu"),
        Triple("What lies behind us and what lies before us are tiny matters compared to what lies within us", "Our inner strength is most important", "Ralph Waldo Emerson"),
        Triple("In the middle of difficulty lies opportunity", "Challenges contain hidden possibilities", "Albert Einstein"),
        Triple("The only way to do great work is to love what you do", "Passion leads to excellence", "Steve Jobs"),
        Triple("It is not the strongest that survive, but those most responsive to change", "Adaptability is key to success", "Charles Darwin")
    )
}
