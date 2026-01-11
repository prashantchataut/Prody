package com.prody.prashant.ui.screens.collaborative

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.domain.collaborative.*
import com.prody.prashant.ui.theme.*
import java.time.format.DateTimeFormatter

/**
 * Message Detail Screen - View sent or received message details.
 *
 * Features:
 * - Full message card display
 * - Delivery status for sent messages
 * - Favorite toggle for received messages
 * - Delete/cancel actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(
    messageId: String,
    isReceived: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: CollaborativeViewModel = hiltViewModel()
) {
    val uiState by viewModel.detailState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    // Load message
    LaunchedEffect(messageId, isReceived) {
        if (isReceived) {
            viewModel.loadReceivedMessageDetail(messageId)
        } else {
            viewModel.loadSentMessageDetail(messageId)
        }
    }

    val sentMessage = uiState.sentMessage
    val receivedMessage = uiState.receivedMessage
    val theme = sentMessage?.cardDesign?.theme ?: receivedMessage?.cardDesign?.theme ?: CardTheme.DEFAULT
    val themeColor = Color(theme.colorDark)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isReceived) "Message" else "Sent Message",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetDetailState()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (isReceived && receivedMessage != null) {
                        IconButton(onClick = {
                            viewModel.toggleReceivedFavorite(messageId, !receivedMessage.isFavorite)
                        }) {
                            Icon(
                                imageVector = if (receivedMessage.isFavorite) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                },
                                contentDescription = "Favorite",
                                tint = if (receivedMessage.isFavorite) ProdyError else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
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
                        CircularProgressIndicator(color = themeColor)
                    }
                }
                sentMessage != null -> {
                    SentMessageDetail(
                        message = sentMessage,
                        theme = theme,
                        onCancelScheduled = {
                            if (sentMessage.status == MessageStatus.SCHEDULED) {
                                showCancelDialog = true
                            }
                        }
                    )
                }
                receivedMessage != null -> {
                    ReceivedMessageDetail(
                        message = receivedMessage,
                        theme = theme
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Message not found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Message") },
            text = {
                Text(
                    if (isReceived) {
                        "Are you sure you want to delete this message? This action cannot be undone."
                    } else {
                        "Are you sure you want to delete this sent message? The recipient will still have their copy."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMessage(messageId, isReceived)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ProdyError)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Cancel scheduled message
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Scheduled Message") },
            text = {
                Text("Are you sure you want to cancel this scheduled message? It will not be sent to the recipient.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelScheduledMessage(messageId)
                        showCancelDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ProdyError)
                ) {
                    Text("Cancel Message")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep")
                }
            }
        )
    }

    // Error handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            viewModel.dismissDetailError()
        }
    }
}

@Composable
private fun SentMessageDetail(
    message: CollaborativeMessage,
    theme: CardTheme,
    onCancelScheduled: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a") }
    val themeColor = Color(theme.colorDark)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Status card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (message.status) {
                    MessageStatus.SCHEDULED -> ProdyAccentBlue.copy(alpha = 0.1f)
                    MessageStatus.DELIVERED, MessageStatus.READ -> ProdySuccess.copy(alpha = 0.1f)
                    MessageStatus.FAILED -> ProdyError.copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = when (message.status) {
                            MessageStatus.SCHEDULED -> Icons.Default.Schedule
                            MessageStatus.DELIVERED, MessageStatus.READ -> Icons.Default.CheckCircle
                            MessageStatus.FAILED -> Icons.Default.Error
                            else -> Icons.Default.HourglassEmpty
                        },
                        contentDescription = null,
                        tint = when (message.status) {
                            MessageStatus.SCHEDULED -> ProdyAccentBlue
                            MessageStatus.DELIVERED, MessageStatus.READ -> ProdySuccess
                            MessageStatus.FAILED -> ProdyError
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Column {
                        Text(
                            text = message.status.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = message.deliveryDate.format(dateFormatter),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (message.status == MessageStatus.SCHEDULED) {
                    TextButton(onClick = onCancelScheduled) {
                        Text("Cancel", color = ProdyError)
                    }
                }
            }
        }

        // Recipient info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(themeColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.recipient.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "To: ${message.recipient.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when (message.recipient.method) {
                            ContactMethod.EMAIL -> Icons.Default.Email
                            ContactMethod.SMS -> Icons.Default.Sms
                            ContactMethod.WHATSAPP -> Icons.Default.Chat
                            ContactMethod.IN_APP -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = message.recipient.contactValue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Message card
        MessageCard(
            title = message.title,
            content = message.content,
            occasion = message.occasion,
            theme = theme,
            attachments = message.attachments
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ReceivedMessageDetail(
    message: ReceivedCollaborativeMessage,
    theme: CardTheme
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a") }
    val themeColor = Color(theme.colorDark)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Sender info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(themeColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.senderName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "From: ${message.senderName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Received ${message.receivedAt.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Message card
        MessageCard(
            title = message.title,
            content = message.content,
            occasion = message.occasion,
            theme = theme,
            attachments = message.attachments
        )

        // Sender's note
        message.senderNote?.let { note ->
            if (note.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.StickyNote2,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Personal Note",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MessageCard(
    title: String,
    content: String,
    occasion: Occasion?,
    theme: CardTheme,
    attachments: MessageAttachments?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(theme.colorLight)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Occasion header
            occasion?.let { occ ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = occ.icon, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = occ.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(theme.colorDark),
                        fontWeight = FontWeight.Medium
                    )
                }
                Divider(
                    color = Color(theme.colorDark).copy(alpha = 0.2f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(theme.colorDark),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Content
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Attachments
            attachments?.let { attach ->
                if (attach.images.isNotEmpty() || attach.audioUrl != null) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(
                        color = Color(theme.colorDark).copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    if (attach.images.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${attach.images.size} image(s) attached",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (attach.audioUrl != null) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Voice message attached",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Theme decoration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = theme.icon, fontSize = 32.sp)
            }
        }
    }
}
