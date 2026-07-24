package com.kairos.app.ui.screens.futuremessage

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kairos.app.data.local.entity.FutureMessageEntity
import com.kairos.app.domain.model.Mood
import com.kairos.app.ui.animation.KairosDurations
import com.kairos.app.ui.animation.KairosReveal
import com.kairos.app.ui.components.kairos.KairosAppBackground
import com.kairos.app.ui.components.kairos.KairosEmptyState
import com.kairos.app.ui.components.kairos.KairosGlassSurface
import com.kairos.app.ui.components.kairos.KairosIconButton
import com.kairos.app.ui.components.kairos.KairosPrimaryButton
import com.kairos.app.ui.components.kairos.KairosReadingSurface
import com.kairos.app.ui.components.kairos.KairosSecondaryButton
import com.kairos.app.ui.icons.KairosIcons
import com.kairos.app.ui.security.KairosSecureScreenEffect
import com.kairos.app.ui.theme.KairosRadius
import com.kairos.app.ui.theme.KairosSpacing
import com.kairos.app.ui.theme.SerifFamily

/**
 * A two-column conversation in time, expressed as one calm vertical reading
 * flow on compact screens. The original letter stays visible while replying so
 * the user does not have to rely on memory or bounce between destinations.
 */
@Composable
fun FutureMessageReplyScreen(
    onNavigateBack: () -> Unit,
    onNavigateToJournal: (String) -> Unit,
    viewModel: FutureMessageReplyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    KairosSecureScreenEffect()
    BackHandler(onBack = onNavigateBack)

    LaunchedEffect(state.shouldNavigateToJournal) {
        if (state.shouldNavigateToJournal) {
            onNavigateToJournal(state.prefilledJournalContent)
            viewModel.clearNavigation()
        }
    }
    LaunchedEffect(state.shouldNavigateBack) {
        if (state.shouldNavigateBack) {
            onNavigateBack()
            viewModel.clearNavigation()
        }
    }

    KairosAppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = KairosSpacing.screen, vertical = KairosSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                KairosIconButton(KairosIcons.ArrowBack, "Back", onNavigateBack)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Across time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        state.timePassedFormatted.takeIf { it.isNotBlank() }?.let { "$it between these moments" } ?: "A private reply",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(Modifier.size(48.dp))
            }

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.originalMessage == null -> KairosEmptyState(
                    icon = KairosIcons.ErrorOutline,
                    title = "The original letter is missing",
                    body = state.errorMessage ?: "This conversation cannot be opened.",
                    actionLabel = "Go back",
                    onAction = onNavigateBack
                )
                else -> AnimatedContent(
                    targetState = state.saveSuccess,
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        (fadeIn(tween(KairosDurations.State)) + slideInVertically(tween(KairosDurations.State)) { it / 14 }) togetherWith
                            fadeOut(tween(KairosDurations.Micro))
                    },
                    label = "future_reply_state"
                ) { success ->
                    if (success) {
                        ReplySaved(
                            message = state.originalMessage!!,
                            reply = state.replyContent,
                            onSaveToJournal = viewModel::saveReplyAsJournal,
                            onClose = onNavigateBack
                        )
                    } else {
                        ReplyComposer(
                            state = state,
                            formattedCreatedDate = viewModel.getFormattedCreatedDate(),
                            deliverySuggestions = viewModel.getDeliveryDateSuggestions(),
                            onReplyChanged = viewModel::onReplyContentChanged,
                            onMoodSelected = viewModel::onMoodSelected,
                            onNextPrompt = viewModel::onNewPromptRequested,
                            onToggleChain = viewModel::toggleChainCreation,
                            onChainDate = viewModel::setChainDeliveryDate,
                            onSave = viewModel::saveReply,
                            onSaveExistingToJournal = viewModel::saveReplyAsJournal
                        )
                    }
                }
            }
        }
    }

    state.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text("Reply not saved") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = viewModel::dismissError) { Text("Return") } }
        )
    }
}

@Composable
private fun ReplyComposer(
    state: FutureMessageReplyUiState,
    formattedCreatedDate: String,
    deliverySuggestions: List<Pair<String, Long>>,
    onReplyChanged: (String) -> Unit,
    onMoodSelected: (Mood?) -> Unit,
    onNextPrompt: () -> Unit,
    onToggleChain: () -> Unit,
    onChainDate: (Long) -> Unit,
    onSave: () -> Unit,
    onSaveExistingToJournal: () -> Unit
) {
    val message = state.originalMessage ?: return
    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = KairosSpacing.screen)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(KairosSpacing.lg)
        ) {
            if (state.isAnniversary && state.anniversaryType != null) {
                AnniversaryMoment(state.anniversaryType)
            }

            KairosReveal(visible = true, delayMillis = 20) {
                OriginalLetter(
                    message = message,
                    writtenDate = formattedCreatedDate,
                    timePassed = state.timePassedFormatted
                )
            }

            if (state.hasReplied && state.existingReply != null) {
                KairosReveal(visible = true, delayMillis = 80) {
                    ExistingConversation(
                        reply = state.existingReply.replyContent,
                        mood = state.existingReply.reactionMood?.let { Mood.fromString(it) },
                        onSaveToJournal = onSaveExistingToJournal
                    )
                }
            } else {
                KairosReveal(visible = true, delayMillis = 80) {
                    PromptCard(prompt = state.currentPrompt, onNext = onNextPrompt)
                }

                KairosReveal(visible = true, delayMillis = 120) {
                    MoodAfterReading(selected = state.selectedMood, onSelected = onMoodSelected)
                }

                KairosReveal(visible = true, delayMillis = 160) {
                    ReplyEditor(value = state.replyContent, onValueChange = onReplyChanged)
                }

                KairosReveal(visible = true, delayMillis = 200) {
                    ChainForwardOption(
                        enabled = state.wantsToCreateChain,
                        selectedDate = state.chainDeliveryDate,
                        suggestions = deliverySuggestions,
                        onToggle = onToggleChain,
                        onDateSelected = onChainDate
                    )
                }
            }
        }

        if (!state.hasReplied) {
            KairosGlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = KairosSpacing.screen, vertical = KairosSpacing.md),
                strong = true,
                shape = RoundedCornerShape(KairosRadius.floating),
                contentPadding = PaddingValues(8.dp)
            ) {
                KairosPrimaryButton(
                    text = if (state.isSaving) "Saving conversation…" else "Save my reply",
                    onClick = onSave,
                    enabled = state.replyContent.isNotBlank() && !state.isSaving,
                    icon = if (state.isSaving) null else KairosIcons.Send,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun OriginalLetter(
    message: FutureMessageEntity,
    writtenDate: String,
    timePassed: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Then", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        KairosReadingSurface(
            modifier = Modifier.fillMaxWidth(),
            accent = Color(0xFF5577B8),
            contentPadding = PaddingValues(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    message.title.ifBlank { "A letter from my past" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = SerifFamily,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = SerifFamily,
                    lineHeight = MaterialTheme.typography.headlineSmall.lineHeight
                )
                Text(
                    "Written $writtenDate${timePassed.takeIf { it.isNotBlank() }?.let { " · $it ago" } ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun PromptCard(prompt: String, onNext: () -> Unit) {
    KairosGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        strong = true,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 8.dp),
        contentPadding = PaddingValues(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("A place to begin", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                KairosIconButton(KairosIcons.Refresh, "Show another reflection prompt", onNext)
            }
            Text(prompt, style = MaterialTheme.typography.titleLarge, fontFamily = SerifFamily)
        }
    }
}

@Composable
private fun MoodAfterReading(selected: Mood?, onSelected: (Mood?) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("How did the letter land?", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Mood.entries.forEach { mood ->
                val active = mood == selected
                Surface(
                    onClick = { onSelected(if (active) null else mood) },
                    shape = RoundedCornerShape(18.dp),
                    color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.semantics {
                        this.selected = active
                        role = Role.Checkbox
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(mood.emoji)
                        Text(mood.displayName, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReplyEditor(value: String, onValueChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Now", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        KairosReadingSurface(
            modifier = Modifier.fillMaxWidth(),
            accent = Color(0xFF3E9B85),
            contentPadding = PaddingValues(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BasicTextField(
                    value = value,
                    onValueChange = { if (it.length <= 10_000) onValueChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .semantics { contentDescription = "Reply to past self" },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = SerifFamily,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    ),
                    decorationBox = { inner ->
                        Box {
                            if (value.isBlank()) {
                                Text(
                                    "Answer honestly. You do not need to turn the past into a lesson.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontFamily = SerifFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.58f)
                                )
                            }
                            inner()
                        }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f))
                Text(
                    "${replyWordCount(value)} words",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun ChainForwardOption(
    enabled: Boolean,
    selectedDate: Long?,
    suggestions: List<Pair<String, Long>>,
    onToggle: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    KairosGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        strong = enabled,
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Continue the conversation", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Seal a short continuation for another future date.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = enabled, onCheckedChange = { onToggle() })
            }

            AnimatedVisibility(visible = enabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestions.forEach { (label, date) ->
                        val selected = selectedDate == date
                        Surface(
                            onClick = { onDateSelected(date) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            border = BorderStroke(1.dp, if (selected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(label, modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExistingConversation(
    reply: String,
    mood: Mood?,
    onSaveToJournal: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Your reply", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        KairosReadingSurface(
            modifier = Modifier.fillMaxWidth(),
            accent = Color(0xFF3E9B85),
            contentPadding = PaddingValues(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                mood?.let {
                    Text("${it.emoji} ${it.displayName}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(reply, style = MaterialTheme.typography.bodyLarge, fontFamily = SerifFamily)
                KairosSecondaryButton(
                    text = "Save conversation to journal",
                    onClick = onSaveToJournal,
                    icon = KairosIcons.Bookmark,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AnniversaryMoment(label: String) {
    KairosGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        strong = true,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 28.dp),
        contentPadding = PaddingValues(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            Box(Modifier.size(46.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(KairosIcons.Celebration, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Column {
                Text("$label since you wrote this", fontWeight = FontWeight.SemiBold)
                Text("Notice what changed without forcing a success story.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ReplySaved(
    message: FutureMessageEntity,
    reply: String,
    onSaveToJournal: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KairosSpacing.screen, vertical = KairosSpacing.section),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(88.dp),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomStart = 30.dp, bottomEnd = 8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(KairosIcons.Check, contentDescription = null, modifier = Modifier.size(34.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("The conversation is saved", style = MaterialTheme.typography.headlineMedium, fontFamily = SerifFamily, textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        Text(
            "You answered “${message.title.ifBlank { "a letter from your past" }}” with ${replyWordCount(reply)} thoughtful words.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        KairosPrimaryButton("Save a copy to journal", onSaveToJournal, Modifier.fillMaxWidth(), icon = KairosIcons.Bookmark)
        Spacer(Modifier.height(10.dp))
        KairosSecondaryButton("Done", onClose, Modifier.fillMaxWidth())
    }
}

private fun replyWordCount(text: String): Int =
    text.trim().split(Regex("\\s+")).count { it.isNotBlank() }
