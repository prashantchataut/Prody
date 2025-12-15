package com.prody.prashant.data.content

/**
 * Prody Journal Prompts Library
 *
 * A comprehensive, production-grade collection of journal prompts designed to
 * facilitate meaningful reflection across different contexts and emotional states.
 *
 * Features:
 * - 100+ unique journal prompts across 8 categories
 * - Time-of-day appropriate prompts
 * - Quick prompts for when time is limited
 * - Creative prompts for deeper exploration
 * - Mood-based prompt suggestions
 * - Streak milestone celebration prompts
 *
 * Design Philosophy:
 * - Non-judgmental and open-ended
 * - Encourages self-discovery without prescribing answers
 * - Varies in depth - from quick captures to deep dives
 * - Respectful of user's time and emotional state
 */
object JournalPrompts {

    // =========================================================================
    // DATA CLASSES
    // =========================================================================

    /**
     * Represents a journal prompt with metadata.
     */
    data class Prompt(
        val text: String,
        val category: PromptCategory,
        val estimatedMinutes: Int = 5,
        val isQuick: Boolean = false,
        val followUp: String? = null
    )

    /**
     * Prompt categories for organization and filtering.
     */
    enum class PromptCategory {
        MORNING,
        EVENING,
        GRATITUDE,
        REFLECTION,
        GROWTH,
        EMOTIONAL,
        QUICK,
        CREATIVE,
        MILESTONE
    }

    // =========================================================================
    // MORNING PROMPTS - Start the day with intention
    // =========================================================================

    val morningPrompts = listOf(
        Prompt(
            text = "What's one thing you want to accomplish today?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 3,
            followUp = "Why does this matter to you?"
        ),
        Prompt(
            text = "How are you feeling this morning? Why?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What would make today great?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 3,
            followUp = "What can you do to make that happen?"
        ),
        Prompt(
            text = "What's your intention for today?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "If today was your best day ever, what would happen?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's one thing you're looking forward to today?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 2,
            isQuick = true
        ),
        Prompt(
            text = "How can you show up as your best self today?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 5,
            followUp = "What might get in the way, and how will you handle it?"
        ),
        Prompt(
            text = "What challenges might you face today? How will you handle them?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "If you could guarantee one win today, what would it be?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "What energy do you want to bring into today?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "Who do you want to be today? What version of yourself?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's something you've been putting off that you could tackle today?",
            category = PromptCategory.MORNING,
            estimatedMinutes = 3
        )
    )

    // =========================================================================
    // EVENING PROMPTS - Reflect and process
    // =========================================================================

    val eveningPrompts = listOf(
        Prompt(
            text = "What went well today? What could have gone better?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What are you grateful for from today?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "What did you learn today?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 5,
            followUp = "How can you apply this tomorrow?"
        ),
        Prompt(
            text = "How did you grow today?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What moment made you smile today?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 3,
            isQuick = true
        ),
        Prompt(
            text = "What drained your energy today? What energized you?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "If you could relive one moment from today, which would it be?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "What would you do differently if you could redo today?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 5,
            followUp = "How will you approach tomorrow differently?"
        ),
        Prompt(
            text = "What's something kind you did today, even small?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "How did you take care of yourself today?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "What conversation or interaction stood out today?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's one thing you're proud of from today?",
            category = PromptCategory.EVENING,
            estimatedMinutes = 3,
            isQuick = true
        )
    )

    // =========================================================================
    // GRATITUDE PROMPTS - Cultivate appreciation
    // =========================================================================

    val gratitudePrompts = listOf(
        Prompt(
            text = "Name 3 things you're grateful for right now.",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 3,
            isQuick = true
        ),
        Prompt(
            text = "Who made a positive impact on your life recently?",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 5,
            followUp = "Have you told them?"
        ),
        Prompt(
            text = "What's something you usually take for granted?",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's a simple pleasure that brought you joy lately?",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "What ability or skill are you thankful to have?",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's something in nature you appreciate?",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "Who has helped you become who you are?",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What challenge are you grateful for in hindsight?",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What comfort do you enjoy that many don't have?",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "What's something about your body you're grateful for?",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "What technology makes your life better that you rarely appreciate?",
            category = PromptCategory.GRATITUDE,
            estimatedMinutes = 3
        )
    )

    // =========================================================================
    // REFLECTION PROMPTS - Deep self-examination
    // =========================================================================

    val reflectionPrompts = listOf(
        Prompt(
            text = "What's been on your mind lately?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What patterns have you noticed in your life recently?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 7
        ),
        Prompt(
            text = "What's one thing you'd tell your younger self?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What does your ideal life look like?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 10
        ),
        Prompt(
            text = "What fears are holding you back?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 7,
            followUp = "What would you do if you weren't afraid?"
        ),
        Prompt(
            text = "What would you do if you knew you couldn't fail?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What beliefs about yourself might not be true?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 7
        ),
        Prompt(
            text = "What's something you need to let go of?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What do you need more of in your life right now?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What boundaries do you need to set or strengthen?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 7
        ),
        Prompt(
            text = "What's a decision you've been avoiding?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 5,
            followUp = "What's really stopping you?"
        ),
        Prompt(
            text = "When do you feel most like yourself?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's a relationship in your life that needs attention?",
            category = PromptCategory.REFLECTION,
            estimatedMinutes = 7
        )
    )

    // =========================================================================
    // GROWTH PROMPTS - Focus on development
    // =========================================================================

    val growthPrompts = listOf(
        Prompt(
            text = "What's one area of your life you want to improve?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 5,
            followUp = "What's one step you could take this week?"
        ),
        Prompt(
            text = "What's a mistake you learned from recently?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's outside your comfort zone that you want to try?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What habit would most improve your life?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What skill do you want to develop?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 3,
            followUp = "What's stopping you from starting?"
        ),
        Prompt(
            text = "What's holding you back from your goals?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 7
        ),
        Prompt(
            text = "What's one small step you can take toward your dreams?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "What feedback have you received that you should act on?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What would the best version of you do right now?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's a book, podcast, or idea that's influenced you recently?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "Where are you playing small when you could aim bigger?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's something you used to struggle with that you've improved at?",
            category = PromptCategory.GROWTH,
            estimatedMinutes = 5
        )
    )

    // =========================================================================
    // EMOTIONAL PROMPTS - Process feelings
    // =========================================================================

    val emotionalPrompts = listOf(
        Prompt(
            text = "How are you really feeling right now? Don't filter it.",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What emotion has been most present for you lately?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 5,
            followUp = "What might this emotion be telling you?"
        ),
        Prompt(
            text = "What's something you haven't allowed yourself to feel?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 7
        ),
        Prompt(
            text = "What would you say to a friend feeling what you're feeling?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What does your stress feel like? Where do you feel it in your body?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What brings you peace?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 3
        ),
        Prompt(
            text = "What makes you feel alive?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What are you worried about that might never happen?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "If your emotions could speak, what would they say?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 7
        ),
        Prompt(
            text = "What's something you need to forgive yourself for?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 7
        ),
        Prompt(
            text = "What makes you feel safe and secure?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What do you need to hear right now?",
            category = PromptCategory.EMOTIONAL,
            estimatedMinutes = 3
        )
    )

    // =========================================================================
    // QUICK PROMPTS - For limited time
    // =========================================================================

    val quickPrompts = listOf(
        Prompt(
            text = "One word to describe today:",
            category = PromptCategory.QUICK,
            estimatedMinutes = 1,
            isQuick = true
        ),
        Prompt(
            text = "Today's highlight:",
            category = PromptCategory.QUICK,
            estimatedMinutes = 1,
            isQuick = true
        ),
        Prompt(
            text = "I'm feeling...",
            category = PromptCategory.QUICK,
            estimatedMinutes = 1,
            isQuick = true
        ),
        Prompt(
            text = "Tomorrow I want to...",
            category = PromptCategory.QUICK,
            estimatedMinutes = 1,
            isQuick = true
        ),
        Prompt(
            text = "Right now I need...",
            category = PromptCategory.QUICK,
            estimatedMinutes = 1,
            isQuick = true
        ),
        Prompt(
            text = "One thing I appreciate:",
            category = PromptCategory.QUICK,
            estimatedMinutes = 1,
            isQuick = true
        ),
        Prompt(
            text = "A thought I want to capture:",
            category = PromptCategory.QUICK,
            estimatedMinutes = 2,
            isQuick = true
        ),
        Prompt(
            text = "The best part of today was...",
            category = PromptCategory.QUICK,
            estimatedMinutes = 1,
            isQuick = true
        ),
        Prompt(
            text = "I'm proud of myself for...",
            category = PromptCategory.QUICK,
            estimatedMinutes = 1,
            isQuick = true
        ),
        Prompt(
            text = "Something that made me think:",
            category = PromptCategory.QUICK,
            estimatedMinutes = 2,
            isQuick = true
        ),
        Prompt(
            text = "My energy level is:",
            category = PromptCategory.QUICK,
            estimatedMinutes = 1,
            isQuick = true
        ),
        Prompt(
            text = "One thing I learned:",
            category = PromptCategory.QUICK,
            estimatedMinutes = 1,
            isQuick = true
        )
    )

    // =========================================================================
    // CREATIVE PROMPTS - Imaginative exploration
    // =========================================================================

    val creativePrompts = listOf(
        Prompt(
            text = "If your life was a book, what would this chapter be called?",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "Write a letter to your future self (1 year from now).",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 10
        ),
        Prompt(
            text = "If you could have dinner with anyone, who and why?",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 5,
            followUp = "What would you ask them?"
        ),
        Prompt(
            text = "Describe your perfect day from start to finish.",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 10
        ),
        Prompt(
            text = "What would your 80-year-old self think about your current worries?",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "If money wasn't a factor, what would you do?",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's a memory that always makes you smile?",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "If you had a superpower for a day, what would it be and what would you do?",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "Write about a turning point in your life.",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 10
        ),
        Prompt(
            text = "What would you tell your 10-year-old self?",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 7
        ),
        Prompt(
            text = "If you could master any skill overnight, which would it be?",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 5,
            followUp = "How would your life change?"
        ),
        Prompt(
            text = "Describe a place where you feel completely at peace.",
            category = PromptCategory.CREATIVE,
            estimatedMinutes = 5
        )
    )

    // =========================================================================
    // MILESTONE PROMPTS - Celebrate streaks and achievements
    // =========================================================================

    val milestonePrompts = listOf(
        Prompt(
            text = "You've kept a {days}-day streak! What has this consistency taught you?",
            category = PromptCategory.MILESTONE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "Reflect on your journey so far. What are you most proud of?",
            category = PromptCategory.MILESTONE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "How has journaling changed your perspective over the past week/month?",
            category = PromptCategory.MILESTONE,
            estimatedMinutes = 7
        ),
        Prompt(
            text = "What patterns have you noticed in your entries?",
            category = PromptCategory.MILESTONE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "If you could read your first entry again, what do you think you'd notice?",
            category = PromptCategory.MILESTONE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "What's the biggest insight you've gained from journaling?",
            category = PromptCategory.MILESTONE,
            estimatedMinutes = 5
        ),
        Prompt(
            text = "How have your goals or priorities shifted since you started?",
            category = PromptCategory.MILESTONE,
            estimatedMinutes = 7
        ),
        Prompt(
            text = "Write a message to yourself to read at your next milestone.",
            category = PromptCategory.MILESTONE,
            estimatedMinutes = 5
        )
    )

    // =========================================================================
    // AGGREGATED COLLECTIONS
    // =========================================================================

    /**
     * All prompts combined for random access.
     */
    val allPrompts: List<Prompt> by lazy {
        morningPrompts +
                eveningPrompts +
                gratitudePrompts +
                reflectionPrompts +
                growthPrompts +
                emotionalPrompts +
                quickPrompts +
                creativePrompts +
                milestonePrompts
    }

    /**
     * All quick prompts for fast access.
     */
    val allQuickPrompts: List<Prompt> by lazy {
        allPrompts.filter { it.isQuick || it.estimatedMinutes <= 2 }
    }

    // =========================================================================
    // HELPER FUNCTIONS
    // =========================================================================

    /**
     * Gets prompts appropriate for time of day.
     */
    fun getPromptsForTimeOfDay(hour: Int): List<Prompt> {
        return when (hour) {
            in 5..11 -> morningPrompts
            in 18..23, in 0..4 -> eveningPrompts
            else -> reflectionPrompts
        }
    }

    /**
     * Gets a single prompt for time of day.
     */
    fun getPromptForTimeOfDay(hour: Int): Prompt {
        return getPromptsForTimeOfDay(hour).random()
    }

    /**
     * Gets random prompts across categories (for variety).
     */
    fun getRandomPrompts(count: Int = 3): List<Prompt> {
        return allPrompts.shuffled().take(count.coerceAtMost(allPrompts.size))
    }

    /**
     * Gets prompts by category.
     */
    fun getPromptsByCategory(category: PromptCategory): List<Prompt> {
        return when (category) {
            PromptCategory.MORNING -> morningPrompts
            PromptCategory.EVENING -> eveningPrompts
            PromptCategory.GRATITUDE -> gratitudePrompts
            PromptCategory.REFLECTION -> reflectionPrompts
            PromptCategory.GROWTH -> growthPrompts
            PromptCategory.EMOTIONAL -> emotionalPrompts
            PromptCategory.QUICK -> quickPrompts
            PromptCategory.CREATIVE -> creativePrompts
            PromptCategory.MILESTONE -> milestonePrompts
        }
    }

    /**
     * Gets a single random prompt from a category.
     */
    fun getRandomFromCategory(category: PromptCategory): Prompt {
        return getPromptsByCategory(category).random()
    }

    /**
     * Gets quick prompts for when time is limited.
     */
    fun getQuickPrompt(): Prompt {
        return allQuickPrompts.random()
    }

    /**
     * Gets prompts appropriate for user's mood.
     */
    fun getPromptForMood(mood: String): Prompt {
        return when (mood.lowercase()) {
            "happy", "joyful", "excited" -> gratitudePrompts.random()
            "sad", "down", "low" -> emotionalPrompts.random()
            "anxious", "worried" -> (emotionalPrompts + reflectionPrompts).random()
            "motivated", "energetic" -> growthPrompts.random()
            "grateful", "thankful" -> gratitudePrompts.random()
            "confused", "lost" -> reflectionPrompts.random()
            "creative", "inspired" -> creativePrompts.random()
            "tired", "exhausted" -> quickPrompts.random()
            else -> allPrompts.random()
        }
    }

    /**
     * Gets a milestone prompt with streak count.
     */
    fun getMilestonePrompt(streakDays: Int): Prompt {
        val prompt = milestonePrompts.random()
        return prompt.copy(
            text = prompt.text.replace("{days}", streakDays.toString())
        )
    }

    /**
     * Gets prompts with follow-ups for deeper exploration.
     */
    fun getPromptsWithFollowUps(): List<Prompt> {
        return allPrompts.filter { it.followUp != null }
    }

    /**
     * Gets prompts within a time limit.
     */
    fun getPromptsWithinTimeLimit(maxMinutes: Int): List<Prompt> {
        return allPrompts.filter { it.estimatedMinutes <= maxMinutes }
    }

    /**
     * Searches prompts by keyword.
     */
    fun searchPrompts(keyword: String): List<Prompt> {
        val lowerKeyword = keyword.lowercase()
        return allPrompts.filter {
            it.text.lowercase().contains(lowerKeyword)
        }
    }

    /**
     * Gets the total count of prompts.
     */
    fun getTotalPromptCount(): Int = allPrompts.size

    /**
     * Gets count by category.
     */
    fun getCountByCategory(category: PromptCategory): Int {
        return getPromptsByCategory(category).size
    }

    /**
     * Gets a diverse set of prompts (one from each category).
     */
    fun getDiversePrompts(): List<Prompt> {
        return PromptCategory.values().mapNotNull { category ->
            getPromptsByCategory(category).randomOrNull()
        }
    }
}
