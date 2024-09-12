package com.bekircaglar.bluchat.domain.usecase.chatinfo

import com.bekircaglar.bluchat.domain.repository.ChatInfoRepository
import javax.inject.Inject

class UpdateChatInfoUseCase @Inject constructor(private val chatInfoRepository: ChatInfoRepository) {

    suspend operator fun invoke(chatId: String, chatName: String, chatImageUrl: String) {
        chatInfoRepository.updateChatInfo(chatId, chatName, chatImageUrl)
    }
}