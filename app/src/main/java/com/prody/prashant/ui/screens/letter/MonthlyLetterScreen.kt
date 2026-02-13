package com.prody.prashant.ui.screens.letter
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.domain.model.MonthlyLetter
import com.prody.prashant.ui.theme.*
import com.prody.prashant.util.SecureScreen

/**
 * Monthly Letter Screen - Full-screen letter reading experience with envelope animation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyLetterScreen(
    letterId: Long? = null,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: MonthlyLetterViewModel = hiltViewModel()
) {
    // Security: Prevent screenshots of private monthly letters
    SecureScreen()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load specific letter if ID provided
    LaunchedEffect(letterId) {
        if (letterId != null) {
            viewModel.loadLetter(letterId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Reflection") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(ProdyIcons.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(ProdyIcons.History, "History")
                    }
                    if (uiState.currentLetter != null) {
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                if (uiState.currentLetter?.isFavorite == true)
                                    ProdyIcons.Favorite
                                else
                                    ProdyIcons.FavoriteBorder,
                                "Favorite"
                            )
                        }
                        IconButton(onClick = { viewModel.showShareDialog() }) {
                            Icon(ProdyIcons.Share, "Share")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.currentLetter != null -> {
                    if (uiState.envelopeOpened) {
                        LetterContent(
                            letter = uiState.currentLetter!!,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        EnvelopeView(
                            letter = uiState.currentLetter!!,
                            onOpen = { viewModel.openEnvelope() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                else -> {
                    EmptyLetterState(
                        onGenerate = { viewModel.generateLetterForPreviousMonth() },
                        isGenerating = uiState.isGenerating,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // Error/Success snackbars
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar
            viewModel.clearError()
        }
    }

    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar
            viewModel.clearSuccess()
        }
    }
}

/**
 * Envelope view with tap-to-open animation
 */
@Composable
fun EnvelopeView(
    letter: MonthlyLetter,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Envelope illustration
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(
                        onClick = onOpen,
                        onClickLabel = "Open letter"
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        ProdyIcons.Mail,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        letter.monthYear.month.name,
                        style = LetterTitleStyle,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        letter.monthYear.year.toString(),
                        style = LetterMetadataStyle,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            // Prompt to open
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Your ${letter.monthYear.month.name} letter is ready",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Tap to open",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Preview stats
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                QuickStat(
                    value = letter.activitySummary.totalEntries.toString(),
                    label = "entries"
                )
                QuickStat(
                    value = letter.activitySummary.activeDays.toString(),
                    label = "active days"
                )
                QuickStat(
                    value = "${letter.activitySummary.totalWords / 1000}k",
                    label = "words"
                )
            }
        }
    }
}

@Composable
fun QuickStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            value,
            style = LetterStatNumberStyle,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            style = LetterMetadataStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Letter content with beautiful typography and scrolling
 */
@Composable
fun LetterContent(
    letter: MonthlyLetter,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                letter.monthYear.month.name,
                style = LetterTitleStyle,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                letter.monthYear.year.toString(),
                style = LetterMetadataStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Greeting
        Text(
            letter.greeting,
            style = LetterGreetingStyle,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Activity Summary
        LetterSection("This Month") {
            Text(
                buildActivityNarrative(letter.activitySummary),
                style = LetterBodyStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Theme Analysis
        if (letter.themeAnalysis.narrative.isNotEmpty()) {
            LetterSection("What You Wrote About") {
                Text(
                    letter.themeAnalysis.narrative,
                    style = LetterBodyStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Mood Journey
        if (letter.moodJourney.narrative.isNotEmpty()) {
            LetterSection("How You Felt") {
                Text(
                    letter.moodJourney.narrative,
                    style = LetterBodyStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Buddha's Insight
        if (letter.buddhaInsight.observation.isNotEmpty()) {
            LetterSection("A Pattern I Noticed") {
                Text(
                    letter.buddhaInsight.observation,
                    style = LetterBodyStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                        .padding(16.dp)
                )
            }
        }

        // Highlight Quote
        letter.highlight?.quote?.let { quote ->
            LetterSection("Something You Wrote") {
                Text(
                    "\"$quote\"",
                    style = LetterQuoteStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                letter.highlight.reason?.let { reason ->
                    Text(
                        reason,
                        style = LetterMetadataStyle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Milestones
        if (letter.milestones.achieved.isNotEmpty()) {
            LetterSection("Milestones") {
                letter.milestones.achieved.forEach { milestone ->
                    MilestoneItem(
                        title = milestone.title,
                        description = milestone.description
                    )
                }
            }
        }

        // Comparison
        letter.comparison?.note?.let { note ->
            Text(
                note,
                style = LetterBodyStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Closing
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                letter.closing.message,
                style = LetterClosingStyle,
                color = MaterialTheme.colorScheme.onSurface
            )

            letter.closing.encouragement?.let { encouragement ->
                Text(
                    encouragement,
                    style = LetterBodyStyle,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun LetterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            title.uppercase(),
            style = LetterSectionHeaderStyle,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
fun MilestoneItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            ProdyIcons.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                description,
                style = LetterMetadataStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyLetterState(
    onGenerate: () -> Unit,
    isGenerating: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(32.dp)
    ) {
        Icon(
            ProdyIcons.Mail,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "No letter yet",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            "Your monthly letter will be generated on the first day of next month",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onGenerate,
            enabled = !isGenerating
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Generate Now")
        }
    }
}

/**
 * Build activity narrative from summary
 */
private fun buildActivityNarrative(summary: com.prody.prashant.domain.model.ActivitySummary): String {
    return buildString {
        append("This month, you showed up ${summary.totalEntries} times. ")
        append("That's ${summary.activeDays} active days. ")
        if (summary.totalWords > 0) {
            append("You wrote ${summary.totalWords} words total. ")
        }
        if (summary.averageWordsPerEntry > 0) {
            append("About ${summary.averageWordsPerEntry} words per entry on average.")
        }
    }
}
