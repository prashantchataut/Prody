package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
// blur import removed - flat design with no blur effects
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.identity.ProdyBanners
import com.prody.prashant.domain.identity.ProdyBanners.PatternType
import com.prody.prashant.ui.theme.ProdyAccentGreen
import com.prody.prashant.ui.theme.ProdyBackgroundDark
import com.prody.prashant.ui.theme.ProdyPremiumViolet
import com.prody.prashant.ui.theme.ProdyPremiumVioletLight
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * BannerRenderer - Premium Profile Banner Display Component
 *
 * Renders banners with their gradient backgrounds and pattern overlays.
 * Supports all pattern types from ProdyBanners with smooth animations.
 *
 * Features:
 * - Multiple pattern types: Solid, Geometric, Waves, Constellation, Mandala, Aurora
 * - Animated patterns for premium feel
 * - Flexible sizing for different contexts (profile header, leaderboard, etc.)
 * - Performance-optimized Canvas rendering
 */

/**
 * Renders a banner with its pattern and gradient.
 *
 * @param banner The banner to render
 * @param modifier Modifier for sizing and positioning
 * @param showAnimation Whether to animate the pattern (disable for lists to save performance)
 * @param cornerRadius Corner radius for the banner shape
 */
@Composable
fun BannerRenderer(
    banner: ProdyBanners.Banner,
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true,
    cornerRadius: Dp = 8.dp
) {
    val gradientColors = remember(banner.gradientColors) {
        banner.gradientColors.map { Color(it) }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "banner_animation")

    // Animation values for different patterns
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val constellationPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "constellation_pulse"
    )

    val auroraOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aurora_offset"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
    ) {
        // Pattern overlay
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f)
        ) {
            val animatedWavePhase = if (showAnimation) wavePhase else 0f
            val animatedRotation = if (showAnimation) rotationAngle else 0f
            val animatedPulse = if (showAnimation) constellationPulse else 0.75f
            val animatedAurora = if (showAnimation) auroraOffset else 0.5f

            when (banner.patternType) {
                PatternType.SOLID -> {
                    // No pattern overlay for solid
                }
                PatternType.GEOMETRIC -> {
                    drawGeometricPattern(animatedRotation)
                }
                PatternType.WAVES -> {
                    drawWavePattern(animatedWavePhase)
                }
                PatternType.CONSTELLATION -> {
                    drawConstellationPattern(animatedPulse)
                }
                PatternType.MANDALA -> {
                    drawMandalaPattern(animatedRotation)
                }
                PatternType.AURORA -> {
                    drawAuroraPattern(animatedAurora)
                }
            }
        }
    }
}

/**
 * Compact banner display for leaderboard rows and small spaces.
 * Shows a small strip of the banner next to the username.
 */
@Composable
fun CompactBannerStrip(
    bannerId: String,
    modifier: Modifier = Modifier
) {
    val banner = remember(bannerId) {
        ProdyBanners.findById(bannerId) ?: ProdyBanners.getDefaultBanner()
    }

    BannerRenderer(
        banner = banner,
        modifier = modifier
            .width(32.dp)
            .height(8.dp),
        showAnimation = false, // Disable animation for compact view (performance)
        cornerRadius = 4.dp
    )
}

/**
 * Medium banner display for profile cards and previews.
 */
@Composable
fun MediumBannerDisplay(
    bannerId: String,
    modifier: Modifier = Modifier
) {
    val banner = remember(bannerId) {
        ProdyBanners.findById(bannerId) ?: ProdyBanners.getDefaultBanner()
    }

    BannerRenderer(
        banner = banner,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        showAnimation = true,
        cornerRadius = 12.dp
    )
}

/**
 * Large banner display for profile header.
 * Full-width with prominent pattern display.
 */
@Composable
fun ProfileBannerHeader(
    bannerId: String,
    modifier: Modifier = Modifier
) {
    val banner = remember(bannerId) {
        ProdyBanners.findById(bannerId) ?: ProdyBanners.getDefaultBanner()
    }

    BannerRenderer(
        banner = banner,
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        showAnimation = true,
        cornerRadius = 0.dp // Full bleed for header
    )
}

/**
 * Banner preview card for selection UI.
 * Shows the banner with name and unlock status.
 */
@Composable
fun BannerPreviewCard(
    banner: ProdyBanners.Banner,
    isUnlocked: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isUnlocked -> Color.Transparent
        else -> Color.Transparent
    }

    Surface(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column {
            Box(modifier = Modifier.alpha(if (isUnlocked) 1f else 0.5f)) {
                BannerRenderer(
                    banner = banner,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    showAnimation = isUnlocked,
                    cornerRadius = 12.dp
                )
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = banner.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isUnlocked) "Unlocked" else banner.unlockRequirement,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// =============================================================================
// SPECIAL BADGES: DEV & BETA
// =============================================================================

/**
 * Special badge types for developers and beta testers.
 * Reserved for specific users identified via backend/OAuth (future).
 * For now, can be previewed via debug settings.
 */
enum class SpecialBadgeType {
    /** Developer badge - single holder */
    DEV,
    /** Beta tester badge - limited holders */
    BETA_TESTER,
    /** Founding user badge */
    FOUNDER
}

/**
 * Dev Badge - Flowing code aesthetic with glitch effects.
 * Reserved for the app developer (Prashant).
 */
@Composable
fun DevBadge(
    modifier: Modifier = Modifier,
    showGlitch: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dev_badge")

    val glitchOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glitch"
    )

    val codeScroll by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "code_scroll"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .height(20.dp)
            .clip(RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Flat design - subtle alpha pulse background instead of blur glow
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(glowAlpha * 0.3f)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            ProdyAccentGreen.copy(alpha = 0.3f),
                            ProdyAccentGreen.copy(alpha = 0.2f)
                        )
                    )
                )
        )

        // Main background with code pattern
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(ProdyBackgroundDark)
        ) {
            // Flowing code effect (subtle)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.3f)
            ) {
                val lineCount = 5
                for (i in 0 until lineCount) {
                    val y = (size.height / lineCount) * i + (codeScroll * size.height / lineCount)
                    val adjustedY = y % size.height
                    drawLine(
                        color = Color(0xFF36F97F), // ProdyAccentGreen
                        start = Offset(0f, adjustedY),
                        end = Offset(size.width * 0.6f, adjustedY),
                        strokeWidth = 1f
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .offset(
                    x = if (showGlitch) (glitchOffset - 1).dp else 0.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Code,
                contentDescription = "Developer",
                tint = ProdyAccentGreen,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "DEV",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = ProdyAccentGreen,
                fontSize = 10.sp
            )
        }
    }
}

/**
 * Beta Badge - Clean, minimal badge for beta testers.
 * Reserved for 2-3 early testers.
 */
@Composable
fun BetaBadge(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "beta_badge")

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .height(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        ProdyPremiumViolet,
                        ProdyPremiumVioletLight
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Shimmer overlay
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f)
        ) {
            val shimmerWidth = size.width * 0.3f
            val shimmerX = shimmer * (size.width + shimmerWidth) - shimmerWidth
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    startX = shimmerX,
                    endX = shimmerX + shimmerWidth
                )
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Science,
                contentDescription = "Beta Tester",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "BETA",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 10.sp
            )
        }
    }
}

/**
 * Combined badge display showing special badge if applicable.
 * Used in profile header and leaderboard.
 */
@Composable
fun SpecialBadgeDisplay(
    badgeType: SpecialBadgeType?,
    modifier: Modifier = Modifier
) {
    when (badgeType) {
        SpecialBadgeType.DEV -> DevBadge(modifier = modifier)
        SpecialBadgeType.BETA_TESTER -> BetaBadge(modifier = modifier)
        SpecialBadgeType.FOUNDER -> BetaBadge(modifier = modifier) // Founder uses similar style
        null -> { /* No badge */ }
    }
}

// =============================================================================
// PATTERN DRAWING FUNCTIONS
// =============================================================================

private fun DrawScope.drawGeometricPattern(rotation: Float) {
    val triangleSize = size.minDimension * 0.15f
    val spacing = triangleSize * 1.5f
    val rows = (size.height / spacing).toInt() + 2
    val cols = (size.width / spacing).toInt() + 2

    rotate(rotation * 0.1f) {
        for (row in -1..rows) {
            for (col in -1..cols) {
                val centerX = col * spacing + (if (row % 2 == 0) 0f else spacing / 2)
                val centerY = row * spacing

                // Draw small triangle
                val path = Path().apply {
                    moveTo(centerX, centerY - triangleSize / 2)
                    lineTo(centerX + triangleSize / 2, centerY + triangleSize / 2)
                    lineTo(centerX - triangleSize / 2, centerY + triangleSize / 2)
                    close()
                }

                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.2f),
                    style = Stroke(width = 1.5f)
                )
            }
        }
    }
}

private fun DrawScope.drawWavePattern(phase: Float) {
    val waveCount = 4
    val waveSpacing = size.height / waveCount

    for (i in 0 until waveCount) {
        val baseY = waveSpacing * i + waveSpacing / 2
        val path = Path()

        path.moveTo(0f, baseY)

        var x = 0f
        while (x <= size.width) {
            val y = baseY + sin((x / size.width * 2 * PI + phase + i * 0.5).toFloat()) * waveSpacing * 0.3f
            if (x == 0f) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            x += 2f
        }

        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.25f - i * 0.05f),
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawConstellationPattern(pulse: Float) {
    // Create constellation points based on size
    val starCount = 12
    val stars = List(starCount) { i ->
        val angle = (i.toFloat() / starCount) * 2 * PI
        val radius = size.minDimension * 0.3f * (0.5f + (i % 3) * 0.25f)
        Offset(
            x = size.width / 2 + (radius * cos(angle)).toFloat(),
            y = size.height / 2 + (radius * sin(angle)).toFloat()
        )
    }

    // Draw connection lines
    for (i in stars.indices) {
        val next = (i + 1) % stars.size
        if (i % 2 == 0) {
            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = stars[i],
                end = stars[next],
                strokeWidth = 1f
            )
        }
    }

    // Draw stars with pulse
    stars.forEachIndexed { index, star ->
        val starPulse = pulse * (0.8f + (index % 3) * 0.2f)
        drawCircle(
            color = Color.White.copy(alpha = 0.4f * starPulse),
            radius = 3f + (index % 2) * 2f,
            center = star
        )
        // Star glow
        drawCircle(
            color = Color.White.copy(alpha = 0.15f * starPulse),
            radius = 6f + (index % 2) * 3f,
            center = star
        )
    }
}

private fun DrawScope.drawMandalaPattern(rotation: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val maxRadius = size.minDimension * 0.4f

    rotate(rotation * 0.2f, Offset(centerX, centerY)) {
        // Draw concentric circles
        val circleCount = 3
        for (i in 1..circleCount) {
            val radius = maxRadius * (i.toFloat() / circleCount)
            drawCircle(
                color = Color.White.copy(alpha = 0.2f - i * 0.05f),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 1.5f)
            )
        }

        // Draw petal shapes
        val petalCount = 8
        for (i in 0 until petalCount) {
            val angle = (i.toFloat() / petalCount) * 2 * PI
            val startX = centerX + (maxRadius * 0.3f * cos(angle)).toFloat()
            val startY = centerY + (maxRadius * 0.3f * sin(angle)).toFloat()
            val endX = centerX + (maxRadius * 0.8f * cos(angle)).toFloat()
            val endY = centerY + (maxRadius * 0.8f * sin(angle)).toFloat()

            drawLine(
                color = Color.White.copy(alpha = 0.2f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawAuroraPattern(offset: Float) {
    val waveCount = 3

    for (i in 0 until waveCount) {
        val path = Path()
        val baseY = size.height * (0.3f + i * 0.2f)
        val amplitude = size.height * 0.1f
        val phaseOffset = offset * 2 * PI.toFloat() + i * (PI.toFloat() / 3)

        path.moveTo(0f, baseY)

        var x = 0f
        while (x <= size.width) {
            val progress = x / size.width
            val y = baseY + sin(progress * 3 * PI + phaseOffset) * amplitude * (1 - progress * 0.3f)
            path.lineTo(x, y.toFloat())
            x += 3f
        }

        // Complete the path to fill
        path.lineTo(size.width, size.height)
        path.lineTo(0f, size.height)
        path.close()

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.15f - i * 0.04f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        )
    }
}

