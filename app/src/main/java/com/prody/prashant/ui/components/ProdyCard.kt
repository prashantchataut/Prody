package com.prody.prashant.ui.components

import androidx.compose.animation.core.Spring
// animateDpAsState removed - no longer needed for flat design
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
// shadow import removed - flat design with no shadows
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.prody.prashant.util.rememberProdyHaptic
import com.prody.prashant.ui.theme.CardShape
import com.prody.prashant.ui.theme.ElevatedCardShape
import com.prody.prashant.ui.theme.FeaturedCardShape
import com.prody.prashant.ui.theme.ProdyDesignTokens

/**
 * Prody Design System - Card Components (Phase 2 Redesign)
 *
 * A collection of card components with flat, minimalist design.
 * All cards feature:
 * - Smooth press animations
 * - Proper accessibility semantics
 * - Flat design (no shadows or elevation)
 * - Support for custom backgrounds including gradients
 *
 * Design principles:
 * - NO shadows or elevation - strictly flat 2D design
 * - Clean borders for visual separation
 * - Subtle background color differentiation
 */

// =============================================================================
// STANDARD PRODY CARD
// =============================================================================

/**
 * Standard card component for content containers.
 * Flat design - no shadows or elevation.
 *
 * @param modifier Modifier for the card
 * @param shape Shape of the card corners (default 20dp radius)
 * @param backgroundColor Background color of the card
 * @param contentDescription Accessibility description
 * @param content Content to display inside the card
 */
@Composable
fun ProdyCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
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
 * Flat design - no shadows, uses scale animation for feedback.
 *
 * @param onClick Callback when card is clicked
 * @param modifier Modifier for the card
 * @param enabled Whether the card is clickable
 * @param shape Shape of the card corners (default 20dp radius)
 * @param backgroundColor Background color of the card
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
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberProdyHaptic()
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animate scale on press for tactile feedback
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "card_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom animation replaces ripple
                enabled = enabled,
                onClick = {
                    haptic.click()
                    onClick()
                }
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
 * Elevated card variant - uses surfaceVariant background for visual distinction.
 * Flat design - no shadows, relies on background color for hierarchy.
 */
@Composable
fun ProdyElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = ElevatedCardShape,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    ProdyCard(
        modifier = modifier,
        shape = shape,
        backgroundColor = backgroundColor,
        contentDescription = contentDescription,
        content = content
    )
}

// =============================================================================
// GRADIENT CARD
// =============================================================================

/**
 * Card with gradient background for visual emphasis.
 * Flat design - no shadows, gradient provides visual interest.
 *
 * @param gradientColors List of colors for the gradient
 * @param modifier Modifier for the card
 * @param shape Shape of the card corners
 * @param contentDescription Accessibility description
 * @param content Content to display inside the card
 */
@Composable
fun ProdyGradientCard(
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    shape: Shape = FeaturedCardShape,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
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
    borderWidth: Dp = ProdyDesignTokens.Elevation.low,
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
 * Flat design - uses primaryContainer background for distinction.
 */
@Composable
fun ProdyFeaturedCard(
    modifier: Modifier = Modifier,
    shape: Shape = FeaturedCardShape,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
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
// PREMIUM CARD WITH GLOW EFFECT
// =============================================================================

/**
 * Premium card component with optional border for highlighted states.
 * Flat design - no shadows or glow effects.
 *
 * Features:
 * - Subtle press animation with spring physics
 * - Optional border for visual emphasis
 * - Full accessibility support
 *
 * @param modifier Modifier for the card
 * @param onClick Optional click callback - if null, card is not clickable
 * @param backgroundColor Background color of the card
 * @param contentColor Content color for text/icons inside the card
 * @param shape Shape of the card corners
 * @param borderColor Optional border color for visual emphasis
 * @param borderWidth Border width when borderColor is specified
 * @param contentDescription Accessibility description
 * @param content Content to display inside the card
 */
@Composable
fun ProdyPremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shape: Shape = RoundedCornerShape(ProdyDesignTokens.Radius.card),
    borderColor: Color? = null,
    borderWidth: Dp = ProdyDesignTokens.Elevation.low,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberProdyHaptic()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "premium_card_scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            haptic.click()
                            onClick()
                        }
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
        shadowElevation = ProdyDesignTokens.Elevation.none
    ) {
        Column(content = content)
    }
}

// =============================================================================
// PREMIUM GRADIENT CARD WITH COLUMN LAYOUT
// =============================================================================

/**
 * Premium gradient card with gradient background.
 * Flat design - no shadows or glow effects.
 *
 * @param gradientColors List of colors for the gradient background
 * @param modifier Modifier for the card
 * @param onClick Optional click callback
 * @param contentColor Content color for text/icons (defaults to white for gradient visibility)
 * @param shape Shape of the card corners
 * @param contentDescription Accessibility description
 * @param content Content to display inside the card
 */
@Composable
fun ProdyPremiumGradientCard(
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(ProdyDesignTokens.Radius.card),
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberProdyHaptic()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "premium_gradient_card_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(Brush.linearGradient(gradientColors))
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            haptic.click()
                            onClick()
                        }
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
            modifier = Modifier.padding(ProdyDesignTokens.Spacing.medium),
            content = content
        )
    }
}

// =============================================================================
// NOTIFICATION-STYLE CARD
// =============================================================================

/**
 * Card styled for notification-like content with engaging visuals.
 * Phase 2: Flat design with no shadows.
 * Card styled for notification-like content with accent border.
 * Flat design - no shadows, uses accent border for visual emphasis.
 *
 * @param accentColor Accent color for the left border
 * @param modifier Modifier for the card
 * @param onClick Optional click callback
 * @param backgroundColor Background color
 * @param contentDescription Accessibility description
 * @param content Content to display inside the card
 */
@Composable
fun ProdyNotificationCard(
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberProdyHaptic()
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
            .clip(RoundedCornerShape(ProdyDesignTokens.Radius.card))
            .background(backgroundColor)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            haptic.click()
                            onClick()
                        }
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
                    .padding(start = ProdyDesignTokens.Spacing.none)
                    .background(
                        color = accentColor,
                        shape = RoundedCornerShape(
                            topStart = ProdyDesignTokens.Radius.card,
                            bottomStart = ProdyDesignTokens.Radius.card
                        )
                    )
                    .padding(horizontal = ProdyDesignTokens.Spacing.extraSmall)
            )
        }

        Box(
            modifier = Modifier.padding(
                start = ProdyDesignTokens.Spacing.medium,
                top = ProdyDesignTokens.Spacing.medium,
                end = ProdyDesignTokens.Spacing.medium,
                bottom = ProdyDesignTokens.Spacing.medium
            ),
            content = content
        )
    }
}
