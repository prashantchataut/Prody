package com.prody.prashant.ui.main

import com.prody.prashant.ui.theme.ThemeMode

data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
