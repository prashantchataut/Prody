package com.prody.prashant.ui.theme
import com.prody.prashant.ui.icons.ProdyIcons

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
import com.prody.prashant.domain.model.ChallengeDifficulty
import com.prody.prashant.domain.model.ChallengeType

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
        ChallengeDifficulty.EASY -> ProdyIcons.SentimentSatisfied
        ChallengeDifficulty.MEDIUM -> ProdyIcons.Psychology
        ChallengeDifficulty.HARD -> ProdyIcons.Whatshot
        ChallengeDifficulty.EXTREME -> ProdyIcons.Bolt
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
        ChallengeType.JOURNALING -> ProdyIcons.Book
        ChallengeType.VOCABULARY -> ProdyIcons.School
        ChallengeType.STREAK -> ProdyIcons.LocalFireDepartment
        ChallengeType.MEDITATION -> ProdyIcons.SelfImprovement
        ChallengeType.REFLECTION -> ProdyIcons.Psychology
        ChallengeType.MIXED -> ProdyIcons.Dashboard
    }

/**
 * Returns the color for this challenge type.
 */
val ChallengeType.color: Color
    get() = when (this) {
        ChallengeType.JOURNALING -> MoodCalm
        ChallengeType.VOCABULARY -> MoodMotivated
        ChallengeType.STREAK -> StreakFire
        ChallengeType.MEDITATION -> ProdyPrimary
        ChallengeType.REFLECTION -> ProdyTertiary
        ChallengeType.MIXED -> GoldTier
    }
