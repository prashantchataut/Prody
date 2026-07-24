package com.kairos.app.ui.theme
import com.kairos.app.ui.icons.KairosIcons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kairos.app.domain.model.ChallengeDifficulty
import com.kairos.app.domain.model.ChallengeType

/**
 * UI extension properties for ChallengeDifficulty enum.
 *
 * These are separated from the ChallengeDifficulty domain model to avoid Compose dependencies
 * in the domain layer, preventing class initialization crashes when ChallengeDifficulty is
 * accessed from background threads or before Compose is initialized.
 */

/**
 * Returns the icon for this difficulty level.
 */
val ChallengeDifficulty.icon: ImageVector
    get() = when (this) {
        ChallengeDifficulty.EASY -> KairosIcons.SentimentSatisfied
        ChallengeDifficulty.MEDIUM -> KairosIcons.Psychology
        ChallengeDifficulty.HARD -> KairosIcons.Whatshot
        ChallengeDifficulty.EXTREME -> KairosIcons.Bolt
    }

/**
 * Returns the color for this difficulty level.
 */
val ChallengeDifficulty.color: Color
    get() = when (this) {
        ChallengeDifficulty.EASY -> MoodCalm
        ChallengeDifficulty.MEDIUM -> MoodMotivated
        ChallengeDifficulty.HARD -> MoodExcited
        ChallengeDifficulty.EXTREME -> Color(0xFFFF5722)
    }

/**
 * UI extension properties for ChallengeType enum.
 *
 * These are separated from the ChallengeType domain model to avoid Compose dependencies
 * in the domain layer, preventing class initialization crashes when ChallengeType is
 * accessed from background threads or before Compose is initialized.
 */

/**
 * Returns the icon for this challenge type.
 */
val ChallengeType.icon: ImageVector
    get() = when (this) {
        ChallengeType.JOURNALING -> KairosIcons.Book
        ChallengeType.VOCABULARY -> KairosIcons.School
        ChallengeType.STREAK -> KairosIcons.LocalFireDepartment
        ChallengeType.MEDITATION -> KairosIcons.SelfImprovement
        ChallengeType.REFLECTION -> KairosIcons.Psychology
        ChallengeType.MIXED -> KairosIcons.Dashboard
    }

/**
 * Returns the color for this challenge type.
 */
val ChallengeType.color: Color
    get() = when (this) {
        ChallengeType.JOURNALING -> MoodCalm
        ChallengeType.VOCABULARY -> MoodMotivated
        ChallengeType.STREAK -> StreakFire
        ChallengeType.MEDITATION -> KairosPrimary
        ChallengeType.REFLECTION -> KairosTertiary
        ChallengeType.MIXED -> GoldTier
    }
