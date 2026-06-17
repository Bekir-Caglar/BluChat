package com.bekircaglar.bluchat.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivityMonitor @Inject constructor(
    private val context: Context
) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private val _justConnected = MutableStateFlow(false)
    val justConnected: StateFlow<Boolean> = _justConnected.asStateFlow()
    
    private var wasConnected = false
    
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d("NetworkMonitor", "Network available")
            val wasDisconnected = !wasConnected
            _isConnected.value = true
            wasConnected = true
            
            // Emit justConnected only when transitioning from disconnected to connected
            if (wasDisconnected) {
                _justConnected.value = true
                // Reset the flag after a short delay
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    _justConnected.value = false
                }, 1000)
            }
        }
        
        override fun onLost(network: Network) {
            Log.d("NetworkMonitor", "Network lost")
            _isConnected.value = false
            wasConnected = false
        }
        
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            
            val wasDisconnected = !wasConnected
            _isConnected.value = hasInternet
            
            if (hasInternet && wasDisconnected) {
                wasConnected = true
                _justConnected.value = true
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    _justConnected.value = false
                }, 1000)
            } else if (!hasInternet) {
                wasConnected = false
            }
        }
    }
    
    fun startMonitoring() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        
        // Set initial state
        _isConnected.value = isInternetAvailable()
        wasConnected = _isConnected.value
    }
    
    fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
    
    private fun isInternetAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}