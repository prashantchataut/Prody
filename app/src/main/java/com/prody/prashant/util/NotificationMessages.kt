package com.prody.prashant.util

import kotlin.random.Random

/**
 * Engaging, lively, and meme-inspired notification messages.
 * Designed to feel personal, fun, and like messages from a supportive friend
 * who happens to be wise and has a great sense of humor.
 *
 * Features:
 * - Custom Nepali expressions for cultural flair
 * - Meme references and internet culture
 * - Personality-driven messages
 * - Contextual notifications based on user behavior
 */
object NotificationMessages {

    // ================== FUN/MEME-STYLE NOTIFICATIONS ==================

    /**
     * Playful notifications when user hasn't shown up regularly
     * Used for re-engagement
     */
    val inactiveUserReminders = listOf(
        Triple("Prody ramro lagena ra?", "We miss you! Your growth journey is waiting...", "Come back"),
        Triple("are you alive pookie?", "Just checking if you're still breathing. Also, growth awaits.", "I'm here"),
        Triple("sanchai ho hami?", "Basically asking... are we good? Your journal misses you.", "We're good"),
        Triple("Ghost mode activated?", "We see you. Or do we? Your streak is getting lonely.", "Back to life"),
        Triple("Missing: Your presence", "Last seen: Too long ago. Reward: Personal growth.", "Found me"),
        Triple("Hello? Is it you we're looking for?", "Lionel Richie reference aside, we genuinely miss you.", "It's me"),
        Triple("Day %d without you", "The app is getting emotional. Not kidding.", "I'm sorry"),
        Triple("We've been trying to reach you...", "About your car's extended warranty. JK, about your growth.", "What's up"),
        Triple("Did you forget about me?", "I know you like me, but can't prove it yet", "Prove it")
    )

    /**
     * Celebratory notifications for achievements (especially leaderboard top 3)
     */
    val topAchieverCelebrations = listOf(
        Triple("Timi ta babal raixau yr!", "You're absolutely crushing it! Big fan!", "Celebrate"),
        Triple("LEGEND STATUS UNLOCKED", "You just made the top 3. This is not a drill.", "Flex"),
        Triple("yo yo yo get ready", "You're about to see your name in lights. Top 3 baby!", "Show me"),
        Triple("Main character energy detected", "Top of the leaderboard? That's you right now.", "I know"),
        Triple("Holy smokes!", "You're literally on fire. Someone call the fire department!", "Burn baby"),
        Triple("Wait... that's you on top?", "Plot twist of the century. You're a leaderboard legend!", "Always was"),
        Triple("The council has spoken", "You've been deemed top tier. Congratulations, champion.", "Bow down")
    )

    /**
     * Competitive/peer pressure notifications
     */
    val competitiveNudges = listOf(
        Triple("Holy fck, your peers are catching up!", "Time to step up your game. The competition is real.", "Let's go"),
        Triple("You are not NERDY enough to top the leaderboard", "Prove us wrong. We dare you.", "Challenge accepted"),
        Triple("Alert: Someone passed you", "Your position on the leaderboard just got threatened.", "Fight back"),
        Triple("The plot thickens...", "Someone's been grinding while you were sleeping.", "I'll grind harder"),
        Triple("Leaderboard drama incoming", "You've been dethroned! Will you reclaim your spot?", "Watch me"),
        Triple("Competition update", "The gap is closing. Your rivals are hungry.", "So am I")
    )

    /**
     * Personalized notifications with user's name
     * Use {name} as placeholder to be replaced with actual username
     */
    val personalizedNudges = listOf(
        Triple("{name}, level up bro", "Your current level is just the beginning. There's more.", "Show me"),
        Triple("{name}, you spoke and we heard", "Your journal entries are inspiring. Keep sharing.", "Continue"),
        Triple("will you ma-", "make a promise to future {name}? A message to your future self?", "Promise"),
        Triple("Hey {name}!", "Random reminder: You're doing amazing. Keep at it.", "Thanks"),
        Triple("{name}, quick question", "Have you journaled today? Just curious.", "Let me check"),
        Triple("Yo {name}!", "The universe has aligned for your growth today. No pressure.", "I'm ready"),
        Triple("{name}, real talk", "You've got potential. Don't let it go to waste.", "Won't")
    )

    /**
     * Fun notifications about words/quotes/proverbs
     */
    val wordDiscoveryFun = listOf(
        Triple("You are absolutely thriving!", "btw did you check this word today? It's a good one.", "Show me"),
        Triple("Word of the day be like:", "I'm about to make you sound 47% more sophisticated.", "Hit me"),
        Triple("Vocabulary flex incoming", "Today's word will win you arguments you don't even have yet.", "Teach me"),
        Triple("Your brain requested an upgrade", "We're delivering. Premium vocabulary, no extra charge.", "Download"),
        Triple("Plot twist:", "This word exists and you don't know it yet. Let's fix that.", "Fix it"),
        Triple("Sneak peek:", "There's a quote today that might just change your perspective.", "Intrigued")
    )

    /**
     * Streak-related fun notifications
     */
    val streakFunNotifications = listOf(
        Triple("Streak check!", "Your streak is like a plant. Water it today or watch it wilt.", "Water it"),
        Triple("The streak gods are watching", "Don't disappoint them. They've seen enough broken streaks.", "Never"),
        Triple("Day %d of being awesome", "That's you. That's the notification.", "Facts"),
        Triple("Your streak is flexing", "%d days and counting. The dedication is real.", "Keep flexing"),
        Triple("Streak status: IMMACULATE", "%d days without breaking. You might actually be a robot.", "Beep boop"),
        Triple("The streak said:", "'Don't you dare abandon me today.' Its words, not ours.", "I won't")
    )

    /**
     * Journal-related fun notifications
     */
    val journalFunNotifications = listOf(
        Triple("Your journal called", "It said you've been ghosting. Time for some quality time.", "Answer"),
        Triple("Buddha's waiting", "He's got tea to spill about today's reflection. Interested?", "Spill it"),
        Triple("Brain dump time", "Your thoughts need a home. The journal has a vacancy.", "Move in"),
        Triple("Therapy is expensive", "Journaling is free. Buddha is ready to listen.", "Talk to Buddha"),
        Triple("Today's plot:", "Only you can write it. Your journal is the manuscript.", "Start writing"),
        Triple("Feelings check:", "On a scale of 1 to 'need to vent', how are you?", "Let's vent"),
        Triple("Hot take:", "Your thoughts are interesting. Document them.", "You're right")
    )

    /**
     * Future message related fun notifications
     */
    val futureMessageFun = listOf(
        Triple("Dear Future You...", "What if present you had something important to say?", "Write it"),
        Triple("Time capsule vibes", "Bury a message for future you to dig up. It's less weird than it sounds.", "Bury it"),
        Triple("Message to tomorrow's you:", "They're probably going to need some encouragement. Just saying.", "Send hope"),
        Triple("Future you just texted back", "JK, but they will when you write them something first.", "Start writing")
    )

    /**
     * Random motivation/check-in notifications
     */
    val randomCheckIns = listOf(
        Triple("Quick vibe check", "How's the self-improvement journey going? Be honest.", "Check vibes"),
        Triple("This is a sign", "Whatever you were procrastinating on? Do it.", "Fine"),
        Triple("Reminder:", "You're literally capable of amazing things. Act like it.", "Noted"),
        Triple("POV:", "You see this notification and decide to be productive.", "POV accepted"),
        Triple("Not a drill:", "Today is a good day to grow. That's it. That's the notification.", "Growing"),
        Triple("Fun fact about you:", "You're more resilient than you give yourself credit for.", "Thanks"),
        Triple("The grind never stops", "But also, take breaks. Balance is key.", "Balanced"),
        Triple("Real ones grow daily", "You're a real one, right?", "Obviously"),
        Triple("Just checking in", "How's life treating you? Take a moment to reflect.", "Check in"),
        Triple("Growth o'clock", "Perfect time for a quick check-in.", "Let's go"),
        Triple("Hey you", "Yeah you. How's it going?", "Going good"),
        Triple("Random reminder:", "You're doing better than you think.", "Thanks"),
        Triple("Quick question:", "When was the last time you celebrated a small win?", "Let me think"),
        Triple("Halfway through the day", "How's your energy level? Time for a reset?", "Reset"),
        Triple("Mental wellness check", "Your mind deserves attention too.", "Focus on me")
    )

    /**
     * Morning motivation notifications
     */
    val morningMotivation = listOf(
        Triple("Rise and grind?", "More like rise and shine. Start your day intentionally.", "Shine on"),
        Triple("Morning!", "Buddha's got coffee... I mean wisdom.", "Get wisdom"),
        Triple("New day, new opportunities", "What will you focus on today?", "Set intention"),
        Triple("Fresh start activated", "Yesterday is gone. Today is yours.", "Claim it"),
        Triple("Sunlight mode: ON", "Your potential is waiting to be unlocked.", "Unlock"),
        Triple("Wake up call", "Not the annoying kind. The inspiring kind.", "Inspired"),
        Triple("Morning mindset", "Choose growth. Choose positivity. Choose you.", "Choosing"),
        Triple("First thought of the day:", "You have 24 hours of opportunity ahead.", "Make them count")
    )

    /**
     * Evening reflection notifications
     */
    val eveningReflectionFun = listOf(
        Triple("Day's wrapping up", "Time for a quick reflection?", "Reflect"),
        Triple("Before you scroll into oblivion...", "Maybe journal first?", "Good idea"),
        Triple("Night mode: activated", "Perfect time to reflect on today.", "Start"),
        Triple("Sunset thoughts", "What made today meaningful?", "Think about it"),
        Triple("Evening wisdom", "Process today before tomorrow arrives.", "Process"),
        Triple("Wind down with Buddha", "Let's talk about how today went.", "Let's talk"),
        Triple("Stars are coming out", "Time to let today's lessons sink in.", "Sink in"),
        Triple("End of day debrief", "Quick reflection before rest.", "Debrief")
    )

    /**
     * Level up / XP notifications
     */
    val levelUpNotifications = listOf(
        Triple("{name}, level up bro", "You just hit level {level}!", "Celebrate"),
        Triple("LEVEL UP!", "Level {level} unlocked. New rewards await!", "Check rewards"),
        Triple("Character development: Complete", "Welcome to level {level}.", "View level"),
        Triple("Ding!", "You've ascended to level {level}.", "Ascend"),
        Triple("XP milestone reached!", "Your consistency is paying off.", "View XP"),
        Triple("New level unlocked", "The view is better up here.", "Enjoy"),
        Triple("Growth detected!", "Your level just increased.", "See progress"),
        Triple("You're leveling up IRL", "And in the app. Level {level} achieved.", "Nice")
    )

    // ================== ORIGINAL NOTIFICATIONS (Enhanced) ==================

    // Morning wisdom notifications
    val morningWisdom = listOf(
        Triple("Rise and shine, scholar!", "Your brain cells are ready for their daily vitamin - a fresh word awaits.", "Check it out"),
        Triple("New day, new word", "Plot twist: learning can actually be fun. Today's word is waiting.", "Let's go"),
        Triple("Good morning, future genius", "Your vocabulary is about to level up. No pressure.", "Show me"),
        Triple("The early learner gets the wisdom", "Or something like that. A new word is ready for you.", "I'm ready"),
        Triple("Wakey wakey, wisdom awaits", "Your daily dose of 'sounding smart at dinner parties' is here.", "Bring it on"),
        Triple("Another day, another word", "That's approximately 365 reasons to be more eloquent this year.", "Count me in"),
        Triple("The universe sent you a word", "Okay, we did. But it felt cosmic typing that.", "Show me"),
        Triple("Your brain requested this", "A new word to flex those neural pathways. You're welcome.", "Let's see it"),
        Triple("yo yo yo get ready", "Morning wisdom dropping in 3... 2... 1...", "I'm ready"),
        Triple("Good morning!", "Today's wisdom is brought to you by Buddha. He said hi.", "Say hi back")
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

    // ================== NEW FUN NOTIFICATION HELPERS ==================

    /**
     * Gets a personalized notification with the user's name.
     *
     * @param userName The user's display name
     * @return A formatted notification Triple(title, body, action)
     */
    fun getPersonalizedNotification(userName: String): Triple<String, String, String> {
        val notification = personalizedNudges.randomOrNull()
            ?: Triple("{name}, level up bro", "Your current level is just the beginning.", "Show me")
        return Triple(
            notification.first.replace("{name}", userName),
            notification.second.replace("{name}", userName),
            notification.third
        )
    }

    /**
     * Gets a fun notification for inactive users.
     *
     * @param daysInactive Number of days the user has been inactive (optional)
     * @return A formatted notification Triple(title, body, action)
     */
    fun getInactiveUserNotification(daysInactive: Int? = null): Triple<String, String, String> {
        val notification = inactiveUserReminders.randomOrNull()
            ?: Triple("We miss you!", "Your growth journey is waiting...", "Come back")

        return if (daysInactive != null && notification.first.contains("%d")) {
            Triple(
                notification.first.replace("%d", daysInactive.toString()),
                notification.second.replace("%d", daysInactive.toString()),
                notification.third
            )
        } else {
            notification
        }
    }

    /**
     * Gets a celebration notification for top 3 leaderboard achievers.
     *
     * @return A celebratory notification Triple(title, body, action)
     */
    fun getTopAchieverCelebration(): Triple<String, String, String> {
        return topAchieverCelebrations.randomOrNull()
            ?: Triple("LEGEND STATUS!", "You just made the top 3!", "Celebrate")
    }

    /**
     * Gets a competitive/peer pressure notification.
     *
     * @return A competitive notification Triple(title, body, action)
     */
    fun getCompetitiveNotification(): Triple<String, String, String> {
        return competitiveNudges.randomOrNull()
            ?: Triple("Alert!", "Your peers are catching up!", "Let's go")
    }

    /**
     * Gets a fun streak notification with the streak count.
     *
     * @param streakDays The current streak count
     * @return A formatted fun streak notification Triple(title, body, action)
     */
    fun getFunStreakNotification(streakDays: Int): Triple<String, String, String> {
        val notification = streakFunNotifications.randomOrNull()
            ?: Triple("Streak check!", "Day %d of being awesome!", "Keep it going")
        return Triple(
            notification.first.replace("%d", streakDays.toString()),
            notification.second.replace("%d", streakDays.toString()),
            notification.third
        )
    }

    /**
     * Gets a fun journal-related notification.
     *
     * @return A journal notification Triple(title, body, action)
     */
    fun getFunJournalNotification(): Triple<String, String, String> {
        return journalFunNotifications.randomOrNull()
            ?: Triple("Journal time!", "Buddha's waiting to hear from you.", "Write")
    }

    /**
     * Gets a fun word/vocabulary notification.
     *
     * @return A vocabulary notification Triple(title, body, action)
     */
    fun getFunWordNotification(): Triple<String, String, String> {
        return wordDiscoveryFun.randomOrNull()
            ?: Triple("Word alert!", "Today's word is waiting.", "Discover")
    }

    /**
     * Gets a random check-in notification for general engagement.
     *
     * @return A random check-in notification Triple(title, body, action)
     */
    fun getRandomCheckIn(): Triple<String, String, String> {
        return randomCheckIns.randomOrNull()
            ?: Triple("Hey!", "Just checking in on your growth journey.", "Check")
    }

    /**
     * Gets a fun future message notification.
     *
     * @return A future message notification Triple(title, body, action)
     */
    fun getFutureMessagePrompt(): Triple<String, String, String> {
        return futureMessageFun.randomOrNull()
            ?: Triple("Dear Future You...", "Got something to say to your future self?", "Write")
    }

    /**
     * Selects a fun/engaging notification based on context.
     * This is the main function for getting contextual notifications.
     *
     * @param context The notification context (streak, journal, word, inactive, achievement, etc.)
     * @param userName Optional user name for personalization
     * @param streakDays Optional streak count
     * @param daysInactive Optional days since last activity
     * @param isTopThree Whether user is in top 3 of leaderboard
     * @return A contextual notification Triple(title, body, action)
     */
    fun getContextualNotification(
        context: NotificationContext,
        userName: String? = null,
        streakDays: Int? = null,
        daysInactive: Int? = null,
        isTopThree: Boolean = false
    ): Triple<String, String, String> {
        return when (context) {
            NotificationContext.INACTIVE_USER -> getInactiveUserNotification(daysInactive)
            NotificationContext.TOP_ACHIEVER -> if (isTopThree) getTopAchieverCelebration() else getCompetitiveNotification()
            NotificationContext.COMPETITIVE -> getCompetitiveNotification()
            NotificationContext.PERSONALIZED -> userName?.let { getPersonalizedNotification(it) } ?: getRandomCheckIn()
            NotificationContext.STREAK -> streakDays?.let { getFunStreakNotification(it) } ?: getStreakNotification(0)
            NotificationContext.JOURNAL -> getFunJournalNotification()
            NotificationContext.WORD -> getFunWordNotification()
            NotificationContext.FUTURE_MESSAGE -> getFutureMessagePrompt()
            NotificationContext.RANDOM -> getRandomCheckIn()
        }
    }

    /**
     * Notification context types for contextual notification selection.
     */
    enum class NotificationContext {
        INACTIVE_USER,
        TOP_ACHIEVER,
        COMPETITIVE,
        PERSONALIZED,
        STREAK,
        JOURNAL,
        WORD,
        FUTURE_MESSAGE,
        RANDOM
    }
}
