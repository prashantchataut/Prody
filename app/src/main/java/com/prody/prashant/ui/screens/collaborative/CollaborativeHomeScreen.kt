package com.prody.prashant.ui.screens.collaborative
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
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.prody.prashant.domain.collaborative.*
import com.prody.prashant.ui.theme.*
import java.time.format.DateTimeFormatter

/**
 * Collaborative Messages Home Screen - Dashboard for sending and receiving messages.
 *
 * Features:
 * - Tab navigation (Inbox, Sent, Contacts)
 * - Upcoming occasions reminders
 * - Message lists with status indicators
 * - Quick compose action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollaborativeHomeScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCompose: (MessageContact?, Occasion?) -> Unit,
    onNavigateToSentDetail: (String) -> Unit,
    onNavigateToReceivedDetail: (String) -> Unit,
    viewModel: CollaborativeViewModel = hiltViewModel()
) {
    val uiState by viewModel.homeState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Messages",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCompose(null, null) },
                containerColor = ProdyAccentGreen,
                contentColor = Color.Black
            ) {
                Icon(ProdyIcons.Edit, contentDescription = "Compose")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats row
            uiState.stats?.let { stats ->
                StatsRow(
                    sentCount = stats.totalSent,
                    receivedCount = stats.totalReceived,
                    scheduledCount = uiState.scheduledCount
                )
            }

            // Tab row
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = ProdyAccentGreen
            ) {
                CollaborativeTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(tab.name.lowercase().replaceFirstChar { it.uppercase() })
                                if (tab == CollaborativeTab.INBOX && uiState.hasUnread) {
                                    Badge(
                                        containerColor = ProdyError
                                    ) {
                                        Text(uiState.unreadCount.toString())
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Tab content
            when (uiState.selectedTab) {
                CollaborativeTab.INBOX -> InboxTab(
                    messages = uiState.receivedMessages,
                    isLoading = uiState.isLoading,
                    onMessageClick = onNavigateToReceivedDetail
                )
                CollaborativeTab.SENT -> SentTab(
                    messages = uiState.sentMessages,
                    isLoading = uiState.isLoading,
                    onMessageClick = onNavigateToSentDetail
                )
                CollaborativeTab.CONTACTS -> ContactsTab(
                    contacts = uiState.contacts,
                    upcomingOccasions = uiState.upcomingOccasions,
                    isLoading = uiState.isLoading,
                    onContactClick = { contact ->
                        onNavigateToCompose(contact, null)
                    },
                    onOccasionClick = { occasion ->
                        onNavigateToCompose(null, occasion.toOccasion())
                    }
                )
            }
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
private fun StatsRow(
    sentCount: Int,
    receivedCount: Int,
    scheduledCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatChip(
            icon = ProdyIcons.AutoMirrored.Filled.Send,
            value = sentCount.toString(),
            label = "Sent"
        )
        StatChip(
            icon = ProdyIcons.Inbox,
            value = receivedCount.toString(),
            label = "Received"
        )
        StatChip(
            icon = ProdyIcons.Schedule,
            value = scheduledCount.toString(),
            label = "Scheduled"
        )
    }
}

@Composable
private fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = ProdyAccentGreen
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InboxTab(
    messages: List<ReceivedCollaborativeMessage>,
    isLoading: Boolean,
    onMessageClick: (String) -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ProdyAccentGreen)
            }
        }
        messages.isEmpty() -> {
            EmptyTabState(
                icon = "📬",
                title = "No Messages Yet",
                description = "Messages you receive will appear here"
            )
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = messages,
                    key = { message -> "inbox_${message.id}" }
                ) { message ->
                    ReceivedMessageCard(
                        message = message,
                        onClick = { onMessageClick(message.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReceivedMessageCard(
    message: ReceivedCollaborativeMessage,
    onClick: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d, h:mm a") }
    val themeColor = Color(message.cardDesign.theme.colorDark)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (!message.isRead) {
                themeColor.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sender avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(themeColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.senderName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (!message.isRead) FontWeight.Bold else FontWeight.Medium
                    )
                    Text(
                        text = message.receivedAt.format(dateFormatter),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (!message.isRead) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Indicators
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!message.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ProdyAccentGreen)
                    )
                }
                if (message.isFavorite) {
                    Icon(
                        imageVector = ProdyIcons.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = ProdyError
                    )
                }
            }
        }
    }
}

@Composable
private fun SentTab(
    messages: List<CollaborativeMessage>,
    isLoading: Boolean,
    onMessageClick: (String) -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ProdyAccentGreen)
            }
        }
        messages.isEmpty() -> {
            EmptyTabState(
                icon = "📤",
                title = "No Sent Messages",
                description = "Messages you send will appear here"
            )
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = messages,
                    key = { message -> "sent_${message.id}" }
                ) { message ->
                    SentMessageCard(
                        message = message,
                        onClick = { onMessageClick(message.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SentMessageCard(
    message: CollaborativeMessage,
    onClick: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d, h:mm a") }
    val themeColor = Color(message.cardDesign.theme.colorDark)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
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
            // Recipient avatar
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

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "To: ${message.recipient.name}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    MessageStatusChip(status = message.status)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (message.status == MessageStatus.SCHEDULED) {
                            ProdyIcons.Schedule
                        } else {
                            ProdyIcons.AutoMirrored.Filled.Send
                        },
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = message.deliveryDate.format(dateFormatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageStatusChip(status: MessageStatus) {
    val (color, text) = when (status) {
        MessageStatus.DRAFT -> ProdyTextSecondary to "Draft"
        MessageStatus.PENDING -> ProdyWarning to "Pending"
        MessageStatus.SCHEDULED -> ProdyAccentBlue to "Scheduled"
        MessageStatus.SENT -> ProdySuccess to "Sent"
        MessageStatus.DELIVERED -> ProdySuccess to "Delivered"
        MessageStatus.READ -> ProdyAccentGreen to "Read"
        MessageStatus.FAILED -> ProdyError to "Failed"
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ContactsTab(
    contacts: List<MessageContact>,
    upcomingOccasions: List<MessageOccasion>,
    isLoading: Boolean,
    onContactClick: (MessageContact) -> Unit,
    onOccasionClick: (MessageOccasion) -> Unit
) {
    when {
        isLoading -> {
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
                // Upcoming occasions
                if (upcomingOccasions.isNotEmpty()) {
                    item(key = "occasions_header") {
                        Text(
                            text = "Upcoming Occasions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item(key = "occasions_row") {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = upcomingOccasions,
                                key = { occasion -> "occasion_${occasion.id}" }
                            ) { occasion ->
                                OccasionChip(
                                    occasion = occasion,
                                    onClick = { onOccasionClick(occasion) }
                                )
                            }
                        }
                    }
                }

                // Contacts
                item(key = "contacts_header") {
                    Text(
                        text = "Your Contacts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = if (upcomingOccasions.isNotEmpty()) 8.dp else 0.dp)
                    )
                }

                if (contacts.isEmpty()) {
                    item(key = "empty_contacts") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "👥", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No contacts yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = contacts,
                        key = { contact -> "contact_${contact.id}" }
                    ) { contact ->
                        ContactCard(
                            contact = contact,
                            onClick = { onContactClick(contact) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OccasionChip(
    occasion: MessageOccasion,
    onClick: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d") }
    val occasionType = Occasion.fromId(occasion.occasion) ?: return
    val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(
        java.time.LocalDate.now(),
        occasion.date.toLocalDate()
    ).toInt()

    Card(
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color(occasionType.color).copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = occasionType.icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = occasion.contactName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = occasionType.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when {
                    daysUntil == 0 -> "Today!"
                    daysUntil == 1 -> "Tomorrow"
                    else -> "In $daysUntil days"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (daysUntil <= 3) ProdyWarning else Color(occasionType.color),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ContactCard(
    contact: MessageContact,
    onClick: () -> Unit
) {
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
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ProdyAccentGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.displayName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyAccentGreen
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when (contact.method) {
                            ContactMethod.EMAIL -> ProdyIcons.Email
                            ContactMethod.SMS -> ProdyIcons.Sms
                            ContactMethod.WHATSAPP -> ProdyIcons.Chat
                            ContactMethod.IN_APP -> ProdyIcons.Notifications
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = contact.contactValue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
private fun EmptyTabState(
    icon: String,
    title: String,
    description: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = icon, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
