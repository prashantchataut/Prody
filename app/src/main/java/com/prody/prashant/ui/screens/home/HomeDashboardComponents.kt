package com.prody.prashant.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * High-performance entrance animation utility.
 * Defers state reads to the draw phase using graphicsLayer.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    content: @Composable () -> Unit
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(index * 50L)
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
            scaleX = 0.95f + (value * 0.05f)
            scaleY = 0.95f + (value * 0.05f)
        }
    ) {
        content()
    }
}

/**
 * Contextual suggestion card.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, color) = remember(nextAction.type) {
        when (nextAction.type) {
            NextActionType.START_JOURNAL -> ProdyIcons.Edit to ProdyForestGreen
            NextActionType.FOLLOW_UP_JOURNAL -> Icons.Default.History to ProdyForestGreen
            NextActionType.REVIEW_WORDS -> ProdyIcons.School to ProdyWarmAmber
            NextActionType.LEARN_WORD -> ProdyIcons.Book to ProdyWarmAmber
            NextActionType.WRITE_FUTURE_MESSAGE -> Icons.Default.Send to Color(0xFF9C27B0)
            NextActionType.REFLECT_ON_QUOTE -> Icons.Default.Lightbulb to ProdyWarmAmber
            NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents to ProdyInfo
        }
    }

    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = color.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
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
                    text = "SUGGESTED FOR YOU",
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
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
                    color = ProdyTextSecondaryLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = ProdyIcons.ArrowForward,
                contentDescription = null,
                tint = color.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * Summary of today's activities.
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
                text = "TODAY'S PROGRESS",
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
                ProgressItem(
                    value = progress.pointsEarned.toString(),
                    label = "XP Gained",
                    icon = Icons.Default.Star,
                    color = ProdyWarmAmber
                )
                ProgressItem(
                    value = progress.wordsLearned.toString(),
                    label = "Words",
                    icon = ProdyIcons.School,
                    color = ProdyForestGreen
                )
                ProgressItem(
                    value = progress.journalEntries.toString(),
                    label = "Entries",
                    icon = ProdyIcons.Edit,
                    color = ProdyInfo
                )
            }

            if (progress.isEmpty) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "You haven't started your journey today. Small steps lead to big growth.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProdyTextSecondaryLight,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun ProgressItem(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
 * Seed -> Bloom status card.
 */
@Composable
fun SeedStatusCard(
    seed: SeedEntity?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (seed == null) return

    val progress = when (seed.state.lowercase()) {
        "planted" -> 0.3f
        "growing" -> 0.7f
        "bloomed" -> 1.0f
        else -> 0.1f
    }

    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    imageVector = if (progress >= 1f) Icons.Default.AutoAwesome else Icons.Default.Eco,
                    contentDescription = null,
                    tint = ProdyForestGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (progress >= 1f) "BLOOMED" else "GROWING SEED",
                    style = MaterialTheme.typography.labelSmall,
                    color = ProdyForestGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = seed.seedContent,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ProdyTextPrimaryLight
                )

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = ProdyForestGreen,
                    trackColor = ProdyForestGreen.copy(alpha = 0.1f)
                )
            }
        }
    }
}
