package com.prody.prashant.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * =============================================================================
 * HOME SCREEN - PRODUCTION GRADE REVAMP
 * =============================================================================
 *
 * The central hub of the Prody experience.
 * Features:
 * - Intelligent, context-aware greeting
 * - Dual Streak system (Wisdom & Reflection)
 * - AI-generated Buddha Wisdom (with Proof Mode support)
 * - Active Progress Layer (Contextual Next Actions)
 * - Surfaced Memories (Magic Moments)
 * - Quick access to core features
 */

@Composable
fun HomeScreen(
    onNavigateToVocabulary: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToIdioms: () -> Unit = onNavigateToQuotes,
    onNavigateToProverbs: () -> Unit = onNavigateToQuotes,
    onNavigateToJournal: () -> Unit,
    onNavigateToFutureMessage: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHaven: () -> Unit = {},
    onNavigateToMeditation: () -> Unit = {},
    onNavigateToChallenges: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToIdiomDetail: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Show a loading state if initial data is still being fetched
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp) // Space for bottom nav and Fab
        ) {
            // 1. Header with Profile and Notifications
            item {
                HomeHeader(
                    userName = uiState.userName,
                    onProfileClick = onNavigateToSettings,
                    onNotificationClick = { /* TODO */ }
                )
            }

            // 2. Intelligent Greeting
            item {
                IntelligentGreetingHeader(
                    greeting = uiState.intelligentGreeting,
                    subtext = uiState.greetingSubtext,
                    userName = null, // Already handled in HomeHeader/IntelligentGreeting
                    isStruggling = uiState.isUserStruggling,
                    isThriving = uiState.isUserThriving,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // 3. AI Configuration Warning
            if (uiState.aiConfigurationStatus != com.prody.prashant.ui.screens.home.AiConfigurationStatus.CONFIGURED) {
                item {
                    AiConfigWarningBanner(
                        status = uiState.aiConfigurationStatus,
                        onConfigureClick = onNavigateToSettings,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }

            // 4. Next Action (Contextual Guidance)
            uiState.nextAction?.let { action ->
                item {
                    NextActionCard(
                        nextAction = action,
                        onClick = {
                            when (action.actionRoute) {
                                "journal/new" -> onNavigateToJournal()
                                "vocabulary" -> onNavigateToVocabulary()
                                "future_message/write" -> onNavigateToFutureMessage()
                                "quotes" -> onNavigateToQuotes()
                                else -> onNavigateToJournal()
                            }
                        },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }

            // 5. Dual Streaks
            item {
                DualStreakCard(
                    dualStreakStatus = uiState.dualStreakStatus,
                    onTapForDetails = { /* Show details dialog */ },
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // 6. Buddha's Wisdom (AI-Generated)
            item {
                BuddhaThoughtCard(
                    thought = uiState.buddhaThought,
                    explanation = uiState.buddhaThoughtExplanation,
                    isLoading = uiState.isBuddhaThoughtLoading,
                    canRefresh = uiState.canRefreshBuddhaThought,
                    proofInfo = uiState.buddhaWisdomProofInfo,
                    onRefresh = { viewModel.refreshBuddhaThought() },
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // 7. Surfaced Memory (Magic Moment)
            if (uiState.showMemoryCard) {
                uiState.surfacedMemory?.let { memory ->
                    item {
                        SurfacedMemoryCard(
                            memory = memory,
                            onExpand = { viewModel.expandMemoryCard() },
                            onDismiss = { viewModel.dismissMemoryCard() },
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 0.dp)
                                .padding(bottom = 24.dp)
                        )
                    }
                }
            }

            // 8. Quick Actions
            item {
                QuickActionsSection(
                    onJournalClick = onNavigateToJournal,
                    onHavenClick = onNavigateToHaven,
                    onWisdomClick = onNavigateToQuotes,
                    onFutureClick = onNavigateToFutureMessage
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = ProdyIcons.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Avatar / Profile
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ProdyForestGreen)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "P",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onJournalClick: () -> Unit,
    onHavenClick: () -> Unit,
    onWisdomClick: () -> Unit,
    onFutureClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "QUICK ACTIONS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionTile(
                title = "Journal",
                icon = ProdyIcons.Rounded.Book,
                color = ProdyForestGreen,
                onClick = onJournalClick,
                modifier = Modifier.weight(1f)
            )
            ActionTile(
                title = "Haven",
                icon = ProdyIcons.Rounded.Psychology,
                color = ProdyInfo,
                onClick = onHavenClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionTile(
                title = "Wisdom",
                icon = ProdyIcons.Rounded.AutoAwesome,
                color = ProdyWarmAmber,
                onClick = onWisdomClick,
                modifier = Modifier.weight(1f)
            )
            ActionTile(
                title = "Future",
                icon = ProdyIcons.Rounded.Send,
                color = Color(0xFF9C27B0),
                onClick = onFutureClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = color,
                fontFamily = PoppinsFamily
            )
        }
    }
}
