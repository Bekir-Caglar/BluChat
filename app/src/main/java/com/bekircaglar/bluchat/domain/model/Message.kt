package com.bekircaglar.bluchat.domain.model

import com.google.firebase.database.Exclude

data class Message(
    val messageId:String? = "",
    val senderId:String? = "",
    val message:String? = "",
    val timestamp: Long? = 0,
    val read:Boolean? = false,
    val messageType : String? = "",
    val imageUrl: String? = "",
    val edited : Boolean? = false,
    val pinned : Boolean? = false,
    val starred : Boolean? = false,
    val replyTo : String? = "",
    ){
    @Exclude
    fun isRead() : Boolean = read == true
}