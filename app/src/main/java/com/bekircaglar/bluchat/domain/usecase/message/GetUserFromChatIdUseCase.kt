package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.data.repository.MessageRepositoryImp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserFromChatIdUseCase @Inject constructor(private val messageRepositoryImp: MessageRepositoryImp) {
    suspend operator fun invoke(chatId: String): Flow<Response<List<String?>>> = messageRepositoryImp.getUserFromChatId(chatId)
}