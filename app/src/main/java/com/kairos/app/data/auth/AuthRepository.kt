package com.kairos.app.data.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext context: Context
) {
    companion object {
        private const val TAG = "AuthRepository"
        const val WEB_CLIENT_ID = "281162417181-jr863kuiqtqc142roqdq96fem0obri63.apps.googleusercontent.com"
        private const val SESSION_PREFS = "Kairos_auth_session"
        private const val LOCAL_SESSION_KEY = "local_session_enabled"
    }

    private val sessionPreferences = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: FirebaseUser? get() = firebaseAuth.currentUser
    val currentUserId: String? get() = currentUser?.uid
    val isSignedIn: Boolean get() = currentUser != null

    private var googleSignInClient: GoogleSignInClient? = null
    private var isListenerAttached = false

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _authState.value = when {
            user != null -> AuthState.Authenticated(
                userId = user.uid,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString(),
                isAnonymous = user.isAnonymous
            )
            hasLocalSession() -> localAuthState()
            else -> AuthState.Unauthenticated
        }
    }

    init {
        try {
            firebaseAuth.addAuthStateListener(authStateListener)
            isListenerAttached = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach auth state listener", e)
            _authState.value = if (hasLocalSession()) {
                localAuthState()
            } else {
                AuthState.Error(e.message ?: "Account services are unavailable")
            }
        }
    }

    fun removeListener() {
        if (isListenerAttached) {
            try {
                firebaseAuth.removeAuthStateListener(authStateListener)
                isListenerAttached = false
            } catch (e: Exception) {
                Log.w(TAG, "Failed to remove auth state listener", e)
            }
        }
    }

    fun getGoogleSignInClient(context: android.content.Context): GoogleSignInClient {
        return googleSignInClient ?: GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
            .let { GoogleSignIn.getClient(context, it) }
            .also { googleSignInClient = it }
    }

    fun getGoogleSignInIntent(context: android.content.Context): Intent? {
        return getGoogleSignInClient(context).signInIntent
    }

    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            _authState.value = AuthState.Loading
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val userId = result.user?.uid
                ?: return Result.failure(Exception("Authentication failed: no user ID"))
            sessionPreferences.edit().putBoolean(LOCAL_SESSION_KEY, false).apply()
            Log.i(TAG, "Google sign-in successful: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed", e)
            _authState.value = AuthState.Error(e.message ?: "Sign-in failed")
            Result.failure(e)
        }
    }

    fun continueLocally() {
        sessionPreferences.edit().putBoolean(LOCAL_SESSION_KEY, true).apply()
        _authState.value = localAuthState()
    }

    suspend fun signOut() {
        sessionPreferences.edit().putBoolean(LOCAL_SESSION_KEY, false).apply()
        try {
            firebaseAuth.signOut()
            googleSignInClient?.signOut()
            _authState.value = AuthState.Unauthenticated
            Log.i(TAG, "Sign-out successful")
        } catch (e: Exception) {
            Log.e(TAG, "Sign-out failed", e)
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun getUserId(): String {
        return currentUser?.uid ?: "local"
    }

    private fun hasLocalSession(): Boolean =
        sessionPreferences.getBoolean(LOCAL_SESSION_KEY, false)

    private fun localAuthState(): AuthState.Authenticated = AuthState.Authenticated(
        userId = "local",
        email = null,
        displayName = "Local profile",
        photoUrl = null,
        isAnonymous = true
    )
}