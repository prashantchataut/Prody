package com.prody.prashant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prody.prashant.notification.NotificationScheduler
import com.prody.prashant.ui.components.ProdyBottomNavBar
import com.prody.prashant.ui.main.MainViewModel
import com.prody.prashant.ui.navigation.BottomNavItem
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.screens.futuremessage.FutureMessageListScreen
import com.prody.prashant.ui.screens.futuremessage.WriteMessageScreen
import com.prody.prashant.ui.screens.home.HomeScreen
import com.prody.prashant.ui.screens.journal.JournalDetailScreen
import com.prody.prashant.ui.screens.journal.JournalListScreen
import com.prody.prashant.ui.screens.journal.NewJournalEntryScreen
import com.prody.prashant.ui.screens.onboarding.OnboardingScreen
import com.prody.prashant.ui.screens.profile.ProfileScreen
import com.prody.prashant.ui.screens.profile.SettingsScreen
import com.prody.prashant.ui.screens.quotes.QuotesScreen
import com.prody.prashant.ui.screens.stats.StatsScreen
import com.prody.prashant.ui.screens.vocabulary.VocabularyDetailScreen
import com.prody.prashant.ui.screens.vocabulary.VocabularyListScreen
import com.prody.prashant.ui.theme.ProdyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleNotificationsSafely()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.isLoading
        }

        requestNotificationPermission()
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            if (!uiState.isLoading) {
                ProdyTheme(themeMode = uiState.themeMode) {
                    ProdyApp(
                        startDestination = if (uiState.isOnboardingCompleted) Screen.Home.route else Screen.Onboarding.route
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    scheduleNotificationsSafely()
                }
            }
        } else {
            scheduleNotificationsSafely()
        }
    }

    private fun scheduleNotificationsSafely() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                notificationScheduler.rescheduleAllNotifications()
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Failed to schedule notifications", e)
            }
        }
    }
}

@Composable
fun ProdyApp(
    startDestination: String
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Journal,
        BottomNavItem.Stats,
        BottomNavItem.Profile
    )

    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(200)
                )
            ) {
                ProdyBottomNavBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                    initialOffsetX = { it / 4 },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                    targetOffsetX = { it / 4 },
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
                    onNavigateToJournal = {
                        navController.navigate(Screen.JournalList.route)
                    },
                    onNavigateToVocabulary = {
                        navController.navigate(Screen.VocabularyList.route)
                    },
                    onNavigateToQuotes = {
                        navController.navigate(Screen.Quotes.route)
                    },
                    onNavigateToFutureMessage = {
                        navController.navigate(Screen.FutureMessageList.route)
                    }
                )
            }
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
            composable(Screen.NewJournalEntry.route) {
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
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.VocabularyList.route) {
                VocabularyListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { wordId ->
                        navController.navigate(Screen.VocabularyDetail.createRoute(wordId))
                    }
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
            composable(Screen.Quotes.route) {
                QuotesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.FutureMessageList.route) {
                FutureMessageListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToWrite = {
                        navController.navigate(Screen.WriteMessage.route)
                    }
                )
            }
            composable(Screen.WriteMessage.route) {
                WriteMessageScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onMessageSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
