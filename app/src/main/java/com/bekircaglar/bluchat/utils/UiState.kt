package com.bekircaglar.bluchat.utils

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String? = null) : UiState()
    data class Error(val message: String? = null) : UiState()
}