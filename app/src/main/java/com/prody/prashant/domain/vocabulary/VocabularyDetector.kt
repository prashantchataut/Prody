package com.prody.prashant.domain.vocabulary

import com.prody.prashant.data.local.entity.VocabularyEntity
import java.time.Instant

/**
 * Interface for detecting learned vocabulary words in text content.
 * Provides smart word detection that handles different word forms,
 * case insensitivity, and proper word boundaries.
 */
interface VocabularyDetector {
    /**
     * Detects learned words that appear in the given content.
     *
     * @param content The text to analyze for vocabulary words
     * @param learnedWords List of vocabulary words that have been learned
     * @return List of word usages detected in the content
     */
    fun detectLearnedWords(content: String, learnedWords: List<VocabularyEntity>): List<WordUsage>

    /**
     * Extracts the sentence containing a word at a specific position.
     *
     * @param content The full text content
     * @param position The range where the word appears
     * @return The sentence containing the word
     */
    fun extractSentence(content: String, position: IntRange): String
}

/**
 * Represents a single usage of a learned vocabulary word in context.
 *
 * @property word The vocabulary entity that was detected
 * @property usedIn The sentence where the word appeared
 * @property position The position range in the content (for highlighting)
 * @property detectedAt The timestamp when detection occurred
 * @property matchedForm The actual form of the word that was matched (e.g., "running" for "run")
 */
data class WordUsage(
    val word: VocabularyEntity,
    val usedIn: String,
    val position: IntRange,
    val detectedAt: Instant = Instant.now(),
    val matchedForm: String
)

/**
 * Configuration for vocabulary detection behavior.
 *
 * @property caseSensitive Whether to match words case-sensitively (default: false)
 * @property matchWordForms Whether to match different word forms (e.g., run/running/ran)
 * @property minWordLength Minimum word length to consider for matching (default: 3)
 */
data class DetectionConfig(
    val caseSensitive: Boolean = false,
    val matchWordForms: Boolean = true,
    val minWordLength: Int = 3
)
