package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.launch

// =============================================================================
// PRODY ONBOARDING - REDESIGNED WITH FLAT DESIGN SYSTEM
// =============================================================================
// A sophisticated 7-screen onboarding flow featuring:
// - Flat, minimalist design with NO shadows or gradients
// - Deep dark teal (#0D2826) for dark mode
// - Clean off-white (#F0F4F3) for light mode
// - Vibrant neon green (#36F97F) accent
// - Exclusively Poppins typography
// - Full Light/Dark mode support
// =============================================================================

// =============================================================================
// COLOR PALETTE - Using Prody Design System Colors
// =============================================================================

// Light Mode Colors - From Color.kt
private val LightBackground = OnboardingBackgroundLight
private val LightCardBackground = OnboardingSurfaceVariantLight
private val LightIconCircleBackground = OnboardingIconContainerLight
private val LightTextPrimary = OnboardingTextPrimaryLight
private val LightTextSecondary = OnboardingTextSecondaryLight
private val LightTextTertiary = OnboardingTextTertiaryLight
private val LightInactiveDot = OnboardingProgressInactiveLight
private val LightProgressBarInactive = OnboardingProgressInactiveLight
private val LightDivider = OnboardingDividerLight
private val LightSubtleLines = ProdyOutlineLight

// Dark Mode Colors - From Color.kt
private val DarkBackground = OnboardingBackgroundDark
private val DarkCardBackground = OnboardingSurfaceVariantDark
private val DarkIconCircleBackground = OnboardingIconContainerDark
private val DarkTextPrimary = OnboardingTextPrimaryDark
private val DarkTextSecondary = OnboardingTextSecondaryDark
private val DarkTextTertiary = OnboardingTextTertiaryDark
private val DarkInactiveDot = OnboardingProgressInactiveDark
private val DarkProgressBarInactive = OnboardingProgressInactiveDark
private val DarkDivider = OnboardingDividerDark
private val DarkSubtleLines = ProdyOutlineDark

// Shared Accent Color - Vibrant Neon Green
private val AccentGreen = OnboardingAccent
private val ButtonTextDark = OnboardingButtonTextLight

// Logo Container Colors
private val LogoContainerDark = ProdySurfaceVariantDark
private val LogoContainerLight = ProdySurfaceLight
private val LogoContainerStroke = ProdyOutlineDark

// Card & Component Colors
private val CardBackgroundDark = ProdySurfaceContainerDark
private val CardBackgroundLight = ProdySurfaceContainerLight
private val GridColorDark = ProdyOutlineDark
private val GridColorLight = ProdyOutlineLight

// Login/Signup Screen Colors - Dark Mode
private val LoginDarkBackground = ProdyBackgroundDark
private val LoginDarkInputBackground = ProdySurfaceVariantDark
private val LoginDarkInputIcon = ProdyTextTertiaryDark
private val LoginDarkPlaceholder = ProdyTextTertiaryDark
private val LoginDarkSubtleText = ProdyTextSecondaryDark
private val LoginDarkLogoContainer = ProdySurfaceVariantDark

// Login/Signup Screen Colors - Light Mode
private val LoginLightBackground = ProdyBackgroundLight
private val LoginLightInputBackground = ProdySurfaceLight
private val LoginLightInputIcon = ProdyTextSecondaryLight
private val LoginLightPlaceholder = ProdyTextSecondaryLight
private val LoginLightSubtleText = ProdyTextSecondaryLight
private val LoginLightLogoContainer = ProdySurfaceLight
private val LoginLightLogoLeaf = ProdyTextPrimaryLight

// =============================================================================
// DATA MODEL
// =============================================================================

private enum class OnboardingPageType {
    WELCOME,
    JOURNALING,
    GAMIFICATION_LEADERBOARD,
    GAMIFICATION_XP,
    DAILY_WISDOM,
    PERSONALIZED_INSIGHTS,
    LOGIN_SIGNUP
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
    val pagerState = rememberPagerState(pageCount = { 7 })
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isDarkTheme()

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
                    totalPages = 7,
                    onNext = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    },
                    onLogin = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(6) // Navigate to login screen
                        }
                    }
                )
                OnboardingPageType.JOURNALING -> JournalingScreen(
                    isDarkTheme = isDarkTheme,
                    currentPage = page,
                    totalPages = 7,
                    onSkip = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(6) // Skip to login screen
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
                    },
                    onLogin = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(6) // Navigate to login screen
                        }
                    }
                )
                OnboardingPageType.GAMIFICATION_LEADERBOARD -> GamificationLeaderboardScreen(
                    isDarkTheme = isDarkTheme,
                    currentPage = page,
                    totalPages = 7,
                    onSkip = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(6) // Skip to login screen
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
                    totalPages = 7,
                    onStartQuest = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    }
                )
                OnboardingPageType.DAILY_WISDOM -> DailyWisdomFeaturesScreen(
                    isDarkTheme = isDarkTheme,
                    currentPage = page,
                    totalPages = 7,
                    onSkip = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(6) // Skip to login screen
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
                    totalPages = 7,
                    onSkip = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(6) // Skip to login screen
                        }
                    },
                    onEnableNotifications = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1) // Navigate to login screen
                        }
                    },
                    onMaybeLater = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1) // Navigate to login screen
                        }
                    }
                )
                OnboardingPageType.LOGIN_SIGNUP -> LoginSignupScreen(
                    isDarkTheme = isDarkTheme,
                    onLogin = {
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    onCreateAccount = {
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    onGoogleLogin = {
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    onForgotPassword = {
                        // Handle forgot password - for now just navigate to onComplete
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
        // Background concentric circles - centered behind logo
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
            Spacer(modifier = Modifier.weight(0.18f))

            // Prody Logo with green accent dot
            ProdyLogo(
                isDarkTheme = isDarkTheme,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Title "Prody."
            Text(
                text = "Prody.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Your daily companion for growth,\nwisdom, and mindful living.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 26.sp
                ),
                color = textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.38f))

            // Pagination Indicators
            ProdyPagerIndicator(
                totalPages = totalPages,
                currentPage = currentPage,
                isDarkTheme = isDarkTheme
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

            Spacer(modifier = Modifier.height(40.dp))
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
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onLogin: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val cardBackground = if (isDarkTheme) DarkCardBackground else LightCardBackground
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary

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

            // Top Header with back arrow, progress bar, and Skip
            JournalingTopHeader(
                currentPage = currentPage,
                totalPages = totalPages,
                isDarkTheme = isDarkTheme,
                onBack = onBack,
                onSkip = onSkip
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Journaling Card with gradient image
            JournalingInsightCard(
                isDarkTheme = isDarkTheme,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Headline with "Mind" highlighted in green
            Text(
                text = buildAnnotatedString {
                    append("Unlock Your ")
                    withStyle(SpanStyle(color = AccentGreen)) {
                        append("Mind")
                    }
                },
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                ),
                color = textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body text
            Text(
                text = "Prody uses on-device AI to turn your daily ramblings into actionable wisdom and future insights, instantly.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                ),
                color = textSecondary,
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
                    label = "Deep Reflection",
                    isDarkTheme = isDarkTheme
                )
                Spacer(modifier = Modifier.width(12.dp))
                FeatureChip(
                    icon = Icons.Outlined.TrendingUp,
                    label = "Growth Tracking",
                    isDarkTheme = isDarkTheme
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

@Composable
private fun JournalingTopHeader(
    currentPage: Int,
    totalPages: Int,
    isDarkTheme: Boolean,
    onBack: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val progressBarBg = if (isDarkTheme) DarkInactiveDot else LightInactiveDot

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = textSecondary,
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
                            if (index <= currentPage) AccentGreen else progressBarBg
                        )
                )
            }
        }

        // Skip button
        Text(
            text = "Skip",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            color = textSecondary,
            modifier = Modifier.clickable { onSkip() }
        )
    }
}

@Composable
private fun JournalingInsightCard(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isDarkTheme) DarkCardBackground else LightCardBackground

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackground)
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
                    .background(if (isDarkTheme) DarkCardBackground.copy(alpha = 0.9f) else LightCardBackground.copy(alpha = 0.95f))
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
                            .background(AccentGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ANALYSIS COMPLETE",
                            style = TextStyle(
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = AccentGreen
                        )
                        Text(
                            text = "Insight Generated",
                            style = TextStyle(
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
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
    label: String,
    isDarkTheme: Boolean
) {
    val chipBg = if (isDarkTheme) DarkCardBackground else LightCardBackground
    val textColor = if (isDarkTheme) DarkTextPrimary else LightTextPrimary

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = chipBg,
        tonalElevation = 0.dp // Flat design
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = textColor
            )
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
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary

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

            // Gamification Graphic Container with grid and leaderboard
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
                color = textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
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
                color = textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

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
                title = "Badges",
                description = "Unlock exclusive milestones as you grow.",
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureListItem(
                icon = Icons.Default.BarChart,
                title = "Compete",
                description = "See where you stand among peers.",
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
    val cardBackground = if (isDarkTheme) CardBackgroundDark else CardBackgroundLight
    val gridColor = if (isDarkTheme) GridColorDark else GridColorLight

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
                    .background(AccentGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = ButtonTextDark,
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
                    tint = AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mini Leaderboard rows
        LeaderboardRow(rank = 1, score = "2,450 XP", isActive = true, isDarkTheme = isDarkTheme)
        Spacer(modifier = Modifier.height(8.dp))
        LeaderboardRow(rank = 2, score = "1,820", isActive = false, isDarkTheme = isDarkTheme)
        Spacer(modifier = Modifier.height(8.dp))
        LeaderboardRow(rank = 3, score = "1,450", isActive = false, isDarkTheme = isDarkTheme)
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
    val rowBackground = if (isActive) {
        if (isDarkTheme) ProdySurfaceContainerDark else ProdySurfaceContainerLight
    } else {
        Color.Transparent
    }
    val textColor = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val progressColor = if (isActive) AccentGreen else (if (isDarkTheme) DarkProgressBarInactive else LightProgressBarInactive)
    val progressBg = if (isDarkTheme) ProdyOutlineDark else ProdyOutlineLight
    val progressFraction = when (rank) { 1 -> 0.85f; 2 -> 0.65f; else -> 0.45f }
    val scoreColor = if (isActive) AccentGreen else textColor

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBackground)
            .then(
                if (isActive) Modifier.border(
                    width = 1.dp,
                    color = AccentGreen.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
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
            color = if (isActive) AccentGreen else textColor,
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
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            color = scoreColor
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
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconCircleBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
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
    onStartQuest: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary

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
            Spacer(modifier = Modifier.height(48.dp))

            // Circular Progress Ring with Level
            XpProgressRing(
                progress = 0.75f,
                isDarkTheme = isDarkTheme,
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
                    iconTint = AccentGreen,
                    value = "7",
                    label = "DAY STREAK",
                    isLocked = false,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Verified,
                    iconTint = AccentGreen,
                    value = "750",
                    label = "TOTAL XP",
                    isLocked = false,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Lock,
                    iconTint = if (isDarkTheme) DarkTextTertiary else LightTextTertiary,
                    value = "???",
                    label = "LOCKED",
                    isLocked = true,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Headline with highlighted word
            Text(
                text = buildAnnotatedString {
                    append("Turn Habits into ")
                    withStyle(SpanStyle(color = AccentGreen)) {
                        append("Quests")
                    }
                },
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 34.sp
                ),
                color = textPrimary,
                textAlign = TextAlign.Center,
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
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Pagination
            ProdyPagerIndicator(
                totalPages = totalPages,
                currentPage = currentPage,
                isDarkTheme = isDarkTheme
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
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val progressInactive = if (isDarkTheme) OnboardingXpArcBackgroundDark else OnboardingXpArcBackgroundLight
    val innerCircleBg = if (isDarkTheme) DarkCardBackground else LightCardBackground
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary

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
            // Trophy icon in circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(innerCircleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Lvl 3",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                color = textPrimary
            )

            Text(
                text = "INITIATE",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                ),
                color = textSecondary
            )
        }

        // Lightning icon on ring (bottom-left)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 32.dp, y = (-24).dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(AccentGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = ButtonTextDark,
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
                tint = AccentGreen,
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
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkTheme) DarkCardBackground else LightCardBackground
    val lockedBorderColor = if (isDarkTheme) ProdyOutlineDark else ProdyOutlineLight
    val textColor = if (isLocked) {
        if (isDarkTheme) DarkTextTertiary else LightTextTertiary
    } else {
        if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    }
    val labelColor = if (isDarkTheme) DarkTextSecondary else LightTextSecondary

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
                            color = AccentGreen,
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
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
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

            Spacer(modifier = Modifier.height(36.dp))

            // Book/Lightbulb Icon Container
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(iconCircleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Headline
            Text(
                text = "Daily Wisdom",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                ),
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(
                text = "Start your day with intention. Prody delivers personalized insights to help you articulate your thoughts.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                ),
                color = textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature Cards
            WisdomFeatureCard(
                icon = Icons.Outlined.TextFormat,
                title = "Word of the Day",
                description = "Expand your vocabulary",
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(12.dp))

            WisdomFeatureCard(
                icon = Icons.Outlined.FormatQuote,
                title = "Quote Collection",
                description = "Insights from great minds",
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(12.dp))

            WisdomFeatureCard(
                icon = Icons.Outlined.School,
                title = "Proverbs & Idioms",
                description = "Timeless cultural wisdom",
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(12.dp))

            WisdomFeatureCard(
                icon = Icons.Outlined.ChatBubble,
                title = "Essential Phrases",
                description = "Communication masterclass",
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

            Spacer(modifier = Modifier.height(16.dp))

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
    val cardBg = if (isDarkTheme) DarkCardBackground else Color(0xFFF5F7F6)
    val iconCircleBg = if (isDarkTheme) DarkIconCircleBackground else Color(0xFFE6F0EE)
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconCircleBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
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
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
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
                color = textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
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
                color = textSecondary,
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
    val cardBackground = if (isDarkTheme) ProdySurfaceContainerDark else ProdySurfaceContainerLight
    val textPrimary = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val dividerColor = if (isDarkTheme) DarkDivider else LightDivider
    val iconCircleBg = if (isDarkTheme) DarkIconCircleBackground else LightIconCircleBackground

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackground)
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
                    .background(iconCircleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            // DAILY WISDOM label
            Text(
                text = "DAILY WISDOM",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp
                ),
                color = textSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quote text with "great work" highlighted
        Text(
            text = buildAnnotatedString {
                append("\"The only way to do ")
                withStyle(SpanStyle(color = AccentGreen)) {
                    append("great work")
                }
                append(" is to love what you do.\"")
            },
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 32.sp
            ),
            color = textPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Divider
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(1.dp)
                .background(dividerColor)
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
                tint = textSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Steve Jobs",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = textSecondary
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
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// =============================================================================
// SCREEN 7: LOGIN/SIGNUP SCREEN
// =============================================================================

@Composable
private fun LoginSignupScreen(
    isDarkTheme: Boolean,
    onLogin: () -> Unit,
    onCreateAccount: () -> Unit,
    onGoogleLogin: () -> Unit,
    onForgotPassword: () -> Unit
) {
    // Colors based on theme
    val backgroundColor = if (isDarkTheme) LoginDarkBackground else LoginLightBackground
    val inputBackground = if (isDarkTheme) LoginDarkInputBackground else LoginLightInputBackground
    val inputIconColor = if (isDarkTheme) LoginDarkInputIcon else LoginLightInputIcon
    val placeholderColor = if (isDarkTheme) LoginDarkPlaceholder else LoginLightPlaceholder
    val subtleTextColor = if (isDarkTheme) LoginDarkSubtleText else LoginLightSubtleText
    val logoContainerColor = if (isDarkTheme) LoginDarkLogoContainer else LoginLightLogoContainer
    val logoLeafColor = if (isDarkTheme) Color.White else LoginLightLogoLeaf
    val textPrimary = if (isDarkTheme) Color.White else LoginLightLogoLeaf
    val dividerLineColor = if (isDarkTheme) ProdyOutlineDark else ProdyOutlineLight
    val socialButtonAddingSoonColor = if (isDarkTheme) DarkTextTertiary else LightTextTertiary

    // State for input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var forgotPasswordSent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Prody Logo with green accent dot - Login specific version
            LoginProdyLogo(
                isDarkTheme = isDarkTheme,
                logoContainerColor = logoContainerColor,
                logoLeafColor = logoLeafColor,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(40.dp))

            // Email Input Field
            LoginInputField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email Address",
                icon = Icons.Outlined.Email,
                isFocused = emailFocused,
                onFocusChange = { emailFocused = it },
                backgroundColor = inputBackground,
                iconColor = inputIconColor,
                placeholderColor = placeholderColor,
                textColor = textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input Field
            LoginInputField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                icon = Icons.Outlined.Lock,
                isPassword = true,
                isFocused = passwordFocused,
                onFocusChange = { passwordFocused = it },
                backgroundColor = inputBackground,
                iconColor = inputIconColor,
                placeholderColor = placeholderColor,
                textColor = textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Forgot Password link - right aligned
            Text(
                text = "Forgot Password?",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = subtleTextColor,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        forgotPasswordEmail = email // Pre-fill with current email if any
                        forgotPasswordSent = false
                        showForgotPasswordDialog = true
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Log In Button
            LoginButton(
                text = "Log In",
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // OR CONTINUE WITH Divider
            OrContinueWithDivider(
                textColor = subtleTextColor,
                lineColor = dividerLineColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Social Login Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Google Button
                SocialLoginButton(
                    onClick = onGoogleLogin,
                    backgroundColor = inputBackground,
                    modifier = Modifier.weight(1f)
                ) {
                    // Google logo using Canvas
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val radius = size.minDimension / 2 - 2.dp.toPx()

                        // Google 'G' colors
                        val googleBlue = Color(0xFF4285F4)
                        val googleRed = Color(0xFFEA4335)
                        val googleYellow = Color(0xFFFBBC05)
                        val googleGreen = Color(0xFF34A853)

                        // Draw the colored arcs of the Google logo
                        // Blue arc (right side)
                        drawArc(
                            color = googleBlue,
                            startAngle = -45f,
                            sweepAngle = 90f,
                            useCenter = true,
                            topLeft = Offset(centerX - radius, centerY - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                        // Red arc (top)
                        drawArc(
                            color = googleRed,
                            startAngle = -135f,
                            sweepAngle = 90f,
                            useCenter = true,
                            topLeft = Offset(centerX - radius, centerY - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                        // Yellow arc (bottom-left)
                        drawArc(
                            color = googleYellow,
                            startAngle = 135f,
                            sweepAngle = 90f,
                            useCenter = true,
                            topLeft = Offset(centerX - radius, centerY - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                        // Green arc (bottom)
                        drawArc(
                            color = googleGreen,
                            startAngle = 45f,
                            sweepAngle = 90f,
                            useCenter = true,
                            topLeft = Offset(centerX - radius, centerY - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                        // White center circle to create the G shape
                        drawCircle(
                            color = if (isDarkTheme) LoginDarkInputBackground else Color.White,
                            radius = radius * 0.55f,
                            center = Offset(centerX, centerY)
                        )
                        // Blue horizontal bar for the G
                        drawRect(
                            color = googleBlue,
                            topLeft = Offset(centerX - 1.dp.toPx(), centerY - 2.dp.toPx()),
                            size = Size(radius + 2.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }

                // Adding soon Button
                SocialLoginButton(
                    onClick = { },
                    backgroundColor = inputBackground,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Adding soon",
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        color = socialButtonAddingSoonColor
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(32.dp))

            // New to Prody? Create an Account
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New to Prody? ",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = subtleTextColor
                )
                Text(
                    text = "Create an Account",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                    ),
                    color = AccentGreen,
                    modifier = Modifier.clickable { onCreateAccount() }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Forgot Password Dialog
        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                email = forgotPasswordEmail,
                onEmailChange = { forgotPasswordEmail = it },
                isSent = forgotPasswordSent,
                onSend = {
                    if (forgotPasswordEmail.isNotBlank() && forgotPasswordEmail.contains("@")) {
                        forgotPasswordSent = true
                    }
                },
                onDismiss = { showForgotPasswordDialog = false },
                isDarkTheme = isDarkTheme
            )
        }
    }
}

/**
 * Forgot Password Dialog - Allows user to request password reset email
 */
@Composable
private fun ForgotPasswordDialog(
    email: String,
    onEmailChange: (String) -> Unit,
    isSent: Boolean,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {
    val dialogBg = if (isDarkTheme) LoginDarkBackground else LoginLightBackground
    val textPrimary = if (isDarkTheme) Color.White else LoginLightLogoLeaf
    val textSecondary = if (isDarkTheme) LoginDarkSubtleText else LoginLightSubtleText
    val inputBg = if (isDarkTheme) LoginDarkInputBackground else LoginLightInputBackground

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = dialogBg,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AccentGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSent) Icons.Filled.MarkEmailRead else Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isSent) "Check Your Email" else "Reset Password",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isSent)
                        "We've sent password reset instructions to $email"
                    else
                        "Enter your email address and we'll send you instructions to reset your password.",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    color = textSecondary,
                    textAlign = TextAlign.Center
                )

                if (!isSent) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Email input
                    LoginInputField(
                        value = email,
                        onValueChange = onEmailChange,
                        placeholder = "Email Address",
                        icon = Icons.Outlined.Email,
                        isFocused = false,
                        onFocusChange = {},
                        backgroundColor = inputBg,
                        iconColor = textSecondary,
                        placeholderColor = textSecondary,
                        textColor = textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isSent) {
                    // Done button
                    Surface(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        color = AccentGreen,
                        tonalElevation = 0.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Done",
                                style = TextStyle(
                                    fontFamily = PoppinsFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = ButtonTextDark
                            )
                        }
                    }
                } else {
                    // Send Reset Link button
                    Surface(
                        onClick = onSend,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        color = if (email.isNotBlank() && email.contains("@")) AccentGreen else AccentGreen.copy(alpha = 0.5f),
                        tonalElevation = 0.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Send Reset Link",
                                style = TextStyle(
                                    fontFamily = PoppinsFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = ButtonTextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cancel link
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = textSecondary,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }
            }
        }
    }
}

/**
 * Login-specific Prody Logo - Circular container with leaf icon and green accent dot
 */
@Composable
private fun LoginProdyLogo(
    isDarkTheme: Boolean,
    logoContainerColor: Color,
    logoLeafColor: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Circular container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(logoContainerColor),
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
                drawPath(centerLeaf, logoLeafColor)

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
                drawPath(leftLeaf, logoLeafColor)

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
                drawPath(rightLeaf, logoLeafColor)
            }
        }

        // Green accent dot at top-right
        Box(
            modifier = Modifier
                .size(12.dp)
                .align(Alignment.TopEnd)
                .offset(x = 0.dp, y = 4.dp)
                .clip(CircleShape)
                .background(AccentGreen)
        )
    }
}

/**
 * Login Input Field - Pill-shaped input with icon
 */
@Composable
private fun LoginInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    backgroundColor: Color,
    iconColor: Color,
    placeholderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(29.dp))
            .background(backgroundColor)
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = AccentGreen,
                        shape = RoundedCornerShape(29.dp)
                    )
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { onFocusChange(it.isFocused) },
                textStyle = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = textColor
                ),
                singleLine = true,
                cursorBrush = SolidColor(AccentGreen),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = TextStyle(
                                    fontFamily = PoppinsFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.sp
                                ),
                                color = placeholderColor
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

/**
 * Login Button - Vibrant green pill button with arrow
 */
@Composable
private fun LoginButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(29.dp),
        color = AccentGreen,
        tonalElevation = 0.dp // Flat design
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = ButtonTextDark,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * OR CONTINUE WITH Divider
 */
@Composable
private fun OrContinueWithDivider(
    textColor: Color,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(lineColor)
        )

        Text(
            text = "OR CONTINUE WITH",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(lineColor)
        )
    }
}

/**
 * Social Login Button - Pill-shaped button for social login options
 */
@Composable
private fun SocialLoginButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(29.dp),
        color = backgroundColor,
        tonalElevation = 0.dp // Flat design
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
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
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isDarkTheme) LogoContainerDark else LogoContainerLight
    val strokeColor = if (isDarkTheme) LogoContainerStroke else Color(0xFFE0E7E6)
    val leafColor = if (isDarkTheme) Color.White else Color(0xFF1A1A1A)

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
 * Primary onboarding button - vibrant green with optional arrow
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
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(29.dp),
        color = AccentGreen,
        tonalElevation = 0.dp // Flat design
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
