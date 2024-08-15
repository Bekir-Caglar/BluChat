package com.bekircaglar.chatappbordo.domain.model

import androidx.compose.ui.graphics.painter.Painter

data class ChatData(
    val profileImage: Painter,
    val userName: String,
    val lastMessage: String,
    val messageTime: String,
    val unreadCount: Int,
    val isOnline: Boolean
)