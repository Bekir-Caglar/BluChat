package com.bekircaglar.chatappbordo.domain.usecase.message

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.data.repository.MessageRepositoryImp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserFromChatIdUseCase @Inject constructor(private val messageRepositoryImp: MessageRepositoryImp) {
    suspend operator fun invoke(chatId: String): Flow<Response<String>> = messageRepositoryImp.getUserFromChatId(chatId)
}