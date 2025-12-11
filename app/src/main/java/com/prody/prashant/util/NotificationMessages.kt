package com.prody.prashant.util

import kotlin.random.Random

/**
 * Engaging, lively notification messages with sports references, memes,
 * elite ball knowledge, and interactive niche content.
 * These are designed to feel modern, relatable, and conversation-worthy.
 */
object NotificationMessages {

    // Morning wisdom notifications - with sports & pop culture references
    val morningWisdom = listOf(
        Triple("Rise and grind!", "Kobe didn't become a legend by sleeping in. Your daily word awaits.", "Mamba mentality"),
        Triple("New day, new word", "Plot twist: learning can actually be fun. Today's word is waiting.", "Let's go"),
        Triple("Good morning, future genius", "Even Einstein started somewhere. Today's word is your first step.", "Show me"),
        Triple("The early learner gets the wisdom", "You're up before your competition. That's already a W.", "I'm ready"),
        Triple("Wakey wakey, wisdom awaits", "Your daily dose of 'sounding smart at dinner parties' is here.", "Bring it on"),
        Triple("Another day, another word", "Consistency wins championships. Just ask Tom Brady's 7 rings.", "Count me in"),
        Triple("Main character energy activated", "The protagonist of your story just woke up. Time to level up.", "Let's write history"),
        Triple("Your brain requested this", "Neural pathways don't build themselves. Feed the machine.", "Let's see it"),
        Triple("Rise like Ronaldo", "CR7 didn't become GOAT by hitting snooze. Neither will you.", "SIUUU"),
        Triple("Good morning, scholar", "Today's word might just be the one you drop in that meeting.", "Impress them")
    )

    // Journal reminder notifications - with relatable references
    val journalReminders = listOf(
        Triple("Hey, how was your day?", "Buddha's been saving a spot for your thoughts. No judgment, just reflection.", "Open journal"),
        Triple("Penny for your thoughts?", "Actually, it's free. And more valuable than any crypto.", "Start writing"),
        Triple("Your thoughts called", "They want to be written down. Something about posterity.", "Answer them"),
        Triple("Journal check-in", "Even LeBron keeps a mental journal. You should too.", "Reflect now"),
        Triple("Plot twist: journaling helps", "Studies show it, therapists recommend it, Buddha vibes with it.", "Let's do this"),
        Triple("Character development time", "Every anime protagonist has a reflection arc. This is yours.", "Write now"),
        Triple("Brain dump incoming", "Sometimes you gotta let the thoughts flow. No filter needed.", "Open journal"),
        Triple("Main character energy", "Every protagonist journals. It's basically a requirement.", "Be the protagonist"),
        Triple("Therapy is expensive", "But journaling is free. And Buddha doesn't charge by the hour.", "Free session"),
        Triple("Your future self will thank you", "Imagine reading this journal entry in 5 years. Make it count.", "Write now"),
        Triple("Post-match analysis", "Like a manager reviewing game tape, reflect on your day's plays.", "Analyze"),
        Triple("The inner monologue", "Your thoughts are the commentary. Time to write the match report.", "Document it")
    )

    // Streak motivation notifications - sports themed
    val streakMotivation = listOf(
        Triple("Streak alert!", "Your %d-day streak is on fire. That's more consistent than Arsenal's title challenges.", "Keep it going"),
        Triple("Consistency unlocked", "%d days and counting. You're building a dynasty like SAF's United.", "Continue"),
        Triple("Your streak said 'thank you'", "%d days of showing up. Klopp would call this 'mentality monsters'.", "Show up again"),
        Triple("Legend in the making", "%d straight days. Historians will write about this like Messi's career.", "Add another day"),
        Triple("The streak lives!", "%d days strong. More reliable than VAR decisions.", "Keep building"),
        Triple("You + Consistency = Magic", "%d days prove it. That's Pep Guardiola levels of consistency.", "Cast another day"),
        Triple("Undefeated streak!", "%d days without missing. Invincibles energy right here.", "Stay unbeaten"),
        Triple("Building a legacy", "%d days. Rome wasn't built in a day, but your streak was built daily.", "One more"),
        Triple("GOAT behavior detected", "%d days of pure dedication. CR7 and Messi would approve.", "Continue the run"),
        Triple("Streak machine", "%d days! You're the 2004 Arsenal of self-improvement.", "Invincible mode")
    )

    // Streak broken/recovery notifications - motivational sports comebacks
    val streakRecovery = listOf(
        Triple("Missed you yesterday", "Even Liverpool had that slip. But Gerrard came back stronger. You will too.", "Let's restart"),
        Triple("Plot twist: It's okay", "Jordan got cut from his high school team. Look what happened next.", "I'm back"),
        Triple("The comeback begins", "Every champion has off days. Real Madrid won 3 UCLs after Ronaldo left.", "Show up now"),
        Triple("Fresh start available", "New streak, who dis? Yesterday is history, today is opportunity.", "Begin again"),
        Triple("Remember why you started", "Mbappe didn't reach the top by giving up. Neither will you.", "Continue"),
        Triple("Redemption arc loading", "The best anime characters have comeback arcs. This is yours.", "Write the script"),
        Triple("Recovery mode: ON", "Even the All Blacks lose sometimes. The legends come back harder.", "Bounce back"),
        Triple("Still in the game", "Missing one day is a red card, not a career ender. Serve your time, return stronger.", "Sub back in")
    )

    // Future message delivery notifications - time travel vibes
    val futureMessageDelivery = listOf(
        Triple("A message from the past!", "Past-you was basically a time traveler. This is your letter.", "Read it"),
        Triple("Special delivery", "Your past self sent this. It's like a DM from a younger, hopefully wiser you.", "Open message"),
        Triple("Remember when you wrote this?", "Past-you was pretty wise. Check out what they said.", "See message"),
        Triple("Message unlocked", "From a younger, hopeful you. They believed in this moment.", "Read now"),
        Triple("Time capsule opened!", "Dr. Strange would be proud of this temporal messaging.", "Discover"),
        Triple("Past-you has entered the chat", "They had something important to say. Don't leave them on read.", "Reply with actions"),
        Triple("Incoming transmission", "From the archives of your past. This hit different now, probably.", "Access file"),
        Triple("Throwback Thursday but profound", "Past-you was cooking. See what they whipped up.", "Check it out")
    )

    // Achievement unlocked notifications - gaming references
    val achievementUnlocked = listOf(
        Triple("Achievement unlocked!", "You just earned '%s'. Xbox achievement noise plays in head.", "View achievement"),
        Triple("Level up!", "'%s' is now yours. XP has been distributed to your soul.", "Check it out"),
        Triple("New trophy acquired", "'%s' has been added to your collection. PlayStation platinum energy.", "See your badge"),
        Triple("You did it!", "'%s' achieved. Speedrun any%% world record pace.", "Celebrate"),
        Triple("Trophy alert", "'%s' is now yours. More satisfying than a goal in FIFA.", "View trophy"),
        Triple("Rare achievement!", "'%s' unlocked. Only the dedicated get this one.", "Flex it"),
        Triple("Badge secured", "'%s' is now in your inventory. Equip it with pride.", "Equip now"),
        Triple("Victory royale!", "'%s' is yours. You outlasted the competition.", "Winner winner")
    )

    // Quote notifications - wisdom with personality
    val quoteNotifications = listOf(
        Triple("Wisdom incoming", "Today's quote might just change your perspective. Or at least make you think.", "Read quote"),
        Triple("Words to live by", "Someone smart said something quotable. Might want to steal this for your bio.", "Show me"),
        Triple("Daily inspiration", "A quote that somehow relates to your life. The algorithm doesn't miss.", "Discover"),
        Triple("Thoughts from the greats", "Standing on the shoulders of giants, one quote at a time.", "Get inspired"),
        Triple("Mental snack time", "A bite-sized piece of wisdom is ready for consumption.", "Consume wisdom"),
        Triple("Fortune cookie energy", "But actually profound and not generic. You're welcome.", "Read it"),
        Triple("Philosophy drop", "Nietzsche is typing... just kidding. But the wisdom is real.", "Check it"),
        Triple("Caption material", "This quote might just be your next Instagram bio update.", "See quote")
    )

    // Leaderboard updates - competitive sports energy
    val leaderboardUpdates = listOf(
        Triple("You're climbing!", "You've moved up to #%d on the leaderboard. Ballon d'Or campaign incoming.", "See standings"),
        Triple("Competition heating up", "Someone's catching up! You're currently at #%d. Defend like prime Maldini.", "View leaderboard"),
        Triple("Top performer alert", "You're in the top 10! Position #%d and rising. UCL anthem playing.", "Check rank"),
        Triple("Peer appreciation", "Someone just boosted you! The community has your back like a proper 12th man.", "See who"),
        Triple("Congratulations received", "A peer just celebrated your progress. Good vibes incoming.", "View message"),
        Triple("Rising through ranks", "#%d and climbing. This is giving 'new signing of the season' vibes.", "Check standings"),
        Triple("Title race update", "You're at #%d. The league table is getting spicy.", "View table"),
        Triple("Form is temporary", "But you at #%d? That's permanent until proven otherwise.", "Stay on top")
    )

    // Weekly summary notifications - analyst energy
    val weeklySummary = listOf(
        Triple("Your week in review", "Buddha has some thoughts on your journey this week. Post-match analysis incoming.", "View summary"),
        Triple("Weekly wisdom drop", "A personalized reflection on your 7-day adventure awaits.", "Read now"),
        Triple("Progress report", "The numbers are in. Your week was productive in ways that matter.", "See stats"),
        Triple("Week wrapped", "Before you start a new week, see how the last one shaped you.", "Review week"),
        Triple("Match report ready", "Like a post-game analysis, but for your life. Stats included.", "Analyze"),
        Triple("Highlights reel", "Your best moments from the week, curated by Buddha himself.", "Watch highlights")
    )

    // Random encouragement - meme energy with depth
    val randomEncouragement = listOf(
        Triple("Just checking in", "You're doing better than you think. Seriously. Main character behavior.", "Thanks, I needed that"),
        Triple("Random reminder", "You're not behind. Life isn't a race, it's a single-player RPG.", "Appreciate it"),
        Triple("Quick note", "Growth isn't always visible. Even bamboo spends years underground.", "Got it"),
        Triple("Hey, you", "Taking time for self-improvement? That's already winning.", "Feeling good"),
        Triple("PSA incoming", "The fact that you're working on yourself puts you ahead of most. True story.", "Thanks Buddha"),
        Triple("Mid-day motivation", "Ronaldo didn't become the GOAT by checking his phone. But since you're here, hi.", "Hi Buddha"),
        Triple("Friendly reminder", "You've survived 100% of your worst days. Undefeated record.", "Still standing"),
        Triple("Reality check", "You're literally evolving. Pokémon could never.", "Level up"),
        Triple("Elite mentality detected", "Working on yourself while others scroll? That's the difference.", "Built different"),
        Triple("Vibe check", "Your energy is immaculate. Keep doing what you're doing.", "No cap")
    )

    // Evening reflection notifications - wind-down vibes
    val eveningReflection = listOf(
        Triple("Evening reflection time", "How did today shape you? Buddha's ready to listen. No cap.", "Reflect now"),
        Triple("Day's end wisdom", "Before you rest, let's process today's journey together.", "Start reflection"),
        Triple("Sunset thoughts", "The day is winding down. What insights are emerging from the fog?", "Share thoughts"),
        Triple("Evening check-in", "Time to pause and reflect on today's experiences. Full-time whistle.", "Begin reflection"),
        Triple("Post-credit scene", "The day's movie is ending. What's in the post-credits?", "Reflect"),
        Triple("Daily recap", "Like a sports anchor summarizing the day's games. You're the headline.", "Review"),
        Triple("Golden hour wisdom", "The best reflections happen when the sun goes down.", "Contemplate")
    )

    // Word of the day notifications - nerd energy
    val wordOfDay = listOf(
        Triple("Word of the day!", "Expand your vocabulary with today's featured word. Big brain time.", "Learn word"),
        Triple("New word unlocked", "Your linguistic arsenal just got stronger. +10 intelligence.", "Discover word"),
        Triple("Vocabulary boost", "Today's word will make you sound smarter instantly. Guaranteed.", "See word"),
        Triple("Word wisdom", "A new word awaits to enrich your conversations. Flex responsibly.", "Explore word"),
        Triple("Lexicon expansion", "One word closer to being insufferably smart at parties.", "Add to brain"),
        Triple("Dictionary energy", "New word just dropped. It's giving 'intellectual' vibes.", "Check it"),
        Triple("Word nerd alert", "Today's word is *chef's kiss*. Your vocab is about to level up.", "Teach me"),
        Triple("Linguistic upgrade", "Installing new word... Download complete when you tap.", "Install now")
    )

    // Future message received notifications - emotional hits
    val futureMessageReceived = listOf(
        Triple("Message from the past!", "Your past self sent you something important. Emotional damage incoming.", "Read message"),
        Triple("Time capsule opened", "A message from yesterday's you has arrived. Hits different now.", "View message"),
        Triple("Special delivery", "Your future message has been delivered right on time. Fate energy.", "Open message"),
        Triple("Past-you says hello", "A thoughtful message from your earlier self awaits. Probably profound.", "Read now"),
        Triple("Temporal mail arrived", "Past-you was really out here writing you letters. Wholesome.", "Open"),
        Triple("The prophecy fulfilled", "Past-you predicted this moment. See what they said.", "Reveal")
    )

    // Streak reminder notifications - competitive edge
    val streakReminder = listOf(
        Triple("Keep your streak alive!", "Don't let your progress streak end today. Champions don't rest.", "Continue streak"),
        Triple("Streak check-in", "Your consistency is impressive. More reliable than Benzema.", "Maintain streak"),
        Triple("Daily habit reminder", "Your streak depends on today's action. Like penalties, don't miss.", "Take action"),
        Triple("Consistency matters", "Every day counts toward your growing streak. Building legacy.", "Stay consistent"),
        Triple("Streak on the line", "One action away from extending your run. Do it for the culture.", "Extend"),
        Triple("Don't break the chain", "Seinfeld didn't become GOAT by breaking chains. Neither should you.", "Keep going"),
        Triple("Defender of the streak", "Your streak needs protecting. Channel your inner Van Dijk.", "Protect it"),
        Triple("Almost forgot!", "Your streak almost ended. But we caught you. Close call.", "Save streak")
    )

    // Journal prompt notifications - thought-provoking
    val journalPrompt = listOf(
        Triple("Journal time!", "Your thoughts are waiting to be captured. No filter required.", "Start journaling"),
        Triple("Reflection moment", "What's on your mind today? Let's explore it. Safe space.", "Open journal"),
        Triple("Daily writing", "A few minutes of journaling can unlock insights. Trust the process.", "Begin writing"),
        Triple("Thought capture", "Your journal is ready for today's reflections. Brain dump authorized.", "Write now"),
        Triple("Creative outlet", "Express yourself. Your journal doesn't judge. Unlike Twitter.", "Express yourself"),
        Triple("Mental cleanse", "Journaling is like a shower for your thoughts. Get clean.", "Cleanse"),
        Triple("Story time", "You're the author. Today's page awaits your narrative.", "Write your story"),
        Triple("Inner monologue", "Turn that inner monologue into external reflection. Document the vibes.", "Document")
    )

    // Vocabulary-specific engaging notifications - nerdy excellence
    val vocabularyNotifications = listOf(
        Triple("Word nerd alert", "New vocabulary incoming. Your conversations are about to level up.", "Teach me"),
        Triple("Linguistic upgrade available", "Install now? A new word will make you sound 27% smarter.*", "Install word"),
        Triple("Your lexicon is calling", "It wants to grow. Today's word is the gains your vocab needs.", "Answer the call"),
        Triple("Vocabulary expansion pack", "Free DLC for your brain. One new word, unlimited uses.", "Download now"),
        Triple("Fun fact:", "People with larger vocabularies are perceived as more intelligent. Just saying.", "Expand vocab"),
        Triple("Word of power", "Today's word could be your new favorite flex. Use wisely.", "Acquire power"),
        Triple("Linguistic level-up", "XP gained: vocabulary. You're one word closer to being a wizard.", "Level up"),
        Triple("Thesaurus energy", "Why use simple words when you can use today's word? Elevated.", "Elevate"),
        Triple("Big brain incoming", "Today's word is giving 'I read books' energy. In a good way.", "Get smart"),
        Triple("Webster's calling", "The dictionary has a new best friend for you. Word edition.", "Meet them")
    )

    // Football/Sports elite references - for the ballers
    val eliteBallKnowledge = listOf(
        Triple("Ball knowledge check", "Only real ones understand why consistency matters. Prove you're built different.", "I'm built different"),
        Triple("Tactical awareness", "Like Pirlo reading the game, learn to read your own progress.", "Read the game"),
        Triple("Total football", "Cruyff believed in constant movement. Your personal growth should too.", "Keep moving"),
        Triple("Elite mentality", "Klopp says: 'We are mentality monsters.' Are you?", "Monster mode"),
        Triple("Transfer window", "Time to invest in yourself. Your potential market value is rising.", "Invest"),
        Triple("Champions League nights", "Big moments require big players. Today's your UCL anthem moment.", "Anthem plays"),
        Triple("The beautiful game", "Self-improvement is the beautiful game of life. Play it well.", "Play on"),
        Triple("Golden boot race", "You're top scorer in your own life's league. Keep finding the net.", "Score again")
    )

    // Meme-inspired notifications - internet culture
    val memeNotifications = listOf(
        Triple("This hits different", "Today's wisdom really do be hitting different tho.", "Facts"),
        Triple("No cap detected", "We're being 100% real with you. Growth is happening.", "On god"),
        Triple("Understood the assignment", "You've been showing up. We see you. Main character.", "Thanks fam"),
        Triple("It's giving growth", "Your progress is giving 'I woke up and chose greatness' vibes.", "Slay"),
        Triple("POV:", "You're becoming the best version of yourself. Character arc complete.", "New season"),
        Triple("Real ones know", "The difference between winners and everyone else? Showing up daily.", "Real"),
        Triple("No thoughts, just vibes", "Just kidding. We have thoughts. And they're about your growth.", "Share them"),
        Triple("Siri play", "Levels by Avicii, because that's what you're doing - leveling up.", "Play")
    )

    /**
     * Gets a random notification from a category with placeholder replacement.
     */
    fun getRandomNotification(category: List<Triple<String, String, String>>): Triple<String, String, String> {
        return category.random()
    }

    /**
     * Gets a streak notification with the streak count filled in.
     */
    fun getStreakNotification(streakDays: Int): Triple<String, String, String> {
        val notification = streakMotivation.random()
        return Triple(
            notification.first,
            notification.second.replace("%d", streakDays.toString()),
            notification.third
        )
    }

    /**
     * Gets an achievement notification with the achievement name filled in.
     */
    fun getAchievementNotification(achievementName: String): Triple<String, String, String> {
        val notification = achievementUnlocked.random()
        return Triple(
            notification.first,
            notification.second.replace("%s", achievementName),
            notification.third
        )
    }

    /**
     * Gets a leaderboard notification with rank filled in.
     */
    fun getLeaderboardNotification(rank: Int): Triple<String, String, String> {
        val notification = leaderboardUpdates[Random.nextInt(3)] // First 3 have rank
        return Triple(
            notification.first,
            notification.second.replace("%d", rank.toString()),
            notification.third
        )
    }

    /**
     * Gets a random elite ball knowledge notification.
     */
    fun getEliteBallKnowledgeNotification(): Triple<String, String, String> {
        return eliteBallKnowledge.random()
    }

    /**
     * Gets a meme-style notification for casual engagement.
     */
    fun getMemeNotification(): Triple<String, String, String> {
        return memeNotifications.random()
    }

    /**
     * Gets a mixed notification - randomly picks from engaging categories.
     */
    fun getEngagingRandomNotification(): Triple<String, String, String> {
        val categories = listOf(
            randomEncouragement,
            memeNotifications,
            eliteBallKnowledge,
            vocabularyNotifications
        )
        return categories.random().random()
    }
}
