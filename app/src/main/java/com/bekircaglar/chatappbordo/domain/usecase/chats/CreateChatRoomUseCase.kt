package com.bekircaglar.chatappbordo.domain.usecase.chats

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.repository.ChatsRepository
import javax.inject.Inject

class CreateChatRoomUseCase @Inject constructor(private val chatsRepository: ChatsRepository) {

    suspend operator fun invoke(user1:String,user2: String,chatRoomId:String,) {
        return chatsRepository.createChatRoom(user1,user2,chatRoomId)
    }
}