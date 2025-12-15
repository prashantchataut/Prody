package com.prody.prashant.data.content

import java.util.Calendar

/**
 * Prody Wisdom Content Library
 *
 * Static wisdom quotes for fallback when AI is unavailable.
 * Organized by theme for contextual selection.
 * Mix of classic philosophy and modern practical wisdom.
 * NOT overly academic or preachy - designed to be relatable and actionable.
 *
 * Features:
 * - 75+ carefully curated quotes across 7 themes
 * - Time-of-day appropriate quote selection
 * - Theme-based filtering for contextual delivery
 * - Stoic, Buddhist, and modern wisdom traditions
 */
object WisdomContent {

    // =============================================================================
    // GROWTH & PROGRESS
    // =============================================================================

    val growthQuotes = listOf(
        Quote(
            text = "Small steps still move you forward.",
            source = "Prody Wisdom",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "You don't have to be great to start, but you have to start to be great.",
            source = "Zig Ziglar",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "Progress, not perfection.",
            source = "Unknown",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "The only person you should try to be better than is who you were yesterday.",
            source = "Unknown",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "Growth is uncomfortable. Staying the same is uncomfortable. Choose your uncomfortable.",
            source = "Unknown",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "Every expert was once a beginner.",
            source = "Helen Hayes",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "The journey of a thousand miles begins with a single step.",
            source = "Lao Tzu",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "Don't compare your beginning to someone else's middle.",
            source = "Tim Hiller",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "Growth and comfort do not coexist.",
            source = "Ginni Rometty",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "What got you here won't get you there.",
            source = "Marshall Goldsmith",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "The only impossible journey is the one you never begin.",
            source = "Tony Robbins",
            theme = Theme.GROWTH
        ),
        Quote(
            text = "A year from now you'll wish you had started today.",
            source = "Karen Lamb",
            theme = Theme.GROWTH
        )
    )

    // =============================================================================
    // RESILIENCE & CHALLENGES
    // =============================================================================

    val resilienceQuotes = listOf(
        Quote(
            text = "Tough times don't last. Tough people do.",
            source = "Robert Schuller",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "The obstacle is the way.",
            source = "Marcus Aurelius",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "What stands in the way becomes the way.",
            source = "Marcus Aurelius",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "This too shall pass. It always does.",
            source = "Prody Wisdom",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "You may have to fight a battle more than once to win it.",
            source = "Margaret Thatcher",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "Fall seven times, stand up eight.",
            source = "Japanese Proverb",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "Rock bottom became the solid foundation on which I rebuilt my life.",
            source = "J.K. Rowling",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "The bamboo that bends is stronger than the oak that resists.",
            source = "Japanese Proverb",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "In the middle of difficulty lies opportunity.",
            source = "Albert Einstein",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "Strength does not come from winning. Your struggles develop your strengths.",
            source = "Arnold Schwarzenegger",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "A smooth sea never made a skilled sailor.",
            source = "Franklin D. Roosevelt",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "When everything seems to be going against you, remember that the airplane takes off against the wind.",
            source = "Henry Ford",
            theme = Theme.RESILIENCE
        )
    )

    // =============================================================================
    // GRATITUDE & APPRECIATION
    // =============================================================================

    val gratitudeQuotes = listOf(
        Quote(
            text = "Gratitude turns what we have into enough.",
            source = "Melody Beattie",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "The more you praise and celebrate life, the more there is to celebrate.",
            source = "Oprah Winfrey",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "When you arise in the morning, think of what a precious privilege it is to be alive.",
            source = "Marcus Aurelius",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "Acknowledging the good that you already have in your life is the foundation for all abundance.",
            source = "Eckhart Tolle",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "Enjoy the little things, for one day you may look back and realize they were the big things.",
            source = "Robert Brault",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "The roots of all goodness lie in the soil of appreciation for goodness.",
            source = "Dalai Lama",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "Gratitude makes sense of our past, brings peace for today, and creates a vision for tomorrow.",
            source = "Melody Beattie",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "It is not happy people who are thankful. It is thankful people who are happy.",
            source = "Unknown",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "The secret to having it all is knowing you already do.",
            source = "Unknown",
            theme = Theme.GRATITUDE
        ),
        Quote(
            text = "What if you woke up today with only the things you thanked God for yesterday?",
            source = "Unknown",
            theme = Theme.GRATITUDE
        )
    )

    // =============================================================================
    // MINDFULNESS & PRESENCE
    // =============================================================================

    val mindfulnessQuotes = listOf(
        Quote(
            text = "Be where you are, not where you think you should be.",
            source = "Unknown",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "The present moment is the only moment available to us, and it is the door to all moments.",
            source = "Thich Nhat Hanh",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "The present moment is filled with joy and happiness. If you are attentive, you will see it.",
            source = "Thich Nhat Hanh",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "Wherever you are, be all there.",
            source = "Jim Elliot",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "Life is available only in the present moment.",
            source = "Thich Nhat Hanh",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "The mind is everything. What you think you become.",
            source = "Buddha",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "Realize deeply that the present moment is all you have.",
            source = "Eckhart Tolle",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "Do not dwell in the past, do not dream of the future, concentrate the mind on the present moment.",
            source = "Buddha",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "Almost everything will work again if you unplug it for a few minutes, including you.",
            source = "Anne Lamott",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "Your calm mind is the ultimate weapon against your challenges.",
            source = "Bryant McGill",
            theme = Theme.MINDFULNESS
        )
    )

    // =============================================================================
    // ACTION & DISCIPLINE
    // =============================================================================

    val actionQuotes = listOf(
        Quote(
            text = "Discipline is choosing between what you want now and what you want most.",
            source = "Abraham Lincoln",
            theme = Theme.ACTION
        ),
        Quote(
            text = "You can't think your way into a new way of living. You have to live your way into a new way of thinking.",
            source = "Richard Rohr",
            theme = Theme.ACTION
        ),
        Quote(
            text = "Action is the foundational key to all success.",
            source = "Pablo Picasso",
            theme = Theme.ACTION
        ),
        Quote(
            text = "The secret of getting ahead is getting started.",
            source = "Mark Twain",
            theme = Theme.ACTION
        ),
        Quote(
            text = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            source = "Aristotle",
            theme = Theme.ACTION
        ),
        Quote(
            text = "You don't have to be motivated to start. You have to start to be motivated.",
            source = "Unknown",
            theme = Theme.ACTION
        ),
        Quote(
            text = "The only way to do great work is to love what you do.",
            source = "Steve Jobs",
            theme = Theme.ACTION
        ),
        Quote(
            text = "If you want something you've never had, you must be willing to do something you've never done.",
            source = "Thomas Jefferson",
            theme = Theme.ACTION
        ),
        Quote(
            text = "Your life doesn't get better by chance, it gets better by change.",
            source = "Jim Rohn",
            theme = Theme.ACTION
        ),
        Quote(
            text = "Don't wait for the perfect moment. Take the moment and make it perfect.",
            source = "Unknown",
            theme = Theme.ACTION
        ),
        Quote(
            text = "Success is the sum of small efforts, repeated day in and day out.",
            source = "Robert Collier",
            theme = Theme.ACTION
        ),
        Quote(
            text = "What you do today can improve all your tomorrows.",
            source = "Ralph Marston",
            theme = Theme.ACTION
        )
    )

    // =============================================================================
    // SELF-COMPASSION
    // =============================================================================

    val selfCompassionQuotes = listOf(
        Quote(
            text = "Be gentle with yourself. You're doing the best you can.",
            source = "Prody Wisdom",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "You're allowed to be both a masterpiece and a work in progress.",
            source = "Sophia Bush",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "Talk to yourself like you would to someone you love.",
            source = "Brené Brown",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "You yourself, as much as anybody in the entire universe, deserve your love and affection.",
            source = "Buddha",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "Forgive yourself for not knowing what you didn't know before you learned it.",
            source = "Maya Angelou",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "Be patient with yourself. Self-growth is tender; it's holy ground.",
            source = "Stephen Covey",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "You are imperfect, you are wired for struggle, but you are worthy of love and belonging.",
            source = "Brené Brown",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "Stop beating yourself up. You are a work in progress, which means you get there a little at a time.",
            source = "Unknown",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "If you want to fly, give up everything that weighs you down, including the weight of self-criticism.",
            source = "Prody Wisdom",
            theme = Theme.SELF_COMPASSION
        ),
        Quote(
            text = "Treat yourself as if you were someone you are responsible for helping.",
            source = "Jordan Peterson",
            theme = Theme.SELF_COMPASSION
        )
    )

    // =============================================================================
    // PERSPECTIVE
    // =============================================================================

    val perspectiveQuotes = listOf(
        Quote(
            text = "It's not what happens to you, but how you react to it that matters.",
            source = "Epictetus",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "Between stimulus and response there is a space. In that space is our power to choose.",
            source = "Viktor Frankl",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "Change the way you look at things and the things you look at change.",
            source = "Wayne Dyer",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "Everything can be taken from a man but one thing: the last of the human freedoms—to choose one's attitude.",
            source = "Viktor Frankl",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "The world as we have created it is a process of our thinking. It cannot be changed without changing our thinking.",
            source = "Albert Einstein",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "You cannot control what happens to you, but you can control your attitude toward what happens to you.",
            source = "Brian Tracy",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "When we are no longer able to change a situation, we are challenged to change ourselves.",
            source = "Viktor Frankl",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "The only thing you sometimes have control over is perspective. You don't have control over your situation.",
            source = "Chris Pine",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "In life, pain is inevitable but suffering is optional.",
            source = "Buddhist Proverb",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "What you focus on expands.",
            source = "Unknown",
            theme = Theme.PERSPECTIVE
        )
    )

    // =============================================================================
    // STOIC WISDOM - Special category for Buddha's responses
    // =============================================================================

    val stoicQuotes = listOf(
        Quote(
            text = "He suffers more than necessary, who suffers before it is necessary.",
            source = "Seneca",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "We suffer more often in imagination than in reality.",
            source = "Seneca",
            theme = Theme.PERSPECTIVE
        ),
        Quote(
            text = "The happiness of your life depends upon the quality of your thoughts.",
            source = "Marcus Aurelius",
            theme = Theme.MINDFULNESS
        ),
        Quote(
            text = "Waste no more time arguing about what a good man should be. Be one.",
            source = "Marcus Aurelius",
            theme = Theme.ACTION
        ),
        Quote(
            text = "You have power over your mind - not outside events. Realize this, and you will find strength.",
            source = "Marcus Aurelius",
            theme = Theme.RESILIENCE
        ),
        Quote(
            text = "No man is free who is not master of himself.",
            source = "Epictetus",
            theme = Theme.ACTION
        ),
        Quote(
            text = "First say to yourself what you would be; and then do what you have to do.",
            source = "Epictetus",
            theme = Theme.ACTION
        ),
        Quote(
            text = "It is not that we have a short time to live, but that we waste a lot of it.",
            source = "Seneca",
            theme = Theme.ACTION
        ),
        Quote(
            text = "Luck is what happens when preparation meets opportunity.",
            source = "Seneca",
            theme = Theme.ACTION
        ),
        Quote(
            text = "If it is not right, do not do it; if it is not true, do not say it.",
            source = "Marcus Aurelius",
            theme = Theme.ACTION
        )
    )

    // =============================================================================
    // ALL QUOTES COMBINED
    // =============================================================================

    val allQuotes: List<Quote> by lazy {
        growthQuotes + resilienceQuotes + gratitudeQuotes +
                mindfulnessQuotes + actionQuotes + selfCompassionQuotes +
                perspectiveQuotes + stoicQuotes
    }

    // =============================================================================
    // HELPER FUNCTIONS
    // =============================================================================

    /**
     * Get a random quote from all available quotes.
     */
    fun getRandomQuote(): Quote = allQuotes.random()

    /**
     * Get a random quote filtered by theme.
     *
     * @param theme The theme to filter by
     * @return A random quote matching the theme, or null if no matches
     */
    fun getQuoteByTheme(theme: Theme): Quote? =
        allQuotes.filter { it.theme == theme }.randomOrNull()

    /**
     * Get a quote appropriate for the current time of day.
     * - Morning (5-11): Action, Growth oriented
     * - Afternoon (12-17): Resilience, Perspective
     * - Evening/Night: Gratitude, Mindfulness, Self-Compassion
     *
     * @param hour The current hour (0-23)
     * @return A contextually appropriate quote
     */
    fun getQuoteForTimeOfDay(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): Quote {
        return when {
            hour in 5..11 -> {
                // Morning - Action, Growth oriented
                (actionQuotes + growthQuotes).random()
            }
            hour in 12..17 -> {
                // Afternoon - Resilience, Perspective
                (resilienceQuotes + perspectiveQuotes).random()
            }
            else -> {
                // Evening/Night - Gratitude, Mindfulness, Self-Compassion
                (gratitudeQuotes + mindfulnessQuotes + selfCompassionQuotes).random()
            }
        }
    }

    /**
     * Get a quote based on user's mood.
     *
     * @param moodName The name of the mood (e.g., "Happy", "Sad", "Anxious")
     * @return A mood-appropriate quote
     */
    fun getQuoteForMood(moodName: String): Quote {
        return when (moodName.lowercase()) {
            "happy", "excited", "grateful" -> gratitudeQuotes.random()
            "sad", "down", "melancholy" -> selfCompassionQuotes.random()
            "anxious", "worried", "stressed" -> mindfulnessQuotes.random()
            "motivated", "energetic", "inspired" -> actionQuotes.random()
            "confused", "lost", "uncertain" -> perspectiveQuotes.random()
            "calm", "peaceful", "serene" -> mindfulnessQuotes.random()
            "frustrated", "angry", "upset" -> resilienceQuotes.random()
            else -> getRandomQuote()
        }
    }

    /**
     * Get multiple unique quotes.
     *
     * @param count Number of quotes to return
     * @return A list of unique quotes
     */
    fun getMultipleQuotes(count: Int): List<Quote> =
        allQuotes.shuffled().take(count.coerceAtMost(allQuotes.size))

    /**
     * Get a Stoic quote (for Buddha's wisdom responses).
     */
    fun getStoicQuote(): Quote = stoicQuotes.random()

    // =============================================================================
    // DATA CLASSES
    // =============================================================================

    /**
     * Represents a wisdom quote with its source and theme.
     */
    data class Quote(
        val text: String,
        val source: String,
        val theme: Theme
    )

    /**
     * Theme categories for quotes.
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
}
