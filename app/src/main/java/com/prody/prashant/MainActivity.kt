package com.prody.prashant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prody.prashant.data.auth.AuthRepository
import com.prody.prashant.data.auth.AuthState
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.notification.NotificationScheduler
import com.prody.prashant.ui.components.kairos.KairosAppBackground
import com.prody.prashant.ui.components.kairos.KairosBottomNavigation
import com.prody.prashant.ui.components.kairos.KairosNavigationRail
import com.prody.prashant.ui.main.MainViewModel
import com.prody.prashant.ui.navigation.BottomNavItem
import com.prody.prashant.ui.navigation.ProdyNavHost
import com.prody.prashant.ui.theme.KairosTheme
import com.prody.prashant.ui.screens.auth.AuthScreen
import com.prody.prashant.util.LocalHapticEnabled
import com.prody.prashant.util.rememberProdyHaptic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val viewModel: MainViewModel by viewModels()

    // Flag to track if Hilt injection is complete
    private var isInjectionComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE calling super.onCreate()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Mark injection as complete after super.onCreate() for Hilt activities
        isInjectionComplete = true

        // Keep splash screen visible until the data is loaded by the ViewModel
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.isLoading
        }

        // Do not interrupt first launch with a system prompt. Existing permission is
        // honored here; new users opt in from Settings where intent is explicit.
        scheduleNotificationsIfAllowed()

        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val authState by authRepository.authState.collectAsStateWithLifecycle()

            if (!uiState.isLoading) {
                KairosTheme(
                    themeMode = uiState.themeMode
                ) {
                    CompositionLocalProvider(
                        LocalHapticEnabled provides uiState.hapticFeedbackEnabled
                    ) {
                        when (authState) {
                            is AuthState.Authenticated -> {
                                uiState.startDestination?.let { startDestination ->
                                    ProdyApp(
                                        startDestination = startDestination
                                    )
                                }
                            }
                            is AuthState.Unauthenticated, is AuthState.Error -> {
                                AuthScreen(
                                    onAuthSuccess = {
                                        // Auth state change will trigger recomposition
                                    }
                                )
                            }
                            AuthState.Idle, AuthState.Loading -> {
                                // Show nothing or a loading indicator while determining auth state
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::preferencesManager.isInitialized) {
            lifecycleScope.launch(Dispatchers.IO) {
                preferencesManager.setLastAppOpenAt(System.currentTimeMillis())
            }
        }
        scheduleNotificationsIfAllowed()
    }

    private fun scheduleNotificationsIfAllowed() {
        val canPostNotifications = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        if (canPostNotifications) {
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
    val haptic = rememberProdyHaptic()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navigationItems = BottomNavItem.items
    val currentTopLevelRoute = navigationItems.firstOrNull { item ->
        currentDestination?.hierarchy?.any { destination ->
            destination.route == item.destinationRoute
        } == true
    }?.destinationRoute
    val showNavigation = currentTopLevelRoute != null

    fun navigateTo(item: BottomNavItem) {
        haptic.selection()
        navController.navigate(item.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    KairosAppBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useRail = maxWidth >= 720.dp
            if (useRail && showNavigation) {
                Row(modifier = Modifier.fillMaxSize()) {
                    KairosNavigationRail(
                        items = navigationItems,
                        selectedRoute = currentTopLevelRoute,
                        onSelect = ::navigateTo
                    )
                    ProdyNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    )
                }
            } else {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        if (showNavigation) {
                            KairosBottomNavigation(
                                items = navigationItems,
                                selectedRoute = currentTopLevelRoute,
                                onSelect = ::navigateTo
                            )
                        }
                    }
                ) { innerPadding ->
                    ProdyNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
