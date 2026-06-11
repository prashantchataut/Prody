package com.prody.prashant.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Security: UI privacy protection component.
 * Applies [WindowManager.LayoutParams.FLAG_SECURE] to the current window while active.
 * This prevents screenshots and screen recordings of sensitive screens, and hides
 * the screen content in the recent apps switcher on some Android versions.
 *
 * Usage:
 * @Composable
 * fun SensitiveScreen() {
 *     SecureScreen()
 *     // ... screen content
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
