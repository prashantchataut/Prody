package com.prody.prashant.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import com.prody.prashant.ui.screens.quotes.WisdomTab
import com.prody.prashant.ui.screens.stats.StatsScreen
import com.prody.prashant.ui.screens.vocabulary.VocabularyDetailScreen
import com.prody.prashant.ui.screens.vocabulary.VocabularyListScreen
import com.prody.prashant.ui.screens.search.SearchScreen
import com.prody.prashant.ui.screens.wisdom.WisdomCollectionScreen
import com.prody.prashant.ui.screens.microjournal.MicroJournalScreen
import com.prody.prashant.ui.screens.ritual.DailyRitualScreen
import com.prody.prashant.ui.screens.digest.WeeklyDigestScreen
import com.prody.prashant.ui.screens.futuremessage.FutureMessageReplyScreen
import com.prody.prashant.ui.screens.futuremessage.TimeCapsuleRevealScreen
import com.prody.prashant.ui.screens.haven.HavenHomeScreen
import com.prody.prashant.ui.screens.haven.HavenChatScreen
import com.prody.prashant.ui.screens.haven.HavenExerciseScreen
import com.prody.prashant.domain.haven.SessionType
import com.prody.prashant.domain.haven.ExerciseType
import com.prody.prashant.ui.screens.learning.LearningHomeScreen
import com.prody.prashant.ui.screens.learning.PathDetailScreen
import com.prody.prashant.ui.screens.learning.LessonScreen
import com.prody.prashant.ui.screens.deepdive.DeepDiveHomeScreen
import com.prody.prashant.ui.screens.deepdive.DeepDiveSessionScreen
import com.prody.prashant.ui.screens.missions.MissionsScreen
import com.prody.prashant.ui.screens.collaborative.CollaborativeHomeScreen
import com.prody.prashant.ui.screens.collaborative.ComposeMessageScreen
import com.prody.prashant.ui.screens.collaborative.MessageDetailScreen

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
    data object Quotes : Screen("quotes/{tab}") {
        fun createRoute(tab: String = "quotes") = "quotes/$tab"
    }
    data object Meditation : Screen("meditation")
    data object Challenges : Screen("challenges")
    data object Search : Screen("search")
    data object IdiomDetail : Screen("idiom/{idiomId}") {
        fun createRoute(idiomId: Long) = "idiom/$idiomId"
    }

    // Daily Engagement Features
    data object WisdomCollection : Screen("wisdom_collection")
    data object MicroJournal : Screen("micro_journal")
    data object DailyRitual : Screen("daily_ritual")
    data object WeeklyDigest : Screen("weekly_digest")
    data object FutureMessageReply : Screen("future_message/reply/{messageId}") {
        fun createRoute(messageId: Long) = "future_message/reply/$messageId"
    }
    data object TimeCapsuleReveal : Screen("time_capsule/reveal/{messageId}") {
        fun createRoute(messageId: Long) = "time_capsule/reveal/$messageId"
    }

    // Haven - Therapeutic Support
    data object HavenHome : Screen("haven")
    data object HavenChat : Screen("haven/chat/{sessionType}?sessionId={sessionId}") {
        fun createRoute(sessionType: SessionType, sessionId: Long? = null): String {
            val base = "haven/chat/${sessionType.name}"
            return if (sessionId != null) "$base?sessionId=$sessionId" else base
        }
    }
    data object HavenExercise : Screen("haven/exercise/{exerciseType}") {
        fun createRoute(exerciseType: ExerciseType) = "haven/exercise/${exerciseType.name}"
    }

    // Learning Paths - Structured Growth Journeys
    data object LearningHome : Screen("learning")
    data object PathDetail : Screen("learning/path/{pathId}") {
        fun createRoute(pathId: String) = "learning/path/$pathId"
    }
    data object Lesson : Screen("learning/path/{pathId}/lesson/{lessonId}") {
        fun createRoute(pathId: String, lessonId: String) = "learning/path/$pathId/lesson/$lessonId"
    }

    // Deep Dive Days - Weekly themed reflection sessions
    data object DeepDiveHome : Screen("deep_dive")
    data object DeepDiveSession : Screen("deep_dive/session/{deepDiveId}") {
        fun createRoute(deepDiveId: Long) = "deep_dive/session/$deepDiveId"
    }

    // Missions - Daily missions & weekly trials
    data object Missions : Screen("missions")

    // Collaborative Messages - Send messages to contacts
    data object CollaborativeHome : Screen("collaborative")
    data object ComposeMessage : Screen("collaborative/compose?contactId={contactId}&occasion={occasion}") {
        fun createRoute(contactId: String? = null, occasion: String? = null): String {
            val params = mutableListOf<String>()
            contactId?.let { params.add("contactId=$it") }
            occasion?.let { params.add("occasion=$it") }
            return if (params.isEmpty()) {
                "collaborative/compose"
            } else {
                "collaborative/compose?${params.joinToString("&")}"
            }
        }
    }
    data object SentMessageDetail : Screen("collaborative/sent/{messageId}") {
        fun createRoute(messageId: String) = "collaborative/sent/$messageId"
    }
    data object ReceivedMessageDetail : Screen("collaborative/received/{messageId}") {
        fun createRoute(messageId: String) = "collaborative/received/$messageId"
    }
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
                    navController.navigate(Screen.Quotes.createRoute("quotes"))
                },
                onNavigateToIdioms = {
                    navController.navigate(Screen.Quotes.createRoute("idioms"))
                },
                onNavigateToProverbs = {
                    navController.navigate(Screen.Quotes.createRoute("proverbs"))
                },
                onNavigateToJournal = {
                    navController.navigate(Screen.JournalList.route)
                },
                onNavigateToFutureMessage = {
                    navController.navigate(Screen.FutureMessageList.route)
                },
                onNavigateToHaven = {
                    navController.navigate(Screen.HavenHome.route)
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
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
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
        // QUOTES / WISDOM COLLECTION
        // =====================================================================
        composable(
            route = Screen.Quotes.route,
            arguments = listOf(navArgument("tab") {
                type = NavType.StringType
                defaultValue = "quotes"
            })
        ) { backStackEntry ->
            val tabName = backStackEntry.arguments?.getString("tab") ?: "quotes"
            val initialTab = when (tabName.lowercase()) {
                "proverbs" -> WisdomTab.PROVERBS
                "idioms" -> WisdomTab.IDIOMS
                "phrases" -> WisdomTab.PHRASES
                else -> WisdomTab.QUOTES
            }
            QuotesScreen(
                onNavigateBack = { navController.popBackStack() },
                initialTab = initialTab
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
                    // Navigate to quotes screen with Quotes tab selected
                    navController.navigate(Screen.Quotes.createRoute("quotes"))
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

        // =====================================================================
        // DAILY ENGAGEMENT FEATURES
        // =====================================================================

        // Wisdom Collection - saved quotes, words, proverbs
        composable(Screen.WisdomCollection.route) {
            WisdomCollectionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Micro-Journaling - quick thought capture
        composable(Screen.MicroJournal.route) {
            MicroJournalScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToJournalWithContent = { content, microEntryId ->
                    // Navigate to journal with prefilled content
                    // The micro entry ID can be used to mark it as expanded after save
                    navController.navigate(Screen.NewJournalEntry.route)
                }
            )
        }

        // Daily Ritual - morning/evening check-ins
        composable(
            route = Screen.DailyRitual.route,
            // Special calming transition for ritual
            enterTransition = {
                fadeIn(tween(400, easing = EaseOutQuart)) + scaleIn(
                    initialScale = 0.96f,
                    animationSpec = tween(400, easing = EaseOutQuart)
                )
            },
            popExitTransition = {
                fadeOut(tween(300)) + scaleOut(
                    targetScale = 0.96f,
                    animationSpec = tween(300, easing = EaseInQuart)
                )
            }
        ) {
            DailyRitualScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToJournal = { prefilledContent ->
                    // Navigate to journal with prefilled content from ritual
                    navController.navigate(Screen.NewJournalEntry.route)
                }
            )
        }

        // Weekly Digest - weekly summaries and patterns
        composable(Screen.WeeklyDigest.route) {
            WeeklyDigestScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEntry = { entryId ->
                    navController.navigate(Screen.JournalDetail.createRoute(entryId))
                }
            )
        }

        // Future Message Reply - reply to past self
        composable(
            route = Screen.FutureMessageReply.route,
            arguments = listOf(navArgument("messageId") { type = NavType.LongType }),
            // Slide up for reply screen
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
        ) { backStackEntry ->
            val messageId = backStackEntry.arguments?.getLong("messageId") ?: return@composable
            FutureMessageReplyScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToJournal = { prefilledContent ->
                    navController.navigate(Screen.NewJournalEntry.route)
                }
            )
        }

        // Time Capsule Reveal - immersive message opening experience
        composable(
            route = Screen.TimeCapsuleReveal.route,
            arguments = listOf(navArgument("messageId") { type = NavType.LongType }),
            // Special magical reveal transition
            enterTransition = {
                fadeIn(tween(600, easing = EaseOutQuart)) + scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(600, easing = EaseOutQuart)
                )
            },
            exitTransition = {
                fadeOut(tween(300))
            },
            popExitTransition = {
                fadeOut(tween(400)) + scaleOut(
                    targetScale = 0.92f,
                    animationSpec = tween(400, easing = EaseInQuart)
                )
            }
        ) { backStackEntry ->
            val messageId = backStackEntry.arguments?.getLong("messageId") ?: return@composable
            TimeCapsuleRevealScreen(
                messageId = messageId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToJournal = { prefilledContent ->
                    navController.navigate(Screen.NewJournalEntry.route)
                }
            )
        }

        // =====================================================================
        // HAVEN - THERAPEUTIC SUPPORT
        // =====================================================================

        // Haven Home - Session type selection and overview
        composable(
            route = Screen.HavenHome.route,
            // Calming transition for Haven
            enterTransition = {
                fadeIn(tween(400, easing = EaseOutQuart)) + scaleIn(
                    initialScale = 0.96f,
                    animationSpec = tween(400, easing = EaseOutQuart)
                )
            },
            popExitTransition = {
                fadeOut(tween(300)) + scaleOut(
                    targetScale = 0.96f,
                    animationSpec = tween(300, easing = EaseInQuart)
                )
            }
        ) {
            HavenHomeScreen(
                onNavigateBack = { navController.popBackStack() },
                onStartSession = { sessionType ->
                    navController.navigate(Screen.HavenChat.createRoute(sessionType))
                },
                onResumeSession = { sessionId ->
                    navController.navigate(Screen.HavenChat.createRoute(SessionType.GENERAL, sessionId))
                },
                onNavigateToExercise = { exerciseType ->
                    navController.navigate(Screen.HavenExercise.createRoute(exerciseType))
                }
            )
        }

        // Haven Chat - Therapeutic conversation
        composable(
            route = Screen.HavenChat.route,
            arguments = listOf(
                navArgument("sessionType") { type = NavType.StringType },
                navArgument("sessionId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            ),
            // Slide up for chat
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
        ) { backStackEntry ->
            val sessionTypeStr = backStackEntry.arguments?.getString("sessionType") ?: return@composable
            val sessionType = try {
                SessionType.valueOf(sessionTypeStr)
            } catch (e: IllegalArgumentException) {
                SessionType.GENERAL
            }
            val sessionId = backStackEntry.arguments?.getLong("sessionId")?.takeIf { it != -1L }

            HavenChatScreen(
                sessionType = sessionType,
                sessionId = sessionId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToExercise = { exerciseType ->
                    navController.navigate(Screen.HavenExercise.createRoute(exerciseType))
                },
                onSessionComplete = {
                    navController.popBackStack(Screen.HavenHome.route, inclusive = false)
                }
            )
        }

        // Haven Exercise - Guided exercises
        composable(
            route = Screen.HavenExercise.route,
            arguments = listOf(navArgument("exerciseType") { type = NavType.StringType }),
            // Calm scale transition for exercises
            enterTransition = {
                fadeIn(tween(500, easing = EaseOutQuart)) + scaleIn(
                    initialScale = 0.95f,
                    animationSpec = tween(500, easing = EaseOutQuart)
                )
            },
            popExitTransition = {
                fadeOut(tween(400)) + scaleOut(
                    targetScale = 0.95f,
                    animationSpec = tween(400, easing = EaseInQuart)
                )
            }
        ) { backStackEntry ->
            val exerciseTypeStr = backStackEntry.arguments?.getString("exerciseType") ?: return@composable
            val exerciseType = try {
                ExerciseType.valueOf(exerciseTypeStr)
            } catch (e: IllegalArgumentException) {
                ExerciseType.BOX_BREATHING
            }

            HavenExerciseScreen(
                exerciseType = exerciseType,
                onNavigateBack = { navController.popBackStack() },
                onExerciseComplete = { navController.popBackStack() }
            )
        }

        // =====================================================================
        // LEARNING PATHS - STRUCTURED GROWTH JOURNEYS
        // =====================================================================

        // Learning Home - Dashboard with paths and progress
        composable(Screen.LearningHome.route) {
            LearningHomeScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPath = { pathId ->
                    navController.navigate(Screen.PathDetail.createRoute(pathId))
                },
                onNavigateToLesson = { pathId, lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(pathId, lessonId))
                }
            )
        }

        // Path Detail - Individual path with lessons
        composable(
            route = Screen.PathDetail.route,
            arguments = listOf(navArgument("pathId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pathId = backStackEntry.arguments?.getString("pathId") ?: return@composable
            PathDetailScreen(
                pathId = pathId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLesson = { pId, lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(pId, lessonId))
                }
            )
        }

        // Lesson - Individual lesson content
        composable(
            route = Screen.Lesson.route,
            arguments = listOf(
                navArgument("pathId") { type = NavType.StringType },
                navArgument("lessonId") { type = NavType.StringType }
            ),
            // Slide up transition for lesson content
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
        ) { backStackEntry ->
            val pathId = backStackEntry.arguments?.getString("pathId") ?: return@composable
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            LessonScreen(
                pathId = pathId,
                lessonId = lessonId,
                onNavigateBack = { navController.popBackStack() },
                onLessonComplete = { navController.popBackStack() }
            )
        }

        // =====================================================================
        // DEEP DIVE DAYS - WEEKLY THEMED REFLECTION SESSIONS
        // =====================================================================

        // Deep Dive Home - Dashboard with sessions and analytics
        composable(
            route = Screen.DeepDiveHome.route,
            // Calming transition for Deep Dive
            enterTransition = {
                fadeIn(tween(400, easing = EaseOutQuart)) + scaleIn(
                    initialScale = 0.96f,
                    animationSpec = tween(400, easing = EaseOutQuart)
                )
            },
            popExitTransition = {
                fadeOut(tween(300)) + scaleOut(
                    targetScale = 0.96f,
                    animationSpec = tween(300, easing = EaseInQuart)
                )
            }
        ) {
            DeepDiveHomeScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSession = { deepDiveId ->
                    navController.navigate(Screen.DeepDiveSession.createRoute(deepDiveId))
                }
            )
        }

        // Deep Dive Session - Guided reflection experience
        composable(
            route = Screen.DeepDiveSession.route,
            arguments = listOf(navArgument("deepDiveId") { type = NavType.LongType }),
            // Immersive transition for session
            enterTransition = {
                fadeIn(tween(500, easing = EaseOutQuart)) + scaleIn(
                    initialScale = 0.94f,
                    animationSpec = tween(500, easing = EaseOutQuart)
                )
            },
            exitTransition = {
                fadeOut(tween(300))
            },
            popExitTransition = {
                fadeOut(tween(400)) + scaleOut(
                    targetScale = 0.94f,
                    animationSpec = tween(400, easing = EaseInQuart)
                )
            }
        ) { backStackEntry ->
            val deepDiveId = backStackEntry.arguments?.getLong("deepDiveId") ?: return@composable
            DeepDiveSessionScreen(
                deepDiveId = deepDiveId,
                onNavigateBack = { navController.popBackStack() },
                onSessionComplete = { navController.popBackStack() }
            )
        }

        // =====================================================================
        // MISSIONS - DAILY MISSIONS & WEEKLY TRIALS
        // =====================================================================

        composable(Screen.Missions.route) {
            MissionsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToJournal = {
                    navController.navigate(Screen.NewJournalEntry.route)
                },
                onNavigateToVocabulary = {
                    navController.navigate(Screen.VocabularyList.route)
                },
                onNavigateToFutureMessage = {
                    navController.navigate(Screen.WriteMessage.route)
                }
            )
        }

        // =====================================================================
        // COLLABORATIVE MESSAGES - SEND MESSAGES TO CONTACTS
        // =====================================================================

        // Collaborative Messages Home - Inbox, Sent, Contacts tabs
        composable(
            route = Screen.CollaborativeHome.route,
            enterTransition = {
                fadeIn(tween(300, easing = EaseOutQuart)) + slideInHorizontally(
                    initialOffsetX = { it / 6 },
                    animationSpec = tween(300, easing = EaseOutQuart)
                )
            },
            popExitTransition = {
                fadeOut(tween(250)) + slideOutHorizontally(
                    targetOffsetX = { it / 6 },
                    animationSpec = tween(250, easing = EaseInQuart)
                )
            }
        ) {
            CollaborativeHomeScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCompose = { contact, occasion ->
                    navController.navigate(
                        Screen.ComposeMessage.createRoute(
                            contactId = contact?.id,
                            occasion = occasion?.name
                        )
                    )
                },
                onNavigateToSentDetail = { messageId ->
                    navController.navigate(Screen.SentMessageDetail.createRoute(messageId))
                },
                onNavigateToReceivedDetail = { messageId ->
                    navController.navigate(Screen.ReceivedMessageDetail.createRoute(messageId))
                }
            )
        }

        // Compose Message - Create and send messages
        composable(
            route = Screen.ComposeMessage.route,
            arguments = listOf(
                navArgument("contactId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("occasion") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = {
                fadeIn(tween(300)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(300, easing = EaseOutQuart)
                )
            },
            popExitTransition = {
                fadeOut(tween(250)) + slideOutVertically(
                    targetOffsetY = { it / 4 },
                    animationSpec = tween(250, easing = EaseInQuart)
                )
            }
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId")
            val occasion = backStackEntry.arguments?.getString("occasion")

            ComposeMessageScreen(
                preSelectedContactId = contactId,
                preSelectedOccasion = occasion,
                onNavigateBack = { navController.popBackStack() },
                onMessageSent = {
                    navController.popBackStack()
                }
            )
        }

        // Sent Message Detail
        composable(
            route = Screen.SentMessageDetail.route,
            arguments = listOf(
                navArgument("messageId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val messageId = backStackEntry.arguments?.getString("messageId") ?: return@composable

            MessageDetailScreen(
                messageId = messageId,
                isReceived = false,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Received Message Detail
        composable(
            route = Screen.ReceivedMessageDetail.route,
            arguments = listOf(
                navArgument("messageId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val messageId = backStackEntry.arguments?.getString("messageId") ?: return@composable

            MessageDetailScreen(
                messageId = messageId,
                isReceived = true,
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}
