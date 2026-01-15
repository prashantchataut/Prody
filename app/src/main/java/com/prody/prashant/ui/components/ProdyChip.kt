package com.prody.prashant.ui.components
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.ProdyTokens

/**
 * Prody Premium Chip System (Phase 2 Redesign)
 *
 * A comprehensive chip system with flat, minimalist design.
 * Features:
 * - Filter chips (toggleable)
 * - Selection chips (radio-style)
 * - Input chips (removable)
 * - Assist chips (action triggers)
 *
 * All chips feature:
 * - 32dp minimum height for accessibility
 * - Smooth selection animations
 * - Proper color transitions
 * - Full accessibility semantics
 * - Flat design (NO shadows)
 */

// =============================================================================
// CHIP SIZE CONFIGURATIONS
// =============================================================================

private object ChipDefaults {
    val Height = 32.dp
    val HorizontalPadding = 12.dp
    val IconSize = 18.dp
    val IconTextGap = 6.dp
    val CornerRadius = ProdyTokens.Radius.full
    const val AnimationDuration = 200
}

// =============================================================================
// FILTER CHIP (TOGGLEABLE)
// =============================================================================

/**
 * Filter chip for toggle-style selections.
 *
 * @param label Chip label text
 * @param selected Whether the chip is selected
 * @param onClick Toggle callback
 * @param modifier Modifier for the chip
 * @param enabled Whether the chip is enabled
 * @param leadingIcon Optional leading icon
 * @param showCheckmark Whether to show checkmark when selected
 * @param contentDescription Accessibility description
 */
@Composable
fun ProdyFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    showCheckmark: Boolean = true,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chip_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            selected -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(ChipDefaults.AnimationDuration),
        label = "chip_background"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            selected -> MaterialTheme.colorScheme.onSecondaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(ChipDefaults.AnimationDuration),
        label = "chip_content"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            selected -> Color.Transparent
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(ChipDefaults.AnimationDuration),
        label = "chip_border"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = ChipDefaults.Height)
            .semantics {
                role = Role.Checkbox
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(ChipDefaults.CornerRadius),
        color = backgroundColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.padding(horizontal = ChipDefaults.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selected && showCheckmark) {
                Icon(
                    imageVector = ProdyIcons.Check,
                    contentDescription = null,
                    modifier = Modifier.size(ChipDefaults.IconSize),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(ChipDefaults.IconTextGap))
            } else if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(ChipDefaults.IconSize),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(ChipDefaults.IconTextGap))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}

// =============================================================================
// SELECTION CHIP (RADIO-STYLE)
// =============================================================================

/**
 * Selection chip for radio-style single selection.
 */
@Composable
fun ProdySelectionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    contentDescription: String? = null,
    selectedBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chip_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            selected -> selectedBackgroundColor
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(ChipDefaults.AnimationDuration),
        label = "chip_background"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            selected -> selectedContentColor
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(ChipDefaults.AnimationDuration),
        label = "chip_content"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = ChipDefaults.Height)
            .semantics {
                role = Role.RadioButton
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(ChipDefaults.CornerRadius),
        color = backgroundColor,
        contentColor = contentColor,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.padding(horizontal = ChipDefaults.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(ChipDefaults.IconSize),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(ChipDefaults.IconTextGap))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}

// =============================================================================
// INPUT CHIP (REMOVABLE)
// =============================================================================

/**
 * Input chip with remove action.
 */
@Composable
fun ProdyInputChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = if (enabled)
        MaterialTheme.colorScheme.onSurfaceVariant
    else
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = ChipDefaults.Height)
            .semantics {
                role = Role.Button
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        shape = RoundedCornerShape(ChipDefaults.CornerRadius),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = contentColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(
                start = ChipDefaults.HorizontalPadding,
                end = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(ChipDefaults.IconSize),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(ChipDefaults.IconTextGap))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )

            Spacer(modifier = Modifier.width(4.dp))

            Surface(
                modifier = Modifier.size(24.dp),
                onClick = onRemove,
                enabled = enabled,
                shape = RoundedCornerShape(50),
                color = Color.Transparent,
                contentColor = contentColor
            ) {
                Icon(
                    imageVector = ProdyIcons.Close,
                    contentDescription = "Remove",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(16.dp),
                    tint = contentColor
                )
            }
        }
    }
}

// =============================================================================
// ASSIST CHIP (ACTION TRIGGER)
// =============================================================================

/**
 * Assist chip for triggering actions.
 */
@Composable
fun ProdyAssistChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chip_scale"
    )

    val contentColor = if (enabled)
        MaterialTheme.colorScheme.onSurface
    else
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    Surface(
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = ChipDefaults.Height)
            .semantics {
                role = Role.Button
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(ChipDefaults.CornerRadius),
        color = MaterialTheme.colorScheme.surface,
        contentColor = contentColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 0.dp, // Flat design
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.padding(horizontal = ChipDefaults.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(ChipDefaults.IconSize),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(ChipDefaults.IconTextGap))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(ChipDefaults.IconTextGap))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(ChipDefaults.IconSize),
                    tint = contentColor
                )
            }
        }
    }
}

// =============================================================================
// SUGGESTION CHIP
// =============================================================================

/**
 * Suggestion chip for contextual suggestions.
 */
@Composable
fun ProdySuggestionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    contentDescription: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
    contentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chip_scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = ChipDefaults.Height)
            .semantics {
                role = Role.Button
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            },
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(ChipDefaults.CornerRadius),
        color = if (enabled) backgroundColor
               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        contentColor = if (enabled) contentColor
                      else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.padding(horizontal = ChipDefaults.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(ChipDefaults.IconSize),
                    tint = if (enabled) contentColor
                          else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
                Spacer(modifier = Modifier.width(ChipDefaults.IconTextGap))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) contentColor
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}
