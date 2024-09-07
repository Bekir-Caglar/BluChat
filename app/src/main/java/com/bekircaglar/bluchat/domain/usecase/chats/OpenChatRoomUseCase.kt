package com.bekircaglar.bluchat.domain.usecase.chats

import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import javax.inject.Inject

class OpenChatRoomUseCase @Inject constructor(private val chatsRepository: ChatsRepository) {
    suspend operator fun invoke(user1Id:String,user2Id:String):Response<String> = chatsRepository.openChatRoom(user1Id,user2Id)

}