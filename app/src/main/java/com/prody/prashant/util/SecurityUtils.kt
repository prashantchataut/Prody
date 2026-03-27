package com.prody.prashant.util

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * A Composable that applies [WindowManager.LayoutParams.FLAG_SECURE] to the current window
 * while it is active. This prevents screenshots and screen recordings of the screen,
 * and also hides the content in the Android Recents (multitasking) screen.
 *
 * Use this on screens displaying sensitive user data like journals, reflections,
 * and personal insights.
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
