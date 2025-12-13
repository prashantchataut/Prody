package com.prody.prashant.ui.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.prody.prashant.ui.screens.challenges.ChallengesScreen
import com.prody.prashant.ui.screens.futuremessage.FutureMessageListScreen
import com.prody.prashant.ui.screens.futuremessage.WriteMessageScreen
import com.prody.prashant.ui.screens.home.HomeScreen
import com.prody.prashant.ui.screens.journal.JournalDetailScreen
import com.prody.prashant.ui.screens.journal.JournalListScreen
import com.prody.prashant.ui.screens.journal.NewJournalEntryScreen
import com.prody.prashant.ui.screens.meditation.MeditationTimerScreen
import com.prody.prashant.ui.screens.onboarding.OnboardingScreen
import com.prody.prashant.ui.screens.profile.ProfileScreen
import com.prody.prashant.ui.screens.profile.SettingsScreen
import com.prody.prashant.ui.screens.quotes.QuotesScreen
import com.prody.prashant.ui.screens.stats.StatsScreen
import com.prody.prashant.ui.screens.vocabulary.VocabularyDetailScreen
import com.prody.prashant.ui.screens.vocabulary.VocabularyListScreen

/**
 * Prody Navigation - Screen Routes & Navigation Graph
 *
 * Defines all navigation destinations and transitions for the app.
 * Features:
 * - Smooth, polished screen transitions
 * - Proper navigation state management
 * - Type-safe route definitions
 */

// =============================================================================
// ANIMATION CONFIGURATION
// =============================================================================

// Custom easing for smooth, natural-feeling transitions
val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
val EaseInQuart = CubicBezierEasing(0.5f, 0f, 0.75f, 0f)

// Animation durations (in milliseconds)
const val TRANSITION_DURATION = 350
const val FADE_DURATION = 250

// Slide offset for horizontal transitions
const val SLIDE_OFFSET_FRACTION = 0.15f

// =============================================================================
// SCREEN ROUTE DEFINITIONS
// =============================================================================

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object JournalList : Screen("journal")
    data object NewJournalEntry : Screen("journal/new")
    data object JournalDetail : Screen("journal/{entryId}") {
        fun createRoute(entryId: Long) = "journal/$entryId"
    }
    data object FutureMessageList : Screen("future_message")
    data object WriteMessage : Screen("future_message/write")
    data object Stats : Screen("stats")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object VocabularyList : Screen("vocabulary")
    data object VocabularyDetail : Screen("vocabulary/{wordId}") {
        fun createRoute(wordId: Long) = "vocabulary/$wordId"
    }
    data object Quotes : Screen("quotes")
    data object Meditation : Screen("meditation")
    data object Challenges : Screen("challenges")
}

// =============================================================================
// NAVIGATION GRAPH BUILDER
// =============================================================================

/**
 * Extension function to build the navigation graph.
 * This approach centralizes the navigation logic.
 */
fun NavGraphBuilder.prodyNavGraph(navController: NavController) {
    // =====================================================================
    // ONBOARDING
    // =====================================================================
    composable(
        route = Screen.Onboarding.route,
        enterTransition = { fadeIn(tween(400)) },
        exitTransition = { fadeOut(tween(300)) }
    ) {
        OnboardingScreen(
            onComplete = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        )
    }

    // =====================================================================
    // HOME
    // =====================================================================
    composable(Screen.Home.route) {
        HomeScreen(
            onNavigateToVocabulary = {
                navController.navigate(Screen.VocabularyList.route)
            },
            onNavigateToQuotes = {
                navController.navigate(Screen.Quotes.route)
            },
            onNavigateToJournal = {
                navController.navigate(Screen.JournalList.route)
            },
            onNavigateToFutureMessage = {
                navController.navigate(Screen.FutureMessageList.route)
            },
            onNavigateToMeditation = {
                navController.navigate(Screen.Meditation.route)
            },
            onNavigateToChallenges = {
                navController.navigate(Screen.Challenges.route)
            }
        )
    }

    // =====================================================================
    // JOURNAL
    // =====================================================================
    composable(Screen.JournalList.route) {
        JournalListScreen(
            onNavigateToNewEntry = {
                navController.navigate(Screen.NewJournalEntry.route)
            },
            onNavigateToDetail = { entryId ->
                navController.navigate(Screen.JournalDetail.createRoute(entryId))
            }
        )
    }

    composable(
        route = Screen.NewJournalEntry.route,
        enterTransition = {
            fadeIn(tween(FADE_DURATION)) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(TRANSITION_DURATION, easing = EaseOutQuart)
            )
        },
        exitTransition = { fadeOut(tween(FADE_DURATION)) },
        popExitTransition = {
            fadeOut(tween(FADE_DURATION)) + slideOutVertically(
                targetOffsetY = { it / 4 },
                animationSpec = tween(TRANSITION_DURATION, easing = EaseInQuart)
            )
        }
    ) {
        NewJournalEntryScreen(
            onNavigateBack = { navController.popBackStack() },
            onEntrySaved = { navController.popBackStack() }
        )
    }

    composable(
        route = Screen.JournalDetail.route,
        arguments = listOf(navArgument("entryId") { type = NavType.LongType })
    ) { backStackEntry ->
        val entryId = backStackEntry.arguments?.getLong("entryId") ?: return@composable
        JournalDetailScreen(
            entryId = entryId,
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // =====================================================================
    // FUTURE MESSAGES
    // =====================================================================
    composable(Screen.FutureMessageList.route) {
        FutureMessageListScreen(
            onNavigateToWrite = {
                navController.navigate(Screen.WriteMessage.route)
            },
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(
        route = Screen.WriteMessage.route,
        enterTransition = {
            fadeIn(tween(FADE_DURATION)) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(TRANSITION_DURATION, easing = EaseOutQuart)
            )
        },
        popExitTransition = {
            fadeOut(tween(FADE_DURATION)) + slideOutVertically(
                targetOffsetY = { it / 4 },
                animationSpec = tween(TRANSITION_DURATION, easing = EaseInQuart)
            )
        }
    ) {
        WriteMessageScreen(
            onNavigateBack = { navController.popBackStack() },
            onMessageSaved = { navController.popBackStack() }
        )
    }

    // =====================================================================
    // STATS
    // =====================================================================
    composable(Screen.Stats.route) {
        StatsScreen()
    }

    // =====================================================================
    // PROFILE & SETTINGS
    // =====================================================================
    composable(Screen.Profile.route) {
        ProfileScreen(
            onNavigateToSettings = {
                navController.navigate(Screen.Settings.route)
            }
        )
    }

    composable(Screen.Settings.route) {
        SettingsScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // =====================================================================
    // VOCABULARY
    // =====================================================================
    composable(Screen.VocabularyList.route) {
        VocabularyListScreen(
            onNavigateToDetail = { wordId ->
                navController.navigate(Screen.VocabularyDetail.createRoute(wordId))
            },
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(
        route = Screen.VocabularyDetail.route,
        arguments = listOf(navArgument("wordId") { type = NavType.LongType })
    ) { backStackEntry ->
        val wordId = backStackEntry.arguments?.getLong("wordId") ?: return@composable
        VocabularyDetailScreen(
            wordId = wordId,
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // =====================================================================
    // QUOTES
    // =====================================================================
    composable(Screen.Quotes.route) {
        QuotesScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // =====================================================================
    // MEDITATION
    // =====================================================================
    composable(
        route = Screen.Meditation.route,
        enterTransition = {
            fadeIn(tween(500, easing = EaseOutQuart)) + scaleIn(
                initialScale = 0.95f,
                animationSpec = tween(500, easing = EaseOutQuart)
            )
        },
        exitTransition = { fadeOut(tween(300)) },
        popExitTransition = {
            fadeOut(tween(400)) + scaleOut(
                targetScale = 0.95f,
                animationSpec = tween(400, easing = EaseInQuart)
            )
        }
    ) {
        MeditationTimerScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // =====================================================================
    // CHALLENGES
    // =====================================================================
    composable(Screen.Challenges.route) {
        ChallengesScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
