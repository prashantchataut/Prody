package com.prody.prashant.ui.screens.futuremessage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteMessageScreen(
    onNavigateBack: () -> Unit,
    onMessageSaved: () -> Unit,
    viewModel: WriteMessageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onMessageSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.write_to_future)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveMessage() },
                        enabled = uiState.canSave && !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.send_message),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Inspirational header
            ProdyCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "A Message Through Time",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Write to your future self. What do you hope they remember? What promises do you want to keep? What wisdom do you want to share?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Title input
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text("Title") },
                placeholder = { Text("e.g., A Letter to Future Me") },
                singleLine = true,
                shape = CardShape
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content input
            OutlinedTextField(
                value = uiState.content,
                onValueChange = { viewModel.updateContent(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp)
                    .padding(horizontal = 16.dp),
                label = { Text("Your Message") },
                placeholder = {
                    Text(stringResource(R.string.future_message_hint))
                },
                shape = CardShape
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Delivery date selector
            ProdyCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.delivery_date),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick select buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickDateChip(
                            text = "1 Week",
                            isSelected = uiState.selectedPreset == DatePreset.ONE_WEEK,
                            onClick = { viewModel.selectDatePreset(DatePreset.ONE_WEEK) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickDateChip(
                            text = "1 Month",
                            isSelected = uiState.selectedPreset == DatePreset.ONE_MONTH,
                            onClick = { viewModel.selectDatePreset(DatePreset.ONE_MONTH) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickDateChip(
                            text = "6 Months",
                            isSelected = uiState.selectedPreset == DatePreset.SIX_MONTHS,
                            onClick = { viewModel.selectDatePreset(DatePreset.SIX_MONTHS) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickDateChip(
                            text = "1 Year",
                            isSelected = uiState.selectedPreset == DatePreset.ONE_YEAR,
                            onClick = { viewModel.selectDatePreset(DatePreset.ONE_YEAR) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickDateChip(
                            text = "Custom",
                            isSelected = uiState.selectedPreset == DatePreset.CUSTOM,
                            onClick = { showDatePicker = true },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selected date display
                    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Delivers on ${dateFormat.format(Date(uiState.deliveryDate))}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Category selector
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Category (Optional)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MessageCategory.entries.forEach { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { viewModel.updateCategory(category) },
                            label = { Text(category.displayName) },
                            leadingIcon = if (uiState.selectedCategory == category) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.deliveryDate
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                viewModel.selectCustomDate(it)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }
    }
}

@Composable
private fun QuickDateChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium
            )
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

enum class DatePreset {
    ONE_WEEK, ONE_MONTH, SIX_MONTHS, ONE_YEAR, CUSTOM
}

enum class MessageCategory(val displayName: String) {
    GENERAL("General"),
    GOAL("Goal"),
    PROMISE("Promise"),
    MOTIVATION("Motivation")
}
