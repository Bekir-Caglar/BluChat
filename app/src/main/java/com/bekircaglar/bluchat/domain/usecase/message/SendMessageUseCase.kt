package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.domain.model.message.Message
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    suspend operator fun invoke(message: Message, chatId: String) = messageRepository.sendMessage(message, chatId)

}