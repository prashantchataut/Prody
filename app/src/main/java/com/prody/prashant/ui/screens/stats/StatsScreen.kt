package com.prody.prashant.ui.screens.stats

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    // Entry animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Animated Header with stats showcase
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(600, easing = EaseOutCubic)
                )
            ) {
                StatsHeader(
                    totalPoints = uiState.totalPoints,
                    currentStreak = uiState.currentStreak,
                    rank = uiState.currentRank,
                    longestStreak = uiState.longestStreak
                )
            }
        }

        // Quick stats cards with staggered animation
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(600, delayMillis = 200, easing = EaseOutCubic)
                )
            ) {
                QuickStatsSection(
                    wordsLearned = uiState.wordsLearned,
                    journalEntries = uiState.journalEntries,
                    futureMessages = uiState.futureMessages,
                    daysActive = uiState.daysActive
                )
            }
        }

        // Activity chart
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(600, delayMillis = 300, easing = EaseOutCubic)
                )
            ) {
                ActivityChartCard(weeklyData = uiState.weeklyProgress)
            }
        }

        // Mood distribution ring
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(600, delayMillis = 400, easing = EaseOutCubic)
                )
            ) {
                MoodDistributionCard(moodData = uiState.moodDistribution)
            }
        }

        // Progress insights
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 500))
            ) {
                ProgressInsightsSection(
                    weeklyGrowth = uiState.weeklyGrowthPercent,
                    consistencyScore = uiState.consistencyScore,
                    learningPace = uiState.learningPace
                )
            }
        }

        // Tab selector for leaderboard
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 600))
            ) {
                Column {
                    SectionHeader(
                        title = stringResource(R.string.leaderboard),
                        icon = Icons.Filled.Leaderboard
                    )

                    StyledTabRow(
                        selectedTab = selectedTab,
                        tabs = listOf(
                            stringResource(R.string.weekly_stats),
                            stringResource(R.string.all_time_stats)
                        ),
                        onTabSelected = { selectedTab = it }
                    )
                }
            }
        }

        // Leaderboard
        val displayedLeaderboard = if (selectedTab == 0)
            uiState.weeklyLeaderboard else uiState.allTimeLeaderboard

        if (displayedLeaderboard.isEmpty()) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600, delayMillis = 700))
                ) {
                    EmptyLeaderboardCard()
                }
            }
        } else {
            itemsIndexed(
                items = displayedLeaderboard.take(10),
                key = { _, item -> item.odId }
            ) { index, entry ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(300, delayMillis = 700 + index * 50)) +
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(400, delayMillis = 700 + index * 50)
                            )
                ) {
                    LeaderboardItem(
                        entry = entry,
                        rank = index + 1,
                        onBoost = { viewModel.boostPeer(entry.odId) },
                        onCongrats = { viewModel.congratulatePeer(entry.odId) }
                    )
                }
            }
        }

        // Motivational footer
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 1000))
            ) {
                MotivationalFooter()
            }
        }
    }
}

@Composable
private fun StatsHeader(
    totalPoints: Int,
    currentStreak: Int,
    rank: Int,
    longestStreak: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_animation")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                )
            )
    ) {
        // Animated background particles
        HeaderBackgroundAnimation()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.nav_stats),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main stats showcase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total Points with animated ring
                AnimatedStatDisplay(
                    value = totalPoints,
                    label = "Points",
                    icon = Icons.Filled.Stars,
                    color = GoldTier,
                    showRing = true
                )

                // Rank badge
                RankBadge(
                    rank = rank,
                    glowAlpha = glowAlpha
                )

                // Streak with fire animation
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedStreakBadge(streakDays = currentStreak)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Streak",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    if (longestStreak > currentStreak) {
                        Text(
                            text = "Best: $longestStreak",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldTier.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderBackgroundAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_particles")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.3f)) {
        val center = Offset(size.width / 2, size.height / 2)

        // Draw orbiting circles
        for (i in 0 until 3) {
            val angle = (rotation + i * 120f) * PI / 180
            val radius = minOf(size.width, size.height) * 0.3f
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = 30f + i * 10f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun AnimatedStatDisplay(
    value: Int,
    label: String,
    icon: ImageVector,
    color: Color,
    showRing: Boolean = false
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "value_animation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            if (showRing) {
                AnimatedProgressRing(
                    progress = (value % 1000) / 1000f,
                    color = color,
                    modifier = Modifier.size(70.dp)
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatNumber(animatedValue),
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
private fun AnimatedProgressRing(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = 4.dp.toPx()
        val radius = (minOf(size.width, size.height) - strokeWidth) / 2

        // Background ring
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = radius,
            style = Stroke(width = strokeWidth)
        )

        // Progress arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = animatedProgress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size = Size(size.width - strokeWidth, size.height - strokeWidth)
        )
    }
}

@Composable
private fun RankBadge(
    rank: Int,
    glowAlpha: Float
) {
    val displayRank = if (rank > 0) "#$rank" else "-"

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(90.dp)
    ) {
        // Glow effect
        Box(
            modifier = Modifier
                .size(85.dp)
                .blur(15.dp)
                .alpha(glowAlpha)
                .background(GoldTier, CircleShape)
        )

        // Badge background
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(2.dp, GoldTier.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = GoldTier,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = displayRank,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AnimatedStreakBadge(streakDays: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(contentAlignment = Alignment.Center) {
        // Fire glow
        if (streakDays > 0) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .scale(fireScale)
                    .blur(12.dp)
                    .alpha(glowAlpha)
                    .background(StreakFire, CircleShape)
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (streakDays > 0) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                StreakFire.copy(alpha = 0.6f),
                                StreakGlow.copy(alpha = 0.4f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.3f),
                                Color.Gray.copy(alpha = 0.2f)
                            )
                        )
                    }
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = if (streakDays > 0) StreakFire else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .scale(if (streakDays > 0) fireScale else 1f)
            )
            Text(
                text = streakDays.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun QuickStatsSection(
    wordsLearned: Int,
    journalEntries: Int,
    futureMessages: Int,
    daysActive: Int
) {
    val stats = listOf(
        QuickStat("Words", wordsLearned, Icons.Outlined.School, MoodMotivated),
        QuickStat("Entries", journalEntries, Icons.Outlined.Book, MoodCalm),
        QuickStat("Messages", futureMessages, Icons.Outlined.Mail, MoodExcited),
        QuickStat("Days", daysActive, Icons.Outlined.CalendarMonth, MoodGrateful)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stats.size) { index ->
            QuickStatCard(
                stat = stats[index],
                delayMillis = index * 100
            )
        }
    }
}

private data class QuickStat(
    val label: String,
    val value: Int,
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun QuickStatCard(
    stat: QuickStat,
    delayMillis: Int
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        isVisible = true
    }

    val animatedValue by animateIntAsState(
        targetValue = if (isVisible) stat.value else 0,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "stat_value"
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    ProdyCard(
        modifier = Modifier
            .width(100.dp)
            .scale(scale),
        backgroundColor = stat.color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(stat.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = stat.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = formatNumber(animatedValue),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = stat.color
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActivityChartCard(weeklyData: List<Int>) {
    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        isAnimated = true
    }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                        imageVector = Icons.Filled.Insights,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "This Week's Activity",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                val totalActivity = weeklyData.sum()
                Text(
                    text = "$totalActivity total",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated bar chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val maxValue = weeklyData.maxOrNull()?.coerceAtLeast(1) ?: 1

                days.forEachIndexed { index, day ->
                    val value = weeklyData.getOrNull(index) ?: 0
                    val targetHeight = (value.toFloat() / maxValue * 70).coerceAtLeast(4f)

                    val animatedHeight by animateFloatAsState(
                        targetValue = if (isAnimated) targetHeight else 0f,
                        animationSpec = tween(
                            durationMillis = 800,
                            delayMillis = index * 80,
                            easing = EaseOutCubic
                        ),
                        label = "bar_height_$index"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Value label
                        if (value > 0) {
                            Text(
                                text = value.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Bar
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(animatedHeight.dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (value > 0) {
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
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Day label
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

@Composable
private fun MoodDistributionCard(moodData: Map<String, Int>) {
    if (moodData.isEmpty()) return

    val moodColors = mapOf(
        "happy" to MoodHappy,
        "calm" to MoodCalm,
        "anxious" to MoodAnxious,
        "sad" to MoodSad,
        "motivated" to MoodMotivated,
        "grateful" to MoodGrateful,
        "confused" to MoodConfused,
        "excited" to MoodExcited
    )

    val total = moodData.values.sum().coerceAtLeast(1)

    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(400)
        isAnimated = true
    }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Mood,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Mood Distribution",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animated donut chart
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MoodDonutChart(
                        data = moodData,
                        colors = moodColors,
                        isAnimated = isAnimated
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = total.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "entries",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Legend
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moodData.entries
                        .sortedByDescending { it.value }
                        .take(4)
                        .forEach { (mood, count) ->
                            val color = moodColors[mood.lowercase()] ?: MoodCalm
                            val percentage = (count.toFloat() / total * 100).toInt()

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Text(
                                    text = mood.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.width(70.dp)
                                )
                                Text(
                                    text = "$percentage%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = color,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun MoodDonutChart(
    data: Map<String, Int>,
    colors: Map<String, Color>,
    isAnimated: Boolean
) {
    val total = data.values.sum().coerceAtLeast(1)

    val animatedSweep by animateFloatAsState(
        targetValue = if (isAnimated) 1f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "donut_animation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        var startAngle = -90f
        val strokeWidth = 20.dp.toPx()

        data.entries.forEach { (mood, count) ->
            val sweepAngle = (count.toFloat() / total * 360f) * animatedSweep
            val color = colors[mood.lowercase()] ?: MoodCalm

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(size.width - strokeWidth, size.height - strokeWidth)
            )

            startAngle += sweepAngle
        }
    }
}

@Composable
private fun ProgressInsightsSection(
    weeklyGrowth: Int,
    consistencyScore: Int,
    learningPace: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InsightCard(
            title = "Weekly Growth",
            value = "${if (weeklyGrowth >= 0) "+" else ""}$weeklyGrowth%",
            icon = if (weeklyGrowth >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
            color = if (weeklyGrowth >= 0) AchievementUnlocked else MoodAnxious,
            modifier = Modifier.weight(1f)
        )

        InsightCard(
            title = "Consistency",
            value = "$consistencyScore%",
            icon = Icons.Filled.CheckCircle,
            color = when {
                consistencyScore >= 80 -> AchievementUnlocked
                consistencyScore >= 50 -> MoodMotivated
                else -> MoodAnxious
            },
            modifier = Modifier.weight(1f)
        )

        InsightCard(
            title = "Pace",
            value = learningPace,
            icon = Icons.Filled.Speed,
            color = MoodExcited,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InsightCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier,
        backgroundColor = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun StyledTabRow(
    selectedTab: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent,
                animationSpec = tween(200),
                label = "tab_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(200),
                label = "tab_text"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(backgroundColor)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = textColor
                )
            }
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
        entry.isCurrentUser -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        rank == 1 -> GoldTier.copy(alpha = 0.15f)
        rank == 2 -> SilverTier.copy(alpha = 0.15f)
        rank == 3 -> BronzeTier.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surface
    }

    val scale by animateFloatAsState(
        targetValue = if (entry.isCurrentUser) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "item_scale"
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .scale(scale),
        backgroundColor = backgroundColor,
        elevation = if (entry.isCurrentUser) 4.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated rank badge
            RankIndicator(rank = rank)

            Spacer(modifier = Modifier.width(16.dp))

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (entry.isCurrentUser) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "You",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
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
                            text = formatNumber(entry.totalPoints),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (entry.currentStreak > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = StreakFire,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${entry.currentStreak}d",
                                style = MaterialTheme.typography.labelSmall,
                                color = StreakFire,
                                fontWeight = FontWeight.Medium
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
                        modifier = Modifier.size(36.dp)
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
                        modifier = Modifier.size(36.dp)
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
private fun RankIndicator(rank: Int) {
    val (backgroundColor, textColor) = when (rank) {
        1 -> GoldTier to Color.White
        2 -> SilverTier to Color.White
        3 -> BronzeTier to Color.White
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rank_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (rank <= 3) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(contentAlignment = Alignment.Center) {
        if (rank <= 3) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .scale(glowScale)
                    .blur(8.dp)
                    .alpha(0.4f)
                    .background(backgroundColor, CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (rank <= 3) {
                        Brush.radialGradient(
                            colors = listOf(
                                backgroundColor,
                                backgroundColor.copy(alpha = 0.8f)
                            )
                        )
                    } else {
                        Brush.radialGradient(colors = listOf(backgroundColor, backgroundColor))
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (rank <= 3) {
                Icon(
                    imageVector = when (rank) {
                        1 -> Icons.Filled.EmojiEvents
                        2 -> Icons.Filled.WorkspacePremium
                        else -> Icons.Filled.MilitaryTech
                    },
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = rank.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun EmptyLeaderboardCard() {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "empty_animation")
            val rotation by infiniteTransition.animateFloat(
                initialValue = -10f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = EaseInOutCubic),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "rotation"
            )

            Icon(
                imageVector = Icons.Filled.Groups,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Join the Community",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Keep growing! Your progress is being tracked.\nSoon you'll see how you compare with other seekers.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun MotivationalFooter() {
    val quotes = remember {
        listOf(
            "The journey of a thousand miles begins with a single step.",
            "Progress, not perfection.",
            "Every day is a new opportunity to grow.",
            "Small steps lead to big changes.",
            "Consistency is the key to mastery."
        )
    }
    // Use randomOrNull with fallback for defensive programming
    val quote = remember { quotes.randomOrNull() ?: "Every step forward is progress." }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        ProdyPrimary.copy(alpha = 0.1f),
                        ProdyTertiary.copy(alpha = 0.1f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.FormatQuote,
                contentDescription = null,
                tint = ProdyPrimary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
        number >= 1000 -> String.format("%.1fK", number / 1000.0)
        else -> number.toString()
    }
}
