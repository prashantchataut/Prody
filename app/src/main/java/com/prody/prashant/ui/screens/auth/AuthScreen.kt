package com.prody.prashant.ui.screens.auth

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.R
import com.prody.prashant.data.auth.AuthState
import com.prody.prashant.ui.theme.ProdyPrimary
import com.prody.prashant.ui.theme.PoppinsFamily

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> onAuthSuccess()
            is AuthState.Error -> {
                // Error is shown in UI
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Prody",
                modifier = Modifier.size(72.dp),
                tint = ProdyPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to Prody",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your mental wellness companion",
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = PoppinsFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(
                visible = authState is AuthState.Loading,
                enter = fadeIn()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = ProdyPrimary
                )
            }

            AnimatedVisibility(
                visible = authState !is AuthState.Loading,
                enter = slideInVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GoogleSignInButton(
                        onClick = {
                            val signInIntent = viewModel.getGoogleSignInIntent(context)
                            if (signInIntent != null) {
                                googleSignInLauncher.launch(signInIntent)
                            }
                        }
                    )

                    (authState as? AuthState.Error)?.message?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = PoppinsFamily,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google",
            modifier = Modifier.size(24.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Continue with Google",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    }
}