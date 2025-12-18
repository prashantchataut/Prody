package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.ui.theme.PoppinsFamily
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =============================================================================
// COLOR PALETTE - Prody Onboarding Design System
// =============================================================================

// Light Mode Colors
private val LightBackground = Color(0xFFF0F4F3)
private val LightCardBackground = Color(0xFFE0E7E6)
private val LightIconCircleBackground = Color(0xFFE6F0EE)
private val LightTextPrimary = Color(0xFF1A1A1A)
private val LightTextSecondary = Color(0xFF6C757D)
private val LightTextTertiary = Color(0xFF8A9493)
private val LightInactiveDot = Color(0xFFB0B8B7)
private val LightProgressBarInactive = Color(0xFFA0A8A7)
private val LightDivider = Color(0xFFCCCCCC)
private val LightSubtleLines = Color(0xFFD0D8D6)

// Dark Mode Colors
private val DarkBackground = Color(0xFF0A1D1C)
private val DarkCardBackground = Color(0xFF2A4240)
private val DarkIconCircleBackground = Color(0xFF3F5857)
private val DarkTextPrimary = Color(0xFFFFFFFF)
private val DarkTextSecondary = Color(0xFFD3D8D7)
private val DarkTextTertiary = Color(0xFF8A9493)
private val DarkInactiveDot = Color(0xFF404B4A)
private val DarkProgressBarInactive = Color(0xFF5A706F)
private val DarkDivider = Color(0xFF404B4A)
private val DarkSubtleLines = Color(0xFF2A3A38)

// Shared Accent Color
private val AccentGreen = Color(0xFF36F97F)
private val ButtonTextDark = Color(0xFF000000)

// Logo Colors
private val LogoContainerDark = Color(0xFF1C1C1E)
private val LogoContainerLight = Color(0xFFE8EBE9)

// =============================================================================
// DATA MODEL
// =============================================================================

/**
 * Represents a single onboarding page with its content
 */
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
    val isDarkTheme = isSystemInDarkTheme()

    // Theme-aware colors
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
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
                    isDarkTheme = isDarkTheme,
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
                    isDarkTheme = isDarkTheme,
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
                OnboardingPageType.GAMIFICATION_LEADERBOARD -> GamificationLeaderboardScreen(
                    isDarkTheme = isDarkTheme,
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
                    isDarkTheme = isDarkTheme,
                    currentPage = page,
                    totalPages = 6,
                    onSkip = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(5)
                        }
                    },
                    onStartQuest = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    }
                )
                OnboardingPageType.DAILY_WISDOM -> DailyWisdomFeaturesScreen(
                    isDarkTheme = isDarkTheme,
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
                    isDarkTheme = isDarkTheme,
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
    isDarkTheme: Boolean,
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onLogin: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val subtleLines = if (isDarkTheme) DarkSubtleLines else LightSubtleLines

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Background concentric circles
        ConcentricCirclesBackground(
            modifier = Modifier.fillMaxSize(),
            color = subtleLines
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.15f))

            // Logo with green dot
            ProdyLogo(
                isDarkTheme = isDarkTheme,
                modifier = Modifier.size(68.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Title "Prody."
            Text(
                text = "Prody.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            Text(
                text = "Your daily companion for growth,\nwisdom, and mindful living.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                color = textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.35f))

            // Pagination Indicators
            ProdyPagerIndicator(
                totalPages = totalPages,
                currentPage = currentPage,
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Get Started Button
            ProdyOnboardingButton(
                text = "Get Started",
                onClick = onNext,
                showArrow = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Already have an account? Log in
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = textSecondary
                )
                Text(
                    text = "Log in",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = AccentGreen,
                    modifier = Modifier.clickable { onLogin() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// =============================================================================
// SCREEN 2: JOURNALING SCREEN
// =============================================================================

@Composable
private fun JournalingScreen(
    isDarkTheme: Boolean,
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    onContinue: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val cardBackground = if (isDarkTheme) DarkCardBackground else LightCardBackground
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val textTertiary = if (isDarkTheme) DarkTextTertiary else LightTextTertiary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header with pagination and Skip
            OnboardingTopHeader(
                currentPage = currentPage,
                totalPages = totalPages,
                isDarkTheme = isDarkTheme,
                onSkip = onSkip
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Journaling Card
            JournalingInsightCard(
                isDarkTheme = isDarkTheme,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Headline
            Text(
                text = "Unlock Your Mind",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                ),
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body text
            Text(
                text = "Experience journaling reinvented. Let AI-powered prompts guide your thoughts and discover emotional patterns.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                color = textSecondary
            )

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            ProdyOnboardingButton(
                text = "Continue",
                onClick = onContinue,
                showArrow = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun JournalingInsightCard(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isDarkTheme) DarkCardBackground else LightCardBackground
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val textTertiary = if (isDarkTheme) DarkTextTertiary else LightTextTertiary

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground)
    ) {
        // Abstract wavy image placeholder (drawn as gradient)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .drawBehind {
                    val colors = listOf(
                        Color(0xFF4A7C59),
                        Color(0xFF8FBC8F),
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
        )

        // Card Content
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // AI Generated tag and Today
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI Generated Tag
                Row(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFE0F8E5),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI GENERATED",
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = AccentGreen
                    )
                }

                Text(
                    text = "Today",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    ),
                    color = textTertiary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Daily Insight Title
            Text(
                text = "Daily Insight",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quote
            Text(
                text = "What small moment brought you joy today?",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontStyle = FontStyle.Italic
                ),
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input field placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = Color(0xFFD0D8D6),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(20.dp)
                            .background(AccentGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Type your thoughts...",
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic
                        ),
                        color = textTertiary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

// =============================================================================
// SCREEN 3: GAMIFICATION LEADERBOARD SCREEN
// =============================================================================

@Composable
private fun GamificationLeaderboardScreen(
    isDarkTheme: Boolean,
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    onContinue: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val cardBackground = if (isDarkTheme) DarkCardBackground else LightCardBackground
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val iconCircleBg = if (isDarkTheme) DarkIconCircleBackground else LightIconCircleBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
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
                isDarkTheme = isDarkTheme,
                onSkip = onSkip
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Gamification Graphic Container
            LeaderboardGraphicCard(
                isDarkTheme = isDarkTheme,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Headline
            Text(
                text = "Turn Habits into Quests",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 34.sp
                ),
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(
                text = "Stay consistent to earn XP, unlock unique badges, and visualize your personal growth journey.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                ),
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Feature List
            FeatureListItem(
                icon = Icons.Default.Star,
                title = "Earn XP",
                description = "Get rewarded for every journal entry you complete.",
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureListItem(
                icon = Icons.Default.EmojiEvents,
                title = "Unlock Badges",
                description = "Collect achievements and show off your progress.",
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureListItem(
                icon = Icons.Default.BarChart,
                title = "Track Growth",
                description = "Visualize your journey with detailed analytics.",
                isDarkTheme = isDarkTheme
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
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isDarkTheme) Color(0xFF1F3A38) else Color(0xFFE0E7E6)
    val gridColor = if (isDarkTheme) Color(0xFF2A4A48) else Color(0xFFD0D8D6)
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val progressInactive = if (isDarkTheme) DarkProgressBarInactive else LightProgressBarInactive

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(cardBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Trophy Icon in circle
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(AccentGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Trophy",
                tint = ButtonTextDark,
                modifier = Modifier.size(36.dp)
            )
        }

        // Small star badge
        Box(
            modifier = Modifier
                .offset(x = 28.dp, y = (-60).dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(AccentGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = ButtonTextDark,
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mini Leaderboard
        LeaderboardRow(rank = 1, score = "2450", isActive = true, isDarkTheme = isDarkTheme)
        Spacer(modifier = Modifier.height(8.dp))
        LeaderboardRow(rank = 2, score = "1820", isActive = false, isDarkTheme = isDarkTheme)
        Spacer(modifier = Modifier.height(8.dp))
        LeaderboardRow(rank = 3, score = "1450", isActive = false, isDarkTheme = isDarkTheme)
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    score: String,
    isActive: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val rowBackground = if (isDarkTheme) Color(0xFF2A4A48) else Color(0xFFD0E0DD)
    val textColor = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val progressColor = if (isActive) AccentGreen else (if (isDarkTheme) DarkProgressBarInactive else LightProgressBarInactive)
    val progressFraction = when (rank) { 1 -> 0.9f; 2 -> 0.7f; else -> 0.5f }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(rowBackground)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank number
        Text(
            text = rank.toString(),
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            color = textColor,
            modifier = Modifier.width(20.dp)
        )

        // Person icon
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isDarkTheme) Color(0xFF3A5A58) else Color(0xFFBBC8C6))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(progressColor)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Score
        Text(
            text = score,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = textColor
        )
    }
}

@Composable
private fun FeatureListItem(
    icon: ImageVector,
    title: String,
    description: String,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val iconCircleBg = if (isDarkTheme) DarkIconCircleBackground else LightIconCircleBackground
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Icon in rounded square
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconCircleBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = textSecondary
            )
        }
    }
}

// =============================================================================
// SCREEN 4: GAMIFICATION XP SCREEN
// =============================================================================

@Composable
private fun GamificationXpScreen(
    isDarkTheme: Boolean,
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    onStartQuest: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF0F2423) else LightBackground
    val cardBackground = if (isDarkTheme) DarkCardBackground else LightCardBackground
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val progressInactive = if (isDarkTheme) Color(0xFF3F5857) else Color(0xFFA0A8A7)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Circular Progress Ring
            XpProgressRing(
                progress = 0.4f,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Statistics Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = Color(0xFFFF6B35),
                    value = "7",
                    label = "DAY STREAK",
                    isLocked = false,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Star,
                    iconTint = AccentGreen,
                    value = "750",
                    label = "TOTAL XP",
                    isLocked = false,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Lock,
                    iconTint = if (isDarkTheme) Color(0xFF707A79) else Color(0xFF909998),
                    value = "???",
                    label = "LOCKED",
                    isLocked = true,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Headline with highlighted word
            Text(
                text = buildAnnotatedString {
                    append("Level Up Your ")
                    withStyle(SpanStyle(color = AccentGreen)) {
                        append("Mindset")
                    }
                },
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 34.sp
                ),
                color = textPrimary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(
                text = "Every entry earns you XP. Unlock wisdom badges and visualize your growth journey.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                ),
                color = textSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Pagination
            ProdyPagerIndicator(
                totalPages = totalPages,
                currentPage = currentPage,
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(24.dp))

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
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val progressInactive = if (isDarkTheme) Color(0xFF3F5857) else Color(0xFFA0A8A7)
    val innerCircleBg = if (isDarkTheme) DarkCardBackground else LightCardBackground
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Ring Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            val startAngle = 135f
            val sweepAngle = 270f

            // Background ring
            drawArc(
                color = progressInactive,
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
                    color = AccentGreen,
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
            // Trophy icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(innerCircleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lvl 3",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = textPrimary
            )

            Text(
                text = "INITIATE",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                ),
                color = textSecondary
            )
        }

        // Lightning icon on ring
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 30.dp, y = (-20).dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(AccentGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = ButtonTextDark,
                modifier = Modifier.size(14.dp)
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
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isLocked) {
        if (isDarkTheme) Color(0xFF1A2520) else Color(0xFFF0F2F1)
    } else {
        if (isDarkTheme) DarkCardBackground else LightCardBackground
    }
    val borderColor = if (isLocked) {
        if (isDarkTheme) Color(0xFF3A4540) else Color(0xFFD0D8D4)
    } else AccentGreen.copy(alpha = 0.5f)
    val textColor = if (isLocked) {
        if (isDarkTheme) Color(0xFF707A79) else Color(0xFF909998)
    } else {
        if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    }
    val labelColor = if (isDarkTheme) DarkTextSecondary else LightTextSecondary

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .then(
                if (isLocked) {
                    Modifier.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier.drawBehind {
                        drawLine(
                            color = AccentGreen,
                            start = Offset(16.dp.toPx(), 0f),
                            end = Offset(size.width - 16.dp.toPx(), 0f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
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
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = textColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 9.sp,
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
    isDarkTheme: Boolean,
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    onContinue: () -> Unit,
    onMaybeLater: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) DarkBackground else Color(0xFFFFFFFF)
    val cardBackground = if (isDarkTheme) DarkCardBackground else Color(0xFFF0F3F2)
    val iconCircleBg = if (isDarkTheme) DarkIconCircleBackground else Color(0xFFE6F0EE)
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val textTertiary = if (isDarkTheme) DarkTextTertiary else LightTextTertiary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
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
                isDarkTheme = isDarkTheme,
                onSkip = onSkip
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Lightbulb Icon Container
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(iconCircleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Headline
            Text(
                text = "Daily Wisdom",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Body
            Text(
                text = "Start your day with intention. Personalized insights to articulate your thoughts.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                color = textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature Cards
            WisdomFeatureCard(
                icon = Icons.Default.TextFormat,
                title = "Word of the Day",
                description = "Expand your vocabulary daily",
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(12.dp))

            WisdomFeatureCard(
                icon = Icons.Default.FormatQuote,
                title = "Quote Collection",
                description = "Curated wisdom from great minds",
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(12.dp))

            WisdomFeatureCard(
                icon = Icons.Default.School,
                title = "Proverbs & Idioms",
                description = "Timeless expressions explained",
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(12.dp))

            WisdomFeatureCard(
                icon = Icons.Default.ChatBubble,
                title = "Essential Phrases",
                description = "Useful expressions for daily life",
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            ProdyOnboardingButton(
                text = "Continue",
                onClick = onContinue,
                showArrow = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Maybe Later
            Text(
                text = "Maybe Later",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = textTertiary,
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
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkTheme) DarkCardBackground else Color(0xFFF0F3F2)
    val iconCircleBg = if (isDarkTheme) DarkIconCircleBackground else Color(0xFFE6F0EE)
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconCircleBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = textPrimary
            )
            Text(
                text = description,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp
                ),
                color = textSecondary
            )
        }
    }
}

// =============================================================================
// SCREEN 6: PERSONALIZED INSIGHTS SCREEN
// =============================================================================

@Composable
private fun PersonalizedInsightsScreen(
    isDarkTheme: Boolean,
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    onEnableNotifications: () -> Unit,
    onMaybeLater: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF1A2E2C) else LightBackground
    val cardBackground = if (isDarkTheme) Color(0xFF2D4240) else Color(0xFFE0E7E6)
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val textTertiary = if (isDarkTheme) DarkTextTertiary else LightTextTertiary
    val dividerColor = if (isDarkTheme) DarkDivider else LightDivider

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
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
                isDarkTheme = isDarkTheme,
                onSkip = onSkip
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quote Card
            QuoteCard(
                isDarkTheme = isDarkTheme,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Headline
            Text(
                text = "Personalized Insights",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 34.sp
                ),
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(
                text = "Prody analyzes your daily entries to surface quotes, stoic principles, and mental models that match your current headspace.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                ),
                color = textSecondary
            )

            Spacer(modifier = Modifier.weight(1f))

            // Enable Notifications Button
            ProdyOnboardingButton(
                text = "Enable Notifications",
                onClick = onEnableNotifications,
                showArrow = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Maybe Later
            Text(
                text = "Maybe later",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = textTertiary,
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
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isDarkTheme) Color(0xFF2D4240) else Color(0xFFE0E7E6)
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val dividerColor = if (isDarkTheme) DarkDivider else LightDivider
    val iconCircleBg = if (isDarkTheme) DarkIconCircleBackground else LightIconCircleBackground

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground)
            .padding(24.dp)
    ) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lightbulb icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconCircleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            // DAILY WISDOM label
            Text(
                text = "DAILY WISDOM",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                ),
                color = textSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quote text with highlights
        Text(
            text = buildAnnotatedString {
                append("The only way to do ")
                withStyle(SpanStyle(color = AccentGreen)) {
                    append("great")
                }
                append(" ")
                withStyle(SpanStyle(color = AccentGreen)) {
                    append("work")
                }
                append(" is to love what you do.")
            },
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 30.sp
            ),
            color = textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(1.dp)
                .background(dividerColor)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Author
        Text(
            text = "- Steve Jobs",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            color = textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom row: pagination and bookmark
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
                        .width(16.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(AccentGreen)
                )
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) DarkInactiveDot else LightInactiveDot)
                    )
                }
            }

            // Bookmark icon
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "Bookmark",
                tint = textPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// =============================================================================
// SHARED COMPONENTS
// =============================================================================

/**
 * Custom Prody Logo - Seedling/Leaf icon in a squircle container
 */
@Composable
private fun ProdyLogo(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isDarkTheme) LogoContainerDark else LogoContainerLight

    Box(modifier = modifier) {
        // Squircle container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            // Seedling/Leaf icon (drawn with Canvas)
            Canvas(modifier = Modifier.size(36.dp)) {
                val centerX = size.width / 2
                val centerY = size.height / 2

                // Draw three leaves
                val leafColor = AccentGreen

                // Center leaf (largest, pointing up)
                val centerLeaf = Path().apply {
                    moveTo(centerX, centerY + 12.dp.toPx())  // Stem base
                    cubicTo(
                        centerX - 8.dp.toPx(), centerY,     // Left control
                        centerX - 6.dp.toPx(), centerY - 14.dp.toPx(),  // Left top
                        centerX, centerY - 16.dp.toPx()     // Top point
                    )
                    cubicTo(
                        centerX + 6.dp.toPx(), centerY - 14.dp.toPx(),  // Right top
                        centerX + 8.dp.toPx(), centerY,     // Right control
                        centerX, centerY + 12.dp.toPx()     // Back to base
                    )
                    close()
                }
                drawPath(centerLeaf, leafColor)

                // Left leaf (smaller, curved left)
                val leftLeaf = Path().apply {
                    moveTo(centerX - 2.dp.toPx(), centerY + 8.dp.toPx())
                    cubicTo(
                        centerX - 12.dp.toPx(), centerY + 2.dp.toPx(),
                        centerX - 16.dp.toPx(), centerY - 6.dp.toPx(),
                        centerX - 10.dp.toPx(), centerY - 10.dp.toPx()
                    )
                    cubicTo(
                        centerX - 8.dp.toPx(), centerY - 4.dp.toPx(),
                        centerX - 6.dp.toPx(), centerY + 2.dp.toPx(),
                        centerX - 2.dp.toPx(), centerY + 8.dp.toPx()
                    )
                    close()
                }
                drawPath(leftLeaf, leafColor)

                // Right leaf (smaller, curved right)
                val rightLeaf = Path().apply {
                    moveTo(centerX + 2.dp.toPx(), centerY + 8.dp.toPx())
                    cubicTo(
                        centerX + 12.dp.toPx(), centerY + 2.dp.toPx(),
                        centerX + 16.dp.toPx(), centerY - 6.dp.toPx(),
                        centerX + 10.dp.toPx(), centerY - 10.dp.toPx()
                    )
                    cubicTo(
                        centerX + 8.dp.toPx(), centerY - 4.dp.toPx(),
                        centerX + 6.dp.toPx(), centerY + 2.dp.toPx(),
                        centerX + 2.dp.toPx(), centerY + 8.dp.toPx()
                    )
                    close()
                }
                drawPath(rightLeaf, leafColor)
            }
        }

        // Green dot at top-right
        Box(
            modifier = Modifier
                .size(12.dp)
                .align(Alignment.TopEnd)
                .offset(x = 2.dp, y = (-2).dp)
                .clip(CircleShape)
                .background(AccentGreen)
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
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val inactiveColor = if (isDarkTheme) DarkInactiveDot else LightInactiveDot

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
                    .background(if (isActive) AccentGreen else inactiveColor)
            )
        }
    }
}

/**
 * Primary onboarding button with vibrant green background
 */
@Composable
private fun ProdyOnboardingButton(
    text: String,
    onClick: () -> Unit,
    showArrow: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = AccentGreen
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = if (showArrow) Arrangement.SpaceBetween else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = ButtonTextDark
            )

            if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = ButtonTextDark,
                    modifier = Modifier.size(20.dp)
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
    isDarkTheme: Boolean,
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
            currentPage = currentPage,
            isDarkTheme = isDarkTheme
        )

        // Skip button on right
        Text(
            text = "Skip",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            color = AccentGreen,
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
        val centerX = size.width * 0.3f
        val centerY = size.height * 0.6f
        val maxRadius = size.width * 0.8f
        val strokeWidth = 1.dp.toPx()

        // Draw multiple concentric circles
        val circleCount = 6
        for (i in 1..circleCount) {
            val radius = maxRadius * i / circleCount
            drawCircle(
                color = color.copy(alpha = 0.3f - (i * 0.04f)),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
