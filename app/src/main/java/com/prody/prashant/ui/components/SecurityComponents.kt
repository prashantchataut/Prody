package com.prody.prashant.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * A Composable utility that prevents screenshots and screen recordings for the current window.
 * It adds the [WindowManager.LayoutParams.FLAG_SECURE] flag when the Composable enters the composition
 * and removes it when it leaves.
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
