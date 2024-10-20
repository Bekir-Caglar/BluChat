package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import javax.inject.Inject

class GetChatRoomUseCase @Inject constructor(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(chatId: String): kotlinx.coroutines.flow.Flow<Response<ChatRoom>> = messageRepository.getChatRoom(chatId)
}