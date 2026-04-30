package com.prody.prashant.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * A reusable Composable that applies FLAG_SECURE to the current window.
 * This prevents screenshots and screen recordings of the screen where it is used.
 *
 * Usage:
 * @Composable
 * fun SensitiveScreen() {
 *     SecureScreen()
 *     // Screen content...
 * }
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
