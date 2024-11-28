package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.domain.repository.MessageRepository
import javax.inject.Inject

class UploadAudioUseCase @Inject constructor(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(audioFilePath: String) = messageRepository.uploadAudio(audioFilePath)
}