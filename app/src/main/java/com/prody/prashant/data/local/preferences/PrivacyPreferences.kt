package com.prody.prashant.data.local.preferences

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

internal class PrivacyPreferences(
    private val support: PreferenceFacadeSupport,
    private val privacyLockJournalKey: Preferences.Key<Boolean>,
    private val privacyLockFutureMessagesKey: Preferences.Key<Boolean>,
    private val privacyLockOnBackgroundKey: Preferences.Key<Boolean>,
    private val privacyLastUnlockedAtKey: Preferences.Key<Long>
) {
    val privacyLockJournal: Flow<Boolean> = support.booleanFlow(privacyLockJournalKey, false)
    val privacyLockFutureMessages: Flow<Boolean> = support.booleanFlow(privacyLockFutureMessagesKey, false)
    val privacyLockOnBackground: Flow<Boolean> = support.booleanFlow(privacyLockOnBackgroundKey, true)
    val privacyLastUnlockedAt: Flow<Long> = support.longFlow(privacyLastUnlockedAtKey, 0L)

    suspend fun setPrivacyLockJournal(enabled: Boolean) = support.setBoolean(privacyLockJournalKey, enabled)
    suspend fun setPrivacyLockFutureMessages(enabled: Boolean) = support.setBoolean(privacyLockFutureMessagesKey, enabled)
    suspend fun setPrivacyLockOnBackground(enabled: Boolean) = support.setBoolean(privacyLockOnBackgroundKey, enabled)
    suspend fun updatePrivacyLastUnlockedAt(timestamp: Long) = support.setLong(privacyLastUnlockedAtKey, timestamp)
}
