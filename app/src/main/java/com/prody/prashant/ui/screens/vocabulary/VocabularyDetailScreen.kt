package com.prody.prashant.ui.screens.vocabulary

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.ui.components.ProdyCard
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                                imageVector = if (isSpeaking) Icons.Filled.Stop
                                else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isSpeaking) "Stop speaking" else "Speak word",
                                tint = if (isSpeaking) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.word?.isFavorite == true) Icons.Filled.Favorite
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.word?.isFavorite == true) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
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
                                            imageVector = Icons.Filled.CheckCircle,
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

                // Mark as learned button
                if (!word.isLearned) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.markAsLearned() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AchievementUnlocked
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.mark_learned))
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
