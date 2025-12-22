package com.prody.prashant.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Network connectivity status
 */
enum class NetworkStatus {
    /** Device is connected to network */
    AVAILABLE,
    /** Device is disconnected from network */
    UNAVAILABLE,
    /** Network is losing connection */
    LOSING,
    /** Network connection was lost */
    LOST
}

/**
 * Network type classification
 */
enum class NetworkType {
    WIFI,
    CELLULAR,
    ETHERNET,
    UNKNOWN,
    NONE
}

/**
 * Comprehensive network state
 */
data class NetworkState(
    val status: NetworkStatus = NetworkStatus.UNAVAILABLE,
    val type: NetworkType = NetworkType.NONE,
    val isMetered: Boolean = true,
    val isRoaming: Boolean = false
) {
    val isConnected: Boolean
        get() = status == NetworkStatus.AVAILABLE

    val isWifi: Boolean
        get() = type == NetworkType.WIFI

    val shouldSync: Boolean
        get() = isConnected && !isRoaming && (!isMetered || type == NetworkType.WIFI)
}

/**
 * NetworkConnectivityManager provides real-time network connectivity monitoring.
 *
 * Features:
 * - Real-time network status updates via StateFlow
 * - Network type detection (WiFi, Cellular, etc.)
 * - Metered/roaming detection for smart sync decisions
 * - Offline-first design support
 *
 * Usage:
 * - Observe networkState for UI updates
 * - Check isOnline for quick connectivity checks
 * - Use shouldSync to determine if syncing is appropriate
 */
@Singleton
class NetworkConnectivityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "NetworkConnectivity"
    }

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkState = MutableStateFlow(getCurrentNetworkState())
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    /** Quick check for online status */
    val isOnline: Boolean
        get() = _networkState.value.isConnected

    /** Check if sync is recommended based on network conditions */
    val shouldSync: Boolean
        get() = _networkState.value.shouldSync

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d(TAG, "Network available")
            updateNetworkState(NetworkStatus.AVAILABLE)
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            Log.d(TAG, "Network losing, max ms to live: $maxMsToLive")
            updateNetworkState(NetworkStatus.LOSING)
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "Network lost")
            updateNetworkState(NetworkStatus.LOST)
        }

        override fun onUnavailable() {
            Log.d(TAG, "Network unavailable")
            updateNetworkState(NetworkStatus.UNAVAILABLE)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val type = getNetworkType(networkCapabilities)
            val isMetered = !networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_NOT_METERED
            )
            val isRoaming = !networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING
            )

            _networkState.value = NetworkState(
                status = NetworkStatus.AVAILABLE,
                type = type,
                isMetered = isMetered,
                isRoaming = isRoaming
            )

            Log.d(TAG, "Network capabilities changed: type=$type, metered=$isMetered, roaming=$isRoaming")
        }
    }

    init {
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        try {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            Log.d(TAG, "Network callback registered")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register network callback", e)
        }
    }

    private fun updateNetworkState(status: NetworkStatus) {
        val currentState = _networkState.value
        _networkState.value = currentState.copy(
            status = status,
            type = if (status == NetworkStatus.AVAILABLE) currentState.type else NetworkType.NONE
        )
    }

    private fun getCurrentNetworkState(): NetworkState {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }

        return if (capabilities != null) {
            NetworkState(
                status = NetworkStatus.AVAILABLE,
                type = getNetworkType(capabilities),
                isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED),
                isRoaming = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING)
            )
        } else {
            NetworkState()
        }
    }

    private fun getNetworkType(capabilities: NetworkCapabilities): NetworkType {
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            else -> NetworkType.UNKNOWN
        }
    }

    /**
     * Returns a Flow that emits only when the online/offline status changes.
     * Useful for showing connection status UI without excessive updates.
     */
    fun observeConnectivity(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Emit current state immediately
        trySend(isOnline)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    /**
     * Check if the current network is suitable for large data transfers.
     * Returns true if on WiFi or unmetered connection.
     */
    fun isSuitableForLargeTransfer(): Boolean {
        return networkState.value.let {
            it.isConnected && !it.isMetered
        }
    }

    /**
     * Cleanup when no longer needed.
     */
    fun unregister() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            Log.d(TAG, "Network callback unregistered")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unregister network callback", e)
        }
    }
}
