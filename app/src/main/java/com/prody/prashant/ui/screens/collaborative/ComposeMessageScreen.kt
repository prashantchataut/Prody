package com.prody.prashant.ui.screens.collaborative

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Compose Message Screen - Create and send collaborative messages.
 *
 * Features:
 * - Recipient selection (contact or manual entry)
 * - Message content with title
 * - Occasion and theme selection
 * - Delivery scheduling
 * - Card preview
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeMessageScreen(
    preSelectedContactId: String?,
    preSelectedOccasion: String?,
    onNavigateBack: () -> Unit,
    onMessageSent: () -> Unit,
    viewModel: CollaborativeViewModel = hiltViewModel()
) {
    val uiState by viewModel.composeState.collectAsStateWithLifecycle()
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showOccasionPicker by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    var showContactPicker by remember { mutableStateOf(false) }

    // Initialize with pre-selected values
    LaunchedEffect(preSelectedContactId, preSelectedOccasion) {
        val contact = preSelectedContactId?.let { id ->
            homeState.contacts.find { it.id == id }
        }
        val occasion = preSelectedOccasion?.let { Occasion.fromId(it) }
        viewModel.startComposing(contact, occasion)
    }

    // Handle successful send
    LaunchedEffect(uiState.messageSent) {
        if (uiState.messageSent) {
            onMessageSent()
            viewModel.resetComposeState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Message",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetComposeState()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.sendMessage() },
                        enabled = uiState.canSend && !uiState.isSending
                    ) {
                        if (uiState.isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (uiState.isScheduledForFuture) {
                                    Icons.Default.Schedule
                                } else {
                                    Icons.AutoMirrored.Filled.Send
                                },
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (uiState.isScheduledForFuture) "Schedule" else "Send")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Card Preview
            CardPreview(
                theme = uiState.selectedTheme,
                title = uiState.title.ifBlank { "Your message title" },
                content = uiState.content.ifBlank { "Your heartfelt message..." },
                occasion = uiState.selectedOccasion
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recipient section
            SectionHeader(title = "To")
            RecipientSection(
                recipientName = uiState.recipientName,
                recipientContact = uiState.recipientContact,
                contactMethod = uiState.contactMethod,
                selectedContact = uiState.selectedContact,
                onNameChange = viewModel::onRecipientNameChanged,
                onContactChange = viewModel::onRecipientContactChanged,
                onMethodChange = viewModel::onContactMethodChanged,
                onSelectFromContacts = { showContactPicker = true }
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Message content
            SectionHeader(title = "Message")
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Subject / Title") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ProdyAccentGreen,
                    cursorColor = ProdyAccentGreen
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.content,
                onValueChange = viewModel::onContentChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Write your message...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ProdyAccentGreen,
                    cursorColor = ProdyAccentGreen
                )
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Occasion selection
            SectionHeader(title = "Occasion (Optional)")
            OccasionSelector(
                selectedOccasion = uiState.selectedOccasion,
                onShowPicker = { showOccasionPicker = true },
                onClear = { viewModel.onOccasionSelected(null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Theme selection
            SectionHeader(title = "Card Theme")
            ThemeSelector(
                selectedTheme = uiState.selectedTheme,
                onShowPicker = { showThemePicker = true }
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Delivery scheduling
            SectionHeader(title = "Delivery")
            DeliveryScheduler(
                deliveryDate = uiState.deliveryDate,
                onShowDatePicker = { showDatePicker = true },
                onShowTimePicker = { showTimePicker = true },
                onSendNow = {
                    viewModel.onDeliveryDateChanged(LocalDateTime.now())
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Contact picker
    if (showContactPicker) {
        ContactPickerSheet(
            contacts = homeState.contacts,
            onSelect = { contact ->
                viewModel.onSelectContact(contact)
                showContactPicker = false
            },
            onDismiss = { showContactPicker = false }
        )
    }

    // Occasion picker
    if (showOccasionPicker) {
        OccasionPickerSheet(
            selectedOccasion = uiState.selectedOccasion,
            onSelect = { occasion ->
                viewModel.onOccasionSelected(occasion)
                showOccasionPicker = false
            },
            onDismiss = { showOccasionPicker = false }
        )
    }

    // Theme picker
    if (showThemePicker) {
        ThemePickerSheet(
            selectedTheme = uiState.selectedTheme,
            onSelect = { theme ->
                viewModel.onThemeSelected(theme)
                showThemePicker = false
            },
            onDismiss = { showThemePicker = false }
        )
    }

    // Date picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.deliveryDate
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                            .atTime(uiState.deliveryDate.toLocalTime())
                        viewModel.onDeliveryDateChanged(newDate)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time picker
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.deliveryDate.hour,
            initialMinute = uiState.deliveryDate.minute
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(onClick = {
                    val newDate = uiState.deliveryDate
                        .withHour(timePickerState.hour)
                        .withMinute(timePickerState.minute)
                    viewModel.onDeliveryDateChanged(newDate)
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Error handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            viewModel.dismissComposeError()
        }
    }
}

@Composable
private fun CardPreview(
    theme: CardTheme,
    title: String,
    content: String,
    occasion: Occasion?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(theme.colorLight)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Occasion indicator
            occasion?.let { occ ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(text = occ.icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = occ.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(theme.colorDark)
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(theme.colorDark)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = if (content == "Your heartfelt message...") FontStyle.Italic else FontStyle.Normal,
                color = if (content == "Your heartfelt message...") {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Theme decoration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = theme.icon, fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun RecipientSection(
    recipientName: String,
    recipientContact: String,
    contactMethod: ContactMethod,
    selectedContact: MessageContact?,
    onNameChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
    onMethodChange: (ContactMethod) -> Unit,
    onSelectFromContacts: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Contact method selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ContactMethod.entries.forEach { method ->
                FilterChip(
                    selected = contactMethod == method,
                    onClick = { onMethodChange(method) },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = when (method) {
                                    ContactMethod.EMAIL -> Icons.Default.Email
                                    ContactMethod.SMS -> Icons.Default.Sms
                                    ContactMethod.WHATSAPP -> Icons.Default.Chat
                                    ContactMethod.IN_APP -> Icons.Default.Notifications
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(method.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ProdyAccentGreen.copy(alpha = 0.2f),
                        selectedLabelColor = ProdyAccentGreen
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Name field
        OutlinedTextField(
            value = recipientName,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Recipient name") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProdyAccentGreen,
                cursorColor = ProdyAccentGreen
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Contact field
        OutlinedTextField(
            value = recipientContact,
            onValueChange = onContactChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    when (contactMethod) {
                        ContactMethod.EMAIL -> "Email address"
                        ContactMethod.SMS -> "Phone number"
                        ContactMethod.WHATSAPP -> "WhatsApp number"
                        ContactMethod.IN_APP -> "Username"
                    }
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = when (contactMethod) {
                        ContactMethod.EMAIL -> Icons.Default.Email
                        ContactMethod.SMS -> Icons.Default.Phone
                        ContactMethod.WHATSAPP -> Icons.Default.Chat
                        ContactMethod.IN_APP -> Icons.Default.AlternateEmail
                    },
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = onSelectFromContacts) {
                    Icon(
                        imageVector = Icons.Default.Contacts,
                        contentDescription = "Select from contacts"
                    )
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProdyAccentGreen,
                cursorColor = ProdyAccentGreen
            )
        )
    }
}

@Composable
private fun OccasionSelector(
    selectedOccasion: Occasion?,
    onShowPicker: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (selectedOccasion != null) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(selectedOccasion.color).copy(alpha = 0.15f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = selectedOccasion.icon, fontSize = 24.sp)
                        Text(
                            text = selectedOccasion.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            OutlinedButton(
                onClick = onShowPicker,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Celebration, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Occasion")
            }
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedTheme: CardTheme,
    onShowPicker: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = CardTheme.getAllThemes().take(6),
            key = { theme -> "theme_preview_${theme.id}" }
        ) { theme ->
            ThemeMiniPreview(
                theme = theme,
                isSelected = theme == selectedTheme,
                onClick = onShowPicker
            )
        }

        item(key = "more_themes") {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onShowPicker() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More themes",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ThemeMiniPreview(
    theme: CardTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(theme.colorLight))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) ProdyAccentGreen else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = theme.icon, fontSize = 24.sp)
    }
}

@Composable
private fun DeliveryScheduler(
    deliveryDate: LocalDateTime,
    onShowDatePicker: () -> Unit,
    onShowTimePicker: () -> Unit,
    onSendNow: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a") }
    val isInFuture = deliveryDate.isAfter(LocalDateTime.now().plusMinutes(1))

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Send now option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onSendNow() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !isInFuture,
                onClick = onSendNow,
                colors = RadioButtonDefaults.colors(
                    selectedColor = ProdyAccentGreen
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Send now",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Schedule option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onShowDatePicker() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isInFuture,
                onClick = { onShowDatePicker() },
                colors = RadioButtonDefaults.colors(
                    selectedColor = ProdyAccentGreen
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Schedule for later",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (isInFuture) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = onShowDatePicker,
                            label = { Text(deliveryDate.format(dateFormatter)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                        AssistChip(
                            onClick = onShowTimePicker,
                            label = { Text(deliveryDate.format(timeFormatter)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactPickerSheet(
    contacts: List<MessageContact>,
    onSelect: (MessageContact) -> Unit,
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
                text = "Select Contact",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (contacts.isEmpty()) {
                Text(
                    text = "No contacts saved yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                contacts.forEach { contact ->
                    ListItem(
                        headlineContent = { Text(contact.displayName) },
                        supportingContent = { Text(contact.contactValue) },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ProdyAccentGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = contact.displayName.firstOrNull()?.uppercase() ?: "?",
                                    fontWeight = FontWeight.Bold,
                                    color = ProdyAccentGreen
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(contact) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OccasionPickerSheet(
    selectedOccasion: Occasion?,
    onSelect: (Occasion) -> Unit,
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
                text = "Select Occasion",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = Occasion.getAllOccasions(),
                    key = { occasion -> "occasion_picker_${occasion.id}" }
                ) { occasion ->
                    Card(
                        modifier = Modifier.width(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        onClick = { onSelect(occasion) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedOccasion == occasion) {
                                Color(occasion.color).copy(alpha = 0.3f)
                            } else {
                                Color(occasion.color).copy(alpha = 0.1f)
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = occasion.icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = occasion.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemePickerSheet(
    selectedTheme: CardTheme,
    onSelect: (CardTheme) -> Unit,
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
                text = "Select Card Theme",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = CardTheme.getAllThemes(),
                    key = { theme -> "theme_picker_${theme.id}" }
                ) { theme ->
                    Card(
                        modifier = Modifier.width(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { onSelect(theme) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(theme.colorLight)
                        ),
                        border = if (selectedTheme == theme) {
                            CardDefaults.outlinedCardBorder().copy(
                                width = 2.dp,
                                brush = androidx.compose.ui.graphics.SolidColor(ProdyAccentGreen)
                            )
                        } else null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = theme.icon, fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = theme.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
