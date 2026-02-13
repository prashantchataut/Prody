package com.prody.prashant.ui.screens.recovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DatabaseRecoveryScreen(
    reason: String?,
    onResetDatabase: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Data recovery required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = reason
                ?: "A required database migration path is unavailable for this install. To prevent silent data loss, Prody paused startup.",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Next: reset local database, then restore your latest backup JSON in the recovery/onboarding flow.",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onResetDatabase,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset local database and continue")
        }
    }
}
