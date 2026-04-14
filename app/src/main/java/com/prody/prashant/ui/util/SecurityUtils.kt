package com.prody.prashant.ui.util

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * A reusable Composable that applies [WindowManager.LayoutParams.FLAG_SECURE] to the current
 * Activity's window. This prevents screenshots, screen recordings, and ensures the screen
 * content is hidden in the recent apps switcher.
 *
 * Use this on screens containing sensitive user data (journals, private reflections, etc.).
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
