package com.prody.prashant.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.domain.intelligence.SurfacedMemory
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.streak.DualStreakStatus
import com.prody.prashant.ui.components.IntelligentGreetingHeader
import com.prody.prashant.ui.components.AiConfigWarningBanner
import com.prody.prashant.ui.components.DualStreakCard
import com.prody.prashant.ui.components.NextActionCard
import com.prody.prashant.ui.components.SurfacedMemoryCard
import com.prody.prashant.ui.components.BuddhaThoughtCard
import com.prody.prashant.ui.components.AnniversaryMemoryCard
import com.prody.prashant.ui.components.DualStreakDetailDialog
import com.prody.prashant.ui.theme.*

// =============================================================================
// PERSONALIZATION DASHBOARD - REACTIVE & DOMAIN-DRIVEN
// =============================================================================

/**
 * The Prody Home Screen - A central dashboard for mindfulness and growth.
 *
 * Features:
 * - Reactive state management with collectAsStateWithLifecycle
 * - Context-aware intelligent greeting
 * - Dual Streak system (Wisdom & Reflection)
 * - Proactive "Next Action" suggestions
 * - Surfaced memories for emotional connection
 * - AI-powered daily wisdom (Buddha)
 */
@Composable
fun HomeScreen(
    onNavigateToVocabulary: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToIdioms: () -> Unit = onNavigateToQuotes,
    onNavigateToProverbs: () -> Unit = onNavigateToQuotes,
    onNavigateToJournal: () -> Unit,
    onNavigateToFutureMessage: () -> Unit,
    onNavigateToHaven: () -> Unit = {},
    onNavigateToMeditation: () -> Unit = {},
    onNavigateToChallenges: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToIdiomDetail: (Long) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Local state for dialogs
    var showStreakDetails by remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp), // Extra bottom padding for FAB/Nav
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Intelligent Greeting Header
            item {
                IntelligentGreetingHeader(
                    greeting = uiState.intelligentGreeting,
                    subtext = uiState.greetingSubtext,
                    userName = uiState.userName,
                    isStruggling = uiState.isUserStruggling,
                    isThriving = uiState.isUserThriving,
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // 2. AI Configuration Warning (Conditional)
            item {
                AiConfigWarningBanner(
                    status = uiState.aiConfigurationStatus,
                    onConfigureClick = onNavigateToSettings
                )
            }

            // 3. Dual Streak Card
            item {
                DualStreakCard(
                    dualStreakStatus = uiState.dualStreakStatus,
                    onTapForDetails = { showStreakDetails = true }
                )
            }

            // 4. Contextual Next Action
            item {
                uiState.nextAction?.let { action ->
                    NextActionCard(
                        nextAction = action,
                        onClick = { route ->
                            handleActionNavigation(
                                route = route,
                                onNavigateToVocabulary = onNavigateToVocabulary,
                                onNavigateToJournal = onNavigateToJournal,
                                onNavigateToFutureMessage = onNavigateToFutureMessage,
                                onNavigateToQuotes = onNavigateToQuotes
                            )
                        }
                    )
                }
            }

            // 5. Surfaced Memory (Intelligent Recall)
            item {
                AnimatedVisibility(
                    visible = uiState.showMemoryCard && uiState.surfacedMemory != null,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    uiState.surfacedMemory?.let { memory ->
                        SurfacedMemoryCard(
                            memory = memory,
                            onExpand = { viewModel.expandMemoryCard() },
                            onDismiss = { viewModel.dismissMemoryCard() },
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }

            // 6. Buddha Wisdom / Reflection Card
            item {
                BuddhaThoughtCard(
                    thought = uiState.buddhaThought,
                    explanation = uiState.buddhaThoughtExplanation,
                    isLoading = uiState.isBuddhaThoughtLoading,
                    isAiGenerated = uiState.isBuddhaThoughtAiGenerated,
                    proofInfo = uiState.buddhaWisdomProofInfo,
                    onRefresh = { viewModel.refreshBuddhaThought() }
                )
            }

            // 7. Anniversary Memories (If any)
            items(uiState.anniversaryMemories) { anniversary ->
                AnniversaryMemoryCard(
                    anniversary = anniversary,
                    onView = { /* Handle view */ },
                    onDismiss = { /* Handle dismiss */ },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Streak Detail Dialog
        if (showStreakDetails) {
            DualStreakDetailDialog(
                dualStreakStatus = uiState.dualStreakStatus,
                onDismiss = { showStreakDetails = false }
            )
        }
    }
}

/**
 * Routes the "Next Action" click to the appropriate navigation callback.
 */
private fun handleActionNavigation(
    route: String,
    onNavigateToVocabulary: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToFutureMessage: () -> Unit,
    onNavigateToQuotes: () -> Unit
) {
    when {
        route == "vocabulary" -> onNavigateToVocabulary()
        route == "journal/new" -> onNavigateToJournal()
        route == "future_message/write" -> onNavigateToFutureMessage()
        route == "quotes" -> onNavigateToQuotes()
        else -> onNavigateToJournal() // Safe fallback
    }
}
