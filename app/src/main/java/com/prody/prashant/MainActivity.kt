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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.prody.prashant.ui.theme.PoppinsFamily
import com.prody.prashant.ui.theme.ProdyAccent
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

    // Flag to track if Hilt injection is complete
    private var isInjectionComplete = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted && isInjectionComplete) {
            // Permission granted, schedule notifications safely
            scheduleNotificationsSafely()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE calling super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Mark injection as complete after super.onCreate() for Hilt activities
        isInjectionComplete = true

        // Check and request notification permission for Android 13+ safely
        try {
            requestNotificationPermission()
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Failed to request notification permission", e)
        }

        enableEdgeToEdge()

        // Use mutable state to update UI when preferences are loaded
        var isOnboardingCompleted by mutableStateOf<Boolean?>(null)

        // Keep splash screen visible until the essential onboarding status is loaded.
        splashScreen.setKeepOnScreenCondition { isOnboardingCompleted == null }

        // Load ONLY the essential preference to unblock the splash screen.
        // The theme is loaded asynchronously in setContent and will update when ready.
        lifecycleScope.launch {
            try {
                isOnboardingCompleted = preferencesManager.onboardingCompleted.first()
            } catch (e: Exception) {
                // If preferences fail, default to showing onboarding.
                android.util.Log.e("MainActivity", "Failed to load onboarding status", e)
                isOnboardingCompleted = false
            }
        }

        setContent {
            // Wait until the onboarding status is loaded before showing the UI.
            val onboardingDone = isOnboardingCompleted
            if (onboardingDone == null) {
                // While this is null, the splash screen is kept visible.
                // We return here to prevent the UI from composing unnecessarily.
                return@setContent
            }

            // Asynchronously collect the theme mode. It will default to "system" and then
            // update the UI automatically when the actual preference is loaded.
            val currentThemeModeString by preferencesManager.themeMode.collectAsStateWithLifecycle(
                initialValue = "system"
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
                    // Permission already granted, schedule notifications safely
                    scheduleNotificationsSafely()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For older versions (API < 33), schedule notifications directly
            scheduleNotificationsSafely()
        }
    }

    /**
     * Safely schedules notifications with proper error handling.
     * This method ensures we don't crash if the notification scheduler
     * has issues during initialization or scheduling.
     */
    private fun scheduleNotificationsSafely() {
        if (!isInjectionComplete) {
            android.util.Log.w("MainActivity", "Injection not complete, skipping notification scheduling")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                if (::notificationScheduler.isInitialized) {
                    notificationScheduler.rescheduleAllNotifications()
                } else {
                    android.util.Log.w("MainActivity", "NotificationScheduler not initialized")
                }
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
                ProdyBottomNavigationBar(
                    items = bottomNavItems,
                    currentRoute = currentDestination?.route,
                    onItemClick = { route ->
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

/**
 * Premium Bottom Navigation Bar - Redesigned for Prody UI/UX Phase 2
 *
 * Design Principles:
 * - Sleek, minimal, flat design (no shadows)
 * - Neon green accent (#36F97F) for active states
 * - Green circular background for active icon
 * - Poppins typography for labels
 * - Smooth micro-interactions
 * - 8dp grid spacing system
 */
@Composable
private fun ProdyBottomNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    // Theme-aware colors
    val backgroundColor = if (isDarkTheme) {
        Color(0xFF0D2826) // Deep dark teal/green
    } else {
        Color(0xFFF0F4F3) // Clean off-white
    }

    val inactiveColor = if (isDarkTheme) {
        Color(0xFFD3D8D7) // Subtle gray for dark mode
    } else {
        Color(0xFF6C757D) // Medium gray for light mode
    }

    val accentColor = ProdyAccent // Vibrant neon green (#36F97F)
    val accentBackground = accentColor.copy(alpha = 0.15f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = backgroundColor,
        tonalElevation = 0.dp // Flat design - no elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // Comfortable touch target height
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route

                ProdyNavItem(
                    item = item,
                    selected = selected,
                    accentColor = accentColor,
                    accentBackground = accentBackground,
                    inactiveColor = inactiveColor,
                    onClick = { onItemClick(item.route) }
                )
            }
        }
    }
}

/**
 * Individual navigation item with premium styling
 */
@Composable
private fun ProdyNavItem(
    item: BottomNavItem,
    selected: Boolean,
    accentColor: Color,
    accentBackground: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // No ripple for cleaner look
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon with optional green circular background for active state
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (selected) accentBackground else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                contentDescription = stringResource(item.contentDescriptionResId),
                modifier = Modifier.size(24.dp),
                tint = if (selected) accentColor else inactiveColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Label with Poppins typography
        Text(
            text = stringResource(item.labelResId),
            fontFamily = PoppinsFamily,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 11.sp,
            color = if (selected) accentColor else inactiveColor,
            letterSpacing = 0.2.sp
        )
    }
}
