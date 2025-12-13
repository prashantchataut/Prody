package com.prody.prashant.domain.identity

import java.util.Calendar

/**
 * [CelebrationMessages] - Elegant messaging system for Prody
 *
 * Provides literary, philosophical messages for all user interactions.
 * All messages are designed to be warm but not effusive, encouraging but not patronizing.
 *
 * Key principles:
 * - No emojis anywhere
 * - No gaming language ("Epic!", "Awesome!", "You crushed it!")
 * - No excessive exclamation marks
 * - Reference wisdom traditions when appropriate
 * - Respect the user's intelligence
 * - Use proper grammar and punctuation
 *
 * Example usage:
 * ```
 * val greeting = CelebrationMessages.dailyGreeting("John", Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
 * val achievementMsg = CelebrationMessages.achievementUnlocked("First Light", "The first word is a doorway.")
 * ```
 */
object CelebrationMessages {

    // ===== ACHIEVEMENT MESSAGES =====

    /**
     * Creates a message for when an achievement is unlocked.
     *
     * @param achievementName The name of the unlocked achievement
     * @param celebrationMessage The achievement's celebration message
     * @return Formatted achievement unlock message
     */
    fun achievementUnlocked(achievementName: String, celebrationMessage: String): String {
        return "Achievement Unlocked: $achievementName\n$celebrationMessage"
    }

    /**
     * Creates a brief notification message for achievement unlock.
     *
     * @param achievementName The name of the unlocked achievement
     * @return Brief notification message
     */
    fun achievementNotification(achievementName: String): String {
        return "New milestone achieved: $achievementName"
    }

    // ===== LEVEL AND RANK MESSAGES =====

    /**
     * Creates a message for level up events.
     *
     * @param newLevel The new level reached
     * @param newRank The rank associated with the new level (if changed)
     * @return Formatted level up message
     */
    fun levelUp(newLevel: Int, newRank: String? = null): String {
        return if (newRank != null) {
            "You have risen to Level $newLevel\nYou are now: $newRank"
        } else {
            "You have risen to Level $newLevel\nThe journey continues."
        }
    }

    /**
     * Creates a message for rank advancement.
     *
     * @param newRank The new rank display name
     * @param description The rank's description
     * @return Formatted rank advancement message
     */
    fun rankAdvancement(newRank: String, description: String): String {
        return "You have become: $newRank\n$description"
    }

    /**
     * Creates a progress message toward next rank.
     *
     * @param currentRank Current rank name
     * @param nextRank Next rank name
     * @param pointsRemaining Points needed to reach next rank
     * @return Progress message
     */
    fun rankProgress(currentRank: String, nextRank: String, pointsRemaining: Int): String {
        return "$pointsRemaining points until you become $nextRank"
    }

    // ===== STREAK MESSAGES =====

    /**
     * Creates a message for streak milestones.
     *
     * @param days Number of days in the streak
     * @return Appropriate milestone message
     */
    fun streakMilestone(days: Int): String {
        return when {
            days == 3 -> "Three days of presence. The kindling catches flame."
            days == 7 -> "Seven days of presence. The practice takes root."
            days == 14 -> "A fortnight of dedication. You are forging a habit."
            days == 21 -> "Twenty-one days. Science says habits form here. You are living proof."
            days == 30 -> "One moon's cycle complete. The way is becoming clear."
            days == 60 -> "Two moons have witnessed your dedication."
            days == 90 -> "A season of growth. Transformation is underway."
            days == 180 -> "Half a year of presence. Remarkable dedication."
            days == 365 -> "One year. An extraordinary commitment to your own becoming."
            days % 100 == 0 -> "$days days of presence. A remarkable journey."
            days % 50 == 0 -> "$days days. Your consistency speaks volumes."
            else -> "$days days and counting. Keep walking the path."
        }
    }

    /**
     * Creates a notification reminder to maintain streak.
     *
     * @param currentStreak Current streak count
     * @return Reminder message
     */
    fun streakReminder(currentStreak: Int): String {
        val messages = listOf(
            "Your streak of $currentStreak days continues. Keep the flame alive.",
            "One more day of presence maintains what you've built.",
            "The chain of $currentStreak days remains unbroken.",
            "$currentStreak days of dedication - another awaits."
        )
        return messages[currentStreak % messages.size]
    }

    /**
     * Creates a message for when a streak is lost.
     *
     * @param previousStreak The streak count that was lost
     * @return Encouraging message for streak loss
     */
    fun streakLost(previousStreak: Int): String {
        return when {
            previousStreak >= 30 -> "A $previousStreak-day streak has ended. What matters now is the next step forward."
            previousStreak >= 7 -> "The streak has ended, but not the journey. Begin again."
            else -> "Every return is a new beginning. The path awaits."
        }
    }

    // ===== BANNER MESSAGES =====

    /**
     * Creates a message for banner unlock.
     *
     * @param bannerName The name of the unlocked banner
     * @return Banner unlock message
     */
    fun bannerUnlocked(bannerName: String): String {
        return "New banner available: $bannerName"
    }

    /**
     * Creates a detailed message for banner unlock.
     *
     * @param bannerName The name of the unlocked banner
     * @param description The banner's description
     * @return Detailed banner unlock message
     */
    fun bannerUnlockedDetailed(bannerName: String, description: String): String {
        return "A new banner graces your collection: $bannerName\n$description"
    }

    // ===== DAILY GREETINGS =====

    /**
     * Creates a time-aware daily greeting.
     *
     * @param displayName User's display name
     * @param hour Current hour (0-23)
     * @return Time-appropriate greeting
     */
    fun dailyGreeting(displayName: String, hour: Int): String {
        return when (hour) {
            in 5..11 -> "Good morning, $displayName. A new day awaits your intention."
            in 12..17 -> "Good afternoon, $displayName. The day is rich with possibility."
            in 18..21 -> "Good evening, $displayName. Time for reflection approaches."
            else -> "The quiet hours find you here, $displayName. Welcome."
        }
    }

    /**
     * Creates a time-aware greeting with rank.
     *
     * @param displayName User's display name
     * @param rank User's current rank
     * @param hour Current hour (0-23)
     * @return Time and rank appropriate greeting
     */
    fun dailyGreetingWithRank(displayName: String, rank: ProdyRanks.Rank, hour: Int): String {
        return ProdyRanks.RankMessages.greetingForRank(rank, displayName)
    }

    /**
     * Gets the current hour for greeting selection.
     */
    fun getCurrentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    // ===== NOTIFICATION MESSAGES =====

    /**
     * Daily reminder notification messages.
     */
    object DailyReminders {
        val messages = listOf(
            "A moment of reflection awaits.",
            "Today's wisdom is ready for you.",
            "The practice continues, one day at a time.",
            "Your future self will thank your present self.",
            "Growth happens in small, consistent steps.",
            "A few moments of presence can shift your entire day.",
            "The journal awaits your thoughts.",
            "What wisdom will you gather today?"
        )

        /**
         * Gets a reminder message, cycling through options.
         */
        fun getMessage(dayIndex: Int): String = messages[dayIndex % messages.size]

        /**
         * Gets a random reminder message.
         */
        fun getRandomMessage(): String = messages.random()
    }

    /**
     * Re-engagement messages for users returning after absence.
     */
    object ReengagementMessages {
        val messages = listOf(
            "We've kept your place on the path. Welcome back.",
            "Every return is a new beginning.",
            "The practice awaits whenever you're ready.",
            "Growth isn't linear. What matters is returning.",
            "Time away can bring fresh perspective. Welcome back.",
            "The journey continues from wherever you stand."
        )

        /**
         * Gets a re-engagement message based on days absent.
         *
         * @param daysAbsent Number of days since last activity
         * @return Appropriate re-engagement message
         */
        fun getMessage(daysAbsent: Int): String {
            return when {
                daysAbsent <= 3 -> "Welcome back. The path awaited your return."
                daysAbsent <= 7 -> "A week away, but not forgotten. Welcome back."
                daysAbsent <= 30 -> "The practice awaits whenever you're ready. Welcome back."
                else -> "Time away can bring fresh perspective. Your journey continues."
            }
        }
    }

    /**
     * Future letter delivery notification messages.
     */
    object FutureLetterMessages {
        val deliveryMessages = listOf(
            "A message from your past self has arrived.",
            "You once wrote words for this moment. They await you.",
            "Your past self had something to share. Will you listen?",
            "A letter has traveled through time to reach you.",
            "Words from who you were have found their way to who you are."
        )

        /**
         * Gets a random delivery notification message.
         */
        fun getDeliveryMessage(): String = deliveryMessages.random()

        /**
         * Creates a message for when a future letter is sent.
         *
         * @param deliveryDate Human-readable delivery date
         * @return Confirmation message
         */
        fun letterSent(deliveryDate: String): String {
            return "Your message travels toward $deliveryDate. May it arrive with meaning."
        }
    }

    // ===== LEARNING MESSAGES =====

    /**
     * Messages for vocabulary learning milestones.
     */
    object LearningMessages {
        /**
         * Creates a message for word learned.
         *
         * @param wordCount Total words learned
         * @return Appropriate message
         */
        fun wordLearned(wordCount: Int): String {
            return when {
                wordCount == 1 -> "Your first word. The journey of language begins."
                wordCount == 10 -> "Ten words. A foundation is being laid."
                wordCount == 25 -> "Twenty-five words. Your vocabulary grows."
                wordCount == 50 -> "Fifty words. A craftsman's toolkit takes shape."
                wordCount == 100 -> "One hundred words. New colors for your expression."
                wordCount == 250 -> "Two hundred fifty words. Scholar-level vocabulary."
                wordCount == 500 -> "Five hundred words. You are a keeper of language."
                wordCount % 100 == 0 -> "$wordCount words learned. The collection expands."
                wordCount % 25 == 0 -> "$wordCount words now color your world."
                else -> "Another word joins your collection."
            }
        }

        /**
         * Creates a message for quote read.
         *
         * @param quoteCount Total quotes read
         * @return Appropriate message
         */
        fun quoteRead(quoteCount: Int): String {
            return when {
                quoteCount == 1 -> "The first voice of wisdom speaks to you."
                quoteCount == 10 -> "Ten voices of wisdom now echo in your thoughts."
                quoteCount == 50 -> "Fifty sages have shared their insight with you."
                quoteCount == 100 -> "A hundred quotations form your inner library."
                else -> "Another voice of wisdom joins the chorus."
            }
        }

        /**
         * Creates a message for proverb explored.
         *
         * @param proverbCount Total proverbs explored
         * @return Appropriate message
         */
        fun proverbExplored(proverbCount: Int): String {
            return when {
                proverbCount == 1 -> "Ancient wisdom begins to speak to you."
                proverbCount == 10 -> "Ten proverbs from the ages guide your way."
                proverbCount == 30 -> "The wisdom of generations walks beside you."
                else -> "Another ancestral truth joins your understanding."
            }
        }
    }

    // ===== JOURNAL MESSAGES =====

    /**
     * Messages for journaling milestones.
     */
    object JournalMessages {
        /**
         * Creates a message for journal entry completion.
         *
         * @param entryCount Total journal entries
         * @return Appropriate message
         */
        fun entryCompleted(entryCount: Int): String {
            return when {
                entryCount == 1 -> "Your first reflection is written. The practice begins."
                entryCount == 5 -> "Five entries. Your authentic voice emerges."
                entryCount == 10 -> "Ten conversations with your deeper self."
                entryCount == 25 -> "Twenty-five chapters of your inner life."
                entryCount == 50 -> "Fifty reflections. Your soul has found its mirror."
                entryCount == 100 -> "One hundred glimpses into your being."
                entryCount % 50 == 0 -> "$entryCount entries. A testament to self-examination."
                else -> "Another moment of reflection preserved."
            }
        }

        /**
         * Journal entry prompt messages.
         */
        val prompts = listOf(
            "What is present for you in this moment?",
            "What lesson did today offer you?",
            "What are you grateful for right now?",
            "What challenged you today, and what did you learn?",
            "What would you like to remember about this day?",
            "What intention will you carry forward?",
            "What did you notice about yourself today?",
            "What truth is asking for your attention?"
        )

        /**
         * Gets a random journal prompt.
         */
        fun getRandomPrompt(): String = prompts.random()
    }

    // ===== BUDDHA CONVERSATION MESSAGES =====

    /**
     * Messages for Buddha (AI guide) interactions.
     */
    object BuddhaMessages {
        /**
         * Creates a message for Buddha conversation milestone.
         *
         * @param conversationCount Total conversations
         * @return Appropriate message
         */
        fun conversationMilestone(conversationCount: Int): String {
            return when {
                conversationCount == 1 -> "The dialogue with wisdom has begun."
                conversationCount == 10 -> "Ten conversations. You are learning to ask."
                conversationCount == 50 -> "Fifty dialogues. The sage within awakens."
                conversationCount == 100 -> "One hundred exchanges. A true student of life."
                conversationCount % 50 == 0 -> "$conversationCount conversations with wisdom."
                else -> "Another exchange with Buddha."
            }
        }

        /**
         * Introductory messages from Buddha.
         */
        val introMessages = listOf(
            "What weighs on your mind today?",
            "I am here to listen and reflect.",
            "What question brings you here?",
            "Speak freely. Wisdom begins with honest inquiry.",
            "What would you like to explore together?"
        )

        /**
         * Gets an introduction message for Buddha conversation.
         */
        fun getIntroMessage(): String = introMessages.random()
    }

    // ===== ERROR AND EMPTY STATE MESSAGES =====

    /**
     * Messages for empty states and encouragement.
     */
    object EmptyStateMessages {
        const val NO_WORDS_LEARNED = "Your vocabulary awaits its first word. Begin the collection."
        const val NO_JOURNAL_ENTRIES = "Your journal awaits your first reflection."
        const val NO_QUOTES_READ = "Words of wisdom are gathered one by one."
        const val NO_FUTURE_LETTERS = "Write to your future self. The river of time awaits your message."
        const val NO_ACHIEVEMENTS = "Achievements await your dedication. The path is before you."
        const val NO_STREAK = "The path of consistency begins today."
        const val NO_BUDDHA_CONVERSATIONS = "Buddha awaits your questions. What would you like to know?"
    }

    /**
     * User-friendly error messages.
     */
    object ErrorMessages {
        const val GENERIC_ERROR = "Something unexpected occurred. Please try again."
        const val NETWORK_ERROR = "Connection interrupted. Please check your network and try again."
        const val SAVE_ERROR = "Unable to save your changes. Please try again."
        const val LOAD_ERROR = "Unable to load content. Please try again."
        const val AI_ERROR = "Buddha is momentarily unavailable. Please try again shortly."

        /**
         * Creates an error message with retry option.
         *
         * @param action What the user was trying to do
         * @return User-friendly error message
         */
        fun withRetry(action: String): String {
            return "Unable to $action. Please try again."
        }
    }
}
