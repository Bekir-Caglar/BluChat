package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.domain.repository.MessageRepository
import javax.inject.Inject

class UnStarMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    suspend operator fun invoke(messageId: String, chatId: String) =
        messageRepository.unStarMessage(messageId, chatId)


}