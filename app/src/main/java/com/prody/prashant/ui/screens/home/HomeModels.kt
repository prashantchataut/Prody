package com.prody.prashant.ui.screens.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * High-performance UI model for badges.
 * Annotated with @Immutable to optimize recomposition cycles.
 */
@Immutable
data class BadgeData(
    val icon: ImageVector,
    val progress: Float,
    val color: Color
)
