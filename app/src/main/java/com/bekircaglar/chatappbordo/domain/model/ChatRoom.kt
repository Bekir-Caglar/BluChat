package com.bekircaglar.chatappbordo.domain.model

data class ChatRoom(
    var users: List<String>? = emptyList(),
    val chatId:String? = ""
) {
}

