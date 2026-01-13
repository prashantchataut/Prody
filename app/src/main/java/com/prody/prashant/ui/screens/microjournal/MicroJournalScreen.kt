package com.prody.prashant.ui.screens.microjournal
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.MicroEntryEntity
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.components.ProdyCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MicroJournalScreen(
    onNavigateBack: () -> Unit,
    onNavigateToJournalWithContent: (String, Long) -> Unit,
    viewModel: MicroJournalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle success/error messages
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Quick Thoughts")
                        Text(
                            text = "${uiState.todayCount} today • ${uiState.totalCount} total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showQuickCapture() },
                icon = { Icon(ProdyIcons.Add, contentDescription = null) },
                text = { Text("Capture") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.microEntries.isEmpty()) {
                EmptyMicroJournal(onCaptureClick = { viewModel.showQuickCapture() })
            } else {
                MicroEntryList(
                    entries = uiState.microEntries,
                    todayEntries = uiState.todayEntries,
                    unexpandedCount = uiState.unexpandedCount,
                    onEntryClick = viewModel::onEntrySelected,
                    onExpandClick = viewModel::showExpandConfirmation
                )
            }
        }
    }

    // Quick capture bottom sheet
    if (uiState.showQuickCapture) {
        QuickCaptureSheet(
            content = uiState.captureContent,
            mood = uiState.captureMood,
            characterCount = uiState.characterCount,
            maxCharacters = uiState.maxCharacters,
            isSaving = uiState.isSaving,
            onContentChange = viewModel::onCaptureContentChanged,
            onMoodSelected = viewModel::onCaptureMoodSelected,
            onSave = viewModel::saveQuickCapture,
            onDismiss = viewModel::hideQuickCapture
        )
    }

    // Entry detail bottom sheet
    if (uiState.showDetailSheet && uiState.selectedEntry != null) {
        MicroEntryDetailSheet(
            entry = uiState.selectedEntry!!,
            onDismiss = viewModel::dismissDetailSheet,
            onExpand = { viewModel.showExpandConfirmation(uiState.selectedEntry!!) },
            onDelete = { viewModel.deleteEntry(uiState.selectedEntry!!) }
        )
    }

    // Expansion confirmation dialog
    if (uiState.showExpandConfirmation && uiState.entryToExpand != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissExpandConfirmation,
            title = { Text("Expand to Journal") },
            text = {
                Text("This thought will be used as a starting point for a full journal entry. Would you like to continue?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.getExpansionContent()?.let { (entry, content) ->
                            onNavigateToJournalWithContent(content, entry.id)
                        }
                    }
                ) {
                    Text("Expand")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissExpandConfirmation) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickCaptureSheet(
    content: String,
    mood: Mood?,
    characterCount: Int,
    maxCharacters: Int,
    isSaving: Boolean,
    onContentChange: (String) -> Unit,
    onMoodSelected: (Mood?) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Capture a thought",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$characterCount/$maxCharacters",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (characterCount > maxCharacters * 0.9)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                placeholder = { Text("What's on your mind right now?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (content.isNotBlank()) onSave() }),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mood selection
            Text(
                text = "How are you feeling?",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            MoodSelector(
                selectedMood = mood,
                onMoodSelected = onMoodSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = content.isNotBlank() && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(ProdyIcons.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MoodSelector(
    selectedMood: Mood?,
    onMoodSelected: (Mood?) -> Unit
) {
    val quickMoods = listOf(
        Mood.HAPPY, Mood.CALM, Mood.GRATEFUL,
        Mood.ANXIOUS, Mood.SAD, Mood.CONFUSED
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(quickMoods) { mood ->
            val isSelected = mood == selectedMood

            FilterChip(
                selected = isSelected,
                onClick = { onMoodSelected(if (isSelected) null else mood) },
                label = {
                    Text(
                        text = "${mood.emoji} ${mood.displayName}",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
private fun MicroEntryList(
    entries: List<MicroEntryEntity>,
    todayEntries: List<MicroEntryEntity>,
    unexpandedCount: Int,
    onEntryClick: (MicroEntryEntity) -> Unit,
    onExpandClick: (MicroEntryEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Today's thoughts section
        if (todayEntries.isNotEmpty()) {
            item {
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(todayEntries, key = { "today_${it.id}" }) { entry ->
                MicroEntryCard(
                    entry = entry,
                    onClick = { onEntryClick(entry) },
                    onExpandClick = { onExpandClick(entry) },
                    showDate = false
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Earlier thoughts
        val earlierEntries = entries.filter { entry ->
            todayEntries.none { it.id == entry.id }
        }

        if (earlierEntries.isNotEmpty()) {
            item {
                Text(
                    text = "Earlier",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(earlierEntries, key = { "earlier_${it.id}" }) { entry ->
                MicroEntryCard(
                    entry = entry,
                    onClick = { onEntryClick(entry) },
                    onExpandClick = { onExpandClick(entry) },
                    showDate = true
                )
            }
        }

        // Expandable hint
        if (unexpandedCount > 0) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$unexpandedCount thought${if (unexpandedCount > 1) "s" else ""} could become journal entries",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MicroEntryCard(
    entry: MicroEntryEntity,
    onClick: () -> Unit,
    onExpandClick: () -> Unit,
    showDate: Boolean
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Mood emoji if present
                entry.mood?.let { moodStr ->
                    Mood.fromString(moodStr)?.let { mood ->
                        Text(
                            text = mood.emoji,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Time
                Text(
                    text = formatEntryTime(entry.createdAt, showDate),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            // Expand button if not already expanded
            if (entry.expandedToEntryId == null) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onExpandClick,
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.OpenInFull,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Expand",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ProdyIcons.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Expanded to journal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MicroEntryDetailSheet(
    entry: MicroEntryEntity,
    onDismiss: () -> Unit,
    onExpand: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header with mood and time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                entry.mood?.let { moodStr ->
                    Mood.fromString(moodStr)?.let { mood ->
                        AssistChip(
                            onClick = {},
                            label = { Text("${mood.emoji} ${mood.displayName}") }
                        )
                    }
                } ?: Spacer(modifier = Modifier.width(1.dp))

                Text(
                    text = formatEntryTime(entry.createdAt, true),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Content
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyLarge
            )

            // Capture context
            entry.captureContext?.let { context ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Captured via: ${formatCaptureContext(context)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expanded indicator
            if (entry.expandedToEntryId != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ProdyIcons.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Already expanded to a journal entry",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (entry.expandedToEntryId == null) {
                    OutlinedButton(
                        onClick = onExpand,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.OpenInFull,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Expand")
                    }
                }

                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Delete confirmation
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Thought?") },
            text = { Text("This thought will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyMicroJournal(onCaptureClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.Bolt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Capture fleeting thoughts",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Quick thoughts don't need to be perfect. Capture them in seconds and expand later if they deserve more attention.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onCaptureClick) {
                Icon(ProdyIcons.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Capture First Thought")
            }
        }
    }
}

private fun formatEntryTime(timestamp: Long, includeDate: Boolean): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    // Less than 1 hour - show minutes
    if (diff < 60 * 60 * 1000) {
        val minutes = (diff / (60 * 1000)).toInt()
        return if (minutes <= 1) "Just now" else "$minutes min ago"
    }

    // Less than 24 hours - show hours
    if (diff < 24 * 60 * 60 * 1000) {
        val hours = (diff / (60 * 60 * 1000)).toInt()
        return "$hours hour${if (hours > 1) "s" else ""} ago"
    }

    // Include full date
    return if (includeDate) {
        SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(timestamp))
    } else {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatCaptureContext(context: String): String {
    return when (context) {
        "quick_capture" -> "Quick capture"
        "morning_ritual" -> "Morning ritual"
        "evening_ritual" -> "Evening ritual"
        "notification" -> "Notification"
        else -> context.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}
