package com.bekircaglar.bluchat.domain.repository

import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun getUserFromChatId(chatId: String): Flow<Response<List<String?>>>

    suspend fun createMessageRoom(chatId: String): Flow<Response<String>>

    suspend fun sendMessage(message: Message, chatId: String): Flow<Response<String>>

    suspend fun getChatRoom(chatId: String): Flow<Response<ChatRoom>>

    fun loadInitialMessages(chatId: String): Flow<List<Message>>

    fun loadMoreMessages(chatId: String, lastKey: String): Flow<List<Message>>

    fun observeGroupStatus(groupId: String): Flow<Boolean>

    fun observeUserStatusInGroup(groupId: String, userId: String): Flow<Boolean>

    suspend fun deleteMessage(chatId: String, messageId: String): Flow<Response<String>>
}