package com.bekircaglar.bluchat.domain.usecase.chats

import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import javax.inject.Inject

class CreateChatRoomUseCase @Inject constructor(private val chatsRepository: ChatsRepository) {

    suspend operator fun invoke(user1:String,user2: String,chatRoomId:String,):Response<String> {
        return chatsRepository.createChatRoom(user1,user2,chatRoomId)
    }
}