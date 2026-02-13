package com.prody.prashant.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun ProfileOverviewSection(
    isVisible: Boolean,
    state: ProfileScreenState,
    isDarkMode: Boolean,
    textPrimary: Color,
    textTertiary: Color,
    accentColor: Color,
    surfaceColor: Color,
    onEditClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400)) + slideInVertically(
            initialOffsetY = { -it / 4 },
            animationSpec = tween(400, easing = EaseOutCubic)
        )
    ) {
        PremiumHeroSection(
            displayName = state.displayName,
            title = state.title,
            bio = state.bio,
            level = state.level,
            levelProgress = state.levelProgress,
            isDev = state.isDev,
            isBetaPioneer = state.isBetaPioneer,
            isDarkMode = isDarkMode,
            onEditClick = onEditClick,
            textPrimary = textPrimary,
            accentColor = accentColor
        )
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400, delayMillis = 100)) + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(400, delayMillis = 100, easing = EaseOutCubic)
        )
    ) {
        PremiumKeyMetricsRow(
            level = state.level,
            streak = state.currentStreak,
            wordsLearned = state.wordsLearned,
            surfaceColor = surfaceColor,
            textPrimary = textPrimary,
            textTertiary = textTertiary,
            accentColor = accentColor
        )
    }
}
