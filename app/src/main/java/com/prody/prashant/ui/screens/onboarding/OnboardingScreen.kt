package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.R
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class OnboardingPage(
    val icon: ImageVector,
    val titleResId: Int,
    val descriptionResId: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val illustrationType: IllustrationType
)

enum class IllustrationType {
    GROWTH_SPIRAL,
    WISDOM_RAYS,
    JOURNAL_WAVES,
    TIME_PORTAL,
    ACHIEVEMENT_STARS
}

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
            primaryColor = ProdyPrimary,
            secondaryColor = ProdyTertiary,
            accentColor = Color(0xFFFFD700),
            illustrationType = IllustrationType.GROWTH_SPIRAL
        ),
        OnboardingPage(
            icon = Icons.Filled.Lightbulb,
            titleResId = R.string.onboarding_wisdom_title,
            descriptionResId = R.string.onboarding_wisdom_desc,
            primaryColor = Color(0xFF5E35B1),
            secondaryColor = Color(0xFF9575CD),
            accentColor = MoodMotivated,
            illustrationType = IllustrationType.WISDOM_RAYS
        ),
        OnboardingPage(
            icon = Icons.Filled.SelfImprovement,
            titleResId = R.string.onboarding_journal_title,
            descriptionResId = R.string.onboarding_journal_desc,
            primaryColor = Color(0xFF00695C),
            secondaryColor = Color(0xFF4DB6AC),
            accentColor = MoodCalm,
            illustrationType = IllustrationType.JOURNAL_WAVES
        ),
        OnboardingPage(
            icon = Icons.Filled.Schedule,
            titleResId = R.string.onboarding_future_title,
            descriptionResId = R.string.onboarding_future_desc,
            primaryColor = Color(0xFF1565C0),
            secondaryColor = Color(0xFF64B5F6),
            accentColor = MoodExcited,
            illustrationType = IllustrationType.TIME_PORTAL
        ),
        OnboardingPage(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            titleResId = R.string.onboarding_growth_title,
            descriptionResId = R.string.onboarding_growth_desc,
            primaryColor = ProdyPrimary,
            secondaryColor = ProdyTertiary,
            accentColor = GoldTier,
            illustrationType = IllustrationType.ACHIEVEMENT_STARS
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    // Animated background color transition
    val currentPage = pages[pagerState.currentPage]
    val targetPage = if (pagerState.currentPageOffsetFraction > 0 && pagerState.currentPage < pages.size - 1) {
        pages[pagerState.currentPage + 1]
    } else if (pagerState.currentPageOffsetFraction < 0 && pagerState.currentPage > 0) {
        pages[pagerState.currentPage - 1]
    } else {
        currentPage
    }

    val animatedPrimaryColor by animateColorAsState(
        targetValue = currentPage.primaryColor,
        animationSpec = tween(500),
        label = "primary_color"
    )
    val animatedSecondaryColor by animateColorAsState(
        targetValue = currentPage.secondaryColor,
        animationSpec = tween(500),
        label = "secondary_color"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        animatedPrimaryColor,
                        animatedPrimaryColor.copy(alpha = 0.9f),
                        animatedSecondaryColor.copy(alpha = 0.7f)
                    )
                )
            )
    ) {
        // Animated background particles
        AnimatedBackgroundParticles(
            primaryColor = animatedPrimaryColor,
            accentColor = currentPage.accentColor
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Top bar with skip button and progress
            TopSection(
                pagerState = pagerState,
                isLastPage = isLastPage,
                pageCount = pages.size,
                onSkip = {
                    viewModel.completeOnboarding()
                    onComplete()
                }
            )

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                beyondViewportPageCount = 1
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    pageIndex = page,
                    isCurrentPage = page == pagerState.currentPage,
                    pagerState = pagerState
                )
            }

            // Bottom section with indicators and buttons
            BottomSection(
                pagerState = pagerState,
                pageCount = pages.size,
                isLastPage = isLastPage,
                currentPageColor = currentPage.accentColor,
                onNext = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }
                },
                onBack = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage - 1,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }
                },
                onComplete = {
                    viewModel.completeOnboarding()
                    onComplete()
                }
            )
        }
    }
}

@Composable
private fun AnimatedBackgroundParticles(
    primaryColor: Color,
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )

    val particles = remember {
        List(30) {
            BackgroundParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 6f + 2f,
                speed = Random.nextFloat() * 0.3f + 0.1f,
                alpha = Random.nextFloat() * 0.4f + 0.1f,
                isAccent = Random.nextFloat() > 0.7f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val y = (particle.y + time * particle.speed) % 1f
            val x = particle.x + sin(y * PI * 2 + particle.x * PI).toFloat() * 0.03f
            val color = if (particle.isAccent) accentColor else Color.White
            drawCircle(
                color = color.copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

private data class BackgroundParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float,
    val isAccent: Boolean
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopSection(
    pagerState: PagerState,
    isLastPage: Boolean,
    pageCount: Int,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Progress indicator
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${pagerState.currentPage + 1} / $pageCount",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
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
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    isCurrentPage: Boolean,
    pagerState: PagerState
) {
    // Animation states
    val animatedScale by animateFloatAsState(
        targetValue = if (isCurrentPage) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isCurrentPage) 1f else 0.5f,
        animationSpec = tween(300),
        label = "alpha"
    )

    // Staggered entrance animation
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            delay(100)
            showContent = true
        } else {
            showContent = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .scale(animatedScale)
            .alpha(animatedAlpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated illustration
        Box(
            modifier = Modifier
                .size(220.dp)
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedIllustration(
                type = page.illustrationType,
                primaryColor = page.primaryColor,
                secondaryColor = page.secondaryColor,
                accentColor = page.accentColor,
                isActive = isCurrentPage
            )

            // Icon overlay
            androidx.compose.animation.AnimatedVisibility(
                visible = showContent,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title with staggered animation
        AnimatedVisibility(
            visible = showContent,
            enter = slideInVertically(
                initialOffsetY = { 50 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(400, delayMillis = 100))
        ) {
            Text(
                text = stringResource(page.titleResId),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    letterSpacing = (-0.5).sp
                ),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description with staggered animation
        AnimatedVisibility(
            visible = showContent,
            enter = slideInVertically(
                initialOffsetY = { 50 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(400, delayMillis = 200))
        ) {
            Text(
                text = stringResource(page.descriptionResId),
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 26.sp
                ),
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AnimatedIllustration(
    type: IllustrationType,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color,
    isActive: Boolean
) {
    when (type) {
        IllustrationType.GROWTH_SPIRAL -> GrowthSpiralAnimation(accentColor, isActive)
        IllustrationType.WISDOM_RAYS -> WisdomRaysAnimation(accentColor, isActive)
        IllustrationType.JOURNAL_WAVES -> JournalWavesAnimation(secondaryColor, isActive)
        IllustrationType.TIME_PORTAL -> TimePortalAnimation(accentColor, isActive)
        IllustrationType.ACHIEVEMENT_STARS -> AchievementStarsAnimation(accentColor, isActive)
    }
}

@Composable
private fun GrowthSpiralAnimation(accentColor: Color, isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "growth_spiral")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spiral_rotation"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .scale(if (isActive) pulseScale else 1f)
            .rotate(rotation)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = minOf(size.width, size.height) / 2 * 0.9f

        // Draw multiple spiral arms
        for (arm in 0 until 3) {
            val armOffset = arm * 120f
            val path = Path()
            var isFirst = true
            for (angle in 0..720 step 5) {
                val radians = (angle + armOffset) * PI / 180
                val progress = angle / 720f
                val radius = maxRadius * progress
                val x = center.x + radius * cos(radians).toFloat()
                val y = center.y + radius * sin(radians).toFloat()
                if (isFirst) {
                    path.moveTo(x, y)
                    isFirst = false
                } else {
                    path.lineTo(x, y)
                }
            }
            drawPath(
                path = path,
                color = if (arm == 0) accentColor else Color.White.copy(alpha = 0.4f - arm * 0.1f),
                style = Stroke(width = 3f - arm * 0.5f, cap = StrokeCap.Round)
            )
        }

        // Center glow
        for (i in 3 downTo 0) {
            drawCircle(
                color = accentColor.copy(alpha = 0.15f - i * 0.03f),
                radius = 40f + i * 15f,
                center = center
            )
        }
    }
}

@Composable
private fun WisdomRaysAnimation(accentColor: Color, isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "wisdom_rays")
    val rayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ray_rotation"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .rotate(rayRotation)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = minOf(size.width, size.height) / 2 * 0.85f

        // Draw sun rays
        for (i in 0 until 12) {
            val angle = i * 30f * PI / 180
            val innerRadius = 50f
            val outerRadius = maxRadius * (if (i % 2 == 0) 1f else 0.7f)

            val startX = center.x + innerRadius * cos(angle).toFloat()
            val startY = center.y + innerRadius * sin(angle).toFloat()
            val endX = center.x + outerRadius * cos(angle).toFloat()
            val endY = center.y + outerRadius * sin(angle).toFloat()

            drawLine(
                color = if (i % 2 == 0) accentColor.copy(alpha = pulseAlpha)
                else Color.White.copy(alpha = pulseAlpha * 0.5f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = if (i % 2 == 0) 4f else 2f,
                cap = StrokeCap.Round
            )
        }

        // Inner circles
        for (i in 2 downTo 0) {
            drawCircle(
                color = Color.White.copy(alpha = 0.1f + i * 0.05f),
                radius = 30f + i * 15f,
                center = center
            )
        }
    }
}

@Composable
private fun JournalWavesAnimation(secondaryColor: Color, isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "journal_waves")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)

        // Draw multiple waves
        for (wave in 0 until 4) {
            val path = Path()
            val waveY = center.y + (wave - 1.5f) * 35f
            val amplitude = 20f - wave * 3f
            val phase = wavePhase + wave * 0.5f

            path.moveTo(0f, waveY)
            for (x in 0..size.width.toInt() step 3) {
                val y = waveY + amplitude * sin(x * 0.02f + phase)
                path.lineTo(x.toFloat(), y.toFloat())
            }

            drawPath(
                path = path,
                color = if (wave == 0) secondaryColor.copy(alpha = 0.8f)
                else Color.White.copy(alpha = 0.4f - wave * 0.08f),
                style = Stroke(width = 3f - wave * 0.5f, cap = StrokeCap.Round)
            )
        }

        // Floating dots
        for (i in 0 until 8) {
            val dotX = (size.width / 8) * (i + 0.5f)
            val dotY = center.y + 20f * sin(wavePhase + i * 0.5f)
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = 4f,
                center = Offset(dotX, dotY.toFloat())
            )
        }
    }
}

@Composable
private fun TimePortalAnimation(accentColor: Color, isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "time_portal")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_rotation"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "portal_pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize().scale(pulseScale)) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = minOf(size.width, size.height) / 2 * 0.8f

        // Outer rotating ring
        drawArc(
            color = accentColor.copy(alpha = 0.4f),
            startAngle = ringRotation,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = 4f, cap = StrokeCap.Round),
            topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
            size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
        )

        // Middle ring
        val midRadius = maxRadius * 0.7f
        drawArc(
            color = Color.White.copy(alpha = 0.5f),
            startAngle = innerRotation,
            sweepAngle = 200f,
            useCenter = false,
            style = Stroke(width = 3f, cap = StrokeCap.Round),
            topLeft = Offset(center.x - midRadius, center.y - midRadius),
            size = androidx.compose.ui.geometry.Size(midRadius * 2, midRadius * 2)
        )

        // Inner ring
        val innerRadius = maxRadius * 0.4f
        drawArc(
            color = accentColor.copy(alpha = 0.6f),
            startAngle = ringRotation * 1.5f,
            sweepAngle = 150f,
            useCenter = false,
            style = Stroke(width = 2f, cap = StrokeCap.Round),
            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
            size = androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2)
        )

        // Center glow
        for (i in 4 downTo 0) {
            drawCircle(
                color = accentColor.copy(alpha = 0.15f - i * 0.02f),
                radius = 20f + i * 10f,
                center = center
            )
        }
    }
}

@Composable
private fun AchievementStarsAnimation(accentColor: Color, isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "achievement_stars")

    val starAngles = remember {
        List(8) { Random.nextFloat() * 360f }
    }
    val starDistances = remember {
        List(8) { Random.nextFloat() * 0.3f + 0.5f }
    }
    val starSizes = remember {
        List(8) { Random.nextFloat() * 6f + 4f }
    }

    val twinkles = starAngles.mapIndexed { index, _ ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000 + index * 200, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "twinkle_$index"
        )
    }

    val mainRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "main_rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize().rotate(mainRotation * 0.1f)) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = minOf(size.width, size.height) / 2 * 0.85f

        // Draw stars
        starAngles.forEachIndexed { index, baseAngle ->
            val angle = (baseAngle + mainRotation * 0.2f) * PI / 180
            val distance = starDistances[index] * maxRadius
            val x = center.x + distance * cos(angle).toFloat()
            val y = center.y + distance * sin(angle).toFloat()
            val starSize = starSizes[index]
            val alpha = twinkles[index].value

            // Draw 4-point star
            val starPath = Path().apply {
                moveTo(x, y - starSize)
                lineTo(x + starSize * 0.3f, y)
                lineTo(x, y + starSize)
                lineTo(x - starSize * 0.3f, y)
                close()
                moveTo(x - starSize, y)
                lineTo(x, y + starSize * 0.3f)
                lineTo(x + starSize, y)
                lineTo(x, y - starSize * 0.3f)
                close()
            }

            drawPath(
                path = starPath,
                color = if (index % 2 == 0) accentColor.copy(alpha = alpha)
                else Color.White.copy(alpha = alpha * 0.8f)
            )
        }

        // Center achievement badge glow
        for (i in 3 downTo 0) {
            drawCircle(
                color = accentColor.copy(alpha = 0.1f + i * 0.03f),
                radius = 35f + i * 12f,
                center = center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BottomSection(
    pagerState: PagerState,
    pageCount: Int,
    isLastPage: Boolean,
    currentPageColor: Color,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated page indicators
        Row(
            modifier = Modifier.padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { index ->
                AnimatedPageIndicator(
                    isSelected = index == pagerState.currentPage,
                    isPassed = index < pagerState.currentPage,
                    accentColor = currentPageColor
                )
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            AnimatedVisibility(
                visible = pagerState.currentPage > 0,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .height(56.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.3f)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Spacer when back button is hidden
            if (pagerState.currentPage == 0) {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Next/Get Started button
            val buttonScale by animateFloatAsState(
                targetValue = if (isLastPage) 1.05f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                ),
                label = "button_scale"
            )

            Button(
                onClick = { if (isLastPage) onComplete() else onNext() },
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f)
                    .scale(buttonScale),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = currentPageColor
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                AnimatedContent(
                    targetState = isLastPage,
                    transitionSpec = {
                        (slideInVertically { it } + fadeIn()) togetherWith
                                (slideOutVertically { -it } + fadeOut())
                    },
                    label = "button_content"
                ) { isLast ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLast) stringResource(R.string.get_started)
                            else stringResource(R.string.next),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (!isLast) {
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
    }
}

@Composable
private fun AnimatedPageIndicator(
    isSelected: Boolean,
    isPassed: Boolean,
    accentColor: Color
) {
    val width by animateDpAsState(
        targetValue = when {
            isSelected -> 32.dp
            isPassed -> 12.dp
            else -> 8.dp
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicator_width"
    )

    val height by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "indicator_height"
    )

    val color by animateColorAsState(
        targetValue = when {
            isSelected -> Color.White
            isPassed -> accentColor.copy(alpha = 0.7f)
            else -> Color.White.copy(alpha = 0.3f)
        },
        animationSpec = tween(300),
        label = "indicator_color"
    )

    Box(
        modifier = Modifier
            .height(height)
            .width(width)
            .clip(CircleShape)
            .background(color)
    )
}
