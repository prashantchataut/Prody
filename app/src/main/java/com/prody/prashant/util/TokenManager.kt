package com.prody.prashant.util

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

/**
 * A singleton object to manage one-time tokens for secure intent validation.
 * This helps prevent other apps from maliciously triggering navigation.
 *
 * This implementation uses SharedPreferences to persist tokens, making it robust
 * against application process death. It is also thread-safe.
 */
object TokenManager {

    private const val PREFS_NAME = "com.prody.prashant.widget.tokens"
    private const val PREFS_KEY_TOKENS = "valid_tokens"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Generates a new unique token and stores it persistently.
     * This method is thread-safe.
     *
     * @param context The context to access SharedPreferences.
     * @return The generated token.
     */
    @Synchronized
    fun generateToken(context: Context): String {
        val token = UUID.randomUUID().toString()
        val prefs = getPrefs(context)
        val tokens = prefs.getStringSet(PREFS_KEY_TOKENS, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        tokens.add(token)
        prefs.edit().putStringSet(PREFS_KEY_TOKENS, tokens).apply()
        return token
    }

    /**
     * Checks if a token is valid and consumes it from persistent storage.
     * This method is thread-safe.
     *
     * @param context The context to access SharedPreferences.
     * @param token The token to validate.
     * @return True if the token was valid, false otherwise.
     */
    @Synchronized
    fun isValidToken(context: Context, token: String?): Boolean {
        if (token == null) {
            return false
        }
        val prefs = getPrefs(context)
        val tokens = prefs.getStringSet(PREFS_KEY_TOKENS, mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        val isValid = tokens.remove(token)

        if (isValid) {
            // If the token was valid and removed, update the stored set.
            prefs.edit().putStringSet(PREFS_KEY_TOKENS, tokens).apply()
        }

        return isValid
    }
}
