package com.prody.prashant.ui.screens.profile

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.isSystemInDarkTheme
import com.prody.prashant.R
import com.prody.prashant.data.ai.WeeklyPatternResult
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.ui.components.AmbientBackground
import com.prody.prashant.ui.components.FloatingParticles
import com.prody.prashant.ui.components.MoodBreathingHalo
import com.prody.prashant.ui.components.getCurrentTimeOfDay
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Profile Screen - Identity Room
 *
 * A premium gamified profile experience featuring:
 * - Clean minimalist design with neon green accents
 * - Avatar with animated progress ring
 * - DEV and BETA PIONEER badges
 * - Key metrics (Level, Streak, Words)
 * - Story of Growth narrative section
 * - Trophy Room achievement showcase
 *
 * Design: Gamified minimalism - no shadows, subtle gradients, clean typography
 */

// ============================================================================
// DESIGN SYSTEM COLORS - Identity Room Theme (Using Theme Colors)
// ============================================================================

private object IdentityRoomColors {
    // Dark Mode - Using theme colors
    val BackgroundDark = ProdyBackgroundDark
    val CardBackgroundDark = ProdySurfaceVariantDark
    val CardBackgroundElevatedDark = ProdySurfaceContainerDark
    val AccentGreen = ProdyAccentGreen
    val AccentGreenDim = ProdyAccentGreenDark
    val TextPrimaryDark = ProdyTextPrimaryDark
    val TextSecondaryDark = ProdyTextSecondaryDark
    val TextTertiaryDark = ProdyTextTertiaryDark
    val BorderDark = ProdyOutlineDark

    // Light Mode - Using theme colors
    val BackgroundLight = ProdyBackgroundLight
    val CardBackgroundLight = ProdySurfaceLight
    val CardBackgroundElevatedLight = ProdySurfaceContainerLight
    val AccentGreenLight = ProdyAccentGreen
    val TextPrimaryLight = ProdyTextPrimaryLight
    val TextSecondaryLight = ProdyTextSecondaryLight
    val TextTertiaryLight = ProdyTextTertiaryLight
    val BorderLight = ProdyOutlineLight

    // Badge Colors - Using theme colors
    val DevBadgeBackground = ProdySurfaceVariantDark
    val DevBadgeText = ProdyAccentGreen
    val BetaBadgeBackground = ProdyPremiumVioletContainer
    val BetaBadgeText = ProdyPremiumViolet

    // Achievement Rarity - Using theme colors
    val RarityCommon = com.prody.prashant.ui.theme.RarityCommon
    val RarityUncommon = com.prody.prashant.ui.theme.RarityUncommon
    val RarityRare = com.prody.prashant.ui.theme.RarityRare
    val RarityEpic = com.prody.prashant.ui.theme.RarityEpic
    val RarityLegendary = com.prody.prashant.ui.theme.RarityLegendary
}

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Determine if dark mode based on system theme
    val isDarkMode = isSystemInDarkTheme()

    // Entry animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) IdentityRoomColors.BackgroundDark
                else IdentityRoomColors.BackgroundLight
            )
    ) {
        // Magical ambient background for immersive profile experience
        AmbientBackground(
            modifier = Modifier.fillMaxSize(),
            timeOfDay = getCurrentTimeOfDay(),
            intensity = 0.1f
        )

        // Premium floating particles celebrating achievements
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            particleCount = 12,
            particleColor = IdentityRoomColors.AccentGreen.copy(alpha = 0.25f)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header with title and actions
            item {
                IdentityRoomHeader(
                    onSettingsClick = onNavigateToSettings,
                    onEditClick = onNavigateToEditProfile,
                    isDarkMode = isDarkMode
                )
            }

            // Hero Section - Avatar, Name, Badges
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        initialOffsetY = { -it / 4 },
                        animationSpec = tween(400, easing = EaseOutCubic)
                    )
                ) {
                    HeroSection(
                        displayName = uiState.displayName,
                        bio = uiState.bio,
                        level = getLevelFromPoints(uiState.totalPoints),
                        levelProgress = calculateLevelProgress(uiState.totalPoints),
                        isDev = true, // TODO: Get from user state
                        isBetaPioneer = true, // TODO: Get from user state
                        isDarkMode = isDarkMode,
                        onEditClick = onNavigateToEditProfile
                    )
                }
            }

            // Key Metrics Row
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, delayMillis = 100)) + slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(400, delayMillis = 100, easing = EaseOutCubic)
                    )
                ) {
                    KeyMetricsRow(
                        level = getLevelFromPoints(uiState.totalPoints),
                        streak = uiState.currentStreak,
                        wordsLearned = uiState.wordsLearned,
                        isDarkMode = isDarkMode
                    )
                }
            }

            // Story of Growth Section
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(400, delayMillis = 200, easing = EaseOutCubic)
                    )
                ) {
                    StoryOfGrowthSection(
                        currentStreak = uiState.currentStreak,
                        longestStreak = uiState.longestStreak,
                        totalPoints = uiState.totalPoints,
                        journalEntries = uiState.journalEntries,
                        wordsLearned = uiState.wordsLearned,
                        daysOnPrody = uiState.daysOnPrody,
                        isDarkMode = isDarkMode
                    )
                }
            }

            // Weekly AI Pattern Card
            item {
                AnimatedVisibility(
                    visible = isVisible && (uiState.weeklyPattern != null || uiState.isLoadingWeeklyPattern),
                    enter = fadeIn(tween(400, delayMillis = 250)) + slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(400, delayMillis = 250, easing = EaseOutCubic)
                    )
                ) {
                    WeeklyPatternSection(
                        weeklyPattern = uiState.weeklyPattern,
                        isLoading = uiState.isLoadingWeeklyPattern,
                        hasEnoughData = uiState.hasEnoughDataForPattern,
                        isDarkMode = isDarkMode
                    )
                }
            }

            // Trophy Room Section
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, delayMillis = 300))
                ) {
                    TrophyRoomHeader(
                        unlockedCount = uiState.unlockedAchievements.size,
                        totalCount = uiState.unlockedAchievements.size + uiState.lockedAchievements.size,
                        isDarkMode = isDarkMode,
                        onClick = onNavigateToAchievements
                    )
                }
            }

            // Featured Achievements
            if (uiState.unlockedAchievements.isNotEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400, delayMillis = 350))
                    ) {
                        FeaturedAchievementsRow(
                            achievements = uiState.unlockedAchievements.take(4),
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }

            // Recent Unlocks Section
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, delayMillis = 400))
                ) {
                    RecentUnlocksSection(
                        unlockedAchievements = uiState.unlockedAchievements,
                        lockedAchievements = uiState.lockedAchievements.take(4),
                        isDarkMode = isDarkMode
                    )
                }
            }

            // Growth Quote
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, delayMillis = 500))
                ) {
                    GrowthQuoteCard(isDarkMode = isDarkMode)
                }
            }
        }
    }
}

// ============================================================================
// HEADER COMPONENT
// ============================================================================

@Composable
private fun IdentityRoomHeader(
    onSettingsClick: () -> Unit,
    onEditClick: () -> Unit,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Identity Room",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) IdentityRoomColors.TextPrimaryDark
                    else IdentityRoomColors.TextPrimaryLight
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Profile",
                    tint = if (isDarkMode) IdentityRoomColors.TextSecondaryDark
                           else IdentityRoomColors.TextSecondaryLight
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = if (isDarkMode) IdentityRoomColors.TextSecondaryDark
                           else IdentityRoomColors.TextSecondaryLight
                )
            }
        }
    }
}

// ============================================================================
// HERO SECTION - Avatar, Name, Badges
// ============================================================================

@Composable
private fun HeroSection(
    displayName: String,
    bio: String,
    level: Int,
    levelProgress: Float,
    isDev: Boolean,
    isBetaPioneer: Boolean,
    isDarkMode: Boolean,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with Progress Ring and Magical Breathing Halo
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp) // Slightly larger to accommodate halo
        ) {
            // Magical breathing halo effect around the avatar
            MoodBreathingHalo(
                modifier = Modifier.size(156.dp),
                baseColor = if (isDarkMode) IdentityRoomColors.AccentGreen.copy(alpha = 0.3f)
                            else IdentityRoomColors.AccentGreenLight.copy(alpha = 0.25f),
                pulseIntensity = 0.15f
            )

            // Avatar container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                // Animated Progress Ring
                NeonProgressRing(
                    progress = levelProgress,
                    modifier = Modifier.fillMaxSize(),
                    accentColor = if (isDarkMode) IdentityRoomColors.AccentGreen
                                  else IdentityRoomColors.AccentGreenLight
                )

                // Avatar Circle
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDarkMode) IdentityRoomColors.CardBackgroundDark
                            else IdentityRoomColors.CardBackgroundLight
                        )
                        .border(
                            width = 3.dp,
                            color = if (isDarkMode) IdentityRoomColors.BorderDark
                                    else IdentityRoomColors.BorderLight,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = if (isDarkMode) IdentityRoomColors.TextSecondaryDark
                               else IdentityRoomColors.TextSecondaryLight
                    )
                }

                // Level Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDarkMode) IdentityRoomColors.AccentGreen
                            else IdentityRoomColors.AccentGreenLight
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Display Name
        Text(
            text = displayName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) IdentityRoomColors.TextPrimaryDark
                    else IdentityRoomColors.TextPrimaryLight
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Badges Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isDev) {
                DevBadge()
            }
            if (isBetaPioneer) {
                BetaPioneerBadge()
            }
        }

        // Bio Section
        if (bio.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            BioSection(
                bio = bio,
                isDarkMode = isDarkMode,
                onEditClick = onEditClick
            )
        }
    }
}

@Composable
private fun BioSection(
    bio: String,
    isDarkMode: Boolean,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        color = if (isDarkMode) IdentityRoomColors.CardBackgroundDark.copy(alpha = 0.5f)
                else IdentityRoomColors.CardBackgroundLight.copy(alpha = 0.8f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.FormatQuote,
                contentDescription = null,
                tint = if (isDarkMode) IdentityRoomColors.AccentGreen
                       else IdentityRoomColors.AccentGreenLight,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) IdentityRoomColors.TextSecondaryDark
                        else IdentityRoomColors.TextSecondaryLight,
                modifier = Modifier.weight(1f),
                lineHeight = 22.sp
            )
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit bio",
                tint = if (isDarkMode) IdentityRoomColors.TextTertiaryDark
                       else IdentityRoomColors.TextTertiaryLight,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun NeonProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    accentColor: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "progress_ring"
    )

    // Subtle glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = 6.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Background ring
        drawCircle(
            color = accentColor.copy(alpha = 0.15f),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Glow effect (subtle)
        drawCircle(
            color = accentColor.copy(alpha = glowAlpha * 0.2f),
            radius = radius + 4.dp.toPx(),
            center = center,
            style = Stroke(width = 8.dp.toPx())
        )

        // Progress arc
        drawArc(
            color = accentColor,
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
private fun DevBadge() {
    Surface(
        color = IdentityRoomColors.DevBadgeBackground,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Code,
                contentDescription = null,
                tint = IdentityRoomColors.DevBadgeText,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "DEV",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = IdentityRoomColors.DevBadgeText,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun BetaPioneerBadge() {
    Surface(
        color = IdentityRoomColors.BetaBadgeBackground,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Rocket,
                contentDescription = null,
                tint = IdentityRoomColors.BetaBadgeText,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "BETA PIONEER",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = IdentityRoomColors.BetaBadgeText,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ============================================================================
// KEY METRICS ROW
// ============================================================================

@Composable
private fun KeyMetricsRow(
    level: Int,
    streak: Int,
    wordsLearned: Int,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            label = "Level",
            value = level.toString(),
            icon = Icons.Filled.TrendingUp,
            iconColor = IdentityRoomColors.AccentGreen,
            isDarkMode = isDarkMode,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label = "Streak",
            value = streak.toString(),
            icon = Icons.Filled.LocalFireDepartment,
            iconColor = StreakFire,
            isDarkMode = isDarkMode,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label = "Words",
            value = formatCompactNumber(wordsLearned),
            icon = Icons.Filled.School,
            iconColor = MoodMotivated,
            isDarkMode = isDarkMode,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    isDarkMode: Boolean,
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
        label = "metric_scale"
    )

    Surface(
        modifier = modifier.scale(scale),
        color = if (isDarkMode) IdentityRoomColors.CardBackgroundDark
                else IdentityRoomColors.CardBackgroundLight,
        shape = RoundedCornerShape(16.dp)
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
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) IdentityRoomColors.TextPrimaryDark
                        else IdentityRoomColors.TextPrimaryLight
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDarkMode) IdentityRoomColors.TextTertiaryDark
                        else IdentityRoomColors.TextTertiaryLight
            )
        }
    }
}

// ============================================================================
// STORY OF GROWTH SECTION
// ============================================================================

@Composable
private fun StoryOfGrowthSection(
    currentStreak: Int,
    longestStreak: Int,
    totalPoints: Int,
    journalEntries: Int,
    wordsLearned: Int,
    daysOnPrody: Int,
    isDarkMode: Boolean
) {
    // Determine the highlight achievement
    val highlight = remember(currentStreak, longestStreak, journalEntries, wordsLearned, totalPoints) {
        when {
            currentStreak >= longestStreak && currentStreak >= 7 -> GrowthHighlight(
                title = "Consistency Master",
                description = "You're on your longest streak ever!",
                icon = Icons.Filled.LocalFireDepartment,
                color = StreakFire
            )
            journalEntries >= 30 -> GrowthHighlight(
                title = "Dedicated Writer",
                description = "$journalEntries journal entries and counting",
                icon = Icons.Filled.AutoStories,
                color = MoodCalm
            )
            wordsLearned >= 100 -> GrowthHighlight(
                title = "Word Collector",
                description = "Over $wordsLearned words mastered",
                icon = Icons.Filled.School,
                color = MoodMotivated
            )
            totalPoints >= 500 -> GrowthHighlight(
                title = "Rising Star",
                description = "Earned ${formatCompactNumber(totalPoints)} XP",
                icon = Icons.Filled.Stars,
                color = GoldTier
            )
            currentStreak >= 3 -> GrowthHighlight(
                title = "Building Momentum",
                description = "$currentStreak day streak going strong",
                icon = Icons.Filled.LocalFireDepartment,
                color = StreakFire
            )
            else -> GrowthHighlight(
                title = "New Journey",
                description = "Every expert was once a beginner",
                icon = Icons.Filled.EmojiNature,
                color = MoodGrateful
            )
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        color = if (isDarkMode) IdentityRoomColors.CardBackgroundDark
                else IdentityRoomColors.CardBackgroundLight,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDarkMode) IdentityRoomColors.AccentGreen.copy(alpha = 0.15f)
                            else IdentityRoomColors.AccentGreenLight.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoGraph,
                        contentDescription = null,
                        tint = if (isDarkMode) IdentityRoomColors.AccentGreen
                               else IdentityRoomColors.AccentGreenLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Story of Growth",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) IdentityRoomColors.TextPrimaryDark
                            else IdentityRoomColors.TextPrimaryLight
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Highlight Card
            Surface(
                color = highlight.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(highlight.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = highlight.icon,
                            contentDescription = null,
                            tint = highlight.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = highlight.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = highlight.color
                        )
                        Text(
                            text = highlight.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkMode) IdentityRoomColors.TextSecondaryDark
                                    else IdentityRoomColors.TextSecondaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat(
                    value = daysOnPrody.toString(),
                    label = "Days Active",
                    isDarkMode = isDarkMode
                )
                MiniStat(
                    value = journalEntries.toString(),
                    label = "Entries",
                    isDarkMode = isDarkMode
                )
                MiniStat(
                    value = longestStreak.toString(),
                    label = "Best Streak",
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

private data class GrowthHighlight(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun MiniStat(
    value: String,
    label: String,
    isDarkMode: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) IdentityRoomColors.TextPrimaryDark
                    else IdentityRoomColors.TextPrimaryLight
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isDarkMode) IdentityRoomColors.TextTertiaryDark
                    else IdentityRoomColors.TextTertiaryLight
        )
    }
}

// ============================================================================
// WEEKLY PATTERN SECTION
// ============================================================================

@Composable
private fun WeeklyPatternSection(
    weeklyPattern: WeeklyPatternResult?,
    isLoading: Boolean,
    hasEnoughData: Boolean,
    isDarkMode: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        color = if (isDarkMode) IdentityRoomColors.CardBackgroundDark
                else IdentityRoomColors.CardBackgroundLight,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ProdyPremiumViolet.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Psychology,
                            contentDescription = null,
                            tint = ProdyPremiumViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Weekly Insights",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) IdentityRoomColors.TextPrimaryDark
                                    else IdentityRoomColors.TextPrimaryLight
                        )
                        Text(
                            text = "AI-powered analysis",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkMode) IdentityRoomColors.TextTertiaryDark
                                    else IdentityRoomColors.TextTertiaryLight
                        )
                    }
                }

                if (weeklyPattern?.isAiGenerated == true) {
                    Surface(
                        color = ProdyPremiumViolet.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ProdyPremiumViolet,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = ProdyPremiumViolet,
                            strokeWidth = 2.dp
                        )
                    }
                }
                !hasEnoughData -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Write 3+ entries this week",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkMode) IdentityRoomColors.TextSecondaryDark
                                    else IdentityRoomColors.TextSecondaryLight
                        )
                        Text(
                            text = "to unlock AI insights",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkMode) IdentityRoomColors.TextTertiaryDark
                                    else IdentityRoomColors.TextTertiaryLight
                        )
                    }
                }
                weeklyPattern != null -> {
                    Text(
                        text = weeklyPattern.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkMode) IdentityRoomColors.TextSecondaryDark
                                else IdentityRoomColors.TextSecondaryLight,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = ProdyTertiary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                tint = ProdyTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = weeklyPattern.suggestion,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDarkMode) IdentityRoomColors.TextPrimaryDark
                                        else IdentityRoomColors.TextPrimaryLight,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// TROPHY ROOM SECTION
// ============================================================================

@Composable
private fun TrophyRoomHeader(
    unlockedCount: Int,
    totalCount: Int,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = GoldTier,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Trophy Room",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) IdentityRoomColors.TextPrimaryDark
                        else IdentityRoomColors.TextPrimaryLight
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = if (isDarkMode) IdentityRoomColors.CardBackgroundDark
                        else IdentityRoomColors.CardBackgroundLight,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$unlockedCount / $totalCount",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkMode) IdentityRoomColors.TextSecondaryDark
                            else IdentityRoomColors.TextSecondaryLight,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "View all achievements",
                tint = if (isDarkMode) IdentityRoomColors.TextTertiaryDark
                       else IdentityRoomColors.TextTertiaryLight,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun FeaturedAchievementsRow(
    achievements: List<AchievementEntity>,
    isDarkMode: Boolean
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = achievements,
            key = { _, item -> item.id }
        ) { index, achievement ->
            FeaturedAchievementCard(
                achievement = achievement,
                isDarkMode = isDarkMode,
                index = index
            )
        }
    }
}

@Composable
private fun FeaturedAchievementCard(
    achievement: AchievementEntity,
    isDarkMode: Boolean,
    index: Int
) {
    val rarity = try {
        AchievementRarity.valueOf(achievement.rarity.uppercase())
    } catch (e: Exception) {
        AchievementRarity.COMMON
    }

    val achievementData = Achievements.getAchievementById(achievement.id)

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((index * 80).toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "achievement_scale"
    )

    Surface(
        modifier = Modifier
            .width(100.dp)
            .scale(scale),
        color = if (isDarkMode) IdentityRoomColors.CardBackgroundElevatedDark
                else IdentityRoomColors.CardBackgroundElevatedLight,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(rarity.color.copy(alpha = 0.15f))
                    .border(2.dp, rarity.color.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = achievementData?.icon ?: Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = rarity.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = achievement.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) IdentityRoomColors.TextPrimaryDark
                        else IdentityRoomColors.TextPrimaryLight,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun RecentUnlocksSection(
    unlockedAchievements: List<AchievementEntity>,
    lockedAchievements: List<AchievementEntity>,
    isDarkMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Recent Unlocks",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (isDarkMode) IdentityRoomColors.TextSecondaryDark
                    else IdentityRoomColors.TextSecondaryLight,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Show recent unlocked (last 2) and next locked (first 2)
            val recentUnlocked = unlockedAchievements.takeLast(2)
            val nextLocked = lockedAchievements.take(2)

            recentUnlocked.forEach { achievement ->
                CompactBadge(
                    achievement = achievement,
                    isUnlocked = true,
                    isDarkMode = isDarkMode,
                    modifier = Modifier.weight(1f)
                )
            }

            nextLocked.forEach { achievement ->
                CompactBadge(
                    achievement = achievement,
                    isUnlocked = false,
                    isDarkMode = isDarkMode,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CompactBadge(
    achievement: AchievementEntity,
    isUnlocked: Boolean,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val rarity = try {
        AchievementRarity.valueOf(achievement.rarity.uppercase())
    } catch (e: Exception) {
        AchievementRarity.COMMON
    }

    val achievementData = Achievements.getAchievementById(achievement.id)

    Surface(
        modifier = modifier,
        color = if (isDarkMode) {
            if (isUnlocked) IdentityRoomColors.CardBackgroundElevatedDark
            else IdentityRoomColors.CardBackgroundDark.copy(alpha = 0.5f)
        } else {
            if (isUnlocked) IdentityRoomColors.CardBackgroundLight
            else IdentityRoomColors.CardBackgroundElevatedLight.copy(alpha = 0.5f)
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) rarity.color.copy(alpha = 0.15f)
                        else AchievementLocked.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Icon(
                        imageVector = achievementData?.icon ?: Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = rarity.color,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = AchievementLocked,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = achievement.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (isUnlocked) {
                    if (isDarkMode) IdentityRoomColors.TextPrimaryDark
                    else IdentityRoomColors.TextPrimaryLight
                } else {
                    if (isDarkMode) IdentityRoomColors.TextTertiaryDark
                    else IdentityRoomColors.TextTertiaryLight
                },
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp
            )
        }
    }
}

// ============================================================================
// GROWTH QUOTE CARD
// ============================================================================

@Composable
private fun GrowthQuoteCard(isDarkMode: Boolean) {
    val quotes = remember {
        listOf(
            "Every step forward is progress.",
            "Your growth journey is unique and beautiful.",
            "Small daily improvements lead to stunning results.",
            "The path to wisdom begins with self-reflection.",
            "You're building something remarkable."
        )
    }
    val quote = remember { quotes.random() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isDarkMode) IdentityRoomColors.AccentGreen.copy(alpha = 0.08f)
                else IdentityRoomColors.AccentGreenLight.copy(alpha = 0.1f)
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.FormatQuote,
                contentDescription = null,
                tint = if (isDarkMode) IdentityRoomColors.AccentGreen
                       else IdentityRoomColors.AccentGreenLight,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) IdentityRoomColors.TextSecondaryDark
                        else IdentityRoomColors.TextSecondaryLight,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                lineHeight = 22.sp
            )
        }
    }
}

// ============================================================================
// HELPER FUNCTIONS
// ============================================================================

private fun formatCompactNumber(number: Int): String {
    return when {
        number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
        number >= 1000 -> String.format("%.1fk", number / 1000.0)
        else -> number.toString()
    }
}

private fun getLevelFromPoints(points: Int): Int {
    return when {
        points >= 10000 -> 10
        points >= 7500 -> 9
        points >= 5000 -> 8
        points >= 3500 -> 7
        points >= 2500 -> 6
        points >= 1500 -> 5
        points >= 1000 -> 4
        points >= 500 -> 3
        points >= 200 -> 2
        else -> 1
    }
}

private fun calculateLevelProgress(points: Int): Float {
    val level = getLevelFromPoints(points)
    val currentThreshold = getLevelThreshold(level)
    val nextThreshold = getLevelThreshold(level + 1)

    return if (nextThreshold > currentThreshold) {
        (points - currentThreshold).toFloat() / (nextThreshold - currentThreshold)
    } else {
        1f
    }
}

private fun getLevelThreshold(level: Int): Int {
    return when (level) {
        1 -> 0
        2 -> 200
        3 -> 500
        4 -> 1000
        5 -> 1500
        6 -> 2500
        7 -> 3500
        8 -> 5000
        9 -> 7500
        10 -> 10000
        else -> 15000
    }
}
