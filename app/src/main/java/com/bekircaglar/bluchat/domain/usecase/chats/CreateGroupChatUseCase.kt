package com.bekircaglar.bluchat.domain.usecase.chats

import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import javax.inject.Inject

class CreateGroupChatUseCase @Inject constructor(private val chatsRepository: ChatsRepository)  {
}