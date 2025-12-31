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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.prody.prashant.ui.components.NavigationBreathingGlow
import com.prody.prashant.ui.main.MainViewModel
import com.prody.prashant.ui.theme.ProdyPrimary
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
    lateinit var notificationScheduler: NotificationScheduler

    private val viewModel: MainViewModel by viewModels()

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

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            // Keep the splash screen visible until the UI state is loaded.
            splashScreen.setKeepOnScreenCondition { uiState.isLoading }

            if (!uiState.isLoading) {
                ProdyTheme(
                    themeMode = uiState.themeMode
                ) {
                    ProdyApp(
                        startDestination = uiState.startDestination
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
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                // Wrap icon with magical breathing glow effect
                                NavigationBreathingGlow(
                                    isActive = selected,
                                    color = ProdyPrimary
                                ) {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = null
                                    )
                                }
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

// =============================================================================
// CUSTOM BOTTOM NAVIGATION BAR - Flat Design
// =============================================================================

/**
 * Premium flat-design bottom navigation bar.
 *
 * Design features:
 * - NO shadows - pure flat design
 * - Clean surface with subtle top border
 * - Animated selection indicator with accent color
 * - Minimal, focused visual hierarchy
 */
@Composable
private fun ProdyBottomNavBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    // Flat design - no elevation, clean surface with subtle top border
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp // Flat - no elevation
    ) {
        Column {
            // Subtle top border for visual separation (flat design)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    ProdyNavItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onNavigate(item.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Individual navigation item with animated selection state.
 */
@Composable
private fun ProdyNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = ProdyPrimary
    val accentBackground = ProdyPrimary.copy(alpha = 0.15f)
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) accentBackground else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = stringResource(item.contentDescriptionResId),
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) accentColor else inactiveColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(item.labelResId),
            fontFamily = PoppinsFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 11.sp,
            color = if (isSelected) accentColor else inactiveColor,
            letterSpacing = 0.2.sp
        )
    }
}
