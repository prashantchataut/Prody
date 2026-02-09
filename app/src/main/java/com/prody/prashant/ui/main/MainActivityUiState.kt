package com.prody.prashant.ui.main

import androidx.compose.runtime.Immutable
import com.prody.prashant.ui.theme.ThemeMode

@Immutable
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val hapticFeedbackEnabled: Boolean = true
)
