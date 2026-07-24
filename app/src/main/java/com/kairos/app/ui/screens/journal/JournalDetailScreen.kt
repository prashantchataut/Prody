package com.kairos.app.ui.screens.journal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kairos.app.data.local.entity.JournalEntryEntity
import com.kairos.app.domain.model.Mood
import com.kairos.app.ui.animation.KairosReveal
import com.kairos.app.ui.components.kairos.KairosAppBackground
import com.kairos.app.ui.components.kairos.KairosEmptyState
import com.kairos.app.ui.components.kairos.KairosGlassSurface
import com.kairos.app.ui.components.kairos.KairosIconButton
import com.kairos.app.ui.components.kairos.KairosReadingSurface
import com.kairos.app.ui.icons.KairosIcons
import com.kairos.app.ui.security.KairosSecureScreenEffect
import com.kairos.app.ui.theme.KairosSpacing
import com.kairos.app.ui.theme.SerifFamily
import com.kairos.app.ui.theme.color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalDetailScreen(
    entryId: Long,
    onNavigateBack: () -> Unit,
    viewModel: JournalDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(entryId) { viewModel.loadEntry(entryId) }
    KairosSecureScreenEffect()

    KairosAppBackground {
        Column(Modifier.fillMaxSize()) {
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
                    Text("Reflection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Private reading view", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KairosIconButton(
                        if (state.entry?.isBookmarked == true) KairosIcons.Bookmark else KairosIcons.BookmarkBorder,
                        if (state.entry?.isBookmarked == true) "Remove bookmark" else "Bookmark",
                        viewModel::toggleBookmark,
                        selected = state.entry?.isBookmarked == true
                    )
                    KairosIconButton(KairosIcons.Delete, "Delete reflection", viewModel::showDeleteDialog)
                }
            }

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                state.error != null -> KairosEmptyState(
                    icon = KairosIcons.ErrorOutline,
                    title = "This reflection is unavailable",
                    body = state.error ?: "The entry could not be read.",
                    actionLabel = "Try again",
                    onAction = { viewModel.retry(entryId) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                state.entry == null -> KairosEmptyState(
                    icon = KairosIcons.AutoStories,
                    title = "Reflection not found",
                    body = "It may have been removed from this device.",
                    actionLabel = "Go back",
                    onAction = onNavigateBack,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                else -> ReflectionArticle(state.entry!!)
            }
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::hideDeleteDialog,
            title = { Text("Delete this reflection?") },
            text = { Text("This removes the entry from the journal. This action cannot be undone from the app.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEntry()
                        onNavigateBack()
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = viewModel::hideDeleteDialog) { Text("Keep") } }
        )
    }
}

@Composable
private fun ReflectionArticle(entry: JournalEntryEntity) {
    val mood = Mood.fromString(entry.mood)
    val date = rememberDate(entry.createdAt)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KairosSpacing.screen)
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(KairosSpacing.lg)
    ) {
        KairosReveal(visible = true, delayMillis = 30) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .background(mood.color.copy(alpha = 0.16f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text(mood.emoji) }
                    Column {
                        Text(date, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${mood.displayName} · intensity ${entry.moodIntensity}/10 · ${entry.wordCount} words",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    entry.title.ifBlank { "Untitled reflection" },
                    style = MaterialTheme.typography.displaySmall,
                    fontFamily = SerifFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        KairosReadingSurface(
            modifier = Modifier.fillMaxWidth(),
            accent = mood.color,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text(
                entry.content,
                style = MaterialTheme.typography.bodyLarge.copy(fontFamily = SerifFamily),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (entry.attachedPhotos.isNotBlank() || entry.attachedVideos.isNotBlank() || entry.voiceRecordingUri != null) {
            KairosGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                strong = true,
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(KairosIcons.Image, contentDescription = null)
                    Column {
                        Text("Media kept with this entry", fontWeight = FontWeight.SemiBold)
                        Text(
                            attachmentDescription(entry),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        val insight = entry.aiInsight?.takeIf { it.isNotBlank() }
            ?: entry.aiSummary?.takeIf { it.isNotBlank() }
            ?: entry.buddhaResponse?.takeIf { it.isNotBlank() }
        AnimatedVisibility(insight != null, enter = fadeIn()) {
            if (insight != null) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(KairosIcons.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Optional perspective", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Text(
                        "Generated reflections may be incomplete. Keep only what feels accurate.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    KairosReadingSurface(
                        modifier = Modifier.fillMaxWidth(),
                        accent = MaterialTheme.colorScheme.tertiary,
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(insight, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
                            entry.aiQuestion?.takeIf { it.isNotBlank() }?.let {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                Text(it, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun rememberDate(timestamp: Long): String = androidx.compose.runtime.remember(timestamp) {
    SimpleDateFormat("EEEE, MMMM d · h:mm a", Locale.getDefault()).format(Date(timestamp))
}

private fun attachmentDescription(entry: JournalEntryEntity): String {
    val parts = buildList {
        val photos = entry.attachedPhotos.split(',').count { it.isNotBlank() }
        val videos = entry.attachedVideos.split(',').count { it.isNotBlank() }
        if (photos > 0) add("$photos photo${if (photos == 1) "" else "s"}")
        if (videos > 0) add("$videos video${if (videos == 1) "" else "s"}")
        if (entry.voiceRecordingUri != null) add("voice note")
    }
    return parts.joinToString(" · ")
}
