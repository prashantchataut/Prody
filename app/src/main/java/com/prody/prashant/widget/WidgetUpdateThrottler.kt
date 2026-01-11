package com.prody.prashant.widget

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.TimeUnit

/**
 * Security: A helper object to rate-limit widget updates.
 * This prevents third-party apps from forcing frequent, resource-intensive updates
 * on our exported GlanceAppWidgetReceivers, mitigating a potential Denial-of-Service vector.
 *
 * How it works:
 * - It uses SharedPreferences to store the last update timestamp for each widget class.
 * - An update is only allowed if enough time has passed since the last one.
 * - This is significantly cheaper than allowing the full widget update logic (database queries, etc.) to run.
 */
object WidgetUpdateThrottler {

    private const val PREFS_NAME = "widget_update_throttle_prefs"
    private val MIN_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1) // 1 minute throttle

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Checks if a widget update should be allowed.
     * If allowed, it updates the timestamp and returns true.
     *
     * @param context The application context.
     * @param widgetClass The class of the widget being updated, used as a key.
     * @return `true` if the update is allowed, `false` if it should be throttled.
     */
    fun shouldUpdate(context: Context, widgetClass: Class<*>): Boolean {
        val prefs = getPrefs(context)
        val key = "last_update_${widgetClass.name}"
        val lastUpdateTime = prefs.getLong(key, 0L)
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastUpdateTime < MIN_INTERVAL_MS) {
            // Throttled
            return false
        }

        // Allowed, update the timestamp
        prefs.edit().putLong(key, currentTime).apply()
        return true
    }
}
