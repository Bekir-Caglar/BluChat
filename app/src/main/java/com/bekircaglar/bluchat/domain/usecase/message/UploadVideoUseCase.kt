package com.bekircaglar.bluchat.domain.usecase.message

import android.net.Uri
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import javax.inject.Inject

class UploadVideoUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    suspend operator fun invoke(uri: Uri) = messageRepository.uploadVideo(uri)

}