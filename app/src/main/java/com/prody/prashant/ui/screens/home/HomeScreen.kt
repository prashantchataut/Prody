package com.prody.prashant.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.SeedEntity
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.components.AmbientBackground
import com.prody.prashant.ui.components.BuddhaContemplatingAnimation
import com.prody.prashant.ui.components.BuddhaGuideIntro
import com.prody.prashant.ui.components.ContextualAiHint
import com.prody.prashant.ui.components.getCurrentTimeOfDay
import com.prody.prashant.ui.theme.*
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.prody.prashant.util.AccessibilityUtils
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Home Screen - Premium Redesign (Phase 2)
 *
 * A completely redesigned dashboard following Prody's new design system:
 * - Extreme minimalism and cleanliness
 * - NO shadows, gradients, or hi-fi elements
 * - Generous white space for premium feel
 * - 8dp grid spacing system
 * - Vibrant neon green accent (#36F97F)
 * - Poppins typography throughout
 * - Flat design with subtle borders
 *
 * Design Principles:
 * - Flat, shadow-free design with NO gradients or skeuomorphism
 * - Clear visual hierarchy
 * - 8dp spacing grid
 * - Vibrant neon green (#36F97F) accents for interactivity
 * - Exclusively Poppins typography
 * - Deep dark teal background in dark mode
 * - Clean off-white background in light mode
 */

@Composable
fun HomeScreen(
    onNavigateToVocabulary: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToFutureMessage: () -> Unit,
    onNavigateToMeditation: () -> Unit = {},
    onNavigateToChallenges: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val greeting = getGreeting()
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    // Theme-aware colors
    val backgroundColor = if (isDarkTheme) Color(0xFF0D2826) else Color(0xFFF0F4F3)
    val surfaceColor = if (isDarkTheme) Color(0xFF2A4240) else Color(0xFFFFFFFF)
    val primaryTextColor = if (isDarkTheme) Color.White else Color(0xFF1A1A1A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFFD3D8D7) else Color(0xFF6C757D)
    val accentColor = ProdyAccent // #36F97F

    // Entry animations
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500, easing = EaseOutCubic)),
        exit = fadeOut(animationSpec = tween(300, easing = EaseOutCubic))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Magical Ambient Background - subtle, organic animation
            AmbientBackground(
                modifier = Modifier.fillMaxSize(),
                timeOfDay = getCurrentTimeOfDay(),
                intensity = 0.2f
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
            // Header with greeting and stats
            item {
                PremiumHeader(
                    greeting = greeting,
                    userName = uiState.userName,
                    currentStreak = uiState.currentStreak,
                    totalPoints = uiState.totalPoints,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    surfaceColor = surfaceColor,
                    accentColor = accentColor,
                    isDarkTheme = isDarkTheme,
                    onSearchClick = onNavigateToSearch
                )
            }

            // Buddha Guide Intro for first-time users
            if (uiState.showBuddhaGuide && uiState.buddhaGuideCards.isNotEmpty()) {
                item {
                    BuddhaGuideIntro(
                        cards = uiState.buddhaGuideCards,
                        onComplete = { viewModel.onBuddhaGuideComplete() },
                        onDontShowAgain = { viewModel.onBuddhaGuideDontShowAgain() }
                    )
                }
            }

            // Spacer for generous spacing
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // ============== TODAY'S PROGRESS MODULE ==============
            // Shows "Today's Progress" summary - never blank
            item {
                TodayProgressCard(
                    todayProgress = uiState.todayProgress,
                    nextAction = uiState.nextAction,
                    onNextActionClick = { route ->
                        when (route) {
                            "journal/new" -> onNavigateToJournal()
                            "vocabulary" -> onNavigateToVocabulary()
                            "future_message/write" -> onNavigateToFutureMessage()
                            "quotes" -> onNavigateToQuotes()
                        }
                    },
                    surfaceColor = surfaceColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentColor,
                    isDarkTheme = isDarkTheme
                )
            }

            // Spacer
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // ============== SEED OF THE DAY (Seed -> Bloom) ==============
            if (uiState.dailySeed != null) {
                item {
                    SeedOfTheDayCard(
                        seed = uiState.dailySeed!!,
                        surfaceColor = surfaceColor,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor,
                        isDarkTheme = isDarkTheme
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Reactive Hero Section - changes based on today's journaling status
            item {
                ReactiveHeroSection(
                    journaledToday = uiState.journaledToday,
                    todayEntryMood = uiState.todayEntryMood,
                    todayEntryPreview = uiState.todayEntryPreview,
                    onJournalClick = onNavigateToJournal,
                    onChallengesClick = onNavigateToChallenges,
                    surfaceColor = surfaceColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentColor,
                    isDarkTheme = isDarkTheme
                )
            }

            // Spacer
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Quick Actions Bar
            item {
                QuickActionsSection(
                    onJournalClick = onNavigateToJournal,
                    onFutureClick = onNavigateToFutureMessage,
                    onQuotesClick = onNavigateToQuotes,
                    surfaceColor = surfaceColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentColor,
                    isDarkTheme = isDarkTheme
                )
            }

            // Spacer
            item { Spacer(modifier = Modifier.height(32.dp)) }

            // Daily Wisdom Section Header
            item {
                DailyWisdomSectionHeader(
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    surfaceColor = surfaceColor
                )
            }

            // Contextual AI Hint for Daily Wisdom (first time)
            if (uiState.showDailyWisdomHint) {
                item {
                    ContextualAiHint(
                        hint = viewModel.getDailyWisdomHint(),
                        onDismiss = { viewModel.onDailyWisdomHintDismiss() }
                    )
                }
            }

            // Spacer
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Buddha Wisdom Card
            item {
                BuddhaWisdomCard(
                    thought = uiState.buddhaThought,
                    explanation = uiState.buddhaThoughtExplanation,
                    isLoading = uiState.isBuddhaThoughtLoading,
                    isAiGenerated = uiState.isBuddhaThoughtAiGenerated,
                    canRefresh = uiState.canRefreshBuddhaThought,
                    onRefresh = { viewModel.refreshBuddhaThought() },
                    surfaceColor = surfaceColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentColor,
                    isDarkTheme = isDarkTheme
                )
            }

            // Spacer
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Quote of the Day Card
            item {
                QuoteCard(
                    quote = uiState.dailyQuote,
                    author = uiState.dailyQuoteAuthor,
                    onShareClick = {
                        val shareText = "\"${uiState.dailyQuote}\"\n\n— ${uiState.dailyQuoteAuthor}\n\nShared via Prody"
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Quote"))
                    },
                    surfaceColor = surfaceColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentColor,
                    isDarkTheme = isDarkTheme
                )
            }

            // Spacer
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Word and Idiom Row
            item {
                WordAndIdiomSection(
                    word = uiState.wordOfTheDay,
                    pronunciation = uiState.wordPronunciation,
                    wordDefinition = uiState.wordDefinition,
                    idiom = uiState.dailyIdiom,
                    idiomMeaning = uiState.idiomMeaning,
                    idiomExample = uiState.idiomExample,
                    onLearnClick = { viewModel.markWordAsLearned() },
                    surfaceColor = surfaceColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentColor,
                    isDarkTheme = isDarkTheme
                )
            }

            // Proverb Card
            if (uiState.dailyProverb.isNotBlank()) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    ProverbSection(
                        proverb = uiState.dailyProverb,
                        origin = uiState.proverbOrigin,
                        surfaceColor = surfaceColor,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor,
                        accentColor = accentColor,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
            }
        }
    }
}

// =============================================================================
// PREMIUM HEADER SECTION
// =============================================================================

@Composable
private fun PremiumHeader(
    greeting: String,
    userName: String,
    currentStreak: Int,
    totalPoints: Int,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    surfaceColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean,
    onSearchClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp) // Generous horizontal padding
            .padding(top = 24.dp) // Generous top padding
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Greeting Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = greeting.uppercase(),
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = secondaryTextColor,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userName,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = primaryTextColor,
                    letterSpacing = (-0.5).sp
                )
            }

            // Search and Stats Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Icon Button
                Surface(
                    shape = CircleShape,
                    color = surfaceColor,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onSearchClick
                        )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = secondaryTextColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Stats Badge
                PremiumStatsBadge(
                    streak = currentStreak,
                    points = totalPoints,
                    surfaceColor = surfaceColor,
                    primaryTextColor = primaryTextColor,
                    accentColor = accentColor,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

/**
 * Compact stats badge showing streak and points with animated flame
 */
@Composable
private fun PremiumStatsBadge(
    streak: Int,
    points: Int,
    surfaceColor: Color,
    primaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val borderColor = if (isDarkTheme) Color(0xFF3A5250) else Color(0xFFDEE2E6)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor,
        tonalElevation = 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .semantics {
                contentDescription = "${AccessibilityUtils.streakDescription(streak)}, ${AccessibilityUtils.pointsDescription(points)}"
            }
    ) {
        Row(
            modifier = Modifier
                .background(surfaceColor)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Streak with animated flame
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalFireDepartment,
                    contentDescription = null, // Parent has combined description
                    tint = StreakFire,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = streak.toString(),
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = primaryTextColor
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(borderColor)
            )

            // Points
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null, // Parent has combined description
                    tint = LeaderboardGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = points.toString(),
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = primaryTextColor
                )
            }
        }
    }
}

// =============================================================================
// REACTIVE HERO SECTION - Changes based on today's journaling status
// =============================================================================

@Composable
private fun ReactiveHeroSection(
    journaledToday: Boolean,
    todayEntryMood: String,
    todayEntryPreview: String,
    onJournalClick: () -> Unit,
    onChallengesClick: () -> Unit,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    if (journaledToday) {
        // User journaled today - show "Today's Reflection" recap card
        TodayReflectionCard(
            mood = todayEntryMood,
            preview = todayEntryPreview,
            onViewClick = onJournalClick,
            surfaceColor = surfaceColor,
            primaryTextColor = primaryTextColor,
            secondaryTextColor = secondaryTextColor,
            accentColor = accentColor,
            isDarkTheme = isDarkTheme
        )
    } else {
        // User hasn't journaled today - show "Start Here" CTA
        StartHereCTA(
            onJournalClick = onJournalClick,
            onChallengesClick = onChallengesClick,
            surfaceColor = surfaceColor,
            primaryTextColor = primaryTextColor,
            secondaryTextColor = secondaryTextColor,
            accentColor = accentColor,
            isDarkTheme = isDarkTheme,
            hour = hour
        )
    }
}

@Composable
private fun TodayReflectionCard(
    mood: String,
    preview: String,
    onViewClick: () -> Unit,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val moodColor = when (mood.lowercase()) {
        "happy" -> MoodHappy
        "calm" -> MoodCalm
        "anxious" -> MoodAnxious
        "sad" -> MoodSad
        "motivated" -> MoodMotivated
        "grateful" -> MoodGrateful
        "confused" -> MoodConfused
        "excited" -> MoodExcited
        else -> accentColor
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onViewClick),
        shape = RoundedCornerShape(20.dp),
        color = moodColor.copy(alpha = 0.1f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(moodColor)
                    )
                    Text(
                        text = "TODAY'S REFLECTION",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = moodColor,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = mood.replaceFirstChar { it.uppercase() },
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = moodColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (preview.length > 80) "${preview.take(80)}..." else preview,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = primaryTextColor.copy(alpha = 0.85f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View your entry",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = moodColor
                )

                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = moodColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun StartHereCTA(
    onJournalClick: () -> Unit,
    onChallengesClick: () -> Unit,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean,
    hour: Int
) {
    val reflectionText = when {
        hour < 12 -> "Start your morning reflection"
        hour < 17 -> "Take a moment to reflect"
        else -> "End your day with reflection"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Start Journal Card
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onJournalClick),
            shape = RoundedCornerShape(20.dp),
            color = accentColor.copy(alpha = 0.1f),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Start Here",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = primaryTextColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = reflectionText,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = secondaryTextColor
                )
            }
        }

        // Challenges Card
        PremiumReflectionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.EmojiEvents,
            iconTint = LeaderboardGold,
            title = "Challenges",
            subtitle = "Compete now",
            showBadge = true,
            badgeText = "NEW",
            surfaceColor = surfaceColor,
            primaryTextColor = primaryTextColor,
            secondaryTextColor = secondaryTextColor,
            isDarkTheme = isDarkTheme,
            onClick = onChallengesClick
        )
    }
}

// =============================================================================
// REFLECTION CARDS SECTION
// =============================================================================

@Composable
private fun ReflectionCardsSection(
    onGratitudeClick: () -> Unit,
    onChallengesClick: () -> Unit,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val reflectionText = when {
        hour < 12 -> "Morning reflection"
        hour < 17 -> "Afternoon reflection"
        else -> "Evening reflection"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp) // More generous spacing
    ) {
        // Gratitude Card
        PremiumReflectionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.NightsStay,
            iconTint = MoodCalm,
            title = "Gratitude",
            subtitle = reflectionText,
            showBadge = false,
            surfaceColor = surfaceColor,
            primaryTextColor = primaryTextColor,
            secondaryTextColor = secondaryTextColor,
            isDarkTheme = isDarkTheme,
            onClick = onGratitudeClick
        )

        // Challenges Card
        PremiumReflectionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.EmojiEvents,
            iconTint = LeaderboardGold,
            title = "Challenges",
            subtitle = "Compete now",
            showBadge = true,
            badgeText = "NEW",
            surfaceColor = surfaceColor,
            primaryTextColor = primaryTextColor,
            secondaryTextColor = secondaryTextColor,
            isDarkTheme = isDarkTheme,
            onClick = onChallengesClick
        )
    }
}

@Composable
private fun PremiumReflectionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    showBadge: Boolean = false,
    badgeText: String = "",
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "cardScale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp) // Generous padding
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Icon Container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Badge
                if (showBadge) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ProdyError
                    ) {
                        Text(
                            text = badgeText,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = primaryTextColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = secondaryTextColor
            )
        }
    }
}

// =============================================================================
// QUICK ACTIONS SECTION
// =============================================================================

@Composable
private fun QuickActionsSection(
    onJournalClick: () -> Unit,
    onFutureClick: () -> Unit,
    onQuotesClick: () -> Unit,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val dividerColor = if (isDarkTheme) Color(0xFF3A5250) else Color(0xFFDEE2E6)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Journal
            QuickActionItem(
                icon = Icons.Outlined.Book,
                label = "Journal",
                textColor = secondaryTextColor,
                onClick = onJournalClick
            )

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(dividerColor)
            )

            // Future
            QuickActionItem(
                icon = Icons.Outlined.Send,
                label = "Future",
                textColor = secondaryTextColor,
                onClick = onFutureClick
            )

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(dividerColor)
            )

            // Quotes
            QuickActionItem(
                icon = Icons.Outlined.FormatQuote,
                label = "Quotes",
                textColor = secondaryTextColor,
                onClick = onQuotesClick
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    textColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = textColor
        )
    }
}

// =============================================================================
// DAILY WISDOM SECTION
// =============================================================================

@Composable
private fun DailyWisdomSectionHeader(
    primaryTextColor: Color,
    secondaryTextColor: Color,
    surfaceColor: Color
) {
    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val currentDate = dateFormat.format(Date()).uppercase()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Daily Wisdom",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = primaryTextColor
        )

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = surfaceColor
        ) {
            Text(
                text = currentDate,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = secondaryTextColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * Quote of the Day card with wisdom reveal animation
 */
@Composable
private fun QuoteCard(
    quote: String,
    author: String,
    onShareClick: () -> Unit,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    // Divider color based on theme
    val dividerColor = if (isDarkTheme) Color(0xFF3A5250) else Color(0xFFDEE2E6)

    // Track if the quote has been revealed
    var isQuoteVisible by remember { mutableStateOf(false) }

    // Trigger reveal animation after a short delay
    LaunchedEffect(quote) {
        delay(300)
        isQuoteVisible = true
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .semantics {
                contentDescription = AccessibilityUtils.quoteDescription(quote, author)
            },
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header with green accent dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Text(
                    text = "QUOTE OF THE DAY",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = accentColor,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quote text with Poppins italic styling
            Text(
                text = "\"$quote\"",
                style = WisdomLargeStyle,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(dividerColor)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Author and Share button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "— $author",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = secondaryTextColor
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onShareClick)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "SHARE",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = accentColor,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = Icons.Outlined.OpenInNew,
                        contentDescription = "Share",
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// =============================================================================
// WORD AND IDIOM SECTION
// =============================================================================

@Composable
private fun WordAndIdiomSection(
    word: String,
    pronunciation: String,
    wordDefinition: String,
    idiom: String,
    idiomMeaning: String,
    idiomExample: String,
    onLearnClick: () -> Unit,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Word Card
        WordCard(
            modifier = Modifier.weight(1f),
            word = word,
            pronunciation = pronunciation,
            definition = wordDefinition,
            onLearnClick = onLearnClick,
            surfaceColor = surfaceColor,
            primaryTextColor = primaryTextColor,
            secondaryTextColor = secondaryTextColor,
            accentColor = accentColor
        )

        // Idiom Card
        IdiomCard(
            modifier = Modifier.weight(1f),
            idiom = idiom,
            meaning = idiomMeaning,
            example = idiomExample,
            surfaceColor = surfaceColor,
            primaryTextColor = primaryTextColor,
            secondaryTextColor = secondaryTextColor
        )
    }
}

@Composable
private fun WordCard(
    modifier: Modifier = Modifier,
    word: String,
    pronunciation: String,
    definition: String,
    onLearnClick: () -> Unit,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color
) {
    var isLearned by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WORD",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = WordOfDayColor,
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = Icons.Outlined.MenuBook,
                    contentDescription = null,
                    tint = WordOfDayColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Word
            Text(
                text = word,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = primaryTextColor
            )

            // Pronunciation
            if (pronunciation.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "/$pronunciation/",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = secondaryTextColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Definition
            Text(
                text = definition,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = secondaryTextColor,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Learn button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        if (!isLearned) {
                            isLearned = true
                            onLearnClick()
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                color = if (isLearned) accentColor.copy(alpha = 0.2f) else accentColor.copy(alpha = 0.12f),
                tonalElevation = 0.dp
            ) {
                Text(
                    text = if (isLearned) "LEARNED!" else "LEARN +25",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = accentColor,
                    modifier = Modifier.padding(vertical = 12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun IdiomCard(
    modifier: Modifier = Modifier,
    idiom: String,
    meaning: String,
    example: String,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IDIOM",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = IdiomPurple,
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = null,
                    tint = IdiomPurple.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Idiom
            Text(
                text = idiom,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = primaryTextColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Meaning
            Text(
                text = meaning,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = secondaryTextColor,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            // Example
            if (example.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\"$example\"",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp,
                    color = secondaryTextColor.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// =============================================================================
// PROVERB SECTION
// =============================================================================

@Composable
private fun ProverbSection(
    proverb: String,
    origin: String,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ProverbTeal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Psychology,
                    contentDescription = null,
                    tint = ProverbTeal,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                // Header with origin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROVERB",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = ProverbTeal,
                        letterSpacing = 1.sp
                    )
                    if (origin.isNotBlank()) {
                        Text(
                            text = origin,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = secondaryTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Proverb text
                Text(
                    text = "\"$proverb\"",
                    style = WisdomMediumStyle.copy(
                        fontStyle = FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// =============================================================================
// BUDDHA WISDOM CARD
// =============================================================================

@Composable
private fun BuddhaWisdomCard(
    thought: String,
    explanation: String?,
    isLoading: Boolean,
    isAiGenerated: Boolean,
    canRefresh: Boolean,
    onRefresh: () -> Unit,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val buddhaGold = Color(0xFFDAA520)
    val buddhaGoldLight = Color(0xFFFFF8DC)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (isDarkTheme) surfaceColor.copy(alpha = 0.9f)
        else buddhaGoldLight.copy(alpha = 0.4f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Buddha icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(buddhaGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SelfImprovement,
                            contentDescription = null,
                            tint = buddhaGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "BUDDHA'S WISDOM",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = buddhaGold
                        )
                        if (isAiGenerated) {
                            Text(
                                text = "AI Generated",
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 9.sp,
                                color = secondaryTextColor
                            )
                        }
                    }
                }

                // Refresh button
                if (canRefresh && !isLoading) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh wisdom",
                            tint = secondaryTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content
            if (isLoading) {
                // Loading state - Premium "Buddha is contemplating" animation
                // Replaces generic spinner with immersive meditation-themed loading indicator
                BuddhaContemplatingAnimation(
                    modifier = Modifier.fillMaxWidth(),
                    primaryColor = buddhaGold,
                    secondaryTextColor = secondaryTextColor,
                    showText = true
                )
            } else if (thought.isNotBlank()) {
                // Wisdom text
                Text(
                    text = thought,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = primaryTextColor
                )

                // Explanation/source if available
                if (explanation != null && explanation.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = explanation,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = secondaryTextColor,
                        fontStyle = FontStyle.Italic
                    )
                }
            } else {
                // Fallback when no content
                Text(
                    text = "Wisdom awaits. Pull down to refresh.",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = secondaryTextColor,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

// =============================================================================
// TODAY'S PROGRESS CARD - Active Progress Layer
// =============================================================================

@Composable
private fun TodayProgressCard(
    todayProgress: TodayProgress,
    nextAction: NextAction?,
    onNextActionClick: (String) -> Unit,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val dividerColor = if (isDarkTheme) Color(0xFF3A5250) else Color(0xFFDEE2E6)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S PROGRESS",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = accentColor,
                    letterSpacing = 1.5.sp
                )
                if (todayProgress.currentStreak > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalFireDepartment,
                            contentDescription = null,
                            tint = StreakFire,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${todayProgress.currentStreak} day streak",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = StreakFire
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Stats Row
            if (todayProgress.isEmpty) {
                // Empty state - designed, not blank
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your day awaits",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = primaryTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Start with a small step below",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = secondaryTextColor
                    )
                }
            } else {
                // Show today's stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Journal entries
                    ProgressStatItem(
                        value = todayProgress.journalEntries.toString(),
                        label = if (todayProgress.journalEntries == 1) "entry" else "entries",
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor
                    )

                    // Words written
                    ProgressStatItem(
                        value = todayProgress.wordsWritten.toString(),
                        label = "words",
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor
                    )

                    // Words learned
                    ProgressStatItem(
                        value = todayProgress.wordsLearned.toString(),
                        label = "learned",
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor
                    )
                }
            }

            // Divider
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(dividerColor)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Next Action - the contextual suggestion
            nextAction?.let { action ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.1f))
                        .clickable { onNextActionClick(action.actionRoute) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = action.title,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = primaryTextColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = action.subtitle,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = "Go",
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressStatItem(
    value: String,
    label: String,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = primaryTextColor
        )
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = secondaryTextColor
        )
    }
}

// =============================================================================
// SEED OF THE DAY CARD - Seed -> Bloom Mechanic
// =============================================================================

@Composable
private fun SeedOfTheDayCard(
    seed: SeedEntity,
    surfaceColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val seedColor = if (seed.hasBloomedToday) MoodGrateful else Color(0xFFE8B42F) // Golden yellow for seed

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        color = seedColor.copy(alpha = 0.1f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Seed/Bloom Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(seedColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (seed.hasBloomedToday) Icons.Outlined.Park else Icons.Outlined.Spa,
                    contentDescription = null,
                    tint = seedColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Seed content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (seed.hasBloomedToday) "BLOOMED" else "SEED OF THE DAY",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = seedColor,
                        letterSpacing = 1.sp
                    )
                    if (seed.hasBloomedToday) {
                        Text(
                            text = "+25",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = seed.seedContent.replaceFirstChar { it.uppercase() },
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = primaryTextColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (seed.hasBloomedToday) {
                        "Applied in your ${seed.bloomedIn ?: "writing"}"
                    } else {
                        "Use this in your journal to bloom"
                    },
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = secondaryTextColor
                )
            }
        }
    }
}

// =============================================================================
// HELPER FUNCTIONS
// =============================================================================

@Composable
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> stringResource(R.string.good_morning)
        hour < 17 -> stringResource(R.string.good_afternoon)
        else -> stringResource(R.string.good_evening)
    }
}
