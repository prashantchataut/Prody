package com.kairos.app.ui.theme
import com.kairos.app.ui.icons.KairosIcons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kairos.app.domain.model.Mood

/**
 * UI extension properties for Mood enum.
 *
 * These are separated from the Mood domain model to avoid Compose dependencies
 * in the domain layer, preventing class initialization crashes when Mood is
 * accessed from background threads or before Compose is initialized.
 */

/**
 * Returns the icon for this mood.
 */
val Mood.icon: ImageVector
    get() = when (this) {
        Mood.HAPPY -> KairosIcons.SentimentVerySatisfied
        Mood.CALM -> KairosIcons.SelfImprovement
        Mood.ANXIOUS -> KairosIcons.Psychology
        Mood.SAD -> KairosIcons.SentimentDissatisfied
        Mood.MOTIVATED -> KairosIcons.LocalFireDepartment
        Mood.GRATEFUL -> KairosIcons.Favorite
        Mood.CONFUSED -> KairosIcons.HelpOutline
        Mood.EXCITED -> KairosIcons.Celebration
        Mood.NOSTALGIC -> KairosIcons.HistoryEdu
    }

/**
 * Returns the color for this mood.
 */
val Mood.color: Color
    get() = when (this) {
        Mood.HAPPY -> MoodHappy
        Mood.CALM -> MoodCalm
        Mood.ANXIOUS -> MoodAnxious
        Mood.SAD -> MoodSad
        Mood.MOTIVATED -> MoodMotivated
        Mood.GRATEFUL -> MoodGrateful
        Mood.CONFUSED -> MoodConfused
        Mood.EXCITED -> MoodExcited
        Mood.NOSTALGIC -> MoodNostalgic
    }
