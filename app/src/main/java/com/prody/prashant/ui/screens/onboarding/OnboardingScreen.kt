package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.R
import com.prody.prashant.ui.components.FloatingParticlesBackground
import com.prody.prashant.ui.components.GeometricPatternBackground
import com.prody.prashant.ui.components.WavePattern
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class OnboardingPage(
    val icon: ImageVector,
    val titleResId: Int,
    val descriptionResId: Int,
    val backgroundColor: Color,
    val iconColor: Color,
    val accentEmoji: String = "",
    val tagline: String = ""
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Filled.AutoAwesome,
            titleResId = R.string.onboarding_welcome_title,
            descriptionResId = R.string.onboarding_welcome_desc,
            backgroundColor = ProdyPrimary,
            iconColor = ProdyTertiary,
            tagline = "Your journey begins here"
        ),
        OnboardingPage(
            icon = Icons.Filled.Lightbulb,
            titleResId = R.string.onboarding_wisdom_title,
            descriptionResId = R.string.onboarding_wisdom_desc,
            backgroundColor = ProdyPrimaryVariant,
            iconColor = MoodMotivated,
            tagline = "Learn something new every day"
        ),
        OnboardingPage(
            icon = Icons.Filled.SelfImprovement,
            titleResId = R.string.onboarding_journal_title,
            descriptionResId = R.string.onboarding_journal_desc,
            backgroundColor = ProdyPrimary,
            iconColor = MoodCalm,
            tagline = "Reflect with Buddha's guidance"
        ),
        OnboardingPage(
            icon = Icons.Filled.Schedule,
            titleResId = R.string.onboarding_future_title,
            descriptionResId = R.string.onboarding_future_desc,
            backgroundColor = ProdyPrimaryVariant,
            iconColor = MoodExcited,
            tagline = "Send messages to tomorrow"
        ),
        OnboardingPage(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            titleResId = R.string.onboarding_growth_title,
            descriptionResId = R.string.onboarding_growth_desc,
            backgroundColor = ProdyPrimary,
            iconColor = AchievementUnlocked,
            tagline = "Track your transformation"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        pages[pagerState.currentPage].backgroundColor,
                        pages[pagerState.currentPage].backgroundColor.copy(alpha = 0.85f),
                        pages[pagerState.currentPage].backgroundColor.copy(alpha = 0.7f)
                    )
                )
            )
    ) {
        // Animated background patterns
        AnimatedOnboardingBackground(
            currentPage = pagerState.currentPage,
            pageColor = pages[pagerState.currentPage].iconColor
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Header with skip button and progress
            OnboardingHeader(
                currentPage = pagerState.currentPage,
                totalPages = pages.size,
                isLastPage = isLastPage,
                onSkip = {
                    viewModel.completeOnboarding()
                    onComplete()
                }
            )

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                AnimatedOnboardingPageContent(
                    page = pages[page],
                    pageIndex = page,
                    isCurrentPage = page == pagerState.currentPage
                )
            }

            // Bottom section with indicators and buttons
            AnimatedBottomSection(
                currentPage = pagerState.currentPage,
                totalPages = pages.size,
                isLastPage = isLastPage,
                pageColor = pages[pagerState.currentPage].backgroundColor,
                onBack = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                onNext = {
                    if (isLastPage) {
                        viewModel.completeOnboarding()
                        onComplete()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun AnimatedOnboardingBackground(
    currentPage: Int,
    pageColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_animation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bg_rotation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Floating particles
        FloatingParticlesBackground(
            modifier = Modifier.fillMaxSize(),
            particleCount = 30,
            particleColor = Color.White.copy(alpha = 0.06f)
        )

        // Geometric pattern
        GeometricPatternBackground(
            modifier = Modifier.fillMaxSize(),
            patternColor = Color.White.copy(alpha = 0.03f),
            animated = true
        )

        // Large decorative circles
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation * 0.1f)
        ) {
            val circleRadius = size.width * 0.6f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        pageColor.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.8f, size.height * 0.2f),
                    radius = circleRadius
                ),
                radius = circleRadius,
                center = Offset(size.width * 0.8f, size.height * 0.2f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.1f, size.height * 0.7f),
                    radius = circleRadius * 0.8f
                ),
                radius = circleRadius * 0.8f,
                center = Offset(size.width * 0.1f, size.height * 0.7f)
            )
        }

        // Wave pattern at bottom
        WavePattern(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .alpha(0.4f),
            waveColor = Color.White.copy(alpha = 0.05f),
            waveCount = 4
        )
    }
}

@Composable
private fun OnboardingHeader(
    currentPage: Int,
    totalPages: Int,
    isLastPage: Boolean,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Progress indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${currentPage + 1}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "/ $totalPages",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        // Skip button
        AnimatedVisibility(
            visible = !isLastPage,
            enter = fadeIn() + slideInHorizontally { it },
            exit = fadeOut() + slideOutHorizontally { it }
        ) {
            TextButton(
                onClick = onSkip,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    text = stringResource(R.string.skip),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedOnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    isCurrentPage: Boolean
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isCurrentPage) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "page_scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isCurrentPage) 1f else 0.5f,
        animationSpec = tween(300),
        label = "page_alpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "icon_animation_$pageIndex")

    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_pulse"
    )

    val iconRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_rotation"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .scale(animatedScale)
            .alpha(animatedAlpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated icon with multiple layers
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow ring
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(iconScale * 1.1f)
                    .blur(30.dp)
                    .alpha(glowAlpha * 0.5f)
                    .clip(CircleShape)
                    .background(page.iconColor)
            )

            // Middle decorative ring
            Canvas(
                modifier = Modifier
                    .size(180.dp)
                    .rotate(iconRotation * 10)
            ) {
                val strokeWidth = 2.dp.toPx()
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            page.iconColor.copy(alpha = 0.6f),
                            Color.Transparent,
                            page.iconColor.copy(alpha = 0.6f)
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 300f,
                    useCenter = false,
                    style = Stroke(strokeWidth)
                )
            }

            // Icon background
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(iconScale)
                    .rotate(iconRotation)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                page.iconColor.copy(alpha = 0.4f),
                                page.iconColor.copy(alpha = 0.15f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White
                )
            }

            // Decorative dots around icon
            repeat(6) { index ->
                val angle = (index * 60f) * (PI / 180f).toFloat()
                val radius = 90.dp
                val dotSize by infiniteTransition.animateFloat(
                    initialValue = 4f,
                    targetValue = 8f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 1500,
                            delayMillis = index * 200,
                            easing = EaseInOutSine
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot_$index"
                )

                Box(
                    modifier = Modifier
                        .offset(
                            x = (cos(angle) * radius.value).dp,
                            y = (sin(angle) * radius.value).dp
                        )
                        .size(dotSize.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Tagline
        if (page.tagline.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Text(
                    text = page.tagline,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Title
        Text(
            text = stringResource(page.titleResId),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = stringResource(page.descriptionResId),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )
    }
}

@Composable
private fun AnimatedBottomSection(
    currentPage: Int,
    totalPages: Int,
    isLastPage: Boolean,
    pageColor: Color,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated page indicators
        Row(
            modifier = Modifier.padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(totalPages) { index ->
                AnimatedPageIndicator(
                    isSelected = index == currentPage,
                    isPassed = index < currentPage
                )
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            AnimatedVisibility(
                visible = currentPage > 0,
                enter = fadeIn() + slideInHorizontally { -it },
                exit = fadeOut() + slideOutHorizontally { -it }
            ) {
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.back),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            if (currentPage == 0) {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Next/Get Started button with animation
            val buttonScale by animateFloatAsState(
                targetValue = if (isLastPage) 1.05f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                ),
                label = "button_scale"
            )

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = pageColor
                ),
                modifier = Modifier
                    .height(56.dp)
                    .scale(buttonScale)
                    .then(
                        if (isLastPage) Modifier.fillMaxWidth(0.75f)
                        else Modifier
                    ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                if (isLastPage) {
                    Icon(
                        imageVector = Icons.Filled.Rocket,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = if (isLastPage) stringResource(R.string.get_started)
                    else stringResource(R.string.next),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                if (!isLastPage) {
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
}

@Composable
private fun AnimatedPageIndicator(
    isSelected: Boolean,
    isPassed: Boolean
) {
    val width by animateDpAsState(
        targetValue = when {
            isSelected -> 32.dp
            isPassed -> 12.dp
            else -> 8.dp
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "indicator_width"
    )

    val color by animateColorAsState(
        targetValue = when {
            isSelected -> Color.White
            isPassed -> Color.White.copy(alpha = 0.6f)
            else -> Color.White.copy(alpha = 0.3f)
        },
        animationSpec = tween(300),
        label = "indicator_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "indicator_scale"
    )

    Box(
        modifier = Modifier
            .height(8.dp)
            .width(width)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}
