package com.prody.prashant.data.content

import java.util.Calendar

/**
 * Prody Journal Prompt Library
 *
 * A comprehensive collection of journal prompts organized by category.
 * Designed to inspire reflection, self-discovery, and personal growth.
 *
 * Features:
 * - 80+ carefully crafted prompts across 8 categories
 * - Time-of-day appropriate prompt selection
 * - Quick prompts for time-constrained journaling
 * - Creative prompts for deeper exploration
 * - Category-based filtering
 */
object JournalPrompts {

    // =============================================================================
    // MORNING PROMPTS - For starting the day with intention
    // =============================================================================

    val morningPrompts = listOf(
        "What's one thing you want to accomplish today?",
        "How are you feeling this morning? Why?",
        "What would make today great?",
        "What's your intention for today?",
        "If today was your best day ever, what would happen?",
        "What's one thing you're looking forward to?",
        "How can you show up as your best self today?",
        "What challenges might you face today? How will you handle them?",
        "What are you grateful for as you start this day?",
        "What energy do you want to bring into today?",
        "If you could accomplish just one thing today, what would it be?",
        "What's something small you can do today that your future self will thank you for?"
    )

    // =============================================================================
    // EVENING PROMPTS - For reflecting on the day
    // =============================================================================

    val eveningPrompts = listOf(
        "What went well today? What could have gone better?",
        "What are you grateful for from today?",
        "What did you learn today?",
        "How did you grow today?",
        "What moment made you smile today?",
        "What drained your energy today? What energized you?",
        "If you could relive one moment from today, which would it be?",
        "What would you do differently if you could redo today?",
        "What's one thing you did today that you're proud of?",
        "How did you handle today's challenges?",
        "What was unexpected about today?",
        "What will you remember about today?"
    )

    // =============================================================================
    // GRATITUDE PROMPTS - For cultivating appreciation
    // =============================================================================

    val gratitudePrompts = listOf(
        "Name 3 things you're grateful for right now.",
        "Who made a positive impact on your life recently?",
        "What's something you usually take for granted?",
        "What's a simple pleasure that brought you joy lately?",
        "What ability or skill are you thankful to have?",
        "What's something in nature you appreciate?",
        "Who has helped you become who you are?",
        "What's a memory you're grateful to have?",
        "What modern convenience are you thankful for today?",
        "What challenge taught you something valuable?",
        "Who in your life makes you feel loved and supported?",
        "What part of your body are you grateful for and why?"
    )

    // =============================================================================
    // REFLECTION PROMPTS - For deeper self-understanding
    // =============================================================================

    val reflectionPrompts = listOf(
        "What's been on your mind lately?",
        "What patterns have you noticed in your life recently?",
        "What's one thing you'd tell your younger self?",
        "What does your ideal life look like?",
        "What fears are holding you back?",
        "What would you do if you knew you couldn't fail?",
        "What beliefs about yourself might not be true?",
        "What's something you need to let go of?",
        "When do you feel most like yourself?",
        "What's a decision you've been avoiding?",
        "What does success mean to you?",
        "What would your life look like with less fear?"
    )

    // =============================================================================
    // GROWTH PROMPTS - For personal development
    // =============================================================================

    val growthPrompts = listOf(
        "What's one area of your life you want to improve?",
        "What's a mistake you learned from recently?",
        "What's outside your comfort zone that you want to try?",
        "What habit would most improve your life?",
        "What skill do you want to develop?",
        "What's holding you back from your goals?",
        "What's one small step you can take toward your dreams?",
        "How have you grown in the past year?",
        "What's a limiting belief you want to overcome?",
        "What would you attempt if you were 10x bolder?",
        "Who inspires you and what can you learn from them?",
        "What does your best self look like?"
    )

    // =============================================================================
    // EMOTIONAL PROCESSING - For understanding and processing feelings
    // =============================================================================

    val emotionalPrompts = listOf(
        "How are you really feeling right now? Don't filter it.",
        "What emotion has been most present for you lately?",
        "What's something you haven't allowed yourself to feel?",
        "What would you say to a friend feeling what you're feeling?",
        "What does your stress feel like? Where do you feel it?",
        "What brings you peace?",
        "What makes you feel alive?",
        "What's weighing on your heart today?",
        "When did you last cry, and what triggered it?",
        "What would emotional freedom look like for you?",
        "What emotion do you tend to avoid? Why?",
        "How do you typically cope with difficult emotions?"
    )

    // =============================================================================
    // QUICK PROMPTS - For time-limited journaling
    // =============================================================================

    val quickPrompts = listOf(
        "One word to describe today:",
        "Today's highlight:",
        "I'm feeling...",
        "Tomorrow I want to...",
        "Right now I need...",
        "One thing I appreciate:",
        "A thought I want to capture:",
        "Today I'm proud of:",
        "Something I learned:",
        "My energy level is:",
        "One word for my mood:",
        "What I'm looking forward to:"
    )

    // =============================================================================
    // CREATIVE PROMPTS - For imaginative exploration
    // =============================================================================

    val creativePrompts = listOf(
        "If your life was a book, what would this chapter be called?",
        "Write a letter to your future self (1 year from now).",
        "If you could have dinner with anyone, who and why?",
        "Describe your perfect day from start to finish.",
        "What would your 80-year-old self think about your current worries?",
        "If money wasn't a factor, what would you do?",
        "What's a memory that always makes you smile?",
        "If you could master any skill instantly, what would it be?",
        "Write a thank you letter to a body part that serves you well.",
        "If your emotions were weather, what's the forecast today?",
        "Describe a place where you feel completely at peace.",
        "What advice would you give to someone going through what you're going through?",
        "If your life had a soundtrack, what song would be playing right now?",
        "Write a letter to your past self from 5 years ago."
    )

    // =============================================================================
    // RELATIONSHIP PROMPTS - For exploring connections
    // =============================================================================

    val relationshipPrompts = listOf(
        "Who do you need to forgive? (Including yourself)",
        "What relationship needs more attention?",
        "How have you shown love to someone recently?",
        "What's a conversation you've been avoiding?",
        "Who makes you feel seen and heard?",
        "What do you value most in your closest friendships?",
        "How can you be a better friend/partner/family member?",
        "What boundary do you need to set?",
        "Who do you need to thank but haven't?",
        "What's something you wish someone knew about you?"
    )

    // =============================================================================
    // ALL PROMPTS COMBINED
    // =============================================================================

    val allPrompts: List<String> by lazy {
        morningPrompts + eveningPrompts + gratitudePrompts + reflectionPrompts +
                growthPrompts + emotionalPrompts + quickPrompts + creativePrompts +
                relationshipPrompts
    }

    // =============================================================================
    // HELPER FUNCTIONS
    // =============================================================================

    /**
     * Get prompts appropriate for the current time of day.
     *
     * @param hour The current hour (0-23)
     * @return A list of time-appropriate prompts
     */
    fun getPromptsForTimeOfDay(
        hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): List<String> {
        return when {
            hour in 5..11 -> morningPrompts
            hour in 18..23 || hour in 0..4 -> eveningPrompts
            else -> reflectionPrompts
        }
    }

    /**
     * Gets a single prompt for time of day.
     */
    fun getPromptForTimeOfDay(hour: Int): String {
        return getPromptsForTimeOfDay(hour).random()
    }

    /**
     * Get a random prompt from all available prompts.
     */
    fun getRandomPrompt(): String = allPrompts.random()

    /**
     * Get multiple random prompts.
     */
    fun getRandomPrompts(count: Int = 3): List<String> {
        return allPrompts.shuffled().take(count.coerceAtMost(allPrompts.size))
    }

    /**
     * Get prompts by category.
     */
    fun getPromptsByCategory(category: PromptCategory): List<String> {
        return when (category) {
            PromptCategory.MORNING -> morningPrompts
            PromptCategory.EVENING -> eveningPrompts
            PromptCategory.GRATITUDE -> gratitudePrompts
            PromptCategory.REFLECTION -> reflectionPrompts
            PromptCategory.GROWTH -> growthPrompts
            PromptCategory.EMOTIONAL -> emotionalPrompts
            PromptCategory.QUICK -> quickPrompts
            PromptCategory.CREATIVE -> creativePrompts
            PromptCategory.RELATIONSHIP -> relationshipPrompts
        }
    }

    /**
     * Get a single random prompt from a specific category.
     */
    fun getPromptByCategory(category: PromptCategory): String {
        return getPromptsByCategory(category).random()
    }

    /**
     * Gets quick prompts for when time is limited.
     */
    fun getQuickPrompt(): String {
        return quickPrompts.random()
    }

    /**
     * Gets prompts appropriate for user's mood.
     */
    fun getPromptForMood(mood: String): String {
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
    fun getMilestonePrompt(streakDays: Int): String {
        // Fallback since milestonePrompts was not in the original list defs shown but referenced in errors
        // We will return a generic growth prompt if specific milestone prompts are missing
        return "You have reached a $streakDays day streak! What kept you going?" 
    }

    /**
     * Get prompts based on the user's mood (returning list).
     */
    fun getPromptsForMood(moodName: String): List<String> {
        return when (moodName.lowercase()) {
            "happy", "excited", "grateful" -> gratitudePrompts
            "sad", "down", "melancholy" -> emotionalPrompts + creativePrompts.take(3)
            "anxious", "worried", "stressed" -> quickPrompts + reflectionPrompts.take(3)
            "motivated", "energetic", "inspired" -> growthPrompts
            "confused", "lost", "uncertain" -> reflectionPrompts + creativePrompts.take(3)
            "calm", "peaceful", "serene" -> gratitudePrompts + reflectionPrompts.take(3)
            "frustrated", "angry", "upset" -> emotionalPrompts
            else -> getRandomPrompts(5)
        }
    }

    /**
     * Get multiple prompts across diverse categories.
     */
    fun getDiversePrompts(count: Int = 3): List<String> {
        val categories = PromptCategory.values().toList().shuffled().take(count)
        return categories.map { getPromptByCategory(it) }
    }

    /**
     * Categories for journal prompts.
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
        RELATIONSHIP
    }
}
