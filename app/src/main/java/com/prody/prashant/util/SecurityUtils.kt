package com.prody.prashant.util

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Composable that prevents screenshots and screen recordings of the current screen.
 * It adds WindowManager.LayoutParams.FLAG_SECURE to the Activity window.
 * The flag is automatically cleared when the Composable is removed from the composition.
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
