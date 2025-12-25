package com.prody.prashant.ui.screens.flashcard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Main flashcard review screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: FlashcardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashcard Review") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (!uiState.sessionComplete && uiState.currentIndex > 0) {
                        IconButton(onClick = { viewModel.undoLastAction() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo"
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.sessionComplete -> {
                    SessionCompleteState(
                        knownCount = uiState.knownCount,
                        unknownCount = uiState.unknownCount,
                        skippedCount = uiState.skippedCount,
                        accuracy = uiState.accuracy,
                        durationMinutes = viewModel.getSessionDurationMinutes(),
                        xpEarned = uiState.sessionXpEarned,
                        onRestartSession = { viewModel.restartSession() },
                        onLoadNewCards = { viewModel.loadReviewCards() },
                        onFinish = onNavigateBack
                    )
                }
                uiState.cards.isEmpty() -> {
                    EmptyFlashcardState()
                }
                else -> {
                    FlashcardReviewContent(
                        uiState = uiState,
                        onKnow = { viewModel.onKnow() },
                        onDontKnow = { viewModel.onDontKnow() },
                        onSkip = { viewModel.onSkip() },
                        onHard = { viewModel.onHard() },
                        onPerfect = { viewModel.onPerfect() },
                        onSpeak = { viewModel.speakWord(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardReviewContent(
    uiState: FlashcardUiState,
    onKnow: () -> Unit,
    onDontKnow: () -> Unit,
    onSkip: () -> Unit,
    onHard: () -> Unit,
    onPerfect: () -> Unit,
    onSpeak: (String) -> Unit
) {
    // Track if user has flipped the card to see the answer
    var hasSeenAnswer by remember(uiState.currentIndex) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator
        FlashcardProgress(
            current = uiState.currentIndex,
            total = uiState.cards.size
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Session stats
        SessionStats(
            known = uiState.knownCount,
            unknown = uiState.unknownCount,
            skipped = uiState.skippedCount
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Flashcard stack
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FlashcardStackWithFlipTracking(
                cards = uiState.cards,
                currentIndex = uiState.currentIndex,
                onSwipeLeft = { onDontKnow() },
                onSwipeRight = { onKnow() },
                onSwipeUp = { onSkip() },
                onSpeak = onSpeak,
                onFlip = { hasSeenAnswer = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Control buttons - show detailed options after user flips the card
        FlashcardControls(
            onDontKnow = onDontKnow,
            onSkip = onSkip,
            onKnow = onKnow,
            onHard = onHard,
            onPerfect = onPerfect,
            showDetailedOptions = hasSeenAnswer
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Swipe instructions (changes based on state)
        Text(
            text = if (hasSeenAnswer) {
                "Rate how well you knew this word"
            } else {
                "Tap card to reveal answer, then rate your recall"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading cards...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SessionCompleteState(
    knownCount: Int,
    unknownCount: Int,
    skippedCount: Int,
    accuracy: Float,
    durationMinutes: Int,
    xpEarned: Int,
    onRestartSession: () -> Unit,
    onLoadNewCards: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Session Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Great work! You reviewed ${knownCount + unknownCount + skippedCount} cards.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Statistics
        SessionStats(
            known = knownCount,
            unknown = unknownCount,
            skipped = skippedCount
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Accuracy, duration, and XP earned
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${accuracy.toInt()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Accuracy",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${durationMinutes}m",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "+$xpEarned",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "XP Earned",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action buttons
        Button(
            onClick = onLoadNewCards,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Review More Cards")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRestartSession,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Review Same Cards Again")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Finish")
        }
    }
}
