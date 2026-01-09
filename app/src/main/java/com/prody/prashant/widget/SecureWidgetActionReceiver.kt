package com.prody.prashant.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.prody.prashant.MainActivity
import com.prody.prashant.util.TokenManager

/**
 * A private BroadcastReceiver to securely handle actions from app widgets.
 * This acts as a "trampoline" to prevent other apps from maliciously triggering
 * widget actions, as this receiver is not exported.
 */
class SecureWidgetActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_QUICK_JOURNAL) {
            // Generate a one-time token for secure navigation
            val navigationToken = TokenManager.generateToken(context)

            val activityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                // Add the secure token and navigation route as extras
                putExtra(EXTRA_NAVIGATION_TOKEN, navigationToken)
                putExtra(EXTRA_NAVIGATE_TO, "journal/new")
            }
            context.startActivity(activityIntent)
        }
    }

    companion object {
        // It's good practice to define intent actions as constants.
        const val ACTION_QUICK_JOURNAL = "com.prody.prashant.widget.action.QUICK_JOURNAL"

        // Constants for intent extras for clarity and safety
        const val EXTRA_NAVIGATE_TO = "navigate_to"
        const val EXTRA_NAVIGATION_TOKEN = "navigation_token"
    }
}
