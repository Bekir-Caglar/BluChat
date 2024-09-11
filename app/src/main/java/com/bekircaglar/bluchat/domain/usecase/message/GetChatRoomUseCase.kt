package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import java.util.concurrent.Flow
import javax.inject.Inject

class GetChatRoomUseCase @Inject constructor(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(chatId: String): kotlinx.coroutines.flow.Flow<Response<ChatRoom>> = messageRepository.getChatRoom(chatId)
}