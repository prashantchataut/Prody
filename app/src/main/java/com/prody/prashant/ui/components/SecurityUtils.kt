package com.prody.prashant.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * A Composable that, when present in the composition, applies FLAG_SECURE to the current window.
 * This prevents screenshots and screen recordings of the screen's content.
 * The flag is automatically removed when the Composable is disposed.
 */
@Composable
fun SecureScreen() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}

/**
 * Utility functions for security-related UI operations.
 */
object SecurityUtils {
    /**
     * Manually applies FLAG_SECURE to an activity's window.
     */
    fun setSecure(activity: Activity, isSecure: Boolean) {
        if (isSecure) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
