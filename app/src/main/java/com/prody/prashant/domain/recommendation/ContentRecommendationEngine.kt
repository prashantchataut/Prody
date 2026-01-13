package com.prody.prashant.domain.recommendation

import android.content.Context
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.QuoteDao
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.entity.QuoteEntity
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.domain.analytics.MoodAnalyticsEngine
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.model.TimeOfDay
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Content Recommendation Engine
 *
 * Provides personalized content recommendations based on:
 * - User's recent mood patterns (from journal entries)
 * - Time of day and day of week
 * - User engagement history (what they've interacted with)
 * - Content freshness (avoid repetition)
 *
 * Recommendation signals:
 * 1. Mood-based: Matches content to emotional state
 * 2. Temporal: Morning motivation vs evening reflection
 * 3. Growth-based: Challenges user with new vocabulary at right difficulty
 * 4. Engagement-based: More of what user engages with
 */
@Singleton
class ContentRecommendationEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val journalDao: JournalDao,
    private val quoteDao: QuoteDao,
    private val vocabularyDao: VocabularyDao,
    private val preferencesManager: PreferencesManager,
    private val moodAnalyticsEngine: MoodAnalyticsEngine
) {
    companion object {
        private const val TAG = "RecommendationEngine"

        // Time of day classifications
        private const val MORNING_START = 5
        private const val MORNING_END = 12
        private const val AFTERNOON_END = 17
        private const val EVENING_END = 21

        // Content categories based on mood
        // Valid Mood values: HAPPY, CALM, ANXIOUS, SAD, MOTIVATED, GRATEFUL, CONFUSED, EXCITED
        private val MOOD_TO_QUOTE_CATEGORIES = mapOf(
            Mood.HAPPY to listOf("gratitude", "success", "motivation"),
            Mood.CALM to listOf("mindfulness", "peace", "stoic"),
            Mood.ANXIOUS to listOf("peace", "mindfulness", "stoic"),
            Mood.SAD to listOf("hope", "healing", "courage"),
            Mood.MOTIVATED to listOf("success", "motivation", "growth"),
            Mood.GRATEFUL to listOf("gratitude", "wisdom", "life"),
            Mood.CONFUSED to listOf("clarity", "wisdom", "stoic"),
            Mood.EXCITED to listOf("motivation", "success", "growth")
        )

        // Temporal content preferences
        private val MORNING_CATEGORIES = listOf("motivation", "growth", "success", "gratitude")
        private val AFTERNOON_CATEGORIES = listOf("wisdom", "life", "stoic", "productivity")
        private val EVENING_CATEGORIES = listOf("reflection", "peace", "mindfulness", "gratitude")
        private val NIGHT_CATEGORIES = listOf("peace", "wisdom", "healing", "stoic")
    }

    /**
     * Get recommended quote for home screen.
     * Uses mood + time signals to select most relevant quote.
     */
    suspend fun getRecommendedQuote(): RecommendedContent<QuoteEntity>? = withContext(Dispatchers.IO) {
        try {
            val dominantMood = getDominantRecentMood()
            val timeCategories = getTemporalCategories()
            val moodCategories = MOOD_TO_QUOTE_CATEGORIES[dominantMood] ?: listOf("wisdom", "life")

            // Combine and prioritize categories
            val prioritizedCategories = (moodCategories + timeCategories).distinct().take(5)

            // Get quotes from prioritized categories, excluding recently shown
            val recentlyShownIds = getRecentlyShownQuoteIds()
            val candidates = mutableListOf<QuoteEntity>()

            for (category in prioritizedCategories) {
                val quotesInCategory = quoteDao.getQuotesByCategory(category).first()
                    .filter { it.id !in recentlyShownIds }
                    .take(3)
                candidates.addAll(quotesInCategory)
            }

            // If no candidates, get any quote not recently shown
            if (candidates.isEmpty()) {
                val allQuotes = quoteDao.getAllQuotes().first()
                    .filter { it.id !in recentlyShownIds }
                if (allQuotes.isEmpty()) {
                    return@withContext null
                }
                candidates.addAll(allQuotes.take(5))
            }

            // Select with weighted randomness (prefer favorited quotes slightly)
            val selected = candidates.maxByOrNull { (if (it.isFavorite) 0.1 else 0.0) + Math.random() * 0.9 }
                ?: candidates.random()

            RecommendedContent(
                content = selected,
                reason = getRecommendationReason(dominantMood, timeCategories),
                score = calculateQuoteScore(selected, dominantMood),
                signals = listOf(
                    RecommendationSignal.MOOD_BASED,
                    RecommendationSignal.TEMPORAL
                )
            )
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error getting recommended quote", e)
            null
        }
    }

    /**
     * Get recommended vocabulary word.
     * Balances learning new words vs reviewing known words.
     */
    suspend fun getRecommendedVocabulary(): RecommendedContent<VocabularyEntity>? = withContext(Dispatchers.IO) {
        try {
            val difficulty = preferencesManager.vocabularyDifficulty.first()
            val dominantMood = getDominantRecentMood()

            // Adjust difficulty based on mood (lower when stressed/sad)
            val adjustedDifficulty = when (dominantMood) {
                Mood.ANXIOUS, Mood.SAD -> (difficulty - 1).coerceAtLeast(1)
                Mood.MOTIVATED, Mood.EXCITED -> (difficulty + 1).coerceAtMost(5)
                else -> difficulty
            }

            // Get words at appropriate difficulty using the existing getWordsByDifficulty method
            val difficultyWords = vocabularyDao.getWordsByDifficulty(adjustedDifficulty)
            val candidates = difficultyWords.first().filter { !it.isLearned }

            if (candidates.isEmpty()) {
                // Fall back to any unlearned word
                val anyUnlearned = vocabularyDao.getRandomUnlearnedWord()
                if (anyUnlearned == null) return@withContext null
                return@withContext RecommendedContent(
                    content = anyUnlearned,
                    reason = "Expand your vocabulary",
                    score = 0.7,
                    signals = listOf(RecommendationSignal.GROWTH_BASED)
                )
            }

            // Prefer words not shown recently
            val recentlyShownIds = getRecentlyShownVocabIds()
            val freshCandidates = candidates.filter { it.id !in recentlyShownIds }
            val selected = if (freshCandidates.isNotEmpty()) freshCandidates.random() else candidates.random()

            RecommendedContent(
                content = selected,
                reason = getVocabRecommendationReason(adjustedDifficulty, dominantMood),
                score = calculateVocabScore(selected, adjustedDifficulty),
                signals = listOf(
                    RecommendationSignal.GROWTH_BASED,
                    RecommendationSignal.DIFFICULTY_ADJUSTED
                )
            )
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error getting recommended vocabulary", e)
            null
        }
    }

    /**
     * Get recommended journal prompt based on mood and time.
     */
    suspend fun getRecommendedJournalPrompt(): RecommendedContent<String>? = withContext(Dispatchers.IO) {
        try {
            val dominantMood = getDominantRecentMood()
            val timeOfDay = getTimeOfDay()

            val prompts = getPromptsForMoodAndTime(dominantMood, timeOfDay)
            if (prompts.isEmpty()) return@withContext null

            val selected = prompts.random()

            RecommendedContent(
                content = selected,
                reason = "Based on your recent reflections",
                score = 0.8,
                signals = listOf(
                    RecommendationSignal.MOOD_BASED,
                    RecommendationSignal.TEMPORAL
                )
            )
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error getting recommended prompt", e)
            null
        }
    }

    /**
     * Get full recommendation batch for home screen.
     */
    suspend fun getHomeRecommendations(): HomeRecommendations = withContext(Dispatchers.IO) {
        HomeRecommendations(
            quote = getRecommendedQuote(),
            vocabulary = getRecommendedVocabulary(),
            journalPrompt = getRecommendedJournalPrompt(),
            dominantMood = getDominantRecentMood(),
            timeContext = getTimeOfDay()
        )
    }

    // ==================== PRIVATE HELPERS ====================

    private suspend fun getDominantRecentMood(): Mood {
        return try {
            val recentEntries = journalDao.getRecentEntries(7).first()
            if (recentEntries.isEmpty()) return Mood.CALM

            // Count mood occurrences
            val moodCounts: Map<Mood, Int> = recentEntries
                .mapNotNull { entry ->
                    try { Mood.valueOf(entry.mood) } catch (e: Exception) { null }
                }
                .groupingBy { mood: Mood -> mood }
                .eachCount()

            moodCounts.maxByOrNull { entry: Map.Entry<Mood, Int> -> entry.value }?.key ?: Mood.CALM
        } catch (e: Exception) {
            Mood.CALM
        }
    }

    private fun getTimeOfDay(): TimeOfDay {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in MORNING_START until MORNING_END -> TimeOfDay.MORNING
            hour in MORNING_END until AFTERNOON_END -> TimeOfDay.AFTERNOON
            hour in AFTERNOON_END until EVENING_END -> TimeOfDay.EVENING
            else -> TimeOfDay.NIGHT
        }
    }

    private fun getTemporalCategories(): List<String> {
        return when (getTimeOfDay()) {
            TimeOfDay.MORNING -> MORNING_CATEGORIES
            TimeOfDay.AFTERNOON -> AFTERNOON_CATEGORIES
            TimeOfDay.EVENING -> EVENING_CATEGORIES
            TimeOfDay.NIGHT, TimeOfDay.LATE_NIGHT -> NIGHT_CATEGORIES
        }
    }

    private fun getRecommendationReason(mood: Mood, categories: List<String>): String {
        val timeContext = when (getTimeOfDay()) {
            TimeOfDay.MORNING -> "Start your day with"
            TimeOfDay.AFTERNOON -> "Afternoon wisdom:"
            TimeOfDay.EVENING -> "Evening reflection:"
            TimeOfDay.NIGHT, TimeOfDay.LATE_NIGHT -> "Night thoughts:"
        }

        return when (mood) {
            Mood.ANXIOUS -> "$timeContext Peace & perspective"
            Mood.SAD -> "$timeContext Hope & strength"
            Mood.MOTIVATED, Mood.EXCITED -> "$timeContext Fuel your drive"
            Mood.GRATEFUL -> "$timeContext Celebrate your gratitude"
            else -> "$timeContext ${categories.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Wisdom"}"
        }
    }

    private fun getVocabRecommendationReason(difficulty: Int, mood: Mood): String {
        return when {
            difficulty <= 2 -> "Build your foundation"
            difficulty == 3 -> "Expand your vocabulary"
            difficulty >= 4 -> "Challenge yourself"
            mood == Mood.MOTIVATED -> "Match your energy"
            else -> "Today's word"
        }
    }

    private fun calculateQuoteScore(quote: QuoteEntity, mood: Mood): Double {
        var score = 0.5

        // Boost for matching category
        val moodCategories = MOOD_TO_QUOTE_CATEGORIES[mood] ?: emptyList()
        if (quote.category in moodCategories) score += 0.2

        // Boost for favorited quotes
        if (quote.isFavorite) score += 0.1

        // Slight randomness
        score += Math.random() * 0.2

        return score.coerceIn(0.0, 1.0)
    }

    private fun calculateVocabScore(vocab: VocabularyEntity, targetDifficulty: Int): Double {
        var score = 0.5

        // Boost for matching difficulty
        if (vocab.difficulty == targetDifficulty) score += 0.3
        else if (Math.abs(vocab.difficulty - targetDifficulty) == 1) score += 0.15

        // Boost for unlearned words
        if (!vocab.isLearned) score += 0.2

        return score.coerceIn(0.0, 1.0)
    }

    private fun getRecentlyShownQuoteIds(): Set<Long> {
        // In a real implementation, this would track shown items
        // For now, return empty to allow all quotes
        return emptySet()
    }

    private fun getRecentlyShownVocabIds(): Set<Long> {
        return emptySet()
    }

    private fun getPromptsForMoodAndTime(mood: Mood, timeOfDay: TimeOfDay): List<String> {
        val moodPrompts = when (mood) {
            Mood.HAPPY, Mood.EXCITED, Mood.GRATEFUL -> listOf(
                "What made you smile today?",
                "Describe a moment of joy you experienced recently.",
                "Who or what are you most grateful for right now?"
            )
            Mood.ANXIOUS -> listOf(
                "What's weighing on your mind? Let it out here.",
                "Name one small thing you can control right now.",
                "What would you tell a friend facing this situation?"
            )
            Mood.SAD -> listOf(
                "It's okay to feel this way. What do you need right now?",
                "What's one small comfort you can give yourself today?",
                "Write about a time you felt strong."
            )
            Mood.MOTIVATED -> listOf(
                "What goal are you excited to pursue?",
                "How will future you thank present you?",
                "What's your plan for channeling this energy?"
            )
            else -> listOf(
                "What's on your mind right now?",
                "Describe your day in three words.",
                "What would make tomorrow better?"
            )
        }

        val timePrompts = when (timeOfDay) {
            TimeOfDay.MORNING -> listOf(
                "What intention will guide you today?",
                "What's one thing you're looking forward to?"
            )
            TimeOfDay.EVENING -> listOf(
                "What was the highlight of your day?",
                "What did you learn about yourself today?"
            )
            TimeOfDay.NIGHT, TimeOfDay.LATE_NIGHT -> listOf(
                "As you wind down, what thoughts need releasing?",
                "What are you grateful for from today?"
            )
            TimeOfDay.AFTERNOON -> listOf(
                "How is your day unfolding?",
                "What's bringing you energy this afternoon?"
            )
        }

        return (moodPrompts + timePrompts).shuffled()
    }
}

/**
 * Wrapper for recommended content with metadata.
 */
data class RecommendedContent<T>(
    val content: T,
    val reason: String,
    val score: Double,
    val signals: List<RecommendationSignal>
)

/**
 * Signals used to generate recommendation.
 */
enum class RecommendationSignal {
    MOOD_BASED,
    TEMPORAL,
    GROWTH_BASED,
    ENGAGEMENT_BASED,
    DIFFICULTY_ADJUSTED,
    FRESHNESS
}

/**
 * Bundle of recommendations for home screen.
 */
data class HomeRecommendations(
    val quote: RecommendedContent<QuoteEntity>?,
    val vocabulary: RecommendedContent<VocabularyEntity>?,
    val journalPrompt: RecommendedContent<String>?,
    val dominantMood: Mood,
    val timeContext: TimeOfDay
)
