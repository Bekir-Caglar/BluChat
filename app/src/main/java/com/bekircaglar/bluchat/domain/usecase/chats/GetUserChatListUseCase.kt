package com.bekircaglar.bluchat.domain.usecase.chats

import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserChatListUseCase @Inject constructor(private val chatsRepository: ChatsRepository) {

    suspend operator fun invoke(): Flow<Response<List<ChatRoom>>> {
        return chatsRepository.getUsersChatList()
    }
}