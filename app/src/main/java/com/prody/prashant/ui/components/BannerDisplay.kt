package com.prody.prashant.ui.components
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
// shadow import removed - flat design with no shadows
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.identity.ProdyBanners
import com.prody.prashant.ui.theme.ProdyTokens
import com.prody.prashant.ui.theme.ProdyAccentGreen
import com.prody.prashant.ui.theme.ProdyAccentGreenDark
import com.prody.prashant.ui.theme.ProdyPremiumViolet
import com.prody.prashant.ui.theme.ProdyPremiumVioletDark
import com.prody.prashant.ui.theme.LeaderboardGold
import com.prody.prashant.ui.theme.LeaderboardGoldDark
import com.prody.prashant.ui.theme.RarityLegendary
import com.prody.prashant.ui.theme.RarityEpic
import com.prody.prashant.ui.theme.RarityRare
import com.prody.prashant.ui.theme.RarityUncommon
import com.prody.prashant.ui.theme.RarityCommon
import com.prody.prashant.ui.theme.ProdyBackgroundDark
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Prody Gamification 2.0 - Banner & Badge Display System
 *
 * A premium visual system for displaying user banners, badges, and special identifiers.
 * Features:
 * - Dynamic banner rendering with pattern overlays
 * - Dev and Beta Tester badge support
 * - Rarity-based visual effects
 * - Smooth animations and premium feel
 *
 * Design principles:
 * - Minimal, tasteful visuals - no cluttery
 * - Premium gradient effects
 * - Subtle animations that don't distract
 * - Consistent with Prody identity
 */

// =============================================================================
// BANNER DISPLAY COMPONENT
// =============================================================================

/**
 * Renders a user's banner with pattern overlay.
 *
 * @param banner The banner to display
 * @param modifier Modifier for the component
 * @param height Height of the banner
 * @param showName Whether to show the banner name overlay
 */
@Composable
fun ProdyBannerDisplay(
    banner: ProdyBanners.Banner,
    modifier: Modifier = Modifier,
    height: Dp = 80.dp,
    showName: Boolean = false
) {
    val gradientColors = banner.gradientColors.map { Color(it) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(ProdyTokens.Radius.md))
            .background(Brush.horizontalGradient(gradientColors))
    ) {
        // Pattern overlay
        BannerPattern(
            patternType = banner.patternType,
            modifier = Modifier.fillMaxSize()
        )

        // Optional name overlay
        if (showName) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f)
                            )
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = banner.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(ProdyTokens.Spacing.sm)
                )
            }
        }
    }
}

/**
 * Compact banner strip for leaderboard rows and profile headers.
 */
@Composable
fun ProdyBannerStrip(
    banner: ProdyBanners.Banner,
    modifier: Modifier = Modifier,
    height: Dp = 4.dp
) {
    val gradientColors = banner.gradientColors.map { Color(it) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(ProdyTokens.Radius.full))
            .background(Brush.horizontalGradient(gradientColors))
    )
}

/**
 * Banner badge for compact displays (next to username).
 */
@Composable
fun ProdyBannerBadge(
    banner: ProdyBanners.Banner,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    val gradientColors = banner.gradientColors.map { Color(it) }
    val primaryColor = gradientColors.firstOrNull() ?: MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradientColors))
            .border(1.dp, primaryColor.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Mini pattern indicator based on pattern type
        val patternIcon: Char = when (banner.patternType) {
            ProdyBanners.PatternType.SOLID -> '\u25CF' // Filled circle
            ProdyBanners.PatternType.GEOMETRIC -> '\u25B3' // Triangle
            ProdyBanners.PatternType.WAVES -> '\u223F' // Wave
            ProdyBanners.PatternType.CONSTELLATION -> '\u2605' // Star
            ProdyBanners.PatternType.MANDALA -> '\u2740' // Flower
            ProdyBanners.PatternType.AURORA -> '\u2727' // Four-pointed star
        }

        Text(
            text = patternIcon.toString(),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = (size.value * 0.5f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// =============================================================================
// BANNER PATTERN RENDERING
// =============================================================================

@Composable
private fun BannerPattern(
    patternType: ProdyBanners.PatternType,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "banner_pattern")

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pattern_animation"
    )

    when (patternType) {
        ProdyBanners.PatternType.SOLID -> { /* No pattern */ }
        ProdyBanners.PatternType.GEOMETRIC -> GeometricPattern(modifier, animationProgress)
        ProdyBanners.PatternType.WAVES -> WavesPattern(modifier, animationProgress)
        ProdyBanners.PatternType.CONSTELLATION -> ConstellationPattern(modifier, animationProgress)
        ProdyBanners.PatternType.MANDALA -> MandalaPattern(modifier, animationProgress)
        ProdyBanners.PatternType.AURORA -> AuroraPattern(modifier, animationProgress)
    }
}

@Composable
private fun GeometricPattern(modifier: Modifier, progress: Float) {
    Canvas(modifier = modifier.alpha(0.15f)) {
        val spacing = 40f
        val triangleSize = 15f

        for (x in 0..((size.width / spacing).toInt() + 1)) {
            for (y in 0..((size.height / spacing).toInt() + 1)) {
                val offsetX = x * spacing + (if (y % 2 == 0) 0f else spacing / 2)
                val offsetY = y * spacing

                drawPath(
                    path = Path().apply {
                        moveTo(offsetX, offsetY - triangleSize)
                        lineTo(offsetX + triangleSize, offsetY + triangleSize)
                        lineTo(offsetX - triangleSize, offsetY + triangleSize)
                        close()
                    },
                    color = Color.White,
                    style = Stroke(width = 1f)
                )
            }
        }
    }
}

@Composable
private fun WavesPattern(modifier: Modifier, progress: Float) {
    Canvas(modifier = modifier.alpha(0.12f)) {
        val waveCount = 4
        val amplitude = 10f

        for (i in 0 until waveCount) {
            val yOffset = (size.height / (waveCount + 1)) * (i + 1)
            val path = Path()

            path.moveTo(0f, yOffset)

            for (x in 0..size.width.toInt() step 10) {
                val y = yOffset + amplitude * sin((x.toFloat() / 50f + progress * 2 * PI.toFloat() + i * 0.5f))
                path.lineTo(x.toFloat(), y.toFloat())
            }

            drawPath(
                path = path,
                color = Color.White,
                style = Stroke(width = 2f, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun ConstellationPattern(modifier: Modifier, progress: Float) {
    // Generate star positions as relative values (0-1) - computed once
    val starPositions = remember {
        (0..15).map {
            Pair(Math.random().toFloat(), Math.random().toFloat())
        }
    }

    Canvas(modifier = modifier.alpha(0.2f)) {
        val stars = starPositions.map { (relX, relY) ->
            Offset(relX * size.width, relY * size.height)
        }

        stars.forEachIndexed { index, star ->
            val twinkle = (sin(progress * 2 * PI.toFloat() + index * 0.5f) + 1) / 2f
            val starSize = 2f + twinkle * 2f

            drawCircle(
                color = Color.White.copy(alpha = 0.5f + twinkle * 0.5f),
                radius = starSize,
                center = star
            )
        }

        // Connect some stars
        for (i in 0 until stars.size - 1 step 3) {
            if (i + 1 < stars.size) {
                drawLine(
                    color = Color.White.copy(alpha = 0.2f),
                    start = stars[i],
                    end = stars[i + 1],
                    strokeWidth = 1f
                )
            }
        }
    }
}

@Composable
private fun MandalaPattern(modifier: Modifier, progress: Float) {
    Canvas(modifier = modifier.alpha(0.1f)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val rings = 3
        val baseRadius = 30f

        for (ring in 1..rings) {
            val radius = baseRadius * ring
            val petalCount = 6 * ring

            for (i in 0 until petalCount) {
                val angle = (2 * PI / petalCount * i + progress * PI / 2).toFloat()
                val x = centerX + radius * cos(angle)
                val y = centerY + radius * sin(angle)

                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = Offset(x, y)
                )
            }

            drawCircle(
                color = Color.White,
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 1f)
            )
        }
    }
}

@Composable
private fun AuroraPattern(modifier: Modifier, progress: Float) {
    Canvas(modifier = modifier.alpha(0.15f)) {
        val auroraPath = Path()
        val curveCount = 3

        for (i in 0 until curveCount) {
            val yBase = size.height * (0.3f + i * 0.2f)
            auroraPath.moveTo(0f, yBase)

            for (x in 0..size.width.toInt() step 20) {
                val y = yBase + 15f * sin((x / 80f + progress * 2 * PI.toFloat() + i))
                auroraPath.lineTo(x.toFloat(), y.toFloat())
            }
        }

        drawPath(
            path = auroraPath,
            color = Color.White,
            style = Stroke(
                width = 3f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.cornerPathEffect(10f)
            )
        )
    }
}

// =============================================================================
// SPECIAL BADGES (DEV / BETA TESTER)
// =============================================================================

// Note: SpecialBadgeType enum is defined in BannerRenderer.kt

/**
 * Special badge display for Dev/Beta/Founder users.
 *
 * @param badgeType Type of special badge
 * @param modifier Modifier for the component
 * @param size Size of the badge
 * @param showLabel Whether to show the badge label
 */
@Composable
fun ProdySpecialBadge(
    badgeType: SpecialBadgeType,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    showLabel: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "special_badge")

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val badgeConfig = when (badgeType) {
        SpecialBadgeType.DEV -> BadgeConfig(
            icon = ProdyIcons.Code,
            label = "DEV",
            primaryColor = ProdyAccentGreen,
            secondaryColor = ProdyAccentGreenDark
        )
        SpecialBadgeType.BETA_TESTER -> BadgeConfig(
            icon = ProdyIcons.VerifiedUser,
            label = "BETA",
            primaryColor = ProdyPremiumViolet,
            secondaryColor = ProdyPremiumVioletDark
        )
        SpecialBadgeType.FOUNDER -> BadgeConfig(
            icon = ProdyIcons.Star,
            label = "FOUNDER",
            primaryColor = LeaderboardGold,
            secondaryColor = LeaderboardGoldDark
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        // Flat design - use alpha pulse instead of shadow
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(badgeConfig.primaryColor.copy(alpha = 0.85f + (0.15f * glowPulse)))
                .border(1.dp, badgeConfig.primaryColor.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = badgeConfig.icon,
                contentDescription = badgeConfig.label,
                modifier = Modifier.size(size * 0.6f),
                tint = Color.White
            )
        }

        if (showLabel) {
            Spacer(modifier = Modifier.width(ProdyTokens.Spacing.xs))
            Text(
                text = badgeConfig.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = badgeConfig.primaryColor
            )
        }
    }
}

private data class BadgeConfig(
    val icon: ImageVector,
    val label: String,
    val primaryColor: Color,
    val secondaryColor: Color
)

// =============================================================================
// DEV BANNER (FLOWING CODE EFFECT)
// =============================================================================

/**
 * Special developer banner with flowing code effect.
 * Reserved for the single DEV badge holder.
 */
@Composable
fun ProdyDevBanner(
    modifier: Modifier = Modifier,
    height: Dp = 80.dp,
    isPlaying: Boolean = false // Added to make the new code snippet syntactically correct
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dev_banner")

    val backgroundColor by animateColorAsState(
        targetValue = if (isPlaying) MoodMotivated else MoodCalm,
        animationSpec = tween(1000),
        label = "banner_color"
    )
    val codeScroll by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "code_scroll"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(ProdyTokens.Radius.md))
            .background(ProdyBackgroundDark)
    ) {
        // Flowing code effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val codeChars = "01{}[]<>/;:=+-*&|!?#$%@".toList()
            val charCount = 50

            for (i in 0 until charCount) {
                val x = ((size.width / charCount) * i + codeScroll * size.width) % size.width
                val y = ((i * 17) % size.height.toInt()).toFloat()
                val alpha = (sin(i.toFloat() + codeScroll * 10) + 1) / 4f

                drawContext.canvas.nativeCanvas.drawText(
                    codeChars[i % codeChars.size].toString(),
                    x,
                    y,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.argb(
                            (alpha * 255).toInt(),
                            54,  // R from #36F97F
                            249, // G from #36F97F
                            127  // B from #36F97F
                        )
                        textSize = 12f
                    }
                )
            }
        }

        // Subtle accent overlay (flat design - no glow)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    ProdyAccentGreen.copy(alpha = glowPulse * 0.08f)
                )
        )

        // Badge overlay
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(ProdyTokens.Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProdySpecialBadge(
                badgeType = SpecialBadgeType.DEV,
                size = 32.dp,
                showLabel = true
            )
        }
    }
}

// =============================================================================
// RARITY FRAME FOR PROFILE
// =============================================================================

/**
 * Rarity-based frame for profile avatar.
 */
@Composable
fun ProdyRarityFrame(
    rarity: String,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    content: @Composable () -> Unit
) {
    val frameColors = when (rarity.lowercase()) {
        "legendary" -> listOf(RarityLegendary, RarityLegendary.copy(alpha = 0.8f), RarityLegendary.copy(alpha = 0.6f))
        "epic" -> listOf(RarityEpic, RarityEpic.copy(alpha = 0.8f), RarityEpic.copy(alpha = 0.6f))
        "rare" -> listOf(RarityRare, RarityRare.copy(alpha = 0.8f), RarityRare.copy(alpha = 0.6f))
        "uncommon" -> listOf(RarityUncommon, RarityUncommon.copy(alpha = 0.8f), RarityUncommon.copy(alpha = 0.6f))
        else -> listOf(RarityCommon, RarityCommon.copy(alpha = 0.8f), RarityCommon.copy(alpha = 0.6f))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "frame_animation")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "frame_rotation"
    )

    Box(
        modifier = modifier.size(size + 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Animated gradient border
        if (rarity.lowercase() in listOf("legendary", "epic", "rare")) {
            Box(
                modifier = Modifier
                    .size(size + 6.dp)
                    .graphicsLayer { rotationZ = rotationAngle }
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            colors = frameColors + frameColors.first()
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .size(size + 4.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(frameColors)
                    )
            )
        }

        // Content container
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
