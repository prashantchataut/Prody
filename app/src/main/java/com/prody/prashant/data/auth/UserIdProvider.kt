package com.prody.prashant.data.auth

import com.prody.prashant.data.local.preferences.PreferencesManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides the current user ID, bridging between the legacy "local" userId
 * and Firebase Auth UIDs.
 *
 * Strategy:
 * - If user is authenticated via Firebase, use their UID
 * - If not authenticated, use the existing "local" userId from PreferencesManager
 * - On first Firebase sign-in, the userId automatically switches to the Firebase UID
 * - No database migration needed — new data uses the Firebase UID, old data remains accessible
 *   via the "local" userId which is mapped in PreferencesManager
 */
@Singleton
class UserIdProvider @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager
) {
    companion object {
        private const val TAG = "UserIdProvider"
        private const val LOCAL_USER_ID = "local"
    }

    /**
     * Returns the current user ID.
     * - Authenticated users: Firebase UID
     * - Unauthenticated users: "local"
     *
     * This is the primary method to use throughout the app.
     */
    fun getCurrentUserId(): String {
        return authRepository.currentUserId ?: LOCAL_USER_ID
    }

    /**
     * Returns the suspend version for coroutine contexts.
     * Checks PreferencesManager first for the stored userId,
     * falls back to Firebase auth state.
     */
    suspend fun getUserId(): String {
        val firebaseUserId = authRepository.currentUserId
        if (firebaseUserId != null) {
            preferencesManager.setUserId(firebaseUserId)
            return firebaseUserId
        }

        val storedUserId = preferencesManager.userId.first()
        return storedUserId.ifBlank { LOCAL_USER_ID }
    }

    /**
     * Called after successful authentication to update the stored userId.
     * This ensures the app uses the Firebase UID consistently.
     */
    suspend fun onAuthenticated(firebaseUserId: String) {
        preferencesManager.setUserId(firebaseUserId)
    }
}