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
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.prody.prashant.data.local.database.ProdyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Streak Counter Widget (1x1 or 2x2)
 * Displays the current streak count with intelligent, context-aware messaging.
 * Powered by Soul Layer intelligence for personalized encouragement.
 */
class StreakWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Security: Rate-limit widget updates to prevent DoS attacks.
        if (!WidgetUpdateThrottler.shouldUpdate(context, this::class.java)) {
            return
        }

        // Get streak from shared preferences (widget-accessible storage)
        val prefs = context.getSharedPreferences("prody_widget_prefs", Context.MODE_PRIVATE)
        val streak = prefs.getInt("current_streak", 0)

        // Get intelligent streak message
        val streakMessage = getIntelligentStreakMessage(context, streak)

        provideContent {
            GlanceTheme {
                StreakContent(
                    streak = streak,
                    message = streakMessage
                )
            }
        }
    }

    /**
     * Get an intelligent, context-aware streak message.
     * Provides personalized encouragement based on streak length and user name.
     */
    private suspend fun getIntelligentStreakMessage(context: Context, streak: Int): IntelligentStreakData = withContext(Dispatchers.IO) {
        try {
            val database = ProdyDatabase.getInstance(context)

            // Get user name for personalization
            val userName = try {
                database.userDao().getUserProfileSync()?.displayName
                    ?.takeIf { it.isNotBlank() && it != "Growth Seeker" }
            } catch (e: Exception) { null }

            // Generate context-aware message based on streak
            val (emoji, title, message) = when {
                streak == 0 -> Triple(
                    "\uD83C\uDF31", // seedling
                    "Start fresh",
                    "Today's a great day to begin"
                )
                streak == 1 -> Triple(
                    "\u2728", // sparkles
                    "Day 1",
                    "Every journey starts here"
                )
                streak in 2..6 -> Triple(
                    "\uD83D\uDD25", // fire
                    "$streak days",
                    "Building momentum"
                )
                streak == 7 -> Triple(
                    "\uD83C\uDF89", // party popper
                    "1 week!",
                    "A whole week of growth"
                )
                streak in 8..13 -> Triple(
                    "\uD83D\uDCAA", // flexed bicep
                    "$streak days",
                    "Impressive dedication"
                )
                streak == 14 -> Triple(
                    "\uD83C\uDFC6", // trophy
                    "2 weeks!",
                    "Two weeks strong"
                )
                streak in 15..29 -> Triple(
                    "\uD83D\uDD25", // fire
                    "$streak days",
                    "You're on fire"
                )
                streak == 30 -> Triple(
                    "\uD83C\uDF1F", // star
                    "1 month!",
                    "A full month of growth"
                )
                streak in 31..99 -> Triple(
                    "\u2B50", // star
                    "$streak days",
                    "Legendary consistency"
                )
                streak >= 100 -> Triple(
                    "\uD83D\uDC51", // crown
                    "$streak days",
                    "You're unstoppable"
                )
                else -> Triple(
                    "\uD83D\uDD25",
                    "$streak days",
                    "Keep going"
                )
            }

            IntelligentStreakData(
                emoji = emoji,
                title = title,
                message = if (userName != null) "$message, $userName" else message
            )
        } catch (e: Exception) {
            IntelligentStreakData(
                emoji = if (streak > 0) "\uD83D\uDD25" else "\uD83C\uDF31",
                title = if (streak > 0) "$streak days" else "Start today",
                message = "Keep reflecting"
            )
        }
    }

    @Composable
    private fun StreakContent(streak: Int, message: IntelligentStreakData) {
        val backgroundColor = if (streak > 0) {
            Color(0xFF1A1A2E)
        } else {
            Color(0xFF2A2A3E)
        }

        val streakColor = when {
            streak >= 100 -> Color(0xFFFFD700) // Gold for 100+ days
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
                // Intelligent emoji based on streak milestone
                Text(
                    text = message.emoji,
                    style = TextStyle(
                        fontSize = 20.sp
                    )
                )

                // Streak count or title
                Text(
                    text = message.title,
                    style = TextStyle(
                        color = ColorProvider(streakColor),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.height(2.dp))

                // Intelligent encouragement message
                Text(
                    text = message.message,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFAAAAAA)),
                        fontSize = 9.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Data class for intelligent streak information.
 */
private data class IntelligentStreakData(
    val emoji: String,
    val title: String,
    val message: String
)

class RefreshStreakAction : androidx.glance.appwidget.action.ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: androidx.glance.action.ActionParameters
    ) {
        StreakWidget().updateAll(context)
    }
}

class StreakWidgetReceiver : BaseSecureWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StreakWidget()
}
