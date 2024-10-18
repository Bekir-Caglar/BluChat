package com.bekircaglar.bluchat

sealed class Response<out T> {
    object Loading : Response<Nothing>()
    object Idle : Response<Nothing>()

    data class Success<out T>(
        val data: T
    ) : Response<T>()

    data class Error(
        val message: String
    ) : Response<Nothing>()
}