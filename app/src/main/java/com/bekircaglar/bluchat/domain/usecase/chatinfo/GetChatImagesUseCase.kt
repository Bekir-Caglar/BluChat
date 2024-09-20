package com.bekircaglar.bluchat.domain.usecase.chatinfo

import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.repository.ChatInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatImagesUseCase @Inject constructor(private val chatInfoRepository: ChatInfoRepository) {
    suspend operator fun invoke(chatId: String) : Flow<Response<List<String>>> = chatInfoRepository.getChatImages(chatId)

}