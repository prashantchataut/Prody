package com.prody.prashant.ui.screens.stats

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

// =============================================================================
// DESIGN SYSTEM CONSTANTS - Stats Screen Redesign
// =============================================================================

// Vibrant Neon Green Accent (Primary brand accent per design spec)
private val NeonGreen = Color(0xFF36F97F)
private val NeonGreenDark = Color(0xFF2BD968)
private val NeonGreenLight = Color(0xFF5AFF9A)

// Dark Mode Card Background (muted green tint)
private val DarkCardBackground = Color(0xFF1A2E21)
private val DarkCardBackgroundElevated = Color(0xFF1E3327)
private val DarkBackground = Color(0xFF0D1F14)

// Light Mode Card Background
private val LightCardBackground = Color(0xFFF0F4F1)
private val LightCardBackgroundElevated = Color(0xFFFAFCFA)
private val LightBackground = Color(0xFFF8F9FA)

// Leaderboard Tier Colors (Animated Banner Colors)
private val GoldBanner = Color(0xFFD4AF37)
private val GoldBannerLight = Color(0xFFF4D03F)
private val GoldBannerDark = Color(0xFFB8960C)

private val SilverBanner = Color(0xFFC0C0C0)
private val SilverBannerLight = Color(0xFFE8E8E8)
private val SilverBannerDark = Color(0xFF9A9A9A)

private val BronzeBanner = Color(0xFFCD7F32)
private val BronzeBannerLight = Color(0xFFE8A057)
private val BronzeBannerDark = Color(0xFFA86523)

// =============================================================================
// MAIN STATS SCREEN
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = !MaterialTheme.colorScheme.background.luminance().let { it > 0.5f }

    // Pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }

    // Support bottom sheet state
    var showSupportSheet by remember { mutableStateOf(false) }
    var selectedUserForSupport by remember { mutableStateOf<LeaderboardEntryEntity?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // Entry animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.refresh()
            delay(1500)
            isRefreshing = false
        }
    }

    // Background colors based on theme
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val cardBackgroundColor = if (isDarkTheme) DarkCardBackground else LightCardBackground
    val cardElevatedColor = if (isDarkTheme) DarkCardBackgroundElevated else LightCardBackgroundElevated
    val textPrimaryColor = if (isDarkTheme) Color.White else Color(0xFF1A2B23)
    val textSecondaryColor = if (isDarkTheme) Color(0xFFB0C4B8) else Color(0xFF5A6B63)
    val textTertiaryColor = if (isDarkTheme) Color(0xFF708878) else Color(0xFF8A9B93)

    // Support bottom sheet
    if (showSupportSheet && selectedUserForSupport != null) {
        SupportBottomSheet(
            sheetState = sheetState,
            entry = selectedUserForSupport!!,
            onDismiss = {
                showSupportSheet = false
                selectedUserForSupport = null
            },
            onBoost = {
                viewModel.sendBoost(selectedUserForSupport!!.odId)
                showSupportSheet = false
                selectedUserForSupport = null
            },
            onRespect = {
                viewModel.sendRespect(selectedUserForSupport!!.odId)
                showSupportSheet = false
                selectedUserForSupport = null
            },
            canBoost = uiState.canBoostToday,
            canRespect = uiState.canRespectToday,
            isDarkTheme = isDarkTheme,
            cardBackgroundColor = cardBackgroundColor,
            textPrimaryColor = textPrimaryColor,
            textSecondaryColor = textSecondaryColor
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true },
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp) // Space for sticky user section
            ) {
                // Header Area
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(600)) + slideInVertically(
                            initialOffsetY = { -it / 2 },
                            animationSpec = tween(600, easing = EaseOutCubic)
                        )
                    ) {
                        StatsHeaderSection(
                            textPrimaryColor = textPrimaryColor,
                            textSecondaryColor = textSecondaryColor,
                            cardBackgroundColor = cardBackgroundColor,
                            isDarkTheme = isDarkTheme
                        )
                    }
                }

                // User Stats Section (72 Active Streak, Global Rank, Total XP)
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(600, delayMillis = 100)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(600, delayMillis = 100, easing = EaseOutCubic)
                        )
                    ) {
                        UserStatsSection(
                            currentStreak = uiState.currentStreak,
                            currentRank = uiState.currentRank,
                            totalXp = uiState.totalPoints,
                            textPrimaryColor = textPrimaryColor,
                            textSecondaryColor = textSecondaryColor
                        )
                    }
                }

                // Activity Pulse Section
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(600, delayMillis = 200, easing = EaseOutCubic)
                        )
                    ) {
                        ActivityPulseSection(
                            weeklyData = uiState.weeklyProgress,
                            textPrimaryColor = textPrimaryColor,
                            textSecondaryColor = textSecondaryColor,
                            isDarkTheme = isDarkTheme
                        )
                    }
                }

                // Summary Cards (Words, Entries, Messages)
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(600, delayMillis = 300, easing = EaseOutCubic)
                        )
                    ) {
                        SummaryCardsSection(
                            wordsWritten = uiState.totalWordsWritten,
                            entries = uiState.journalEntries,
                            messages = uiState.futureMessages,
                            cardBackgroundColor = cardBackgroundColor,
                            textPrimaryColor = textPrimaryColor,
                            textSecondaryColor = textSecondaryColor
                        )
                    }
                }

                // Top Performers Section Header
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(600, delayMillis = 400))
                    ) {
                        TopPerformersHeader(textPrimaryColor = textPrimaryColor)
                    }
                }

                // Leaderboard Items
                val leaderboard = uiState.allTimeLeaderboard
                if (leaderboard.isNotEmpty()) {
                    itemsIndexed(
                        items = leaderboard,
                        key = { _, item -> item.odId }
                    ) { index, entry ->
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(tween(300, delayMillis = 500 + index * 30)) +
                                    slideInHorizontally(
                                        initialOffsetX = { it / 2 },
                                        animationSpec = tween(400, delayMillis = 500 + index * 30)
                                    )
                        ) {
                            LeaderboardItemRow(
                                entry = entry,
                                rank = index + 1,
                                isDarkTheme = isDarkTheme,
                                cardBackgroundColor = cardBackgroundColor,
                                textPrimaryColor = textPrimaryColor,
                                textSecondaryColor = textSecondaryColor,
                                onSupportClick = { selectedEntry ->
                                    if (!selectedEntry.isCurrentUser) {
                                        selectedUserForSupport = selectedEntry
                                        showSupportSheet = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Sticky User Section at Bottom
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            StickyUserSection(
                userName = getCurrentUserName(uiState.allTimeLeaderboard),
                userScore = getCurrentUserScore(uiState.allTimeLeaderboard),
                userRank = uiState.currentRank,
                totalUsers = uiState.allTimeLeaderboard.size
            )
        }
    }
}

// =============================================================================
// HEADER SECTION
// =============================================================================

@Composable
private fun StatsHeaderSection(
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    cardBackgroundColor: Color,
    isDarkTheme: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        // Top Row: DASHBOARD text and THIS WEEK button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DASHBOARD",
                style = MaterialTheme.typography.labelSmall,
                color = textSecondaryColor,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Medium
            )

            // THIS WEEK Button with Calendar Icon
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(cardBackgroundColor)
                    .clickable { /* TODO: Implement filter */ }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "THIS WEEK",
                        style = MaterialTheme.typography.labelSmall,
                        color = textSecondaryColor,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = textSecondaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Your Impact Title
        Text(
            text = "Your",
            style = MaterialTheme.typography.headlineMedium,
            color = textPrimaryColor,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = "Impact",
            style = MaterialTheme.typography.headlineLarge,
            color = textPrimaryColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// =============================================================================
// USER STATS SECTION (Streak, Rank, XP)
// =============================================================================

@Composable
private fun UserStatsSection(
    currentStreak: Int,
    currentRank: Int,
    totalXp: Int,
    textPrimaryColor: Color,
    textSecondaryColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Active Streak (Large, prominent, neon green)
        Column {
            // Animated streak number
            val animatedStreak by animateIntAsState(
                targetValue = currentStreak,
                animationSpec = tween(1200, easing = EaseOutCubic),
                label = "streak_animation"
            )

            Text(
                text = animatedStreak.toString(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp,
                    lineHeight = 72.sp
                ),
                color = NeonGreen,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ACTIVE STREAK",
                style = MaterialTheme.typography.labelSmall,
                color = textSecondaryColor,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Right side stats: Global Rank and Total XP
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Global Rank
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (currentRank > 0) "#${formatNumber(currentRank)}" else "-",
                    style = MaterialTheme.typography.headlineMedium,
                    color = textPrimaryColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "GLOBAL RANK",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondaryColor,
                    letterSpacing = 0.5.sp
                )
            }

            // Total XP
            Column(horizontalAlignment = Alignment.End) {
                val animatedXp by animateIntAsState(
                    targetValue = totalXp,
                    animationSpec = tween(1200, easing = EaseOutCubic),
                    label = "xp_animation"
                )

                Text(
                    text = formatNumber(animatedXp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = textPrimaryColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TOTAL XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondaryColor,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// =============================================================================
// ACTIVITY PULSE SECTION - Innovative Wave Visualization
// =============================================================================

@Composable
private fun ActivityPulseSection(
    weeklyData: List<Int>,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    isDarkTheme: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Activity Pulse",
                style = MaterialTheme.typography.titleMedium,
                color = textPrimaryColor,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "LAST 7 DAYS",
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Animated Wave Visualization
        ActivityPulseVisualization(
            data = weeklyData,
            isDarkTheme = isDarkTheme
        )
    }
}

@Composable
private fun ActivityPulseVisualization(
    data: List<Int>,
    isDarkTheme: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_animation")
    val animationPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        isAnimated = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isAnimated) 1f else 0f,
        animationSpec = tween(1500, easing = EaseOutCubic),
        label = "pulse_progress"
    )

    val maxValue = data.maxOrNull()?.coerceAtLeast(1) ?: 1

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val barWidth = (width - (data.size - 1) * 12.dp.toPx()) / data.size
            val maxBarHeight = height * 0.85f

            // Draw bars with wave animation
            data.forEachIndexed { index, value ->
                val normalizedValue = value.toFloat() / maxValue
                val barHeight = (normalizedValue * maxBarHeight * animatedProgress).coerceAtLeast(4.dp.toPx())

                // Add subtle wave effect to bar height
                val waveOffset = sin(animationPhase + index * 0.5f) * 3.dp.toPx() * normalizedValue
                val finalBarHeight = (barHeight + waveOffset).coerceAtLeast(4.dp.toPx())

                val x = index * (barWidth + 12.dp.toPx())
                val y = height - finalBarHeight

                // Draw bar with gradient
                val barColor = if (value > 0) NeonGreen else {
                    if (isDarkTheme) Color(0xFF2A4033) else Color(0xFFD0DDD6)
                }

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(barWidth, finalBarHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }
}

// =============================================================================
// SUMMARY CARDS SECTION
// =============================================================================

@Composable
private fun SummaryCardsSection(
    wordsWritten: Int,
    entries: Int,
    messages: Int,
    cardBackgroundColor: Color,
    textPrimaryColor: Color,
    textSecondaryColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            value = wordsWritten,
            label = "WORDS",
            cardBackgroundColor = cardBackgroundColor,
            textPrimaryColor = textPrimaryColor,
            textSecondaryColor = textSecondaryColor,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            value = entries,
            label = "ENTRIES",
            cardBackgroundColor = cardBackgroundColor,
            textPrimaryColor = textPrimaryColor,
            textSecondaryColor = textSecondaryColor,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            value = messages,
            label = "MSGS",
            cardBackgroundColor = cardBackgroundColor,
            textPrimaryColor = textPrimaryColor,
            textSecondaryColor = textSecondaryColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    value: Int,
    label: String,
    cardBackgroundColor: Color,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "card_value_animation"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(cardBackgroundColor)
            .padding(vertical = 20.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatNumber(animatedValue),
                style = MaterialTheme.typography.headlineSmall,
                color = textPrimaryColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textSecondaryColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// =============================================================================
// TOP PERFORMERS SECTION
// =============================================================================

@Composable
private fun TopPerformersHeader(textPrimaryColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Green dot indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(NeonGreen)
        )
        Text(
            text = "Top Performers",
            style = MaterialTheme.typography.titleMedium,
            color = textPrimaryColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// =============================================================================
// LEADERBOARD ITEM ROW WITH ANIMATED BANNERS
// =============================================================================

@Composable
private fun LeaderboardItemRow(
    entry: LeaderboardEntryEntity,
    rank: Int,
    isDarkTheme: Boolean,
    cardBackgroundColor: Color,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    onSupportClick: (LeaderboardEntryEntity) -> Unit
) {
    val isTopThree = rank <= 3

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        // Animated Banner Background for Top 3
        if (isTopThree) {
            AnimatedBannerBackground(
                rank = rank,
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        // Main Row Content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (!isTopThree) {
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Transparent)
                    } else Modifier
                )
                .clickable { onSupportClick(entry) }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Box(
                modifier = Modifier
                    .width(36.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (isTopThree) {
                    // Special rank badge for top 3
                    TopRankBadge(rank = rank)
                } else {
                    Text(
                        text = rank.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondaryColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name and Badge
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = entry.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isTopThree) Color.White else textPrimaryColor,
                        fontWeight = if (isTopThree) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Title badge for top 3
                    if (isTopThree && rank == 1) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "CHAMPION",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            // Score with Boost Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formatNumber(entry.totalPoints),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isTopThree) NeonGreen else NeonGreen,
                    fontWeight = FontWeight.SemiBold
                )

                // Boost indicator
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TopRankBadge(rank: Int) {
    val badgeColor = when (rank) {
        1 -> GoldBanner
        2 -> SilverBanner
        3 -> BronzeBanner
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(badgeColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

// =============================================================================
// ANIMATED BANNER BACKGROUND FOR TOP 3
// =============================================================================

@Composable
private fun AnimatedBannerBackground(
    rank: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "banner_$rank")

    // Shimmer animation
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_$rank"
    )

    // Glow pulse animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_$rank"
    )

    val (primaryColor, secondaryColor, shimmerColor) = when (rank) {
        1 -> Triple(GoldBannerDark, GoldBanner, GoldBannerLight)
        2 -> Triple(SilverBannerDark, SilverBanner, SilverBannerLight)
        3 -> Triple(BronzeBannerDark, BronzeBanner, BronzeBannerLight)
        else -> Triple(Color.Gray, Color.Gray, Color.Gray)
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Base gradient background
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    primaryColor.copy(alpha = glowAlpha),
                    secondaryColor.copy(alpha = glowAlpha * 0.9f),
                    primaryColor.copy(alpha = glowAlpha * 0.8f)
                )
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx())
        )

        // Animated shimmer effect
        val shimmerWidth = width * 0.4f
        val shimmerX = shimmerOffset * width

        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    shimmerColor.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                startX = shimmerX - shimmerWidth / 2,
                endX = shimmerX + shimmerWidth / 2
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx())
        )
    }
}

// =============================================================================
// STICKY USER SECTION AT BOTTOM
// =============================================================================

@Composable
private fun StickyUserSection(
    userName: String,
    userScore: Int,
    userRank: Int,
    totalUsers: Int
) {
    val percentile = if (totalUsers > 0) {
        ((totalUsers - userRank + 1).toFloat() / totalUsers * 100).toInt().coerceIn(1, 99)
    } else 15

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NeonGreen)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // YOU label with lightning bolt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "YOU",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF0D1F14),
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF0D1F14),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Lightning bolt icon
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = null,
                    tint = Color(0xFF0D1F14),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Score and Percentile
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatNumber(userScore),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0D1F14),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Top $percentile%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF0D1F14).copy(alpha = 0.7f)
                )
            }
        }
    }
}

// =============================================================================
// SUPPORT BOTTOM SHEET
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportBottomSheet(
    sheetState: SheetState,
    entry: LeaderboardEntryEntity,
    onDismiss: () -> Unit,
    onBoost: () -> Unit,
    onRespect: () -> Unit,
    canBoost: Boolean,
    canRespect: Boolean,
    isDarkTheme: Boolean,
    cardBackgroundColor: Color,
    textPrimaryColor: Color,
    textSecondaryColor: Color
) {
    val sheetBackgroundColor = if (isDarkTheme) DarkCardBackgroundElevated else Color.White

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBackgroundColor,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Support ${entry.displayName}",
                style = MaterialTheme.typography.titleMedium,
                color = textPrimaryColor,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Show your appreciation for their growth journey",
                style = MaterialTheme.typography.bodySmall,
                color = textSecondaryColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Boost button
                SupportActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.RocketLaunch,
                    title = "Boost",
                    description = "Power up their journey",
                    count = entry.boostsReceived,
                    color = NeonGreen,
                    enabled = canBoost,
                    textPrimaryColor = textPrimaryColor,
                    textSecondaryColor = textSecondaryColor,
                    cardBackgroundColor = cardBackgroundColor,
                    onClick = onBoost
                )

                // Respect button
                SupportActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.ThumbUp,
                    title = "Respect",
                    description = "Acknowledge dedication",
                    count = entry.respectsReceived,
                    color = ProdyTertiary,
                    enabled = canRespect,
                    textPrimaryColor = textPrimaryColor,
                    textSecondaryColor = textSecondaryColor,
                    cardBackgroundColor = cardBackgroundColor,
                    onClick = onRespect
                )
            }

            // Rate limit warning
            if (!canBoost || !canRespect) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = textSecondaryColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Daily support limits help keep interactions meaningful",
                        style = MaterialTheme.typography.labelSmall,
                        color = textSecondaryColor.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SupportActionButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    count: Int,
    color: Color,
    enabled: Boolean,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    cardBackgroundColor: Color,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.5f

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .alpha(alpha),
        color = cardBackgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon with count badge
            Box {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Count badge
                if (count > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (count > 99) "99+" else count.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )

            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = textSecondaryColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            if (!enabled) {
                Text(
                    text = "Limit reached today",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFBF3B3B).copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

// =============================================================================
// HELPER FUNCTIONS
// =============================================================================

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
        number >= 10000 -> String.format("%.1fk", number / 1000.0)
        number >= 1000 -> String.format("%.1fk", number / 1000.0)
        else -> number.toString()
    }
}

private fun getCurrentUserName(leaderboard: List<LeaderboardEntryEntity>): String {
    return leaderboard.find { it.isCurrentUser }?.displayName ?: "Alex Morgan"
}

private fun getCurrentUserScore(leaderboard: List<LeaderboardEntryEntity>): Int {
    return leaderboard.find { it.isCurrentUser }?.totalPoints ?: 8450
}

// Extension function to calculate luminance
private fun Color.luminance(): Float {
    val r = red
    val g = green
    val b = blue
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}
