package com.prody.prashant.util

import com.prody.prashant.data.local.preferences.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for handling Privacy Mode lock state.
 * Tracks whether Journal and Future Messages sections require authentication.
 */
@Singleton
class PrivacyLockManager @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    // Session-based unlock state (resets when app is killed)
    @Volatile
    private var isJournalUnlockedThisSession = false

    @Volatile
    private var isFutureMessagesUnlockedThisSession = false

    // Lock timeout in milliseconds (5 minutes)
    private val lockTimeoutMs = 5 * 60 * 1000L

    /**
     * Check if journal access requires authentication.
     */
    suspend fun isJournalLocked(): Boolean {
        val lockEnabled = preferencesManager.privacyLockJournal.first()
        if (!lockEnabled) return false

        // Check session unlock
        if (isJournalUnlockedThisSession) {
            // Verify timeout hasn't expired
            val lastUnlock = preferencesManager.privacyLastUnlockedAt.first()
            val now = System.currentTimeMillis()
            if (now - lastUnlock < lockTimeoutMs) {
                return false
            }
            // Timeout expired, re-lock
            isJournalUnlockedThisSession = false
        }
        return true
    }

    /**
     * Check if future messages access requires authentication.
     */
    suspend fun isFutureMessagesLocked(): Boolean {
        val lockEnabled = preferencesManager.privacyLockFutureMessages.first()
        if (!lockEnabled) return false

        // Check session unlock
        if (isFutureMessagesUnlockedThisSession) {
            // Verify timeout hasn't expired
            val lastUnlock = preferencesManager.privacyLastUnlockedAt.first()
            val now = System.currentTimeMillis()
            if (now - lastUnlock < lockTimeoutMs) {
                return false
            }
            // Timeout expired, re-lock
            isFutureMessagesUnlockedThisSession = false
        }
        return true
    }

    /**
     * Flow to observe journal lock state.
     */
    val journalLockEnabled: Flow<Boolean> = preferencesManager.privacyLockJournal

    /**
     * Flow to observe future messages lock state.
     */
    val futureMessagesLockEnabled: Flow<Boolean> = preferencesManager.privacyLockFutureMessages

    /**
     * Flow to observe if any privacy lock is enabled.
     */
    val anyPrivacyLockEnabled: Flow<Boolean> = combine(
        preferencesManager.privacyLockJournal,
        preferencesManager.privacyLockFutureMessages
    ) { journal, future ->
        journal || future
    }

    /**
     * Mark journal as unlocked for this session.
     */
    suspend fun unlockJournal() {
        isJournalUnlockedThisSession = true
        preferencesManager.setPrivacyLastUnlockedAt(System.currentTimeMillis())
    }

    /**
     * Mark future messages as unlocked for this session.
     */
    suspend fun unlockFutureMessages() {
        isFutureMessagesUnlockedThisSession = true
        preferencesManager.setPrivacyLastUnlockedAt(System.currentTimeMillis())
    }

    /**
     * Mark both as unlocked (single authentication for both).
     */
    suspend fun unlockAll() {
        val now = System.currentTimeMillis()
        isJournalUnlockedThisSession = true
        isFutureMessagesUnlockedThisSession = true
        preferencesManager.setPrivacyLastUnlockedAt(now)
    }

    /**
     * Re-lock all sections (called when app goes to background if enabled).
     */
    fun lockAll() {
        isJournalUnlockedThisSession = false
        isFutureMessagesUnlockedThisSession = false
    }

    /**
     * Called when app goes to background.
     * Will re-lock if lockOnBackground is enabled.
     */
    suspend fun onAppBackground() {
        if (preferencesManager.privacyLockOnBackground.first()) {
            lockAll()
        }
    }

    /**
     * Check if lockOnBackground is enabled.
     */
    suspend fun isLockOnBackgroundEnabled(): Boolean {
        return preferencesManager.privacyLockOnBackground.first()
    }
}
