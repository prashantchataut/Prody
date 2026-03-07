package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.domain.intelligence.SurfacedMemory
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.streak.DualStreakStatus
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

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
    onNavigateToSettings: () -> Unit = {},
    onNavigateToIdiomDetail: (Long) -> Unit = {},
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
        contentPadding = PaddingValues(bottom = 80.dp), // Space for bottom nav
        verticalArrangement = Arrangement.spacedBy(ProdyDesignTokens.Spacing.sectionGap)
    ) {
        // AI Configuration Warning (Sticky if missing/error)
        if (uiState.aiConfigurationStatus != AiConfigurationStatus.CONFIGURED) {
            item(key = "ai_warning") {
                AiConfigWarningBanner(
                    status = uiState.aiConfigurationStatus,
                    onClick = onNavigateToSettings
                )
            }
        }

        // Header with Contextual Greeting
        item(key = "greeting") {
            IntelligentGreetingHeader(
                greeting = uiState.intelligentGreeting,
                subtext = uiState.greetingSubtext,
                userName = uiState.userName,
                isStruggling = uiState.isUserStruggling,
                isThriving = uiState.isUserThriving,
                modifier = Modifier.statusBarsPadding().padding(top = 16.dp)
            )
        }

        // Active Progress: Next Action
        uiState.nextAction?.let { nextAction ->
            item(key = "next_action") {
                NextActionCard(
                    nextAction = nextAction,
                    onClick = { route ->
                        when {
                            route.startsWith("journal") -> onNavigateToJournal()
                            route.startsWith("vocabulary") -> onNavigateToVocabulary()
                            route.startsWith("future_message") -> onNavigateToFutureMessage()
                            route.startsWith("quotes") -> onNavigateToQuotes()
                            else -> onNavigateToJournal()
                        }
                    }
                )
            }
        }

        // Dual Streak System
        item(key = "dual_streak") {
            DualStreakCard(
                dualStreakStatus = uiState.dualStreakStatus,
                onTapForDetails = { showStreakDetails = true }
            )
        }

        // Surfaced Memory (Soul Layer Intelligence)
        if (uiState.showMemoryCard) {
            uiState.surfacedMemory?.let { memory ->
                item(key = "surfaced_memory") {
                    SurfacedMemoryCard(
                        memory = memory,
                        onExpand = { /* TODO: Nav to detail */ },
                        onDismiss = { viewModel.dismissMemoryCard() }
                    )
                }
            }
        }

        // Daily Wisdom (Buddha AI)
        item(key = "buddha_wisdom") {
            BuddhaThoughtCard(
                thought = uiState.buddhaThought,
                explanation = uiState.buddhaThoughtExplanation,
                isLoading = uiState.isBuddhaThoughtLoading,
                isAiGenerated = uiState.isBuddhaThoughtAiGenerated,
                proofInfo = uiState.buddhaWisdomProofInfo,
                onRefresh = { viewModel.refreshBuddhaThought() }
            )
        }

        // Weekly Summary Metrics
        item(key = "weekly_summary") {
            WeeklySummarySection(
                journalEntries = uiState.journalEntriesThisWeek,
                wordsLearned = uiState.wordsLearnedThisWeek,
                daysActive = uiState.daysActiveThisWeek
            )
        }

        // Mood Trend Chart (Placeholder until domain layer supports historical mood)
        item(key = "mood_trend") {
            MoodTrendSection(
                moodData = listOf(3f, 4f, 2f, 5f, 4f, 5f, 4f)
            )
        }
        
        // Quick Actions Grid
        item(key = "quick_actions") {
            QuickActionsGrid(
                onJournalClick = onNavigateToJournal,
                onHavenClick = onNavigateToHaven,
                onWisdomClick = onNavigateToQuotes,
                onFutureClick = onNavigateToFutureMessage
            )
        }
        
        // Recent Activity
        item(key = "recent_activity") {
            RecentActivitySection()
        }
    }
}

@Composable
fun MoodTrendSection(moodData: List<Float>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Mood Trend",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = ProdyTextPrimaryLight
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Chart Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            color = ProdySurfaceLight,
            shadowElevation = 4.dp
        ) {
            MoodChart(data = moodData, modifier = Modifier.padding(24.dp))
        }
    }
}

@Composable
fun MoodChart(data: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (data.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1)
        
        // Normalize data to height (1-5 scale)
        val points = data.mapIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - 1) / 4f) * height
            Offset(x, y)
        }

        // Draw Line
        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                // Bezier curve for smoothness
                val p0 = points[i - 1]
                val p1 = points[i]
                val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2, p0.y)
                val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2, p1.y)
                cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
            }
        }

        drawPath(
            path = path,
            color = ProdyForestGreen,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw Points
        points.forEach { point ->
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = point
            )
            drawCircle(
                color = ProdyForestGreen,
                radius = 4.dp.toPx(),
                center = point
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
                label = "Active Days",
                value = daysActive.toString(),
                icon = ProdyIcons.Today,
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
        shape = RoundedCornerShape(12.dp),
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
    onFutureClick: () -> Unit
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
fun QuickActionTile(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
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

@Composable
fun RecentActivitySection() {
    // Placeholder for Recent Activity
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Recent",
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
            shape = RoundedCornerShape(12.dp),
            color = ProdySurfaceLight,
            shadowElevation = 2.dp
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ProdyForestGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(ProdyIcons.Check, null, tint = ProdyForestGreen)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Daily Journal", fontWeight = FontWeight.SemiBold, fontFamily = PoppinsFamily)
                    Text("Completed today", fontSize = 12.sp, color = ProdyTextSecondaryLight, fontFamily = PoppinsFamily)
                }
            }
        }
    }
}
