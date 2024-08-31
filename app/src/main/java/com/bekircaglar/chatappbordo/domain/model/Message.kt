package com.bekircaglar.chatappbordo.domain.model

data class Message(
    var senderId:String,
    var message:String,
    var timestamp:String,
    var isRead:Boolean
)
