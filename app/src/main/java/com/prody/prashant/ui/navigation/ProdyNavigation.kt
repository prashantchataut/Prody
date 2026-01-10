package com.prody.prashant.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.prody.prashant.ui.screens.challenges.ChallengesScreen
import com.prody.prashant.ui.screens.futuremessage.FutureMessageListScreen
import com.prody.prashant.ui.screens.futuremessage.WriteMessageScreen
import com.prody.prashant.ui.screens.home.HomeScreen
import com.prody.prashant.ui.screens.idiom.IdiomDetailScreen
import com.prody.prashant.ui.screens.journal.JournalDetailScreen
import com.prody.prashant.ui.screens.journal.JournalHistoryScreen
import com.prody.prashant.ui.screens.journal.JournalListScreen
import com.prody.prashant.ui.screens.journal.NewJournalEntryScreen
import com.prody.prashant.ui.screens.meditation.MeditationTimerScreen
import com.prody.prashant.ui.screens.onboarding.OnboardingScreen
import com.prody.prashant.ui.screens.profile.AchievementsCollectionScreen
import com.prody.prashant.ui.screens.profile.BannerSelectionScreen
import com.prody.prashant.ui.screens.profile.EditProfileScreen
import com.prody.prashant.ui.screens.profile.ProfileScreen
import com.prody.prashant.ui.screens.profile.SettingsScreen
import com.prody.prashant.ui.screens.quotes.QuotesScreen
import com.prody.prashant.ui.screens.stats.StatsScreen
import com.prody.prashant.ui.screens.vocabulary.VocabularyDetailScreen
import com.prody.prashant.ui.screens.vocabulary.VocabularyListScreen
import com.prody.prashant.ui.screens.search.SearchScreen

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
private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private val EaseInQuart = CubicBezierEasing(0.5f, 0f, 0.75f, 0f)

// Animation durations (in milliseconds)
private const val TRANSITION_DURATION = 350
private const val FADE_DURATION = 250

// Slide offset for horizontal transitions
private const val SLIDE_OFFSET_FRACTION = 0.15f

// =============================================================================
// SCREEN ROUTE DEFINITIONS
// =============================================================================

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object JournalList : Screen("journal")
    data object JournalHistory : Screen("journal/history")
    data object NewJournalEntry : Screen("journal/new")
    data object JournalDetail : Screen("journal/{entryId}") {
        fun createRoute(entryId: Long) = "journal/$entryId"
    }
    data object FutureMessageList : Screen("future_message")
    data object WriteMessage : Screen("future_message/write")
    data object Stats : Screen("stats")
    data object Profile : Screen("profile")
    data object EditProfile : Screen("profile/edit")
    data object BannerSelection : Screen("profile/banner")
    data object AchievementsCollection : Screen("profile/achievements")
    data object Settings : Screen("settings")
    data object VocabularyList : Screen("vocabulary")
    data object VocabularyDetail : Screen("vocabulary/{wordId}") {
        fun createRoute(wordId: Long) = "vocabulary/$wordId"
    }
    data object IdiomDetail : Screen("idiom/{idiomId}") {
        fun createRoute(idiomId: Long) = "idiom/$idiomId"
    }
    data object Quotes : Screen("quotes")
    data object Meditation : Screen("meditation")
    data object Challenges : Screen("challenges")
    data object Search : Screen("search")
}

// =============================================================================
// NAVIGATION HOST
// =============================================================================

/**
 * Main navigation host with smooth transitions between screens.
 */
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
        // Enter transition - new screen sliding in from right
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = FADE_DURATION,
                    easing = EaseOutQuart
                )
            ) + slideInHorizontally(
                initialOffsetX = { (it * SLIDE_OFFSET_FRACTION).toInt() },
                animationSpec = tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = EaseOutQuart
                )
            )
        },
        // Exit transition - current screen fading out while staying mostly in place
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = FADE_DURATION,
                    easing = EaseInQuart
                )
            ) + slideOutHorizontally(
                targetOffsetX = { -(it * SLIDE_OFFSET_FRACTION * 0.3f).toInt() },
                animationSpec = tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = EaseInQuart
                )
            )
        },
        // Pop enter transition - returning screen sliding in from left
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = FADE_DURATION,
                    easing = EaseOutQuart
                )
            ) + slideInHorizontally(
                initialOffsetX = { -(it * SLIDE_OFFSET_FRACTION * 0.3f).toInt() },
                animationSpec = tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = EaseOutQuart
                )
            )
        },
        // Pop exit transition - current screen sliding out to right
        popExitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = FADE_DURATION,
                    easing = EaseInQuart
                )
            ) + slideOutHorizontally(
                targetOffsetX = { (it * SLIDE_OFFSET_FRACTION).toInt() },
                animationSpec = tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = EaseInQuart
                )
            )
        }
    ) {
        // =====================================================================
        // ONBOARDING
        // =====================================================================
        composable(
            route = Screen.Onboarding.route,
            // Special fade-only transition for onboarding
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
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToIdiomDetail = { idiomId ->
                    navController.navigate(Screen.IdiomDetail.createRoute(idiomId))
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
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.JournalHistory.route)
                }
            )
        }

        composable(Screen.JournalHistory.route) {
            JournalHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { entryId ->
                    navController.navigate(Screen.JournalDetail.createRoute(entryId))
                }
            )
        }

        composable(
            route = Screen.NewJournalEntry.route,
            // Slide up transition for creation screens
            enterTransition = {
                fadeIn(tween(FADE_DURATION)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseOutQuart)
                )
            },
            exitTransition = {
                fadeOut(tween(FADE_DURATION))
            },
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
            // Slide up for creation screens
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
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToAchievements = {
                    navController.navigate(Screen.AchievementsCollection.route)
                }
            )
        }

        composable(
            route = Screen.EditProfile.route,
            // Slide up for edit screens
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
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBannerSelection = {
                    navController.navigate(Screen.BannerSelection.route)
                }
            )
        }

        composable(Screen.BannerSelection.route) {
            BannerSelectionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AchievementsCollection.route) {
            AchievementsCollectionScreen(
                onNavigateBack = { navController.popBackStack() }
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
        // IDIOM DETAIL
        // =====================================================================
        composable(
            route = Screen.IdiomDetail.route,
            arguments = listOf(navArgument("idiomId") { type = NavType.LongType })
        ) { backStackEntry ->
            val idiomId = backStackEntry.arguments?.getLong("idiomId") ?: return@composable
            IdiomDetailScreen(
                idiomId = idiomId,
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
            // Special calm transition for meditation
            enterTransition = {
                fadeIn(tween(500, easing = EaseOutQuart)) + scaleIn(
                    initialScale = 0.95f,
                    animationSpec = tween(500, easing = EaseOutQuart)
                )
            },
            exitTransition = {
                fadeOut(tween(300))
            },
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

        // =====================================================================
        // GLOBAL SEARCH
        // =====================================================================
        composable(
            route = Screen.Search.route,
            // Fade transition for search (overlay-like)
            enterTransition = {
                fadeIn(tween(250))
            },
            exitTransition = {
                fadeOut(tween(200))
            },
            popEnterTransition = {
                fadeIn(tween(250))
            },
            popExitTransition = {
                fadeOut(tween(200))
            }
        ) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToJournalEntry = { entryId ->
                    navController.navigate(Screen.JournalDetail.createRoute(entryId))
                },
                onNavigateToQuote = { quoteId ->
                    // Navigate to quotes screen (quote detail not implemented separately)
                    navController.navigate(Screen.Quotes.route)
                },
                onNavigateToVocabulary = { wordId ->
                    navController.navigate(Screen.VocabularyDetail.createRoute(wordId))
                },
                onNavigateToFutureMessage = { messageId ->
                    // Navigate to future message list (detail not implemented separately)
                    navController.navigate(Screen.FutureMessageList.route)
                }
            )
        }
    }
}
