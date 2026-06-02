package com.prody.prashant.data.auth

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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    companion object {
        private const val TAG = "AuthRepository"
        const val WEB_CLIENT_ID = "281162417181-jr863kuiqtqc142roqdq96fem0obri63.apps.googleusercontent.com"
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: FirebaseUser? get() = firebaseAuth.currentUser
    val currentUserId: String? get() = currentUser?.uid
    val isSignedIn: Boolean get() = currentUser != null

    private var googleSignInClient: GoogleSignInClient? = null

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

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _authState.value = if (user != null) {
            AuthState.Authenticated(
                userId = user.uid,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString(),
                isAnonymous = user.isAnonymous
            )
        } else {
            AuthState.Unauthenticated
        }
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    fun removeListener() {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            _authState.value = AuthState.Loading
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val userId = result.user?.uid
                ?: return Result.failure(Exception("Authentication failed: no user ID"))
            Log.i(TAG, "Google sign-in successful: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed", e)
            _authState.value = AuthState.Error(e.message ?: "Sign-in failed")
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            googleSignInClient?.signOut()
            Log.i(TAG, "Sign-out successful")
        } catch (e: Exception) {
            Log.e(TAG, "Sign-out failed", e)
        }
    }

    fun getUserId(): String {
        return currentUser?.uid ?: "local"
    }
}