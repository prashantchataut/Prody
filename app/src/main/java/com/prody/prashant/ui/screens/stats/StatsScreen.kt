package com.prody.prashant.ui.screens.stats

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.components.StreakBadge
import com.prody.prashant.ui.theme.*

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            StatsHeader(
                totalPoints = uiState.totalPoints,
                currentStreak = uiState.currentStreak,
                rank = uiState.currentRank
            )
        }

        // Stats cards
        item {
            StatsOverview(
                wordsLearned = uiState.wordsLearned,
                journalEntries = uiState.journalEntries,
                futureMessages = uiState.futureMessages,
                daysActive = uiState.daysActive
            )
        }

        // Tab selector for leaderboard
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.weekly_stats)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.all_time_stats)) }
                )
            }
        }

        // Leaderboard
        item {
            Text(
                text = stringResource(R.string.leaderboard),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        val displayedLeaderboard = if (selectedTab == 0) uiState.weeklyLeaderboard else uiState.allTimeLeaderboard

        if (displayedLeaderboard.isEmpty()) {
            item {
                EmptyLeaderboard()
            }
        } else {
            itemsIndexed(
                items = displayedLeaderboard.take(10),
                key = { _, item -> item.odId }
            ) { index, entry ->
                LeaderboardItem(
                    entry = entry,
                    rank = index + 1,
                    onBoost = { viewModel.boostPeer(entry.odId) },
                    onCongrats = { viewModel.congratulatePeer(entry.odId) }
                )
            }
        }

        // Weekly progress chart placeholder
        item {
            WeeklyProgressCard(
                weeklyData = uiState.weeklyProgress
            )
        }
    }
}

@Composable
private fun StatsHeader(
    totalPoints: Int,
    currentStreak: Int,
    rank: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                )
            )
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.nav_stats),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = totalPoints.toString(),
                    label = "Points",
                    icon = Icons.Filled.Stars
                )
                StatItem(
                    value = if (rank > 0) "#$rank" else "-",
                    label = "Rank",
                    icon = Icons.Filled.Leaderboard
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StreakBadge(streakDays = currentStreak)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Streak",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldTier,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun StatsOverview(
    wordsLearned: Int,
    journalEntries: Int,
    futureMessages: Int,
    daysActive: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            value = wordsLearned.toString(),
            label = "Words",
            color = MoodMotivated,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = journalEntries.toString(),
            label = "Entries",
            color = MoodCalm,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = futureMessages.toString(),
            label = "Messages",
            color = MoodExcited,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = daysActive.toString(),
            label = "Days",
            color = MoodGrateful,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier,
        backgroundColor = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LeaderboardItem(
    entry: LeaderboardEntryEntity,
    rank: Int,
    onBoost: () -> Unit,
    onCongrats: () -> Unit
) {
    val backgroundColor = when {
        entry.isCurrentUser -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        rank == 1 -> GoldTier.copy(alpha = 0.1f)
        rank == 2 -> SilverTier.copy(alpha = 0.1f)
        rank == 3 -> BronzeTier.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        backgroundColor = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (rank) {
                            1 -> GoldTier
                            2 -> SilverTier
                            3 -> BronzeTier
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = entry.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Medium
                    )
                    if (entry.isCurrentUser) {
                        Text(
                            text = "(You)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${entry.totalPoints} pts",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (entry.currentStreak > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = StreakFire,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = entry.currentStreak.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = StreakFire
                            )
                        }
                    }
                }
            }

            // Actions (only for other users)
            if (!entry.isCurrentUser) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onBoost,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ThumbUp,
                            contentDescription = stringResource(R.string.boost_peer),
                            modifier = Modifier.size(18.dp),
                            tint = MoodMotivated
                        )
                    }
                    IconButton(
                        onClick = onCongrats,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Celebration,
                            contentDescription = stringResource(R.string.congratulate),
                            modifier = Modifier.size(18.dp),
                            tint = MoodExcited
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLeaderboard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Groups,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Leaderboard Coming Soon",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Keep growing! Your progress is being tracked.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WeeklyProgressCard(
    weeklyData: List<Int>
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "This Week's Activity",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                val maxValue = weeklyData.maxOrNull()?.coerceAtLeast(1) ?: 1

                days.forEachIndexed { index, day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val value = weeklyData.getOrNull(index) ?: 0
                        val height = (value.toFloat() / maxValue * 60).coerceAtLeast(4f)

                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(height.dp)
                                .clip(CardShape)
                                .background(
                                    if (value > 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
