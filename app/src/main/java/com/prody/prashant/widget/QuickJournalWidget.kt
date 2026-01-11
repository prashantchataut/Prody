package com.prody.prashant.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.prody.prashant.data.local.database.ProdyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

/**
 * Quick Journal Entry Widget (4x1)
 * A tap-to-open widget that launches the app to the journal entry screen.
 * Now powered by Soul Layer intelligence for context-aware prompts.
 */
class QuickJournalWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Security: Rate-limit widget updates to prevent DoS attacks.
        if (!WidgetUpdateThrottler.shouldUpdate(context, this::class.java)) {
            return
        }

        val prompt = getIntelligentPrompt(context)

        provideContent {
            GlanceTheme {
                QuickJournalContent(context, prompt)
            }
        }
    }

    /**
     * Get an intelligent, context-aware journal prompt.
     * Considers time of day and adapts the message accordingly.
     */
    private suspend fun getIntelligentPrompt(context: Context): IntelligentPromptData = withContext(Dispatchers.IO) {
        try {
            val hour = LocalDateTime.now().hour
            val database = ProdyDatabase.getInstance(context)

            // Get user name if available
            val userName = try {
                database.userDao().getUserProfileSync()?.displayName
                    ?.takeIf { it.isNotBlank() && it != "Growth Seeker" }
            } catch (e: Exception) { null }

            // Get days since last entry
            val daysSinceLastEntry = try {
                val lastEntry = database.journalDao().getLatestEntry()
                if (lastEntry != null) {
                    val lastDate = java.time.Instant.ofEpochMilli(lastEntry.createdAt)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                    val today = java.time.LocalDate.now()
                    java.time.temporal.ChronoUnit.DAYS.between(lastDate, today).toInt()
                } else {
                    Int.MAX_VALUE
                }
            } catch (e: Exception) { Int.MAX_VALUE }

            // Generate context-aware prompt
            val (prompt, icon) = when {
                // Returning user after absence
                daysSinceLastEntry > 7 -> Pair(
                    "It's been a while. Welcome back!",
                    "\uD83D\uDC4B" // wave
                )
                daysSinceLastEntry > 3 -> Pair(
                    "We've missed you! What's new?",
                    "\u2728" // sparkles
                )
                // Time-based prompts
                hour in 5..7 -> Pair(
                    listOf(
                        "Early morning thoughts...",
                        "What's on your mind this dawn?",
                        "A quiet moment to reflect"
                    ).random(),
                    "\uD83C\uDF05" // sunrise
                )
                hour in 8..11 -> Pair(
                    listOf(
                        "Good morning! What's ahead today?",
                        "How are you feeling this morning?",
                        "Start your day with reflection"
                    ).random(),
                    "\u2600\uFE0F" // sun
                )
                hour in 12..16 -> Pair(
                    listOf(
                        "Midday check-in: how's it going?",
                        "Take a moment to pause and reflect",
                        "What's been on your mind?"
                    ).random(),
                    "\u2615" // coffee
                )
                hour in 17..20 -> Pair(
                    listOf(
                        "How was your day?",
                        "Evening reflection time",
                        "What's the highlight of today?"
                    ).random(),
                    "\uD83C\uDF06" // sunset
                )
                hour in 21..23 -> Pair(
                    listOf(
                        "Wind down with a few thoughts",
                        "What's on your mind tonight?",
                        "End the day mindfully"
                    ).random(),
                    "\uD83C\uDF19" // moon
                )
                else -> Pair(
                    listOf(
                        "Late night thoughts?",
                        "Can't sleep? Write it out",
                        "The night is for thinkers"
                    ).random(),
                    "\u2728" // sparkles
                )
            }

            IntelligentPromptData(
                prompt = prompt,
                icon = icon,
                userName = userName
            )
        } catch (e: Exception) {
            IntelligentPromptData(
                prompt = "What's on your mind today?",
                icon = "\u270F\uFE0F",
                userName = null
            )
        }
    }

    @Composable
    private fun QuickJournalContent(context: Context, promptData: IntelligentPromptData) {
        val broadcastIntent = Intent(context, SecureWidgetActionReceiver::class.java).apply {
            action = SecureWidgetActionReceiver.ACTION_QUICK_JOURNAL
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable(actionSendBroadcast(broadcastIntent)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Context-aware icon
                Text(
                    text = promptData.icon,
                    style = TextStyle(fontSize = 20.sp)
                )

                Spacer(modifier = GlanceModifier.width(12.dp))

                // Intelligent prompt text
                Column(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    Text(
                        text = promptData.prompt,
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 14.sp
                        ),
                        maxLines = 1
                    )
                    if (promptData.userName != null) {
                        Text(
                            text = "Hi, ${promptData.userName}",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFF6C63FF)),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Prody label
                Text(
                    text = "Prody",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF6C63FF)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Data class for intelligent prompt information.
 */
private data class IntelligentPromptData(
    val prompt: String,
    val icon: String,
    val userName: String?
)

class QuickJournalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuickJournalWidget()
}
