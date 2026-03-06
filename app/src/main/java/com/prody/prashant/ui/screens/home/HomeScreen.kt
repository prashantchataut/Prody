package com.prody.prashant.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * ================================================================================================
 * PRODY HOME SCREEN (Phase 2 Redesign)
 * ================================================================================================
 *
 * The central hub of the Prody experience. It intelligently surfaced content based on
 * the user's journey, emotional state, and progress.
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
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ContentLoadingState(
        data = if (uiState.isLoading) null else uiState,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onRetry = { viewModel.retry() }
    ) { state ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // 1. Intelligent Greeting Header
                item(key = "greeting") {
                    IntelligentGreetingHeader(
                        greeting = state.intelligentGreeting,
                        subtext = state.greetingSubtext,
                        userName = state.userName,
                        isStruggling = state.isUserStruggling,
                        isThriving = state.isUserThriving,
                        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                    )
                }

                // 2. AI Configuration Warning
                if (state.aiConfigurationStatus == AiConfigurationStatus.MISSING) {
                    item(key = "ai_warning") {
                        AiConfigWarningBanner(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }

                // 3. First Week Journey Card
                if (state.isInFirstWeek) {
                    item(key = "first_week") {
                        FirstWeekProgressCard(
                            dayNumber = state.firstWeekDayNumber,
                            progress = state.firstWeekProgress,
                            dayContent = state.firstWeekDayContent,
                            onContinue = { /* Navigation handled in UI */ },
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }

                // 4. Dual Streak Card
                item(key = "streaks") {
                    DualStreakCard(
                        dualStreakStatus = state.dualStreakStatus,
                        onTapForDetails = { /* Show details */ },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // 5. Surfaced Memory
                if (state.showMemoryCard && state.surfacedMemory != null) {
                    item(key = "memory") {
                        SurfacedMemoryCard(
                            memory = state.surfacedMemory,
                            onExpand = { viewModel.expandMemoryCard() },
                            onDismiss = { viewModel.dismissMemoryCard() },
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }

                // 6. Anniversary Memories
                if (state.anniversaryMemories.isNotEmpty()) {
                    items(state.anniversaryMemories, key = { "anniversary_${it.memory.id}" }) { memory ->
                        AnniversaryMemoryCard(
                            anniversary = memory,
                            onView = { /* View */ },
                            onDismiss = { /* Dismiss */ },
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }

                // 7. Buddha's Thought
                item(key = "buddha_thought") {
                    BuddhaThoughtCard(
                        thought = state.buddhaThought,
                        explanation = state.buddhaThoughtExplanation,
                        isAiGenerated = state.isBuddhaThoughtAiGenerated,
                        isLoading = state.isBuddhaThoughtLoading,
                        canRefresh = state.canRefreshBuddhaThought,
                        onRefresh = { viewModel.refreshBuddhaThought() },
                        proofInfo = if (state.buddhaWisdomProofInfo.isEnabled) state.buddhaWisdomProofInfo else null,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }

                // 8. Next Action
                item(key = "next_action") {
                    state.nextAction?.let { action ->
                        NextActionCard(
                            action = action,
                            onActionClick = { /* Action */ },
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }

                // 9. Daily Wisdom Section
                item(key = "daily_wisdom") {
                    DailyWisdomRow(
                        quote = state.dailyQuote,
                        quoteAuthor = state.dailyQuoteAuthor,
                        word = state.wordOfTheDay,
                        wordDef = state.wordDefinition,
                        onQuoteClick = onNavigateToQuotes,
                        onWordClick = onNavigateToVocabulary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                // 10. Weekly Summary Stats
                item(key = "summary") {
                    WeeklySummarySection(
                        journalEntries = state.journalEntriesThisWeek,
                        wordsLearned = state.wordsLearnedThisWeek,
                        daysActive = state.daysActiveThisWeek,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }

                // 11. Quick Actions Grid
                item(key = "quick_actions") {
                    QuickActionsGrid(
                        onJournalClick = onNavigateToJournal,
                        onHavenClick = onNavigateToHaven,
                        onWisdomClick = onNavigateToQuotes,
                        onFutureClick = onNavigateToFutureMessage,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
            }

            // Buddha Guide
            if (state.showBuddhaGuide) {
                BuddhaGuideIntro(
                    cards = state.buddhaGuideCards,
                    onComplete = { viewModel.onBuddhaGuideComplete() },
                    onDontShowAgain = { viewModel.onBuddhaGuideDontShowAgain() }
                )
            }

            // Contextual AI Hint
            if (state.showDailyWisdomHint) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    ContextualAiHint(
                        hint = viewModel.getDailyWisdomHint(),
                        onDismiss = { viewModel.onDailyWisdomHintDismiss() }
                    )
                }
            }
        }
    }
}

@Composable
fun AiConfigWarningBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(ProdyIcons.Warning, contentDescription = null, modifier = Modifier.size(24.dp))
            Column {
                Text("AI features are limited", fontWeight = FontWeight.Bold)
                Text("Configure your Gemini API key to enable Buddha's full intelligence.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun WeeklySummarySection(
    journalEntries: Int,
    wordsLearned: Int,
    daysActive: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Your Progress This Week",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryStatCard("Entries", journalEntries.toString(), ProdyIcons.Edit, ProdyForestGreen, Modifier.weight(1f))
            SummaryStatCard("Words", wordsLearned.toString(), ProdyIcons.School, ProdyWarmAmber, Modifier.weight(1f))
            SummaryStatCard("Active", "$daysActive d", ProdyIcons.CalendarToday, ProdyInfo, Modifier.weight(1f))
        }
    }
}

@Composable
fun SummaryStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DailyWisdomRow(
    quote: String,
    quoteAuthor: String,
    word: String,
    wordDef: String,
    onQuoteClick: () -> Unit,
    onWordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WisdomFeatureCard("Quote of the Day", "\"$quote\"", quoteAuthor, ProdyIcons.FormatQuote, ProdyForestGreen, onQuoteClick, Modifier.width(280.dp))
        }
        item {
            WisdomFeatureCard("Word of the Day", word, wordDef, ProdyIcons.School, ProdyWarmAmber, onWordClick, Modifier.width(280.dp))
        }
    }
}

@Composable
fun WisdomFeatureCard(title: String, content: String, author: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(content, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(author, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun QuickActionsGrid(
    onJournalClick: () -> Unit,
    onHavenClick: () -> Unit,
    onWisdomClick: () -> Unit,
    onFutureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Explore Prody", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionTile("Journal", ProdyIcons.Book, ProdyForestGreen, onJournalClick, Modifier.weight(1f))
            QuickActionTile("Haven", ProdyIcons.Psychology, ProdyInfo, onHavenClick, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionTile("Wisdom", ProdyIcons.Lightbulb, ProdyWarmAmber, onWisdomClick, Modifier.weight(1f))
            QuickActionTile("Future Self", ProdyIcons.Send, Color(0xFF9C27B0), onFutureClick, Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionTile(title: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
        }
    }
}
