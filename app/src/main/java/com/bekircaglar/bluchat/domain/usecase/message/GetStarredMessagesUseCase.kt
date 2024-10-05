package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.domain.repository.MessageRepository
import javax.inject.Inject

class GetStarredMessagesUseCase @Inject constructor(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(chatId: String) = messageRepository.getStarredMessages(chatId)

}