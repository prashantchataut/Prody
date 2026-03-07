package com.prody.prashant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.screens.home.AiConfigurationStatus
import com.prody.prashant.ui.screens.home.AiProofModeInfo
import com.prody.prashant.ui.theme.ProdyDesignTokens
import com.prody.prashant.ui.theme.ProdyForestGreen
import com.prody.prashant.ui.theme.ProdyPrimary
import com.prody.prashant.ui.theme.ProdySurfaceLight
import com.prody.prashant.ui.theme.ProdyTextPrimaryLight
import com.prody.prashant.ui.theme.ProdyTextSecondaryLight
import com.prody.prashant.ui.theme.ProdyWarmAmber
import com.prody.prashant.util.rememberProdyHaptic
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ================================================================================================
 * HOME INTELLIGENCE COMPONENTS
 * ================================================================================================
 *
 * Contextual, AI-driven, and state-aware components for the Home Screen.
 * These components follow Prody's flat design principles and performance best practices.
 */

// ================================================================================================
// NEXT ACTION CARD
// ================================================================================================

/**
 * Contextual suggestion card that guides the user to their next mindful activity.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberProdyHaptic()
    val actionColor = getNextActionColor(nextAction.type)
    val actionIcon = getNextActionIcon(nextAction.type)

    ProdyClickableCard(
        onClick = {
            haptic.click()
            onClick(nextAction.actionRoute)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ProdyDesignTokens.Spacing.screenHorizontal),
        backgroundColor = actionColor.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .padding(ProdyDesignTokens.Spacing.cardPadding)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Icon Container
            val infiniteTransition = rememberInfiniteTransition(label = "next_action_pulse")
            val iconScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "icon_scale"
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(actionColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = null,
                    tint = actionColor,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                        }
                )
            }

            Spacer(modifier = Modifier.width(ProdyDesignTokens.Spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NEXT STEP",
                    style = MaterialTheme.typography.labelSmall,
                    color = actionColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = ProdyIcons.ArrowForward,
                contentDescription = null,
                tint = actionColor.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun getNextActionColor(type: NextActionType): Color {
    return when (type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyForestGreen
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyWarmAmber
        NextActionType.WRITE_FUTURE_MESSAGE -> Color(0xFF9C27B0)
        NextActionType.REFLECT_ON_QUOTE -> ProdyPrimary
        NextActionType.COMPLETE_CHALLENGE -> Color(0xFFE91E63)
    }
}

private fun getNextActionIcon(type: NextActionType): ImageVector {
    return when (type) {
        NextActionType.START_JOURNAL -> ProdyIcons.Edit
        NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.EditNote
        NextActionType.REVIEW_WORDS -> ProdyIcons.History
        NextActionType.LEARN_WORD -> ProdyIcons.School
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.FormatQuote
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents
    }
}

private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

// ================================================================================================
// BUDDHA THOUGHT CARD
// ================================================================================================

/**
 * Displays AI-generated wisdom with a contemplative design.
 * Features a reveal animation and optional AI Proof Mode debug info.
 */
@Composable
fun BuddhaThoughtCard(
    thought: String,
    explanation: String,
    isLoading: Boolean,
    isAiGenerated: Boolean,
    proofInfo: AiProofModeInfo,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ProdyDesignTokens.Spacing.screenHorizontal),
        backgroundColor = ProdySurfaceLight
    ) {
        Column(
            modifier = Modifier
                .padding(ProdyDesignTokens.Spacing.cardPadding)
                .fillMaxWidth()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ProdyIcons.Psychology,
                        contentDescription = null,
                        tint = ProdyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(ProdyDesignTokens.Spacing.small))
                    Text(
                        text = "Daily Reflection",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ProdyTextSecondaryLight
                    )
                }

                if (!isLoading) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Refresh,
                            contentDescription = "Refresh wisdom",
                            tint = ProdyPrimary.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(ProdyDesignTokens.Spacing.medium))

            // Thought Content
            if (isLoading) {
                BuddhaContemplatingCompact()
            } else {
                Text(
                    text = "\"$thought\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = ProdyTextPrimaryLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                )

                AnimatedVisibility(
                    visible = isExpanded && explanation.isNotEmpty(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(ProdyDesignTokens.Spacing.small))
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = ProdyTextSecondaryLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // AI Proof Mode Info
            if (proofInfo.isEnabled && !isLoading) {
                Spacer(modifier = Modifier.height(ProdyDesignTokens.Spacing.medium))
                AiProofModeFooter(info = proofInfo)
            }
        }
    }
}

@Composable
private fun AiProofModeFooter(info: AiProofModeInfo) {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeStr = sdf.format(Date(info.timestamp))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.05f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ProdyIcons.Code,
                    contentDescription = null,
                    tint = ProdyTextSecondaryLight,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${info.provider} | ${info.cacheStatus}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = ProdyTextSecondaryLight
                )
            }
            Text(
                text = timeStr,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = ProdyTextSecondaryLight
            )
        }
    }
}

// ================================================================================================
// AI CONFIG WARNING BANNER
// ================================================================================================

/**
 * A subtle but clear warning when AI services are not configured.
 */
@Composable
fun AiConfigWarningBanner(
    status: AiConfigurationStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (status == AiConfigurationStatus.CONFIGURED) return

    val haptic = rememberProdyHaptic()
    val backgroundColor = if (status == AiConfigurationStatus.MISSING) {
        ProdyWarmAmber.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    }

    val contentColor = if (status == AiConfigurationStatus.MISSING) {
        ProdyWarmAmber
    } else {
        MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ProdyDesignTokens.Spacing.screenHorizontal)
            .clip(RoundedCornerShape(ProdyDesignTokens.Radius.medium))
            .clickable {
                haptic.click()
                onClick()
            },
        color = backgroundColor,
        shape = RoundedCornerShape(ProdyDesignTokens.Radius.medium)
    ) {
        Row(
            modifier = Modifier.padding(ProdyDesignTokens.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(ProdyDesignTokens.Spacing.medium))

            Column {
                Text(
                    text = if (status == AiConfigurationStatus.MISSING) "AI Not Configured" else "AI Service Error",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = if (status == AiConfigurationStatus.MISSING)
                        "Tap to set up your AI companion"
                    else "Tap to check settings",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
