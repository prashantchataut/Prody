package com.prody.prashant.ui.screens.journal

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Journal History Screen
 *
 * A premium, minimalist screen displaying journal entries in chronological sections.
 * Features:
 * - Chronological grouping (This Week, Last Week, Older)
 * - Dark/Light mode support with exact design spec compliance
 * - Mood indicators with custom colors
 * - Intensity display
 * - Load more functionality for older entries
 * - Filter/Sort capabilities
 *
 * Design Philosophy:
 * - Flat, modern, compact, extremely clean
 * - No shadows, gradients, or hi-fi visual elements
 * - Premium, professional feel
 * - Poppins font family throughout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalHistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: JournalHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()

    // Theme colors based on mode
    val backgroundColor = if (isDarkTheme) JournalHistoryBackgroundDark else JournalHistoryBackgroundLight
    val textPrimary = if (isDarkTheme) JournalHistoryTextPrimaryDark else JournalHistoryTextPrimaryLight
    val textSecondary = if (isDarkTheme) JournalHistoryTextSecondaryDark else JournalHistoryTextSecondaryLight
    val dividerColor = if (isDarkTheme) JournalHistoryDividerDark.copy(alpha = 0.3f) else JournalHistoryDividerLight.copy(alpha = 0.3f)
    val buttonBorderColor = if (isDarkTheme) JournalHistoryButtonBorderDark else JournalHistoryButtonBorderLight

    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            JournalHistoryTopBar(
                onNavigateBack = onNavigateBack,
                onFilterClick = { showFilterSheet = true },
                textColor = textPrimary,
                backgroundColor = backgroundColor,
                hasActiveFilters = uiState.hasActiveFilters,
                activeFilterCount = uiState.activeFilterCount
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = JournalHistoryAccent)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(backgroundColor),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // This Week Section
                if (uiState.thisWeekEntries.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "THIS WEEK",
                            textColor = textPrimary,
                            dividerColor = dividerColor
                        )
                    }
                    items(
                        items = uiState.thisWeekEntries,
                        key = { "thisweek_${it.id}" }
                    ) { entry ->
                        JournalHistoryEntryCard(
                            entry = entry,
                            onClick = { onNavigateToDetail(entry.id) },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }

                // Last Week Section
                if (uiState.lastWeekEntries.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = "LAST WEEK",
                            textColor = textPrimary,
                            dividerColor = dividerColor
                        )
                    }
                    items(
                        items = uiState.lastWeekEntries,
                        key = { "lastweek_${it.id}" }
                    ) { entry ->
                        JournalHistoryEntryCard(
                            entry = entry,
                            onClick = { onNavigateToDetail(entry.id) },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }

                // Older Section
                if (uiState.olderEntries.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = "OLDER",
                            textColor = textPrimary,
                            dividerColor = dividerColor
                        )
                    }
                    items(
                        items = uiState.olderEntries.take(uiState.displayedOlderCount),
                        key = { "older_${it.id}" }
                    ) { entry ->
                        JournalHistoryEntryCard(
                            entry = entry,
                            onClick = { onNavigateToDetail(entry.id) },
                            isDarkTheme = isDarkTheme
                        )
                    }

                    // Load More Button
                    if (uiState.displayedOlderCount < uiState.totalOlderCount) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            LoadMoreButton(
                                onClick = { viewModel.loadMoreOlderEntries() },
                                textColor = textPrimary,
                                borderColor = buttonBorderColor
                            )
                        }
                    }
                }

                // Empty State
                if (uiState.thisWeekEntries.isEmpty() &&
                    uiState.lastWeekEntries.isEmpty() &&
                    uiState.olderEntries.isEmpty()
                ) {
                    item {
                        EmptyHistoryState(
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            hasActiveFilters = uiState.hasActiveFilters,
                            onClearFilters = { viewModel.clearAllFilters() }
                        )
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Filter/Sort Bottom Sheet
    if (showFilterSheet) {
        FilterSortBottomSheet(
            currentSortOrder = uiState.sortOrder,
            currentFilterMood = uiState.selectedFilterMood,
            currentBookmarkedOnly = uiState.showBookmarkedOnly,
            currentDateRangeFilter = uiState.dateRangeFilter,
            hasActiveFilters = uiState.hasActiveFilters,
            onSortOrderChange = { viewModel.setSortOrder(it) },
            onFilterMoodChange = { viewModel.setFilterMood(it) },
            onBookmarkedOnlyChange = { viewModel.setBookmarkedOnly(it) },
            onDateRangeFilterChange = { viewModel.setDateRangeFilter(it) },
            onClearAllFilters = { viewModel.clearAllFilters() },
            onDismiss = { showFilterSheet = false },
            isDarkTheme = isDarkTheme
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalHistoryTopBar(
    onNavigateBack: () -> Unit,
    onFilterClick: () -> Unit,
    textColor: Color,
    backgroundColor: Color,
    hasActiveFilters: Boolean,
    activeFilterCount: Int
) {
    TopAppBar(
        title = {
            Text(
                text = "Journal History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        actions = {
            Box {
                IconButton(
                    onClick = onFilterClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (hasActiveFilters) Icons.Filled.FilterAlt else Icons.Filled.Tune,
                        contentDescription = "Filter and sort",
                        tint = if (hasActiveFilters) JournalHistoryAccent else textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                // Filter badge
                if (hasActiveFilters) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                            .size(18.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(JournalHistoryAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$activeFilterCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = textColor,
            navigationIconContentColor = textColor,
            actionIconContentColor = textColor
        )
    )
}

@Composable
private fun SectionHeader(
    title: String,
    textColor: Color,
    dividerColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = textColor,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = dividerColor,
            thickness = 1.dp
        )
    }
}

@Composable
private fun JournalHistoryEntryCard(
    entry: JournalEntryEntity,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val cardBackground = if (isDarkTheme) JournalHistoryCardDark else JournalHistoryCardLight
    val textPrimary = if (isDarkTheme) JournalHistoryTextPrimaryDark else JournalHistoryTextPrimaryLight
    val textSecondary = if (isDarkTheme) JournalHistoryTextSecondaryDark else JournalHistoryTextSecondaryLight
    val intensityBg = if (isDarkTheme) JournalHistoryIntensityBgDark else JournalHistoryIntensityBgLight
    val dateBlockBg = if (isDarkTheme) JournalHistoryDateBlockBgDark else JournalHistoryDateBlockBgLight
    val dateBlockText = if (isDarkTheme) JournalHistoryDateBlockTextDark else JournalHistoryDateBlockTextLight

    val dateFormat = remember { SimpleDateFormat("MMM", Locale.getDefault()) }
    val dayFormat = remember { SimpleDateFormat("d", Locale.getDefault()) }
    val dayOfWeekFormat = remember { SimpleDateFormat("EEE", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val date = remember(entry.createdAt) { Date(entry.createdAt) }

    val mood = remember(entry.mood) { Mood.fromString(entry.mood) }
    val moodColor = getMoodColorForHistory(mood)
    val moodIcon = getMoodIconForHistory(mood)

    // Get entry type label based on tags or default
    val entryType = remember(entry.tags) {
        when {
            entry.tags.contains("reflection", ignoreCase = true) -> "REFLECTION"
            entry.tags.contains("gratitude", ignoreCase = true) -> "GRATITUDE"
            entry.tags.contains("free", ignoreCase = true) -> "FREE WRITE"
            entry.tags.contains("goal", ignoreCase = true) -> "GOALS"
            else -> "REFLECTION"
        }
    }

    val entryTypeIcon = remember(entryType) {
        when (entryType) {
            "GRATITUDE" -> Icons.Filled.Favorite
            "FREE WRITE" -> Icons.Filled.EditNote
            "GOALS" -> Icons.Filled.Flag
            else -> Icons.Filled.Psychology
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = cardBackground,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left side: Date and entry info
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    // Date display - formatted like "Nov 23 SAT"
                    Column {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                                    append(dateFormat.format(date))
                                }
                                append(" ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                                    append(dayFormat.format(date))
                                }
                                append(" ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = textSecondary)) {
                                    append(dayOfWeekFormat.format(date).uppercase())
                                }
                            },
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Entry type with icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = entryTypeIcon,
                                contentDescription = null,
                                tint = textSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = entryType,
                                style = MaterialTheme.typography.labelSmall,
                                color = textSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Right side: Mood and Intensity
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mood chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = moodColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = moodIcon,
                                contentDescription = null,
                                tint = moodColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = mood.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = moodColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Intensity tag
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = intensityBg
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Intensity ",
                                style = MaterialTheme.typography.labelSmall,
                                color = textPrimary
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(color = JournalHistoryAccent, fontWeight = FontWeight.Bold)) {
                                        append("${entry.moodIntensity}")
                                    }
                                    withStyle(SpanStyle(color = textPrimary)) {
                                        append("/10")
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Entry snippet
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun LoadMoreButton(
    onClick: () -> Unit,
    textColor: Color,
    borderColor: Color
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onClick),
            color = Color.Transparent
        ) {
            Text(
                text = "Load More Entries",
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
            )
        }
    }
}

@Composable
private fun EmptyHistoryState(
    textPrimary: Color,
    textSecondary: Color,
    hasActiveFilters: Boolean = false,
    onClearFilters: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (hasActiveFilters) Icons.Outlined.SearchOff else Icons.Outlined.AutoStories,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = textSecondary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasActiveFilters) "No Matching Entries" else "No Journal Entries Yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hasActiveFilters) "Try adjusting your filters to see more entries"
                   else "Start writing to build your journal history",
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondary
        )
        if (hasActiveFilters) {
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onClearFilters),
                color = JournalHistoryAccent.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                        tint = JournalHistoryAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Clear Filters",
                        style = MaterialTheme.typography.labelMedium,
                        color = JournalHistoryAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSortBottomSheet(
    currentSortOrder: SortOrder,
    currentFilterMood: String?,
    currentBookmarkedOnly: Boolean,
    currentDateRangeFilter: DateRangeFilter,
    hasActiveFilters: Boolean,
    onSortOrderChange: (SortOrder) -> Unit,
    onFilterMoodChange: (String?) -> Unit,
    onBookmarkedOnlyChange: (Boolean) -> Unit,
    onDateRangeFilterChange: (DateRangeFilter) -> Unit,
    onClearAllFilters: () -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {
    val sheetBackground = if (isDarkTheme) JournalHistoryCardDark else JournalHistoryCardLight
    val textPrimary = if (isDarkTheme) JournalHistoryTextPrimaryDark else JournalHistoryTextPrimaryLight
    val textSecondary = if (isDarkTheme) JournalHistoryTextSecondaryDark else JournalHistoryTextSecondaryLight
    val dividerColor = if (isDarkTheme) JournalHistoryDividerDark.copy(alpha = 0.3f) else JournalHistoryDividerLight.copy(alpha = 0.3f)

    // Track expanded sections
    var sortExpanded by remember { mutableStateOf(false) }
    var moodExpanded by remember { mutableStateOf(false) }
    var dateRangeExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = sheetBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(textSecondary.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header with Clear All button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter & Sort",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                if (hasActiveFilters) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onClearAllFilters() },
                        color = JournalHistoryAccent.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = null,
                                tint = JournalHistoryAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Clear All",
                                style = MaterialTheme.typography.labelSmall,
                                color = JournalHistoryAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bookmarked Only Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onBookmarkedOnlyChange(!currentBookmarkedOnly) }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (currentBookmarkedOnly) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = if (currentBookmarkedOnly) JournalHistoryAccent else textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Bookmarked Only",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textPrimary,
                        fontWeight = if (currentBookmarkedOnly) FontWeight.Medium else FontWeight.Normal
                    )
                }
                Switch(
                    checked = currentBookmarkedOnly,
                    onCheckedChange = { onBookmarkedOnlyChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = JournalHistoryAccent,
                        checkedTrackColor = JournalHistoryAccent.copy(alpha = 0.3f)
                    )
                )
            }

            HorizontalDivider(color = dividerColor, modifier = Modifier.padding(vertical = 8.dp))

            // Sort Section (Collapsible)
            FilterSectionHeader(
                title = "SORT BY",
                currentValue = when (currentSortOrder) {
                    SortOrder.NEWEST_FIRST -> "Newest First"
                    SortOrder.OLDEST_FIRST -> "Oldest First"
                    SortOrder.HIGHEST_INTENSITY -> "Highest Intensity"
                    SortOrder.LOWEST_INTENSITY -> "Lowest Intensity"
                },
                isExpanded = sortExpanded,
                onToggle = { sortExpanded = !sortExpanded },
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            AnimatedVisibility(visible = sortExpanded) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    SortOrder.entries.forEach { sortOrder ->
                        FilterSortOption(
                            label = when (sortOrder) {
                                SortOrder.NEWEST_FIRST -> "Newest First"
                                SortOrder.OLDEST_FIRST -> "Oldest First"
                                SortOrder.HIGHEST_INTENSITY -> "Highest Intensity"
                                SortOrder.LOWEST_INTENSITY -> "Lowest Intensity"
                            },
                            isSelected = currentSortOrder == sortOrder,
                            onClick = {
                                onSortOrderChange(sortOrder)
                                sortExpanded = false
                            },
                            textPrimary = textPrimary,
                            accentColor = JournalHistoryAccent
                        )
                    }
                }
            }

            HorizontalDivider(color = dividerColor, modifier = Modifier.padding(vertical = 8.dp))

            // Date Range Section (Collapsible)
            FilterSectionHeader(
                title = "DATE RANGE",
                currentValue = when (currentDateRangeFilter) {
                    DateRangeFilter.ALL_TIME -> "All Time"
                    DateRangeFilter.THIS_WEEK -> "This Week"
                    DateRangeFilter.THIS_MONTH -> "This Month"
                    DateRangeFilter.LAST_3_MONTHS -> "Last 3 Months"
                    DateRangeFilter.THIS_YEAR -> "This Year"
                },
                isExpanded = dateRangeExpanded,
                onToggle = { dateRangeExpanded = !dateRangeExpanded },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                isActive = currentDateRangeFilter != DateRangeFilter.ALL_TIME
            )

            AnimatedVisibility(visible = dateRangeExpanded) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    DateRangeFilter.entries.forEach { dateRange ->
                        FilterSortOption(
                            label = when (dateRange) {
                                DateRangeFilter.ALL_TIME -> "All Time"
                                DateRangeFilter.THIS_WEEK -> "This Week"
                                DateRangeFilter.THIS_MONTH -> "This Month"
                                DateRangeFilter.LAST_3_MONTHS -> "Last 3 Months"
                                DateRangeFilter.THIS_YEAR -> "This Year"
                            },
                            isSelected = currentDateRangeFilter == dateRange,
                            onClick = {
                                onDateRangeFilterChange(dateRange)
                                dateRangeExpanded = false
                            },
                            textPrimary = textPrimary,
                            accentColor = JournalHistoryAccent
                        )
                    }
                }
            }

            HorizontalDivider(color = dividerColor, modifier = Modifier.padding(vertical = 8.dp))

            // Mood Filter Section (Collapsible)
            FilterSectionHeader(
                title = "MOOD",
                currentValue = currentFilterMood?.let {
                    try {
                        Mood.valueOf(it).displayName
                    } catch (e: Exception) {
                        "All Moods"
                    }
                } ?: "All Moods",
                isExpanded = moodExpanded,
                onToggle = { moodExpanded = !moodExpanded },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                isActive = currentFilterMood != null
            )

            AnimatedVisibility(visible = moodExpanded) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    // All moods option
                    FilterSortOption(
                        label = "All Moods",
                        isSelected = currentFilterMood == null,
                        onClick = {
                            onFilterMoodChange(null)
                            moodExpanded = false
                        },
                        textPrimary = textPrimary,
                        accentColor = JournalHistoryAccent
                    )

                    // Individual mood options
                    Mood.entries.forEach { mood ->
                        FilterSortOption(
                            label = mood.displayName,
                            isSelected = currentFilterMood?.equals(mood.name, ignoreCase = true) == true,
                            onClick = {
                                onFilterMoodChange(mood.name)
                                moodExpanded = false
                            },
                            textPrimary = textPrimary,
                            accentColor = getMoodColorForHistory(mood),
                            icon = getMoodIconForHistory(mood)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apply button
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JournalHistoryAccent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterSectionHeader(
    title: String,
    currentValue: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    isActive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = textSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentValue,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isActive) JournalHistoryAccent else textPrimary,
                fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
            )
        }
        Icon(
            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = textSecondary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun FilterSortOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    textPrimary: Color,
    accentColor: Color,
    icon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) accentColor else textPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) accentColor else textPrimary,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Maps Mood to the Journal History specific colors as per design spec
 */
private fun getMoodColorForHistory(mood: Mood): Color {
    return when (mood) {
        Mood.HAPPY -> JournalHistoryMoodEcstatic    // Vibrant neon green
        Mood.EXCITED -> JournalHistoryMoodEcstatic  // Also vibrant green for high energy
        Mood.CALM -> JournalHistoryMoodCalm         // Muted light green
        Mood.GRATEFUL -> JournalHistoryMoodCalm     // Also calm green
        Mood.ANXIOUS -> JournalHistoryMoodAnxious   // Orange
        Mood.CONFUSED -> JournalHistoryMoodAnxious  // Also orange
        Mood.SAD -> JournalHistoryMoodMelancholy    // Light blue
        Mood.MOTIVATED -> JournalHistoryAccent      // Neon green for positive energy
    }
}

/**
 * Maps Mood to appropriate icons for Journal History
 */
private fun getMoodIconForHistory(mood: Mood): ImageVector {
    return when (mood) {
        Mood.HAPPY -> Icons.Filled.SentimentVerySatisfied
        Mood.CALM -> Icons.Filled.SelfImprovement
        Mood.ANXIOUS -> Icons.Outlined.Psychology
        Mood.SAD -> Icons.Filled.SentimentDissatisfied
        Mood.MOTIVATED -> Icons.Filled.LocalFireDepartment
        Mood.GRATEFUL -> Icons.Filled.Favorite
        Mood.CONFUSED -> Icons.Outlined.HelpOutline
        Mood.EXCITED -> Icons.Filled.Celebration
    }
}
