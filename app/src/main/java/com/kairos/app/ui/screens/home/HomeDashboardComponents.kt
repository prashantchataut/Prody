package com.kairos.app.ui.screens.home

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kairos.app.data.local.entity.SeedEntity
import com.kairos.app.domain.progress.NextAction
import com.kairos.app.domain.progress.NextActionType
import com.kairos.app.domain.progress.TodayProgress
import com.kairos.app.ui.components.KairosCard
import com.kairos.app.ui.components.KairosClickableCard
import com.kairos.app.ui.icons.KairosIcons
import com.kairos.app.ui.theme.*
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
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> KairosIcons.Edit to KairosForestGreen
        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> KairosIcons.School to KairosWarmAmber
        NextActionType.WRITE_FUTURE_MESSAGE -> KairosIcons.Send to Color(0xFF9C27B0)
        NextActionType.REFLECT_ON_QUOTE -> KairosIcons.Lightbulb to KairosWarmAmber
        NextActionType.COMPLETE_CHALLENGE -> KairosIcons.EmojiEvents to KairosWarmAmber
    }

    KairosClickableCard(
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
                    color = KairosTextPrimaryLight
                )
                Text(
                    text = nextAction.subtitle,
                    fontSize = 14.sp,
                    color = KairosTextSecondaryLight,
                    fontFamily = PoppinsFamily
                )
            }

            Icon(
                imageVector = KairosIcons.ChevronRight,
                contentDescription = null,
                tint = KairosTextTertiaryLight
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
    KairosCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = KairosSurfaceLight
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
                    color = KairosTextPrimaryLight
                )

                Surface(
                    color = KairosWarmAmber.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "+${progress.pointsEarned} XP",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = KairosWarmAmber,
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
                    icon = KairosIcons.Edit
                )
                ProgressItem(
                    label = "Wisdom",
                    isComplete = progress.wordsLearned > 0,
                    icon = KairosIcons.Lightbulb
                )
                ProgressItem(
                    label = "Streaks",
                    isComplete = progress.currentStreak > 0,
                    icon = KairosIcons.LocalFireDepartment
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
                    if (isComplete) KairosForestGreen.copy(alpha = 0.1f)
                    else KairosOutlineLight.copy(alpha = 0.2f)
                )
        ) {
            Icon(
                imageVector = if (isComplete) KairosIcons.Check else icon,
                contentDescription = null,
                tint = if (isComplete) KairosForestGreen else KairosTextTertiaryLight,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = PoppinsFamily,
            color = if (isComplete) KairosTextPrimaryLight else KairosTextSecondaryLight
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
        progress >= 1.0f -> KairosForestGreen
        progress >= 0.7f -> KairosWarmAmber
        else -> KairosInfo
    }

    KairosCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = KairosSurfaceLight
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
                    color = KairosTextSecondaryLight,
                    fontFamily = PoppinsFamily
                )
                Text(
                    text = if (progress >= 1.0f) "Fully Bloomed!" else "Growing your wisdom",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = KairosTextPrimaryLight
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
