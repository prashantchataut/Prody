package com.prody.prashant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import com.prody.prashant.ui.icons.ProdyIcons
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.ProdyTokens

/**
 * Prody Premium Header System (Phase 2 Redesign)
 *
 * A production-grade, reusable header component designed for premium user experience.
 * Features three variants:
 * - Minimal: Clean surface-based header for content-focused screens
 * - Contextual: Subtle gradient/tonal header for feature screens
 * - ScrollAware: Dynamic header that responds to scroll state
 *
 * Design principles:
 * - Edge-to-edge support with proper insets
 * - No harsh cutoff lines between header and content
 * - Proper text contrast on all backgrounds
 * - 48dp minimum touch targets for accessibility
 * - Smooth animations for state changes
 * - Flat design (NO shadows or elevation)
 */

// =============================================================================
// HEADER STYLE ENUM
// =============================================================================

/**
 * Header visual style variants.
 */
enum class ProdyHeaderStyle {
    /** Clean, minimal surface-based header */
    MINIMAL,
    /** Subtle tonal/gradient header for contextual screens */
    CONTEXTUAL,
    /** Dynamic header that responds to scroll state */
    SCROLL_AWARE
}

// =============================================================================
// HEADER SIZE CONFIGURATIONS
// =============================================================================

private object HeaderDefaults {
    val MinimalHeight = 56.dp
    val ContextualHeight = 64.dp
    val ExpandedHeight = 120.dp
    val CollapsedHeight = 56.dp

    val HorizontalPadding = ProdyTokens.Spacing.lg
    val VerticalPadding = ProdyTokens.Spacing.md

    val TitleMaxLines = 1
    val SubtitleMaxLines = 1

    const val CollapsedScrollThreshold = 100f
    const val AnimationDuration = 200
}

// =============================================================================
// MAIN HEADER COMPOSABLE
// =============================================================================

/**
 * Premium header component for Prody screens.
 *
 * @param title Primary title text
 * @param modifier Modifier for the header
 * @param subtitle Optional subtitle text
 * @param style Header style variant
 * @param navigationIcon Optional navigation icon (back button)
 * @param onNavigationClick Callback for navigation icon click
 * @param actions Optional trailing action icons
 * @param scrollOffset Current scroll offset for SCROLL_AWARE style
 * @param backgroundColor Custom background color (overrides style default)
 * @param contentColor Custom content color (overrides style default)
 * @param gradientColors Custom gradient colors for CONTEXTUAL style
 */
@Composable
fun ProdyHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    style: ProdyHeaderStyle = ProdyHeaderStyle.MINIMAL,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    scrollOffset: Float = 0f,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    gradientColors: List<Color>? = null
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()

    // Determine colors based on style
    val resolvedBackgroundColor = backgroundColor ?: when (style) {
        ProdyHeaderStyle.MINIMAL -> MaterialTheme.colorScheme.surface
        ProdyHeaderStyle.CONTEXTUAL -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ProdyHeaderStyle.SCROLL_AWARE -> MaterialTheme.colorScheme.surface
    }

    val resolvedContentColor = contentColor ?: when (style) {
        ProdyHeaderStyle.MINIMAL -> MaterialTheme.colorScheme.onSurface
        ProdyHeaderStyle.CONTEXTUAL -> MaterialTheme.colorScheme.onPrimaryContainer
        ProdyHeaderStyle.SCROLL_AWARE -> MaterialTheme.colorScheme.onSurface
    }

    when (style) {
        ProdyHeaderStyle.MINIMAL -> MinimalHeader(
            title = title,
            subtitle = subtitle,
            modifier = modifier,
            navigationIcon = navigationIcon,
            onNavigationClick = onNavigationClick,
            actions = actions,
            backgroundColor = resolvedBackgroundColor,
            contentColor = resolvedContentColor,
            statusBarPadding = statusBarPadding.calculateTopPadding()
        )

        ProdyHeaderStyle.CONTEXTUAL -> ContextualHeader(
            title = title,
            subtitle = subtitle,
            modifier = modifier,
            navigationIcon = navigationIcon,
            onNavigationClick = onNavigationClick,
            actions = actions,
            backgroundColor = resolvedBackgroundColor,
            contentColor = resolvedContentColor,
            gradientColors = gradientColors,
            statusBarPadding = statusBarPadding.calculateTopPadding()
        )

        ProdyHeaderStyle.SCROLL_AWARE -> ScrollAwareHeader(
            title = title,
            subtitle = subtitle,
            modifier = modifier,
            navigationIcon = navigationIcon,
            onNavigationClick = onNavigationClick,
            actions = actions,
            scrollOffset = scrollOffset,
            backgroundColor = resolvedBackgroundColor,
            contentColor = resolvedContentColor,
            statusBarPadding = statusBarPadding.calculateTopPadding()
        )
    }
}

// =============================================================================
// MINIMAL HEADER
// =============================================================================

@Composable
private fun MinimalHeader(
    title: String,
    subtitle: String?,
    modifier: Modifier,
    navigationIcon: ImageVector?,
    onNavigationClick: (() -> Unit)?,
    actions: @Composable (RowScope.() -> Unit)?,
    backgroundColor: Color,
    contentColor: Color,
    statusBarPadding: Dp
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        contentColor = contentColor,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(top = statusBarPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HeaderDefaults.MinimalHeight)
                    .padding(horizontal = HeaderDefaults.HorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Leading section: Navigation + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (navigationIcon != null && onNavigationClick != null) {
                        IconButton(
                            onClick = onNavigationClick,
                            modifier = Modifier.padding(end = ProdyTokens.Spacing.xs)
                        ) {
                            Icon(
                                imageVector = navigationIcon,
                                contentDescription = "Navigate back",
                                tint = contentColor
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor,
                            maxLines = HeaderDefaults.TitleMaxLines,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.7f),
                                maxLines = HeaderDefaults.SubtitleMaxLines,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Trailing section: Actions
                if (actions != null) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        actions()
                    }
                }
            }
        }
    }
}

// =============================================================================
// CONTEXTUAL HEADER
// =============================================================================

@Composable
private fun ContextualHeader(
    title: String,
    subtitle: String?,
    modifier: Modifier,
    navigationIcon: ImageVector?,
    onNavigationClick: (() -> Unit)?,
    actions: @Composable (RowScope.() -> Unit)?,
    backgroundColor: Color,
    contentColor: Color,
    gradientColors: List<Color>?,
    statusBarPadding: Dp
) {
    val backgroundModifier = if (gradientColors != null && gradientColors.size >= 2) {
        Modifier.background(
            Brush.verticalGradient(
                colors = gradientColors.map { it.copy(alpha = 0.15f) }
            )
        )
    } else {
        Modifier.background(backgroundColor)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(backgroundModifier)
    ) {
        Column(
            modifier = Modifier.padding(top = statusBarPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HeaderDefaults.ContextualHeight)
                    .padding(horizontal = HeaderDefaults.HorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Leading section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (navigationIcon != null && onNavigationClick != null) {
                        IconButton(
                            onClick = onNavigationClick,
                            modifier = Modifier.padding(end = ProdyTokens.Spacing.xs)
                        ) {
                            Icon(
                                imageVector = navigationIcon,
                                contentDescription = "Navigate back",
                                tint = contentColor
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            maxLines = HeaderDefaults.TitleMaxLines,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (subtitle != null) {
                            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xxs))
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = contentColor.copy(alpha = 0.8f),
                                maxLines = HeaderDefaults.SubtitleMaxLines,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Trailing section
                if (actions != null) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        actions()
                    }
                }
            }
        }
    }
}

// =============================================================================
// SCROLL-AWARE HEADER
// =============================================================================

@Composable
private fun ScrollAwareHeader(
    title: String,
    subtitle: String?,
    modifier: Modifier,
    navigationIcon: ImageVector?,
    onNavigationClick: (() -> Unit)?,
    actions: @Composable (RowScope.() -> Unit)?,
    scrollOffset: Float,
    backgroundColor: Color,
    contentColor: Color,
    statusBarPadding: Dp
) {
    // Calculate collapse progress (0 = expanded, 1 = collapsed)
    val collapseProgress by remember(scrollOffset) {
        derivedStateOf {
            (scrollOffset / HeaderDefaults.CollapsedScrollThreshold).coerceIn(0f, 1f)
        }
    }

    // Animated header height
    val headerHeight by animateDpAsState(
        targetValue = HeaderDefaults.ExpandedHeight -
            ((HeaderDefaults.ExpandedHeight - HeaderDefaults.CollapsedHeight) * collapseProgress),
        animationSpec = tween(HeaderDefaults.AnimationDuration),
        label = "header_height"
    )

    // Flat design - no animated elevation
    // Previously animated elevation removed for Phase 2 flat design

    // Animated title size alpha for subtitle
    val subtitleAlpha by animateFloatAsState(
        targetValue = 1f - collapseProgress,
        animationSpec = tween(HeaderDefaults.AnimationDuration),
        label = "subtitle_alpha"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        contentColor = contentColor,
        shadowElevation = 0.dp, // Flat design
        tonalElevation = 0.dp // Flat design
    ) {
        Column(
            modifier = Modifier.padding(top = statusBarPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .padding(horizontal = HeaderDefaults.HorizontalPadding),
                verticalAlignment = if (collapseProgress < 0.5f)
                    Alignment.Bottom else Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Leading section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (navigationIcon != null && onNavigationClick != null) {
                        IconButton(
                            onClick = onNavigationClick,
                            modifier = Modifier.padding(end = ProdyTokens.Spacing.xs)
                        ) {
                            Icon(
                                imageVector = navigationIcon,
                                contentDescription = "Navigate back",
                                tint = contentColor
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = if (collapseProgress < 0.5f)
                                ProdyTokens.Spacing.md else 0.dp)
                    ) {
                        Text(
                            text = title,
                            style = if (collapseProgress < 0.5f)
                                MaterialTheme.typography.headlineMedium
                                else MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            maxLines = HeaderDefaults.TitleMaxLines,
                            overflow = TextOverflow.Ellipsis
                        )

                        AnimatedVisibility(
                            visible = subtitle != null && collapseProgress < 0.7f,
                            enter = fadeIn(tween(HeaderDefaults.AnimationDuration)),
                            exit = fadeOut(tween(HeaderDefaults.AnimationDuration))
                        ) {
                            if (subtitle != null) {
                                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xxs))
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = contentColor.copy(alpha = 0.7f * subtitleAlpha),
                                    maxLines = HeaderDefaults.SubtitleMaxLines,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.alpha(subtitleAlpha)
                                )
                            }
                        }
                    }
                }

                // Trailing section
                if (actions != null) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            bottom = if (collapseProgress < 0.5f)
                                ProdyTokens.Spacing.md else 0.dp
                        )
                    ) {
                        actions()
                    }
                }
            }
        }
    }
}

// =============================================================================
// CONVENIENCE COMPOSABLES
// =============================================================================

/**
 * Convenience header with back navigation.
 */
@Composable
fun ProdyBackHeader(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    style: ProdyHeaderStyle = ProdyHeaderStyle.MINIMAL,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    ProdyHeader(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        style = style,
        navigationIcon = ProdyIcons.ArrowBack,
        onNavigationClick = onNavigateBack,
        actions = actions
    )
}

/**
 * Large hero-style header for feature screens.
 */
@Composable
fun ProdyHeroHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    scrollOffset: Float = 0f,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    gradientColors: List<Color>? = null
) {
    ProdyHeader(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        style = ProdyHeaderStyle.SCROLL_AWARE,
        navigationIcon = navigationIcon,
        onNavigationClick = onNavigationClick,
        actions = actions,
        scrollOffset = scrollOffset,
        gradientColors = gradientColors
    )
}
