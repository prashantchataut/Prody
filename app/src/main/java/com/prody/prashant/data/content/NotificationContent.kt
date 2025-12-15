package com.prody.prashant.data.content

import kotlin.random.Random

/**
 * Prody Notification Content Library
 *
 * A comprehensive, production-grade notification message library designed to
 * engage users with personality, cultural relevance, and contextual awareness.
 *
 * Features:
 * - 90+ unique notification messages across 12 categories
 * - Nepali expressions for cultural personality (with English context)
 * - Meme-inspired and internet culture references
 * - Contextual selection based on user state and behavior
 * - Placeholder support for personalization ({name}, {days}, {level}, etc.)
 * - Time-of-day appropriate messaging
 * - Expandable notification support
 *
 * Design Philosophy:
 * - Friendly and supportive, never preachy
 * - Playful humor without being annoying
 * - Cultural authenticity with Nepali expressions
 * - Respect for user's time and attention
 */
object NotificationContent {

    // =========================================================================
    // DATA CLASSES
    // =========================================================================

    /**
     * Represents a notification message with all its variants and metadata.
     */
    data class NotificationMessage(
        val title: String,
        val body: String,
        val action: String = "Open",
        val context: String? = null,           // For translations/explanations
        val isExpandable: Boolean = false,
        val expandedTitle: String? = null,
        val expandedBody: String? = null,
        val hasPlaceholder: Boolean = false,
        val category: NotificationCategory = NotificationCategory.GENERAL,
        val priority: NotificationPriority = NotificationPriority.DEFAULT
    )

    /**
     * Notification categories for contextual selection.
     */
    enum class NotificationCategory {
        INACTIVE_USER,
        CELEBRATION,
        COMPETITIVE,
        LEVEL_UP,
        STREAK,
        JOURNAL,
        FUTURE_MESSAGE,
        WISDOM,
        CHECK_IN,
        MORNING,
        EVENING,
        GENERAL
    }

    /**
     * Notification priority levels.
     */
    enum class NotificationPriority {
        LOW,
        DEFAULT,
        HIGH,
        URGENT
    }

    // =========================================================================
    // INACTIVE USER - RE-ENGAGEMENT MESSAGES
    // =========================================================================

    val inactiveMessages = listOf(
        // Nepali expressions with cultural flair
        NotificationMessage(
            title = "Prody ramro lagena ra?",
            body = "We miss having you around. Your growth journey awaits.",
            action = "Come back",
            context = "Nepali: Didn't you like Prody?",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "sanchai ho hami?",
            body = "Just checking if we're still cool. Your journal misses you.",
            action = "We're good",
            context = "Nepali: Are we good?",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "Kasto cha aajkal?",
            body = "Haven't seen you in a while. Buddha's been waiting.",
            action = "I'm here",
            context = "Nepali: How is it going these days?",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "Kata harayo timi?",
            body = "Your streak is gathering dust. Ready to restart?",
            action = "Let's go",
            context = "Nepali: Where did you disappear?",
            category = NotificationCategory.INACTIVE_USER
        ),

        // English casual - meme-inspired
        NotificationMessage(
            title = "are you alive pookie?",
            body = "Your journal is gathering dust. Just checking in.",
            action = "I'm here",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "Miss you. That's it. That's the notification.",
            body = "Come back when you're ready. No pressure.",
            action = "I'm ready",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "It's lonely without you",
            body = "Buddha has been practicing his wisdom. Want to hear it?",
            action = "Tell me",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "Your growth journey paused...",
            body = "Ready to unpause? We're here whenever you are.",
            action = "Unpause",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "Plot twist: You forgot about us",
            body = "But we didn't forget about you. Main character energy?",
            action = "My bad",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "Day {days} without you",
            body = "Not that we're counting... okay we're counting.",
            action = "I'm sorry",
            hasPlaceholder = true,
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "POV: You abandoned your streak",
            body = "But plot twist: it's not too late to restart.",
            action = "Restart",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "Did you forget about me?",
            body = "I know you like me, but can't prove it yet.",
            action = "Prove it",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "Ghost mode activated?",
            body = "We see you. Or do we? Your wisdom awaits.",
            action = "Deactivate",
            category = NotificationCategory.INACTIVE_USER
        ),
        NotificationMessage(
            title = "We've been trying to reach you...",
            body = "About your car's extended warranty. JK, about your growth.",
            action = "What's up",
            category = NotificationCategory.INACTIVE_USER
        )
    )

    // =========================================================================
    // CELEBRATION - TOP ACHIEVERS & ACHIEVEMENTS
    // =========================================================================

    val celebrationMessages = listOf(
        // Nepali celebrations
        NotificationMessage(
            title = "Timi ta babal raixau yr, big fan!",
            body = "You made it to the top 3! This is your moment.",
            action = "Celebrate",
            context = "Nepali: You're amazing, big fan!",
            category = NotificationCategory.CELEBRATION,
            priority = NotificationPriority.HIGH
        ),
        NotificationMessage(
            title = "Ramro garyou bhai/baini!",
            body = "Achievement unlocked! Buddha is impressed.",
            action = "View",
            context = "Nepali: Well done brother/sister!",
            category = NotificationCategory.CELEBRATION
        ),

        // English celebrations
        NotificationMessage(
            title = "Main character energy detected",
            body = "Top 3 on the leaderboard! The protagonist has arrived.",
            action = "I know",
            category = NotificationCategory.CELEBRATION,
            priority = NotificationPriority.HIGH
        ),
        NotificationMessage(
            title = "Is it lonely at the top?",
            body = "Asking for the people below you on the leaderboard.",
            action = "Very",
            category = NotificationCategory.CELEBRATION
        ),
        NotificationMessage(
            title = "Certified legend status",
            body = "You're dominating. Everyone else is just participating.",
            action = "Facts",
            category = NotificationCategory.CELEBRATION
        ),
        NotificationMessage(
            title = "Everyone else: 'How do they do it?'",
            body = "You: Just built different. Top performer status.",
            action = "Indeed",
            category = NotificationCategory.CELEBRATION
        ),
        NotificationMessage(
            title = "Achievement Unlocked!",
            body = "You earned: {badge_name}. Add it to the trophy case.",
            action = "View badge",
            hasPlaceholder = true,
            category = NotificationCategory.CELEBRATION
        ),
        NotificationMessage(
            title = "New badge just dropped",
            body = "{badge_name} is now yours forever. Well deserved!",
            action = "Flex it",
            hasPlaceholder = true,
            category = NotificationCategory.CELEBRATION
        ),
        NotificationMessage(
            title = "The council has spoken",
            body = "You've been deemed top tier. Congratulations, champion.",
            action = "Bow down",
            category = NotificationCategory.CELEBRATION
        ),
        NotificationMessage(
            title = "Wait... that's you on top?",
            body = "Plot twist of the century. You're a leaderboard legend!",
            action = "Always was",
            category = NotificationCategory.CELEBRATION
        )
    )

    // =========================================================================
    // COMPETITIVE - PEER PRESSURE & MOTIVATION
    // =========================================================================

    val competitiveMessages = listOf(
        NotificationMessage(
            title = "Holy fck, your peers are catching up!",
            body = "Better do something about that... unless you don't mind?",
            action = "Let's go",
            category = NotificationCategory.COMPETITIVE
        ),
        NotificationMessage(
            title = "You are not NERDY enough to top the leaderboard",
            body = "Prove us wrong? We dare you.",
            action = "Challenge accepted",
            category = NotificationCategory.COMPETITIVE
        ),
        NotificationMessage(
            title = "{name} just passed you on the leaderboard",
            body = "Are you gonna take that? Your call.",
            action = "Fight back",
            hasPlaceholder = true,
            category = NotificationCategory.COMPETITIVE
        ),
        NotificationMessage(
            title = "50 XP away from overtaking #{rank}",
            body = "One good entry could do it...",
            action = "Do it",
            hasPlaceholder = true,
            category = NotificationCategory.COMPETITIVE
        ),
        NotificationMessage(
            title = "Your rival just journaled",
            body = "They're not slowing down. Will you?",
            action = "Never",
            category = NotificationCategory.COMPETITIVE
        ),
        NotificationMessage(
            title = "Friendly reminder: You have competition",
            body = "And they're putting in work while you're reading this.",
            action = "So am I",
            category = NotificationCategory.COMPETITIVE
        ),
        NotificationMessage(
            title = "Leaderboard drama incoming",
            body = "You've been dethroned! Will you reclaim your spot?",
            action = "Watch me",
            category = NotificationCategory.COMPETITIVE
        ),
        NotificationMessage(
            title = "The plot thickens...",
            body = "Someone's been grinding while you were away.",
            action = "I'll grind harder",
            category = NotificationCategory.COMPETITIVE
        )
    )

    // =========================================================================
    // LEVEL UP - XP & PROGRESSION
    // =========================================================================

    val levelUpMessages = listOf(
        NotificationMessage(
            title = "{name}, level up bro!",
            body = "You just hit level {level}! New horizons await.",
            action = "Show me",
            hasPlaceholder = true,
            category = NotificationCategory.LEVEL_UP,
            priority = NotificationPriority.HIGH
        ),
        NotificationMessage(
            title = "LEVEL UP!",
            body = "Level {level} unlocked. You're literally leveling up in life.",
            action = "Nice",
            hasPlaceholder = true,
            category = NotificationCategory.LEVEL_UP,
            priority = NotificationPriority.HIGH
        ),
        NotificationMessage(
            title = "Character development: Complete",
            body = "Welcome to level {level}. The journey continues.",
            action = "Continue",
            hasPlaceholder = true,
            category = NotificationCategory.LEVEL_UP
        ),
        NotificationMessage(
            title = "Ding!",
            body = "You've ascended to level {level}. Gaming reference intended.",
            action = "Ascend",
            hasPlaceholder = true,
            category = NotificationCategory.LEVEL_UP
        ),
        NotificationMessage(
            title = "Experience gained!",
            body = "You just earned {xp} XP. Keep stacking!",
            action = "Stack more",
            hasPlaceholder = true,
            category = NotificationCategory.LEVEL_UP
        ),
        NotificationMessage(
            title = "XP milestone reached",
            body = "You've hit {xp} total XP. That's dedication.",
            action = "View stats",
            hasPlaceholder = true,
            category = NotificationCategory.LEVEL_UP
        )
    )

    // =========================================================================
    // STREAK MESSAGES - CELEBRATION, AT-RISK, BROKEN
    // =========================================================================

    val streakCelebrationMessages = listOf(
        NotificationMessage(
            title = "Your streak is lowkey impressive ngl",
            body = "{days} days and counting! Keep it going.",
            action = "Will do",
            hasPlaceholder = true,
            category = NotificationCategory.STREAK
        ),
        NotificationMessage(
            title = "Streak check: still going strong",
            body = "Day {days} of being consistent. This is the way.",
            action = "This is the way",
            hasPlaceholder = true,
            category = NotificationCategory.STREAK
        ),
        NotificationMessage(
            title = "Fire emoji doesn't do this justice",
            body = "{days}-day streak! You might actually be on fire.",
            action = "Stay hot",
            hasPlaceholder = true,
            category = NotificationCategory.STREAK
        ),
        NotificationMessage(
            title = "The streak gods are pleased",
            body = "{days} days of pure dedication. They're watching.",
            action = "Good",
            hasPlaceholder = true,
            category = NotificationCategory.STREAK
        ),
        NotificationMessage(
            title = "Day {days} of being awesome",
            body = "That's you. That's the notification.",
            action = "Facts",
            hasPlaceholder = true,
            category = NotificationCategory.STREAK
        ),
        NotificationMessage(
            title = "Your streak is flexing",
            body = "{days} days and counting. The dedication is real.",
            action = "Keep flexing",
            hasPlaceholder = true,
            category = NotificationCategory.STREAK
        )
    )

    val streakAtRiskMessages = listOf(
        NotificationMessage(
            title = "Streak in danger!",
            body = "Don't let {days} days go to waste. A quick entry saves it!",
            action = "Save it",
            hasPlaceholder = true,
            category = NotificationCategory.STREAK,
            priority = NotificationPriority.HIGH
        ),
        NotificationMessage(
            title = "Your streak is screaming for help",
            body = "A quick entry can save it! Only a few hours left.",
            action = "On it",
            category = NotificationCategory.STREAK,
            priority = NotificationPriority.HIGH
        ),
        NotificationMessage(
            title = "The streak gods are nervous",
            body = "Only a few hours left to continue. Don't let them down.",
            action = "I won't",
            category = NotificationCategory.STREAK
        ),
        NotificationMessage(
            title = "Streak SOS!",
            body = "Your {days}-day streak needs you. 2 minutes is all it takes.",
            action = "Be a hero",
            hasPlaceholder = true,
            category = NotificationCategory.STREAK,
            priority = NotificationPriority.URGENT
        )
    )

    val streakBrokenMessages = listOf(
        NotificationMessage(
            title = "Plot twist: Streak ended",
            body = "But every master was once a disaster. Ready for the comeback?",
            action = "Restart",
            category = NotificationCategory.STREAK
        ),
        NotificationMessage(
            title = "Streak broke, but you didn't",
            body = "Ready for a comeback story? They're the best kind.",
            action = "Let's write it",
            category = NotificationCategory.STREAK
        ),
        NotificationMessage(
            title = "Missed you yesterday",
            body = "Streaks break, but spirits don't. Fresh start available.",
            action = "Fresh start",
            category = NotificationCategory.STREAK
        ),
        NotificationMessage(
            title = "The comeback begins",
            body = "Every master has off days. The key is showing up again.",
            action = "Show up",
            category = NotificationCategory.STREAK
        )
    )

    // =========================================================================
    // JOURNAL MESSAGES - WRITING PROMPTS & REMINDERS
    // =========================================================================

    val journalMessages = listOf(
        NotificationMessage(
            title = "{name}, you spoke and we heard",
            body = "Buddha has a fresh prompt waiting just for you.",
            action = "See prompt",
            hasPlaceholder = true,
            category = NotificationCategory.JOURNAL
        ),
        NotificationMessage(
            title = "Your journal misses you fr fr",
            body = "What's on your mind today? No filter needed.",
            action = "Write",
            category = NotificationCategory.JOURNAL
        ),
        NotificationMessage(
            title = "Thoughts?",
            body = "Your journal is ready when you are. Buddha's listening.",
            action = "Share",
            category = NotificationCategory.JOURNAL
        ),
        NotificationMessage(
            title = "Brain dump time?",
            body = "Sometimes you just need to write it out. No judgment.",
            action = "Dump it",
            category = NotificationCategory.JOURNAL
        ),
        NotificationMessage(
            title = "That thing you've been thinking about?",
            body = "Maybe it's time to journal about it. Just saying.",
            action = "Maybe",
            category = NotificationCategory.JOURNAL
        ),
        NotificationMessage(
            title = "2 mins is all it takes",
            body = "A quick reflection can change your whole day.",
            action = "2 mins",
            category = NotificationCategory.JOURNAL
        ),
        NotificationMessage(
            title = "Your journal called",
            body = "It said you've been ghosting. Time for some quality time.",
            action = "Answer",
            category = NotificationCategory.JOURNAL
        ),
        NotificationMessage(
            title = "Buddha's waiting",
            body = "He's got tea to spill about today's reflection. Interested?",
            action = "Spill it",
            category = NotificationCategory.JOURNAL
        ),
        NotificationMessage(
            title = "Therapy is expensive",
            body = "Journaling is free. Buddha is ready to listen.",
            action = "Talk",
            category = NotificationCategory.JOURNAL
        ),
        NotificationMessage(
            title = "Hot take:",
            body = "Your thoughts are interesting. Document them.",
            action = "You're right",
            category = NotificationCategory.JOURNAL
        )
    )

    // =========================================================================
    // FUTURE MESSAGE MESSAGES
    // =========================================================================

    val futureMessagePrompts = listOf(
        NotificationMessage(
            title = "will you ma-",
            body = "make a promise to future {name}?",
            action = "Promise",
            isExpandable = true,
            expandedTitle = "will you make a promise to future {name}?",
            hasPlaceholder = true,
            category = NotificationCategory.FUTURE_MESSAGE
        ),
        NotificationMessage(
            title = "Dear Future Me...",
            body = "What would you tell yourself? The future is listening.",
            action = "Write",
            category = NotificationCategory.FUTURE_MESSAGE
        ),
        NotificationMessage(
            title = "Time capsule incoming",
            body = "Write something for future you. It's less weird than it sounds.",
            action = "Bury it",
            category = NotificationCategory.FUTURE_MESSAGE
        ),
        NotificationMessage(
            title = "Your future self called",
            body = "They want a message from present you. Deliver?",
            action = "Deliver",
            category = NotificationCategory.FUTURE_MESSAGE
        ),
        NotificationMessage(
            title = "Plot a future plot twist",
            body = "Write yourself a message to receive later. Time travel vibes.",
            action = "Time travel",
            category = NotificationCategory.FUTURE_MESSAGE
        )
    )

    val futureMessageArrivedMessages = listOf(
        NotificationMessage(
            title = "A message from the past!",
            body = "Past you had something important to say. This is exciting.",
            action = "Read it",
            category = NotificationCategory.FUTURE_MESSAGE,
            priority = NotificationPriority.HIGH
        ),
        NotificationMessage(
            title = "Time travel complete",
            body = "Your past self sent you a message. It just arrived.",
            action = "Open",
            category = NotificationCategory.FUTURE_MESSAGE,
            priority = NotificationPriority.HIGH
        ),
        NotificationMessage(
            title = "Message unlocked!",
            body = "See what past you wrote to present you. It's kind of beautiful.",
            action = "Discover",
            category = NotificationCategory.FUTURE_MESSAGE
        ),
        NotificationMessage(
            title = "Special delivery",
            body = "Your past self sent this. Time capsule officially opened.",
            action = "View",
            category = NotificationCategory.FUTURE_MESSAGE
        )
    )

    // =========================================================================
    // WISDOM & WORD OF THE DAY
    // =========================================================================

    val wisdomMessages = listOf(
        NotificationMessage(
            title = "You are absolutely thriving",
            body = "btw did you check this word today? It's a good one.",
            action = "Show me",
            category = NotificationCategory.WISDOM
        ),
        NotificationMessage(
            title = "New wisdom just dropped",
            body = "Buddha has something for you. Might change your perspective.",
            action = "Enlighten me",
            category = NotificationCategory.WISDOM
        ),
        NotificationMessage(
            title = "Brain food served",
            body = "Today's insight is ready for consumption. Nutritious.",
            action = "Consume",
            category = NotificationCategory.WISDOM
        ),
        NotificationMessage(
            title = "One word. Big meaning.",
            body = "Check out today's word. It might just stick with you.",
            action = "See word",
            category = NotificationCategory.WISDOM
        ),
        NotificationMessage(
            title = "Word nerd alert",
            body = "New vocabulary incoming. Your conversations are about to level up.",
            action = "Teach me",
            category = NotificationCategory.WISDOM
        ),
        NotificationMessage(
            title = "Vocabulary flex incoming",
            body = "Today's word will win you arguments you don't even have yet.",
            action = "Download",
            category = NotificationCategory.WISDOM
        ),
        NotificationMessage(
            title = "Your lexicon is calling",
            body = "It wants to grow. Today's word is waiting.",
            action = "Answer",
            category = NotificationCategory.WISDOM
        ),
        NotificationMessage(
            title = "Linguistic upgrade available",
            body = "Install now? A new word will make you sound 27% smarter.*",
            action = "Install",
            category = NotificationCategory.WISDOM
        )
    )

    // =========================================================================
    // GENERAL CHECK-INS
    // =========================================================================

    val checkInMessages = listOf(
        NotificationMessage(
            title = "Just checking in",
            body = "How's life treating you? Take a moment to reflect.",
            action = "Reflect",
            category = NotificationCategory.CHECK_IN
        ),
        NotificationMessage(
            title = "Quick vibe check",
            body = "How are you feeling today? Be honest.",
            action = "Check vibes",
            category = NotificationCategory.CHECK_IN
        ),
        NotificationMessage(
            title = "yo yo yo get ready",
            body = "Time for some self-improvement. No pressure though.",
            action = "Ready",
            category = NotificationCategory.CHECK_IN
        ),
        NotificationMessage(
            title = "I know you like me, but can't prove it yet",
            body = "Open the app and prove me right.",
            action = "Proving it",
            category = NotificationCategory.CHECK_IN
        ),
        NotificationMessage(
            title = "Growth o'clock",
            body = "Perfect time for a quick check-in with yourself.",
            action = "Check in",
            category = NotificationCategory.CHECK_IN
        ),
        NotificationMessage(
            title = "Hey you",
            body = "Yeah you. How's it going? Just curious.",
            action = "It's going",
            category = NotificationCategory.CHECK_IN
        ),
        NotificationMessage(
            title = "Random reminder:",
            body = "You're doing better than you think. Seriously.",
            action = "Thanks",
            category = NotificationCategory.CHECK_IN
        ),
        NotificationMessage(
            title = "This is a sign",
            body = "Whatever you were procrastinating on? Maybe do it.",
            action = "Fine",
            category = NotificationCategory.CHECK_IN
        ),
        NotificationMessage(
            title = "Real ones grow daily",
            body = "You're a real one, right?",
            action = "Obviously",
            category = NotificationCategory.CHECK_IN
        ),
        NotificationMessage(
            title = "PSA",
            body = "The fact that you're working on yourself puts you ahead of most.",
            action = "Thanks Buddha",
            category = NotificationCategory.CHECK_IN
        )
    )

    // =========================================================================
    // MORNING MOTIVATION
    // =========================================================================

    val morningMessages = listOf(
        NotificationMessage(
            title = "Rise and shine, scholar!",
            body = "Your brain cells are ready for their daily vitamin. Fresh word awaits.",
            action = "Check it",
            category = NotificationCategory.MORNING
        ),
        NotificationMessage(
            title = "Rise and grind? More like rise and shine",
            body = "Start your day intentionally. Wisdom is brewing.",
            action = "Pour it",
            category = NotificationCategory.MORNING
        ),
        NotificationMessage(
            title = "Morning! Buddha's got coffee... I mean wisdom",
            body = "Today's insight is hot and ready. Careful, might change your day.",
            action = "Sip it",
            category = NotificationCategory.MORNING
        ),
        NotificationMessage(
            title = "New day, new opportunities",
            body = "What will you focus on today? Set your intention.",
            action = "Set it",
            category = NotificationCategory.MORNING
        ),
        NotificationMessage(
            title = "Good morning, future genius",
            body = "Your vocabulary is about to level up. No pressure.",
            action = "Show me",
            category = NotificationCategory.MORNING
        ),
        NotificationMessage(
            title = "The early learner gets the wisdom",
            body = "Or something like that. A new word is ready for you.",
            action = "I'm ready",
            category = NotificationCategory.MORNING
        ),
        NotificationMessage(
            title = "Another day, another opportunity",
            body = "That's approximately 365 reasons to grow this year.",
            action = "Count me in",
            category = NotificationCategory.MORNING
        )
    )

    // =========================================================================
    // EVENING REFLECTION
    // =========================================================================

    val eveningMessages = listOf(
        NotificationMessage(
            title = "Day's wrapping up",
            body = "Time for a quick reflection? Process today before tomorrow.",
            action = "Reflect",
            category = NotificationCategory.EVENING
        ),
        NotificationMessage(
            title = "Before you scroll into oblivion...",
            body = "Maybe journal first? Just a thought.",
            action = "Fine",
            category = NotificationCategory.EVENING
        ),
        NotificationMessage(
            title = "Night mode: activated",
            body = "Perfect time to reflect on today's journey.",
            action = "Begin",
            category = NotificationCategory.EVENING
        ),
        NotificationMessage(
            title = "Evening reflection time",
            body = "How did today shape you? Buddha's ready to listen.",
            action = "Share",
            category = NotificationCategory.EVENING
        ),
        NotificationMessage(
            title = "Sunset thoughts",
            body = "The day is winding down. What insights are emerging?",
            action = "Explore",
            category = NotificationCategory.EVENING
        ),
        NotificationMessage(
            title = "Day's end wisdom",
            body = "Before you rest, let's process today's journey together.",
            action = "Process",
            category = NotificationCategory.EVENING
        )
    )

    // =========================================================================
    // HELPER FUNCTIONS
    // =========================================================================

    /**
     * Gets a random notification from a specific category.
     */
    fun getRandomFromCategory(category: NotificationCategory): NotificationMessage {
        return when (category) {
            NotificationCategory.INACTIVE_USER -> inactiveMessages.random()
            NotificationCategory.CELEBRATION -> celebrationMessages.random()
            NotificationCategory.COMPETITIVE -> competitiveMessages.random()
            NotificationCategory.LEVEL_UP -> levelUpMessages.random()
            NotificationCategory.STREAK -> streakCelebrationMessages.random()
            NotificationCategory.JOURNAL -> journalMessages.random()
            NotificationCategory.FUTURE_MESSAGE -> futureMessagePrompts.random()
            NotificationCategory.WISDOM -> wisdomMessages.random()
            NotificationCategory.CHECK_IN -> checkInMessages.random()
            NotificationCategory.MORNING -> morningMessages.random()
            NotificationCategory.EVENING -> eveningMessages.random()
            NotificationCategory.GENERAL -> checkInMessages.random()
        }
    }

    /**
     * Gets a notification appropriate for the time of day.
     */
    fun getTimeAppropriateMessage(hour: Int): NotificationMessage {
        return when (hour) {
            in 5..11 -> morningMessages.random()
            in 18..23 -> eveningMessages.random()
            else -> checkInMessages.random()
        }
    }

    /**
     * Formats a notification message with provided placeholders.
     */
    fun formatMessage(
        message: NotificationMessage,
        name: String? = null,
        days: Int? = null,
        level: Int? = null,
        xp: Int? = null,
        rank: Int? = null,
        badgeName: String? = null
    ): NotificationMessage {
        var title = message.title
        var body = message.body
        var expandedTitle = message.expandedTitle
        var expandedBody = message.expandedBody

        name?.let {
            title = title.replace("{name}", it)
            body = body.replace("{name}", it)
            expandedTitle = expandedTitle?.replace("{name}", it)
            expandedBody = expandedBody?.replace("{name}", it)
        }

        days?.let {
            title = title.replace("{days}", it.toString())
            body = body.replace("{days}", it.toString())
        }

        level?.let {
            title = title.replace("{level}", it.toString())
            body = body.replace("{level}", it.toString())
        }

        xp?.let {
            title = title.replace("{xp}", it.toString())
            body = body.replace("{xp}", it.toString())
        }

        rank?.let {
            title = title.replace("{rank}", it.toString())
            body = body.replace("{rank}", it.toString())
        }

        badgeName?.let {
            title = title.replace("{badge_name}", it)
            body = body.replace("{badge_name}", it)
        }

        return message.copy(
            title = title,
            body = body,
            expandedTitle = expandedTitle,
            expandedBody = expandedBody
        )
    }

    /**
     * Gets a streak-appropriate notification based on streak count.
     */
    fun getStreakNotification(streakDays: Int, isAtRisk: Boolean = false): NotificationMessage {
        val message = when {
            isAtRisk -> streakAtRiskMessages.random()
            streakDays == 0 -> streakBrokenMessages.random()
            else -> streakCelebrationMessages.random()
        }
        return formatMessage(message, days = streakDays)
    }

    /**
     * Gets an inactive user notification with days count.
     */
    fun getInactiveNotification(daysInactive: Int): NotificationMessage {
        val message = inactiveMessages.random()
        return formatMessage(message, days = daysInactive)
    }

    /**
     * Gets a level up notification with user's name and level.
     */
    fun getLevelUpNotification(userName: String, newLevel: Int): NotificationMessage {
        val message = levelUpMessages.random()
        return formatMessage(message, name = userName, level = newLevel)
    }

    /**
     * Gets a personalized journal notification.
     */
    fun getJournalNotification(userName: String? = null): NotificationMessage {
        val message = journalMessages.random()
        return if (userName != null) {
            formatMessage(message, name = userName)
        } else {
            message
        }
    }

    /**
     * Gets a competitive notification with optional rank info.
     */
    fun getCompetitiveNotification(
        rivalName: String? = null,
        currentRank: Int? = null
    ): NotificationMessage {
        val message = competitiveMessages.random()
        return formatMessage(message, name = rivalName, rank = currentRank)
    }

    /**
     * Gets a celebration notification for achievement unlocks.
     */
    fun getAchievementNotification(badgeName: String): NotificationMessage {
        val message = celebrationMessages.filter { it.hasPlaceholder }.randomOrNull()
            ?: celebrationMessages.random()
        return formatMessage(message, badgeName = badgeName)
    }

    /**
     * Gets all messages as a flat list for testing/debugging.
     */
    fun getAllMessages(): List<NotificationMessage> {
        return inactiveMessages +
                celebrationMessages +
                competitiveMessages +
                levelUpMessages +
                streakCelebrationMessages +
                streakAtRiskMessages +
                streakBrokenMessages +
                journalMessages +
                futureMessagePrompts +
                futureMessageArrivedMessages +
                wisdomMessages +
                checkInMessages +
                morningMessages +
                eveningMessages
    }

    /**
     * Returns the total count of unique notification messages.
     */
    fun getTotalMessageCount(): Int = getAllMessages().size
}
