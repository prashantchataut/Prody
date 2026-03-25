package com.prody.prashant.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.util.BiometricAuthResult
import com.prody.prashant.util.BiometricAuthenticator

/**
 * Security: Prevents screenshots and screen recordings for the current screen.
 *
 * Should be used at the top level of any Composable that displays sensitive data.
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

/**
 * Security: Gated content that requires biometric authentication if enabled.
 *
 * @param lockEnabled Whether the biometric lock is enabled by the user.
 * @param biometricAuthenticator The authenticator utility.
 * @param title Title for the lock screen and biometric prompt.
 * @param subtitle Subtitle for the lock screen and biometric prompt.
 * @param content The sensitive content to display after authentication.
 */
@Composable
fun RequireBiometricAuth(
    lockEnabled: Boolean,
    biometricAuthenticator: BiometricAuthenticator,
    title: String = "Private Content",
    subtitle: String = "Use biometrics to unlock",
    content: @Composable () -> Unit
) {
    if (!lockEnabled || !biometricAuthenticator.canAuthenticate()) {
        content()
        return
    }

    var isAuthenticated by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    if (isAuthenticated) {
        content()
    } else {
        // Trigger authentication on first launch if not authenticated
        LaunchedEffect(Unit) {
            activity?.let {
                biometricAuthenticator.authenticate(it, title, subtitle) { result ->
                    if (result is BiometricAuthResult.Success) {
                        isAuthenticated = true
                    }
                }
            }
        }

        // Lock Screen UI
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ProdyIcons.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        activity?.let {
                            biometricAuthenticator.authenticate(it, title, subtitle) { result ->
                                if (result is BiometricAuthResult.Success) {
                                    isAuthenticated = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = ProdyIcons.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unlock Content")
                }
            }
        }
    }
}
