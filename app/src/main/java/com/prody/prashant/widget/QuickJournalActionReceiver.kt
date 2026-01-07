package com.prody.prashant.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.prody.prashant.MainActivity
import com.prody.prashant.util.Constants
import com.prody.prashant.util.NavigationTokenManager

class QuickJournalActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Constants.ACTION_QUICK_JOURNAL) {
            val token = NavigationTokenManager.generateToken()
            val activityIntent = Intent(context, MainActivity::class.java).apply {
                action = Constants.ACTION_SECURE_NAVIGATE
                putExtra(Constants.EXTRA_NAVIGATION_TOKEN, token)
                putExtra(Constants.EXTRA_NAVIGATE_TO, "journal/new")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(activityIntent)
        }
    }
}
