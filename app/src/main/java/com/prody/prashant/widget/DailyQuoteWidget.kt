package com.prody.prashant.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
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
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.prody.prashant.data.content.WisdomContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Calendar

/**
 * Daily Quote Widget (4x2)
 * Displays an inspiring quote that changes daily with the author name.
 * Powered by Soul Layer intelligence for time-aware introductions.
 */
class DailyQuoteWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val quoteData = getDailyQuoteData()

        provideContent {
            GlanceTheme {
                DailyQuoteContent(
                    introduction = quoteData.introduction,
                    quote = quoteData.quote,
                    author = quoteData.author
                )
            }
        }
    }

    /**
     * Get daily quote with intelligent, time-aware introduction.
     */
    private suspend fun getDailyQuoteData(): IntelligentQuoteData = withContext(Dispatchers.IO) {
        val quotes = WisdomContent.allQuotes
        val (quote, author) = if (quotes.isEmpty()) {
            Pair("The obstacle is the way.", "Marcus Aurelius")
        } else {
            // Use day of year to get consistent daily quote
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val index = dayOfYear % quotes.size
            val q = quotes[index]
            Pair(q.text, q.source)
        }

        // Get time-aware introduction
        val hour = LocalDateTime.now().hour
        val introduction = when (hour) {
            in 5..6 -> "Early morning wisdom"
            in 7..9 -> "Morning wisdom"
            in 10..11 -> "Midday reflection"
            in 12..16 -> "Afternoon inspiration"
            in 17..20 -> "Evening reflection"
            in 21..23 -> "Night thoughts"
            else -> "Late night wisdom"
        }

        IntelligentQuoteData(
            introduction = introduction,
            quote = quote,
            author = author
        )
    }

    @Composable
    private fun DailyQuoteContent(introduction: String, quote: String, author: String) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E))
                .padding(16.dp)
                .clickable(actionRunCallback<RefreshQuoteAction>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Time-aware introduction
                Text(
                    text = introduction,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF6C63FF)),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                // Quote icon
                Text(
                    text = "❝",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF6C63FF)),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.height(6.dp))

                // Quote text
                Text(
                    text = quote,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    ),
                    maxLines = 3
                )

                Spacer(modifier = GlanceModifier.height(6.dp))

                // Author
                Text(
                    text = "— $author",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFB0B0B0)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                // Prody branding
                Text(
                    text = "Prody",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF6C63FF)),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Data class for intelligent quote information.
 */
private data class IntelligentQuoteData(
    val introduction: String,
    val quote: String,
    val author: String
)

class RefreshQuoteAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        DailyQuoteWidget().updateAll(context)
    }
}

class DailyQuoteWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DailyQuoteWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Security: Rate-limit widget updates to prevent DoS attacks.
        // Check performed at entry point before calling super.onUpdate()
        if (!WidgetUpdateThrottler.shouldUpdate(context, this::class.java)) {
            return
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}
