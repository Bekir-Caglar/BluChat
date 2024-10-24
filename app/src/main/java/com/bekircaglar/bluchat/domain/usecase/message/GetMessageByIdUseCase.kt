package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.domain.repository.MessageRepository
import javax.inject.Inject

class GetMessageByIdUseCase @Inject constructor(private val messageRepository: MessageRepository)  {
    suspend operator fun invoke(id: String,chatId:String) = messageRepository.getMessageById(id,chatId)

}