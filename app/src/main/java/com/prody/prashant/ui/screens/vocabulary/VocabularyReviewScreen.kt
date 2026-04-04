package com.prody.prashant.ui.screens.vocabulary
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.ui.theme.PoppinsFamily

/**
 * Vocabulary Review Screen
 *
 * Shows vocabulary progress including:
 * - Words learned this week
 * - Words used in context (highlighted with celebration)
 * - Words not yet used (gentle nudge)
 * - Total vocabulary growth chart
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyReviewScreen(
    onNavigateBack: () -> Unit,
    onWordClick: (Long) -> Unit,
    viewModel: VocabularyReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error as snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vocabulary Review",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = ProdyIcons.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = ProdyIcons.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (!uiState.hasData) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Statistics cards
                item {
                    StatisticsSection(uiState = uiState)
                }

                // Words used in context
                if (uiState.hasWordsUsed) {
                    item {
                        WordsUsedSection(
                            words = uiState.wordsUsedInContext,
                            onWordClick = onWordClick
                        )
                    }
                }

                // Words not yet used
                if (uiState.hasUnusedWords) {
                    item {
                        WordsNotUsedSection(
                            words = uiState.wordsNotYetUsed,
                            onWordClick = onWordClick
                        )
                    }
                }

                // Words needing practice
                if (uiState.wordsNeedingPractice.isNotEmpty()) {
                    item {
                        WordsNeedingPracticeSection(
                            words = uiState.wordsNeedingPractice,
                            onWordClick = onWordClick
                        )
                    }
                }

                // Growth chart
                if (uiState.growthData.isNotEmpty()) {
                    item {
                        GrowthChartSection(growthData = uiState.growthData)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsSection(uiState: VocabularyReviewUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Your Progress",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = ProdyIcons.School,
                label = "Learned",
                value = "${uiState.totalWordsLearned}",
                subtitle = "+${uiState.wordsLearnedThisWeek} this week",
                color = Color(0xFF4CAF50)
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = ProdyIcons.CheckCircle,
                label = "Used",
                value = "${uiState.totalWordsUsed}",
                subtitle = "${uiState.usagePercentage}% applied",
                color = Color(0xFF2196F3)
            )
        }

        if (uiState.totalBonusPoints > 0) {
            StatCard(
                modifier = Modifier.fillMaxWidth(),
                icon = ProdyIcons.EmojiEvents,
                label = "Bonus Discipline Points",
                value = "+${uiState.totalBonusPoints}",
                subtitle = "Earned from vocabulary usage",
                color = Color(0xFFFF9800)
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    subtitle: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = color
            )

            Text(
                text = label,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = subtitle,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun WordsUsedSection(
    words: List<VocabularyEntity>,
    onWordClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Words Used in Context",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Great work! You've applied these words in your journal entries.",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = words,
                key = { word -> "used_${word.id}" }
            ) { word ->
                WordChip(
                    word = word,
                    isUsed = true,
                    onClick = { onWordClick(word.id) }
                )
            }
        }
    }
}

@Composable
private fun WordsNotUsedSection(
    words: List<VocabularyEntity>,
    onWordClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.Lightbulb,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Words to Practice",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try using these words in your next journal entry!",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = words,
                key = { word -> "practice_${word.id}" }
            ) { word ->
                WordChip(
                    word = word,
                    isUsed = false,
                    onClick = { onWordClick(word.id) }
                )
            }
        }
    }
}

@Composable
private fun WordsNeedingPracticeSection(
    words: List<VocabularyEntity>,
    onWordClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.TipsAndUpdates,
                contentDescription = null,
                tint = Color(0xFF9C27B0),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Refresh Your Memory",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "These words could use some practice.",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = words,
                key = { word -> "refresh_${word.id}" }
            ) { word ->
                WordChip(
                    word = word,
                    isUsed = false,
                    onClick = { onWordClick(word.id) }
                )
            }
        }
    }
}

@Composable
private fun WordChip(
    word: VocabularyEntity,
    isUsed: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isUsed) Color(0xFF4CAF50).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .width(140.dp)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = word.word,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                if (isUsed) {
                    Icon(
                        imageVector = ProdyIcons.Check,
                        contentDescription = "Used",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = word.partOfSpeech.ifEmpty { "word" },
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = word.definition.take(50) + if (word.definition.length > 50) "..." else "",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun GrowthChartSection(growthData: List<VocabularyGrowthPoint>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Vocabulary Growth",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Legend
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ChartLegendItem(
                        color = Color(0xFF4CAF50),
                        label = "Learned"
                    )
                    ChartLegendItem(
                        color = Color(0xFF2196F3),
                        label = "Used"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simple bar chart
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    growthData.takeLast(8).forEach { point ->
                        GrowthChartBar(point = point)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GrowthChartBar(point: VocabularyGrowthPoint) {
    Column {
        Text(
            text = point.weekLabel,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Learned bar
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .width((point.wordsLearned * 20).dp.coerceAtLeast(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF4CAF50))
            )

            Text(
                text = "${point.wordsLearned}",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Used bar
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .width((point.wordsUsed * 20).dp.coerceAtLeast(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF2196F3))
            )

            Text(
                text = "${point.wordsUsed}",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = "Start Learning Vocabulary",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Learn new words and track your progress here",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
