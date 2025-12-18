package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.ui.theme.PoppinsFamily
import kotlinx.coroutines.launch

// =============================================================================
// PRODY ONBOARDING - THEME-ALIGNED IMPLEMENTATION
// =============================================================================
// A sophisticated 6-screen onboarding flow featuring:
// - A design that adapts to the app's official ProdyTheme
// - Custom components: Logo, Pager Indicator, Buttons
// - Canvas-drawn concentric circles background
// - Full Light/Dark mode support via MaterialTheme
// =============================================================================

// =============================================================================
// DATA MODEL
// =============================================================================

private enum class OnboardingPageType {
    WELCOME,
    JOURNALING,
    GAMIFICATION_LEADERBOARD,
    GAMIFICATION_XP,
    DAILY_WISDOM,
    PERSONALIZED_INSIGHTS
}

// =============================================================================
// MAIN ONBOARDING SCREEN
// =============================================================================

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 6 })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            val pageType = OnboardingPageType.entries[page]
            when (pageType) {
                OnboardingPageType.WELCOME -> WelcomeScreen(
                    currentPage = page,
                    totalPages = 6,
                    onNext = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    },
                    onLogin = { /* Navigate to login */ }
                )
                OnboardingPageType.JOURNALING -> JournalingScreen(
                    currentPage = page,
                    totalPages = 6,
                    onSkip = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(5)
                        }
                    },
                    onBack = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page - 1)
                        }
                    },
                    onContinue = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    }
                )
                OnboardingPageType.GAMIFICATION_LEADERBOARD -> GamificationLeaderboardScreen(
                    currentPage = page,
                    totalPages = 6,
                    onSkip = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(5)
                        }
                    },
                    onContinue = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    }
                )
                OnboardingPageType.GAMIFICATION_XP -> GamificationXpScreen(
                    currentPage = page,
                    totalPages = 6,
                    onStartQuest = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    }
                )
                OnboardingPageType.DAILY_WISDOM -> DailyWisdomFeaturesScreen(
                    currentPage = page,
                    totalPages = 6,
                    onSkip = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(5)
                        }
                    },
                    onContinue = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    },
                    onMaybeLater = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    }
                )
                OnboardingPageType.PERSONALIZED_INSIGHTS -> PersonalizedInsightsScreen(
                    currentPage = page,
                    totalPages = 6,
                    onSkip = {
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    onEnableNotifications = {
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    onMaybeLater = {
                        viewModel.completeOnboarding()
                        onComplete()
                    }
                )
            }
        }
    }
}

// =============================================================================
// SCREEN 1: WELCOME SCREEN
// =============================================================================

@Composable
private fun WelcomeScreen(
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background concentric circles - centered behind logo
        ConcentricCirclesBackground(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.18f))

            // Prody Logo with green accent dot
            ProdyLogo(
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Title "Prody."
            Text(
                text = "Prody.",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Your daily companion for growth,\nwisdom, and mindful living.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 26.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.38f))

            // Pagination Indicators
            ProdyPagerIndicator(
                totalPages = totalPages,
                currentPage = currentPage,
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Get Started Button - Full width pill with arrow
            ProdyOnboardingButton(
                text = "Get Started",
                onClick = onNext,
                showArrow = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Already have an account? Log in
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Log in",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.clickable { onLogin() }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// =============================================================================
// SCREEN 2: JOURNALING SCREEN
// =============================================================================

@Composable
private fun JournalingScreen(
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header with back arrow, progress bar, and Skip
            JournalingTopHeader(
                currentPage = currentPage,
                totalPages = totalPages,
                onBack = onBack,
                onSkip = onSkip
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Journaling Card with gradient image
            JournalingInsightCard(
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Headline with "Mind" highlighted in green
            Text(
                text = buildAnnotatedString {
                    append("Unlock Your ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Mind")
                    }
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body text
            Text(
                text = "Prody uses on-device AI to turn your daily ramblings into actionable wisdom and future insights, instantly.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Feature chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeatureChip(
                    icon = Icons.Outlined.Psychology,
                    label = "Deep Reflection"
                )
                Spacer(modifier = Modifier.width(12.dp))
                FeatureChip(
                    icon = Icons.Outlined.TrendingUp,
                    label = "Growth Tracking"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Start Journaling Button
            ProdyOnboardingButton(
                text = "Start Journaling",
                onClick = onContinue,
                showArrow = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Already have an account? Log in
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Log in",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun JournalingTopHeader(
    currentPage: Int,
    totalPages: Int,
    onBack: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBack() }
        )

        // Progress bar
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
        ) {
            repeat(totalPages) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (index <= currentPage) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        // Skip button
        Text(
            text = "Skip",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable { onSkip() }
        )
    }
}

@Composable
private fun JournalingInsightCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Abstract wavy gradient image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .drawBehind {
                    // Dark mode: brownish diagonal lines pattern
                    // Light mode: greenish wavy gradient
                    if (isDarkTheme) {
                        // Dark diagonal streaks
                        val colors = listOf(
                            Color(0xFF1A2E2C),
                            Color(0xFF2A3E3C),
                            Color(0xFF3A4E4C),
                            Color(0xFF2A3E3C),
                            Color(0xFF1A2E2C)
                        )
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = colors,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, 0f)
                            )
                        )
                    } else {
                        // Light wavy greens
                        val colors = listOf(
                            Color(0xFF4A7C59),
                            Color(0xFF8FBC8F),
                            Color(0xFFD4E5D4),
                            Color(0xFFFFFFFF),
                            Color(0xFF98D8AA)
                        )
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = colors,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, 0f)
                            )
                        )
                    }
                }
        ) {
            // Analysis Complete badge at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Sparkle icon in green circle
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ANALYSIS COMPLETE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Insight Generated",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureChip(
    icon: ImageVector,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// =============================================================================
// SCREEN 3: GAMIFICATION LEADERBOARD SCREEN
// =============================================================================

@Composable
private fun GamificationLeaderboardScreen(
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header
            OnboardingTopHeader(
                currentPage = currentPage,
                totalPages = totalPages,
                onSkip = onSkip
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Gamification Graphic Container with grid and leaderboard
            LeaderboardGraphicCard(
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Headline
            Text(
                text = "Turn Habits into Quests",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(
                text = "Stay consistent to earn XP, unlock unique badges, and visualize your personal growth journey.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Feature List
            FeatureListItem(
                icon = Icons.Default.Star,
                title = "Earn XP",
                description = "Get rewarded for every journal entry you complete."
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureListItem(
                icon = Icons.Default.EmojiEvents,
                title = "Badges",
                description = "Unlock exclusive milestones as you grow."
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureListItem(
                icon = Icons.Default.BarChart,
                title = "Compete",
                description = "See where you stand among peers."
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            ProdyOnboardingButton(
                text = "Continue",
                onClick = onContinue,
                showArrow = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LeaderboardGraphicCard(
    modifier: Modifier = Modifier
) {
    val cardBackground = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(cardBackground)
            .drawBehind {
                // Draw subtle grid pattern
                val gridSize = 40.dp.toPx()
                val strokeWidth = 1.dp.toPx()
                // Horizontal lines
                var y = gridSize
                while (y < size.height) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                    y += gridSize
                }
                // Vertical lines
                var x = gridSize
                while (x < size.width) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = strokeWidth
                    )
                    x += gridSize
                }
            }
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Trophy Icon in green circle with star
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
            // Small star badge at top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mini Leaderboard rows
        LeaderboardRow(rank = 1, score = "2,450 XP", isActive = true)
        Spacer(modifier = Modifier.height(8.dp))
        LeaderboardRow(rank = 2, score = "1,820", isActive = false)
        Spacer(modifier = Modifier.height(8.dp))
        LeaderboardRow(rank = 3, score = "1,450", isActive = false)
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    score: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val rowBackground = if (isActive) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        Color.Transparent
    }
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val progressColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val progressBg = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    val progressFraction = when (rank) { 1 -> 0.85f; 2 -> 0.65f; else -> 0.45f }
    val scoreColor = if (isActive) MaterialTheme.colorScheme.primary else textColor

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBackground)
            .then(
                if (isActive) Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank number
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isActive) MaterialTheme.colorScheme.primary else textColor,
            modifier = Modifier.width(24.dp)
        )

        // Person icon
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(progressBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(progressColor)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Score
        Text(
            text = score,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = scoreColor
        )
    }
}

@Composable
private fun FeatureListItem(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Icon in rounded square
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =============================================================================
// SCREEN 4: GAMIFICATION XP SCREEN
// =============================================================================

@Composable
private fun GamificationXpScreen(
    currentPage: Int,
    totalPages: Int,
    onStartQuest: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Circular Progress Ring with Level
            XpProgressRing(
                progress = 0.75f,
                modifier = Modifier.size(220.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Statistics Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = MaterialTheme.colorScheme.primary,
                    value = "7",
                    label = "DAY STREAK",
                    isLocked = false,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Verified,
                    iconTint = MaterialTheme.colorScheme.primary,
                    value = "750",
                    label = "TOTAL XP",
                    isLocked = false,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Lock,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    value = "???",
                    label = "LOCKED",
                    isLocked = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Headline with highlighted word
            Text(
                text = buildAnnotatedString {
                    append("Turn Habits into ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Quests")
                    }
                },
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(
                text = "Every entry earns you XP. Unlock wisdom badges and visualize your growth journey.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Pagination
            ProdyPagerIndicator(
                totalPages = totalPages,
                currentPage = currentPage
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Start My Quest Button
            ProdyOnboardingButton(
                text = "Start My Quest",
                onClick = onStartQuest,
                showArrow = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun XpProgressRing(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Ring Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            val startAngle = 135f
            val sweepAngle = 270f

            // Background ring
            drawArc(
                color = MaterialTheme.colorScheme.surfaceVariant,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress ring
            if (progress > 0f) {
                drawArc(
                    color = MaterialTheme.colorScheme.primary,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Inner content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Trophy icon in circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Lvl 3",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "INITIATE",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Lightning icon on ring (bottom-left)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 32.dp, y = (-24).dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        // Star icon on ring (top-right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-32).dp, y = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    isLocked: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val lockedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val textColor = if (isLocked) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .then(
                if (isLocked) {
                    Modifier.border(
                        width = 1.dp,
                        color = lockedBorderColor,
                        shape = RoundedCornerShape(14.dp)
                    )
                } else {
                    Modifier.drawBehind {
                        // Green accent line at top
                        drawRoundRect(
                            color = MaterialTheme.colorScheme.primary,
                            topLeft = Offset(16.dp.toPx(), 0f),
                            size = Size(size.width - 32.dp.toPx(), 3.dp.toPx()),
                            cornerRadius = CornerRadius(2.dp.toPx())
                        )
                    }
                }
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 0.5.sp
            ),
            color = labelColor
        )
    }
}

// =============================================================================
// SCREEN 5: DAILY WISDOM FEATURES SCREEN
// =============================================================================

@Composable
private fun DailyWisdomFeaturesScreen(
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    onContinue: () -> Unit,
    onMaybeLater: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header
            OnboardingTopHeader(
                currentPage = currentPage,
                totalPages = totalPages,
                onSkip = onSkip
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Book/Lightbulb Icon Container
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Headline
            Text(
                text = "Daily Wisdom",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(
                text = "Start your day with intention. Prody delivers personalized insights to help you articulate your thoughts.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature Cards
            WisdomFeatureCard(
                icon = Icons.Outlined.TextFormat,
                title = "Word of the Day",
                description = "Expand your vocabulary"
            )

            Spacer(modifier = Modifier.height(12.dp))

            WisdomFeatureCard(
                icon = Icons.Outlined.FormatQuote,
                title = "Quote Collection",
                description = "Insights from great minds"
            )

            Spacer(modifier = Modifier.height(12.dp))

            WisdomFeatureCard(
                icon = Icons.Outlined.School,
                title = "Proverbs & Idioms",
                description = "Timeless cultural wisdom"
            )

            Spacer(modifier = Modifier.height(12.dp))

            WisdomFeatureCard(
                icon = Icons.Outlined.ChatBubble,
                title = "Essential Phrases",
                description = "Communication masterclass"
            )

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            ProdyOnboardingButton(
                text = "Continue",
                onClick = onContinue,
                showArrow = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Maybe Later
            Text(
                text = "Maybe Later",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onMaybeLater() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun WisdomFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =============================================================================
// SCREEN 6: PERSONALIZED INSIGHTS SCREEN
// =============================================================================

@Composable
private fun PersonalizedInsightsScreen(
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    onEnableNotifications: () -> Unit,
    onMaybeLater: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header
            OnboardingTopHeader(
                currentPage = currentPage,
                totalPages = totalPages,
                onSkip = onSkip
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quote Card
            QuoteCard(
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Headline
            Text(
                text = "Personalized Insights",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(
                text = "Prody analyzes your daily entries to surface quotes, stoic principles, and mental models that match your current headspace.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Enable Notifications Button
            ProdyOnboardingButton(
                text = "Enable Notifications",
                onClick = onEnableNotifications,
                showArrow = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Maybe Later
            Text(
                text = "Maybe later",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMaybeLater() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun QuoteCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
            .padding(24.dp)
    ) {
        // Header row - lightbulb icon and DAILY WISDOM label
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lightbulb icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // DAILY WISDOM label
            Text(
                text = "DAILY WISDOM",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quote text with "great work" highlighted
        Text(
            text = buildAnnotatedString {
                append("\"The only way to do ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("great work")
                }
                append(" is to love what you do.\"")
            },
            style = MaterialTheme.typography.titleLarge.copy(
                lineHeight = 32.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Divider
        HorizontalDivider(
            modifier = Modifier.width(48.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Author with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Steve Jobs",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom row: mini pagination and bookmark
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini pagination dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }

            // Bookmark icon
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "Bookmark",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// =============================================================================
// SHARED COMPONENTS
// =============================================================================

/**
 * Prody Logo - Seedling/Leaf icon in a squircle container with green accent dot
 */
@Composable
private fun ProdyLogo(
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.surface
    val strokeColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val leafColor = MaterialTheme.colorScheme.onSurface

    Box(modifier = modifier) {
        // Squircle container with subtle border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(containerColor)
                .border(
                    width = 1.dp,
                    color = strokeColor,
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Seedling/Leaf icon drawn with Canvas
            Canvas(modifier = Modifier.size(48.dp)) {
                val centerX = size.width / 2
                val centerY = size.height / 2

                // Center leaf (pointing up)
                val centerLeaf = Path().apply {
                    moveTo(centerX, centerY + 16.dp.toPx())
                    cubicTo(
                        centerX - 10.dp.toPx(), centerY + 4.dp.toPx(),
                        centerX - 8.dp.toPx(), centerY - 16.dp.toPx(),
                        centerX, centerY - 20.dp.toPx()
                    )
                    cubicTo(
                        centerX + 8.dp.toPx(), centerY - 16.dp.toPx(),
                        centerX + 10.dp.toPx(), centerY + 4.dp.toPx(),
                        centerX, centerY + 16.dp.toPx()
                    )
                    close()
                }
                drawPath(centerLeaf, leafColor)

                // Left leaf
                val leftLeaf = Path().apply {
                    moveTo(centerX - 2.dp.toPx(), centerY + 10.dp.toPx())
                    cubicTo(
                        centerX - 14.dp.toPx(), centerY + 2.dp.toPx(),
                        centerX - 18.dp.toPx(), centerY - 8.dp.toPx(),
                        centerX - 12.dp.toPx(), centerY - 12.dp.toPx()
                    )
                    cubicTo(
                        centerX - 10.dp.toPx(), centerY - 4.dp.toPx(),
                        centerX - 8.dp.toPx(), centerY + 4.dp.toPx(),
                        centerX - 2.dp.toPx(), centerY + 10.dp.toPx()
                    )
                    close()
                }
                drawPath(leftLeaf, leafColor)

                // Right leaf
                val rightLeaf = Path().apply {
                    moveTo(centerX + 2.dp.toPx(), centerY + 10.dp.toPx())
                    cubicTo(
                        centerX + 14.dp.toPx(), centerY + 2.dp.toPx(),
                        centerX + 18.dp.toPx(), centerY - 8.dp.toPx(),
                        centerX + 12.dp.toPx(), centerY - 12.dp.toPx()
                    )
                    cubicTo(
                        centerX + 10.dp.toPx(), centerY - 4.dp.toPx(),
                        centerX + 8.dp.toPx(), centerY + 4.dp.toPx(),
                        centerX + 2.dp.toPx(), centerY + 10.dp.toPx()
                    )
                    close()
                }
                drawPath(rightLeaf, leafColor)
            }
        }

        // Green accent dot at top-right
        Box(
            modifier = Modifier
                .size(14.dp)
                .align(Alignment.TopEnd)
                .offset(x = 2.dp, y = (-2).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

/**
 * Custom page indicator with animated pill for active state
 */
@Composable
private fun ProdyPagerIndicator(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(totalPages) { index ->
            val isActive = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isActive) 24.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "indicatorWidth"
            )

            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

/**
 * Primary onboarding button - vibrant green with optional arrow
 */
@Composable
private fun ProdyOnboardingButton(
    text: String,
    onClick: () -> Unit,
    showArrow: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(29.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalArrangement = if (showArrow) Arrangement.SpaceBetween else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * Top header with pagination dots and Skip button
 */
@Composable
private fun OnboardingTopHeader(
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pagination on left
        ProdyPagerIndicator(
            totalPages = totalPages,
            currentPage = currentPage
        )

        // Skip button on right
        Text(
            text = "Skip",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.clickable { onSkip() }
        )
    }
}

/**
 * Background with subtle concentric circles
 */
@Composable
private fun ConcentricCirclesBackground(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.35f
        val maxRadius = size.width * 0.7f
        val strokeWidth = 1.dp.toPx()

        // Draw multiple concentric circles
        val circleCount = 5
        for (i in 1..circleCount) {
            val radius = maxRadius * i / circleCount
            val alpha = 0.25f - (i * 0.03f)
            drawCircle(
                color = color.copy(alpha = alpha.coerceAtLeast(0.05f)),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
