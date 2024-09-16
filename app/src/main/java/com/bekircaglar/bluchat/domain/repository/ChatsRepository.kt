package com.bekircaglar.bluchat.domain.repository

import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.Users
import kotlinx.coroutines.flow.Flow

interface ChatsRepository {

    suspend fun searchContacts(query: String): Flow<Response<List<Users>>>

    suspend fun createChatRoom(user1: String, user2: String, chatRoomId: String): Flow<Response<String>>

    suspend fun getUserData(userId: String): Flow<Response<Users>>

    suspend fun getUsersChatList(): Flow<Response<List<ChatRoom>>>


    suspend fun createGroupChatRoom(
        currentUser: String,
        groupMembers: List<String>,
        chatId: String,
        groupName: String,
        groupImg: String
    ): Flow<Response<String>>

}