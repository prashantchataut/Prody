package com.prody.prashant.ui.screens.stats
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.prody.prashant.ui.theme.isDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import com.prody.prashant.ui.components.AmbientBackground
import com.prody.prashant.ui.components.FloatingParticles
import com.prody.prashant.ui.components.StreakFlame
import com.prody.prashant.ui.components.getCurrentTimeOfDay
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

// =============================================================================
// DESIGN SYSTEM CONSTANTS - Stats Screen Redesign
// Using theme colors from Color.kt for consistent design language
// =============================================================================

// Vibrant Neon Green Accent - Using theme accent
private val NeonGreen = ProdyAccentGreen
private val NeonGreenDark = ProdyAccentGreenDark
private val NeonGreenLight = ProdyAccentGreenLight

// Dark Mode Card Background - Using theme colors (deep teal #0D2826 based)
private val DarkCardBackground = ProdySurfaceVariantDark
private val DarkCardBackgroundElevated = ProdySurfaceContainerDark
private val DarkBackground = ProdyBackgroundDark

// Light Mode Card Background - Using theme colors (off-white #F0F4F3 based)
private val LightCardBackground = ProdySurfaceVariantLight
private val LightCardBackgroundElevated = ProdySurfaceContainerLight
private val LightBackground = ProdyBackgroundLight

// Leaderboard Tier Colors (Animated Banner Colors) - Using theme colors
private val GoldBanner = LeaderboardGold
private val GoldBannerLight = LeaderboardGoldLight
private val GoldBannerDark = LeaderboardGoldDark

private val SilverBanner = LeaderboardSilver
private val SilverBannerLight = LeaderboardSilverLight
private val SilverBannerDark = LeaderboardSilverDark

private val BronzeBanner = LeaderboardBronze
private val BronzeBannerLight = LeaderboardBronzeLight
private val BronzeBannerDark = LeaderboardBronzeDark

// =============================================================================
// MAIN STATS SCREEN
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isDarkTheme()

    // Premium theme colors using MaterialTheme
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceElevated = MaterialTheme.colorScheme.surfaceVariant
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val textTertiary = if (isDark) ProdyTextTertiaryDark else ProdyTextTertiaryLight
    val accentColor = MaterialTheme.colorScheme.primary
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    // Leaderboard tier colors
    val goldColor = LeaderboardGold
    val silverColor = LeaderboardSilver
    val bronzeColor = LeaderboardBronze

    // Pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }

    // Support bottom sheet state
    var showSupportSheet by remember { mutableStateOf(false) }
    var selectedUserForSupport by remember { mutableStateOf<LeaderboardEntryEntity?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // Filter dialog state
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilterPeriod by remember { mutableStateOf("This Week") }

    // Entry animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.refresh()
            delay(1500)
            isRefreshing = false
        }
    }

    // Background colors using MaterialTheme
    val cardBackgroundColor = MaterialTheme.colorScheme.surface
    val cardElevatedColor = MaterialTheme.colorScheme.surfaceVariant
    val textPrimaryColor = MaterialTheme.colorScheme.onSurface
    val textSecondaryColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textTertiaryColor = if (isDark) ProdyTextTertiaryDark else ProdyTextTertiaryLight

    // Support bottom sheet
    if (showSupportSheet && selectedUserForSupport != null) {
        PremiumSupportBottomSheet(
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
            isDarkTheme = isDark,
            surfaceColor = surfaceColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            accentColor = accentColor
        )
    }

    // Stats filter dialog
    if (showFilterDialog) {
        StatsFilterDialog(
            selectedPeriod = selectedFilterPeriod,
            onPeriodSelected = { period ->
                selectedFilterPeriod = period
                viewModel.setFilterPeriod(period)
            },
            onDismiss = { showFilterDialog = false },
            isDarkTheme = isDark,
            surfaceColor = surfaceColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            accentColor = accentColor
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Magical ambient background for immersive stats experience
        AmbientBackground(
            modifier = Modifier.fillMaxSize(),
            timeOfDay = getCurrentTimeOfDay(),
            intensity = 0.15f
        )

        // Subtle floating particles for achievement-oriented atmosphere
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            particleCount = 8,
            particleColor = NeonGreen.copy(alpha = 0.3f)
        )

        // Error state handling
        if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = textPrimary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = uiState.error ?: "Something went wrong",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.refresh() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Retry",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        } else {
            PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true },
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Header Section
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400)) + slideInVertically(
                            initialOffsetY = { -it / 4 },
                            animationSpec = tween(400, easing = EaseOutCubic)
                        )
                    ) {
                        PremiumStatsHeader(
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            surfaceColor = surfaceColor,
                            accentColor = accentColor,
                            onFilterClick = { showFilterDialog = true }
                        )
                    }
                }

                // User Stats Section (Streak, Rank, XP)
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400, delayMillis = 100)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(400, delayMillis = 100, easing = EaseOutCubic)
                        )
                    ) {
                        PremiumUserStatsSection(
                            currentStreak = uiState.currentStreak,
                            currentRank = uiState.currentRank,
                            totalXp = uiState.totalPoints,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            accentColor = accentColor
                        )
                    }
                }

                // Activity Pulse Section
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(400, delayMillis = 200, easing = EaseOutCubic)
                        )
                    ) {
                        PremiumActivityPulseSection(
                            weeklyData = uiState.weeklyProgress,
                            surfaceColor = surfaceColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            accentColor = accentColor,
                            isDarkTheme = isDark
                        )
                    }
                }

                // Summary Cards (Words, Entries, Messages)
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400, delayMillis = 300)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(400, delayMillis = 300, easing = EaseOutCubic)
                        )
                    ) {
                        PremiumSummaryCardsSection(
                            wordsWritten = uiState.totalWordsWritten,
                            entries = uiState.journalEntries,
                            messages = uiState.futureMessages,
                            surfaceColor = surfaceColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            accentColor = accentColor
                        )
                    }
                }

                // Top Performers Header
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400, delayMillis = 400))
                    ) {
                        PremiumTopPerformersHeader(
                            textPrimary = textPrimary,
                            accentColor = accentColor
                        )
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
                            PremiumLeaderboardItemRow(
                                entry = entry,
                                rank = index + 1,
                                isDarkTheme = isDark,
                                surfaceColor = surfaceColor,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                accentColor = accentColor,
                                goldColor = goldColor,
                                silverColor = silverColor,
                                bronzeColor = bronzeColor,
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
        }

        // Sticky User Section at Bottom
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            PremiumStickyUserSection(
                userName = getCurrentUserName(uiState.allTimeLeaderboard),
                userScore = getCurrentUserScore(uiState.allTimeLeaderboard),
                userRank = uiState.currentRank,
                totalUsers = uiState.allTimeLeaderboard.size,
                accentColor = accentColor
            )
        }
    }
}

// ============================================================================
// HEADER SECTION
// ============================================================================

@Composable
private fun PremiumStatsHeader(
    textPrimary: Color,
    textSecondary: Color,
    surfaceColor: Color,
    accentColor: Color,
    onFilterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp)
    ) {
        // Top Row: Dashboard label and filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DASHBOARD",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = textSecondary,
                letterSpacing = 1.5.sp
            )

            // Filter Button
            Surface(
                modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                color = surfaceColor,
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onFilterClick
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "THIS WEEK",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        color = textSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = ProdyIcons.Outlined.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Your",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            color = textPrimary
        )
        Text(
            text = "Impact",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            color = textPrimary
        )
    }
}

// ============================================================================
// USER STATS SECTION (Streak, Rank, XP)
// ============================================================================

@Composable
private fun PremiumUserStatsSection(
    currentStreak: Int,
    currentRank: Int,
    totalXp: Int,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Active Streak (Large, prominent, neon green) with magical flame
        Column {
            // Animated streak number with flame
            val animatedStreak by animateIntAsState(
                targetValue = currentStreak,
                animationSpec = tween(1200, easing = EaseOutCubic),
                label = "streak_animation"
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = animatedStreak.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 72.sp,
                        lineHeight = 72.sp
                    ),
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
                // Magical animated flame that grows with streak
                Box(modifier = Modifier.size(48.dp)) {
                    StreakFlame(
                        streakDays = currentStreak,
                        size = 48.dp
                    )
                }
            }
            Text(
                text = "ACTIVE STREAK",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = textSecondary,
                letterSpacing = 1.sp
            )
        }

        // Right side stats: Global Rank and Total XP
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Global Rank
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (currentRank > 0) "#${formatNumber(currentRank)}" else "-",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = textPrimary
                )
                Text(
                    text = "GLOBAL RANK",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    color = textSecondary,
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
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = textPrimary
                )
                Text(
                    text = "TOTAL XP",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    color = textSecondary,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// ============================================================================
// ACTIVITY PULSE SECTION
// ============================================================================

@Composable
private fun PremiumActivityPulseSection(
    weeklyData: List<Int>,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp)),
        color = surfaceColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ProdyIcons.ShowChart,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Activity Pulse",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = textPrimary
                    )
                }
                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 0.dp
                ) {
                    Text(
                        text = "LAST 7 DAYS",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        color = accentColor,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Wave Visualization
            PremiumActivityPulseVisualization(
                data = weeklyData,
                accentColor = accentColor,
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
private fun PremiumActivityPulseVisualization(
    data: List<Int>,
    accentColor: Color,
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
    val inactiveColor = if (isDarkTheme) ProdyOutlineDark else ProdyOutlineLight

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
                    if (isDarkTheme) ActivityPulseBackground else ActivityPulseBackgroundLight
                }

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, finalBarHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }
    }
}

// ============================================================================
// SUMMARY CARDS SECTION
// ============================================================================

@Composable
private fun PremiumSummaryCardsSection(
    wordsWritten: Int,
    entries: Int,
    messages: Int,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PremiumSummaryCard(
            value = wordsWritten,
            label = "Words",
            icon = ProdyIcons.EditNote,
            iconColor = Color(0xFFFFD166), // Energetic amber
            surfaceColor = surfaceColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            modifier = Modifier.weight(1f)
        )
        PremiumSummaryCard(
            value = entries,
            label = "Entries",
            icon = ProdyIcons.AutoStories,
            iconColor = MoodCalm, // Serene blue
            surfaceColor = surfaceColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            modifier = Modifier.weight(1f)
        )
        PremiumSummaryCard(
            value = messages,
            label = "Messages",
            icon = ProdyIcons.Email,
            iconColor = Color(0xFFB57EDC), // Premium violet
            surfaceColor = surfaceColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PremiumSummaryCard(
    value: Int,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier
) {
    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        isAnimated = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isAnimated) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "card_value_animation"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp)),
        color = surfaceColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatNumber(animatedValue),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = textPrimary
            )
            Text(
                text = label,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = textSecondary
            )
        }
    }
}

// ============================================================================
// TOP PERFORMERS SECTION
// ============================================================================

@Composable
private fun PremiumTopPerformersHeader(
    textPrimary: Color,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = ProdyIcons.EmojiEvents,
            contentDescription = null,
            tint = Color(0xFFD4AF37), // Gold
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Top Performers",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = textPrimary
        )
    }
}

// ============================================================================
// LEADERBOARD ITEM ROW
// ============================================================================

@Composable
private fun PremiumLeaderboardItemRow(
    entry: LeaderboardEntryEntity,
    rank: Int,
    isDarkTheme: Boolean,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color,
    goldColor: Color,
    silverColor: Color,
    bronzeColor: Color,
    onSupportClick: (LeaderboardEntryEntity) -> Unit
) {
    val isTopThree = rank <= 3

    val rankColor = when (rank) {
        1 -> goldColor
        2 -> silverColor
        3 -> bronzeColor
        else -> textSecondary
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onSupportClick(entry) },
        color = if (isTopThree) rankColor.copy(alpha = 0.15f) else surfaceColor,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Badge
            if (isTopThree) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(rankColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            } else {
                Box(
                    modifier = Modifier.width(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Name and Badge - Fixed layout to prevent vertical text wrapping
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    // Display name with weight to allow proper ellipsis
                    Text(
                        text = entry.displayName,
                        fontFamily = PoppinsFamily,
                        fontWeight = if (isTopThree) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 15.sp,
                        color = if (isTopThree) rankColor else textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // CHAMPION badge for rank 1 - fixed width container to prevent text wrapping
                    if (rank == 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = goldColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            tonalElevation = 0.dp,
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CHAMPION",
                                    fontFamily = PoppinsFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    color = goldColor,
                                    letterSpacing = 0.5.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
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
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = accentColor
                )
                Icon(
                    imageVector = ProdyIcons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ============================================================================
// STICKY USER SECTION
// ============================================================================

@Composable
private fun PremiumStickyUserSection(
    userName: String,
    userScore: Int,
    userRank: Int,
    totalUsers: Int,
    accentColor: Color
) {
    val percentile = if (totalUsers > 0) {
        ((totalUsers - userRank + 1).toFloat() / totalUsers * 100).toInt().coerceIn(1, 99)
    } else 15

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = accentColor,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // YOU label with name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    tonalElevation = 0.dp
                ) {
                    Text(
                        text = "YOU",
                        style = MaterialTheme.typography.labelMedium,
                        color = ProdyTextOnAccentLight,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Icon(
                    imageVector = ProdyIcons.Bolt,
                    contentDescription = null,
                    tint = ProdyTextOnAccentLight,
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
                    color = ProdyTextOnAccentLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Top $percentile%",
                    style = MaterialTheme.typography.labelSmall,
                    color = ProdyTextOnAccentLight.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ============================================================================
// SUPPORT BOTTOM SHEET
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumSupportBottomSheet(
    sheetState: SheetState,
    entry: LeaderboardEntryEntity,
    onDismiss: () -> Unit,
    onBoost: () -> Unit,
    onRespect: () -> Unit,
    canBoost: Boolean,
    canRespect: Boolean,
    isDarkTheme: Boolean,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    val sheetBackgroundColor = if (isDarkTheme) ProdySurfaceVariantDark else ProdySurfaceLight

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBackgroundColor,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(textSecondary.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Support ${entry.displayName}",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Show your appreciation for their growth journey",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PremiumSupportActionButton(
                    modifier = Modifier.weight(1f),
                    icon = ProdyIcons.RocketLaunch,
                    title = "Boost",
                    description = "Power up their journey",
                    count = entry.boostsReceived,
                    color = accentColor,
                    enabled = canBoost,
                    surfaceColor = surfaceColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = onBoost
                )

                PremiumSupportActionButton(
                    modifier = Modifier.weight(1f),
                    icon = ProdyIcons.ThumbUp,
                    title = "Respect",
                    description = "Acknowledge dedication",
                    count = entry.respectsReceived,
                    color = Color(0xFF6CB4D4), // Serene blue
                    enabled = canRespect,
                    surfaceColor = surfaceColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
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
                        imageVector = ProdyIcons.Outlined.Schedule,
                        contentDescription = null,
                        tint = textSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Daily support limits help keep interactions meaningful",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        color = textSecondary.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumSupportActionButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    count: Int,
    color: Color,
    enabled: Boolean,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.5f

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .alpha(alpha),
        color = surfaceColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp
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
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Count badge
                if (count > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (count > 99) "99+" else count.toString(),
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Text(
                text = title,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = color
            )

            Text(
                text = description,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            if (!enabled) {
                Text(
                    text = "Limit reached today",
                    style = MaterialTheme.typography.labelSmall,
                    color = ProdyError.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

// ============================================================================
// STATS FILTER DIALOG
// ============================================================================

@Composable
private fun StatsFilterDialog(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    val dialogBgColor = if (isDarkTheme) ProdySurfaceVariantDark else ProdySurfaceLight

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBgColor,
        title = {
            Text(
                text = "Filter Stats",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textPrimary
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Choose a time range to filter your stats dashboard.",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = textSecondary
                )

                // Filter options
                val filterOptions = listOf("This Week", "This Month", "Last 3 Months", "This Year", "All Time")
                filterOptions.forEach { option ->
                    val isSelected = option == selectedPeriod
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onPeriodSelected(option)
                                onDismiss()
                            },
                        color = if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                fontFamily = PoppinsFamily,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp,
                                color = if (isSelected) accentColor else textPrimary
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = ProdyIcons.Check,
                                    contentDescription = "Selected",
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Done",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
            }
        }
    )
}

// ============================================================================
// HELPER FUNCTIONS
// ============================================================================

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
        number >= 10000 -> String.format("%.1fk", number / 1000.0)
        number >= 1000 -> String.format("%.1fk", number / 1000.0)
        else -> number.toString()
    }
}

private fun getCurrentUserName(leaderboard: List<LeaderboardEntryEntity>): String {
    return leaderboard.find { it.isCurrentUser }?.displayName ?: "You"
}

private fun getCurrentUserScore(leaderboard: List<LeaderboardEntryEntity>): Int {
    return leaderboard.find { it.isCurrentUser }?.totalPoints ?: 0
}
