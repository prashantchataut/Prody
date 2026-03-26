package com.prody.prashant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * ================================================================================================
 * ACTIVE PROGRESS UI COMPONENTS
 * ================================================================================================
 *
 * These components visualize the user's immediate goals and achievements.
 * They bridge the gap between backend logic and premium UI experience.
 */

// ================================================================================================
// NEXT ACTION CARD
// ================================================================================================

/**
 * Displays a contextual suggestion for the user's next mindful action.
 *
 * @param nextAction The action data from ActiveProgressService
 * @param onClick Callback when the card is clicked
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actionColor = getActionTypeColor(nextAction.type)
    val actionIcon = getActionTypeIcon(nextAction.type)

    ProdyNotificationCard(
        accentColor = actionColor,
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.md)
        ) {
            // Action Icon
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
                    modifier = Modifier.size(24.dp)
                )
            }

            // Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "UP NEXT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = actionColor,
                    letterSpacing = 1.sp
                )
                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Arrow
            Icon(
                imageVector = ProdyIcons.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ================================================================================================
// TODAY'S PROGRESS CARD
// ================================================================================================

/**
 * Displays a summary of the user's activity for the current day.
 */
@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "TODAY'S MOMENTUM",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (progress.isEmpty) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your journey for today starts here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProgressStatItem(
                        value = progress.journalEntries.toString(),
                        label = "Entries",
                        icon = ProdyIcons.EditNote,
                        color = ProdyForestGreen
                    )

                    VerticalDivider(
                        modifier = Modifier.height(40.dp).align(Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    ProgressStatItem(
                        value = progress.wordsLearned.toString(),
                        label = "Words",
                        icon = ProdyIcons.School,
                        color = ProdyWarmAmber
                    )

                    VerticalDivider(
                        modifier = Modifier.height(40.dp).align(Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    ProgressStatItem(
                        value = "+${progress.pointsEarned}",
                        label = "Points",
                        icon = ProdyIcons.Stars,
                        color = ProdyInfo
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressStatItem(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ================================================================================================
// HELPERS
// ================================================================================================

private fun getActionTypeColor(type: NextActionType): Color {
    return when (type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyForestGreen
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyWarmAmber
        NextActionType.WRITE_FUTURE_MESSAGE -> Color(0xFF8B5CF6) // Future Purple
        NextActionType.REFLECT_ON_QUOTE -> ProdyInfo
        NextActionType.COMPLETE_CHALLENGE -> Color(0xFFF59E0B) // Gold
    }
}

private fun getActionTypeIcon(type: NextActionType): ImageVector {
    return when (type) {
        NextActionType.START_JOURNAL -> ProdyIcons.EditNote
        NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.HistoryEdu
        NextActionType.REVIEW_WORDS -> ProdyIcons.CheckCircle
        NextActionType.LEARN_WORD -> ProdyIcons.School
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents
    }
}
