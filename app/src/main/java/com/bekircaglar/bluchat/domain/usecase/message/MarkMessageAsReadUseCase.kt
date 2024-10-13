package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.domain.repository.MessageRepository
import javax.inject.Inject

class MarkMessageAsReadUseCase @Inject constructor(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(messageId: String, chatId: String) = messageRepository.markMessageAsRead(messageId, chatId)

}