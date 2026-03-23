package com.prody.prashant.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.ui.components.ProdyIconButton
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * AI-related UI components for the Home Screen.
 *
 * Provides modular cards for Buddha's thoughts, next action suggestions,
 * and AI configuration status warnings.
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
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ProdyTokens.Radius.lg),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(ProdyTokens.Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ProdyIcons.SelfImprovement,
                        contentDescription = null,
                        tint = ProdyForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Buddha's Thought",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ProdyTextSecondaryLight
                    )
                }

                if (isAiGenerated) {
                    Surface(
                        color = ProdyForestGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "AI",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ProdyForestGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                        color = ProdyForestGreen
                    )
                }
            } else {
                Text(
                    text = "\"$thought\"",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic,
                        lineHeight = 24.sp
                    ),
                    color = ProdyTextPrimaryLight,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (explanation.isNotEmpty()) {
                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = ProdyTextSecondaryLight,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (proofInfo.isEnabled) {
                        Text(
                            text = "Source: ${proofInfo.provider} (${proofInfo.cacheStatus})",
                            style = MaterialTheme.typography.labelSmall,
                            color = ProdyTextTertiaryLight
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    ProdyIconButton(
                        icon = Icons.Default.Refresh,
                        onClick = onRefresh,
                        tint = ProdyForestGreen,
                        size = 32.dp
                    )
                }
            }
        }
    }
}

@Composable
fun NextActionCard(
    nextAction: NextAction?,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    nextAction?.let { action ->
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onActionClick),
            shape = RoundedCornerShape(ProdyTokens.Radius.lg),
            color = ProdyForestGreen.copy(alpha = 0.05f),
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(ProdyTokens.Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ProdyForestGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (action.type) {
                            NextActionType.START_JOURNAL -> ProdyIcons.Edit
                            NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Chat
                            NextActionType.REVIEW_WORDS -> ProdyIcons.School
                            NextActionType.LEARN_WORD -> ProdyIcons.Lightbulb
                            NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send
                            NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.SelfImprovement
                            NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents
                        },
                        contentDescription = null,
                        tint = ProdyForestGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ProdyTextPrimaryLight
                    )
                    Text(
                        text = action.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = ProdyTextSecondaryLight
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = ProdyTextTertiaryLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AiConfigWarningBanner(
    status: AiConfigurationStatus,
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = status != AiConfigurationStatus.CONFIGURED,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            color = ProdyWarningContainer,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ProdyWarning.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = ProdyWarning,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Not Configured",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ProdyTextPrimaryLight
                    )
                    Text(
                        text = "API key is missing. Using fallback wisdom content.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ProdyTextSecondaryLight
                    )
                }

                TextButton(onClick = onConfigureClick) {
                    Text(
                        text = "Fix",
                        color = ProdyForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
