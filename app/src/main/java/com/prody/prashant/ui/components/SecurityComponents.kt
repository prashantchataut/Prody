package com.prody.prashant.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.prody.prashant.util.BiometricAuthenticator

/**
 * Extension to find FragmentActivity from Context.
 */
fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}

/**
 * Composable that prevents screenshots and screen recordings while active.
 */
@Composable
fun PreventScreenshots() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context.findActivity() as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}

/**
 * Higher-order Composable that gates content behind biometric authentication.
 *
 * @param title Title for the biometric prompt
 * @param biometricAuthenticator Injected authenticator
 * @param onAuthenticated Callback when authentication is successful
 * @param content The content to display when authenticated
 */
@Composable
fun RequireBiometricAuth(
    title: String,
    biometricAuthenticator: BiometricAuthenticator,
    onAuthenticated: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var isAuthenticated by rememberSaveable {
        // Initial state is authenticated if device cannot authenticate,
        // preventing lockout on unsecured devices.
        mutableStateOf(!biometricAuthenticator.canAuthenticate())
    }

    val context = LocalContext.current

    if (isAuthenticated) {
        content()
    } else {
        PrivacyLockScreen(
            title = title,
            onUnlockClick = {
                val activity = context.findActivity()
                if (activity != null) {
                    biometricAuthenticator.authenticate(activity, title) { success ->
                        if (success) {
                            isAuthenticated = true
                            onAuthenticated()
                        }
                    }
                }
            }
        )
    }
}
