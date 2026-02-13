package com.prody.prashant.domain.gamification

import android.util.Log
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.dao.VocabularyDao
import com.prody.prashant.data.local.entity.VocabularyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * WisdomQuestEngine - Active Recall Gamification System
 *
 * Replaces the "click-to-learn" XP farming with meaningful cognitive challenges.
 * Users must complete a "Micro-Quest" to earn XP for learning a word.
 *
 * Challenge Types:
 * - UNSCRAMBLE: Rearrange letters to form the word (3-second timer)
 * - MULTIPLE_CHOICE: Select the correct definition from 4 options
 * - CONTEXT_FIT: Choose the correct word for a sentence context
 *
 * XP Scaling:
 * - Base XP for correct answer: 15
 * - Streak bonus: +5 XP per consecutive correct answer
 * - Max streak bonus: 50 XP (10 streak)
 * - Daily Focus bonus: 2x XP for matching category
 *
 * Psychological Investment: By requiring cognitive effort, users feel they
 * truly "earned" the XP, creating stronger retention and engagement.
 */
@Singleton
class WisdomQuestEngine @Inject constructor(
    private val userDao: UserDao,
    private val vocabularyDao: VocabularyDao,
    private val gamificationService: GamificationService
) {
    companion object {
        private const val TAG = "WisdomQuestEngine"

        // XP Values
        const val BASE_XP_CORRECT = 15
        const val STREAK_BONUS_XP = 5
        const val MAX_STREAK_BONUS = 50
        const val DAILY_FOCUS_MULTIPLIER = 2

        // Challenge settings
        const val UNSCRAMBLE_TIME_LIMIT_MS = 8000L // 8 seconds
        const val MULTIPLE_CHOICE_OPTIONS = 4
    }

    /**
     * Challenge types for Active Recall
     */
    enum class ChallengeType {
        /** Rearrange scrambled letters to form the word */
        UNSCRAMBLE,
        /** Select correct definition from multiple options */
        MULTIPLE_CHOICE,
        /** Choose the word that fits the sentence context */
        CONTEXT_FIT
    }

    /**
     * Daily Focus categories for XP doubling
     */
    enum class DailyFocus(val displayName: String, val description: String) {
        GRATITUDE("Gratitude", "Focus on appreciation and thankfulness"),
        STOICISM("Stoicism", "Embrace resilience and inner strength"),
        MINDFULNESS("Mindfulness", "Practice presence and awareness"),
        GROWTH("Growth", "Pursue learning and self-improvement"),
        COMPASSION("Compassion", "Cultivate kindness and empathy"),
        WISDOM("Wisdom", "Seek deeper understanding")
    }

    /**
     * Represents a Wisdom Quest challenge
     */
    data class WisdomChallenge(
        val type: ChallengeType,
        val word: VocabularyEntity,
        val scrambledWord: String? = null, // For UNSCRAMBLE
        val options: List<String>? = null, // For MULTIPLE_CHOICE
        val contextSentence: String? = null, // For CONTEXT_FIT with blank
        val correctAnswer: String,
        val timeLimitMs: Long = UNSCRAMBLE_TIME_LIMIT_MS
    )

    /**
     * Result of completing a Wisdom Quest
     */
    data class QuestResult(
        val isCorrect: Boolean,
        val baseXp: Int,
        val streakBonus: Int,
        val dailyFocusBonus: Int,
        val totalXp: Int,
        val newStreak: Int,
        val message: String
    )

    /**
     * Generates an Active Recall challenge for a word.
     *
     * @param word The vocabulary word to create a challenge for
     * @param preferredType Optional preferred challenge type
     * @return A WisdomChallenge ready for the user
     */
    suspend fun generateChallenge(
        word: VocabularyEntity,
        preferredType: ChallengeType? = null
    ): WisdomChallenge = withContext(Dispatchers.Default) {
        val type = preferredType ?: ChallengeType.entries[Random.nextInt(ChallengeType.entries.size)]

        when (type) {
            ChallengeType.UNSCRAMBLE -> generateUnscrambleChallenge(word)
            ChallengeType.MULTIPLE_CHOICE -> generateMultipleChoiceChallenge(word)
            ChallengeType.CONTEXT_FIT -> generateContextFitChallenge(word)
        }
    }

    /**
     * Generate a word unscramble challenge
     */
    private fun generateUnscrambleChallenge(word: VocabularyEntity): WisdomChallenge {
        val scrambled = scrambleWord(word.word)
        return WisdomChallenge(
            type = ChallengeType.UNSCRAMBLE,
            word = word,
            scrambledWord = scrambled,
            correctAnswer = word.word.lowercase(),
            timeLimitMs = UNSCRAMBLE_TIME_LIMIT_MS
        )
    }

    /**
     * Generate a multiple choice definition challenge
     */
    private suspend fun generateMultipleChoiceChallenge(word: VocabularyEntity): WisdomChallenge {
        // Get other words for wrong options
        val allWords = vocabularyDao.getAllVocabularySync()
        val otherWords = allWords.filter { it.id != word.id }.shuffled().take(3)

        val options = mutableListOf(word.definition)
        options.addAll(otherWords.map { it.definition })
        options.shuffle()

        return WisdomChallenge(
            type = ChallengeType.MULTIPLE_CHOICE,
            word = word,
            options = options,
            correctAnswer = word.definition,
            timeLimitMs = 0 // No time limit for multiple choice
        )
    }

    /**
     * Generate a context fit challenge
     */
    private fun generateContextFitChallenge(word: VocabularyEntity): WisdomChallenge {
        // Create a sentence with a blank where the word should go
        val contextSentence = if (word.exampleSentence.isNotBlank()) {
            word.exampleSentence.replace(
                word.word,
                "_____",
                ignoreCase = true
            )
        } else {
            "The concept of ${word.partOfSpeech} meaning '${word.definition}' is represented by _____."
        }

        return WisdomChallenge(
            type = ChallengeType.CONTEXT_FIT,
            word = word,
            contextSentence = contextSentence,
            correctAnswer = word.word.lowercase(),
            timeLimitMs = 0
        )
    }

    /**
     * Scramble a word ensuring it's different from the original
     */
    private fun scrambleWord(word: String): String {
        val chars = word.lowercase().toCharArray().toMutableList()
        var scrambled: String
        var attempts = 0

        do {
            chars.shuffle()
            scrambled = chars.joinToString("")
            attempts++
        } while (scrambled == word.lowercase() && attempts < 10)

        return scrambled.uppercase()
    }

    /**
     * Validates the user's answer and awards XP accordingly.
     *
     * @param challenge The challenge that was presented
     * @param userAnswer The user's submitted answer
     * @param timeSpentMs Time taken to answer (for time-based scoring)
     * @return QuestResult with XP breakdown and new streak
     */
    suspend fun validateAnswer(
        challenge: WisdomChallenge,
        userAnswer: String,
        timeSpentMs: Long = 0
    ): QuestResult = withContext(Dispatchers.IO) {
        val isCorrect = when (challenge.type) {
            ChallengeType.UNSCRAMBLE -> {
                userAnswer.trim().equals(challenge.correctAnswer, ignoreCase = true)
            }
            ChallengeType.MULTIPLE_CHOICE -> {
                userAnswer.trim().equals(challenge.correctAnswer, ignoreCase = true)
            }
            ChallengeType.CONTEXT_FIT -> {
                userAnswer.trim().equals(challenge.correctAnswer, ignoreCase = true)
            }
        }

        val profile = userDao.getUserProfileSync()
        val currentStreak = profile?.preferences?.let {
            try {
                // Parse JSON to get active recall streak
                val json = org.json.JSONObject(it)
                json.optInt("activeRecallStreak", 0)
            } catch (e: Exception) {
                0
            }
        } ?: 0

        if (isCorrect) {
            // Calculate XP
            val newStreak = currentStreak + 1
            val streakBonus = (newStreak * STREAK_BONUS_XP).coerceAtMost(MAX_STREAK_BONUS)

            // Check daily focus bonus
            val dailyFocusBonus = getDailyFocusBonus(challenge.word.category)

            val baseXp = BASE_XP_CORRECT
            val totalXp = (baseXp + streakBonus) * (if (dailyFocusBonus > 0) DAILY_FOCUS_MULTIPLIER else 1)

            // Update streak
            updateActiveRecallStreak(newStreak)

            // Award XP through gamification service
            userDao.addPoints(totalXp)

            // Mark word as learned
            vocabularyDao.markAsLearned(challenge.word.id)
            userDao.incrementWordsLearned()

            com.prody.prashant.util.AppLogger.d(TAG, "Correct! XP: $totalXp (base: $baseXp, streak: $streakBonus, focus: $dailyFocusBonus)")

            QuestResult(
                isCorrect = true,
                baseXp = baseXp,
                streakBonus = streakBonus,
                dailyFocusBonus = dailyFocusBonus,
                totalXp = totalXp,
                newStreak = newStreak,
                message = getSuccessMessage(newStreak)
            )
        } else {
            // Reset streak on wrong answer
            updateActiveRecallStreak(0)

            com.prody.prashant.util.AppLogger.d(TAG, "Incorrect. Streak reset.")

            QuestResult(
                isCorrect = false,
                baseXp = 0,
                streakBonus = 0,
                dailyFocusBonus = 0,
                totalXp = 0,
                newStreak = 0,
                message = getFailureMessage()
            )
        }
    }

    /**
     * Updates the active recall streak in user preferences
     */
    private suspend fun updateActiveRecallStreak(newStreak: Int) {
        try {
            val profile = userDao.getUserProfileSync() ?: return
            val currentPrefs = try {
                org.json.JSONObject(profile.preferences)
            } catch (e: Exception) {
                org.json.JSONObject()
            }

            currentPrefs.put("activeRecallStreak", newStreak)
            currentPrefs.put("lastActiveRecallDate", System.currentTimeMillis())

            userDao.updatePreferences(currentPrefs.toString())
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error updating streak", e)
        }
    }

    /**
     * Gets the current active recall streak
     */
    suspend fun getCurrentStreak(): Int = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync() ?: return@withContext 0
            val json = org.json.JSONObject(profile.preferences)
            json.optInt("activeRecallStreak", 0)
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Sets the daily focus category
     */
    suspend fun setDailyFocus(focus: DailyFocus) = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync() ?: return@withContext
            val currentPrefs = try {
                org.json.JSONObject(profile.preferences)
            } catch (e: Exception) {
                org.json.JSONObject()
            }

            currentPrefs.put("dailyFocus", focus.name)
            currentPrefs.put("dailyFocusSetDate", System.currentTimeMillis())

            userDao.updatePreferences(currentPrefs.toString())
            com.prody.prashant.util.AppLogger.d(TAG, "Daily focus set to: ${focus.displayName}")
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error setting daily focus", e)
        }
    }

    /**
     * Gets the current daily focus
     */
    suspend fun getCurrentDailyFocus(): DailyFocus? = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync() ?: return@withContext null
            val json = org.json.JSONObject(profile.preferences)

            // Check if focus was set today
            val focusDate = json.optLong("dailyFocusSetDate", 0)
            val today = getStartOfDayMillis()

            if (focusDate < today) {
                // Focus expired, needs to be set again
                return@withContext null
            }

            val focusName = json.optString("dailyFocus", "")
            DailyFocus.entries.find { it.name == focusName }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Checks if user needs to set daily focus
     */
    suspend fun needsDailyFocus(): Boolean = withContext(Dispatchers.IO) {
        getCurrentDailyFocus() == null
    }

    /**
     * Gets XP bonus if word category matches daily focus
     */
    private suspend fun getDailyFocusBonus(category: String): Int {
        val focus = getCurrentDailyFocus() ?: return 0

        // Map word categories to daily focus
        val categoryFocusMap = mapOf(
            "gratitude" to DailyFocus.GRATITUDE,
            "stoicism" to DailyFocus.STOICISM,
            "mindfulness" to DailyFocus.MINDFULNESS,
            "growth" to DailyFocus.GROWTH,
            "compassion" to DailyFocus.COMPASSION,
            "wisdom" to DailyFocus.WISDOM,
            "general" to DailyFocus.GROWTH,
            "academic" to DailyFocus.WISDOM,
            "literary" to DailyFocus.WISDOM
        )

        val matchedFocus = categoryFocusMap[category.lowercase()]
        return if (matchedFocus == focus) BASE_XP_CORRECT else 0
    }

    private fun getStartOfDayMillis(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getSuccessMessage(streak: Int): String {
        return when {
            streak >= 10 -> "Enlightened! Your wisdom flows like a river."
            streak >= 7 -> "Masterful! Your understanding deepens."
            streak >= 5 -> "Brilliant! Your knowledge grows."
            streak >= 3 -> "Well done! You're building momentum."
            else -> "Correct! The path to wisdom continues."
        }
    }

    private fun getFailureMessage(): String {
        val messages = listOf(
            "The journey continues. Try again.",
            "Wisdom requires patience. Reflect and retry.",
            "Every stumble is a lesson learned.",
            "The path is not always clear. Persist."
        )
        return messages[Random.nextInt(messages.size)]
    }
}
