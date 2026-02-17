package com.prody.prashant.util

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * SecureScreen Composable
 *
 * Provides a centralized way to enforce UI privacy on sensitive screens.
 * When this composable is active in the composition, it applies the
 * WindowManager.LayoutParams.FLAG_SECURE flag to the current window,
 * which prevents screenshots and screen recordings of the content.
 *
 * The flag is automatically removed when the composable leaves the composition.
 */
@Composable
fun SecureScreen() {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window

        // Add FLAG_SECURE to prevent screenshots/recordings
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        onDispose {
            // Remove FLAG_SECURE when leaving the screen
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
