package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Gamification Components - Premium UI elements for progress & achievements
 *
 * Production-grade components including:
 * - AnimatedCounter with counting animation
 * - Premium XP Bar with gradient, glow, and particle effects
 * - Level Progress Ring
 * - Rank Badge with metallic effects
 * - Point Burst animation
 *
 * All components optimized for 60fps performance
 */

// =============================================================================
// ANIMATED COUNTER
// =============================================================================

/**
 * Animated counter that counts up from 0 (or previous value) to target value
 *
 * @param targetValue The value to count up to
 * @param duration Animation duration in milliseconds
 * @param prefix Text before the number (e.g., "+")
 * @param suffix Text after the number (e.g., " XP", " pts")
 * @param style Text style for the counter
 * @param color Text color
 * @param formatter Optional custom number formatter
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    duration: Int = 1500,
    delay: Int = 0,
    prefix: String = "",
    suffix: String = "",
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Bold,
    formatter: ((Int) -> String)? = null
) {
    var previousValue by remember { mutableIntStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(targetValue) {
        if (delay > 0) {
            kotlinx.coroutines.delay(delay.toLong())
        }
        isAnimating = true
    }

    val animatedValue by animateIntAsState(
        targetValue = if (isAnimating) targetValue else previousValue,
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        ),
        label = "counter_animation"
    )

    LaunchedEffect(animatedValue) {
        if (animatedValue == targetValue) {
            previousValue = targetValue
        }
    }

    val displayText = formatter?.invoke(animatedValue)
        ?: formatNumberWithCommas(animatedValue)

    Text(
        text = "$prefix$displayText$suffix",
        style = style,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}

/**
 * Animated counter for floating point values
 */
@Composable
fun AnimatedFloatCounter(
    targetValue: Float,
    modifier: Modifier = Modifier,
    duration: Int = 1500,
    decimalPlaces: Int = 1,
    prefix: String = "",
    suffix: String = "",
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Bold
) {
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(targetValue) {
        isAnimating = true
    }

    val animatedValue by animateFloatAsState(
        targetValue = if (isAnimating) targetValue else 0f,
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        ),
        label = "float_counter_animation"
    )

    val formatString = "%.${decimalPlaces}f"
    Text(
        text = "$prefix${String.format(formatString, animatedValue)}$suffix",
        style = style,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}

// =============================================================================
// PREMIUM XP BAR
// =============================================================================

/**
 * Premium XP progress bar with gradient fill, glow effect, and optional particles
 *
 * @param currentXP Current XP value
 * @param maxXP Maximum XP for current level
 * @param level Current level number
 * @param modifier Modifier for the bar container
 * @param height Bar height
 * @param showParticles Whether to show animated particles on the bar
 * @param showLevel Whether to display level badge
 * @param showLabels Whether to show XP labels
 */
@Composable
fun PremiumXPBar(
    currentXP: Int,
    maxXP: Int,
    modifier: Modifier = Modifier,
    level: Int = 1,
    height: Dp = 16.dp,
    showParticles: Boolean = true,
    showLevel: Boolean = true,
    showLabels: Boolean = true,
    showGlow: Boolean = true,
    animateOnEntry: Boolean = true,
    primaryColor: Color = ProdyPrimary,
    secondaryColor: Color = GoldTier
) {
    val progress = (currentXP.toFloat() / maxXP).coerceIn(0f, 1f)

    var isAnimated by remember { mutableStateOf(!animateOnEntry) }
    LaunchedEffect(Unit) {
        if (animateOnEntry) {
            delay(300)
            isAnimated = true
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isAnimated) progress else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "xp_progress"
    )

    // Glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "xp_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Shimmer position for filled portion
    val shimmerPosition by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Column(modifier = modifier) {
        if (showLabels) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = secondaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    AnimatedCounter(
                        targetValue = currentXP,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        suffix = " XP"
                    )
                }
                Text(
                    text = "${maxXP - currentXP} XP to level ${level + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            contentAlignment = Alignment.CenterStart
        ) {
            // Glow effect behind bar
            if (showGlow && animatedProgress > 0.05f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(height + 8.dp)
                        .blur(8.dp)
                        .alpha(glowAlpha * animatedProgress)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(primaryColor, secondaryColor)
                            ),
                            RoundedCornerShape(height / 2)
                        )
                )
            }

            // Background track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(height / 2))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            // Filled progress with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(height)
                    .clip(RoundedCornerShape(height / 2))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                primaryColor,
                                primaryColor.copy(alpha = 0.9f),
                                secondaryColor
                            )
                        )
                    )
            )

            // Shimmer overlay on filled portion
            if (animatedProgress > 0.1f) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(height)
                        .clip(RoundedCornerShape(height / 2))
                ) {
                    val shimmerWidth = size.width * 0.3f
                    val shimmerX = shimmerPosition * (size.width + shimmerWidth) - shimmerWidth

                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            startX = shimmerX,
                            endX = shimmerX + shimmerWidth
                        )
                    )
                }
            }

            // Particles on the bar
            if (showParticles && animatedProgress > 0.2f) {
                XPBarParticles(
                    progress = animatedProgress,
                    barHeight = height,
                    color = Color.White
                )
            }

            // Level badge at start
            if (showLevel) {
                Box(
                    modifier = Modifier
                        .size(height + 8.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(secondaryColor, secondaryColor.copy(alpha = 0.8f))
                            )
                        )
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun XPBarParticles(
    progress: Float,
    barHeight: Dp,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    // Create 5 particles
    val particles = remember {
        List(5) {
            ParticleState(
                xOffset = 0.1f + it * 0.2f,
                size = 3f + it % 3,
                speed = 1000 + it * 200
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(progress)
            .height(barHeight)
    ) {
        particles.forEach { particle ->
            if (particle.xOffset <= progress) {
                val yOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(particle.speed, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "particle_y_${particle.xOffset}"
                )

                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 0.8f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(particle.speed / 2, easing = EaseInOutCubic),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "particle_alpha_${particle.xOffset}"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val x = particle.xOffset * size.width / progress
                    val y = (yOffset * 0.6f + 0.2f) * size.height
                    drawCircle(
                        color = color.copy(alpha = alpha),
                        radius = particle.size,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

private data class ParticleState(
    val xOffset: Float,
    val size: Float,
    val speed: Int
)

// =============================================================================
// CIRCULAR LEVEL PROGRESS
// =============================================================================

/**
 * Circular progress ring showing level progress
 */
@Composable
fun LevelProgressRing(
    currentXP: Int,
    maxXP: Int,
    level: Int,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    primaryColor: Color = ProdyPrimary,
    secondaryColor: Color = GoldTier,
    showGlow: Boolean = true
) {
    val progress = (currentXP.toFloat() / maxXP).coerceIn(0f, 1f)

    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        isAnimated = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isAnimated) progress else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "ring_progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "ring_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Glow behind ring
        if (showGlow) {
            Box(
                modifier = Modifier
                    .size(size + 16.dp)
                    .blur(12.dp)
                    .alpha(glowAlpha * animatedProgress)
                    .background(primaryColor, CircleShape)
            )
        }

        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val radius = (this.size.minDimension - stroke) / 2

            // Background ring
            drawCircle(
                color = primaryColor.copy(alpha = 0.15f),
                radius = radius,
                style = Stroke(width = stroke)
            )

            // Progress arc with gradient effect simulated
            val sweepAngle = animatedProgress * 360f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        primaryColor,
                        secondaryColor,
                        primaryColor
                    )
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(this.size.width - stroke, this.size.height - stroke)
            )
        }

        // Level number in center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = level.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
            Text(
                text = "LEVEL",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
        }
    }
}

// =============================================================================
// RANK BADGE WITH METALLIC EFFECT
// =============================================================================

/**
 * Premium rank badge with metallic gradient effect
 */
@Composable
fun RankBadge(
    rank: Int,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showShine: Boolean = true
) {
    val (tierColor, tierName) = when {
        rank <= 3 -> GoldTier to "GOLD"
        rank <= 10 -> SilverTier to "SILVER"
        rank <= 50 -> BronzeTier to "BRONZE"
        else -> Color.Gray to "IRON"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "badge_shine")
    val shineRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Glow
        Box(
            modifier = Modifier
                .size(size + 8.dp)
                .blur(10.dp)
                .alpha(0.4f)
                .background(tierColor, CircleShape)
        )

        // Badge background with metallic gradient
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val radius = this.size.minDimension / 2

            // Create metallic effect with layered gradients
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        tierColor.copy(alpha = 1f),
                        tierColor.copy(alpha = 0.7f),
                        tierColor.copy(alpha = 0.9f),
                        tierColor.copy(alpha = 0.6f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(this.size.width, this.size.height)
                ),
                radius = radius
            )

            // Shine overlay
            if (showShine) {
                val shineAngle = shineRotation * PI / 180
                val shineOffset = radius * 0.3f
                val shineX = center.x + shineOffset * cos(shineAngle).toFloat()
                val shineY = center.y + shineOffset * sin(shineAngle).toFloat()

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(shineX, shineY),
                        radius = radius * 0.5f
                    ),
                    radius = radius
                )
            }

            // Inner border
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = radius - 2.dp.toPx(),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Rank number
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// =============================================================================
// POINT BURST ANIMATION
// =============================================================================

/**
 * Animated point burst effect for showing point gains
 */
@Composable
fun PointBurst(
    points: Int,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    onAnimationComplete: () -> Unit = {}
) {
    if (!isVisible) return

    var animationPhase by remember { mutableIntStateOf(0) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            animationPhase = 1
            delay(1500)
            animationPhase = 2
            delay(500)
            onAnimationComplete()
        }
    }

    val scale by animateFloatAsState(
        targetValue = when (animationPhase) {
            1 -> 1.2f
            2 -> 0f
            else -> 0f
        },
        animationSpec = when (animationPhase) {
            1 -> spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            else -> tween(300)
        },
        label = "burst_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = when (animationPhase) {
            1 -> 1f
            2 -> 0f
            else -> 0f
        },
        animationSpec = tween(300),
        label = "burst_alpha"
    )

    val yOffset by animateFloatAsState(
        targetValue = if (animationPhase >= 1) -30f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "burst_y"
    )

    Box(
        modifier = modifier
            .offset(y = yOffset.dp)
            .scale(scale)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        // Glow
        Box(
            modifier = Modifier
                .size(60.dp)
                .blur(15.dp)
                .background(GoldTier, CircleShape)
        )

        // Points text
        Surface(
            color = GoldTier,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Stars,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "+$points",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// =============================================================================
// STREAK COUNTER
// =============================================================================

/**
 * Animated streak counter with fire effect
 */
@Composable
fun StreakCounter(
    streak: Int,
    modifier: Modifier = Modifier,
    showFlame: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak")

    val flameScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showFlame && streak > 0) {
            Box(contentAlignment = Alignment.Center) {
                // Fire glow
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .scale(flameScale)
                        .blur(12.dp)
                        .alpha(glowAlpha)
                        .background(StreakFire, CircleShape)
                )

                // Flame icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .scale(flameScale)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(StreakGlow, StreakFire)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Streak fire",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Column {
            AnimatedCounter(
                targetValue = streak,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (streak > 0) StreakFire else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "day streak",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =============================================================================
// HELPER FUNCTIONS
// =============================================================================

private fun formatNumberWithCommas(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}
