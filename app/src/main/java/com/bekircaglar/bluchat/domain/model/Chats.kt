package com.bekircaglar.bluchat.domain.model

data class Chats(
    var chatRoomId: String = "",
    var name : String = "",
    var surname : String = "",
    var imageUrl : String = "",
    var lastMessage: String? = null,
    var messageTime: String? = null,
    var isOnline: Boolean = false,

) {
}