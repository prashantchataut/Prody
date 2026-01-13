package com.prody.prashant.ui.screens.deepdive
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.DeepDiveEntity
import com.prody.prashant.domain.deepdive.DeepDiveTheme
import com.prody.prashant.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Deep Dive Home Screen - Dashboard for weekly reflection sessions.
 *
 * Features:
 * - Next scheduled deep dive card
 * - Upcoming sessions list
 * - Completed sessions history
 * - Analytics/insights
 * - Schedule new session
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepDiveHomeScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSession: (Long) -> Unit,
    viewModel: DeepDiveViewModel = hiltViewModel()
) {
    val uiState by viewModel.homeState.collectAsStateWithLifecycle()
    var showThemeSelectionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Deep Dive Days",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = ProdyIcons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showThemeSelectionDialog = true }) {
                        Icon(
                            imageVector = ProdyIcons.Add,
                            contentDescription = "Schedule Deep Dive"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ProdyAccentGreen)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header info
                        item(key = "header_info") {
                            DeepDiveInfoCard()
                        }

                        // Next scheduled session
                        uiState.nextScheduled?.let { next ->
                            item(key = "next_session_${next.id}") {
                                NextDeepDiveCard(
                                    deepDive = next,
                                    onStart = { onNavigateToSession(next.id) }
                                )
                            }
                        }

                        // Analytics card
                        uiState.analytics?.let { analytics ->
                            item(key = "analytics") {
                                DeepDiveAnalyticsCard(
                                    totalCompleted = analytics.totalCompleted,
                                    averageMoodImprovement = analytics.averageMoodImprovement,
                                    mostExploredTheme = analytics.getMostExploredTheme()
                                )
                            }
                        }

                        // Upcoming sessions
                        if (uiState.scheduledSessions.size > 1) {
                            item(key = "upcoming_header") {
                                Text(
                                    text = "Upcoming Sessions",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            items(
                                items = uiState.scheduledSessions.drop(1),
                                key = { session -> "upcoming_${session.id}" }
                            ) { session ->
                                ScheduledDeepDiveCard(
                                    deepDive = session,
                                    onReschedule = { /* Show reschedule dialog */ },
                                    onDelete = { viewModel.deleteDeepDive(session.id) }
                                )
                            }
                        }

                        // Completed sessions
                        if (uiState.completedSessions.isNotEmpty()) {
                            item(key = "completed_header") {
                                Text(
                                    text = "Completed Deep Dives",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            items(
                                items = uiState.completedSessions.take(5),
                                key = { session -> "completed_${session.id}" }
                            ) { session ->
                                CompletedDeepDiveCard(
                                    deepDive = session,
                                    onClick = { onNavigateToSession(session.id) }
                                )
                            }
                        }

                        // Empty state
                        if (!uiState.hasUpcoming && uiState.completedSessions.isEmpty()) {
                            item(key = "empty_state") {
                                EmptyStateCard(
                                    onSchedule = { showThemeSelectionDialog = true }
                                )
                            }
                        }

                        item(key = "bottom_spacer") {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }

    // Theme selection dialog
    if (showThemeSelectionDialog) {
        ThemeSelectionDialog(
            onThemeSelected = { theme ->
                viewModel.scheduleNewDeepDive(theme)
                showThemeSelectionDialog = false
            },
            onDismiss = { showThemeSelectionDialog = false }
        )
    }

    // Schedule success snackbar
    if (uiState.showScheduleSuccess) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            viewModel.dismissScheduleSuccess()
        }
    }

    // Error handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            viewModel.dismissHomeError()
        }
    }
}

@Composable
private fun DeepDiveInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ProdyAccentGreen.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ProdyAccentGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌊", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Weekly Deep Reflection",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Set aside time each week for deeper self-exploration on meaningful themes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NextDeepDiveCard(
    deepDive: DeepDiveEntity,
    onStart: () -> Unit
) {
    val theme = DeepDiveTheme.fromId(deepDive.theme) ?: return
    val dateFormatter = remember { SimpleDateFormat("EEEE, MMM d", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(theme.colorLight)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Next Deep Dive",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(theme.colorDark)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = theme.displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = theme.icon,
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = theme.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateFormatter.format(Date(deepDive.scheduledDate)),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = timeFormatter.format(Date(deepDive.scheduledDate)),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onStart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(theme.colorDark)
                    )
                ) {
                    Text("Begin", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = ProdyIcons.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun DeepDiveAnalyticsCard(
    totalCompleted: Int,
    averageMoodImprovement: Double,
    mostExploredTheme: DeepDiveTheme?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Your Deep Dive Journey",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnalyticItem(
                    value = totalCompleted.toString(),
                    label = "Completed"
                )
                if (averageMoodImprovement > 0) {
                    AnalyticItem(
                        value = "+${String.format("%.1f", averageMoodImprovement)}",
                        label = "Avg Mood ↑"
                    )
                }
                mostExploredTheme?.let { theme ->
                    AnalyticItem(
                        value = theme.icon,
                        label = "Favorite"
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticItem(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ProdyAccentGreen
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ScheduledDeepDiveCard(
    deepDive: DeepDiveEntity,
    onReschedule: () -> Unit,
    onDelete: () -> Unit
) {
    val theme = DeepDiveTheme.fromId(deepDive.theme) ?: return
    val dateFormatter = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(theme.colorLight)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = theme.icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormatter.format(Date(deepDive.scheduledDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = ProdyIcons.Close,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CompletedDeepDiveCard(
    deepDive: DeepDiveEntity,
    onClick: () -> Unit
) {
    val theme = DeepDiveTheme.fromId(deepDive.theme) ?: return
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val moodChange = deepDive.getMoodChange()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(theme.colorLight)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = theme.icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateFormatter.format(Date(deepDive.completedAt ?: deepDive.scheduledDate)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    moodChange?.let { change ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (change >= 0) "Mood +$change" else "Mood $change",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (change >= 0) ProdySuccess else ProdyError
                        )
                    }
                }
            }
            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyStateCard(onSchedule: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🌊", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Deep Dives Scheduled",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Schedule your first deep dive session to begin exploring meaningful themes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onSchedule,
                colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
            ) {
                Icon(ProdyIcons.Add, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Schedule Deep Dive", color = Color.Black)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelectionDialog(
    onThemeSelected: (DeepDiveTheme) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Choose a Theme",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Select a theme for your next deep dive reflection",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = DeepDiveTheme.getAllThemes(),
                    key = { theme -> theme.id }
                ) { theme ->
                    ThemeChip(
                        theme = theme,
                        onSelect = { onThemeSelected(theme) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ThemeChip(
    theme: DeepDiveTheme,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = Color(theme.colorLight)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = theme.icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = theme.displayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
