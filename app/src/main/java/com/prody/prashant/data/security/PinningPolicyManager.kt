package com.prody.prashant.data.security

import android.util.Log
import com.prody.prashant.BuildConfig
import com.prody.prashant.data.monitoring.PerformanceMonitor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_REFRESH_INTERVAL_MS = 6 * 60 * 60 * 1000L
private const val FAIL_OPEN_FALLBACK_TTL_MS = 24 * 60 * 60 * 1000L

enum class FallbackTransportMode {
    NONE,
    SYSTEM_CA_ONLY
}

data class PinningTransportPolicy(
    val pinningEnabled: Boolean = true,
    val fallbackTransportMode: FallbackTransportMode = FallbackTransportMode.NONE,
    val allowAutomaticFallbackOnPinFailure: Boolean = false,
    val reason: String = "default"
)

@Serializable
private data class RemotePinningPolicy(
    @SerialName("pinning_enabled") val pinningEnabled: Boolean? = null,
    @SerialName("fallback_transport_mode") val fallbackTransportMode: String? = null,
    @SerialName("allow_automatic_fallback_on_pin_failure") val allowAutomaticFallbackOnPinFailure: Boolean? = null,
    @SerialName("reason") val reason: String? = null
)

@Singleton
class PinningPolicyManager @Inject constructor(
    private val performanceMonitor: PerformanceMonitor
) {
    companion object {
        private const val TAG = "PinningPolicyManager"
    }

    private val policyHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var currentPolicy = PinningTransportPolicy()

    private val lastRefreshEpochMs = AtomicLong(0)
    private val fallbackUntilEpochMs = AtomicLong(0)

    fun getCurrentPolicy(): PinningTransportPolicy {
        val fallbackActive = System.currentTimeMillis() < fallbackUntilEpochMs.get()
        return if (fallbackActive) {
            currentPolicy.copy(
                pinningEnabled = false,
                fallbackTransportMode = FallbackTransportMode.SYSTEM_CA_ONLY,
                reason = "temporary_fail_open_for_pin_expiry"
            )
        } else {
            currentPolicy
        }
    }

    suspend fun refreshPolicyIfNeeded(force: Boolean = false): PinningTransportPolicy {
        val now = System.currentTimeMillis()
        if (!force && now - lastRefreshEpochMs.get() < DEFAULT_REFRESH_INTERVAL_MS) {
            return getCurrentPolicy()
        }

        return runCatching {
            val request = Request.Builder()
                .url(BuildConfig.PINNING_POLICY_URL)
                .get()
                .build()

            policyHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Policy endpoint returned HTTP ${response.code}")
                }

                val body = response.body?.string().orEmpty()
                val remotePolicy = json.decodeFromString(RemotePinningPolicy.serializer(), body)

                val mappedPolicy = PinningTransportPolicy(
                    pinningEnabled = remotePolicy.pinningEnabled ?: true,
                    fallbackTransportMode = when (remotePolicy.fallbackTransportMode?.uppercase()) {
                        "SYSTEM_CA_ONLY" -> FallbackTransportMode.SYSTEM_CA_ONLY
                        else -> FallbackTransportMode.NONE
                    },
                    allowAutomaticFallbackOnPinFailure =
                        remotePolicy.allowAutomaticFallbackOnPinFailure ?: false,
                    reason = remotePolicy.reason ?: "remote_policy"
                )

                currentPolicy = mappedPolicy
                lastRefreshEpochMs.set(now)
                mappedPolicy
            }
        }.onFailure { error ->
            performanceMonitor.recordError(
                message = "Unable to refresh pinning policy",
                exception = error,
                context = "network_security"
            )
            Log.w(TAG, "Falling back to cached pinning policy", error)
        }.getOrElse { getCurrentPolicy() }
    }

    fun activateTemporaryFallback(reason: String) {
        fallbackUntilEpochMs.set(System.currentTimeMillis() + FAIL_OPEN_FALLBACK_TTL_MS)
        performanceMonitor.recordError(
            message = "Pinning kill-switch activated",
            context = "network_security:$reason"
        )
        Log.w(TAG, "Temporary fallback transport enabled: $reason")
    }
}
