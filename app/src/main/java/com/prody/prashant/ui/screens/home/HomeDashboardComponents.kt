package com.prody.prashant.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Staggered Entrance Animation Utility
 *
 * Applies a premium fade-in and slide-up animation to components.
 * Uses graphicsLayer to avoid recomposition during animation frames.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    content: @Composable () -> Unit
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(index * 100L)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = Modifier.graphicsLayer {
            alpha = animatedProgress.value
            translationY = (1f - animatedProgress.value) * 50f
        }
    ) {
        content()
    }
}

/**
 * Next Action Card - Suggests the most relevant next step for the user.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Edit to ProdyForestGreen
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyIcons.School to ProdyWarmAmber
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.SendIcon to Color(0xFF9C27B0)
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb to ProdyInfo
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents to ProdyWarmAmber
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
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
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = color.copy(alpha = 0.8f)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = color,
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
    ProdyCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = ProdySurfaceLight
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Today's Progress",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = ProdyTextSecondaryLight
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStat(
                    value = progress.pointsEarned.toString(),
                    label = "XP Gained",
                    color = ProdyWarmAmber
                )
                ProgressStat(
                    value = progress.journalEntries.toString(),
                    label = "Entries",
                    color = ProdyForestGreen
                )
                ProgressStat(
                    value = progress.wordsLearned.toString(),
                    label = "Words",
                    color = ProdyInfo
                )
            }

            if (progress.journalEntries > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = ProdyForestGreen,
                    trackColor = ProdyForestGreen.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
private fun ProgressStat(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
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
 * Seed Status Card - Visualizes the Seed -> Bloom progress.
 */
@Composable
fun SeedStatusCard(
    seed: SeedEntity?,
    modifier: Modifier = Modifier
) {
    val progress = when (seed?.state) {
        "planted" -> 0.3f
        "growing" -> 0.7f
        "bloomed" -> 1.0f
        else -> 0f
    }

    val statusText = when (seed?.state) {
        "planted" -> "Your seed is taking root"
        "growing" -> "Keep nurturing your growth"
        "bloomed" -> "A beautiful bloom of wisdom"
        else -> "Ready to plant today's seed?"
    }

    ProdyCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = ProdySurfaceLight
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
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
                    imageVector = if (progress >= 1f) ProdyIcons.Spa else Icons.Default.Park,
                    contentDescription = null,
                    tint = ProdyForestGreen
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = ProdyForestGreen,
                    trackColor = ProdyForestGreen.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun MoodTrendSection(moodData: List<Float>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Mood Trend",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = ProdyTextPrimaryLight
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProdyCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            backgroundColor = ProdySurfaceLight
        ) {
            MoodChart(data = moodData, modifier = Modifier.padding(24.dp))
        }
    }
}

@Composable
fun MoodChart(data: List<Float>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Continue journaling to see your trend",
                style = MaterialTheme.typography.bodySmall,
                color = ProdyTextSecondaryLight
            )
        }
        return
    }

    Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val path = Path()
                val width = size.width
                val height = size.height
                val stepX = if (data.size > 1) width / (data.size - 1) else 0f

                val points = data.mapIndexed { index, value ->
                    val x = index * stepX
                    val y = height - ((value - 1) / 4f) * height
                    Offset(x, y)
                }

                path.reset()
                if (points.isNotEmpty()) {
                    path.moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val p0 = points[i - 1]
                        val p1 = points[i]
                        val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2, p0.y)
                        val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2, p1.y)
                        path.cubicTo(
                            controlPoint1.x, controlPoint1.y,
                            controlPoint2.x, controlPoint2.y,
                            p1.x, p1.y
                        )
                    }
                }

                onDrawBehind {
                    if (data.isEmpty()) return@onDrawBehind

                    drawPath(
                        path = path,
                        color = ProdyForestGreen,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    points.forEach { point ->
                        drawCircle(
                            color = Color.White,
                            radius = 6.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = ProdyForestGreen,
                            radius = 4.dp.toPx(),
                            center = point
                        )
                    }
                }
            }
    )
}
