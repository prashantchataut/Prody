package com.prody.prashant.domain.identity

/**
 * [ProdyRanks] - Wisdom-inspired rank system for Prody
 *
 * Defines the hierarchical rank system based on philosophical and wisdom traditions.
 * Each rank represents a stage in the user's journey of self-mastery within the app.
 * Points are earned through consistent engagement with learning, journaling, and reflection.
 *
 * The rank names draw from diverse wisdom traditions:
 * - Seeker: Beginning the path of inquiry
 * - Initiate: Ancient mystery school traditions
 * - Student of Life: Socratic tradition of perpetual learning
 * - Practitioner: Stoic philosophy of praxis (action-based wisdom)
 * - Contemplative: Meditative traditions East and West
 * - Philosopher: Greek tradition of loving wisdom
 * - Sage: Universal archetype of the wise elder
 * - Luminary: Latin origin meaning "bearer of light"
 * - Wayfinder: Polynesian navigation wisdom
 * - Awakened: Buddhist concept of Bodhi (enlightenment)
 *
 * Example usage:
 * ```
 * val currentRank = ProdyRanks.Rank.fromPoints(userPoints)
 * val nextRank = ProdyRanks.Rank.getNextRank(currentRank)
 * ```
 */
object ProdyRanks {

    /**
     * Rank enumeration representing stages in the journey of self-mastery.
     *
     * @property id Unique identifier for persistence and lookup
     * @property displayName User-facing name of the rank
     * @property description Brief explanation of what the rank represents
     * @property requiredPoints Minimum points needed to achieve this rank
     * @property philosophicalOrigin Historical or philosophical source of the rank name
     */
    enum class Rank(
        val id: String,
        val displayName: String,
        val description: String,
        val requiredPoints: Int,
        val philosophicalOrigin: String
    ) {
        SEEKER(
            id = "seeker",
            displayName = "Seeker",
            description = "One who has begun the path of inquiry",
            requiredPoints = 0,
            philosophicalOrigin = "The first step in any journey of wisdom"
        ),
        INITIATE(
            id = "initiate",
            displayName = "Initiate",
            description = "One who has committed to the practice",
            requiredPoints = 200,
            philosophicalOrigin = "Ancient mystery school traditions"
        ),
        STUDENT(
            id = "student",
            displayName = "Student of Life",
            description = "One who learns from every experience",
            requiredPoints = 500,
            philosophicalOrigin = "Socratic tradition of perpetual learning"
        ),
        PRACTITIONER(
            id = "practitioner",
            displayName = "Practitioner",
            description = "One who embodies wisdom in daily action",
            requiredPoints = 1000,
            philosophicalOrigin = "Stoic philosophy of praxis"
        ),
        CONTEMPLATIVE(
            id = "contemplative",
            displayName = "Contemplative",
            description = "One who sees deeply into the nature of things",
            requiredPoints = 1500,
            philosophicalOrigin = "Meditative traditions East and West"
        ),
        PHILOSOPHER(
            id = "philosopher",
            displayName = "Philosopher",
            description = "A lover of wisdom who questions and reflects",
            requiredPoints = 2500,
            philosophicalOrigin = "Greek: philos (lover) + sophia (wisdom)"
        ),
        SAGE(
            id = "sage",
            displayName = "Sage",
            description = "One whose wisdom has been tempered by time",
            requiredPoints = 4000,
            philosophicalOrigin = "Universal archetype of the wise elder"
        ),
        LUMINARY(
            id = "luminary",
            displayName = "Luminary",
            description = "One whose presence illuminates the path for others",
            requiredPoints = 6000,
            philosophicalOrigin = "Latin: lumen - light"
        ),
        WAYFINDER(
            id = "wayfinder",
            displayName = "Wayfinder",
            description = "One who navigates by inner stars",
            requiredPoints = 8500,
            philosophicalOrigin = "Polynesian navigation wisdom"
        ),
        AWAKENED(
            id = "awakened",
            displayName = "Awakened",
            description = "One who sees clearly and lives fully",
            requiredPoints = 12000,
            philosophicalOrigin = "Buddhist concept of Bodhi"
        );

        companion object {
            /**
             * Determines the appropriate rank for a given point total.
             *
             * @param points The user's current total points
             * @return The highest rank the user has achieved based on their points
             */
            fun fromPoints(points: Int): Rank {
                return entries.lastOrNull { points >= it.requiredPoints } ?: SEEKER
            }

            /**
             * Gets the next rank in the progression.
             *
             * @param current The user's current rank
             * @return The next rank in the progression, or null if at maximum rank
             */
            fun getNextRank(current: Rank): Rank? {
                val currentIndex = entries.indexOf(current)
                return entries.getOrNull(currentIndex + 1)
            }

            /**
             * Finds a rank by its unique identifier.
             *
             * @param id The rank's unique identifier
             * @return The matching rank, or null if not found
             */
            fun fromId(id: String): Rank? {
                return entries.find { it.id == id }
            }

            /**
             * Calculates progress toward the next rank as a percentage.
             *
             * @param points The user's current total points
             * @return Float between 0.0 and 1.0 representing progress to next rank,
             *         or 1.0 if already at maximum rank
             */
            fun calculateProgressToNextRank(points: Int): Float {
                val currentRank = fromPoints(points)
                val nextRank = getNextRank(currentRank) ?: return 1f

                val progressInRank = points - currentRank.requiredPoints
                val pointsNeeded = nextRank.requiredPoints - currentRank.requiredPoints

                return if (pointsNeeded > 0) {
                    (progressInRank.toFloat() / pointsNeeded).coerceIn(0f, 1f)
                } else {
                    1f
                }
            }

            /**
             * Calculates the points needed to reach the next rank.
             *
             * @param points The user's current total points
             * @return The number of points needed to advance, or 0 if at maximum rank
             */
            fun pointsToNextRank(points: Int): Int {
                val currentRank = fromPoints(points)
                val nextRank = getNextRank(currentRank) ?: return 0
                return (nextRank.requiredPoints - points).coerceAtLeast(0)
            }
        }
    }

    /**
     * Point values awarded for various user activities.
     * These values are designed to encourage balanced engagement across all features.
     */
    object PointValues {
        const val WORD_LEARNED = 10
        const val QUOTE_READ = 5
        const val PROVERB_EXPLORED = 8
        const val JOURNAL_ENTRY = 25
        const val BUDDHA_CONVERSATION = 15
        const val FUTURE_LETTER_SENT = 50
        const val FUTURE_LETTER_RECEIVED = 30
        const val DAILY_CHECK_IN = 5
        const val STREAK_BONUS_PER_DAY = 2
        const val ACHIEVEMENT_UNLOCKED_BONUS = 50
        const val MOOD_LOGGED = 5
        const val REVIEW_COMPLETED = 15
    }

    /**
     * Messages for rank advancement celebrations.
     * These messages are literary and philosophical in tone, avoiding gaming language.
     */
    object RankMessages {
        fun advancementMessage(newRank: Rank): String {
            return when (newRank) {
                Rank.SEEKER -> "Your journey begins. Every great sage was once where you stand now."
                Rank.INITIATE -> "You have committed to the path. The practice deepens with each step."
                Rank.STUDENT -> "A student sees lessons everywhere. Your eyes are opening."
                Rank.PRACTITIONER -> "Wisdom without action is merely theory. You embody what you learn."
                Rank.CONTEMPLATIVE -> "In stillness, you find depth. Your understanding grows profound."
                Rank.PHILOSOPHER -> "You have become a lover of wisdom. Questions matter as much as answers."
                Rank.SAGE -> "Time has tempered your understanding. Others may learn from your journey."
                Rank.LUMINARY -> "Your presence illuminates the path. You carry light for those who follow."
                Rank.WAYFINDER -> "You navigate by inner stars. The way forward is clear to you now."
                Rank.AWAKENED -> "You have reached the summit of this journey. True seeing is yours."
            }
        }

        fun greetingForRank(rank: Rank, displayName: String): String {
            return when (rank) {
                Rank.SEEKER -> "Welcome, $displayName. The path awaits."
                Rank.INITIATE -> "Greetings, Initiate $displayName. Your commitment is noted."
                Rank.STUDENT -> "Welcome back, Student $displayName. What will you learn today?"
                Rank.PRACTITIONER -> "Good to see you, Practitioner $displayName. The practice continues."
                Rank.CONTEMPLATIVE -> "Welcome, Contemplative $displayName. Depth awaits your attention."
                Rank.PHILOSOPHER -> "Greetings, Philosopher $displayName. What questions arise today?"
                Rank.SAGE -> "Welcome, Sage $displayName. Your wisdom grows with each return."
                Rank.LUMINARY -> "Greetings, Luminary $displayName. Your light guides others."
                Rank.WAYFINDER -> "Welcome, Wayfinder $displayName. The stars align for your journey."
                Rank.AWAKENED -> "Greetings, Awakened One. You see clearly now."
            }
        }
    }
}
