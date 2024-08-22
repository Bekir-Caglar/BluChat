package com.bekircaglar.chatappbordo.domain.usecase.profile

import android.net.Uri
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    suspend fun invoke(uri: Uri):Flow<Response<String>> = profileRepository.uploadImage(uri)
}