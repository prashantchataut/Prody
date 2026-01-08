package com.prody.prashant.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.prody.prashant.MainActivity

/**
 * A private BroadcastReceiver to securely handle actions from app widgets.
 * This acts as a "trampoline" to prevent other apps from maliciously triggering
 * widget actions, as this receiver is not exported.
 */
class SecureWidgetActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_QUICK_JOURNAL) {
            val activityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("navigate_to", "journal/new")
            }
            context.startActivity(activityIntent)
        }
    }

    companion object {
        // It's good practice to define intent actions as constants.
        const val ACTION_QUICK_JOURNAL = "com.prody.prashant.widget.action.QUICK_JOURNAL"
    }
}
