package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated floating particles background for visual appeal
 */
@Composable
fun FloatingParticlesBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 15,
    particleColor: Color = Color.White.copy(alpha = 0.1f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val particles = remember {
        List(particleCount) {
            Particle(
                x = (Math.random() * 1f).toFloat(),
                y = (Math.random() * 1f).toFloat(),
                radius = (3 + Math.random() * 8).toFloat(),
                speed = (0.5f + Math.random() * 1.5f).toFloat()
            )
        }
    }

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_movement"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val offsetY = ((particle.y + animationProgress * particle.speed) % 1.2f) - 0.1f
            val offsetX = particle.x + sin(offsetY * PI.toFloat() * 2) * 0.05f

            drawCircle(
                color = particleColor,
                radius = particle.radius.dp.toPx(),
                center = Offset(
                    x = offsetX * size.width,
                    y = offsetY * size.height
                )
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float
)

/**
 * Animated gradient orbs for background decoration
 */
@Composable
fun AnimatedGradientOrbs(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(MoodCalm, MoodExcited, MoodMotivated)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    Box(modifier = modifier) {
        colors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotation + index * 120f)
                    .scale(scale)
                    .blur(60.dp)
                    .alpha(0.3f)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .offset(
                            x = (50 * (index + 1)).dp,
                            y = (30 * (index + 1)).dp
                        )
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

/**
 * Animated pattern background with geometric shapes
 */
@Composable
fun GeometricPatternBackground(
    modifier: Modifier = Modifier,
    patternColor: Color = Color.White.copy(alpha = 0.05f),
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pattern")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) 60f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pattern_offset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val spacing = 60.dp.toPx()
        val lineWidth = 1.dp.toPx()

        // Draw diagonal lines
        var x = -size.height + offset
        while (x < size.width + size.height) {
            drawLine(
                color = patternColor,
                start = Offset(x, 0f),
                end = Offset(x + size.height, size.height),
                strokeWidth = lineWidth
            )
            x += spacing
        }

        // Draw opposite diagonal lines
        x = -offset
        while (x < size.width + size.height) {
            drawLine(
                color = patternColor.copy(alpha = patternColor.alpha * 0.5f),
                start = Offset(x, size.height),
                end = Offset(x + size.height, 0f),
                strokeWidth = lineWidth
            )
            x += spacing
        }
    }
}

/**
 * Pulsing glow effect for important elements
 */
@Composable
fun PulsingGlow(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Box(modifier = modifier) {
        // Glow layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(glowScale)
                .blur(20.dp)
                .alpha(glowAlpha)
                .background(color, CircleShape)
        )
        // Content
        Box(content = content)
    }
}

/**
 * Animated wave pattern for headers
 */
@Composable
fun WavePattern(
    modifier: Modifier = Modifier,
    waveColor: Color = Color.White.copy(alpha = 0.1f),
    waveCount: Int = 3
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waves")

    val phases = (0 until waveCount).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 2f * PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + index * 500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "wave_phase_$index"
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        phases.forEachIndexed { index, phase ->
            val amplitude = size.height * 0.02f * (index + 1)
            val wavelength = size.width / (2 + index * 0.5f)

            val path = Path().apply {
                moveTo(0f, size.height / 2)
                var x = 0f
                while (x <= size.width) {
                    val y = size.height / 2 + amplitude * sin(x / wavelength * 2 * PI.toFloat() + phase.value)
                    lineTo(x, y)
                    x += 5f
                }
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                color = waveColor.copy(alpha = waveColor.alpha / (index + 1))
            )
        }
    }
}

/**
 * Animated stat counter with spring animation
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier
): Int {
    var previousValue by remember { mutableIntStateOf(0) }
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "counter"
    )

    LaunchedEffect(targetValue) {
        previousValue = targetValue
    }

    return animatedValue
}

/**
 * Shimmer effect for loading states
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.surface
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        baseColor,
                        highlightColor,
                        baseColor
                    ),
                    start = Offset(
                        x = shimmerProgress * 1000f - 500f,
                        y = shimmerProgress * 1000f - 500f
                    ),
                    end = Offset(
                        x = shimmerProgress * 1000f + 500f,
                        y = shimmerProgress * 1000f + 500f
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
    )
}

/**
 * Animated progress ring
 */
@Composable
fun AnimatedProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    Canvas(modifier = modifier) {
        val stroke = strokeWidth.toPx()
        val radius = (minOf(size.width, size.height) - stroke) / 2

        // Track
        drawCircle(
            color = trackColor,
            radius = radius,
            style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
        )

        // Progress
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = stroke,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            ),
            topLeft = Offset(stroke / 2, stroke / 2),
            size = androidx.compose.ui.geometry.Size(
                size.width - stroke,
                size.height - stroke
            )
        )
    }
}

/**
 * Bouncing dots loading indicator
 */
@Composable
fun BouncingDotsIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotCount: Int = 3,
    dotSize: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) { index ->
            val offsetY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = index * 150,
                        easing = EaseInOutSine
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .offset(y = offsetY.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

/**
 * Confetti celebration effect
 */
@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    colors: List<Color> = listOf(
        GoldTier, MoodExcited, MoodCalm, MoodMotivated, AchievementUnlocked
    )
) {
    if (!isActive) return

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")

    val confettiPieces = remember {
        List(30) {
            ConfettiPiece(
                x = (Math.random()).toFloat(),
                rotation = (Math.random() * 360).toFloat(),
                size = (4 + Math.random() * 8).toFloat(),
                color = colors.random(),
                speed = (0.3f + Math.random() * 0.7f).toFloat()
            )
        }
    }

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_fall"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        confettiPieces.forEach { piece ->
            val y = (piece.speed * animationProgress * 1.5f) % 1.2f - 0.1f
            val x = piece.x + sin(y * PI.toFloat() * 4) * 0.1f
            val rotation = piece.rotation + animationProgress * 360f * piece.speed

            rotate(rotation, Offset(x * size.width, y * size.height)) {
                drawRect(
                    color = piece.color,
                    topLeft = Offset(
                        x * size.width - piece.size / 2,
                        y * size.height - piece.size / 2
                    ),
                    size = androidx.compose.ui.geometry.Size(piece.size, piece.size * 0.6f)
                )
            }
        }
    }
}

private data class ConfettiPiece(
    val x: Float,
    val rotation: Float,
    val size: Float,
    val color: Color,
    val speed: Float
)
