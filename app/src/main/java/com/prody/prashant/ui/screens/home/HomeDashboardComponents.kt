package com.prody.prashant.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.components.ProdyClickableCard
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * High-performance staggered entrance animation for dashboard items.
 * Uses graphicsLayer to defer animation updates to the draw phase,
 * preventing parent recompositions during the animation.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    delayPerItem: Int = 80,
    content: @Composable () -> Unit
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(index * delayPerItem.toLong())
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
            val progress = animatable.value
            alpha = progress
            translationY = (1f - progress) * 50.dp.toPx()
            scaleX = 0.95f + (progress * 0.05f)
            scaleY = 0.95f + (progress * 0.05f)
        }
    ) {
        content()
    }
}

/**
 * NextActionCard - Intelligent suggestion card.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: (NextActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val actionColor = when(nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyForestGreen
        NextActionType.REFLECT_ON_QUOTE -> ProdyWarmAmber
        else -> ProdyPrimary
    }

    ProdyClickableCard(
        onClick = { onClick(nextAction.type) },
        modifier = modifier.fillMaxWidth(),
        backgroundColor = actionColor.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(actionColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNextActionIcon(nextAction.type),
                    contentDescription = null,
                    tint = actionColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nextAction.title,
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = actionColor
                    )
                )
                Text(
                    text = nextAction.subtitle,
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontSize = 13.sp,
                        color = ProdyTextSecondaryLight
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = ProdyIcons.ArrowForward,
                contentDescription = null,
                tint = actionColor.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * TodayProgressCard - Visual summary of today's achievements.
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
            Text(
                text = "TODAY'S PROGRESS",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = ProdyTextSecondaryLight,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressMetric(
                    label = "XP Gained",
                    value = progress.pointsEarned.toString(),
                    icon = ProdyIcons.Star,
                    color = ProdyWarmAmber
                )
                ProgressMetric(
                    label = "Journal",
                    value = if (progress.journalEntries > 0) "Done" else "Pending",
                    icon = ProdyIcons.Edit,
                    color = ProdyForestGreen
                )
                ProgressMetric(
                    label = "Activity",
                    value = "${progress.journalEntries + progress.wordsLearned}", // Derived activity score
                    icon = ProdyIcons.CheckCircle,
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
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ProdyTextPrimaryLight
            )
        )
        Text(
            text = label,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontSize = 11.sp,
                color = ProdyTextSecondaryLight
            )
        )
    }
}

/**
 * SeedStatusCard - Tracks the Seed -> Bloom progress.
 */
@Composable
fun SeedStatusCard(
    seed: SeedEntity?,
    modifier: Modifier = Modifier
) {
    if (seed == null) return

    val progress = when(seed.state) {
        "planted" -> 0.3f
        "growing" -> 0.7f
        "bloomed" -> 1.0f
        else -> 0.1f
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
                Text(
                    text = when(seed.state) {
                        "bloomed" -> "🌸"
                        "growing" -> "🌱"
                        else -> "🌱"
                    },
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (seed.state == "bloomed") "Your seed has bloomed!" else "Growing your daily seed",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
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

private fun getNextActionIcon(type: NextActionType): androidx.compose.ui.graphics.vector.ImageVector {
    return when(type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Edit
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> ProdyIcons.SelfImprovement
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Psychology
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents
    }
}
