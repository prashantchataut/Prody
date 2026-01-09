package com.prody.prashant.data.local.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartupPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }

    /**
     * Reads the onboarding completed flag synchronously.
     * Defaults to false if not found.
     */
    fun getOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    /**
     * Writes the onboarding completed flag synchronously.
     */
    fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }
}
