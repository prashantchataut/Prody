package com.prody.prashant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.notification.NotificationReceiver
import com.prody.prashant.notification.NotificationScheduler
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
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, schedule notifications
            lifecycleScope.launch(Dispatchers.IO) {
                notificationScheduler.rescheduleAllNotifications()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Create notification channels
        NotificationReceiver.createNotificationChannels(this)

        // Check and request notification permission for Android 13+
        requestNotificationPermission()

        enableEdgeToEdge()

        // Use mutable state to update UI when preferences are loaded
        var isOnboardingCompleted by mutableStateOf<Boolean?>(null)
        var initialThemeMode by mutableStateOf("system")

        // Keep splash screen visible until preferences are loaded
        splashScreen.setKeepOnScreenCondition { isOnboardingCompleted == null }

        // Load preferences asynchronously to avoid blocking main thread
        lifecycleScope.launch {
            try {
                val onboardingResult = withContext(Dispatchers.IO) {
                    preferencesManager.onboardingCompleted.first()
                }
                val themeResult = withContext(Dispatchers.IO) {
                    preferencesManager.themeMode.first()
                }
                initialThemeMode = themeResult
                isOnboardingCompleted = onboardingResult
            } catch (e: Exception) {
                // Default values if preferences can't be read
                initialThemeMode = "system"
                isOnboardingCompleted = false
            }
        }

        setContent {
            // Wait until preferences are loaded
            val onboardingDone = isOnboardingCompleted
            if (onboardingDone == null) {
                // Show nothing while loading (splash screen is still visible)
                return@setContent
            }

            val currentThemeModeString by preferencesManager.themeMode.collectAsStateWithLifecycle(
                initialValue = initialThemeMode
            )

            val themeModeState = when (currentThemeModeString.lowercase()) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }

            ProdyTheme(
                themeMode = themeModeState
            ) {
                ProdyApp(
                    startDestination = if (onboardingDone) Screen.Home.route else Screen.Onboarding.route
                )
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    lifecycleScope.launch(Dispatchers.IO) {
                        notificationScheduler.rescheduleAllNotifications()
                    }
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For older versions, schedule notifications directly
            lifecycleScope.launch(Dispatchers.IO) {
                notificationScheduler.rescheduleAllNotifications()
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

    // Bottom navigation items
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Journal,
        BottomNavItem.Stats,
        BottomNavItem.Profile
    )

    // Determine if bottom nav should be shown
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.JournalList.route,
        Screen.Stats.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(stringResource(item.labelResId)) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
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
            // Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // Home
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

            // Journal List
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

            // New Journal Entry
            composable(Screen.NewJournalEntry.route) {
                NewJournalEntryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onEntrySaved = { navController.popBackStack() }
                )
            }

            // Journal Detail
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

            // Stats
            composable(Screen.Stats.route) {
                StatsScreen()
            }

            // Profile
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Vocabulary List
            composable(Screen.VocabularyList.route) {
                VocabularyListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { wordId ->
                        navController.navigate(Screen.VocabularyDetail.createRoute(wordId))
                    }
                )
            }

            // Vocabulary Detail
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

            // Quotes
            composable(Screen.Quotes.route) {
                QuotesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Future Message List
            composable(Screen.FutureMessageList.route) {
                FutureMessageListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToWrite = {
                        navController.navigate(Screen.WriteMessage.route)
                    }
                )
            }

            // Write Message
            composable(Screen.WriteMessage.route) {
                WriteMessageScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onMessageSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
