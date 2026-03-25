package com.prody.prashant.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
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
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*
import com.prody.prashant.domain.progress.NextAction

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

    // Performance: Remember navigation callbacks to prevent unnecessary recompositions
    val onActionClick: (String) -> Unit = remember(
        onNavigateToJournal, onNavigateToVocabulary, onNavigateToFutureMessage,
        onNavigateToQuotes, onNavigateToHaven, onNavigateToMeditation,
        onNavigateToChallenges, onNavigateToSearch
    ) {
        { route ->
            when (route) {
                "journal/new" -> onNavigateToJournal()
                "vocabulary" -> onNavigateToVocabulary()
                "future_message/write" -> onNavigateToFutureMessage()
                "quotes" -> onNavigateToQuotes()
                "haven" -> onNavigateToHaven()
                "meditation" -> onNavigateToMeditation()
                "challenges" -> onNavigateToChallenges()
                "search" -> onNavigateToSearch()
                "profile" -> {} // Handled via Scaffold bottom nav usually
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header with Greeting from Soul Layer
        item {
            DashboardHeader(
                greeting = uiState.intelligentGreeting,
                userName = uiState.userName,
                onProfileClick = { onActionClick("profile") },
                onNotificationClick = {}
            )
        }

        // Dual Streak System
        item {
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                DualStreakCard(status = uiState.dualStreakStatus)
            }
        }

        // Active Progress: Next Action
        uiState.nextAction?.let { nextAction ->
            item {
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    NextActionCard(
                        nextAction = nextAction,
                        onClick = { onActionClick(nextAction.actionRoute) }
                    )
                }
            }
        }

        // Today's Progress Summary
        item {
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                TodayProgressCard(progress = uiState.todayProgress)
            }
        }

        // Mood Trend Chart - Reactive from uiState
        if (uiState.moodTrend.isNotEmpty()) {
            item {
                MoodTrendSection(
                    moodData = uiState.moodTrend
                )
            }
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
        
        // Recent / Suggestions
        item {
            RecentActivitySection(
                journaledToday = uiState.journaledToday
            )
        }
    }
}

@Composable
fun DashboardHeader(
    greeting: String,
    userName: String,
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
                text = if (greeting.isNotEmpty()) greeting else "Good Morning,",
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
fun MoodTrendSection(moodData: List<Float>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
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

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(ProdyTokens.Radius.lg),
            color = ProdySurfaceLight,
            shadowElevation = ProdyTokens.Elevation.sm
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
        val stepX = if (data.size > 1) width / (data.size - 1) else 0f
        
        val points = data.mapIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - 1) / 4f) * height
            Offset(x, y)
        }

        if (data.size > 1) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
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
        }

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
            text = "Explore Features",
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
        shape = RoundedCornerShape(ProdyTokens.Radius.lg),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
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
fun RecentActivitySection(journaledToday: Boolean) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Today",
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
            shape = RoundedCornerShape(ProdyTokens.Radius.md),
            color = ProdySurfaceLight,
            shadowElevation = ProdyTokens.Elevation.xs
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (journaledToday) ProdyForestGreen.copy(alpha = 0.1f)
                            else ProdyWarmAmber.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (journaledToday) ProdyIcons.Check else ProdyIcons.Info,
                        contentDescription = null,
                        tint = if (journaledToday) ProdyForestGreen else ProdyWarmAmber
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (journaledToday) "Daily Journal" else "Reflection Pending",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PoppinsFamily
                    )
                    Text(
                        text = if (journaledToday) "Completed today" else "Take a moment to reflect",
                        fontSize = 12.sp,
                        color = ProdyTextSecondaryLight,
                        fontFamily = PoppinsFamily
                    )
                }
            }
        }
    }
}
