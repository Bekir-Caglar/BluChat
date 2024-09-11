package com.bekircaglar.bluchat.domain.model

data class ChatRoom(
    var users: List<String>? = emptyList(),
    val chatId:String? = "",
    val chatName:String? = "",
    val chatImage:String? = "",
    val chatType:String? = "",
) {
}

