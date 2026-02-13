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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prody.prashant.notification.NotificationScheduler
import com.prody.prashant.ui.components.NavigationBreathingGlow
import com.prody.prashant.ui.main.MainViewModel
import com.prody.prashant.ui.navigation.BottomNavItem
import com.prody.prashant.ui.navigation.ProdyNavHost
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.PoppinsFamily
import com.prody.prashant.ui.theme.ProdyPrimary
import com.prody.prashant.ui.theme.ProdyTheme
import com.prody.prashant.util.LocalHapticEnabled
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        // Keep splash screen visible until the data is loaded by the ViewModel
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.isLoading
        }

        // Check and request notification permission for Android 13+ safely
        try {
            requestNotificationPermission()
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Failed to request notification permission", e)
        }

        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            if (!uiState.isLoading) {
                ProdyTheme(
                    themeMode = uiState.themeMode
                ) {
                    CompositionLocalProvider(
                        LocalHapticEnabled provides uiState.hapticFeedbackEnabled
                    ) {
                        uiState.startDestination?.let { startDestination ->
                            ProdyApp(
                                startDestination = startDestination
                            )
                        }
                    }
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
        BottomNavItem.Haven,
        BottomNavItem.Stats,
        BottomNavItem.Profile
    )

    // Determine if bottom nav should be shown (only on main tabs)
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.JournalList.route,
        Screen.HavenHome.route,
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
                    bottomNavItems.forEachIndexed { index, item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        if (item == BottomNavItem.Haven) {
                            // Special Haven FAB Item
                            NavigationBarItem(
                                modifier = Modifier.semantics {
                                    role = Role.Tab
                                    traversalIndex = index.toFloat()
                                    contentDescription = stringResource(item.contentDescriptionResId)
                                    stateDescription = if (selected) "Selected" else "Not selected"
                                },
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    // Breathing Pulse Animation
                                    val infiniteTransition = rememberInfiniteTransition(label = "HavenPulse")
                                    val alpha by infiniteTransition.animateFloat(
                                        initialValue = 0.6f,
                                        targetValue = 1f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(2000, easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "HavenAlpha"
                                    )
                                    val scale by infiniteTransition.animateFloat(
                                        initialValue = 0.95f,
                                        targetValue = 1.05f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(2000, easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "HavenScale"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(56.dp) // Larger than standard icon
                                            .scale(scale)
                                            .clip(CircleShape)
                                            .background(
                                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                                    colors = listOf(
                                                        com.prody.prashant.ui.theme.HavenBubbleLight,
                                                        com.prody.prashant.ui.theme.HavenBubbleLight.copy(alpha = 0.8f)
                                                    )
                                                )
                                            )
                                            .alpha(if (selected) 1f else alpha), // Pulse when not selected (waiting)
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = null,
                                            tint = com.prody.prashant.ui.theme.HavenTextLight,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                },
                                label = { /* No label for FAB look */ },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent // Disable standard indicator
                                )
                            )
                        } else {
                            // Standard Navigation Item
                            NavigationBarItem(
                                modifier = Modifier.semantics {
                                    role = Role.Tab
                                    traversalIndex = index.toFloat()
                                    contentDescription = stringResource(item.contentDescriptionResId)
                                    stateDescription = if (selected) "Selected" else "Not selected"
                                },
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
        }
    ) { innerPadding ->
        // Use the complete ProdyNavHost with all routes properly configured
        ProdyNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        )
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
