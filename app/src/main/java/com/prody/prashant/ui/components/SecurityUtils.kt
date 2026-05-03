package com.prody.prashant.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * SecureScreen - Centralized utility to protect sensitive screens from
 * screenshots and screen recordings.
 *
 * Use this composable at the top level of any screen containing private user data
 * (Journals, Future Messages, Haven Chats, etc.) to apply FLAG_SECURE to the window.
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
