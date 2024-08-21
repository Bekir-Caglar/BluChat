package com.bekircaglar.chatappbordo.domain.model

data class Users(
    val uid: String= "",
    val name: String=  "",
    val email: String= "",
    val phoneNumber: String= "",
    val profileImageUrl: String= "",
    val status: Boolean = false,
    val lastSeen: String = "",
)
