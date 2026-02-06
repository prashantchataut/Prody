package com.prody.prashant.ui.screens.home

import com.prody.prashant.ui.icons.ProdyIcons
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.intelligence.SurfacedMemory
import com.prody.prashant.domain.streak.DualStreakStatus
import com.prody.prashant.domain.streak.StreakInfo
import com.prody.prashant.domain.streak.StreakType
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
    onNavigateToIdiomDetail: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ProdyForestGreen)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav
        ) {
            // Header with Intelligent Greeting
            item(key = "header") {
                DashboardHeader(
                    userName = uiState.userName,
                    greeting = uiState.intelligentGreeting,
                    subtext = uiState.greetingSubtext,
                    onProfileClick = { /* TODO: Profile Nav */ },
                    onNotificationClick = { /* TODO: Notification Nav */ }
                )
            }

            // Overview Section (Dual Streaks)
            item(key = "streaks") {
                OverviewSection(
                    dualStreakStatus = uiState.dualStreakStatus,
                    totalPoints = uiState.totalPoints
                )
            }

            // Next Action Section
            uiState.nextAction?.let { action ->
                item(key = "next_action") {
                    NextActionCard(
                        action = action,
                        onClick = {
                            // Handle navigation based on actionRoute
                            when (action.actionRoute) {
                                "journal/new" -> onNavigateToJournal()
                                "vocabulary" -> onNavigateToVocabulary()
                                "quotes" -> onNavigateToQuotes()
                                "future_message/write" -> onNavigateToFutureMessage()
                            }
                        }
                    )
                }
            }

            // Seed -> Bloom Mechanic
            uiState.dailySeed?.let { seed ->
                item(key = "daily_seed") {
                    DailySeedCard(seed = seed)
                }
            }

            // Weekly Summary
            item(key = "weekly_summary") {
                WeeklySummarySection(
                    journalEntries = uiState.journalEntriesThisWeek,
                    wordsLearned = uiState.wordsLearnedThisWeek,
                    daysActive = uiState.daysActiveThisWeek
                )
            }

            // Surfaced Memory
            if (uiState.showMemoryCard && uiState.surfacedMemory != null) {
                item(key = "memory") {
                    SurfacedMemoryCard(
                        memory = uiState.surfacedMemory!!,
                        onDismiss = { viewModel.dismissMemoryCard() },
                        onExpand = { viewModel.expandMemoryCard() }
                    )
                }
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

            // Mood Trend Chart (Only if we have data)
            item(key = "mood_trend") {
                MoodTrendSection(
                    moodData = listOf(3f, 4f, 2f, 5f, 4f, 5f, 4f) // Still using placeholder for now as it's not in UiState yet
                )
            }
        }
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
                text = subtext,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = ProdyTextPrimaryLight,
                maxLines = 2
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
fun OverviewSection(
    dualStreakStatus: DualStreakStatus,
    totalPoints: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Wisdom Streak
            StreakCard(
                streakInfo = dualStreakStatus.wisdomStreak,
                icon = ProdyIcons.Lightbulb,
                color = ProdyWarmAmber,
                modifier = Modifier.weight(1f)
            )

            // Reflection Streak
            StreakCard(
                streakInfo = dualStreakStatus.reflectionStreak,
                icon = ProdyIcons.Edit,
                color = ProdyForestGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total Points / Level Progress (Simplified)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = ProdySurfaceLight,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(ProdyIcons.EmojiEvents, null, tint = ProdyWarmAmber, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Total XP",
                        style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
                        color = ProdyTextSecondaryLight
                    )
                }
                Text(
                    text = totalPoints.toString(),
                    style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    color = ProdyForestGreen
                )
            }
        }
    }
}

@Composable
fun StreakCard(
    streakInfo: StreakInfo,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = ProdySurfaceLight,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (streakInfo.maintainedToday) color else ProdyTextTertiaryLight,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = streakInfo.current.toString(),
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = if (streakInfo.maintainedToday) color else ProdyTextPrimaryLight
                )
            )
            Text(
                text = if (streakInfo.type == StreakType.WISDOM) "Wisdom" else "Reflection",
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

data class BadgeData(val icon: androidx.compose.ui.graphics.vector.ImageVector, val progress: Float, val color: Color)

@Composable
fun BadgeItem(badge: BadgeData) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp)) {
        CircularProgressIndicator(
            progress = { badge.progress },
            modifier = Modifier.fillMaxSize(),
            color = badge.color,
            trackColor = badge.color.copy(alpha = 0.2f),
            strokeWidth = 3.dp,
        )
        Icon(
            imageVector = badge.icon,
            contentDescription = null,
            tint = badge.color,
            modifier = Modifier.size(16.dp)
        )
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
                icon = ProdyIcons.LocalFireDepartment,
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
        shape = RoundedCornerShape(16.dp),
        color = ProdyForestGreen,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (action.type) {
                        NextActionType.START_JOURNAL -> ProdyIcons.Edit
                        NextActionType.REVIEW_WORDS -> ProdyIcons.School
                        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send
                        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb
                        else -> ProdyIcons.AutoAwesome
                    },
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.title,
                    style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    color = Color.White
                )
                Text(
                    text = action.subtitle,
                    style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Icon(ProdyIcons.ChevronRight, null, tint = Color.White)
        }
    }
}

@Composable
fun DailySeedCard(seed: SeedEntity) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = ProdyWarmAmber.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, ProdyWarmAmber.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(ProdyIcons.Spa, null, tint = ProdyWarmAmber, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Daily Seed",
                    style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    color = ProdyWarmAmber
                )
                Text(
                    text = "You have a new seed to bloom today.",
                    style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
                    color = ProdyTextSecondaryLight
                )
            }
        }
    }
}

@Composable
fun SurfacedMemoryCard(
    memory: SurfacedMemory,
    onDismiss: () -> Unit,
    onExpand: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = ProdySurfaceLight,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(ProdyIcons.History, null, tint = ProdyInfo, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Memory Lane",
                        style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                        color = ProdyInfo
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(ProdyIcons.Close, null, tint = ProdyTextTertiaryLight, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = memory.memory.preview,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = ProdyTextPrimaryLight,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "View memory",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.Underline
                ),
                color = ProdyInfo,
                modifier = Modifier.clickable { onExpand() }
            )
        }
    }
}
