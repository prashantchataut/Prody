package com.prody.prashant.ui.components
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prody.prashant.domain.gamification.WisdomShield
import com.prody.prashant.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * WisdomShieldIndicator - Visual representation of streak protection
 *
 * Shows the current state of the user's Wisdom Shield:
 * - Active shield: Glowing green shield icon with particles
 * - Regenerating: Outlined shield with progress
 * - Unlocking: Progress toward earning first shield
 * - Used: Shield outline with "Protected!" message
 */

@Composable
fun WisdomShieldIndicator(
    shieldStatus: WisdomShield.ShieldStatus,
    currentStreak: Int,
    modifier: Modifier = Modifier,
    showDetails: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shield")

    // Glow pulse for active shield
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Rotation for particles
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Shield visual
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                shieldStatus.hasShield && shieldStatus.isActive -> {
                    // Active shield with glow
                    ActiveShieldVisual(
                        glowPulse = glowPulse,
                        rotation = rotation
                    )
                }
                shieldStatus.daysUntilRegeneration > 0 -> {
                    // Regenerating or unlocking
                    RegeneratingShieldVisual(
                        daysRemaining = shieldStatus.daysUntilRegeneration,
                        totalDays = WisdomShield.SHIELD_UNLOCK_STREAK,
                        isRegeneration = shieldStatus.shieldUsedDate != null
                    )
                }
                else -> {
                    // Inactive/none
                    InactiveShieldVisual()
                }
            }
        }

        if (showDetails) {
            Spacer(modifier = Modifier.height(8.dp))

            // Status text
            Text(
                text = getShieldStatusText(shieldStatus, currentStreak),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = getShieldStatusColor(shieldStatus),
                textAlign = TextAlign.Center
            )

            // Progress or info text
            Text(
                text = getShieldInfoText(shieldStatus, currentStreak),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ActiveShieldVisual(
    glowPulse: Float,
    rotation: Float
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2

            // Outer glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ProdyAccentGreen.copy(alpha = glowPulse * 0.4f),
                        ProdyAccentGreen.copy(alpha = glowPulse * 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Orbiting particles
            val particleCount = 6
            val angleStep = 360f / particleCount
            for (i in 0 until particleCount) {
                val angle = (rotation + i * angleStep) * PI.toFloat() / 180f
                val particleRadius = radius * 0.7f
                val particleX = center.x + cos(angle) * particleRadius
                val particleY = center.y + sin(angle) * particleRadius

                drawCircle(
                    color = ProdyAccentGreen.copy(alpha = glowPulse),
                    radius = 3.dp.toPx(),
                    center = Offset(particleX, particleY)
                )
            }
        }

        // Shield icon
        Icon(
            imageVector = ProdyIcons.Shield,
            contentDescription = "Wisdom Shield Active",
            modifier = Modifier.size(36.dp),
            tint = ProdyAccentGreen
        )
    }
}

@Composable
private fun RegeneratingShieldVisual(
    daysRemaining: Int,
    totalDays: Int,
    isRegeneration: Boolean
) {
    val progress = 1f - (daysRemaining.toFloat() / totalDays)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Progress arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 4.dp.toPx()

            // Background arc
            drawArc(
                color = ProdyAccentGreen.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )

            // Progress arc
            drawArc(
                color = if (isRegeneration) ProdyWarning else ProdyAccentGreen,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }

        // Shield icon
        Icon(
            imageVector = ProdyIcons.Outlined.Shield,
            contentDescription = if (isRegeneration) "Shield Regenerating" else "Shield Unlocking",
            modifier = Modifier
                .size(32.dp)
                .alpha(0.6f),
            tint = if (isRegeneration) ProdyWarning else ProdyAccentGreen.copy(alpha = 0.6f)
        )

        // Days counter
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$daysRemaining",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isRegeneration) ProdyWarning else ProdyAccentGreen
            )
        }
    }
}

@Composable
private fun InactiveShieldVisual() {
    Icon(
        imageVector = ProdyIcons.Outlined.Shield,
        contentDescription = "Shield Inactive",
        modifier = Modifier
            .size(36.dp)
            .alpha(0.3f),
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private fun getShieldStatusText(status: WisdomShield.ShieldStatus, streak: Int): String {
    return when {
        status.hasShield && status.isActive -> "Shield Active"
        status.shieldUsedDate != null && status.daysUntilRegeneration > 0 -> "Regenerating"
        streak < WisdomShield.SHIELD_UNLOCK_STREAK -> "Unlocking..."
        else -> "Shield Ready"
    }
}

private fun getShieldInfoText(status: WisdomShield.ShieldStatus, streak: Int): String {
    return when {
        status.hasShield && status.isActive -> "Protected from 1 missed day"
        status.shieldUsedDate != null && status.daysUntilRegeneration > 0 ->
            "${status.daysUntilRegeneration} days until restored"
        streak < WisdomShield.SHIELD_UNLOCK_STREAK ->
            "${WisdomShield.SHIELD_UNLOCK_STREAK - streak} more days to unlock"
        else -> "Maintain 7-day streak"
    }
}

@Composable
private fun getShieldStatusColor(status: WisdomShield.ShieldStatus): Color {
    return when {
        status.hasShield && status.isActive -> ProdyAccentGreen
        status.shieldUsedDate != null && status.daysUntilRegeneration > 0 -> ProdyWarning
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

/**
 * Compact shield indicator for streak counter.
 */
@Composable
fun CompactShieldIndicator(
    hasShield: Boolean,
    modifier: Modifier = Modifier
) {
    if (!hasShield) return

    val infiniteTransition = rememberInfiniteTransition(label = "compact_shield")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(ProdyAccentGreen.copy(alpha = glow * 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ProdyIcons.Shield,
            contentDescription = "Protected",
            modifier = Modifier.size(14.dp),
            tint = ProdyAccentGreen.copy(alpha = glow)
        )
    }
}

/**
 * Shield protected notification banner.
 * Shows when shield was just used to protect a streak.
 */
@Composable
fun ShieldProtectedBanner(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "protected")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ProdyTokens.Radius.md),
        color = ProdyAccentGreen.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    ProdyAccentGreen.copy(alpha = 0.3f),
                    ProdyAccentGreen.copy(alpha = 0.6f),
                    ProdyAccentGreen.copy(alpha = 0.3f)
                ),
                startX = shimmer * 500 - 100,
                endX = shimmer * 500 + 100
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shield icon with glow
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ProdyAccentGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ProdyIcons.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = ProdyAccentGreen
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Streak Protected!",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ProdyAccentGreen
                )
                Text(
                    text = "Your Wisdom Shield saved your streak",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(onClick = onDismiss) {
                Text("OK", color = ProdyAccentGreen)
            }
        }
    }
}
