package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.domain.intelligence.SurfacedMemory
import com.prody.prashant.ui.components.ProdyClickableCard
import com.prody.prashant.ui.components.ProdyNotificationCard
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * High-fidelity components for the Home Screen.
 * Part of the Active Progress Layer and Soul Layer integration.
 */

@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyForestGreen
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyWarmAmber
        NextActionType.WRITE_FUTURE_MESSAGE -> Color(0xFF9C27B0)
        NextActionType.REFLECT_ON_QUOTE -> ProdyInfo
        else -> ProdyForestGreen
    }

    val icon = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Edit
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyIcons.School
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb
        else -> ProdyIcons.Bolt
    }

    ProdyNotificationCard(
        accentColor = accentColor,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = ProdyTextPrimaryLight
                )
                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ProdyTextSecondaryLight,
                    fontFamily = PoppinsFamily
                )
            }
            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = ProdyOutlineLight
            )
        }
    }
}

@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        color = ProdySurfaceLight,
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "TODAY'S PROGRESS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = ProdyTextSecondaryLight
            )
            Spacer(modifier = Modifier.height(16.dp))
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
                    label = "Points",
                    value = progress.pointsEarned.toString(),
                    icon = ProdyIcons.Star,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }
}

@Composable
private fun ProgressItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = PoppinsFamily,
            color = ProdyTextPrimaryLight
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ProdyTextSecondaryLight,
            fontFamily = PoppinsFamily
        )
    }
}

@Composable
fun SurfacedMemoryCard(
    memory: SurfacedMemory,
    onRevisitClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProdyClickableCard(
        onClick = onRevisitClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        backgroundColor = ProdyForestGreen.copy(alpha = 0.05f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.AutoAwesome,
                    contentDescription = null,
                    tint = ProdyForestGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = memory.surfaceReason,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyForestGreen,
                    fontFamily = PoppinsFamily
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = memory.memory.preview,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                fontFamily = PoppinsFamily,
                lineHeight = 22.sp,
                color = ProdyTextPrimaryLight
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Written on ${memory.memory.date}",
                style = MaterialTheme.typography.bodySmall,
                color = ProdyTextSecondaryLight,
                fontFamily = PoppinsFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = memory.dismissText,
                        color = ProdyTextSecondaryLight,
                        fontFamily = PoppinsFamily
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onRevisitClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ProdyForestGreen)
                ) {
                    Text(
                        text = memory.actionText,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
