package com.prody.prashant.data.content

/**
 * Prody Wisdom Content Library
 *
 * A comprehensive, production-grade collection of wisdom quotes for fallback
 * when AI is unavailable, organized by theme for contextual selection.
 *
 * Features:
 * - 60+ curated wisdom quotes across 7 themes
 * - Mix of classic philosophy and modern practical wisdom
 * - NOT overly academic or preachy - accessible and actionable
 * - Time-of-day appropriate selection
 * - Theme-based filtering for contextual relevance
 *
 * Design Philosophy:
 * - Quotes that inspire action, not just contemplation
 * - Accessible wisdom from diverse sources
 * - Practical insights applicable to daily life
 * - Brief enough to remember, deep enough to ponder
 */
object WisdomContent {

    // =========================================================================
    // DATA CLASSES
    // =========================================================================

    /**
     * Represents a wisdom quote with metadata.
     */
    data class Quote(
        val text: String,
        val source: String,
        val theme: Theme,
        val reflectionPrompt: String? = null,
        val isFeatured: Boolean = false
    )

    /**
     * Wisdom themes for categorization.
     */
    enum class Theme {
        GROWTH,
        RESILIENCE,
        GRATITUDE,
        MINDFULNESS,
        ACTION,
        SELF_COMPASSION,
        PERSPECTIVE
    }

    // =========================================================================
    // GROWTH & PROGRESS QUOTES
    // =========================================================================

    val growthQuotes = listOf(
        Quote(
            text = "Small steps still move you forward.",
            source = "Prody Wisdom",
            theme = Theme.GROWTH,
            reflectionPrompt = "What small step could you take today?",
            isFeatured = true
        ),
        Quote(
            text = "You don't have to be great to start, but you have to start to be great.",
            source = "Zig Ziglar",
            theme = Theme.GROWTH,
            reflectionPrompt = "What have you been waiting to start?"
        ),
        Quote(
            text = "Progress, not perfection.",
            source = "Prody Wisdom",
            theme = Theme.GROWTH,
            reflectionPrompt = "Where are you demanding perfection when progress would suffice?"
        ),
        Quote(
            text = "The only person you should try to be better than is who you were yesterday.",
            source = "Prody Wisdom",
            theme = Theme.GROWTH,
            reflectionPrompt = "In what way are you better than yesterday?"
        ),
        Quote(
            text = "Growth is uncomfortable. Staying the same is uncomfortable. Choose your uncomfortable.",
            source = "Prody Wisdom",
            theme = Theme.GROWTH,
            reflectionPrompt = "Which discomfort are you choosing?"
        ),
        Quote(
            text = "The expert in anything was once a beginner.",
            source = "Helen Hayes",
            theme = Theme.GROWTH,
            reflectionPrompt = "What are you willing to be a beginner at?"
        ),
        Quote(
            text = "Your comfort zone is a beautiful place, but nothing ever grows there.",
            source = "Prody Wisdom",
            theme = Theme.GROWTH,
            reflectionPrompt = "What's one step outside your comfort zone?"
        ),
        Quote(
            text = "What got you here won't get you there.",
            source = "Marshall Goldsmith",
            theme = Theme.GROWTH,
            reflectionPrompt = "What new skill or habit do you need to develop?"
        ),
        Quote(
            text = "Every accomplishment starts with the decision to try.",
            source = "Prody Wisdom",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "Become who you are by learning who you are.",
            source = "Pindar",
            theme = Theme.GROWTH,
            reflectionPrompt = "What have you learned about yourself recently?"
        )
    )

    // =========================================================================
    // RESILIENCE & CHALLENGES QUOTES
    // =========================================================================

    val resilienceQuotes = listOf(
        Quote(
            text = "Tough times don't last. Tough people do.",
            source = "Robert Schuller",
            theme = Theme.RESILIENCE,
            isFeatured = true
        ),
        Quote(
            text = "The obstacle is the way.",
            source = "Marcus Aurelius",
            theme = Theme.RESILIENCE,
            reflectionPrompt = "How might your current obstacle be showing you the path forward?"
        ),
        Quote(
            text = "What stands in the way becomes the way.",
            source = "Marcus Aurelius, Meditations",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "This too shall pass. It always does.",
            source = "Prody Wisdom",
            theme = Theme.RESILIENCE,
            reflectionPrompt = "What difficulty have you overcome that felt impossible at the time?"
        ),
        Quote(
            text = "Rock bottom became the solid foundation on which I rebuilt my life.",
            source = "J.K. Rowling",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "The wound is the place where the light enters you.",
            source = "Rumi",
            theme = Theme.RESILIENCE,
            reflectionPrompt = "What has your struggle taught you?"
        ),
        Quote(
            text = "Fall seven times, stand up eight.",
            source = "Japanese Proverb",
            theme = Theme.RESILIENCE,
            isFeatured = true
        ),
        Quote(
            text = "You may have to fight a battle more than once to win it.",
            source = "Margaret Thatcher",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "A smooth sea never made a skilled sailor.",
            source = "Franklin D. Roosevelt",
            theme = Theme.RESILIENCE,
            reflectionPrompt = "What skill have your challenges developed?"
        ),
        Quote(
            text = "The bamboo that bends is stronger than the oak that resists.",
            source = "Japanese Proverb",
            theme = Theme.RESILIENCE,
            reflectionPrompt = "Where in your life could you be more flexible?"
        )
    )

    // =========================================================================
    // GRATITUDE & APPRECIATION QUOTES
    // =========================================================================

    val gratitudeQuotes = listOf(
        Quote(
            text = "Gratitude turns what we have into enough.",
            source = "Melody Beattie",
            theme = Theme.GRATITUDE,
            reflectionPrompt = "What do you have that is already enough?",
            isFeatured = true
        ),
        Quote(
            text = "The more you praise and celebrate life, the more there is to celebrate.",
            source = "Oprah Winfrey",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "Acknowledging the good that you already have in your life is the foundation for all abundance.",
            source = "Eckhart Tolle",
            theme = Theme.GRATITUDE,
            reflectionPrompt = "What three things are you grateful for right now?"
        ),
        Quote(
            text = "When you are grateful, fear disappears and abundance appears.",
            source = "Tony Robbins",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "Enjoy the little things, for one day you may look back and realize they were the big things.",
            source = "Robert Brault",
            theme = Theme.GRATITUDE,
            reflectionPrompt = "What small moment brought you joy recently?"
        ),
        Quote(
            text = "Gratitude is not only the greatest of virtues but the parent of all others.",
            source = "Cicero",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "The root of joy is gratefulness.",
            source = "David Steindl-Rast",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "If the only prayer you ever say is 'thank you', that will be enough.",
            source = "Meister Eckhart",
            theme = Theme.GRATITUDE,
            isFeatured = true
        )
    )

    // =========================================================================
    // MINDFULNESS & PRESENCE QUOTES
    // =========================================================================

    val mindfulnessQuotes = listOf(
        Quote(
            text = "Be where you are, not where you think you should be.",
            source = "Prody Wisdom",
            theme = Theme.MINDFULNESS,
            reflectionPrompt = "Are you fully here right now?",
            isFeatured = true
        ),
        Quote(
            text = "The present moment is the only moment available to us, and it is the door to all moments.",
            source = "Thich Nhat Hanh",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "Life is available only in the present moment.",
            source = "Thich Nhat Hanh",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "Wherever you are, be all there.",
            source = "Jim Elliot",
            theme = Theme.MINDFULNESS,
            reflectionPrompt = "What's distracting you from this moment?"
        ),
        Quote(
            text = "The best way to capture moments is to pay attention.",
            source = "Jon Kabat-Zinn",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "Feelings come and go like clouds in a windy sky. Conscious breathing is my anchor.",
            source = "Thich Nhat Hanh",
            theme = Theme.MINDFULNESS,
            reflectionPrompt = "Take three conscious breaths right now."
        ),
        Quote(
            text = "Almost everything will work again if you unplug it for a few minutes, including you.",
            source = "Anne Lamott",
            theme = Theme.MINDFULNESS,
            isFeatured = true
        ),
        Quote(
            text = "In today's rush, we all think too much, seek too much, want too much, and forget about the joy of just being.",
            source = "Eckhart Tolle",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "The quieter you become, the more you can hear.",
            source = "Ram Dass",
            theme = Theme.MINDFULNESS
        )
    )

    // =========================================================================
    // ACTION & DISCIPLINE QUOTES
    // =========================================================================

    val actionQuotes = listOf(
        Quote(
            text = "Discipline is choosing between what you want now and what you want most.",
            source = "Abraham Lincoln",
            theme = Theme.ACTION,
            reflectionPrompt = "What do you want most?",
            isFeatured = true
        ),
        Quote(
            text = "You can't think your way into a new way of living. You have to live your way into a new way of thinking.",
            source = "Richard Rohr",
            theme = Theme.ACTION,
            reflectionPrompt = "What action could shift your thinking?"
        ),
        Quote(
            text = "Action is the foundational key to all success.",
            source = "Pablo Picasso",
            theme = Theme.ACTION
        ),
        Quote(
            text = "The way to get started is to quit talking and begin doing.",
            source = "Walt Disney",
            theme = Theme.ACTION,
            isFeatured = true
        ),
        Quote(
            text = "Knowing is not enough; we must apply. Willing is not enough; we must do.",
            source = "Johann Wolfgang von Goethe",
            theme = Theme.ACTION
        ),
        Quote(
            text = "Waste no more time arguing about what a good man should be. Be one.",
            source = "Marcus Aurelius",
            theme = Theme.ACTION,
            reflectionPrompt = "What could you stop debating and start doing?"
        ),
        Quote(
            text = "The best time to plant a tree was 20 years ago. The second best time is now.",
            source = "Chinese Proverb",
            theme = Theme.ACTION,
            reflectionPrompt = "What seed can you plant today?"
        ),
        Quote(
            text = "Do something today that your future self will thank you for.",
            source = "Prody Wisdom",
            theme = Theme.ACTION
        ),
        Quote(
            text = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            source = "Will Durant (summarizing Aristotle)",
            theme = Theme.ACTION,
            reflectionPrompt = "What habit defines you?"
        ),
        Quote(
            text = "Don't count the days, make the days count.",
            source = "Muhammad Ali",
            theme = Theme.ACTION
        )
    )

    // =========================================================================
    // SELF-COMPASSION QUOTES
    // =========================================================================

    val selfCompassionQuotes = listOf(
        Quote(
            text = "Be gentle with yourself. You're doing the best you can.",
            source = "Prody Wisdom",
            theme = Theme.SELF_COMPASSION,
            reflectionPrompt = "How can you be kinder to yourself today?",
            isFeatured = true
        ),
        Quote(
            text = "You're allowed to be both a masterpiece and a work in progress.",
            source = "Sophia Bush",
            theme = Theme.SELF_COMPASSION,
            isFeatured = true
        ),
        Quote(
            text = "Talk to yourself like you would to someone you love.",
            source = "Brene Brown",
            theme = Theme.SELF_COMPASSION,
            reflectionPrompt = "What would you say to a friend in your situation?"
        ),
        Quote(
            text = "You yourself, as much as anybody in the entire universe, deserve your love and affection.",
            source = "Buddha",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "Be patient with yourself. Self-growth is tender; it's holy ground.",
            source = "Stephen Covey",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "You don't have to have it all figured out to move forward.",
            source = "Prody Wisdom",
            theme = Theme.SELF_COMPASSION,
            reflectionPrompt = "What are you waiting to figure out before acting?"
        ),
        Quote(
            text = "Rest when you're weary. Refresh and renew yourself.",
            source = "Ralph Marston",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "The most terrifying thing is to accept oneself completely.",
            source = "Carl Jung",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "Perfectionism is self-abuse of the highest order.",
            source = "Anne Wilson Schaef",
            theme = Theme.SELF_COMPASSION,
            reflectionPrompt = "Where is perfectionism holding you back?"
        )
    )

    // =========================================================================
    // PERSPECTIVE QUOTES
    // =========================================================================

    val perspectiveQuotes = listOf(
        Quote(
            text = "It's not what happens to you, but how you react to it that matters.",
            source = "Epictetus",
            theme = Theme.PERSPECTIVE,
            reflectionPrompt = "How are you choosing to respond to your current situation?",
            isFeatured = true
        ),
        Quote(
            text = "Between stimulus and response there is a space. In that space is our power to choose.",
            source = "Viktor Frankl",
            theme = Theme.PERSPECTIVE,
            isFeatured = true
        ),
        Quote(
            text = "We suffer more often in imagination than in reality.",
            source = "Seneca",
            theme = Theme.PERSPECTIVE,
            reflectionPrompt = "What fear exists only in your imagination?"
        ),
        Quote(
            text = "The happiness of your life depends upon the quality of your thoughts.",
            source = "Marcus Aurelius",
            theme = Theme.PERSPECTIVE,
            reflectionPrompt = "What thought pattern could you improve?"
        ),
        Quote(
            text = "The mind is everything. What you think you become.",
            source = "Buddha",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "Change the way you look at things and the things you look at change.",
            source = "Wayne Dyer",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "It is not that we have a short time to live, but that we waste a lot of it.",
            source = "Seneca",
            theme = Theme.PERSPECTIVE,
            reflectionPrompt = "Where might you be wasting time?"
        ),
        Quote(
            text = "He who has a why to live can bear almost any how.",
            source = "Friedrich Nietzsche",
            theme = Theme.PERSPECTIVE,
            reflectionPrompt = "What is your why?"
        ),
        Quote(
            text = "The unexamined life is not worth living.",
            source = "Socrates",
            theme = Theme.PERSPECTIVE,
            reflectionPrompt = "When did you last examine your choices and beliefs?"
        ),
        Quote(
            text = "What you get by achieving your goals is not as important as what you become by achieving your goals.",
            source = "Zig Ziglar",
            theme = Theme.PERSPECTIVE
        )
    )

    // =========================================================================
    // AGGREGATED COLLECTIONS
    // =========================================================================

    /**
     * All quotes combined for random access.
     */
    val allQuotes: List<Quote> by lazy {
        growthQuotes +
                resilienceQuotes +
                gratitudeQuotes +
                mindfulnessQuotes +
                actionQuotes +
                selfCompassionQuotes +
                perspectiveQuotes
    }

    /**
     * Featured quotes for special displays.
     */
    val featuredQuotes: List<Quote> by lazy {
        allQuotes.filter { it.isFeatured }
    }

    /**
     * Quotes with reflection prompts for deeper engagement.
     */
    val reflectiveQuotes: List<Quote> by lazy {
        allQuotes.filter { it.reflectionPrompt != null }
    }

    // =========================================================================
    // HELPER FUNCTIONS
    // =========================================================================

    /**
     * Gets a completely random quote.
     */
    fun getRandomQuote(): Quote = allQuotes.random()

    /**
     * Gets a random featured quote.
     */
    fun getRandomFeaturedQuote(): Quote = featuredQuotes.random()

    /**
     * Gets a random quote by theme.
     */
    fun getQuoteByTheme(theme: Theme): Quote {
        return when (theme) {
            Theme.GROWTH -> growthQuotes.random()
            Theme.RESILIENCE -> resilienceQuotes.random()
            Theme.GRATITUDE -> gratitudeQuotes.random()
            Theme.MINDFULNESS -> mindfulnessQuotes.random()
            Theme.ACTION -> actionQuotes.random()
            Theme.SELF_COMPASSION -> selfCompassionQuotes.random()
            Theme.PERSPECTIVE -> perspectiveQuotes.random()
        }
    }

    /**
     * Gets quotes appropriate for time of day.
     *
     * Morning (5-11): Action, Growth - start the day with momentum
     * Afternoon (12-17): Resilience, Perspective - push through challenges
     * Evening (18-23): Gratitude, Mindfulness, Self-Compassion - wind down
     * Night (0-4): Mindfulness, Self-Compassion - peaceful reflection
     */
    fun getQuoteForTimeOfDay(hour: Int): Quote {
        return when (hour) {
            in 5..11 -> {
                // Morning - Action and Growth oriented
                (actionQuotes + growthQuotes).random()
            }
            in 12..17 -> {
                // Afternoon - Resilience and Perspective for pushing through
                (resilienceQuotes + perspectiveQuotes).random()
            }
            in 18..23 -> {
                // Evening - Gratitude, Mindfulness, Self-Compassion for winding down
                (gratitudeQuotes + mindfulnessQuotes + selfCompassionQuotes).random()
            }
            else -> {
                // Late night - Peaceful, gentle quotes
                (mindfulnessQuotes + selfCompassionQuotes).random()
            }
        }
    }

    /**
     * Gets a quote appropriate for the user's current mood/state.
     */
    fun getQuoteForMood(mood: String): Quote {
        return when (mood.lowercase()) {
            "happy", "joyful", "excited" -> gratitudeQuotes.random()
            "sad", "down", "low" -> selfCompassionQuotes.random()
            "anxious", "worried", "stressed" -> mindfulnessQuotes.random()
            "angry", "frustrated" -> perspectiveQuotes.random()
            "motivated", "energetic" -> actionQuotes.random()
            "confused", "lost" -> perspectiveQuotes.random()
            "grateful", "thankful" -> gratitudeQuotes.random()
            "stuck", "blocked" -> resilienceQuotes.random()
            else -> getRandomQuote()
        }
    }

    /**
     * Gets a quote with a reflection prompt for journaling.
     */
    fun getReflectiveQuote(): Quote {
        return reflectiveQuotes.random()
    }

    /**
     * Gets multiple unique quotes for display (e.g., carousel).
     */
    fun getMultipleQuotes(count: Int = 3): List<Quote> {
        return allQuotes.shuffled().take(count.coerceAtMost(allQuotes.size))
    }

    /**
     * Searches quotes by keyword in text or source.
     */
    fun searchQuotes(keyword: String): List<Quote> {
        val lowerKeyword = keyword.lowercase()
        return allQuotes.filter {
            it.text.lowercase().contains(lowerKeyword) ||
                    it.source.lowercase().contains(lowerKeyword)
        }
    }

    /**
     * Gets the total count of quotes.
     */
    fun getTotalQuoteCount(): Int = allQuotes.size

    /**
     * Gets count by theme.
     */
    fun getCountByTheme(theme: Theme): Int {
        return when (theme) {
            Theme.GROWTH -> growthQuotes.size
            Theme.RESILIENCE -> resilienceQuotes.size
            Theme.GRATITUDE -> gratitudeQuotes.size
            Theme.MINDFULNESS -> mindfulnessQuotes.size
            Theme.ACTION -> actionQuotes.size
            Theme.SELF_COMPASSION -> selfCompassionQuotes.size
            Theme.PERSPECTIVE -> perspectiveQuotes.size
        }
    }
}
