package com.prody.prashant.domain.gamification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * SRS (Spaced Repetition System) Integration with Gamification
 *
 * This module integrates the SM-2 spaced repetition algorithm with the
 * gamification system, providing:
 *
 * 1. XP REWARDS for vocabulary reviews
 *    - Base XP per correct answer
 *    - Bonus XP for streaks of correct answers
 *    - Mastery bonuses when words graduate to higher boxes
 *    - Session completion bonuses
 *
 * 2. SKILL INTEGRATION
 *    - Vocabulary mastery contributes to Discipline skill
 *    - Using learned words in writing (Bloom) gives bonus XP
 *
 * 3. STATISTICS & PROGRESS
 *    - Track learning velocity
 *    - Predict mastery dates
 *    - Celebrate milestones
 *
 * 4. ADAPTIVE DIFFICULTY
 *    - Adjust card selection based on performance
 *    - Balance between new and review cards
 */

/**
 * Review session types with different XP multipliers.
 */
enum class ReviewSessionType(
    val displayName: String,
    val description: String,
    val baseXpPerCard: Int,
    val correctBonusXp: Int,
    val sessionCompletionBonus: Int
) {
    QUICK_REVIEW(
        displayName = "Quick Review",
        description = "Review 5 cards in under 2 minutes",
        baseXpPerCard = 3,
        correctBonusXp = 2,
        sessionCompletionBonus = 5
    ),
    STANDARD_REVIEW(
        displayName = "Review Session",
        description = "Review 10-20 due cards",
        baseXpPerCard = 5,
        correctBonusXp = 3,
        sessionCompletionBonus = 15
    ),
    DEEP_PRACTICE(
        displayName = "Deep Practice",
        description = "Extended session with 20+ cards",
        baseXpPerCard = 5,
        correctBonusXp = 4,
        sessionCompletionBonus = 30
    ),
    NEW_WORDS(
        displayName = "Learn New Words",
        description = "Introduction to new vocabulary",
        baseXpPerCard = 8,
        correctBonusXp = 5,
        sessionCompletionBonus = 20
    ),
    CHALLENGE_MODE(
        displayName = "Challenge Mode",
        description = "Time-limited high-stakes review",
        baseXpPerCard = 10,
        correctBonusXp = 8,
        sessionCompletionBonus = 50
    )
}

/**
 * Represents a vocabulary card's learning state for gamification purposes.
 */
data class VocabularyCardState(
    val wordId: Long,
    val word: String,
    val meaning: String,
    val boxLevel: Int, // 1-5 Leitner box
    val easeFactor: Float,
    val interval: Int, // Days until next review
    val repetitions: Int,
    val totalReviews: Int,
    val correctReviews: Int,
    val correctStreak: Int,
    val longestStreak: Int,
    val nextReviewDate: Long,
    val lastReviewDate: Long?,
    val firstLearnedDate: Long?,
    val masteredDate: Long?,
    val usedInContext: Boolean,
    val timesUsedInContext: Int
) {
    /**
     * Mastery percentage (0-100).
     */
    val masteryPercent: Int
        get() = when (boxLevel) {
            1 -> 10
            2 -> 30
            3 -> 55
            4 -> 80
            5 -> 100
            else -> 0
        }

    /**
     * Is this word mastered?
     */
    val isMastered: Boolean
        get() = boxLevel >= 5 && repetitions >= 3

    /**
     * Is this word due for review?
     */
    val isDue: Boolean
        get() = System.currentTimeMillis() >= nextReviewDate

    /**
     * Days overdue (negative if not yet due).
     */
    val daysOverdue: Int
        get() {
            val diff = System.currentTimeMillis() - nextReviewDate
            return (diff / (24 * 60 * 60 * 1000)).toInt()
        }

    /**
     * Accuracy percentage.
     */
    val accuracy: Float
        get() = if (totalReviews > 0) (correctReviews.toFloat() / totalReviews) * 100 else 0f

    /**
     * Difficulty category based on ease factor.
     */
    val difficulty: CardDifficulty
        get() = when {
            easeFactor >= 2.8f -> CardDifficulty.VERY_EASY
            easeFactor >= 2.4f -> CardDifficulty.EASY
            easeFactor >= 2.0f -> CardDifficulty.MEDIUM
            easeFactor >= 1.6f -> CardDifficulty.HARD
            else -> CardDifficulty.VERY_HARD
        }
}

/**
 * Card difficulty levels.
 */
enum class CardDifficulty(
    val displayName: String,
    val xpMultiplier: Float
) {
    VERY_EASY("Very Easy", 0.8f),
    EASY("Easy", 1.0f),
    MEDIUM("Medium", 1.2f),
    HARD("Hard", 1.5f),
    VERY_HARD("Very Hard", 2.0f)
}

/**
 * Result of a single card review.
 */
data class CardReviewResult(
    val wordId: Long,
    val wasCorrect: Boolean,
    val responseQuality: Int, // 0-5 SM-2 quality
    val responseTimeMs: Long,
    val previousBoxLevel: Int,
    val newBoxLevel: Int,
    val xpEarned: Int,
    val bonusXp: Int,
    val streakBonus: Int,
    val totalXp: Int,
    val celebrationMessage: String?,
    val milestoneReached: VocabularyMilestone?
) {
    val boxLevelChanged: Boolean
        get() = newBoxLevel != previousBoxLevel

    val wasPromoted: Boolean
        get() = newBoxLevel > previousBoxLevel

    val wasDemoted: Boolean
        get() = newBoxLevel < previousBoxLevel
}

/**
 * Vocabulary learning milestones.
 */
enum class VocabularyMilestone(
    val id: String,
    val title: String,
    val description: String,
    val xpBonus: Int,
    val celebrationDuration: Int
) {
    FIRST_WORD(
        "first_word",
        "First Word Learned",
        "You've reviewed your first word!",
        10,
        2000
    ),
    TEN_WORDS(
        "ten_words",
        "Growing Vocabulary",
        "You've learned 10 words!",
        25,
        2500
    ),
    FIFTY_WORDS(
        "fifty_words",
        "Word Collector",
        "You've learned 50 words!",
        75,
        3000
    ),
    HUNDRED_WORDS(
        "hundred_words",
        "Centurion",
        "You've learned 100 words!",
        150,
        3500
    ),
    FIRST_MASTERY(
        "first_mastery",
        "First Mastery",
        "You've mastered your first word!",
        50,
        3000
    ),
    TEN_MASTERED(
        "ten_mastered",
        "Vocabulary Builder",
        "You've mastered 10 words!",
        100,
        3500
    ),
    FIFTY_MASTERED(
        "fifty_mastered",
        "Word Master",
        "You've mastered 50 words!",
        250,
        4000
    ),
    PERFECT_SESSION(
        "perfect_session",
        "Perfect Session",
        "100% accuracy in a review session!",
        30,
        2500
    ),
    STREAK_TEN(
        "streak_ten",
        "On Fire",
        "10 correct answers in a row!",
        20,
        2000
    ),
    STREAK_TWENTY_FIVE(
        "streak_twenty_five",
        "Unstoppable",
        "25 correct answers in a row!",
        50,
        2500
    ),
    STREAK_FIFTY(
        "streak_fifty",
        "Legendary Streak",
        "50 correct answers in a row!",
        100,
        3000
    ),
    DAILY_GOAL(
        "daily_goal",
        "Daily Goal",
        "Completed your daily review goal!",
        15,
        2000
    ),
    WEEK_STREAK(
        "week_streak",
        "Weekly Dedication",
        "Reviewed every day for a week!",
        100,
        3500
    )
}

/**
 * Complete session result with XP breakdown.
 */
data class ReviewSessionResult(
    val sessionType: ReviewSessionType,
    val cardsReviewed: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val accuracy: Float,
    val totalXpEarned: Int,
    val skillXp: Map<Skill, Int>,
    val baseXp: Int,
    val correctBonusXp: Int,
    val streakBonusXp: Int,
    val sessionBonusXp: Int,
    val perfectBonusXp: Int,
    val durationMs: Long,
    val averageResponseTimeMs: Long,
    val longestCorrectStreak: Int,
    val wordsPromoted: Int,
    val wordsMastered: Int,
    val milestonesReached: List<VocabularyMilestone>,
    val celebrationMessage: String
) {
    val isPerfect: Boolean
        get() = correctCount > 0 && incorrectCount == 0

    val durationMinutes: Int
        get() = (durationMs / 60000).toInt()

    val cardsPerMinute: Float
        get() = if (durationMs > 0) cardsReviewed.toFloat() / (durationMs / 60000f) else 0f
}

/**
 * User's overall vocabulary statistics.
 */
data class VocabularyStats(
    val totalWordsLearned: Int,
    val totalWordsMastered: Int,
    val wordsDueToday: Int,
    val wordsOverdue: Int,
    val totalReviews: Int,
    val totalCorrect: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val averageAccuracy: Float,
    val averageEaseFactor: Float,
    val reviewsByBox: Map<Int, Int>, // Box level -> count
    val totalXpFromVocabulary: Int,
    val dailyGoalProgress: Int,
    val dailyGoalTarget: Int,
    val lastReviewDate: LocalDate?,
    val daysSinceLastReview: Int,
    val predictedMasteryDate: LocalDate?, // When all current words will be mastered
    val learningVelocity: Float // Words mastered per week
) {
    val dailyGoalPercent: Float
        get() = if (dailyGoalTarget > 0) (dailyGoalProgress.toFloat() / dailyGoalTarget).coerceAtMost(1f) else 0f

    val overallMasteryPercent: Float
        get() = if (totalWordsLearned > 0) (totalWordsMastered.toFloat() / totalWordsLearned) * 100 else 0f

    val isOnStreak: Boolean
        get() = daysSinceLastReview <= 1
}

/**
 * Daily review goal configuration.
 */
data class DailyReviewGoal(
    val targetCards: Int,
    val targetNewWords: Int,
    val bonusXpForCompletion: Int,
    val streakMultiplier: Float // Bonus multiplier for maintaining daily review streak
) {
    companion object {
        val BEGINNER = DailyReviewGoal(10, 3, 15, 1.0f)
        val INTERMEDIATE = DailyReviewGoal(20, 5, 25, 1.1f)
        val ADVANCED = DailyReviewGoal(30, 7, 40, 1.2f)
        val INTENSIVE = DailyReviewGoal(50, 10, 60, 1.3f)

        fun forLevel(disciplineLevel: Int): DailyReviewGoal = when {
            disciplineLevel >= 15 -> INTENSIVE
            disciplineLevel >= 10 -> ADVANCED
            disciplineLevel >= 5 -> INTERMEDIATE
            else -> BEGINNER
        }
    }
}

/**
 * Calculator for SRS gamification rewards.
 */
object SRSCalculator {

    /**
     * Calculate XP for a single card review.
     */
    fun calculateCardXp(
        sessionType: ReviewSessionType,
        wasCorrect: Boolean,
        responseQuality: Int,
        difficulty: CardDifficulty,
        currentStreak: Int,
        wasPromoted: Boolean,
        wasMastered: Boolean
    ): CardXpBreakdown {
        // Base XP
        val baseXp = if (wasCorrect) sessionType.baseXpPerCard else 0

        // Correct bonus
        val correctBonus = if (wasCorrect) sessionType.correctBonusXp else 0

        // Difficulty multiplier
        val difficultyMultiplier = difficulty.xpMultiplier
        val difficultyBonus = if (wasCorrect) {
            ((baseXp + correctBonus) * (difficultyMultiplier - 1)).toInt()
        } else 0

        // Streak bonus (increases every 5 correct in a row)
        val streakBonus = if (wasCorrect && currentStreak >= 5) {
            val streakTier = (currentStreak / 5).coerceAtMost(5)
            streakTier * 2
        } else 0

        // Promotion bonus (moving to higher box)
        val promotionBonus = when {
            wasMastered -> 25 // First time mastering a word
            wasPromoted -> 10
            else -> 0
        }

        // Quality bonus (for perfect recall)
        val qualityBonus = if (responseQuality == 5) 3 else 0

        val total = baseXp + correctBonus + difficultyBonus + streakBonus + promotionBonus + qualityBonus

        return CardXpBreakdown(
            baseXp = baseXp,
            correctBonus = correctBonus,
            difficultyBonus = difficultyBonus,
            streakBonus = streakBonus,
            promotionBonus = promotionBonus,
            qualityBonus = qualityBonus,
            totalXp = total
        )
    }

    /**
     * Calculate session completion XP.
     */
    fun calculateSessionXp(
        sessionType: ReviewSessionType,
        cardsReviewed: Int,
        correctCount: Int,
        longestStreak: Int,
        hasActiveStreak: Boolean,
        streakDays: Int
    ): SessionXpBreakdown {
        val accuracy = if (cardsReviewed > 0) correctCount.toFloat() / cardsReviewed else 0f

        // Session completion bonus
        val sessionBonus = sessionType.sessionCompletionBonus

        // Perfect session bonus
        val perfectBonus = if (accuracy >= 1f && cardsReviewed >= 5) 20 else 0

        // High accuracy bonus (90%+)
        val accuracyBonus = when {
            accuracy >= 0.95f -> 15
            accuracy >= 0.90f -> 10
            accuracy >= 0.80f -> 5
            else -> 0
        }

        // Streak multiplier for daily review streak
        val streakMultiplier = if (hasActiveStreak) {
            1f + (streakDays / 30f).coerceAtMost(0.5f) // Up to 50% bonus at 30+ day streak
        } else 1f

        val subtotal = sessionBonus + perfectBonus + accuracyBonus
        val total = (subtotal * streakMultiplier).toInt()

        return SessionXpBreakdown(
            sessionBonus = sessionBonus,
            perfectBonus = perfectBonus,
            accuracyBonus = accuracyBonus,
            streakMultiplier = streakMultiplier,
            totalXp = total
        )
    }

    /**
     * Get celebration message for card review.
     */
    fun getCardCelebrationMessage(
        wasCorrect: Boolean,
        responseQuality: Int,
        currentStreak: Int,
        wasPromoted: Boolean,
        wasMastered: Boolean,
        newBoxLevel: Int
    ): String? = when {
        wasMastered -> "Word mastered! 🎓"
        wasPromoted && newBoxLevel == 4 -> "Almost there! Box 4 reached."
        wasPromoted && newBoxLevel == 3 -> "Moving up! Now in Box 3."
        currentStreak == 10 -> "10 in a row! 🔥"
        currentStreak == 25 -> "25 streak! Unstoppable! 💪"
        currentStreak == 50 -> "LEGENDARY 50 STREAK! 🌟"
        responseQuality == 5 -> "Perfect recall!"
        wasCorrect -> null // No special message for normal correct
        else -> null
    }

    /**
     * Get session completion message.
     */
    fun getSessionCelebrationMessage(
        accuracy: Float,
        cardsReviewed: Int,
        wordsMastered: Int,
        longestStreak: Int
    ): String = when {
        accuracy >= 1f && cardsReviewed >= 10 -> "Perfect session! Flawless performance! 🌟"
        accuracy >= 1f -> "100% accuracy! Great job!"
        wordsMastered > 0 -> "You mastered $wordsMastered word${if (wordsMastered > 1) "s" else ""}! 🎓"
        accuracy >= 0.9f -> "Excellent session! ${(accuracy * 100).toInt()}% accuracy!"
        accuracy >= 0.8f -> "Good session! Keep practicing!"
        longestStreak >= 10 -> "Nice $longestStreak card streak!"
        else -> "Session complete! Every review counts."
    }

    /**
     * Check for milestones reached.
     */
    fun checkMilestones(
        totalWordsLearned: Int,
        totalWordsMastered: Int,
        sessionAccuracy: Float,
        currentStreak: Int,
        dailyGoalMet: Boolean,
        weeklyStreakDays: Int
    ): List<VocabularyMilestone> {
        val milestones = mutableListOf<VocabularyMilestone>()

        // Word count milestones
        when (totalWordsLearned) {
            1 -> milestones.add(VocabularyMilestone.FIRST_WORD)
            10 -> milestones.add(VocabularyMilestone.TEN_WORDS)
            50 -> milestones.add(VocabularyMilestone.FIFTY_WORDS)
            100 -> milestones.add(VocabularyMilestone.HUNDRED_WORDS)
        }

        // Mastery milestones
        when (totalWordsMastered) {
            1 -> milestones.add(VocabularyMilestone.FIRST_MASTERY)
            10 -> milestones.add(VocabularyMilestone.TEN_MASTERED)
            50 -> milestones.add(VocabularyMilestone.FIFTY_MASTERED)
        }

        // Perfect session
        if (sessionAccuracy >= 1f) {
            milestones.add(VocabularyMilestone.PERFECT_SESSION)
        }

        // Streak milestones
        when (currentStreak) {
            10 -> milestones.add(VocabularyMilestone.STREAK_TEN)
            25 -> milestones.add(VocabularyMilestone.STREAK_TWENTY_FIVE)
            50 -> milestones.add(VocabularyMilestone.STREAK_FIFTY)
        }

        // Daily goal
        if (dailyGoalMet) {
            milestones.add(VocabularyMilestone.DAILY_GOAL)
        }

        // Weekly streak
        if (weeklyStreakDays == 7) {
            milestones.add(VocabularyMilestone.WEEK_STREAK)
        }

        return milestones
    }

    /**
     * Calculate predicted mastery date based on learning velocity.
     */
    fun calculatePredictedMasteryDate(
        wordsToMaster: Int,
        wordsPerWeek: Float
    ): LocalDate? {
        if (wordsPerWeek <= 0 || wordsToMaster <= 0) return null
        val weeksNeeded = (wordsToMaster / wordsPerWeek).toLong()
        return LocalDate.now().plusWeeks(weeksNeeded)
    }

    /**
     * Calculate learning velocity (words mastered per week).
     */
    fun calculateLearningVelocity(
        wordsMasteredLastMonth: Int
    ): Float {
        return wordsMasteredLastMonth / 4f // Approximate weeks in a month
    }

    /**
     * Determine optimal session type based on current state.
     */
    fun recommendSessionType(
        wordsDue: Int,
        wordsOverdue: Int,
        availableMinutes: Int,
        newWordsToLearn: Int
    ): ReviewSessionType = when {
        availableMinutes < 3 -> ReviewSessionType.QUICK_REVIEW
        wordsOverdue > 10 -> ReviewSessionType.DEEP_PRACTICE
        newWordsToLearn > 0 && wordsDue < 5 -> ReviewSessionType.NEW_WORDS
        wordsDue > 20 -> ReviewSessionType.STANDARD_REVIEW
        else -> ReviewSessionType.STANDARD_REVIEW
    }

    /**
     * Get motivational message based on stats.
     */
    fun getMotivationalMessage(stats: VocabularyStats): String = when {
        stats.wordsDueToday == 0 && stats.dailyGoalProgress >= stats.dailyGoalTarget ->
            "All caught up! You've met your daily goal. 🌟"
        stats.wordsOverdue > 10 ->
            "You have ${stats.wordsOverdue} overdue words. Let's catch up!"
        stats.currentStreak >= 7 ->
            "${stats.currentStreak}-day streak! Keep the momentum going!"
        stats.totalWordsMastered > 0 && stats.overallMasteryPercent >= 50 ->
            "Over half your vocabulary is mastered! Great progress!"
        stats.dailyGoalProgress > 0 ->
            "${stats.dailyGoalProgress}/${stats.dailyGoalTarget} cards reviewed today."
        stats.wordsDueToday > 0 ->
            "${stats.wordsDueToday} words waiting for you today."
        else ->
            "Ready to expand your vocabulary?"
    }
}

/**
 * Breakdown of XP earned from a single card.
 */
data class CardXpBreakdown(
    val baseXp: Int,
    val correctBonus: Int,
    val difficultyBonus: Int,
    val streakBonus: Int,
    val promotionBonus: Int,
    val qualityBonus: Int,
    val totalXp: Int
)

/**
 * Breakdown of XP earned from session completion.
 */
data class SessionXpBreakdown(
    val sessionBonus: Int,
    val perfectBonus: Int,
    val accuracyBonus: Int,
    val streakMultiplier: Float,
    val totalXp: Int
)

/**
 * Card selection strategy for review sessions.
 */
enum class CardSelectionStrategy(
    val displayName: String,
    val description: String
) {
    DUE_FIRST(
        "Due First",
        "Prioritize cards that are due or overdue"
    ),
    STRUGGLING(
        "Focus on Difficult",
        "Prioritize cards with low accuracy"
    ),
    BALANCED(
        "Balanced Mix",
        "Mix of due cards and reinforcement"
    ),
    NEW_WORDS_PRIORITY(
        "Learn New",
        "Prioritize learning new words"
    ),
    RANDOM(
        "Random",
        "Random selection from all cards"
    )
}

/**
 * Configuration for a review session.
 */
data class ReviewSessionConfig(
    val sessionType: ReviewSessionType,
    val maxCards: Int,
    val maxNewCards: Int,
    val selectionStrategy: CardSelectionStrategy,
    val timeLimitMinutes: Int?,
    val showHints: Boolean,
    val allowSkip: Boolean,
    val autoAdvance: Boolean,
    val autoAdvanceDelayMs: Long
) {
    companion object {
        val DEFAULT = ReviewSessionConfig(
            sessionType = ReviewSessionType.STANDARD_REVIEW,
            maxCards = 20,
            maxNewCards = 5,
            selectionStrategy = CardSelectionStrategy.DUE_FIRST,
            timeLimitMinutes = null,
            showHints = false,
            allowSkip = true,
            autoAdvance = false,
            autoAdvanceDelayMs = 1500
        )

        val QUICK = ReviewSessionConfig(
            sessionType = ReviewSessionType.QUICK_REVIEW,
            maxCards = 5,
            maxNewCards = 0,
            selectionStrategy = CardSelectionStrategy.DUE_FIRST,
            timeLimitMinutes = 2,
            showHints = false,
            allowSkip = false,
            autoAdvance = true,
            autoAdvanceDelayMs = 1000
        )

        val INTENSIVE = ReviewSessionConfig(
            sessionType = ReviewSessionType.DEEP_PRACTICE,
            maxCards = 50,
            maxNewCards = 10,
            selectionStrategy = CardSelectionStrategy.BALANCED,
            timeLimitMinutes = null,
            showHints = false,
            allowSkip = true,
            autoAdvance = false,
            autoAdvanceDelayMs = 1500
        )
    }
}
