package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
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
import com.prody.prashant.ui.components.DualStreakCard
import com.prody.prashant.ui.components.IntelligentGreetingHeader
import com.prody.prashant.ui.components.SurfacedMemoryCard
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // AI Warning Banner
        item {
            AiConfigWarningBanner(
                status = uiState.aiConfigurationStatus,
                onConfigureClick = onNavigateToSettings
            )
        }

        // Header with Intelligent Greeting
        item {
            DashboardHeader(
                userName = uiState.userName,
                greeting = uiState.intelligentGreeting,
                subtext = uiState.greetingSubtext,
                onProfileClick = onNavigateToSettings,
                onNotificationClick = {}
            )
        }

        // Active Progress Suggestion (Next Action)
        item {
            NextActionCard(
                nextAction = uiState.nextAction,
                onClick = {
                    when (uiState.nextAction?.type) {
                        com.prody.prashant.domain.progress.NextActionType.START_JOURNAL,
                        com.prody.prashant.domain.progress.NextActionType.FOLLOW_UP_JOURNAL -> onNavigateToJournal()
                        com.prody.prashant.domain.progress.NextActionType.LEARN_WORD,
                        com.prody.prashant.domain.progress.NextActionType.REVIEW_WORDS -> onNavigateToVocabulary()
                        com.prody.prashant.domain.progress.NextActionType.WRITE_FUTURE_MESSAGE -> onNavigateToFutureMessage()
                        com.prody.prashant.domain.progress.NextActionType.REFLECT_ON_QUOTE -> onNavigateToQuotes()
                        com.prody.prashant.domain.progress.NextActionType.COMPLETE_CHALLENGE -> onNavigateToChallenges()
                        null -> {}
                    }
                }
            )
        }

        // Dual Streak Card
        item {
            DualStreakCard(
                dualStreakStatus = uiState.dualStreakStatus,
                onTapForDetails = {} // TODO: Show details dialog
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Buddha's Daily Thought
        item {
            BuddhaThoughtCard(
                thought = uiState.buddhaThought,
                explanation = uiState.buddhaThoughtExplanation,
                isLoading = uiState.isBuddhaThoughtLoading,
                isAiGenerated = uiState.isBuddhaThoughtAiGenerated,
                canRefresh = uiState.canRefreshBuddhaThought,
                proofInfo = uiState.buddhaWisdomProofInfo,
                onRefresh = { viewModel.refreshBuddhaThought() }
            )
        }

        // Surfaced Memory (Magic Moment)
        item {
            uiState.surfacedMemory?.let { memory ->
                if (uiState.showMemoryCard) {
                    SurfacedMemoryCard(
                        memory = memory,
                        onExpand = { viewModel.expandMemoryCard() },
                        onDismiss = { viewModel.dismissMemoryCard() }
                    )
                }
            }
        }

        // Quick Actions Grid (Restored and cleaned up)
        item {
            QuickActionsGrid(
                onJournalClick = onNavigateToJournal,
                onHavenClick = onNavigateToHaven,
                onWisdomClick = onNavigateToQuotes,
                onFutureClick = onNavigateToFutureMessage
            )
        }
    }
}

@Composable
fun DashboardHeader(
    userName: String,
    greeting: String,
    subtext: String,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        IntelligentGreetingHeader(
            greeting = greeting,
            subtext = subtext,
            userName = userName,
            modifier = Modifier.weight(1f)
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = ProdyIcons.Notifications,
                    contentDescription = "Notifications",
                    tint = ProdyTextPrimaryLight
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
            .padding(24.dp)
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
fun QuickActionTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Surface(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
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
