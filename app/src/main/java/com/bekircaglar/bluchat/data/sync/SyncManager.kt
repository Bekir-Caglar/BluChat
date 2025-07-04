package com.bekircaglar.bluchat.data.sync

import android.util.Log
import com.bekircaglar.bluchat.utils.network.NetworkConnectivityMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val dataSyncService: DataSyncService,
    private val networkConnectivityMonitor: NetworkConnectivityMonitor
) {
    
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val tag = "SyncManager"
    private var isInitialized = false
    
    fun initialize() {
        if (isInitialized) return
        
        Log.d(tag, "Initializing SyncManager")
        
        // Start monitoring network connectivity
        networkConnectivityMonitor.startMonitoring()
        
        // Listen for network connectivity changes
        networkConnectivityMonitor.justConnected
            .onEach { justConnected ->
                if (justConnected) {
                    Log.d(tag, "Device just connected to internet, triggering sync")
                    dataSyncService.syncAllData()
                }
            }
            .launchIn(syncScope)
        
        isInitialized = true
        Log.d(tag, "SyncManager initialized successfully")
    }
    
    fun cleanup() {
        Log.d(tag, "Cleaning up SyncManager")
        networkConnectivityMonitor.stopMonitoring()
        isInitialized = false
    }
    
    fun triggerManualSync() {
        Log.d(tag, "Manual sync triggered")
        dataSyncService.syncAllData()
    }
    
    fun clearLocalData() {
        Log.d(tag, "Clearing local data")
        dataSyncService.clearAllLocalData()
    }
}