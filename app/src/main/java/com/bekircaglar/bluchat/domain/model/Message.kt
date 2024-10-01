package com.bekircaglar.bluchat.domain.model

data class Message(
    val messageId:String? = "",
    val senderId:String? = "",
    val message:String? = "",
    val timestamp: Long? = 0,
    val isRead:Boolean? = false,
    val messageType : String? = "",
    val imageUrl: String? = "",
    val edited : Boolean? = false,
    val pinned : Boolean? = false
)