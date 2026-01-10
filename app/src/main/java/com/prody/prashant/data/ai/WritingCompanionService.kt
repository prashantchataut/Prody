package com.prody.prashant.data.ai

import com.prody.prashant.domain.model.Mood
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI Writing Companion Service
 *
 * Provides intelligent, contextual writing prompts to help users
 * when they're stuck or need inspiration while journaling.
 *
 * Key principles:
 * - Never intrusive, always dismissable
 * - Suggestions feel human, not robotic
 * - No generic "What are you grateful for?" nonsense
 * - Context-aware based on time, mood, and recent patterns
 */
@Singleton
class WritingCompanionService @Inject constructor() {

    /**
     * Get a contextual starting prompt when journal is empty
     */
    fun getStartingPrompt(
        mood: Mood? = null,
        recentThemes: List<String> = emptyList(),
        timeOfDay: TimeOfDay = getCurrentTimeOfDay()
    ): String {
        // First priority: mood-specific prompts
        mood?.let {
            getMoodSpecificPrompt(it)?.let { prompt -> return prompt }
        }

        // Second priority: recent theme continuation
        if (recentThemes.isNotEmpty()) {
            getThemeContinuationPrompt(recentThemes.first())?.let { prompt -> return prompt }
        }

        // Default: time-of-day prompts
        return getTimeBasedPrompt(timeOfDay)
    }

    /**
     * Get a continuation suggestion when user pauses after writing 1-2 sentences
     */
    fun getContinuationSuggestion(
        currentContent: String,
        mood: Mood? = null
    ): String? {
        val wordCount = currentContent.split("\\s+".toRegex()).filter { it.isNotBlank() }.size

        // Only suggest if content is short (1-3 sentences, roughly 10-50 words)
        if (wordCount < 5 || wordCount > 60) return null

        val lowerContent = currentContent.lowercase()

        // Analyze content for contextual continuation
        return when {
            containsAny(lowerContent, listOf("happened", "today", "yesterday", "work", "meeting")) ->
                continuationPrompts["event"]?.random()

            containsAny(lowerContent, listOf("feel", "feeling", "felt", "emotion")) ->
                continuationPrompts["emotion"]?.random()

            containsAny(lowerContent, listOf("think", "thought", "wonder", "maybe")) ->
                continuationPrompts["thought"]?.random()

            containsAny(lowerContent, listOf("want", "need", "wish", "hope")) ->
                continuationPrompts["desire"]?.random()

            containsAny(lowerContent, listOf("someone", "they", "she", "he", "friend", "family")) ->
                continuationPrompts["person"]?.random()

            containsAny(lowerContent, listOf("afraid", "scared", "worry", "anxious", "nervous")) ->
                continuationPrompts["fear"]?.random()

            containsAny(lowerContent, listOf("angry", "frustrated", "annoyed", "upset")) ->
                continuationPrompts["anger"]?.random()

            containsAny(lowerContent, listOf("happy", "excited", "glad", "grateful")) ->
                continuationPrompts["joy"]?.random()

            else -> genericContinuations.random()
        }
    }

    /**
     * Get a "stuck help" prompt when user has been idle for 10+ seconds
     */
    fun getStuckHelpPrompt(
        currentContent: String,
        mood: Mood? = null,
        recentThemes: List<String> = emptyList()
    ): String {
        val isEmpty = currentContent.isBlank()
        val isShort = currentContent.split("\\s+".toRegex()).filter { it.isNotBlank() }.size < 20

        return when {
            isEmpty -> getStartingPrompt(mood, recentThemes)
            isShort -> getContinuationSuggestion(currentContent, mood) ?: "What else comes to mind?"
            else -> deepeningPrompts.random()
        }
    }

    // ==================== PRIVATE METHODS ====================

    private fun getMoodSpecificPrompt(mood: Mood): String? {
        return moodPrompts[mood]?.random()
    }

    private fun getThemeContinuationPrompt(theme: String): String? {
        val lowerTheme = theme.lowercase()
        return when {
            containsAny(lowerTheme, listOf("work", "job", "career", "project")) ->
                "How's that situation at work evolving?"

            containsAny(lowerTheme, listOf("relationship", "partner", "friend", "family")) ->
                "Any new thoughts about that relationship?"

            containsAny(lowerTheme, listOf("health", "exercise", "sleep", "body")) ->
                "How have you been feeling physically lately?"

            containsAny(lowerTheme, listOf("stress", "anxiety", "overwhelm")) ->
                "Has the pressure eased at all since last time?"

            containsAny(lowerTheme, listOf("goal", "plan", "future", "dream")) ->
                "Any progress on what you've been working toward?"

            else -> null
        }
    }

    private fun getTimeBasedPrompt(timeOfDay: TimeOfDay): String {
        return when (timeOfDay) {
            TimeOfDay.MORNING -> morningPrompts.random()
            TimeOfDay.AFTERNOON -> afternoonPrompts.random()
            TimeOfDay.EVENING -> eveningPrompts.random()
            TimeOfDay.NIGHT -> nightPrompts.random()
        }
    }

    private fun getCurrentTimeOfDay(): TimeOfDay {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 6 -> TimeOfDay.NIGHT
            hour < 12 -> TimeOfDay.MORNING
            hour < 17 -> TimeOfDay.AFTERNOON
            hour < 21 -> TimeOfDay.EVENING
            else -> TimeOfDay.NIGHT
        }
    }

    private fun containsAny(text: String, keywords: List<String>): Boolean {
        return keywords.any { text.contains(it) }
    }

    // ==================== PROMPT COLLECTIONS ====================

    private val morningPrompts = listOf(
        "What's on your mind as you start today?",
        "How did you sleep? What dreams are lingering?",
        "What's the one thing you want to accomplish today?",
        "How are you feeling as the day begins?",
        "What's already happened this morning that's worth noting?"
    )

    private val afternoonPrompts = listOf(
        "How's your day going so far?",
        "What's been on your mind today?",
        "Anything unexpected happen?",
        "What are you looking forward to for the rest of the day?",
        "How's your energy level right now?"
    )

    private val eveningPrompts = listOf(
        "What happened today that stuck with you?",
        "How are you feeling as the day winds down?",
        "What would you do differently if you could redo today?",
        "What's one thing from today you want to remember?",
        "What are you carrying into tomorrow?"
    )

    private val nightPrompts = listOf(
        "What's keeping you up?",
        "What thoughts are running through your mind right now?",
        "What do you need to let go of before sleep?",
        "What's weighing on you tonight?",
        "What would make tomorrow better than today?"
    )

    private val moodPrompts = mapOf(
        Mood.ANXIOUS to listOf(
            "What's the one thing weighing on you most right now?",
            "What would help you feel more at ease?",
            "What's the worst that could actually happen? And then what?",
            "What do you need right now?",
            "What's within your control here?"
        ),
        Mood.SAD to listOf(
            "What's making your heart heavy today?",
            "When did this feeling start?",
            "What would bring you a little comfort right now?",
            "What do you wish someone understood about how you're feeling?",
            "What's the kindest thing you could do for yourself today?"
        ),
        Mood.HAPPY to listOf(
            "What's making you smile today?",
            "What contributed to this good feeling?",
            "Who would you want to share this moment with?",
            "What do you want to remember about today?",
            "How can you carry this feeling forward?"
        ),
        Mood.MOTIVATED to listOf(
            "What's driving this energy today?",
            "What do you want to tackle first?",
            "What's the biggest thing you could accomplish right now?",
            "What would make this motivation last?",
            "What's been holding you back that you're ready to address?"
        ),
        Mood.CONFUSED to listOf(
            "What decision is weighing on you?",
            "What information would help clarify things?",
            "What does your gut tell you?",
            "What would you advise a friend in this situation?",
            "What's the simplest next step you could take?"
        ),
        Mood.GRATEFUL to listOf(
            "What sparked this gratitude?",
            "Who deserves thanks today?",
            "What's something small you're appreciating?",
            "How has this gratitude changed your perspective?",
            "What do you want to create more of in your life?"
        ),
        Mood.CALM to listOf(
            "What's contributing to this peaceful feeling?",
            "What insights come to you in this calm state?",
            "What do you see clearly right now?",
            "What would you like to reflect on while you feel this centered?",
            "What truth is emerging?"
        ),
        Mood.EXCITED to listOf(
            "What's got you so energized?",
            "What are you looking forward to most?",
            "How do you want to channel this excitement?",
            "What possibilities are opening up?",
            "What needs to happen to make this a reality?"
        )
    )

    private val continuationPrompts = mapOf(
        "event" to listOf(
            "What happened next?",
            "How did that make you feel?",
            "What surprised you about it?",
            "What did you learn from that?"
        ),
        "emotion" to listOf(
            "Where do you feel that in your body?",
            "What triggered that feeling?",
            "What do you need right now?",
            "How long have you been feeling this way?"
        ),
        "thought" to listOf(
            "What makes you think that?",
            "What's the other side of that?",
            "Where does that thought lead?",
            "Is that thought serving you?"
        ),
        "desire" to listOf(
            "What's stopping you?",
            "What would it take to get there?",
            "How important is this to you?",
            "What's the first step?"
        ),
        "person" to listOf(
            "How do they make you feel?",
            "What do you wish they knew?",
            "What's the history there?",
            "What do you need from them?"
        ),
        "fear" to listOf(
            "What's the worst that could happen?",
            "What would you do if that happened?",
            "Is this fear protecting you or holding you back?",
            "What would you do if you weren't afraid?"
        ),
        "anger" to listOf(
            "What boundary was crossed?",
            "What do you need to let go of?",
            "What would resolution look like?",
            "What's underneath the anger?"
        ),
        "joy" to listOf(
            "What made this happen?",
            "How can you create more of this?",
            "Who would you share this with?",
            "What does this tell you about what matters?"
        )
    )

    private val genericContinuations = listOf(
        "What else comes to mind?",
        "And then?",
        "What's underneath that?",
        "How does that feel?",
        "What would change if you acted on that?"
    )

    private val deepeningPrompts = listOf(
        "What's the deeper truth here?",
        "What would your wisest self say about this?",
        "What pattern do you notice?",
        "What would you tell a friend in this situation?",
        "What's the real question you're grappling with?",
        "If you could only share one thing from this entry, what would it be?",
        "What's the one thing you're avoiding saying?",
        "What would change if you fully accepted this situation?"
    )

    enum class TimeOfDay {
        MORNING, AFTERNOON, EVENING, NIGHT
    }
}
