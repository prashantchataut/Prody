package com.prody.prashant.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.CardShape
import com.prody.prashant.ui.theme.ElevatedCardShape
import com.prody.prashant.ui.theme.FeaturedCardShape
import com.prody.prashant.ui.theme.ProdyTokens

/**
 * Prody Design System - Card Components
 *
 * A collection of card components with various styles for different use cases.
 * All cards feature:
 * - Smooth press animations
 * - Proper accessibility semantics
 * - Consistent elevation and shadow
 * - Support for custom backgrounds including gradients
 */

// =============================================================================
// STANDARD PRODY CARD
// =============================================================================

/**
 * Standard card component for content containers.
 *
 * @param modifier Modifier for the card
 * @param shape Shape of the card corners
 * @param backgroundColor Background color of the card
 * @param elevation Shadow elevation
 * @param contentDescription Accessibility description
 * @param content Content to display inside the card
 */
@Composable
fun ProdyCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 2.dp,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else Modifier
            ),
        content = content
    )
}

// =============================================================================
// INTERACTIVE PRODY CARD
// =============================================================================

/**
 * Interactive card with press feedback animation.
 *
 * @param onClick Callback when card is clicked
 * @param modifier Modifier for the card
 * @param enabled Whether the card is clickable
 * @param shape Shape of the card corners
 * @param backgroundColor Background color of the card
 * @param elevation Shadow elevation (animates on press)
 * @param contentDescription Accessibility description for screen readers
 * @param content Content to display inside the card
 */
@Composable
fun ProdyClickableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardShape,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 2.dp,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animate scale on press for tactile feedback
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "card_scale"
    )

    // Animate elevation on press
    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed && enabled) elevation * 0.5f else elevation,
        animationSpec = tween(durationMillis = 100),
        label = "card_elevation"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = animatedElevation,
                shape = shape,
                clip = false,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom animation replaces ripple
                enabled = enabled,
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        content = content
    )
}

// =============================================================================
// ELEVATED CARD
// =============================================================================

/**
 * Card with higher elevation for prominent content.
 */
@Composable
fun ProdyElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = ElevatedCardShape,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 4.dp,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    ProdyCard(
        modifier = modifier,
        shape = shape,
        backgroundColor = backgroundColor,
        elevation = elevation,
        contentDescription = contentDescription,
        content = content
    )
}

// =============================================================================
// GRADIENT CARD
// =============================================================================

/**
 * Card with gradient background for visual emphasis.
 *
 * @param gradientColors List of colors for the gradient
 * @param modifier Modifier for the card
 * @param shape Shape of the card corners
 * @param elevation Shadow elevation
 * @param contentDescription Accessibility description
 * @param content Content to display inside the card
 */
@Composable
fun ProdyGradientCard(
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    shape: Shape = FeaturedCardShape,
    elevation: Dp = 4.dp,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false,
                ambientColor = gradientColors.firstOrNull()?.copy(alpha = 0.2f)
                    ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                spotColor = gradientColors.firstOrNull()?.copy(alpha = 0.3f)
                    ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(Brush.linearGradient(gradientColors))
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else Modifier
            ),
        content = content
    )
}

// =============================================================================
// OUTLINED CARD
// =============================================================================

/**
 * Card with border outline instead of shadow elevation.
 */
@Composable
fun ProdyOutlinedCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    borderWidth: Dp = 1.dp,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else Modifier
            ),
        content = content
    )
}

// =============================================================================
// FEATURED CARD
// =============================================================================

/**
 * Featured card for hero content with extra visual emphasis.
 */
@Composable
fun ProdyFeaturedCard(
    modifier: Modifier = Modifier,
    shape: Shape = FeaturedCardShape,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    elevation: Dp = 6.dp,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            .clip(shape)
            .background(backgroundColor)
            .padding(1.dp) // Subtle inner padding for refined look
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else Modifier
            ),
        content = content
    )
}

// =============================================================================
// PREMIUM CARD WITH GLOW EFFECT
// =============================================================================

/**
 * Premium card component with optional glow effect for highlighted states.
 *
 * Features:
 * - Subtle press animation with spring physics
 * - Gradient background support
 * - Glow effect for highlighted states
 * - Full accessibility support
 *
 * @param modifier Modifier for the card
 * @param onClick Optional click callback - if null, card is not clickable
 * @param backgroundColor Background color of the card
 * @param contentColor Content color for text/icons inside the card
 * @param shape Shape of the card corners
 * @param elevation Shadow elevation
 * @param borderColor Optional border color
 * @param borderWidth Border width when borderColor is specified
 * @param glowColor Optional glow color for highlighting - null disables glow
 * @param contentDescription Accessibility description
 * @param content Content to display inside the card
 */
@Composable
fun ProdyPremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shape: Shape = RoundedCornerShape(ProdyTokens.Radius.lg),
    elevation: Dp = ProdyTokens.Elevation.sm,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    glowColor: Color? = null,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "premium_card_scale"
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed && onClick != null) elevation / 2 else elevation,
        animationSpec = tween(ProdyTokens.Animation.fast),
        label = "premium_card_elevation"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (glowColor != null) {
                    Modifier.shadow(
                        elevation = 12.dp,
                        shape = shape,
                        ambientColor = glowColor.copy(alpha = 0.3f),
                        spotColor = glowColor.copy(alpha = 0.3f)
                    )
                } else Modifier
            )
    ) {
        Surface(
            modifier = Modifier
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                    } else Modifier
                )
                .then(
                    if (borderColor != null) {
                        Modifier.border(borderWidth, borderColor, shape)
                    } else Modifier
                )
                .semantics {
                    if (contentDescription != null) {
                        this.contentDescription = contentDescription
                    }
                    if (onClick != null) {
                        role = Role.Button
                    }
                },
            shape = shape,
            color = backgroundColor,
            contentColor = contentColor,
            shadowElevation = animatedElevation
        ) {
            Column(content = content)
        }
    }
}

// =============================================================================
// PREMIUM GRADIENT CARD WITH COLUMN LAYOUT
// =============================================================================

/**
 * Premium gradient card with enhanced visual effects.
 *
 * @param gradientColors List of colors for the gradient background
 * @param modifier Modifier for the card
 * @param onClick Optional click callback
 * @param contentColor Content color for text/icons (defaults to white for gradient visibility)
 * @param shape Shape of the card corners
 * @param elevation Shadow elevation
 * @param glowEnabled Whether to show glow effect using the first gradient color
 * @param contentDescription Accessibility description
 * @param content Content to display inside the card
 */
@Composable
fun ProdyPremiumGradientCard(
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(ProdyTokens.Radius.lg),
    elevation: Dp = ProdyTokens.Elevation.md,
    glowEnabled: Boolean = false,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "premium_gradient_card_scale"
    )

    val glowColor = if (glowEnabled) gradientColors.firstOrNull() else null

    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (glowColor != null) {
                    Modifier.shadow(
                        elevation = 16.dp,
                        shape = shape,
                        ambientColor = glowColor.copy(alpha = 0.4f),
                        spotColor = glowColor.copy(alpha = 0.4f)
                    )
                } else Modifier
            )
            .shadow(elevation, shape)
            .clip(shape)
            .background(Brush.linearGradient(gradientColors))
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
                if (onClick != null) {
                    role = Role.Button
                }
            }
    ) {
        Column(
            modifier = Modifier.padding(ProdyTokens.Spacing.lg),
            content = content
        )
    }
}

// =============================================================================
// NOTIFICATION-STYLE CARD
// =============================================================================

/**
 * Card styled for notification-like content with engaging visuals.
 *
 * @param accentColor Accent color for the left border and subtle glow
 * @param modifier Modifier for the card
 * @param onClick Optional click callback
 * @param backgroundColor Background color
 * @param elevation Shadow elevation
 * @param contentDescription Accessibility description
 * @param content Content to display inside the card
 */
@Composable
fun ProdyNotificationCard(
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = ProdyTokens.Elevation.md,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "notification_card_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(ProdyTokens.Radius.lg),
                ambientColor = accentColor.copy(alpha = 0.15f),
                spotColor = accentColor.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(ProdyTokens.Radius.lg))
            .background(backgroundColor)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
                if (onClick != null) {
                    role = Role.Button
                }
            }
    ) {
        // Accent border on the left side
        Box(
            modifier = Modifier
                .matchParentSize()
        ) {
            Box(
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.CenterStart)
                    .padding(start = 0.dp)
                    .background(
                        color = accentColor,
                        shape = RoundedCornerShape(
                            topStart = ProdyTokens.Radius.lg,
                            bottomStart = ProdyTokens.Radius.lg
                        )
                    )
                    .padding(horizontal = 4.dp)
            )
        }

        Box(
            modifier = Modifier.padding(
                start = ProdyTokens.Spacing.md,
                top = ProdyTokens.Spacing.lg,
                end = ProdyTokens.Spacing.lg,
                bottom = ProdyTokens.Spacing.lg
            ),
            content = content
        )
    }
}
