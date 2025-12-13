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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Floating particles animation effect for backgrounds
 */
@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 20,
    particleColor: Color = Color.White.copy(alpha = 0.3f),
    minSize: Dp = 4.dp,
    maxSize: Dp = 12.dp
) {
    val particles = remember(particleCount, minSize, maxSize) {
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

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(scale)
                .alpha(alpha)
                .blur(20.dp)
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

    val particles = remember(particleCount, colors) {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                startY = -Random.nextFloat() * 0.2f,
                rotation = Random.nextFloat() * 360f,
                size = Random.nextFloat() * 8f + 4f,
                color = colors.random(),
                speed = Random.nextFloat() * 0.3f + 0.5f,
                rotationSpeed = Random.nextFloat() * 5f - 2.5f
            )
        }
    }

    val time by rememberInfiniteTransition(label = "confetti").animateFloat(
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
fun <T> StaggeredAnimationState(
    items: List<T>,
    delayPerItem: Int = 100
): List<State<Float>> {
    return items.mapIndexed { index, item ->
        val animatable = remember(item) { Animatable(0f) }
        LaunchedEffect(item) {
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
 * Number counter animation
 */
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<Float> = tween(1000, easing = FastOutSlowInEasing)
): State<Int> {
    val animatedCount = remember { Animatable(0f) }

    LaunchedEffect(count) {
        animatedCount.animateTo(
            targetValue = count.toFloat(),
            animationSpec = animationSpec
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
    strokeWidth: Dp = 8.dp,
    animationSpec: AnimationSpec<Float> = tween(1000, easing = FastOutSlowInEasing)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = animationSpec,
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
