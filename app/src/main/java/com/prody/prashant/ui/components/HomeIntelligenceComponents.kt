package com.prody.prashant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.prody.prashant.ui.theme.*

/**
 * ================================================================================================
 * HOME INTELLIGENCE COMPONENTS
 * ================================================================================================
 *
 * Specialized components for the home screen that leverage AI and user behavior intelligence.
 */

// ================================================================================================
// NEXT ACTION CARD
// ================================================================================================

/**
 * Displays a contextual suggestion for the user's next action.
 * Powered by ActiveProgressService.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val actionColor = getNextActionColor(nextAction.type)
    val actionIcon = getNextActionIcon(nextAction.type)

    ProdyPremiumCard(
        onClick = { onClick(nextAction.actionRoute) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderColor = actionColor.copy(alpha = 0.3f),
        borderWidth = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated Icon Box
            val infiniteTransition = rememberInfiniteTransition(label = "next_action_pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "icon_scale"
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .background(actionColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = null,
                    tint = actionColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NEXT SUGGESTION",
                    style = MaterialTheme.typography.labelSmall,
                    color = actionColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

// ================================================================================================
// BUDDHA THOUGHT CARD
// ================================================================================================

/**
 * Displays the daily wisdom/reflection prompt from "Buddha".
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
    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Psychology,
                        contentDescription = null,
                        tint = ProdyForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "BUDDHA'S WISDOM",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProdyForestGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                if (isAiGenerated) {
                    Surface(
                        color = ProdyForestGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "AI",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = ProdyForestGreen,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = ProdyForestGreen,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Text(
                    text = thought,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (explanation.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProdyGhostButton(
                    text = "Reflect deeper",
                    onClick = { /* Navigate to Reflection */ },
                    size = ProdyButtonSize.SMALL
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(32.dp),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = ProdyIcons.Refresh,
                        contentDescription = "Refresh",
                        tint = ProdyForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // AI Proof Mode Debug Info
            if (proofInfo.isEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                AiProofModeDebugInfo(proofInfo = proofInfo)
            }
        }
    }
}

// =============================================================================
// AI CONFIG WARNING BANNER
// =============================================================================

/**
 * Banner shown when AI is not configured (missing API key).
 */
@Composable
fun AiConfigWarningBanner(
    status: AiConfigurationStatus,
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (status == AiConfigurationStatus.CONFIGURED) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (status == AiConfigurationStatus.MISSING) "AI Not Configured" else "AI Configuration Error",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Personalized wisdom and insights are limited. Add your API key in settings.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )
            }

            TextButton(onClick = onConfigureClick) {
                Text(
                    text = "FIX",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// =============================================================================
// HELPERS
// =============================================================================

private fun getNextActionColor(type: NextActionType): Color {
    return when (type) {
        NextActionType.START_JOURNAL -> ProdyForestGreen
        NextActionType.FOLLOW_UP_JOURNAL -> ProdyForestGreen
        NextActionType.REVIEW_WORDS -> ProdyWarmAmber
        NextActionType.LEARN_WORD -> ProdyWarmAmber
        NextActionType.WRITE_FUTURE_MESSAGE -> Color(0xFF9C27B0)
        NextActionType.REFLECT_ON_QUOTE -> ProdyInfo
        NextActionType.COMPLETE_CHALLENGE -> ProdyWarmAmber
    }
}

private fun getNextActionIcon(type: NextActionType): ImageVector {
    return when (type) {
        NextActionType.START_JOURNAL -> ProdyIcons.EditNote
        NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.History
        NextActionType.REVIEW_WORDS -> ProdyIcons.Refresh
        NextActionType.LEARN_WORD -> ProdyIcons.School
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.FormatQuote
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents
    }
}
