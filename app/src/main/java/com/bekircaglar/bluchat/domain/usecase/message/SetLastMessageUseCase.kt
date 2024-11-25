package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.domain.model.message.Message
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import javax.inject.Inject

class SetLastMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    suspend operator fun invoke(chatId: String, lastMessage: Message) = messageRepository.setLastMessage(chatId, lastMessage)

}