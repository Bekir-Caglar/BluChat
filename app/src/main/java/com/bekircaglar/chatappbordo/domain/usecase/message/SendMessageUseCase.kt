package com.bekircaglar.chatappbordo.domain.usecase.message

import com.bekircaglar.chatappbordo.domain.model.Message
import com.bekircaglar.chatappbordo.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    suspend operator fun invoke(message: Message, chatId: String) = messageRepository.sendMessage(message, chatId)

}