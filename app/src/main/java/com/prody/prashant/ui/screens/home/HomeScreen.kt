package com.prody.prashant.ui.screens.home

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.theme.*
import java.util.Calendar

@Composable
fun HomeScreen(
    onNavigateToVocabulary: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToFutureMessage: () -> Unit,
    onNavigateToMeditation: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val greeting = getGreeting()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Animated Header with greeting and streak
        item {
            AnimatedHomeHeader(
                greeting = greeting,
                userName = uiState.userName,
                currentStreak = uiState.currentStreak,
                totalPoints = uiState.totalPoints
            )
        }

        // Daily motivation card with animation
        item {
            DailyMotivationCard()
        }

        // Daily wisdom card with animation
        item {
            AnimatedDailyWisdomCard(
                quote = uiState.dailyQuote,
                author = uiState.dailyQuoteAuthor,
                onQuoteTap = onNavigateToQuotes
            )
        }

        // Word of the day with enhanced animations
        item {
            AnimatedWordOfTheDayCard(
                word = uiState.wordOfTheDay,
                definition = uiState.wordDefinition,
                pronunciation = uiState.wordPronunciation,
                onWordTap = onNavigateToVocabulary,
                onMarkLearned = { viewModel.markWordAsLearned() }
            )
        }

        // Quick actions grid with staggered animations
        item {
            AnimatedQuickActionsSection(
                onJournalClick = onNavigateToJournal,
                onFutureMessageClick = onNavigateToFutureMessage,
                onVocabularyClick = onNavigateToVocabulary,
                onQuotesClick = onNavigateToQuotes
            )
        }

        // Proverb of the day with animation
        if (uiState.dailyProverb.isNotBlank()) {
            item {
                AnimatedProverbCard(
                    proverb = uiState.dailyProverb,
                    meaning = uiState.proverbMeaning,
                    origin = uiState.proverbOrigin
                )
            }
        }

        // Idiom of the day with animation
        if (uiState.dailyIdiom.isNotBlank()) {
            item {
                AnimatedIdiomCard(
                    idiom = uiState.dailyIdiom,
                    meaning = uiState.idiomMeaning,
                    example = uiState.idiomExample
                )
            }
        }

        // Buddha's daily thought with animation
        item {
            AnimatedBuddhaThoughtCard(
                thought = uiState.buddhaThought
            )
        }

        // Recent activity summary with animated counters
        item {
            AnimatedRecentActivityCard(
                journalEntriesThisWeek = uiState.journalEntriesThisWeek,
                wordsLearnedThisWeek = uiState.wordsLearnedThisWeek,
                daysActiveThisWeek = uiState.daysActiveThisWeek
            )
        }
    }
}

@Composable
private fun AnimatedHomeHeader(
    greeting: String,
    userName: String,
    currentStreak: Int,
    totalPoints: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header")

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        ProdyPrimaryVariant
                    )
                )
            )
    ) {
        // Animated particles background
        FloatingParticlesBackground(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            particleCount = 15,
            particleColor = Color.White.copy(alpha = 0.06f)
        )

        // Wave pattern
        WavePattern(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .alpha(0.3f),
            waveColor = Color.White.copy(alpha = 0.05f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp)
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
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                StreakBadge(
                    streakDays = currentStreak,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Points display
            AnimatedPointsDisplay(totalPoints = totalPoints)
        }
    }
}

@Composable
private fun AnimatedPointsDisplay(totalPoints: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "points")

    val starScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_scale"
    )

    val animatedPoints = AnimatedCounter(targetValue = totalPoints)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Stars,
            contentDescription = null,
            tint = GoldTier,
            modifier = Modifier
                .size(24.dp)
                .scale(starScale)
        )
        Text(
            text = "$animatedPoints points",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
private fun DailyMotivationCard() {
    val motivations = listOf(
        "Today is your day to shine!" to MoodMotivated,
        "Small progress is still progress" to MoodCalm,
        "You're one step closer to your goals" to AchievementUnlocked,
        "Every expert was once a beginner" to MoodGrateful,
        "Your future self will thank you" to MoodExcited
    )
    val (motivation, color) = remember { motivations.random() }

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "motivation_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "motivation_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale)
            .alpha(alpha)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.15f),
                        color.copy(alpha = 0.08f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
            val sparkleRotation by infiniteTransition.animateFloat(
                initialValue = -10f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "sparkle_rotation"
            )

            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(sparkleRotation)
            )
            Text(
                text = motivation,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AnimatedDailyWisdomCard(
    quote: String,
    author: String,
    onQuoteTap: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "wisdom")
    val quoteRotation by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "quote_rotation"
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                isExpanded = !isExpanded
                onQuoteTap()
            },
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FormatQuote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(quoteRotation)
                )
                Text(
                    text = stringResource(R.string.quote_of_the_day),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "\"$quote\"",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "— $author",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun AnimatedWordOfTheDayCard(
    word: String,
    definition: String,
    pronunciation: String,
    onWordTap: () -> Unit,
    onMarkLearned: () -> Unit
) {
    var isLearned by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "word")
    val bookScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "book_scale"
    )

    val checkScale by animateFloatAsState(
        targetValue = if (isLearned) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "check_scale"
    )

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
                    Icon(
                        imageVector = Icons.Outlined.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(bookScale)
                    )
                    Text(
                        text = stringResource(R.string.word_of_the_day),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = {
                        isLearned = true
                        onMarkLearned()
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .scale(checkScale)
                ) {
                    Icon(
                        imageVector = if (isLearned) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = stringResource(R.string.mark_learned),
                        tint = if (isLearned) AchievementUnlocked else AchievementUnlocked.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = definition,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            // Learn more hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap to explore",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedQuickActionsSection(
    onJournalClick: () -> Unit,
    onFutureMessageClick: () -> Unit,
    onVocabularyClick: () -> Unit,
    onQuotesClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedQuickActionItem(
                icon = Icons.Filled.Book,
                label = "Journal",
                color = MoodCalm,
                onClick = onJournalClick,
                modifier = Modifier.weight(1f),
                animationDelay = 0
            )
            AnimatedQuickActionItem(
                icon = Icons.Filled.Schedule,
                label = "Future",
                color = MoodExcited,
                onClick = onFutureMessageClick,
                modifier = Modifier.weight(1f),
                animationDelay = 100
            )
            AnimatedQuickActionItem(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                label = "Words",
                color = MoodMotivated,
                onClick = onVocabularyClick,
                modifier = Modifier.weight(1f),
                animationDelay = 200
            )
            AnimatedQuickActionItem(
                icon = Icons.Filled.FormatQuote,
                label = "Quotes",
                color = MoodGrateful,
                onClick = onQuotesClick,
                modifier = Modifier.weight(1f),
                animationDelay = 300
            )
        }
    }
}

@Composable
private fun AnimatedQuickActionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isVisible -> 1f
            else -> 0.8f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "action_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "action_alpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "action_$label")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + animationDelay, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_pulse"
    )

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(iconScale)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AnimatedProverbCard(
    proverb: String,
    meaning: String,
    origin: String
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(400)
        isVisible = true
    }

    val slideOffset by animateIntAsState(
        targetValue = if (isVisible) 0 else 50,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "proverb_slide"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "proverb_alpha"
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .offset(y = slideOffset.dp)
            .alpha(alpha),
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.proverb_of_the_day),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = proverb,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = meaning,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )

            if (origin.isNotBlank()) {
                Text(
                    text = "— $origin",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun AnimatedIdiomCard(
    idiom: String,
    meaning: String,
    example: String
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "idiom_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "idiom_alpha"
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale)
            .alpha(alpha)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.idiom_of_the_day),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = idiom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = meaning,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            if (example.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Example: \"$example\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AnimatedBuddhaThoughtCard(
    thought: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "buddha")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buddha_glow"
    )

    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buddha_icon"
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        backgroundColor = ProdyPrimary.copy(alpha = glowAlpha)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SelfImprovement,
                    contentDescription = null,
                    tint = ProdyPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(iconScale)
                )
                Text(
                    text = "Buddha's Thought",
                    style = MaterialTheme.typography.labelLarge,
                    color = ProdyPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = thought,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
            )
        }
    }
}

@Composable
private fun AnimatedRecentActivityCard(
    journalEntriesThisWeek: Int,
    wordsLearnedThisWeek: Int,
    daysActiveThisWeek: Int
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(600)
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "activity_scale"
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "This Week",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AchievementUnlocked.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Keep going!",
                        style = MaterialTheme.typography.labelSmall,
                        color = AchievementUnlocked,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnimatedActivityStat(
                    value = journalEntriesThisWeek,
                    label = "Entries",
                    icon = Icons.Outlined.Book,
                    color = MoodCalm,
                    animationDelay = 0,
                    isVisible = isVisible
                )
                AnimatedActivityStat(
                    value = wordsLearnedThisWeek,
                    label = "Words",
                    icon = Icons.Outlined.School,
                    color = MoodMotivated,
                    animationDelay = 100,
                    isVisible = isVisible
                )
                AnimatedActivityStat(
                    value = daysActiveThisWeek,
                    label = "Active",
                    icon = Icons.Outlined.CalendarMonth,
                    color = AchievementUnlocked,
                    animationDelay = 200,
                    isVisible = isVisible,
                    suffix = "/7"
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
    animationDelay: Int,
    isVisible: Boolean,
    suffix: String = ""
) {
    var showStat by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            kotlinx.coroutines.delay(animationDelay.toLong())
            showStat = true
        }
    }

    val animatedValue = AnimatedCounter(targetValue = if (showStat) value else 0)

    val scale by animateFloatAsState(
        targetValue = if (showStat) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "stat_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$animatedValue$suffix",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> stringResource(R.string.good_morning)
        hour < 17 -> stringResource(R.string.good_afternoon)
        else -> stringResource(R.string.good_evening)
    }
}
