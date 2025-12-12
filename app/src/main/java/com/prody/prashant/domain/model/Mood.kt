package com.prody.prashant.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.prody.prashant.ui.theme.*

enum class Mood(
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
    val description: String,
    val buddhaPromptHint: String
) {
    HAPPY(
        displayName = "Happy",
        icon = Icons.Filled.SentimentVerySatisfied,
        color = MoodHappy,
        description = "Feeling joyful and content",
        buddhaPromptHint = "embrace this moment of joy"
    ),
    CALM(
        displayName = "Calm",
        icon = Icons.Filled.SelfImprovement,
        color = MoodCalm,
        description = "Peaceful and serene",
        buddhaPromptHint = "maintain this inner peace"
    ),
    ANXIOUS(
        displayName = "Anxious",
        icon = Icons.Filled.Psychology,
        color = MoodAnxious,
        description = "Feeling worried or nervous",
        buddhaPromptHint = "find stillness amidst the storm"
    ),
    SAD(
        displayName = "Sad",
        icon = Icons.Filled.SentimentDissatisfied,
        color = MoodSad,
        description = "Feeling down or melancholic",
        buddhaPromptHint = "remember that this too shall pass"
    ),
    MOTIVATED(
        displayName = "Motivated",
        icon = Icons.Filled.LocalFireDepartment,
        color = MoodMotivated,
        description = "Ready to conquer the world",
        buddhaPromptHint = "channel this energy wisely"
    ),
    GRATEFUL(
        displayName = "Grateful",
        icon = Icons.Filled.Favorite,
        color = MoodGrateful,
        description = "Appreciating life's blessings",
        buddhaPromptHint = "cultivate this gratitude daily"
    ),
    CONFUSED(
        displayName = "Confused",
        icon = Icons.Outlined.HelpOutline,
        color = MoodConfused,
        description = "Seeking clarity and direction",
        buddhaPromptHint = "find wisdom in uncertainty"
    ),
    EXCITED(
        displayName = "Excited",
        icon = Icons.Filled.Celebration,
        color = MoodExcited,
        description = "Enthusiastic and energized",
        buddhaPromptHint = "harness this excitement purposefully"
    );

    companion object {
        fun fromString(value: String): Mood {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: CALM
        }

        fun all(): List<Mood> = entries.toList()
    }
}
