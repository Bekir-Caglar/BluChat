package com.bekircaglar.chatappbordo.domain.repository

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users

interface ChatsRepository {

    suspend fun searchContacts(query: String): Response<List<Users>>

    suspend fun createChatRoom(user1: String, user2: String,chatRoomId: String)

    suspend fun getUserData(userId:String): Response<Users>


}