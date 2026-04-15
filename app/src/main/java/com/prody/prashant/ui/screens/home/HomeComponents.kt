package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * Next Action Card - A high-visibility card that suggests the most impactful next step
 * for the user based on their current behavior and progress.
 */
@Composable
fun NextActionCard(
    action: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (action.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Edit
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyIcons.School
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents
    }

    val iconContainerColor = when (action.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyForestGreen.copy(alpha = 0.1f)
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyInfo.copy(alpha = 0.1f)
        NextActionType.WRITE_FUTURE_MESSAGE -> Color(0xFF9C27B0).copy(alpha = 0.1f)
        else -> ProdyWarmAmber.copy(alpha = 0.1f)
    }

    val iconTint = when (action.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyForestGreen
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyInfo
        NextActionType.WRITE_FUTURE_MESSAGE -> Color(0xFF9C27B0)
        else -> ProdyWarmAmber
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
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
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NEXT ACTION",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = ProdyTextSecondaryLight,
                    letterSpacing = 1.sp
                )
                Text(
                    text = action.title,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ProdyTextPrimaryLight
                )
                Text(
                    text = action.subtitle,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = ProdyTextSecondaryLight
                )
            }

            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = ProdyTextTertiaryLight,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Today Progress Card - A summary of user achievements for the current day.
 */
@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "TODAY'S PROGRESS",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = ProdyForestGreen,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStat(
                    value = progress.journalEntries.toString(),
                    label = "Entries",
                    icon = ProdyIcons.Edit,
                    color = ProdyForestGreen
                )
                ProgressStat(
                    value = progress.wordsLearned.toString(),
                    label = "Words",
                    icon = ProdyIcons.School,
                    color = ProdyWarmAmber
                )
                ProgressStat(
                    value = progress.pointsEarned.toString(),
                    label = "Points",
                    icon = ProdyIcons.Star,
                    color = ProdyInfo
                )
            }

            if (!progress.isEmpty) {
                // Performance Optimization: Calculate real daily goal progress (1 entry + 1 word)
                val dailyGoalProgress = remember(progress) {
                    val entryProgress = if (progress.journalEntries > 0) 0.5f else 0f
                    val wordProgress = if (progress.wordsLearned > 0) 0.5f else 0f
                    (entryProgress + wordProgress).coerceIn(0f, 1f)
                }

                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { dailyGoalProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = ProdyForestGreen,
                    trackColor = ProdyForestGreen.copy(alpha = 0.1f)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No activity yet today. Start with a journal entry!",
                    fontFamily = PoppinsFamily,
                    fontSize = 12.sp,
                    color = ProdyTextSecondaryLight,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ProgressStat(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ProdyTextPrimaryLight
        )
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = ProdyTextSecondaryLight
        )
    }
}
