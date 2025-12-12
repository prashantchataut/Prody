package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.R
import com.prody.prashant.ui.theme.GoldTier
import com.prody.prashant.ui.theme.StreakBadgeShape
import com.prody.prashant.ui.theme.StreakFire
import com.prody.prashant.ui.theme.StreakGlow
import com.prody.prashant.ui.theme.StreakMonth
import com.prody.prashant.ui.theme.StreakQuarter
import com.prody.prashant.ui.theme.StreakWeek

/**
 * Prody Design System - Streak Badge Components
 *
 * Badges to display user streak information with celebratory animations.
 * Features:
 * - Tiered visual feedback based on streak length
 * - Pulsing glow animation for active streaks
 * - Full accessibility support
 * - Compact and expanded variants
 */

// =============================================================================
// STANDARD STREAK BADGE
// =============================================================================

/**
 * Animated streak badge showing the current streak count.
 *
 * @param streakDays Number of consecutive days
 * @param modifier Modifier for the badge
 * @param showAnimation Whether to show pulsing animation
 * @param size Badge size variant
 */
@Composable
fun StreakBadge(
    streakDays: Int,
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true,
    size: StreakBadgeSize = StreakBadgeSize.Medium
) {
    // Determine streak tier colors based on length
    val (primaryColor, secondaryColor) = getStreakColors(streakDays)

    // Animation setup
    val infiniteTransition = rememberInfiniteTransition(label = "streak_animation")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (showAnimation && streakDays > 0) 1.06f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "streak_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Accessibility description
    val description = when {
        streakDays == 0 -> "No active streak"
        streakDays == 1 -> "1 day streak"
        else -> "$streakDays day streak"
    }

    Box(
        modifier = modifier.semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        // Glow effect behind badge (only when active)
        if (streakDays > 0 && showAnimation) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scale(scale * 1.2f)
                    .blur(12.dp)
                    .alpha(glowAlpha * 0.5f)
                    .clip(StreakBadgeShape)
                    .background(primaryColor)
            )
        }

        // Main badge
        Row(
            modifier = Modifier
                .scale(scale)
                .clip(StreakBadgeShape)
                .background(
                    Brush.horizontalGradient(
                        colors = if (streakDays > 0) {
                            listOf(
                                primaryColor.copy(alpha = glowAlpha + 0.2f),
                                secondaryColor.copy(alpha = glowAlpha)
                            )
                        } else {
                            listOf(
                                Color.Gray.copy(alpha = 0.25f),
                                Color.Gray.copy(alpha = 0.18f)
                            )
                        }
                    )
                )
                .padding(
                    horizontal = size.horizontalPadding,
                    vertical = size.verticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(size.iconSpacing)
        ) {
            Icon(
                imageVector = if (streakDays >= 30) Icons.Filled.Whatshot else Icons.Filled.LocalFireDepartment,
                contentDescription = null, // Description handled by parent
                tint = if (streakDays > 0) primaryColor else Color.Gray,
                modifier = Modifier.size(size.iconSize)
            )
            Text(
                text = streakDays.toString(),
                style = when (size) {
                    StreakBadgeSize.Small -> MaterialTheme.typography.labelMedium
                    StreakBadgeSize.Medium -> MaterialTheme.typography.titleMedium
                    StreakBadgeSize.Large -> MaterialTheme.typography.titleLarge
                },
                fontWeight = FontWeight.Bold,
                color = if (streakDays > 0) Color.White else Color.Gray.copy(alpha = 0.8f)
            )
        }
    }
}

// =============================================================================
// STREAK BADGE WITH LABEL
// =============================================================================

/**
 * Streak badge with accompanying label text.
 */
@Composable
fun StreakBadgeWithLabel(
    streakDays: Int,
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true,
    label: String = "Day Streak"
) {
    val description = when {
        streakDays == 0 -> "No active streak"
        streakDays == 1 -> "1 $label"
        else -> "$streakDays $label"
    }

    Column(
        modifier = modifier.semantics { contentDescription = description },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        StreakBadge(
            streakDays = streakDays,
            showAnimation = showAnimation,
            size = StreakBadgeSize.Large
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =============================================================================
// COMPACT STREAK INDICATOR
// =============================================================================

/**
 * Minimal streak indicator for tight spaces.
 */
@Composable
fun CompactStreakIndicator(
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val (primaryColor, _) = getStreakColors(streakDays)

    val description = when {
        streakDays == 0 -> "No streak"
        else -> "$streakDays days"
    }

    Row(
        modifier = modifier.semantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = if (streakDays > 0) primaryColor else Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = streakDays.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (streakDays > 0) primaryColor else Color.Gray.copy(alpha = 0.7f)
        )
    }
}

// =============================================================================
// HELPER FUNCTIONS & ENUMS
// =============================================================================

/**
 * Badge size variants.
 */
enum class StreakBadgeSize(
    val iconSize: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val iconSpacing: Dp
) {
    Small(iconSize = 16.dp, horizontalPadding = 8.dp, verticalPadding = 4.dp, iconSpacing = 4.dp),
    Medium(iconSize = 20.dp, horizontalPadding = 12.dp, verticalPadding = 8.dp, iconSpacing = 6.dp),
    Large(iconSize = 24.dp, horizontalPadding = 16.dp, verticalPadding = 10.dp, iconSpacing = 8.dp)
}

/**
 * Get streak colors based on streak length (tiered system).
 *
 * @param streakDays Number of consecutive days
 * @return Pair of (primary color, secondary color for gradient)
 */
private fun getStreakColors(streakDays: Int): Pair<Color, Color> {
    return when {
        streakDays >= 90 -> GoldTier to StreakGlow          // 90+ days: Gold (legendary)
        streakDays >= 30 -> StreakQuarter to StreakFire     // 30+ days: Deep red (monthly)
        streakDays >= 7 -> StreakMonth to StreakWeek        // 7+ days: Orange (weekly)
        streakDays > 0 -> StreakFire to StreakGlow          // 1-6 days: Standard fire
        else -> Color.Gray to Color.Gray.copy(alpha = 0.5f) // No streak
    }
}
