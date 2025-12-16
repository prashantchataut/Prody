package com.prody.prashant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.ProdyTokens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Prody Gamification 2.0 - Boosting System
 *
 * A modern, tasteful, and motivating support interaction system.
 * Replaces the old obtrusive boost/congrats buttons with:
 * - Single contextual "Support" action
 * - Elegant bottom sheet with options
 * - Subtle feedback animations
 * - Daily rate limiting and anti-spam
 *
 * Design principles:
 * - Non-obtrusive: triggered via long-press or small icon
 * - Meaningful: each action has purpose
 * - Minimal UI: no giant buttons per row
 * - Premium feel: smooth animations, tasteful effects
 */

// =============================================================================
// SUPPORT INTERACTION TYPES
// =============================================================================

/**
 * Types of support interactions available.
 */
enum class SupportType {
    /** Lightweight acknowledgment of effort */
    BOOST,
    /** Show respect for achievement */
    RESPECT,
    /** Friendly encouragement */
    ENCOURAGE
}

data class SupportAction(
    val type: SupportType,
    val icon: ImageVector,
    val label: String,
    val description: String,
    val color: Color
)

private val supportActions = listOf(
    SupportAction(
        type = SupportType.BOOST,
        icon = Icons.Outlined.Bolt,
        label = "Boost",
        description = "Give them an energy boost",
        color = Color(0xFFFFB300)
    ),
    SupportAction(
        type = SupportType.RESPECT,
        icon = Icons.Outlined.EmojiEvents,
        label = "Respect",
        description = "Show respect for their work",
        color = Color(0xFF7C4DFF)
    ),
    SupportAction(
        type = SupportType.ENCOURAGE,
        icon = Icons.Outlined.Handshake,
        label = "Encourage",
        description = "Send encouragement their way",
        color = Color(0xFF00BFA5)
    )
)

// =============================================================================
// SUPPORT ICON BUTTON (FOR LEADERBOARD ROWS)
// =============================================================================

/**
 * Small, contextual support action icon for leaderboard rows.
 *
 * @param onSupportClick Callback when support is triggered
 * @param modifier Modifier for the component
 * @param enabled Whether the action is enabled (rate limiting)
 * @param hasSupported Whether the user has already supported this person today
 */
@Composable
fun ProdySupportIconButton(
    onSupportClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hasSupported: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "support_scale"
    )

    val iconColor = when {
        hasSupported -> Color(0xFFFFB300)
        enabled -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }

    IconButton(
        onClick = {
            if (enabled && !hasSupported) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSupportClick()
            }
        },
        modifier = modifier.scale(scale),
        enabled = enabled && !hasSupported,
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = if (hasSupported) Icons.Filled.Bolt else Icons.Outlined.Bolt,
            contentDescription = if (hasSupported) "Already supported" else "Support",
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

// =============================================================================
// SUPPORT BOTTOM SHEET
// =============================================================================

/**
 * Elegant bottom sheet for support interactions.
 *
 * @param userName Name of the user being supported
 * @param isVisible Whether the sheet is visible
 * @param onDismiss Callback when sheet is dismissed
 * @param onSupportSelected Callback when a support type is selected
 * @param dailyBoostsRemaining Number of daily boosts remaining
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdySupportBottomSheet(
    userName: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSupportSelected: (SupportType) -> Unit,
    dailyBoostsRemaining: Int = 5
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(50)
                ) {
                    Box(modifier = Modifier.size(width = 32.dp, height = 4.dp))
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ProdyTokens.Spacing.lg)
                    .padding(bottom = ProdyTokens.Spacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Support $userName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xs))

                Text(
                    text = "$dailyBoostsRemaining supports remaining today",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xl))

                // Support options
                supportActions.forEach { action ->
                    SupportOptionCard(
                        action = action,
                        onClick = {
                            scope.launch {
                                onSupportSelected(action.type)
                                sheetState.hide()
                                onDismiss()
                            }
                        },
                        enabled = dailyBoostsRemaining > 0
                    )

                    Spacer(modifier = Modifier.height(ProdyTokens.Spacing.sm))
                }

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

                // Note about local storage
                Text(
                    text = "Supports are stored locally",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun SupportOptionCard(
    action: SupportAction,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    Card(
        onClick = {
            if (enabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        shape = RoundedCornerShape(ProdyTokens.Radius.md),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProdyTokens.Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container with colored background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(action.color.copy(alpha = if (enabled) 0.15f else 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = if (enabled) action.color else action.color.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(ProdyTokens.Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )

                Text(
                    text = action.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                )
            }
        }
    }
}

// =============================================================================
// SUPPORT SUCCESS FEEDBACK
// =============================================================================

/**
 * Subtle animation feedback when support is sent.
 *
 * @param supportType Type of support that was sent
 * @param isVisible Whether the feedback is visible
 * @param onAnimationComplete Callback when animation completes
 */
@Composable
fun ProdySupportFeedback(
    supportType: SupportType,
    isVisible: Boolean,
    onAnimationComplete: () -> Unit
) {
    val action = supportActions.find { it.type == supportType } ?: return

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(1500)
            onAnimationComplete()
        }
    }

    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                alpha.animateTo(1f, animationSpec = tween(200))
                delay(1000)
                alpha.animateTo(0f, animationSpec = tween(300))
            }
        } else {
            scale.snapTo(0f)
            alpha.snapTo(0f)
        }
    }

    AnimatedVisibility(
        visible = isVisible && alpha.value > 0f,
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        exit = fadeOut() + scaleOut(targetScale = 1.2f)
    ) {
        Box(
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .shadow(8.dp, CircleShape, ambientColor = action.color)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            action.color,
                            action.color.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(ProdyTokens.Spacing.lg),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xs))

                Text(
                    text = action.label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// =============================================================================
// SUPPORT COUNTER DISPLAY
// =============================================================================

/**
 * Compact display of boost count for leaderboard rows.
 *
 * @param boostCount Number of boosts received
 * @param modifier Modifier for the component
 */
@Composable
fun ProdyBoostCounter(
    boostCount: Int,
    modifier: Modifier = Modifier
) {
    if (boostCount <= 0) return

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Bolt,
            contentDescription = null,
            tint = Color(0xFFFFB300),
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(2.dp))

        Text(
            text = formatBoostCount(boostCount),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatBoostCount(count: Int): String {
    return when {
        count >= 1000 -> "${count / 1000}k"
        else -> count.toString()
    }
}

// =============================================================================
// LEADERBOARD ROW ADORNMENTS
// =============================================================================

/**
 * Top 3 position indicator with rank-based styling.
 *
 * @param rank Position in leaderboard (1, 2, or 3)
 * @param modifier Modifier for the component
 */
@Composable
fun ProdyTopRankIndicator(
    rank: Int,
    modifier: Modifier = Modifier
) {
    if (rank !in 1..3) return

    val infiniteTransition = rememberInfiniteTransition(label = "rank_indicator")

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val (color, emoji) = when (rank) {
        1 -> Color(0xFFFFD700) to "1st"
        2 -> Color(0xFFC0C0C0) to "2nd"
        3 -> Color(0xFFCD7F32) to "3rd"
        else -> Color.Gray to ""
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = (4.dp * glowPulse),
                shape = CircleShape,
                ambientColor = color.copy(alpha = 0.3f * glowPulse),
                spotColor = color.copy(alpha = 0.3f * glowPulse)
            )
            .size(28.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        color,
                        color.copy(alpha = 0.7f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (rank == 2) Color.DarkGray else Color.White
        )
    }
}

// =============================================================================
// STREAK MILESTONE ADORNMENT
// =============================================================================

/**
 * Special adornment for streak milestones.
 *
 * @param streakDays Number of consecutive days
 * @param modifier Modifier for the component
 */
@Composable
fun ProdyStreakMilestoneIndicator(
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val (color, label) = when {
        streakDays >= 365 -> Color(0xFFFF6B6B) to "365"
        streakDays >= 100 -> Color(0xFFFFD93D) to "100"
        streakDays >= 30 -> Color(0xFF6BCB77) to "30"
        streakDays >= 7 -> Color(0xFF4D96FF) to "7"
        else -> return
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$label+",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
