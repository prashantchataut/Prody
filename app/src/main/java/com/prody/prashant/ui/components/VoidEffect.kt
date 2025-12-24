package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.*

/**
 * VoidEffect - Grayscale UI after 48hr no journaling
 *
 * A gentle psychological nudge that makes the app less visually appealing
 * when users haven't journaled in 48+ hours. The world "loses its color"
 * until they return to reflection.
 *
 * Design Philosophy:
 * - Not punitive, but a gentle visual reminder
 * - Easy to dismiss by journaling
 * - Preserves functionality - just reduces visual appeal
 * - Creates FOMO for the vibrant, colorful experience
 *
 * Implementation:
 * - ColorMatrix filter applied to content
 * - Gentle pulsing overlay
 * - Encouraging message to return to practice
 */

/**
 * Void state levels based on hours since last journal
 */
enum class VoidLevel(
    val saturation: Float,
    val message: String,
    val intensity: Float
) {
    /** Normal state - full color */
    NONE(saturation = 1f, message = "", intensity = 0f),

    /** 48-72 hours - subtle desaturation */
    CREEPING(
        saturation = 0.6f,
        message = "The world seems... quieter without your reflections.",
        intensity = 0.3f
    ),

    /** 72-96 hours - noticeable grayscale */
    SETTLING(
        saturation = 0.35f,
        message = "Colors fade when we stop looking inward.",
        intensity = 0.6f
    ),

    /** 96+ hours - full void effect */
    DEEP(
        saturation = 0.15f,
        message = "The void awaits your return to reflection.",
        intensity = 0.9f
    );

    companion object {
        fun fromHoursSinceJournal(hours: Long): VoidLevel {
            return when {
                hours < 48 -> NONE
                hours < 72 -> CREEPING
                hours < 96 -> SETTLING
                else -> DEEP
            }
        }
    }
}

/**
 * Applies the Void Effect grayscale filter to content.
 * Wrap screen content with this to enable the effect.
 *
 * @param hoursSinceLastJournal Hours since user last journaled
 * @param onJournalClick Callback when user clicks the journal prompt
 * @param content The content to apply the effect to
 */
@Composable
fun VoidEffectContainer(
    hoursSinceLastJournal: Long,
    onJournalClick: () -> Unit,
    modifier: Modifier = Modifier,
    showPrompt: Boolean = true,
    content: @Composable () -> Unit
) {
    val voidLevel = VoidLevel.fromHoursSinceJournal(hoursSinceLastJournal)

    // Animated saturation for smooth transitions
    val animatedSaturation by animateFloatAsState(
        targetValue = voidLevel.saturation,
        animationSpec = tween(durationMillis = 1500, easing = EaseInOutCubic),
        label = "saturation"
    )

    // Color matrix for grayscale effect
    val colorMatrix = remember(animatedSaturation) {
        ColorMatrix().apply {
            setToSaturation(animatedSaturation)
        }
    }

    Box(modifier = modifier) {
        // Content with grayscale filter
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    // Apply color filter overlay
                    if (animatedSaturation < 1f) {
                        drawRect(
                            color = Color.Gray.copy(alpha = (1f - animatedSaturation) * 0.15f)
                        )
                    }
                }
                .graphicsLayer {
                    if (animatedSaturation < 1f) {
                        colorFilter = ColorFilter.colorMatrix(colorMatrix)
                    }
                }
        ) {
            content()
        }

        // Void prompt overlay
        if (showPrompt && voidLevel != VoidLevel.NONE) {
            VoidPromptOverlay(
                voidLevel = voidLevel,
                onJournalClick = onJournalClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Overlay prompt encouraging user to return to journaling.
 */
@Composable
private fun VoidPromptOverlay(
    voidLevel: VoidLevel,
    onJournalClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "void_pulse")

    // Gentle pulsing for the prompt
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .alpha(pulse),
        shape = RoundedCornerShape(ProdyTokens.Radius.lg),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onJournalClick)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = ProdyAccentGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = ProdyAccentGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Message
            Text(
                text = voidLevel.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CTA Button
            Button(
                onClick = onJournalClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProdyAccentGreen,
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Return to Reflection",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Utility modifier to apply grayscale effect to any composable.
 */
fun Modifier.voidGrayscale(hoursSinceLastJournal: Long): Modifier {
    val voidLevel = VoidLevel.fromHoursSinceJournal(hoursSinceLastJournal)
    if (voidLevel == VoidLevel.NONE) return this

    val colorMatrix = ColorMatrix().apply {
        setToSaturation(voidLevel.saturation)
    }

    return this.graphicsLayer {
        colorFilter = ColorFilter.colorMatrix(colorMatrix)
    }
}

/**
 * Small indicator showing void effect status.
 */
@Composable
fun VoidStatusIndicator(
    hoursSinceLastJournal: Long,
    modifier: Modifier = Modifier
) {
    val voidLevel = VoidLevel.fromHoursSinceJournal(hoursSinceLastJournal)
    if (voidLevel == VoidLevel.NONE) return

    val infiniteTransition = rememberInfiniteTransition(label = "void_indicator")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ProdyTokens.Radius.full),
        color = when (voidLevel) {
            VoidLevel.CREEPING -> ProdyWarning.copy(alpha = alpha * 0.2f)
            VoidLevel.SETTLING -> ProdyError.copy(alpha = alpha * 0.2f)
            VoidLevel.DEEP -> ProdyError.copy(alpha = alpha * 0.3f)
            else -> Color.Transparent
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = when (voidLevel) {
                            VoidLevel.CREEPING -> ProdyWarning
                            VoidLevel.SETTLING -> ProdyError.copy(alpha = 0.8f)
                            VoidLevel.DEEP -> ProdyError
                            else -> Color.Transparent
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
            )
            Text(
                text = when (voidLevel) {
                    VoidLevel.CREEPING -> "Colors fading..."
                    VoidLevel.SETTLING -> "Void settling"
                    VoidLevel.DEEP -> "Deep void"
                    else -> ""
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = when (voidLevel) {
                    VoidLevel.CREEPING -> ProdyWarning
                    VoidLevel.SETTLING -> ProdyError.copy(alpha = 0.9f)
                    VoidLevel.DEEP -> ProdyError
                    else -> Color.Transparent
                }
            )
        }
    }
}

/**
 * Restoration celebration when user journals after being in void.
 */
@Composable
fun VoidRestorationCelebration(
    previousVoidLevel: VoidLevel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (previousVoidLevel == VoidLevel.NONE) return

    val infiniteTransition = rememberInfiniteTransition(label = "restoration")
    val colorPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color_pulse"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ProdyTokens.Radius.lg),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ProdyAccentGreen.copy(alpha = 0.1f + colorPulse * 0.1f),
                            ProdyAccentGreenLight.copy(alpha = 0.15f + colorPulse * 0.1f),
                            ProdyAccentGreen.copy(alpha = 0.1f + colorPulse * 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(ProdyTokens.Radius.lg)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Colors Restored",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyAccentGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when (previousVoidLevel) {
                        VoidLevel.DEEP -> "Welcome back from the deep void. Your reflection has restored light to your journey."
                        VoidLevel.SETTLING -> "The world brightens with your return to reflection."
                        VoidLevel.CREEPING -> "Colors flow back as you reconnect with your thoughts."
                        else -> "Your practice continues."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onDismiss) {
                    Text("Continue", color = ProdyAccentGreen)
                }
            }
        }
    }
}
