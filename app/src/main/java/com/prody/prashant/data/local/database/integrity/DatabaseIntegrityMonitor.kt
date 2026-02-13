package com.prody.prashant.data.local.database.integrity

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DatabaseHealthStatus {
    UNKNOWN,
    VERIFYING,
    HEALTHY,
    DEGRADED
}

data class DatabaseHealthSnapshot(
    val status: DatabaseHealthStatus = DatabaseHealthStatus.UNKNOWN,
    val checkedAtMillis: Long = 0L,
    val degradedMode: Boolean = false,
    val userMessage: String? = null
)

object DatabaseIntegrityMonitor {
    private const val PREF_NAME = "database_health_cache"
    private const val KEY_STATUS = "status"
    private const val KEY_CHECKED_AT = "checked_at"
    private const val KEY_DEGRADED_MODE = "degraded_mode"
    private const val KEY_USER_MESSAGE = "user_message"

    private val healthState = MutableStateFlow(DatabaseHealthSnapshot())
    val health: StateFlow<DatabaseHealthSnapshot> = healthState.asStateFlow()

    fun initialize(context: Context) {
        val prefs = prefs(context)
        healthState.value = DatabaseHealthSnapshot(
            status = runCatching {
                DatabaseHealthStatus.valueOf(
                    prefs.getString(KEY_STATUS, DatabaseHealthStatus.UNKNOWN.name)
                        ?: DatabaseHealthStatus.UNKNOWN.name
                )
            }.getOrDefault(DatabaseHealthStatus.UNKNOWN),
            checkedAtMillis = prefs.getLong(KEY_CHECKED_AT, 0L),
            degradedMode = prefs.getBoolean(KEY_DEGRADED_MODE, false),
            userMessage = prefs.getString(KEY_USER_MESSAGE, null)
        )
    }

    fun markVerifying(context: Context) {
        persist(
            context,
            healthState.value.copy(
                status = DatabaseHealthStatus.VERIFYING,
                userMessage = null
            )
        )
    }

    fun markHealthy(context: Context, checkedAtMillis: Long = System.currentTimeMillis()) {
        persist(
            context,
            DatabaseHealthSnapshot(
                status = DatabaseHealthStatus.HEALTHY,
                checkedAtMillis = checkedAtMillis,
                degradedMode = false,
                userMessage = null
            )
        )
    }

    fun markDegraded(
        context: Context,
        checkedAtMillis: Long = System.currentTimeMillis(),
        userMessage: String = "We found a local data issue. Prody is running in safe mode while we recover your data."
    ) {
        persist(
            context,
            DatabaseHealthSnapshot(
                status = DatabaseHealthStatus.DEGRADED,
                checkedAtMillis = checkedAtMillis,
                degradedMode = true,
                userMessage = userMessage
            )
        )
    }

    private fun persist(context: Context, snapshot: DatabaseHealthSnapshot) {
        healthState.value = snapshot
        prefs(context).edit()
            .putString(KEY_STATUS, snapshot.status.name)
            .putLong(KEY_CHECKED_AT, snapshot.checkedAtMillis)
            .putBoolean(KEY_DEGRADED_MODE, snapshot.degradedMode)
            .putString(KEY_USER_MESSAGE, snapshot.userMessage)
            .apply()
    }

    private fun prefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}
