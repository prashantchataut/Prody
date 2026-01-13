package com.prody.prashant.ui.components
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
// blur import removed - flat design with no blur effects
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.theme.GoldTier
import com.prody.prashant.ui.theme.ProdyTertiary
import com.prody.prashant.ui.theme.StreakEmber
import com.prody.prashant.ui.theme.StreakFire
import com.prody.prashant.ui.theme.StreakGlow

/**
 * Prody Design System - Premium Streak Display Components
 *
 * Animated streak displays with dynamic fire effects that celebrate
 * user consistency and achievement. Features premium animations
 * that feel alive without being overwhelming.
 *
 * Design Philosophy:
 * - Fire animations should feel organic, not mechanical
 * - Glow effects add warmth without being distracting
 * - Color tiers recognize milestones meaningfully
 */

// =============================================================================
// MAIN STREAK DISPLAY
// =============================================================================

/**
 * Premium animated streak display with fire effect.
 *
 * Features:
 * - Pulsing fire icon with natural movement
 * - Ambient glow effect for active streaks
 * - Color tier system based on streak length
 * - Full accessibility support
 *
 * @param currentStreak Number of consecutive days
 * @param modifier Modifier for the component
 * @param showLongestStreak Whether to display longest streak below
 * @param longestStreak Highest streak achieved
 * @param size Display size variant
 */
@Composable
fun StreakDisplay(
    currentStreak: Int,
    modifier: Modifier = Modifier,
    showLongestStreak: Boolean = false,
    longestStreak: Int = 0,
    size: StreakDisplaySize = StreakDisplaySize.Medium
) {
    val isActive = currentStreak > 0

    val infiniteTransition = rememberInfiniteTransition(label = "streak_animation")

    // Fire pulsing scale animation
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_scale"
    )

    // Ambient glow pulsing
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isActive) 0.7f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Flame flickering offset
    val flameOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isActive) 3f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_offset"
    )

    val dimensions = when (size) {
        StreakDisplaySize.Small -> StreakDimensions(
            glowSize = 48.dp,
            iconSize = 20.dp,
            countSize = 12.sp,
            labelSize = 10.sp
        )
        StreakDisplaySize.Medium -> StreakDimensions(
            glowSize = 70.dp,
            iconSize = 28.dp,
            countSize = 18.sp,
            labelSize = 12.sp
        )
        StreakDisplaySize.Large -> StreakDimensions(
            glowSize = 100.dp,
            iconSize = 40.dp,
            countSize = 24.sp,
            labelSize = 14.sp
        )
    }

    // Determine colors based on streak tier
    val (primaryColor, secondaryColor) = getStreakTierColors(currentStreak)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.semantics {
            contentDescription = if (isActive) {
                "Current streak: $currentStreak days"
            } else {
                "No active streak"
            }
        }
    ) {
        // Flat design - subtle alpha pulse instead of blur glow
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(dimensions.glowSize * 0.8f)
                    .scale(fireScale)
                    .alpha(glowAlpha * 0.2f)
                    .background(primaryColor, CircleShape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fire icon and count container
            Row(
                modifier = Modifier
                    .background(
                        brush = if (isActive) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.6f),
                                    secondaryColor.copy(alpha = 0.4f)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Gray.copy(alpha = 0.3f),
                                    Color.Gray.copy(alpha = 0.2f)
                                )
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.LocalFireDepartment,
                    contentDescription = null,
                    tint = if (isActive) primaryColor else Color.Gray,
                    modifier = Modifier
                        .size(dimensions.iconSize)
                        .scale(if (isActive) fireScale else 1f)
                        .offset(y = (-flameOffset).dp)
                )

                Text(
                    text = currentStreak.toString(),
                    fontSize = dimensions.countSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (currentStreak == 1) "Day" else "Days",
                fontSize = dimensions.labelSize,
                color = Color.White.copy(alpha = 0.8f)
            )

            if (showLongestStreak && longestStreak > currentStreak) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Best: $longestStreak",
                    fontSize = 10.sp,
                    color = ProdyTertiary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// =============================================================================
// COMPACT STREAK INDICATOR
// =============================================================================

/**
 * Compact streak indicator for tight spaces like headers or list items.
 *
 * @param streakDays Number of consecutive days
 * @param modifier Modifier for the component
 * @param showAnimation Whether to animate the fire icon
 */
@Composable
fun CompactStreakIndicator(
    streakDays: Int,
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true
) {
    val isActive = streakDays > 0

    val infiniteTransition = rememberInfiniteTransition(label = "compact_streak")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive && showAnimation) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "compact_fire_scale"
    )

    val (primaryColor, _) = getStreakTierColors(streakDays)

    Row(
        modifier = modifier
            .background(
                color = if (isActive) primaryColor.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .semantics {
                contentDescription = if (isActive) "$streakDays day streak" else "No active streak"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = ProdyIcons.LocalFireDepartment,
            contentDescription = null,
            tint = if (isActive) primaryColor else Color.Gray,
            modifier = Modifier
                .size(16.dp)
                .scale(scale)
        )

        Text(
            text = streakDays.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) primaryColor else Color.Gray
        )
    }
}

// =============================================================================
// INLINE STREAK COUNT
// =============================================================================

/**
 * Minimal inline streak display for text contexts.
 *
 * @param streakDays Number of consecutive days
 * @param modifier Modifier for the component
 */
@Composable
fun InlineStreakCount(
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val isActive = streakDays > 0
    val (primaryColor, _) = getStreakTierColors(streakDays)

    Row(
        modifier = modifier.semantics {
            contentDescription = if (isActive) "$streakDays day streak" else "No streak"
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = ProdyIcons.LocalFireDepartment,
            contentDescription = null,
            tint = if (isActive) primaryColor else Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
        )

        Text(
            text = streakDays.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) primaryColor else Color.Gray.copy(alpha = 0.7f)
        )
    }
}

// =============================================================================
// STREAK MILESTONE CELEBRATION
// =============================================================================

/**
 * Celebration display for streak milestones.
 *
 * @param streakDays Number of consecutive days
 * @param milestoneName Name of the milestone achieved (e.g., "Weekly Warrior")
 * @param modifier Modifier for the component
 */
@Composable
fun StreakMilestoneCelebration(
    streakDays: Int,
    milestoneName: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "milestone_celebration")

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celebration_glow_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celebration_glow_alpha"
    )

    val (primaryColor, secondaryColor) = getStreakTierColors(streakDays)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.semantics {
            contentDescription = "$milestoneName: $streakDays day streak achieved"
        }
    ) {
        // Flat design - subtle layered alpha effect instead of blur glow
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(glowScale * 1.1f)
                .alpha(glowAlpha * 0.15f)
                .background(secondaryColor, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(glowScale)
                .alpha(glowAlpha * 0.25f)
                .background(primaryColor, CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = ProdyIcons.LocalFireDepartment,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = streakDays.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Days",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = milestoneName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryColor
            )
        }
    }
}

// =============================================================================
// HELPER TYPES & FUNCTIONS
// =============================================================================

/**
 * Size variants for streak display.
 */
enum class StreakDisplaySize {
    Small, Medium, Large
}

/**
 * Dimensions for streak display based on size variant.
 */
private data class StreakDimensions(
    val glowSize: Dp,
    val iconSize: Dp,
    val countSize: TextUnit,
    val labelSize: TextUnit
)

/**
 * Get color pair based on streak tier.
 *
 * Tiers:
 * - 0 days: Gray (inactive)
 * - 1-6 days: Standard fire
 * - 7-29 days: Week warrior
 * - 30-89 days: Monthly champion
 * - 90+ days: Legendary gold
 *
 * @param streakDays Number of consecutive days
 * @return Pair of (primary color, secondary color for gradient)
 */
private fun getStreakTierColors(streakDays: Int): Pair<Color, Color> {
    return when {
        streakDays >= 90 -> GoldTier to StreakGlow           // Legendary tier
        streakDays >= 30 -> StreakFire to StreakEmber        // Monthly champion
        streakDays >= 7 -> StreakGlow to StreakEmber         // Week warrior
        streakDays > 0 -> StreakFire to StreakGlow           // Standard fire
        else -> Color.Gray to Color.Gray.copy(alpha = 0.5f)  // Inactive
    }
}
