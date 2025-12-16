package com.prody.prashant.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.ai.AiStats
import com.prody.prashant.data.ai.BuddhaAiRepository
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * AI Debug Panel - For monitoring AI service health and usage.
 *
 * Shows:
 * - Cache hit rate
 * - Last call error
 * - Last prompt type executed
 * - Last latency
 * - Never logs API keys
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDebugScreen(
    onNavigateBack: () -> Unit,
    viewModel: AiDebugViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Debug Panel",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshStats() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cache Statistics Card
            DebugSectionCard(
                title = "Cache Performance",
                icon = Icons.Filled.Cached
            ) {
                StatRow(
                    label = "Cache Hit Rate",
                    value = "${(uiState.stats.cacheHitRate * 100).toInt()}%",
                    valueColor = if (uiState.stats.cacheHitRate > 0.5f)
                        MoodGrateful else MoodAnxious
                )
                StatRow(
                    label = "Cache Hits",
                    value = "${uiState.stats.cacheHits}"
                )
                StatRow(
                    label = "Cache Misses",
                    value = "${uiState.stats.cacheMisses}"
                )
            }

            // API Statistics Card
            DebugSectionCard(
                title = "API Usage",
                icon = Icons.Filled.Api
            ) {
                StatRow(
                    label = "Total API Calls",
                    value = "${uiState.stats.totalApiCalls}"
                )
                StatRow(
                    label = "Rate Limit Hits",
                    value = "${uiState.stats.rateLimitHits}",
                    valueColor = if (uiState.stats.rateLimitHits > 0)
                        MoodAnxious else MaterialTheme.colorScheme.onSurface
                )
            }

            // Last Call Info Card
            DebugSectionCard(
                title = "Last Call Info",
                icon = Icons.Filled.History
            ) {
                if (uiState.stats.lastCallTimestamp > 0) {
                    StatRow(
                        label = "Provider",
                        value = uiState.stats.lastProvider.ifEmpty { "N/A" }
                    )
                    StatRow(
                        label = "Prompt Type",
                        value = uiState.stats.lastPromptType.ifEmpty { "N/A" }
                    )
                    StatRow(
                        label = "Latency",
                        value = "${uiState.stats.lastLatencyMs}ms",
                        valueColor = when {
                            uiState.stats.lastLatencyMs < 1000 -> MoodGrateful
                            uiState.stats.lastLatencyMs < 3000 -> GoldTier
                            else -> MoodAnxious
                        }
                    )
                    StatRow(
                        label = "Time",
                        value = formatTimestamp(uiState.stats.lastCallTimestamp)
                    )

                    // Error section
                    if (!uiState.stats.lastError.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Last Error",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MoodAnxious
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MoodAnxious.copy(alpha = 0.1f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = uiState.stats.lastError ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No API calls recorded yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Clear Cache Button
            OutlinedButton(
                onClick = { viewModel.clearCache() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MoodAnxious
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear AI Cache")
            }

            // Privacy Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "API keys are never logged or displayed here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DebugSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "N/A"
    }
}

// ViewModel
data class AiDebugUiState(
    val stats: AiStats = AiStats(),
    val isLoading: Boolean = false
)

@HiltViewModel
class AiDebugViewModel @Inject constructor(
    private val buddhaAiRepository: BuddhaAiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiDebugUiState())
    val uiState: StateFlow<AiDebugUiState> = _uiState.asStateFlow()

    init {
        refreshStats()
    }

    fun refreshStats() {
        _uiState.value = _uiState.value.copy(
            stats = buddhaAiRepository.getStats()
        )
    }

    fun clearCache() {
        viewModelScope.launch {
            buddhaAiRepository.clearCache()
            refreshStats()
        }
    }
}
