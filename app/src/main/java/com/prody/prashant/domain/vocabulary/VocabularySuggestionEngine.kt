package com.prody.prashant.domain.vocabulary

import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.dao.VocabularyLearningDao
import com.prody.prashant.data.local.dao.WordUsageDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import kotlinx.coroutines.flow.first
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Engine for suggesting relevant vocabulary words while writing.
 *
 * Features:
 * - Analyzes journal content to detect topics
 * - Suggests learned words that fit the context
 * - Only suggests words that haven't been used recently
 * - Non-intrusive: shows only highly relevant suggestions
 * - One-tap insertion into text
 */
@Singleton
class VocabularySuggestionEngine @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    private val vocabularyLearningDao: VocabularyLearningDao,
    private val wordUsageDao: WordUsageDao
) {

    companion object {
        // Minimum relevance score to show a suggestion (0.0 to 1.0)
        private const val MIN_RELEVANCE_SCORE = 0.6f

        // Maximum suggestions to show at once
        private const val MAX_SUGGESTIONS = 3

        // Don't suggest words used in the last N days
        private const val SUGGESTION_COOLDOWN_DAYS = 3

        // Topic keywords for different categories
        private val TOPIC_KEYWORDS = mapOf(
            "emotions" to listOf("feel", "feeling", "felt", "emotion", "mood", "happy", "sad", "angry", "anxious", "excited", "calm"),
            "work" to listOf("work", "job", "career", "project", "meeting", "deadline", "task", "professional", "colleague", "boss"),
            "relationships" to listOf("friend", "family", "relationship", "love", "partner", "connection", "talk", "conversation", "support"),
            "growth" to listOf("learn", "learning", "grow", "growth", "improve", "improvement", "develop", "development", "progress", "goal"),
            "challenges" to listOf("difficult", "challenge", "struggle", "problem", "issue", "obstacle", "hard", "tough", "overcome"),
            "success" to listOf("success", "achieve", "achievement", "accomplish", "win", "victory", "proud", "celebrate", "milestone"),
            "health" to listOf("health", "exercise", "workout", "sleep", "eat", "diet", "wellness", "fitness", "energy", "tired"),
            "creativity" to listOf("creative", "create", "art", "write", "writing", "design", "idea", "imagine", "inspiration", "project"),
            "reflection" to listOf("think", "thought", "reflect", "reflection", "consider", "wonder", "question", "realize", "understand"),
            "gratitude" to listOf("grateful", "thank", "thankful", "appreciate", "appreciation", "blessing", "fortunate", "luck", "gift")
        )
    }

    /**
     * Analyze content and suggest relevant vocabulary words.
     *
     * @param content The journal entry content being written
     * @param userId The user ID
     * @return List of vocabulary suggestions
     */
    suspend fun getSuggestions(
        content: String,
        userId: String = "local"
    ): List<VocabularySuggestion> {
        if (content.length < 50) {
            return emptyList() // Not enough content to analyze
        }

        // Detect topics from content
        val topics = detectTopics(content)
        if (topics.isEmpty()) {
            return emptyList()
        }

        // Get learned words from vocabulary
        val learnedWords = vocabularyDao.getLearnedWords().first()
        if (learnedWords.isEmpty()) {
            return emptyList()
        }

        // Get words used recently (to avoid repeating suggestions)
        val recentlyUsedIds = getRecentlyUsedWordIds(userId)

        // Score and filter words
        val suggestions = learnedWords
            .filter { !recentlyUsedIds.contains(it.id) } // Not used recently
            .mapNotNull { word ->
                val relevanceScore = calculateRelevance(word, topics, content)
                if (relevanceScore >= MIN_RELEVANCE_SCORE) {
                    VocabularySuggestion(
                        word = word,
                        relevanceScore = relevanceScore,
                        suggestedTopic = topics.maxByOrNull { calculateTopicRelevance(word, it, content) }?.first ?: "",
                        contextHint = generateContextHint(word, topics.firstOrNull()?.first)
                    )
                } else {
                    null
                }
            }
            .sortedByDescending { it.relevanceScore }
            .take(MAX_SUGGESTIONS)

        return suggestions
    }

    /**
     * Detect topics from the content based on keywords.
     */
    private fun detectTopics(content: String): List<Pair<String, Float>> {
        val lowercaseContent = content.lowercase(Locale.getDefault())
        val words = lowercaseContent.split(Regex("\\W+"))

        return TOPIC_KEYWORDS.mapNotNull { (topic, keywords) ->
            val matches = keywords.count { keyword -> words.contains(keyword) }
            if (matches > 0) {
                val score = matches.toFloat() / keywords.size
                Pair(topic, score)
            } else {
                null
            }
        }.sortedByDescending { it.second }
    }

    /**
     * Calculate relevance of a word to the detected topics and content.
     */
    private fun calculateRelevance(
        word: VocabularyEntity,
        topics: List<Pair<String, Float>>,
        content: String
    ): Float {
        if (topics.isEmpty()) return 0f

        // Check if word category matches any topic
        val categoryMatch = topics.any { (topic, _) ->
            word.category.lowercase(Locale.getDefault()).contains(topic) ||
                    topic.contains(word.category.lowercase(Locale.getDefault()))
        }

        // Check if word definition/synonyms relate to topics
        val definitionWords = (word.definition + " " + word.synonyms)
            .lowercase(Locale.getDefault())
            .split(Regex("\\W+"))

        var topicRelevance = 0f
        for ((topic, topicScore) in topics) {
            val keywords = TOPIC_KEYWORDS[topic] ?: continue
            val matches = keywords.count { keyword -> definitionWords.contains(keyword) }
            if (matches > 0) {
                topicRelevance += (matches.toFloat() / keywords.size) * topicScore
            }
        }

        // Boost score if category matches
        if (categoryMatch) {
            topicRelevance *= 1.5f
        }

        // Check if word part of speech fits the context
        val posBoost = when (word.partOfSpeech.lowercase(Locale.getDefault())) {
            "adjective" -> 1.2f // Adjectives are often useful for description
            "verb" -> 1.1f // Verbs for actions
            "adverb" -> 1.0f
            else -> 0.9f
        }

        return (topicRelevance * posBoost).coerceIn(0f, 1f)
    }

    /**
     * Calculate how relevant a word is to a specific topic.
     */
    private fun calculateTopicRelevance(word: VocabularyEntity, topic: Pair<String, Float>, content: String): Float {
        val topicName = topic.first
        val topicScore = topic.second

        val keywords = TOPIC_KEYWORDS[topicName] ?: return 0f
        val definitionWords = (word.definition + " " + word.synonyms)
            .lowercase(Locale.getDefault())
            .split(Regex("\\W+"))

        val matches = keywords.count { keyword -> definitionWords.contains(keyword) }
        return if (matches > 0) {
            (matches.toFloat() / keywords.size) * topicScore
        } else {
            0f
        }
    }

    /**
     * Generate a helpful context hint for the suggestion.
     */
    private fun generateContextHint(word: VocabularyEntity, topic: String?): String {
        return when {
            topic != null -> "You're writing about $topic. The word '${word.word}' might fit."
            word.exampleSentence.isNotEmpty() -> "Try using '${word.word}': ${word.exampleSentence}"
            else -> "Consider using '${word.word}' (${word.definition.take(50)}...)"
        }
    }

    /**
     * Get word IDs that have been used recently.
     */
    private suspend fun getRecentlyUsedWordIds(userId: String): Set<Long> {
        val cutoffTime = System.currentTimeMillis() - (SUGGESTION_COOLDOWN_DAYS * 24 * 60 * 60 * 1000L)
        val recentUsages = wordUsageDao.getUsagesInDateRange(
            userId = userId,
            startTime = cutoffTime,
            endTime = System.currentTimeMillis()
        )
        return recentUsages.map { it.wordId }.toSet()
    }

    /**
     * Get words that should be practiced (learned but not used much).
     */
    suspend fun getWordsNeedingPractice(userId: String = "local", limit: Int = 10): List<VocabularyEntity> {
        // Get learned words that haven't been used or were used long ago
        val learnedWordIds = wordUsageDao.getLearnedButUnusedWordIds(userId, limit * 2)

        return learnedWordIds
            .mapNotNull { vocabularyDao.getWordById(it) }
            .take(limit)
    }
}

/**
 * Represents a vocabulary word suggestion for the current writing context.
 */
data class VocabularySuggestion(
    val word: VocabularyEntity,
    val relevanceScore: Float,
    val suggestedTopic: String,
    val contextHint: String
) {
    /**
     * Get a short display text for the suggestion chip.
     */
    val chipText: String
        get() = "${word.word} (${word.partOfSpeech})"

    /**
     * Get the full suggestion message.
     */
    val fullMessage: String
        get() = contextHint
}
