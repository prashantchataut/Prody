package com.prody.prashant.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.animation.shimmerEffect
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.components.ProdyClickableCard
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * High-performance animation utility for dashboard cards.
 * Uses staggered delays and graphicsLayer for smoothness.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    content: @Composable () -> Unit
) {
    val animatedState = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(index * 100L)
        animatedState.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = Modifier.graphicsLayer {
            alpha = animatedState.value
            translationY = (1f - animatedState.value) * 50f
        }
    ) {
        content()
    }
}

/**
 * Suggested next action card based on user behavior.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit
) {
    val (icon, color) = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Edit to ProdyForestGreen
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyIcons.School to ProdyWarmAmber
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send to Color(0xFF9C27B0)
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb to ProdyWarmAmber
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents to ProdyWarmAmber
    }

    ProdyClickableCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = color.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
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
                    text = "Suggested Action",
                    fontSize = 12.sp,
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PoppinsFamily
                )
                Text(
                    text = nextAction.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = ProdyTextPrimaryLight
                )
                Text(
                    text = nextAction.subtitle,
                    fontSize = 14.sp,
                    color = ProdyTextSecondaryLight,
                    fontFamily = PoppinsFamily
                )
            }

            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = ProdyTextTertiaryLight
            )
        }
    }
}

/**
 * Today's progress summary card showing XP and completion status.
 */
@Composable
fun TodayProgressCard(
    progress: TodayProgress
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = ProdySurfaceLight
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Progress",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = ProdyTextPrimaryLight
                )

                Surface(
                    color = ProdyWarmAmber.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "+${progress.pointsEarned} XP",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = ProdyWarmAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressItem(
                    label = "Journal",
                    isComplete = progress.journalEntries > 0,
                    icon = ProdyIcons.Edit
                )
                ProgressItem(
                    label = "Wisdom",
                    isComplete = progress.wordsLearned > 0,
                    icon = ProdyIcons.Lightbulb
                )
                ProgressItem(
                    label = "Streaks",
                    isComplete = progress.currentStreak > 0,
                    icon = ProdyIcons.LocalFireDepartment
                )
            }
        }
    }
}

@Composable
private fun ProgressItem(
    label: String,
    isComplete: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isComplete) ProdyForestGreen.copy(alpha = 0.1f)
                    else ProdyOutlineLight.copy(alpha = 0.2f)
                )
        ) {
            Icon(
                imageVector = if (isComplete) ProdyIcons.Check else icon,
                contentDescription = null,
                tint = if (isComplete) ProdyForestGreen else ProdyTextTertiaryLight,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = PoppinsFamily,
            color = if (isComplete) ProdyTextPrimaryLight else ProdyTextSecondaryLight
        )
    }
}

/**
 * Shimmering skeleton loader for the Home Screen.
 * Provides a premium, perceived-speed improvement over standard progress indicators.
 */
@Composable
fun HomeSkeletonLoader() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header Skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Greeting Skeleton
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Overview Cards Skeleton
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shimmerEffect()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Card Skeleton (Dual Streak / Next Action)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(20.dp))
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Today Progress Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .shimmerEffect()
        )
    }
}

/**
 * Seed to Bloom status card.
 */
@Composable
fun SeedStatusCard(
    seed: SeedEntity
) {
    // derive progress from state
    val progress = when (seed.state.lowercase()) {
        "planted" -> 0.3f
        "growing" -> 0.7f
        "bloomed" -> 1.0f
        else -> 0.1f
    }

    val color = when {
        progress >= 1.0f -> ProdyForestGreen
        progress >= 0.7f -> ProdyWarmAmber
        else -> ProdyInfo
    }

    ProdyCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = ProdySurfaceLight
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your Daily Seed",
                    fontSize = 12.sp,
                    color = ProdyTextSecondaryLight,
                    fontFamily = PoppinsFamily
                )
                Text(
                    text = if (progress >= 1.0f) "Fully Bloomed!" else "Growing your wisdom",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = ProdyTextPrimaryLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = color,
                    trackColor = color.copy(alpha = 0.1f)
                )
            }
        }
    }
}

/**
 * Mood trend section showing emotional progress over time.
 */
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

        // Chart Card
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

/**
 * High-performance Mood Chart using drawWithCache to optimize path rendering.
 * Deferring state reads and using graphicsLayer prevents parent recompositions.
 */
@Composable
fun MoodChart(data: List<Float>, modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val path = Path()

                val width = size.width
                val height = size.height
                val stepX = if (data.size > 1) width / (data.size - 1) else 0f

                // Normalize data to height (1-5 scale)
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

                    // Draw Points
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
            .graphicsLayer {
                // Ensure chart updates are isolated to drawing layer
            }
    )
}
