package com.bekircaglar.bluchat.presentation.components.network

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bekircaglar.bluchat.presentation.viewmodel.NetworkStatusViewModel

@Composable
fun NetworkStatusBar(
    viewModel: NetworkStatusViewModel = hiltViewModel()
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val justConnected by viewModel.justConnected.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.startMonitoring()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopMonitoring()
        }
    }
    
    // Show sync success message briefly when just connected
    if (justConnected) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Green.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✓ Connected - Syncing data...",
                    color = Color.Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
    
    // Show offline indicator when not connected
    if (!isConnected && !justConnected) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Orange.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠ Offline mode - Showing cached data",
                    color = Color.Orange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}