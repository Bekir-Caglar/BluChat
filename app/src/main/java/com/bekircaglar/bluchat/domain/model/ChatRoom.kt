package com.bekircaglar.bluchat.domain.model

data class ChatRoom(
    val users: List<String>? = emptyList(),
    val chatId:String? = "",
    val chatName:String? = "",
    val chatImage:String? = "",
    val chatType:String? = "",
    val chatAdminId : String? = "",
    val chatLastMessageSenderId:String? = "",
    val chatLastMessage:String? = "",
    val chatLastMessageTime:String? = "",
) {
}

