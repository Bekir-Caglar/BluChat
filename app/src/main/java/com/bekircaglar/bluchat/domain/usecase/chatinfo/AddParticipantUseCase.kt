package com.bekircaglar.bluchat.domain.usecase.chatinfo

import com.bekircaglar.bluchat.domain.repository.ChatInfoRepository
import javax.inject.Inject

class AddParticipantUseCase @Inject constructor(private val chatInfoRepository: ChatInfoRepository) {

    suspend operator fun invoke(chatId: String, userIdList: List<String?>) = chatInfoRepository.addParticipant(chatId, userIdList)
}