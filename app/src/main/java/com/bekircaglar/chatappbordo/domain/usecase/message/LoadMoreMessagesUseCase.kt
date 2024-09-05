package com.bekircaglar.chatappbordo.domain.usecase.message

import com.bekircaglar.chatappbordo.domain.model.Message
import com.bekircaglar.chatappbordo.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadMoreMessagesUseCase @Inject constructor(private val repository: MessageRepository) {
    operator fun invoke(chatId: String, lastKey: String): Flow<List<Message>> {
        return repository.loadMoreMessages(chatId, lastKey)
    }
}