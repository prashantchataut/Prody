package com.prody.prashant.ui.screens.home

import com.prody.prashant.ui.icons.ProdyIcons
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.ui.theme.*
import com.prody.prashant.util.rememberProdyHaptic

// =============================================================================
// PERSONALIZATION DASHBOARD - Bolt Optimized
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
    onNavigateToStats: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = rememberProdyHaptic()

    val backgroundColor = MaterialTheme.colorScheme.background

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ProdyForestGreen)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Header with Intelligent Greeting
        item(key = "header") {
            DashboardHeader(
                userName = uiState.userName,
                greeting = uiState.intelligentGreeting.ifEmpty { "Good Morning," },
                subtext = uiState.greetingSubtext.ifEmpty { "Ready for your journey?" },
                onProfileClick = onNavigateToProfile,
                onNotificationClick = { haptic.click() }
            )
        }

        // 2. Next Action Suggestion (High Visibility)
        uiState.nextAction?.let { action ->
            item(key = "next_action") {
                NextActionCard(
                    action = action,
                    onClick = {
                        haptic.click()
                        when (action.type) {
                            NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> onNavigateToJournal()
                            NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> onNavigateToVocabulary()
                            NextActionType.REFLECT_ON_QUOTE -> onNavigateToQuotes()
                            NextActionType.WRITE_FUTURE_MESSAGE -> onNavigateToFutureMessage()
                            else -> {}
                        }
                    }
                )
            }
        }

        // 3. Dual Streak Overview
        item(key = "streaks") {
            DualStreakSection(
                wisdomStreak = uiState.dualStreakStatus.wisdomStreak.current,
                reflectionStreak = uiState.dualStreakStatus.reflectionStreak.current
            )
        }

        // 4. Buddha's Daily Thought (AI Wisdom)
        item(key = "buddha_thought") {
            BuddhaThoughtSection(
                thought = uiState.buddhaThought,
                explanation = uiState.buddhaThoughtExplanation,
                isLoading = uiState.isBuddhaThoughtLoading,
                onRefresh = {
                    haptic.selection()
                    viewModel.refreshBuddhaThought()
                }
            )
        }

        // 5. Weekly Activity Summary
        item(key = "weekly_summary") {
            WeeklySummarySection(
                journalEntries = uiState.journalEntriesThisWeek,
                wordsLearned = uiState.wordsLearnedThisWeek,
                mindfulMinutes = (uiState.journalEntriesThisWeek * 5) // Estimated
            )
        }

        // 6. Daily Wisdom Discovery Row
        item(key = "daily_wisdom_discovery") {
            DailyWisdomDiscoverySection(
                quote = uiState.dailyQuote,
                author = uiState.dailyQuoteAuthor,
                word = uiState.wordOfTheDay,
                proverb = uiState.dailyProverb,
                onNavigateToQuotes = onNavigateToQuotes,
                onNavigateToVocabulary = onNavigateToVocabulary,
                onNavigateToProverbs = onNavigateToProverbs
            )
        }
        
        // 7. Quick Actions Grid
        item(key = "quick_actions") {
            QuickActionsGrid(
                onJournalClick = onNavigateToJournal,
                onHavenClick = onNavigateToHaven,
                onWisdomClick = onNavigateToQuotes,
                onFutureClick = onNavigateToFutureMessage,
                onSearchClick = onNavigateToSearch
            )
        }
        
        // 8. Recent Activity
        item(key = "recent_activity") {
            RecentActivitySection(
                journaledToday = uiState.journaledToday,
                todayMood = uiState.todayEntryMood,
                todayPreview = uiState.todayEntryPreview
            )
        }

        // Spacer for bottom navigation
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// =============================================================================
// DASHBOARD COMPONENTS
// =============================================================================

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
        Column(modifier = Modifier.weight(1f)) {
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
                color = ProdyTextPrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtext,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                ),
                color = ProdyForestGreen.copy(alpha = 0.8f)
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = ProdyTextPrimaryLight
                )
            }
            // Avatar / Profile
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ProdyForestGreen)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.toString() ?: "P",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun NextActionCard(
    action: NextAction,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = ProdyForestGreen,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(action.type) {
                        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> Icons.Default.Edit
                        NextActionType.REVIEW_WORDS, NextActionType.LEARN_WORD -> Icons.Default.School
                        NextActionType.REFLECT_ON_QUOTE -> Icons.Default.Lightbulb
                        NextActionType.WRITE_FUTURE_MESSAGE -> Icons.Default.Send
                        else -> Icons.Default.Star
                    },
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = action.subtitle,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White
            )
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
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Reflection Streak Card
        StreakCard(
            count = reflectionStreak,
            label = "Reflection",
            icon = Icons.Default.Book,
            color = ProdyForestGreen,
            modifier = Modifier.weight(1f)
        )

        // Wisdom Streak Card
        StreakCard(
            count = wisdomStreak,
            label = "Wisdom",
            icon = Icons.Default.Lightbulb,
            color = ProdyWarmAmber,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StreakCard(
    count: Int,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = ProdySurfaceLight,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
                text = count.toString(),
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = color
                )
            )
            Text(
                text = "$label Streak",
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

@Composable
fun BuddhaThoughtSection(
    thought: String,
    explanation: String,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFFFDF7E7), // Warm wisdom paper color
        border = androidx.compose.foundation.BorderStroke(1.dp, ProdyWarmAmber.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Buddha Wisdom",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    ),
                    color = ProdyWarmAmber
                )

                IconButton(onClick = onRefresh, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = ProdyWarmAmber,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = ProdyWarmAmber)
                }
            } else {
                Text(
                    text = "“$thought”",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        lineHeight = 28.sp
                    ),
                    color = ProdyTextPrimaryLight,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                if (explanation.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = explanation,
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        ),
                        color = ProdyTextSecondaryLight,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun DailyWisdomDiscoverySection(
    quote: String,
    author: String,
    word: String,
    proverb: String,
    onNavigateToQuotes: () -> Unit,
    onNavigateToVocabulary: () -> Unit,
    onNavigateToProverbs: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Discover Wisdom",
            modifier = Modifier.padding(horizontal = 24.dp),
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = ProdyTextPrimaryLight
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quote Card
            item {
                WisdomDiscoveryCard(
                    title = "Daily Quote",
                    content = quote,
                    subContent = author,
                    icon = Icons.Default.FormatQuote,
                    color = ProdyInfo,
                    onClick = onNavigateToQuotes
                )
            }

            // Word Card
            item {
                WisdomDiscoveryCard(
                    title = "Word of Day",
                    content = word,
                    subContent = "Expand vocabulary",
                    icon = Icons.Default.School,
                    color = ProdyForestGreen,
                    onClick = onNavigateToVocabulary
                )
            }

            // Proverb Card
            item {
                WisdomDiscoveryCard(
                    title = "Daily Proverb",
                    content = proverb,
                    subContent = "Ancient wisdom",
                    icon = Icons.Default.MenuBook,
                    color = ProdyWarmAmber,
                    onClick = onNavigateToProverbs
                )
            }
        }
    }
}

@Composable
fun WisdomDiscoveryCard(
    title: String,
    content: String,
    subContent: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(200.dp)
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = ProdyTextPrimaryLight
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = subContent,
                fontSize = 11.sp,
                color = ProdyTextSecondaryLight
            )
        }
    }
}

@Composable
fun WeeklySummarySection(journalEntries: Int, wordsLearned: Int, mindfulMinutes: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Your Progress This Week",
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
                label = "Mindful Min",
                value = mindfulMinutes.toString(),
                icon = ProdyIcons.SelfImprovement,
                color = ProdyInfo,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = ProdySurfaceLight,
        shadowElevation = 2.dp
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
                    fontSize = 12.sp,
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
    onFutureClick: () -> Unit,
    onSearchClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
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
fun QuickActionTile(title: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
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

@Composable
fun RecentActivitySection(
    journaledToday: Boolean,
    todayMood: String,
    todayPreview: String
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = "Recent Activity",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = ProdyTextPrimaryLight
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = ProdySurfaceLight,
            shadowElevation = 2.dp
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (journaledToday) ProdyForestGreen.copy(alpha = 0.1f)
                            else ProdyTextTertiaryLight.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (journaledToday) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (journaledToday) ProdyForestGreen else ProdyTextTertiaryLight
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (journaledToday) "Journal Entry Saved" else "No Entry Today",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PoppinsFamily
                    )
                    Text(
                        text = if (journaledToday) {
                            if (todayPreview.isNotEmpty()) todayPreview else "You reflected on your day"
                        } else "Consistency is key to growth",
                        fontSize = 12.sp,
                        color = ProdyTextSecondaryLight,
                        fontFamily = PoppinsFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
