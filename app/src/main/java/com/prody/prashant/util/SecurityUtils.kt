package com.prody.prashant.util

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * A Composable utility that secures the current screen by preventing screenshots
 * and screen recordings. It adds [WindowManager.LayoutParams.FLAG_SECURE] to
 * the window when the composable enters the composition and removes it when
 * it leaves.
 *
 * This is critical for screens displaying highly sensitive user data like
 * journal entries, therapeutic conversations, and private reflections.
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
