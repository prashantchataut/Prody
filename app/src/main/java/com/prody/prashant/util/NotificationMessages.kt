package com.prody.prashant.util

import kotlin.random.Random

/**
 * Engaging, lively notification messages that feel modern and relatable.
 * These are designed to feel like messages from a supportive friend
 * who happens to be wise, not a generic app notification.
 */
object NotificationMessages {

    // Morning wisdom notifications
    val morningWisdom = listOf(
        Triple("Rise and shine, scholar!", "Your brain cells are ready for their daily vitamin - a fresh word awaits.", "Check it out"),
        Triple("New day, new word", "Plot twist: learning can actually be fun. Today's word is waiting.", "Let's go"),
        Triple("Good morning, future genius", "Your vocabulary is about to level up. No pressure.", "Show me"),
        Triple("The early learner gets the wisdom", "Or something like that. A new word is ready for you.", "I'm ready"),
        Triple("Wakey wakey, wisdom awaits", "Your daily dose of 'sounding smart at dinner parties' is here.", "Bring it on"),
        Triple("Another day, another word", "That's approximately 365 reasons to be more eloquent this year.", "Count me in"),
        Triple("The universe sent you a word", "Okay, we did. But it felt cosmic typing that.", "Show me"),
        Triple("Your brain requested this", "A new word to flex those neural pathways. You're welcome.", "Let's see it")
    )

    // Journal reminder notifications
    val journalReminders = listOf(
        Triple("Hey, how was your day?", "Buddha's been saving a spot for your thoughts. No judgment, just reflection.", "Open journal"),
        Triple("Penny for your thoughts?", "Actually, it's free. Your journal is waiting.", "Start writing"),
        Triple("Your thoughts called", "They want to be written down. Something about posterity.", "Answer them"),
        Triple("Journal check-in", "Even two sentences can unlock insights. Buddha's a good listener.", "Reflect now"),
        Triple("Plot twist: journaling helps", "Studies show it, therapists recommend it, Buddha vibes with it.", "Let's do this"),
        Triple("Your future self will thank you", "For the journal entry you're about to write. Just saying.", "Write now"),
        Triple("Brain dump time?", "Sometimes you gotta let the thoughts flow. No filter needed.", "Open journal"),
        Triple("Main character energy", "Every protagonist journals. It's basically a requirement.", "Be the protagonist")
    )

    // Streak motivation notifications
    val streakMotivation = listOf(
        Triple("Streak alert!", "Your %d-day streak is on fire. Don't let it go cold.", "Keep it going"),
        Triple("Consistency unlocked", "%d days and counting. You're basically unstoppable.", "Continue"),
        Triple("Your streak said 'thank you'", "%d days of showing up. That's the whole secret.", "Show up again"),
        Triple("Legend in the making", "%d straight days. Historians will write about this.", "Add another day"),
        Triple("The streak lives!", "%d days strong. This is what discipline looks like.", "Keep building"),
        Triple("You + Consistency = Magic", "%d days prove it. Don't break the spell.", "Cast another day")
    )

    // Streak broken/recovery notifications
    val streakRecovery = listOf(
        Triple("Missed you yesterday", "Streaks break, but spirits don't. Ready to start fresh?", "Let's restart"),
        Triple("Plot twist: It's okay", "Missing a day isn't failing. Not coming back would be. Welcome back?", "I'm back"),
        Triple("The comeback begins", "Every master has off days. The key is showing up again.", "Show up now"),
        Triple("Fresh start available", "Your new streak is ready when you are. No judgment here.", "Begin again"),
        Triple("Remember why you started", "A streak ended, but your journey didn't. Shall we continue?", "Continue")
    )

    // Future message delivery notifications
    val futureMessageDelivery = listOf(
        Triple("A message from the past!", "Past-you had something important to say. This is exciting.", "Read it"),
        Triple("Special delivery", "Your past self sent this. It's like time travel, but with feelings.", "Open message"),
        Triple("Remember when you wrote this?", "Past-you was pretty wise. Check out what they said.", "See message"),
        Triple("Message unlocked", "From a younger, hopeful you. They believed in this moment.", "Read now"),
        Triple("Time capsule opened!", "Your past self has a message for today-you. It's kind of beautiful.", "Discover")
    )

    // Achievement unlocked notifications
    val achievementUnlocked = listOf(
        Triple("Achievement unlocked!", "You just earned '%s'. Your trophy case is getting crowded.", "View achievement"),
        Triple("Level up!", "'%s' is now yours. You're collecting accomplishments like they're going out of style.", "Check it out"),
        Triple("New badge acquired", "'%s' has been added to your collection. Flex incoming.", "See your badge"),
        Triple("You did it!", "'%s' achieved. Past-you would be proud. Present-you should be too.", "Celebrate"),
        Triple("Trophy alert", "'%s' is now yours. Nobody can take this from you.", "View trophy")
    )

    // Quote notifications
    val quoteNotifications = listOf(
        Triple("Wisdom incoming", "Today's quote might just change your perspective. Or at least make you think.", "Read quote"),
        Triple("Words to live by", "Someone smart said something quotable. You should probably see it.", "Show me"),
        Triple("Daily inspiration", "A quote that somehow relates to your life. Coincidence? Maybe not.", "Discover"),
        Triple("Thoughts from the greats", "Standing on the shoulders of giants, one quote at a time.", "Get inspired"),
        Triple("Mental snack time", "A bite-sized piece of wisdom is ready for consumption.", "Consume wisdom")
    )

    // Leaderboard updates
    val leaderboardUpdates = listOf(
        Triple("You're climbing!", "You've moved up to #%d on the leaderboard. The view up here is nice.", "See standings"),
        Triple("Competition heating up", "Someone's catching up! You're currently at #%d. Time to defend?", "View leaderboard"),
        Triple("Top performer alert", "You're in the top 10! Position #%d and rising.", "Check rank"),
        Triple("Peer appreciation", "Someone just boosted you! The community has your back.", "See who"),
        Triple("Congratulations received", "A peer just celebrated your progress. Good vibes incoming.", "View message")
    )

    // Weekly summary notifications
    val weeklySummary = listOf(
        Triple("Your week in review", "Buddha has some thoughts on your journey this week. Spoiler: You did great.", "View summary"),
        Triple("Weekly wisdom drop", "A personalized reflection on your 7-day adventure awaits.", "Read now"),
        Triple("Progress report", "The numbers are in. Your week was productive in ways that matter.", "See stats"),
        Triple("Week wrapped", "Before you start a new week, see how the last one shaped you.", "Review week")
    )

    // Random encouragement (for those random motivation moments)
    val randomEncouragement = listOf(
        Triple("Just checking in", "You're doing better than you think. Seriously.", "Thanks, I needed that"),
        Triple("Random reminder", "You're not behind. You're exactly where you need to be.", "Appreciate it"),
        Triple("Quick note", "Growth isn't always visible. Trust the process you're in.", "Got it"),
        Triple("Hey, you", "Taking time for self-improvement? That's already winning.", "Feeling good"),
        Triple("PSA", "The fact that you're working on yourself puts you ahead of most. True story.", "Thanks Buddha")
    )

    // Evening reflection notifications
    val eveningReflection = listOf(
        Triple("Evening reflection time", "How did today shape you? Buddha's ready to listen.", "Reflect now"),
        Triple("Day's end wisdom", "Before you rest, let's process today's journey together.", "Start reflection"),
        Triple("Sunset thoughts", "The day is winding down. What insights are emerging?", "Share thoughts"),
        Triple("Evening check-in", "Time to pause and reflect on today's experiences.", "Begin reflection")
    )

    // Word of the day notifications
    val wordOfDay = listOf(
        Triple("Word of the day!", "Expand your vocabulary with today's featured word.", "Learn word"),
        Triple("New word unlocked", "Your linguistic arsenal just got stronger.", "Discover word"),
        Triple("Vocabulary boost", "Today's word will make you sound smarter instantly.", "See word"),
        Triple("Word wisdom", "A new word awaits to enrich your conversations.", "Explore word")
    )

    // Future message received notifications
    val futureMessageReceived = listOf(
        Triple("Message from the past!", "Your past self sent you something important.", "Read message"),
        Triple("Time capsule opened", "A message from yesterday's you has arrived.", "View message"),
        Triple("Special delivery", "Your future message has been delivered right on time.", "Open message"),
        Triple("Past-you says hello", "A thoughtful message from your earlier self awaits.", "Read now")
    )

    // Streak reminder notifications
    val streakReminder = listOf(
        Triple("Keep your streak alive!", "Don't let your progress streak end today.", "Continue streak"),
        Triple("Streak check-in", "Your consistency is impressive. Keep it going!", "Maintain streak"),
        Triple("Daily habit reminder", "Your streak depends on today's action.", "Take action"),
        Triple("Consistency matters", "Every day counts toward your growing streak.", "Stay consistent")
    )

    // Journal prompt notifications
    val journalPrompt = listOf(
        Triple("Journal time!", "Your thoughts are waiting to be captured.", "Start journaling"),
        Triple("Reflection moment", "What's on your mind today? Let's explore it.", "Open journal"),
        Triple("Daily writing", "A few minutes of journaling can unlock insights.", "Begin writing"),
        Triple("Thought capture", "Your journal is ready for today's reflections.", "Write now")
    )

    // Vocabulary-specific engaging notifications
    val vocabularyNotifications = listOf(
        Triple("Word nerd alert", "New vocabulary incoming. Your conversations are about to level up.", "Teach me"),
        Triple("Linguistic upgrade available", "Install now? A new word will make you sound 27% smarter.*", "Install word"),
        Triple("Your lexicon is calling", "It wants to grow. Today's word is waiting.", "Answer the call"),
        Triple("Vocabulary expansion pack", "Free DLC for your brain. One new word, unlimited uses.", "Download now"),
        Triple("Fun fact:", "People with larger vocabularies are perceived as more intelligent. Just saying.", "Expand vocab")
    )

    // Default fallback notification for when a category is empty
    private val DEFAULT_NOTIFICATION = Triple(
        "Prody Notification",
        "You have a new notification from Prody.",
        "View"
    )

    /**
     * Gets a random notification from a category with placeholder replacement.
     * Returns a default notification if the category is empty.
     *
     * @param category The list of notification messages to choose from
     * @return A random Triple(title, body, action) from the category, or default if empty
     */
    fun getRandomNotification(category: List<Triple<String, String, String>>): Triple<String, String, String> {
        return category.randomOrNull() ?: DEFAULT_NOTIFICATION
    }

    /**
     * Gets a streak notification with the streak count filled in.
     *
     * @param streakDays The current streak count to display
     * @return A formatted notification Triple(title, body, action)
     */
    fun getStreakNotification(streakDays: Int): Triple<String, String, String> {
        val notification = streakMotivation.randomOrNull()
            ?: Triple("Streak alert!", "Your %d-day streak is on fire.", "Keep it going")
        return Triple(
            notification.first,
            notification.second.replace("%d", streakDays.toString()),
            notification.third
        )
    }

    /**
     * Gets an achievement notification with the achievement name filled in.
     *
     * @param achievementName The name of the unlocked achievement
     * @return A formatted notification Triple(title, body, action)
     */
    fun getAchievementNotification(achievementName: String): Triple<String, String, String> {
        val notification = achievementUnlocked.randomOrNull()
            ?: Triple("Achievement unlocked!", "You just earned '%s'.", "View achievement")
        return Triple(
            notification.first,
            notification.second.replace("%s", achievementName),
            notification.third
        )
    }

    /**
     * Gets a leaderboard notification with rank filled in.
     *
     * @param rank The user's current leaderboard position
     * @return A formatted notification Triple(title, body, action)
     */
    fun getLeaderboardNotification(rank: Int): Triple<String, String, String> {
        // Get first 3 notifications (which have rank placeholders)
        // Use safe access with coerceIn to prevent IndexOutOfBoundsException
        val safeIndex = (leaderboardUpdates.size.coerceAtMost(3) - 1).coerceAtLeast(0)
        val notification = if (leaderboardUpdates.isNotEmpty()) {
            leaderboardUpdates[Random.nextInt(safeIndex + 1)]
        } else {
            Triple("You're climbing!", "You've moved up to #%d on the leaderboard.", "See standings")
        }
        return Triple(
            notification.first,
            notification.second.replace("%d", rank.toString()),
            notification.third
        )
    }
}
