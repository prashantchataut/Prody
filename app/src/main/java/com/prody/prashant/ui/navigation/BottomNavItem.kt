package com.prody.prashant.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.prody.prashant.R

/**
 * Bottom Navigation Items
 *
 * Defines the main navigation destinations accessible from the bottom navigation bar.
 * Each item includes:
 * - Route for navigation
 * - Localized label resource (used for both text and content description)
 * - Selected and unselected icon states
 */
sealed class BottomNavItem(
    val route: String,
    @StringRes val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    /**
     * Home - Main dashboard and daily content
     */
    data object Home : BottomNavItem(
        route = Screen.Home.route,
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    /**
     * Journal - Personal journal and reflections
     */
    data object Journal : BottomNavItem(
        route = Screen.JournalList.route,
        labelResId = R.string.nav_journal,
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    )

    /**
     * Stats - Progress and analytics
     */
    data object Stats : BottomNavItem(
        route = Screen.Stats.route,
        labelResId = R.string.nav_stats,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )

    /**
     * Profile - User profile and settings
     */
    data object Profile : BottomNavItem(
        route = Screen.Profile.route,
        labelResId = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    companion object {
        /**
         * List of all bottom navigation items in display order.
         */
        val items = listOf(Home, Journal, Stats, Profile)
    }
}
