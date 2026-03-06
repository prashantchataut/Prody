package com.prody.prashant.ui.screens.home

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.domain.intelligence.SurfacedMemory
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

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
    val backgroundColor = MaterialTheme.colorScheme.background
    var showStreakDetails by remember { mutableStateOf(false) }

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
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. AI Configuration Warning
        if (uiState.aiConfigurationStatus == AiConfigurationStatus.MISSING) {
            item {
                AiConfigWarningBanner(
                    onConfigClick = onNavigateToSettings,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        // 2. Intelligent Greeting Header
        item {
            IntelligentGreetingHeader(
                greeting = uiState.intelligentGreeting,
                subtext = uiState.greetingSubtext,
                userName = uiState.userName,
                isStruggling = uiState.isUserStruggling,
                isThriving = uiState.isUserThriving,
                modifier = Modifier.statusBarsPadding().padding(top = 16.dp)
            )
        }

        // 3. Dual Streak Card
        item {
            DualStreakCard(
                dualStreakStatus = uiState.dualStreakStatus,
                onTapForDetails = { showStreakDetails = true }
            )
        }

        // 4. Buddha Thought Card
        item {
            BuddhaThoughtCard(
                thought = uiState.buddhaThought,
                explanation = uiState.buddhaThoughtExplanation,
                isLoading = uiState.isBuddhaThoughtLoading,
                isAiGenerated = uiState.isBuddhaThoughtAiGenerated,
                onRefresh = {
                    viewModel.refreshBuddhaThought()
                    viewModel.onWisdomContentViewed()
                },
                canRefresh = uiState.canRefreshBuddhaThought
            )
        }

        // 5. Next Action suggestion
        uiState.nextAction?.let { action ->
            item {
                NextActionCard(
                    nextAction = action,
                    onClick = {
                        // Handle action navigation based on type
                    when (action.type) {
                        com.prody.prashant.domain.progress.NextActionType.START_JOURNAL,
                        com.prody.prashant.domain.progress.NextActionType.FOLLOW_UP_JOURNAL -> onNavigateToJournal()
                        com.prody.prashant.domain.progress.NextActionType.LEARN_WORD,
                        com.prody.prashant.domain.progress.NextActionType.REVIEW_WORDS -> onNavigateToVocabulary()
                        com.prody.prashant.domain.progress.NextActionType.REFLECT_ON_QUOTE -> onNavigateToQuotes()
                        com.prody.prashant.domain.progress.NextActionType.WRITE_FUTURE_MESSAGE -> onNavigateToFutureMessage()
                        com.prody.prashant.domain.progress.NextActionType.COMPLETE_CHALLENGE -> onNavigateToChallenges()
                        }
                    }
                )
            }
        }

        // 6. Surfaced Memory
        if (uiState.showMemoryCard) {
            uiState.surfacedMemory?.let { memory ->
                item {
                    SurfacedMemoryCard(
                        memory = memory,
                        onExpand = { viewModel.expandMemoryCard() },
                        onDismiss = { viewModel.dismissMemoryCard() },
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }

        // 7. Weekly Summary Section
        item {
            WeeklySummarySection(
                journalEntries = uiState.journalEntriesThisWeek,
                wordsLearned = uiState.wordsLearnedThisWeek,
                daysActive = uiState.daysActiveThisWeek
            )
        }
        
        // 8. Quick Actions (Navigation)
        item {
            QuickActionsGrid(
                onJournalClick = onNavigateToJournal,
                onHavenClick = onNavigateToHaven,
                onWisdomClick = onNavigateToQuotes,
                onFutureClick = onNavigateToFutureMessage
            )
        }

        // 9. Recent Activity
        item {
            RecentActivitySection(
                journaledToday = uiState.journaledToday,
                mood = uiState.todayEntryMood,
                preview = uiState.todayEntryPreview
            )
        }
    }
}

@Composable
fun WeeklySummarySection(journalEntries: Int, wordsLearned: Int, daysActive: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "This Week",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        
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
                color = Color(0xFFF59E0B), // Warm Amber
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Active Days",
                value = daysActive.toString(),
                icon = ProdyIcons.CalendarMonth,
                color = Color(0xFF3B82F6), // Info Blue
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        
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
                color = Color(0xFF3B82F6),
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
                color = Color(0xFFF59E0B),
                onClick = onWisdomClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                title = "Future",
                icon = ProdyIcons.Send,
                color = Color(0xFF8B5CF6),
                onClick = onFutureClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionTile(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
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

@Composable
fun RecentActivitySection(
    journaledToday: Boolean,
    mood: String,
    preview: String
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Today's Status",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (journaledToday) ProdyForestGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (journaledToday) ProdyIcons.Check else ProdyIcons.Edit,
                        contentDescription = null,
                        tint = if (journaledToday) ProdyForestGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (journaledToday) "Journal Entry Recorded" else "No Entry Today",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PoppinsFamily,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (journaledToday) "Mood: $mood • ${preview.take(30)}..." else "Write down your thoughts to maintain your streak.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = PoppinsFamily
                    )
                }
            }
        }
    }
}
