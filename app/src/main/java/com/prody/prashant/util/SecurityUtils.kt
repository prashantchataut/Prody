package com.prody.prashant.util

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Security: Centralized UI privacy utilities for Prody.
 */

/**
 * A composable that protects highly sensitive user data from screenshots and screen recordings.
 *
 * It applies [WindowManager.LayoutParams.FLAG_SECURE] to the current window while the
 * composable is in the composition and clears it when it leaves.
 *
 * Use this on screens displaying:
 * - User journals and personal reflections
 * - Future messages (time capsules)
 * - Therapeutic chats and exercises (Haven)
 * - Weekly/Monthly growth summaries
 * - Evidence locker content
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
