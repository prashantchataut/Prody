package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

/**
 * ChampionBanners - Premium animated backgrounds for top 3 leaderboard positions
 *
 * These are high-retention visual effects designed to create a "must-have" feeling:
 * - #1 Champion: Starfield effect with twinkling stars and subtle nebula
 * - #2 Runner-up: Liquid/Mercury flowing effect with metallic sheen
 * - #3 Bronze: Aurora Borealis effect with flowing color waves
 *
 * All animations use Compose animation APIs for 60fps performance.
 * No setInterval - all timing handled by infiniteRepeatable animations.
 */

// =============================================================================
// CHAMPION (#1) - STARFIELD BANNER
// =============================================================================

/**
 * Champion's starfield background with twinkling stars and nebula effect.
 * Creates a cosmic "champion of the universe" feeling.
 */
@Composable
fun ChampionStarfieldBanner(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "starfield")

    // Global time for star twinkling (0 to 2PI cycle)
    val twinklePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle_phase"
    )

    // Slow nebula drift
    val nebulaDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebula_drift"
    )

    // Shooting star timing (periodic)
    val shootingStarPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shooting_star"
    )

    // Gold glow pulse for champion prestige
    val goldPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gold_pulse"
    )

    // Remember star positions (generated once)
    val stars = remember {
        List(30) {
            StarData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 1f,
                twinkleOffset = Random.nextFloat() * 2f * PI.toFloat(),
                brightness = Random.nextFloat() * 0.5f + 0.5f
            )
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
    ) {
        val width = size.width
        val height = size.height

        // Deep space background with gold accent
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A0A14), // Deep space black
                    Color(0xFF1A1428), // Dark purple hint
                    Color(0xFF0D0D0D)  // Pure black
                )
            )
        )

        // Nebula clouds with gold tint (drift animation)
        drawNebulaCloud(
            centerX = width * (0.3f + nebulaDrift * 0.1f),
            centerY = height * 0.4f,
            radius = width * 0.4f,
            color = LeaderboardGold.copy(alpha = goldPulse * 0.15f)
        )

        drawNebulaCloud(
            centerX = width * (0.7f - nebulaDrift * 0.1f),
            centerY = height * 0.6f,
            radius = width * 0.3f,
            color = Color(0xFFB8860B).copy(alpha = goldPulse * 0.1f) // Dark goldenrod
        )

        // Draw twinkling stars
        stars.forEach { star ->
            val twinkle = (sin(twinklePhase + star.twinkleOffset) + 1f) / 2f
            val currentBrightness = star.brightness * (0.5f + twinkle * 0.5f)

            // Star glow
            drawCircle(
                color = Color.White.copy(alpha = currentBrightness * 0.3f),
                radius = star.size * 3f,
                center = Offset(star.x * width, star.y * height)
            )

            // Star core
            drawCircle(
                color = Color.White.copy(alpha = currentBrightness),
                radius = star.size,
                center = Offset(star.x * width, star.y * height)
            )
        }

        // Occasional shooting star
        if (shootingStarPhase in 0.4f..0.6f) {
            val progress = (shootingStarPhase - 0.4f) / 0.2f
            val startX = width * 0.8f
            val startY = height * 0.1f
            val endX = width * 0.2f
            val endY = height * 0.8f

            val currentX = startX + (endX - startX) * progress
            val currentY = startY + (endY - startY) * progress

            // Shooting star trail
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        LeaderboardGold.copy(alpha = 0.6f),
                        Color.White
                    ),
                    start = Offset(currentX + 40, currentY - 40),
                    end = Offset(currentX, currentY)
                ),
                start = Offset(currentX + 40, currentY - 40),
                end = Offset(currentX, currentY),
                strokeWidth = 2f
            )

            // Star head
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = Offset(currentX, currentY)
            )
        }

        // Gold edge glow for champion prestige
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    LeaderboardGold.copy(alpha = goldPulse * 0.4f),
                    Color.Transparent,
                    Color.Transparent,
                    LeaderboardGold.copy(alpha = goldPulse * 0.4f)
                )
            )
        )
    }
}

private data class StarData(
    val x: Float,
    val y: Float,
    val size: Float,
    val twinkleOffset: Float,
    val brightness: Float
)

private fun DrawScope.drawNebulaCloud(
    centerX: Float,
    centerY: Float,
    radius: Float,
    color: Color
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color,
                color.copy(alpha = color.alpha * 0.5f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius
        ),
        radius = radius,
        center = Offset(centerX, centerY)
    )
}

// =============================================================================
// RUNNER-UP (#2) - LIQUID MERCURY BANNER
// =============================================================================

/**
 * Runner-up's liquid mercury effect with metallic sheen.
 * Creates a premium "liquid metal" feeling.
 */
@Composable
fun RunnerUpLiquidBanner(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid")

    // Wave phases for liquid effect
    val wave1Phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    val wave2Phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    val wave3Phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave3"
    )

    // Metallic shimmer sweep
    val shimmerPosition by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // Bubble positions
    val bubbles = remember {
        List(8) {
            BubbleData(
                x = Random.nextFloat(),
                startY = Random.nextFloat() * 0.3f + 0.7f,
                size = Random.nextFloat() * 6f + 4f,
                speed = Random.nextFloat() * 0.5f + 0.5f,
                phaseOffset = Random.nextFloat() * 2f * PI.toFloat()
            )
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
    ) {
        val width = size.width
        val height = size.height

        // Silver/mercury base gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF2C3E50), // Dark steel
                    Color(0xFF4A5568), // Medium gray
                    Color(0xFF2D3748)  // Dark blue-gray
                )
            )
        )

        // Liquid mercury waves
        drawLiquidWave(
            width = width,
            height = height,
            phase = wave1Phase,
            baseY = 0.6f,
            amplitude = 0.08f,
            color = LeaderboardSilver.copy(alpha = 0.4f)
        )

        drawLiquidWave(
            width = width,
            height = height,
            phase = wave2Phase,
            baseY = 0.5f,
            amplitude = 0.06f,
            color = LeaderboardSilverLight.copy(alpha = 0.3f)
        )

        drawLiquidWave(
            width = width,
            height = height,
            phase = wave3Phase,
            baseY = 0.7f,
            amplitude = 0.1f,
            color = LeaderboardSilverDark.copy(alpha = 0.35f)
        )

        // Rising bubbles
        bubbles.forEach { bubble ->
            val bubbleY = (bubble.startY - (wave1Phase / (2f * PI.toFloat())) * bubble.speed) % 1.2f
            if (bubbleY > 0f) {
                val wobble = sin(wave1Phase * 2 + bubble.phaseOffset) * 10f

                // Bubble with metallic sheen
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            LeaderboardSilver.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    radius = bubble.size,
                    center = Offset(bubble.x * width + wobble, bubbleY * height)
                )
            }
        }

        // Metallic shimmer sweep
        val shimmerWidth = width * 0.4f
        val shimmerX = shimmerPosition * (width + shimmerWidth) - shimmerWidth / 2

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.15f),
                    Color.White.copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                startX = shimmerX - shimmerWidth / 2,
                endX = shimmerX + shimmerWidth / 2
            )
        )

        // Silver edge highlight
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    LeaderboardSilverLight.copy(alpha = 0.3f),
                    Color.Transparent,
                    Color.Transparent,
                    LeaderboardSilver.copy(alpha = 0.2f)
                )
            )
        )
    }
}

private data class BubbleData(
    val x: Float,
    val startY: Float,
    val size: Float,
    val speed: Float,
    val phaseOffset: Float
)

private fun DrawScope.drawLiquidWave(
    width: Float,
    height: Float,
    phase: Float,
    baseY: Float,
    amplitude: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(0f, height)

        var x = 0f
        while (x <= width) {
            val y = height * baseY +
                sin((x / width * 3 * PI + phase).toFloat()) * height * amplitude +
                sin((x / width * 5 * PI + phase * 1.3f).toFloat()) * height * amplitude * 0.5f
            lineTo(x, y)
            x += 2f
        }

        lineTo(width, height)
        close()
    }

    drawPath(
        path = path,
        color = color
    )
}

// =============================================================================
// BRONZE (#3) - AURORA BOREALIS BANNER
// =============================================================================

/**
 * Bronze position's aurora borealis effect with flowing colors.
 * Creates a natural "northern lights" feeling.
 */
@Composable
fun BronzeAuroraBanner(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")

    // Aurora wave phases
    val auroraPhase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aurora1"
    )

    val auroraPhase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aurora2"
    )

    val auroraPhase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aurora3"
    )

    // Vertical shimmer for aurora columns
    val verticalShimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vertical_shimmer"
    )

    // Bronze glow intensity
    val bronzeGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bronze_glow"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
    ) {
        val width = size.width
        val height = size.height

        // Dark night sky background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0D1117), // GitHub dark
                    Color(0xFF161B22), // Slightly lighter
                    Color(0xFF0D1117)
                )
            )
        )

        // Aurora curtain 1 - Bronze/copper tones
        drawAuroraCurtain(
            width = width,
            height = height,
            phase = auroraPhase1,
            baseX = 0.2f,
            colors = listOf(
                LeaderboardBronze.copy(alpha = 0.4f * bronzeGlow),
                LeaderboardBronzeLight.copy(alpha = 0.3f * bronzeGlow),
                Color(0xFFDAA06D).copy(alpha = 0.2f * bronzeGlow) // Champagne bronze
            ),
            verticalOffset = verticalShimmer
        )

        // Aurora curtain 2 - Green/teal accent
        drawAuroraCurtain(
            width = width,
            height = height,
            phase = auroraPhase2,
            baseX = 0.5f,
            colors = listOf(
                Color(0xFF50C878).copy(alpha = 0.25f), // Emerald
                ProdyAccentGreen.copy(alpha = 0.2f),
                Color(0xFF2E8B57).copy(alpha = 0.15f) // Sea green
            ),
            verticalOffset = 1f - verticalShimmer
        )

        // Aurora curtain 3 - Warm bronze
        drawAuroraCurtain(
            width = width,
            height = height,
            phase = auroraPhase3,
            baseX = 0.8f,
            colors = listOf(
                LeaderboardBronzeDark.copy(alpha = 0.35f * bronzeGlow),
                Color(0xFFB8860B).copy(alpha = 0.25f * bronzeGlow), // Dark goldenrod
                LeaderboardBronze.copy(alpha = 0.2f * bronzeGlow)
            ),
            verticalOffset = verticalShimmer * 0.5f
        )

        // Star field background (subtle) - use normalized coordinates
        val auroraStars = listOf(
            Offset(0.1f, 0.15f), Offset(0.25f, 0.08f), Offset(0.4f, 0.22f),
            Offset(0.55f, 0.12f), Offset(0.7f, 0.28f), Offset(0.85f, 0.18f),
            Offset(0.15f, 0.35f), Offset(0.3f, 0.42f), Offset(0.5f, 0.38f),
            Offset(0.65f, 0.45f), Offset(0.8f, 0.32f), Offset(0.9f, 0.4f),
            Offset(0.2f, 0.48f), Offset(0.45f, 0.05f), Offset(0.75f, 0.1f)
        )
        auroraStars.forEach { normalizedStar ->
            val star = Offset(normalizedStar.x * width, normalizedStar.y * height)
            val twinkle = (sin(auroraPhase1 + star.x / 100) + 1f) / 2f
            drawCircle(
                color = Color.White.copy(alpha = 0.3f + twinkle * 0.3f),
                radius = 1.5f,
                center = star
            )
        }

        // Bronze edge glow
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    LeaderboardBronze.copy(alpha = bronzeGlow * 0.3f),
                    Color.Transparent,
                    Color.Transparent,
                    LeaderboardBronze.copy(alpha = bronzeGlow * 0.3f)
                )
            )
        )
    }
}

private fun DrawScope.drawAuroraCurtain(
    width: Float,
    height: Float,
    phase: Float,
    baseX: Float,
    colors: List<Color>,
    verticalOffset: Float
) {
    val curtainWidth = width * 0.4f
    val centerX = width * baseX

    // Draw multiple vertical strips with wave distortion
    val stripCount = 8
    for (i in 0 until stripCount) {
        val stripOffset = (i - stripCount / 2) * (curtainWidth / stripCount)
        val waveX = sin(phase + i * 0.5f) * 15f
        val x = centerX + stripOffset + waveX

        // Vertical gradient strip with aurora colors
        val stripHeight = height * (0.5f + verticalOffset * 0.3f + sin(phase + i) * 0.1f)

        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    colors[i % colors.size],
                    colors[(i + 1) % colors.size],
                    Color.Transparent
                ),
                startY = 0f,
                endY = stripHeight
            ),
            start = Offset(x, 0f),
            end = Offset(x + sin(phase * 2 + i) * 5f, stripHeight),
            strokeWidth = (curtainWidth / stripCount) * 1.5f,
            cap = StrokeCap.Round
        )
    }
}

// =============================================================================
// UNIFIED LEADERBOARD BANNER SELECTOR
// =============================================================================

/**
 * Selects and renders the appropriate animated banner based on rank.
 *
 * @param rank The leaderboard position (1, 2, or 3)
 * @param modifier Modifier for the banner
 */
@Composable
fun LeaderboardChampionBanner(
    rank: Int,
    modifier: Modifier = Modifier
) {
    when (rank) {
        1 -> ChampionStarfieldBanner(modifier = modifier)
        2 -> RunnerUpLiquidBanner(modifier = modifier)
        3 -> BronzeAuroraBanner(modifier = modifier)
        else -> { /* No special banner for ranks > 3 */ }
    }
}

// =============================================================================
// YOUR CARD PULSE EFFECT
// =============================================================================

/**
 * Animated pulse border for the "YOU" card at the bottom of leaderboard.
 * Creates a distinct visual separation showing exactly where the user stands.
 */
@Composable
fun YourCardPulseBorder(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "your_card")

    // Pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Border glow alpha
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Rotating gradient angle
    val gradientAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_angle"
    )

    Box(
        modifier = modifier
    ) {
        // Animated border background
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(16.dp))
        ) {
            val borderWidth = 3.dp.toPx()

            // Calculate gradient points based on angle
            val angleRad = gradientAngle * PI.toFloat() / 180f
            val gradientLength = maxOf(size.width, size.height)
            val centerX = size.width / 2
            val centerY = size.height / 2

            val startX = centerX - cos(angleRad) * gradientLength / 2
            val startY = centerY - sin(angleRad) * gradientLength / 2
            val endX = centerX + cos(angleRad) * gradientLength / 2
            val endY = centerY + sin(angleRad) * gradientLength / 2

            // Draw glowing border
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        ProdyAccentGreen.copy(alpha = glowAlpha),
                        ProdyAccentGreenLight.copy(alpha = glowAlpha * 0.7f),
                        ProdyAccentGreen.copy(alpha = glowAlpha)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY)
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = borderWidth)
            )
        }

        // Content
        Box(
            modifier = Modifier.padding(3.dp)
        ) {
            content()
        }
    }
}
