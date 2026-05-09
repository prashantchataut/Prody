package com.prody.prashant.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.components.ProdyClickableCard
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * High-performance entrance animation for dashboard components.
 * Uses graphicsLayer to avoid parent recomposition during animation.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    content: @Composable () -> Unit
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val delay = (index * 80L)
        kotlinx.coroutines.delay(delay)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = Modifier.graphicsLayer {
            val value = animatable.value
            alpha = value
            translationY = (1f - value) * 50f
        }
    ) {
        content()
    }
}

/**
 * Premium Next Action card - Contextual suggestion based on user behavior.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProdyClickableCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        backgroundColor = ProdySurfaceLight
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ProdyForestGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (nextAction.type) {
                        com.prody.prashant.domain.progress.NextActionType.START_JOURNAL,
                        com.prody.prashant.domain.progress.NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Edit
                        com.prody.prashant.domain.progress.NextActionType.LEARN_WORD,
                        com.prody.prashant.domain.progress.NextActionType.REVIEW_WORDS,
                        com.prody.prashant.domain.progress.NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb
                        else -> ProdyIcons.ArrowForward
                    },
                    contentDescription = null,
                    tint = ProdyForestGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyTextPrimaryLight
                )
                Text(
                    text = nextAction.subtitle, // Memory optimization: NextAction uses subtitle for description
                    style = MaterialTheme.typography.bodySmall,
                    color = ProdyTextSecondaryLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = ProdyTextSecondaryLight.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Today's Progress Card - summary of today's points and journaling status.
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = ProdyTextSecondaryLight
                    )
                    Text(
                        text = "${progress.pointsEarned} XP Earned",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = NotificationAchievement // Brand color for progress
                    )
                }

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { (progress.pointsEarned.toFloat() / 500f).coerceIn(0f, 1f) },
                        modifier = Modifier.size(48.dp),
                        color = NotificationAchievement,
                        trackColor = NotificationAchievement.copy(alpha = 0.1f),
                        strokeWidth = 4.dp
                    )
                    Icon(
                        imageVector = ProdyIcons.Bolt,
                        contentDescription = null,
                        tint = NotificationAchievement,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActivityChip(
                    label = "Journal",
                    completed = progress.journalEntries > 0,
                    modifier = Modifier.weight(1f)
                )
                ActivityChip(
                    label = "Wisdom",
                    completed = progress.wordsLearned > 0,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActivityChip(
    label: String,
    completed: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (completed) ProdyForestGreen else ProdyTextSecondaryLight.copy(alpha = 0.1f)
    val contentColor = if (completed) Color.White else ProdyTextSecondaryLight

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (completed) {
                Icon(
                    imageVector = ProdyIcons.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

/**
 * Seed Status Card - visual progress of the daily growth seed.
 */
@Composable
fun SeedStatusCard(
    seed: SeedEntity?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Memory: Seed status derived from enum as growthProgress is not a numeric property
    val progress = when (seed?.state) {
        "planted" -> 0.3f
        "growing" -> 0.7f
        "bloomed" -> 1.0f
        else -> 0f
    }

    val statusText = when (seed?.state) {
        "planted" -> "Your seed is taking root"
        "growing" -> "It's starting to sprout!"
        "bloomed" -> "Ready to bloom!"
        else -> "Ready for a new start"
    }

    ProdyClickableCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        backgroundColor = ProdySurfaceLight
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ProdyWarmAmber.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (progress >= 1f) ProdyIcons.LocalFlorist else ProdyIcons.Grass,
                    contentDescription = null,
                    tint = ProdyWarmAmber,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Growth Seed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyTextPrimaryLight
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = ProdyTextSecondaryLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ProdyWarmAmber,
                    trackColor = ProdyWarmAmber.copy(alpha = 0.1f)
                )
            }
        }
    }
}
