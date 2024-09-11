package com.bekircaglar.bluchat.domain.usecase.chats

import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import javax.inject.Inject

class CreateGroupChatRoomUseCase @Inject constructor(private val chatsRepository: ChatsRepository)  {

    suspend operator fun invoke(currentUser:String,groupMembers:List<String>,chatId:String,groupName:String,groupImg: String) = chatsRepository.createGroupChatRoom(currentUser,groupMembers,chatId,groupName,groupImg)
}