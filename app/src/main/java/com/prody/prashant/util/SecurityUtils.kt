package com.prody.prashant.util

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Composable that prevents screenshots and screen recordings for the current screen.
 * It adds WindowManager.LayoutParams.FLAG_SECURE to the hosting Activity's window
 * when the composable enters the composition and removes it when it leaves.
 */
@Composable
fun PreventScreenshots() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
