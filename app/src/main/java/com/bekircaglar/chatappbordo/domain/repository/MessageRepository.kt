package com.bekircaglar.chatappbordo.domain.repository

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun getUserFromChatId(chatId: String): Flow<Response<String>>

    suspend fun createMessageRoom(chatId: String): Flow<Response<String>>

    suspend fun sendMessage(message: Message,chatId: String): Flow<Response<String>>

    suspend fun getMessages(chatId: String): Flow<Response<List<Message>>>
}