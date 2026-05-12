package com.prody.prashant.ui.screens.home

import com.prody.prashant.ui.icons.ProdyIcons
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.R

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.ui.theme.*
import com.prody.prashant.ui.components.*
import com.prody.prashant.domain.progress.NextActionType

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Intelligent Greeting Header
        item(key = "header") {
            StaggeredEntrance(index = 0) {
                IntelligentGreetingHeader(
                    greeting = uiState.intelligentGreeting,
                    subtext = uiState.greetingSubtext,
                    userName = uiState.userName,
                    isStruggling = uiState.isUserStruggling,
                    isThriving = uiState.isUserThriving,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                )
            }
        }

        // 2. Active Progress Layer - Next Action
        uiState.nextAction?.let { action ->
            item(key = "next_action") {
                StaggeredEntrance(index = 1) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                        Text(
                            text = "Next for you",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        NextActionCard(
                            nextAction = action,
                            onClick = {
                                when (action.type) {
                                    NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> onNavigateToJournal()
                                    NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> onNavigateToVocabulary()
                                    NextActionType.WRITE_FUTURE_MESSAGE -> onNavigateToFutureMessage()
                                    NextActionType.REFLECT_ON_QUOTE -> onNavigateToQuotes()
                                    NextActionType.COMPLETE_CHALLENGE -> onNavigateToChallenges()
                                }
                            }
                        )
                    }
                }
            }
        }

        // 3. Today's Progress summary
        item(key = "today_progress") {
            StaggeredEntrance(index = 2) {
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    TodayProgressCard(progress = uiState.todayProgress)
                }
            }
        }

        // 4. Dual Streak System
        item(key = "dual_streak") {
            StaggeredEntrance(index = 3) {
                Box(modifier = Modifier.padding(vertical = 8.dp)) {
                    DualStreakCard(
                        dualStreakStatus = uiState.dualStreakStatus,
                        onTapForDetails = { /* Show details dialog if needed */ }
                    )
                }
            }
        }

        // 5. Seed Status (if available)
        uiState.dailySeed?.let { seed ->
            item(key = "daily_seed") {
                StaggeredEntrance(index = 4) {
                    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                        SeedStatusCard(
                            seed = seed,
                            onClick = { /* Navigate to seed detail */ }
                        )
                    }
                }
            }
        }

        // 6. Soul Layer - Surfaced Memory
        if (uiState.showMemoryCard) {
            uiState.surfacedMemory?.let { memory ->
                item(key = "surfaced_memory") {
                    StaggeredEntrance(index = 5) {
                        SurfacedMemoryCard(
                            memory = memory,
                            onExpand = { viewModel.expandMemoryCard() },
                            onDismiss = { viewModel.dismissMemoryCard() },
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // 7. Soul Layer - First Week Journey
        if (uiState.isInFirstWeek) {
            item(key = "first_week") {
                StaggeredEntrance(index = 6) {
                    FirstWeekProgressCard(
                        dayNumber = uiState.firstWeekDayNumber,
                        progress = uiState.firstWeekProgress,
                        dayContent = uiState.firstWeekDayContent,
                        onContinue = { /* Handle first week action */ },
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // 8. Quick Actions (Navigation)
        item(key = "quick_actions") {
            StaggeredEntrance(index = 7) {
                QuickActionsGrid(
                    onJournalClick = onNavigateToJournal,
                    onHavenClick = onNavigateToHaven,
                    onWisdomClick = onNavigateToQuotes,
                    onFutureClick = onNavigateToFutureMessage
                )
            }
        }

        // 9. Personalized Pattern Card
        if (uiState.personalizedPatternText.isNotEmpty()) {
            item(key = "personalized_pattern") {
                StaggeredEntrance(index = 8) {
                    PersonalizedPatternCard(
                        patternText = uiState.personalizedPatternText,
                        patternSuggestion = uiState.personalizedPatternSuggestion
                    )
                }
            }
        }

        // 10. Explore Section
        item(key = "explore") {
            StaggeredEntrance(index = 9) {
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

        // 11. Anniversary Memories
        if (uiState.anniversaryMemories.isNotEmpty()) {
            itemsIndexed(uiState.anniversaryMemories, key = { _, m -> "anniversary_${m.memory.id}" }) { i, memory ->
                StaggeredEntrance(index = 10 + i) {
                    AnniversaryMemoryCard(
                        anniversary = memory,
                        onView = { /* Revisit memory */ },
                        onDismiss = { /* Dismiss */ },
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    // Celebration Dialog (if triggered)
    if (uiState.showFirstWeekCelebration) {
        uiState.firstWeekCelebration?.let { celebration ->
            CelebrationDialog(
                celebration = celebration,
                onDismiss = { viewModel.dismissFirstWeekCelebration() }
            )
        }
    }
}

// =============================================================================
// RE-USED DASHBOARD COMPONENTS
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
            .padding(horizontal = 24.dp, vertical = 16.dp)
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
            .padding(horizontal = 24.dp, vertical = 16.dp)
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
                    icon = Icons.Filled.Flag,
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
                    icon = Icons.Filled.Spa,
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
private fun PersonalizedPatternCard(
    patternText: String,
    patternSuggestion: String
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        backgroundColor = ProdySurfaceLight
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.Lightbulb,
                    contentDescription = null,
                    tint = ProdyWarmAmber,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Your Pattern",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = ProdyTextPrimaryLight
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = patternText,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = ProdyTextPrimaryLight,
                    lineHeight = 20.sp
                )
            )
            if (patternSuggestion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = patternSuggestion,
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = ProdyTextSecondaryLight,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}
