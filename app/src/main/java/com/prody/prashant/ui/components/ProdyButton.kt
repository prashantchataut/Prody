package com.prody.prashant.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.animation.premiumShimmer
import com.prody.prashant.ui.theme.PoppinsFamily
import com.prody.prashant.ui.theme.ProdyTokens
import com.prody.prashant.util.rememberProdyHaptic

/**
 * Prody Premium Button System
 *
 * A comprehensive button system with consistent styling, accessibility,
 * and premium interactions.
 *
 * Button hierarchy:
 * - Primary: High emphasis, main call-to-action
 * - Secondary: Medium emphasis, alternative actions
 * - Ghost: Low emphasis, tertiary actions
 * - Outlined: Medium emphasis with border
 *
 * All buttons feature:
 * - 48dp minimum touch target
 * - Smooth press animations
 * - Loading state support
 * - Icon support (leading/trailing)
 * - Full accessibility semantics
 */

// =============================================================================
// BUTTON SIZE CONFIGURATIONS
// =============================================================================

/**
 * Button size variants.
 */
enum class ProdyButtonSize {
    SMALL,
    MEDIUM,
    LARGE
}

private object ProdyButtonDefaults {
    val SmallHeight = 36.dp
    val MediumHeight = 48.dp
    val LargeHeight = 56.dp

    val SmallHorizontalPadding = 12.dp
    val MediumHorizontalPadding = 16.dp
    val LargeHorizontalPadding = 24.dp

    val IconSize = 20.dp
    val LoadingIndicatorSize = 20.dp
    val IconTextGap = 8.dp

val CornerRadius = ProdyTokens.Radius.sm
}

// =============================================================================
// PRIMARY BUTTON
// =============================================================================

/**
 * Primary button for main call-to-action.
 *
 * @param text Button label
 * @param onClick Click callback
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 * @param loading Whether to show loading state
 * @param size Button size variant
 * @param leadingIcon Optional icon before text
 * @param trailingIcon Optional icon after text
 * @param contentDescription Accessibility description
 */
@Composable
fun ProdyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ProdyButtonSize = ProdyButtonSize.MEDIUM,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberProdyHaptic()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    val height = when (size) {
        ProdyButtonSize.SMALL -> ProdyButtonDefaults.SmallHeight
        ProdyButtonSize.MEDIUM -> ProdyButtonDefaults.MediumHeight
        ProdyButtonSize.LARGE -> ProdyButtonDefaults.LargeHeight
    }

    val horizontalPadding = when (size) {
        ProdyButtonSize.SMALL -> ProdyButtonDefaults.SmallHorizontalPadding
        ProdyButtonSize.MEDIUM -> ProdyButtonDefaults.MediumHorizontalPadding
        ProdyButtonSize.LARGE -> ProdyButtonDefaults.LargeHorizontalPadding
    }

    Button(
        onClick = {
            if (!loading) {
                haptic.click()
                onClick()
            }
        },
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = height)
            .premiumShimmer(isVisible = enabled && !loading)
            .semantics {
                role = Role.Button
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        enabled = enabled && !loading,
        shape = RoundedCornerShape(ProdyButtonDefaults.CornerRadius),
        contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 0.dp),
        interactionSource = interactionSource,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            size = size
        )
    }
}

// =============================================================================
// SECONDARY BUTTON
// =============================================================================

/**
 * Secondary button for alternative actions.
 */
@Composable
fun ProdySecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ProdyButtonSize = ProdyButtonSize.MEDIUM,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberProdyHaptic()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    val height = when (size) {
        ProdyButtonSize.SMALL -> ProdyButtonDefaults.SmallHeight
        ProdyButtonSize.MEDIUM -> ProdyButtonDefaults.MediumHeight
        ProdyButtonSize.LARGE -> ProdyButtonDefaults.LargeHeight
    }

    val horizontalPadding = when (size) {
        ProdyButtonSize.SMALL -> ProdyButtonDefaults.SmallHorizontalPadding
        ProdyButtonSize.MEDIUM -> ProdyButtonDefaults.MediumHorizontalPadding
        ProdyButtonSize.LARGE -> ProdyButtonDefaults.LargeHorizontalPadding
    }

    Button(
        onClick = {
            if (!loading) {
                haptic.click()
                onClick()
            }
        },
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = height)
            .semantics {
                role = Role.Button
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        enabled = enabled && !loading,
        shape = RoundedCornerShape(ProdyButtonDefaults.CornerRadius),
        contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 0.dp),
        interactionSource = interactionSource,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            size = size
        )
    }
}

// =============================================================================
// OUTLINED BUTTON
// =============================================================================

/**
 * Outlined button for medium emphasis actions.
 */
@Composable
fun ProdyOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ProdyButtonSize = ProdyButtonSize.MEDIUM,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentDescription: String? = null,
    borderColor: Color = MaterialTheme.colorScheme.outline
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberProdyHaptic()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    val height = when (size) {
        ProdyButtonSize.SMALL -> ProdyButtonDefaults.SmallHeight
        ProdyButtonSize.MEDIUM -> ProdyButtonDefaults.MediumHeight
        ProdyButtonSize.LARGE -> ProdyButtonDefaults.LargeHeight
    }

    val horizontalPadding = when (size) {
        ProdyButtonSize.SMALL -> ProdyButtonDefaults.SmallHorizontalPadding
        ProdyButtonSize.MEDIUM -> ProdyButtonDefaults.MediumHorizontalPadding
        ProdyButtonSize.LARGE -> ProdyButtonDefaults.LargeHorizontalPadding
    }

    OutlinedButton(
        onClick = {
            if (!loading) {
                haptic.click()
                onClick()
            }
        },
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = height)
            .semantics {
                role = Role.Button
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        enabled = enabled && !loading,
        shape = RoundedCornerShape(ProdyButtonDefaults.CornerRadius),
        contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 0.dp),
        interactionSource = interactionSource,
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) borderColor else borderColor.copy(alpha = 0.38f)
        ),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            contentColor = MaterialTheme.colorScheme.primary,
            size = size
        )
    }
}

// =============================================================================
// GHOST BUTTON
// =============================================================================

/**
 * Ghost/Text button for low emphasis actions.
 */
@Composable
fun ProdyGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ProdyButtonSize = ProdyButtonSize.MEDIUM,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentDescription: String? = null,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberProdyHaptic()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    val height = when (size) {
        ProdyButtonSize.SMALL -> ProdyButtonDefaults.SmallHeight
        ProdyButtonSize.MEDIUM -> ProdyButtonDefaults.MediumHeight
        ProdyButtonSize.LARGE -> ProdyButtonDefaults.LargeHeight
    }

    val horizontalPadding = when (size) {
        ProdyButtonSize.SMALL -> ProdyButtonDefaults.SmallHorizontalPadding
        ProdyButtonSize.MEDIUM -> ProdyButtonDefaults.MediumHorizontalPadding
        ProdyButtonSize.LARGE -> ProdyButtonDefaults.LargeHorizontalPadding
    }

    TextButton(
        onClick = {
            if (!loading) {
                haptic.click()
                onClick()
            }
        },
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = height)
            .semantics {
                role = Role.Button
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        enabled = enabled && !loading,
        shape = RoundedCornerShape(ProdyButtonDefaults.CornerRadius),
        contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 0.dp),
        interactionSource = interactionSource,
        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
            contentColor = contentColor,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            contentColor = contentColor,
            size = size
        )
    }
}

// =============================================================================
// BUTTON CONTENT HELPER
// =============================================================================

@Composable
private fun ButtonContent(
    text: String,
    loading: Boolean,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
    contentColor: Color,
    size: ProdyButtonSize
) {
    val textStyle = when (size) {
        ProdyButtonSize.SMALL -> MaterialTheme.typography.labelMedium
        ProdyButtonSize.MEDIUM -> MaterialTheme.typography.labelLarge
        ProdyButtonSize.LARGE -> MaterialTheme.typography.titleSmall
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(ProdyButtonDefaults.LoadingIndicatorSize),
                color = contentColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(ProdyButtonDefaults.IconTextGap))
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(ProdyButtonDefaults.IconTextGap))
        }

        Text(
            text = text,
            style = textStyle,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )

        if (trailingIcon != null && !loading) {
            Spacer(modifier = Modifier.width(ProdyButtonDefaults.IconTextGap))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = contentColor
            )
        }
    }
}

// =============================================================================
// ICON BUTTON
// =============================================================================

/**
 * Circular icon button with proper touch target.
 *
 * @param icon The icon to display
 * @param onClick Click callback
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 * @param contentDescription Accessibility description
 * @param tooltip Optional tooltip text
 * @param tint Icon color tint
 * @param size Button touch target size
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdyIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    tooltip: String? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    size: Dp = ProdyTokens.Touch.minimum
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberProdyHaptic()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_button_scale"
    )

    val buttonContent = @Composable { contentModifier: Modifier ->
        Surface(
            onClick = {
                haptic.click()
                onClick()
            },
            modifier = contentModifier
                .scale(scale)
                .size(size)
                .semantics {
                    role = Role.Button
                    if (contentDescription != null) {
                        this.contentDescription = contentDescription
                    }
                },
            enabled = enabled,
            shape = RoundedCornerShape(50),
            color = Color.Transparent,
            contentColor = tint,
            interactionSource = interactionSource
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp),
                tint = if (enabled) tint else tint.copy(alpha = 0.38f)
            )
        }
    }

    if (tooltip != null) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(ProdyTokens.Radius.sm)
                ) {
                    Text(
                        text = tooltip,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            },
            state = rememberTooltipState(isPersistent = false),
            modifier = modifier
        ) {
            buttonContent(Modifier)
        }
    } else {
        buttonContent(modifier)
    }
}
