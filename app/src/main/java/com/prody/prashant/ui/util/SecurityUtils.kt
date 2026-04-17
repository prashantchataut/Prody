package com.prody.prashant.ui.util

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * A reusable Composable that applies [WindowManager.LayoutParams.FLAG_SECURE] to the current window.
 * This prevents screenshots and screen recordings of the screen's content.
 *
 * Usage:
 * ```
 * @Composable
 * fun PrivateScreen() {
 *     SecureScreen()
 *     // ... screen content
 * }
 * ```
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
