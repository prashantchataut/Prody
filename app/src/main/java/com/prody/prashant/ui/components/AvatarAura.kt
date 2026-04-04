package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.domain.identity.ProdyRanks
import com.prody.prashant.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * AvatarAura - Dynamic glow effects based on user rank progression
 *
 * Creates premium visual effects around user avatars that reflect their journey:
 * - Seeker/Initiate: Subtle pulsing outline
 * - Student/Practitioner: Gentle breathing glow
 * - Contemplative/Philosopher: Flowing energy rings
 * - Sage/Luminary: Radiant particle effects
 * - Wayfinder/Awakened: Full aura with dynamic particles
 *
 * All animations use Compose animation APIs for smooth 60fps performance.
 */

/**
 * Aura tier definitions based on rank progression.
 * Each tier has distinct visual characteristics.
 */
enum class AuraTier(
    val displayName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val glowIntensity: Float,
    val particleCount: Int
) {
    /** Ranks: Seeker, Initiate */
    EMERGING(
        displayName = "Emerging",
        primaryColor = ProdyAccentGreen.copy(alpha = 0.4f),
        secondaryColor = ProdyAccentGreenLight.copy(alpha = 0.2f),
        glowIntensity = 0.3f,
        particleCount = 0
    ),
    /** Ranks: Student, Practitioner */
    GROWING(
        displayName = "Growing",
        primaryColor = ProdyAccentGreen.copy(alpha = 0.6f),
        secondaryColor = ProdyAccentGreenLight.copy(alpha = 0.4f),
        glowIntensity = 0.5f,
        particleCount = 4
    ),
    /** Ranks: Contemplative, Philosopher */
    RADIANT(
        displayName = "Radiant",
        primaryColor = ProdyAccentGreen,
        secondaryColor = LeaderboardGold.copy(alpha = 0.4f),
        glowIntensity = 0.7f,
        particleCount = 8
    ),
    /** Ranks: Sage, Luminary */
    ILLUMINATED(
        displayName = "Illuminated",
        primaryColor = LeaderboardGold,
        secondaryColor = ProdyAccentGreen,
        glowIntensity = 0.85f,
        particleCount = 12
    ),
    /** Ranks: Wayfinder, Awakened */
    TRANSCENDENT(
        displayName = "Transcendent",
        primaryColor = LeaderboardGold,
        secondaryColor = Color(0xFFFFD700),
        glowIntensity = 1.0f,
        particleCount = 16
    );

    companion object {
        fun fromRank(rank: ProdyRanks.Rank): AuraTier {
            return when (rank) {
                ProdyRanks.Rank.SEEKER, ProdyRanks.Rank.INITIATE -> EMERGING
                ProdyRanks.Rank.STUDENT, ProdyRanks.Rank.PRACTITIONER -> GROWING
                ProdyRanks.Rank.CONTEMPLATIVE, ProdyRanks.Rank.PHILOSOPHER -> RADIANT
                ProdyRanks.Rank.SAGE, ProdyRanks.Rank.LUMINARY -> ILLUMINATED
                ProdyRanks.Rank.WAYFINDER, ProdyRanks.Rank.AWAKENED -> TRANSCENDENT
            }
        }
    }
}

/**
 * Avatar with dynamic aura effect based on user rank.
 *
 * @param rank User's current rank
 * @param size Size of the avatar
 * @param content Content to display inside the avatar
 */
@Composable
fun ProdyAvatarWithAura(
    rank: ProdyRanks.Rank,
    size: Dp = 64.dp,
    showRankBadge: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val auraTier = AuraTier.fromRank(rank)
    val auraSize = size + 24.dp // Extra space for aura effect

    Box(
        modifier = Modifier.size(auraSize),
        contentAlignment = Alignment.Center
    ) {
        // Aura effect layer
        AuraEffect(
            tier = auraTier,
            size = auraSize,
            modifier = Modifier.matchParentSize()
        )

        // Avatar content
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
            content = content
        )

        // Rank badge
        if (showRankBadge) {
            RankBadge(
                rank = rank,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

/**
 * Animated aura effect surrounding the avatar.
 */
@Composable
private fun AuraEffect(
    tier: AuraTier,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura")

    // Breathing pulse animation
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Glow intensity animation
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = tier.glowIntensity * 0.7f,
        targetValue = tier.glowIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Rotation for particle orbits
    val particleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Secondary rotation (counter)
    val secondaryRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "secondary_rotation"
    )

    Canvas(modifier = modifier) {
        val centerX = size.toPx() / 2
        val centerY = size.toPx() / 2
        val baseRadius = size.toPx() / 2 - 8.dp.toPx()

        // Draw aura based on tier
        when (tier) {
            AuraTier.EMERGING -> {
                drawEmergingAura(
                    centerX = centerX,
                    centerY = centerY,
                    radius = baseRadius,
                    color = tier.primaryColor,
                    glowIntensity = glowPulse
                )
            }
            AuraTier.GROWING -> {
                drawGrowingAura(
                    centerX = centerX,
                    centerY = centerY,
                    radius = baseRadius,
                    primaryColor = tier.primaryColor,
                    secondaryColor = tier.secondaryColor,
                    breathingScale = breathingScale,
                    glowIntensity = glowPulse,
                    particleRotation = particleRotation,
                    particleCount = tier.particleCount
                )
            }
            AuraTier.RADIANT -> {
                drawRadiantAura(
                    centerX = centerX,
                    centerY = centerY,
                    radius = baseRadius,
                    primaryColor = tier.primaryColor,
                    secondaryColor = tier.secondaryColor,
                    breathingScale = breathingScale,
                    glowIntensity = glowPulse,
                    particleRotation = particleRotation,
                    secondaryRotation = secondaryRotation,
                    particleCount = tier.particleCount
                )
            }
            AuraTier.ILLUMINATED -> {
                drawIlluminatedAura(
                    centerX = centerX,
                    centerY = centerY,
                    radius = baseRadius,
                    primaryColor = tier.primaryColor,
                    secondaryColor = tier.secondaryColor,
                    breathingScale = breathingScale,
                    glowIntensity = glowPulse,
                    particleRotation = particleRotation,
                    secondaryRotation = secondaryRotation,
                    particleCount = tier.particleCount
                )
            }
            AuraTier.TRANSCENDENT -> {
                drawTranscendentAura(
                    centerX = centerX,
                    centerY = centerY,
                    radius = baseRadius,
                    primaryColor = tier.primaryColor,
                    secondaryColor = tier.secondaryColor,
                    breathingScale = breathingScale,
                    glowIntensity = glowPulse,
                    particleRotation = particleRotation,
                    secondaryRotation = secondaryRotation,
                    particleCount = tier.particleCount
                )
            }
        }
    }
}

private fun DrawScope.drawEmergingAura(
    centerX: Float,
    centerY: Float,
    radius: Float,
    color: Color,
    glowIntensity: Float
) {
    // Simple pulsing ring
    drawCircle(
        color = color.copy(alpha = glowIntensity * 0.5f),
        radius = radius + 4.dp.toPx(),
        center = Offset(centerX, centerY),
        style = Stroke(width = 2.dp.toPx())
    )

    // Subtle outer glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = glowIntensity * 0.3f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius + 10.dp.toPx()
        ),
        radius = radius + 10.dp.toPx(),
        center = Offset(centerX, centerY)
    )
}

private fun DrawScope.drawGrowingAura(
    centerX: Float,
    centerY: Float,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    breathingScale: Float,
    glowIntensity: Float,
    particleRotation: Float,
    particleCount: Int
) {
    // Breathing outer glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = glowIntensity * 0.4f),
                secondaryColor.copy(alpha = glowIntensity * 0.2f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius * breathingScale + 12.dp.toPx()
        ),
        radius = radius * breathingScale + 12.dp.toPx(),
        center = Offset(centerX, centerY)
    )

    // Inner ring
    drawCircle(
        color = primaryColor.copy(alpha = glowIntensity),
        radius = radius + 3.dp.toPx(),
        center = Offset(centerX, centerY),
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Orbiting particles
    drawOrbitingParticles(
        centerX = centerX,
        centerY = centerY,
        radius = radius + 8.dp.toPx(),
        rotation = particleRotation,
        particleCount = particleCount,
        particleColor = primaryColor,
        particleSize = 2.dp.toPx()
    )
}

private fun DrawScope.drawRadiantAura(
    centerX: Float,
    centerY: Float,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    breathingScale: Float,
    glowIntensity: Float,
    particleRotation: Float,
    secondaryRotation: Float,
    particleCount: Int
) {
    // Multi-layer glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = glowIntensity * 0.3f),
                secondaryColor.copy(alpha = glowIntensity * 0.2f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius * breathingScale + 16.dp.toPx()
        ),
        radius = radius * breathingScale + 16.dp.toPx(),
        center = Offset(centerX, centerY)
    )

    // Energy rings
    drawCircle(
        color = primaryColor.copy(alpha = glowIntensity * 0.6f),
        radius = radius + 4.dp.toPx(),
        center = Offset(centerX, centerY),
        style = Stroke(width = 2.dp.toPx())
    )

    drawCircle(
        color = secondaryColor.copy(alpha = glowIntensity * 0.4f),
        radius = radius + 8.dp.toPx(),
        center = Offset(centerX, centerY),
        style = Stroke(width = 1.dp.toPx())
    )

    // Primary orbit particles
    drawOrbitingParticles(
        centerX = centerX,
        centerY = centerY,
        radius = radius + 10.dp.toPx(),
        rotation = particleRotation,
        particleCount = particleCount,
        particleColor = primaryColor,
        particleSize = 2.5.dp.toPx()
    )

    // Secondary orbit (counter-rotation)
    drawOrbitingParticles(
        centerX = centerX,
        centerY = centerY,
        radius = radius + 6.dp.toPx(),
        rotation = secondaryRotation,
        particleCount = particleCount / 2,
        particleColor = secondaryColor,
        particleSize = 1.5.dp.toPx()
    )
}

private fun DrawScope.drawIlluminatedAura(
    centerX: Float,
    centerY: Float,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    breathingScale: Float,
    glowIntensity: Float,
    particleRotation: Float,
    secondaryRotation: Float,
    particleCount: Int
) {
    // Golden outer glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = glowIntensity * 0.35f),
                secondaryColor.copy(alpha = glowIntensity * 0.2f),
                primaryColor.copy(alpha = glowIntensity * 0.1f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius * breathingScale + 20.dp.toPx()
        ),
        radius = radius * breathingScale + 20.dp.toPx(),
        center = Offset(centerX, centerY)
    )

    // Light rays
    drawLightRays(
        centerX = centerX,
        centerY = centerY,
        radius = radius + 8.dp.toPx(),
        rayCount = 8,
        rayLength = 12.dp.toPx(),
        rotation = particleRotation / 2,
        color = primaryColor.copy(alpha = glowIntensity * 0.5f)
    )

    // Inner glow ring
    drawCircle(
        color = primaryColor.copy(alpha = glowIntensity * 0.8f),
        radius = radius + 3.dp.toPx(),
        center = Offset(centerX, centerY),
        style = Stroke(width = 2.5.dp.toPx())
    )

    // Primary particles
    drawOrbitingParticles(
        centerX = centerX,
        centerY = centerY,
        radius = radius + 12.dp.toPx(),
        rotation = particleRotation,
        particleCount = particleCount,
        particleColor = primaryColor,
        particleSize = 3.dp.toPx()
    )

    // Secondary particles
    drawOrbitingParticles(
        centerX = centerX,
        centerY = centerY,
        radius = radius + 7.dp.toPx(),
        rotation = secondaryRotation,
        particleCount = particleCount / 2,
        particleColor = secondaryColor,
        particleSize = 2.dp.toPx()
    )
}

private fun DrawScope.drawTranscendentAura(
    centerX: Float,
    centerY: Float,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    breathingScale: Float,
    glowIntensity: Float,
    particleRotation: Float,
    secondaryRotation: Float,
    particleCount: Int
) {
    // Transcendent outer glow with multiple layers
    listOf(24.dp, 18.dp, 12.dp).forEachIndexed { index, extraRadius ->
        val alpha = (1f - index * 0.25f) * glowIntensity * 0.3f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = alpha),
                    secondaryColor.copy(alpha = alpha * 0.6f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * breathingScale + extraRadius.toPx()
            ),
            radius = radius * breathingScale + extraRadius.toPx(),
            center = Offset(centerX, centerY)
        )
    }

    // Crown light rays
    drawLightRays(
        centerX = centerX,
        centerY = centerY,
        radius = radius + 6.dp.toPx(),
        rayCount = 12,
        rayLength = 16.dp.toPx(),
        rotation = particleRotation / 3,
        color = secondaryColor.copy(alpha = glowIntensity * 0.6f)
    )

    // Core energy ring
    drawCircle(
        brush = Brush.sweepGradient(
            colors = listOf(
                primaryColor,
                secondaryColor,
                primaryColor,
                secondaryColor,
                primaryColor
            ),
            center = Offset(centerX, centerY)
        ),
        radius = radius + 4.dp.toPx(),
        center = Offset(centerX, centerY),
        style = Stroke(width = 3.dp.toPx())
    )

    // Primary orbit - large particles
    drawOrbitingParticles(
        centerX = centerX,
        centerY = centerY,
        radius = radius + 14.dp.toPx(),
        rotation = particleRotation,
        particleCount = particleCount,
        particleColor = primaryColor,
        particleSize = 3.5.dp.toPx()
    )

    // Secondary orbit
    drawOrbitingParticles(
        centerX = centerX,
        centerY = centerY,
        radius = radius + 9.dp.toPx(),
        rotation = secondaryRotation,
        particleCount = particleCount * 2 / 3,
        particleColor = secondaryColor,
        particleSize = 2.5.dp.toPx()
    )

    // Inner orbit - small particles
    drawOrbitingParticles(
        centerX = centerX,
        centerY = centerY,
        radius = radius + 5.dp.toPx(),
        rotation = particleRotation * 1.5f,
        particleCount = particleCount / 2,
        particleColor = primaryColor.copy(alpha = 0.8f),
        particleSize = 1.5.dp.toPx()
    )
}

private fun DrawScope.drawOrbitingParticles(
    centerX: Float,
    centerY: Float,
    radius: Float,
    rotation: Float,
    particleCount: Int,
    particleColor: Color,
    particleSize: Float
) {
    val angleStep = 360f / particleCount
    for (i in 0 until particleCount) {
        val angle = (rotation + i * angleStep) * PI.toFloat() / 180f
        val particleX = centerX + cos(angle) * radius
        val particleY = centerY + sin(angle) * radius

        // Particle glow
        drawCircle(
            color = particleColor.copy(alpha = 0.3f),
            radius = particleSize * 2,
            center = Offset(particleX, particleY)
        )

        // Particle core
        drawCircle(
            color = particleColor,
            radius = particleSize,
            center = Offset(particleX, particleY)
        )
    }
}

private fun DrawScope.drawLightRays(
    centerX: Float,
    centerY: Float,
    radius: Float,
    rayCount: Int,
    rayLength: Float,
    rotation: Float,
    color: Color
) {
    val angleStep = 360f / rayCount
    for (i in 0 until rayCount) {
        val angle = (rotation + i * angleStep) * PI.toFloat() / 180f
        val startX = centerX + cos(angle) * radius
        val startY = centerY + sin(angle) * radius
        val endX = centerX + cos(angle) * (radius + rayLength)
        val endY = centerY + sin(angle) * (radius + rayLength)

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(color, Color.Transparent),
                start = Offset(startX, startY),
                end = Offset(endX, endY)
            ),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

/**
 * Small rank badge to display on avatar.
 */
@Composable
private fun RankBadge(
    rank: ProdyRanks.Rank,
    modifier: Modifier = Modifier
) {
    val tier = AuraTier.fromRank(rank)

    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(tier.primaryColor, tier.secondaryColor)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getRankInitial(rank),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

private fun getRankInitial(rank: ProdyRanks.Rank): String {
    return when (rank) {
        ProdyRanks.Rank.SEEKER -> "S"
        ProdyRanks.Rank.INITIATE -> "I"
        ProdyRanks.Rank.STUDENT -> "St"
        ProdyRanks.Rank.PRACTITIONER -> "P"
        ProdyRanks.Rank.CONTEMPLATIVE -> "C"
        ProdyRanks.Rank.PHILOSOPHER -> "Ph"
        ProdyRanks.Rank.SAGE -> "Sa"
        ProdyRanks.Rank.LUMINARY -> "L"
        ProdyRanks.Rank.WAYFINDER -> "W"
        ProdyRanks.Rank.AWAKENED -> "A"
    }
}

/**
 * Tiered display name with rank prefix.
 * Shows the user's name with their rank title in a premium format.
 */
@Composable
fun TieredDisplayName(
    displayName: String,
    rank: ProdyRanks.Rank,
    modifier: Modifier = Modifier,
    showFullTitle: Boolean = false
) {
    val tier = AuraTier.fromRank(rank)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Rank title
        Text(
            text = if (showFullTitle) rank.displayName else getRankInitial(rank),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = tier.primaryColor
        )

        // Name
        Text(
            text = displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
