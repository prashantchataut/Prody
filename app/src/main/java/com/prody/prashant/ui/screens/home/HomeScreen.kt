package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.ui.theme.*
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.icons.ProdyIcons

// =============================================================================
// PERSONALIZATION DASHBOARD - REVAMPED 2026
// =============================================================================

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
    onNavigateToLearning: () -> Unit = {},
    onNavigateToDeepDive: () -> Unit = {},
    onNavigateToMissions: () -> Unit = {},
    onNavigateToMicroJournal: () -> Unit = {},
    onNavigateToDailyRitual: () -> Unit = {},
    onNavigateToWeeklyDigest: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backgroundColor = MaterialTheme.colorScheme.background

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ProdyForestGreen)
        }
        return
    }

    if (uiState.hasLoadError) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = ProdyWarmAmber,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.error ?: "Something went wrong",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = ProdyTextPrimaryLight
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.OutlinedButton(onClick = { viewModel.retry() }) {
                    Text("Retry", fontFamily = PoppinsFamily)
                }
            }
        }
        return
    }

    // Determine greeting based on ViewModel state or time-based fallback
    val greeting = remember(uiState.intelligentGreeting) {
        if (uiState.intelligentGreeting.isNotEmpty()) {
            uiState.intelligentGreeting
        } else {
            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            when {
                hour < 12 -> "Good Morning,"
                hour < 17 -> "Good Afternoon,"
                else -> "Good Evening,"
            }
        }
    }

    // Build badge data from real achievement progress
    val badges = remember(uiState.totalPoints, uiState.journalEntriesThisWeek, uiState.daysActiveThisWeek) {
        listOf(
            BadgeData(ProdyIcons.EmojiEvents, (uiState.totalPoints.coerceAtMost(1000) / 1000f).coerceIn(0f, 1f), ProdyWarmAmber),
            BadgeData(ProdyIcons.Edit, (uiState.journalEntriesThisWeek.coerceAtMost(7) / 7f).coerceIn(0f, 1f), ProdyForestGreen),
            BadgeData(ProdyIcons.Psychology, (uiState.daysActiveThisWeek.coerceAtMost(7) / 7f).coerceIn(0f, 1f), ProdyInfo)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header with Greeting
        item(key = "header") {
            DashboardHeader(
                userName = uiState.userName,
                greeting = greeting,
                onProfileClick = {},
                onNotificationClick = onNavigateToSearch
            )
        }

        // Dual Streak Card (Enhanced Consistency Visualization)
        item(key = "dual_streak") {
            DualStreakCard(
                dualStreakStatus = uiState.dualStreakStatus,
                onTapForDetails = { /* Navigation or Dialog handled in component */ }
            )
        }

        // Surfaced Memory (Soul Layer Insight)
        if (uiState.showMemoryCard && uiState.surfacedMemory != null) {
            item(key = "surfaced_memory") {
                Spacer(modifier = Modifier.height(16.dp))
                SurfacedMemoryCard(
                    memory = uiState.surfacedMemory!!,
                    onExpand = { viewModel.expandMemoryCard() },
                    onDismiss = { viewModel.dismissMemoryCard() },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Anniversary Memories
        uiState.anniversaryMemories.forEachIndexed { index, memory ->
            item(key = "anniversary_$index") {
                Spacer(modifier = Modifier.height(16.dp))
                AnniversaryMemoryCard(
                    anniversary = memory,
                    onView = { /* Navigate to memory detail */ },
                    onDismiss = { /* Dismiss logic if needed */ },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Personalized Pattern Card (opt-in, local ML)
        if (uiState.personalizedPatternText.isNotEmpty()) {
            item(key = "personalized_pattern") {
                PersonalizedPatternCard(
                    patternText = uiState.personalizedPatternText,
                    patternSuggestion = uiState.personalizedPatternSuggestion
                )
            }
        }

        // Contextual Next Action
        if (uiState.nextAction != null) {
            item(key = "next_action") {
                val nextAction = uiState.nextAction!!
                NextActionCard(
                    nextAction = nextAction,
                    onClick = {
                        when (nextAction.type) {
                            com.prody.prashant.domain.progress.NextActionType.START_JOURNAL,
                            com.prody.prashant.domain.progress.NextActionType.FOLLOW_UP_JOURNAL -> onNavigateToJournal()
                            com.prody.prashant.domain.progress.NextActionType.REVIEW_WORDS,
                            com.prody.prashant.domain.progress.NextActionType.LEARN_WORD -> onNavigateToVocabulary()
                            com.prody.prashant.domain.progress.NextActionType.WRITE_FUTURE_MESSAGE -> onNavigateToFutureMessage()
                            com.prody.prashant.domain.progress.NextActionType.REFLECT_ON_QUOTE -> onNavigateToQuotes()
                            com.prody.prashant.domain.progress.NextActionType.COMPLETE_CHALLENGE -> onNavigateToChallenges()
                        }
                    }
                )
            }
        }

        // Today's Progress Summary
        item(key = "today_progress") {
            TodayProgressCard(progress = uiState.todayProgress)
        }

        // Premium Intelligence Insights (Opt-in)
        if (uiState.isPremiumIntelligenceEnabled && uiState.intelligenceInsights.isNotEmpty()) {
            item(key = "intelligence_insight") {
                Spacer(modifier = Modifier.height(24.dp))
                IntelligenceInsightCard(
                    insight = uiState.intelligenceInsights.first(),
                    onActionClick = {}
                )
            }
        }

        // Mood Trend Chart - only show if there's real data
        if (uiState.moodTrend.isNotEmpty()) {
            item(key = "mood_trend") {
                MoodTrendSection(moodData = uiState.moodTrend)
            }
        }

        // Weekly Summary from real data
        item(key = "weekly_summary") {
            WeeklySummarySection(
                journalEntries = uiState.journalEntriesThisWeek,
                wordsLearned = uiState.wordsLearnedThisWeek,
                mindfulMinutes = uiState.daysActiveThisWeek * 15 // Approximate based on active days
            )
        }

        // Quick Actions (Navigation)
        item(key = "quick_actions") {
            QuickActionsGrid(
                onJournalClick = onNavigateToJournal,
                onHavenClick = onNavigateToHaven,
                onWisdomClick = onNavigateToQuotes,
                onFutureClick = onNavigateToFutureMessage
            )
        }

        // Explore Section - routes to additional features
        item(key = "explore") {
            ExploreSection(
                onMeditationClick = onNavigateToMeditation,
                onChallengesClick = onNavigateToChallenges,
                onMissionsClick = onNavigateToMissions,
                onLearningClick = onNavigateToLearning,
                onDeepDiveClick = onNavigateToDeepDive,
                onVocabularyClick = onNavigateToVocabulary,
                onMicroJournalClick = onNavigateToMicroJournal,
                onDailyRitualClick = onNavigateToDailyRitual
            )
        }

        // Recent Activity - show today's journal status
        item(key = "recent_activity") {
            RecentActivitySection(
                journaledToday = uiState.journaledToday,
                todayMood = uiState.todayEntryMood,
                todayPreview = uiState.todayEntryPreview,
                onJournalClick = onNavigateToJournal
            )
        }
    }
}
