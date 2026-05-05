package com.prody.prashant.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * Active Progress Components
 *
 * Components for the "Active Progress Layer" of the dashboard.
 * Designed to make personal growth feel actionable and rewarding.
 */

@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val actionColor = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyForestGreen
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> Color(0xFF2196F3)
        NextActionType.WRITE_FUTURE_MESSAGE -> Color(0xFF9C27B0)
        NextActionType.REFLECT_ON_QUOTE -> ProdyWarmAmber
        NextActionType.COMPLETE_CHALLENGE -> ProdyInfo
    }

    val actionIcon = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Edit
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyIcons.School
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents
    }

    ProdyClickableCard(
        onClick = { onClick(nextAction.actionRoute) },
        modifier = modifier.fillMaxWidth(),
        backgroundColor = actionColor.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = actionColor
                )
                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ProdyTextSecondaryLight
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = actionColor.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = ProdySurfaceLight
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "TODAY'S MOMENTUM",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = ProdyTextSecondaryLight,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStatItem(
                    label = "Points",
                    value = progress.pointsEarned.toString(),
                    icon = ProdyIcons.EmojiEvents,
                    color = ProdyWarmAmber
                )
                ProgressStatItem(
                    label = "Entries",
                    value = progress.journalEntries.toString(),
                    icon = ProdyIcons.Edit,
                    color = ProdyForestGreen
                )
                ProgressStatItem(
                    label = "Words",
                    value = progress.wordsLearned.toString(),
                    icon = ProdyIcons.School,
                    color = Color(0xFF2196F3)
                )
            }

            if (progress.isEmpty) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your journey for today is just beginning.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProdyTextTertiaryLight,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun ProgressStatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
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
