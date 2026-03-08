package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * Prody Home Screen - The central hub of the user's growth journey.
 *
 * Optimized for performance:
 * - Uses collectAsStateWithLifecycle for efficient state collection.
 * - LazyColumn with stable keys to prevent redundant recompositions.
 * - Production-grade components from Soul Layer and Intelligence layers.
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
    onNavigateToSettings: () -> Unit = {},
    onNavigateToIdiomDetail: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backgroundColor = MaterialTheme.colorScheme.background
    var showStreakDetails by remember { mutableStateOf(false) }

    // Dialogs
    if (showStreakDetails) {
        DualStreakDetailDialog(
            dualStreakStatus = uiState.dualStreakStatus,
            onDismiss = { showStreakDetails = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Configuration Warning
        if (uiState.aiConfigurationStatus != AiConfigurationStatus.CONFIGURED) {
            item(key = "ai_config_warning") {
                AiConfigWarningBanner(
                    onConfigureClick = onNavigateToSettings
                )
            }
        }

        // Personalized Greeting (Soul Layer)
        item(key = "greeting") {
            IntelligentGreetingHeader(
                greeting = uiState.intelligentGreeting,
                subtext = uiState.greetingSubtext,
                userName = uiState.userName,
                isStruggling = uiState.isUserStruggling,
                isThriving = uiState.isUserThriving
            )
        }

        // Dual Streak Tracking
        item(key = "streaks") {
            DualStreakCard(
                dualStreakStatus = uiState.dualStreakStatus,
                onTapForDetails = { showStreakDetails = true }
            )
        }

        // Buddha's Daily Thought (AI Wisdom)
        item(key = "buddha_thought") {
            BuddhaThoughtCard(
                thought = uiState.buddhaThought,
                explanation = uiState.buddhaThoughtExplanation,
                isLoading = uiState.isBuddhaThoughtLoading,
                isAiGenerated = uiState.isBuddhaThoughtAiGenerated,
                onRefresh = { viewModel.refreshBuddhaThought() }
            )
        }

        // Contextual Next Action
        uiState.nextAction?.let { action ->
            item(key = "next_action") {
                NextActionCard(
                    nextAction = action,
                    onClick = {
                        when (action.actionRoute) {
                            "journal" -> onNavigateToJournal()
                            "vocabulary" -> onNavigateToVocabulary()
                            "quotes" -> onNavigateToQuotes()
                            "haven" -> onNavigateToHaven()
                            else -> {}
                        }
                    }
                )
            }
        }

        // Surfaced Memory (Soul Layer)
        if (uiState.showMemoryCard) {
            uiState.surfacedMemory?.let { memory ->
                item(key = "surfaced_memory") {
                    SurfacedMemoryCard(
                        memory = memory,
                        onExpand = { viewModel.expandMemoryCard() },
                        onDismiss = { viewModel.dismissMemoryCard() }
                    )
                }
            }
        }

        // Dashboard Metrics
        item(key = "stats") {
            WeeklySummarySection(
                journalEntries = uiState.journalEntriesThisWeek,
                wordsLearned = uiState.wordsLearnedThisWeek,
                mindfulMinutes = 45
            )
        }

        // Quick Access Grid
        item(key = "quick_actions") {
            QuickActionsGrid(
                onJournalClick = onNavigateToJournal,
                onHavenClick = onNavigateToHaven,
                onWisdomClick = onNavigateToQuotes,
                onFutureClick = onNavigateToFutureMessage
            )
        }

        // Recent Activity
        item(key = "recent") {
            RecentActivitySection()
        }
    }
}

// =============================================================================
// REFACTORED DASHBOARD COMPONENTS (Internal to HomeScreen)
// =============================================================================

@Composable
fun WeeklySummarySection(journalEntries: Int, wordsLearned: Int, mindfulMinutes: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "This Week",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = ProdyTextPrimaryLight
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(
                label = "Entries",
                value = journalEntries.toString(),
                icon = ProdyIcons.Edit,
                color = ProdyForestGreen,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Words",
                value = wordsLearned.toString(),
                icon = ProdyIcons.School,
                color = ProdyWarmAmber,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Minutes",
                value = mindfulMinutes.toString(),
                icon = ProdyIcons.SelfImprovement,
                color = ProdyInfo,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    ProdyCard(
        modifier = modifier,
        backgroundColor = ProdySurfaceLight
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ProdyTextPrimaryLight
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = ProdyTextSecondaryLight
            )
        }
    }
}

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
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = ProdyTextPrimaryLight
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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
fun QuickActionTile(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    ProdyClickableCard(
        onClick = onClick,
        modifier = modifier.height(90.dp),
        backgroundColor = color.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun RecentActivitySection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = ProdyTextPrimaryLight
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        ProdyCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = ProdySurfaceLight
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ProdyForestGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(ProdyIcons.Check, null, tint = ProdyForestGreen, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Daily Journal",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Completed today",
                        style = MaterialTheme.typography.bodySmall,
                        color = ProdyTextSecondaryLight
                    )
                }
            }
        }
    }
}
