package com.prody.prashant.widget

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
import java.util.Calendar

/**
 * Daily Quote Widget (4x2)
 * Displays an inspiring quote that changes daily with the author name.
 */
class DailyQuoteWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val quote = getDailyQuote()

        provideContent {
            GlanceTheme {
                DailyQuoteContent(
                    quote = quote.first,
                    author = quote.second
                )
            }
        }
    }

    private suspend fun getDailyQuote(): Pair<String, String> = withContext(Dispatchers.IO) {
        val quotes = WisdomContent.allQuotes
        if (quotes.isEmpty()) {
            return@withContext Pair(
                "The obstacle is the way.",
                "Marcus Aurelius"
            )
        }

        // Use day of year to get consistent daily quote
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = dayOfYear % quotes.size
        val quote = quotes[index]
        Pair(quote.text, quote.source)
    }

    @Composable
    private fun DailyQuoteContent(quote: String, author: String) {
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
                // Quote icon
                Text(
                    text = "❝",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF6C63FF)),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

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

                Spacer(modifier = GlanceModifier.height(8.dp))

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
}
