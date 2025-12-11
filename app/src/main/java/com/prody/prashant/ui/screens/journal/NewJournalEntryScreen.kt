package com.prody.prashant.ui.screens.journal

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*

@Composable
fun NewJournalEntryScreen(
    onNavigateBack: () -> Unit,
    onEntrySaved: () -> Unit,
    viewModel: NewJournalEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onEntrySaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_entry)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveEntry() },
                        enabled = uiState.content.isNotBlank() && !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.save_entry),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Mood selector
            MoodSelector(
                selectedMood = uiState.selectedMood,
                onMoodSelected = { viewModel.updateMood(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Journal content
            OutlinedTextField(
                value = uiState.content,
                onValueChange = { viewModel.updateContent(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp)
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.journal_placeholder),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = CardShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Word count
            Text(
                text = "${uiState.wordCount} words",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Buddha's prompt
            BuddhaPromptCard(
                mood = uiState.selectedMood,
                promptHint = uiState.selectedMood.buddhaPromptHint
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun MoodSelector(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.mood_question),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(Mood.all()) { mood ->
                MoodChip(
                    mood = mood,
                    isSelected = mood == selectedMood,
                    onClick = { onMoodSelected(mood) }
                )
            }
        }
    }
}

@Composable
private fun MoodChip(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(CardShape)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.background(mood.color.copy(alpha = 0.15f))
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            )
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, mood.color, CardShape)
                } else {
                    Modifier
                }
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) mood.color.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surface
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = mood.icon,
                contentDescription = mood.displayName,
                tint = if (isSelected) mood.color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = mood.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) mood.color else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BuddhaPromptCard(
    mood: Mood,
    promptHint: String
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        backgroundColor = ProdyPrimary.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SelfImprovement,
                contentDescription = null,
                tint = ProdyPrimary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "Buddha's Guidance",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = ProdyPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "As you write about feeling ${mood.displayName.lowercase()}, consider how you might $promptHint.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}
