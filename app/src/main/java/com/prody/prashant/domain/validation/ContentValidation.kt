package com.prody.prashant.domain.validation

/**
 * Content Validation - Pre-save validation for journal entries with helpful guidance.
 *
 * This validation system provides real-time feedback to users as they write,
 * encouraging more meaningful entries without being intrusive.
 *
 * Validation levels:
 * - Valid: Ready to save, AI can generate meaningful insights
 * - MinimalContent: Saveable but AI insights will be limited
 * - TooShort: Needs more content for meaningful reflection
 * - TooVague: Content exists but lacks substance
 * - Empty: No content at all
 */
sealed class ContentValidation {
    /**
     * Content is valid and ready for saving.
     * AI will be able to generate meaningful insights.
     */
    data object Valid : ContentValidation()

    /**
     * Content is minimal but saveable.
     * AI insights may be limited but the entry can be saved.
     */
    data class MinimalContent(
        val suggestion: String,
        val wordCount: Int
    ) : ContentValidation()

    /**
     * Content is too short for meaningful reflection.
     * Provides a helpful suggestion to encourage more writing.
     */
    data class TooShort(
        val suggestion: String,
        val currentWords: Int,
        val minimumWords: Int
    ) : ContentValidation()

    /**
     * Content exists but appears too vague or lacks substance.
     * Provides a suggestion to add more detail.
     */
    data class TooVague(
        val suggestion: String,
        val detectedPatterns: List<String>
    ) : ContentValidation()

    /**
     * No content has been entered.
     */
    data object Empty : ContentValidation()

    /**
     * Whether the content can be saved (regardless of quality).
     */
    val canSave: Boolean
        get() = this is Valid || this is MinimalContent

    /**
     * Whether AI can generate meaningful insights for this content.
     */
    val aiCanGenerateInsights: Boolean
        get() = this is Valid

    /**
     * Get a user-friendly message for the validation state.
     */
    fun getMessage(): String? = when (this) {
        is Valid -> null
        is MinimalContent -> suggestion
        is TooShort -> suggestion
        is TooVague -> suggestion
        is Empty -> null
    }
}

/**
 * Content Validator - Validates journal entry content and provides guidance.
 */
object ContentValidator {

    // Minimum words for a "valid" entry (AI can provide good insights)
    private const val MINIMUM_WORDS_FOR_INSIGHTS = 20

    // Minimum words to save at all
    private const val MINIMUM_WORDS_TO_SAVE = 5

    // Patterns that indicate vague content
    private val vaguePatterns = listOf(
        "today was" to "What made today different? How did it affect you?",
        "i feel" to "Can you describe what triggered this feeling?",
        "nothing much" to "Even small moments matter. What's one thing that caught your attention?",
        "same as usual" to "What's one small detail that stood out today?",
        "idk" to "It's okay not to know. What's one thing you're curious about?",
        "whatever" to "Take a breath. What's actually on your mind right now?",
        "fine" to "Let's dig deeper. What would make things better than 'fine'?",
        "ok" to "What's behind that 'ok'? Is there more you want to explore?",
        "meh" to "What would shift 'meh' to something more interesting?",
        "bored" to "What kind of experience would feel meaningful right now?"
    )

    // Filler words that don't add substance
    private val fillerWords = setOf(
        "um", "uh", "like", "just", "really", "very", "so", "well",
        "basically", "literally", "actually", "honestly", "anyway"
    )

    // Encouraging prompts for different situations
    private val shortContentPrompts = listOf(
        "A few more sentences would help Buddha understand you better.",
        "What happened? How did it make you feel?",
        "Try adding some context—what led to this moment?",
        "Describe the scene. Where were you? What were you thinking?",
        "Write as if explaining this to a friend who wasn't there."
    )

    private val minimalContentPrompts = listOf(
        "This is a good start! Adding a bit more detail would unlock deeper insights.",
        "You're on the right track. What else comes to mind?",
        "Consider adding how this affects you going forward."
    )

    /**
     * Validate journal entry content.
     *
     * @param content The raw journal content
     * @return ContentValidation result with appropriate guidance
     */
    fun validate(content: String): ContentValidation {
        val trimmedContent = content.trim()

        // Empty check
        if (trimmedContent.isEmpty()) {
            return ContentValidation.Empty
        }

        // Word count analysis
        val words = trimmedContent.split(Regex("\\s+")).filter { it.isNotBlank() }
        val wordCount = words.size

        // Remove filler words for substantive word count
        val substantiveWords = words.filter { word ->
            word.lowercase() !in fillerWords && word.length > 1
        }
        val substantiveWordCount = substantiveWords.size

        // Check for vague patterns
        val lowerContent = trimmedContent.lowercase()
        val detectedVaguePatterns = vaguePatterns.filter { (pattern, _) ->
            lowerContent.contains(pattern)
        }

        // If content is mostly a vague phrase and very short
        if (detectedVaguePatterns.isNotEmpty() && wordCount < 10) {
            val suggestions = detectedVaguePatterns.map { it.second }
            return ContentValidation.TooVague(
                suggestion = suggestions.first(),
                detectedPatterns = detectedVaguePatterns.map { it.first }
            )
        }

        // Too short to save at all
        if (wordCount < MINIMUM_WORDS_TO_SAVE) {
            return ContentValidation.TooShort(
                suggestion = shortContentPrompts.random(),
                currentWords = wordCount,
                minimumWords = MINIMUM_WORDS_TO_SAVE
            )
        }

        // Can save but minimal (AI insights will be limited)
        if (substantiveWordCount < MINIMUM_WORDS_FOR_INSIGHTS) {
            return ContentValidation.MinimalContent(
                suggestion = minimalContentPrompts.random(),
                wordCount = wordCount
            )
        }

        // Valid - good to go!
        return ContentValidation.Valid
    }

    /**
     * Get a progress indicator (0.0 to 1.0) for how complete the entry is.
     * This can be used for visual feedback (progress bar, etc.)
     */
    fun getCompletionProgress(content: String): Float {
        val wordCount = content.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        return (wordCount.toFloat() / MINIMUM_WORDS_FOR_INSIGHTS).coerceIn(0f, 1f)
    }

    /**
     * Get the recommended minimum word count for full AI insights.
     */
    fun getRecommendedMinimumWords(): Int = MINIMUM_WORDS_FOR_INSIGHTS

    /**
     * Get the absolute minimum word count to save.
     */
    fun getAbsoluteMinimumWords(): Int = MINIMUM_WORDS_TO_SAVE
}
