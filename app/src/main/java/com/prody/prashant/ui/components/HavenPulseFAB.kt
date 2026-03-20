package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.navigation.BottomNavItem
import com.prody.prashant.ui.theme.HavenBubbleLight
import com.prody.prashant.ui.theme.HavenTextLight

/**
 * Haven Pulse FAB - High-performance Floating Action Button for Navigation
 *
 * This component isolates high-frequency animations (breathing pulse) to its own
 * scope and uses graphicsLayer for hardware-accelerated property updates.
 * This prevents the parent NavigationBar from recomposing on every frame.
 */
@Composable
fun HavenPulseFAB(
    selected: Boolean,
    item: BottomNavItem
) {
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
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
                this.alpha = if (selected) 1f else animatedAlpha
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
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = null,
            tint = HavenTextLight,
            modifier = Modifier.size(28.dp)
        )
    }
}
