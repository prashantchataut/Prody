package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
 * HOME SCREEN - PERSONALIZED GROWTH DASHBOARD
 * =============================================================================
 *
 * The central hub of Prody. This screen provides a personalized overview of the
 * user's growth journey, powered by the Soul Layer intelligence system.
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
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMeditation: () -> Unit = {},
    onNavigateToChallenges: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToIdiomDetail: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.lg)
        ) {
            // 1. Intelligent Greeting Header
            item {
                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.lg))
                IntelligentGreetingHeader(
                    greeting = uiState.intelligentGreeting,
                    subtext = uiState.greetingSubtext,
                    userName = uiState.userName,
                    isStruggling = uiState.isUserStruggling,
                    isThriving = uiState.isUserThriving,
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // 2. AI Configuration Warning
            if (uiState.aiConfigurationStatus == AiConfigurationStatus.MISSING) {
                item {
                    AiConfigWarningBanner(
                        onConfigureClick = onNavigateToSettings,
                        modifier = Modifier.padding(horizontal = ProdyTokens.Spacing.lg)
                    )
                }
            }

            // 3. Dual Streak Status
            item {
                DualStreakCard(
                    dualStreakStatus = uiState.dualStreakStatus,
                    onTapForDetails = { /* Details dialog logic can be added here */ },
                    modifier = Modifier.padding(horizontal = 0.dp)
                )
            }

            // 4. Next Action Suggestion
            uiState.nextAction?.let { action ->
                item {
                    NextActionCard(
                        nextAction = action,
                        onClick = {
                            when (action.type) {
                                com.prody.prashant.domain.progress.NextActionType.START_JOURNAL -> onNavigateToJournal()
                                com.prody.prashant.domain.progress.NextActionType.FOLLOW_UP_JOURNAL -> onNavigateToJournal()
                                com.prody.prashant.domain.progress.NextActionType.REVIEW_WORDS -> onNavigateToVocabulary()
                                com.prody.prashant.domain.progress.NextActionType.LEARN_WORD -> onNavigateToVocabulary()
                                com.prody.prashant.domain.progress.NextActionType.WRITE_FUTURE_MESSAGE -> onNavigateToFutureMessage()
                                com.prody.prashant.domain.progress.NextActionType.REFLECT_ON_QUOTE -> onNavigateToQuotes()
                                com.prody.prashant.domain.progress.NextActionType.COMPLETE_CHALLENGE -> onNavigateToChallenges()
                                else -> {}
                            }
                        },
                        modifier = Modifier.padding(horizontal = ProdyTokens.Spacing.lg)
                    )
                }
            }

            // 5. First Week Journey
            if (uiState.isInFirstWeek) {
                item {
                    FirstWeekProgressCard(
                        dayNumber = uiState.firstWeekDayNumber,
                        progress = uiState.firstWeekProgress,
                        dayContent = uiState.firstWeekDayContent,
                        onContinue = { /* Navigation based on dayContent */ },
                        modifier = Modifier.padding(horizontal = ProdyTokens.Spacing.lg)
                    )
                }
            }

            // 6. Buddha's Insight
            item {
                BuddhaThoughtCard(
                    thought = uiState.buddhaThought,
                    explanation = uiState.buddhaThoughtExplanation,
                    isLoading = uiState.isBuddhaThoughtLoading,
                    isAiGenerated = uiState.isBuddhaThoughtAiGenerated,
                    canRefresh = uiState.canRefreshBuddhaThought,
                    proofInfo = uiState.buddhaWisdomProofInfo,
                    onRefresh = { viewModel.refreshBuddhaThought() },
                    modifier = Modifier.padding(horizontal = ProdyTokens.Spacing.lg)
                )
            }

            // 7. Surfaced Memory
            if (uiState.showMemoryCard && uiState.surfacedMemory != null) {
                item {
                    SurfacedMemoryCard(
                        memory = uiState.surfacedMemory!!,
                        onExpand = { viewModel.expandMemoryCard() },
                        onDismiss = { viewModel.dismissMemoryCard() },
                        modifier = Modifier.padding(horizontal = ProdyTokens.Spacing.lg)
                    )
                }
            }

            // 8. Daily Wisdom Hint
            if (uiState.showDailyWisdomHint) {
                item {
                    ContextualAiHint(
                        hint = viewModel.getDailyWisdomHint(),
                        onDismiss = { viewModel.onDailyWisdomHintDismiss() },
                        modifier = Modifier.padding(horizontal = 0.dp)
                    )
                }
            }

            // 9. Quick Navigation Grid
            item {
                QuickActionsGrid(
                    onJournalClick = onNavigateToJournal,
                    onHavenClick = onNavigateToHaven,
                    onWisdomClick = onNavigateToQuotes,
                    onFutureClick = onNavigateToFutureMessage
                )
            }

            item {
                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xxl))
            }
        }
    }

    // Buddha Guide Introduction
    if (uiState.showBuddhaGuide) {
        BuddhaGuideIntro(
            cards = uiState.buddhaGuideCards,
            onComplete = { viewModel.onBuddhaGuideComplete() },
            onDontShowAgain = { viewModel.onBuddhaGuideDontShowAgain() }
        )
    }

    // First Week Celebration Dialog
    if (uiState.showFirstWeekCelebration && uiState.firstWeekCelebration != null) {
        CelebrationDialog(
            celebration = uiState.firstWeekCelebration!!,
            onDismiss = { viewModel.dismissFirstWeekCelebration() }
        )
    }
}

// =============================================================================
// REFACTORED QUICK ACTIONS
// =============================================================================

@Composable
fun QuickActionsGrid(
    onJournalClick: () -> Unit,
    onHavenClick: () -> Unit,
    onWisdomClick: () -> Unit,
    onFutureClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ProdyTokens.Spacing.lg)
    ) {
        Text(
            text = "Explore Prody",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(ProdyTokens.Spacing.lg))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.lg)
        ) {
            QuickActionTile(
                title = "Journal",
                icon = ProdyIcons.Book,
                color = ProdyForestGreen,
                onClick = onJournalClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                title = "Haven",
                icon = ProdyIcons.Psychology,
                color = ProdyInfo,
                onClick = onHavenClick,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(ProdyTokens.Spacing.lg))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.lg)
        ) {
            QuickActionTile(
                title = "Wisdom",
                icon = ProdyIcons.Lightbulb,
                color = ProdyWarmAmber,
                onClick = onWisdomClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                title = "Future",
                icon = ProdyIcons.Send,
                color = Color(0xFF9C27B0),
                onClick = onFutureClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val backgroundColor = remember(color) { color.copy(alpha = 0.1f) }
    val borderColor = remember(color) { color.copy(alpha = 0.2f) }
    val shape = remember { RoundedCornerShape(16.dp) }

    Surface(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = shape,
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(ProdyTokens.Spacing.lg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.sm))
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = color
                )
            )
        }
    }
}
