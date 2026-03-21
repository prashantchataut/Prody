package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * Revitalized Home Screen: Performance-first implementation.
 *
 * Optimizations:
 * 1. Binding to HomeUiState from ViewModel (Real Data).
 * 2. collectAsStateWithLifecycle for lifecycle-aware state updates.
 * 3. LazyColumn for efficient scrolling.
 * 4. Modular intelligence components for focused recomposition.
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // AI Config Warning (Conditional)
        item {
            AiConfigWarningBanner(
                status = uiState.aiConfigurationStatus,
                onDismiss = {},
                modifier = Modifier.padding(horizontal = 24.dp).padding(top = 16.dp)
            )
        }

        // Header with Greeting (Soul Layer Integrated)
        item {
            DashboardHeader(
                userName = uiState.userName,
                greeting = uiState.intelligentGreeting.ifEmpty { "Welcome back," },
                subtext = uiState.greetingSubtext.ifEmpty { "Ready for your journey?" },
                onProfileClick = {}, // TODO: Profile Nav
                onNotificationClick = {}
            )
        }

        // Dual Streak System (Confirmed property: dualStreakStatus)
        item {
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                DualStreakCard(
                    dualStreakStatus = uiState.dualStreakStatus,
                    onTapForDetails = { /* Handle streak detail navigation or dialog */ }
                )
            }
        }

        // Next Action (AI contextual suggestion)
        item {
            NextActionCard(
                nextAction = uiState.nextAction,
                onClick = { /* Handle action navigation */ },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }

        // Buddha's Thought (AI generated wisdom)
        item {
            BuddhaThoughtCard(
                thought = uiState.buddhaThought,
                explanation = uiState.buddhaThoughtExplanation,
                isLoading = uiState.isBuddhaThoughtLoading,
                canRefresh = uiState.canRefreshBuddhaThought,
                onRefresh = { viewModel.refreshBuddhaThought() },
                proofInfo = uiState.buddhaWisdomProofInfo,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        // Daily Wisdom Spotlight
        item {
            DailyWisdomSpotlight(
                quote = uiState.dailyQuote,
                author = uiState.dailyQuoteAuthor,
                word = uiState.wordOfTheDay,
                onQuoteClick = onNavigateToQuotes,
                onWordClick = onNavigateToVocabulary
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
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = ProdyTextPrimaryLight
                )
            }
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
fun DailyWisdomSpotlight(
    quote: String,
    author: String,
    word: String,
    onQuoteClick: () -> Unit,
    onWordClick: () -> Unit
) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Daily Wisdom",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            fontFamily = PoppinsFamily
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                modifier = Modifier.weight(1f).clickable { onQuoteClick() },
                shape = RoundedCornerShape(16.dp),
                color = ProdyWarmAmber.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ProdyWarmAmber.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(ProdyIcons.Lightbulb, null, tint = ProdyWarmAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(quote, maxLines = 3, fontSize = 14.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium)
                    Text("— $author", fontSize = 11.sp, color = ProdyTextSecondaryLight)
                }
            }
            Surface(
                modifier = Modifier.weight(1f).clickable { onWordClick() },
                shape = RoundedCornerShape(16.dp),
                color = ProdyInfo.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ProdyInfo.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(ProdyIcons.School, null, tint = ProdyInfo, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Word of the Day", fontSize = 11.sp, color = ProdyTextSecondaryLight)
                    Text(word, fontSize = 18.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, color = ProdyInfo)
                }
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
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Explore Prody",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            fontFamily = PoppinsFamily
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionTile("Journal", ProdyIcons.Book, ProdyForestGreen, onJournalClick, Modifier.weight(1f))
            QuickActionTile("Haven", ProdyIcons.Psychology, ProdyInfo, onHavenClick, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionTile("Wisdom", ProdyIcons.Lightbulb, ProdyWarmAmber, onWisdomClick, Modifier.weight(1f))
            QuickActionTile("Future", ProdyIcons.Send, Color(0xFF9C27B0), onFutureClick, Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionTile(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier.height(90.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = color, fontFamily = PoppinsFamily)
        }
    }
}
