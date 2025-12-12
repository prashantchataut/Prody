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
 * - Localized label resource
 * - Selected and unselected icon states
 * - Content description for accessibility
 */
sealed class BottomNavItem(
    val route: String,
    @StringRes val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val contentDescriptionResId: Int = labelResId
) {
    /**
     * Home - Main dashboard and daily content
     */
    data object Home : BottomNavItem(
        route = Screen.Home.route,
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        contentDescriptionResId = R.string.nav_home
    )

    /**
     * Journal - Personal journal and reflections
     */
    data object Journal : BottomNavItem(
        route = Screen.JournalList.route,
        labelResId = R.string.nav_journal,
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book,
        contentDescriptionResId = R.string.nav_journal
    )

    /**
     * Stats - Progress and analytics
     */
    data object Stats : BottomNavItem(
        route = Screen.Stats.route,
        labelResId = R.string.nav_stats,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
        contentDescriptionResId = R.string.nav_stats
    )

    /**
     * Profile - User profile and settings
     */
    data object Profile : BottomNavItem(
        route = Screen.Profile.route,
        labelResId = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        contentDescriptionResId = R.string.nav_profile
    )

    companion object {
        /**
         * List of all bottom navigation items in display order.
         */
        val items = listOf(Home, Journal, Stats, Profile)

        /**
         * Routes that should show the bottom navigation bar.
         */
        val bottomBarRoutes = items.map { it.route }
    }
}
