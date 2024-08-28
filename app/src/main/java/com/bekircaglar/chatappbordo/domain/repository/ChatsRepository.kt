package com.bekircaglar.chatappbordo.domain.repository

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.ChatRoom
import com.bekircaglar.chatappbordo.domain.model.Users
import kotlinx.coroutines.flow.Flow

interface ChatsRepository {

    suspend fun searchContacts(query: String): Response<List<Users>>

    suspend fun createChatRoom(user1: String, user2: String,chatRoomId: String):Response<String>

    suspend fun getUserData(userId:String): Response<Users>

    suspend fun getUsersChatList(): Flow<Response<List<ChatRoom>>>

    suspend fun openChatRoom(user1: String,user2Id:String):Response<String>

}