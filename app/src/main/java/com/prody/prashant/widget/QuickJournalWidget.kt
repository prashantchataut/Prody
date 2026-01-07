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
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
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
import com.prody.prashant.MainActivity

/**
 * Quick Journal Entry Widget (4x1)
 * A tap-to-open widget that launches the app to the journal entry screen.
 */
class QuickJournalWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                QuickJournalContent(context)
            }
        }
    }

    @Composable
    private fun QuickJournalContent(context: Context) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable(actionRunCallback<QuickJournalClickAction>()),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pencil icon (using emoji as placeholder)
                Text(
                    text = "✏️",
                    style = TextStyle(fontSize = 20.sp)
                )

                Spacer(modifier = GlanceModifier.width(12.dp))

                // Prompt text
                Text(
                    text = "What's on your mind today?",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFB0B0B0)),
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = GlanceModifier.defaultWeight())

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

class QuickJournalClickAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val intent = Intent(context, QuickJournalActionReceiver::class.java).apply {
            action = com.prody.prashant.util.Constants.ACTION_QUICK_JOURNAL
        }
        context.sendBroadcast(intent)
    }
}

class QuickJournalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuickJournalWidget()
}
