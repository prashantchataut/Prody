package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.R
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Onboarding Screen - Complete redesign based on provided Sketch designs
 *
 * A 6-page horizontal pager flow with pixel-perfect recreation of the designs:
 * 1. Welcome Screen - Logo, app name, tagline, Get Started
 * 2. Journaling/AI Feature - Daily insight card with abstract waves
 * 3. Gamification Leaderboard - Trophy with rankings preview
 * 4. XP Arc Screen - Circular progress with level display
 * 5. Daily Wisdom Features - Feature list with icons
 * 6. Personalized Insights - Quote card with notifications CTA
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 6 })
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    // Theme colors based on system theme
    val backgroundColor = if (isDarkTheme) OnboardingBackgroundDark else OnboardingBackgroundLight

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar with back button, progress indicator, and skip button
            OnboardingTopBar(
                pagerState = pagerState,
                isDarkTheme = isDarkTheme,
                onBack = {
                    coroutineScope.launch {
                        if (pagerState.currentPage > 0) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
                onSkip = {
                    viewModel.completeOnboarding()
                    onComplete()
                }
            )

            // Main pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                beyondViewportPageCount = 1
            ) { page ->
                when (page) {
                    0 -> WelcomeScreen(isDarkTheme = isDarkTheme)
                    1 -> JournalingFeatureScreen(isDarkTheme = isDarkTheme)
                    2 -> GamificationLeaderboardScreen(isDarkTheme = isDarkTheme)
                    3 -> XpArcScreen(isDarkTheme = isDarkTheme)
                    4 -> DailyWisdomFeaturesScreen(isDarkTheme = isDarkTheme)
                    5 -> PersonalizedInsightsScreen(isDarkTheme = isDarkTheme)
                }
            }

            // Bottom navigation section
            OnboardingBottomSection(
                pagerState = pagerState,
                isDarkTheme = isDarkTheme,
                onContinue = {
                    coroutineScope.launch {
                        if (pagerState.currentPage < 5) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            viewModel.completeOnboarding()
                            onComplete()
                        }
                    }
                },
                onMaybeLater = {
                    viewModel.completeOnboarding()
                    onComplete()
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingTopBar(
    pagerState: PagerState,
    isDarkTheme: Boolean,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    val showBackButton = pagerState.currentPage > 0
    val showSkipButton = pagerState.currentPage < 5
    val showProgressIndicator = pagerState.currentPage > 0

    val textColor = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight
    val progressActive = OnboardingProgressActive
    val progressInactive = if (isDarkTheme) OnboardingProgressInactiveDark else OnboardingProgressInactiveLight

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button (visible from page 1 onwards)
        if (showBackButton) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = textColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }

        // Progress indicator (segmented bar style - visible from page 1)
        if (showProgressIndicator) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                repeat(4) { index ->
                    val isActive = index < pagerState.currentPage
                    val isCurrent = index == pagerState.currentPage - 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    isActive || isCurrent -> progressActive
                                    else -> progressInactive
                                }
                            )
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // Skip button
        if (showSkipButton) {
            TextButton(
                onClick = onSkip,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_skip),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnboardingAccent
                )
            }
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingBottomSection(
    pagerState: PagerState,
    isDarkTheme: Boolean,
    onContinue: () -> Unit,
    onMaybeLater: () -> Unit
) {
    val currentPage = pagerState.currentPage
    val textSecondary = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page indicator dots (only for pages 0 and 4/5)
        if (currentPage == 0 || currentPage >= 4) {
            OnboardingPageIndicator(
                pageCount = if (currentPage == 0) 3 else 4,
                currentPage = if (currentPage == 0) 0 else currentPage - 2,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Main CTA button
        OnboardingPrimaryButton(
            text = when (currentPage) {
                0 -> stringResource(R.string.onboarding_get_started)
                1 -> stringResource(R.string.onboarding_continue)
                2 -> stringResource(R.string.onboarding_continue)
                3 -> stringResource(R.string.onboarding_start_quest)
                4 -> stringResource(R.string.onboarding_continue)
                5 -> stringResource(R.string.onboarding_enable_notifications)
                else -> stringResource(R.string.onboarding_continue)
            },
            onClick = onContinue,
            isDarkTheme = isDarkTheme,
            showArrow = currentPage == 0 || currentPage == 1 || currentPage == 3 || currentPage == 5,
            modifier = Modifier.fillMaxWidth()
        )

        // Secondary text/button
        when (currentPage) {
            0 -> {
                // "Already have an account? Log in"
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_already_have_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.onboarding_log_in),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { /* Handle login */ }
                    )
                }
            }
            4, 5 -> {
                // "Maybe Later" option
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onMaybeLater) {
                    Text(
                        text = stringResource(R.string.onboarding_maybe_later),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageIndicator(
    pageCount: Int,
    currentPage: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val activeColor = OnboardingProgressActive
    val inactiveColor = if (isDarkTheme) OnboardingProgressInactiveDark else OnboardingProgressInactiveLight

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            Box(
                modifier = Modifier
                    .size(
                        width = if (isActive) 24.dp else 8.dp,
                        height = 8.dp
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isActive) activeColor else inactiveColor)
            )
        }
    }
}

@Composable
private fun OnboardingPrimaryButton(
    text: String,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    showArrow: Boolean = false,
    modifier: Modifier = Modifier
) {
    val buttonColor = OnboardingButtonPrimary
    val textColor = OnboardingButtonTextLight

    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// =============================================================================
// SCREEN 1: WELCOME SCREEN
// =============================================================================

@Composable
private fun WelcomeScreen(isDarkTheme: Boolean) {
    val textPrimary = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight
    val textSecondary = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight
    val logoContainerColor = if (isDarkTheme) OnboardingCardDark else OnboardingSurfaceLight

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Subtle concentric rings background (light mode only)
        if (!isDarkTheme) {
            ConcentricRingsBackground(
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            // Logo container with leaf icon
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(logoContainerColor)
                    .then(
                        if (!isDarkTheme) {
                            Modifier.shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(32.dp),
                                ambientColor = Color.Black.copy(alpha = 0.05f)
                            )
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Leaf icon with green dot
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = Icons.Filled.Spa,
                            contentDescription = "Prody Logo",
                            modifier = Modifier.size(64.dp),
                            tint = if (isDarkTheme) OnboardingAccent else OnboardingTextPrimaryLight
                        )
                        // Small green notification dot
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .offset(x = 4.dp, y = (-4).dp)
                                .clip(CircleShape)
                                .background(OnboardingAccent)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name "Prody"
            Text(
                text = stringResource(R.string.onboarding_app_name),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            Text(
                text = stringResource(R.string.onboarding_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}

@Composable
private fun ConcentricRingsBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.alpha(0.08f)) {
        val center = Offset(size.width / 2, size.height / 3)
        val maxRadius = size.width * 0.8f
        val ringCount = 4

        repeat(ringCount) { index ->
            val radius = maxRadius * (index + 1) / ringCount
            drawCircle(
                color = Color.Gray,
                radius = radius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

// =============================================================================
// SCREEN 2: JOURNALING / AI FEATURE SCREEN
// =============================================================================

@Composable
private fun JournalingFeatureScreen(isDarkTheme: Boolean) {
    val cardColor = if (isDarkTheme) OnboardingCardDark else OnboardingSurfaceLight
    val textPrimary = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight
    val textSecondary = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight
    val badgeColor = if (isDarkTheme) OnboardingFeatureIconBgDark else OnboardingFeatureIconBgLight

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Main card with abstract wave header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Abstract wave header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.55f)
                ) {
                    AbstractWaveBackground(
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Analysis complete badge (dark mode variant)
                    if (isDarkTheme) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OnboardingSurfaceVariantDark)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(OnboardingFeatureIconBgDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = OnboardingAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = stringResource(R.string.onboarding_journaling_analysis_complete),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnboardingAccent,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = stringResource(R.string.onboarding_journaling_insight_generated),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // Card content (light mode variant)
                if (!isDarkTheme) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // AI Generated badge
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeColor)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = OnboardingAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = stringResource(R.string.onboarding_journaling_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnboardingAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = stringResource(R.string.onboarding_journaling_today),
                                style = MaterialTheme.typography.bodySmall,
                                color = textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.onboarding_journaling_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = textPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.onboarding_journaling_prompt),
                            style = MaterialTheme.typography.bodyMedium,
                            color = textSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Input hint with left border
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(24.dp)
                                    .background(OnboardingAccent, RoundedCornerShape(1.5.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.onboarding_journaling_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = textSecondary.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Headline text with highlight
        val headline = buildAnnotatedString {
            append("Unlock Your ")
            withStyle(style = SpanStyle(color = OnboardingAccent)) {
                append("Mind")
            }
        }
        Text(
            text = headline,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isDarkTheme) {
                stringResource(R.string.onboarding_journaling_desc_alt)
            } else {
                stringResource(R.string.onboarding_journaling_desc)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Feature pills (dark mode)
        if (isDarkTheme) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeaturePill(
                    icon = Icons.Outlined.Psychology,
                    text = stringResource(R.string.onboarding_journaling_feature_reflection),
                    isDarkTheme = true
                )
                FeaturePill(
                    icon = Icons.Outlined.TrendingUp,
                    text = stringResource(R.string.onboarding_journaling_feature_growth),
                    isDarkTheme = true
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))
    }
}

@Composable
private fun FeaturePill(
    icon: ImageVector,
    text: String,
    isDarkTheme: Boolean
) {
    val backgroundColor = if (isDarkTheme) OnboardingSurfaceVariantDark else OnboardingFeatureIconBgLight
    val textColor = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OnboardingAccent,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AbstractWaveBackground(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val baseColor = if (isDarkTheme) {
        Color(0xFF2A3D30)
    } else {
        Color(0xFF8BA888)
    }

    Canvas(modifier = modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))) {
        val width = size.width
        val height = size.height

        // Draw gradient background
        drawRect(
            brush = Brush.verticalGradient(
                colors = if (isDarkTheme) {
                    listOf(
                        Color(0xFF1A2E21),
                        Color(0xFF253D2C)
                    )
                } else {
                    listOf(
                        Color(0xFF9DB99A),
                        Color(0xFFB8CAB5)
                    )
                }
            )
        )

        // Draw flowing wave lines
        val wavePaint = baseColor.copy(alpha = 0.4f)
        val waveCount = 8

        repeat(waveCount) { i ->
            val yOffset = height * (0.1f + i * 0.12f)
            val amplitude = height * 0.08f
            val frequency = 0.008f + i * 0.002f

            val path = Path().apply {
                moveTo(0f, yOffset)
                var x = 0f
                while (x <= width) {
                    val y = yOffset + amplitude * sin(x * frequency * PI.toFloat() + i * 0.5f)
                    lineTo(x, y)
                    x += 2f
                }
            }

            drawPath(
                path = path,
                color = wavePaint,
                style = Stroke(width = 1.5f)
            )
        }
    }
}

// =============================================================================
// SCREEN 3: GAMIFICATION LEADERBOARD SCREEN
// =============================================================================

@Composable
private fun GamificationLeaderboardScreen(isDarkTheme: Boolean) {
    val cardColor = if (isDarkTheme) OnboardingCardDark else OnboardingSurfaceLight
    val textPrimary = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight
    val textSecondary = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Leaderboard card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Grid background (subtle)
                if (isDarkTheme) {
                    GridBackground(modifier = Modifier.fillMaxSize())
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Trophy icon with stars
                    Box(
                        modifier = Modifier.padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Trophy circle
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(OnboardingAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = if (isDarkTheme) OnboardingBackgroundDark else Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        // Decorative stars
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = OnboardingAccent,
                            modifier = Modifier
                                .size(20.dp)
                                .offset(x = 50.dp, y = (-30).dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = OnboardingAccent.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(14.dp)
                                .offset(x = (-45).dp, y = (-10).dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Leaderboard rows
                    LeaderboardRow(
                        rank = 1,
                        xp = 2450,
                        isHighlighted = true,
                        isDarkTheme = isDarkTheme
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LeaderboardRow(
                        rank = 2,
                        xp = 1820,
                        isHighlighted = false,
                        isDarkTheme = isDarkTheme
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LeaderboardRow(
                        rank = 3,
                        xp = 1450,
                        isHighlighted = false,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Headline
        Text(
            text = stringResource(R.string.onboarding_gamification_headline),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.onboarding_gamification_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Feature bullets
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureBulletItem(
                icon = Icons.Filled.Star,
                title = stringResource(R.string.onboarding_gamification_earn_xp),
                description = stringResource(R.string.onboarding_gamification_earn_xp_desc),
                isDarkTheme = isDarkTheme
            )
            FeatureBulletItem(
                icon = Icons.Filled.EmojiEvents,
                title = stringResource(R.string.onboarding_gamification_badges),
                description = stringResource(R.string.onboarding_gamification_badges_desc),
                isDarkTheme = isDarkTheme
            )
            FeatureBulletItem(
                icon = Icons.Filled.BarChart,
                title = stringResource(R.string.onboarding_gamification_compete),
                description = stringResource(R.string.onboarding_gamification_compete_desc),
                isDarkTheme = isDarkTheme
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))
    }
}

@Composable
private fun GridBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.alpha(0.15f)) {
        val gridSize = 30.dp.toPx()
        val strokeWidth = 1.dp.toPx()

        // Vertical lines
        var x = 0f
        while (x <= size.width) {
            drawLine(
                color = OnboardingAccent,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = strokeWidth
            )
            x += gridSize
        }

        // Horizontal lines
        var y = 0f
        while (y <= size.height) {
            drawLine(
                color = OnboardingAccent,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = strokeWidth
            )
            y += gridSize
        }
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    xp: Int,
    isHighlighted: Boolean,
    isDarkTheme: Boolean
) {
    val backgroundColor = when {
        isHighlighted && isDarkTheme -> OnboardingLeaderboardRowActiveDark
        isHighlighted -> OnboardingLeaderboardRowActiveLight
        else -> Color.Transparent
    }
    val borderColor = if (isHighlighted) OnboardingAccent else Color.Transparent
    val secondaryTextColor = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .then(
                if (isHighlighted) {
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isHighlighted) OnboardingAccent else secondaryTextColor,
                fontWeight = FontWeight.SemiBold
            )

            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isHighlighted) OnboardingAccent.copy(alpha = 0.2f)
                        else OnboardingProgressInactiveDark.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = if (isHighlighted) OnboardingAccent else secondaryTextColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Progress bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isDarkTheme) OnboardingProgressInactiveDark
                        else OnboardingProgressInactiveLight
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(xp / 3000f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(OnboardingAccent)
                )
            }
        }

        Text(
            text = if (isHighlighted) "$xp XP" else xp.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isHighlighted) OnboardingAccent else secondaryTextColor,
            fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun FeatureBulletItem(
    icon: ImageVector,
    title: String,
    description: String,
    isDarkTheme: Boolean
) {
    val iconBgColor = if (isDarkTheme) OnboardingFeatureIconBgDark else OnboardingFeatureIconBgLight
    val textPrimary = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight
    val textSecondary = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OnboardingAccent,
                modifier = Modifier.size(24.dp)
            )
        }

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary
            )
        }
    }
}

// =============================================================================
// SCREEN 4: XP ARC SCREEN
// =============================================================================

@Composable
private fun XpArcScreen(isDarkTheme: Boolean) {
    val textPrimary = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight
    val textSecondary = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // XP Arc with level display
        Box(
            modifier = Modifier
                .size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            // Circular arc progress
            XpArcIndicator(
                progress = 0.75f,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.fillMaxSize()
            )

            // Center content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDarkTheme) OnboardingFeatureIconBgDark
                            else OnboardingFeatureIconBgLight
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = OnboardingAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Level text
                Text(
                    text = String.format(stringResource(R.string.onboarding_xp_level), 3),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = textPrimary
                )

                // Rank subtitle
                Text(
                    text = stringResource(R.string.onboarding_xp_rank_initiate),
                    style = MaterialTheme.typography.labelLarge,
                    color = textSecondary,
                    letterSpacing = 2.sp
                )
            }

            // Star badge (top right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-20).dp, y = 40.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkTheme) OnboardingFeatureIconBgDark
                        else OnboardingFeatureIconBgLight
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = OnboardingAccent,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Lightning bolt badge (bottom left)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 20.dp, y = (-40).dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkTheme) OnboardingFeatureIconBgDark
                        else OnboardingFeatureIconBgLight
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = null,
                    tint = OnboardingAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Filled.LocalFireDepartment,
                value = "7",
                label = stringResource(R.string.onboarding_xp_day_streak),
                isLocked = false,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Filled.CheckCircle,
                value = "750",
                label = stringResource(R.string.onboarding_xp_total_xp),
                isLocked = false,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Filled.Lock,
                value = "???",
                label = stringResource(R.string.onboarding_xp_locked),
                isLocked = true,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Headline with highlight
        val headline = buildAnnotatedString {
            append("Turn Habits into ")
            withStyle(style = SpanStyle(color = OnboardingAccent)) {
                append("Quests")
            }
        }
        Text(
            text = headline,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.onboarding_xp_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.weight(0.1f))
    }
}

@Composable
private fun XpArcIndicator(
    progress: Float,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val arcBackground = if (isDarkTheme) OnboardingXpArcBackgroundDark else OnboardingXpArcBackground
    val arcFill = OnboardingXpArcFill
    val glowColor = OnboardingXpArcGlow

    Canvas(modifier = modifier) {
        val strokeWidth = 16.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Arc angles: starts from bottom-left, sweeps to bottom-right (270 degree arc)
        val startAngle = 135f
        val sweepAngle = 270f

        // Background arc
        drawArc(
            color = arcBackground,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Progress arc
        val progressSweep = sweepAngle * progress
        drawArc(
            color = arcFill,
            startAngle = startAngle,
            sweepAngle = progressSweep,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Glow effect at the end of progress
        val endAngle = Math.toRadians((startAngle + progressSweep).toDouble())
        val glowX = center.x + radius * cos(endAngle).toFloat()
        val glowY = center.y + radius * sin(endAngle).toFloat()

        // Outer glow
        drawCircle(
            color = glowColor,
            radius = strokeWidth * 1.5f,
            center = Offset(glowX, glowY)
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    isLocked: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = when {
        isLocked && isDarkTheme -> OnboardingStatCardLockedDark
        isLocked -> OnboardingStatCardLockedLight
        isDarkTheme -> OnboardingStatCardDark
        else -> OnboardingStatCardLight
    }
    val textPrimary = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight
    val textSecondary = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight
    val lockedTextColor = if (isDarkTheme) OnboardingTextTertiaryDark else OnboardingTextTertiaryLight

    val borderModifier = if (isLocked) {
        Modifier.border(
            width = 1.dp,
            color = if (isDarkTheme) OnboardingDividerDark else OnboardingDividerLight,
            shape = RoundedCornerShape(16.dp)
        )
    } else {
        if (!isDarkTheme) {
            Modifier.border(
                width = 1.dp,
                color = OnboardingDividerLight,
                shape = RoundedCornerShape(16.dp)
            )
        } else {
            Modifier
        }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .then(borderModifier)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isLocked) lockedTextColor else OnboardingAccent,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = if (isLocked) lockedTextColor else textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isLocked) lockedTextColor else textSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

// =============================================================================
// SCREEN 5: DAILY WISDOM FEATURES SCREEN
// =============================================================================

@Composable
private fun DailyWisdomFeaturesScreen(isDarkTheme: Boolean) {
    val textPrimary = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight
    val textSecondary = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight
    val iconBgColor = if (isDarkTheme) OnboardingFeatureIconBgDark else OnboardingFeatureIconBgLight

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Icon header
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Filled.MenuBook else Icons.Filled.Lightbulb,
                contentDescription = null,
                tint = OnboardingAccent,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Headline
        Text(
            text = stringResource(R.string.onboarding_wisdom_headline),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isDarkTheme) {
                stringResource(R.string.onboarding_wisdom_desc_alt)
            } else {
                stringResource(R.string.onboarding_wisdom_desc)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Feature list
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WisdomFeatureCard(
                icon = Icons.Filled.TextFields,
                iconText = if (isDarkTheme) "Aa" else null,
                title = stringResource(R.string.onboarding_wisdom_word_title),
                description = if (isDarkTheme) {
                    stringResource(R.string.onboarding_wisdom_word_desc_alt)
                } else {
                    stringResource(R.string.onboarding_wisdom_word_desc)
                },
                isDarkTheme = isDarkTheme
            )
            WisdomFeatureCard(
                icon = Icons.Filled.FormatQuote,
                title = stringResource(R.string.onboarding_wisdom_quotes_title),
                description = stringResource(R.string.onboarding_wisdom_quotes_desc),
                isDarkTheme = isDarkTheme
            )
            WisdomFeatureCard(
                icon = Icons.Filled.CollectionsBookmark,
                title = stringResource(R.string.onboarding_wisdom_proverbs_title),
                description = stringResource(R.string.onboarding_wisdom_proverbs_desc),
                isDarkTheme = isDarkTheme
            )
            WisdomFeatureCard(
                icon = Icons.Filled.Chat,
                title = stringResource(R.string.onboarding_wisdom_phrases_title),
                description = if (isDarkTheme) {
                    stringResource(R.string.onboarding_wisdom_phrases_desc_alt)
                } else {
                    stringResource(R.string.onboarding_wisdom_phrases_desc)
                },
                isDarkTheme = isDarkTheme
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))
    }
}

@Composable
private fun WisdomFeatureCard(
    icon: ImageVector,
    iconText: String? = null,
    title: String,
    description: String,
    isDarkTheme: Boolean
) {
    val cardColor = if (isDarkTheme) OnboardingCardDark else OnboardingSurfaceLight
    val iconBgColor = if (isDarkTheme) OnboardingFeatureIconBgDark else OnboardingFeatureIconBgLight
    val textPrimary = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight
    val textSecondary = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            if (iconText != null) {
                Text(
                    text = iconText,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnboardingAccent,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OnboardingAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary
            )
        }
    }
}

// =============================================================================
// SCREEN 6: PERSONALIZED INSIGHTS / NOTIFICATIONS SCREEN
// =============================================================================

@Composable
private fun PersonalizedInsightsScreen(isDarkTheme: Boolean) {
    val textPrimary = if (isDarkTheme) OnboardingTextPrimaryDark else OnboardingTextPrimaryLight
    val textSecondary = if (isDarkTheme) OnboardingTextSecondaryDark else OnboardingTextSecondaryLight
    val cardColor = if (isDarkTheme) OnboardingQuoteCardDark else OnboardingQuoteCardLight
    val iconBgColor = if (isDarkTheme) OnboardingFeatureIconBgDark else OnboardingFeatureIconBgLight

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Quote card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Lightbulb icon
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(iconBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                tint = OnboardingAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Daily Wisdom label
                        Text(
                            text = stringResource(R.string.onboarding_insights_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = textSecondary,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.3f))

                    // Quote text with highlighted words
                    val quoteText = buildAnnotatedString {
                        append("\"The only way to do ")
                        withStyle(
                            style = SpanStyle(
                                color = OnboardingAccent,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("great work")
                        }
                        append(" is to love what you do.\"")
                    }
                    Text(
                        text = quoteText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 34.sp
                        ),
                        color = textPrimary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(2.dp)
                            .background(
                                if (isDarkTheme) OnboardingDividerDark
                                else OnboardingDividerLight
                            )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Author with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!isDarkTheme) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = "—",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textSecondary
                            )
                        }
                        Text(
                            text = stringResource(R.string.onboarding_insights_author),
                            style = MaterialTheme.typography.bodyMedium,
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.4f))

                    // Bottom row with indicators and bookmark
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quote indicators
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(3) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == 0) OnboardingAccent
                                            else if (isDarkTheme) OnboardingProgressInactiveDark
                                            else OnboardingProgressInactiveLight
                                        )
                                )
                            }
                        }

                        // Bookmark icon
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Headline
        Text(
            text = stringResource(R.string.onboarding_insights_headline),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.onboarding_insights_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.weight(0.1f))
    }
}
