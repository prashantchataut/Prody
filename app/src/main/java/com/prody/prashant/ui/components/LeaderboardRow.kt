package com.prody.prashant.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import com.prody.prashant.domain.identity.ProdyBanners
import com.prody.prashant.ui.theme.ProdyTokens

/**
 * Prody Premium Leaderboard Row Component
 *
 * A clean, minimal leaderboard row that integrates Gamification 2.0 features:
 * - Banner badge next to username
 * - DEV/Beta badges for special users
 * - Subtle boost counter
 * - Rank indicator for top 3
 * - Long-press for support interaction
 *
 * Design principles:
 * - No cluttery UI - everything is minimal
 * - Premium feel with subtle animations
 * - Clear visual hierarchy
 */

/**
 * Premium leaderboard row component.
 *
 * @param entry The leaderboard entry to display
 * @param onRowClick Callback when row is clicked
 * @param onLongPress Callback when row is long-pressed (for support)
 * @param modifier Modifier for the component
 * @param showSupportButton Whether to show the support icon button
 * @param hasUserSupportedToday Whether the current user has supported this person today
 * @param onSupportClick Callback when support button is clicked
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProdyLeaderboardRow(
    entry: LeaderboardEntryEntity,
    onRowClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    showSupportButton: Boolean = true,
    hasUserSupportedToday: Boolean = false,
    onSupportClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "row_scale"
    )

    // Determine if this is a highlighted row (top 3 or current user)
    val isHighlighted = entry.rank in 1..3 || entry.isCurrentUser
    val backgroundColor = when {
        entry.isCurrentUser -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        entry.rank == 1 -> Color(0xFFFFD700).copy(alpha = 0.08f)
        entry.rank == 2 -> Color(0xFFC0C0C0).copy(alpha = 0.08f)
        entry.rank == 3 -> Color(0xFFCD7F32).copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onRowClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress()
                }
            ),
        shape = RoundedCornerShape(ProdyTokens.Radius.md),
        color = backgroundColor,
        tonalElevation = if (isHighlighted) 1.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ProdyTokens.Spacing.md, vertical = ProdyTokens.Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank indicator
            Box(
                modifier = Modifier.width(36.dp),
                contentAlignment = Alignment.Center
            ) {
                if (entry.rank in 1..3) {
                    ProdyTopRankIndicator(rank = entry.rank)
                } else {
                    Text(
                        text = "#${entry.rank}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(ProdyTokens.Spacing.sm))

            // Avatar with rarity frame
            ProdyRarityFrame(
                rarity = entry.profileFrameRarity,
                size = 44.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(ProdyTokens.Spacing.md))

            // User info column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display name
                    Text(
                        text = entry.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Special badges
                    if (entry.isDevBadgeHolder) {
                        Spacer(modifier = Modifier.width(6.dp))
                        ProdySpecialBadge(
                            badgeType = SpecialBadgeType.DEV,
                            size = 18.dp
                        )
                    } else if (entry.isBetaTester) {
                        Spacer(modifier = Modifier.width(6.dp))
                        ProdySpecialBadge(
                            badgeType = SpecialBadgeType.BETA_TESTER,
                            size = 18.dp
                        )
                    }

                    // Banner badge (if not a special badge holder)
                    if (!entry.isDevBadgeHolder && !entry.isBetaTester && entry.bannerId != "default_dawn") {
                        Spacer(modifier = Modifier.width(6.dp))
                        val banner = ProdyBanners.getBannerById(entry.bannerId)
                        if (banner != null) {
                            ProdyBannerBadge(
                                banner = banner,
                                size = 16.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Points
                    Text(
                        text = formatPoints(entry.totalPoints),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Streak indicator for notable streaks
                    if (entry.currentStreak >= 7) {
                        Spacer(modifier = Modifier.width(8.dp))
                        ProdyStreakMilestoneIndicator(streakDays = entry.currentStreak)
                    }

                    // Boost count
                    if (entry.boostsReceived > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        ProdyBoostCounter(boostCount = entry.boostsReceived)
                    }
                }
            }

            // Rank change indicator
            RankChangeIndicator(
                currentRank = entry.rank,
                previousRank = entry.previousRank
            )

            // Support button
            if (showSupportButton && !entry.isCurrentUser) {
                Spacer(modifier = Modifier.width(ProdyTokens.Spacing.xs))
                ProdySupportIconButton(
                    onSupportClick = onSupportClick,
                    hasSupported = hasUserSupportedToday
                )
            }
        }
    }
}

@Composable
private fun RankChangeIndicator(
    currentRank: Int,
    previousRank: Int
) {
    val change = previousRank - currentRank

    when {
        change > 0 -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = ProdyTokens.Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Rank up",
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF4CAF50)
                )
                Text(
                    text = "+$change",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        change < 0 -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = ProdyTokens.Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingDown,
                    contentDescription = "Rank down",
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFFE57373)
                )
                Text(
                    text = "$change",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE57373),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        else -> {
            // No change - show nothing for cleaner UI
            Spacer(modifier = Modifier.width(32.dp))
        }
    }
}

private fun formatPoints(points: Int): String {
    return when {
        points >= 1000000 -> "${points / 1000000}M pts"
        points >= 1000 -> "${points / 1000}k pts"
        else -> "$points pts"
    }
}

/**
 * Compact leaderboard row for smaller displays.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProdyCompactLeaderboardRow(
    entry: LeaderboardEntryEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ),
        shape = RoundedCornerShape(ProdyTokens.Radius.sm),
        color = if (entry.isCurrentUser)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        else
            MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProdyTokens.Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = "#${entry.rank}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = when (entry.rank) {
                    1 -> Color(0xFFFFD700)
                    2 -> Color(0xFFC0C0C0)
                    3 -> Color(0xFFCD7F32)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.width(32.dp)
            )

            // Name
            Text(
                text = entry.displayName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Points
            Text(
                text = formatPoints(entry.totalPoints),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Podium display for top 3 leaderboard entries.
 */
@Composable
fun ProdyLeaderboardPodium(
    first: LeaderboardEntryEntity?,
    second: LeaderboardEntryEntity?,
    third: LeaderboardEntryEntity?,
    onUserClick: (LeaderboardEntryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd place (left)
        second?.let {
            PodiumPlace(
                entry = it,
                place = 2,
                height = 100.dp,
                color = Color(0xFFC0C0C0),
                onClick = { onUserClick(it) }
            )
        }

        // 1st place (center)
        first?.let {
            PodiumPlace(
                entry = it,
                place = 1,
                height = 120.dp,
                color = Color(0xFFFFD700),
                onClick = { onUserClick(it) }
            )
        }

        // 3rd place (right)
        third?.let {
            PodiumPlace(
                entry = it,
                place = 3,
                height = 80.dp,
                color = Color(0xFFCD7F32),
                onClick = { onUserClick(it) }
            )
        }
    }
}

@Composable
private fun PodiumPlace(
    entry: LeaderboardEntryEntity,
    place: Int,
    height: androidx.compose.ui.unit.Dp,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .combinedClickable(onClick = onClick, onLongClick = {})
    ) {
        // Avatar
        ProdyRarityFrame(
            rarity = entry.profileFrameRarity,
            size = if (place == 1) 64.dp else 52.dp
        ) {
            Box(
                modifier = Modifier
                    .size(if (place == 1) 64.dp else 52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(if (place == 1) 32.dp else 24.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Name
        Text(
            text = entry.displayName.split(" ").firstOrNull() ?: entry.displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Podium stand
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(height)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(color, color.copy(alpha = 0.7f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = place.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (place == 2) Color.DarkGray else Color.White
                )
                Text(
                    text = formatPoints(entry.totalPoints),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (place == 2) Color.DarkGray.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.combinedClickable(
    onClick: () -> Unit,
    onLongClick: () -> Unit
): Modifier = this.then(
    Modifier.combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick
    )
)
