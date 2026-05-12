package com.prody.prashant.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.components.ProdySmallCircularProgress
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * StaggeredEntrance - High-performance entrance animation for dashboard cards.
 *
 * Performance features:
 * - Uses [Modifier.graphicsLayer] to avoid parent recompositions during animation frames.
 * - Accesses [Animatable.value] within the draw block for maximum efficiency.
 * - Uses a [delay] based on index for the staggered effect.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    content: @Composable () -> Unit
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(index * 100L)
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
            alpha = animatable.value
            translationY = (1f - animatable.value) * 50.dp.toPx()
        }
    ) {
        content()
    }
}

/**
 * NextActionCard - A contextual suggestion based on user behavior.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, color) = getNextActionStyle(nextAction.type)

    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(ProdyDesignTokens.Spacing.cardPadding),
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

            Spacer(modifier = Modifier.width(ProdyDesignTokens.Spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * TodayProgressCard - A summary of today's mindfulness activity.
 */
@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(ProdyDesignTokens.Spacing.cardPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S PROGRESS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyDesignTokens.SemanticColors.info,
                    letterSpacing = 1.2.sp
                )

                if (progress.pointsEarned > 0) {
                    Surface(
                        color = ProdyDesignTokens.SemanticColors.goldPrimary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "+${progress.pointsEarned} XP",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ProdyDesignTokens.SemanticColors.goldPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(ProdyDesignTokens.Spacing.medium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressMetric(
                    label = "Journals",
                    value = progress.journalEntries.toString(),
                    icon = ProdyIcons.Edit,
                    color = ProdyDesignTokens.SemanticColors.success
                )
                ProgressMetric(
                    label = "Words",
                    value = progress.wordsLearned.toString(),
                    icon = ProdyIcons.School,
                    color = ProdyDesignTokens.SemanticColors.warning
                )
                ProgressMetric(
                    label = "Streak",
                    value = progress.currentStreak.toString(),
                    icon = ProdyIcons.LocalFireDepartment,
                    color = ProdyDesignTokens.SemanticColors.streakPrimary
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * SeedStatusCard - Derived visual progress for the daily seed.
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
        else -> 0.1f
    }

    val statusText = when (seed.state) {
        "planted" -> "Seed planted"
        "growing" -> "Wisdom growing"
        "bloomed" -> "Full bloom"
        else -> "New seed"
    }

    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = ProdyDesignTokens.SemanticColors.success.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier.padding(ProdyDesignTokens.Spacing.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProdySmallCircularProgress(
                progress = progress,
                size = 40.dp,
                strokeWidth = 4.dp,
                progressColor = ProdyDesignTokens.SemanticColors.success
            )

            Spacer(modifier = Modifier.width(ProdyDesignTokens.Spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = seed.seedContent,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = ProdyDesignTokens.SemanticColors.success
                )
            }

            Icon(
                imageVector = ProdyIcons.Spa,
                contentDescription = null,
                tint = ProdyDesignTokens.SemanticColors.success.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun getNextActionStyle(type: NextActionType): Pair<ImageVector, Color> {
    return when (type) {
        NextActionType.START_JOURNAL -> ProdyIcons.Edit to ProdyDesignTokens.SemanticColors.success
        NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.History to ProdyDesignTokens.SemanticColors.info
        NextActionType.REVIEW_WORDS -> ProdyIcons.School to ProdyDesignTokens.SemanticColors.warning
        NextActionType.LEARN_WORD -> ProdyIcons.Book to ProdyDesignTokens.SemanticColors.success
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.SendIcon to Color(0xFF9C27B0)
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.FormatQuote to ProdyDesignTokens.SemanticColors.warning
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents to ProdyDesignTokens.SemanticColors.goldPrimary
    }
}
