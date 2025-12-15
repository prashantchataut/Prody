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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Premium Visual Effects Library
 *
 * Production-grade animation effects for the Prody app including:
 * - Metallic shine effects for achievements
 * - Premium shimmer loading states
 * - Celebration animations (confetti, particles)
 * - Glass-morphism backgrounds
 * - Animated gradients
 * - Glow and aura effects
 *
 * All effects are optimized for 60fps performance using Compose best practices.
 */

// =============================================================================
// METALLIC SHINE EFFECT
// =============================================================================

/**
 * Premium metallic shine effect that sweeps across a surface.
 * Use for achievement unlocks, rank badges, and premium content.
 *
 * @param modifier Modifier for the effect container
 * @param isActive Whether the shine animation should play
 * @param shineColor The base color of the shine (typically gold/silver)
 * @param duration Duration of one shine sweep in milliseconds
 * @param delay Delay between shine animations
 */
@Composable
fun MetallicShine(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    shineColor: Color = GoldTier,
    duration: Int = 1500,
    delay: Int = 2000
) {
    if (!isActive) return

    val infiniteTransition = rememberInfiniteTransition(label = "metallic_shine")
    val shinePosition by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine_position"
    )

    val shineAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration + delay
                0f at 0 using LinearEasing
                0.7f at duration / 3 using FastOutSlowInEasing
                0.7f at (duration * 2) / 3 using FastOutSlowInEasing
                0f at duration using LinearEasing
                0f at duration + delay
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "shine_alpha"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val shineWidth = size.width * 0.4f
        val shineX = shinePosition * (size.width + shineWidth) - shineWidth

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    shineColor.copy(alpha = shineAlpha * 0.3f),
                    Color.White.copy(alpha = shineAlpha * 0.6f),
                    shineColor.copy(alpha = shineAlpha * 0.3f),
                    Color.Transparent
                ),
                startX = shineX,
                endX = shineX + shineWidth
            ),
            size = size
        )
    }
}

// =============================================================================
// PREMIUM SHIMMER LOADING EFFECT
// =============================================================================

/**
 * Premium shimmer loading effect with customizable gradient and speed.
 * Optimized for smooth 60fps performance.
 *
 * @param modifier Modifier for the shimmer area
 * @param isLoading Whether to show the shimmer effect
 * @param baseColor Base color of the shimmer
 * @param highlightColor Highlight color of the shimmer sweep
 * @param speed Animation speed multiplier (1.0 = normal)
 */
@Composable
fun PremiumShimmer(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.surface,
    speed: Float = 1f
) {
    if (!isLoading) return

    val shimmerColors = listOf(
        baseColor,
        highlightColor.copy(alpha = 0.9f),
        baseColor
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1200 / speed).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(modifier = modifier.background(brush))
}

// =============================================================================
// SKELETON LOADING COMPONENTS
// =============================================================================

/**
 * Skeleton loading placeholder for text content
 */
@Composable
fun SkeletonText(
    modifier: Modifier = Modifier,
    lines: Int = 3,
    lineHeight: Dp = 14.dp,
    lineSpacing: Dp = 8.dp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(lineSpacing)
    ) {
        repeat(lines) { index ->
            val widthFraction = when (index) {
                lines - 1 -> 0.6f // Last line is shorter
                else -> 0.85f + Random.nextFloat() * 0.15f
            }
            PremiumShimmer(
                modifier = Modifier
                    .fillMaxWidth(widthFraction)
                    .height(lineHeight)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

/**
 * Skeleton loading placeholder for cards
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
    cornerRadius: Dp = 16.dp
) {
    PremiumShimmer(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
    )
}

// =============================================================================
// CELEBRATION EFFECTS
// =============================================================================

/**
 * State holder for celebration animations
 */
class CelebrationState {
    var isPlaying by mutableStateOf(false)
        private set

    var celebrationType by mutableStateOf(CelebrationType.CONFETTI)
        private set

    fun trigger(type: CelebrationType = CelebrationType.CONFETTI) {
        celebrationType = type
        isPlaying = true
    }

    fun stop() {
        isPlaying = false
    }
}

enum class CelebrationType {
    CONFETTI,       // Colorful falling confetti
    FIREWORKS,      // Burst pattern
    SPARKLES,       // Twinkling stars
    ACHIEVEMENT     // Gold particles with shine
}

@Composable
fun rememberCelebrationState(): CelebrationState {
    return remember { CelebrationState() }
}

/**
 * Full-screen celebration overlay with multiple effect types
 *
 * @param state CelebrationState controlling the animation
 * @param duration Duration of the celebration in milliseconds
 * @param onComplete Callback when celebration finishes
 */
@Composable
fun CelebrationOverlay(
    state: CelebrationState,
    duration: Long = 3000L,
    onComplete: () -> Unit = {}
) {
    if (!state.isPlaying) return

    LaunchedEffect(state.isPlaying) {
        if (state.isPlaying) {
            delay(duration)
            state.stop()
            onComplete()
        }
    }

    when (state.celebrationType) {
        CelebrationType.CONFETTI -> ConfettiCelebration()
        CelebrationType.FIREWORKS -> FireworksCelebration()
        CelebrationType.SPARKLES -> SparklesCelebration()
        CelebrationType.ACHIEVEMENT -> AchievementCelebration()
    }
}

/**
 * Advanced confetti celebration with physics-based falling particles
 */
@Composable
private fun ConfettiCelebration(
    particleCount: Int = 80,
    colors: List<Color> = listOf(
        MoodHappy, MoodExcited, MoodMotivated, MoodGrateful,
        GoldTier, ProdyPrimary, ProdyTertiary, Color.White
    )
) {
    val particles = remember {
        List(particleCount) {
            ConfettiParticleState(
                x = Random.nextFloat(),
                startY = -Random.nextFloat() * 0.3f,
                rotation = Random.nextFloat() * 360f,
                size = Random.nextFloat() * 10f + 6f,
                color = colors.random(),
                speed = Random.nextFloat() * 0.4f + 0.3f,
                rotationSpeed = Random.nextFloat() * 8f - 4f,
                swayAmplitude = Random.nextFloat() * 0.08f + 0.02f,
                swayFrequency = Random.nextFloat() * 2f + 1f,
                shape = ConfettiShape.entries.random()
            )
        }
    }

    val time by rememberInfiniteTransition(label = "confetti")
        .animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "confetti_time"
        )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val y = (particle.startY + time * particle.speed) % 1.3f
            if (y > -0.1f && y < 1.1f) {
                val sway = sin(y * particle.swayFrequency * PI * 2).toFloat() * particle.swayAmplitude
                val x = (particle.x + sway).coerceIn(0f, 1f)
                val currentRotation = particle.rotation + time * particle.rotationSpeed * 360f

                rotate(currentRotation, pivot = Offset(x * size.width, y * size.height)) {
                    when (particle.shape) {
                        ConfettiShape.RECTANGLE -> drawRect(
                            color = particle.color,
                            topLeft = Offset(x * size.width - particle.size / 2, y * size.height),
                            size = Size(particle.size, particle.size * 1.5f)
                        )
                        ConfettiShape.CIRCLE -> drawCircle(
                            color = particle.color,
                            radius = particle.size / 2,
                            center = Offset(x * size.width, y * size.height)
                        )
                        ConfettiShape.STAR -> drawStar(
                            color = particle.color,
                            center = Offset(x * size.width, y * size.height),
                            radius = particle.size
                        )
                    }
                }
            }
        }
    }
}

private enum class ConfettiShape { RECTANGLE, CIRCLE, STAR }

private data class ConfettiParticleState(
    val x: Float,
    val startY: Float,
    val rotation: Float,
    val size: Float,
    val color: Color,
    val speed: Float,
    val rotationSpeed: Float,
    val swayAmplitude: Float,
    val swayFrequency: Float,
    val shape: ConfettiShape
)

private fun DrawScope.drawStar(
    color: Color,
    center: Offset,
    radius: Float,
    points: Int = 5
) {
    val path = Path()
    val innerRadius = radius * 0.4f

    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = (i * PI / points - PI / 2).toFloat()
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

/**
 * Fireworks burst celebration effect
 */
@Composable
private fun FireworksCelebration() {
    val bursts = remember {
        List(5) {
            FireworkBurst(
                x = Random.nextFloat() * 0.6f + 0.2f,
                y = Random.nextFloat() * 0.4f + 0.2f,
                delay = it * 400,
                color = listOf(GoldTier, MoodExcited, MoodHappy, ProdyPrimary).random()
            )
        }
    }

    bursts.forEach { burst ->
        FireworkBurstAnimation(burst)
    }
}

private data class FireworkBurst(
    val x: Float,
    val y: Float,
    val delay: Int,
    val color: Color
)

@Composable
private fun FireworkBurstAnimation(burst: FireworkBurst) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(burst.delay.toLong())
        isVisible = true
    }

    if (!isVisible) return

    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "firework_progress"
    )

    val alpha by animateFloatAsState(
        targetValue = if (progress > 0.7f) 0f else 1f,
        animationSpec = tween(400),
        label = "firework_alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = burst.x * size.width
        val centerY = burst.y * size.height
        val maxRadius = minOf(size.width, size.height) * 0.2f

        // Draw expanding particles
        repeat(24) { i ->
            val angle = (i * 15f) * (PI / 180f)
            val radius = maxRadius * progress
            val particleX = centerX + radius * cos(angle).toFloat()
            val particleY = centerY + radius * sin(angle).toFloat()
            val particleSize = 8f * (1f - progress * 0.5f)

            drawCircle(
                color = burst.color.copy(alpha = alpha * (1f - progress * 0.3f)),
                radius = particleSize,
                center = Offset(particleX, particleY)
            )
        }

        // Central glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    burst.color.copy(alpha = alpha * 0.5f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = maxRadius * progress * 0.5f
            ),
            radius = maxRadius * progress * 0.5f,
            center = Offset(centerX, centerY)
        )
    }
}

/**
 * Sparkles/twinkling stars celebration
 */
@Composable
private fun SparklesCelebration() {
    val sparkles = remember {
        List(30) {
            SparkleState(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 8f + 4f,
                delay = Random.nextInt(1000),
                color = listOf(Color.White, GoldTier, SilverTier).random()
            )
        }
    }

    sparkles.forEach { sparkle ->
        SparkleAnimation(sparkle)
    }
}

private data class SparkleState(
    val x: Float,
    val y: Float,
    val size: Float,
    val delay: Int,
    val color: Color
)

@Composable
private fun SparkleAnimation(sparkle: SparkleState) {
    var isActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(sparkle.delay.toLong())
        isActive = true
    }

    if (!isActive) return

    val transition = rememberInfiniteTransition(label = "sparkle_${sparkle.x}_${sparkle.y}")
    val scale by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1500
                0f at 0
                1f at 300 using FastOutSlowInEasing
                0.8f at 600
                1f at 900 using FastOutSlowInEasing
                0f at 1500 using FastOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_scale"
    )

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = sparkle.x * size.width
        val centerY = sparkle.y * size.height
        val currentSize = sparkle.size * scale

        rotate(rotation, pivot = Offset(centerX, centerY)) {
            // Draw 4-point star
            val starPath = Path().apply {
                moveTo(centerX, centerY - currentSize)
                lineTo(centerX + currentSize * 0.3f, centerY)
                lineTo(centerX, centerY + currentSize)
                lineTo(centerX - currentSize * 0.3f, centerY)
                close()
                moveTo(centerX - currentSize, centerY)
                lineTo(centerX, centerY + currentSize * 0.3f)
                lineTo(centerX + currentSize, centerY)
                lineTo(centerX, centerY - currentSize * 0.3f)
                close()
            }
            drawPath(starPath, sparkle.color.copy(alpha = scale))
        }
    }
}

/**
 * Achievement unlock celebration with golden particles
 */
@Composable
private fun AchievementCelebration() {
    val particles = remember {
        List(50) {
            AchievementParticle(
                angle = Random.nextFloat() * 360f,
                distance = Random.nextFloat() * 0.3f + 0.1f,
                size = Random.nextFloat() * 6f + 4f,
                speed = Random.nextFloat() * 0.5f + 0.5f,
                color = listOf(
                    GoldTier,
                    GoldTier.copy(alpha = 0.8f),
                    Color(0xFFF4D03F),
                    Color.White
                ).random()
            )
        }
    }

    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "achievement_progress"
    )

    val alpha by animateFloatAsState(
        targetValue = if (progress > 0.6f) 0f else 1f,
        animationSpec = tween(800),
        label = "achievement_alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = minOf(size.width, size.height) * 0.4f

        // Central glow burst
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GoldTier.copy(alpha = alpha * 0.6f),
                    GoldTier.copy(alpha = alpha * 0.3f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = maxRadius * progress
            ),
            radius = maxRadius * progress,
            center = Offset(centerX, centerY)
        )

        // Expanding particles
        particles.forEach { particle ->
            val angle = particle.angle * (PI / 180f)
            val currentDistance = particle.distance * maxRadius * progress * particle.speed
            val x = centerX + currentDistance * cos(angle).toFloat()
            val y = centerY + currentDistance * sin(angle).toFloat()
            val particleAlpha = alpha * (1f - progress * 0.5f)

            drawCircle(
                color = particle.color.copy(alpha = particleAlpha),
                radius = particle.size * (1f - progress * 0.3f),
                center = Offset(x, y)
            )
        }
    }
}

private data class AchievementParticle(
    val angle: Float,
    val distance: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)

// =============================================================================
// GLASS-MORPHISM EFFECT
// =============================================================================

/**
 * Glass-morphism background modifier
 * Creates a frosted glass effect with blur and subtle transparency
 *
 * @param blurRadius Amount of background blur
 * @param tintColor Overlay tint color
 * @param tintAlpha Opacity of the tint overlay
 */
@Composable
fun Modifier.glassMorphism(
    blurRadius: Dp = 20.dp,
    tintColor: Color = Color.White,
    tintAlpha: Float = 0.15f,
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = 16.dp
): Modifier {
    return this
        .clip(RoundedCornerShape(cornerRadius))
        .blur(blurRadius)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    tintColor.copy(alpha = tintAlpha * 1.2f),
                    tintColor.copy(alpha = tintAlpha * 0.8f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}

/**
 * Glass card with premium frosted effect
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.1f),
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = backgroundColor.alpha * 1.3f),
                        backgroundColor
                    )
                )
            )
            .then(
                Modifier.background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            borderColor,
                            Color.Transparent,
                            borderColor.copy(alpha = borderColor.alpha * 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
            ),
        content = content
    )
}

// =============================================================================
// ANIMATED GLOW EFFECTS
// =============================================================================

/**
 * Pulsing glow effect wrapper for any composable
 *
 * @param glowColor Color of the glow
 * @param glowRadius Maximum blur radius
 * @param pulseSpeed Speed of the pulse animation
 */
@Composable
fun AnimatedGlow(
    modifier: Modifier = Modifier,
    glowColor: Color = ProdyPrimary,
    glowRadius: Dp = 16.dp,
    pulseSpeed: Int = 1500,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseSpeed, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseSpeed, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(glowScale)
                .blur(glowRadius)
                .alpha(glowAlpha)
                .background(glowColor, CircleShape)
        )
        content()
    }
}

// =============================================================================
// GRADIENT ANIMATIONS
// =============================================================================

/**
 * Animated gradient that shifts colors smoothly
 */
@Composable
fun AnimatedShiftingGradient(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(ProdyPrimary, ProdyTertiary, ProdyPrimary),
    duration: Int = 5000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shifting_gradient")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_offset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val shiftedColors = colors.mapIndexed { index, color ->
            val shiftedIndex = (index + (offset * colors.size).toInt()) % colors.size
            colors[shiftedIndex]
        }

        drawRect(
            brush = Brush.linearGradient(
                colors = shiftedColors + shiftedColors.first(),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
        )
    }
}

// =============================================================================
// ACHIEVEMENT BADGE SHINE
// =============================================================================

/**
 * Premium badge shine effect specifically designed for achievement badges
 */
@Composable
fun BadgeShine(
    modifier: Modifier = Modifier,
    tier: BadgeTier = BadgeTier.GOLD,
    isAnimated: Boolean = true
) {
    val tierColor = when (tier) {
        BadgeTier.BRONZE -> BronzeTier
        BadgeTier.SILVER -> SilverTier
        BadgeTier.GOLD -> GoldTier
        BadgeTier.PLATINUM -> PlatinumTier
    }

    if (!isAnimated) return

    val infiniteTransition = rememberInfiniteTransition(label = "badge_shine")
    val shineRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine_rotation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) / 2

        // Rotating shine highlight
        rotate(shineRotation, pivot = center) {
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        tierColor.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.6f),
                        tierColor.copy(alpha = 0.4f),
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent
                    ),
                    center = center
                ),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
        }
    }
}

enum class BadgeTier {
    BRONZE, SILVER, GOLD, PLATINUM
}

// =============================================================================
// TYPING INDICATOR
// =============================================================================

/**
 * Animated typing indicator with bouncing dots
 */
@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 8.dp,
    spacing: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    val dot1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 150, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 300, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(dot1Offset, dot2Offset, dot3Offset).forEach { offset ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .offset(y = (offset * 4).dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}
