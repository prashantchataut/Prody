package com.prody.prashant.util

import java.util.UUID

/**
 * A singleton object to manage secure, one-time tokens (nonces) for internal navigation.
 * This prevents malicious apps from spoofing intents to navigate to sensitive screens.
 */
object NavigationTokenManager {

    private var currentToken: String? = null

    /**
     * Generates a new, unique token and stores it.
     * @return The generated token.
     */
    @Synchronized
    fun generateToken(): String {
        val token = UUID.randomUUID().toString()
        currentToken = token
        return token
    }

    /**
     * Validates the given token against the currently stored token.
     * If the token is valid, it is consumed and cannot be used again.
     *
     * @param token The token to validate.
     * @return `true` if the token is valid, `false` otherwise.
     */
    @Synchronized
    fun isTokenValid(token: String?): Boolean {
        if (token == null || token != currentToken) {
            return false
        }
        // Consume the token after validation
        currentToken = null
        return true
    }
}
