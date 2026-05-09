package com.prody.prashant.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import java.util.concurrent.atomic.AtomicInteger

/**
 * Global reference counter for FLAG_SECURE.
 * Since FLAG_SECURE is activity-wide, we use reference counting to ensure it remains active
 * if at least one "secure" screen is currently in the composition.
 * This prevents the flag from being cleared when navigating between two secure screens.
 */
private val secureScreenCount = AtomicInteger(0)

/**
 * A utility component that applies WindowManager.LayoutParams.FLAG_SECURE to the current window.
 * This prevents screenshots, screen recordings, and obscures the app's content in the
 * recent apps switcher.
 *
 * Use this wrapper around screens that display or capture sensitive user data.
 */
@Composable
fun SecureScreen() {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window

        if (window != null) {
            val count = secureScreenCount.incrementAndGet()
            // Always set the flag when a secure screen enters composition
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

            android.util.Log.d("SecureScreen", "Secure screen entered. Total active: $count")
        }

        onDispose {
            if (window != null) {
                val count = secureScreenCount.decrementAndGet()
                // Only clear the flag if no more secure screens are in composition
                if (count <= 0) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    android.util.Log.d("SecureScreen", "All secure screens disposed. Clearing FLAG_SECURE.")
                } else {
                    android.util.Log.d("SecureScreen", "Secure screen disposed. $count still remaining.")
                }
            }
        }
    }
}
