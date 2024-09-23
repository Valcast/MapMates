package com.example.socialmeetingapp.data.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class NetworkManager(private val connectivityManager: ConnectivityManager) {

    private var _isConnected: Boolean = false
    val isConnected: Boolean
        get() = _isConnected

    private val networkRequest = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()

    init {
        registerCallback()
    }

    private fun registerCallback(){
        connectivityManager.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback () {
                override fun onAvailable(network: android.net.Network) {
                    super.onAvailable(network)
                    _isConnected = true
                }

                override fun onLost(network: android.net.Network) {
                    super.onLost(network)
                    _isConnected = false
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    _isConnected = false
                }
            }
        )


    }


}