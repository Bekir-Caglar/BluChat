package com.bekircaglar.bluchat.domain.usecase.chats

import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateChatRoomUseCase @Inject constructor(private val chatsRepository: ChatsRepository) {

    suspend operator fun invoke(user1:String,user2: String,chatRoomId:String,): Flow<Response<String>> {
        return chatsRepository.createChatRoom(user1,user2,chatRoomId)
    }
}