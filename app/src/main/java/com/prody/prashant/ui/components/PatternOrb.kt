package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.*
import kotlin.math.*

/**
 * PatternOrb - 3D Sphere Visualization for Stats Page
 *
 * A premium, animated 3D-looking sphere that morphs its colors based on
 * AI-driven analysis of user patterns. Creates a mesmerizing focal point
 * that represents the user's mindfulness journey.
 *
 * Visual Effects:
 * - Multi-layered concentric circles creating depth illusion
 * - Dynamic color morphing based on user activity patterns
 * - Orbiting particle effects
 * - Subtle internal "flow" animation
 * - Glowing ambient effect
 *
 * Pattern Analysis Integration:
 * - Colors shift based on mood patterns
 * - Intensity reflects journaling frequency
 * - Orbital speed correlates with streak activity
 */

/**
 * Pattern data representing user's analyzed behavioral patterns.
 */
data class PatternAnalysis(
    val dominantMood: MoodPattern = MoodPattern.BALANCED,
    val journalingConsistency: Float = 0.5f, // 0.0 to 1.0
    val growthTrend: Float = 0.5f, // -1.0 (declining) to 1.0 (growing)
    val streakStrength: Float = 0.5f, // 0.0 to 1.0
    val activityLevel: Float = 0.5f // 0.0 to 1.0
)

/**
 * Mood patterns that influence orb coloring.
 */
enum class MoodPattern(
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val displayName: String
) {
    /** Calm, peaceful state */
    SERENE(
        primaryColor = MoodCalm,
        secondaryColor = Color(0xFF88D4E5),
        accentColor = MoodGrateful,
        displayName = "Serene"
    ),

    /** Energetic, motivated state */
    ENERGIZED(
        primaryColor = MoodMotivated,
        secondaryColor = MoodEnergetic,
        accentColor = MoodExcited,
        displayName = "Energized"
    ),

    /** Thoughtful, introspective state */
    REFLECTIVE(
        primaryColor = MoodConfused, // Lavender for reflection
        secondaryColor = MoodInspired,
        accentColor = Color(0xFF7E57C2),
        displayName = "Reflective"
    ),

    /** Balanced, centered state */
    BALANCED(
        primaryColor = ProdyAccentGreen,
        secondaryColor = ProdyAccentGreenLight,
        accentColor = LeaderboardGold,
        displayName = "Balanced"
    ),

    /** Growth-focused, ambitious state */
    ASCENDING(
        primaryColor = LeaderboardGold,
        secondaryColor = ProdyAccentGreen,
        accentColor = Color(0xFFFFE082),
        displayName = "Ascending"
    )
}

/**
 * Main Pattern Orb composable - the 3D sphere visualization.
 *
 * @param patternAnalysis User's analyzed patterns
 * @param size Size of the orb
 * @param showLabel Whether to show the pattern label below
 * @param modifier Modifier for the component
 */
@Composable
fun PatternOrb(
    patternAnalysis: PatternAnalysis = PatternAnalysis(),
    size: Dp = 180.dp,
    showLabel: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pattern_orb")

    // Primary rotation for the orb
    val primaryRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (20000 / (0.5f + patternAnalysis.activityLevel)).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "primary_rotation"
    )

    // Secondary rotation (counter)
    val secondaryRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "secondary_rotation"
    )

    // Color morphing phase
    val colorPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color_phase"
    )

    // Breathing pulse for depth
    val breathingPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Internal flow animation
    val flowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.toPx() / 2
                val centerY = size.toPx() / 2
                val baseRadius = minOf(centerX, centerY) * 0.8f

                // Draw the 3D orb
                draw3DOrb(
                    centerX = centerX,
                    centerY = centerY,
                    radius = baseRadius * breathingPulse,
                    pattern = patternAnalysis,
                    colorPhase = colorPhase,
                    flowOffset = flowOffset,
                    primaryRotation = primaryRotation,
                    secondaryRotation = secondaryRotation
                )
            }
        }

        // Pattern label
        if (showLabel) {
            Spacer(modifier = Modifier.height(12.dp))
            PatternLabel(
                pattern = patternAnalysis.dominantMood,
                consistency = patternAnalysis.journalingConsistency
            )
        }
    }
}

/**
 * Draws the multi-layered 3D orb effect.
 */
private fun DrawScope.draw3DOrb(
    centerX: Float,
    centerY: Float,
    radius: Float,
    pattern: PatternAnalysis,
    colorPhase: Float,
    flowOffset: Float,
    primaryRotation: Float,
    secondaryRotation: Float
) {
    val mood = pattern.dominantMood

    // Layer 1: Outer glow (ambient)
    drawAmbientGlow(
        centerX = centerX,
        centerY = centerY,
        radius = radius * 1.3f,
        color = mood.primaryColor,
        intensity = 0.3f + pattern.activityLevel * 0.3f
    )

    // Layer 2: Outer shell gradient (creates 3D depth)
    drawOrbShell(
        centerX = centerX,
        centerY = centerY,
        radius = radius,
        primaryColor = mood.primaryColor,
        secondaryColor = mood.secondaryColor,
        colorPhase = colorPhase
    )

    // Layer 3: Internal flow patterns
    drawInternalFlow(
        centerX = centerX,
        centerY = centerY,
        radius = radius * 0.85f,
        accentColor = mood.accentColor,
        flowOffset = flowOffset,
        rotation = primaryRotation
    )

    // Layer 4: Core highlight (gives 3D highlight effect)
    drawCoreHighlight(
        centerX = centerX,
        centerY = centerY,
        radius = radius,
        intensity = pattern.growthTrend * 0.5f + 0.5f
    )

    // Layer 5: Orbiting particles
    drawOrbitingParticles(
        centerX = centerX,
        centerY = centerY,
        radius = radius * 0.95f,
        rotation = secondaryRotation,
        particleCount = (6 + pattern.streakStrength * 6).toInt(),
        color = mood.accentColor
    )

    // Layer 6: Surface detail lines
    drawSurfaceDetails(
        centerX = centerX,
        centerY = centerY,
        radius = radius * 0.88f,
        rotation = primaryRotation,
        color = mood.secondaryColor.copy(alpha = 0.3f)
    )
}

/**
 * Draws the ambient glow around the orb.
 */
private fun DrawScope.drawAmbientGlow(
    centerX: Float,
    centerY: Float,
    radius: Float,
    color: Color,
    intensity: Float
) {
    val glowLayers = 4
    for (i in glowLayers downTo 1) {
        val layerRadius = radius * (1f + i * 0.08f)
        val alpha = intensity * (1f - i * 0.2f) * 0.3f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = alpha),
                    color.copy(alpha = alpha * 0.5f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = layerRadius
            ),
            radius = layerRadius,
            center = Offset(centerX, centerY)
        )
    }
}

/**
 * Draws the main orb shell with 3D gradient effect.
 */
private fun DrawScope.drawOrbShell(
    centerX: Float,
    centerY: Float,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    colorPhase: Float
) {
    // Dynamic color blend based on phase
    val blendFactor = (sin(colorPhase) + 1f) / 2f
    val blendedPrimary = lerp(primaryColor, secondaryColor, blendFactor * 0.3f)
    val blendedSecondary = lerp(secondaryColor, primaryColor, blendFactor * 0.3f)

    // Main sphere gradient (top-left highlight for 3D effect)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                blendedPrimary.copy(alpha = 0.9f),
                blendedSecondary.copy(alpha = 0.7f),
                blendedPrimary.copy(alpha = 0.5f)
            ),
            center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f),
            radius = radius * 1.5f
        ),
        radius = radius,
        center = Offset(centerX, centerY)
    )
}

/**
 * Draws internal flowing energy patterns.
 */
private fun DrawScope.drawInternalFlow(
    centerX: Float,
    centerY: Float,
    radius: Float,
    accentColor: Color,
    flowOffset: Float,
    rotation: Float
) {
    rotate(rotation, pivot = Offset(centerX, centerY)) {
        // Draw flowing bands
        val bandCount = 3
        for (i in 0 until bandCount) {
            val phase = flowOffset + i * (1f / bandCount)
            val adjustedPhase = phase % 1f

            val bandY = centerY + (adjustedPhase - 0.5f) * radius * 1.2f
            val bandWidth = radius * cos((adjustedPhase - 0.5f) * PI.toFloat()) * 0.7f

            if (bandWidth > 0) {
                drawCircle(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            accentColor.copy(alpha = 0.3f),
                            accentColor.copy(alpha = 0.5f),
                            accentColor.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        startX = centerX - bandWidth,
                        endX = centerX + bandWidth
                    ),
                    radius = 8.dp.toPx(),
                    center = Offset(centerX, bandY)
                )
            }
        }
    }
}

/**
 * Draws the core highlight for 3D depth.
 */
private fun DrawScope.drawCoreHighlight(
    centerX: Float,
    centerY: Float,
    radius: Float,
    intensity: Float
) {
    // Top-left specular highlight
    val highlightRadius = radius * 0.4f
    val highlightX = centerX - radius * 0.35f
    val highlightY = centerY - radius * 0.35f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.6f * intensity),
                Color.White.copy(alpha = 0.2f * intensity),
                Color.Transparent
            ),
            center = Offset(highlightX, highlightY),
            radius = highlightRadius
        ),
        radius = highlightRadius,
        center = Offset(highlightX, highlightY)
    )

    // Secondary smaller highlight
    val secondaryRadius = radius * 0.15f
    val secondaryX = centerX - radius * 0.2f
    val secondaryY = centerY - radius * 0.5f

    drawCircle(
        color = Color.White.copy(alpha = 0.4f * intensity),
        radius = secondaryRadius,
        center = Offset(secondaryX, secondaryY)
    )
}

/**
 * Draws particles orbiting the orb surface.
 */
private fun DrawScope.drawOrbitingParticles(
    centerX: Float,
    centerY: Float,
    radius: Float,
    rotation: Float,
    particleCount: Int,
    color: Color
) {
    val angleStep = 360f / particleCount

    for (i in 0 until particleCount) {
        val angle = (rotation + i * angleStep) * PI.toFloat() / 180f

        // Create varied orbit radii for depth
        val orbitRadius = radius * (0.95f + sin(angle * 2) * 0.1f)
        val particleX = centerX + cos(angle) * orbitRadius
        val particleY = centerY + sin(angle) * orbitRadius

        // Particle size varies based on position (larger when "in front")
        val depth = (sin(angle) + 1f) / 2f
        val particleSize = 3.dp.toPx() * (0.5f + depth * 0.5f)

        // Particle glow
        drawCircle(
            color = color.copy(alpha = 0.3f * depth),
            radius = particleSize * 2,
            center = Offset(particleX, particleY)
        )

        // Particle core
        drawCircle(
            color = color.copy(alpha = 0.7f + depth * 0.3f),
            radius = particleSize,
            center = Offset(particleX, particleY)
        )
    }
}

/**
 * Draws surface detail lines for texture.
 */
private fun DrawScope.drawSurfaceDetails(
    centerX: Float,
    centerY: Float,
    radius: Float,
    rotation: Float,
    color: Color
) {
    rotate(rotation * 0.5f, pivot = Offset(centerX, centerY)) {
        // Horizontal latitude lines
        val lineCount = 5
        for (i in 1 until lineCount) {
            val latitudeY = centerY + (i.toFloat() / lineCount - 0.5f) * radius * 1.6f
            val latitudeWidth = sqrt(
                maxOf(0f, radius * radius - (latitudeY - centerY).pow(2))
            )

            if (latitudeWidth > 0) {
                drawLine(
                    color = color,
                    start = Offset(centerX - latitudeWidth, latitudeY),
                    end = Offset(centerX + latitudeWidth, latitudeY),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

/**
 * Simple color interpolation.
 */
private fun lerp(start: Color, stop: Color, fraction: Float): Color {
    return Color(
        red = start.red + (stop.red - start.red) * fraction,
        green = start.green + (stop.green - start.green) * fraction,
        blue = start.blue + (stop.blue - start.blue) * fraction,
        alpha = start.alpha + (stop.alpha - start.alpha) * fraction
    )
}

/**
 * Pattern label showing the current mood state.
 */
@Composable
private fun PatternLabel(
    pattern: MoodPattern,
    consistency: Float
) {
    Surface(
        shape = RoundedCornerShape(ProdyTokens.Radius.full),
        color = pattern.primaryColor.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Colored dot indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(pattern.primaryColor)
            )

            Text(
                text = pattern.displayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = pattern.primaryColor
            )

            // Consistency indicator
            if (consistency > 0.7f) {
                Text(
                    text = "Strong",
                    style = MaterialTheme.typography.labelSmall,
                    color = pattern.secondaryColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Compact Pattern Orb for smaller displays.
 */
@Composable
fun CompactPatternOrb(
    patternAnalysis: PatternAnalysis = PatternAnalysis(),
    size: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    PatternOrb(
        patternAnalysis = patternAnalysis,
        size = size,
        showLabel = false,
        modifier = modifier
    )
}

/**
 * Pattern Orb Card - A card wrapper with the orb and pattern insights.
 */
@Composable
fun PatternOrbCard(
    patternAnalysis: PatternAnalysis = PatternAnalysis(),
    title: String = "Your Pattern",
    subtitle: String = "Based on your recent activity",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ProdyTokens.Radius.lg)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(ProdyTokens.Radius.lg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // The Pattern Orb
            PatternOrb(
                patternAnalysis = patternAnalysis,
                size = 160.dp,
                showLabel = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pattern insights row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PatternInsightItem(
                    label = "Consistency",
                    value = "${(patternAnalysis.journalingConsistency * 100).toInt()}%",
                    color = if (patternAnalysis.journalingConsistency > 0.7f)
                        ProdyAccentGreen else ProdyWarning
                )

                PatternInsightItem(
                    label = "Growth",
                    value = when {
                        patternAnalysis.growthTrend > 0.3f -> "Rising"
                        patternAnalysis.growthTrend < -0.3f -> "Declining"
                        else -> "Steady"
                    },
                    color = when {
                        patternAnalysis.growthTrend > 0.3f -> ProdyAccentGreen
                        patternAnalysis.growthTrend < -0.3f -> ProdyError
                        else -> LeaderboardGold
                    }
                )

                PatternInsightItem(
                    label = "Activity",
                    value = when {
                        patternAnalysis.activityLevel > 0.7f -> "High"
                        patternAnalysis.activityLevel < 0.3f -> "Low"
                        else -> "Medium"
                    },
                    color = when {
                        patternAnalysis.activityLevel > 0.7f -> ProdyAccentGreen
                        patternAnalysis.activityLevel < 0.3f -> ProdyWarning
                        else -> ProdyInfo
                    }
                )
            }
        }
    }
}

@Composable
private fun PatternInsightItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
