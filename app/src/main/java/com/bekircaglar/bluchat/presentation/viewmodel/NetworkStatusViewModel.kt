package com.bekircaglar.bluchat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.bekircaglar.bluchat.utils.network.NetworkConnectivityMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NetworkStatusViewModel @Inject constructor(
    private val networkConnectivityMonitor: NetworkConnectivityMonitor
) : ViewModel() {
    
    val isConnected: StateFlow<Boolean> = networkConnectivityMonitor.isConnected
    val justConnected: StateFlow<Boolean> = networkConnectivityMonitor.justConnected
    
    fun startMonitoring() {
        networkConnectivityMonitor.startMonitoring()
    }
    
    fun stopMonitoring() {
        networkConnectivityMonitor.stopMonitoring()
    }
    
    override fun onCleared() {
        super.onCleared()
        networkConnectivityMonitor.stopMonitoring()
    }
}