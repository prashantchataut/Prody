package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
 * Optimized Haven Navigation Item with high-frequency animations isolated.
 *
 * Performance Optimization: Using graphicsLayer { ... } with lambda assignments
 * (like alpha = value) within this function defers state reads to the drawing phase,
 * preventing unnecessary recompositions of the parent layout (like the NavigationBar).
 *
 * We use delegated properties sparingly or access state directly in the lambda
 * to ensure the composable itself doesn't recompose on every animation frame.
 */
@Composable
fun RowScope.HavenNavigationItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HavenPulse")

    // Using Animatable or State objects directly to avoid triggering recomposition in the parent scope
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
                    .graphicsLayer {
                        // Deferring state reads to the drawing phase by accessing .value inside the lambda
                        this.scaleX = scaleState.value
                        this.scaleY = scaleState.value
                        this.alpha = if (selected) 1f else alphaState.value
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
            indicatorColor = androidx.compose.ui.graphics.Color.Transparent
        )
    )
}
