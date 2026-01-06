package com.prody.prashant.ui.screens.futuremessage

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.prody.prashant.ui.theme.isDarkTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.FutureMessageEntity
import com.prody.prashant.ui.components.DeliveryCountdownAura
import com.prody.prashant.ui.components.PreventScreenshots
import com.prody.prashant.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin

/**
 * Time Capsule Overview Screen
 *
 * A premium, minimalist redesign featuring:
 * - Custom tab switch with animated pill indicator
 * - Immersive empty state with hourglass illustration
 * - Support for both dark and light themes
 * - Flat design with no shadows or gradients
 */
@Composable
fun FutureMessageListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToWrite: () -> Unit,
    viewModel: FutureMessageViewModel = hiltViewModel()
) {
    // Prevent screenshots of this sensitive screen
    PreventScreenshots()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showFilterDialog by remember { mutableStateOf(false) }
    val isDark = isDarkTheme()

    // Theme-aware colors
    val backgroundColor = if (isDark) TimeCapsuleBackgroundDark else TimeCapsuleBackgroundLight
    val primaryTextColor = if (isDark) TimeCapsuleTextPrimaryDark else TimeCapsuleTextPrimaryLight
    val secondaryTextColor = if (isDark) TimeCapsuleTextSecondaryDark else TimeCapsuleTextSecondaryLight
    val iconColor = if (isDark) TimeCapsuleIconDark else TimeCapsuleIconLight

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Header Bar
            TimeCapsuleHeader(
                onNavigateBack = onNavigateBack,
                onFilterClick = { showFilterDialog = true },
                iconColor = iconColor,
                titleColor = primaryTextColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Tab Switch
            TimeCapsuleTabSwitch(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                isDarkTheme = isDark
            )

            // Content based on selected tab with loading/error states
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> DeliveredMessagesTab(
                        messages = uiState.deliveredMessages,
                        onMessageClick = { viewModel.markAsRead(it.id) },
                        isDarkTheme = isDark
                    )
                    1 -> PendingMessagesTab(
                        messages = uiState.pendingMessages,
                        isDarkTheme = isDark
                    )
                }
            }
        }

        // Bottom CTA Button
        TimeCapsuleCTAButton(
            onClick = onNavigateToWrite,
            isDarkTheme = isDark,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .navigationBarsPadding()
        )

        // Filter Dialog
        if (showFilterDialog) {
            TimeCapsuleFilterDialog(
                onDismiss = { showFilterDialog = false },
                isDarkTheme = isDark
            )
        }
    }
}

/**
 * Custom header bar with back arrow, centered title, and filter icon
 */
@Composable
private fun TimeCapsuleHeader(
    onNavigateBack: () -> Unit,
    onFilterClick: () -> Unit,
    iconColor: Color,
    titleColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        // Centered title
        Text(
            text = stringResource(R.string.time_capsule),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )

        // Filter icon
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "Filter",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Custom tab switch with animated pill indicator
 */
@Composable
private fun TimeCapsuleTabSwitch(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isDarkTheme: Boolean
) {
    val tabContainerColor = if (isDarkTheme) TimeCapsuleTabContainerDark else TimeCapsuleTabContainerLight
    val activeTabTextColor = if (isDarkTheme) TimeCapsuleActiveTabTextDark else TimeCapsuleActiveTabTextLight
    val inactiveTabTextColor = if (isDarkTheme) TimeCapsuleTextSecondaryDark else TimeCapsuleTextSecondaryLight

    // Animated pill position using fraction for responsive sizing
    val animatedOffsetFraction by animateFloatAsState(
        targetValue = if (selectedTab == 0) 0f else 1f,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "tab_pill_offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Tab container background
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(tabContainerColor)
        ) {
            val containerWidth = maxWidth
            val pillPadding = 4.dp
            val pillWidth = (containerWidth - pillPadding * 2) / 2
            val pillOffset = pillPadding + (pillWidth * animatedOffsetFraction)

            // Animated active tab pill
            Box(
                modifier = Modifier
                    .offset(x = pillOffset)
                    .padding(vertical = 4.dp)
                    .width(pillWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(21.dp))
                    .background(TimeCapsuleAccent)
            )

            // Tab labels
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delivered tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(0) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.time_capsule_tab_delivered),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedTab == 0) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedTab == 0) activeTabTextColor else inactiveTabTextColor
                    )
                }

                // Pending tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.time_capsule_tab_pending),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedTab == 1) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedTab == 1) activeTabTextColor else inactiveTabTextColor
                    )
                }
            }
        }
    }
}

/**
 * Primary CTA button at the bottom of the screen
 */
@Composable
private fun TimeCapsuleCTAButton(
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = RoundedCornerShape(27.5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TimeCapsuleAccent,
            contentColor = Color.Black
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.time_capsule_write_cta),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

/**
 * Delivered messages tab content
 */
@Composable
private fun DeliveredMessagesTab(
    messages: List<FutureMessageEntity>,
    onMessageClick: (FutureMessageEntity) -> Unit,
    isDarkTheme: Boolean
) {
    if (messages.isEmpty()) {
        TimeCapsuleEmptyState(
            isDarkTheme = isDarkTheme
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp,
                bottom = 140.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                DeliveredMessageCard(
                    message = message,
                    onClick = { onMessageClick(message) },
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

/**
 * Pending messages tab content
 */
@Composable
private fun PendingMessagesTab(
    messages: List<FutureMessageEntity>,
    isDarkTheme: Boolean
) {
    if (messages.isEmpty()) {
        TimeCapsuleEmptyState(
            isDarkTheme = isDarkTheme,
            isPending = true
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp,
                bottom = 140.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = messages.sortedBy { it.deliveryDate },
                key = { it.id }
            ) { message ->
                PendingMessageCard(
                    message = message,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

/**
 * Empty state illustration with hourglass icon, dashed circle, and progress indicators
 */
@Composable
private fun TimeCapsuleEmptyState(
    isDarkTheme: Boolean,
    isPending: Boolean = false
) {
    val primaryTextColor = if (isDarkTheme) TimeCapsuleTextPrimaryDark else TimeCapsuleTextPrimaryLight
    val secondaryTextColor = if (isDarkTheme) TimeCapsuleTextSecondaryDark else TimeCapsuleTextSecondaryLight
    val innerCircleColor = if (isDarkTheme) TimeCapsuleEmptyCircleBgDark else TimeCapsuleEmptyCircleBgLight
    val dashedCircleColor = if (isDarkTheme) TimeCapsuleDashedCircleDark else TimeCapsuleDashedCircleLight

    // Animation for the progress arcs
    val infiniteTransition = rememberInfiniteTransition(label = "empty_state_animation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsating glow for dots
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Empty state illustration
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer dashed circle with progress indicators
            Canvas(
                modifier = Modifier.size(180.dp)
            ) {
                val radius = size.minDimension / 2
                val center = Offset(size.width / 2, size.height / 2)

                // Draw dashed outer circle
                val dashPathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(8f, 12f),
                    phase = 0f
                )
                drawCircle(
                    color = dashedCircleColor,
                    radius = radius,
                    center = center,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = dashPathEffect
                    )
                )

                // Draw progress arc segments (neon green)
                val arcAngles = listOf(30f, 120f, 210f, 300f)
                arcAngles.forEach { startAngle ->
                    rotate(rotationAngle, pivot = center) {
                        drawArc(
                            color = TimeCapsuleAccent,
                            startAngle = startAngle,
                            sweepAngle = 25f,
                            useCenter = false,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            ),
                            size = size
                        )
                    }
                }

                // Draw small dots at various positions
                val dotAngles = listOf(75f, 165f, 255f, 345f)
                dotAngles.forEach { angle ->
                    val adjustedAngle = angle + rotationAngle
                    val radians = Math.toRadians(adjustedAngle.toDouble())
                    val dotX = center.x + (radius - 2.dp.toPx()) * cos(radians).toFloat()
                    val dotY = center.y + (radius - 2.dp.toPx()) * sin(radians).toFloat()
                    drawCircle(
                        color = TimeCapsuleAccent,
                        radius = 3.dp.toPx() * pulseScale,
                        center = Offset(dotX, dotY)
                    )
                }
            }

            // Inner solid circle with hourglass
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(innerCircleColor),
                contentAlignment = Alignment.Center
            ) {
                // Custom Hourglass Icon
                HourglassIcon(
                    modifier = Modifier.size(48.dp),
                    color = TimeCapsuleAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Empty state text
        Text(
            text = stringResource(R.string.future_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = primaryTextColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(
                if (isPending) R.string.future_empty_pending_message
                else R.string.future_empty_message
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = secondaryTextColor,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        // Add bottom padding to account for the CTA button
        Spacer(modifier = Modifier.height(100.dp))
    }
}

/**
 * Custom Hourglass icon drawn with Canvas
 */
@Composable
private fun HourglassIcon(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val strokeWidth = 3.dp.toPx()

        // Top horizontal line
        drawLine(
            color = color,
            start = Offset(width * 0.2f, height * 0.1f),
            end = Offset(width * 0.8f, height * 0.1f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Bottom horizontal line
        drawLine(
            color = color,
            start = Offset(width * 0.2f, height * 0.9f),
            end = Offset(width * 0.8f, height * 0.9f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Left top diagonal
        drawLine(
            color = color,
            start = Offset(width * 0.25f, height * 0.15f),
            end = Offset(width * 0.5f, height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Right top diagonal
        drawLine(
            color = color,
            start = Offset(width * 0.75f, height * 0.15f),
            end = Offset(width * 0.5f, height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Left bottom diagonal
        drawLine(
            color = color,
            start = Offset(width * 0.25f, height * 0.85f),
            end = Offset(width * 0.5f, height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Right bottom diagonal
        drawLine(
            color = color,
            start = Offset(width * 0.75f, height * 0.85f),
            end = Offset(width * 0.5f, height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Left top vertical
        drawLine(
            color = color,
            start = Offset(width * 0.2f, height * 0.1f),
            end = Offset(width * 0.25f, height * 0.15f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Right top vertical
        drawLine(
            color = color,
            start = Offset(width * 0.8f, height * 0.1f),
            end = Offset(width * 0.75f, height * 0.15f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Left bottom vertical
        drawLine(
            color = color,
            start = Offset(width * 0.2f, height * 0.9f),
            end = Offset(width * 0.25f, height * 0.85f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Right bottom vertical
        drawLine(
            color = color,
            start = Offset(width * 0.8f, height * 0.9f),
            end = Offset(width * 0.75f, height * 0.85f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Filter dialog for time capsule messages
 */
@Composable
private fun TimeCapsuleFilterDialog(
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {
    val dialogBgColor = if (isDarkTheme) TimeCapsuleBackgroundDark else TimeCapsuleBackgroundLight
    val textPrimaryColor = if (isDarkTheme) TimeCapsuleTextPrimaryDark else TimeCapsuleTextPrimaryLight
    val textSecondaryColor = if (isDarkTheme) TimeCapsuleTextSecondaryDark else TimeCapsuleTextSecondaryLight

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBgColor,
        title = {
            Text(
                text = "Filter Messages",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textPrimaryColor
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Filter options for time capsule messages will be available in a future update.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondaryColor
                )
                Text(
                    text = "Planned filters:\n• By date range\n• By read/unread status\n• By delivery date",
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondaryColor.copy(alpha = 0.8f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Got it",
                    color = TimeCapsuleAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

/**
 * Card for delivered messages
 */
@Composable
private fun DeliveredMessageCard(
    message: FutureMessageEntity,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    var expanded by remember { mutableStateOf(false) }

    val cardBgColor = if (isDarkTheme) {
        if (!message.isRead) TimeCapsuleTabContainerDark else TimeCapsuleEmptyCircleBgDark
    } else {
        if (!message.isRead) TimeCapsuleAccent.copy(alpha = 0.1f) else TimeCapsuleTabContainerLight
    }

    val primaryTextColor = if (isDarkTheme) TimeCapsuleTextPrimaryDark else TimeCapsuleTextPrimaryLight
    val secondaryTextColor = if (isDarkTheme) TimeCapsuleTextSecondaryDark else TimeCapsuleTextSecondaryLight

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
                if (!message.isRead) onClick()
            },
        shape = RoundedCornerShape(16.dp),
        color = cardBgColor
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
                    // Icon circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TimeCapsuleAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (message.isRead) Icons.Filled.MarkEmailRead
                            else Icons.Filled.Mail,
                            contentDescription = null,
                            tint = TimeCapsuleAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = message.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (!message.isRead) FontWeight.Bold else FontWeight.Medium,
                            color = primaryTextColor
                        )
                        Text(
                            text = "Written ${dateFormat.format(Date(message.createdAt))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = secondaryTextColor
                        )
                    }
                }

                if (!message.isRead) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TimeCapsuleAccent
                    ) {
                        Text(
                            text = "New",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = secondaryTextColor.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = primaryTextColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Delivered ${dateFormat.format(Date(message.deliveredAt ?: message.deliveryDate))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TimeCapsuleAccent
                    )
                }
            }

            if (!expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryTextColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Card for pending messages with magical countdown aura
 */
@Composable
private fun PendingMessageCard(
    message: FutureMessageEntity,
    isDarkTheme: Boolean
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val daysRemaining = remember(message.deliveryDate) {
        val diff = message.deliveryDate - System.currentTimeMillis()
        TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0)
    }

    val cardBgColor = if (isDarkTheme) TimeCapsuleEmptyCircleBgDark else TimeCapsuleTabContainerLight
    val primaryTextColor = if (isDarkTheme) TimeCapsuleTextPrimaryDark else TimeCapsuleTextPrimaryLight
    val secondaryTextColor = if (isDarkTheme) TimeCapsuleTextSecondaryDark else TimeCapsuleTextSecondaryLight

    // Wrap with countdown aura for messages close to delivery
    Box {
        // Magical countdown aura effect for messages close to delivery
        DeliveryCountdownAura(
            daysUntilDelivery = daysRemaining.toInt()
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = cardBgColor
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
                        // Icon circle
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TimeCapsuleAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.HourglassBottom,
                                contentDescription = null,
                                tint = TimeCapsuleAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = message.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = primaryTextColor
                            )
                            Text(
                                text = "Delivers ${dateFormat.format(Date(message.deliveryDate))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = secondaryTextColor
                            )
                        }
                    }

                    // Days remaining badge with enhanced styling for close deliveries
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (daysRemaining <= 7)
                            TimeCapsuleAccent.copy(alpha = 0.25f)
                        else
                            TimeCapsuleAccent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = when {
                                daysRemaining == 0L -> "Today!"
                                daysRemaining == 1L -> "Tomorrow!"
                                else -> "$daysRemaining days"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = TimeCapsuleAccent,
                            fontWeight = if (daysRemaining <= 3) FontWeight.Bold else FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sealed content indicator
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDarkTheme) TimeCapsuleDashedCircleDark.copy(alpha = 0.5f)
                    else TimeCapsuleDashedCircleLight
                ) {
                    Text(
                        text = if (daysRemaining <= 1)
                            "Almost time to reveal your message..."
                        else
                            "Content sealed until delivery...",
                        style = MaterialTheme.typography.bodySmall,
                        color = secondaryTextColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
