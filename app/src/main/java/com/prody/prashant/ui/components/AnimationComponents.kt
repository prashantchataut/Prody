package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
// blur import removed - flat design with no blur effects
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Floating particles animation effect for backgrounds with customizable size range.
 * Use [FloatingParticles] from EnhancedAnimations.kt for a simpler version.
 */
@Composable
fun FloatingParticlesWithSize(
    modifier: Modifier = Modifier,
    particleCount: Int = 20,
    particleColor: Color = Color.White.copy(alpha = 0.3f),
    minSize: Dp = 4.dp,
    maxSize: Dp = 12.dp
) {
    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * (maxSize.value - minSize.value) + minSize.value,
                speed = Random.nextFloat() * 0.5f + 0.2f,
                alpha = Random.nextFloat() * 0.5f + 0.2f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val y = (particle.y + time * particle.speed) % 1f
            val x = particle.x + sin(y * PI * 2).toFloat() * 0.05f
            drawCircle(
                color = particleColor.copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)

/**
 * Pulsing glow effect for highlighting elements
 */
@Composable
fun PulsingGlow(
    modifier: Modifier = Modifier,
    color: Color = ProdyPrimary,
    pulseScale: Float = 1.2f,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseScale,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Flat design - subtle alpha pulse instead of blur glow
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(scale)
                .alpha(alpha * 0.3f)
                .background(color, CircleShape)
        )
        content()
    }
}

/**
 * Animated gradient background with smooth color transitions
 */
@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(ProdyPrimary, ProdyPrimaryVariant, ProdyTertiary),
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )

    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset(animatedOffset * 500f, 0f),
        end = Offset(animatedOffset * 500f + 1000f, 1000f)
    )

    Box(
        modifier = modifier.background(brush),
        content = content
    )
}

/**
 * Confetti animation for celebrations
 */
@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    particleCount: Int = 50,
    colors: List<Color> = listOf(
        MoodHappy, MoodExcited, MoodMotivated, MoodGrateful,
        GoldTier, ProdyPrimary, ProdyTertiary
    )
) {
    if (!isPlaying) return

    // Ensure we have at least one color to prevent crash on empty list
    // This provides defensive programming against callers passing empty lists
    val safeColors = colors.ifEmpty {
        listOf(MoodHappy, MoodExcited, MoodMotivated, MoodGrateful, GoldTier, ProdyPrimary, ProdyTertiary)
    }

    val particles = remember(safeColors) {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                startY = -Random.nextFloat() * 0.2f,
                rotation = Random.nextFloat() * 360f,
                size = Random.nextFloat() * 8f + 4f,
                color = safeColors.random(),
                speed = Random.nextFloat() * 0.3f + 0.5f,
                rotationSpeed = Random.nextFloat() * 5f - 2.5f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val y = (particle.startY + time * particle.speed) % 1.2f
            if (y > 0f && y < 1f) {
                val x = particle.x + sin(y * PI * 3).toFloat() * 0.1f
                rotate(particle.rotation + time * particle.rotationSpeed * 360f) {
                    drawRect(
                        color = particle.color,
                        topLeft = Offset(x * size.width - particle.size / 2, y * size.height),
                        size = androidx.compose.ui.geometry.Size(particle.size, particle.size * 1.5f)
                    )
                }
            }
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val startY: Float,
    val rotation: Float,
    val size: Float,
    val color: Color,
    val speed: Float,
    val rotationSpeed: Float
)

/**
 * Breathing circle animation for meditation and calm states
 */
@Composable
fun BreathingCircle(
    modifier: Modifier = Modifier,
    color: Color = ProdyPrimary,
    breathDuration: Int = 4000,
    minScale: Float = 0.8f,
    maxScale: Float = 1.2f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(breathDuration, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(breathDuration, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_alpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * Orbiting dots animation
 */
@Composable
fun OrbitingDots(
    modifier: Modifier = Modifier,
    dotCount: Int = 3,
    color: Color = ProdyPrimary,
    orbitRadius: Dp = 40.dp,
    dotSize: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_rotation"
    )

    Canvas(modifier = modifier.size(orbitRadius * 2 + dotSize)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = orbitRadius.toPx()

        repeat(dotCount) { index ->
            val angle = (rotation + (360f / dotCount) * index) * (PI / 180f)
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            drawCircle(
                color = color.copy(alpha = 1f - (index * 0.2f)),
                radius = dotSize.toPx() / 2,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Wave animation effect
 */
@Composable
fun WaveAnimation(
    modifier: Modifier = Modifier,
    color: Color = ProdyPrimary.copy(alpha = 0.3f),
    waveCount: Int = 3
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phases = (0 until waveCount).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 2 * PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(3000 + index * 500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "wave_phase_$index"
        )
    }

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)) {
        phases.forEachIndexed { index, phaseState ->
            val phase = phaseState.value
            val path = Path()
            val amplitude = 20f - index * 5f
            val frequency = 0.02f + index * 0.005f

            path.moveTo(0f, size.height / 2)
            for (x in 0..size.width.toInt() step 5) {
                val y = size.height / 2 + amplitude * sin(x * frequency + phase)
                path.lineTo(x.toFloat(), y.toFloat())
            }

            drawPath(
                path = path,
                color = color.copy(alpha = (0.5f - index * 0.1f).coerceAtLeast(0.1f)),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Shimmer loading effect
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.surface
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(modifier = modifier.background(brush))
}

/**
 * Rotating ring animation
 */
@Composable
fun RotatingRing(
    modifier: Modifier = Modifier,
    color: Color = ProdyPrimary,
    ringWidth: Dp = 4.dp,
    size: Dp = 60.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotating_ring")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    Canvas(modifier = modifier.size(size).rotate(rotation)) {
        drawArc(
            color = color.copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = ringWidth.toPx())
        )
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = ringWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

/**
 * Sparkle animation for highlighting achievements
 */
@Composable
fun SparkleEffect(
    modifier: Modifier = Modifier,
    sparkleCount: Int = 8,
    color: Color = GoldTier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val sparkles = (0 until sparkleCount).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, delayMillis = index * 100, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "sparkle_$index"
        )
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        sparkles.forEachIndexed { index, animState ->
            val angle = (360f / sparkleCount) * index * (PI / 180f)
            val maxRadius = minOf(size.width, size.height) / 2 * 0.8f
            val radius = maxRadius * (0.5f + animState.value * 0.5f)
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            val sparkleSize = 4f + animState.value * 4f

            // Draw 4-point star
            val starPath = Path().apply {
                moveTo(x, y - sparkleSize)
                lineTo(x + sparkleSize * 0.3f, y)
                lineTo(x, y + sparkleSize)
                lineTo(x - sparkleSize * 0.3f, y)
                close()
                moveTo(x - sparkleSize, y)
                lineTo(x, y + sparkleSize * 0.3f)
                lineTo(x + sparkleSize, y)
                lineTo(x, y - sparkleSize * 0.3f)
                close()
            }
            drawPath(
                path = starPath,
                color = color.copy(alpha = animState.value)
            )
        }
    }
}

/**
 * Staggered entrance animation helper
 */
@Composable
fun StaggeredAnimationState(
    itemCount: Int,
    delayPerItem: Int = 100
): List<State<Float>> {
    return (0 until itemCount).map { index ->
        val animatable = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(index * delayPerItem.toLong())
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        animatable.asState()
    }
}

/**
 * Number counter animation that returns a State<Int>.
 * Use [AnimatedCounter] from GamificationComponents.kt for a Text-based counter with more options.
 */
@Composable
fun animatedCounterState(
    count: Int,
    modifier: Modifier = Modifier
): State<Int> {
    val animatedCount = remember { Animatable(0f) }

    LaunchedEffect(count) {
        animatedCount.animateTo(
            targetValue = count.toFloat(),
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    return remember {
        derivedStateOf { animatedCount.value.toInt() }
    }
}

/**
 * Progress ring animation
 */
@Composable
fun AnimatedProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ProdyPrimary,
    backgroundColor: Color = ProdyPrimary.copy(alpha = 0.2f),
    strokeWidth: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress_ring"
    )

    Canvas(modifier = modifier) {
        val sweepAngle = animatedProgress * 360f

        // Background ring
        drawArc(
            color = backgroundColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        // Progress ring
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

/**
 * Buddha Contemplating Animation - A serene, meditative loading indicator
 *
 * This replaces generic loading spinners when the Buddha AI is generating a response.
 * Features:
 * - Concentric breathing circles representing meditation
 * - Subtle glowing orb at center representing enlightenment
 * - Gentle pulsing animation to mask AI latency
 * - Animated text dots that cycle through contemplation states
 *
 * @param modifier Modifier for the container
 * @param primaryColor The main color for the animation (typically gold/amber)
 * @param secondaryTextColor Color for the "Buddha is contemplating" text
 * @param showText Whether to show the "Buddha is contemplating..." text
 */
@Composable
fun BuddhaContemplatingAnimation(
    modifier: Modifier = Modifier,
    primaryColor: Color = GoldTier,
    secondaryTextColor: Color = Color.Gray,
    showText: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "buddha_contemplating")

    // Main breathing animation - slow, meditative pace
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_scale"
    )

    // Inner glow alpha animation
    val innerGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "inner_glow"
    )

    // Outer ring rotation - very slow, contemplative
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_rotation"
    )

    // Animated dots for text
    var dotCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dotCount = (dotCount + 1) % 4
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animation container
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer rotating ring (subtle)
            Canvas(
                modifier = Modifier
                    .size(48.dp)
                    .rotate(outerRotation)
            ) {
                val strokeWidth = 2.dp.toPx()

                // Draw partial arc segments
                for (i in 0 until 4) {
                    val startAngle = i * 90f + 10f
                    val sweepAngle = 70f
                    drawArc(
                        color = primaryColor.copy(alpha = 0.2f + (i * 0.05f)),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }

            // Middle breathing circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .scale(breathScale)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.15f))
            )

            // Inner glowing orb (center of enlightenment)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = innerGlowAlpha),
                                primaryColor.copy(alpha = innerGlowAlpha * 0.5f),
                                primaryColor.copy(alpha = 0f)
                            )
                        )
                    )
            )

            // Tiny center dot
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = innerGlowAlpha))
            )
        }

        // Text below animation
        if (showText) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Text(
                    text = "Buddha is contemplating",
                    fontFamily = PoppinsFamily,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                    fontSize = 13.sp,
                    color = secondaryTextColor
                )

                // Animated dots
                androidx.compose.material3.Text(
                    text = ".".repeat(dotCount),
                    fontFamily = PoppinsFamily,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                    fontSize = 13.sp,
                    color = secondaryTextColor,
                    modifier = Modifier.width(20.dp) // Fixed width to prevent layout shift
                )
            }
        }
    }
}

/**
 * Compact version of BuddhaContemplatingAnimation for inline use
 * Used in buttons, cards, and other tight spaces
 */
@Composable
fun BuddhaContemplatingCompact(
    modifier: Modifier = Modifier,
    primaryColor: Color = GoldTier,
    size: Dp = 20.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "buddha_compact")

    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "compact_breath"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "compact_rotation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Rotating outer arc
        Canvas(
            modifier = Modifier
                .size(size)
                .rotate(rotation)
        ) {
            val strokeWidth = 1.5.dp.toPx()
            drawArc(
                color = primaryColor.copy(alpha = 0.4f),
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Breathing inner dot
        Box(
            modifier = Modifier
                .size(size * 0.4f)
                .scale(breathScale)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.6f))
        )
    }
}
