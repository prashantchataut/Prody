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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random

/**
 * BannerCanvas - Premium Algorithmic Pattern System
 *
 * Implements 5 unique, visually stunning banner patterns:
 * 1. NEBULA: Dark gradient with random star points and cosmic dust
 * 2. ZEN: Sand color with concentric circles and peaceful ripples
 * 3. FOCUS: Geometric triangles in high contrast for productivity
 * 4. FLOW: Sine wave gradients creating movement and energy
 * 5. NIGHT: Deep blue/black with glowing edge effects
 *
 * Each pattern is algorithmically generated using Canvas drawing,
 * providing unique, premium visuals without requiring image assets.
 */

/**
 * Pattern types available for BannerCanvas
 */
enum class BannerPatternType {
    NEBULA,
    ZEN,
    FOCUS,
    FLOW,
    NIGHT
}

/**
 * Main BannerCanvas composable that renders algorithmic patterns.
 *
 * @param patternType The type of pattern to render
 * @param modifier Modifier for sizing and positioning
 * @param showAnimation Whether to animate the pattern
 * @param cornerRadius Corner radius for the banner shape
 * @param seed Random seed for deterministic star/point placement
 */
@Composable
fun BannerCanvas(
    patternType: BannerPatternType,
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true,
    cornerRadius: Dp = 12.dp,
    seed: Int = 42
) {
    val infiniteTransition = rememberInfiniteTransition(label = "banner_canvas_animation")

    // Animation values
    val animationPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val pulseValue by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val phase = if (showAnimation) animationPhase else 0f
    val pulse = if (showAnimation) pulseValue else 0.85f
    val glow = if (showAnimation) glowPulse else 0.5f

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (patternType) {
                BannerPatternType.NEBULA -> drawNebulaPattern(phase, pulse, seed)
                BannerPatternType.ZEN -> drawZenPattern(phase, pulse)
                BannerPatternType.FOCUS -> drawFocusPattern(phase)
                BannerPatternType.FLOW -> drawFlowPattern(phase)
                BannerPatternType.NIGHT -> drawNightPattern(glow, seed)
            }
        }
    }
}

/**
 * NEBULA PATTERN
 * Dark gradient with random star points and cosmic dust clouds.
 * Creates a sense of infinite space and wonder.
 */
private fun DrawScope.drawNebulaPattern(phase: Float, pulse: Float, seed: Int) {
    val width = size.width
    val height = size.height

    // Deep space background gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D0D1A), // Very dark blue-black
            Color(0xFF1A1A2E), // Dark navy
            Color(0xFF16213E), // Slightly lighter navy
            Color(0xFF0F3460)  // Deep blue
        )
    )
    drawRect(brush = backgroundBrush)

    // Cosmic dust/nebula clouds
    val random = Random(seed)
    val nebulaColors = listOf(
        Color(0xFF6B5CE7).copy(alpha = 0.15f), // Purple
        Color(0xFFE91E63).copy(alpha = 0.1f),  // Pink
        Color(0xFF00BCD4).copy(alpha = 0.12f)  // Cyan
    )

    repeat(3) { cloudIndex ->
        val cloudCenterX = width * (0.2f + cloudIndex * 0.3f) + sin(phase + cloudIndex) * 20f
        val cloudCenterY = height * (0.3f + random.nextFloat() * 0.4f)
        val cloudRadius = width * (0.3f + random.nextFloat() * 0.2f) * pulse

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    nebulaColors[cloudIndex % nebulaColors.size],
                    Color.Transparent
                ),
                center = Offset(cloudCenterX, cloudCenterY),
                radius = cloudRadius
            ),
            center = Offset(cloudCenterX, cloudCenterY),
            radius = cloudRadius
        )
    }

    // Stars - different sizes and brightness
    val starCount = 60
    repeat(starCount) { i ->
        val starRandom = Random(seed + i)
        val x = starRandom.nextFloat() * width
        val y = starRandom.nextFloat() * height
        val baseSize = 0.5f + starRandom.nextFloat() * 2f
        val twinkle = 0.5f + 0.5f * sin(phase * 2 + i * 0.3f)
        val starSize = baseSize * (0.7f + twinkle * 0.3f)
        val starAlpha = 0.4f + twinkle * 0.6f

        // Star glow
        if (starSize > 1f) {
            drawCircle(
                color = Color.White.copy(alpha = starAlpha * 0.3f),
                radius = starSize * 3f,
                center = Offset(x, y)
            )
        }

        // Star core
        drawCircle(
            color = Color.White.copy(alpha = starAlpha),
            radius = starSize,
            center = Offset(x, y)
        )
    }

    // Bright stars with cross flare
    repeat(5) { i ->
        val brightRandom = Random(seed + 1000 + i)
        val x = brightRandom.nextFloat() * width
        val y = brightRandom.nextFloat() * height
        val flareSize = 8f + brightRandom.nextFloat() * 8f
        val flareAlpha = 0.3f + 0.4f * sin(phase * 1.5f + i)

        // Horizontal flare
        drawLine(
            color = Color.White.copy(alpha = flareAlpha),
            start = Offset(x - flareSize, y),
            end = Offset(x + flareSize, y),
            strokeWidth = 1f
        )
        // Vertical flare
        drawLine(
            color = Color.White.copy(alpha = flareAlpha),
            start = Offset(x, y - flareSize),
            end = Offset(x, y + flareSize),
            strokeWidth = 1f
        )
        // Core
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = 2f,
            center = Offset(x, y)
        )
    }
}

/**
 * ZEN PATTERN
 * Sand color with concentric circles creating peaceful ripples.
 * Inspired by zen gardens and meditation.
 */
private fun DrawScope.drawZenPattern(phase: Float, pulse: Float) {
    val width = size.width
    val height = size.height
    val centerX = width / 2
    val centerY = height / 2

    // Warm sand background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF5E6D3), // Light sand
            Color(0xFFE8D4B8), // Medium sand
            Color(0xFFDBC4A0)  // Darker sand
        )
    )
    drawRect(brush = backgroundBrush)

    // Subtle texture gradient overlay
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFAF0).copy(alpha = 0.3f),
                Color.Transparent
            ),
            center = Offset(width * 0.3f, height * 0.4f),
            radius = width * 0.5f
        )
    )

    // Concentric ripple circles
    val circleCount = 8
    val maxRadius = maxOf(width, height) * 0.6f
    val strokeColor = Color(0xFF8B7355).copy(alpha = 0.4f) // Earth brown

    repeat(circleCount) { i ->
        val baseRadius = maxRadius * (i + 1) / circleCount
        val animatedRadius = baseRadius + sin(phase + i * 0.5f) * 5f
        val strokeAlpha = 0.15f + (1f - i.toFloat() / circleCount) * 0.25f

        drawCircle(
            color = strokeColor.copy(alpha = strokeAlpha),
            radius = animatedRadius * pulse,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.5f)
        )
    }

    // Secondary ripple center (offset)
    val secondCenterX = width * 0.7f
    val secondCenterY = height * 0.6f
    repeat(5) { i ->
        val baseRadius = maxRadius * 0.4f * (i + 1) / 5
        val animatedRadius = baseRadius + sin(phase + PI.toFloat() + i * 0.5f) * 3f
        val strokeAlpha = 0.1f + (1f - i.toFloat() / 5) * 0.15f

        drawCircle(
            color = strokeColor.copy(alpha = strokeAlpha),
            radius = animatedRadius,
            center = Offset(secondCenterX, secondCenterY),
            style = Stroke(width = 1f)
        )
    }

    // Decorative stones (small circles)
    val stonePositions = listOf(
        Offset(width * 0.2f, height * 0.3f),
        Offset(width * 0.8f, height * 0.5f),
        Offset(width * 0.4f, height * 0.7f)
    )
    stonePositions.forEach { pos ->
        drawCircle(
            color = Color(0xFF6B5B4A).copy(alpha = 0.6f),
            radius = 4f,
            center = pos
        )
        drawCircle(
            color = Color(0xFF8B7B6A).copy(alpha = 0.4f),
            radius = 6f,
            center = pos,
            style = Stroke(width = 1f)
        )
    }
}

/**
 * FOCUS PATTERN
 * Geometric triangles in high contrast for productivity and clarity.
 * Sharp, modern design that conveys precision and focus.
 */
private fun DrawScope.drawFocusPattern(phase: Float) {
    val width = size.width
    val height = size.height

    // High contrast background
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A1A2E), // Deep dark blue
            Color(0xFF16213E), // Navy
            Color(0xFF0F3460)  // Blue
        ),
        start = Offset.Zero,
        end = Offset(width, height)
    )
    drawRect(brush = backgroundBrush)

    // Grid of triangles
    val triangleSize = 40f
    val rows = (height / triangleSize).toInt() + 2
    val cols = (width / triangleSize).toInt() + 2

    val accentColor = Color(0xFF36F97F) // Neon green
    val secondaryColor = Color(0xFF00BCD4) // Cyan

    for (row in 0..rows) {
        for (col in 0..cols) {
            val baseX = col * triangleSize
            val baseY = row * triangleSize
            val isOffset = row % 2 == 1
            val offsetX = if (isOffset) triangleSize / 2 else 0f

            val x = baseX + offsetX
            val y = baseY

            // Determine if this triangle should be highlighted
            val distance = sqrt((x - width / 2).pow(2) + (y - height / 2).pow(2))
            val maxDist = sqrt((width / 2).pow(2) + (height / 2).pow(2))
            val normalizedDist = distance / maxDist

            val waveEffect = sin(normalizedDist * 4 * PI.toFloat() - phase) * 0.5f + 0.5f
            val alpha = 0.1f + waveEffect * 0.3f

            val triangleColor = if ((row + col) % 3 == 0) {
                accentColor.copy(alpha = alpha)
            } else if ((row + col) % 5 == 0) {
                secondaryColor.copy(alpha = alpha * 0.7f)
            } else {
                Color.White.copy(alpha = alpha * 0.3f)
            }

            // Draw upward-pointing triangle
            val path = Path().apply {
                moveTo(x, y + triangleSize)
                lineTo(x + triangleSize / 2, y)
                lineTo(x + triangleSize, y + triangleSize)
                close()
            }

            drawPath(
                path = path,
                color = triangleColor,
                style = Stroke(width = 1f)
            )

            // Fill some triangles for depth
            if (waveEffect > 0.7f && (row + col) % 4 == 0) {
                drawPath(
                    path = path,
                    color = accentColor.copy(alpha = 0.1f)
                )
            }
        }
    }

    // Central focus point with glow
    val centerX = width / 2
    val centerY = height / 2
    val glowRadius = 80f + sin(phase) * 20f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.3f),
                accentColor.copy(alpha = 0.1f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = glowRadius
        ),
        center = Offset(centerX, centerY),
        radius = glowRadius
    )
}

/**
 * FLOW PATTERN
 * Sine wave gradients creating movement and energy.
 * Dynamic, flowing design that suggests momentum and progress.
 */
private fun DrawScope.drawFlowPattern(phase: Float) {
    val width = size.width
    val height = size.height

    // Gradient background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF667EEA), // Purple-blue
            Color(0xFF764BA2), // Purple
            Color(0xFFAB47BC)  // Light purple
        )
    )
    drawRect(brush = backgroundBrush)

    // Multiple flowing wave layers
    val waveColors = listOf(
        Color.White.copy(alpha = 0.2f),
        Color.White.copy(alpha = 0.15f),
        Color.White.copy(alpha = 0.1f),
        Color(0xFFFFD54F).copy(alpha = 0.15f), // Golden accent
        Color(0xFF36F97F).copy(alpha = 0.1f)  // Green accent
    )

    waveColors.forEachIndexed { index, color ->
        val path = Path()
        val amplitude = height * (0.08f + index * 0.02f)
        val frequency = 2f + index * 0.5f
        val phaseOffset = index * PI.toFloat() / 3
        val yOffset = height * (0.2f + index * 0.15f)

        path.moveTo(0f, yOffset)

        var x = 0f
        while (x <= width) {
            val y = yOffset + sin((x / width * frequency * PI.toFloat()) + phase + phaseOffset) * amplitude
            path.lineTo(x, y)
            x += 2f
        }

        // Complete path to bottom
        path.lineTo(width, height)
        path.lineTo(0f, height)
        path.close()

        drawPath(path = path, color = color)
    }

    // Top wave lines (thinner, more detailed)
    repeat(6) { i ->
        val path = Path()
        val baseY = height * (0.1f + i * 0.08f)
        val amplitude = 15f + i * 5f
        val frequency = 3f + i * 0.3f
        val phaseOffset = i * PI.toFloat() / 4

        path.moveTo(0f, baseY)

        var x = 0f
        while (x <= width) {
            val y = baseY + sin((x / width * frequency * PI.toFloat()) + phase * 0.8f + phaseOffset) * amplitude
            if (x == 0f) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            x += 3f
        }

        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.15f - i * 0.02f),
            style = Stroke(width = 1.5f, cap = StrokeCap.Round)
        )
    }
}

/**
 * NIGHT PATTERN
 * Deep blue/black with glowing edge effects.
 * Calming, nocturnal design with subtle luminescence.
 */
private fun DrawScope.drawNightPattern(glowPulse: Float, seed: Int) {
    val width = size.width
    val height = size.height

    // Deep night sky gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0A0F), // Almost black
            Color(0xFF0D1117), // Dark blue-black
            Color(0xFF161B22), // Slightly lighter
            Color(0xFF1A1F2C)  // Dark navy
        )
    )
    drawRect(brush = backgroundBrush)

    // Edge glow effects (top and bottom)
    val topGlowBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF36F97F).copy(alpha = 0.15f * glowPulse),
            Color(0xFF36F97F).copy(alpha = 0.05f),
            Color.Transparent
        ),
        startY = 0f,
        endY = height * 0.3f
    )
    drawRect(brush = topGlowBrush)

    val bottomGlowBrush = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color(0xFF6B5CE7).copy(alpha = 0.05f),
            Color(0xFF6B5CE7).copy(alpha = 0.12f * glowPulse)
        ),
        startY = height * 0.7f,
        endY = height
    )
    drawRect(brush = bottomGlowBrush)

    // Side glow accents
    val leftGlowBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF00BCD4).copy(alpha = 0.08f * glowPulse),
            Color.Transparent
        ),
        startX = 0f,
        endX = width * 0.2f
    )
    drawRect(brush = leftGlowBrush)

    val rightGlowBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            Color(0xFFE91E63).copy(alpha = 0.06f * glowPulse)
        ),
        startX = width * 0.8f,
        endX = width
    )
    drawRect(brush = rightGlowBrush)

    // Subtle stars
    val random = Random(seed)
    repeat(30) { i ->
        val starRandom = Random(seed + i)
        val x = starRandom.nextFloat() * width
        val y = starRandom.nextFloat() * height
        val starSize = 0.5f + starRandom.nextFloat() * 1f
        val starAlpha = 0.2f + starRandom.nextFloat() * 0.3f

        drawCircle(
            color = Color.White.copy(alpha = starAlpha * glowPulse),
            radius = starSize,
            center = Offset(x, y)
        )
    }

    // Ambient glow spots
    repeat(3) { i ->
        val spotRandom = Random(seed + 500 + i)
        val x = spotRandom.nextFloat() * width
        val y = spotRandom.nextFloat() * height
        val radius = width * (0.15f + spotRandom.nextFloat() * 0.1f)

        val spotColors = listOf(
            Color(0xFF36F97F),
            Color(0xFF6B5CE7),
            Color(0xFF00BCD4)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    spotColors[i % spotColors.size].copy(alpha = 0.05f * glowPulse),
                    Color.Transparent
                ),
                center = Offset(x, y),
                radius = radius
            ),
            center = Offset(x, y),
            radius = radius
        )
    }

    // Glowing border/frame effect
    val borderPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(width, 0f)
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }

    drawPath(
        path = borderPath,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF36F97F).copy(alpha = 0.2f * glowPulse),
                Color(0xFF6B5CE7).copy(alpha = 0.15f * glowPulse),
                Color(0xFF00BCD4).copy(alpha = 0.2f * glowPulse)
            )
        ),
        style = Stroke(width = 2f)
    )
}

/**
 * Composable that renders a banner pattern by ID.
 * Maps banner pattern IDs to BannerPatternType enum.
 */
@Composable
fun BannerCanvasById(
    patternId: String,
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true,
    cornerRadius: Dp = 12.dp
) {
    val patternType = when (patternId) {
        "nebula", "pattern_nebula" -> BannerPatternType.NEBULA
        "zen", "pattern_zen" -> BannerPatternType.ZEN
        "focus", "pattern_focus" -> BannerPatternType.FOCUS
        "flow", "pattern_flow" -> BannerPatternType.FLOW
        "night", "pattern_night" -> BannerPatternType.NIGHT
        else -> BannerPatternType.NEBULA // Default fallback
    }

    BannerCanvas(
        patternType = patternType,
        modifier = modifier,
        showAnimation = showAnimation,
        cornerRadius = cornerRadius
    )
}

/**
 * Preview-friendly composable showing all patterns in a grid.
 */
@Composable
fun BannerCanvasShowcase(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BannerPatternType.values().forEach { patternType ->
            BannerCanvas(
                patternType = patternType,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                showAnimation = true
            )
        }
    }
}
