package com.bekircaglar.chatappbordo.domain.repository

import com.bekircaglar.chatappbordo.Response
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun getUserFromChatId(chatId: String): Flow<Response<String>>

    suspend fun createMessageRoom(chatId: String): Flow<Response<String>>
}