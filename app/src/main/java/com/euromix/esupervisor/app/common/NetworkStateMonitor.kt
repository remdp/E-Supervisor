package com.euromix.esupervisor.app.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat.getSystemService

class NetworkStateMonitor(context: Context, private val stateListener: (display: Boolean) -> Unit) {

    private val connectivityManager: ConnectivityManager =
        getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager

    private val request = NetworkRequest.Builder().build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            stateListener(!isOnline(network))
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            stateListener(!isOnline(network))
        }
    }

    private fun isOnline(network: Network): Boolean {

        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return if (capabilities != null) {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        } else false
    }

    fun registerNetworkCallback() {
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
