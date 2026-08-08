package com.kairos.app.ui.screens.vocabulary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kairos.app.data.local.entity.VocabularyEntity
import com.kairos.app.ui.animation.KairosDurations
import com.kairos.app.ui.animation.KairosEasing
import com.kairos.app.ui.animation.KairosReveal
import com.kairos.app.ui.animation.rememberKairosReducedMotion
import com.kairos.app.ui.components.kairos.KairosEmptyState
import com.kairos.app.ui.components.kairos.KairosGlassSurface
import com.kairos.app.ui.components.kairos.KairosIconButton
import com.kairos.app.ui.components.kairos.KairosPrimaryButton
import com.kairos.app.ui.components.kairos.KairosReadingSurface
import com.kairos.app.ui.components.kairos.KairosScreenHeader
import com.kairos.app.ui.components.kairos.KairosSecondaryButton
import com.kairos.app.ui.components.kairos.KairosSegmentedControl
import com.kairos.app.ui.components.kairos.KairosSkeletonList
import com.kairos.app.ui.icons.KairosIcons
import com.kairos.app.ui.theme.KairosClay
import com.kairos.app.ui.theme.KairosPeriwinkle
import com.kairos.app.ui.theme.KairosRadius
import com.kairos.app.ui.theme.KairosSeaGlass
import com.kairos.app.ui.theme.KairosSpacing

@Composable
fun FocusedLearnScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSession: (SessionMode) -> Unit,
    viewModel: VocabularyListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchVisible by rememberSaveable { mutableStateOf(false) }
    val filters = VocabularyFilter.entries
    val selectedFilter = filters.indexOfFirst { it.key == state.currentFilter }.coerceAtLeast(0)

    Column(modifier = Modifier.fillMaxSize()) {
        KairosScreenHeader(
            title = "Learn",
            eyebrow = "Vocabulary",
            subtitle = learningSummary(state),
            actions = {
                KairosIconButton(
                    icon = if (searchVisible) KairosIcons.Close else KairosIcons.Outlined.Search,
                    contentDescription = if (searchVisible) "Close search" else "Search words",
                    selected = searchVisible,
                    onClick = {
                        searchVisible = !searchVisible
                        if (!searchVisible) viewModel.setQuery("")
                    }
                )
                KairosIconButton(
                    icon = if (state.showFavoritesOnly) KairosIcons.Favorite else KairosIcons.FavoriteBorder,
                    contentDescription = if (state.showFavoritesOnly) "Show all words" else "Show favorite words",
                    selected = state.showFavoritesOnly,
                    onClick = viewModel::toggleFavoritesOnly
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 840.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = KairosSpacing.screen),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (!state.isLoading) {
                SessionLauncher(
                    dueReviewCount = state.dueReviewCount,
                    totalCount = state.totalCount,
                    onStartReview = { onNavigateToSession(SessionMode.REVIEW_DUE) },
                    onStartNewWords = { onNavigateToSession(SessionMode.NEW_WORDS) }
                )
            }
            if (!state.isLoading && state.totalCount > 0) {
                KairosReveal(visible = true) {
                    LearningOverview(state = state)
                }
            }
            AnimatedVisibility(
                visible = searchVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                KairosSearchField(
                    query = state.query,
                    onQueryChange = viewModel::setQuery,
                    placeholder = "Search words, meanings, or topics"
                )
            }
            KairosSegmentedControl(
                items = filters.map { it.label },
                selectedIndex = selectedFilter,
                onSelected = { index -> viewModel.setFilter(filters[index].key) }
            )
        }

        when {
            state.isLoading -> {
                KairosReadingSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 840.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(KairosSpacing.screen)
                ) {
                    KairosSkeletonList(rows = 5)
                }
            }
            state.error != null -> {
                KairosEmptyState(
                    icon = KairosIcons.ErrorOutline,
                    title = "Vocabulary is unavailable",
                    body = state.error ?: "The local library could not be read.",
                    actionLabel = "Try again",
                    onAction = viewModel::retry,
                    modifier = Modifier.fillMaxSize()
                )
            }
            state.words.isEmpty() -> {
                KairosEmptyState(
                    icon = KairosIcons.Outlined.MenuBook,
                    title = emptyTitle(state),
                    body = emptyBody(state),
                    actionLabel = "Reset filters",
                    onAction = {
                        viewModel.setQuery("")
                        viewModel.setFilter(VocabularyFilter.ALL.key)
                        if (state.showFavoritesOnly) viewModel.toggleFavoritesOnly()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 840.dp)
                        .align(Alignment.CenterHorizontally),
                    contentPadding = PaddingValues(
                        start = KairosSpacing.screen,
                        end = KairosSpacing.screen,
                        top = 16.dp,
                        bottom = 28.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(state.words, key = { _, word -> word.id }) { index, word ->
                        KairosReveal(
                            visible = true,
                            delayMillis = (index.coerceAtMost(7) * 24)
                        ) {
                            VocabularyRow(
                                word = word,
                                onClick = { onNavigateToDetail(word.id) },
                                onFavorite = { viewModel.toggleFavorite(word.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Liquid-glass launcher for the hybrid practice session. Shows the spaced-repetition
 * review queue when words are due, plus a fresh-words option so the stream never runs dry.
 */
@Composable
private fun SessionLauncher(
    dueReviewCount: Int,
    totalCount: Int,
    onStartReview: () -> Unit,
    onStartNewWords: () -> Unit
) {
    KairosGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Practice session",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Recall cards, then match meanings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = KairosSeaGlass.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = if (dueReviewCount > 0) "$dueReviewCount due" else "All reviewed",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = KairosSeaGlass,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KairosPrimaryButton(
                    text = if (dueReviewCount > 0) "Review $dueReviewCount words" else "Review",
                    onClick = onStartReview,
                    modifier = Modifier.weight(1f),
                    icon = KairosIcons.Outlined.Refresh
                )
                KairosSecondaryButton(
                    text = "New words",
                    onClick = onStartNewWords,
                    modifier = Modifier.weight(1f),
                    icon = if (totalCount > 0) KairosIcons.Outlined.School else null
                )
            }
        }
    }
}

@Composable
private fun LearningOverview(state: VocabularyListUiState) {
    val targetProgress = if (state.totalCount == 0) 0f else state.savedCount.toFloat() / state.totalCount.toFloat()
    val reducedMotion = rememberKairosReducedMotion()
    val progress by animateFloatAsState(
        targetValue = targetProgress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = if (reducedMotion) KairosDurations.Micro else 640,
            easing = KairosEasing.EaseOutExpo
        ),
        label = "vocabulary-progress"
    )
    val percentage = (progress * 100).toInt().coerceIn(0, 100)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "Your vocabulary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Progress without pressure",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                LearningMetric(
                    value = state.savedCount.toString(),
                    label = "Saved",
                    accent = KairosSeaGlass,
                    modifier = Modifier.weight(1f)
                )
                LearningMetric(
                    value = (state.totalCount - state.savedCount).coerceAtLeast(0).toString(),
                    label = "Unsaved",
                    accent = KairosPeriwinkle,
                    modifier = Modifier.weight(1f)
                )
                LearningMetric(
                    value = state.totalCount.toString(),
                    label = "Library",
                    accent = KairosClay,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LearningMetric(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(76.dp),
        shape = RoundedCornerShape(20.dp),
        color = accent.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = accent
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun KairosSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(KairosRadius.controlLarge),
        leadingIcon = {
            Icon(KairosIcons.Outlined.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(KairosIcons.Close, contentDescription = "Clear search")
                }
            }
        },
        placeholder = { Text(placeholder) },
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.72f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun VocabularyRow(
    word: VocabularyEntity,
    onClick: () -> Unit,
    onFavorite: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = buildString {
                    append(word.word)
                    if (word.partOfSpeech.isNotBlank()) append(", ${word.partOfSpeech}")
                    append(". ${word.definition}")
                    if (word.isFavorite) append(". Saved")
                }
            },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(start = 18.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DifficultyMark(difficulty = word.difficulty, saved = word.isFavorite)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (word.partOfSpeech.isNotBlank()) {
                        Text(
                            text = word.partOfSpeech,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = word.definition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (word.category.isNotBlank() && word.category != "general") {
                    Text(
                        text = word.category.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f)
                    )
                }
            }
            IconButton(
                onClick = onFavorite,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (word.isFavorite) KairosIcons.Favorite else KairosIcons.FavoriteBorder,
                    contentDescription = if (word.isFavorite) "Remove ${word.word} from favorites" else "Add ${word.word} to favorites",
                    tint = if (word.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DifficultyMark(difficulty: Int, saved: Boolean) {
    val color = if (saved) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(42.dp)
            .semantics { contentDescription = if (saved) "Saved" else "Difficulty ${difficulty.coerceIn(1, 5)} of 5" },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = RoundedCornerShape(14.dp),
            color = color.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (saved) KairosIcons.Favorite else KairosIcons.Outlined.School,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

private fun learningSummary(state: VocabularyListUiState): String = when {
    state.totalCount == 0 -> "A focused vocabulary practice space"
    state.savedCount == 0 -> "${state.totalCount} words ready to explore"
    else -> "${state.savedCount} saved · ${state.totalCount - state.savedCount} in the wild"
}

private fun emptyTitle(state: VocabularyListUiState): String = when {
    state.query.isNotBlank() -> "No matching words"
    state.showFavoritesOnly -> "No favorite words yet"
    state.currentFilter == VocabularyFilter.SAVED.key -> "No saved words yet"
    else -> "No words available"
}

private fun emptyBody(state: VocabularyListUiState): String = when {
    state.query.isNotBlank() -> "Try a broader spelling, meaning, or topic."
    state.showFavoritesOnly -> "Favorite useful words as you find them, then return here for a clean shortlist."
    state.currentFilter == VocabularyFilter.SAVED.key -> "Tap the heart on any word to keep it here."
    else -> "The local vocabulary catalog is empty."
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Vocabulary row",
    showBackground = true,
    backgroundColor = 0xFFF6F4EF,
    widthDp = 390
)
@Composable
private fun VocabularyRowPreview() {
    com.kairos.app.ui.theme.KairosTheme(themeMode = com.kairos.app.ui.theme.ThemeMode.LIGHT) {
        VocabularyRow(
            word = VocabularyEntity(
                id = 1,
                word = "Lucid",
                definition = "Expressed clearly and easy to understand.",
                partOfSpeech = "adjective",
                category = "communication",
                difficulty = 2,
                isFavorite = true
            ),
            onClick = {},
            onFavorite = {}
        )
    }
}
