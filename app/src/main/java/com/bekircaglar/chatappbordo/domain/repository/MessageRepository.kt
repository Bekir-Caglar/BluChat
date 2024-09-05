package com.bekircaglar.chatappbordo.domain.repository

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun getUserFromChatId(chatId: String): Flow<Response<String>>

    suspend fun createMessageRoom(chatId: String): Flow<Response<String>>

    suspend fun sendMessage(message: Message,chatId: String): Flow<Response<String>>

    fun loadInitialMessages(chatId: String): Flow<List<Message>>

    fun loadMoreMessages(chatId: String, lastKey: String): Flow<List<Message>>
}