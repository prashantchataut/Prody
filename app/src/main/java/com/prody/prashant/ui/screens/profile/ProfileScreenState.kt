package com.prody.prashant.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

data class ProfileScreenState(
    val displayName: String,
    val title: String,
    val bio: String,
    val isDev: Boolean,
    val isBetaPioneer: Boolean,
    val level: Int,
    val levelProgress: Float,
    val currentStreak: Int,
    val wordsLearned: Int
)

@Composable
fun rememberProfileScreenState(uiState: ProfileUiState): ProfileScreenState = remember(uiState) {
    ProfileScreenState(
        displayName = uiState.displayName,
        title = uiState.title,
        bio = uiState.bio,
        isDev = uiState.isDev,
        isBetaPioneer = uiState.isBetaPioneer,
        level = uiState.totalPoints / 1000 + 1,
        levelProgress = (uiState.totalPoints % 1000) / 1000f,
        currentStreak = uiState.currentStreak,
        wordsLearned = uiState.wordsLearned
    )
}
