package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.ui.theme.*
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.domain.progress.NextActionType

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
                ProdyOutlinedButton(
                    text = "Retry",
                    onClick = { viewModel.retry() }
                )
            }
        }
        return
    }

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

    val dashboardItems = remember(uiState) {
        mutableListOf<DashboardItem>().apply {
            uiState.nextAction?.let { add(DashboardItem.NextAction(it)) }
            add(DashboardItem.DualStreak)
            add(DashboardItem.TodayProgress)
            uiState.surfacedMemory?.let { add(DashboardItem.SurfacedMemory(it)) }
            if (uiState.anniversaryMemories.isNotEmpty()) {
                add(DashboardItem.AnniversaryMemory(uiState.anniversaryMemories.first()))
            }
            if (uiState.isInFirstWeek) {
                add(DashboardItem.FirstWeekProgress)
            }
            if (uiState.weeklyMoodTrend.isNotEmpty()) {
                add(DashboardItem.MoodTrend)
            }
            add(DashboardItem.SeedStatus)
            add(DashboardItem.WeeklySummary)
            add(DashboardItem.QuickActions)
            add(DashboardItem.Explore)
            add(DashboardItem.RecentActivity)
            if (uiState.isPremiumIntelligenceEnabled && uiState.intelligenceInsights.isNotEmpty()) {
                add(DashboardItem.IntelligenceInsight(uiState.intelligenceInsights.first()))
            }
            if (uiState.personalizedPatternText.isNotEmpty()) {
                add(DashboardItem.PersonalizedPattern)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item(key = "header") {
            DashboardHeader(
                userName = uiState.userName,
                greeting = greeting,
                onProfileClick = { /* Profile click logic */ },
                onNotificationClick = onNavigateToSearch
            )
        }

        itemsIndexed(
            items = dashboardItems,
            key = { _, item -> item.key }
        ) { index, item ->
            StaggeredEntrance(index = index) {
                Box(modifier = Modifier.padding(bottom = 16.dp)) {
                    when (item) {
                        is DashboardItem.NextAction -> {
                            NextActionCard(
                                nextAction = item.action,
                                onClick = {
                                    when (item.action.type) {
                                        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> onNavigateToJournal()
                                        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> onNavigateToVocabulary()
                                        NextActionType.WRITE_FUTURE_MESSAGE -> onNavigateToFutureMessage()
                                        NextActionType.REFLECT_ON_QUOTE -> onNavigateToQuotes()
                                        NextActionType.COMPLETE_CHALLENGE -> onNavigateToChallenges()
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        is DashboardItem.DualStreak -> {
                            DualStreakCard(
                                dualStreakStatus = uiState.dualStreakStatus,
                                onTapForDetails = { /* Details dialog */ }
                            )
                        }
                        is DashboardItem.TodayProgress -> {
                            TodayProgressCard(
                                progress = uiState.todayProgress,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        is DashboardItem.SurfacedMemory -> {
                            SurfacedMemoryCard(
                                memory = item.memory,
                                onExpand = { /* Navigate to memory */ },
                                onDismiss = { viewModel.dismissMemoryCard() },
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        is DashboardItem.AnniversaryMemory -> {
                            AnniversaryMemoryCard(
                                anniversary = item.memory,
                                onView = { /* View anniversary */ },
                                onDismiss = { /* Dismiss logic */ },
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        is DashboardItem.FirstWeekProgress -> {
                            FirstWeekProgressCard(
                                dayNumber = uiState.firstWeekDayNumber,
                                progress = uiState.firstWeekProgress,
                                dayContent = uiState.firstWeekDayContent,
                                onContinue = { /* Continue journey */ },
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        is DashboardItem.MoodTrend -> {
                            MoodTrendSection(moodData = uiState.weeklyMoodTrend)
                        }
                        is DashboardItem.SeedStatus -> {
                            SeedStatusCard(
                                seed = uiState.dailySeed,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        is DashboardItem.WeeklySummary -> {
                            WeeklySummarySection(
                                journalEntries = uiState.journalEntriesThisWeek,
                                wordsLearned = uiState.wordsLearnedThisWeek,
                                mindfulMinutes = uiState.daysActiveThisWeek * 15
                            )
                        }
                        is DashboardItem.QuickActions -> {
                            QuickActionsGrid(
                                onJournalClick = onNavigateToJournal,
                                onHavenClick = onNavigateToHaven,
                                onWisdomClick = onNavigateToQuotes,
                                onFutureClick = onNavigateToFutureMessage
                            )
                        }
                        is DashboardItem.Explore -> {
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
                        is DashboardItem.RecentActivity -> {
                            RecentActivitySection(
                                journaledToday = uiState.journaledToday,
                                todayMood = uiState.todayEntryMood,
                                todayPreview = uiState.todayEntryPreview,
                                onClick = onNavigateToJournal
                            )
                        }
                        is DashboardItem.IntelligenceInsight -> {
                            IntelligenceInsightCard(
                                insight = item.insight,
                                onActionClick = { /* Handle action */ }
                            )
                        }
                        is DashboardItem.PersonalizedPattern -> {
                            PersonalizedPatternCard(
                                patternText = uiState.personalizedPatternText,
                                patternSuggestion = uiState.personalizedPatternSuggestion
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class DashboardItem(val key: String) {
    data class NextAction(val action: com.prody.prashant.domain.progress.NextAction) : DashboardItem("next_action")
    object DualStreak : DashboardItem("dual_streak")
    object TodayProgress : DashboardItem("today_progress")
    data class SurfacedMemory(val memory: com.prody.prashant.domain.intelligence.SurfacedMemory) : DashboardItem("surfaced_memory")
    data class AnniversaryMemory(val memory: com.prody.prashant.domain.intelligence.AnniversaryMemory) : DashboardItem("anniversary_memory")
    object FirstWeekProgress : DashboardItem("first_week")
    object MoodTrend : DashboardItem("mood_trend")
    object SeedStatus : DashboardItem("seed_status")
    object WeeklySummary : DashboardItem("weekly_summary")
    object QuickActions : DashboardItem("quick_actions")
    object Explore : DashboardItem("explore")
    object RecentActivity : DashboardItem("recent_activity")
    data class IntelligenceInsight(val insight: com.prody.prashant.domain.intelligence.IntelligenceInsight) : DashboardItem("intelligence_insight")
    object PersonalizedPattern : DashboardItem("personalized_pattern")
}

@Composable
fun DashboardHeader(
    userName: String,
    greeting: String = "Good Morning,",
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = greeting, style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp), color = ProdyTextSecondaryLight)
            Text(text = userName, style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp), color = ProdyTextPrimaryLight)
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ProdyIconButton(icon = Icons.Outlined.Notifications, onClick = onNotificationClick, contentDescription = "Notifications", tint = ProdyTextPrimaryLight)
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(ProdyForestGreen).clickable { onProfileClick() }
                    .semantics { role = Role.Button; contentDescription = "Profile" },
                contentAlignment = Alignment.Center
            ) {
                Text(text = userName.firstOrNull()?.toString() ?: "P", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WeeklySummarySection(journalEntries: Int, wordsLearned: Int, mindfulMinutes: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Text(text = "This Week", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = ProdyTextPrimaryLight))
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(label = "Entries", value = journalEntries.toString(), icon = ProdyIcons.Edit, color = ProdyForestGreen, modifier = Modifier.weight(1f))
            SummaryCard(label = "Words", value = wordsLearned.toString(), icon = ProdyIcons.School, color = ProdyWarmAmber, modifier = Modifier.weight(1f))
            SummaryCard(label = "Minutes", value = mindfulMinutes.toString(), icon = ProdyIcons.SelfImprovement, color = ProdyInfo, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SummaryCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    ProdyCard(modifier = modifier, backgroundColor = ProdySurfaceLight) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ProdyTextPrimaryLight))
            Text(text = label, style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, color = ProdyTextSecondaryLight))
        }
    }
}

@Composable
fun QuickActionsGrid(onJournalClick: () -> Unit, onHavenClick: () -> Unit, onWisdomClick: () -> Unit, onFutureClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Text(text = "Quick Actions", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = ProdyTextPrimaryLight))
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionTile(title = "Journal", icon = ProdyIcons.Book, color = ProdyForestGreen, onClick = onJournalClick, modifier = Modifier.weight(1f))
            QuickActionTile(title = "Haven", icon = ProdyIcons.Psychology, color = ProdyInfo, onClick = onHavenClick, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionTile(title = "Wisdom", icon = ProdyIcons.Lightbulb, color = ProdyWarmAmber, onClick = onWisdomClick, modifier = Modifier.weight(1f))
            QuickActionTile(title = "Future", icon = ProdyIcons.Send, color = Color(0xFF9C27B0), onClick = onFutureClick, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionTile(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    ProdyClickableCard(onClick = onClick, modifier = modifier.height(100.dp), backgroundColor = color.copy(alpha = 0.1f)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = color))
        }
    }
}

@Composable
fun ExploreSection(onMeditationClick: () -> Unit, onChallengesClick: () -> Unit, onMissionsClick: () -> Unit, onLearningClick: () -> Unit, onDeepDiveClick: () -> Unit, onVocabularyClick: () -> Unit, onMicroJournalClick: () -> Unit, onDailyRitualClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Text(text = "Explore", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = ProdyTextPrimaryLight))
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item { ExploreChip(title = "Meditation", icon = ProdyIcons.SelfImprovement, color = ProdyInfo, onClick = onMeditationClick) }
            item { ExploreChip(title = "Challenges", icon = ProdyIcons.EmojiEvents, color = ProdyWarmAmber, onClick = onChallengesClick) }
            item { ExploreChip(title = "Missions", icon = Icons.Filled.Flag, color = ProdyForestGreen, onClick = onMissionsClick) }
            item { ExploreChip(title = "Learning", icon = ProdyIcons.School, color = Color(0xFF2196F3), onClick = onLearningClick) }
            item { ExploreChip(title = "Deep Dive", icon = ProdyIcons.Psychology, color = Color(0xFF9C27B0), onClick = onDeepDiveClick) }
            item { ExploreChip(title = "Vocabulary", icon = ProdyIcons.Book, color = Color(0xFFFF9800), onClick = onVocabularyClick) }
            item { ExploreChip(title = "Quick Note", icon = ProdyIcons.Edit, color = ProdyForestGreen.copy(alpha = 0.8f), onClick = onMicroJournalClick) }
            item { ExploreChip(title = "Daily Ritual", icon = Icons.Filled.Spa, color = ProdyInfo.copy(alpha = 0.8f), onClick = onDailyRitualClick) }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ExploreChip(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    ProdyClickableCard(onClick = onClick, backgroundColor = color.copy(alpha = 0.1f)) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Text(text = title, style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = color))
        }
    }
}

@Composable
fun RecentActivitySection(journaledToday: Boolean = false, todayMood: String = "", todayPreview: String = "", onClick: () -> Unit = {}) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(text = "Today", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = ProdyTextPrimaryLight))
        Spacer(modifier = Modifier.height(16.dp))
        ProdyClickableCard(onClick = onClick, modifier = Modifier.fillMaxWidth(), backgroundColor = ProdySurfaceLight) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (journaledToday) ProdyForestGreen.copy(alpha = 0.1f) else ProdyWarmAmber.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(imageVector = if (journaledToday) ProdyIcons.Check else ProdyIcons.Edit, contentDescription = null, tint = if (journaledToday) ProdyForestGreen else ProdyWarmAmber)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = if (journaledToday) "Journal Entry" else "Daily Journal", fontWeight = FontWeight.SemiBold, fontFamily = PoppinsFamily)
                    Text(text = when { journaledToday && todayMood.isNotEmpty() -> "Feeling ${todayMood.lowercase().replaceFirstChar { it.uppercase() }}"; journaledToday -> "Completed today"; else -> "Tap to write your thoughts" }, fontSize = 12.sp, color = ProdyTextSecondaryLight, fontFamily = PoppinsFamily, maxLines = 1)
                    if (journaledToday && todayPreview.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = todayPreview, fontSize = 11.sp, color = ProdyTextSecondaryLight.copy(alpha = 0.7f), fontFamily = PoppinsFamily, maxLines = 2)
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalizedPatternCard(patternText: String, patternSuggestion: String) {
    ProdyCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), backgroundColor = ProdySurfaceLight) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = ProdyIcons.Lightbulb, contentDescription = null, tint = ProdyWarmAmber, modifier = Modifier.size(20.dp))
                Text(text = "Your Pattern", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ProdyTextPrimaryLight))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = patternText, style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, color = ProdyTextPrimaryLight, lineHeight = 20.sp))
            if (patternSuggestion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = patternSuggestion, style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp, color = ProdyTextSecondaryLight, lineHeight = 18.sp))
            }
        }
    }
}

@Composable
fun IntelligenceInsightCard(insight: com.prody.prashant.domain.intelligence.IntelligenceInsight, onActionClick: () -> Unit) {
    ProdyCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), backgroundColor = ProdySurfaceLight) {
        Column(modifier = Modifier.background(Brush.verticalGradient(colors = listOf(ProdyForestGreen.copy(alpha = 0.05f), Color.Transparent))).padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(ProdyForestGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ProdyForestGreen, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(text = "Identity Insight", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = ProdyForestGreen))
                    Text(text = insight.title, style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ProdyTextPrimaryLight))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = insight.description, style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp, color = ProdyTextSecondaryLight))
            if (insight.actionable != null) {
                Spacer(modifier = Modifier.height(20.dp))
                ProdyPrimaryButton(text = insight.actionable!!, onClick = onActionClick, modifier = Modifier.fillMaxWidth(), size = ProdyButtonSize.MEDIUM)
            }
        }
    }
}
