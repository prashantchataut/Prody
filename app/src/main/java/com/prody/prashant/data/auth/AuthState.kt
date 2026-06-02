package com.prody.prashant.data.auth

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Authenticated(
        val userId: String,
        val email: String?,
        val displayName: String?,
        val photoUrl: String?,
        val isAnonymous: Boolean
    ) : AuthState()
    data object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}