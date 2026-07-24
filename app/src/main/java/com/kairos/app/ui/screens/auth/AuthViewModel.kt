package com.kairos.app.ui.screens.auth

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.app.data.auth.AuthRepository
import com.kairos.app.data.auth.AuthState
import com.kairos.app.data.auth.GoogleSignInHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState

    fun getGoogleSignInIntent(context: Context): Intent? {
        return authRepository.getGoogleSignInIntent(context)
    }

    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            val idToken = GoogleSignInHelper.getIdTokenFromResult(data)
            if (idToken != null) {
                authRepository.signInWithGoogle(idToken)
            }
        }
    }

    fun continueLocally() {
        authRepository.continueLocally()
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}