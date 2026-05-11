package com.prody.prashant.ui.screens.home

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.components.ProdyClickableCard
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * Performance-optimized entrance animation for dashboard components.
 * Accesses animation value inside graphicsLayer to avoid parent recomposition.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    content: @Composable () -> Unit
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 100L)
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
            val progress = animatable.value
            alpha = progress
            translationY = (1f - progress) * 50f
        }
    ) {
        content()
    }
}

/**
 * Contextual action card suggested by ActiveProgressService.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL ->
            ProdyIcons.Edit to ProdyForestGreen
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD ->
            ProdyIcons.School to ProdyWarmAmber
        NextActionType.WRITE_FUTURE_MESSAGE ->
            ProdyIcons.Send to Color(0xFF9C27B0)
        NextActionType.REFLECT_ON_QUOTE ->
            ProdyIcons.Lightbulb to ProdyWarmAmber
        NextActionType.COMPLETE_CHALLENGE ->
            ProdyIcons.EmojiEvents to ProdyInfo
    }

    ProdyClickableCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        backgroundColor = color.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NEXT STEP",
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
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
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = color.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * Summary card for today's active progress.
 */
@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        backgroundColor = ProdySurfaceLight
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "TODAY'S GROWTH",
                style = MaterialTheme.typography.labelSmall,
                color = ProdyTextSecondaryLight,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStat(
                    label = "Journal",
                    value = progress.journalEntries.toString(),
                    isCompleted = progress.journalEntries > 0,
                    color = ProdyForestGreen
                )
                ProgressStat(
                    label = "Words",
                    value = progress.wordsLearned.toString(),
                    isCompleted = progress.wordsLearned > 0,
                    color = ProdyWarmAmber
                )
                ProgressStat(
                    label = "Today's XP",
                    value = progress.pointsEarned.toString(),
                    isCompleted = progress.pointsEarned > 0,
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
    isCompleted: Boolean,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isCompleted) color.copy(alpha = 0.15f) else ProdyOutlineLight.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isCompleted) ProdyIcons.Check else ProdyIcons.Add,
                contentDescription = null,
                tint = if (isCompleted) color else ProdyTextTertiaryLight,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
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

/**
 * Visualizes the Seed -> Bloom mechanic status.
 */
@Composable
fun SeedStatusCard(
    seed: SeedEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = when (seed.state) {
        "planted" -> 0.3f
        "growing" -> 0.7f
        "bloomed" -> 1.0f
        else -> 0.3f
    }

    val stateLabel = when (seed.state) {
        "planted" -> "PLANTED"
        "growing" -> "GROWING"
        "bloomed" -> "BLOOMED"
        else -> "PLANTED"
    }

    val color = when (seed.state) {
        "bloomed" -> BloomReady
        "growing" -> BloomGrowing
        else -> SeedDormant
    }

    ProdyClickableCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        backgroundColor = ProdySurfaceLight
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = color,
                    strokeWidth = 3.dp,
                    trackColor = color.copy(alpha = 0.1f)
                )
                Icon(
                    imageVector = if (seed.hasBloomed()) ProdyIcons.LocalFlorist else ProdyIcons.Grass,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "DAILY SEED",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProdyTextSecondaryLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = color.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = stateLabel,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = seed.seedContent,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyTextPrimaryLight
                )
                Text(
                    text = if (seed.hasBloomed()) "Successfully applied in your reflection" else "Use this word in your journal to bloom",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProdyTextSecondaryLight
                )
            }
        }
    }
}
