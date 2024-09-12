package com.bekircaglar.bluchat.domain.usecase.chatinfo

import com.bekircaglar.bluchat.domain.repository.ChatInfoRepository
import javax.inject.Inject

class KickUserUseCase @Inject constructor(
    private val chatInfoRepository: ChatInfoRepository
){

    suspend operator fun invoke(userId: String,chatId:String) {
        chatInfoRepository.kickUser(userId,chatId)
    }
}