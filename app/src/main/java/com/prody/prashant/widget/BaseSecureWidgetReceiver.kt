package com.prody.prashant.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Security: Base class for all Prody GlanceAppWidgetReceivers.
 *
 * Implements centralized rate-limiting to protect against Denial-of-Service (DoS) attacks
 * from third-party apps triggering frequent widget updates.
 */
abstract class BaseSecureWidgetReceiver : GlanceAppWidgetReceiver() {

    companion object {
        private const val TAG = "BaseSecureWidgetReceiver"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Security: Earliest entry point rate-limiting check
        if (!WidgetUpdateThrottler.shouldUpdate(context, this::class.java)) {
            com.prody.prashant.util.AppLogger.w(TAG, "Widget update throttled for ${this::class.java.simpleName}")
            return
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Security: Rate-limit based on update actions at the receiver level
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE ||
            intent.action == "androidx.glance.appwidget.action.UPDATE_ALL") {
            if (!WidgetUpdateThrottler.shouldUpdate(context, this::class.java)) {
                com.prody.prashant.util.AppLogger.w(TAG, "Widget broadcast throttled for ${this::class.java.simpleName}")
                return
            }
        }

        super.onReceive(context, intent)
    }
}
