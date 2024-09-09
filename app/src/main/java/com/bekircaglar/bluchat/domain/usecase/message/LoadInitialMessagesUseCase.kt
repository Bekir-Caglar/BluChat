package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadInitialMessagesUseCase @Inject constructor(private val repository: MessageRepository) {
    operator fun invoke(chatId: String): Flow<List<Message>> {
        return repository.loadInitialMessages(chatId)
    }
}