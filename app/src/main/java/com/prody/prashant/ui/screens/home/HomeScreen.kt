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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav
    ) {
        // Header with Greeting and Notification
        item {
            DashboardHeader(
                greeting = uiState.intelligentGreeting.ifEmpty { "Good Morning," },
                subtext = uiState.greetingSubtext.ifEmpty { "Welcome back" },
                userName = uiState.userName,
                onProfileClick = {}, // TODO: Profile Nav
                onNotificationClick = {}
            )
        }

        // Active Progress Layer (Contextual Suggestions)
        if (uiState.nextAction != null) {
            item {
                ActiveProgressSection(
                    nextAction = uiState.nextAction!!,
                    onActionClick = {
                        // Handle contextual navigation based on action type
                    }
                )
            }
        }

        // Overview Section (Streak & Badges)
        item {
            OverviewSection(
                streakDays = uiState.currentStreak,
                badges = listOf(
                    BadgeData(ProdyIcons.EmojiEvents, 0.75f, ProdyWarmAmber),
                    BadgeData(ProdyIcons.Edit, 0.5f, ProdyForestGreen),
                    BadgeData(ProdyIcons.Psychology, 0.3f, ProdyInfo)
                )
            )
        }

        // Buddha's Daily Wisdom (AI-Powered)
        item {
            BuddhaWisdomSection(
                thought = uiState.buddhaThought,
                explanation = uiState.buddhaThoughtExplanation,
                isLoading = uiState.isBuddhaThoughtLoading,
                onRefresh = { viewModel.refreshBuddhaThought() }
            )
        }

        // Wisdom Cards (Quote, Word, Proverb, Idiom)
        item {
            WisdomCardsSection(
                uiState = uiState,
                onQuoteClick = onNavigateToQuotes,
                onWordClick = onNavigateToVocabulary,
                onProverbClick = onNavigateToProverbs,
                onIdiomClick = onNavigateToIdioms
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
                mindfulMinutes = uiState.daysActiveThisWeek * 15 // Placeholder calc
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
            RecentActivitySection()
        }
    }
}

// =============================================================================
// NEW FUNCTIONAL SECTIONS
// =============================================================================

@Composable
fun ActiveProgressSection(
    nextAction: com.prody.prashant.domain.progress.NextAction,
    onActionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        ProdyPremiumCard(
            backgroundColor = ProdyForestGreen.copy(alpha = 0.05f),
            borderColor = ProdyForestGreen.copy(alpha = 0.1f),
            onClick = onActionClick
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ProdyForestGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ProdyIcons.AutoAwesome,
                        contentDescription = null,
                        tint = ProdyForestGreen
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Next Step",
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = ProdyForestGreen
                        )
                    )
                    Text(
                        text = nextAction.title,
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ProdyTextPrimaryLight
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun BuddhaWisdomSection(
    thought: String,
    explanation: String,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Buddha's Thought",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = ProdyTextPrimaryLight
                )
            )

            IconButton(
                onClick = onRefresh,
                modifier = Modifier.size(24.dp),
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = ProdyIcons.Refresh,
                    contentDescription = "Refresh",
                    tint = if (isLoading) ProdyTextSecondaryLight else ProdyForestGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProdyPremiumCard(
            backgroundColor = ProdySurfaceLight,
            borderColor = ProdyOutlineLight.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = ProdyForestGreen,
                        trackColor = ProdyForestGreen.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Icon(
                    imageVector = ProdyIcons.FormatQuote,
                    contentDescription = null,
                    tint = ProdyForestGreen.copy(alpha = 0.2f),
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = thought,
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = ProdyTextPrimaryLight
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (explanation.isNotEmpty()) {
                    Text(
                        text = explanation,
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = ProdyTextSecondaryLight
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun WisdomCardsSection(
    uiState: HomeUiState,
    onQuoteClick: () -> Unit,
    onWordClick: () -> Unit,
    onProverbClick: () -> Unit,
    onIdiomClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Daily Wisdom",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = ProdyTextPrimaryLight
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quote Card
            item {
                WisdomTile(
                    title = "Daily Quote",
                    content = uiState.dailyQuote,
                    subcontent = "- " + uiState.dailyQuoteAuthor,
                    icon = ProdyIcons.FormatQuote,
                    color = ProdyForestGreen,
                    onClick = onQuoteClick
                )
            }

            // Word Card
            item {
                WisdomTile(
                    title = "Word of the Day",
                    content = uiState.wordOfTheDay,
                    subcontent = uiState.wordDefinition,
                    icon = ProdyIcons.School,
                    color = ProdyWarmAmber,
                    onClick = onWordClick
                )
            }

            // Proverb Card
            item {
                WisdomTile(
                    title = "Daily Proverb",
                    content = uiState.dailyProverb,
                    subcontent = uiState.proverbMeaning,
                    icon = ProdyIcons.MenuBook,
                    color = ProdyInfo,
                    onClick = onProverbClick
                )
            }

            // Idiom Card
            item {
                WisdomTile(
                    title = "Daily Idiom",
                    content = uiState.dailyIdiom,
                    subcontent = uiState.idiomMeaning,
                    icon = ProdyIcons.Lightbulb,
                    color = Color(0xFF9C27B0),
                    onClick = onIdiomClick
                )
            }
        }
    }
}

@Composable
fun WisdomTile(
    title: String,
    content: String,
    subcontent: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    ProdyPremiumCard(
        modifier = Modifier.width(280.dp),
        onClick = onClick,
        backgroundColor = color.copy(alpha = 0.05f),
        borderColor = color.copy(alpha = 0.1f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = color
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = content,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ProdyTextPrimaryLight
                ),
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subcontent,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = ProdyTextSecondaryLight
                ),
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

// =============================================================================
// DASHBOARD COMPONENTS
// =============================================================================

@Composable
fun DashboardHeader(
    greeting: String,
    subtext: String,
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
                text = greeting,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = ProdyTextPrimaryLight
            )
            Text(
                text = if (subtext.isNotEmpty()) subtext else userName,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                color = ProdyTextSecondaryLight
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
