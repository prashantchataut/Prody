package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.School
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
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.ui.components.ProdyIconButton
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * Prody Home Screen - The central hub for the user's growth journey.
 *
 * This screen is fully reactive and binds directly to [HomeViewModel.uiState].
 * Performance is optimized by minimizing recompositions through scoped state reads
 * and modularized components.
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
    val backgroundColor = MaterialTheme.colorScheme.background

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header with Dynamic Greeting
            item {
                DashboardHeader(
                    userName = uiState.userName,
                    greeting = uiState.intelligentGreeting,
                    onProfileClick = { /* Navigate to Profile */ },
                    onNotificationClick = { /* Navigate to Notifications */ }
                )
            }

            // AI Configuration Warning (Conditional)
            if (uiState.aiConfigurationStatus == AiConfigurationStatus.MISSING) {
                item {
                    AiConfigWarningBanner(
                        onClick = { /* Navigate to AI Setup */ }
                    )
                }
            }

            // Dual Streak Overview
            item {
                DualStreakSection(
                    wisdomStreak = uiState.dualStreakStatus.wisdomStreak.current,
                    reflectionStreak = uiState.dualStreakStatus.reflectionStreak.current
                )
            }

            // Contextual Next Action
            uiState.nextAction?.let { action ->
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    NextActionCard(
                        actionTitle = action.title,
                        actionSubtitle = action.subtitle,
                        icon = when (action.type) {
                            NextActionType.START_JOURNAL -> ProdyIcons.Edit
                            NextActionType.REVIEW_WORDS -> Icons.Outlined.School
                            NextActionType.LEARN_WORD -> ProdyIcons.Lightbulb
                            NextActionType.WRITE_FUTURE_MESSAGE -> Icons.AutoMirrored.Outlined.Send
                            else -> Icons.AutoMirrored.Outlined.ArrowForward
                        },
                        onClick = {
                            when (action.type) {
                                NextActionType.START_JOURNAL -> onNavigateToJournal()
                                NextActionType.REVIEW_WORDS -> onNavigateToVocabulary()
                                NextActionType.LEARN_WORD -> onNavigateToVocabulary()
                                NextActionType.WRITE_FUTURE_MESSAGE -> onNavigateToFutureMessage()
                                else -> {}
                            }
                        }
                    )
                }
            }

            // Buddha Wisdom Card
            item {
                Spacer(modifier = Modifier.height(24.dp))
                BuddhaThoughtCard(
                    thought = uiState.buddhaThought,
                    explanation = uiState.buddhaThoughtExplanation,
                    isLoading = uiState.isBuddhaThoughtLoading,
                    isAiGenerated = uiState.isBuddhaThoughtAiGenerated,
                    canRefresh = uiState.canRefreshBuddhaThought,
                    onRefresh = { viewModel.refreshBuddhaThought() },
                    proofInfo = if (uiState.buddhaWisdomProofInfo.isEnabled) uiState.buddhaWisdomProofInfo else null
                )
            }

            // Weekly Summary
            item {
                Spacer(modifier = Modifier.height(24.dp))
                WeeklySummarySection(
                    journalEntries = uiState.journalEntriesThisWeek,
                    wordsLearned = uiState.wordsLearnedThisWeek,
                    mindfulMinutes = 45 // Placeholder for now, could be in state
                )
            }

            // Quick Actions Grid
            item {
                QuickActionsGrid(
                    onJournalClick = onNavigateToJournal,
                    onHavenClick = onNavigateToHaven,
                    onWisdomClick = onNavigateToQuotes,
                    onFutureClick = onNavigateToFutureMessage
                )
            }

            // Surfaced Memory (Conditional)
            if (uiState.showMemoryCard && uiState.surfacedMemory != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SurfacedMemorySection(
                        memoryPreview = uiState.surfacedMemory!!.memory.preview,
                        surfaceReason = uiState.surfacedMemory!!.surfaceReason,
                        onDismiss = { viewModel.dismissMemoryCard() },
                        onClick = { viewModel.expandMemoryCard() }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(
    userName: String,
    greeting: String,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = greeting.ifEmpty { "Welcome back," },
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
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProdyIconButton(
                icon = Icons.Outlined.Notifications,
                onClick = onNotificationClick,
                contentDescription = "Notifications"
            )

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
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily
                )
            }
        }
    }
}

@Composable
fun DualStreakSection(
    wisdomStreak: Int,
    reflectionStreak: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wisdom Streak Card
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = ProdySurfaceLight,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, ProdyOutlineLight.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = wisdomStreak.toString(),
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = ProdyWarmAmber
                    )
                )
                Text(
                    text = "Wisdom",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = ProdyTextSecondaryLight
                    )
                )
            }
        }

        // Reflection Streak Card
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = ProdySurfaceLight,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, ProdyOutlineLight.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = reflectionStreak.toString(),
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = ProdyForestGreen
                    )
                )
                Text(
                    text = "Reflection",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = ProdyTextSecondaryLight
                    )
                )
            }
        }
    }
}

@Composable
fun SurfacedMemorySection(
    memoryPreview: String,
    surfaceReason: String,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = ProdyInfoContainer.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, ProdyInfo.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = surfaceReason,
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = ProdyInfo
                    )
                )
                ProdyIconButton(
                    icon = ProdyIcons.Close,
                    onClick = onDismiss,
                    size = 24.dp,
                    tint = ProdyTextSecondaryLight
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = memoryPreview,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = ProdyTextPrimaryLight,
                maxLines = 2
            )
        }
    }
}

@Composable
fun WeeklySummarySection(journalEntries: Int, wordsLearned: Int, mindfulMinutes: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Your Growth This Week",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = ProdyTextPrimaryLight
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
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = ProdySurfaceLight,
        shadowElevation = 1.dp
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
                    color = ProdyTextPrimaryLight
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = ProdyTextSecondaryLight
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
            .padding(24.dp)
    ) {
        Text(
            text = "Quick Exploration",
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
fun QuickActionTile(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.15f))
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
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = color
                )
            )
        }
    }
}
