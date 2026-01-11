package com.prody.prashant.widget

import android.content.Context
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
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.prody.prashant.data.content.VocabularyContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Calendar

/**
 * Word of the Day Widget (2x2)
 * Displays the daily vocabulary word with its definition.
 * Powered by Soul Layer intelligence for time-aware learning prompts.
 */
class WordOfDayWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val wordData = getDailyWordData()

        provideContent {
            GlanceTheme {
                WordOfDayContent(
                    label = wordData.label,
                    word = wordData.word,
                    partOfSpeech = wordData.partOfSpeech,
                    definition = wordData.definition
                )
            }
        }
    }

    /**
     * Get daily word with intelligent, time-aware label.
     */
    private suspend fun getDailyWordData(): IntelligentWordData = withContext(Dispatchers.IO) {
        val words = VocabularyContent.allWords
        val (word, partOfSpeech, definition) = if (words.isEmpty()) {
            Triple("Resilience", "noun", "The capacity to recover quickly from difficulties")
        } else {
            // Use day of year to get consistent daily word
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val index = dayOfYear % words.size
            val w = words[index]
            Triple(w.word, w.partOfSpeech, w.definition)
        }

        // Get time-aware label
        val hour = LocalDateTime.now().hour
        val label = when (hour) {
            in 5..9 -> "MORNING WORD"
            in 10..16 -> "WORD OF THE DAY"
            in 17..21 -> "EVENING WORD"
            else -> "NIGHT WORD"
        }

        IntelligentWordData(
            label = label,
            word = word,
            partOfSpeech = partOfSpeech,
            definition = definition
        )
    }

    @Composable
    private fun WordOfDayContent(label: String, word: String, partOfSpeech: String, definition: String) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E))
                .padding(12.dp)
                .clickable(actionRunCallback<RefreshWordAction>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Time-aware label
                Text(
                    text = label,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF6C63FF)),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                // Word
                Text(
                    text = word,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Part of speech
                Text(
                    text = partOfSpeech,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF888888)),
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = GlanceModifier.height(6.dp))

                // Definition (truncated)
                Text(
                    text = definition,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFCCCCCC)),
                        fontSize = 11.sp
                    ),
                    maxLines = 3
                )
            }
        }
    }
}

/**
 * Data class for intelligent word information.
 */
private data class IntelligentWordData(
    val label: String,
    val word: String,
    val partOfSpeech: String,
    val definition: String
)

class RefreshWordAction : androidx.glance.appwidget.action.ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: androidx.glance.action.ActionParameters
    ) {
        WordOfDayWidget().updateAll(context)
    }
}

class WordOfDayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WordOfDayWidget()
}
