package com.bekircaglar.bluchat.domain.usecase.profile

import android.net.Uri
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    suspend fun invoke(uri: Uri,):Flow<Response<String>> = profileRepository.uploadImage(uri)
}