package com.prody.prashant.ui.screens.futuremessage
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.FutureMessageEntity
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.components.ProdyCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutureMessageReplyScreen(
    onNavigateBack: () -> Unit,
    onNavigateToJournal: (String) -> Unit,
    viewModel: FutureMessageReplyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle navigation
    LaunchedEffect(uiState.shouldNavigateToJournal) {
        if (uiState.shouldNavigateToJournal) {
            onNavigateToJournal(uiState.prefilledJournalContent)
            viewModel.clearNavigation()
        }
    }

    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            onNavigateBack()
            viewModel.clearNavigation()
        }
    }

    // Handle errors
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
                        Text("Message from Your Past Self")
                        uiState.originalMessage?.let {
                            Text(
                                text = viewModel.getFormattedCreatedDate(),
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
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.originalMessage == null -> {
                    ErrorState(
                        message = "Message not found",
                        onGoBack = onNavigateBack
                    )
                }

                uiState.saveSuccess -> {
                    SuccessState(
                        message = uiState.originalMessage!!,
                        timePassedFormatted = uiState.timePassedFormatted,
                        isAnniversary = uiState.isAnniversary,
                        anniversaryType = uiState.anniversaryType,
                        onSaveToJournal = viewModel::saveReplyAsJournal,
                        onClose = onNavigateBack
                    )
                }

                else -> {
                    ReplyContent(
                        message = uiState.originalMessage!!,
                        timePassedFormatted = uiState.timePassedFormatted,
                        daysSinceWritten = uiState.daysSinceWritten,
                        isAnniversary = uiState.isAnniversary,
                        anniversaryType = uiState.anniversaryType,
                        currentPrompt = uiState.currentPrompt,
                        replyContent = uiState.replyContent,
                        selectedMood = uiState.selectedMood,
                        hasReplied = uiState.hasReplied,
                        existingReply = uiState.existingReply?.replyContent,
                        wantsToCreateChain = uiState.wantsToCreateChain,
                        chainDeliveryDate = uiState.chainDeliveryDate,
                        isSaving = uiState.isSaving,
                        onReplyChanged = viewModel::onReplyContentChanged,
                        onMoodSelected = viewModel::onMoodSelected,
                        onNewPrompt = viewModel::onNewPromptRequested,
                        onToggleChain = viewModel::toggleChainCreation,
                        onSetChainDate = viewModel::setChainDeliveryDate,
                        onSaveReply = viewModel::saveReply,
                        deliverySuggestions = viewModel.getDeliveryDateSuggestions()
                    )
                }
            }
        }
    }
}

@Composable
private fun ReplyContent(
    message: FutureMessageEntity,
    timePassedFormatted: String,
    daysSinceWritten: Long,
    isAnniversary: Boolean,
    anniversaryType: String?,
    currentPrompt: String,
    replyContent: String,
    selectedMood: Mood?,
    hasReplied: Boolean,
    existingReply: String?,
    wantsToCreateChain: Boolean,
    chainDeliveryDate: Long?,
    isSaving: Boolean,
    onReplyChanged: (String) -> Unit,
    onMoodSelected: (Mood?) -> Unit,
    onNewPrompt: () -> Unit,
    onToggleChain: () -> Unit,
    onSetChainDate: (Long) -> Unit,
    onSaveReply: () -> Unit,
    deliverySuggestions: List<Pair<String, Long>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Anniversary banner
        if (isAnniversary && anniversaryType != null) {
            AnniversaryBanner(anniversaryType = anniversaryType)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Original message
        OriginalMessageCard(
            message = message,
            timePassedFormatted = timePassedFormatted
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Existing reply indicator
        if (hasReplied && existingReply != null) {
            ExistingReplyCard(reply = existingReply)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Reply section
        if (!hasReplied) {
            ReplySection(
                currentPrompt = currentPrompt,
                replyContent = replyContent,
                selectedMood = selectedMood,
                onReplyChanged = onReplyChanged,
                onMoodSelected = onMoodSelected,
                onNewPrompt = onNewPrompt
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Time capsule chain option
            ChainOption(
                wantsToCreateChain = wantsToCreateChain,
                chainDeliveryDate = chainDeliveryDate,
                deliverySuggestions = deliverySuggestions,
                onToggleChain = onToggleChain,
                onSetChainDate = onSetChainDate
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = onSaveReply,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = replyContent.isNotBlank() && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(ProdyIcons.AutoMirrored.Filled.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Reply to Past Self")
                }
            }
        }
    }
}

@Composable
private fun AnniversaryBanner(anniversaryType: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ProdyIcons.Celebration,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "$anniversaryType Anniversary!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "A special moment to reconnect with your past self",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun OriginalMessageCard(
    message: FutureMessageEntity,
    timePassedFormatted: String
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
                    imageVector = ProdyIcons.Mail,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Written $timePassedFormatted ago",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ExistingReplyCard(reply: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ProdyIcons.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your reply",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = reply,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun ReplySection(
    currentPrompt: String,
    replyContent: String,
    selectedMood: Mood?,
    onReplyChanged: (String) -> Unit,
    onMoodSelected: (Mood?) -> Unit,
    onNewPrompt: () -> Unit
) {
    Column {
        // Prompt
        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ProdyIcons.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentPrompt,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onNewPrompt,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Refresh,
                        contentDescription = "New prompt",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reply text field
        OutlinedTextField(
            value = replyContent,
            onValueChange = onReplyChanged,
            placeholder = { Text("Write your reply...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            maxLines = 8
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mood selection
        Text(
            text = "How do you feel reading this?",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        val moods = listOf(
            Mood.HAPPY, Mood.GRATEFUL, Mood.SAD, Mood.NOSTALGIC,
            Mood.MOTIVATED, Mood.CALM, Mood.CONFUSED
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(moods) { mood ->
                val isSelected = mood == selectedMood

                FilterChip(
                    selected = isSelected,
                    onClick = { onMoodSelected(if (isSelected) null else mood) },
                    label = {
                        Text(
                            text = "${mood.emoji} ${mood.displayName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ChainOption(
    wantsToCreateChain: Boolean,
    chainDeliveryDate: Long?,
    deliverySuggestions: List<Pair<String, Long>>,
    onToggleChain: () -> Unit,
    onSetChainDate: (Long) -> Unit
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleChain),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = wantsToCreateChain,
                    onCheckedChange = { onToggleChain() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Continue the conversation",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Send this exchange to your future self",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = ProdyIcons.Loop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = wantsToCreateChain) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "When should your future self receive this?",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(deliverySuggestions) { (label, timestamp) ->
                            val isSelected = chainDeliveryDate == timestamp

                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetChainDate(timestamp) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessState(
    message: FutureMessageEntity,
    timePassedFormatted: String,
    isAnniversary: Boolean,
    anniversaryType: String?,
    onSaveToJournal: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(96.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ProdyIcons.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Reply Sent",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You've connected with yourself across time",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (isAnniversary && anniversaryType != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ProdyIcons.Celebration,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You celebrated a $anniversaryType anniversary reflection!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedButton(
            onClick = onSaveToJournal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(ProdyIcons.Book, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save as Journal Entry")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onGoBack: () -> Unit
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
                imageVector = ProdyIcons.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onGoBack) {
                Text("Go Back")
            }
        }
    }
}
