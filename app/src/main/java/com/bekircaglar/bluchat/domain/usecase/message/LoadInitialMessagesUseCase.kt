package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.model.message.Message
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadInitialMessagesUseCase @Inject constructor(private val repository: MessageRepository) {
    operator fun invoke(chatId: String): Flow<Response<List<Message>>> {
        return repository.loadInitialMessages(chatId)
    }
}