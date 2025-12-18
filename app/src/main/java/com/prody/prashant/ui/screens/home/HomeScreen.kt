package com.prody.prashant.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.prody.prashant.ui.theme.PlayfairFamily
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Home Screen - Redesigned Dashboard
 *
 * A clean, minimal, modern interface featuring:
 * - Personalized greeting with streak and points badge
 * - Gratitude and Challenges reflection cards
 * - Quick action buttons for core features
 * - Daily Wisdom section with Quote, Word, Idiom, and Proverb cards
 *
 * Design Principles:
 * - Flat, shadow-free design
 * - Clear visual hierarchy
 * - 8dp spacing grid
 * - Green accents for interactivity
 * - Elegant typography with Poppins + Playfair Display
 */

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

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500, easing = EaseOutCubic)),
        exit = fadeOut(animationSpec = tween(300, easing = EaseOutCubic))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header with greeting and stats
            item {
                HomeHeader(
                    greeting = greeting,
                    userName = uiState.userName,
                    currentStreak = uiState.currentStreak,
                    totalPoints = uiState.totalPoints
                )
            }

            // Gratitude and Challenges cards row
            item {
                ReflectionCardsRow(
                    onGratitudeClick = onNavigateToJournal,
                    onChallengesClick = onNavigateToChallenges
                )
            }

            // Quick Actions bar
            item {
                QuickActionsBar(
                    onJournalClick = onNavigateToJournal,
                    onFutureClick = onNavigateToFutureMessage,
                    onQuotesClick = onNavigateToQuotes
                )
            }

            // Daily Wisdom section header
            item {
                DailyWisdomHeader()
            }

            // Quote of the Day card
            item {
                QuoteOfTheDayCard(
                    quote = uiState.dailyQuote,
                    author = uiState.dailyQuoteAuthor,
                    onShareClick = { /* Share functionality */ }
                )
            }

            // Word and Idiom side-by-side cards
            item {
                WordAndIdiomRow(
                    word = uiState.wordOfTheDay,
                    pronunciation = uiState.wordPronunciation,
                    wordDefinition = uiState.wordDefinition,
                    idiom = uiState.dailyIdiom,
                    idiomMeaning = uiState.idiomMeaning,
                    idiomExample = uiState.idiomExample,
                    onLearnClick = { viewModel.markWordAsLearned() }
                )
            }

            // Proverb card
            if (uiState.dailyProverb.isNotBlank()) {
                item {
                    ProverbCard(
                        proverb = uiState.dailyProverb,
                        origin = uiState.proverbOrigin
                    )
                }
            }
        }
    }
}

/**
 * Clean header with greeting and stats badge
 */
@Composable
private fun HomeHeader(
    greeting: String,
    userName: String,
    currentStreak: Int,
    totalPoints: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Greeting column
            Column {
                Text(
                    text = greeting.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Stats badge
            StatsBadge(
                streak = currentStreak,
                points = totalPoints
            )
        }
    }
}

/**
 * Compact stats badge showing streak and points
 */
@Composable
private fun StatsBadge(
    streak: Int,
    points: Int
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Streak
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "\uD83D\uDD25",
                    fontSize = 14.sp
                )
                Text(
                    text = streak.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )

            // Points
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "\u2B50",
                    fontSize = 14.sp
                )
                Text(
                    text = points.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Reflection cards row - Gratitude and Challenges
 */
@Composable
private fun ReflectionCardsRow(
    onGratitudeClick: () -> Unit,
    onChallengesClick: () -> Unit
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
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Gratitude card
        ReflectionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.NightsStay,
            iconTint = Color(0xFF5B8DEF),
            title = "Gratitude",
            subtitle = reflectionText,
            onClick = onGratitudeClick
        )

        // Challenges card
        ReflectionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.EmojiEvents,
            iconTint = Color(0xFFFF9500),
            title = "Challenges",
            subtitle = "Compete now",
            showNewBadge = true,
            onClick = onChallengesClick
        )
    }
}

/**
 * Individual reflection card
 */
@Composable
private fun ReflectionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    showNewBadge: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // NEW badge
                if (showNewBadge) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFF3B30)
                    ) {
                        Text(
                            text = "NEW",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Quick actions bar with Journal, Future, Quotes
 */
@Composable
private fun QuickActionsBar(
    onJournalClick: () -> Unit,
    onFutureClick: () -> Unit,
    onQuotesClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Journal
            QuickActionItem(
                icon = Icons.Outlined.Book,
                label = "Journal",
                onClick = onJournalClick
            )

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )

            // Future
            QuickActionItem(
                icon = Icons.Outlined.Send,
                label = "Future",
                onClick = onFutureClick
            )

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )

            // Quotes
            QuickActionItem(
                icon = Icons.Outlined.FormatQuote,
                label = "Quotes",
                onClick = onQuotesClick
            )
        }
    }
}

/**
 * Individual quick action item
 */
@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Daily Wisdom section header with date
 */
@Composable
private fun DailyWisdomHeader() {
    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val currentDate = dateFormat.format(Date()).uppercase()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Daily Wisdom",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = currentDate,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

/**
 * Quote of the Day card
 */
@Composable
private fun QuoteOfTheDayCard(
    quote: String,
    author: String,
    onShareClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with green dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "QUOTE OF THE DAY",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quote text with Playfair-like styling
            Text(
                text = "\"$quote\"",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = PlayfairFamily,
                    fontStyle = FontStyle.Italic,
                    fontSize = 18.sp,
                    lineHeight = 28.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Author and Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "— $author",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(onClick = onShareClick)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "SHARE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = Icons.Outlined.OpenInNew,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Word and Idiom cards side by side
 */
@Composable
private fun WordAndIdiomRow(
    word: String,
    pronunciation: String,
    wordDefinition: String,
    idiom: String,
    idiomMeaning: String,
    idiomExample: String,
    onLearnClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Word card
        WordCard(
            modifier = Modifier.weight(1f),
            word = word,
            pronunciation = pronunciation,
            definition = wordDefinition,
            onLearnClick = onLearnClick
        )

        // Idiom card
        IdiomCard(
            modifier = Modifier.weight(1f),
            idiom = idiom,
            meaning = idiomMeaning,
            example = idiomExample
        )
    }
}

/**
 * Word of the Day card
 */
@Composable
private fun WordCard(
    modifier: Modifier = Modifier,
    word: String,
    pronunciation: String,
    definition: String,
    onLearnClick: () -> Unit
) {
    var isLearned by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WORD",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE6B422),
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    tint = Color(0xFFE6B422).copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Word
            Text(
                text = word,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Pronunciation
            if (pronunciation.isNotBlank()) {
                Text(
                    text = "/$pronunciation/",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Definition
            Text(
                text = definition,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Learn button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        if (!isLearned) {
                            isLearned = true
                            onLearnClick()
                        }
                    },
                shape = RoundedCornerShape(20.dp),
                color = if (isLearned)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                tonalElevation = 0.dp
            ) {
                Text(
                    text = if (isLearned) "LEARNED!" else "LEARN +25",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Idiom of the Day card
 */
@Composable
private fun IdiomCard(
    modifier: Modifier = Modifier,
    idiom: String,
    meaning: String,
    example: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IDIOM",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFB39DDB),
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = null,
                    tint = Color(0xFFB39DDB).copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Idiom
            Text(
                text = idiom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Meaning
            Text(
                text = meaning,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Example
            if (example.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"$example\"",
                    style = MaterialTheme.typography.labelSmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Proverb of the Day card
 */
@Composable
private fun ProverbCard(
    proverb: String,
    origin: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF26A69A).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Psychology,
                    contentDescription = null,
                    tint = Color(0xFF26A69A),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Header with origin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROVERB",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF26A69A),
                        letterSpacing = 1.sp
                    )
                    if (origin.isNotBlank()) {
                        Text(
                            text = origin,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Proverb text
                Text(
                    text = "\"$proverb\"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = PlayfairFamily,
                        fontStyle = FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
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
