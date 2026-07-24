package com.kairos.app.domain.futuremessage

/**
 * Deterministic prompts for revisiting a message from the past.
 *
 * A stable prompt prevents the screen from feeling arbitrarily generated and
 * makes tests, screenshots, and restoration predictable. Users can still cycle
 * through the full set deliberately.
 */
object FutureReplyPromptPolicy {
    private val reflective = listOf(
        "How does it feel reading this now?",
        "What has changed since you wrote this?",
        "What would you tell the person you were then?",
        "Did things unfold as you expected?",
        "What do you understand now that you did not then?"
    )

    private val growth = listOf(
        "Where can you see genuine growth?",
        "What did this season teach you?",
        "What are you proud you carried through?",
        "What still deserves your attention?"
    )

    private val connection = listOf(
        "What should your future self remember from this moment?",
        "Which part of this message still feels alive?",
        "What promise is worth carrying forward?"
    )

    private val all = reflective + growth + connection

    fun contextualPrompt(daysSinceWritten: Long): String {
        val safeDays = daysSinceWritten.coerceAtLeast(0L)
        val pool = when {
            safeDays < 30 -> reflective
            safeDays < 365 -> growth
            else -> connection
        }
        return pool[(safeDays % pool.size).toInt()]
    }

    fun nextPrompt(current: String): String {
        val currentIndex = all.indexOf(current)
        return all[(currentIndex + 1).mod(all.size)]
    }
}
