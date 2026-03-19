package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.HavenBubbleLight
import com.prody.prashant.ui.theme.HavenTextLight

/**
 * Haven Pulse FAB
 *
 * An optimized, specialized Floating Action Button for the Haven tab.
 *
 * Performance features:
 * - Uses Modifier.graphicsLayer to defer state reads (alpha, scale) to the drawing phase.
 * - This prevents the parent NavigationBar from recomposing on every frame of the breathing animation.
 */
@Composable
fun HavenPulseFAB(
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    // Breathing Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "HavenPulse")

    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HavenAlpha"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HavenScale"
    )

    Box(
        modifier = modifier
            .size(56.dp) // Larger than standard icon
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (selected) 1f else animatedAlpha
            }
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        HavenBubbleLight,
                        HavenBubbleLight.copy(alpha = 0.8f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HavenTextLight,
            modifier = Modifier.size(28.dp)
        )
    }
}
