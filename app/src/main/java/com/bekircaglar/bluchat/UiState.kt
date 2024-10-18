package com.bekircaglar.bluchat

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String? = null) : UiState()
    data class Error(val message: String? = null) : UiState()
}

enum class UiEvent {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}