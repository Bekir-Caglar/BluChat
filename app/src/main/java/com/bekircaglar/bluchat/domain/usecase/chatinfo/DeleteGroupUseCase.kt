package com.bekircaglar.bluchat.domain.usecase.chatinfo

import com.bekircaglar.bluchat.domain.repository.ChatInfoRepository
import javax.inject.Inject

class DeleteGroupUseCase @Inject constructor(private val chatInfoRepository: ChatInfoRepository){
    suspend operator fun invoke(chatId: String) {
        chatInfoRepository.deleteGroup(chatId)
    }

}