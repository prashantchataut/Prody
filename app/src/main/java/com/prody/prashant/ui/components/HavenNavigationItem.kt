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
 * Haven Navigation Item - Dedicated component for the Haven FAB pulse effect.
 *
 * Performance:
 * - Isolated animation state within this component to prevent recomposition of the parent NavigationBar.
 * - Used graphicsLayer lambda block for deferred state reads (alpha and scale), modifying the drawing phase only.
 * - Optimized state reads: Passing State objects directly into graphicsLayer to avoid even local recomposition.
 *
 * Design:
 * - Implements a "breathing" pulse effect for the Haven FAB in the bottom navigation.
 */
@Composable
fun RowScope.HavenNavigationItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    // PERFORMANCE: Use '=' instead of 'by' to capture the State object.
    // Reading .value inside the graphicsLayer lambda block defers the read to the drawing phase.
    val infiniteTransition = rememberInfiniteTransition(label = "HavenPulse")

    val alphaState = infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HavenAlpha"
    )

    val scaleState = infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HavenScale"
    )

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    // PERFORMANCE: State reads are deferred to the drawing phase.
                    // This prevents the component itself from recomposing on every animation frame.
                    .graphicsLayer {
                        scaleX = scaleState.value
                        scaleY = scaleState.value
                        alpha = if (selected) 1f else alphaState.value
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
            indicatorColor = Color.Transparent // Disable standard indicator
        )
    )
}
