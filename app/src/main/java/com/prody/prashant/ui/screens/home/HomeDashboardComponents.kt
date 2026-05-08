package com.prody.prashant.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.theme.ProdyDesignTokens
import com.prody.prashant.util.rememberProdyHaptic
import kotlinx.coroutines.delay

/**
 * =============================================================================
 * HOME DASHBOARD COMPONENTS - HIGH PERFORMANCE
 * =============================================================================
 *
 * This file contains premium components for the Prody Home Screen.
 * Performance Optimizations:
 * 1. Deferred Animation Reads: All animation values are read within the
 *    graphicsLayer scope to avoid parent recomposition.
 * 2. Flat Design Hierarchy: Minimized nesting to reduce measure/layout overhead.
 * 3. Stable Keys: Designed to work with LazyColumn stable keys for fast scrolling.
 */

// =============================================================================
// STAGGERED ENTRANCE ANIMATION
// =============================================================================

/**
 * Provides a high-performance staggered entrance animation for list items.
 * Uses deferred state reading to ensure 60fps even during complex screen loads.
 *
 * @param index The item's index in the list (used for delay calculation)
 * @param content The composable content to animate
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    content: @Composable () -> Unit
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Initial delay based on index for staggering effect
        delay(index * 60L)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = Modifier.graphicsLayer {
            // Deferring the reading of animatable.value to the draw phase
            // to completely avoid parent recomposition during animation.
            val progress = animatable.value
            alpha = progress
            translationY = (1f - progress) * 40.dp.toPx()
            scaleX = 0.95f + (progress * 0.05f)
            scaleY = 0.95f + (progress * 0.05f)
        }
    ) {
        content()
    }
}

// =============================================================================
// NEXT ACTION CARD
// =============================================================================

/**
 * Displays the contextually suggested next action for the user.
 * Part of the "Active Progress Layer".
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberProdyHaptic()

    ProdyNotificationCard(
        accentColor = ProdyDesignTokens.SemanticColors.info,
        modifier = modifier.fillMaxWidth(),
        onClick = {
            haptic.click()
            onClick()
        },
        contentDescription = "Suggested next action: ${nextAction.title}. ${nextAction.subtitle}"
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ProdyDesignTokens.SemanticColors.info,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SUGGESTED FOR YOU",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ProdyDesignTokens.SemanticColors.info,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

// =============================================================================
// TODAY PROGRESS CARD
// =============================================================================

/**
 * Displays a summary of the user's progress for today.
 * Shows XP earned, journal status, and vocabulary learned.
 */
@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    ProdyPremiumCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Today's Journey",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressItem(
                    label = "XP Earned",
                    value = "${progress.pointsEarned}",
                    icon = Icons.Default.AutoAwesome,
                    color = ProdyDesignTokens.SemanticColors.goldPrimary,
                    modifier = Modifier.weight(1f)
                )

                ProgressItem(
                    label = "Journal",
                    value = if (progress.journalEntries > 0) "Complete" else "Pending",
                    icon = com.prody.prashant.ui.icons.ProdyIcons.Book,
                    color = ProdyDesignTokens.SemanticColors.success,
                    modifier = Modifier.weight(1.2f)
                )

                ProgressItem(
                    label = "Words",
                    value = "${progress.wordsLearned}",
                    icon = com.prody.prashant.ui.icons.ProdyIcons.School,
                    color = ProdyDesignTokens.SemanticColors.info,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ProgressItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =============================================================================
// SEED STATUS CARD
// =============================================================================

/**
 * Visualizes the Seed -> Bloom mechanic.
 * The seed grows as the user completes actions throughout the day.
 */
@Composable
fun SeedStatusCard(
    seed: SeedEntity?,
    modifier: Modifier = Modifier
) {
    if (seed == null) return

    val progress = when(seed.state) {
        "bloomed" -> 1.0f
        "growing" -> 0.7f
        else -> 0.3f
    }

    ProdyPremiumGradientCard(
        gradientColors = listOf(
            Color(0xFF2E7D32), // Forest Green
            Color(0xFF1B5E20)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Seed/Plant Icon with growth animation
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                // Growth visualization (simplified)
                val scale = 0.5f + (progress * 0.5f)
                Icon(
                    imageVector = com.prody.prashant.ui.icons.ProdyIcons.SelfImprovement,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Growth Seed",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Text(
                    text = if (progress >= 1f) "Your seed has bloomed!" else "Your seed is growing...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Custom progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                if (progress < 1f) {
                    Text(
                        text = "${(progress * 100).toInt()}% towards blooming",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
