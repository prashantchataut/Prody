package com.prody.prashant.ui.screens.vocabulary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.ui.components.kairos.KairosEmptyState
import com.prody.prashant.ui.components.kairos.KairosIconButton
import com.prody.prashant.ui.components.kairos.KairosReadingSurface
import com.prody.prashant.ui.components.kairos.KairosScreenHeader
import com.prody.prashant.ui.components.kairos.KairosSegmentedControl
import com.prody.prashant.ui.components.kairos.KairosSkeletonList
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.KairosRadius
import com.prody.prashant.ui.theme.KairosSpacing

@Composable
fun FocusedLearnScreen(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: VocabularyListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchVisible by rememberSaveable { mutableStateOf(false) }
    val filters = VocabularyFilter.entries
    val selectedFilter = filters.indexOfFirst { it.key == state.currentFilter }.coerceAtLeast(0)

    Column(modifier = Modifier.fillMaxSize()) {
        KairosScreenHeader(
            title = "Learn",
            eyebrow = "VOCABULARY",
            subtitle = learningSummary(state),
            actions = {
                KairosIconButton(
                    icon = if (searchVisible) ProdyIcons.Close else ProdyIcons.Outlined.Search,
                    contentDescription = if (searchVisible) "Close search" else "Search words",
                    selected = searchVisible,
                    onClick = {
                        searchVisible = !searchVisible
                        if (!searchVisible) viewModel.setQuery("")
                    }
                )
                KairosIconButton(
                    icon = if (state.showFavoritesOnly) ProdyIcons.Favorite else ProdyIcons.FavoriteBorder,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                    icon = ProdyIcons.ErrorOutline,
                    title = "Vocabulary is unavailable",
                    body = state.error ?: "The local library could not be read.",
                    actionLabel = "Try again",
                    onAction = viewModel::retry,
                    modifier = Modifier.fillMaxSize()
                )
            }
            state.words.isEmpty() -> {
                KairosEmptyState(
                    icon = ProdyIcons.Outlined.MenuBook,
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
                    items(state.words, key = { it.id }) { word ->
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
            Icon(ProdyIcons.Outlined.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(ProdyIcons.Close, contentDescription = "Clear search")
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
                    if (word.isLearned) append(". Learned")
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
            DifficultyMark(difficulty = word.difficulty, learned = word.isLearned)
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
                    imageVector = if (word.isFavorite) ProdyIcons.Favorite else ProdyIcons.FavoriteBorder,
                    contentDescription = if (word.isFavorite) "Remove ${word.word} from favorites" else "Add ${word.word} to favorites",
                    tint = if (word.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DifficultyMark(difficulty: Int, learned: Boolean) {
    val color = if (learned) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(42.dp)
            .semantics { contentDescription = if (learned) "Learned" else "Difficulty ${difficulty.coerceIn(1, 5)} of 5" },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = RoundedCornerShape(14.dp),
            color = color.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (learned) ProdyIcons.CheckCircle else ProdyIcons.Outlined.School,
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
    state.learnedCount == 0 -> "${state.totalCount} words ready to learn"
    else -> "${state.learnedCount} learned · ${state.totalCount - state.learnedCount} still in motion"
}

private fun emptyTitle(state: VocabularyListUiState): String = when {
    state.query.isNotBlank() -> "No matching words"
    state.showFavoritesOnly -> "No favorite words yet"
    state.currentFilter == VocabularyFilter.LEARNED.key -> "No learned words yet"
    else -> "No words available"
}

private fun emptyBody(state: VocabularyListUiState): String = when {
    state.query.isNotBlank() -> "Try a broader spelling, meaning, or topic."
    state.showFavoritesOnly -> "Favorite useful words as you find them, then return here for a clean shortlist."
    state.currentFilter == VocabularyFilter.LEARNED.key -> "Complete today’s word or open a word to begin learning."
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
    com.prody.prashant.ui.theme.KairosTheme(themeMode = com.prody.prashant.ui.theme.ThemeMode.LIGHT) {
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
