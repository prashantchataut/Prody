package com.prody.prashant.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * A composable that prevents screenshots and screen recording of the screen it's on.
 *
 * This should be used on screens that display sensitive user data, such as journal entries,
 * future messages, or personal reflections.
 *
 * It works by adding the `FLAG_SECURE` to the window of the current activity.
 * The flag is removed when the composable is disposed of.
 */
@Composable
fun PreventScreenshots() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
