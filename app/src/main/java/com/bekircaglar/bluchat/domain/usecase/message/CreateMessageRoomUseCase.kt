package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateMessageRoomUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    suspend operator fun invoke(chatId: String): Flow<Response<String>> = messageRepository.createMessageRoom(chatId)
}