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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

/**
 * Streak Counter Widget (1x1)
 * Displays the current streak count with a fire emoji.
 */
class StreakWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Get streak from shared preferences (widget-accessible storage)
        val prefs = context.getSharedPreferences("prody_widget_prefs", Context.MODE_PRIVATE)
        val streak = prefs.getInt("current_streak", 0)

        provideContent {
            GlanceTheme {
                StreakContent(streak = streak)
            }
        }
    }

    @Composable
    private fun StreakContent(streak: Int) {
        val backgroundColor = if (streak > 0) {
            Color(0xFF1A1A2E)
        } else {
            Color(0xFF2A2A3E)
        }

        val streakColor = when {
            streak >= 30 -> Color(0xFFFFD700) // Gold for 30+ days
            streak >= 14 -> Color(0xFFFF6B35) // Orange for 14+ days
            streak >= 7 -> Color(0xFF6C63FF) // Purple for 7+ days
            streak > 0 -> Color(0xFF4CAF50) // Green for active streak
            else -> Color(0xFF888888) // Gray for no streak
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(8.dp)
                .clickable(actionRunCallback<RefreshStreakAction>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fire emoji or indicator
                Text(
                    text = if (streak > 0) "🔥" else "○",
                    style = TextStyle(
                        fontSize = 20.sp
                    )
                )

                // Streak count
                Text(
                    text = "$streak",
                    style = TextStyle(
                        color = ColorProvider(streakColor),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Label
                Text(
                    text = if (streak == 1) "day" else "days",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFAAAAAA)),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

class RefreshStreakAction : androidx.glance.appwidget.action.ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: androidx.glance.action.ActionParameters
    ) {
        StreakWidget().updateAll(context)
    }
}

class StreakWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StreakWidget()
}
