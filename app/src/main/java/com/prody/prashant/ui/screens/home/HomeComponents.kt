package com.prody.prashant.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * Next Action Card - Dynamic suggestion based on user context.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyForestGreen.copy(alpha = 0.1f)
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyWarmAmber.copy(alpha = 0.1f)
        NextActionType.WRITE_FUTURE_MESSAGE -> Color(0xFF9C27B0).copy(alpha = 0.1f)
        else -> ProdyInfo.copy(alpha = 0.1f)
    }

    val accentColor = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyForestGreen
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyWarmAmber
        NextActionType.WRITE_FUTURE_MESSAGE -> Color(0xFF9C27B0)
        else -> ProdyInfo
    }

    val icon = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Edit
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyIcons.School
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb
        else -> ProdyIcons.AutoAwesome
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nextAction.title,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ProdyTextPrimaryLight
                )
                Text(
                    text = nextAction.subtitle,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = ProdyTextSecondaryLight
                )
            }

            Icon(
                imageVector = ProdyIcons.ArrowForward,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Today's Progress Card - Summary of today's achievements.
 */
@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        color = ProdySurfaceLight,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, ProdyOutlineLight)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "TODAY'S PROGRESS",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = ProdyTextSecondaryLight,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressMetric(
                    label = "Journal",
                    value = progress.journalEntries.toString(),
                    icon = ProdyIcons.Edit,
                    color = ProdyForestGreen
                )
                ProgressMetric(
                    label = "Words",
                    value = progress.wordsLearned.toString(),
                    icon = ProdyIcons.School,
                    color = ProdyWarmAmber
                )
                ProgressMetric(
                    label = "Points",
                    value = progress.pointsEarned.toString(),
                    icon = ProdyIcons.EmojiEvents,
                    color = ProdyInfo
                )
            }
        }
    }
}

@Composable
private fun ProgressMetric(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = ProdyTextPrimaryLight
        )
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = ProdyTextSecondaryLight
        )
    }
}
