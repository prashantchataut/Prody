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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.os.Build
import android.provider.Settings

import androidx.hilt.navigation.compose.hiltViewModel
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
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onNavigateToJournal = onNavigateToJournal,
        onNavigateToQuotes = onNavigateToQuotes,
        onNavigateToFutureMessage = onNavigateToFutureMessage,
        onNavigateToHaven = onNavigateToHaven,
        onNavigateToProfile = onNavigateToProfile,
        onOpenNotificationSettings = {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            }
            context.startActivity(intent)
        },
        onRetry = viewModel::retryLoad
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToJournal: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToFutureMessage: () -> Unit,
    onNavigateToHaven: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onRetry: () -> Unit
) {
    val homeAggregate = uiState.aggregateState
    
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize().testTag("home_loading"), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.hasLoadError -> {
            HomeStatusState(
                title = "You're offline right now",
                message = uiState.error ?: "Unable to load home data. Check your connection and try again.",
                actionLabel = "Retry",
                onAction = onRetry,
                tag = "home_error"
            )
        }
        !homeAggregate.hasData -> {
            HomeStatusState(
                title = "No home data yet",
                message = "Start with a journal entry or open daily wisdom to personalize this space.",
                actionLabel = "Write Journal",
                onAction = onNavigateToJournal,
                tag = "home_empty"
            )
        }
        else -> LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav
    ) {
        // Header with Greeting and Notification
        item {
            DashboardHeader(
                userName = uiState.userName,
                onProfileClick = onNavigateToProfile,
                onNotificationClick = onOpenNotificationSettings
            )
        }

        // Overview Section (Streak & Badges)
        item {
            OverviewSection(
                streakDays = homeAggregate.streakDays,
                badges = toBadgeData(homeAggregate.badges)
            )
        }

        // Mood Trend Chart
        item {
            MoodTrendSection(
                moodData = listOf(3f, 4f, 2f, 5f, 4f, 5f, 4f) // 1-5 Scale
            )
        }

        // Weekly Summary
        item {
            WeeklySummarySection(
                journalEntries = uiState.journalEntriesThisWeek,
                wordsLearned = uiState.wordsLearnedThisWeek,
                mindfulMinutes = 45
            )
        }
        
        // Quick Actions (Navigation)
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
            RecentActivitySection(homeAggregate.recommendations)
        }
    }
    }
}

private fun toBadgeData(badges: List<HomeBadgeProgress>): List<BadgeData> = badges.map {
    when (it.id) {
        "streak" -> BadgeData(ProdyIcons.EmojiEvents, it.progress, ProdyWarmAmber)
        "journal" -> BadgeData(ProdyIcons.Edit, it.progress, ProdyForestGreen)
        else -> BadgeData(ProdyIcons.Psychology, it.progress, ProdyInfo)
    }
}

@Composable
private fun HomeStatusState(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    tag: String
) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp).testTag(tag), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = ProdyTextSecondaryLight)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}

// =============================================================================
// DASHBOARD COMPONENTS
// =============================================================================

@Composable
fun DashboardHeader(
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
                text = "Good Morning,",
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
    streakDays: Int,
    badges: List<BadgeData>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Streak Card
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = ProdySurfaceLight,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = streakDays.toString(),
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        color = ProdyForestGreen
                    )
                )
                Text(
                    text = "Day Streak",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = ProdyTextSecondaryLight
                    )
                )
            }
        }

        // Badges Card
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = ProdySurfaceLight,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    badges.forEach { badge ->
                        BadgeItem(badge)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Achievements",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = ProdyTextSecondaryLight
                    )
                )
            }
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
fun WeeklySummarySection(journalEntries: Int, wordsLearned: Int, mindfulMinutes: Int) {
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
fun RecentActivitySection(recommendations: List<String>) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Recommended",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = ProdyTextPrimaryLight
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        recommendations.forEach { recommendation ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
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
                    Text(recommendation, fontWeight = FontWeight.Medium, fontFamily = PoppinsFamily)
                }
            }
        }
    }
}
