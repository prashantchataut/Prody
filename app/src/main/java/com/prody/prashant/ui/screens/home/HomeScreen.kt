package com.prody.prashant.ui.screens.home

import com.prody.prashant.ui.icons.ProdyIcons
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawWithCache
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.R

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.ui.theme.*
import com.prody.prashant.domain.intelligence.IntelligenceInsight
import com.prody.prashant.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    // Determine greeting based on ViewModel state or time-based fallback
    val greeting = remember(uiState.intelligentGreeting) {
        if (uiState.intelligentGreeting.isNotEmpty()) {
            uiState.intelligentGreeting
        } else {
            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            when {
                hour < 12 -> "Good Morning,"
                hour < 17 -> "Good Afternoon,"
                else -> "Good Evening,"
            }
        }
    }

    // Build badge data from real achievement progress
    val badges = remember(uiState.totalPoints, uiState.journalEntriesThisWeek, uiState.daysActiveThisWeek) {
        listOf(
            BadgeData(ProdyIcons.EmojiEvents, (uiState.totalPoints.coerceAtMost(1000) / 1000f).coerceIn(0f, 1f), ProdyWarmAmber),
            BadgeData(ProdyIcons.Edit, (uiState.journalEntriesThisWeek.coerceAtMost(7) / 7f).coerceIn(0f, 1f), ProdyForestGreen),
            BadgeData(ProdyIcons.Psychology, (uiState.daysActiveThisWeek.coerceAtMost(7) / 7f).coerceIn(0f, 1f), ProdyInfo)
        )
    }

    // Celebration Dialog for Soul Layer milestones
    if (uiState.showFirstWeekCelebration && uiState.firstWeekCelebration != null) {
        CelebrationDialog(
            celebration = uiState.firstWeekCelebration!!,
            onDismiss = { viewModel.dismissFirstWeekCelebration() }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header with Greeting
        item(key = "header") {
            val entranceAlpha = remember { Animatable(0f) }
            val entranceTranslation = remember { Animatable(20f) }

            LaunchedEffect(Unit) {
                launch {
                    entranceAlpha.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
                }
                launch {
                    entranceTranslation.animateTo(0f, animationSpec = tween(600, easing = EaseOutCubic))
                }
            }

            Box(modifier = Modifier.graphicsLayer {
                alpha = entranceAlpha.value
                translationY = entranceTranslation.value
            }) {
                DashboardHeader(
                    userName = uiState.userName,
                    greeting = greeting,
                    onProfileClick = {},
                    onNotificationClick = onNavigateToSearch
                )
            }
        }

        // Overview Section (Consistency & Badges)
        item(key = "overview") {
            val entranceAlpha = remember { Animatable(0f) }
            val entranceTranslation = remember { Animatable(20f) }

            LaunchedEffect(Unit) {
                delay(100)
                launch {
                    entranceAlpha.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
                }
                launch {
                    entranceTranslation.animateTo(0f, animationSpec = tween(600, easing = EaseOutCubic))
                }
            }

            Box(modifier = Modifier.graphicsLayer {
                alpha = entranceAlpha.value
                translationY = entranceTranslation.value
            }) {
                OverviewSection(
                    streakDays = uiState.currentStreak,
                    badges = badges
                )
            }
        }

        // Soul Layer: First Week Journey
        if (uiState.isInFirstWeek) {
            item(key = "first_week_journey") {
                Spacer(modifier = Modifier.height(16.dp))
                FirstWeekProgressCard(
                    dayNumber = uiState.firstWeekDayNumber,
                    progress = uiState.firstWeekProgress,
                    dayContent = uiState.firstWeekDayContent,
                    onContinue = { /* Handle continue - navigation or action */ },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Soul Layer: Surfaced Memory
        if (uiState.showMemoryCard && uiState.surfacedMemory != null) {
            item(key = "surfaced_memory") {
                Spacer(modifier = Modifier.height(16.dp))
                SurfacedMemoryCard(
                    memory = uiState.surfacedMemory!!,
                    onExpand = { viewModel.expandMemoryCard() },
                    onDismiss = { viewModel.dismissMemoryCard() },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Soul Layer: Anniversary Memories
        if (uiState.anniversaryMemories.isNotEmpty()) {
            items(
                items = uiState.anniversaryMemories,
                key = { "anniversary_${it.memory.id}" }
            ) { anniversary ->
                Spacer(modifier = Modifier.height(16.dp))
                AnniversaryMemoryCard(
                    anniversary = anniversary,
                    onView = { /* Navigate to memory detail */ },
                    onDismiss = { /* Handle dismiss */ },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Personalized Pattern Card (opt-in, only shown when data exists)
        if (uiState.personalizedPatternText.isNotEmpty()) {
            item(key = "personalized_pattern") {
                PersonalizedPatternCard(
                    patternText = uiState.personalizedPatternText,
                    patternSuggestion = uiState.personalizedPatternSuggestion
                )
            }
        }

        // Premium Intelligence Insights (Opt-in)
        if (uiState.isPremiumIntelligenceEnabled && uiState.intelligenceInsights.isNotEmpty()) {
            item(key = "intelligence_insight") {
                Spacer(modifier = Modifier.height(24.dp))
                IntelligenceInsightCard(
                    insight = uiState.intelligenceInsights.first(),
                    onActionClick = {}
                )
            }
        }

        // Mood Trend Chart - only show if there's real data
        if (uiState.journalEntriesThisWeek > 0) {
            item(key = "mood_trend") {
                MoodTrendSection(
                    moodData = emptyList() // Mood trend requires historical mood data not exposed yet
                )
            }
        }

        // Weekly Summary from real data
        item(key = "weekly_summary") {
            WeeklySummarySection(
                journalEntries = uiState.journalEntriesThisWeek,
                wordsLearned = uiState.wordsLearnedThisWeek,
                mindfulMinutes = uiState.daysActiveThisWeek * 15 // Approximate based on active days
            )
        }

        // Quick Actions (Navigation)
        item(key = "quick_actions") {
            val entranceAlpha = remember { Animatable(0f) }
            val entranceTranslation = remember { Animatable(20f) }

            LaunchedEffect(Unit) {
                delay(200)
                launch {
                    entranceAlpha.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
                }
                launch {
                    entranceTranslation.animateTo(0f, animationSpec = tween(600, easing = EaseOutCubic))
                }
            }

            Box(modifier = Modifier.graphicsLayer {
                alpha = entranceAlpha.value
                translationY = entranceTranslation.value
            }) {
                QuickActionsGrid(
                    onJournalClick = onNavigateToJournal,
                    onHavenClick = onNavigateToHaven,
                    onWisdomClick = onNavigateToQuotes,
                    onFutureClick = onNavigateToFutureMessage
                )
            }
        }

        // Explore Section - routes to additional features
        item(key = "explore") {
            val entranceAlpha = remember { Animatable(0f) }
            val entranceTranslation = remember { Animatable(20f) }

            LaunchedEffect(Unit) {
                delay(300)
                launch {
                    entranceAlpha.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
                }
                launch {
                    entranceTranslation.animateTo(0f, animationSpec = tween(600, easing = EaseOutCubic))
                }
            }

            Box(modifier = Modifier.graphicsLayer {
                alpha = entranceAlpha.value
                translationY = entranceTranslation.value
            }) {
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

        // Recent Activity - show today's journal status
        item(key = "recent_activity") {
            val entranceAlpha = remember { Animatable(0f) }
            val entranceTranslation = remember { Animatable(20f) }

            LaunchedEffect(Unit) {
                delay(400)
                launch {
                    entranceAlpha.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
                }
                launch {
                    entranceTranslation.animateTo(0f, animationSpec = tween(600, easing = EaseOutCubic))
                }
            }

            Box(modifier = Modifier.graphicsLayer {
                alpha = entranceAlpha.value
                translationY = entranceTranslation.value
            }) {
                RecentActivitySection(
                    journaledToday = uiState.journaledToday,
                    todayMood = uiState.todayEntryMood,
                    todayPreview = uiState.todayEntryPreview,
                    onClick = onNavigateToJournal
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
    greeting: String = "Good Morning,",
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
        
        val profileContentDescription = stringResource(R.string.cd_profile_picture)

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProdyIconButton(
                icon = Icons.Outlined.Notifications,
                onClick = onNotificationClick,
                contentDescription = "Notifications",
                tint = ProdyTextPrimaryLight
            )
            // Avatar / Profile
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ProdyForestGreen)
                    .clickable { onProfileClick() }
                    .semantics {
                        role = Role.Button
                        contentDescription = profileContentDescription
                    },
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
        ProdyCard(
            modifier = Modifier.weight(1f),
            backgroundColor = ProdySurfaceLight
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
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
                    text = "Consistency Score",
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
        ProdyCard(
            modifier = Modifier.weight(1f),
            backgroundColor = ProdySurfaceLight
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
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
                    text = "Points to Grow",
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
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .graphicsLayer {
                // Deferring any potential transformations to drawing layer
            }
    ) {
        CircularProgressIndicator(
            progress = { badge.progress },
            modifier = Modifier.fillMaxSize(),
            color = badge.color,
            trackColor = badge.color.copy(alpha = 0.2f),
            strokeWidth = 3.dp,
        )
        Icon(
            imageVector = badge.icon,
            contentDescription = stringResource(R.string.cd_badge_icon, badge.icon.name),
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
        ProdyCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            backgroundColor = ProdySurfaceLight
        ) {
            MoodChart(data = moodData, modifier = Modifier.padding(24.dp))
        }
    }
}

@Composable
fun MoodChart(data: List<Float>, modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val path = Path()

                val width = size.width
                val height = size.height
                val stepX = if (data.size > 1) width / (data.size - 1) else 0f

                // Normalize data to height (1-5 scale)
                val points = data.mapIndexed { index, value ->
                    val x = index * stepX
                    val y = height - ((value - 1) / 4f) * height
                    Offset(x, y)
                }

                path.reset()
                if (points.isNotEmpty()) {
                    path.moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val p0 = points[i - 1]
                        val p1 = points[i]
                        val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2, p0.y)
                        val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2, p1.y)
                        path.cubicTo(
                            controlPoint1.x, controlPoint1.y,
                            controlPoint2.x, controlPoint2.y,
                            p1.x, p1.y
                        )
                    }
                }

                onDrawBehind {
                    if (data.isEmpty()) return@onDrawBehind

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
    )
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
    ProdyCard(
        modifier = modifier,
        backgroundColor = ProdySurfaceLight
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
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
            .padding(horizontal = 24.dp)
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
        Spacer(modifier = Modifier.height(16.dp))
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
fun RecentActivitySection(
    journaledToday: Boolean = false,
    todayMood: String = "",
    todayPreview: String = "",
    onClick: () -> Unit = {}
) {
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
        
        ProdyClickableCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = ProdySurfaceLight
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
                        imageVector = if (journaledToday) ProdyIcons.Check else ProdyIcons.Edit,
                        contentDescription = null,
                        tint = if (journaledToday) ProdyForestGreen else ProdyWarmAmber
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (journaledToday) "Journal Entry" else "Daily Journal",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PoppinsFamily
                    )
                    Text(
                        text = when {
                            journaledToday && todayMood.isNotEmpty() -> "Feeling ${todayMood.lowercase().replaceFirstChar { it.uppercase() }}"
                            journaledToday -> "Completed today"
                            else -> "Tap to write your thoughts"
                        },
                        fontSize = 12.sp,
                        color = ProdyTextSecondaryLight,
                        fontFamily = PoppinsFamily,
                        maxLines = 1
                    )
                    if (journaledToday && todayPreview.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = todayPreview,
                            fontSize = 11.sp,
                            color = ProdyTextSecondaryLight.copy(alpha = 0.7f),
                            fontFamily = PoppinsFamily,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// PERSONALIZED PATTERN CARD (opt-in, local ML)
// =============================================================================

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

@Composable
fun IntelligenceInsightCard(
    insight: IntelligenceInsight,
    onActionClick: () -> Unit
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        backgroundColor = ProdySurfaceLight
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ProdyForestGreen.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ProdyForestGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ProdyForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Column {
                    Text(
                        text = "Identity Insight",
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = ProdyForestGreen
                        )
                    )
                    Text(
                        text = insight.title,
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ProdyTextPrimaryLight
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = insight.description,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = ProdyTextSecondaryLight
                )
            )
            
            if (insight.actionable != null) {
                Spacer(modifier = Modifier.height(20.dp))
                
                ProdyPrimaryButton(
                    text = insight.actionable!!,
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(),
                    size = ProdyButtonSize.MEDIUM
                )
            }
        }
    }
}
