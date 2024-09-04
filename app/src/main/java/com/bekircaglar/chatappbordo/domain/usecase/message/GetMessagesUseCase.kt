package com.bekircaglar.chatappbordo.domain.usecase.message

import androidx.paging.PagingData
import com.bekircaglar.chatappbordo.domain.model.Message
import com.bekircaglar.chatappbordo.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    operator fun invoke(chatId: String): Flow<PagingData<Message>> = messageRepository.getMessages(chatId)
}