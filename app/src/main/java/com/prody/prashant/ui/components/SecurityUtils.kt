package com.prody.prashant.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * A Composable that secures the current screen by preventing screenshots and screen recordings.
 * It applies [WindowManager.LayoutParams.FLAG_SECURE] to the [Activity]'s window.
 *
 * This should be called at the top level of any screen that displays sensitive user data
 * such as journals, private messages, or personal locker content.
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
