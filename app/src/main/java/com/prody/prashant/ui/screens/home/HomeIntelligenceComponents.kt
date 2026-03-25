package com.prody.prashant.ui.screens.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.domain.streak.DualStreakStatus
import com.prody.prashant.domain.streak.StreakInfo
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * Home Intelligence Components
 *
 * Reusable components for the Home Screen that display contextual
 * intelligence and progress tracking.
 */

// =============================================================================
// NEXT ACTION CARD
// =============================================================================

@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_press_scale"
    )

    val (icon, color) = remember(nextAction.type) {
        when (nextAction.type) {
            NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL ->
                ProdyIcons.Edit to ProdyForestGreen
            NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD ->
                ProdyIcons.School to ProdyWarmAmber
            NextActionType.WRITE_FUTURE_MESSAGE ->
                ProdyIcons.Send to Color(0xFF9C27B0)
            NextActionType.REFLECT_ON_QUOTE ->
                ProdyIcons.FormatQuote to ProdyInfo
            NextActionType.COMPLETE_CHALLENGE ->
                ProdyIcons.EmojiEvents to ProdyPrimary
        }
    }

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(ProdyTokens.Radius.lg),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProdyTokens.Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(ProdyTokens.Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = color.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = color.copy(alpha = 0.5f)
            )
        }
    }
}

// =============================================================================
// TODAY PROGRESS CARD
// =============================================================================

@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ProdyTokens.Radius.lg),
        color = ProdySurfaceLight,
        shadowElevation = ProdyTokens.Elevation.sm
    ) {
        Column(
            modifier = Modifier.padding(ProdyTokens.Spacing.lg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Progress",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ProdyTextSecondaryLight
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(ProdyTokens.Radius.full))
                        .background(ProdyWarmAmber.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+${progress.pointsEarned} XP",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ProdyWarmAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressItem(
                    label = "Journals",
                    value = progress.journalEntries.toString(),
                    icon = ProdyIcons.Edit,
                    color = ProdyForestGreen
                )
                ProgressItem(
                    label = "Words",
                    value = progress.wordsLearned.toString(),
                    icon = ProdyIcons.School,
                    color = ProdyWarmAmber
                )
                ProgressItem(
                    label = "Words Written",
                    value = progress.wordsWritten.toString(),
                    icon = ProdyIcons.ChatBubble,
                    color = ProdyInfo
                )
            }
        }
    }
}

@Composable
private fun ProgressItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ProdyTextPrimaryLight
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ProdyTextSecondaryLight
        )
    }
}

// =============================================================================
// DUAL STREAK CARD
// =============================================================================

@Composable
fun DualStreakCard(
    status: DualStreakStatus,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.md)
    ) {
        StreakSubCard(
            label = "Wisdom",
            streakInfo = status.wisdomStreak,
            activeColor = ProdyWarmAmber,
            icon = ProdyIcons.Lightbulb,
            modifier = Modifier.weight(1f)
        )
        StreakSubCard(
            label = "Reflection",
            streakInfo = status.reflectionStreak,
            activeColor = ProdyForestGreen,
            icon = ProdyIcons.Edit,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StreakSubCard(
    label: String,
    streakInfo: StreakInfo,
    activeColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val isActive = streakInfo.maintainedToday
    val count = streakInfo.current
    val backgroundColor = if (isActive) activeColor.copy(alpha = 0.1f) else ProdySurfaceLight
    val contentColor = if (isActive) activeColor else ProdyTextSecondaryLight

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ProdyTokens.Radius.lg),
        color = backgroundColor,
        shadowElevation = if (isActive) 0.dp else ProdyTokens.Elevation.xs,
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, activeColor.copy(alpha = 0.2f)) else null
    ) {
        Column(
            modifier = Modifier.padding(ProdyTokens.Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        alpha = if (isActive) 1f else 0.5f
                    }
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isActive) activeColor else ProdyTextPrimaryLight
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = ProdyTextSecondaryLight
            )
        }
    }
}
