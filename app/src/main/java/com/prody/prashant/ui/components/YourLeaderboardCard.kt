package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.theme.*
import kotlin.math.*

/**
 * YourLeaderboardCard - Distinct "YOU" card with premium pulse animation
 *
 * This is the user's personal entry in the leaderboard, designed to:
 * - Stand out visually from other entries with animated borders
 * - Create personal investment through "YOU" badge highlighting
 * - Show rank movement trends (up/down/steady)
 * - Display XP progress to next rank
 *
 * Visual Effects:
 * - Rotating gradient border animation
 * - Pulsing glow effect
 * - "YOU" badge with micro-animation
 * - Rank movement indicator
 */

/**
 * Data class for user's leaderboard entry
 */
data class YourLeaderboardData(
    val displayName: String,
    val rank: Int,
    val totalXp: Int,
    val currentStreak: Int,
    val rankChange: RankChange = RankChange.STEADY,
    val percentile: Int = 50, // Top X%
    val xpToNextRank: Int = 0,
    val nextRankXp: Int = 100
)

enum class RankChange {
    UP, DOWN, STEADY
}

/**
 * Full-featured YOUR card for leaderboard with all animations.
 */
@Composable
fun YourLeaderboardCard(
    data: YourLeaderboardData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "your_card")

    // Border rotation animation
    val borderRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "border_rotation"
    )

    // Glow pulse animation
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    // Scale pulse for subtle "breathing" effect
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_pulse"
    )

    // YOU badge bounce
    val badgeBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badge_bounce"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scalePulse)
    ) {
        // Animated glowing border
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .padding(2.dp)
        ) {
            val cornerRadius = 20.dp.toPx()
            val borderWidth = 3.dp.toPx()

            // Calculate gradient angle
            val angleRad = borderRotation * PI.toFloat() / 180f
            val gradientLength = maxOf(size.width, size.height) * 1.5f
            val centerX = size.width / 2
            val centerY = size.height / 2

            val startX = centerX - cos(angleRad) * gradientLength / 2
            val startY = centerY - sin(angleRad) * gradientLength / 2
            val endX = centerX + cos(angleRad) * gradientLength / 2
            val endY = centerY + sin(angleRad) * gradientLength / 2

            // Outer glow
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        ProdyAccentGreen.copy(alpha = 0.3f * glowPulse),
                        ProdyAccentGreenLight.copy(alpha = 0.2f * glowPulse),
                        LeaderboardGold.copy(alpha = 0.15f * glowPulse),
                        ProdyAccentGreen.copy(alpha = 0.3f * glowPulse)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY)
                ),
                cornerRadius = CornerRadius(cornerRadius + 4.dp.toPx()),
                style = Stroke(width = borderWidth + 6.dp.toPx())
            )

            // Main animated border
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        ProdyAccentGreen,
                        ProdyAccentGreenLight,
                        LeaderboardGold.copy(alpha = 0.7f),
                        ProdyAccentGreen
                    ),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY)
                ),
                cornerRadius = CornerRadius(cornerRadius),
                style = Stroke(width = borderWidth)
            )
        }

        // Card content
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            shape = RoundedCornerShape(18.dp),
            color = ProdyAccentGreen.copy(alpha = 0.1f),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank with movement indicator
                RankIndicator(
                    rank = data.rank,
                    rankChange = data.rankChange
                )

                Spacer(modifier = Modifier.width(16.dp))

                // User info and XP progress
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // YOU badge with animation
                        YouBadge(bounce = badgeBounce)

                        Text(
                            text = data.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Percentile indicator
                    Text(
                        text = "Top ${data.percentile}% of all users",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // XP progress to next rank
                    if (data.xpToNextRank > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        XpProgressToNextRank(
                            current = data.nextRankXp - data.xpToNextRank,
                            total = data.nextRankXp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // XP and Streak column
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Total XP
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = formatXp(data.totalXp),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ProdyAccentGreen
                        )
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = ProdyAccentGreen
                        )
                    }

                    // Current streak
                    if (data.currentStreak > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = StreakFire
                            )
                            Text(
                                text = "${data.currentStreak} day streak",
                                style = MaterialTheme.typography.labelSmall,
                                color = StreakFire
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Rank indicator with movement arrow
 */
@Composable
private fun RankIndicator(
    rank: Int,
    rankChange: RankChange
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        // Rank number
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ProdyAccentGreen,
                            ProdyAccentGreenLight
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Movement indicator
        if (rankChange != RankChange.STEADY) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(
                        when (rankChange) {
                            RankChange.UP -> ProdySuccess
                            RankChange.DOWN -> ProdyError
                            RankChange.STEADY -> Color.Transparent
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (rankChange) {
                        RankChange.UP -> Icons.Filled.ArrowUpward
                        RankChange.DOWN -> Icons.Filled.ArrowDownward
                        RankChange.STEADY -> Icons.Filled.Remove
                    },
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * Animated YOU badge
 */
@Composable
private fun YouBadge(bounce: Float) {
    val yOffset = sin(bounce * PI.toFloat()) * 2

    Surface(
        modifier = Modifier.offset(y = yOffset.dp),
        shape = RoundedCornerShape(6.dp),
        color = ProdyAccentGreen
    ) {
        Text(
            text = "YOU",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            letterSpacing = 1.sp
        )
    }
}

/**
 * XP progress bar to next rank
 */
@Composable
private fun XpProgressToNextRank(
    current: Int,
    total: Int
) {
    val progress = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Next rank",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$current / $total XP",
                style = MaterialTheme.typography.labelSmall,
                color = ProdyAccentGreen
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = ProdyAccentGreen,
            trackColor = ProdyAccentGreen.copy(alpha = 0.2f)
        )
    }
}

/**
 * Compact YOUR indicator for inline leaderboard display.
 */
@Composable
fun CompactYouIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "compact_you")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        modifier = modifier.scale(pulse),
        shape = RoundedCornerShape(4.dp),
        color = ProdyAccentGreen
    ) {
        Text(
            text = "YOU",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Sticky bottom bar variant of YOUR card (for always-visible user position)
 */
@Composable
fun StickyYourBar(
    data: YourLeaderboardData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sticky_bar")

    // Border shimmer
    val shimmerPosition by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        color = ProdyAccentGreen
    ) {
        Box {
            // Shimmer overlay
            Canvas(modifier = Modifier.matchParentSize()) {
                val shimmerWidth = size.width * 0.3f
                val shimmerX = shimmerPosition * size.width

                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        startX = shimmerX - shimmerWidth / 2,
                        endX = shimmerX + shimmerWidth / 2
                    )
                )
            }

            // Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // YOU badge + Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "YOU",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Text(
                        text = data.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Rank badge
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "#${data.rank}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                // XP and percentile
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatXp(data.totalXp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Top ${data.percentile}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Format XP for display (e.g., 1.2k, 15k)
 */
private fun formatXp(xp: Int): String {
    return when {
        xp >= 1000000 -> String.format("%.1fM", xp / 1000000.0)
        xp >= 10000 -> String.format("%.1fk", xp / 1000.0)
        xp >= 1000 -> String.format("%.1fk", xp / 1000.0)
        else -> xp.toString()
    }
}
