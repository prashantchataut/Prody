package com.prody.prashant.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import com.prody.prashant.domain.identity.ProdyBanners
import com.prody.prashant.domain.identity.ProdyTitles
import com.prody.prashant.ui.theme.LeaderboardBronze
import com.prody.prashant.ui.theme.LeaderboardBronzeDark
import com.prody.prashant.ui.theme.LeaderboardBronzeLight
import com.prody.prashant.ui.theme.LeaderboardGold
import com.prody.prashant.ui.theme.LeaderboardGoldDark
import com.prody.prashant.ui.theme.LeaderboardGoldLight
import com.prody.prashant.ui.theme.LeaderboardSilver
import com.prody.prashant.ui.theme.LeaderboardSilverDark
import com.prody.prashant.ui.theme.LeaderboardSilverLight
import com.prody.prashant.ui.theme.ProdySuccess
import com.prody.prashant.ui.theme.ProdyError
import com.prody.prashant.ui.theme.ProdyTokens
import com.prody.prashant.ui.theme.ProdyAccentGreen
import com.prody.prashant.ui.theme.ProdyAccentGreenLight
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI
import kotlin.random.Random

/**
 * Prody Premium Leaderboard Row Component
 *
 * A clean, minimal leaderboard row that integrates Gamification 2.0 features:
 * - Banner badge next to username
 * - DEV/Beta badges for special users
 * - Subtle boost counter
 * - Rank indicator for top 3 with flowing animated banners
 * - Long-press for support interaction
 *
 * Design principles:
 * - No cluttery UI - everything is minimal
 * - Premium feel with subtle animations
 * - Clear visual hierarchy
 */

// =============================================================================
// TOP 3 RANK COLORS - Using Theme Colors from Color.kt
// =============================================================================

private object LeaderboardRankColors {
    // Gold - 1st Place (using theme colors)
    val GoldPrimary = LeaderboardGold
    val GoldSecondary = LeaderboardGoldDark
    val GoldTertiary = LeaderboardGoldLight
    val GoldGlow = LeaderboardGold.copy(alpha = 0.25f)

    // Silver - 2nd Place (using theme colors)
    val SilverPrimary = LeaderboardSilver
    val SilverSecondary = LeaderboardSilverDark
    val SilverTertiary = LeaderboardSilverLight
    val SilverGlow = LeaderboardSilver.copy(alpha = 0.25f)

    // Bronze - 3rd Place (using theme colors)
    val BronzePrimary = LeaderboardBronze
    val BronzeSecondary = LeaderboardBronzeDark
    val BronzeTertiary = LeaderboardBronzeLight
    val BronzeGlow = LeaderboardBronze.copy(alpha = 0.25f)
}

/**
 * Champion animated banner for top 3 leaderboard positions.
 *
 * Creates premium visual effects to distinguish top performers:
 * - #1 Champion: Starfield with twinkling stars and gold accents
 * - #2 Runner-up: Liquid mercury effect with metallic sheen
 * - #3 Bronze: Aurora borealis with flowing color waves
 */
@Composable
private fun FlowingRankBanner(
    rank: Int,
    modifier: Modifier = Modifier
) {
    when (rank) {
        1 -> ChampionStarfieldBannerInline(modifier = modifier)
        2 -> RunnerUpLiquidBannerInline(modifier = modifier)
        3 -> BronzeAuroraBannerInline(modifier = modifier)
        else -> return // No banner for ranks > 3
    }
}

/**
 * Inline Champion Starfield Banner for leaderboard row.
 * Twinkling stars with golden nebula - "Champion of the Universe" feel.
 */
@Composable
private fun ChampionStarfieldBannerInline(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "starfield_inline")

    val twinklePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle"
    )

    val goldPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = androidx.compose.animation.core.EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gold_pulse"
    )

    val shootingStarPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shooting_star"
    )

    val stars = remember {
        List(20) {
            StarDataInline(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 1.5f + 0.5f,
                twinkleOffset = Random.nextFloat() * 2f * PI.toFloat(),
                brightness = Random.nextFloat() * 0.4f + 0.4f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Deep space gradient with gold tint
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF0A0A14).copy(alpha = 0.9f),
                    Color(0xFF1A1428).copy(alpha = 0.85f),
                    Color(0xFF0D0D0D).copy(alpha = 0.9f)
                )
            )
        )

        // Golden nebula clouds
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    LeaderboardGold.copy(alpha = goldPulse * 0.2f),
                    LeaderboardGold.copy(alpha = goldPulse * 0.08f),
                    Color.Transparent
                ),
                center = Offset(width * 0.3f, height * 0.5f),
                radius = width * 0.4f
            ),
            radius = width * 0.4f,
            center = Offset(width * 0.3f, height * 0.5f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    LeaderboardGoldDark.copy(alpha = goldPulse * 0.15f),
                    Color.Transparent
                ),
                center = Offset(width * 0.7f, height * 0.5f),
                radius = width * 0.3f
            ),
            radius = width * 0.3f,
            center = Offset(width * 0.7f, height * 0.5f)
        )

        // Twinkling stars
        stars.forEach { star ->
            val twinkle = (sin(twinklePhase + star.twinkleOffset) + 1f) / 2f
            val currentBrightness = star.brightness * (0.4f + twinkle * 0.6f)

            // Star glow
            drawCircle(
                color = Color.White.copy(alpha = currentBrightness * 0.25f),
                radius = star.size * 2.5f,
                center = Offset(star.x * width, star.y * height)
            )

            // Star core
            drawCircle(
                color = Color.White.copy(alpha = currentBrightness),
                radius = star.size,
                center = Offset(star.x * width, star.y * height)
            )
        }

        // Shooting star effect
        if (shootingStarPhase in 0.3f..0.55f) {
            val progress = (shootingStarPhase - 0.3f) / 0.25f
            val startX = width * 0.85f
            val startY = height * 0.1f
            val endX = width * 0.15f
            val endY = height * 0.9f

            val currentX = startX + (endX - startX) * progress
            val currentY = startY + (endY - startY) * progress

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        LeaderboardGold.copy(alpha = 0.5f),
                        Color.White
                    ),
                    start = Offset(currentX + 30, currentY - 30),
                    end = Offset(currentX, currentY)
                ),
                start = Offset(currentX + 30, currentY - 30),
                end = Offset(currentX, currentY),
                strokeWidth = 1.5f
            )

            drawCircle(
                color = Color.White,
                radius = 2f,
                center = Offset(currentX, currentY)
            )
        }

        // Gold edge accents
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    LeaderboardGold.copy(alpha = goldPulse * 0.5f),
                    Color.Transparent,
                    Color.Transparent,
                    LeaderboardGold.copy(alpha = goldPulse * 0.5f)
                )
            )
        )
    }
}

private data class StarDataInline(
    val x: Float,
    val y: Float,
    val size: Float,
    val twinkleOffset: Float,
    val brightness: Float
)

/**
 * Inline Runner-Up Liquid Banner for leaderboard row.
 * Liquid mercury/metal effect with flowing waves.
 */
@Composable
private fun RunnerUpLiquidBannerInline(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_inline")

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

    val shimmerPosition by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val bubbles = remember {
        List(5) {
            BubbleDataInline(
                x = Random.nextFloat(),
                startY = Random.nextFloat() * 0.3f + 0.7f,
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 0.4f + 0.4f,
                phaseOffset = Random.nextFloat() * 2f * PI.toFloat()
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Mercury/steel base
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF2C3E50).copy(alpha = 0.9f),
                    Color(0xFF4A5568).copy(alpha = 0.85f),
                    Color(0xFF2D3748).copy(alpha = 0.9f)
                )
            )
        )

        // Liquid mercury waves
        drawLiquidWaveInline(width, height, wave1Phase, 0.55f, 0.1f, LeaderboardSilver.copy(alpha = 0.35f))
        drawLiquidWaveInline(width, height, wave2Phase, 0.65f, 0.08f, LeaderboardSilverLight.copy(alpha = 0.25f))
        drawLiquidWaveInline(width, height, wave1Phase * 1.5f, 0.75f, 0.06f, LeaderboardSilverDark.copy(alpha = 0.3f))

        // Rising bubbles
        bubbles.forEach { bubble ->
            val bubbleY = (bubble.startY - (wave1Phase / (2f * PI.toFloat())) * bubble.speed) % 1.1f
            if (bubbleY > 0f) {
                val wobble = sin(wave1Phase * 2 + bubble.phaseOffset) * 8f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            LeaderboardSilver.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    radius = bubble.size,
                    center = Offset(bubble.x * width + wobble, bubbleY * height)
                )
            }
        }

        // Metallic shimmer sweep
        val shimmerWidth = width * 0.35f
        val shimmerX = shimmerPosition * (width + shimmerWidth) - shimmerWidth / 2

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.12f),
                    Color.White.copy(alpha = 0.2f),
                    Color.White.copy(alpha = 0.12f),
                    Color.Transparent
                ),
                startX = shimmerX - shimmerWidth / 2,
                endX = shimmerX + shimmerWidth / 2
            )
        )

        // Silver edge highlights
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    LeaderboardSilverLight.copy(alpha = 0.25f),
                    Color.Transparent,
                    Color.Transparent,
                    LeaderboardSilver.copy(alpha = 0.15f)
                )
            )
        )
    }
}

private data class BubbleDataInline(
    val x: Float,
    val startY: Float,
    val size: Float,
    val speed: Float,
    val phaseOffset: Float
)

private fun DrawScope.drawLiquidWaveInline(
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
                sin((x / width * 5 * PI + phase * 1.3f).toFloat()) * height * amplitude * 0.4f
            lineTo(x, y)
            x += 3f
        }

        lineTo(width, height)
        close()
    }

    drawPath(path = path, color = color)
}

/**
 * Inline Bronze Aurora Banner for leaderboard row.
 * Northern lights effect with bronze/copper and green accents.
 */
@Composable
private fun BronzeAuroraBannerInline(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora_inline")

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

    val verticalShimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = androidx.compose.animation.core.EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vertical_shimmer"
    )

    val bronzeGlow by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = androidx.compose.animation.core.EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bronze_glow"
    )

    val auroraStars = remember {
        List(10) {
            Offset(Random.nextFloat(), Random.nextFloat() * 0.4f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Dark night sky
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF0D1117).copy(alpha = 0.9f),
                    Color(0xFF161B22).copy(alpha = 0.85f),
                    Color(0xFF0D1117).copy(alpha = 0.9f)
                )
            )
        )

        // Aurora curtain 1 - Bronze/copper
        drawAuroraCurtainInline(
            width = width,
            height = height,
            phase = auroraPhase1,
            baseX = 0.25f,
            colors = listOf(
                LeaderboardBronze.copy(alpha = 0.35f * bronzeGlow),
                LeaderboardBronzeLight.copy(alpha = 0.25f * bronzeGlow),
                Color(0xFFDAA06D).copy(alpha = 0.15f * bronzeGlow)
            ),
            verticalOffset = verticalShimmer
        )

        // Aurora curtain 2 - Teal/green accent
        drawAuroraCurtainInline(
            width = width,
            height = height,
            phase = auroraPhase2,
            baseX = 0.55f,
            colors = listOf(
                Color(0xFF50C878).copy(alpha = 0.2f),
                ProdyAccentGreen.copy(alpha = 0.15f),
                Color(0xFF2E8B57).copy(alpha = 0.1f)
            ),
            verticalOffset = 1f - verticalShimmer
        )

        // Aurora curtain 3 - Warm bronze
        drawAuroraCurtainInline(
            width = width,
            height = height,
            phase = auroraPhase1 * 1.3f,
            baseX = 0.8f,
            colors = listOf(
                LeaderboardBronzeDark.copy(alpha = 0.3f * bronzeGlow),
                Color(0xFFB8860B).copy(alpha = 0.2f * bronzeGlow),
                LeaderboardBronze.copy(alpha = 0.15f * bronzeGlow)
            ),
            verticalOffset = verticalShimmer * 0.5f
        )

        // Subtle stars
        auroraStars.forEach { star ->
            val twinkle = (sin(auroraPhase1 + star.x * 10) + 1f) / 2f
            drawCircle(
                color = Color.White.copy(alpha = 0.25f + twinkle * 0.25f),
                radius = 1f,
                center = Offset(star.x * width, star.y * height)
            )
        }

        // Bronze edge glow
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    LeaderboardBronze.copy(alpha = bronzeGlow * 0.35f),
                    Color.Transparent,
                    Color.Transparent,
                    LeaderboardBronze.copy(alpha = bronzeGlow * 0.35f)
                )
            )
        )
    }
}

private fun DrawScope.drawAuroraCurtainInline(
    width: Float,
    height: Float,
    phase: Float,
    baseX: Float,
    colors: List<Color>,
    verticalOffset: Float
) {
    val curtainWidth = width * 0.35f
    val centerX = width * baseX

    val stripCount = 6
    for (i in 0 until stripCount) {
        val stripOffset = (i - stripCount / 2) * (curtainWidth / stripCount)
        val waveX = sin(phase + i * 0.5f) * 10f
        val x = centerX + stripOffset + waveX

        val stripHeight = height * (0.45f + verticalOffset * 0.25f + sin(phase + i) * 0.08f)

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
            end = Offset(x + sin(phase * 2 + i) * 4f, stripHeight),
            strokeWidth = (curtainWidth / stripCount) * 1.3f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

/**
 * Premium leaderboard row component.
 *
 * @param entry The leaderboard entry to display
 * @param onRowClick Callback when row is clicked
 * @param onLongPress Callback when row is long-pressed (for support)
 * @param modifier Modifier for the component
 * @param showSupportButton Whether to show the support icon button
 * @param hasUserSupportedToday Whether the current user has supported this person today
 * @param onSupportClick Callback when support button is clicked
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProdyLeaderboardRow(
    entry: LeaderboardEntryEntity,
    onRowClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    showSupportButton: Boolean = true,
    hasUserSupportedToday: Boolean = false,
    onSupportClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "row_scale"
    )

    // Determine if this is a highlighted row (top 3 or current user)
    val isHighlighted = entry.rank in 1..3 || entry.isCurrentUser
    val backgroundColor = when {
        entry.isCurrentUser -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        entry.rank == 1 -> LeaderboardGold.copy(alpha = 0.08f)
        entry.rank == 2 -> LeaderboardSilver.copy(alpha = 0.08f)
        entry.rank == 3 -> LeaderboardBronze.copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.surface
    }

    // Check if this row should have a flowing banner (top 3)
    val hasFlowingBanner = entry.rank in 1..3

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onRowClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress()
                }
            ),
        shape = RoundedCornerShape(ProdyTokens.Radius.md),
        color = backgroundColor,
        tonalElevation = 0.dp // Flat design
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Flowing animated banner for top 3 positions
            if (hasFlowingBanner) {
                FlowingRankBanner(
                    rank = entry.rank,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(ProdyTokens.Radius.md))
                )
            }

            // Row content (on top of banner)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ProdyTokens.Spacing.md, vertical = ProdyTokens.Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank indicator
                Box(
                    modifier = Modifier.width(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (entry.rank in 1..3) {
                        ProdyTopRankIndicator(rank = entry.rank)
                    } else {
                        Text(
                            text = "#${entry.rank}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(ProdyTokens.Spacing.sm))

                // Avatar with rarity frame
                ProdyRarityFrame(
                    rarity = entry.profileFrameRarity,
                    size = 44.dp
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(ProdyTokens.Spacing.md))

                // User info column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display name
                        Text(
                            text = entry.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        // Special badges
                        if (entry.isDevBadgeHolder) {
                            Spacer(modifier = Modifier.width(6.dp))
                            ProdySpecialBadge(
                                badgeType = SpecialBadgeType.DEV,
                                size = 18.dp
                            )
                        } else if (entry.isBetaTester) {
                            Spacer(modifier = Modifier.width(6.dp))
                            ProdySpecialBadge(
                                badgeType = SpecialBadgeType.BETA_TESTER,
                                size = 18.dp
                            )
                        }

                        // Banner badge (if not a special badge holder)
                        if (!entry.isDevBadgeHolder && !entry.isBetaTester && entry.bannerId != "default_dawn") {
                            Spacer(modifier = Modifier.width(6.dp))
                            val banner = ProdyBanners.findById(entry.bannerId)
                            if (banner != null) {
                                ProdyBannerBadge(
                                    banner = banner,
                                    size = 16.dp
                                )
                            }
                        }
                    }

                    // Equipped title (shown subtly below name)
                    val title = ProdyTitles.findById(entry.titleId)
                    if (title != null) {
                        Text(
                            text = title.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Points
                        Text(
                            text = formatPoints(entry.totalPoints),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Streak indicator for notable streaks
                        if (entry.currentStreak >= 7) {
                            Spacer(modifier = Modifier.width(8.dp))
                            ProdyStreakMilestoneIndicator(streakDays = entry.currentStreak)
                        }

                        // Boost count
                        if (entry.boostsReceived > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            ProdyBoostCounter(boostCount = entry.boostsReceived)
                        }
                    }
                }

                // Rank change indicator
                RankChangeIndicator(
                    currentRank = entry.rank,
                    previousRank = entry.previousRank
                )

                // Support button
                if (showSupportButton && !entry.isCurrentUser) {
                    Spacer(modifier = Modifier.width(ProdyTokens.Spacing.xs))
                    ProdySupportIconButton(
                        onSupportClick = onSupportClick,
                        hasSupported = hasUserSupportedToday
                    )
                }
            }
        }
    }
}

@Composable
private fun RankChangeIndicator(
    currentRank: Int,
    previousRank: Int
) {
    val change = previousRank - currentRank

    when {
        change > 0 -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = ProdyTokens.Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Rank up",
                    modifier = Modifier.size(14.dp),
                    tint = ProdySuccess
                )
                Text(
                    text = "+$change",
                    style = MaterialTheme.typography.labelSmall,
                    color = ProdySuccess,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        change < 0 -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = ProdyTokens.Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingDown,
                    contentDescription = "Rank down",
                    modifier = Modifier.size(14.dp),
                    tint = ProdyError
                )
                Text(
                    text = "$change",
                    style = MaterialTheme.typography.labelSmall,
                    color = ProdyError,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        else -> {
            // No change - show nothing for cleaner UI
            Spacer(modifier = Modifier.width(32.dp))
        }
    }
}

private fun formatPoints(points: Int): String {
    return when {
        points >= 1000000 -> "${points / 1000000}M pts"
        points >= 1000 -> "${points / 1000}k pts"
        else -> "$points pts"
    }
}

/**
 * Compact leaderboard row for smaller displays.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProdyCompactLeaderboardRow(
    entry: LeaderboardEntryEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ),
        shape = RoundedCornerShape(ProdyTokens.Radius.sm),
        color = if (entry.isCurrentUser)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        else
            MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProdyTokens.Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = "#${entry.rank}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = when (entry.rank) {
                    1 -> LeaderboardGold
                    2 -> LeaderboardSilver
                    3 -> LeaderboardBronze
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.width(32.dp)
            )

            // Name
            Text(
                text = entry.displayName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Points
            Text(
                text = formatPoints(entry.totalPoints),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Podium display for top 3 leaderboard entries.
 */
@Composable
fun ProdyLeaderboardPodium(
    first: LeaderboardEntryEntity?,
    second: LeaderboardEntryEntity?,
    third: LeaderboardEntryEntity?,
    onUserClick: (LeaderboardEntryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd place (left)
        second?.let {
            PodiumPlace(
                entry = it,
                place = 2,
                height = 100.dp,
                color = LeaderboardSilver,
                onClick = { onUserClick(it) }
            )
        }

        // 1st place (center)
        first?.let {
            PodiumPlace(
                entry = it,
                place = 1,
                height = 120.dp,
                color = LeaderboardGold,
                onClick = { onUserClick(it) }
            )
        }

        // 3rd place (right)
        third?.let {
            PodiumPlace(
                entry = it,
                place = 3,
                height = 80.dp,
                color = LeaderboardBronze,
                onClick = { onUserClick(it) }
            )
        }
    }
}

@Composable
private fun PodiumPlace(
    entry: LeaderboardEntryEntity,
    place: Int,
    height: androidx.compose.ui.unit.Dp,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .combinedClickable(onClick = onClick, onLongClick = {})
    ) {
        // Avatar
        ProdyRarityFrame(
            rarity = entry.profileFrameRarity,
            size = if (place == 1) 64.dp else 52.dp
        ) {
            Box(
                modifier = Modifier
                    .size(if (place == 1) 64.dp else 52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(if (place == 1) 32.dp else 24.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Name
        Text(
            text = entry.displayName.split(" ").firstOrNull() ?: entry.displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Podium stand with flowing animation
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(height)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        ) {
            // Base gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(color, color.copy(alpha = 0.7f))
                        )
                    )
            )

            // Flowing wave animation overlay
            FlowingPodiumBanner(
                place = place,
                modifier = Modifier.fillMaxSize()
            )

            // Content on top
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = place.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (place == 2) Color.DarkGray else Color.White
                )
                Text(
                    text = formatPoints(entry.totalPoints),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (place == 2) Color.DarkGray.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

/**
 * Flowing animated overlay for podium stands.
 * Creates a premium shimmer effect on the podium blocks.
 */
@Composable
private fun FlowingPodiumBanner(
    place: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "podium_shimmer")

    // Vertical shimmer animation
    val shimmerPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "podium_shimmer_phase"
    )

    // Glow pulse animation
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "podium_glow_pulse"
    )

    val glowColor = when (place) {
        1 -> LeaderboardRankColors.GoldGlow
        2 -> LeaderboardRankColors.SilverGlow
        3 -> LeaderboardRankColors.BronzeGlow
        else -> Color.Transparent
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Vertical shimmer sweep
        val shimmerY = height * shimmerPhase
        val shimmerHeight = height * 0.4f

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = glowPulse),
                    Color.Transparent
                ),
                startY = shimmerY - shimmerHeight / 2,
                endY = shimmerY + shimmerHeight / 2
            )
        )

        // Subtle edge glow
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    glowColor.copy(alpha = 0.3f),
                    Color.Transparent,
                    Color.Transparent,
                    glowColor.copy(alpha = 0.3f)
                )
            )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.combinedClickable(
    onClick: () -> Unit,
    onLongClick: () -> Unit
): Modifier = this.then(
    Modifier.combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick
    )
)
