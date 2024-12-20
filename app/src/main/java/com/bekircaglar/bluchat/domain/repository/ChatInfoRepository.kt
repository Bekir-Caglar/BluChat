package com.bekircaglar.bluchat.domain.repository

import com.bekircaglar.bluchat.utils.Response
import kotlinx.coroutines.flow.Flow

interface ChatInfoRepository {

    suspend fun kickUser(userId: String, chatId: String)

    suspend fun deleteGroup(chatId: String)

    suspend fun leaveChat(chatId: String, userId: String)

    suspend fun addParticipant(chatId: String, userIdList: List<String?>)

    suspend fun getChatImages(chatId: String): Flow<Response<List<String>>>

    suspend fun updateChatInfo(chatId: String, chatName: String, chatImageUrl: String)
}