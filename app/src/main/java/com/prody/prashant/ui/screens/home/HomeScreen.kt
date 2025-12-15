package com.prody.prashant.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*
import com.prody.prashant.ui.theme.WisdomHeroStyle
import kotlinx.coroutines.delay

/**
 * Home Screen - Main Dashboard
 *
 * The central hub of the Prody app featuring:
 * - Personalized greeting with streak and points display
 * - Daily wisdom content (quotes, words, proverbs, idioms)
 * - Quick action shortcuts to core features
 * - Weekly progress tracking
 * - AI-powered Buddha's daily thought
 *
 * UI/UX Features:
 * - Staggered entrance animations for visual delight
 * - Animated background elements for engagement
 * - Interactive cards with press feedback
 * - Progress animations for stats
 * - Proper accessibility support with content descriptions
 *
 * Design Principles:
 * - Clear visual hierarchy with card-based layout
 * - Color-coded sections for easy scanning
 * - Celebratory animations for achievements
 * - Sufficient touch targets (min 48dp)
 */
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    onNavigateToVocabulary: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToFutureMessage: () -> Unit,
    onNavigateToMeditation: () -> Unit = {},
    onNavigateToChallenges: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val greeting = getGreeting()

    // Entry animations
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Animated header with greeting and streak
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(600, easing = EaseOutCubic)
                )
            ) {
                EnhancedHomeHeader(
                    greeting = greeting,
                    userName = uiState.userName,
                    currentStreak = uiState.currentStreak,
                    totalPoints = uiState.totalPoints
                )
            }
        }

        // Daily focus card
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 150)) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(500, delayMillis = 150, easing = EaseOutCubic)
                )
            ) {
                DailyFocusCard()
            }
        }

        // Community Challenges card
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 175)) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(500, delayMillis = 175, easing = EaseOutCubic)
                )
            ) {
                CommunityChallengesCard(onClick = onNavigateToChallenges)
            }
        }

        // Daily wisdom card
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(500, delayMillis = 200, easing = EaseOutCubic)
                )
            ) {
                EnhancedDailyWisdomCard(
                    quote = uiState.dailyQuote,
                    author = uiState.dailyQuoteAuthor,
                    onQuoteTap = onNavigateToQuotes
                )
            }
        }

        // Word of the day
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 250)) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(500, delayMillis = 250, easing = EaseOutCubic)
                )
            ) {
                EnhancedWordOfTheDayCard(
                    word = uiState.wordOfTheDay,
                    definition = uiState.wordDefinition,
                    pronunciation = uiState.wordPronunciation,
                    onWordTap = onNavigateToVocabulary,
                    onMarkLearned = { viewModel.markWordAsLearned() }
                )
            }
        }

        // Quick actions grid
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 300)) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(500, delayMillis = 300, easing = EaseOutCubic)
                )
            ) {
                EnhancedQuickActionsSection(
                    onJournalClick = onNavigateToJournal,
                    onFutureMessageClick = onNavigateToFutureMessage,
                    onVocabularyClick = onNavigateToVocabulary,
                    onQuotesClick = onNavigateToQuotes
                )
            }
        }

        // Proverb of the day
        if (uiState.dailyProverb.isNotBlank()) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500, delayMillis = 350)) + slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(500, delayMillis = 350, easing = EaseOutCubic)
                    )
                ) {
                    EnhancedProverbCard(
                        proverb = uiState.dailyProverb,
                        meaning = uiState.proverbMeaning,
                        origin = uiState.proverbOrigin
                    )
                }
            }
        }

        // Idiom of the day
        if (uiState.dailyIdiom.isNotBlank()) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500, delayMillis = 400)) + slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(500, delayMillis = 400, easing = EaseOutCubic)
                    )
                ) {
                    EnhancedIdiomCard(
                        idiom = uiState.dailyIdiom,
                        meaning = uiState.idiomMeaning,
                        example = uiState.idiomExample
                    )
                }
            }
        }

        // Buddha's daily thought
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 450)) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(500, delayMillis = 450, easing = EaseOutCubic)
                )
            ) {
                EnhancedBuddhaThoughtCard(
                    thought = uiState.buddhaThought
                )
            }
        }

        // Weekly progress card
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 500)) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(500, delayMillis = 500, easing = EaseOutCubic)
                )
            ) {
                WeeklyProgressCard(
                    journalEntriesThisWeek = uiState.journalEntriesThisWeek,
                    wordsLearnedThisWeek = uiState.wordsLearnedThisWeek,
                    daysActiveThisWeek = uiState.daysActiveThisWeek
                )
            }
        }
    }
}

@Composable
private fun EnhancedHomeHeader(
    greeting: String,
    userName: String,
    currentStreak: Int,
    totalPoints: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_animation")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                    )
                )
            )
    ) {
        // Animated background particles
        HeaderBackgroundAnimation()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Animated streak badge
                AnimatedStreakBadge(
                    streakDays = currentStreak
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Points display with animation
            AnimatedPointsDisplay(
                totalPoints = totalPoints,
                glowAlpha = glowAlpha
            )
        }
    }
}

@Composable
private fun HeaderBackgroundAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_particles")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.25f)) {
        val center = Offset(size.width * 0.8f, size.height * 0.3f)

        // Draw orbiting circles
        for (i in 0 until 4) {
            val angle = (rotation + i * 90f) * PI / 180
            val radius = minOf(size.width, size.height) * 0.25f
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            drawCircle(
                color = Color.White.copy(alpha = 0.15f - i * 0.03f),
                radius = 25f - i * 5f,
                center = Offset(x, y)
            )
        }

        // Draw decorative circles
        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = size.height * 0.4f,
            center = Offset(size.width * 0.9f, size.height * 0.2f)
        )
    }
}

@Composable
private fun AnimatedStreakBadge(streakDays: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(contentAlignment = Alignment.Center) {
        // Fire glow
        if (streakDays > 0) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .scale(fireScale)
                    .blur(12.dp)
                    .alpha(glowAlpha)
                    .background(StreakFire, CircleShape)
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (streakDays > 0) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                StreakFire.copy(alpha = 0.7f),
                                StreakGlow.copy(alpha = 0.5f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.3f),
                                Color.Gray.copy(alpha = 0.2f)
                            )
                        )
                    }
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = if (streakDays > 0) StreakFire else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .scale(if (streakDays > 0) fireScale else 1f)
            )
            Column {
                Text(
                    text = streakDays.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "day streak",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun AnimatedPointsDisplay(
    totalPoints: Int,
    glowAlpha: Float
) {
    val animatedPoints by animateIntAsState(
        targetValue = totalPoints,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "points"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Animated star icon
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .blur(10.dp)
                    .alpha(glowAlpha)
                    .background(GoldTier, CircleShape)
            )
            Icon(
                imageVector = Icons.Filled.Stars,
                contentDescription = null,
                tint = GoldTier,
                modifier = Modifier.size(28.dp)
            )
        }

        Column {
            Text(
                text = formatNumber(animatedPoints),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Total Points",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Level indicator
        Surface(
            color = GoldTier.copy(alpha = 0.3f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Lvl ${getLevelFromPoints(totalPoints)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = GoldTier,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun DailyFocusCard() {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val (focusText, focusIcon) = when {
        hour < 12 -> "Morning Reflection" to Icons.Filled.WbSunny
        hour < 17 -> "Afternoon Growth" to Icons.AutoMirrored.Filled.TrendingUp
        else -> "Evening Gratitude" to Icons.Filled.NightsStay
    }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = focusIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = focusText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Take a moment to center yourself",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CommunityChallengesCard(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "challenges_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        backgroundColor = MoodExcited.copy(alpha = 0.15f)
    ) {
        Box {
            // Glow effect
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(60.dp)
                    .offset(x = 20.dp, y = (-10).dp)
                    .blur(25.dp)
                    .alpha(pulseAlpha)
                    .background(MoodExcited, CircleShape)
            )

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MoodExcited.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = MoodExcited,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Community Challenges",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = AchievementUnlocked.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "NEW",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AchievementUnlocked,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Join monthly challenges and compete with the community",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MoodExcited
                )
            }
        }
    }
}

@Composable
private fun EnhancedDailyWisdomCard(
    quote: String,
    author: String,
    onQuoteTap: () -> Unit
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onQuoteTap),
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
    ) {
        Box {
            // Decorative quote mark
            Text(
                text = "\"",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp)
                    .offset(y = (-10).dp)
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FormatQuote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.quote_of_the_day),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "\"$quote\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "— $author",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "More",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedWordOfTheDayCard(
    word: String,
    definition: String,
    pronunciation: String,
    onWordTap: () -> Unit,
    onMarkLearned: () -> Unit
) {
    var isLearned by remember { mutableStateOf(false) }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onWordTap)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MoodMotivated.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.School,
                            contentDescription = null,
                            tint = MoodMotivated,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.word_of_the_day),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MoodMotivated
                    )
                }

                // Animated learn button
                val buttonScale by animateFloatAsState(
                    targetValue = if (isLearned) 1.1f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "button_scale"
                )

                Surface(
                    modifier = Modifier
                        .scale(buttonScale)
                        .clickable {
                            if (!isLearned) {
                                isLearned = true
                                onMarkLearned()
                            }
                        },
                    color = if (isLearned) AchievementUnlocked.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isLearned) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = stringResource(R.string.mark_learned),
                            tint = if (isLearned) AchievementUnlocked else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isLearned) "Learned!" else "Learn",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isLearned) AchievementUnlocked else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Word display with pronunciation
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (pronunciation.isNotBlank()) {
                    Text(
                        text = "/$pronunciation/",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = definition,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Point reward indicator
            Surface(
                color = GoldTier.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "+25 points when learned",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldTier,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedQuickActionsSection(
    onJournalClick: () -> Unit,
    onFutureMessageClick: () -> Unit,
    onVocabularyClick: () -> Unit,
    onQuotesClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Your daily tools",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EnhancedQuickActionItem(
                icon = Icons.Filled.Book,
                label = "Journal",
                subtitle = "Reflect",
                color = MoodCalm,
                onClick = onJournalClick,
                modifier = Modifier.weight(1f)
            )
            EnhancedQuickActionItem(
                icon = Icons.Filled.Schedule,
                label = "Future",
                subtitle = "Message",
                color = MoodExcited,
                onClick = onFutureMessageClick,
                modifier = Modifier.weight(1f)
            )
            EnhancedQuickActionItem(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                label = "Words",
                subtitle = "Learn",
                color = MoodMotivated,
                onClick = onVocabularyClick,
                modifier = Modifier.weight(1f)
            )
            EnhancedQuickActionItem(
                icon = Icons.Filled.FormatQuote,
                label = "Quotes",
                subtitle = "Inspire",
                color = MoodGrateful,
                onClick = onQuotesClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EnhancedQuickActionItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        onClick = onClick,
        modifier = modifier.scale(interactionScale),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun EnhancedProverbCard(
    proverb: String,
    meaning: String,
    origin: String
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Psychology,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.proverb_of_the_day),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = proverb,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Meaning in a highlighted box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f))
                    .padding(12.dp)
            ) {
                Text(
                    text = meaning,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f)
                )
            }

            if (origin.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "— $origin",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun EnhancedIdiomCard(
    idiom: String,
    meaning: String,
    example: String
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MoodConfused.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Translate,
                        contentDescription = null,
                        tint = MoodConfused,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.idiom_of_the_day),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MoodConfused
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = idiom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = meaning,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            if (example.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LightbulbCircle,
                            contentDescription = null,
                            tint = MoodMotivated,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "\"$example\"",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Premium Buddha's Thought Card - Hero-level wisdom display
 *
 * This is the unique selling point of Prody, featuring:
 * - Elegant serif typography (Playfair Display) for philosophical content
 * - Animated gradient background with subtle movement
 * - Decorative quote marks and visual hierarchy
 * - Share and save actions readily accessible
 * - Breathing/pulsing glow effect for meditative feel
 */
@Composable
private fun EnhancedBuddhaThoughtCard(
    thought: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "buddha_glow")

    // Primary glow animation - soft pulsing for meditative effect
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_primary"
    )

    // Secondary accent glow - offset timing for depth
    val accentGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, delayMillis = 500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_accent"
    )

    // Subtle scale breathing effect
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Main card with gradient background
        ProdyCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ProdyPrimary.copy(alpha = 0.12f),
                                ProdyTertiary.copy(alpha = 0.08f),
                                ProdyPrimary.copy(alpha = 0.06f)
                            )
                        )
                    )
            ) {
                // Top-right decorative glow orb
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(120.dp)
                        .offset(x = 40.dp, y = (-30).dp)
                        .blur(40.dp)
                        .alpha(glowAlpha)
                        .background(ProdyTertiary, CircleShape)
                )

                // Bottom-left accent glow
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(80.dp)
                        .offset(x = (-20).dp, y = 20.dp)
                        .blur(30.dp)
                        .alpha(accentGlowAlpha)
                        .background(GoldTier.copy(alpha = 0.6f), CircleShape)
                )

                // Decorative large quote mark (background)
                Text(
                    text = """,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Light
                    ),
                    color = ProdyPrimary.copy(alpha = 0.06f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-8).dp, y = (-40).dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Header with icon and title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Animated icon container with glow
                            Box(contentAlignment = Alignment.Center) {
                                // Glow behind icon
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .scale(breathScale)
                                        .blur(12.dp)
                                        .alpha(glowAlpha * 0.8f)
                                        .background(ProdyPrimary, CircleShape)
                                )
                                // Icon background
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    ProdyPrimary.copy(alpha = 0.25f),
                                                    ProdyPrimary.copy(alpha = 0.15f)
                                                )
                                            )
                                        )
                                        .border(
                                            width = 1.dp,
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    ProdyPrimary.copy(alpha = 0.4f),
                                                    ProdyPrimary.copy(alpha = 0.1f)
                                                )
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SelfImprovement,
                                        contentDescription = null,
                                        tint = ProdyPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "Buddha's Thought",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ProdyPrimary
                                )
                                Text(
                                    text = "Daily Wisdom",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        // Action buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Share button
                            IconButton(
                                onClick = { /* Share functionality */ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = "Share thought",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // Save/bookmark button
                            IconButton(
                                onClick = { /* Save functionality */ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Save thought",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Main wisdom text with serif typography
                    Text(
                        text = thought,
                        style = WisdomHeroStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Decorative bottom divider with gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        ProdyPrimary.copy(alpha = 0.3f),
                                        GoldTier.copy(alpha = 0.4f),
                                        ProdyPrimary.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom attribution and meditation prompt
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Meditation timer suggestion
                        Surface(
                            color = ProdyPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Timer,
                                    contentDescription = null,
                                    tint = ProdyPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Reflect for 2 min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ProdyPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Point reward
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stars,
                                contentDescription = null,
                                tint = GoldTier,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "+15 XP daily",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldTier.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyProgressCard(
    journalEntriesThisWeek: Int,
    wordsLearnedThisWeek: Int,
    daysActiveThisWeek: Int
) {
    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(600)
        isAnimated = true
    }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Insights,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "This Week",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$daysActiveThisWeek/7 days",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnimatedActivityStat(
                    value = journalEntriesThisWeek,
                    label = "Entries",
                    icon = Icons.Outlined.Book,
                    color = MoodCalm,
                    isAnimated = isAnimated
                )
                AnimatedActivityStat(
                    value = wordsLearnedThisWeek,
                    label = "Words",
                    icon = Icons.Outlined.School,
                    color = MoodMotivated,
                    isAnimated = isAnimated
                )
                AnimatedActivityStat(
                    value = daysActiveThisWeek,
                    label = "Active",
                    icon = Icons.Outlined.CalendarMonth,
                    color = MoodGrateful,
                    isAnimated = isAnimated
                )
            }
        }
    }
}

@Composable
private fun AnimatedActivityStat(
    value: Int,
    label: String,
    icon: ImageVector,
    color: Color,
    isAnimated: Boolean
) {
    val animatedValue by animateIntAsState(
        targetValue = if (isAnimated) value else 0,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "stat_value"
    )

    val scale by animateFloatAsState(
        targetValue = if (isAnimated) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = animatedValue.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Helper functions
@Composable
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> stringResource(R.string.good_morning)
        hour < 17 -> stringResource(R.string.good_afternoon)
        else -> stringResource(R.string.good_evening)
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
        number >= 1000 -> String.format("%.1fK", number / 1000.0)
        else -> number.toString()
    }
}

private fun getLevelFromPoints(points: Int): Int {
    return when {
        points >= 10000 -> 10
        points >= 7500 -> 9
        points >= 5000 -> 8
        points >= 3500 -> 7
        points >= 2500 -> 6
        points >= 1500 -> 5
        points >= 1000 -> 4
        points >= 500 -> 3
        points >= 200 -> 2
        else -> 1
    }
}
