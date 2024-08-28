package com.bekircaglar.chatappbordo.domain.usecase.chats

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.ChatRoom
import com.bekircaglar.chatappbordo.domain.repository.ChatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserChatListUseCase @Inject constructor(private val chatsRepository: ChatsRepository) {

    suspend operator fun invoke(): Flow<Response<List<ChatRoom>>> {
        return chatsRepository.getUsersChatList()
    }
}