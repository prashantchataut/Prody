package com.prody.prashant.domain.model

/**
 * Domain model for mood states.
 *
 * Note: UI properties (icon, color) are provided separately via MoodUi extensions
 * in the ui.theme package to avoid Compose dependencies in the domain layer.
 * This prevents class initialization crashes when Mood is accessed from
 * background threads or before Compose is initialized.
 */
enum class Mood(
    val displayName: String,
    val emoji: String,
    val description: String,
    val buddhaPromptHint: String
) {
    HAPPY(
        displayName = "Happy",
        emoji = "😊",
        description = "Feeling joyful and content",
        buddhaPromptHint = "embrace this moment of joy"
    ),
    CALM(
        displayName = "Calm",
        emoji = "😌",
        description = "Peaceful and serene",
        buddhaPromptHint = "maintain this inner peace"
    ),
    ANXIOUS(
        displayName = "Anxious",
        emoji = "😰",
        description = "Feeling worried or nervous",
        buddhaPromptHint = "find stillness amidst the storm"
    ),
    SAD(
        displayName = "Sad",
        emoji = "😢",
        description = "Feeling down or melancholic",
        buddhaPromptHint = "remember that this too shall pass"
    ),
    MOTIVATED(
        displayName = "Motivated",
        emoji = "💪",
        description = "Ready to conquer the world",
        buddhaPromptHint = "channel this energy wisely"
    ),
    GRATEFUL(
        displayName = "Grateful",
        emoji = "🙏",
        description = "Appreciating life's blessings",
        buddhaPromptHint = "cultivate this gratitude daily"
    ),
    CONFUSED(
        displayName = "Confused",
        emoji = "😕",
        description = "Seeking clarity and direction",
        buddhaPromptHint = "find wisdom in uncertainty"
    ),
    EXCITED(
        displayName = "Excited",
        emoji = "🎉",
        description = "Enthusiastic and energized",
        buddhaPromptHint = "harness this excitement purposefully"
    ),
    NOSTALGIC(
        displayName = "Nostalgic",
        emoji = "🥹",
        description = "Reflecting on memories with bittersweet fondness",
        buddhaPromptHint = "honor the past while embracing the present"
    );

    companion object {
        fun fromString(value: String): Mood {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: CALM
        }

        fun all(): List<Mood> = entries.toList()
    }
}
