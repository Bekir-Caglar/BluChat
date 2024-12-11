package com.bekircaglar.bluchat.domain.model

data class Chats(
    val chatRoomId: String = "",
    val name : String = "",
    val surname : String = "",
    val imageUrl : String = "",
    val lastMessageSenderId : String? = null,
    val lastMessage: String? = null,
    val messageTime: Long? = 0L,
    val isOnline: Boolean = false,
) {
}