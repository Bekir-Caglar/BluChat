package com.bekircaglar.chatappbordo.domain.usecase.message

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Message
import com.bekircaglar.chatappbordo.domain.repository.MessageRepository
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    suspend operator fun invoke(chatId: String): kotlinx.coroutines.flow.Flow<Response<List<Message>>> = messageRepository.getMessages(chatId)
}