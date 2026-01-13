package com.prody.prashant.ui.screens.learning
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.domain.learning.*
import com.prody.prashant.ui.theme.*

/**
 * Path Detail Screen - Shows all lessons in a learning path
 *
 * Features:
 * - Path overview with progress
 * - Lesson list with completion status
 * - Continue from current lesson
 * - Completion celebration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PathDetailScreen(
    pathId: String,
    onNavigateBack: () -> Unit,
    onNavigateToLesson: (String, String) -> Unit,
    viewModel: PathDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val path = uiState.path
    val pathColor = path?.let {
        Color(android.graphics.Color.parseColor(it.type.color))
    } ?: ProdyAccentGreen

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = path?.type?.displayName ?: "Loading...",
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
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = pathColor)
                }
            } else if (path != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Path Header
                    item(key = "header") {
                        PathHeader(
                            path = path,
                            pathColor = pathColor
                        )
                    }

                    // Progress Card
                    item(key = "progress") {
                        PathProgressCard(
                            progress = path.progress,
                            pathColor = pathColor,
                            currentLesson = uiState.currentLesson,
                            onContinue = {
                                uiState.currentLesson?.let { lesson ->
                                    onNavigateToLesson(path.id, lesson.id)
                                }
                            }
                        )
                    }

                    // Lessons Section
                    item(key = "lessons_header") {
                        Text(
                            text = "Lessons",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(
                        items = uiState.lessons,
                        key = { lesson -> "lesson_${lesson.id}" }
                    ) { lesson ->
                        LessonItem(
                            lesson = lesson,
                            pathColor = pathColor,
                            isCurrent = lesson.id == uiState.currentLesson?.id,
                            onClick = {
                                if (!lesson.isLocked) {
                                    onNavigateToLesson(path.id, lesson.id)
                                }
                            }
                        )
                    }

                    item(key = "bottom_spacer") {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    // Completion Celebration Dialog
    if (uiState.showCompletionCelebration && path != null) {
        PathCompletionDialog(
            pathType = path.type,
            pathColor = pathColor,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }
}

@Composable
private fun PathHeader(
    path: LearningPath,
    pathColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = pathColor.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = path.type.icon, fontSize = 64.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = path.type.displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = path.type.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoChip(
                    icon = ProdyIcons.Schedule,
                    text = "${path.type.estimatedMinutes} min",
                    color = pathColor
                )
                InfoChip(
                    icon = ProdyIcons.School,
                    text = "${path.lessons.size} lessons",
                    color = pathColor
                )
                InfoChip(
                    icon = ProdyIcons.TrendingUp,
                    text = path.type.difficultyLevel.replaceFirstChar { it.uppercase() },
                    color = pathColor
                )
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun PathProgressCard(
    progress: PathProgress,
    pathColor: Color,
    currentLesson: Lesson?,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${progress.completedLessons} of ${progress.totalLessons} lessons",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${progress.percentage.toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = pathColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress.percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = pathColor,
                trackColor = pathColor.copy(alpha = 0.2f)
            )

            if (currentLesson != null && progress.percentage < 100f) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Up Next",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currentLesson.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Button(
                        onClick = onContinue,
                        colors = ButtonDefaults.buttonColors(containerColor = pathColor)
                    ) {
                        Text("Continue", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonItem(
    lesson: Lesson,
    pathColor: Color,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        lesson.isCompleted -> ProdySuccess.copy(alpha = 0.1f)
        isCurrent -> pathColor.copy(alpha = 0.15f)
        lesson.isLocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val iconTint = when {
        lesson.isCompleted -> ProdySuccess
        isCurrent -> pathColor
        lesson.isLocked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !lesson.isLocked, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (isCurrent) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(pathColor)
        ) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lesson number/status circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            lesson.isCompleted -> ProdySuccess.copy(alpha = 0.2f)
                            isCurrent -> pathColor.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    lesson.isCompleted -> Icon(
                        imageVector = ProdyIcons.Check,
                        contentDescription = "Completed",
                        tint = ProdySuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    lesson.isLocked -> Icon(
                        imageVector = ProdyIcons.Lock,
                        contentDescription = "Locked",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                    else -> Text(
                        text = (lesson.orderIndex + 1).toString(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) pathColor else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (lesson.isLocked)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LessonTypeIcon(type = lesson.type, tint = iconTint)
                    Text(
                        text = lesson.type.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = iconTint
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = iconTint
                    )
                    Text(
                        text = "${lesson.estimatedMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = iconTint
                    )
                }
            }

            if (!lesson.isLocked && !lesson.isCompleted) {
                Icon(
                    imageVector = ProdyIcons.ChevronRight,
                    contentDescription = null,
                    tint = if (isCurrent) pathColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quiz score badge
            lesson.quizScore?.let { score ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ProdySuccess.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$score%",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProdySuccess,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonTypeIcon(
    type: LessonType,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val icon = when (type) {
        LessonType.READING -> ProdyIcons.MenuBook
        LessonType.REFLECTION -> ProdyIcons.Edit
        LessonType.EXERCISE -> ProdyIcons.FitnessCenter
        LessonType.JOURNAL_PROMPT -> ProdyIcons.Create
        LessonType.MEDITATION -> ProdyIcons.SelfImprovement
        LessonType.QUIZ -> ProdyIcons.Quiz
    }
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier.size(14.dp),
        tint = tint
    )
}

@Composable
private fun PathCompletionDialog(
    pathType: PathType,
    pathColor: Color,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🎉", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Path Complete!",
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Congratulations! You've completed the ${pathType.displayName} path.",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "You've earned a new badge and valuable insights for your journey.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = pathColor)
            ) {
                Text("Celebrate!", color = Color.White)
            }
        }
    )
}
