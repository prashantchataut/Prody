package com.prody.prashant.ui.screens.search
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.repository.SearchCategory
import com.prody.prashant.data.repository.SearchResult
import com.prody.prashant.ui.theme.PoppinsFamily
import com.prody.prashant.ui.theme.isDarkTheme
import java.text.SimpleDateFormat
import java.util.*

// Color definitions
private val LightBackground = Color(0xFFF9FAFB)
private val DarkBackground = Color(0xFF0D2826)
private val LightCardBackground = Color(0xFFFFFFFF)
private val DarkCardBackground = Color(0xFF1A3331)
private val LightPrimaryText = Color(0xFF212529)
private val DarkPrimaryText = Color(0xFFFFFFFF)
private val LightSecondaryText = Color(0xFF6C757D)
private val DarkSecondaryText = Color(0xFFD3D8D7)
private val AccentGreen = Color(0xFF36F97F)
private val JournalColor = Color(0xFF4A90D9)
private val QuoteColor = Color(0xFFE67E22)
private val VocabularyColor = Color(0xFF9B59B6)
private val FutureMessageColor = Color(0xFF2ECC71)

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToJournalEntry: (Long) -> Unit,
    onNavigateToQuote: (Long) -> Unit,
    onNavigateToVocabulary: (Long) -> Unit,
    onNavigateToFutureMessage: (Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isDarkTheme()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val backgroundColor = if (isDark) DarkBackground else LightBackground
    val cardBackground = if (isDark) DarkCardBackground else LightCardBackground
    val primaryText = if (isDark) DarkPrimaryText else LightPrimaryText
    val secondaryText = if (isDark) DarkSecondaryText else LightSecondaryText

    // Request focus when screen appears
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Search Header
        SearchHeader(
            query = uiState.query,
            onQueryChange = viewModel::onQueryChange,
            onBack = {
                focusManager.clearFocus()
                onNavigateBack()
            },
            onClear = viewModel::clearSearch,
            onSearch = { focusManager.clearFocus() },
            isDark = isDark,
            focusRequester = focusRequester
        )

        // Category Filter Chips
        CategoryFilterRow(
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = viewModel::onCategorySelected,
            isDark = isDark
        )

        // Search Results or Recent Content
        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isSearching -> {
                    // Loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AccentGreen,
                            strokeWidth = 2.dp
                        )
                    }
                }
                uiState.hasSearched && uiState.results.isEmpty() -> {
                    // No results
                    EmptySearchResults(
                        query = uiState.query,
                        primaryText = primaryText,
                        secondaryText = secondaryText
                    )
                }
                uiState.hasSearched -> {
                    // Show results
                    SearchResultsList(
                        results = uiState.results,
                        onResultClick = { result ->
                            when (result) {
                                is SearchResult.JournalResult -> onNavigateToJournalEntry(result.id)
                                is SearchResult.QuoteResult -> onNavigateToQuote(result.id)
                                is SearchResult.VocabularyResult -> onNavigateToVocabulary(result.id)
                                is SearchResult.FutureMessageResult -> onNavigateToFutureMessage(result.id)
                            }
                        },
                        isDark = isDark
                    )
                }
                else -> {
                    // Show recent content
                    RecentContentSection(
                        recentContent = uiState.recentContent,
                        onResultClick = { result ->
                            when (result) {
                                is SearchResult.JournalResult -> onNavigateToJournalEntry(result.id)
                                is SearchResult.QuoteResult -> onNavigateToQuote(result.id)
                                is SearchResult.VocabularyResult -> onNavigateToVocabulary(result.id)
                                is SearchResult.FutureMessageResult -> onNavigateToFutureMessage(result.id)
                            }
                        },
                        isDark = isDark,
                        primaryText = primaryText,
                        secondaryText = secondaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    isDark: Boolean,
    focusRequester: FocusRequester
) {
    val backgroundColor = if (isDark) DarkCardBackground else LightCardBackground
    val textColor = if (isDark) DarkPrimaryText else LightPrimaryText
    val hintColor = if (isDark) DarkSecondaryText else LightSecondaryText

    Surface(
        color = backgroundColor,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = ProdyIcons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textColor
                )
            }

            // Search input
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (isDark) Color(0xFF2A4240) else Color(0xFFE0E7E6))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = ProdyIcons.Search,
                        contentDescription = null,
                        tint = hintColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Search journal, quotes, vocabulary...",
                                style = TextStyle(
                                    fontFamily = PoppinsFamily,
                                    fontSize = 15.sp,
                                    color = hintColor
                                )
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            textStyle = TextStyle(
                                fontFamily = PoppinsFamily,
                                fontSize = 15.sp,
                                color = textColor
                            ),
                            cursorBrush = SolidColor(AccentGreen),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { onSearch() }
                            )
                        )
                    }

                    // Clear button
                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        IconButton(
                            onClick = onClear,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = ProdyIcons.Close,
                                contentDescription = "Clear",
                                tint = hintColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selectedCategory: SearchCategory,
    onCategorySelected: (SearchCategory) -> Unit,
    isDark: Boolean
) {
    val backgroundColor = if (isDark) DarkBackground else LightBackground

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(SearchCategory.entries) { category ->
            CategoryChip(
                category = category,
                isSelected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                isDark = isDark
            )
        }
    }
}

@Composable
private fun CategoryChip(
    category: SearchCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val selectedBg = AccentGreen
    val unselectedBg = if (isDark) Color(0xFF2A4240) else Color(0xFFE0E7E6)
    val selectedText = Color(0xFF0D2826)
    val unselectedText = if (isDark) DarkPrimaryText else LightPrimaryText

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) selectedBg else unselectedBg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = null,
                tint = if (isSelected) selectedText else unselectedText,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isSelected) selectedText else unselectedText
            )
        }
    }
}

@Composable
private fun SearchResultsList(
    results: List<SearchResult>,
    onResultClick: (SearchResult) -> Unit,
    isDark: Boolean
) {
    val primaryText = if (isDark) DarkPrimaryText else LightPrimaryText
    val secondaryText = if (isDark) DarkSecondaryText else LightSecondaryText

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(results, key = { "${it.category.name}-${it.id}" }) { result ->
            SearchResultItem(
                result = result,
                onClick = { onResultClick(result) },
                isDark = isDark,
                primaryText = primaryText,
                secondaryText = secondaryText
            )
        }
    }
}

@Composable
private fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit,
    isDark: Boolean,
    primaryText: Color,
    secondaryText: Color
) {
    val cardBackground = if (isDark) DarkCardBackground else LightCardBackground
    val categoryColor = getCategoryColor(result.category)
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = cardBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(result.category),
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium
                    ),
                    color = primaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle
                Text(
                    text = result.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = PoppinsFamily
                    ),
                    color = secondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Metadata row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category badge
                    Text(
                        text = result.category.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium
                        ),
                        color = categoryColor
                    )

                    if (result.timestamp > 0) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = secondaryText
                        )
                        Text(
                            text = dateFormatter.format(Date(result.timestamp)),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = PoppinsFamily
                            ),
                            color = secondaryText
                        )
                    }

                    // Extra info based on type
                    when (result) {
                        is SearchResult.JournalResult -> {
                            result.mood?.let { mood ->
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = secondaryText
                                )
                                Text(
                                    text = mood,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = PoppinsFamily
                                    ),
                                    color = secondaryText
                                )
                            }
                        }
                        is SearchResult.VocabularyResult -> {
                            if (result.isLearned) {
                                Icon(
                                    imageVector = ProdyIcons.CheckCircle,
                                    contentDescription = "Learned",
                                    tint = AccentGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        is SearchResult.FutureMessageResult -> {
                            if (result.isDelivered) {
                                Icon(
                                    imageVector = ProdyIcons.MarkEmailRead,
                                    contentDescription = "Delivered",
                                    tint = AccentGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = ProdyIcons.Schedule,
                                    contentDescription = "Pending",
                                    tint = secondaryText,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }

            // Arrow indicator
            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = secondaryText,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun EmptySearchResults(
    query: String,
    primaryText: Color,
    secondaryText: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ProdyIcons.SearchOff,
            contentDescription = null,
            tint = secondaryText,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No results found",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold
            ),
            color = primaryText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No matches for \"$query\"",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = PoppinsFamily
            ),
            color = secondaryText
        )
    }
}

@Composable
private fun RecentContentSection(
    recentContent: List<SearchResult>,
    onResultClick: (SearchResult) -> Unit,
    isDark: Boolean,
    primaryText: Color,
    secondaryText: Color
) {
    if (recentContent.isEmpty()) {
        // Empty state
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ProdyIcons.Search,
                contentDescription = null,
                tint = secondaryText.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Search your content",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold
                ),
                color = primaryText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Find journal entries, quotes, vocabulary, and future messages",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = PoppinsFamily
                ),
                color = secondaryText,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = secondaryText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(recentContent, key = { "${it.category.name}-${it.id}" }) { result ->
                SearchResultItem(
                    result = result,
                    onClick = { onResultClick(result) },
                    isDark = isDark,
                    primaryText = primaryText,
                    secondaryText = secondaryText
                )
            }
        }
    }
}

private fun getCategoryIcon(category: SearchCategory): ImageVector {
    return when (category) {
        SearchCategory.ALL -> ProdyIcons.Search
        SearchCategory.JOURNAL -> ProdyIcons.MenuBook
        SearchCategory.QUOTES -> ProdyIcons.FormatQuote
        SearchCategory.VOCABULARY -> ProdyIcons.Translate
        SearchCategory.FUTURE_MESSAGES -> ProdyIcons.Schedule
    }
}

private fun getCategoryColor(category: SearchCategory): Color {
    return when (category) {
        SearchCategory.ALL -> AccentGreen
        SearchCategory.JOURNAL -> JournalColor
        SearchCategory.QUOTES -> QuoteColor
        SearchCategory.VOCABULARY -> VocabularyColor
        SearchCategory.FUTURE_MESSAGES -> FutureMessageColor
    }
}
