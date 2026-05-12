package com.prody.prashant.ui.screens.home

import com.prody.prashant.ui.icons.ProdyIcons
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.R

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.ui.theme.*
import com.prody.prashant.ui.components.*

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
                ProdyOutlinedButton(
                    text = "Retry",
                    onClick = { viewModel.retry() }
                )
            }
        }
        return
    }

    // Determine greeting based on ViewModel state
    val greeting = remember(uiState.intelligentGreeting) {
        uiState.intelligentGreeting.ifEmpty {
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
            // 1. Active Progress Layer - Next Action
            uiState.nextAction?.let { add(DashboardItem.NextAction(it)) }

            // 2. Dual Streak Card
            add(DashboardItem.Streak(uiState.dualStreakStatus))

            // 3. Today's Progress Summary
            add(DashboardItem.TodayProgress(uiState.todayProgress))

            // 4. Seed Status
            uiState.dailySeed?.let { add(DashboardItem.Seed(it)) }

            // 5. Soul Layer - Surfaced Memory
            if (uiState.showMemoryCard) {
                uiState.surfacedMemory?.let { add(DashboardItem.Memory(it)) }
            }

            // 6. Soul Layer - Anniversary Memories
            uiState.anniversaryMemories.forEach { add(DashboardItem.Anniversary(it)) }

            // 7. Intelligence Insights
            if (uiState.isPremiumIntelligenceEnabled && uiState.intelligenceInsights.isNotEmpty()) {
                add(DashboardItem.Insight(uiState.intelligenceInsights.first()))
            }

            // 8. Quick Actions
            add(DashboardItem.QuickActions)

            // 9. Explore Section
            add(DashboardItem.Explore)
        }
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

        // Dynamic Dashboard Items with Staggered Entrance
        itemsIndexed(
            items = dashboardItems,
            key = { index, item ->
                when (item) {
                    is DashboardItem.NextAction -> "next_action_${item.action.type}"
                    is DashboardItem.Streak -> "dual_streak"
                    is DashboardItem.TodayProgress -> "today_progress"
                    is DashboardItem.Seed -> "daily_seed"
                    is DashboardItem.Memory -> "memory_${item.memory.memory.id}"
                    is DashboardItem.Anniversary -> "anniversary_${item.anniversary.memory.id}"
                    is DashboardItem.Insight -> "insight_${item.insight.title}"
                    DashboardItem.QuickActions -> "quick_actions"
                    DashboardItem.Explore -> "explore"
                }
            }
        ) { index, item ->
            StaggeredEntrance(index = index) {
                Box(modifier = Modifier.padding(bottom = 16.dp)) {
                    when (item) {
                        is DashboardItem.NextAction -> {
                            NextActionCard(
                                nextAction = item.action,
                                onClick = {
                                    when (item.action.type) {
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
                        is DashboardItem.Streak -> {
                            DualStreakCard(dualStreakStatus = item.status)
                        }
                        is DashboardItem.TodayProgress -> {
                            TodayProgressCard(progress = item.progress)
                        }
                        is DashboardItem.Seed -> {
                            SeedStatusCard(
                                seed = item.seed,
                                onClick = onNavigateToMissions
                            )
                        }
                        is DashboardItem.Memory -> {
                            SurfacedMemoryCard(
                                memory = item.memory,
                                onExpand = { viewModel.expandMemoryCard() },
                                onDismiss = { viewModel.dismissMemoryCard() }
                            )
                        }
                        is DashboardItem.Anniversary -> {
                            AnniversaryMemoryCard(
                                anniversary = item.anniversary,
                                onView = { onNavigateToJournal() },
                                onDismiss = { /* Local dismiss if implemented in VM */ }
                            )
                        }
                        is DashboardItem.Insight -> {
                            IntelligenceInsightCard(
                                insight = item.insight,
                                onActionClick = { /* Handle action */ }
                            )
                        }
                        DashboardItem.QuickActions -> {
                            QuickActionsGrid(
                                onJournalClick = onNavigateToJournal,
                                onHavenClick = onNavigateToHaven,
                                onWisdomClick = onNavigateToQuotes,
                                onFutureClick = onNavigateToFutureMessage
                            )
                        }
                        DashboardItem.Explore -> {
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
                    }
                }
            }
        }
    }
}

// =============================================================================
// DASHBOARD MODELS
// =============================================================================

sealed class DashboardItem {
    data class NextAction(val action: com.prody.prashant.domain.progress.NextAction) : DashboardItem()
    data class Streak(val status: com.prody.prashant.domain.streak.DualStreakStatus) : DashboardItem()
    data class TodayProgress(val progress: com.prody.prashant.domain.progress.TodayProgress) : DashboardItem()
    data class Seed(val seed: com.prody.prashant.data.local.entity.SeedEntity) : DashboardItem()
    data class Memory(val memory: com.prody.prashant.domain.intelligence.SurfacedMemory) : DashboardItem()
    data class Anniversary(val anniversary: com.prody.prashant.domain.intelligence.AnniversaryMemory) : DashboardItem()
    data class Insight(val insight: com.prody.prashant.domain.intelligence.IntelligenceInsight) : DashboardItem()
    data object QuickActions : DashboardItem()
    data object Explore : DashboardItem()
}

// =============================================================================
// DASHBOARD COMPONENTS
// =============================================================================

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
            Text(
                text = greeting,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                color = ProdyTextSecondaryLight
            )
            Text(
                text = userName,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = ProdyTextPrimaryLight
            )
        }
        
        val profileContentDescription = stringResource(R.string.cd_profile_picture)

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProdyIconButton(
                icon = Icons.Outlined.Notifications,
                onClick = onNotificationClick,
                contentDescription = "Notifications",
                tint = ProdyTextPrimaryLight
            )
            // Avatar / Profile
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ProdyForestGreen)
                    .clickable { onProfileClick() }
                    .semantics {
                        role = Role.Button
                        contentDescription = profileContentDescription
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.toString() ?: "P",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
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
                color = ProdyTextPrimaryLight
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Journal
            QuickActionTile(
                title = "Journal",
                icon = ProdyIcons.Book,
                color = ProdyForestGreen,
                onClick = onJournalClick,
                modifier = Modifier.weight(1f)
            )
            // Haven
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
            // Wisdom
            QuickActionTile(
                title = "Wisdom",
                icon = ProdyIcons.Lightbulb,
                color = ProdyWarmAmber,
                onClick = onWisdomClick,
                modifier = Modifier.weight(1f)
            )
            // Future
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
        modifier = modifier.height(100.dp),
        backgroundColor = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
fun ExploreSection(
    onMeditationClick: () -> Unit,
    onChallengesClick: () -> Unit,
    onMissionsClick: () -> Unit,
    onLearningClick: () -> Unit,
    onDeepDiveClick: () -> Unit,
    onVocabularyClick: () -> Unit,
    onMicroJournalClick: () -> Unit,
    onDailyRitualClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Explore",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = ProdyTextPrimaryLight
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ExploreChip(
                    title = "Meditation",
                    icon = ProdyIcons.SelfImprovement,
                    color = ProdyInfo,
                    onClick = onMeditationClick
                )
            }
            item {
                ExploreChip(
                    title = "Challenges",
                    icon = ProdyIcons.EmojiEvents,
                    color = ProdyWarmAmber,
                    onClick = onChallengesClick
                )
            }
            item {
                ExploreChip(
                    title = "Missions",
                    icon = Icons.Outlined.Flag,
                    color = ProdyForestGreen,
                    onClick = onMissionsClick
                )
            }
            item {
                ExploreChip(
                    title = "Learning",
                    icon = ProdyIcons.School,
                    color = Color(0xFF2196F3),
                    onClick = onLearningClick
                )
            }
            item {
                ExploreChip(
                    title = "Deep Dive",
                    icon = ProdyIcons.Psychology,
                    color = Color(0xFF9C27B0),
                    onClick = onDeepDiveClick
                )
            }
            item {
                ExploreChip(
                    title = "Vocabulary",
                    icon = ProdyIcons.Book,
                    color = Color(0xFFFF9800),
                    onClick = onVocabularyClick
                )
            }
            item {
                ExploreChip(
                    title = "Quick Note",
                    icon = ProdyIcons.Edit,
                    color = ProdyForestGreen.copy(alpha = 0.8f),
                    onClick = onMicroJournalClick
                )
            }
            item {
                ExploreChip(
                    title = "Daily Ritual",
                    icon = Icons.Outlined.Spa,
                    color = ProdyInfo.copy(alpha = 0.8f),
                    onClick = onDailyRitualClick
                )
            }
        }
    }
}

@Composable
private fun ExploreChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    ProdyClickableCard(
        onClick = onClick,
        backgroundColor = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = color
                )
            )
        }
    }
}

@Composable
fun IntelligenceInsightCard(
    insight: com.prody.prashant.domain.intelligence.IntelligenceInsight,
    onActionClick: () -> Unit
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        backgroundColor = ProdySurfaceLight
    ) {
        Column(
            modifier = Modifier
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            ProdyForestGreen.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ProdyForestGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = ProdyForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Column {
                    Text(
                        text = "Identity Insight",
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = ProdyForestGreen
                        )
                    )
                    Text(
                        text = insight.title,
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ProdyTextPrimaryLight
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = insight.description,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = ProdyTextSecondaryLight
                )
            )
            
            if (insight.actionable != null) {
                Spacer(modifier = Modifier.height(20.dp))
                
                ProdyPrimaryButton(
                    text = insight.actionable!!,
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(),
                    size = ProdyButtonSize.MEDIUM
                )
            }
        }
    }
}
