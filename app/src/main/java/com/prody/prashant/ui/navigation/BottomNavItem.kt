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
     * Note: Using string literal instead of Screen.Home.route to avoid static initialization
     * order dependency that can cause ExceptionInInitializerError on some devices.
     */
    data object Home : BottomNavItem(
        route = "home",
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        contentDescriptionResId = R.string.nav_home
    )

    /**
     * Journal - Personal journal and reflections
     * Note: Using string literal instead of Screen.JournalList.route to avoid static initialization
     * order dependency that can cause ExceptionInInitializerError on some devices.
     */
    data object Journal : BottomNavItem(
        route = "journal",
        labelResId = R.string.nav_journal,
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book,
        contentDescriptionResId = R.string.nav_journal
    )

    /**
     * Stats - Progress and analytics
     * Note: Using string literal instead of Screen.Stats.route to avoid static initialization
     * order dependency that can cause ExceptionInInitializerError on some devices.
     */
    data object Stats : BottomNavItem(
        route = "stats",
        labelResId = R.string.nav_stats,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
        contentDescriptionResId = R.string.nav_stats
    )

    /**
     * Profile - User profile and settings
     * Note: Using string literal instead of Screen.Profile.route to avoid static initialization
     * order dependency that can cause ExceptionInInitializerError on some devices.
     */
    data object Profile : BottomNavItem(
        route = "profile",
        labelResId = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        contentDescriptionResId = R.string.nav_profile
    )

    companion object {
        /**
         * List of all bottom navigation items in display order.
         *
         * Note: Using lazy initialization to avoid static initialization order issues
         * that can cause ExceptionInInitializerError/NullPointerException on some devices
         * when data objects are accessed before they are fully initialized.
         */
        val items: List<BottomNavItem> by lazy {
            listOf(Home, Journal, Stats, Profile)
        }

        /**
         * Routes that should show the bottom navigation bar.
         *
         * Note: Using lazy initialization to avoid static initialization order issues.
         */
        val bottomBarRoutes: List<String> by lazy {
            items.map { it.route }
        }
    }
}
