package com.kairos.app.ui.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kairos.app.ui.theme.KairosTokens

/**
 * Kairos Design System - Empty State Components
 *
 * Elegant empty state displays for when content is not available.
 * Designed to calm and inspire users rather than frustrate them.
 *
 * Features:
 * - Subtle breathing animation for icon
 * - Supportive, encouraging messaging
 * - Optional action button
 * - Full accessibility support
 */

// =============================================================================
// STANDARD EMPTY STATE
// =============================================================================

/**
 * Elegant empty state component for empty lists and content areas.
 *
 * @param icon Icon to display (use Material Icons)
 * @param title Main headline - should be brief and clear
 * @param message Supporting text explaining the empty state
 * @param modifier Modifier for the component
 * @param iconContentDescription Accessibility description for the icon
 * @param actionButton Optional composable for a call-to-action button
 */
@Composable
fun KairosEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    iconContentDescription: String? = null,
    actionButton: @Composable (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty_state")

    val iconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KairosTokens.Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconContentDescription,
            modifier = Modifier
                .size(KairosTokens.IconSize.hero)
                .alpha(iconAlpha),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(KairosTokens.Spacing.xl))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(KairosTokens.Spacing.sm))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 280.dp)
        )

        if (actionButton != null) {
            Spacer(modifier = Modifier.height(KairosTokens.Spacing.xl))
            actionButton()
        }
    }
}

// =============================================================================
// EMPTY STATE WITH PRIMARY ACTION
// =============================================================================

/**
 * Empty state with a prominent primary action button.
 *
 * @param icon Icon to display
 * @param title Main headline
 * @param message Supporting text
 * @param actionLabel Label for the action button
 * @param onActionClick Callback when action button is clicked
 * @param modifier Modifier for the component
 * @param iconContentDescription Optional icon description
 */
@Composable
fun KairosEmptyStateWithAction(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconContentDescription: String? = null
) {
    KairosEmptyState(
        icon = icon,
        title = title,
        message = message,
        modifier = modifier,
        iconContentDescription = iconContentDescription,
        actionButton = {
            KairosPrimaryButton(
                text = actionLabel,
                onClick = onActionClick,
                modifier = Modifier.height(KairosTokens.Touch.minimum)
            )
        }
    )
}

// =============================================================================
// EMPTY STATE WITH SECONDARY ACTION
// =============================================================================

/**
 * Empty state with a subtle secondary action button.
 *
 * @param icon Icon to display
 * @param title Main headline
 * @param message Supporting text
 * @param actionLabel Label for the action button
 * @param onActionClick Callback when action button is clicked
 * @param modifier Modifier for the component
 * @param iconContentDescription Optional icon description
 */
@Composable
fun KairosEmptyStateWithSecondaryAction(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconContentDescription: String? = null
) {
    KairosEmptyState(
        icon = icon,
        title = title,
        message = message,
        modifier = modifier,
        iconContentDescription = iconContentDescription,
        actionButton = {
            KairosOutlinedButton(
                text = actionLabel,
                onClick = onActionClick,
                modifier = Modifier.height(KairosTokens.Touch.minimum)
            )
        }
    )
}

// =============================================================================
// COMPACT EMPTY STATE
// =============================================================================

/**
 * Compact empty state for smaller containers.
 *
 * @param icon Icon to display
 * @param message Brief message explaining the empty state
 * @param modifier Modifier for the component
 */
@Composable
fun KairosCompactEmptyState(
    icon: ImageVector,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KairosTokens.Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Compact icon is typically decorative
            modifier = Modifier.size(KairosTokens.IconSize.xl),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(KairosTokens.Spacing.sm))

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// =============================================================================
// LOADING EMPTY STATE
// =============================================================================

/**
 * Empty state variant for loading states with shimmer effect.
 *
 * @param modifier Modifier for the component
 * @param itemCount Number of placeholder items to show
 */
@Composable
fun KairosLoadingEmptyState(
    modifier: Modifier = Modifier,
    itemCount: Int = 3
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KairosTokens.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(KairosTokens.Spacing.md)
    ) {
        repeat(itemCount) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )
        }
    }
}

// =============================================================================
// ERROR EMPTY STATE
// =============================================================================

/**
 * Empty state variant for error conditions.
 *
 * @param icon Icon to display
 * @param title Error headline
 * @param message Error description
 * @param retryLabel Label for retry button
 * @param onRetryClick Callback when retry is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun KairosErrorEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    retryLabel: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "error_state")

    val iconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "error_icon_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KairosTokens.Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = AccessibilityHelper.ContentDescriptions.ERROR,
            modifier = Modifier
                .size(KairosTokens.IconSize.hero)
                .alpha(iconAlpha),
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(KairosTokens.Spacing.xl))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(KairosTokens.Spacing.sm))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 280.dp)
        )

        Spacer(modifier = Modifier.height(KairosTokens.Spacing.xl))

        KairosOutlinedButton(
            text = retryLabel,
            onClick = onRetryClick,
            modifier = Modifier.height(KairosTokens.Touch.minimum)
        )
    }
}

// =============================================================================
// FIRST-TIME USER EMPTY STATE
// =============================================================================

/**
 * Welcoming empty state for first-time users.
 *
 * @param icon Icon representing the feature
 * @param title Welcome headline
 * @param message Encouraging description of the feature
 * @param getStartedLabel Label for the get started button
 * @param onGetStartedClick Callback when get started is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun KairosWelcomeEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    getStartedLabel: String,
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_state")

    val iconScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "welcome_icon_scale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KairosTokens.Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Welcome",
            modifier = Modifier
                .size(96.dp)
                .alpha(0.9f),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(KairosTokens.Spacing.xxl))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(KairosTokens.Spacing.md))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 320.dp)
        )

        Spacer(modifier = Modifier.height(KairosTokens.Spacing.xxl))

        KairosPrimaryButton(
            text = getStartedLabel,
            onClick = onGetStartedClick,
            modifier = Modifier
                .height(KairosTokens.Touch.comfortable)
                .widthIn(min = 200.dp),
            size = KairosButtonSize.LARGE
        )
    }
}
