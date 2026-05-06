package com.prody.prashant.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.components.ProdyClickableCard
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * =============================================================================
 * ACTIVE PROGRESS LAYER COMPONENTS
 * =============================================================================
 */

/**
 * Next Action Card - Prompts the user with a contextual behavior-driven action.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = ProdyForestGreen.copy(alpha = 0.08f)
    val accentColor = ProdyForestGreen

    val icon = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Edit
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyIcons.School
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents
    }

    ProdyClickableCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        backgroundColor = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyTextPrimaryLight
                )
                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ProdyTextSecondaryLight
                )
            }

            Icon(
                imageVector = ProdyIcons.ArrowForward,
                contentDescription = null,
                tint = accentColor.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Today's Progress Card - Summarizes the user's activity for the current day.
 */
@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = ProdySurfaceLight
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "TODAY'S PROGRESS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = ProdyForestGreen,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStat(
                    label = "Points",
                    value = progress.pointsEarned.toString(),
                    icon = ProdyIcons.Stars,
                    color = ProdyWarmAmber
                )
                ProgressStat(
                    label = "Entries",
                    value = progress.journalEntries.toString(),
                    icon = ProdyIcons.Edit,
                    color = ProdyForestGreen
                )
                ProgressStat(
                    label = "Volume",
                    value = "${progress.wordsWritten}w",
                    icon = ProdyIcons.List,
                    color = ProdyInfo
                )
            }
        }
    }
}

@Composable
private fun ProgressStat(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ProdyTextPrimaryLight
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ProdyTextSecondaryLight
        )
    }
}

/**
 * Staggered Entrance Animation Utility
 *
 * Performance-optimized animation that uses graphicsLayer to avoid parent recomposition.
 *
 * @param index The position index for staggering
 * @param delayMillis Delay per index
 * @param content The composable content to animate
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    delayMillis: Int = 100,
    content: @Composable () -> Unit
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay((index * delayMillis).toLong())
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = Modifier.graphicsLayer {
            alpha = animatable.value
            translationY = (1f - animatable.value) * 50f
            scaleX = 0.95f + (0.05f * animatable.value)
            scaleY = 0.95f + (0.05f * animatable.value)
        }
    ) {
        content()
    }
}
