package com.prody.prashant.ui.screens.vocabulary
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.ui.components.DailyFocusSelector
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.components.WisdomQuestChallenge
import com.prody.prashant.ui.components.WisdomQuestResult
import com.prody.prashant.ui.theme.*

@Composable
fun VocabularyDetailScreen(
    wordId: Long,
    onNavigateBack: () -> Unit,
    viewModel: VocabularyDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isTtsInitialized by viewModel.isTtsInitialized.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()

    LaunchedEffect(wordId) {
        viewModel.loadWord(wordId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.vocabulary)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = ProdyIcons.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    // Text-to-Speech button
                    if (isTtsInitialized) {
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    viewModel.stopSpeaking()
                                } else {
                                    viewModel.speakAll()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) ProdyIcons.Stop
                                else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isSpeaking) "Stop speaking" else "Speak word",
                                tint = if (isSpeaking) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.word?.isFavorite == true) ProdyIcons.Favorite
                            else ProdyIcons.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.word?.isFavorite == true) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        // Loading state
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Loading word...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Scaffold
        }

        // Error state
        uiState.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.retry(wordId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Retry")
                    }
                    TextButton(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }
            }
            return@Scaffold
        }

        // Word not found state
        if (uiState.word == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Word not found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "This vocabulary word may have been deleted.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Go Back")
                    }
                }
            }
            return@Scaffold
        }

        uiState.word?.let { word ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Word header
                ProdyCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = word.word,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (word.isLearned) {
                                Surface(
                                    shape = ChipShape,
                                    color = AchievementUnlocked.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = ProdyIcons.CheckCircle,
                                            contentDescription = null,
                                            tint = AchievementUnlocked,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Learned",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AchievementUnlocked
                                        )
                                    }
                                }
                            }
                        }

                        if (word.pronunciation.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "/${word.pronunciation}/",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isTtsInitialized) {
                                    IconButton(
                                        onClick = { viewModel.speakWord() },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Pronounce word",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (word.partOfSpeech.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = ChipShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = word.partOfSpeech,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Definition
                DetailSection(
                    title = stringResource(R.string.definition),
                    content = word.definition
                )

                // Example
                if (word.exampleSentence.isNotBlank()) {
                    DetailSection(
                        title = stringResource(R.string.example),
                        content = "\"${word.exampleSentence}\"",
                        isItalic = true
                    )
                }

                // Synonyms
                if (word.synonyms.isNotBlank()) {
                    DetailSection(
                        title = stringResource(R.string.synonyms),
                        content = word.synonyms.split(",").joinToString(", ") { it.trim() }
                    )
                }

                // Antonyms
                if (word.antonyms.isNotBlank()) {
                    DetailSection(
                        title = stringResource(R.string.antonyms),
                        content = word.antonyms.split(",").joinToString(", ") { it.trim() }
                    )
                }

                // Origin
                if (word.origin.isNotBlank()) {
                    DetailSection(
                        title = stringResource(R.string.origin),
                        content = word.origin
                    )
                }

                // Wisdom Quest section - replaces boring "Mark as Learned" button
                if (!word.isLearned) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Wisdom Quest animated content
                    AnimatedContent(
                        targetState = uiState.wisdomQuestState,
                        transitionSpec = {
                            (fadeIn() + scaleIn(initialScale = 0.95f)) togetherWith
                                (fadeOut() + scaleOut(targetScale = 0.95f))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        label = "wisdom_quest_transition"
                    ) { questState ->
                        when (questState) {
                            is WisdomQuestState.Idle -> {
                                // Start Challenge button
                                Button(
                                    onClick = { viewModel.startWisdomQuest() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ProdyAccentGreen,
                                        contentColor = androidx.compose.ui.graphics.Color.Black
                                    )
                                ) {
                                    Icon(
                                        imageVector = ProdyIcons.Psychology,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Start Wisdom Quest",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            is WisdomQuestState.Loading -> {
                                // Loading state
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = ProdyAccentGreen
                                    )
                                }
                            }

                            is WisdomQuestState.NeedsDailyFocus -> {
                                // Daily Focus selector
                                DailyFocusSelector(
                                    onFocusSelected = { focus ->
                                        viewModel.setDailyFocus(focus)
                                    }
                                )
                            }

                            is WisdomQuestState.Active -> {
                                // Active challenge UI
                                WisdomQuestChallenge(
                                    challenge = questState.challenge,
                                    currentStreak = questState.currentStreak,
                                    dailyFocus = questState.dailyFocus,
                                    onAnswerSubmit = { answer ->
                                        viewModel.submitWisdomQuestAnswer(answer)
                                    },
                                    onSkip = { viewModel.skipWisdomQuest() }
                                )
                            }

                            is WisdomQuestState.Result -> {
                                // Result display
                                WisdomQuestResult(
                                    result = questState.result,
                                    onContinue = { viewModel.continueAfterResult() }
                                )
                            }
                        }
                    }
                } else {
                    // Word already learned - show completion badge
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = ChipShape,
                        color = AchievementUnlocked.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = ProdyIcons.CheckCircle,
                                contentDescription = null,
                                tint = AchievementUnlocked,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Wisdom Acquired",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AchievementUnlocked
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: String,
    isItalic: Boolean = false
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
