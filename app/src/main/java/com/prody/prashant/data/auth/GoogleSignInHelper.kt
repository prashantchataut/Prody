package com.prody.prashant.data.auth

import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

object GoogleSignInHelper {

    private const val TAG = "GoogleSignInHelper"

    fun getSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(AuthRepository.WEB_CLIENT_ID)
            .requestEmail()
            .build()
    }

    fun getIdTokenFromResult(data: Intent?): String? {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            account.idToken
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign-in failed with status code ${e.statusCode}: ${e.message}", e)
            null
        }
    }
}