package com.prody.prashant.ui.screens.stats

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.theme.*
import kotlin.math.PI
import kotlin.math.sin

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
        // Animated Header with visual effects
        item {
            AnimatedStatsHeader(
                totalPoints = uiState.totalPoints,
                currentStreak = uiState.currentStreak,
                rank = uiState.currentRank
            )
        }

        // Interactive stats cards with animations
        item {
            AnimatedStatsOverview(
                wordsLearned = uiState.wordsLearned,
                journalEntries = uiState.journalEntries,
                futureMessages = uiState.futureMessages,
                daysActive = uiState.daysActive
            )
        }

        // Progress rings section
        item {
            ProgressRingsSection(
                wordsLearned = uiState.wordsLearned,
                journalEntries = uiState.journalEntries,
                currentStreak = uiState.currentStreak
            )
        }

        // Tab selector for leaderboard with animation
        item {
            AnimatedTabSelector(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        // Leaderboard header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.leaderboard),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                AnimatedVisibility(
                    visible = uiState.weeklyLeaderboard.isNotEmpty() || uiState.allTimeLeaderboard.isNotEmpty(),
                    enter = fadeIn() + slideInHorizontally { it },
                    exit = fadeOut() + slideOutHorizontally { it }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Live Rankings",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        val displayedLeaderboard = if (selectedTab == 0) uiState.weeklyLeaderboard else uiState.allTimeLeaderboard

        if (displayedLeaderboard.isEmpty()) {
            item {
                AnimatedEmptyLeaderboard()
            }
        } else {
            itemsIndexed(
                items = displayedLeaderboard.take(10),
                key = { _, item -> item.odId }
            ) { index, entry ->
                AnimatedLeaderboardItem(
                    entry = entry,
                    rank = index + 1,
                    animationDelay = index * 50,
                    onBoost = { viewModel.boostPeer(entry.odId) },
                    onCongrats = { viewModel.congratulatePeer(entry.odId) }
                )
            }
        }

        // Weekly progress chart with animations
        item {
            AnimatedWeeklyProgressCard(
                weeklyData = uiState.weeklyProgress
            )
        }

        // Motivational footer
        item {
            MotivationalFooter()
        }
    }
}

@Composable
private fun AnimatedStatsHeader(
    totalPoints: Int,
    currentStreak: Int,
    rank: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_animation")

    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        ProdyPrimaryVariant
                    )
                )
            )
    ) {
        // Animated background particles
        FloatingParticlesBackground(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            particleCount = 20,
            particleColor = Color.White.copy(alpha = 0.08f)
        )

        // Wave pattern overlay
        WavePattern(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .alpha(0.3f),
            waveColor = Color.White.copy(alpha = 0.05f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title with animation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Analytics,
                    contentDescription = null,
                    tint = GoldTier,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = stringResource(R.string.nav_stats),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Stats row with animated counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnimatedStatItem(
                    value = totalPoints,
                    label = "Points",
                    icon = Icons.Filled.Stars,
                    iconColor = GoldTier
                )
                AnimatedStatItem(
                    value = if (rank > 0) rank else 0,
                    label = "Rank",
                    icon = Icons.Filled.Leaderboard,
                    iconColor = SilverTier,
                    prefix = if (rank > 0) "#" else ""
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StreakBadge(streakDays = currentStreak)
                    Spacer(modifier = Modifier.height(6.dp))
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
private fun AnimatedStatItem(
    value: Int,
    label: String,
    icon: ImageVector,
    iconColor: Color,
    prefix: String = ""
) {
    val animatedValue = AnimatedCounter(targetValue = value)

    val infiniteTransition = rememberInfiniteTransition(label = "stat_pulse")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(28.dp)
                .scale(iconScale)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (value == 0 && prefix.isEmpty()) "-" else "$prefix$animatedValue",
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
private fun AnimatedStatsOverview(
    wordsLearned: Int,
    journalEntries: Int,
    futureMessages: Int,
    daysActive: Int
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedStatCard(
                value = wordsLearned,
                label = "Words Learned",
                icon = Icons.Filled.School,
                color = MoodMotivated,
                modifier = Modifier.weight(1f),
                animationDelay = 0
            )
            AnimatedStatCard(
                value = journalEntries,
                label = "Journal Entries",
                icon = Icons.Filled.Book,
                color = MoodCalm,
                modifier = Modifier.weight(1f),
                animationDelay = 100
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedStatCard(
                value = futureMessages,
                label = "Future Letters",
                icon = Icons.Filled.Schedule,
                color = MoodExcited,
                modifier = Modifier.weight(1f),
                animationDelay = 200
            )
            AnimatedStatCard(
                value = daysActive,
                label = "Days Active",
                icon = Icons.Filled.CalendarMonth,
                color = MoodGrateful,
                modifier = Modifier.weight(1f),
                animationDelay = 300
            )
        }
    }
}

@Composable
private fun AnimatedStatCard(
    value: Int,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }
    val animatedValue = AnimatedCounter(targetValue = if (isVisible) value else 0)

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "card_alpha"
    )

    ProdyCard(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        backgroundColor = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = animatedValue.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProgressRingsSection(
    wordsLearned: Int,
    journalEntries: Int,
    currentStreak: Int
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressRingItem(
                    progress = (wordsLearned / 100f).coerceAtMost(1f),
                    value = wordsLearned,
                    target = 100,
                    label = "Words",
                    color = MoodMotivated
                )
                ProgressRingItem(
                    progress = (journalEntries / 50f).coerceAtMost(1f),
                    value = journalEntries,
                    target = 50,
                    label = "Entries",
                    color = MoodCalm
                )
                ProgressRingItem(
                    progress = (currentStreak / 30f).coerceAtMost(1f),
                    value = currentStreak,
                    target = 30,
                    label = "Streak",
                    color = StreakFire
                )
            }
        }
    }
}

@Composable
private fun ProgressRingItem(
    progress: Float,
    value: Int,
    target: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedProgressRing(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                color = color,
                trackColor = color.copy(alpha = 0.15f),
                strokeWidth = 6.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "/$target",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AnimatedTabSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("This Week" to Icons.Filled.DateRange, "All Time" to Icons.Filled.EmojiEvents)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { index, (label, icon) ->
            val isSelected = selectedTab == index
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent,
                animationSpec = tween(200),
                label = "tab_bg"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(200),
                label = "tab_content"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    onClick = { onTabSelected(index) },
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = contentColor,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedLeaderboardItem(
    entry: LeaderboardEntryEntity,
    rank: Int,
    animationDelay: Int,
    onBoost: () -> Unit,
    onCongrats: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    val offsetX by animateIntAsState(
        targetValue = if (isVisible) 0 else 100,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "item_offset"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(200),
        label = "item_alpha"
    )

    val backgroundColor = when {
        entry.isCurrentUser -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        rank == 1 -> GoldTier.copy(alpha = 0.12f)
        rank == 2 -> SilverTier.copy(alpha = 0.12f)
        rank == 3 -> BronzeTier.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surface
    }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .offset(x = offsetX.dp)
            .alpha(alpha),
        backgroundColor = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated rank badge
            AnimatedRankBadge(rank = rank)

            Spacer(modifier = Modifier.width(14.dp))

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Medium
                    )
                    if (entry.isCurrentUser) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "You",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Row(
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
                            text = "${entry.totalPoints} pts",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (entry.currentStreak > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = StreakFire,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${entry.currentStreak} days",
                                style = MaterialTheme.typography.labelSmall,
                                color = StreakFire
                            )
                        }
                    }
                }
            }

            // Actions for other users
            if (!entry.isCurrentUser) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        onClick = onBoost,
                        shape = CircleShape,
                        color = MoodMotivated.copy(alpha = 0.1f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Filled.ThumbUp,
                                contentDescription = stringResource(R.string.boost_peer),
                                modifier = Modifier.size(18.dp),
                                tint = MoodMotivated
                            )
                        }
                    }
                    Surface(
                        onClick = onCongrats,
                        shape = CircleShape,
                        color = MoodExcited.copy(alpha = 0.1f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
}

@Composable
private fun AnimatedRankBadge(rank: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "rank_animation")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rank_glow"
    )

    val badgeColor = when (rank) {
        1 -> GoldTier
        2 -> SilverTier
        3 -> BronzeTier
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = Modifier.size(44.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect for top 3
        if (rank <= 3) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .blur(8.dp)
                    .alpha(glowAlpha)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(badgeColor),
            contentAlignment = Alignment.Center
        ) {
            if (rank == 1) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = rank.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AnimatedEmptyLeaderboard() {
    val infiniteTransition = rememberInfiniteTransition(label = "empty_animation")

    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(iconScale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Groups,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Leaderboard Coming Soon",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Keep growing! Your progress is being tracked and you'll be on the board soon.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AnimatedWeeklyProgressCard(
    weeklyData: List<Int>
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        isVisible = true
    }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val total = weeklyData.sum()
                    Icon(
                        imageVector = Icons.Filled.TrendingUp,
                        contentDescription = null,
                        tint = AchievementUnlocked,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$total pts this week",
                        style = MaterialTheme.typography.labelMedium,
                        color = AchievementUnlocked
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val maxValue = weeklyData.maxOrNull()?.coerceAtLeast(1) ?: 1

                days.forEachIndexed { index, day ->
                    AnimatedBarItem(
                        value = weeklyData.getOrNull(index) ?: 0,
                        maxValue = maxValue,
                        day = day,
                        isVisible = isVisible,
                        animationDelay = index * 80
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedBarItem(
    value: Int,
    maxValue: Int,
    day: String,
    isVisible: Boolean,
    animationDelay: Int
) {
    var showBar by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            kotlinx.coroutines.delay(animationDelay.toLong())
            showBar = true
        }
    }

    val targetHeight = (value.toFloat() / maxValue * 80).coerceAtLeast(8f)
    val animatedHeight by animateFloatAsState(
        targetValue = if (showBar) targetHeight else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bar_height"
    )

    val barColor = if (value > 0) {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Value label
        AnimatedVisibility(
            visible = value > 0 && showBar,
            enter = fadeIn() + scaleIn()
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Animated bar
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(animatedHeight.dp)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(barColor)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = day,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MotivationalFooter() {
    val quotes = listOf(
        "Every day is a chance to improve.",
        "Your consistency defines your destiny.",
        "Small steps lead to big changes.",
        "Growth is a journey, not a destination.",
        "You're doing better than you think."
    )
    val randomQuote = remember { quotes.random() }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = ProdyPrimary.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Spa,
                contentDescription = null,
                tint = ProdyPrimary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = randomQuote,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
