package com.prody.prashant.ui.screens.digest
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.WeeklyDigestEntity
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.components.ProdyCard
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyDigestScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEntry: (Long) -> Unit,
    viewModel: WeeklyDigestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle messages
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    // Handle navigation
    LaunchedEffect(uiState.shouldNavigateToEntry) {
        if (uiState.shouldNavigateToEntry && uiState.highlightEntryId != null) {
            onNavigateToEntry(uiState.highlightEntryId!!)
            viewModel.clearNavigation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Weekly Digest")
                        if (uiState.weekRangeFormatted.isNotEmpty()) {
                            Text(
                                text = uiState.weekRangeFormatted,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = ProdyIcons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.canGenerateDigest && !uiState.isGenerating) {
                        TextButton(onClick = viewModel::generateWeeklyDigest) {
                            Icon(
                                imageVector = ProdyIcons.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Generate")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading || uiState.isGenerating -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        if (uiState.isGenerating) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Generating your weekly digest...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                uiState.selectedDigest == null && uiState.allDigests.isEmpty() -> {
                    EmptyDigestState(
                        canGenerate = uiState.canGenerateDigest,
                        onGenerate = viewModel::generateWeeklyDigest
                    )
                }

                else -> {
                    DigestContent(
                        digest = uiState.selectedDigest,
                        allDigests = uiState.allDigests,
                        moodDistribution = uiState.moodDistribution,
                        topThemes = uiState.topThemes,
                        patterns = uiState.patterns,
                        highlightEntryId = uiState.highlightEntryId,
                        onDigestSelected = viewModel::selectDigest,
                        onViewHighlight = viewModel::navigateToHighlightEntry,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun DigestContent(
    digest: WeeklyDigestEntity?,
    allDigests: List<WeeklyDigestEntity>,
    moodDistribution: List<MoodDistributionItem>,
    topThemes: List<String>,
    patterns: List<String>,
    highlightEntryId: Long?,
    onDigestSelected: (WeeklyDigestEntity) -> Unit,
    onViewHighlight: () -> Unit,
    viewModel: WeeklyDigestViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Week selector (if multiple digests)
        if (allDigests.size > 1) {
            item {
                WeekSelector(
                    digests = allDigests,
                    selectedDigest = digest,
                    onDigestSelected = onDigestSelected
                )
            }
        }

        digest?.let { currentDigest ->
            // Overview stats
            item {
                StatsOverviewCard(digest = currentDigest, viewModel = viewModel)
            }

            // Mood distribution
            if (moodDistribution.isNotEmpty()) {
                item {
                    MoodDistributionCard(distribution = moodDistribution)
                }
            }

            // Mood trend
            item {
                MoodTrendCard(
                    trend = currentDigest.moodTrend,
                    dominantMood = currentDigest.dominantMood,
                    viewModel = viewModel
                )
            }

            // Top themes
            if (topThemes.isNotEmpty()) {
                item {
                    ThemesCard(themes = topThemes, viewModel = viewModel)
                }
            }

            // Patterns
            if (patterns.isNotEmpty()) {
                item {
                    PatternsCard(patterns = patterns, viewModel = viewModel)
                }
            }

            // Buddha reflection
            currentDigest.buddhaReflection?.let { reflection ->
                item {
                    BuddhaReflectionCard(reflection = reflection)
                }
            }

            // Highlight quote
            currentDigest.highlightQuote?.let { quote ->
                item {
                    HighlightCard(
                        quote = quote,
                        hasEntry = highlightEntryId != null,
                        onViewEntry = onViewHighlight
                    )
                }
            }

            // Comparison with previous week
            item {
                WeekComparisonCard(digest = currentDigest, viewModel = viewModel)
            }

            // Celebration or encouragement message
            item {
                CelebrationCard(
                    message = getCelebrationMessage(currentDigest),
                    entriesCount = currentDigest.entriesCount,
                    activeDays = currentDigest.activeDays
                )
            }

            // Share button
            item {
                ShareSummaryButton(
                    onClick = { /* TODO: Implement share */ },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun getCelebrationMessage(digest: WeeklyDigestEntity): String {
    return when {
        digest.activeDays == 7 -> "Seven days, seven entries. You showed up for yourself every single day this week! 🌟"
        digest.entriesCount >= 7 -> "You journaled ${digest.entriesCount} times this week. That kind of consistency compounds."
        digest.entriesCount >= 5 -> "You maintained a strong practice this week. ${digest.entriesCount} entries - that's dedication."
        digest.entriesCount >= 3 -> "You've been checking in with yourself regularly. Keep showing up."
        digest.entriesCount >= 1 -> "Even in busy weeks, you made time for reflection. That matters."
        else -> "Life gets busy, and that's okay. Your journal is here whenever you're ready to return."
    }
}

@Composable
private fun WeekSelector(
    digests: List<WeeklyDigestEntity>,
    selectedDigest: WeeklyDigestEntity?,
    onDigestSelected: (WeeklyDigestEntity) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(digests) { digest ->
            val isSelected = digest.id == selectedDigest?.id
            val startDate = Instant.ofEpochMilli(digest.weekStartDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            FilterChip(
                selected = isSelected,
                onClick = { onDigestSelected(digest) },
                label = {
                    Text(startDate.format(DateTimeFormatter.ofPattern("MMM d")))
                },
                leadingIcon = if (!digest.isRead && !isSelected) {
                    {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                } else null
            )
        }
    }
}

@Composable
private fun StatsOverviewCard(
    digest: WeeklyDigestEntity,
    viewModel: WeeklyDigestViewModel
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Week at a Glance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = digest.entriesCount.toString(),
                    label = "Entries",
                    icon = ProdyIcons.Article
                )
                StatItem(
                    value = digest.totalWordsWritten.toString(),
                    label = "Words",
                    icon = ProdyIcons.TextFields
                )
                StatItem(
                    value = "${digest.activeDays}/7",
                    label = "Active Days",
                    icon = ProdyIcons.CalendarToday
                )
            }

            if (digest.microEntriesCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ProdyIcons.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${digest.microEntriesCount} quick thoughts captured",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MoodDistributionCard(distribution: List<MoodDistributionItem>) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Emotional Landscape",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            distribution.take(5).forEach { item ->
                val mood = Mood.fromString(item.mood)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mood?.emoji ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mood?.displayName ?: item.mood,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    // Progress bar
                    LinearProgressIndicator(
                        progress = { item.percentage },
                        modifier = Modifier
                            .width(80.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(item.percentage * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodTrendCard(
    trend: String,
    dominantMood: String?,
    viewModel: WeeklyDigestViewModel
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = when (trend) {
                    "improving" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    "declining" -> Color(0xFFFF5722).copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Icon(
                    imageVector = when (trend) {
                        "improving" -> ProdyIcons.AutoMirrored.Filled.TrendingUp
                        "declining" -> ProdyIcons.AutoMirrored.Filled.TrendingDown
                        else -> ProdyIcons.TrendingFlat
                    },
                    contentDescription = null,
                    tint = when (trend) {
                        "improving" -> Color(0xFF4CAF50)
                        "declining" -> Color(0xFFFF5722)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mood Trend",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = viewModel.getMoodTrendDescription(trend),
                    style = MaterialTheme.typography.bodyMedium
                )
                dominantMood?.let { mood ->
                    val moodObj = Mood.fromString(mood)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dominant mood: ${moodObj?.emoji ?: ""} ${moodObj?.displayName ?: mood}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemesCard(
    themes: List<String>,
    viewModel: WeeklyDigestViewModel
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ProdyIcons.Tag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "What You Wrote About",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(themes) { theme ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(viewModel.getThemeDisplayName(theme)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PatternsCard(
    patterns: List<String>,
    viewModel: WeeklyDigestViewModel
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ProdyIcons.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Patterns Noticed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            patterns.forEach { pattern ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ProdyIcons.Circle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(8.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = viewModel.getPatternDisplayName(pattern),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun BuddhaReflectionCard(reflection: String) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ProdyIcons.SelfImprovement,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Buddha's Reflection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = reflection,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HighlightCard(
    quote: String,
    hasEntry: Boolean,
    onViewEntry: () -> Unit
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ProdyIcons.FormatQuote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Highlight of the Week",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "\"$quote\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (hasEntry) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onViewEntry) {
                    Text("View full entry")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = ProdyIcons.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekComparisonCard(
    digest: WeeklyDigestEntity,
    viewModel: WeeklyDigestViewModel
) {
    val hasComparison = digest.previousWeekEntriesCount > 0 || digest.previousWeekWordsWritten > 0

    if (!hasComparison) return

    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Compared to Last Week",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ComparisonItem(
                    label = "Entries",
                    current = digest.entriesCount,
                    previous = digest.previousWeekEntriesCount,
                    changePercent = digest.entriesChangePercent
                )
                ComparisonItem(
                    label = "Words",
                    current = digest.totalWordsWritten,
                    previous = digest.previousWeekWordsWritten,
                    changePercent = digest.wordsChangePercent
                )
            }
        }
    }
}

@Composable
private fun ComparisonItem(
    label: String,
    current: Int,
    previous: Int,
    changePercent: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = current.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            when {
                changePercent > 0 -> {
                    Icon(
                        imageVector = ProdyIcons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                }
                changePercent < 0 -> {
                    Icon(
                        imageVector = ProdyIcons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        if (changePercent != 0) {
            Text(
                text = "${if (changePercent > 0) "+" else ""}$changePercent%",
                style = MaterialTheme.typography.labelSmall,
                color = if (changePercent > 0) Color(0xFF4CAF50) else Color(0xFFFF5722)
            )
        }
    }
}

@Composable
private fun CelebrationCard(
    message: String,
    entriesCount: Int,
    activeDays: Int
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Celebration icon
                Icon(
                    imageVector = when {
                        activeDays == 7 -> ProdyIcons.EmojiEvents
                        entriesCount >= 5 -> ProdyIcons.AutoAwesome
                        entriesCount >= 3 -> ProdyIcons.LocalFireDepartment
                        else -> ProdyIcons.FavoriteBorder
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Keep going. Your practice is building something lasting.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun ShareSummaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ProdyIcons.Share,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Share Your Weekly Summary",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EmptyDigestState(
    canGenerate: Boolean,
    onGenerate: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.Summarize,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No weekly digest yet",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (canGenerate) {
                    "Generate your first weekly digest to see patterns and insights from your journal entries."
                } else {
                    "Keep journaling this week. Your digest will be ready soon!"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (canGenerate) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onGenerate) {
                    Icon(ProdyIcons.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Digest")
                }
            }
        }
    }
}
