package com.prody.prashant.ui.screens.quotes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatQuote
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
import com.prody.prashant.data.local.entity.*
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.launch

enum class WisdomTab(val title: String) {
    QUOTES("Quotes"),
    PROVERBS("Proverbs"),
    IDIOMS("Idioms"),
    PHRASES("Phrases")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { WisdomTab.entries.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.wisdom_collection)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 16.dp
            ) {
                WisdomTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(tab.title) },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    WisdomTab.QUOTES -> Icons.AutoMirrored.Filled.FormatQuote
                                    WisdomTab.PROVERBS -> Icons.Filled.AutoStories
                                    WisdomTab.IDIOMS -> Icons.Filled.Lightbulb
                                    WisdomTab.PHRASES -> Icons.Filled.Forum
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // Content Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (WisdomTab.entries[page]) {
                    WisdomTab.QUOTES -> QuotesList(
                        quotes = uiState.quotes,
                        onFavoriteToggle = { viewModel.toggleQuoteFavorite(it) }
                    )
                    WisdomTab.PROVERBS -> ProverbsList(
                        proverbs = uiState.proverbs,
                        onFavoriteToggle = { viewModel.toggleProverbFavorite(it) }
                    )
                    WisdomTab.IDIOMS -> IdiomsList(
                        idioms = uiState.idioms,
                        onFavoriteToggle = { viewModel.toggleIdiomFavorite(it) }
                    )
                    WisdomTab.PHRASES -> PhrasesList(
                        phrases = uiState.phrases,
                        onFavoriteToggle = { viewModel.togglePhraseFavorite(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuotesList(
    quotes: List<QuoteEntity>,
    onFavoriteToggle: (QuoteEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(quotes, key = { it.id }) { quote ->
            QuoteCard(
                quote = quote,
                onFavoriteToggle = { onFavoriteToggle(quote) }
            )
        }
    }
}

@Composable
private fun QuoteCard(
    quote: QuoteEntity,
    onFavoriteToggle: () -> Unit
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.FormatQuote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (quote.isFavorite) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (quote.isFavorite) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"${quote.content}\"",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "— ${quote.author}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End)
            )

            if (quote.category.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = ChipShape,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = quote.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProverbsList(
    proverbs: List<ProverbEntity>,
    onFavoriteToggle: (ProverbEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(proverbs, key = { it.id }) { proverb ->
            ProverbCard(
                proverb = proverb,
                onFavoriteToggle = { onFavoriteToggle(proverb) }
            )
        }
    }
}

@Composable
private fun ProverbCard(
    proverb: ProverbEntity,
    onFavoriteToggle: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ProdyCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded },
        backgroundColor = ProverbBackground
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoStories,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (proverb.isFavorite) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (proverb.isFavorite) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = proverb.content,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Meaning",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = proverb.meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (proverb.origin.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Origin: ${proverb.origin}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            if (!expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to see meaning",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun IdiomsList(
    idioms: List<IdiomEntity>,
    onFavoriteToggle: (IdiomEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(idioms, key = { it.id }) { idiom ->
            IdiomCard(
                idiom = idiom,
                onFavoriteToggle = { onFavoriteToggle(idiom) }
            )
        }
    }
}

@Composable
private fun IdiomCard(
    idiom: IdiomEntity,
    onFavoriteToggle: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ProdyCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded },
        backgroundColor = IdiomBackground
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp)
                )
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (idiom.isFavorite) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (idiom.isFavorite) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = idiom.phrase,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Meaning",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = idiom.meaning,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (idiom.exampleSentence.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Example",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${idiom.exampleSentence}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (idiom.origin.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Origin: ${idiom.origin}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            if (!expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to learn more",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun PhrasesList(
    phrases: List<PhraseEntity>,
    onFavoriteToggle: (PhraseEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(phrases, key = { it.id }) { phrase ->
            PhraseCard(
                phrase = phrase,
                onFavoriteToggle = { onFavoriteToggle(phrase) }
            )
        }
    }
}

@Composable
private fun PhraseCard(
    phrase: PhraseEntity,
    onFavoriteToggle: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ProdyCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded },
        backgroundColor = PhraseBackground
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Forum,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (phrase.isFavorite) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (phrase.isFavorite) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = phrase.phrase,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Meaning",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = phrase.meaning,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (phrase.exampleSentence.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Usage",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${phrase.exampleSentence}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = ChipShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = phrase.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = ChipShape,
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = phrase.formality,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (!expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to see usage",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
