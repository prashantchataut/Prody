package com.prody.prashant.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.prody.prashant.R

sealed class BottomNavItem(
    val route: String,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomNavItem(
        route = Screen.Home.route,
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Journal : BottomNavItem(
        route = Screen.JournalList.route,
        labelResId = R.string.nav_journal,
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    )

    data object Stats : BottomNavItem(
        route = Screen.Stats.route,
        labelResId = R.string.nav_stats,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )

    data object Profile : BottomNavItem(
        route = Screen.Profile.route,
        labelResId = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    companion object {
        val items = listOf(Home, Journal, Stats, Profile)
    }
}
