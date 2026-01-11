package com.prody.prashant.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.gamification.ChallengeCategory
import com.prody.prashant.domain.gamification.ChallengeDuration
import com.prody.prashant.domain.gamification.DailyChallenge
import com.prody.prashant.domain.gamification.DailyChallengeProgress
import com.prody.prashant.domain.gamification.WeeklyChallenge
import com.prody.prashant.domain.gamification.WeeklyChallengeProgress
import com.prody.prashant.ui.theme.*

/**
 * Challenge UI Components for the Gamification System
 *
 * Components for displaying:
 * - Daily challenge cards with progress tracking
 * - Weekly challenge cards with milestones
 * - Challenge progress indicators
 * - Category-based styling
 */

// =============================================================================
// DAILY CHALLENGE CARD
// =============================================================================

/**
 * Daily challenge card showing progress and rewards.
 *
 * @param challenge The daily challenge definition
 * @param progress Current progress on the challenge
 * @param onChallengeClick Callback when card is clicked
 */
@Composable
fun DailyChallengeCard(
    challenge: DailyChallenge,
    progress: DailyChallengeProgress?,
    modifier: Modifier = Modifier,
    onChallengeClick: (() -> Unit)? = null
) {
    val categoryColor = challenge.category.color
    val isCompleted = progress?.isCompleted == true
    val currentProgress = progress?.progressPercent ?: 0f

    val infiniteTransition = rememberInfiniteTransition(label = "challenge_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onChallengeClick != null) Modifier.clickable { onChallengeClick() } else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                categoryColor.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) categoryColor
                            else categoryColor.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = challenge.category.icon,
                        contentDescription = null,
                        tint = if (isCompleted) Color.White else categoryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Challenge info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = challenge.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            }
                        )

                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Completed",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // XP reward
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldTier.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stars,
                                contentDescription = null,
                                tint = GoldTier,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "+${challenge.xpReward}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldTier
                            )
                        }
                    }

                    if (challenge.streakBonus) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = StreakFire.copy(alpha = pulseAlpha),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Streak bonus",
                                style = MaterialTheme.typography.labelSmall,
                                color = StreakFire.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Progress bar
            if (!isCompleted && progress != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${progress.currentProgress} / ${progress.targetCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor
                        )
                        Text(
                            text = "${(currentProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { currentProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = categoryColor,
                        trackColor = categoryColor.copy(alpha = 0.1f)
                    )
                }
            }

            // Encouragement message
            if (!isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = challenge.encouragementMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

// =============================================================================
// WEEKLY CHALLENGE CARD
// =============================================================================

/**
 * Weekly challenge card with milestone indicators.
 */
@Composable
fun WeeklyChallengeCard(
    challenge: WeeklyChallenge,
    progress: WeeklyChallengeProgress?,
    modifier: Modifier = Modifier,
    onChallengeClick: (() -> Unit)? = null
) {
    val categoryColor = challenge.category.color
    val isCompleted = progress?.isCompleted == true
    val currentProgress = progress?.progressPercent ?: 0f
    val currentMilestone = progress?.currentMilestone ?: 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onChallengeClick != null) Modifier.clickable { onChallengeClick() } else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                categoryColor.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Featured badge
                if (challenge.isFeatured) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = GoldTier.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "FEATURED",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldTier,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Duration badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = categoryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "WEEKLY",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // XP reward
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+${challenge.xpReward} XP",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldTier
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title and description
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) categoryColor
                            else categoryColor.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = challenge.category.icon,
                        contentDescription = null,
                        tint = if (isCompleted) Color.White else categoryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Completed",
                        tint = GoldTier,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar with milestones
            Column {
                LinearProgressIndicator(
                    progress = { currentProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = categoryColor,
                    trackColor = categoryColor.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Milestone indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    challenge.milestones.forEachIndexed { index, milestone ->
                        MilestoneIndicator(
                            title = milestone.title,
                            isReached = currentMilestone > index,
                            isCurrent = currentMilestone == index + 1,
                            color = categoryColor
                        )
                    }
                }
            }

            // Progress text
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${progress?.currentProgress ?: 0} / ${challenge.targetCount}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = categoryColor
                )

                if (challenge.communityTarget > 0 && progress != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Community: ${formatNumber(progress.communityProgress)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneIndicator(
    title: String,
    isReached: Boolean,
    isCurrent: Boolean,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isReached -> color
                        isCurrent -> color.copy(alpha = 0.3f)
                        else -> color.copy(alpha = 0.1f)
                    }
                )
                .then(
                    if (isCurrent) Modifier.border(2.dp, color, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isReached) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// =============================================================================
// COMPACT CHALLENGE CARD
// =============================================================================

/**
 * Compact challenge card for lists or summaries.
 */
@Composable
fun CompactChallengeCard(
    title: String,
    category: ChallengeCategory,
    progress: Float,
    xpReward: Int,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val categoryColor = category.color

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        color = if (isCompleted) {
            categoryColor.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) categoryColor
                        else categoryColor.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Filled.Check else category.icon,
                    contentDescription = null,
                    tint = if (isCompleted) Color.White else categoryColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title and progress
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = categoryColor,
                        trackColor = categoryColor.copy(alpha = 0.1f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // XP badge
            Text(
                text = "+$xpReward",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = GoldTier
            )
        }
    }
}

// =============================================================================
// CHALLENGE SUMMARY CARD
// =============================================================================

/**
 * Summary card showing daily challenge progress overview.
 */
@Composable
fun ChallengeSummaryCard(
    completedToday: Int,
    totalChallenges: Int,
    xpEarnedToday: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val progress = if (totalChallenges > 0) {
        completedToday.toFloat() / totalChallenges
    } else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Assignment,
                        contentDescription = null,
                        tint = ChallengeActive,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Today's Challenges",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "$completedToday / $totalChallenges",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = ChallengeActive,
                trackColor = ChallengeActive.copy(alpha = 0.1f)
            )

            if (xpEarnedToday > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+$xpEarnedToday XP earned today",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldTier
                    )
                }
            }
        }
    }
}

// =============================================================================
// CHALLENGE COMPLETION CELEBRATION
// =============================================================================

/**
 * Challenge completion celebration animation.
 */
@Composable
fun ChallengeCompletionCelebration(
    challengeTitle: String,
    xpEarned: Int,
    category: ChallengeCategory,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    var animationPhase by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        animationPhase = 1
        kotlinx.coroutines.delay(2500)
        animationPhase = 2
        kotlinx.coroutines.delay(500)
        onDismiss()
    }

    val scale by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0f
            1 -> 1f
            else -> 0.8f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "celebration_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0f
            1 -> 1f
            else -> 0f
        },
        animationSpec = tween(300),
        label = "celebration_alpha"
    )

    val categoryColor = category.color

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Completed badge
                Text(
                    text = "CHALLENGE COMPLETE!",
                    style = MaterialTheme.typography.labelMedium,
                    color = ChallengeCompleted,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(categoryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Challenge title
                Text(
                    text = challengeTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // XP reward
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldTier.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Stars,
                            contentDescription = null,
                            tint = GoldTier,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "+$xpEarned XP",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldTier
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// HELPER EXTENSIONS
// =============================================================================

/**
 * Get icon for challenge category.
 */
val ChallengeCategory.icon: ImageVector
    get() = when (this) {
        ChallengeCategory.JOURNALING -> Icons.Filled.Book
        ChallengeCategory.VOCABULARY -> Icons.Filled.School
        ChallengeCategory.FUTURE_SELF -> Icons.Filled.Schedule
        ChallengeCategory.WISDOM -> Icons.Filled.Psychology
        ChallengeCategory.BLOOM -> Icons.Filled.LocalFlorist
        ChallengeCategory.STREAK -> Icons.Filled.LocalFireDepartment
        ChallengeCategory.MIXED -> Icons.Filled.Dashboard
        ChallengeCategory.COMMUNITY -> Icons.Filled.Groups
    }

/**
 * Get color for challenge category.
 */
val ChallengeCategory.color: Color
    get() = when (this) {
        ChallengeCategory.JOURNALING -> MoodCalm
        ChallengeCategory.VOCABULARY -> MoodMotivated
        ChallengeCategory.FUTURE_SELF -> FutureCategoryGoal
        ChallengeCategory.WISDOM -> WisdomPerspective
        ChallengeCategory.BLOOM -> MoodGrateful
        ChallengeCategory.STREAK -> StreakFire
        ChallengeCategory.MIXED -> ProdyAccentGreen
        ChallengeCategory.COMMUNITY -> ProdyInfo
    }

private fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}
