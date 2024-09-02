package com.bekircaglar.chatappbordo.domain.model

data class Message(
    val senderId:String? = "",
    val message:String? = "",
    val timestamp: Long? = 0,
    val isRead:Boolean? = false
)