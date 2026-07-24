package com.kairos.app.ui.security

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Prevents sensitive reflection and future-letter content from appearing in
 * screenshots, screen recordings, and the Android recent-apps snapshot.
 *
 * The flag is scoped to the composable lifetime so ordinary app screens remain
 * shareable. It also works through themed ContextWrappers used by Compose.
 */
@Composable
fun KairosSecureScreenEffect(enabled: Boolean = true) {
    val activity = LocalContext.current.findActivity()
    DisposableEffect(activity, enabled) {
        if (enabled) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        onDispose {
            if (enabled) {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
