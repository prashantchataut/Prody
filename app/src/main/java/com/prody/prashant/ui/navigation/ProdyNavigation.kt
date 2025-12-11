package com.prody.prashant.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.prody.prashant.ui.screens.futuremessage.FutureMessageScreen
import com.prody.prashant.ui.screens.futuremessage.WriteMessageScreen
import com.prody.prashant.ui.screens.home.HomeScreen
import com.prody.prashant.ui.screens.journal.JournalDetailScreen
import com.prody.prashant.ui.screens.journal.JournalScreen
import com.prody.prashant.ui.screens.journal.NewJournalEntryScreen
import com.prody.prashant.ui.screens.onboarding.OnboardingScreen
import com.prody.prashant.ui.screens.profile.ProfileScreen
import com.prody.prashant.ui.screens.profile.SettingsScreen
import com.prody.prashant.ui.screens.stats.StatsScreen
import com.prody.prashant.ui.screens.vocabulary.VocabularyDetailScreen
import com.prody.prashant.ui.screens.vocabulary.VocabularyListScreen
import com.prody.prashant.ui.screens.quotes.QuotesScreen

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
}

@Composable
fun ProdyNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { 300 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { -300 },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { -300 },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { 300 },
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToVocabulary = {
                    navController.navigate(Screen.VocabularyList.route)
                },
                onNavigateToQuotes = {
                    navController.navigate(Screen.Quotes.route)
                },
                onNavigateToJournal = {
                    navController.navigate(Screen.Journal.route)
                },
                onNavigateToFutureMessage = {
                    navController.navigate(Screen.FutureMessage.route)
                }
            )
        }

        composable(Screen.Journal.route) {
            JournalScreen(
                onNavigateToNewEntry = {
                    navController.navigate(Screen.NewJournalEntry.route)
                },
                onNavigateToDetail = { entryId ->
                    navController.navigate(Screen.JournalDetail.createRoute(entryId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.NewJournalEntry.route) {
            NewJournalEntryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEntrySaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.JournalDetail.route,
            arguments = listOf(
                navArgument("entryId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: return@composable
            JournalDetailScreen(
                entryId = entryId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.FutureMessage.route) {
            FutureMessageScreen(
                onNavigateToWrite = {
                    navController.navigate(Screen.WriteMessage.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.WriteMessage.route) {
            WriteMessageScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMessageSaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.VocabularyList.route) {
            VocabularyListScreen(
                onNavigateToDetail = { wordId ->
                    navController.navigate(Screen.VocabularyDetail.createRoute(wordId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.VocabularyDetail.route,
            arguments = listOf(
                navArgument("wordId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getLong("wordId") ?: return@composable
            VocabularyDetailScreen(
                wordId = wordId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Quotes.route) {
            QuotesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
