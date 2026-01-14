package com.prody.prashant.ui.screens.haven
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
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
import com.prody.prashant.data.local.entity.HavenSessionEntity
import com.prody.prashant.domain.haven.*
import com.prody.prashant.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Haven Home Screen - Entry point for Haven therapeutic support
 *
 * Features:
 * - Session type selection grid
 * - Recent sessions list
 * - Quick access to exercises
 * - Haven statistics
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HavenHomeScreen(
    onNavigateBack: () -> Unit,
    onStartSession: (SessionType) -> Unit,
    onResumeSession: (Long) -> Unit,
    onNavigateToExercise: (ExerciseType) -> Unit,
    viewModel: HavenViewModel = hiltViewModel()
) {
    val uiState by viewModel.homeState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Haven",
                        fontWeight = FontWeight.SemiBold
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ProdyAccentGreen)
            }
        } else if (!uiState.isConfigured) {
            HavenNotConfiguredState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome header
                item(key = "header") {
                    HavenWelcomeHeader(
                        stats = uiState.stats,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Session type selection
                item(key = "session_types") {
                    SessionTypeSection(
                        onSelectType = onStartSession,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Quick exercises
                item(key = "exercises") {
                    QuickExercisesSection(
                        onSelectExercise = onNavigateToExercise,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Recent sessions
                if (uiState.sessions.isNotEmpty()) {
                    item(key = "recent_header") {
                        Text(
                            text = "Recent Sessions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    items(
                        items = uiState.sessions.take(5),
                        key = { session -> "session_${session.id}" }
                    ) { session ->
                        SessionHistoryCard(
                            session = session,
                            onClick = { onResumeSession(session.id) },
                            onDelete = { viewModel.deleteSession(session.id) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // Crisis resources footer
                item(key = "crisis_resources") {
                    CrisisResourcesCard(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    // Error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error then clear
            viewModel.clearHomeError()
        }
    }
}

@Composable
private fun HavenWelcomeHeader(
    stats: HavenStats?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Welcome to Haven",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A safe space for reflection, support, and growth. What would you like to explore today?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (stats != null && stats.totalSessions > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        value = stats.completedSessions.toString(),
                        label = "Sessions"
                    )
                    StatItem(
                        value = stats.completedExercises.toString(),
                        label = "Exercises"
                    )
                    StatItem(
                        value = "${stats.totalTimeMinutes}m",
                        label = "Time Spent"
                    )
                    stats.averageMoodImprovement?.let { improvement ->
                        StatItem(
                            value = if (improvement > 0) "+${"%.1f".format(improvement)}" else "%.1f".format(improvement),
                            label = "Avg Mood"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
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
private fun SessionTypeSection(
    onSelectType: (SessionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Grid of session types (excluding CRISIS_SUPPORT for main grid)
        val sessionTypes = SessionType.entries.filter { it != SessionType.CRISIS_SUPPORT }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            sessionTypes.chunked(2).forEach { rowTypes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowTypes.forEach { type ->
                        SessionTypeCard(
                            sessionType = type,
                            onClick = { onSelectType(type) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space if odd number
                    if (rowTypes.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionTypeCard(
    sessionType: SessionType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeColor = Color(sessionType.color)

    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(typeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sessionType.icon,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sessionType.displayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = sessionType.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickExercisesSection(
    onSelectExercise: (ExerciseType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Quick Exercises",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = ExerciseType.entries.toList(),
                key = { exercise -> "exercise_${exercise.name}" }
            ) { exerciseType ->
                ExerciseChip(
                    exerciseType = exerciseType,
                    onClick = { onSelectExercise(exerciseType) }
                )
            }
        }
    }
}

@Composable
private fun ExerciseChip(
    exerciseType: ExerciseType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = exerciseType.icon,
                fontSize = 20.sp
            )
            Column {
                Text(
                    text = exerciseType.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${exerciseType.estimatedDuration / 60} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SessionHistoryCard(
    session: HavenSessionEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val sessionType = SessionType.fromString(session.sessionType)
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(sessionType.color).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sessionType.icon,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sessionType.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormat.format(Date(session.startedAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status indicator
            if (session.isCompleted) {
                Icon(
                    imageVector = ProdyIcons.CheckCircle,
                    contentDescription = "Completed",
                    tint = ProdySuccess,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.labelMedium,
                    color = ProdyAccentGreen
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = ProdyIcons.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Session?") },
            text = { Text("This will permanently remove this session and its messages.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = ProdyError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CrisisResourcesCard(
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ProdyIcons.HealthAndSafety,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Need immediate support?",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Crisis resources are available 24/7",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (expanded) ProdyIcons.ExpandLess else ProdyIcons.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CrisisResource.CRISIS_RESOURCES.take(3).forEach { resource ->
                        CrisisResourceItem(resource = resource)
                    }
                }
            }
        }
    }
}

@Composable
private fun CrisisResourceItem(
    resource: CrisisResource,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = resource.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = resource.phone,
                style = MaterialTheme.typography.bodySmall,
                color = ProdyAccentGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = resource.availability,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HavenNotConfiguredState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.Settings,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Haven Setup Required",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Haven requires AI configuration to provide therapeutic support. Please configure your API key in Settings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
