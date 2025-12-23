package com.prody.prashant.domain.gamification

import android.util.Log
import com.prody.prashant.data.local.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WisdomShield - Streak Protection System
 *
 * At 7+ days streak, users earn a "Wisdom Shield" that protects their streak
 * from a single missed day. This reduces anxiety around streaks while still
 * encouraging consistent engagement.
 *
 * Shield Mechanics:
 * - Unlocked at 7-day streak
 * - Protects ONE missed day
 * - Auto-activates when user misses a day
 * - Regenerates after 7 consecutive days post-use
 * - Visual indicator shows shield status
 *
 * This creates a psychological safety net that actually increases engagement
 * because users feel less pressured and more willing to maintain streaks.
 */
@Singleton
class WisdomShield @Inject constructor(
    private val userDao: UserDao
) {
    companion object {
        private const val TAG = "WisdomShield"
        const val SHIELD_UNLOCK_STREAK = 7
        const val SHIELD_REGENERATION_DAYS = 7
    }

    /**
     * Shield status for the user
     */
    data class ShieldStatus(
        val hasShield: Boolean,
        val isActive: Boolean,
        val daysUntilRegeneration: Int,
        val shieldUsedDate: Long?,
        val shieldEarnedAt: Int // Streak count when shield was earned
    )

    /**
     * Checks if user has earned a Wisdom Shield.
     * Shield is earned at 7+ day streak.
     */
    suspend fun getShieldStatus(): ShieldStatus = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync() ?: return@withContext ShieldStatus(
                hasShield = false,
                isActive = false,
                daysUntilRegeneration = SHIELD_UNLOCK_STREAK,
                shieldUsedDate = null,
                shieldEarnedAt = 0
            )

            val prefs = try {
                JSONObject(profile.preferences)
            } catch (e: Exception) {
                JSONObject()
            }

            val hasShield = prefs.optBoolean("wisdomShieldActive", false)
            val shieldUsedDate = prefs.optLong("wisdomShieldUsedDate", 0)
            val shieldEarnedAt = prefs.optInt("wisdomShieldEarnedAt", 0)
            val currentStreak = profile.currentStreak

            // Calculate days until regeneration if shield was used
            val daysUntilRegeneration = if (shieldUsedDate > 0 && !hasShield) {
                val daysSinceUse = ((System.currentTimeMillis() - shieldUsedDate) / (24 * 60 * 60 * 1000)).toInt()
                (SHIELD_REGENERATION_DAYS - daysSinceUse).coerceAtLeast(0)
            } else if (!hasShield && currentStreak < SHIELD_UNLOCK_STREAK) {
                SHIELD_UNLOCK_STREAK - currentStreak
            } else {
                0
            }

            ShieldStatus(
                hasShield = hasShield,
                isActive = hasShield && currentStreak >= SHIELD_UNLOCK_STREAK,
                daysUntilRegeneration = daysUntilRegeneration,
                shieldUsedDate = if (shieldUsedDate > 0) shieldUsedDate else null,
                shieldEarnedAt = shieldEarnedAt
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting shield status", e)
            ShieldStatus(
                hasShield = false,
                isActive = false,
                daysUntilRegeneration = SHIELD_UNLOCK_STREAK,
                shieldUsedDate = null,
                shieldEarnedAt = 0
            )
        }
    }

    /**
     * Earns a Wisdom Shield when streak reaches 7 days.
     * Called automatically by streak tracking system.
     */
    suspend fun earnShield(currentStreak: Int): Boolean = withContext(Dispatchers.IO) {
        if (currentStreak < SHIELD_UNLOCK_STREAK) return@withContext false

        try {
            val profile = userDao.getUserProfileSync() ?: return@withContext false
            val prefs = try {
                JSONObject(profile.preferences)
            } catch (e: Exception) {
                JSONObject()
            }

            // Don't earn if already have shield
            if (prefs.optBoolean("wisdomShieldActive", false)) {
                return@withContext false
            }

            // Check if shield was recently used and needs regeneration
            val shieldUsedDate = prefs.optLong("wisdomShieldUsedDate", 0)
            if (shieldUsedDate > 0) {
                val daysSinceUse = ((System.currentTimeMillis() - shieldUsedDate) / (24 * 60 * 60 * 1000)).toInt()
                if (daysSinceUse < SHIELD_REGENERATION_DAYS) {
                    return@withContext false // Still regenerating
                }
            }

            // Earn the shield
            prefs.put("wisdomShieldActive", true)
            prefs.put("wisdomShieldEarnedAt", currentStreak)
            prefs.put("wisdomShieldEarnedDate", System.currentTimeMillis())

            userDao.updatePreferences(prefs.toString())
            Log.d(TAG, "Wisdom Shield earned at $currentStreak day streak!")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error earning shield", e)
            false
        }
    }

    /**
     * Uses the Wisdom Shield to protect a streak.
     * Called when user would have lost their streak.
     *
     * @return true if shield was used successfully
     */
    suspend fun useShield(): Boolean = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync() ?: return@withContext false
            val prefs = try {
                JSONObject(profile.preferences)
            } catch (e: Exception) {
                JSONObject()
            }

            // Check if user has an active shield
            if (!prefs.optBoolean("wisdomShieldActive", false)) {
                return@withContext false
            }

            // Use the shield
            prefs.put("wisdomShieldActive", false)
            prefs.put("wisdomShieldUsedDate", System.currentTimeMillis())

            userDao.updatePreferences(prefs.toString())
            Log.d(TAG, "Wisdom Shield used to protect streak!")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error using shield", e)
            false
        }
    }

    /**
     * Checks if shield should be used to protect streak.
     * Called during daily check-in when streak would be lost.
     *
     * @param lastActivityDate Last time user was active
     * @return true if shield protected the streak
     */
    suspend fun checkAndProtectStreak(lastActivityDate: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val daysSinceLastActivity = ((System.currentTimeMillis() - lastActivityDate) / (24 * 60 * 60 * 1000)).toInt()

            // Only protect for exactly 1 missed day
            if (daysSinceLastActivity != 1) {
                return@withContext false
            }

            val status = getShieldStatus()
            if (status.hasShield && status.isActive) {
                return@withContext useShield()
            }

            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking shield protection", e)
            false
        }
    }

    /**
     * Regenerates shield if conditions are met.
     * Called during streak update when conditions might allow regeneration.
     */
    suspend fun checkAndRegenerateShield(currentStreak: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val profile = userDao.getUserProfileSync() ?: return@withContext false
            val prefs = try {
                JSONObject(profile.preferences)
            } catch (e: Exception) {
                JSONObject()
            }

            // Already have shield
            if (prefs.optBoolean("wisdomShieldActive", false)) {
                return@withContext false
            }

            val shieldUsedDate = prefs.optLong("wisdomShieldUsedDate", 0)
            if (shieldUsedDate == 0L) {
                // Never used a shield, check if eligible to earn first one
                return@withContext earnShield(currentStreak)
            }

            val daysSinceUse = ((System.currentTimeMillis() - shieldUsedDate) / (24 * 60 * 60 * 1000)).toInt()
            if (daysSinceUse >= SHIELD_REGENERATION_DAYS && currentStreak >= SHIELD_UNLOCK_STREAK) {
                // Regenerate shield
                prefs.put("wisdomShieldActive", true)
                prefs.put("wisdomShieldEarnedAt", currentStreak)
                prefs.put("wisdomShieldEarnedDate", System.currentTimeMillis())

                userDao.updatePreferences(prefs.toString())
                Log.d(TAG, "Wisdom Shield regenerated!")
                return@withContext true
            }

            false
        } catch (e: Exception) {
            Log.e(TAG, "Error regenerating shield", e)
            false
        }
    }
}
