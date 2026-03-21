package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.navigation.BottomNavItem
import com.prody.prashant.ui.theme.HavenBubbleLight
import com.prody.prashant.ui.theme.HavenTextLight

/**
 * Optimized Haven FAB Navigation Item.
 *
 * Uses [Modifier.graphicsLayer] to isolate high-frequency animations (breathing pulse)
 * from the rest of the NavigationBar, preventing unnecessary recompositions of the
 * parent Bottom Navigation.
 */
@Composable
fun RowScope.HavenNavigationItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            // Breathing Pulse Animation
            val infiniteTransition = rememberInfiniteTransition(label = "HavenPulse")

            val alphaValue = infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "HavenAlpha"
            )

            val scaleValue = infiniteTransition.animateFloat(
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
                        // DEFER state reads to the drawing phase to avoid recomposition
                        val scale = scaleValue.value
                        this.scaleX = scale
                        this.scaleY = scale
                        this.alpha = if (selected) 1f else alphaValue.value
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
        },
        label = { /* No label for FAB look */ },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}
