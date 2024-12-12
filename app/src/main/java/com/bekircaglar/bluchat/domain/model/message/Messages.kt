package com.bekircaglar.bluchat.domain.model.message

data class Messages(
    val id:String,
    var messages:List<Message>,
    var pinnedMessages : List<Message> = emptyList(),
    var starredMessages : List<Message> = emptyList()
)