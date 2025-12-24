package com.prody.prashant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.screens.home.AiProofModeInfo
import com.prody.prashant.ui.theme.MoodAnxious
import com.prody.prashant.ui.theme.MoodGrateful
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AI Proof Mode Debug Info - A compact debug overlay for AI surfaces.
 *
 * Shows when AI Proof Mode is enabled in Settings (debug-only feature):
 * - Provider used: Gemini/OpenRouter/Fallback
 * - Cache status: HIT/MISS
 * - Timestamp of last successful generation
 * - Last error (short code/message) if failed
 * - API key configuration warning
 *
 * Design: Small, non-invasive, monospace font for debug appearance.
 */
@Composable
fun AiProofModeDebugInfo(
    proofInfo: AiProofModeInfo,
    modifier: Modifier = Modifier
) {
    // Only show when AI Proof Mode is enabled
    if (!proofInfo.isEnabled) return

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // API Key Warning - show prominently if not configured
            if (!proofInfo.isAiConfigured) {
                Text(
                    text = "AI is not configured. Add an API key in local.properties or Settings.",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = MoodAnxious
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Main debug line
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Provider
                DebugLabel(
                    label = "Provider",
                    value = proofInfo.provider.ifEmpty { "N/A" },
                    valueColor = when (proofInfo.provider) {
                        "Gemini", "OpenRouter" -> MoodGrateful
                        "Fallback" -> MaterialTheme.colorScheme.onSurfaceVariant
                        "Error" -> MoodAnxious
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                // Cache status
                DebugLabel(
                    label = "Cache",
                    value = proofInfo.cacheStatus.ifEmpty { "N/A" },
                    valueColor = when (proofInfo.cacheStatus) {
                        "HIT" -> MoodGrateful
                        "MISS" -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                // Timestamp
                DebugLabel(
                    label = "Time",
                    value = formatDebugTimestamp(proofInfo.timestamp),
                    valueColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Error line (if present and not duplicate of API key warning)
            if (!proofInfo.lastError.isNullOrEmpty() && proofInfo.isAiConfigured) {
                Text(
                    text = "ERR: ${proofInfo.lastError.take(50)}${if (proofInfo.lastError.length > 50) "..." else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MoodAnxious
                )
            }
        }
    }
}

@Composable
private fun DebugLabel(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

private fun formatDebugTimestamp(timestamp: Long): String {
    if (timestamp <= 0) return "N/A"
    return try {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "N/A"
    }
}
