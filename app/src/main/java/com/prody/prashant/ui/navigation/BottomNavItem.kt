package com.prody.prashant.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.prody.prashant.R
import com.prody.prashant.ui.icons.ProdyIcons

/**
 * Focused top-level navigation.
 *
 * Advanced and experimental features still have routes, but they are no longer
 * presented as equally important destinations. This keeps the primary product
 * loop understandable: see today's moment, learn, reflect, and revisit saved content.
 */
sealed class BottomNavItem(
    val route: String,
    val destinationRoute: String = route,
    @StringRes val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val contentDescriptionResId: Int = labelResId
) {
    data object Today : BottomNavItem(
        route = Screen.Home.route,
        labelResId = R.string.nav_today,
        selectedIcon = ProdyIcons.Home,
        unselectedIcon = ProdyIcons.Outlined.Home
    )

    data object Learn : BottomNavItem(
        route = Screen.VocabularyList.route,
        labelResId = R.string.nav_learn,
        selectedIcon = ProdyIcons.School,
        unselectedIcon = ProdyIcons.Outlined.School
    )

    data object Reflect : BottomNavItem(
        route = Screen.JournalList.route,
        labelResId = R.string.nav_reflect,
        selectedIcon = ProdyIcons.Book,
        unselectedIcon = ProdyIcons.Outlined.Book
    )

    data object Library : BottomNavItem(
        route = Screen.Quotes.createRoute("quotes"),
        destinationRoute = Screen.Quotes.route,
        labelResId = R.string.nav_library,
        selectedIcon = ProdyIcons.AutoStories,
        unselectedIcon = ProdyIcons.Outlined.AutoStories
    )

    companion object {
        val items: List<BottomNavItem> by lazy { listOf(Today, Learn, Reflect, Library) }
        val bottomBarRoutes: Set<String> by lazy { items.mapTo(mutableSetOf()) { it.destinationRoute } }
    }
}
