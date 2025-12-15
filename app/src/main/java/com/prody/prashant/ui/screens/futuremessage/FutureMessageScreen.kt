package com.prody.prashant.ui.screens.futuremessage

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.FutureMessageEntity
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun FutureMessageListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToWrite: () -> Unit,
    viewModel: FutureMessageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Future Messages",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToWrite,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null
                    )
                },
                text = { Text(stringResource(R.string.write_to_future)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.past_messages))
                            if (uiState.unreadCount > 0) {
                                Badge { Text(uiState.unreadCount.toString()) }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.pending_messages))
                            Badge { Text(uiState.pendingMessages.size.toString()) }
                        }
                    }
                )
            }

            when (selectedTab) {
                0 -> DeliveredMessagesTab(
                    messages = uiState.deliveredMessages,
                    onMessageClick = { viewModel.markAsRead(it.id) }
                )
                1 -> PendingMessagesTab(
                    messages = uiState.pendingMessages
                )
            }
        }
    }
}

@Composable
private fun DeliveredMessagesTab(
    messages: List<FutureMessageEntity>,
    onMessageClick: (FutureMessageEntity) -> Unit
) {
    if (messages.isEmpty()) {
        EmptyState(
            icon = Icons.Filled.MarkEmailRead,
            title = "No Messages Yet",
            description = "Messages you send to your future self will appear here when delivered"
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                DeliveredMessageCard(
                    message = message,
                    onClick = { onMessageClick(message) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun PendingMessagesTab(
    messages: List<FutureMessageEntity>
) {
    if (messages.isEmpty()) {
        EmptyState(
            icon = Icons.Filled.Schedule,
            title = "No Pending Messages",
            description = "Write a message to your future self and watch it countdown"
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Timeline visualization header
            item {
                MessageTimelineStrip(
                    messages = messages
                )
            }

            // Message countdown header
            if (messages.isNotEmpty()) {
                item {
                    NextMessageCountdown(
                        message = messages.minByOrNull { it.deliveryDate } ?: messages.first()
                    )
                }
            }

            // Message cards
            items(
                items = messages.sortedBy { it.deliveryDate },
                key = { it.id }
            ) { message ->
                PendingMessageCard(message = message)
            }
        }
    }
}

/**
 * Timeline Strip - Visual representation of message delivery schedule
 */
@Composable
private fun MessageTimelineStrip(
    messages: List<FutureMessageEntity>
) {
    val sortedMessages = messages.sortedBy { it.deliveryDate }
    val now = System.currentTimeMillis()

    // Group by month for timeline nodes
    val monthFormat = remember { SimpleDateFormat("MMM", Locale.getDefault()) }
    val dayFormat = remember { SimpleDateFormat("d", Locale.getDefault()) }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Timeline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Delivery Timeline",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal scrollable timeline
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Today marker
                TimelineNode(
                    label = "Today",
                    sublabel = dayFormat.format(Date(now)),
                    isHighlighted = true,
                    isCurrent = true,
                    color = MaterialTheme.colorScheme.primary
                )

                sortedMessages.forEachIndexed { index, message ->
                    // Connector line
                    TimelineConnector()

                    val daysRemaining = TimeUnit.MILLISECONDS.toDays(message.deliveryDate - now)
                    val isWithinWeek = daysRemaining <= 7
                    val isWithinMonth = daysRemaining <= 30

                    TimelineNode(
                        label = monthFormat.format(Date(message.deliveryDate)),
                        sublabel = dayFormat.format(Date(message.deliveryDate)),
                        isHighlighted = isWithinWeek,
                        isCurrent = false,
                        color = when {
                            isWithinWeek -> MoodExcited
                            isWithinMonth -> MoodCalm
                            else -> MaterialTheme.colorScheme.outline
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineNode(
    label: String,
    sublabel: String,
    isHighlighted: Boolean,
    isCurrent: Boolean,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "node_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCurrent) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        // Date circle
        Box(
            modifier = Modifier
                .size(if (isCurrent) 44.dp else 36.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    if (isHighlighted || isCurrent)
                        color.copy(alpha = 0.2f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = sublabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                    color = color
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isHighlighted || isCurrent) color
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
        )

        if (isCurrent) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun TimelineConnector() {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(2.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                )
            )
    )
}

/**
 * Next Message Countdown - Visual countdown for the nearest pending message
 */
@Composable
private fun NextMessageCountdown(
    message: FutureMessageEntity
) {
    val now = System.currentTimeMillis()
    val totalMillisRemaining = message.deliveryDate - now
    val daysRemaining = TimeUnit.MILLISECONDS.toDays(totalMillisRemaining).coerceAtLeast(0)
    val hoursRemaining = TimeUnit.MILLISECONDS.toHours(totalMillisRemaining % TimeUnit.DAYS.toMillis(1)).coerceAtLeast(0)
    val minutesRemaining = TimeUnit.MILLISECONDS.toMinutes(totalMillisRemaining % TimeUnit.HOURS.toMillis(1)).coerceAtLeast(0)

    // Animated entry
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "countdown_alpha"
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .alpha(alpha),
        backgroundColor = MoodExcited.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = null,
                    tint = MoodExcited,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Next Message Arriving",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MoodExcited
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Countdown display
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                CountdownUnit(value = daysRemaining.toInt(), label = "Days")
                CountdownDivider()
                CountdownUnit(value = hoursRemaining.toInt(), label = "Hours")
                CountdownDivider()
                CountdownUnit(value = minutesRemaining.toInt(), label = "Minutes")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "\"${message.title}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CountdownUnit(
    value: Int,
    label: String
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "countdown_value"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = animatedValue.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MoodExcited
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CountdownDivider() {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colon_blink"
    )

    Text(
        text = ":",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MoodExcited.copy(alpha = alpha),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
private fun DeliveredMessageCard(
    message: FutureMessageEntity,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    var expanded by remember { mutableStateOf(false) }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
                if (!message.isRead) onClick()
            },
        backgroundColor = if (!message.isRead)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MoodExcited.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (message.isRead) Icons.Filled.MarkEmailRead
                            else Icons.Filled.Mail,
                            contentDescription = null,
                            tint = MoodExcited,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = message.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (!message.isRead) FontWeight.Bold else FontWeight.Medium
                        )
                        Text(
                            text = "Written ${dateFormat.format(Date(message.createdAt))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!message.isRead) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text("New")
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Delivered ${dateFormat.format(Date(message.deliveredAt ?: message.deliveryDate))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (!expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PendingMessageCard(
    message: FutureMessageEntity
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val daysRemaining = remember(message.deliveryDate) {
        val diff = message.deliveryDate - System.currentTimeMillis()
        TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0)
    }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MoodCalm.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HourglassBottom,
                            contentDescription = null,
                            tint = MoodCalm,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = message.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Delivers ${dateFormat.format(Date(message.deliveryDate))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = ChipShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = when {
                            daysRemaining == 0L -> "Today!"
                            daysRemaining == 1L -> "Tomorrow"
                            else -> "$daysRemaining days"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Blurred preview (intentionally obscured)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = CardShape
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "Content sealed until delivery...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
