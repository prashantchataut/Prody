package com.prody.prashant.util

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Reusable Composable that prevents screenshots and screen recordings for the current screen.
 * Adds WindowManager.LayoutParams.FLAG_SECURE to the Activity window.
 *
 * Secure by default - use this on all screens displaying sensitive user data
 * (Journals, Future Messages, Haven Chats, etc.)
 */
@Composable
fun PreventScreenshots() {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val window = activity?.window

        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
