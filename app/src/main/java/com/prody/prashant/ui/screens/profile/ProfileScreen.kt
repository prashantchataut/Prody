package com.prody.prashant.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
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
 * Profile Screen - Identity & Trophy Room - Premium Phase 2 Redesign
 *
 * A completely redesigned gamified profile experience featuring:
 *
 * Design Philosophy:
 * - Extreme minimalism, flat design - NO shadows, gradients, or hi-fi elements
 * - Deep teal dark (#0D2826), clean off-white light (#F0F4F3)
 * - Vibrant neon green accent (#36F97F) for interactive elements
 * - Poppins typography throughout
 * - 8dp grid spacing system
 *
 * Features:
 * - Avatar with animated neon green progress ring
 * - DEV and BETA PIONEER badges
 * - Key metrics (Level, Streak, Words) with clean cards
 * - Story of Growth narrative section
 * - Trophy Room achievement showcase
 * - Weekly AI Insights card
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
    val isDarkMode = isSystemInDarkTheme()

    // Premium theme colors
    val backgroundColor = if (isDarkMode) Color(0xFF0D2826) else Color(0xFFF0F4F3)
    val surfaceColor = if (isDarkMode) Color(0xFF1A3331) else Color(0xFFFFFFFF)
    val surfaceElevated = if (isDarkMode) Color(0xFF2A4240) else Color(0xFFF5F7F6)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF1A1A1A)
    val textSecondary = if (isDarkMode) Color(0xFFD3D8D7) else Color(0xFF6C757D)
    val textTertiary = if (isDarkMode) Color(0xFF8A9493) else Color(0xFF9CA3AF)
    val accentColor = Color(0xFF36F97F) // Vibrant neon green
    val dividerColor = if (isDarkMode) Color(0xFF3A5250) else Color(0xFFDEE2E6)

    // Entry animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
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
                PremiumProfileHeader(
                    onSettingsClick = onNavigateToSettings,
                    onEditClick = onNavigateToEditProfile,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    accentColor = accentColor
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
                    PremiumHeroSection(
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
                    PremiumKeyMetricsRow(
                        level = getLevelFromPoints(uiState.totalPoints),
                        streak = uiState.currentStreak,
                        wordsLearned = uiState.wordsLearned,
                        surfaceColor = surfaceColor,
                        textPrimary = textPrimary,
                        textTertiary = textTertiary,
                        accentColor = accentColor
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
                    PremiumStoryOfGrowthSection(
                        currentStreak = uiState.currentStreak,
                        longestStreak = uiState.longestStreak,
                        totalPoints = uiState.totalPoints,
                        journalEntries = uiState.journalEntries,
                        wordsLearned = uiState.wordsLearned,
                        daysOnPrody = uiState.daysOnPrody,
                        surfaceColor = surfaceColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        textTertiary = textTertiary,
                        accentColor = accentColor
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
                    PremiumWeeklyPatternSection(
                        weeklyPattern = uiState.weeklyPattern,
                        isLoading = uiState.isLoadingWeeklyPattern,
                        hasEnoughData = uiState.hasEnoughDataForPattern,
                        surfaceColor = surfaceColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        textTertiary = textTertiary,
                        accentColor = accentColor
                    )
                }
            }

            // Trophy Room Section
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, delayMillis = 300))
                ) {
                    PremiumTrophyRoomHeader(
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
                        PremiumFeaturedAchievementsRow(
                            achievements = uiState.unlockedAchievements.take(4),
                            surfaceElevated = surfaceElevated,
                            textPrimary = textPrimary,
                            accentColor = accentColor
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
                    PremiumRecentUnlocksSection(
                        unlockedAchievements = uiState.unlockedAchievements,
                        lockedAchievements = uiState.lockedAchievements.take(4),
                        surfaceColor = surfaceColor,
                        surfaceElevated = surfaceElevated,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        textTertiary = textTertiary
                    )
                }
            }

            // Growth Quote
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, delayMillis = 500))
                ) {
                    PremiumGrowthQuoteCard(
                        textSecondary = textSecondary,
                        accentColor = accentColor
                    )
                }
            }
        }
    }
}

// ============================================================================
// HEADER COMPONENT
// ============================================================================

@Composable
private fun PremiumProfileHeader(
    onSettingsClick: () -> Unit,
    onEditClick: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Identity Room",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = textPrimary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Profile",
                    tint = textSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ============================================================================
// HERO SECTION - Avatar, Name, Badges
// ============================================================================

@Composable
private fun PremiumHeroSection(
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
            .padding(horizontal = 24.dp, vertical = 16.dp),
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
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = textPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Badges Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isDev) {
                PremiumDevBadge(accentColor = accentColor)
            }
            if (isBetaPioneer) {
                PremiumBetaPioneerBadge()
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
            color = accentColor.copy(alpha = glowAlpha * 0.15f),
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
private fun PremiumDevBadge(accentColor: Color) {
    Surface(
        color = accentColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Code,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "DEV",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = accentColor,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun PremiumBetaPioneerBadge() {
    val badgeColor = Color(0xFFB57EDC) // Premium violet

    Surface(
        color = badgeColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Rocket,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "BETA PIONEER",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = badgeColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ============================================================================
// KEY METRICS ROW
// ============================================================================

@Composable
private fun PremiumKeyMetricsRow(
    level: Int,
    streak: Int,
    wordsLearned: Int,
    surfaceColor: Color,
    textPrimary: Color,
    textTertiary: Color,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PremiumMetricCard(
            label = "Level",
            value = level.toString(),
            icon = Icons.Filled.TrendingUp,
            iconColor = accentColor,
            surfaceColor = surfaceColor,
            textPrimary = textPrimary,
            textTertiary = textTertiary,
            modifier = Modifier.weight(1f)
        )
        PremiumMetricCard(
            label = "Streak",
            value = streak.toString(),
            icon = Icons.Filled.LocalFireDepartment,
            iconColor = Color(0xFFE65C2C), // Fire orange
            surfaceColor = surfaceColor,
            textPrimary = textPrimary,
            textTertiary = textTertiary,
            modifier = Modifier.weight(1f)
        )
        PremiumMetricCard(
            label = "Words",
            value = formatCompactNumber(wordsLearned),
            icon = Icons.Filled.School,
            iconColor = Color(0xFFFFD166), // Energetic amber
            surfaceColor = surfaceColor,
            textPrimary = textPrimary,
            textTertiary = textTertiary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PremiumMetricCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    surfaceColor: Color,
    textPrimary: Color,
    textTertiary: Color,
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
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp)),
        color = surfaceColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp // Flat design
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
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = textPrimary
            )
            Text(
                text = label,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = textTertiary
            )
        }
    }
}

// ============================================================================
// STORY OF GROWTH SECTION
// ============================================================================

@Composable
private fun PremiumStoryOfGrowthSection(
    currentStreak: Int,
    longestStreak: Int,
    totalPoints: Int,
    journalEntries: Int,
    wordsLearned: Int,
    daysOnPrody: Int,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    textTertiary: Color,
    accentColor: Color
) {
    // Determine the highlight achievement
    val highlight = remember(currentStreak, longestStreak, journalEntries, wordsLearned, totalPoints) {
        when {
            currentStreak >= longestStreak && currentStreak >= 7 -> GrowthHighlight(
                title = "Consistency Master",
                description = "You're on your longest streak ever!",
                icon = Icons.Filled.LocalFireDepartment,
                color = Color(0xFFE65C2C)
            )
            journalEntries >= 30 -> GrowthHighlight(
                title = "Dedicated Writer",
                description = "$journalEntries journal entries and counting",
                icon = Icons.Filled.AutoStories,
                color = Color(0xFF6CB4D4)
            )
            wordsLearned >= 100 -> GrowthHighlight(
                title = "Word Collector",
                description = "Over $wordsLearned words mastered",
                icon = Icons.Filled.School,
                color = Color(0xFFFFD166)
            )
            totalPoints >= 500 -> GrowthHighlight(
                title = "Rising Star",
                description = "Earned ${formatCompactNumber(totalPoints)} XP",
                icon = Icons.Filled.Stars,
                color = Color(0xFFD4AF37)
            )
            currentStreak >= 3 -> GrowthHighlight(
                title = "Building Momentum",
                description = "$currentStreak day streak going strong",
                icon = Icons.Filled.LocalFireDepartment,
                color = Color(0xFFE65C2C)
            )
            else -> GrowthHighlight(
                title = "New Journey",
                description = "Every expert was once a beginner",
                icon = Icons.Filled.EmojiNature,
                color = Color(0xFF7EC8A3)
            )
        }
    }

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
            // Section Header
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
                        imageVector = Icons.Filled.AutoGraph,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Story of Growth",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Highlight Card
            Surface(
                color = highlight.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 0.dp
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
                            .size(48.dp)
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
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = highlight.color
                        )
                        Text(
                            text = highlight.description,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = textSecondary
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
                PremiumMiniStat(
                    value = daysOnPrody.toString(),
                    label = "Days Active",
                    textPrimary = textPrimary,
                    textTertiary = textTertiary
                )
                PremiumMiniStat(
                    value = journalEntries.toString(),
                    label = "Entries",
                    textPrimary = textPrimary,
                    textTertiary = textTertiary
                )
                PremiumMiniStat(
                    value = longestStreak.toString(),
                    label = "Best Streak",
                    textPrimary = textPrimary,
                    textTertiary = textTertiary
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
private fun PremiumMiniStat(
    value: String,
    label: String,
    textPrimary: Color,
    textTertiary: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = textPrimary
        )
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = textTertiary
        )
    }
}

// ============================================================================
// WEEKLY PATTERN SECTION
// ============================================================================

@Composable
private fun PremiumWeeklyPatternSection(
    weeklyPattern: WeeklyPatternResult?,
    isLoading: Boolean,
    hasEnoughData: Boolean,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    textTertiary: Color,
    accentColor: Color
) {
    val violetColor = Color(0xFF6B5CE7) // Premium violet for AI

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp)),
        color = surfaceColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(violetColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Psychology,
                            contentDescription = null,
                            tint = violetColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Weekly Insights",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = textPrimary
                        )
                        Text(
                            text = "AI-powered analysis",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            color = textTertiary
                        )
                    }
                }

                if (weeklyPattern?.isAiGenerated == true) {
                    Surface(
                        color = violetColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        tonalElevation = 0.dp
                    ) {
                        Text(
                            text = "AI",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = violetColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
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
                            color = violetColor,
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
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                        Text(
                            text = "to unlock AI insights",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            color = textTertiary
                        )
                    }
                }
                weeklyPattern != null -> {
                    Text(
                        text = weeklyPattern.summary,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = textSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = accentColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = weeklyPattern.suggestion,
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = textPrimary,
                                lineHeight = 20.sp
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
private fun PremiumTrophyRoomHeader(
    unlockedCount: Int,
    totalCount: Int,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val goldColor = Color(0xFFD4AF37)

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
                tint = goldColor,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = "Trophy Room",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = textPrimary
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
private fun PremiumFeaturedAchievementsRow(
    achievements: List<AchievementEntity>,
    surfaceElevated: Color,
    textPrimary: Color,
    accentColor: Color
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = achievements,
            key = { _, item -> item.id }
        ) { index, achievement ->
            PremiumFeaturedAchievementCard(
                achievement = achievement,
                surfaceElevated = surfaceElevated,
                textPrimary = textPrimary,
                index = index
            )
        }
    }
}

@Composable
private fun PremiumFeaturedAchievementCard(
    achievement: AchievementEntity,
    surfaceElevated: Color,
    textPrimary: Color,
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
            .scale(scale)
            .clip(RoundedCornerShape(20.dp)),
        color = surfaceElevated,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(rarity.color.copy(alpha = 0.15f))
                    .border(2.dp, rarity.color.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = achievementData?.icon ?: Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = rarity.color,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = achievement.name,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun PremiumRecentUnlocksSection(
    unlockedAchievements: List<AchievementEntity>,
    lockedAchievements: List<AchievementEntity>,
    surfaceColor: Color,
    surfaceElevated: Color,
    textPrimary: Color,
    textSecondary: Color,
    textTertiary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Recent Unlocks",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = textSecondary,
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
                PremiumCompactBadge(
                    achievement = achievement,
                    isUnlocked = true,
                    surfaceColor = surfaceElevated,
                    textPrimary = textPrimary,
                    textTertiary = textTertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            nextLocked.forEach { achievement ->
                PremiumCompactBadge(
                    achievement = achievement,
                    isUnlocked = false,
                    surfaceColor = surfaceColor,
                    textPrimary = textPrimary,
                    textTertiary = textTertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PremiumCompactBadge(
    achievement: AchievementEntity,
    isUnlocked: Boolean,
    surfaceColor: Color,
    textPrimary: Color,
    textTertiary: Color,
    modifier: Modifier = Modifier
) {
    val rarity = try {
        AchievementRarity.valueOf(achievement.rarity.uppercase())
    } catch (e: Exception) {
        AchievementRarity.COMMON
    }

    val achievementData = Achievements.getAchievementById(achievement.id)
    val lockedGray = Color(0xFF888888)

    Surface(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        color = if (isUnlocked) surfaceColor else surfaceColor.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) rarity.color.copy(alpha = 0.15f)
                        else lockedGray.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Icon(
                        imageVector = achievementData?.icon ?: Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = rarity.color,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = lockedGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = achievement.name,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = if (isUnlocked) textPrimary else textTertiary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// GROWTH QUOTE CARD
// ============================================================================

@Composable
private fun PremiumGrowthQuoteCard(
    textSecondary: Color,
    accentColor: Color
) {
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
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(accentColor.copy(alpha = 0.08f))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.FormatQuote,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = quote,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = textSecondary,
                fontStyle = FontStyle.Italic,
                lineHeight = 24.sp
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
