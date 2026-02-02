package com.prody.prashant.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Base Secure Widget Receiver that provides protection against Denial-of-Service (DoS) attacks.
 *
 * Exported BroadcastReceivers on Android are entry points that any application can trigger.
 * Malicious apps could send a flood of APPWIDGET_UPDATE broadcasts to force frequent,
 * resource-intensive updates (database queries, AI logic, etc.) on our widgets.
 *
 * This base class implements rate-limiting at the earliest possible entry point (onUpdate),
 * preventing the scheduling of expensive background work if updates are requested too frequently.
 */
abstract class BaseSecureWidgetReceiver : GlanceAppWidgetReceiver() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Security: Rate-limit widget updates to prevent DoS attacks.
        // We check this at the entry point (onUpdate) before calling super.onUpdate(),
        // which would otherwise schedule a Glance background worker.
        if (!WidgetUpdateThrottler.shouldUpdate(context, this::class.java)) {
            // Throttled: Exit early to save system resources.
            return
        }

        // Allowed: Proceed with standard Glance update logic.
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}
