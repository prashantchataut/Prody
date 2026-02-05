package com.prody.prashant.ui.screens.letter
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * History screen showing all past monthly letters.
 *
 * Displays letters in a beautiful list with filtering options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyLetterHistoryScreen(
    onNavigateBack: () -> Unit,
    onLetterClick: (Long) -> Unit,
    viewModel: MonthlyLetterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showFavoritesOnly by remember { mutableStateOf(false) }

    val displayedLetters = if (showFavoritesOnly) {
        uiState.recentLetters.filter { it.isFavorite }
    } else {
        uiState.recentLetters
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Letter History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(ProdyIcons.ArrowBack, "Back")
                    }
                },
                actions = {
                    FilterChip(
                        selected = showFavoritesOnly,
                        onClick = { showFavoritesOnly = !showFavoritesOnly },
                        label = { Text("Favorites") },
                        leadingIcon = if (showFavoritesOnly) {
                            { Icon(ProdyIcons.FilterList, null, Modifier.size(18.dp)) }
                        } else null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                displayedLetters.isEmpty() -> {
                    EmptyHistoryState(
                        showingFavorites = showFavoritesOnly,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Group by year
                        val lettersByYear = displayedLetters.groupBy { it.monthYear.year }

                        lettersByYear.entries.sortedByDescending { it.key }.forEach { (year, letters) ->
                            item {
                                Text(
                                    year.toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            items(
                                items = letters.sortedByDescending { it.monthYear.monthValue },
                                key = { it.id }
                            ) { letter ->
                                MonthlyLetterHistoryItem(
                                    letter = letter,
                                    onClick = { onLetterClick(letter.id) }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryState(
    showingFavorites: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(32.dp)
    ) {
        Text(
            if (showingFavorites) "No favorite letters yet" else "No letters yet",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            if (showingFavorites) {
                "Mark letters as favorites by tapping the heart icon when reading them"
            } else {
                "Your monthly letters will appear here once generated"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
