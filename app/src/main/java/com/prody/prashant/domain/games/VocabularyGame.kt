package com.prody.prashant.domain.games

import com.prody.prashant.data.local.entity.VocabularyEntity

/**
 * Sealed class representing different types of vocabulary games.
 */
sealed class VocabularyGame {
    abstract val id: String
    abstract val points: Int
    abstract val timeLimit: Int // seconds

    /**
     * Match word with its synonym.
     */
    data class SynonymMatch(
        override val id: String,
        override val points: Int = 10,
        override val timeLimit: Int = 15,
        val word: VocabularyEntity,
        val options: List<String>,
        val correctAnswer: String
    ) : VocabularyGame()

    /**
     * Match word with its antonym.
     */
    data class AntonymMatch(
        override val id: String,
        override val points: Int = 10,
        override val timeLimit: Int = 15,
        val word: VocabularyEntity,
        val options: List<String>,
        val correctAnswer: String
    ) : VocabularyGame()

    /**
     * Match word with its definition.
     */
    data class DefinitionMatch(
        override val id: String,
        override val points: Int = 10,
        override val timeLimit: Int = 15,
        val word: VocabularyEntity,
        val options: List<String>,
        val correctAnswer: String
    ) : VocabularyGame()

    /**
     * Timed quiz with multiple words.
     */
    data class TimedQuiz(
        override val id: String,
        override val points: Int = 5, // per question
        override val timeLimit: Int = 10, // per question
        val questions: List<QuizQuestion>,
        val currentQuestionIndex: Int = 0
    ) : VocabularyGame() {
        val currentQuestion: QuizQuestion?
            get() = questions.getOrNull(currentQuestionIndex)

        val isComplete: Boolean
            get() = currentQuestionIndex >= questions.size

        val totalQuestions: Int
            get() = questions.size
    }

    /**
     * Spelling challenge - type the correct spelling.
     */
    data class SpellingChallenge(
        override val id: String,
        override val points: Int = 20,
        override val timeLimit: Int = 30,
        val word: VocabularyEntity,
        val hint: String // definition or partial word
    ) : VocabularyGame()

    /**
     * Word category sorting.
     */
    data class CategorySort(
        override val id: String,
        override val points: Int = 25,
        override val timeLimit: Int = 45,
        val words: List<VocabularyEntity>,
        val categories: List<String>
    ) : VocabularyGame()

    /**
     * Match multiple words with their definitions.
     */
    data class MatchingPairs(
        override val id: String,
        override val points: Int = 30,
        override val timeLimit: Int = 60,
        val pairs: List<WordDefinitionPair>
    ) : VocabularyGame()
}

/**
 * A single quiz question.
 */
data class QuizQuestion(
    val word: VocabularyEntity,
    val questionType: QuestionType,
    val options: List<String>,
    val correctAnswer: String
)

enum class QuestionType {
    DEFINITION,      // What does this word mean?
    SYNONYM,         // Which is a synonym?
    ANTONYM,         // Which is an antonym?
    EXAMPLE_USAGE,   // Which sentence uses this word correctly?
    PART_OF_SPEECH   // What part of speech is this word?
}

/**
 * Word-definition pair for matching games.
 */
data class WordDefinitionPair(
    val word: String,
    val definition: String,
    val isMatched: Boolean = false
)

/**
 * Game session tracking.
 */
data class GameSession(
    val id: String,
    val gameType: GameType,
    val startTime: Long,
    val endTime: Long? = null,
    val totalQuestions: Int,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val skippedQuestions: Int = 0,
    val pointsEarned: Int = 0,
    val streak: Int = 0,
    val maxStreak: Int = 0,
    val averageResponseTime: Long = 0
) {
    val isComplete: Boolean
        get() = endTime != null

    val accuracy: Float
        get() {
            val attempted = correctAnswers + wrongAnswers
            return if (attempted > 0) (correctAnswers.toFloat() / attempted) * 100 else 0f
        }

    val duration: Long
        get() = (endTime ?: System.currentTimeMillis()) - startTime
}

enum class GameType(val displayName: String, val description: String) {
    SYNONYM_MATCH("Synonym Match", "Match words with their synonyms"),
    ANTONYM_MATCH("Antonym Match", "Match words with their antonyms"),
    DEFINITION_MATCH("Definition Match", "Match words with their meanings"),
    TIMED_QUIZ("Timed Quiz", "Answer as many as you can before time runs out"),
    SPELLING("Spelling Challenge", "Type the correct spelling"),
    CATEGORY_SORT("Category Sort", "Sort words into their categories"),
    MATCHING_PAIRS("Matching Pairs", "Match words with definitions")
}

/**
 * Game difficulty levels.
 */
enum class GameDifficulty(
    val displayName: String,
    val optionCount: Int,
    val timeMultiplier: Float,
    val pointMultiplier: Float
) {
    EASY("Easy", 3, 1.5f, 0.5f),
    MEDIUM("Medium", 4, 1.0f, 1.0f),
    HARD("Hard", 5, 0.75f, 1.5f),
    EXPERT("Expert", 6, 0.5f, 2.0f)
}

/**
 * Game result after completing a game.
 */
data class GameResult(
    val session: GameSession,
    val newHighScore: Boolean,
    val achievementsUnlocked: List<String>,
    val xpEarned: Int,
    val streakBonus: Int,
    val timeBonus: Int,
    val accuracyBonus: Int
) {
    val totalXp: Int
        get() = xpEarned + streakBonus + timeBonus + accuracyBonus
}

/**
 * Daily challenge configuration for vocabulary games.
 *
 * Renamed from DailyChallenge to avoid collision with
 * com.prody.prashant.domain.gamification.DailyChallenge.
 */
data class GameDailyChallenge(
    val date: Long,
    val gameType: GameType,
    val difficulty: GameDifficulty,
    val targetScore: Int,
    val bonusXp: Int,
    val isCompleted: Boolean = false,
    val bestScore: Int = 0
)

/**
 * Leaderboard entry for game scores.
 */
data class GameLeaderboardEntry(
    val odId: String,
    val displayName: String,
    val avatarId: String,
    val gameType: GameType,
    val highScore: Int,
    val gamesPlayed: Int,
    val totalXpEarned: Int,
    val rank: Int
)
